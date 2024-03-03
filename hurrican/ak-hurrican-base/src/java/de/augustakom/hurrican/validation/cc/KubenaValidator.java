/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2005 11:29:43
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.kubena.Kubena;


/**
 * Validator fuer Objekte des Typs <code>Kubena</code>.
 *
 *
 */
public class KubenaValidator extends AbstractValidator {

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return Kubena.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    public void validate(Object object, Errors errors) {
        Kubena k = (Kubena) object;

        if (StringUtils.isBlank(k.getName())) {
            errors.rejectValue("name", "required", "Sie müssen einen Namen für die Kubena definieren.");
        }

        if (k.getKriterium() == null) {
            errors.rejectValue("kriterium", "required", "Sie müssen ein Filter-Kriterium für die Kubena definieren.");
        }
        else if (!NumberTools.isIn(k.getKriterium(), Kubena.getKriterien())) {
            errors.rejectValue("kriterium", "required", "Das angegebene Filter-Kriterium wird nicht unterstützt.");
        }
    }

}


