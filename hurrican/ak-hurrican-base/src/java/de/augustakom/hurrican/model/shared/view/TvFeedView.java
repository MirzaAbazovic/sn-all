/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.11.2012 17:18:02
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;

/**
 * View-Modell, um Auftrags-, HVT-, Equipment- und HW-Daten darzustellen.
 */
public class TvFeedView {

    private Long geoId;
    private Long auftragsId;
    private String auftragsStatus;
    private Long billingAuftragsId;
    private String techStandortName;
    private String geraetebezeichnung;
    private String portBezeichner;
    private String ontBezeichner;
    private Date geoIdGueltigVon;
    private Date geoIdGueltigBis;

    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    public Long getAuftragsId() {
        return auftragsId;
    }

    public void setAuftragsId(Long auftragsId) {
        this.auftragsId = auftragsId;
    }

    public String getAuftragsStatus() {
        return auftragsStatus;
    }

    public void setAuftragsStatus(String auftragsStatus) {
        this.auftragsStatus = auftragsStatus;
    }

    public Long getBillingAuftragsId() {
        return billingAuftragsId;
    }

    public void setBillingAuftragsId(Long billingAuftragsId) {
        this.billingAuftragsId = billingAuftragsId;
    }

    public String getTechStandortName() {
        return techStandortName;
    }

    public void setTechStandortName(String technStandortName) {
        this.techStandortName = technStandortName;
    }

    public String getGeraetebezeichnung() {
        return geraetebezeichnung;
    }

    public void setGeraetebezeichnung(String geraetebezeichnung) {
        this.geraetebezeichnung = geraetebezeichnung;
    }

    public String getPortBezeichner() {
        return portBezeichner;
    }

    public void setPortBezeichner(String portBezeichner) {
        this.portBezeichner = portBezeichner;
    }

    public String getOntBezeichner() {
        return ontBezeichner;
    }

    public void setOntBezeichner(String ontBezeichner) {
        this.ontBezeichner = ontBezeichner;
    }

    public Date getGeoIdGueltigVon() {
        return geoIdGueltigVon;
    }

    public void setGeoIdGueltigVon(Date geoIdGueltigVon) {
        this.geoIdGueltigVon = geoIdGueltigVon;
    }

    public Date getGeoIdGueltigBis() {
        return geoIdGueltigBis;
    }

    public void setGeoIdGueltigBis(Date geoIdGueltigBis) {
        this.geoIdGueltigBis = geoIdGueltigBis;
    }
}


