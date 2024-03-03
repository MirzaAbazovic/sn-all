/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2006 09:15:15
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.Routing;


/**
 * Validator fuer Objekte des Typs <code>Routing</code>.
 *
 *
 */
public class RoutingValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Routing.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        Routing routing = (Routing) object;

        if ((routing.getDestinationAdressRef() == null) || StringUtils.isBlank(routing.getDestinationAdressRef().getAddress())) {
            errors.rejectValue("destinationAdressRef", "required", "Es muss eine Ziel-Adresse angegeben werden!");
        }

        if (StringUtils.isBlank(routing.getNextHop())) {
            errors.rejectValue("nextHop", "required", "Es muss ein Next Hop angegeben werden!");
        }
    }

}


