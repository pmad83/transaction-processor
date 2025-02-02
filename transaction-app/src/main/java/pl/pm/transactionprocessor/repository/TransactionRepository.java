package pl.pm.transactionprocessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pm.transactionprocessor.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Repozytorium TransactionRepository zapewnia operacje CRUD dla encji Transaction oraz dodatkowe metody wyszukiwania transakcji.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Zwraca transakcje o statusie "COMPLETED", które zostały zmienione w ciągu ostatnich 30 sekund
    List<Transaction> findByStatusAndUpdatedAtGreaterThan(String status, LocalDateTime updatedAt);

    // Zwraca transakcje o statusie "PENDING", które mają czas utworzenia przekraczający 30 sekund
    List<Transaction> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAt);
}
