/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.06.2005 16:55:14
 */
package de.augustakom.hurrican.validation.cc;


import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.Carrierbestellung;


/**
 * Validator fuer Objekte des Typs <code>CarrierbestellungValidator</code>.
 *
 *
 */
public class CarrierbestellungValidator extends AbstractValidator {

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return Carrierbestellung.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    public void validate(Object object, Errors errors) {
        Carrierbestellung cb = (Carrierbestellung) object;

        if (cb.getCarrier() == null) {
            errors.rejectValue("carrier", "required", "Sie müssen für die Carrierbestellung einen Carrier definieren.");
        }
    }

}


