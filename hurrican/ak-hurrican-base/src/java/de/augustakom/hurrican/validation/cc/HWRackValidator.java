/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2008 12:20:04
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.hardware.HWRack;


/**
 * Validator fuer Objekte des Typs <code>HWRack</code>
 *
 *
 */
public class HWRackValidator extends AbstractValidator {

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class clazz) {
        return HWRack.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        HWRack rack = (HWRack) object;

        if (StringUtils.isBlank(rack.getRackTyp())) {
            errors.rejectValue("rackTyp", "required", "Rack-Typ ist nicht definiert.");
        }
        if (rack.getHvtIdStandort() == null) {
            errors.rejectValue("hvtIdStandort", "required", "HVT-Standort ist nicht definiert.");
        }
        if (rack.getHwProducer() == null) {
            errors.rejectValue("hwProducer", "required", "Hersteller ist nicht angegeben.");
        }
        if (rack.getGueltigVon() == null) {
            errors.rejectValue("gueltigVon", "required", "Gültig-von-Datum ist nicht gesetzt.");
        }
        if (rack.getGueltigBis() == null) {
            errors.rejectValue("gueltigBis", "required", "Gültig-bis-Datum ist nicht gesetzt.");
        }
    }

}


