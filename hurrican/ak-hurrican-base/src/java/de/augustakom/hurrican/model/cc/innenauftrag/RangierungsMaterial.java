/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2007 09:05:52
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import de.augustakom.hurrican.model.cc.AbstractCCHistoryModel;


/**
 * Ueber dieses Modell ist definiert, welche Materialien fuer den Einbau einer bestimmten Baugruppe benoetigt werden.
 * (Werden fuer eine Baugruppe mehrere Materialien benoetigt, wird dies ueber mehrere Objekte von diesem Modell
 * abgebildet.)
 *
 *
 */
public class RangierungsMaterial extends AbstractCCHistoryModel {

    private String hwBgTypName = null;
    private String materialNr = null;
    private Integer lfdNr = null;

    public String getHwBgTypName() {
        return hwBgTypName;
    }

    public void setHwBgTypName(String hwBgTypName) {
        this.hwBgTypName = hwBgTypName;
    }

    public String getMaterialNr() {
        return materialNr;
    }

    public void setMaterialNr(String materialNr) {
        this.materialNr = materialNr;
    }

    public Integer getLfdNr() {
        return lfdNr;
    }

    public void setLfdNr(Integer lfdNr) {
        this.lfdNr = lfdNr;
    }

}


