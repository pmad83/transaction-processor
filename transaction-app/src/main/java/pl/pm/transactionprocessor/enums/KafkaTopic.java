package pl.pm.transactionprocessor.enums;

/*
 * Klasa KafkaTopic zawiera stałe reprezentujące nazwy tematów w systemie Kafka,
 * używane do kategoryzowania danych związanych z transakcjami.
 */
public class KafkaTopic {
    public static final String COMPLETED_TRANSACTIONS = "transakcje-zrealizowane";
    public static final String EXPIRED_TRANSACTIONS = "transakcje-przeterminowane";
}
