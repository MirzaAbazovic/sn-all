/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 09:03:32
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.query;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;

/**
 * Modell-Klasse fuer die SO-Data, die bei einem Query vom CPS verwendet wird Sektion zur Abfrage der Hardware
 */
@XStreamAlias("INSTANCE")
public class CPSQueryInstanceData extends AbstractCPSServiceOrderDataModel {
    @XStreamAlias("ID")
    private CPSQueryIdData id;
    @XStreamAlias("ENTRY")
    private CPSQueryEntryData entry;

    public CPSQueryInstanceData() {
        id = new CPSQueryIdData();
        entry = new CPSQueryEntryData();
    }

    public CPSQueryIdData getId() {
        return id;
    }

    public void setId(CPSQueryIdData id) {
        this.id = id;
    }

    public CPSQueryEntryData getEntry() {
        return entry;
    }

    public void setEntry(CPSQueryEntryData entry) {
        this.entry = entry;
    }
}


