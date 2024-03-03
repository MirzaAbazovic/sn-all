/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2004 14:39:32
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.tools.lang.StringTools;


/**
 * Modell enthaelt Daten fuer eine Brief-Anrede.
 *
 *
 */
public class Anrede extends AbstractCCIDModel {

    /**
     * Anrede-Key fuer 'Herr'.
     */
    public static final String KEY_HERR = "HERR";
    /**
     * Anrede-Key fuer 'Frau'.
     */
    public static final String KEY_FRAU = "FRAU";
    /**
     * Anrede-Key fuer 'Firma'.
     */
    public static final String KEY_FIRMA = "FIRMA";
    /**
     * Anrede-Key fuer 'Herr und Frau'.
     */
    public static final String KEY_HERRFRAU = "HERRFRAU";
    /**
     * Anrede-Key fuer 'Frau und Herr'.
     */
    public static final String KEY_FRAUHERR = "FRAUHERR";
    /**
     * Anrede-Key fuer <keine Anrede>.
     */
    public static final String KEY_KEINE = "KEINE";

    private static final String[] ANREDE_KEYS =
            new String[] { KEY_HERR, KEY_FRAU, KEY_FIRMA, KEY_HERRFRAU, KEY_FRAUHERR, KEY_KEINE };

    /**
     * Key fuer die Anredeart, die fuer eine Ansprache (z.B. Sehr geehrter Herr) verwendet wird.
     */
    public static final Long ANREDEART_ANSPRACHE = Long.valueOf(1);
    /**
     * Key fuer die Anredeart, die fuer eine Adresse (z.B. Herrn) verwendet wird.
     */
    public static final Long ANREDEART_ADRESSE = Long.valueOf(2);
    /**
     * Key fuer die Anredeart, die fuer eine kurze Ansprache (z.B. Herr Xxx) verwendet wird.
     */
    public static final Long ANREDEART_ANSPRACHE_SHORT = Long.valueOf(3);

    /**
     * ID fuer die Anrede 'keine Anrede'.
     */
    public static final Long ANREDE_ID_KEINE_ANREDE = Long.valueOf(12);

    private String anredeKey = null;
    private String anrede = null;
    private Long anredeArt = null;
    private Boolean persoenlicheAnrede = null;

    /**
     * Ueberprueft, ob der Wert 'key' ein gueltiger Anrede-Key ist.
     *
     * @param key
     * @return
     *
     */
    public static boolean isValidAnredeKey(String key) {
        return StringTools.isIn(key, ANREDE_KEYS);
    }

    /**
     * @return Returns the anrede.
     */
    public String getAnrede() {
        return anrede;
    }

    /**
     * @param anrede The anrede to set.
     */
    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    /**
     * @return Returns the anredeArt.
     */
    public Long getAnredeArt() {
        return anredeArt;
    }

    /**
     * @param anredeArt The anredeArt to set.
     */
    public void setAnredeArt(Long anredeArt) {
        this.anredeArt = anredeArt;
    }

    /**
     * @return Returns the anredeKey.
     */
    public String getAnredeKey() {
        return anredeKey;
    }

    /**
     * @param anredeKey The anredeKey to set.
     */
    public void setAnredeKey(String anredeKey) {
        this.anredeKey = anredeKey;
    }

    /**
     * @return Returns the persoenlicheAnrede.
     */
    public Boolean getPersoenlicheAnrede() {
        return persoenlicheAnrede;
    }

    /**
     * @return Returns the persoenlicheAnrede as boolean
     */
    public boolean isPersoenlicheAnrede() {
        return (persoenlicheAnrede != null) ? persoenlicheAnrede.booleanValue() : false;
    }

    /**
     * @param persoenlicheAnrede The persoenlicheAnrede to set.
     */
    public void setPersoenlicheAnrede(Boolean persoenlicheAnrede) {
        this.persoenlicheAnrede = persoenlicheAnrede;
    }

    /**
     * @param persoenlicheAnrede The persoenlicheAnrede to set.
     */
    public void setPersoenlicheAnrede(boolean persoenlicheAnrede) {
        this.persoenlicheAnrede = Boolean.valueOf(persoenlicheAnrede);
    }
}


