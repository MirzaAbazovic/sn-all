/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.07.2004 10:42:19
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;


/**
 * Validator fuer Objekte des Typs <code>EndstelleLtgDaten</code>.
 *
 *
 */
public class EndstelleLtgDatenValidator extends AbstractValidator {

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return EndstelleLtgDaten.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object object, Errors errors) {
        EndstelleLtgDaten e = (EndstelleLtgDaten) object;
        if (e.getEndstelleId() == null) {
            errors.rejectValue("endstelleId", "required", "Die Leitungsdaten sind keiner Endstelle zugeordnet.");
        }

        if (e.getSchnittstelleId() == null) {
            errors.rejectValue("schnittstelleId", "required", "Bitte definieren Sie eine Schnittstelle für die Leitungsdaten.");
        }
    }

}


