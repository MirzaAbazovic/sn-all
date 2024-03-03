/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2007 15:21:14
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell zur Abbildung eines BLZ-Objektes aus Taifun.
 *
 *
 */
public class BLZ extends AbstractBillingModel {

    private Long blzNo = null;
    private String name = null;
    private String plz = null;
    private String ort = null;

    /**
     * @return blzNo
     */
    public Long getBlzNo() {
        return blzNo;
    }

    /**
     * @param blzNo Festzulegender blzNo
     */
    public void setBlzNo(Long blzNo) {
        this.blzNo = blzNo;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Festzulegender name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return ort
     */
    public String getOrt() {
        return ort;
    }

    /**
     * @param ort Festzulegender ort
     */
    public void setOrt(String ort) {
        this.ort = ort;
    }

    /**
     * @return plz
     */
    public String getPlz() {
        return plz;
    }

    /**
     * @param plz Festzulegender plz
     */
    public void setPlz(String plz) {
        this.plz = plz;
    }

}


