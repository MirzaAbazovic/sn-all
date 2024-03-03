/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 11:16:28
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse fuer die Abbildung der Radius-Daten zur CPS-Provisionierung
 *
 *
 */
@XStreamAlias("RADIUS")
public class CPSRadiusData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("ACCOUNT")
    private CPSRadiusAccountData radiusAccount = null;

    /**
     * @return the radiusAccount
     */
    public CPSRadiusAccountData getRadiusAccount() {
        return radiusAccount;
    }

    /**
     * @param radiusAccount the radiusAccount to set
     */
    public void setRadiusAccount(CPSRadiusAccountData radiusAccount) {
        this.radiusAccount = radiusAccount;
    }

}


