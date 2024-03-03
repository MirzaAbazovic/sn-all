/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2011 13:19:45
 */
package de.mnet.wita.validators;

import java.util.*;
import javax.validation.*;
import org.apache.commons.collections.CollectionUtils;

/**
 * Hilfs-Basisklasse für eigene ConstraintValidator, die prüfen, ob Felder gesetzt sind.
 */
public abstract class BasePresenceValidator {

    protected boolean mandatory;
    protected boolean permitted;
    protected int minOccurrences = 0;
    protected int maxOccurrences = -1;

    /**
     * Fehlermeldung, falls ein required Attribut null ist
     */
    protected String errorMsgIfRequired = "Erforderliches Feld nicht gesetzt.";
    /**
     * Fehlermeldung, falls ein nicht erlaubtes Attribute gesetzt ist
     */
    protected String errorMsgIfNotAllowed = "Nicht erlaubtes Feld gesetzt.";
    /**
     * Fehlermeldung, falls die Anzahl der gesetzten Attribute die zulaessige Anzahl ueberschreitet
     */
    protected String errorMsgIfMaxOccurrencesExceeded = "Anzahl gesetzte Attribute ueberschritten.";
    /**
     * Fehlermeldung, falls die Anzahl der gesetzten Attribute nicht die Mindestanzahl ueberschreitet
     */
    protected String errorMsgIfMinOccurrences = "Anzahl gesetzte Attribute ueberschreitet nicht die Mindestanzahl.";

    protected boolean checkPresence(ConstraintValidatorContext context, Object... toCheck) {
        for (Object o : toCheck) {
            // mandatory is ignored if permitted is set to false
            if (!permitted && (o != null)) {
                return notValid(context, errorMsgIfNotAllowed);
            }
            else if (permitted && mandatory && (o == null)) {
                return notValid(context, errorMsgIfRequired);
            }
        }
        return true;
    }

    protected boolean checkPresenceAndEmpty(ConstraintValidatorContext context, Collection<?> toCheck) {
        // mandatory is ignored if permitted is set to false
        if (!permitted && !CollectionUtils.isEmpty(toCheck)) {
            return notValid(context, errorMsgIfNotAllowed);
        }
        else if (permitted && mandatory && CollectionUtils.isEmpty(toCheck)) {
            return notValid(context, errorMsgIfRequired);
        }
        return true;
    }

    protected boolean checkOccurrences(ConstraintValidatorContext context, Collection<?> toCheck) {
        boolean valid = checkPresenceAndEmpty(context, toCheck);
        if (valid) {
            if (!CollectionUtils.isEmpty(toCheck)) {
                if (maxOccurrences != -1 && toCheck.size() > maxOccurrences) {
                    return notValid(context, errorMsgIfMaxOccurrencesExceeded);
                }
                if (minOccurrences > 0 && toCheck.size() < minOccurrences) {
                    return notValid(context, errorMsgIfMinOccurrences);
                }
            }
            return true;
        }
        return false;
    }

    protected boolean notValid(ConstraintValidatorContext context, String errorMsg) {
        return ValidationUtils.addConstraintViolation(context, errorMsg);
    }

}
