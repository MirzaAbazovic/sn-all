/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 15:43:08
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;


/**
 * View-Modell, um die Auftrag-IDs (von Auftrag, AuftragDaten und AuftragTechnik) und die VerbindungsBezeichnung
 * (Technische Dokumentationsnummer) aus dem CC-System zusammenzufassen.
 *
 *
 */
public class CCAuftragIDsView extends AbstractCCModel implements CCAuftragModel {

    private Long auftragId = null;
    private Long auftragDatenId = null;
    private Long auftragTechnikId = null;
    private Long auftragNoOrig = null;
    private String vbz = null;
    private Long auftragStatusId = null;
    private String auftragStatusText = null;
    private String produktName = null;

    /**
     * @return Returns the auftragDatenId.
     */
    public Long getAuftragDatenId() {
        return auftragDatenId;
    }

    /**
     * @param auftragDatenId The auftragDatenId to set.
     */
    public void setAuftragDatenId(Long auftragDatenId) {
        this.auftragDatenId = auftragDatenId;
    }

    /**
     * @return Returns the auftragId.
     */
    @Override
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    @Override
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the auftragTechnikId.
     */
    public Long getAuftragTechnikId() {
        return auftragTechnikId;
    }

    /**
     * @param auftragTechnikId The auftragTechnikId to set.
     */
    public void setAuftragTechnikId(Long auftragTechnikId) {
        this.auftragTechnikId = auftragTechnikId;
    }

    /**
     * @return Returns the verbindungsBezeichnung.
     */
    public String getVbz() {
        return vbz;
    }

    /**
     * @param vbz The verbindungsBezeichnung to set.
     */
    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

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
     * @return Returns the auftragStatusId.
     */
    public Long getAuftragStatusId() {
        return auftragStatusId;
    }

    /**
     * @param auftragStatusId The auftragStatusId to set.
     */
    public void setAuftragStatusId(Long auftragStatusId) {
        this.auftragStatusId = auftragStatusId;
    }

    public String getProduktName() {
        return produktName;
    }

    public void setProduktName(String produktName) {
        this.produktName = produktName;
    }

}


