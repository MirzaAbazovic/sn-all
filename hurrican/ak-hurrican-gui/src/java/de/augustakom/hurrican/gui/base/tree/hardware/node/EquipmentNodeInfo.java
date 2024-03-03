/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.03.2011 11:48:30
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;


/**
 *
 */
public class EquipmentNodeInfo {
    public final Equipment equipment;
    public final PhysikTyp physikTyp;
    public final boolean talActive;

    public EquipmentNodeInfo(Equipment equipment, PhysikTyp physikTyp, boolean talActive) {
        this.equipment = equipment;
        this.physikTyp = physikTyp;
        this.talActive = talActive;
    }
}
