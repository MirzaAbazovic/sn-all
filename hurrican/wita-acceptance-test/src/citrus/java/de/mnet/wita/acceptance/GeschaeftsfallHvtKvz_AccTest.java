/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2015
 */
package de.mnet.wita.acceptance;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static org.testng.Assert.*;

import java.text.*;
import java.time.*;
import java.util.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.common.CreatedData;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.citrus.actions.AbstractWitaTestAction;
import de.mnet.wita.model.WitaCBVorgang;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class GeschaeftsfallHvtKvz_AccTest extends AbstractWitaAcceptanceTest {

    @Qualifier("txCBVorgangDAO")
    @Autowired
    protected CBVorgangDAO cbVorgangDAO;

    /**
     * Tests the HVT_KVZ geschaeftsfall
     */
    @CitrusTest(name = "GeschaeftsfallHvtKvz_AccTest")
    public void testHvtKvz() throws Exception {
        useCase(WitaAcceptanceUseCase.HVT_KVZ, WitaCdmVersion.V1);

        AcceptanceTestWorkflow neuWorkflow = workflow().get();
        AcceptanceTestWorkflow kueWorkflow = workflow().get();
        final LocalDateTime kwt = asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17)).atStartOfDay();

        AcceptanceTestDataBuilder neuBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withUserName(WitaSimulatorTestUser.HVT_KVZ);
        CreatedData neuCreatedData = neuWorkflow.createData(neuBuilder);

        AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())
                .withCarrierbestellungRealDate(new Date())
                .withCarrierbestellungLbz("96W/82100/82100/1234")
                .withUserName(WitaSimulatorTestUser.HVT_KVZ);
        CreatedData kueCreatedData = kueWorkflow.createData(kueBuilder);

        hurrican().createHvtKvzCBVorgaenge(neuWorkflow, neuCreatedData, kueWorkflow, kueCreatedData);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("K_QEB");
        workflow().select(kueWorkflow).waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        atlas().sendNotification("K_ABM");
        workflow().select(kueWorkflow).waitForABM();

        hurrican().findAndTriggerHvtKvzBereitstellungAuftrag();

        atlas().sendNotification("K_ERLM");
        workflow().select(kueWorkflow).waitForERLM();

        atlas().sendNotification("K_ENTM");
        workflow().select(kueWorkflow).waitForENTM();

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("N_QEB");
        workflow().select(neuWorkflow).waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        atlas().sendNotification("N_ABM");
        workflow().select(neuWorkflow).waitForABM();

        atlas().sendNotification("N_ERLM");
        workflow().select(neuWorkflow).waitForERLM();

        atlas().sendNotification("N_ENTM");
        workflow().select(neuWorkflow).waitForENTM();
    }

    /**
     * Tests the HVT_KVZ geschaeftsfall, where the requested and confirmed cancellation dates do not match and the
     * neubestellung has not been sent yet to the other carrier.
     */
    @CitrusTest(name = "GeschaeftsfallHvtKvz_DifferentCancellationDate_AccTest")
    public void testHvtKvz_withConfirmedCancellationDateDifferentNeubestellungNotSentYet() throws Exception {
        useCase(WitaAcceptanceUseCase.HVT_KVZ_OTHER_KUENDIGUNG_DATE, WitaCdmVersion.V1);

        AcceptanceTestWorkflow neuWorkflow = workflow().get();
        AcceptanceTestWorkflow kueWorkflow = workflow().get();
        final LocalDateTime kwt = asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17)).atStartOfDay();

        AcceptanceTestDataBuilder neuBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withUserName(WitaSimulatorTestUser.HVT_KVZ_OTHER_KUENDIGUNG_DATE);
        CreatedData neuCreatedData = neuWorkflow.createData(neuBuilder);

        AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())
                .withCarrierbestellungRealDate(new Date())
                .withCarrierbestellungLbz("96W/82100/82100/1234")
                .withUserName(WitaSimulatorTestUser.HVT_KVZ_OTHER_KUENDIGUNG_DATE);
        CreatedData kueCreatedData = kueWorkflow.createData(kueBuilder);

        hurrican().createHvtKvzCBVorgaenge(neuWorkflow, neuCreatedData, kueWorkflow, kueCreatedData);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("K_QEB");
        workflow().select(kueWorkflow).waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        // confirm the cancellation date 1 week later as the request date
        variables().add(VariableNames.CONFIRMED_CUSTOMER_DATE,
                String.format("atlas:asWorkingDayAndNextDayNotHoliday('yyyy-MM-dd', ${%s}, '+7d')",
                        VariableNames.REQUESTED_CUSTOMER_DATE)
        );

        atlas().sendNotification("K_ABM");
        workflow().select(kueWorkflow).waitForABM();

        hurrican().findAndTriggerHvtKvzBereitstellungAuftrag();

        atlas().sendNotification("K_ERLM");
        workflow().select(kueWorkflow).waitForERLM();

        atlas().sendNotification("K_ENTM");
        workflow().select(kueWorkflow).waitForENTM();

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        atlas().sendNotification("N_QEB");
        workflow().select(neuWorkflow).waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        action(new AbstractWitaTestAction("checkRequestedCustomerDate") {
            @Override
            public void doExecute(TestContext context) {
                final String requestedCustomerDate = context.getVariable(VariableNames.REQUESTED_CUSTOMER_DATE);
                String datePattern = "yyyy-MM-dd";
                final LocalDate originalKwt =
                        asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17));
                Date expectedKwt = Date.from(asWorkingDayAndNextDayNotHoliday(originalKwt.plusDays(7)).atStartOfDay(ZoneId.systemDefault()).toInstant());

                if (!requestedCustomerDate.equals(new SimpleDateFormat(datePattern).format(expectedKwt))) {
                    throw new CitrusRuntimeException(String.format("Failed to validate requested customer date, expected %s but was %s",
                            expectedKwt, requestedCustomerDate));
                }
            }
        });

        atlas().sendNotification("N_ABM");
        workflow().select(neuWorkflow).waitForABM();

        atlas().sendNotification("N_ERLM");
        workflow().select(neuWorkflow).waitForERLM();

        atlas().sendNotification("N_ENTM");
        workflow().select(neuWorkflow).waitForENTM();

        hurrican().assertKlaerfall("Neubestellung wird zum best.*tigten K.*ndigungstermin '\\d{4}-\\d{2}-\\d{2}' ausgel.*st .*");
    }

    /**
     * Tests the HVT_KVZ geschaeftsfall, where the requested and confirmed cancellation dates do not match and the
     * neubestellung has already been sent to the other carrier.
     */
    @CitrusTest(name = "GeschaeftsfallHvtKvz_DifferentCancellationDate_AlreadySent_AccTest")
    public void testHvtKvz_withConfirmedCancellationDateDifferentNeubestellungAlreadySent() throws Exception {
        useCase(WitaAcceptanceUseCase.HVT_KVZ_OTHER_KUE_DATE_2, WitaCdmVersion.V1);

        AcceptanceTestWorkflow neuWorkflow = workflow().get();
        AcceptanceTestWorkflow kueWorkflow = workflow().get();
        final LocalDateTime kwt = asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17)).atStartOfDay();

        AcceptanceTestDataBuilder neuBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withUserName(WitaSimulatorTestUser.HVT_KVZ_OTHER_KUE_DATE_2);
        CreatedData neuCreatedData = neuWorkflow.createData(neuBuilder);

        AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())
                .withCarrierbestellungRealDate(new Date())
                .withCarrierbestellungLbz("96W/82100/82100/1234")
                .withUserName(WitaSimulatorTestUser.HVT_KVZ_OTHER_KUE_DATE_2);
        CreatedData kueCreatedData = kueWorkflow.createData(kueBuilder);

        hurrican().createHvtKvzCBVorgaenge(neuWorkflow, neuCreatedData, kueWorkflow, kueCreatedData);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        //
        // KUE workflow
        //

        workflow().select(kueWorkflow);
        atlas().sendNotification("K_QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        // confirm the cancellation date 1 week later as the request date
        variables().add(VariableNames.CONFIRMED_CUSTOMER_DATE,
                String.format("atlas:asWorkingDayAndNextDayNotHoliday('yyyy-MM-dd', ${%s}, '+7d')",
                        VariableNames.REQUESTED_CUSTOMER_DATE)
        );

        atlas().sendNotification("K_ABM");
        workflow().waitForABM();

        hurrican().findAndTriggerHvtKvzBereitstellungAuftrag();

        atlas().sendNotification("K_TV_ABM");
        workflow().waitForABM();

        atlas().sendNotification("K_ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("K_ENTM");
        workflow().waitForENTM();

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        //
        // NEU workflow
        //

        workflow().select(neuWorkflow);
        atlas().sendNotification("N_QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        atlas().sendNotification("N_ABM");
        workflow().waitForABM();

        atlas().sendNotification("N_ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("N_ENTM");
        workflow().waitForENTM();

        action(new AbstractWitaTestAction("checkBereitstellung") {
            @Override
            public void doExecute(TestContext testContext) {
                final Long vorgangId = Long.valueOf(testContext.getVariable(VariableNames.CB_VORGANG_ID));
                final String carrierRefNr = testContext.getVariable(VariableNames.MASTER_EXTERNAL_ORDER_ID);

                final WitaCBVorgang bereitstellung = (WitaCBVorgang) cbVorgangDAO
                        .findCBVorgangByCarrierRefNr(carrierRefNr);
                final String regex = "Der best.*tigte Liefertermin '\\d{2}.\\d{2}.\\d{4}' f.*r die Neubestellung weicht vom " +
                        "Kundenwunschtermin '\\d{2}.\\d{2}.\\d{4}' ab.*";
                assertTrue(bereitstellung.isKlaerfall());
                assertTrue(bereitstellung.getKlaerfallBemerkung().matches(regex),
                        String.format("Got: '%s', Tried to match with: '%s'", bereitstellung.getKlaerfallBemerkung(), regex));

            }
        });

    }

    /**
     * Tests the HVT_KVZ geschaeftsfall, where the HVt-Kuendigung has been answered with an ABBM. In this case it should
     * be marked for clarification and the associated KVz-Bereitstellung should be cancelled.
     */
    @CitrusTest(name = "GeschaeftsfallHvtKvz_HvtAnsweredWithAnAbbm_AccTest")
    public void testHvtKvz_HvtAnsweredWithAnAbbm() throws Exception {
        useCase(WitaAcceptanceUseCase.HVT_KVZ_ABBM_HVT, WitaCdmVersion.V1);

        AcceptanceTestWorkflow neuWorkflow = workflow().get();
        AcceptanceTestWorkflow kueWorkflow = workflow().get();
        final LocalDateTime kwt = asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17)).atStartOfDay();

        AcceptanceTestDataBuilder neuBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withUserName(WitaSimulatorTestUser.HVT_KVZ_ABBM_HVT);
        CreatedData neuCreatedData = neuWorkflow.createData(neuBuilder);

        AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())
                .withCarrierbestellungRealDate(new Date())
                .withCarrierbestellungLbz("96W/82100/82100/1234")
                .withUserName(WitaSimulatorTestUser.HVT_KVZ_ABBM_HVT);
        CreatedData kueCreatedData = kueWorkflow.createData(kueBuilder);

        hurrican().createHvtKvzCBVorgaenge(neuWorkflow, neuCreatedData, kueWorkflow, kueCreatedData);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        //
        // KUE workflow
        //

        workflow().select(kueWorkflow);
        atlas().sendNotification("K_QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        // confirm the cancellation date 1 week later as the request date
        variables().add(VariableNames.CONFIRMED_CUSTOMER_DATE,
                String.format("atlas:asWorkingDayAndNextDayNotHoliday('yyyy-MM-dd', ${%s}, '+7d')",
                        VariableNames.REQUESTED_CUSTOMER_DATE)
        );

        atlas().sendNotification("K_ABBM");
        workflow().waitForABBM();

        action(new AbstractWitaTestAction("checkBereitstellung") {
            @Override
            public void doExecute(TestContext testContext) {
                final String carrierRefNrKuendigung = testContext.getVariable(VariableNames.KUENDIGUNG_EXTERNAL_ORDER_ID);
                final WitaCBVorgang kuendigung = (WitaCBVorgang) cbVorgangDAO.findCBVorgangByCarrierRefNr(carrierRefNrKuendigung);
                assertTrue(kuendigung.isKlaerfall());
                final String regex = "Die K.*ndigung im HVt-nach-KVz wurde mit einer ABBM beantwortet.*";
                assertTrue(kuendigung.getKlaerfallBemerkung().matches(regex),
                        String.format("Got: '%s', Tried to match with: '%s'", kuendigung.getKlaerfallBemerkung(), regex));

            }
        });
    }

    /**
     * Es wird der HVT_KVZ-Geschaeftsfall getestet. Dabei wird eine ABM auf die Neubestellung mit einem abweichenden
     * Termin geschickt, was dazu führen sollte, dass automatisch eine TV auf die Kuendigung ausgelöst wird.
     */
    @CitrusTest(name = "GeschaeftsfallHvtKvz_ConfirmedNeubestellungDateDifferent_AccTest")
    public void testHvtKvz_confirmedNeubestellungDateDifferent() throws Exception {
        useCase(WitaAcceptanceUseCase.HVT_KVZ_OTHER_KUE_DATE_KVZ, WitaCdmVersion.V1);

        AcceptanceTestWorkflow neuWorkflow = workflow().get();
        AcceptanceTestWorkflow kueWorkflow = workflow().get();
        final LocalDateTime kwt = asWorkingDayAndNextDayNotHoliday(addWorkingDays(LocalDate.now(), 17)).atStartOfDay();

        AcceptanceTestDataBuilder neuBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withUserName(WitaSimulatorTestUser.HVT_KVZ_OTHER_KUE_DATE_KVZ);
        CreatedData neuCreatedData = neuWorkflow.createData(neuBuilder);

        AcceptanceTestDataBuilder kueBuilder = workflow().getTestDataBuilder()
                .withVorgabeMnet(kwt)
                .withCarrierbestellungKuendigungAn(new Date())
                .withCarrierbestellungVtrNr(hurrican().getNextVertragsnummer())
                .withCarrierbestellungRealDate(new Date())
                .withCarrierbestellungLbz("96W/82100/82100/1234")
                .withUserName(WitaSimulatorTestUser.HVT_KVZ_OTHER_KUE_DATE_KVZ);
        CreatedData kueCreatedData = kueWorkflow.createData(kueBuilder);

        hurrican().createHvtKvzCBVorgaenge(neuWorkflow, neuCreatedData, kueWorkflow, kueCreatedData);

        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        //
        // KUE workflow
        //

        workflow().select(kueWorkflow);
        atlas().sendNotification("K_QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        // confirm the cancellation date 1 week later as the request date
        variables().add(VariableNames.CONFIRMED_CUSTOMER_DATE,
                String.format("atlas:asWorkingDayAndNextDayNotHoliday('yyyy-MM-dd', ${%s}, '+7d')",
                        VariableNames.REQUESTED_CUSTOMER_DATE)
        );

        atlas().sendNotification("K_ABM");
        workflow().waitForABM();

        hurrican().findAndTriggerHvtKvzBereitstellungAuftrag();

        atlas().sendNotification("TV_ABM");
        workflow().waitForABM();


        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.PRODUCT_IDENTIFIER.getXpath(), VariableNames.PRODUCT_IDENTIFIER)
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), VariableNames.REQUESTED_CUSTOMER_DATE);

        //
        // NEU workflow
        //

        workflow().select(neuWorkflow);
        atlas().sendNotification("N_QEB");
        workflow().waitForQEB();

        // create new contract id for next messages
        variables().add(VariableNames.CONTRACT_ID, "citrus:randomNumber(10)");

        atlas().sendNotification("N_ABM");
        workflow().waitForABM();

        atlas().sendNotification("N_ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("N_ENTM");
        workflow().waitForENTM();

        action(new AbstractWitaTestAction("checkBereitstellung") {
            @Override
            public void doExecute(TestContext testContext) {
                final String carrierRefNr = testContext.getVariable(VariableNames.MASTER_EXTERNAL_ORDER_ID);
                final WitaCBVorgang bereitstellung = (WitaCBVorgang) cbVorgangDAO.findCBVorgangByCarrierRefNr(carrierRefNr);
                assertFalse(bereitstellung.isKlaerfall());
                assertNull(bereitstellung.getKlaerfallBemerkung());
            }
        });

    }

}
