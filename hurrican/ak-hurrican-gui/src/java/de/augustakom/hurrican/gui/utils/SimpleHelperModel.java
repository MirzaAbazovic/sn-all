/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2004 10:04:44
 */
package de.augustakom.hurrican.gui.utils;


/**
 * Einfache Modell-Klasse mit einer ID und einen Text. Das Modell kann z.B. in ComboBoxes verwendet werden, die ihre
 * Daten nicht aus der DB erhalten.
 *
 *
 */
public class SimpleHelperModel {

    private Integer id = null;
    private Long longId = null;
    private String text = null;

    /**
     * Default-Konstruktor.
     */
    public SimpleHelperModel() {
        super();
    }

    /**
     * Konstruktor fuer das Helper-Model
     *
     * @param text anzuzeigender Text
     */
    public SimpleHelperModel(String text) {
        this.text = text;
    }

    /**
     * Konstruktor fuer das Helper-Model
     *
     * @param longId eigentlicher Wert des Modells.
     * @param text   anzuzeigender Text
     */
    public SimpleHelperModel(Long longId, String text) {
        this.longId = longId;
        this.text = text;
    }

    /**
     * Konstruktor fuer das Helper-Model
     *
     * @param id   eigentlicher Wert des Modells.
     * @param text anzuzeigender Text
     */
    public SimpleHelperModel(Integer id, String text) {
        this.id = id;
        this.text = text;
    }

    /**
     * Gibt die ID zurueck.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gibt die Long-ID zurueck.
     */
    public Long getLongId() {
        return longId;
    }

    /**
     * Gibt den anzuzeigenden Text zurueck.
     */
    public String getText() {
        return text;
    }
}


