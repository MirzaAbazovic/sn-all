/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.05.2009 07:23:59
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse fuer die Darstellung von IP-Adresse des Typs v6.
 *
 *
 */
@XStreamAlias("IPv6")
public class CPSRadiusIPv6Data extends AbstractCPSRadiusIPData {

    @XStreamAlias("FIXEDIPV6")
    private String fixedIPv6 = null;

    /**
     * @return the fixedIPv6
     */
    public String getFixedIPv6() {
        return fixedIPv6;
    }

    /**
     * @param fixedIPv6 the fixedIPv6 to set
     */
    public void setFixedIPv6(String fixedIPv6) {
        this.fixedIPv6 = fixedIPv6;
    }

}


