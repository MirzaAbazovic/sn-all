/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.07.2004 13:21:03
 */
package de.augustakom.hurrican.model.shared.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche ueber best. Carrier-Daten.
 *
 *
 */
public class AuftragCarrierQuery extends AbstractHurricanQuery {

    private String lbz = null;
    private String vtrNr = null;
    private String carrierRefNr = null;

    public String getLbz() {
        return lbz;
    }

    public void setLbz(String lbz) {
        this.lbz = lbz;
    }

    public String getVtrNr() {
        return vtrNr;
    }

    public void setVtrNr(String vtrNr) {
        this.vtrNr = vtrNr;
    }

    public String getCarrierRefNr() {
        return carrierRefNr;
    }

    public void setCarrierRefNr(String carrierRefNr) {
        this.carrierRefNr = carrierRefNr;
    }

    @Override
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getLbz())) {
            return false;
        }
        if (StringUtils.isNotBlank(getVtrNr())) {
            return false;
        }
        if (StringUtils.isNotBlank(getCarrierRefNr())) {
            return false;
        }

        return true;
    }
}


