/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2010 12:14:03
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.IpRoute;


/**
 * Validator fuer Objekte des Typs {@link IpRoute}
 */
public class IpRouteValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return IpRoute.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        IpRoute toValidate = (IpRoute) object;

        if (toValidate.getDeleted() == null) {
            toValidate.setDeleted(Boolean.FALSE);
        }

        if (toValidate.getAuftragId() == null) {
            errors.rejectValue("auftragId", ERRCODE_REQUIRED, "Auftrags-ID muss definiert werden!");
        }
        if ((toValidate.getIpAddressRef() == null) || StringUtils.isBlank(toValidate.getIpAddressRef().getAddress())) {
            errors.rejectValue("ipAddressRef", ERRCODE_INVALID, "IP-Adresse nicht angegeben!");
        }
        if (toValidate.getMetrik() == null) {
            errors.rejectValue("metrik", ERRCODE_REQUIRED, "Metrik muss definiert werden!");
        }
    }

}


