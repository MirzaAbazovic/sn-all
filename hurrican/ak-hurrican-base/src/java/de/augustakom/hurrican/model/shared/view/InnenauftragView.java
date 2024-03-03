/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2009 08:57:22
 */
package de.augustakom.hurrican.model.shared.view;


/**
 * View-Modell fuer die Darstellung von relevanten Daten von Innenauftraegen.
 *
 *
 */
public class InnenauftragView extends DefaultSharedAuftragView {

    private String iaNummer = null;
    private String bedarfsNr = null;
    private String workingType = null;
    private String auftragBemerkung = null;

    /**
     * @return Returns the iaNummer.
     */
    public String getIaNummer() {
        return iaNummer;
    }

    /**
     * @param iaNummer The iaNummer to set.
     */
    public void setIaNummer(String iaNummer) {
        this.iaNummer = iaNummer;
    }

    /**
     * @return Returns the bedarfsNr.
     */
    public String getBedarfsNr() {
        return bedarfsNr;
    }

    /**
     * @param bedarfsNr The bedarfsNr to set.
     */
    public void setBedarfsNr(String bedarfsNr) {
        this.bedarfsNr = bedarfsNr;
    }

    /**
     * @return Returns the workingType.
     */
    public String getWorkingType() {
        return workingType;
    }

    /**
     * @param workingType The workingType to set.
     */
    public void setWorkingType(String workingType) {
        this.workingType = workingType;
    }

    /**
     * @return the auftragBemerkung
     */
    public String getAuftragBemerkung() {
        return auftragBemerkung;
    }

    /**
     * @param auftragBemerkung the auftragBemerkung to set
     */
    public void setAuftragBemerkung(String auftragBemerkung) {
        this.auftragBemerkung = auftragBemerkung;
    }

}


