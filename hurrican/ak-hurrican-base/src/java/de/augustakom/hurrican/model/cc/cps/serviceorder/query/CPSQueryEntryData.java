/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2012 09:04:19
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.query;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;

/**
 * Modell-Klasse fuer die SO-Data, die bei einem Query vom CPS verwendet wird Liste der Werte die abgefragt werden kann
 */
@XStreamAlias("ENTRY")
public class CPSQueryEntryData extends AbstractCPSServiceOrderDataModel {

    // zur Festlegung welcher Wert abgefragt werden soll, muessen die relevanten Felder
    // im Request mit " " versehen werden. Dies ist auch der Grund warum die Felder alle
    // als String realisiert sind.
    @XStreamAlias("MAXATTBRDN")
    private String maxAttBrDn;
    @XStreamAlias("MAXATTBRUP")
    private String maxAttBrUp;

    public boolean hasReasonableValues() {
        if ((getMaxAttBrDn() == null) || (getMaxAttBrDn().length() <= 0) || StringUtils.equals(getMaxAttBrDn(), "0")
                || (getMaxAttBrUp() == null) || (getMaxAttBrUp().length() <= 0) || StringUtils.equals(getMaxAttBrUp(), "0")) {
            return false;
        }
        return true;
    }

    public String getMaxAttBrDn() {
        return maxAttBrDn;
    }

    public void setMaxAttBrDn(String maxAttBrDn) {
        this.maxAttBrDn = maxAttBrDn;
    }

    public String getMaxAttBrUp() {
        return maxAttBrUp;
    }

    public void setMaxAttBrUp(String maxAttBrUp) {
        this.maxAttBrUp = maxAttBrUp;
    }
}


