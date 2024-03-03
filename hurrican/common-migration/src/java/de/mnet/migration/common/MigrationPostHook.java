/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2010 08:54:27
 */

package de.mnet.migration.common;

import de.mnet.migration.common.result.MigrationResult;


/**
 * Allgemeines Interface für alle PostHooks.
 *
 *
 */
public interface MigrationPostHook {

    /**
     * Führt den Posthook auf dem gegebenen Result aus.
     *
     * @param result
     * @return das aktualisierte MigrationResult nach der Manipulation durch den PostHook
     */
    MigrationResult call(MigrationResult result);
}
