/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 10:48:52
 */
package de.mnet.wita.dao;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static de.mnet.wita.model.KollokationsTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wita.exceptions.WitaConfigException;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaSendCount;
import de.mnet.wita.model.WitaSendCountBuilder;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.model.WitaSendLimitBuilder;

@Test(groups = SERVICE)
public class WitaConfigDaoTest extends AbstractServiceTest {

    @Autowired
    private WitaConfigDao witaConfigDao;

    public void resetSendCountShouldWork() {
        getSentCountShouldWork();

        witaConfigDao.resetWitaSentCount(BEREITSTELLUNG, HVT);

        assertEquals(witaConfigDao.getWitaSentCount(BEREITSTELLUNG.name(), HVT, null), Long.valueOf(0L));
    }

    public void getSentCountShouldWork() {
        WitaSendCount witaSendCount =
                new WitaSendCountBuilder()
                        .withGeschaeftsfallTyp(BEREITSTELLUNG.name())
                        .withKollokationsTyp(HVT)
                        .withSentAt(LocalDate.now())
                        .build();
        witaConfigDao.store(witaSendCount);

        Long sendCount = witaConfigDao.getWitaSentCount(BEREITSTELLUNG.name(), HVT, null);

        assertThat(sendCount, greaterThanOrEqualTo(1L));
    }

    public void getWitaSendCountBereitstellung() {
        checkSendCount(BEREITSTELLUNG, HVT);
    }

    public void getWitaSendCountVerbundleistung() {
        checkSendCount(VERBUNDLEISTUNG, null);
    }

    private void checkSendCount(de.mnet.wita.message.GeschaeftsfallTyp typ, KollokationsTyp kollokationsTyp) {
        Long expectedCount = 1L;

        Long currentCount = witaConfigDao.getWitaSentCount(typ.name(), kollokationsTyp, null);
        if(currentCount != null) {
            expectedCount = currentCount + 1;
        }
        witaConfigDao.store(new WitaSendCountBuilder()
                .withGeschaeftsfallTyp(typ.name())
                .withKollokationsTyp(kollokationsTyp)
                .withSentAt(LocalDate.now())
                .build());
        assertEquals(witaConfigDao.getWitaSentCount(typ.name(), kollokationsTyp, null), expectedCount);
    }

    public void findWitaSendLimitForWbci() {
        witaConfigDao.store(new WitaSendLimitBuilder()
                .withGeschaeftsfallTyp(GeschaeftsfallTyp.VA_UNBEKANNT.name())
                .withKollokationsTyp(null)
                .withSendLimit(1L)
                .withItuCarrierCode(CarrierCode.MNET.getITUCarrierCode())
                .build());
        WitaSendLimit witaSentLimit = witaConfigDao.findWitaSendLimit(GeschaeftsfallTyp.VA_UNBEKANNT.name(), null, CarrierCode.MNET.getITUCarrierCode());
        assertEquals(witaSentLimit.getWitaSendLimit(), new Long(1));
        witaSentLimit = witaConfigDao.findWitaSendLimit(GeschaeftsfallTyp.VA_UNBEKANNT.name(), null, CarrierCode.DTAG.getITUCarrierCode());
        assertNull(witaSentLimit);
    }

    public void findWitaSendLimitForWita() {
        witaConfigDao.store(new WitaSendLimitBuilder()
                .withGeschaeftsfallTyp(UNBEKANNT.name())
                .withKollokationsTyp(HVT)
                .withSendLimit(10L)
                .build());
        WitaSendLimit witaSentLimit = witaConfigDao.findWitaSendLimit(UNBEKANNT.name(), HVT, null);
        assertEquals(witaSentLimit.getWitaSendLimit(), new Long(10));
        witaSentLimit = witaConfigDao.findWitaSendLimit(UNBEKANNT.name(), FTTC_KVZ, null);
        assertNull(witaSentLimit);
    }

    @Test(expectedExceptions = WitaConfigException.class)
    public void findUnknownConfig() {
        witaConfigDao.findWitaConfig("UNKNOWN");
    }

}
