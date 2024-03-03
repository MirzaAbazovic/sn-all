/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.03.2015
 */
package de.mnet.migration.hurrican.cchuawei;

import java.util.*;

import de.augustakom.hurrican.service.cc.impl.crossconnect.CcHelper;
import de.mnet.migration.common.util.ColumnName;

/**
 *
 */
public class CcHuawei {

    @ColumnName("NIEDERLASSUNG_ID")
    public Long niederlassungId;
    @ColumnName("EQUIPMENT_ID")
    public Long equipmentId;
    @ColumnName("AUFTRAG_ID")
    public Long auftragId;
    @ColumnName("AUFTRAG_STATUS_ID")
    public Long auftragStatusId;
    @ColumnName("HW_EQN")
    public String hwEqn;
    @ColumnName("MOD_NUMBER")
    public String modelNumber;
    @ColumnName("HW_SCHNITTSTELLE")
    public String hwSchnittstelle;
    @ColumnName("DSLAM_TYPE")
    public String dslamType;
    @ColumnName("REALISIERUNGSTERMIN")
    public Date bauauftragsdatum;

    @Override
    public String toString() {
        return "{" +
                "niederlassungId=" + CcHelper.getNlType(niederlassungId) +
                ", auftragId=" + auftragId +
                ", equipmentId=" + equipmentId +
                ", auftragStatusId=" + auftragStatusId +
                ", hwEqn='" + hwEqn + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", hwSchnittstelle='" + hwSchnittstelle + '\'' +
                ", dslamType='" + dslamType + '\'' +
                ", bauauftragsdatum='" + bauauftragsdatum + '\'' +
                '}';
    }

}
