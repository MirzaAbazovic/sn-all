/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 13:49:09
 */
package de.mnet.migration.common.main;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationBaseTest;
import de.mnet.migration.common.main.MigrationStarter.Exit;
import de.mnet.migration.common.util.CollectionUtil;


/**
 *
 */
public class MigrationStarterTest extends MigrationBaseTest {

    @Test(groups = "unit")
    public void testMigration() {
        // TODO refactor test so that system propery 'migration.simulate' does not have to be true -> overriding this system value impacts other tests
        String key = "migration.simulate";
        String originalValue = System.getProperty(key);
        try {
            System.setProperty("migration.simulate", "true");
            Exit result = new MigrationStarter("log4j-test").startMigration(new String[] { }, CollectionUtil.list(
                    "de/mnet/migration/common/main/migration-starter-test.xml"), true);
            Assert.assertEquals(result.getCode(), MigrationStarter.Exit.Code.SUCCESS);
        }
        finally {
            if (originalValue != null) {
                System.setProperty("migration.simulate", originalValue);
            }
            else {
                System.clearProperty("migration.simulate");
            }
        }

    }
}
