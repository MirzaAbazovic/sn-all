/*

 * Copyright (c) 2017 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 09.02.2017

 */

package de.mnet.hurrican.acceptance.wholesale;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.wholesale.dao.WholesaleAuditDAO;
import de.mnet.hurrican.wholesale.model.PvStatus;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;

/**
 * This test simulates receiving of a SPRI ABM message for PV (Providerwechsel) and
 * expects a new wholesale audit entry.
 * <p>
 * <p>
 * Created by morozovse on 14.03.2017.
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class ReceiveAllMessageForPvTest extends AbstractHurricanTestBuilder {

    public final String TEST_VORABSTIMMUNG_ID = "testVaId-123";
    private final String REQUEST_XML = "<geschaeftsfall>PV</geschaeftsfall>";

    public final String UPDATE_ORDER_ABM = "updateOrderABMForPv";
    public final String UPDATE_ORDER_QEB = "updateOrderQEBForPv";
    public final String ABM = "ABM";
    public final String QEB = "QEB";

    @Autowired
    private WholesaleAuditDAO wholesaleAuditDAO;

    @CitrusTest
    public void receiveAbmMessageTest() {
        updatePvOrderTest(UPDATE_ORDER_ABM, ABM, TEST_VORABSTIMMUNG_ID);
    }

    @CitrusTest
    public void receiveQebMessageTest() {
        updatePvOrderTest(UPDATE_ORDER_QEB, QEB, TEST_VORABSTIMMUNG_ID);
    }

    public void updatePvOrderTest(String updateOrderMessage, String message, String vorabstimmungsId) {
        simulatorUseCase(SimulatorUseCase.WholesaleOrder, WholesaleOrderOutboundTestVersion.V2);

        //when
        atlas().sendWholesaleUpdateOrderService(updateOrderMessage);

        sleep(10000);

        //then
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                assertWholesaleAudit(message, vorabstimmungsId);
            }
        });

    }

    private void assertWholesaleAudit(String message,String vorabstimmungsId) {
        List<WholesaleAudit> wholesaleAudits;
        try {
            wholesaleAudits = wholesaleAuditDAO.findByVorabstimmungsId(vorabstimmungsId);
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }
        WholesaleAudit wholesaleAudit = wholesaleAudits.get(0);
        assertThat(wholesaleAudits, Matchers.hasSize(1));
        assertThat(wholesaleAudit.getBeschreibung(), is(message));
        assertThat(wholesaleAudit.getStatus(), is(PvStatus.EMPFANGEN));
        assertThat(wholesaleAudit.getRequestXml(), containsString(REQUEST_XML));
    }

}

