/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 13:10:36
 */
package de.mnet.wita.model;

import java.io.*;

/**
 * DTO für zugehörige Hurrican-Aufträge zu einer Aenderung.
 */
public class TalSubOrder implements Serializable {

    private static final long serialVersionUID = 578666226208366611L;
    /**
     * Flag ob der Auftrag ebenfalls geaendert werden soll.
     */
    private Boolean selected = Boolean.TRUE;
    private Long auftragId;
    private String vbz;
    private String dtagPort;
    private Long cbVorgangId;
    private String externeAuftragsnummer;

    public TalSubOrder(Long auftragId, String vbz, String dtagPort,
            Long cbVorgangId, String externeAuftragsnummer) {
        this.auftragId = auftragId;
        this.vbz = vbz;
        this.dtagPort = dtagPort;
        this.cbVorgangId = cbVorgangId;
        this.externeAuftragsnummer = externeAuftragsnummer;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Long getAuftragId() {
        return auftragId;
    }

    public String getVbz() {
        return vbz;
    }

    public String getDtagPort() {
        return dtagPort;
    }

    public Long getCbVorgangId() {
        return cbVorgangId;
    }

    public String getExterneAuftragsnummer() {
        return externeAuftragsnummer;
    }
}
