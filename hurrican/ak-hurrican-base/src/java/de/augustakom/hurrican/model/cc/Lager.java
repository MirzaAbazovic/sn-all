/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.01.2007 13:47:10
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell zur Darstellung eines Lagers.
 *
 *
 */
public class Lager extends AbstractCCIDModel {

    /**
     * ID fuer das Lager 'Augsburg'.
     */
    public static final Long LAGER_ID_AUGSBURG = Long.valueOf(1);

    private String name = null;
    private String strasse = null;
    private String nummer = null;
    private String plz = null;
    private String ort = null;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the nummer.
     */
    public String getNummer() {
        return this.nummer;
    }

    /**
     * @param nummer The nummer to set.
     */
    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    /**
     * @return Returns the ort.
     */
    public String getOrt() {
        return this.ort;
    }

    /**
     * @param ort The ort to set.
     */
    public void setOrt(String ort) {
        this.ort = ort;
    }

    /**
     * @return Returns the plz.
     */
    public String getPlz() {
        return this.plz;
    }

    /**
     * @param plz The plz to set.
     */
    public void setPlz(String plz) {
        this.plz = plz;
    }

    /**
     * @return Returns the strasse.
     */
    public String getStrasse() {
        return this.strasse;
    }

    /**
     * @param strasse The strasse to set.
     */
    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

}


