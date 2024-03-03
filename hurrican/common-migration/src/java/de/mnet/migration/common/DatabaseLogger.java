/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 10:45:07
 */

package de.mnet.migration.common;

import de.mnet.migration.common.result.MigrationResult;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;

/**
 * Logging-Komponente, die Log-Einträge in eine konfigurierbare Datenbank schreibt.
 */
public interface DatabaseLogger {

    /**
     * Erstellt einen Log-Eintrag in der konfigurierten Datenbank.
     *
     * @param transformationResult Resultat der Transformation
     * @param migrationResult      Vorlaeufiges resultat der kompletten (Teil-)Migration
     * @return {@code true} iff logging did not throw an exception (-> was successful)
     */
    boolean log(TransformationResult transformationResult, MigrationResult migrationResult);

    /**
     * Konfiguration des Loggers
     */
    void setSimulate(Boolean simulate);

    /**
     * Liefert die Anzahl der Log Entries in der Datenbank
     *
     * @param migresultId zu dieser Migresult Id
     * @param severity    mit dieser Severity
     * @return die Anzahl der gefunden Eintraege
     */
    int loadNumberOfLogEntries(Long migresultId, TransformationStatus severity);

    /**
     * Ueberschreibt die Werte im migrationResult mit den echten Anzahlen, die im Mig-Log stehen und liefert dann das
     * geaenderte Objekt zurueck. Achtung: Die Id die im uebergebenen MigrationResult steht darf nicht null sein!
     */
    MigrationResult readMigrationResultFromDatabase(MigrationResult migrationResult);

    /**
     * Erstellt Indizes auf der Datenbank (die gleichen, die in {@link #dropIndices()} entfernt werden)
     */
    void createIndices();

    /**
     * Entfernt Datenbank-Indizes (dies beschleunigt die Migration) (die gleichen, die in {@link #createIndices()}
     * erstellt werden)
     */
    void dropIndices();
}
