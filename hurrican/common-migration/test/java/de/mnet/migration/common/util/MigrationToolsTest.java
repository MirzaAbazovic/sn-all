/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2009 18:23:09
 */
package de.mnet.migration.common.util;


import static de.mnet.migration.common.MigrationTransformator.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.NumberTools;
import de.mnet.migration.base.MigrationBaseTest;
import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.result.SourceIdList;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


public class MigrationToolsTest extends MigrationBaseTest {

    @Test(groups = { "unit" })
    public void testContainsWarning() {
        List<String> warnMessages = Arrays.asList("WARN: This did nearly work", "INFO: Not so bad.");
        assertTrue(MigrationTools.containsWarning(warnMessages));

        List<String> notWarnMessages = Arrays.asList("INFO: This did nearly work", "INFO: Not so bad.");
        assertFalse(MigrationTools.containsWarning(notWarnMessages));
    }

    @Test(groups = { "unit" })
    public void testCreateStringFromWarnings() {
        List<String> warnMessages = Arrays.asList("WARN: This did nearly work", "INFO: Not so bad.");

        String warnings = MigrationTools.createStringFromWarnings(warnMessages);
        assertTrue(warnings.contains("WARN"));
        assertTrue(warnings.contains("INFO"));

    }

    @Test(groups = { "unit" })
    public void testTransformationOkOrWarning() {
        List<String> warnMessages = Arrays.asList("WARN: This did nearly work", "INFO: Not so bad.");
        SourceIdList sourceValues = MigrationTransformator.source("srcValue");
        TargetIdList destinationValues = MigrationTransformator.target("destValue");
        TransformationResult transformationResult =
                MigrationTools.transformationOkOrWarning(sourceValues, destinationValues, warnMessages);
        assertEquals(transformationResult.getTranformationStatus(),
                TransformationStatus.WARNING);
        assertEquals(transformationResult.getSourceValues(), sourceValues);
        assertEquals(transformationResult.getTargetValues(), destinationValues);
        assertFalse(StringUtils.isBlank(transformationResult.getInfoText()));
    }

    @Test(groups = { "unit" })
    public void testTransformationOkOrWarningOk() {
        List<String> warnMessages = Arrays.asList("INFO: This did nearly work", "INFO: Not so bad.");
        SourceIdList sourceValues = source("srcValue");
        TargetIdList targetValues = target("destValue");
        TransformationResult transformationResult =
                MigrationTools.transformationOkOrWarning(sourceValues, targetValues, warnMessages);
        assertEquals(transformationResult.getTranformationStatus(),
                TransformationStatus.OK);
        assertEquals(transformationResult.getSourceValues(), sourceValues);
        assertEquals(transformationResult.getTargetValues(), targetValues);
    }

    @Test(groups = { "unit" })
    public void testFilter() {
        Collection<Integer> integers = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
        MigrationTools.Predicate<Integer> isThree = new MigrationTools.Predicate<Integer>() {

            @Override
            public boolean evaluate(Integer object) {
                return NumberTools.equal(3, object);
            }
        };
        MigrationTools.filter(integers, isThree);
        assertEquals(integers.size(), 1);
        assertEquals(integers.iterator().next(), Integer.valueOf(3));
    }

}
