package de.bitconex.adlatus.common.infrastructure.exception;

public class GenerateMessageException extends RuntimeException {
    public GenerateMessageException(Exception e) {
        super(e);
    }

    public GenerateMessageException() {
        super();
    }

    public GenerateMessageException(String message) {
        super(message);
    }
}
