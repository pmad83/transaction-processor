package pl.pm.transactionprocessor.enums;

/*
 * Klasa TransactionStatus zawiera stałe reprezentujące statusy transakcji,
 * używane do oznaczania bieżącego stanu transakcji w systemie.
 */
public class TransactionStatus {

    public static final String REGEXP = "PENDING|COMPLETED|EXPIRED";
    public static final String PENDING = "PENDING";
    public static final String COMPLETED = "COMPLETED";
    public static final String EXPIRED = "EXPIRED";
}
