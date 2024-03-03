/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.04.2012 09:01:01
 */
package de.augustakom.hurrican.service.cc.fttx.impl;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.HVTTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10Nsp;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CVlan.CVlanProtocoll;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;

@Test(groups = UNIT)
public class VlanCalculatorStrategyTest extends BaseTest {

    private static final Integer OLT_NUMBER = Integer.valueOf(3);
    private static final Integer A10NSP_NUMBER = Integer.valueOf(8);
    private static final Integer GSLAM_SVLAN = Integer.valueOf(3001);

    private HWOlt olt;
    private A10Nsp a10Nsp;
    private HWDslam gslam;

    @BeforeMethod
    public void init() {
        gslam = new HWDslamBuilder().withRackTyp(HWRack.RACK_TYPE_DSLAM)
                .withHwProducerBuilder(new HVTTechnikBuilder().toAlcatel().setPersist(false))
                .withSVlan(GSLAM_SVLAN).setPersist(false).build();
        olt = new HWOltBuilder().withOltNr(OLT_NUMBER).setPersist(false).build();
        a10Nsp = new A10NspBuilder().withNummer(A10NSP_NUMBER).setPersist(false).build();
    }

    @DataProvider
    public Object[][] svlanEkpDataProvider() {
        // @formatter:off
        CVlan hsiUnicastPppoE = new CVlanBuilder().withTyp(CvlanServiceTyp.HSI).withProtocoll(CVlanProtocoll.PPPoE).setPersist(false).build();
        CVlan mcMulticastPppoE = new CVlanBuilder().withTyp(CvlanServiceTyp.MC).withProtocoll(CVlanProtocoll.PPPoE).setPersist(false).build();
        return new Object[][] {
                {HVTTechnik.HUAWEI, "00-07-03-010", hsiUnicastPppoE, Integer.valueOf(157) },
                {HVTTechnik.HUAWEI, "00-07-03-010", mcMulticastPppoE, Integer.valueOf(150) },
                {HVTTechnik.ALCATEL,"01-01-07-01-004", hsiUnicastPppoE, Integer.valueOf(157) },
                {HVTTechnik.ALCATEL,"01-01-07-01-004", mcMulticastPppoE, Integer.valueOf(150) },
            };
    }

    @Test(dataProvider = "svlanEkpDataProvider")
    public void calculateSvlanEkp(Long hvtTechnikId, String hwEqn, CVlan cvlan, Integer expectedResult) {
        Integer oltSlot = Integer.valueOf(new EquipmentBuilder().withHwEQN(hwEqn).build().getHwEQNPart(Equipment.HWEQNPART_FTTX_OLT_GPON_SLOT));
        olt.setHwProducer(hvtTechnikId);
        assertThat(VlanCalculatorStrategy.get(olt).calculateSvlanEkp(a10Nsp.getNummer(), olt.getOltNr(), oltSlot, cvlan, 50), equalTo(expectedResult));
    }


    @DataProvider
    public Object[][] svlanOltDataProvider() {
        // @formatter:off
        CVlan hsiUnicastPppoE = new CVlanBuilder().withTyp(CvlanServiceTyp.HSI).withProtocoll(CVlanProtocoll.PPPoE).setPersist(false).build();
        CVlan mcMulticastPppoE = new CVlanBuilder().withTyp(CvlanServiceTyp.MC).withProtocoll(CVlanProtocoll.PPPoE).setPersist(false).build();
        CVlan hsiUnicastIpoE = new CVlanBuilder().withTyp(CvlanServiceTyp.HSI).withProtocoll(CVlanProtocoll.IPoE).setPersist(false).build();
        CVlan mcMulticastIpoE = new CVlanBuilder().withTyp(CvlanServiceTyp.MC).withProtocoll(CVlanProtocoll.IPoE).setPersist(false).build();
        return new Object[][] {
                {HVTTechnik.HUAWEI, "00-07-03-010", null, hsiUnicastPppoE, Integer.valueOf(407) },
                {HVTTechnik.HUAWEI, "00-07-03-010", null, mcMulticastPppoE, Integer.valueOf(400) },
                {HVTTechnik.ALCATEL, "01-01-07-01-004", null, hsiUnicastPppoE, Integer.valueOf(407) },
                {HVTTechnik.ALCATEL, "01-01-07-01-004", null, mcMulticastPppoE, Integer.valueOf(400) },
                {HVTTechnik.ALCATEL, "01-01-07-01-004", null, hsiUnicastIpoE, Integer.valueOf(407) },
                {HVTTechnik.ALCATEL, "01-01-07-01-004", 2000, hsiUnicastIpoE, Integer.valueOf(2407) },
                {HVTTechnik.ALCATEL, "01-01-07-01-004", 2000, mcMulticastIpoE, Integer.valueOf(2400) },
            };
    }

    @Test(dataProvider = "svlanOltDataProvider")
    public void calculateSvlanOlt(Long hvtTechnikId, String hwEqn, Integer svlanOffset, CVlan cvlan, Integer expectedResult) {
        Integer oltSlot = Integer.valueOf(new EquipmentBuilder().withHwEQN(hwEqn).build().getHwEQNPart(Equipment.HWEQNPART_FTTX_OLT_GPON_SLOT));
        olt.setHwProducer(hvtTechnikId);
        assertThat(VlanCalculatorStrategy.get(olt).calculateSvlanOlt(a10Nsp.getNummer(), olt.getOltNr(), oltSlot, svlanOffset, cvlan),
                equalTo(expectedResult));
    }


    @Test
    public void calculateSvlanMdu() {
        olt.setHwProducer(HVTTechnik.HUAWEI);
        assertThat(VlanCalculatorStrategy.get(olt).calculateSvlanMdu(a10Nsp.getNummer(), olt.getOltNr(), Integer.valueOf(7), null), equalTo(Integer.valueOf(449)));
    }

    public void calculateSvlanOlt4Gslam() {
        assertThat(VlanCalculatorStrategy.get(gslam).calculateSvlanOlt(0, null, null, null, null), equalTo(GSLAM_SVLAN));
    }

    @Test
    public void calculateSvlanMdu4Gslam() {
        olt.setHwProducer(HVTTechnik.HUAWEI);
        assertThat(VlanCalculatorStrategy.get(gslam).calculateSvlanMdu(1, null, null, null), equalTo(Integer.valueOf(99)));
    }
}
