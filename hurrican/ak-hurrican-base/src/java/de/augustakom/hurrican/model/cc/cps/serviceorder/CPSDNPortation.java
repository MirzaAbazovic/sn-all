/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2009 17:14:03
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse zur Abbildung einer (abgehenden) Portierungsinformation.
 *
 *
 */
@XStreamAlias("PORTATION")
public class CPSDNPortation extends AbstractCPSDNData {

    /**
     * Konstante fuer 'portationType' gibt eine abgehende Portierung an.
     */
    public static final String PTYPE_PORTATION_GOING = "PORTATION";
    /**
     * Konstante fuer 'portationType' gibt eine AGRU an.
     */
    public static final String PTYPE_PORTATION_AGRU = "AGRU";
    /**
     * Konstante fuer 'portationType' gibt eine Rufumwertung an.
     */
    public static final String PTYPE_PORTATION_RUW = "RUW";

    @XStreamAlias("TARGET")
    private String target = null;
    @XStreamAlias("PORTATION_TYPE")
    private String portationType = null;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getPortationType() {
        return portationType;
    }

    public void setPortationType(String portationType) {
        this.portationType = portationType;
    }

}


