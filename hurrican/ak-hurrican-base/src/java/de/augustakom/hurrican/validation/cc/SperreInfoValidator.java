/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.2004 08:00:42
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.EMailValidator;
import de.augustakom.hurrican.model.cc.SperreInfo;


/**
 * Validator fuer Objekte des Typs <code>SperreInfo</code>.
 *
 *
 */
public class SperreInfoValidator extends AbstractValidator {

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return SperreInfo.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object object, Errors errors) {
        SperreInfo si = (SperreInfo) object;
        if (!EMailValidator.getInstance().isValid(si.getEmail())) {
            errors.rejectValue("email", "required", "Es wurde keine gültige EMail-Adresse für die Sperr-Info angegeben.");
        }

        if (si.getAbteilungId() == null || si.getAbteilungId().intValue() <= 0) {
            errors.rejectValue("abteilungId", "required", "Die Sperr-Info muss einer Abteilung zugeordnet werden.");
        }
    }

}


