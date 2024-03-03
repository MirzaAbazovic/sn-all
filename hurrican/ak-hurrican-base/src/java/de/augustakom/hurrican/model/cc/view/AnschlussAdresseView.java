/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2007 16:28:34
 */
package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

public class AnschlussAdresseView extends AbstractCCIDModel {

    private Long kundeNo = null;
    private Long prodAkOrderNo = null;
    private String endstelle = null;
    private String name = null;
    private String plz = null;
    private String ort = null;
    private Long geoId = null;

    public String getEndstelle() {
        return endstelle;
    }

    public void setEndstelle(String endstelle) {
        this.endstelle = endstelle;
    }

    public Long getKundeNo() {
        return kundeNo;
    }

    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public Long getProdAkOrderNo() {
        return prodAkOrderNo;
    }

    public void setProdAkOrderNo(Long prodAkOrderNo) {
        this.prodAkOrderNo = prodAkOrderNo;
    }

    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }


}
