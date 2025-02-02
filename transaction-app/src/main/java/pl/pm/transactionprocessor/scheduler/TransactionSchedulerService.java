package pl.pm.transactionprocessor.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.pm.transactionprocessor.entity.Transaction;
import pl.pm.transactionprocessor.enums.TransactionStatus;
import pl.pm.transactionprocessor.kafka.TransactionKafkaProducer;
import pl.pm.transactionprocessor.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
 * Klasa TransactionSchedulerService wykorzystywana jest do cyklicznego przetwarzania transakcji.
 */
@Service
public class TransactionSchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionSchedulerService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionKafkaProducer transactionKafkaProducer;

    // Cykliczne zadanie, które uruchamia się co 30 sekund
    @Scheduled(fixedRate = 30000)
    public void processTransactions() {
        LocalDateTime currentTime = LocalDateTime.now();
        logger.info("Scheduler został uruchomiony o godzinie: {}", currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Transakcje zrealizowane w ciągu ostatnich 30 sekund (status: COMPLETED)
        List<Transaction> completedTransactions = transactionRepository
                .findByStatusAndUpdatedAtGreaterThan(TransactionStatus.COMPLETED, currentTime.minusSeconds(30));

        for (Transaction transaction : completedTransactions) {
            logger.info("Transakcja zrealizowana: ID={}, Amount={}, Currency={}, Status={}",
                    transaction.getId(), transaction.getAmount(), transaction.getCurrency(), transaction.getStatus());
            transactionKafkaProducer.sendCompletedTransaction(transaction);
        }

        // Transakcje przeterminowane (status: PENDING, czas utworzenia > 30 sekund)
        List<Transaction> pendingTransactions = transactionRepository
                .findByStatusAndCreatedAtBefore(TransactionStatus.PENDING, currentTime.minusSeconds(30));

        for (Transaction transaction : pendingTransactions) {
            transaction.setStatus(TransactionStatus.EXPIRED);
            transaction.setUpdatedAt(LocalDateTime.now());
            transactionRepository.save(transaction);
            logger.info("Transakcja przeterminowana: ID={}, Amount={}, Currency={}, Status={}",
                    transaction.getId(), transaction.getAmount(), transaction.getCurrency(), transaction.getStatus());
            transactionKafkaProducer.sendExpiredTransaction(transaction);
        }
    }
}
