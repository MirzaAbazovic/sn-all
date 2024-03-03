/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2008 14:36:04
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;


/**
 * Validator fuer Objekte des Typs <code>RSMonitorConfig</code>
 *
 *
 */
public class MonitorConfigValidator extends AbstractValidator {

    /**
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return RSMonitorConfig.class.isAssignableFrom(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    public void validate(Object object, Errors errors) {
        RSMonitorConfig config = (RSMonitorConfig) object;

        if (config.getHvtIdStandort() == null) {
            errors.rejectValue("hvtIdStandort", ERRCODE_REQUIRED, "HVT-Standort-ID ist nicht definiert.");
        }

        if (config.getMonitorType() == null) {
            errors.rejectValue("monitorType", ERRCODE_REQUIRED, "Monitor-Typ ist nicht definiert.");
        }

        if (config.getMinCount() == null) {
            errors.rejectValue("minCount", ERRCODE_REQUIRED, "Schwellwert ist nicht definiert.");
        }

        if (NumberTools.equal(config.getMonitorType(), RSMonitorRun.RS_REF_TYPE_EQ_MONITOR)
                && (config.getEqRangSchnittstelle() == null || config.getEqUEVT() == null)) {
            errors.rejectValue("monitorType", ERRCODE_INVALID, "Parameter passen nicht zum Monitor-Typ.");
        }

        if (NumberTools.equal(config.getMonitorType(), RSMonitorRun.RS_REF_TYPE_RANG_MONITOR)
                && config.getPhysiktyp() == null) {
            errors.rejectValue("monitorType", ERRCODE_INVALID, "Parameter passen nicht zum Monitor-Typ.");
        }
    }

}


