/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2008 15:23:17
 */
package de.augustakom.hurrican.model.cc.hardware;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell fuer eine n:m Relation. Sollte nach Moeglichkeit nicht benutzt werden und TODO mit einem Upgrade von Hibernate
 * entfallen.
 *
 *
 */
public class HwBaugruppeToPhysikTyp extends AbstractCCIDModel {

    private Long hwBaugruppenTypId = null;
    public static final String HW_BAUGRUPPEN_TYP_ID = "hwBaugruppenTypId";
    private Long physikTypId = null;
    public static final String PHYSIK_TYP_ID = "physikTypId";

    public Long getHwBaugruppenTypId() {
        return hwBaugruppenTypId;
    }

    public void setHwBaugruppenTypId(Long hwBaugruppenTypId) {
        this.hwBaugruppenTypId = hwBaugruppenTypId;
    }

    public Long getPhysikTypId() {
        return physikTypId;
    }

    public void setPhysikTypId(Long physikTypId) {
        this.physikTypId = physikTypId;
    }
}


