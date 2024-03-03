/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2005 08:31:01
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.lang.ResourceReader;
import de.augustakom.common.tools.validation.AbstractValidator;


/**
 * Validator, um die LBZ einer Carrierbestellung fuer den Carrier DTAG zu pruefen.
 *
 *
 */
public class CarrierLbzDTAGValidator extends AbstractValidator {

    private static ResourceReader rr =
            new ResourceReader("de.augustakom.hurrican.validation.cc.resources.CarrierLbzDTAGValidator");

    private String regExp = "[0-9][0-9K][A-Z]/[1-9]\\d{4}/[1-9]\\d{4}/\\d+";

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class clazz) {
        return String.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        String lbz = (String) object;

        if (StringUtils.isNotBlank(lbz) && !lbz.matches(regExp)) {
            errors.reject("lbz", rr.getValue("lbz.not.valid", new Object[] { lbz, regExp }));
        }
    }

}


