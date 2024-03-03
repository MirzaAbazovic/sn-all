/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2012 08:01:22
 */
package de.mnet.wita.model;

import java.io.*;

public class AuftragZuordnungDto implements Serializable {

    private static final long serialVersionUID = -4446240843329492846L;

    private boolean selected;
    private Long auftragId;
    private String vbz;
    private Long carrierbestellungId;
    private String dtagVertragsnummer;
    private String dtagLbz;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    public Long getCarrierbestellungId() {
        return carrierbestellungId;
    }

    public void setCarrierbestellungId(Long carrierbestellungId) {
        this.carrierbestellungId = carrierbestellungId;
    }

    public String getDtagVertragsnummer() {
        return dtagVertragsnummer;
    }

    public void setDtagVertragsnummer(String dtagVertragsnummer) {
        this.dtagVertragsnummer = dtagVertragsnummer;
    }

    public String getDtagLbz() {
        return dtagLbz;
    }

    public void setDtagLbz(String dtagLbz) {
        this.dtagLbz = dtagLbz;
    }
}
