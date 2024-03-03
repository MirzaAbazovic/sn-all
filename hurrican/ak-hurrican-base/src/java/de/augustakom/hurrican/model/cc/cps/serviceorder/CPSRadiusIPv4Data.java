/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.05.2009 07:23:59
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse fuer die Darstellung von IP-Adresse des Typs v4.
 *
 *
 */
@XStreamAlias("IPv4")
public class CPSRadiusIPv4Data extends AbstractCPSRadiusIPData {

    @XStreamAlias("FIXEDIPV4")
    private String fixedIPv4 = null;

    /**
     * @return the fixedIPv4
     */
    public String getFixedIPv4() {
        return fixedIPv4;
    }

    /**
     * @param fixedIPv4 the fixedIPv4 to set
     */
    public void setFixedIPv4(String fixedIPv4) {
        this.fixedIPv4 = fixedIPv4;
    }

}


