package pl.pm.transactionprocessor.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.pm.transactionprocessor.entity.Transaction;
import pl.pm.transactionprocessor.enums.TransactionStatus;
import pl.pm.transactionprocessor.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/*
 * Klasa konfigurująca endpoint usługi sieciowej SOAP do obsługi transakcji, zawierająca logikę biznesową zaimplementowanych usług.
 */
@Endpoint
public class TransactionsEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsEndpoint.class);
    private static final String NAMESPACE_URI = "http://transactionprocessor.pm.pl/soap";

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionsEndpoint(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createTransactionRequest")
    @ResponsePayload
    public CreateTransactionResponse createTransaction(@RequestPayload CreateTransactionRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (request.getCurrency() == null) {
            throw new IllegalArgumentException("Currency is required");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount().doubleValue());
        transaction.setCurrency(request.getCurrency().value());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);
        logger.info("Zapisano nową transakcję, ID: {}", transaction.getId());

        CreateTransactionResponse response = new CreateTransactionResponse();
        response.setId(transaction.getId());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "correctTransactionRequest")
    @ResponsePayload
    public CorrectTransactionResponse correctTransaction(@RequestPayload CorrectTransactionRequest request) {
        if (request.getId() <= 0) {
            throw new IllegalArgumentException("Transaction ID is required");
        }
        if (request.getCorrectionAmount() == null) {
            throw new IllegalArgumentException("Correction amount cannot be null");
        }

        Optional<Transaction> optionalTransaction = transactionRepository.findById(request.getId());

        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            transaction.setAmount(request.getCorrectionAmount().doubleValue());
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);
            logger.info("Zaktualizowano transakcję, ID: {}, Kwota: {}, Waluta: {}, Status: {}",
                    transaction.getId(), transaction.getAmount(), transaction.getCurrency(), transaction.getStatus());

            CorrectTransactionResponse response = new CorrectTransactionResponse();
            response.setStatus(transaction.getStatus());
            return response;
        } else {
            throw new IllegalArgumentException("Transaction not found");
        }
    }
}
