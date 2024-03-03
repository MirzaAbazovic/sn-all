/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 13:46:28
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;

import de.augustakom.common.model.DebugModel;


/**
 * View-Modell, um Auftrags- und INRufnummer-Daten darzustellen. (Die Informationen werden ueber Billing- und
 * CC-Services zusammengetragen.)
 *
 *
 */
public class AuftragINRufnummerView extends DefaultSharedAuftragView implements DebugModel {

    private String prefix = null;
    private String businessNr = null;
    private Date inbetriebnahme0800 = null;
    private Date kuendigung0800 = null;
    private String ort = null;
    private String auftragStatusText = null;

    /**
     * @return Returns the auftragStatusText.
     */
    public String getAuftragStatusText() {
        return auftragStatusText;
    }

    /**
     * @param auftragStatusText The auftragStatusText to set.
     */
    public void setAuftragStatusText(String auftragStatusText) {
        this.auftragStatusText = auftragStatusText;
    }

    /**
     * @return Returns the inbetriebnahme0800.
     */
    public Date getInbetriebnahme0800() {
        return inbetriebnahme0800;
    }

    /**
     * @param inbetriebnahme0800 The inbetriebnahme0800 to set.
     */
    public void setInbetriebnahme0800(Date inbetriebnahme0800) {
        this.inbetriebnahme0800 = inbetriebnahme0800;
    }

    /**
     * @return Returns the kuendigung0800.
     */
    public Date getKuendigung0800() {
        return kuendigung0800;
    }

    /**
     * @param kuendigung0800 The kuendigung0800 to set.
     */
    public void setKuendigung0800(Date kuendigung0800) {
        this.kuendigung0800 = kuendigung0800;
    }

    /**
     * @return Returns the ort.
     */
    public String getOrt() {
        return ort;
    }

    /**
     * @param ort The ort to set.
     */
    public void setOrt(String ort) {
        this.ort = ort;
    }

    /**
     * @return Returns the businessNr.
     */
    public String getBusinessNr() {
        return this.businessNr;
    }

    /**
     * @param businessNr The businessNr to set.
     */
    public void setBusinessNr(String businessNr) {
        this.businessNr = businessNr;
    }

    /**
     * @return Returns the prefix.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * @param prefix The prefix to set.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}


