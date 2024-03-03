package de.bitconex.adlatus.common.infrastructure.exception;

public class WitaMessageException extends RuntimeException {
    public WitaMessageException(Exception e) {
        super(e);
    }

    public WitaMessageException() {
        super();
    }
}
