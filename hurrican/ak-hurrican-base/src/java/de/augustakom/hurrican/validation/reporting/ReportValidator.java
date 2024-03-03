/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.05.2008 09:18:02
 */
package de.augustakom.hurrican.validation.reporting;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.reporting.Report;


/**
 * Validator fuer Objekte des Typs <code>Report</code>.
 *
 *
 */
public class ReportValidator extends AbstractValidator {

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return Report.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    public void validate(Object object, Errors errors) {
        Report toValidate = (Report) object;

        if (toValidate.getName() == null) {
            errors.rejectValue("name", ERRCODE_REQUIRED, "Es muss ein Name eingetragen sein!");
        }

        if (toValidate.getUserw() == null) {
            errors.rejectValue("userw", ERRCODE_REQUIRED, "Kein userw vorhanden!");
        }

        if (toValidate.getType() == null) {
            errors.rejectValue("type", ERRCODE_REQUIRED, "Kein Report-Typ vorhanden!");
        }
    }

}


