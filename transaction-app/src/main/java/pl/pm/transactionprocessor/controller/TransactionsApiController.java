package pl.pm.transactionprocessor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;
import pl.pm.transactionprocessor.entity.Transaction;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.NativeWebRequest;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;
import pl.pm.transactionprocessor.enums.TransactionCurrency;
import pl.pm.transactionprocessor.enums.TransactionStatus;
import pl.pm.transactionprocessor.repository.TransactionRepository;

/*
 * Klasa TransactionsApiController to kontroler REST API obsługujący operacje CRUD na transakcjach.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-25T20:24:51.030596300+01:00[Europe/Berlin]", comments = "Generator version: 7.12.0")
@Controller
@RequestMapping("${openapi.transaction.base-path:}")
public class TransactionsApiController implements TransactionsApi {

    private static final Logger logger = LoggerFactory.getLogger(TransactionsApiController.class);
    private final TransactionRepository repository;
    private final NativeWebRequest request;

    @Autowired
    public TransactionsApiController(NativeWebRequest request, TransactionRepository repository) {

        this.request = request;
        this.repository = repository;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<Transaction> createTransaction(Transaction transaction) {

        // Waluta musi być jedną z: PLN, EUR, GDP, USD
        if (transaction.getCurrency() == null || !transaction.getCurrency().matches(TransactionCurrency.REGEXP)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Status musi być jednym z: PENDING, COMPLETED, EXPIRED
        if (transaction.getStatus() == null || !transaction.getStatus().matches(TransactionStatus.REGEXP)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        transaction.setId(null);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        Transaction savedTransaction = repository.save(transaction);
        logger.info("Zapisano nową transakcję, ID: {}", savedTransaction.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(Long id) {

        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transakcja nie znaleziona"));

        repository.delete(transaction);
        logger.info("Usunięto transakcję, ID: {}", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<List<Transaction>> getAllTransactions() {

        return ResponseEntity.status(HttpStatus.OK).body(repository.findAll());
    }

    @Override
    public ResponseEntity<Transaction> getTransactionById(Long id) {

        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transakcja nie znaleziona"));

        return ResponseEntity.status(HttpStatus.OK).body(transaction);
    }

    @Override
    public ResponseEntity<Transaction> updateTransaction(Long id, Transaction transaction) {
        if (!repository.existsById(id) || (transaction.getId() != null && !transaction.getId().equals(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Transaction currentTrans = repository.findById(id).get();

        // Waluta musi być jedną z: PLN, EUR, GDP, USD
        if (currentTrans.getCurrency() == null || !currentTrans.getCurrency().matches(TransactionCurrency.REGEXP)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Status musi być jednym z: PENDING, COMPLETED, EXPIRED
        if (currentTrans.getStatus() == null || !currentTrans.getStatus().matches(TransactionStatus.REGEXP)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        currentTrans.setAmount(transaction.getAmount());
        currentTrans.setCurrency(transaction.getCurrency());
        currentTrans.setStatus(transaction.getStatus());
        currentTrans.setUpdatedAt(LocalDateTime.now());

        repository.save(currentTrans);

        logger.info("Zaktualizowano transakcję, ID: {}, Kwota: {}, Waluta: {}, Status: {}",
                currentTrans.getId(), currentTrans.getAmount(), currentTrans.getCurrency(), currentTrans.getStatus());

        return ResponseEntity.status(HttpStatus.OK).body(currentTrans);
    }
}
