/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.08.2010 15:23:01
 */

package de.augustakom.hurrican.model.cc.temp;

import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Produkt;


/**
 * Temp. Modell, das zur Selektion von Auftraegen dient.
 *
 *
 */
public class BASelectSubOrder {

    private Boolean selected;
    private Long auftragId;
    private String anschlussart;

    public BASelectSubOrder(AuftragDaten auftragDaten, Produkt produkt) {
        setSelected(true);
        setAuftragId((auftragDaten != null) ? auftragDaten.getAuftragId() : null);
        setAnschlussart((produkt != null) ? produkt.getAnschlussart() : null);
    }

    public BASelectSubOrder(Long auftragId, String anschlussart) {
        setAuftragId(auftragId);
        setAnschlussart(anschlussart);
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the selected
     */
    public Boolean getSelected() {
        return selected;
    }

    /**
     * @param auftragId the auftragId to set
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return the auftragId
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param anschlussart the anschlussart to set
     */
    public void setAnschlussart(String anschlussart) {
        this.anschlussart = anschlussart;
    }

    /**
     * @return the anschlussart
     */
    public String getAnschlussart() {
        return anschlussart;
    }

}
