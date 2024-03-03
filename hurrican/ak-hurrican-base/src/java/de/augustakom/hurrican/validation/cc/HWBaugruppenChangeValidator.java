/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.04.2010 12:33:55
 */
package de.augustakom.hurrican.validation.cc;

import org.springframework.validation.Errors;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;


/**
 * Validator fuer {@link HWBaugruppenChange} Objekte.
 */
public class HWBaugruppenChangeValidator extends AbstractValidator {

    private static final long serialVersionUID = 5865400787832120465L;

    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return HWBaugruppenChange.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        HWBaugruppenChange change = (HWBaugruppenChange) object;

        if (change.getHvtStandort() == null) {
            errors.reject(HWBaugruppenChange.HVT_STANDORT, "Es muss ein Standort fuer die Aenderung angegeben werden!");
        }

        if (change.getPlannedDate() == null) {
            errors.reject(HWBaugruppenChange.PLANNED_DATE, "Es muss ein Datum fuer die Aenderung angegeben werden!");
        }

        if (change.getPlannedFrom() == null) {
            errors.reject(HWBaugruppenChange.PLANNED_FROM, "Es muss ein Benutzer angegeben werden, der die Aenderung erstellt hat!");
        }

        if (change.getChangeType() == null) {
            errors.reject(HWBaugruppenChange.CHANGE_TYPE, "Es muss ein Typ fuer die Aenderung angegeben werden!");
            return;
        }
        else if (NumberTools.equal(change.getChangeType().getId(), HWBaugruppenChange.ChangeType.CHANGE_BG_TYPE.refId())
                && (CollectionTools.isNotEmpty(change.getHwBgChangeCard())
                || CollectionTools.isNotEmpty(change.getHwBgChangeDslamSplit()))) {
            errors.reject(HWBaugruppenChange.CHANGE_TYPE, "Die angegebenen Relationen passen nicht zu dem definierten Aenderungstyp!");
        }
        else if (NumberTools.equal(change.getChangeType().getId(), HWBaugruppenChange.ChangeType.CHANGE_CARD.refId())
                && (CollectionTools.isNotEmpty(change.getHwBgChangeBgType())
                || CollectionTools.isNotEmpty(change.getHwBgChangeDslamSplit()))) {
            errors.reject(HWBaugruppenChange.CHANGE_TYPE, "Die angegebenen Relationen passen nicht zu dem definierten Aenderungstyp!");
        }
        else if (NumberTools.equal(change.getChangeType().getId(), HWBaugruppenChange.ChangeType.MERGE_CARDS.refId())
                && (CollectionTools.isNotEmpty(change.getHwBgChangeBgType())
                || CollectionTools.isNotEmpty(change.getHwBgChangeDslamSplit()))) {
            errors.reject(HWBaugruppenChange.CHANGE_TYPE, "Die angegebenen Relationen passen nicht zu dem definierten Aenderungstyp!");
        }
        else if (NumberTools.equal(change.getChangeType().getId(), HWBaugruppenChange.ChangeType.DSLAM_SPLIT.refId())
                && (CollectionTools.isNotEmpty(change.getHwBgChangeBgType())
                || CollectionTools.isNotEmpty(change.getHwBgChangeCard()))) {
            errors.reject(HWBaugruppenChange.CHANGE_TYPE, "Die angegebenen Relationen passen nicht zu dem definierten Aenderungstyp!");
        }

        validateState(change, errors);
    }

    private void validateState(HWBaugruppenChange change, Errors errors) {
        if (change.getChangeState() == null) {
            errors.reject(HWBaugruppenChange.CHANGE_STATE, "Es muss ein Status fuer die Aenderung angegeben werden!");
            return;
        }

        if ((change.getId() == null)
                && NumberTools.notEqual(change.getChangeState().getId(), HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING.refId())) {
            errors.reject(HWBaugruppenChange.CHANGE_STATE, "Der angegebene Status stimmt nicht!");
        }
        else if ((change.getCancelledAt() != null)
                && NumberTools.notEqual(change.getChangeState().getId(), HWBaugruppenChange.ChangeState.CHANGE_STATE_CANCELLED.refId())) {
            errors.reject(HWBaugruppenChange.CHANGE_STATE, "Der angegebene Status definiert keine Stornierung!");
        }
        else if ((change.getClosedAt() != null)
                && NumberTools.notEqual(change.getChangeState().getId(), HWBaugruppenChange.ChangeState.CHANGE_STATE_CLOSED.refId())) {
            errors.reject(HWBaugruppenChange.CHANGE_STATE, "Der angegebene Status definiert keinen abgeschlossenen Baugruppen-Schwenk!");
        }
        else if ((change.getExecutedAt() != null)
                && NumberTools.isNotIn(change.getChangeState().getId(),
                new Number[] {
                        HWBaugruppenChange.ChangeState.CHANGE_STATE_EXECUTED.refId(),
                        HWBaugruppenChange.ChangeState.CHANGE_STATE_CLOSED.refId() }
        )) {
            errors.reject(HWBaugruppenChange.CHANGE_STATE, "Der angegebene Status definiert nicht, dass der Baugruppen-Schwenk ausgefuehrt wurde!");
        }
    }

}


