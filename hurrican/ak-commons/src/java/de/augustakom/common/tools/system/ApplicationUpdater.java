/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.2006 08:11:49
 */
package de.augustakom.common.tools.system;

import java.io.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.augustakom.common.tools.exceptions.UpdateException;

/**
 * Klasse implementiert eine Update-Funktion, die anhand einer XML-Datei ueberprueft, ob eine neuere Programmversion zur
 * Verfuegung steht und ruft bei Bedarf eine Batch-Datei auf, die dann das Update durchfuehrt.
 */
public class ApplicationUpdater {

    private static final Logger LOGGER = Logger.getLogger(ApplicationUpdater.class);

    private static final String LOCAL_VERSION_FILE = "hurrican-update.xml";

    // Konstanten für Felder in XML-Datei
    public static final String XML_VERSION = "HurricanVersion";
    public static final String XML_MANDATORY = "Mandatory";
    public static final String XML_VERSION_FILE = "VersionFile";
    public static final String XML_BATCH_WINDOWS = "UpdateWindows";
    public static final String XML_BATCH_LINUX = "UpdateLinux";
    public static final String XML_CMD_WINDOWS = "WindowsCommand";
    public static final String XML_CMD_LINUX = "LinuxShell";

    /**
     * Funktion ueberprueft, ob ein Update noetig ist und stoesst dies bei Bedarf an
     *
     * @param result VersionResult-Objekt mit Update-Informationen
     * @return liefert false, falls Update nicht noetig oder nicht erfolgreich war. Im Erfolgsfall kann kein
     * Rueckgabewert uebermittelt werden, da die laufende Anwendung zum Einspielen des Updates beendet werden muss.
     */
    public static boolean update(VersionResult result) throws UpdateException {
        if (result == null) { return false; }
        try {
            // Starte Update
            if (!result.isAct()) {
                if (StringUtils.isBlank(result.getUpdateCommand())) {
                    throw new UpdateException("Command für Update-Vorgang nicht vorhanden.");
                }

                Runtime.getRuntime().exec(result.getUpdateCommand());
                System.exit(0);
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            throw new UpdateException(e.getMessage(), e);
        }
    }

    /**
     * Funktion ueberprueft die verfuegbare Versionsnummer und liefert ein VersionResult-Objekt zurueck
     *
     * @param msec - Timeout, falls beim Lesen der XML-Datei Probleme auftreten
     * @return VersionResult-Objekt
     *
     */
    public static VersionResult getVersionResult(int msec) throws UpdateException {
        try {
            SAXBuilder builder = new SAXBuilder();
            String currentVersion = null;
            String versionFile = null;

            // Ermittle lokale Version
            Document currentVersionFile = builder.build(LOCAL_VERSION_FILE);
            if (currentVersionFile != null) {
                currentVersion = currentVersionFile.getRootElement().getChildText(XML_VERSION);
                versionFile = currentVersionFile.getRootElement().getChildText(XML_VERSION_FILE);
            }

            // Erzeuge File-Objekt mit Timeout
            File file = getFile(versionFile, msec);

            // Lese XML-Datei
            if (file != null) {
                VersionResult result = new VersionResult();

                Document doc = builder.build(file);
                if (doc != null) {
                    String actVersion = doc.getRootElement().getChildText(XML_VERSION);
                    String updateWindows = doc.getRootElement().getChildText(XML_BATCH_WINDOWS);
                    String updateLinux = doc.getRootElement().getChildText(XML_BATCH_LINUX);
                    String cmdLinux = doc.getRootElement().getChildText(XML_CMD_LINUX);
                    String cmdWindows = doc.getRootElement().getChildText(XML_CMD_WINDOWS);
                    String mandatory = doc.getRootElement().getChildText(XML_MANDATORY);

                    // Setze Versionnummern
                    result.setActVersion(actVersion);
                    result.setCurrentVersion(currentVersion);
                    result.setMandatory(StringUtils.equals(mandatory, "1"));

                    // Setze Flag, ob Update ausgeführt werden muss
                    // Falls Datum der neuen Version nicht lesbar -> kein Update
                    if (result.getActVersion() == null) {
                        result.setAct(true);
                    }
                    //  Falls Datum der aktuellen Version nicht lesbar -> Update
                    else if (result.getCurrentVersion() == null) {
                        result.setAct(false);
                    }
                    else {
                        result.setAct(StringUtils.equals(result.getActVersion(), result.getCurrentVersion()));
                    }

                    result.setUpdateCommand(getUpdateCommand(updateWindows, updateLinux, cmdWindows, cmdLinux));
                    return result;
                }
            }
            return null;
        }
        catch (IllegalStateException | IOException | JDOMException e) {
            throw new UpdateException(e.getMessage(), e);
        }
    }


    /*
     * Funktion ueberprueft das Vorhandensein der Batchdateien
     * und liefert Command-String zurueck, falls Update ausgefuehrt werden kann.
     */
    private static String getUpdateCommand(String updateWindows, String updateLinux, String cmdWindows, String cmdLinux) throws UpdateException {
        // Ermittle Betriebssystem und überprüfe ob die passende Batchdatei existiert.
        if (SystemUtils.IS_OS_WINDOWS) {
            return checkIfBatchFileForWindowsExists(updateWindows, cmdWindows);
        }
        else if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_UNIX) {
            return checkIfBatchFileForLinuxExists(updateLinux, cmdLinux);
        }
        return null;
    }

    private static String checkIfBatchFileForLinuxExists(String updateLinux, String cmdLinux) throws UpdateException {
        if ((new File(updateLinux)).exists()) {
            if (StringUtils.isNotBlank(cmdLinux)) {
                return cmdLinux + " " + updateLinux;
            }
            else {
                throw new UpdateException("Shell-Aufruf nicht vorhanden!");
            }
        }
        else {
            throw new UpdateException("Linux-Shellscript für die Installation des Updates existiert nicht!");
        }
    }

    private static String checkIfBatchFileForWindowsExists(String updateWindows, String cmdWindows) throws UpdateException {
        if ((new File(updateWindows)).exists()) {
            if (StringUtils.isNotBlank(cmdWindows)) {
                return cmdWindows + " " + updateWindows;
            }
            else {
                throw new UpdateException("Command-Aufruf nicht vorhanden!");
            }
        }
        else {
            throw new UpdateException("Windows-Batchdatei für die Installation des Updates existiert nicht!");
        }
    }


    /*
     * Hilfsfunktion zum Erzeugen eines File-Objekts mit Timeout,
     * falls Probleme beim Lesen der Datei auftreten.
     */
    static private File getFile(String fileName, int msec) {
        if (StringUtils.isBlank(fileName)) { return null; }

        FileConnect connect = new FileConnect(fileName);
        Thread t = new Thread(connect, "Connect");

        t.start();

        // Warte eine vorgegebene Zeit auf Thread t
        synchronized (t) {
            try {
                t.wait(msec);
            }
            catch (InterruptedException e) {
                // ignore
            }
        }

        // Falls Thread t in der vorgegebenen Zeit nicht abgeschlossen wurde, beende t
        if (t.isAlive()) {
            t.stop();
            LOGGER.warn("Fehler bei der Ermittlung der bereitstehenden Hurrican-Version");
            return null;
        }
        // Falls t erfolgreich beendet wurde, liefere File-Objekt zurueck
        return (connect.getFile() != null) ? connect.getFile() : null;
    }

    /**
     * Hilfsklasse zum Erzeugen eines File-Objekts
     */
    static class FileConnect implements Runnable {

        private File file = null;
        private String fileName = null;

        public FileConnect(String fileName) {
            super();
            this.fileName = fileName;
        }

        public void run() {
            // Erzeuge File-Objekt für XML-Datei
            try {
                file = new File(fileName);
                if (!file.exists()) {
                    file = null;
                }
            }
            catch (Exception e) {
                // Falls beim ersten Zugriff ein Fehler auftritt, setze File-Objekt zurueck
                file = null;
            }
        }

        /**
         * @return file
         */
        public File getFile() {
            return file;
        }

        /**
         * @param file Festzulegender file
         */
        public void setFile(File file) {
            this.file = file;
        }
    }
}
