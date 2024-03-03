/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 09:00:08
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.query;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;

/**
 * Modell-Klasse fuer die SO-Data, die bei einem Query vom CPS verwendet wird Query Oberklasse
 */
@XStreamAlias("QUERY")
public class CPSQueryData extends AbstractCPSServiceOrderDataModel {
    @XStreamAlias("GENERAL_PARAMS")
    private CPSQueryGeneralParamsData generalParams;
    @XStreamAlias("INSTANCE")
    private CPSQueryInstanceData instance;

    public CPSQueryData() {
        generalParams = new CPSQueryGeneralParamsData();
        instance = new CPSQueryInstanceData();
    }

    public CPSQueryGeneralParamsData getGeneralParams() {
        return generalParams;
    }

    public void setGeneralParams(CPSQueryGeneralParamsData generalParams) {
        this.generalParams = generalParams;
    }

    public CPSQueryInstanceData getInstance() {
        return instance;
    }

    public void setInstance(CPSQueryInstanceData instance) {
        this.instance = instance;
    }
}


