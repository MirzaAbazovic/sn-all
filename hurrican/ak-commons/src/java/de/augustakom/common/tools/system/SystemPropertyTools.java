/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 14:53:34
 */
package de.augustakom.common.tools.system;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.file.FileTools;


/**
 * Hilfsklasse fuer die Arbeit mit System-Properties.
 */
public final class SystemPropertyTools {

    private static final Logger LOGGER = Logger.getLogger(SystemPropertyTools.class);
    private static SystemPropertyTools instance = null;

    private SystemPropertyTools() {
        // singelton instance
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     */
    public static SystemPropertyTools instance() {
        if (instance == null) {
            instance = new SystemPropertyTools();
        }
        return instance;
    }

    /**
     * Setzt ein System-Property mit der Bezeichnung <code>name</code> und dem Wert <code>value</code>. Das Property
     * wird nur gesetzt, wenn 'key' nicht 'blank' und 'value' nicht null ist.
     *
     * @see System#setProperty(String, String)
     */
    public void setSystemProperty(String key, String value) {
        if (StringUtils.isNotBlank(key) && (value != null)) {
            System.setProperty(key, value);
        }
    }

    /**
     * Speichert die Eintraege aus <code>properties</code> als System-Properties.
     *
     * @param properties Angabe der Properties, die in die System-Properties uebertragen werden sollen.
     */
    public void setSystemProperties(Properties properties) {
        if ((properties != null) && !properties.isEmpty()) {
            for (Enumeration<Object> e = properties.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                System.setProperty(key, properties.getProperty(key));
            }
        }
    }

    /**
     * Kopiert die Key-Value Kombinationen eines Property-Files in die System-Properties.
     *
     * @param file Angabe des Property-Files <br> Bsp.: de/augustakom/resources/MyProperty.properties
     */
    public void loadPropertiesFromFile(String file) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(file);
            Properties props = new Properties();
            props.load(is);

            Enumeration<Object> keys = props.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                System.setProperty(key, props.getProperty(key));
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Laedt die Properties aus einer Datei.
     */
    public Properties getPropertiesFromFile(String file) {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(file));
            Properties props = new Properties();
            props.load(is);
            return props;
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        finally {
            FileTools.closeStreamSilent(is);
        }
    }

    /**
     * Laedt die Properties der Datei <code>file</code> und gibt diese in einem Property-Objekt zurueck. <br> Das File
     * muss ueber den ClassLoader der Applikation erreichbar sein!
     *
     * @param file Angabe des Property-Files, das eingelesen werden soll.
     * @return Properties-Objekt mit den eingelesenen Key/Value Paaren.
     */
    public Properties getProperties(String file) {
        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream(file);
            Properties props = new Properties();
            props.load(is);
            return props;
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        finally {
            FileTools.closeStreamSilent(is);
        }
    }

    /**
     * Speichert die Properties in dem angegebenen File.
     */
    public void storeProperties(Properties props, String file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            props.store(out, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            FileTools.closeStreamSilent(out);
        }
    }


    /**
     * Ermittelt den Wert des System-Properties {@code key} und wandelt den String in ein Boolean-Objekt um. Ist das
     * SystemProperty nicht gesetzt, wird {@code defaultValue} zurueck geliefert.
     */
    public static Boolean getSystemPropertyAsBoolean(String key, Boolean defaultValue) {
        String value = System.getProperty(key);
        if (StringUtils.isNotBlank(value)) {
            return Boolean.valueOf(value);
        }

        return defaultValue;
    }

}
