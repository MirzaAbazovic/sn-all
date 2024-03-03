/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 16:27:51
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.EGConfig;


/**
 * Validator fuer Objekte des Typs <code>IPEndgeraetConfig</code>.
 *
 *
 */
public class EGConfigValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EGConfig.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        EGConfig toValidate = (EGConfig) object;

        if (toValidate.getEg2AuftragId() == null) {
            errors.rejectValue("eg2AuftragId", ERRCODE_REQUIRED,
                    "Die Endgeraete-Konfiguration muss einem Endgeraet zugeordnet sein!");
        }
    }

}


