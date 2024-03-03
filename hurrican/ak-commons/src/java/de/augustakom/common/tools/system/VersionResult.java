/*
* Copyright (c) 2008 - M-net Telekommunikations GmbH
* All rights reserved.
* -------------------------------------------------------
* File created: 17.03.2008 10:33:59
*/
package de.augustakom.common.tools.system;

/**
 * Klasse enthaelt Update-Informationen mit Daten zur aktuellen Version, sowie zur verfuegbaren Version im Netz.
 */
public class VersionResult {

    // Variable gibt an ob die laufende Anwendung aktuell ist
    private boolean isAct = true;
    // Versionsnummer der laufenden Anwendung
    private String currentVersion = null;
    // Versionsnummer des aktuellsten, bereitstehenden Updates
    private String actVersion = null;
    // Ausfuehrbares Batch-File fuer Update
    private String updateCommand = null;
    // Flag fuer Pflichtupdate
    private boolean mandatory = false;

    /**
     * Getter-Methode für actVersion
     *
     * @return actVersion
     */
    public String getActVersion() {
        return actVersion;
    }

    /**
     * Setter-Methode für actVersion
     *
     * @param actVersion
     */
    public void setActVersion(String actVersion) {
        this.actVersion = actVersion;
    }

    /**
     * Getter-Methode für batchFile
     *
     * @return batchFile
     */
    public String getUpdateCommand() {
        return updateCommand;
    }

    /**
     * Setter-Methode für batchFile
     *
     * @param batchFile
     */
    public void setUpdateCommand(String batchFile) {
        this.updateCommand = batchFile;
    }

    /**
     * Getter-Methode für currentVersion
     *
     * @return currentVersion
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Getter-Methode für currentVersion
     *
     * @param currentVersion
     */
    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    /**
     * Getter-Methode für isAct
     *
     * @return isAct
     */
    public boolean isAct() {
        return isAct;
    }

    /**
     * Setter-Methode für isAct
     *
     * @param isAct
     */
    public void setAct(boolean isAct) {
        this.isAct = isAct;
    }

    /**
     * @return mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * @param mandatory Festzulegender mandatory
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public String toString() {
        return "VersionResult{" +
                ", currentVersion='" + currentVersion + '\'' +
                ", actVersion='" + actVersion + '\'' +
                "isAct=" + isAct +
                ", updateCommand='" + updateCommand + '\'' +
                ", mandatory=" + mandatory +
                '}';
    }
}
