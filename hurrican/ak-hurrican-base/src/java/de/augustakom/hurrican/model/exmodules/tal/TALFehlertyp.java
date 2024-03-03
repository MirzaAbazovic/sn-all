/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2007 17:09:45
 */
package de.augustakom.hurrican.model.exmodules.tal;

import de.augustakom.common.tools.lang.StringTools;


/**
 * Modell definiert eine Rueckmeldung der el. TAL-Bestellung. Bei der Rueckmeldung kann es sich um fehlgeschlagene aber
 * auch um erfolgreiche Bestellungen handeln. Um welchen Typ es sich handelt, ist nur aus der Beschreibung des Fehlers
 * ersichtlich.
 *
 *
 */
public class TALFehlertyp extends AbstractTALModel {

    private static final String TYP_AEA = "AEA";
    private static final String TYP_NAT = "NAT";
    private static final String TYP_SEA = "SEA";

    private String kurzText = null;
    private String klasse = null;
    private String description = null;

    /**
     * Ueberprueft, ob bei einer Kuendigung die Rueckmeldung positiv (return=true) oder negativ (return=false) ist.
     *
     * @param typ der zurueck gemeldete Return-Typ
     * @return true wenn die Kuendigung von DTAG bestaetigt ist.
     *
     */
    public static boolean isCancellationOk(String typ) {
        return StringTools.isIn(typ, new String[] { TYP_AEA, TYP_NAT, TYP_SEA });
    }

    /**
     * @return Returns the kurzText.
     */
    public String getKurzText() {
        return kurzText;
    }

    /**
     * @param kurzText The kurzText to set.
     */
    public void setKurzText(String kurzText) {
        this.kurzText = kurzText;
    }

    /**
     * @return Returns the klasse.
     */
    public String getKlasse() {
        return klasse;
    }

    /**
     * @param klasse The klasse to set.
     */
    public void setKlasse(String klasse) {
        this.klasse = klasse;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

}


