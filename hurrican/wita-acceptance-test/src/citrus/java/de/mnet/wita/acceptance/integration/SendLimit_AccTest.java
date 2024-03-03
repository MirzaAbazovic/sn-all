/*

 * Copyright (c) 2015 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 05.11.2015

 */

package de.mnet.wita.acceptance.integration;


import static de.mnet.wita.WitaSimulatorTestUser.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.common.WitaMessageAssertionTestService;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.model.KollokationsTyp;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaSendLimit;
import de.mnet.wita.service.WitaConfigService;

/**
 * Created by petersde on 05.11.2015.
 */
@Test(groups = BaseTest.ACCEPTANCE_INTEGRATION)
public class SendLimit_AccTest extends AbstractWitaAcceptanceTest {
    @Autowired
    private WitaConfigService witaConfigService;
    @Autowired
    private WitaMessageAssertionTestService messageAssertions;

    @BeforeMethod(alwaysRun = true)
    public void resetSendCounts() throws FindException {
        for (GeschaeftsfallTyp geschaeftsfallTyp : GeschaeftsfallTyp.values()) {
            witaConfigService.resetWitaSentCount(geschaeftsfallTyp, KollokationsTyp.HVT);
            witaConfigService.resetWitaSentCount(geschaeftsfallTyp, KollokationsTyp.FTTC_KVZ);
        }
    }

    @CitrusTest(name = "sendWithConfiguredLimitExpectZeroMessage")
    public void sendWithConfiguredLimitExpectZeroMessage() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM, WitaCdmVersion.V1);
        setSendLimit(0);

        workflow().select(workflow().get()).sendBereitstellung(NEU_QEB_ABM);
        workflow().doWithWorkflow(wf -> messageAssertions.assertNoIoArchiveEntryReceived(wf.getCbVorgang()));
    }

    @CitrusTest(name = "sendWithConfiguredLimitOneMessage")
    public void sendWithConfiguredLimitOneMessage() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM, WitaCdmVersion.V1);
        setSendLimit(1);

        final AcceptanceTestWorkflow workflow1 = workflow().get();
        final AcceptanceTestWorkflow workflow2 = workflow().get();

        workflow().select(workflow1).sendBereitstellung(NEU_QEB_ABM);
        workflow().doWithWorkflow(wf -> messageAssertions.assertIoArchiveEntryReceived(wf.getCbVorgang()));

        workflow().select(workflow2).sendBereitstellung(NEU_QEB_ABM);
        workflow().doWithWorkflow(wf -> messageAssertions.assertNoIoArchiveEntryReceived(wf.getCbVorgang()));
    }

    private void setSendLimit(int limit) throws FindException {
        WitaSendLimit witaSendLimit = witaConfigService.findWitaSendLimit(GeschaeftsfallTyp.BEREITSTELLUNG.name(), KollokationsTyp.HVT, null);
        witaSendLimit.setWitaSendLimit(Long.valueOf(limit));
        witaConfigService.saveWitaSendLimit(witaSendLimit);
    }

    @AfterMethod(alwaysRun = true)
    public void resetSendLimit() throws FindException {
        if (witaConfigService == null) {
            return;
        }
        for (GeschaeftsfallTyp geschaeftsfallTyp : GeschaeftsfallTyp.values()) {
            WitaSendLimit limit = witaConfigService.findWitaSendLimit(geschaeftsfallTyp.name(), KollokationsTyp.HVT, null);
            if (limit == null) {
                continue;
            }
            limit.setWitaSendLimit(Long.valueOf(-1));
            witaConfigService.saveWitaSendLimit(limit);
        }
    }
}
