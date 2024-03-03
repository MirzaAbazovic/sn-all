/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 28.06.2010 14:03:47
  */

package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Feature.FeatureName;
import de.augustakom.hurrican.model.cc.FeatureBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.FeatureService;


/**
 * Service-Test fuer FeatureService.
 *
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class FeatureServiceTest extends AbstractHurricanBaseServiceTest {

    @DataProvider(name = "isFeatureOnlineDataProvider")
    public Object[][] isFeatureOnlineDataProvider() {
        return new Object[][] {
                { FeatureName.DUMMY_ORDER_AUTOMATION, Boolean.TRUE },
                { FeatureName.DUMMY_ORDER_AUTOMATION, Boolean.FALSE },
        };
    }

    @Test(dataProvider = "isFeatureOnlineDataProvider")
    public void testIsFeatureOnline(FeatureName featureName, Boolean featureOnline) {
        FeatureService featureService = getCCService(FeatureService.class);
        // Test fall back to FALSE if feature not in DB
        assertEquals(Boolean.FALSE, featureService.isFeatureOnline(featureName));

        getBuilder(FeatureBuilder.class).withNameAndFlag(featureName, featureOnline).build();

        Boolean result = featureService.isFeatureOnline(featureName);
        assertNotNull(result);
        assertEquals(result, featureOnline);
    }

    @Test(dataProvider = "isFeatureOnlineDataProvider")
    public void testIsFeatureOffline(FeatureName featureName, Boolean featureOnline) {
        FeatureService featureService = getCCService(FeatureService.class);
        // Test fall back to TRUE if feature not in DB
        assertEquals(Boolean.TRUE, featureService.isFeatureOffline(featureName));

        getBuilder(FeatureBuilder.class).withNameAndFlag(featureName, featureOnline).build();

        Boolean result = featureService.isFeatureOffline(featureName);
        assertNotNull(result);
        assertNotEquals(result, featureOnline);
    }
}
