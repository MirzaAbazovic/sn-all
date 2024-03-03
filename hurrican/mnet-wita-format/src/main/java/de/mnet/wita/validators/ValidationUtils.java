/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 15:51:13
 */
package de.mnet.wita.validators;

import javax.validation.*;

public class ValidationUtils {

    public static boolean addConstraintViolation(ConstraintValidatorContext context, String errorMsg) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
        return false;
    }
}
