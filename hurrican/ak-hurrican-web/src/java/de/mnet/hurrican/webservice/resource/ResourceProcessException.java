package de.mnet.hurrican.webservice.resource;

public class ResourceProcessException extends Exception {

    private static final long serialVersionUID = 8373875808008794335L;

    public ResourceProcessException(String message) {
        super(message);
    }

    public ResourceProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
