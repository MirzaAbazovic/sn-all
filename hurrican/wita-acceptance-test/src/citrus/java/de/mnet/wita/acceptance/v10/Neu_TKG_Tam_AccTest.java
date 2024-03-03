/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2016
 */
package de.mnet.wita.acceptance.v10;

import static de.mnet.wita.model.WitaCBVorgang.ANBIETERWECHSEL_46TKG;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.common.WbciTestDataBuilder;

@Test(groups = BaseTest.ACCEPTANCE)
public class Neu_TKG_Tam_AccTest extends AbstractWitaAcceptanceTest {
    @CitrusTest(name = "GeschaeftsfallNeu_Neubestellung_AccTest")
    public void neubestellung() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_NEU_TKG_TAM, getWitaVersionForAcceptanceTest());

        final WbciTestDataBuilder wbciTestDataBuilder = new WbciTestDataBuilder(wbciDao);
        final String vorabstimmungsId = wbciTestDataBuilder.getWbciVorabstimmungsId();

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder baseBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14).atStartOfDay())
                .withUserName(WitaSimulatorTestUser.TAL_KUENDIGUNG_KUE_KD_01_NEUBESTELLUNG);

        wbciTestDataBuilder.withWbciVorabstimmungsId(vorabstimmungsId)
                .createWbciVorabstimmungForAccpetanceTestBuilder(baseBuilder, WbciCdmVersion.V1,
                        GeschaeftsfallTyp.VA_KUE_MRN, de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG);

        baseBuilder.withCBVorgangMontagehinweis(ANBIETERWECHSEL_46TKG);
        baseBuilder.withCBVorgangAnbieterwechselTKG46(true);
        final CreatedData baseData = workflow.createData(baseBuilder);
        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        atlas().receiveOrder("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("TAM");
    }
}

