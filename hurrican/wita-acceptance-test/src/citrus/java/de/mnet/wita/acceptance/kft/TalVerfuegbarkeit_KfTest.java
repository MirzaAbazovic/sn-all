/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.08.2011 17:45:44
 */
package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.WitaSimulatorTestUser.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;

/**
 * Verfuegbarkeitstests für KFT
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class TalVerfuegbarkeit_KfTest extends AbstractWitaAcceptanceTest {

    /**
     * Der Auftrag wird mit 100 Meldungen innerhalb einer Minute beantwortet. Korrekte Verarbeitung (positive TEQs)
     * aller Meldungen ist erwartet.
     */
    @CitrusTest(name = "TalVerfuegbarkeit_01_KfT")
    public void talVerfuegbarkeitSendBereitstellung() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM, getWitaVersionForAcceptanceTest());
        workflow().sendBereitstellung(TAL_VERFUEGBARKEIT_01);
        /*
          sendBereitstellung ist equivalent zu:
          START
         */
//        final AcceptanceTestWorkflow workflow = workflow().get();
//        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder().withUserName(TAL_VERFUEGBARKEIT_01);
//        final CreatedData baseData = workflow.createData(testData);
//        workflow().select(workflow).send(baseData, CBVorgang.TYP_NEU);
        /*
            END
         */

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();
    }
}


