/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.05.2015
 */
package de.augustakom.hurrican.gui.shared;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Dialog, ueber den ein Equipment einer Rangierung zugeordnet werden kann. Anders als beim normalen
 * {@link de.augustakom.hurrican.gui.shared.AssignEquipmentDialog} wird hier nur ein Plan erstellt.
 * Dies bedeutet, dass keine Änderungen an der Endstelle gemacht werden, es werden lediglich Rangierungen erzeugt und
 * Equipments zugeordnet. Die erzeugten Rangierungen werden als gesperrt markiert mit entsprechendem Hinweis auf die
 * anstehende Planung.
 *
 *
 */
public class AssignEquipmentPlanDialog extends AssignEquipmentDialog {

    private Rangierung rangierungPlan;
    private boolean defaultRangierung;

    private final Long rangierId;
    private final Long rangierIdAdditional;

    public AssignEquipmentPlanDialog(Endstelle endstelle, Long hvtIdStandort, Long rangierId, Long rangierIdAdditional) {
        super(endstelle, hvtIdStandort);

        this.rangierId = rangierId;
        this.rangierIdAdditional = rangierIdAdditional;
    }

    @Override
    protected void validatePreconditions(boolean isDefault) throws ServiceNotFoundException, ValidationException, FindException, HurricanGUIException {
        if (isDefault && rangierId != null) {
            throw new HurricanGUIException("HvtUmzug Detailplan besitzt bereits eine neue Rangierung!");
        }

        if (!isDefault && rangierIdAdditional != null) {
            throw new HurricanGUIException("HvtUmzug Detailplan besitzt bereits eine neue Zusatzrangierung!");
        }
    }

    @Override
    protected Rangierung createRangierung() {
        Rangierung rangierung = super.createRangierung();
        rangierung.setFreigegeben(Rangierung.Freigegeben.gesperrt);

        return rangierung;
    }

    @Override
    protected Rangierung getRangierung() throws FindException {
        return (rangierId != null)
                ? getRangierungsService().findRangierung(rangierId) : null;
    }

    @Override
    protected Rangierung getRangierungAdditional() throws FindException {
        return (rangierIdAdditional != null)
                ? getRangierungsService().findRangierung(rangierIdAdditional) : null;
    }

    @Override
    protected void assignRangierung(Rangierung rangierung, boolean isDefault) throws StoreException {
        // instead of saving new rangierung to endstelle we store rangierung plan locally
        this.rangierungPlan = rangierung;
        this.defaultRangierung = isDefault;
    }

    public Rangierung getRangierungPlan() {
        return rangierungPlan;
    }

    public boolean isDefault() {
        return defaultRangierung;
    }
}
