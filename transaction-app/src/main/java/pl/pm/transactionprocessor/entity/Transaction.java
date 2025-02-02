package pl.pm.transactionprocessor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
 * Klasa Transaction reprezentuje encję transakcji w systemie.
 */
@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double amount;

    @NotNull
    private String currency;

    @NotNull
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
