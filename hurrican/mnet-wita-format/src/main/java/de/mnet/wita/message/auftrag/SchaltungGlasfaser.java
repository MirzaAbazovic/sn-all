/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:49
 */
package de.mnet.wita.message.auftrag;

import de.mnet.wita.message.MwfEntity;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
public class SchaltungGlasfaser extends MwfEntity {

    private static final long serialVersionUID = -6131389953927943192L;

    private String uevt;
    /**
     * Endverschluss
     */
    private String evs;
    private String kupplung;

    @Override
    public String toString() {
        return "SchaltungGlasfaser [UEVT=" + uevt + ", EVS=" + evs + ", kupplung=" + kupplung + "]";
    }

    public String getUEVT() {
        return uevt;
    }

    public void setUEVT(String uEVT) {
        uevt = uEVT;
    }

    public String getEVS() {
        return evs;
    }

    public void setEVS(String eVS) {
        evs = eVS;
    }

    public String getKupplung() {
        return kupplung;
    }

    public void setKupplung(String kupplung) {
        this.kupplung = kupplung;
    }

}
