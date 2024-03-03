/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2009 09:51:51
 */
package de.augustakom.hurrican.model.cc.equipment;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Modell-Klasse, um Detail-Angaben fuer eine Port-Generierung zu definieren. Es handelt sich hier kein persistentes
 * Objekt!
 *
 *
 */
public class PortGeneratorDetails extends AbstractCCModel {

    private Long hwBaugruppenId;
    private Boolean createPortsForCombiPhysic;

    public Long getHwBaugruppenId() {
        return hwBaugruppenId;
    }

    public void setHwBaugruppenId(Long hwBaugruppenId) {
        this.hwBaugruppenId = hwBaugruppenId;
    }

    public Boolean getCreatePortsForCombiPhysic() {
        return createPortsForCombiPhysic;
    }

    public void setCreatePortsForCombiPhysic(Boolean createPortsForCombiPhysic) {
        this.createPortsForCombiPhysic = createPortsForCombiPhysic;
    }

}


