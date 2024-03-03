/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2009 07:49:20
 */
package de.augustakom.hurrican.model.billing;


/**
 * Entity-Builder fuer DNTNB Objekte.
 *
 *
 */
@SuppressWarnings("unused")
public class DNTNBBuilder extends BillingEntityBuilder<DNTNBBuilder, DNTNB> {

    private String tnb = null;
    private String name = null;
    private String portKennung = null;
    private String serviceType = null;

    public DNTNBBuilder withTnb(String tnb) {
        this.tnb = tnb;
        return this;
    }

    public DNTNBBuilder withPortKennung(String portKennung) {
        this.portKennung = portKennung;
        return this;
    }
}
