/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 09:05:04
 */
package de.augustakom.hurrican.model.cc.view;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * View-Modell, um die Aderquerschnitte und Leitungslaengen fuer eine best. Strasse (Endstelle) darzustellen.
 *
 *
 */
public class AQSView extends AbstractCCModel implements DebugModel, CCAuftragModel {

    private String endstelle = null;
    private String aqs = null;
    private String ll = null;
    private Long auftragId = null;

    public AQSView() {
        super();
    }

    public AQSView(String endstelle, String aqs, String ll, Long auftragId) {
        super();
        this.endstelle = endstelle;
        this.aqs = aqs;
        this.ll = ll;
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the aqs.
     */
    public String getAqs() {
        return aqs;
    }

    /**
     * @param aqs The aqs to set.
     */
    public void setAqs(String aqs) {
        this.aqs = aqs;
    }

    /**
     * @return Returns the endstelle.
     */
    public String getEndstelle() {
        return endstelle;
    }

    /**
     * @param endstelle The endstelle to set.
     */
    public void setEndstelle(String endstelle) {
        this.endstelle = endstelle;
    }

    /**
     * @return Returns the ll.
     */
    public String getLl() {
        return ll;
    }

    /**
     * @param ll The ll to set.
     */
    public void setLl(String ll) {
        this.ll = ll;
    }

    public Long getAuftragId() {
        return auftragId;
    }


    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + AQSView.class.getName());
            logger.debug("  Endstelle: " + getEndstelle());
            logger.debug("  AQS      : " + getAqs());
            logger.debug("  LL       : " + getLl());
            logger.debug("  AuftragID: " + getAuftragId());
        }
    }
}


