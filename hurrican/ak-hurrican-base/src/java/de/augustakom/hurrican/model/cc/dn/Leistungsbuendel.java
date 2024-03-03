/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2006 12:13:44
 */
package de.augustakom.hurrican.model.cc.dn;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell stellt die zuordnug der Leistungenbuendel dar. Dieses Buendel kann einem Produkt zugeordnet werden.
 *
 *
 */

public class Leistungsbuendel extends AbstractCCIDModel {

    private String name = null;
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

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }


}


