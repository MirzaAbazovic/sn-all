/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 09:03:09
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.query;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;

/**
 * Modell-Klasse fuer die SO-Data, die bei einem Query vom CPS verwendet wird Sektion mit den Angaben zum Billing
 * Auftrag etc.
 */
@XStreamAlias("GENERAL_PARAMS")
public class CPSQueryGeneralParamsData extends AbstractCPSServiceOrderDataModel {
    @SuppressWarnings("unused")
    @XStreamAlias("IMMEDIATELY")
    private String immediately = "1";
    @XStreamAlias("TAIFUNNUMBER")
    private Long taifunNumber;
    @XStreamAlias("USERNAME")
    private String userName;

    public Long getTaifunNumber() {
        return taifunNumber;
    }

    public void setTaifunNumber(Long taifunNumber) {
        this.taifunNumber = taifunNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}


