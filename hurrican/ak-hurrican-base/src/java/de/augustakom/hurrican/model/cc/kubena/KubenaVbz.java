/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2005 14:27:39
 */
package de.augustakom.hurrican.model.cc.kubena;


/**
 * Modell zur Abbildung der VBZs, die einer Kubena zugeordnet sind.
 *
 *
 */
public class KubenaVbz extends AbstractKubenaRefModel {

    /**
     * Wert fuer den Input-Type 'manuell'
     */
    public static final String INPUT_TYPE_MANUELL = "M";
    /**
     * Wert fuer den Input-Type 'auto'
     */
    public static final String INPUT_TYPE_AUTO = "A";

    private String vbz = null;
    private String input = null;
    private Boolean vorhanden = null;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getVbz() {
        return vbz;
    }

    public void setVbz(String vbz) {
        this.vbz = vbz;
    }

    public Boolean getVorhanden() {
        return vorhanden;
    }

    public void setVorhanden(Boolean vorhanden) {
        this.vorhanden = vorhanden;
    }

}


