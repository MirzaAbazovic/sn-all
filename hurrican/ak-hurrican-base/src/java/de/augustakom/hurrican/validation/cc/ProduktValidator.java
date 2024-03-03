/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2004 09:05:04
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.Produkt;


/**
 * Validator fuer Objekte des Typs <code>Produkt</code>
 *
 *
 */
public class ProduktValidator extends AbstractValidator {

    private static final long serialVersionUID = -5323106614519983357L;

    @Override
    public boolean supports(Class<?> clazz) {
        return Produkt.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Produkt p = (Produkt) object;

        if (p.getId() == null) {
            errors.rejectValue("id", "required", "Dem Produkt muss eine eindeutige ID zugeordnet werden!");
        }

        if ((p.getProduktGruppeId() == null) || (p.getProduktGruppeId() <= 0)) {
            errors.rejectValue("produktGruppeId", "required", "Das Produkt muss einer Produktgruppe zugeordnet werden.");
        }

        if (p.getAktionsId() == null) {
            errors.rejectValue("aktionsId", "required", "Dem Produkt muss eine Aktions-ID zugeordnet werden.");
        }

        if (p.getMinDnCount() == null) {
            errors.rejectValue("minDnCount", "required", "Für das Produkt muss eine minimale Anzahl an möglichen Rufnummern (DNs) definiert werden.");
        }

        if (p.getMaxDnCount() == null) {
            errors.rejectValue("maxDnCount", "required", "Für das Produkt muss eine maximale Anzahl an möglichen Rufnummern (DNs) definiert werden.");
        }

        if (p.getAuftragserstellung() == null) {
            errors.rejectValue("auftragserstellung", "required",
                    "Es muss definiert werden, ob das Produkt eine Auftragserstellung benötigt.");
        }

        if (p.getLeitungsNrAnlegen() == null) {
            errors.rejectValue("leitungsNrAnlegen", "required",
                    "Es muss definiert werden, ob für das Produkt eine Leitungs-Nr. angelegt werden muss.");
        }

        if (p.getBuendelProdukt() == null) {
            errors.rejectValue("buendelProdukt", "required",
                    "Es muss definiert werden, ob es sich bei dem Produkt um ein 'Bündel-Produkt' handelt.");
        }
        if (p.getBuendelBillingHauptauftrag() == null) {
            errors.rejectValue("buendelBillingHauptauftrag", "required",
                    "Es muss definiert werden, ob es sich bei dem Produkt um ein 'Bündel-Produkt' handelt.");
        }

        if (p.getEndstellenTyp() == null) {
            errors.rejectValue("endstellenTyp", "required",
                    "Es muss definiert werden, welche Endstellen das Produkt benötigt.");
        }

        if (DateTools.isAfter(p.getGueltigVon(), p.getGueltigBis())) {
            errors.rejectValue("gueltigBis", "invalid", "Das 'Gültig bis'-Datum muss nach dem 'Gültig von'-Datum liegen.");
        }
    }

}


