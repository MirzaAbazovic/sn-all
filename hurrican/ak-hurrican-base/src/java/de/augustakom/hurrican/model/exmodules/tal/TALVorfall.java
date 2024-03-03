/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2007 17:04:43
 */
package de.augustakom.hurrican.model.exmodules.tal;

import de.augustakom.common.tools.lang.StringTools;

/**
 * Modell fuer die Abbildung der moeglichen Bestellvorgaenge.
 *
 *
 */
public class TALVorfall extends AbstractTALModel {

    private static final long serialVersionUID = -5803912938308190021L;

    /**
     * Meldungstyp fuer Storno-Rueckmeldungen.
     */
    public static final String MELDUNGSTYP_STR = "STR";

    private String name = null;
    private String typ = null;
    private String header = null;
    private String meldungstyp = null;
    private String vorfall = null;

    /**
     * Prueft, ob der aktuelle UseCase fuer eine Auftragsklammerung moeglich ist.
     */
    public boolean isPossibleUseCase4Klammer() {
        return StringTools.isIn(getTyp(), new String[] { "TALM", "TALN", "TALO" });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMeldungstyp() {
        return meldungstyp;
    }

    public void setMeldungstyp(String meldungstyp) {
        this.meldungstyp = meldungstyp;
    }

    public String getVorfall() {
        return vorfall;
    }

    public void setVorfall(String vorfall) {
        this.vorfall = vorfall;
    }

}
