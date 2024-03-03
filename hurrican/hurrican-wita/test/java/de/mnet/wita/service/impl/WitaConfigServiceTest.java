/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2011 10:28:44
 */
package de.mnet.wita.service.impl;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaSendCountBuilder;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = BaseTest.SLOW)
public class WitaConfigServiceTest extends AbstractServiceTest {

    @Autowired
    private WitaConfigService underTest;

    @DataProvider
    public Object[][] witaSendLimitDP() {
        // @formatter:off
        return new Object[][] {
                { GeschaeftsfallTyp.BEREITSTELLUNG, true },
                { GeschaeftsfallTyp.BESTANDSUEBERSICHT, true },
                { GeschaeftsfallTyp.KUENDIGUNG_KUNDE, true },
                { GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG, true },
                { GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, true },
                { GeschaeftsfallTyp.PORTWECHSEL, true },
                { GeschaeftsfallTyp.PRODUKTGRUPPENWECHSEL, true },
                { GeschaeftsfallTyp.PROVIDERWECHSEL, true },
                { GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, true },
                { GeschaeftsfallTyp.VERBUNDLEISTUNG, true },
                { GeschaeftsfallTyp.KUENDIGUNG_TELEKOM, false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "witaSendLimitDP")
    public void findWitaSendLimit(GeschaeftsfallTyp gfTyp, boolean resultExpected) throws FindException {
        WitaSendLimit result = underTest.findWitaSendLimit(gfTyp.name(), KollokationsTyp.HVT, null);
        if (resultExpected) {
            assertNotNull(result);
        }
        else {
            assertNull(result);
        }
    }

    public void getWitaSentCount() throws FindException {
        Long startSentCount = underTest.getWitaSentCount(GeschaeftsfallTyp.BEREITSTELLUNG.name(), KollokationsTyp.HVT,
                null);

        getBuilder(WitaSendCountBuilder.class).setPersist(true).build();
        getBuilder(WitaSendCountBuilder.class).setPersist(true).build();
        getBuilder(WitaSendCountBuilder.class).setPersist(true).withGeschaeftsfallTyp(
                GeschaeftsfallTyp.KUENDIGUNG_KUNDE.name()).build();

        Long result = underTest.getWitaSentCount(GeschaeftsfallTyp.BEREITSTELLUNG.name(), KollokationsTyp.HVT, null);
        assertEquals((result - startSentCount), 2);
    }
}
