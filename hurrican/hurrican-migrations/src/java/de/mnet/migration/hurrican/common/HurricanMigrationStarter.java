package de.mnet.migration.hurrican.common;

import java.util.*;

import de.mnet.migration.common.main.MigrationHurricanStarter;
import de.mnet.migration.common.util.CollectionUtil;

/**
 * Used by MigrationStarter classes.
 */
public class HurricanMigrationStarter extends MigrationHurricanStarter {

    @Override
    protected List<String> getBaseConfiguration() {
        return CollectionUtil.list(
                "de/mnet/migration/common/resources/base-migration.xml",
                "de/mnet/migration/common/resources/hurrican-migration.xml",
                "de/mnet/migration/hurrican/common/resources/hurrican-migrations.xml"
        );
    }

    @Override
    protected String[] getApplicationContextLocations() {
        return new String[] {
                "de/mnet/migration/common/resources/hurrican-context.xml",
                "de/mnet/migration/common/resources/hurrican-context-wita.xml",
                "de/mnet/migration/hurrican/common/resources/hurrican-migrations-context.xml"
        };
    }

    @Override
    protected List<String> getPropertyFiles() {
        return CollectionUtil.list("common", "hurrican-base", "migration", "hurrican-migrations", "hurrican-web");
    }
}
