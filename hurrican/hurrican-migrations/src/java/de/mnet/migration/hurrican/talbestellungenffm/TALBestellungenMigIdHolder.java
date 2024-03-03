/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2015
 */
package de.mnet.migration.hurrican.talbestellungenffm;

import java.util.*;

/**
 * Singleton Id holder for {@link TALBestellungenFFMTransformer} migration
 * Used to control migration flows and avoid double migrations
 *
 */
public class TALBestellungenMigIdHolder {

    private final List<Long> migratedVerlaufIds = new ArrayList<>();

    public TALBestellungenMigIdHolder() {
    }

    private boolean isVerlaufAlreadyMigrated(Long verlaufId) {
        return this.migratedVerlaufIds.contains(verlaufId);
    }

    /**
     * Synchronized checker for verlauf ids. Lists verlauf ids in migration list
     * @param verlaufId
     * @return true if verlauf NOT migrated
     */
    public synchronized boolean checkVerlaufIdAndMigrate(Long verlaufId) {
        if (!isVerlaufAlreadyMigrated(verlaufId)) {
            this.migratedVerlaufIds.add(verlaufId);
            return true;
        } else {
            return false;
        }
    }

}
