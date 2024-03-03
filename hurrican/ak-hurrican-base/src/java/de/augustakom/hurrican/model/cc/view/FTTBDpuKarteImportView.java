/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.augustakom.hurrican.model.cc.view;

/**
 *
 */
public class FTTBDpuKarteImportView extends OltChildKarteImportView {

    private String dpu = null;
    private String kartentyp = null;

    public String getKartentyp() {
        return kartentyp;
    }

    public void setKartentyp(String kartentyp) {
        this.kartentyp = kartentyp;
    }

    public String getDpu() {
        return dpu;
    }

    public void setDpu(String dpu) {
        this.dpu = dpu;
    }
}
