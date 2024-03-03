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
public class FTTBHDpoPortImportView extends OltChildPortImportView {

    private String dpo = null;
    private String leiste = null;
    private String stift = null;

    public String getLeiste() {
        return leiste;
    }

    public void setLeiste(String leiste) {
        this.leiste = leiste;
    }

    public String getStift() {
        return stift;
    }

    public void setStift(String stift) {
        this.stift = stift;
    }

    public String getDpo() {
        return dpo;
    }

    public void setDpo(String dpo) {
        this.dpo = dpo;
    }

}
