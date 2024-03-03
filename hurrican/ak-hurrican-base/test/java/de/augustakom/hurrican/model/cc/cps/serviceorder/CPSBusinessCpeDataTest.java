/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.03.14 07:15
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import static org.hamcrest.MatcherAssert.*;

import java.util.*;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EG2AuftragBuilder;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGConfigBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;

/**
 *
 */
public class CPSBusinessCpeDataTest {

    CPSBusinessCpeData cut;

    @BeforeMethod
    void setUp() {
        cut = new CPSBusinessCpeData();
    }

    @Test
    public void testTransferEgConfigDataWithDestinations() throws Exception {
        final EGConfig egConfig = new EGConfigBuilder().withRandomId().build();
        final EG2Auftrag eg2Auftrag = new EG2AuftragBuilder()
                .withRandomId()
                .build();

        cut.transferEgConfigData(egConfig, eg2Auftrag, Collections.<TechLeistung>emptyList());
        assertThat(cut.getWanIpFixed(), CoreMatchers.equalTo(BooleanTools.getBooleanAsString(egConfig.getWanIpFest())));
        assertThat(cut.getWanOuterTag(), CoreMatchers.equalTo(egConfig.getWanVp()));
        assertThat(cut.getWanInnerTag(), CoreMatchers.equalTo(egConfig.getWanVc()));
        assertThat(cut.getMgmtUsername(), CoreMatchers.equalTo(egConfig.getEgUser()));
        assertThat(cut.getMgmtPassword(), CoreMatchers.equalTo(egConfig.getEgPassword()));
    }

}
