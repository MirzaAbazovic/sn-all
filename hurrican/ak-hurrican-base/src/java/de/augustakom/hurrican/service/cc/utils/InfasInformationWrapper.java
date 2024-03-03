/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2012 09:40:09
 */
package de.augustakom.hurrican.service.cc.utils;


/**
 *
 */
public class InfasInformationWrapper {

    String onkz;
    String asb;
    String kvzNummer;
    String distance;

    public InfasInformationWrapper(String onkz, String asb, String kvzNummer) {
        setOnkz(onkz);
        setAsb(asb);
        setKvzNummer(kvzNummer);
    }

    public InfasInformationWrapper() {
    }

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public String getAsb() {
        return asb;
    }

    public void setAsb(String asb) {
        this.asb = asb;
    }

    public String getKvzNummer() {
        return kvzNummer;
    }

    public void setKvzNummer(String kvzNummer) {
        this.kvzNummer = kvzNummer;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
