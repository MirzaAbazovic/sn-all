/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2009 11:58:55
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse zur Abbildung der IAD-Daten (Endgeraete).
 *
 *
 */
@XStreamAlias("IAD")
public class CPSIADData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("TYPE")
    private String type = null;
    @XStreamAlias("CWMP_ID")
    private String cwmpId = null;
    @XStreamAlias("MANUFACTURER")
    private String manufacturer = null;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the cwmpId
     */
    public String getCwmpId() {
        return cwmpId;
    }

    /**
     * @param cwmpId the cwmpId to set
     */
    public void setCwmpId(String cwmpId) {
        this.cwmpId = cwmpId;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

}


