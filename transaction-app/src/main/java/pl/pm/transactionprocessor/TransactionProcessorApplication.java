package pl.pm.transactionprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * Główna klasa aplikacji Spring Boot.
 */
@SpringBootApplication
@EnableScheduling
public class TransactionProcessorApplication {
    public static void main(String[] args) {

        SpringApplication.run(TransactionProcessorApplication.class, args);
    }
}
