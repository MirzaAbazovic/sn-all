/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2008 12:20:04
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Validator fuer Objekte des Typs <code>HWBaugruppe</code>
 *
 *
 */
public class HWBaugruppeValidator extends AbstractValidator {
    private static final Logger LOGGER = Logger.getLogger(HWBaugruppeValidator.class);

    private QueryCCService queryCcService;

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class clazz) {
        return HWBaugruppe.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        HWBaugruppe baugruppe = (HWBaugruppe) object;

        if (baugruppe.getRackId() == null) {
            errors.rejectValue(HWBaugruppe.RACK_ID, "required", "Rack ist nicht definiert.");
        }
        else {
            if (baugruppe.getSubrackId() != null) {
                HWSubrack subrack = null;
                try {
                    subrack = queryCcService.findById(baugruppe.getSubrackId(), HWSubrack.class);
                    if ((subrack != null) && !baugruppe.getRackId().equals(subrack.getRackId())) {
                        errors.rejectValue(HWBaugruppe.SUBRACK_ID, "invalid", "Subrack ist nicht dem gleichen Rack zugeordnet.");
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Error validating subrack", e);
                    errors.rejectValue(HWBaugruppe.SUBRACK_ID, "invalid", "Fehler bei der Validierung des Subracks aufgetreten.");
                }
            }
        }
        if (baugruppe.getHwBaugruppenTyp() == null) {
            errors.rejectValue(HWBaugruppe.HW_BAUGRUPPEN_TYP, "required", "Baugruppen-Typ ist nicht definiert.");
        }
    }

    /**
     * Injected
     */
    public void setQueryCcService(QueryCCService queryCcService) {
        this.queryCcService = queryCcService;
    }
}


