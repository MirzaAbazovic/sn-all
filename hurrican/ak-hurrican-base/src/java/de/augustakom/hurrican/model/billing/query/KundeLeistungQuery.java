/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2005 14:42:43
 */
package de.augustakom.hurrican.model.billing.query;

import java.util.*;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * Query-Objekt, um nach Leistungen zu einem Kunden zu filtern.
 *
 *
 */
public class KundeLeistungQuery extends AbstractHurricanQuery implements KundenModel {

    private Long kundeNo = null;
    private List<Long> oeNoOrigs = null;

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
     * @return Returns the oeNoOrigs.
     */
    public List<Long> getOeNoOrigs() {
        return oeNoOrigs;
    }

    /**
     * @param oeNoOrigs The oeNoOrigs to set.
     */
    public void setOeNoOrigs(List<Long> oeNoOrigs) {
        this.oeNoOrigs = oeNoOrigs;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty() ACHTUNG: Das Query-Objekt ist nur dann
     * 'not empty', wenn eine Kundennummer UND OE-NOs gesetzt sind!
     */
    public boolean isEmpty() {
        if (getKundeNo() != null && getOeNoOrigs() != null && !getOeNoOrigs().isEmpty()) {
            return false;
        }

        return true;
    }

}


