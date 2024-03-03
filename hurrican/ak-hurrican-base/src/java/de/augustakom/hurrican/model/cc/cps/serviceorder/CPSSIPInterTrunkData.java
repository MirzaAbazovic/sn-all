/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 12:26:03
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSSIPInterTrunkSwitchConverter;


/**
 * Modell-Klasse fuer die Abbildung der SIP InterTrunk Daten zur CPS-Provisionierung.
 */
@XStreamAlias("TRUNKED_NUMBER")
public class CPSSIPInterTrunkData extends AbstractCpsDnWithCarrierIdData {

    @XStreamAlias("RESELLER_ID")
    private String resellerId;
    @XStreamAlias("SWITCHE")
    @XStreamConverter(CPSSIPInterTrunkSwitchConverter.class)
    private List<String> switche;

    public void addSwitch(String switchToAdd) {
        if (switche == null) {
            switche = new ArrayList<String>();
        }
        switche.add(switchToAdd);
    }

    public String getResellerId() {
        return resellerId;
    }

    public void setResellerId(String resellerId) {
        this.resellerId = resellerId;
    }

    public List<String> getSwitche() {
        return switche;
    }

    public void setSwitche(List<String> switche) {
        this.switche = switche;
    }

}


