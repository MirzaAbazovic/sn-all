/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2012 15:09:13
 */
package de.augustakom.hurrican.model.cc;

/**
 *
 */
@SuppressWarnings("unused")
public class KvzSperreBuilder extends AbstractCCIDModelBuilder<KvzSperreBuilder, KvzSperre> {

    private String onkz = "089";
    private Integer asb = new Integer(45);
    private String kvzNummer = randomString(5);

    public KvzSperreBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public KvzSperreBuilder withAsb(Integer asb) {
        this.asb = asb;
        return this;
    }

    public KvzSperreBuilder withKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
        return this;
    }
}


