/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2004 09:05:04
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.HVTBestellung;


/**
 * Validator fuer Objekte des Typs <code>HVTBestellung</code>
 *
 *
 */
public class HVTBestellungValidator extends AbstractValidator {

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return HVTBestellung.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object object, Errors errors) {
        HVTBestellung hb = (HVTBestellung) object;

        if (hb.getUevtId() == null) {
            errors.rejectValue("uevtId", "required", "Die Bestellung muss einem UEVT zugeordnet sein!");
        }

        if (hb.getAngebotDatum() == null) {
            errors.rejectValue("angebotDatum", "required", "Für die Bestellung muss ein Angebots-Datum definiert sein!");
        }

        if (hb.getPhysiktyp() == null) {
            errors.rejectValue("physiktyp", "required", "Für die Bestellung muss ein Physiktyp ausgewählt sein!");
        }

        if (hb.getAnzahlCuDA() == null) {
            errors.rejectValue("anzahlCuDA", "required", "Für die Bestellung muss eine CuDA-Anzahl definiert werden!");
        }
    }

}


