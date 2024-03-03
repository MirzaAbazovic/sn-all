/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.07.2004 14:52:28
 */
package de.augustakom.hurrican.model.cc;


/**
 * Dieses Modell definiert eine Gruppe, die Auftragsaenderungen zusammen fasst.
 *
 *
 */
public class BAVerlaufAenderungGruppe extends AbstractCCIDModel {

    private String beschreibung = null;

    /**
     * @return Returns the beschreibung.
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * @param beschreibung The beschreibung to set.
     */
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

}


