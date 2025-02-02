package pl.pm.transactionprocessor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import pl.pm.transactionprocessor.enums.TransactionCurrency;
import pl.pm.transactionprocessor.enums.TransactionStatus;
import pl.pm.transactionprocessor.repository.TransactionRepository;
import pl.pm.transactionprocessor.entity.Transaction;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * Klasa TransactionApiController to kontroler REST API obsługujący operacje CRUD na transakcjach.
 */
@RestController
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-01-30T22:04:09.164651800+01:00[Europe/Berlin]", comments = "Generator version: 7.10.0")
@Validated
@Tag(name = "TransactionProcessor", description = "Transactions Processor REST API")
public class TransactionApiController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionApiController.class);
    private final TransactionRepository repository;

    public TransactionApiController(TransactionRepository repository) {

        this.repository = repository;
    }

    /**
     * POST /transactions : Tworzy nową transakcję
     *
     * @param transaction  (required)
     * @return Transakcja została utworzona (status code 201)
     *         or Błąd walidacji danych (status code 400)
     */
    @Operation(
            operationId = "createTransaction",
            summary = "Tworzy nową transakcję",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transakcja została utworzona", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Błąd walidacji danych")
            }
    )
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/transactions",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(@Valid @RequestBody Transaction transaction) {

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

        return savedTransaction;
    }

    /**
     * GET /transactions : Pobiera wszystkie transakcje
     *
     * @return Lista wszystkich transakcji (status code 200)
     */
    @Operation(
            operationId = "getAllTransactions",
            summary = "Pobiera wszystkie transakcje",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista wszystkich transakcji", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Transaction.class)))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/transactions",
            produces = { "application/json" }
    )
    public List<Transaction> getAllTransactions() {

        return repository.findAll();
    }

    /**
     * GET /transactions/{id} : Pobiera transakcję na podstawie ID
     *
     * @param id  (required)
     * @return Zwraca transakcję o podanym ID (status code 200)
     *         or Transakcja nie znaleziona (status code 404)
     */
    @Operation(
            operationId = "getTransactionById",
            summary = "Pobiera transakcję na podstawie ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zwraca transakcję o podanym ID", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Transakcja nie znaleziona")
            }
    )
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/transactions/{id}",
            produces = { "application/json" }
    )
    public Optional<Transaction> getTransactionById(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transakcja nie znaleziona");
        }

        return repository.findById(id);
    }

    /**
     * PUT /transactions/{id} : Aktualizuje transakcję na podstawie ID
     *
     * @param id  (required)
     * @param transaction  (required)
     * @return Transakcja została zaktualizowana (status code 200)
     *         or Błąd walidacji danych (status code 400)
     *         or Transakcja nie znaleziona (status code 404)
     */
    @Operation(
            operationId = "updateTransaction",
            summary = "Aktualizuje transakcję na podstawie ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transakcja została zaktualizowana", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Błąd walidacji danych"),
                    @ApiResponse(responseCode = "404", description = "Transakcja nie znaleziona")
            }
    )
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/transactions/{id}",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {

        if (!repository.existsById(id) || (transactionDetails.getId() != null && !transactionDetails.getId().equals(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Transaction transaction = repository.findById(id).get();

        // Waluta musi być jedną z: PLN, EUR, GDP, USD
        if (transaction.getCurrency() == null || !transaction.getCurrency().matches(TransactionCurrency.REGEXP)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Status musi być jednym z: PENDING, COMPLETED, EXPIRED
        if (transaction.getStatus() == null || !transaction.getStatus().matches(TransactionStatus.REGEXP)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        transaction.setAmount(transactionDetails.getAmount());
        transaction.setCurrency(transactionDetails.getCurrency());
        transaction.setStatus(transactionDetails.getStatus());
        transaction.setUpdatedAt(LocalDateTime.now());

        repository.save(transaction);

        logger.info("Zaktualizowano transakcję, ID: {}, Kwota: {}, Waluta: {}, Status: {}",
                transaction.getId(), transaction.getAmount(), transaction.getCurrency(), transaction.getStatus());

        return transaction;
    }

    /**
     * DELETE /transactions/{id} : Usuwa transakcję na podstawie ID
     *
     * @param id  (required)
     * @return Transakcja została usunięta (status code 204)
     *         or Transakcja nie znaleziona (status code 404)
     */
    @Operation(
            operationId = "deleteTransaction",
            summary = "Usuwa transakcję na podstawie ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Transakcja została usunięta"),
                    @ApiResponse(responseCode = "404", description = "Transakcja nie znaleziona")
            }
    )
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/transactions/{id}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transakcja nie znaleziona");
        }

        repository.deleteById(id);
        logger.info("Usunięto transakcję, ID: {}", id);
    }
}
