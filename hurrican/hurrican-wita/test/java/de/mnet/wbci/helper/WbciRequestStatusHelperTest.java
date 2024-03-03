/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.14
 */
package de.mnet.wbci.helper;

import static org.testng.Assert.*;

import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.model.helper.WbciRequestHelper;
import de.mnet.wita.message.GeschaeftsfallTyp;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WbciRequestStatusHelperTest {

    @DataProvider
    public static Object[][] getActiveWbciRequestStatusDataProvider() {
        return new Object[][] {
                { GeschaeftsfallTyp.BEREITSTELLUNG, WbciRequestStatus.AKM_TR_VERSENDET },
                { GeschaeftsfallTyp.BESTANDSUEBERSICHT, WbciRequestStatus.AKM_TR_VERSENDET },
                { GeschaeftsfallTyp.KUENDIGUNG_KUNDE, WbciRequestStatus.AKM_TR_EMPFANGEN },
        };
    }

    @Test(dataProvider = "getActiveWbciRequestStatusDataProvider")
    public void testGetActiveWbciRequestStatus(GeschaeftsfallTyp geschaeftsfallTyp,
            WbciRequestStatus expectedRequestStatus) throws Exception {
        assertEquals(WbciRequestStatusHelper.getActiveWbciRequestStatus(geschaeftsfallTyp), expectedRequestStatus);
    }

    @Test
    public void testExtractTaifunOrderIds() throws Exception {
        Assert.assertEquals("", WbciRequestHelper.extractTaifonOrderIds(new WbciGeschaeftsfallKueMrn()));

        Assert.assertEquals(WbciRequestHelper.extractTaifonOrderIds(new WbciGeschaeftsfallKueMrnBuilder()
                        .withBillingOrderNoOrig(100L)
                        .build()),
                "100 (Hauptauftrag)"
        );

        Assert.assertEquals(WbciRequestHelper.extractTaifonOrderIds(new WbciGeschaeftsfallKueMrnBuilder()
                        .withBillingOrderNoOrig(100L)
                        .withNonBillingRelevantOrderNos(Sets.newHashSet(200L, 300L, 400L))
                        .build()),
                "100 (Hauptauftrag)"
                        + "\n200"
                        + "\n300"
                        + "\n400"
        );
    }
}
