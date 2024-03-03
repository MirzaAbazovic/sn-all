/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 16:27:51
 */
package de.augustakom.hurrican.validation.cc;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.EQCrossConnection;


/**
 * Validator fuer einzelne Objekte des Typs <code>EQCrossConnection</code>.
 *
 *
 */
public class EQCrossConnectionValidator extends AbstractValidator {

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#supports(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean supports(Class clazz) {
        return EQCrossConnection.class.isAssignableFrom(clazz);
    }

    /**
     * @see de.augustakom.common.tools.validation.AbstractValidator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object object, Errors errors) {
        EQCrossConnection cc = (EQCrossConnection) object;

        if (cc.getCrossConnectionTypeRefId() == null) {
            errors.rejectValue(EQCrossConnection.CROSS_CONNECTION_TYPE_REF_ID, ERRCODE_REQUIRED,
                    "Der Typ der Cross Connection muss ausgewählt werden.");
        }

        if (DateTools.isAfter(cc.getGueltigVon(), cc.getGueltigBis())) {
            errors.rejectValue(EQCrossConnection.GUELTIG_VON, ERRCODE_INVALID,
                    "Das gueltigVon-Datum muss vor dem gueltigBis-Datum liegen");
        }

        if (StringUtils.isEmpty(cc.getUserW())) {
            errors.rejectValue(EQCrossConnection.USER_W, ERRCODE_REQUIRED,
                    "Der Bearbeiter der Cross Connection muss angegeben werden.");
        }

        if (cc.getBrasPoolId() != null) {
            if (cc.getBrasInner() == null) {
                errors.rejectValue(EQCrossConnection.BRAS_INNER, ERRCODE_REQUIRED,
                        "Es wurde ein Bras-Pool aber kein brasInner angegeben.");
            }
            if (cc.getBrasOuter() == null) {
                errors.rejectValue(EQCrossConnection.BRAS_OUTER, ERRCODE_REQUIRED,
                        "Es wurde ein Bras-Pool aber kein brasOuter angegeben.");
            }
        }
    }

}


