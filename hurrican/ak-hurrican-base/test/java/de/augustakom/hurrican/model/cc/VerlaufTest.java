/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.06.2010 09:39:34
 */
package de.augustakom.hurrican.model.cc;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class VerlaufTest extends BaseTest {

    @DataProvider
    public Object[][] dataProviderIsVerteilt() {
        return new Object[][] {
                { VerlaufStatus.BEI_DISPO, false },
                { VerlaufStatus.STATUS_ERLEDIGT, false },
                { VerlaufStatus.KUENDIGUNG_BEI_DISPO, false },
                { VerlaufStatus.KUENDIGUNG_BEI_TECHNIK, true },
                { VerlaufStatus.RUECKLAEUFER_ZENTRALE_DISPO, true },
        };
    }

    @DataProvider
    public Object[][] dataProviderIsKuendigung() {
        return new Object[][] {
                { VerlaufStatus.BEI_DISPO, false },
                { VerlaufStatus.STATUS_ERLEDIGT, false },
                { VerlaufStatus.KUENDIGUNG_BEI_DISPO, true },
                { VerlaufStatus.KUENDIGUNG_BEI_TECHNIK, true },
                { VerlaufStatus.RUECKLAEUFER_ZENTRALE_DISPO, false },
        };
    }

    @Test(dataProvider = "dataProviderIsVerteilt")
    public void testIsVerteilt(Long verlaufStatus, boolean expectedResult) {
        Verlauf verlauf = new VerlaufBuilder().withVerlaufStatusId(verlaufStatus).setPersist(false).build();
        assertEquals(verlauf.isVerteilt(), expectedResult);
    }


    @Test(dataProvider = "dataProviderIsKuendigung")
    public void testIsKuendigung(Long verlaufStatus, boolean expectedResult) {
        Verlauf verlauf = new VerlaufBuilder().withVerlaufStatusId(verlaufStatus).setPersist(false).build();
        assertEquals(verlauf.isKuendigung(), expectedResult);
    }
}


