package de.augustakom.hurrican.service.cc.impl.logindata.model;

/**
 *  Product is not valid for getting LoginData exception
 */
public class LoginDataNotValidProductException extends LoginDataException {

    /**
     * @see LoginDataException (String)
     */
    public LoginDataNotValidProductException(String message) {
        super(message);
    }
}
