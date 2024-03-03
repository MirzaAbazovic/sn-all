/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.06.2010 13:49:09
 */
package de.mnet.migration.common.main;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.mnet.migration.base.MigrationBaseTest;
import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.main.MigrationStarter.Exit;
import de.mnet.migration.common.result.TransformationResult;


/**
 * Tests if the hurrican application context is loaded successfully
 *
 *
 */
public class MigrationHurricanAppContextTest extends MigrationBaseTest {

    public static class TestTransformator extends MigrationTransformator<Void> {

        @Autowired
        HVTService hvtService;

        @Override
        public TransformationResult transform(Void row) {
            boolean success = true;
            try {
                if ((hvtService == null) || (hvtService.findHVTGruppen().size() == 0)) {
                    success = false;
                }
            }
            catch (FindException e) {
                success = false;
            }
            if (!success) {
                return error(source(null), "Test", CLASS_DEFAULT, "TEST", null);
            }
            return ok(source(null), target(null), "Test");
        }
    }

    @Test(groups = "integration")
    public void testMigration() {
        final String key = "migration.simulate";
        final String originalValue = System.getProperty(key);

        try {
            System.setProperty(key, "true");
            Exit result = new MigrationHurricanStarter("log4j-test").startMigration(new String[] { },
                    Arrays.asList("de/mnet/migration/common/main/migration-hurrican-app-context-test.xml"), true);
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
