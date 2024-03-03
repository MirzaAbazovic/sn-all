/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2010 09:06:36
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import java.util.*;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import de.augustakom.hurrican.model.cc.cps.serviceorder.converter.CPSSdslDslamPortConverter;


/**
 * Modell-Klasse fuer die Abbildung von SDSL-Daten zur CPS-Provisionierung.
 *
 *
 */
@XStreamAlias("SDSL")
public class CPSSdslData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("DSLAM_NAME")
    private String dslamName = null;
    @XStreamAlias("DSLAM_MANUFACTURER")
    private String dslamManufacturer = null;
    @XStreamAlias("DSLAM_PORT_TYPE")
    private String dslamPortType = null;
    @XStreamAlias("DSLAM_PORTS")
    @XStreamConverter(CPSSdslDslamPortConverter.class)
    private List<CPSSdslDslamPortData> dslamPorts = null;

    public void addCPSSdslDslamPortData(CPSSdslDslamPortData toAdd) {
        if (dslamPorts == null) {
            dslamPorts = new ArrayList<CPSSdslDslamPortData>();
        }
        dslamPorts.add(toAdd);
    }

    public String getDslamName() {
        return dslamName;
    }

    public void setDslamName(String dslamName) {
        this.dslamName = dslamName;
    }

    public String getDslamManufacturer() {
        return dslamManufacturer;
    }

    public void setDslamManufacturer(String dslamManufacturer) {
        this.dslamManufacturer = dslamManufacturer;
    }

    public String getDslamPortType() {
        return dslamPortType;
    }

    public void setDslamPortType(String dslamPortType) {
        this.dslamPortType = dslamPortType;
    }

    public List<CPSSdslDslamPortData> getDslamPorts() {
        return dslamPorts;
    }

    public void setDslamPorts(List<CPSSdslDslamPortData> dslamPorts) {
        this.dslamPorts = dslamPorts;
    }

}


