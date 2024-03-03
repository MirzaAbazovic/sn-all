/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.12.2005 08:23:18
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.AuftragImport;
import de.augustakom.hurrican.model.cc.AuftragImportStatus;


/**
 * Validator-Klasse, um Objekte des Typs <code>AuftragImport</code> zu ueberpruefen.
 *
 *
 */
public class AuftragImportValidator extends AbstractValidator {

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#supports(java.lang.Class)
     */
    public boolean supports(Class clazz) {
        return AuftragImport.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    public void validate(Object object, Errors errors) {
        AuftragImport ai = (AuftragImport) object;

        if (ai.getImportStatus() > AuftragImportStatus.IMPORT_STORNO &&
                ai.getKundeNo() == null) {
            errors.rejectValue("kundeNo", "required",
                    "Fuer den angegebenen Import-Status wird die Kundennummer benoetigt!");
        }

    }

}


