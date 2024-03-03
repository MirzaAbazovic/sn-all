package de.augustakom.hurrican.service.cc.impl;


import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HvtUmzugBuilder;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HvtUmzugService;

/**
 *
 */
@Test(groups = { BaseTest.SERVICE })
public class HvtUmzugServiceTest extends AbstractHurricanBaseServiceTest {

    private HvtUmzugService testling;
    private HVTService hvtService;

    @BeforeMethod
    public void init() {
        testling = getCCService(HvtUmzugService.class);
        hvtService = getCCService(HVTService.class);
    }

    @Test
    public void testCreateAndDisableHvtUmzug() throws Exception {
        HvtUmzug hvtUmzug = getBuilder(HvtUmzugBuilder.class).get();
        final GeoId geoId = getBuilder(GeoIdBuilder.class).withKvz(hvtUmzug.getKvzNr()).get();
        final HvtUmzug result = testling.createHvtUmzug(hvtUmzug);
        assertNotNull(result);
        assertNotNull(result.getKvzSperre().getId());
        assertEquals(result.getHvtStandort(), hvtUmzug.getHvtStandort());
        assertEquals(result.getKvzSperre().getKvzNummer(), hvtUmzug.getKvzNr());
        assertEquals(result.getStatus(), HvtUmzugStatus.OFFEN);
        hvtService = getCCService(HVTService.class);
        final HVTStandort hvtStandort = hvtService.findHVTStandort(hvtUmzug.getHvtStandort());
        assertEquals(result.getKvzSperre().getAsb(), hvtStandort.getDTAGAsb());

        final HvtUmzug disabledUmzug = testling.disableUmzug(result.getId());
        assertEquals(disabledUmzug.getStatus(), HvtUmzugStatus.DEAKTIVIERT);
        flushAndClear();

        assertNull(disabledUmzug.getKvzSperre());
        assertNull(hvtService.findKvzSperre(hvtUmzug.getHvtStandort(), geoId.getId()));
    }

    @Test
    public void testFindAffectedStandorte4Umzug() {
        Set<Long> result = testling.findAffectedStandorte4Umzug();
        // Liste der umgezogenen und umzuziehenden HVTs
        // Dies ist im Moment nur die 761, Sattlerstrasse, (MUC-SATTL-001_089-26)
        assertThat(result, containsInAnyOrder(761L));
    }
}
