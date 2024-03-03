package de.bitconex.adlatus.common.infrastructure.exception;

public class OrderInvalidException extends RuntimeException {
    public OrderInvalidException(Exception e) {
        super(e);
    }

    public OrderInvalidException(String message) {
        super(message);
    }

    public OrderInvalidException() {
        super();
    }
}
