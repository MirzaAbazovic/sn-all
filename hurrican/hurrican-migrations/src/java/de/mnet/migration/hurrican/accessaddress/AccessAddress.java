/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.06.2015
 */
package de.mnet.migration.hurrican.accessaddress;

import de.mnet.migration.common.util.ColumnName;

/**
 * Created by glinkjo on 25.06.2015.
 */
public class AccessAddress {

    @ColumnName("AUFTRAG_ID")
    Long auftragId;
    @ColumnName("AUFTRAG__NO")
    Long auftragNoOrig;
    @ColumnName("ENDSTELLE_ID")
    Long endstelleId;
    @ColumnName("ENDSTELLE_TYP")
    String esTyp;

    @Override
    public String toString() {
        return "{" +
                "auftragId=" + auftragId +
                ", auftragNoOrig=" + auftragNoOrig +
                ", endstelleId=" + endstelleId +
                ", esTyp=" + esTyp +
                '}';
    }

}
