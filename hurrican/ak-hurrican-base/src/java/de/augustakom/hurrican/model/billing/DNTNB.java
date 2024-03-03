/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2009 08:19:34
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell-Klasse fuer die Abbildung von Teilnehmer-Netzbetreibern.
 *
 *
 */
public class DNTNB extends AbstractBillingModel {

    private String tnb = null;
    private String name = null;
    private String portKennung = null;
    private String serviceType = null;

    /**
     * @return the tnb
     */
    public String getTnb() {
        return tnb;
    }

    /**
     * @param tnb the tnb to set
     */
    public void setTnb(String tnb) {
        this.tnb = tnb;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the portKennung
     */
    public String getPortKennung() {
        return portKennung;
    }

    /**
     * @param portKennung the portKennung to set
     */
    public void setPortKennung(String portKennung) {
        this.portKennung = portKennung;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

}
