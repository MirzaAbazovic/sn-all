/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.08.2009 16:08:36
 */
package de.mnet.migration.common;

import de.mnet.migration.common.result.MigrationResult;

/**
 * Allgemeines Interface fuer alle Migration-Controller.
 *
 *
 */
public interface MigrationController {

    /**
     * Start der Migration
     *
     * @param migrationSuite Name der Migrations-Suite
     */
    MigrationResult doMigration(String migrationSuite);

    /**
     * @return Name der Migration
     */
    String getMigrationName();
}
