# Aplikacja do przetwarzania transakcji

## Architektura rozwiązania

### Komponenty systemu

1. **Aplikacja transakcyjna** `transaction-app`
    - Aplikacja zbudowana na Spring Boot, obsługująca główną logikę biznesową systemu, w tym interakcję z bazą danych i komunikację przez REST API oraz SOAP.
    - Dostęp do bazy danych realizowany jest przy pomocy Spring Data JPA.
    - Używa Spring Scheduler do wykonywania zaplanowanych zadań.
    - Realizuje komunikację asynchroniczną za pomocą Apache Kafka.

2. **Kafka**
    - System kolejkowy Apache Kafka służy do przetwarzania transakcji, których producentem jest aplikacja transakcyjna `transaction-app`.

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
2. W konsoli Windows przejść do katalogu `dev` w aplikacji np. `cd C:\git\transaction-processor\dev`.
3. Uruchomić skrypt `run-docker-containers.bat` - plik ten kompiluje źródła, buduje aplikacje, a także buduje wszystkie obrazy i uruchamia kontenery aplikacji.
4. W celu zatrzymania oraz usunięcia kontenerów i woluminów należy uruchomić skrypt `cleanup-docker-containers.bat`.

## Dostęp do aplikacji

Poniżej znajduje się lista adresów, pod jakimi dostępne są poszczególne komponenty aplikacji.
Widok komunikatów dostępny jest dopiero po utworzeniu konkretnych topiców.

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
2. Uruchomić polecenie `gradlew test`.
3. Testy wykonają się automatycznie. 
4. Raport z testów dostępny będzie w katalogu transaction-app\build\reports\tests\test.

## Uruchomienie aplikacji w klastrze Kubernetes z wykorzystaniem Helm

W celu uruchomienia aplikacji w klastrze Kubernetes, wymagane jest zainstalowanie Helm oraz Kubernetes.
W przypadku potrzeby pracy w środowisku deweloperskim można skorzystać z Minikube.
Sposób instalacji tych narzędzi wykracza jednak poza zakres niniejszej dokumentacji.

### Uruchomienie aplikacji

1. W pierwszym kroku uruchamiamy Minikube za pomocą poniższego polecenia:
   `minikube start`

2. Następnie konieczne jest zbudowanie zależności Helm. W tym celu w katalogu `transaction-processor-chart` należy uruchomić polecenie:
   `helm dependency build`

3. W katalogu głównym aplikacji uruchamiamy polecenie, aby zainstalować aplikację w klastrze Kubernetes:
   `helm install transaction-processor ./transaction-processor-chart`

4. Po zainstalowaniu aplikacji, ustawiamy tunel Minikube, który umożliwia dostęp do aplikacji działającej w klastrze Kubernetes z poziomu localhosta. Polecenie:
   `minikube tunnel`

5. W pliku `hosts` (katalog C:\Windows\System32\drivers\etc\hosts dla Windows) należy dodać wpis:
   `127.0.0.1 transaction-processor.local`

6. Po zakończeniu pracy, aby usunąć aplikację z klastra Kubernetes, należy wykonać poniższe polecenie:
   `helm uninstall transaction-processor`

### Informacje o dostępnych komponentach

Poniżej znajduje się lista adresów, pod jakimi dostępne są poszczególne komponenty aplikacji.
Widok komunikatów dostępny jest dopiero po utworzeniu konkretnych topiców.

| Nazwa komponentu                           | Adres                                                          |
|--------------------------------------------|----------------------------------------------------------------|
| SwaggerUI                                  | [http://transaction-processor.local/swagger-ui/index.html](http://transaction-processor.local/swagger-ui/index.html) |
| Usługa REST                                | [http://transaction-processor.local/transactions](http://transaction-processor.local/transactions) |
| WSDL Web Service                           | [http://transaction-processor.local/ws/transactions.wsdl](http://transaction-processor.local/ws/transactions.wsdl) |
| Kafdrop                                    | [http://transaction-processor.local:9000](http://transaction-processor.local:9000) |
| Kafdrop (topic transakcje-przeterminowane) | [http://transaction-processor.local:9000/topic/transakcje-przeterminowane](http://transaction-processor.local:9000/topic/transakcje-przeterminowane) |
| Kafdrop (topic transakcje-zrealizowane)    | [http://transaction-processor.local:9000/topic/transakcje-zrealizowane](http://transaction-processor.local:9000/topic/transakcje-zrealizowane) |

### Weryfikacja komunikatów na Kafka

Aby manualnie zweryfikować komunikaty na Kafka, należy wywołać poniższe polecenie, aby uzyskać dostęp do środowiska Kafka:

`kubectl exec -it transaction-processor-kafka-0 -- /bin/bash`

Następnie, aby nasłuchiwać odpowiednie tematy (topics), należy użyć poniższych poleceń:

#### Topic: transakcje-przeterminowane
`kafka-console-consumer.sh --bootstrap-server transaction-processor-kafka-0.transaction-processor-kafka-headless.default.svc.cluster.local:9092 --topic transakcje-przeterminowane --from-beginning`

#### Topic: transakcje-zrealizowane
`kafka-console-consumer.sh --bootstrap-server transaction-processor-kafka-0.transaction-processor-kafka-headless.default.svc.cluster.local:9092 --topic transakcje-zrealizowane --from-beginning`
