/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2004 15:10:09
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragStatusModel;


/**
 * View-Modell fuer die Darstellung der wichtigsten Auftrags-, Produkt- und Leitungsdaten.
 *
 *
 */
public class CCAuftragProduktVbzView extends AbstractCCModel implements CCAuftragModel, CCAuftragStatusModel {

    private Long auftragId = null;
    private Long auftragNoOrig = null;
    private String vbz = null;
    private String anschlussart = null;
    private String auftragStatus = null;
    private Long statusId = null;

    /**
     * @return Returns the anschlussart.
     */
    public String getAnschlussart() {
        return anschlussart;
    }

    /**
     * @param anschlussart The anschlussart to set.
     */
    public void setAnschlussart(String anschlussart) {
        this.anschlussart = anschlussart;
    }

    /**
     * @return Returns the auftragId.
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId The auftragId to set.
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return Returns the verbindungsBezeichnung.
     */
    public String getVbz() {
        return vbz;
    }

    /**
     * @param verbindungsBezeichnung The verbindungsBezeichnung to set.
     */
    public void setVbz(String vbz) {
        this.vbz = vbz;
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
     * @return Returns the auftragStatus.
     */
    public String getAuftragStatus() {
        return auftragStatus;
    }

    /**
     * @param auftragStatus The auftragStatus to set.
     */
    public void setAuftragStatus(String auftragStatus) {
        this.auftragStatus = auftragStatus;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragStatusModel#getAuftragStatusId()
     */
    public Long getAuftragStatusId() {
        return statusId;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCAuftragStatusModel#setAuftragStatusId(java.lang.Integer)
     */
    public void setAuftragStatusId(Long statusId) {
        this.statusId = statusId;
    }


}


