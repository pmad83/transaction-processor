package pl.pm.transactionprocessor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.pm.transactionprocessor.entity.Transaction;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TransactionApiControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void createAndGetTransaction() {
        // Przygotowanie danych do wysłania
        Transaction transaction = new Transaction();
        transaction.setAmount(100.00);
        transaction.setCurrency("PLN");
        transaction.setStatus("PENDING");

        // Wysłanie żądania POST do stworzenia obiektu
        ResponseEntity<Transaction> postResponse = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/transactions", transaction, Transaction.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Transaction createdTransaction = postResponse.getBody();
        assertThat(createdTransaction).isNotNull();
        assertThat(createdTransaction.getAmount()).isEqualTo(100.00);
        assertThat(createdTransaction.getCurrency()).isEqualTo("PLN");
        assertThat(createdTransaction.getStatus()).isEqualTo("PENDING");

        // Zapisanie ID z odpowiedzi POST
        Long createdTransactionId = createdTransaction.getId();

        // Wysłanie żądania GET do pobrania obiektu przy użyciu ID zwróconego przez POST
        ResponseEntity<Transaction> getResponse = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/transactions/" + createdTransactionId, Transaction.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Transaction fetchedTransaction = getResponse.getBody();
        assertThat(fetchedTransaction).isNotNull();
        assertThat(fetchedTransaction.getAmount()).isEqualTo(100.00);
        assertThat(fetchedTransaction.getCurrency()).isEqualTo("PLN");
        assertThat(fetchedTransaction.getStatus()).isEqualTo("PENDING");
    }
}
