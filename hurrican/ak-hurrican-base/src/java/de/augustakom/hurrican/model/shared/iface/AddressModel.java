/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.07.2008 07:52:21
 */
package de.augustakom.hurrican.model.shared.iface;

import java.io.*;


/**
 * Interface fuer Adress-Modelle.
 *
 *
 */
public interface AddressModel extends Serializable {

    /**
     * Laenderkennung fuer Deutschland.
     */
    public static final String LAND_ID_GERMANY = "DE";

    /**
     * Gibt die Strassendaten des Adress-Objekts in einem String zurueck. <br> Es werden dabei folgende Daten
     * zusammengefuegt: <br> - Strasse <br> - Nummer, Hausnummernzusatz <br> - Strassenzusatz <br>
     */
    public String getCombinedStreetData();

    /**
     * Gibt die Namens-Daten des Adress-Objekts in einem String zurueck.
     */
    public String getCombinedNameData();

    /**
     * @return Ort - Ortsteil (wenn kein Ortsteil vorhanden, nur Ort)
     */
    public String getCombinedOrtOrtsteil();

    public String getFormatName();

    public void setFormatName(String formatName);

    public String getName();

    public void setName(String name);

    public String getName2();

    public void setName2(String name2);

    public String getVorname();

    public void setVorname(String vorname);

    public String getVorname2();

    public void setVorname2(String vorname2);

    public String getStrasse();

    public void setStrasse(String strasse);

    public String getStrasseAdd();

    public void setStrasseAdd(String strasseAdd);

    public String getNummer();

    public void setNummer(String nummer);

    public String getHausnummerZusatz();

    public void setHausnummerZusatz(String hausnummerZusatz);

    public String getPostfach();

    public void setPostfach(String postfach);

    public String getPlz();

    /**
     * Gibt die Postleitzahl 'getrimmt' zurueck. <br> Der Trim darf nicht in 'getPlz' erfolgen, da sonst das Objekt als
     * dirty/geaendert angesehen werden wuerde und dadurch ein Update erwartet wird!
     *
     * @return eine 'getrimmte' PLZ (Leerzeichen sind abgeschnitten)
     */
    public String getPlzTrimmed();

    public void setPlz(String plz);

    public String getOrt();

    public void setOrt(String ort);

    public String getOrtsteil();

    public void setOrtsteil(String ortsteil);

    public String getLandId();

    public void setLandId(String landId);

}


