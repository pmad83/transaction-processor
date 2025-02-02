package pl.pm.transactionprocessor.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.pm.transactionprocessor.entity.Transaction;
import pl.pm.transactionprocessor.enums.KafkaTopic;

/*
 * Klasa TransactionKafkaProducer wykorzystywana jest do wysyłania transakcji na Kafke (messages producer).
 */
@Service
public class TransactionKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(TransactionKafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransactionKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCompletedTransaction(Transaction transaction) {
        String message = "Transakcja zrealizowana: ID=" + transaction.getId() +
                ", Amount=" + transaction.getAmount() +
                ", Currency=" + transaction.getCurrency() +
                ", Status=" + transaction.getStatus();

        String key = String.valueOf(transaction.getId());

        kafkaTemplate.send(KafkaTopic.COMPLETED_TRANSACTIONS, key, message);
        logger.info("Transakcja wysłana na Kafke: ID={}", transaction.getId());
    }

    public void sendExpiredTransaction(Transaction transaction) {
        String message = "Transakcja przeterminowana: ID=" + transaction.getId() +
                ", Amount=" + transaction.getAmount() +
                ", Currency=" + transaction.getCurrency() +
                ", Status=" + transaction.getStatus();

        String key = String.valueOf(transaction.getId());

        kafkaTemplate.send(KafkaTopic.EXPIRED_TRANSACTIONS, key, message);
        logger.info("Transakcja wysłana na Kafke: ID={}", transaction.getId());
    }
}
