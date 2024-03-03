/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.10.2007 10:18:31
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.ProduktEQConfig;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.RangierungsAuftragBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.RangierungAdminService;


/**
 * Test fuer <code>RangierungAdminService</code>.
 *
 *
 */
public class RangierungAdminServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(RangierungAdminServiceTest.class);

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.RangierungAdminServiceImpl#createExampleEquipmentsFromProdEqConfig(java.lang.Integer,
     * java.lang.String)}.
     */
    @Test(groups = BaseTest.SERVICE)
    public void testCreateEquipmentsFromProdEqConfig() throws Exception {
        RangierungAdminService ras = getCCService(RangierungAdminService.class);
        List<Equipment> result =
                ras.createExampleEquipmentsFromProdEqConfig(Long.valueOf(450), ProduktEQConfig.EQ_TYP_OUT, true, false);
        assertNotEmpty(result, "Keine Equipment-Examples generiert/gefunden!");

        for (Equipment eq : result) {
            eq.debugModel(LOGGER);
        }
    }

    @DataProvider(name = "testCreateSDHPDHRangierungenDataProvider")
    public Object[][] testCreateSDHPDHRangierungenDataProvider() {
        //@formatter:off
        return new Object[][] {
                { PhysikTyp.PHYSIKTYP_ADSL2P_HUAWEI },
                { PhysikTyp.PHYSIKTYP_ADSL2P_MS_HUAWEI },
        };
    }

    @Test(groups = BaseTest.SERVICE, dataProvider="testCreateSDHPDHRangierungenDataProvider")
    public void testCreateSDHPDHRangierungen(Long physikTypParent) throws Exception {
        RangierungAdminService ras = getCCService(RangierungAdminService.class);
        HVTStandortBuilder hvtBuilder = getBuilder(HVTStandortBuilder.class);
        EquipmentBuilder builder = getBuilder(EquipmentBuilder.class);
        RangierungsAuftragBuilder raBuilder = getBuilder(RangierungsAuftragBuilder.class);

        builder.withHvtStandortBuilder(hvtBuilder);
        RangierungsAuftrag ra = raBuilder.withHvtStandortBuilder(hvtBuilder)
            .withAnzahlPorts(4).withPhysiktypParent(physikTypParent)
            .withAuftragVon("me").withAuftragAm(new Date()).build();

        List<Long> eqCarrierIds = new ArrayList<Long>();
        List<Long> eqPDHInIds = new ArrayList<Long>();
        List<Long> eqPDHOutIds = new ArrayList<Long>();
        List<Long> eqSDHIds = new ArrayList<Long>();
        eqCarrierIds.add(builder.withRangSchnittstelle(RangSchnittstelle.H).build().getId());
        eqCarrierIds.add(builder.withRangSchnittstelle(RangSchnittstelle.H).build().getId());
        eqCarrierIds.add(builder.withRangSchnittstelle(RangSchnittstelle.H).build().getId());
        eqCarrierIds.add(builder.withRangSchnittstelle(RangSchnittstelle.H).build().getId());
        eqPDHInIds.add(builder.withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_PDH_IN).build().getId());
        eqPDHInIds.add(builder.withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_PDH_IN).build().getId());
        eqPDHInIds.add(builder.withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_PDH_IN).build().getId());
        eqPDHInIds.add(builder.withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_PDH_IN).build().getId());
        eqPDHOutIds.add(builder.withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_PDH_OUT).build().getId());
        eqSDHIds.add(builder.withHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_SDH).build().getId());

        Long sessionId = getSessionId();
        assertNotNull(sessionId, "SessionId is null!");

        ras.createSDHPDHRangierung(ra.getId(), eqSDHIds, eqCarrierIds, eqPDHInIds, eqPDHOutIds, null, null, sessionId);

        List<Rangierung> rangierungen = ras.findRangierungen4RA(ra.getId());
        assertNotEmpty(rangierungen, "Keine Rangierungen erzeugt!");
        assertEquals(rangierungen.size(), 5);
    }

}


