/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.01.2007 13:36:38
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import javax.annotation.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;

/**
 * Modell zur Abbildung eines Innenauftrags. <br>
 *
 *
 */
public class IA extends AbstractCCIDModel implements CCAuftragModel {

    private static final long serialVersionUID = -6657979071957229523L;

    /**
     * Laenge der fortlaufenden Nummer fuer Innenauftraege bei Betriebsraeumen.
     */
    public static final int NUMBER_LENGTH_BETRIEBSRAUM = 8;
    /**
     * Prefix fuer die laufende Nummer des Innenauftrags. Laufende Nummer: 8-stellig: W + 7 Ziffern (z.B. W0005467)
     */
    public static final String PREFIX_SERVICE_ROOM_CODE = "W";

    private Long auftragId = null;
    private Long rangierungsAuftragId = null;
    private String iaNummer = null;
    private Long projectLeadId = null;
    private String kostenstelle = null;
    private String projektbez;

    public Long getAuftragId() {
        return this.auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public String getIaNummer() {
        return this.iaNummer;
    }

    public void setIaNummer(String iaNummer) {
        this.iaNummer = iaNummer;
    }

    public Long getRangierungsAuftragId() {
        return rangierungsAuftragId;
    }

    public void setRangierungsAuftragId(Long rangierungsAuftragId) {
        this.rangierungsAuftragId = rangierungsAuftragId;
    }

    public Long getProjectLeadId() {
        return projectLeadId;
    }

    public void setProjectLeadId(Long projectLeadId) {
        this.projectLeadId = projectLeadId;
    }

    public String getKostenstelle() {
        return kostenstelle;
    }

    /**
     * @param kostenstelle the kostenstelle to set
     */
    public void setKostenstelle(String kostenstelle) {
        this.kostenstelle = kostenstelle;
    }

    @Nullable
    public String getProjektbez() {
        return projektbez;
    }

    public void setProjektbez(String projektbez) {
        this.projektbez = projektbez;
    }
}
