package de.bitconex.adlatus.wholebuy.provision.adapter.wita.exception;

public class WitaMessageException extends RuntimeException {

    public WitaMessageException(Exception exception) {
        super(exception);
    }

    public WitaMessageException(String message) {
        super(message);
    }

    public WitaMessageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
