/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2006 09:15:15
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.PortForwarding;


/**
 * Validator fuer Objekte des Typs <code>PortForwarding</code>.
 *
 *
 */
public class PortForwardingValidator extends AbstractValidator {

    private static final long serialVersionUID = 6894430154565531781L;

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#supports(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return PortForwarding.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        PortForwarding toValidate = (PortForwarding) object;

        // Required fields

        if (toValidate.getDestIpAddressRef() == null) {
            errors.rejectValue("destIpAddressRef", "required", "Es muss eine Ziel-IP-Adresse angegeben werden!");
        }

        if ((toValidate.getDestIpAddressRef() != null && toValidate.getSourceIpAddressRef() != null)
                && ((toValidate.getDestIpAddressRef().isIPV4() && !toValidate.getSourceIpAddressRef().isIPV4())
                || (toValidate.getDestIpAddressRef().isIPV6() && !toValidate.getSourceIpAddressRef().isIPV6()))
                ) {
            errors.rejectValue("sourceIpAddressRef", "required", "Der Typ der Quell-IP-Addresse muss mit dem Typ der Ziel-IP-Adresse übereinstimmen!");
        }

        Integer destPort = toValidate.getDestPort();
        if (destPort == null) {
            errors.rejectValue("destPort", "required", "Bitte geben Sie den Ziel-Port an");
        }
        else if (isPortOutOfScope(destPort)) {
            errors.rejectValue("destPort", "required", "Der angegebene Ziel-Port ist ungueltig.");
        }

        if (toValidate.getActive() == null) {
            errors.rejectValue("active", "required", "Bitte geben Sie einen Wert fuer 'aktiv' an.");
        }
        if (toValidate.getBearbeiter() == null) {
            errors.rejectValue("bearbeiter", "required", "Der Bearbeiter wurde nicht angegeben");
        }

        // Optional fields
        Integer sourcePort = toValidate.getSourcePort();
        if ((sourcePort != null) && isPortOutOfScope(sourcePort)) {
            errors.rejectValue("sourcePort", "required", "Der angegebene Quell-Port ist ungueltig.");
        }
    }

    private boolean isPortOutOfScope(Integer port) {
        return ((port < -1) || (port > 65535)); // -1 means all ports
    }

}
