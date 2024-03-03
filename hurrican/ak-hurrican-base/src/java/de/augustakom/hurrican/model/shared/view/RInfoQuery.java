/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2007 13:28:03
 */
package de.augustakom.hurrican.model.shared.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Query-Objekt fuer die Suche ueber best. Rechnungsdaten.
 *
 *
 */
public class RInfoQuery extends AbstractHurricanQuery implements KundenModel {

    private String sapDebitorNo = null;
    private Long rInfoNoOrig = null;
    private Long kundeNo = null;

    /**
     * @return rInfoNoOrig
     */
    public Long getRInfoNoOrig() {
        return rInfoNoOrig;
    }

    /**
     * @param infoNoOrig Festzulegender rInfoNoOrig
     */
    public void setRInfoNoOrig(Long infoNoOrig) {
        rInfoNoOrig = infoNoOrig;
    }

    /**
     * @return sapDebitorNo
     */
    public String getSapDebitorNo() {
        return sapDebitorNo;
    }

    /**
     * @param sapDebitorNo Festzulegender sapDebitorNo
     */
    public void setSapDebitorNo(String sapDebitorNo) {
        this.sapDebitorNo = sapDebitorNo;
    }

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
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getSapDebitorNo())) {
            return false;
        }
        if (getRInfoNoOrig() != null) {
            return false;
        }
        if (getKundeNo() != null) {
            return false;
        }

        return true;
    }
}


