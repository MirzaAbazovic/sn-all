package de.augustakom.hurrican.service.cc.impl.logindata.model;

/**
 * Superclass for login data exceptions
 */
public class LoginDataException extends Exception {

    /**
     * @see Exception()
     */
    public LoginDataException() {
        super();
    }

    public LoginDataException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @see Exception(String)
     */
    public LoginDataException(String message) {
        super(message);
    }
}
