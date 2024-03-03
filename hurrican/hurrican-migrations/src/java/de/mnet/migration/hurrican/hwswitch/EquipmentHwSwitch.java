/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2015
 */
package de.mnet.migration.hurrican.hwswitch;

import de.mnet.migration.common.util.ColumnName;

/**
 *
 */
public class EquipmentHwSwitch {

    @ColumnName("EQUIPMENT_ID")
    public Long equipmentId;

    @ColumnName("RANGIER_ID")
    public Long rangierId;

    @ColumnName("AUFTRAG_ID")
    public Long auftragId;
}