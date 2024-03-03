/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2004
 */
package de.augustakom.common.tools.lang;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Hilfsklasse, um Werte aus Property-Dateien zu lesen und diese ggf. zu modifizieren. <br> Mit Hilfe der
 * Property-Dateien koennen sprachabhaengige Texte auf einfache Weise ausgelesen werden.
 *
 *
 */
public class ResourceReader implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(ResourceReader.class);
    private static final long serialVersionUID = 7321473860417562726L;

    private Locale locale = null;

    private transient ResourceBundle resourceBundle = null;

    /**
     * Konstruktor mit Angabe des zu lesenden Property-Files.
     *
     * @param baseName Angabe des Property-Files, das gelesen werden soll (z.B.: de.augustakom.mypackage.MyPropertyFile).
     */
    public ResourceReader(String baseName) {
        init(baseName, null);
    }

    /**
     * Konstruktor mit Angabe des zu lesenden Property-Files und des gewuenschten Locales.
     *
     * @param baseName Angabe des Property-Files, das gelesen werden soll (z.B.: de.augustakom.mypackage.MyPropertyFile).
     * @param locale   Angabe der Locale
     */
    public ResourceReader(String baseName, Locale locale) {
        init(baseName, locale);
    }

    /* Initialisiert den ResourceReader */
    private void init(String baseName, Locale locale) {
        this.locale = locale;

        if (this.locale == null) {
            this.locale = Locale.getDefault();
        }

        try {
            resourceBundle = ResourceBundle.getBundle(baseName, this.locale);
        }
        catch (ClassCastException e) {
            LOGGER.warn("baseName is a class path, but does not implement ResourceBundle " + baseName);
        }
        catch (MissingResourceException e) {
            LOGGER.warn("could not find resource " + baseName);
        }
    }

    /**
     * Gibt das erzeugte ResourceBundle zurueck.
     *
     * @return Instanz von java.util.ResourceBundle oder null
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Sucht in dem ResourceBundle nach dem angegebenen Key und gibt den zugehoerigen Wert zurueck. <br> Ist der Key
     * nicht vorhanden, wird <code>null</code> zurueck gegeben.<br>
     * Bitte beachten: Die Properties werden vom ResourceBundle <b>immer</b> als 'ISO 8859-1' gelesen. Um also einen
     * Umlaut anzugeben, ist es unbedingt notwendig diesen als UTF-Escape Sequenz zu schreiben!
     * <ul>
     *   <li>&Auml; \u00c4</li>
     *   <li>&auml; \u00e4</li>
     *   <li>&Ouml; \u00d6</li>
     *   <li>&ouml; \u00f6</li>
     *   <li>&Uuml; \u00dc</li>
     *   <li>&uuml; \u00fc</li>
     *   <li>&szlig; \u00df</li>
     * </ul>
     *
     * @param key Angabe des Keys, dessen Value gelesen werden soll.
     * @return zum Key gehoerender Value (oder <code>null</code>)
     */
    public String getValue(String key) {
        String value = null;
        if ((resourceBundle != null) && (key != null)) {
            try {
                value = resourceBundle.getString(key);
            }
            catch (MissingResourceException e) {
                LOGGER.debug("getValue() - Resource nicht gefunden fuer Key >>> " + key);
            }
            catch (ClassCastException e) {
                LOGGER.error("resource does not contain string object for key " + key, e);
            }
        }

        return value;
    }

    /**
     * Sucht in dem ResourceBundle nach dem angegebenen Key. Sind in dem gefundenen String Platzhalter vorhanden, werden
     * diese durch die angegebenen Parameter ersetzt. <br> Die Angabe der Platzhalter erfolgt durch {x} wobei x durch
     * eine fortlaufende Nummer (beginnend bei 0) zu ersetzen ist.
     *
     * @param key    Angabe des Keys, dessen Value gelesen werden soll.
     * @param params Parameter, die an Stelle der Platzhalter geschrieben werden sollen.
     * @return zum Key gehoerender Value (oder <code>null</code>)
     */
    public String getValue(String key, Object[] params) {
        return StringTools.formatString(getValue(key), params, this.locale);
    }
}
