/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:56
 */
package de.mnet.wita.message.auftrag;

import de.mnet.wita.message.MwfEntity;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
public class SchaltungIsisOpal extends MwfEntity {

    private static final long serialVersionUID = -1020311139424759128L;

    public enum Anschlussart {
        ANALOG,
        ISDN_BASIS,
        PMX;
    }

    private String uevt;
    private Anschlussart anschlussart;

    public String getUEVT() {
        return uevt;
    }

    public void setUEVT(String uEVT) {
        uevt = uEVT;
    }

    public Anschlussart getAnschlussart() {
        return anschlussart;
    }

    public void setAnschlussart(Anschlussart anschlussart) {
        this.anschlussart = anschlussart;
    }

    @Override
    public String toString() {
        return "SchaltungIsisOpal [uevt=" + uevt + ", anschlussart=" + anschlussart + "]";
    }

}
