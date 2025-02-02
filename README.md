# Aplikacja do przetwarzania transakcji

## Architektura rozwiązania

### Komponenty systemu

1. **Aplikacja transakcyjna** (`transaction-app`)
    - Aplikacja zbudowana na Spring Boot, obsługująca główną logikę biznesową systemu, w tym interakcję z bazą danych i komunikację przez REST API oraz SOAP.
    - Dostęp do bazy danych realizowany jest przy pomocy Spring Data JPA.
    - Używa Spring Scheduler do wykonywania zaplanowanych zadań.
    - Realizuje komunikację asynchroniczną za pomocą Apache Kafka.

2. **Kafka**
    - System kolejkowy Apache Kafka służy do przetwarzania transakcji, których producentem jest aplikacja transakcyjna (`transaction-app`).

3. **Baza danych H2**
    - Wbudowana baza danych H2 jest używana głównie w środowisku deweloperskim i testowym.
    - Służy do przechowywania danych aplikacji, takich jak transakcje płatnicze.

4. **Kafdrop**
    - Narzędzie do monitorowania i wizualizacji stanu komponentu Kafka.
    - Umożliwia przeglądanie topiców i wiadomości w Kafka w interfejsie webowym.

### Technologie

Aplikacja korzysta z następujących technologii i komponentów:

- **Java 21** - najnowsza wersja LTS, zapewniająca stabilność i długoterminowe wsparcie.
- **Spring Boot** - główny framework aplikacji.
- **Spring Data JPA** - warstwa dostępu do bazy danych oparta na JPA, umożliwiająca interakcję z bazą danych w sposób obiektowy.
- **Spring Web** - pozwala na tworzenie interfejsów REST dla aplikacji.
- **Spring Web Services (SOAP)** - obsługuje usługi Web Services w formacie SOAP.
- **Spring Scheduler** - do uruchamiania zadań zaplanowanych.
- **Apache Kafka** - system kolejkowy do przetwarzania komunikatów w systemie.
- **OpenAPI/Swagger** - narzędzia do dokumentacji REST API i generowania interfejsów REST.
- **H2 Database** - wbudowana baza danych, używana do testów i dewelopmentu.

## Uruchamienie aplikacji

W celu uruchomienia aplikacji konieczne jest skompilowanie źródeł oraz zbudowanie aplikacji i kontenerów Docker.
Komiplacja źródeł odbywa się automatycznie z wykorzystaniem Gradle. Poniżej opisano kroki konieczne do uruchomienia aplikacji:

1. Uruchomić Docker Desktop.
2. W konsoli Windows przejść do katalogu (`dev`) w aplikacji np. (`cd C:\git\transaction-processor\dev`).
3. Uruchomić skrypt (`run-docker-containers.bat`) - plik ten kompiluje źródła, buduje aplikacje, a także buduje wszystkie obrazy i uruchamia kontenery aplikacji.
4. W celu zatrzymania oraz usunięcia kontenerów i woluminów należy uruchomić skrypt (`cleanup-docker-containers.bat`).

## Dostęp do aplikacji

Poniżej znajduje się lista adresów, pod jakimi dostępne są poszczególne komponenty aplikacji.

| Nazwa komponentu                           | Adres                                                          |
|--------------------------------------------|----------------------------------------------------------------|
| SwaggerUI                                  | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| Usługa REST                                | [http://localhost:8080/transactions](http://localhost:8080/transactions) |
| WSDL Web Service                           | [http://localhost:8080/ws/transactions.wsdl](http://localhost:8080/ws/transactions.wsdl) |
| Kafdrop                                    | [http://localhost:9000](http://localhost:9000) |
| Kafdrop (topic transakcje-przeterminowane) | [http://localhost:9000/topic/transakcje-przeterminowane](http://localhost:9000/topic/transakcje-przeterminowane) |
| Kafdrop (topic transakcje-zrealizowane)    | [http://localhost:9000/topic/transakcje-zrealizowane](http://localhost:9000/topic/transakcje-zrealizowane) |

Przykładowy kod do utworzenia nowej transakcji w usłudze REST:
```JSON
{
  "amount": 100.00,
  "currency": "PLN",
  "status": "PENDING"
}
```

## Testy integracyjne

Testy integracyjne usług REST zostały zdefiniowane w klasie TransactionApiControllerTest.
Poniżej znajduje się instrukcja ich uruchomienia.
1. W wierszu poleceń przejść do katalogu transaction-app projektu.
2. Uruchomić polecenie (`gradlew test`).
3. Testy wykonają się automatycznie. 
4. Raport z testów dostępny będzie w katalogu transaction-app\build\reports\tests\test.
