/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 09.02.2017

 */

package de.mnet.hurrican.acceptance.wholesale;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.math.*;
import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.wholesale.dao.WholesaleAuditDAO;
import de.mnet.hurrican.wholesale.model.PvStatus;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.hurrican.wholesale.ws.inbound.processor.UpdateOrderProcessor;

/**
 * This test simulates the Hurrican Client - sending a wholesale order for PV (Providerwechsel) and
 * expects a createOrder message to atlas.
 * <p>
 * <p>
 * Created by wieran on 09.02.2017.
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class UpdatePvOrderTest extends AbstractHurricanTestBuilder {

    private final String TEST_VORABSTIMMUNG_ID = "testVaId-123";
    private static final BigInteger SPRI_VERSION = BigInteger.valueOf(4);

    @Mock
    private UpdateOrderProcessor updateOrderProcessor;

    @Autowired
    private WholesaleAuditDAO wholesaleAuditDAO;


    @CitrusTest
    public void updatePvOrderTest() {
        simulatorUseCase(SimulatorUseCase.WholesaleOrder, WholesaleOrderOutboundTestVersion.V2);
        //given

        //when
        atlas().sendWholesaleUpdateOrderService("updateOrderAKMPV");

        sleep(6000);

        //then
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                assertWholesaleAudit("AKM-PV", PvStatus.EMPFANGEN);
                assertWholesaleAudit("RUEM-PV", PvStatus.GESENDET);
            }
        });
        atlas().receiveWholesaleUpdateOrderService("updateOrderRUEMPV");

    }


    private void assertWholesaleAudit(String wholesaleAuditBeschreibung, PvStatus pvStatus) {
        List<WholesaleAudit> wholesaleAudits;
        wholesaleAudits = wholesaleAuditDAO.findByProperty(WholesaleAudit.class, "beschreibung", wholesaleAuditBeschreibung);

        assertThat(wholesaleAudits, Matchers.hasSize(1));

        WholesaleAudit wholesaleAudit = wholesaleAudits.get(0);
        assertThat(wholesaleAudit.getStatus(), is(pvStatus));
        assertThat(wholesaleAudit.getRequestXml(), containsString("<geschaeftsfall>PV</geschaeftsfall>"));
    }

}

