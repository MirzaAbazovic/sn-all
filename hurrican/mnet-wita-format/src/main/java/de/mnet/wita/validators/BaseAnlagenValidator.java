/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2011 17:04:53
 */
package de.mnet.wita.validators;

import java.util.*;
import javax.validation.*;

import de.mnet.wita.message.common.Anlage;

public abstract class BaseAnlagenValidator {

    private static final int MAX_FILESIZE_IN_BYTE = 1024 * 1024;
    private static final int MAX_TOTAL_FILESIZE_IN_BYTE = 4 * 1024 * 1024;

    protected boolean filesizesValid(Collection<Anlage> anlagen, ConstraintValidatorContext context) {
        long totalFilesize = 0;

        for (Anlage anlage : anlagen) {
            long filesize = anlage.getInhalt().length;
            if (filesize > MAX_FILESIZE_IN_BYTE) {
                ValidationUtils.addConstraintViolation(context,
                        "Die Größe einer einzelnen Anlage darf 1 MB nicht überschreiten.");
                return false;
            }
            totalFilesize += filesize;
        }

        if (totalFilesize > MAX_TOTAL_FILESIZE_IN_BYTE) {
            ValidationUtils.addConstraintViolation(context, "Die Größe der Anlagen darf 1 MB (bzw. 4 MB für alle zusammen) nicht überschreiten.");
            return false;
        }
        return true;
    }

}
