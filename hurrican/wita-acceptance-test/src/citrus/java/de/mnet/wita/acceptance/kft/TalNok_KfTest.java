/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.09.2015
 */
package de.mnet.wita.acceptance.kft;

import static de.mnet.wita.WitaSimulatorTestUser.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.MeldungsType;

@Test(groups = BaseTest.ACCEPTANCE)
public class TalNok_KfTest extends AbstractWitaAcceptanceTest {

    /**
     * Test des Empfanges verschiedener inhaltlich falscher Meldungen.<br /> Providersystem muss jeden Fehler mit
     * spezifizierten TEQ Code beantworten können.<br /><br /> Valider Auftrag wird abgebrochen mit ABBM 1304.<br />
     * ABBM enthält ein Dummy Alternativprodukt und wird abgewiesen mit TEQ "0987 - Ein angegebener Produktbezeichner
     * ist ungültig"<br />
     * <p/>
     * Nachdem Hurrican keine TEQs mehr schickt, muss von AtlasESB die negative TEQs geschickt werden. Da AtlasESB
     * aktuell keine "ALTERNATIVPRODUKT" Prüfung macht und keine KFT Tests ansteht, wurde der Test hier bis zum nächsten
     * KFT disabled.
     */
    @CitrusTest(name = "TalNok_01_KfTest")
    public void talNok01() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_TEQ_NOK_01, WitaCdmVersion.V1);

        workflow().sendBereitstellung(TAL_NOK_01);
        atlas().receiveOrder();

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABBM");
        workflow().doWithWorkflow(wf -> {
            wf.waitForIOArchiveEntry(wf.getCbVorgang().getCarrierRefNr(), de.mnet.wita.message.GeschaeftsfallTyp.BEREITSTELLUNG,
                    MeldungsType.ABBM, AbbmMeldungsCode.ALTERNATIVPRODUKT.meldungsCode);
        });
    }

    /**
     * Auftrag wird an DTAG gesendet; Anschliessend muss ActiveMQ manuell beendet werden, damit DTAG eine negative TEQ
     * (0999) erhaelt! <br> Ergebnispruefung muss manuell erfolgen!!!
     */
    @CitrusTest(name = "TalNok_02_KfTest")
    public void talNok02() throws Exception {
        useCase(WitaAcceptanceUseCase.NEU_QEB_ABM, WitaCdmVersion.V1);

        workflow().sendBereitstellung(TAL_NOK_02);
        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add("contractId", "citrus:randomNumber(10)");

        atlas().sendNotification("ABM");
        workflow().waitForABM();
   }

}