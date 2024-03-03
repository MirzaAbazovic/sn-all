/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 15:55:43
 */
package de.mnet.common.service.status;

import java.util.*;
import com.google.common.base.Joiner;

/**
 * DTO fuer das Ergebnis einer Status-Abfrage
 */
public class ApplicationStatusResult {

    private List<String> errors = new ArrayList<String>();

    public boolean isOk() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        errors.add(error);
    }

    @Override
    public String toString() {
        if (isOk()) {
            return "OK";
        }
        return "Failed: " + Joiner.on(" ").join(errors);
    }

}


