/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.11.2004 14:07:41
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;


/**
 * Validator fuer Objekte des Typs <code>Rangierungsmatrix</code>.
 *
 *
 */
public class RangierungsmatrixValidator extends AbstractValidator {

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class clazz) {
        return Rangierungsmatrix.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        Rangierungsmatrix rm = (Rangierungsmatrix) object;

        if ((rm.getProduktId() == null) || (rm.getProduktId().longValue() <= 0)) {
            errors.rejectValue("produktId", "required", "Die Rangierungsmatrix benötigt eine Produkt-ID.");
        }

        if ((rm.getUevtId() == null) || (rm.getUevtId().longValue() <= 0)) {
            errors.rejectValue("uevtId", "required", "Der Rangierungsmatrix muss ein UEVT zugeordnet werden.");
        }

        if ((rm.getProdukt2PhysikTypId() == null) || (rm.getProdukt2PhysikTypId().longValue() <= 0)) {
            errors.rejectValue("produkt2PhysikTypId", "required", "Der Rangierungsmatrix muss die ID einer Produkt-Physiktyp-Zuordnung übergeben werden.");
        }

        if ((rm.getHvtStandortIdZiel() == null) || (rm.getHvtStandortIdZiel().longValue() <= 0)) {
            errors.rejectValue("hvtStandortIdZiel", "required", "Der Rangierungsmatrix muss die ID des HVT-Standorts zugewiesen werden, der als Ziel verwendet werden soll.");
        }

        if (StringUtils.isBlank(rm.getBearbeiter())) {
            errors.rejectValue("bearbeiter", "required", "Der Rangierungsmatrix muss der aktuelle Bearbeiter übergeben werden.");
        }
    }

}


