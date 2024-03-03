/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 16:27:51
 */
package de.augustakom.hurrican.validation.cc;

import javax.annotation.*;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Validator fuer einzelne Objekte des Typs <code>Ansprechpartner</code>.
 *
 *
 */
public class AnsprechpartnerValidator extends AbstractValidator {

    private CCAddressValidator addressValidator;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#supports(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return Ansprechpartner.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        Ansprechpartner ap = (Ansprechpartner) object;

        if (ap.getAddress() != null) {
            errors.pushNestedPath(Ansprechpartner.ADDRESS);
            addressValidator.validate(ap.getAddress(), errors);
            errors.popNestedPath();
        }
        if (ap.getTypeRefId() == null) {
            errors.rejectValue(Ansprechpartner.TYPE_REF_ID, ERRCODE_REQUIRED, "Ein Ansprechpartner muss einen Typ haben.");
        }
        if (ap.getAuftragId() == null) {
            errors.rejectValue(Ansprechpartner.AUFTRAG_ID, ERRCODE_REQUIRED, "Ein Ansprechpartner muss einem Auftrag zugeordnet sein.");
        }

        try {
            Reference reference = referenceService.findReference(ap.getTypeRefId());
            if ((reference.getIntValue() != null)
                    && !ap.getAddress().getAddressType().equals(Long.valueOf(reference.getIntValue().longValue()))) {
                errors.rejectValue(Ansprechpartner.TYPE_REF_ID, ERRCODE_INVALID, "Der Typ der Adresse stimmt nicht mit dem Typen des Ansprechpartners überein");
            }
        }
        catch (FindException e) {
            errors.rejectValue(Ansprechpartner.TYPE_REF_ID, ERRCODE_INVALID, "Fehler beim Suchen der Ansprechpartner-Typ-Referenz");
        }

    }


    /**
     * Injected
     */
    public void setAddressValidator(CCAddressValidator addressValidator) {
        this.addressValidator = addressValidator;
    }
}


