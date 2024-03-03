/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.07.2004 10:09:04
 */
package de.augustakom.common.tools.validation;

import java.io.*;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Abstrakte Validierungs-Klasse. <br> <br> Beispiel fuer die Implementierung: <br><br> <code> public boolean
 * supports(Class<?> clazz) { <br> &nbsp;&nbsp; return ObjectToValidate.class.isAssignableFrom(clazz); <br> } <br> <br>
 * public void validate(Object object, Errors errors) { <br> &nbsp;&nbsp; ObjectToValidate otv = (ObjectToValidate)
 * object; <br> &nbsp;&nbsp; if (otv.getValue() == null) { <br> &nbsp;&nbsp;&nbsp;&nbsp; errors.rejectValue("value",
 * "required", "Value darf nicht null sein."); <br> &nbsp;&nbsp; } <br> } <br> </code>
 * <p/>
 * <br><br> Beispiel fuer den Aufruf: <br> <code> ValidationException ve = new ValidationException(obj,
 * "ObjectToValidate"); <br> new MyValidator().validate(obj, ve); <br> if (ve.hasErrors) { <br> &nbsp;&nbsp; throw ve;
 * <br> } <br> </code>
 */
public abstract class AbstractValidator implements Validator, Serializable {

    /**
     * Error-Code, wenn ein Field 'required' ist.
     */
    public static final String ERRCODE_REQUIRED = "required";

    /**
     * Error-Code, wenn der Value eines Fields 'invalid' ist.
     */
    public static final String ERRCODE_INVALID = "invalid";

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public abstract boolean supports(Class<?> clazz);

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public abstract void validate(Object object, Errors errors);
}
