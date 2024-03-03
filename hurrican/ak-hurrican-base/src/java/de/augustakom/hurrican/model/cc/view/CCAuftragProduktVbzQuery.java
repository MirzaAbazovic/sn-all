/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2004 15:13:27
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Query-Objekt fuer die Suche nach Auftrags-, Produkt- und Leitungsdaten.
 *
 *
 */
public class CCAuftragProduktVbzQuery extends AbstractHurricanQuery implements KundenModel {

    private Long kundeNo = null;
    private Long produktId = null;
    private Boolean nurAuftraege4VPN = null;

    /**
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the nurAuftraege4VPN.
     */
    public Boolean getNurAuftraege4VPN() {
        return nurAuftraege4VPN;
    }

    /**
     * @param nurAuftraege4VPN The nurAuftraege4VPN to set.
     */
    public void setNurAuftraege4VPN(Boolean nurAuftraege4VPN) {
        this.nurAuftraege4VPN = nurAuftraege4VPN;
    }

    /**
     * @return Returns the produktId.
     */
    public Long getProduktId() {
        return produktId;
    }

    /**
     * @param produktId The produktId to set.
     */
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getKundeNo() != null) { return false; }
        if (getNurAuftraege4VPN() != null) { return false; }
        if (getProduktId() != null) { return false; }
        return true;
    }

}


