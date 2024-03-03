/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2009 10:52:03
 */

package de.mnet.migration.common;

import java.util.*;

import de.mnet.migration.common.result.MigrationResult;


/**
 * DAO für Ergebnisse von Migrationen.
 */
public interface MigrationResultDao {

    /**
     * Speichert das Ergebnis einer Migration für eine Kombination aus migrationName und migrationSuite.
     *
     * @param migrationResult Ergebnis einer Migration
     * @param migrationName   Name der Migration
     * @param migrationSuite  Name der Migrations-Suite
     */
    void saveMigrationResult(MigrationResult migrationResult, String migrationSuite);

    MigrationResult mergeMigrationResult(MigrationResult dest, MigrationResult src);

    /**
     * Wurde eine Migration mit der Kombination aus migrationName und migrationSuite bereits erfolgreich durchgeführt.
     *
     * @param migrationName  Name der Migration
     * @param migrationSuite Name der Migrations-Suite
     * @return true falls eine Migration bereits erfolgreich war ansonsten false
     */
    boolean wasMigrationAlreadySuccesful(String migrationName, String migrationSuite);

    /**
     * Läd das letzte Migrations-Ergebnis der Kombination aus Migrationsname und MigrationsSuite.
     *
     * @param migrationName  Name der Migration
     * @param migrationSuite Name der Migrationssuite
     * @return das letzte Migrationsergebnis oder null, falls kein Migrationsergebnis gefunden wurde
     */
    MigrationResult loadLastMigrationResult(String migrationName, String migrationSuite);

    /**
     * Löd die Liste aller Migrations-Ergebnisse
     *
     * @return eine List aller Migrationsergebnisse oder die leere Liste, falls keine Migraionsergebnisse gefunden
     * wurden
     */
    List<MigrationResult> loadAllMigrationResult();

}
