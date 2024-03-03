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
import de.augustakom.hurrican.model.cc.CfgRegularExpression;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RegularExpressionService;


/**
 * Validator fuer Objekte des Typs <code>HWRack</code>
 *
 *
 */
public class HWSubrackValidator extends AbstractValidator {
    private static final Logger LOGGER = Logger.getLogger(HWSubrackValidator.class);

    private RegularExpressionService regularExpressionService;
    private QueryCCService queryCcService;

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class clazz) {
        return HWSubrack.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        HWSubrack subrack = (HWSubrack) object;

        if (subrack.getRackId() == null) {
            errors.rejectValue(HWSubrack.RACK_ID, "required", "Rack ist nicht definiert.");
        }
        if (subrack.getSubrackTyp() == null) {
            errors.rejectValue(HWSubrack.SUBRACK_TYP, "required", "Subrack-Typ ist nicht definiert.");
        }
        else {
            HWRack rack = null;
            try {
                rack = queryCcService.findById(subrack.getRackId(), HWRack.class);
                if (!subrack.getSubrackTyp().getRackTyp().equals(rack.getRackTyp())) {
                    errors.rejectValue(HWSubrack.SUBRACK_TYP, "invalid", "Subrack ist für dieses Rack nicht zulässig.");
                }
            }
            catch (Exception e) {
                LOGGER.error("Error validating subrack type", e);
                errors.rejectValue(HWSubrack.SUBRACK_TYP, "invalid", "Fehler bei der Validierung des Subracks aufgetreten.");
            }

            String result = regularExpressionService.matches(subrack.getSubrackTyp().getHwTypeName(), HWSubrackTyp.class,
                    CfgRegularExpression.Info.SUBRACK_MOD_NUMBER, subrack.getModNumber());
            if (result != null) {
                errors.rejectValue(HWSubrack.MOD_NUMBER, "invalid", result);
            }
        }
    }

    /**
     * Injected
     */
    public void setRegularExpressionService(RegularExpressionService regularExpressionService) {
        this.regularExpressionService = regularExpressionService;
    }

    /**
     * Injected
     */
    public void setQueryCcService(QueryCCService queryCcService) {
        this.queryCcService = queryCcService;
    }
}


