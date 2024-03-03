/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 16:27:51
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.EndgeraetIp;


/**
 * Validator fuer Objekte des Typs <code>EndgeraetIp</code>.
 *
 *
 */
public class EndgeraetIpValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EndgeraetIp.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        EndgeraetIp toValidate = (EndgeraetIp) object;

        if ((toValidate.getIpAddressRef() == null) || StringUtils.isBlank(toValidate.getIpAddressRef().getAddress())) {
            errors.rejectValue("ipAddressRef", ERRCODE_REQUIRED, "Es muss eine IP-Adresse angegeben werden!");
        }
    }

}


