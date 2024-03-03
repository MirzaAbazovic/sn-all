package de.mnet.wita.acceptance.kft;

import static de.augustakom.hurrican.model.cc.Carrier.*;
import static de.mnet.wita.WitaSimulatorTestUser.*;
import static de.mnet.wita.acceptance.common.utils.DateCalculation.*;
import static de.mnet.wita.citrus.VariableNames.*;

import java.text.*;
import java.time.*;
import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wita.AbbmMeldungsCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaSimulatorTestUser;
import de.mnet.wita.acceptance.AbstractWitaAcceptanceTest;
import de.mnet.wita.acceptance.WitaAcceptanceUseCase;
import de.mnet.wita.acceptance.common.AcceptanceTestDataBuilder;
import de.mnet.wita.acceptance.common.AcceptanceTestWorkflow;
import de.mnet.wita.acceptance.xpath.WitaLineOrderXpathExpressions;
import de.mnet.wita.citrus.VariableNames;
import de.mnet.wita.message.auftrag.Auftragsposition;

@SuppressWarnings("Duplicates")
@Test(groups = BaseTest.ACCEPTANCE)
public class TalPv_KfTest extends AbstractWitaAcceptanceTest {
    private static final Logger LOGGER = Logger.getLogger(TalPv_KfTest.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * Zugang zum zertifizierenden Provider. Wechsel ohne Leistungsänderung. Wechselanfrage bestätigt
     */
    @CitrusTest(name = "TalProviderwechselPv301_KfTest")
    public void talProviderwechselPv301() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_01, getWitaVersionForAcceptanceTest());

        final LocalDate vorbageMnet = DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                DateCalculationHelper.addWorkingDays(LocalDate.now(), 21));
        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorgabeMnet(vorbageMnet.atStartOfDay())
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, Carrier.ID_QSC)
                .withUserName(TAL_PROVIDERWECHSEL_PV3_01);

        workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL);
        atlas().receiveOrder("PV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID);
        workflow().waitForQEB();

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable(REQUESTED_CUSTOMER_DATE)));
            }
        });

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Zugang zum zertifizierenden Provider. Wechsel mit Leistungsänderung. Wechselanfrage bestätigt
     * <p/>
     * Da der Ausgangszustand nicht bekannt ist wird davon ausgegangen, dass der geschickte Providerwechsel einer
     * Leistungsänderung entspricht.
     */
    @CitrusTest(name = "TalProviderwechselPv302_KfTest")
    public void talProviderwechselPv302() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_02, getWitaVersionForAcceptanceTest());

        final String montagehinweis = "#OSL_Bereit_MM#";
        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, ID_QSC)
                .withUserName(TAL_PROVIDERWECHSEL_PV3_02)
                .withCBVorgangMontagehinweis(montagehinweis)
                .withTerminReservierungsId("1000000000")
                .withFirma()
                .withCreateAnschlussinhaberAddress(false);

        workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL);
        atlas().receiveOrder("PV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID);
        workflow().waitForQEB();

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        if (getWitaVersionForAcceptanceTest().isGreaterOrEqualThan(WitaCdmVersion.V2)) {
            atlas().sendNotification("TAM");
            workflow().waitForTAM();

            atlas().sendNotification("TAM");
            workflow().waitForTAM();

            // dynamic calculation of new delivery date based on order date
            // if the requested order-date is on the weekend, then the next week day is chosen
            action(new AbstractTestAction() {
                @Override
                public void doExecute(TestContext context) {
                    String deliveryDate = context.getVariable("deliveryDate");
                    String newDeliveryDate = addOffsetAndCalculateDeliveryDate(deliveryDate, 7);
                    context.setVariable("deliveryDate", calculateDeliveryDate(newDeliveryDate));
                }
            });
        }

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());

        workflow().doWithWorkflow(wf -> wf.assertMontageleistungSet());
        workflow().doWithWorkflow(wf -> wf.assertMontageleistungHinweis(montagehinweis));
        workflow().doWithWorkflow(wf -> wf.assertUebertragungsverfahrenSet());
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Zugang zum zertifizierenden Provider. Wechsel ohne Leistungsänderung. Wechselanfrage nicht bestätigt
     */
    @CitrusTest(name = "TalProviderwechselPv303_KfTest")
    public void talProviderwechselPv303() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_03, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, ID_QSC)
                .withUserName(TAL_PROVIDERWECHSEL_PV3_03);

        workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL);
        atlas().receiveOrder("PV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID);
        workflow().waitForQEB();

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        atlas().sendNotification("ABBM");
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.ABLEHNUNG_ABGEBENDER_PROVIDER);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Weggang von zertifizierenden Provider. Wechsel ohne Leistungsänderung. Wechselanfrage bestätigt, mit geändertem
     * Abgabedatum
     */
    @CitrusTest(name = "GeschaeftsfallPv_Pv304_AccTest")
    public void talProviderwechselPv304() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_04, getWitaVersionForAcceptanceTest());

        workflow().sendBereitstellung(WitaSimulatorTestUser.TAL_PROVIDERWECHSEL_PV3_04);

        atlas().receiveOrderWithRequestedCustomerDate("NEU");

        atlas().sendNotification("QEB");
        workflow().waitForQEB();

        atlas().sendNotificationWithNewVariables("ABM", VariableNames.CONTRACT_ID);
        workflow().waitForABM();
        workflow().doWithWorkflow(AcceptanceTestWorkflow::closeCbVorgang);

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        variables().add("plannedAquisitionDate", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 7)");
        variables().add("replyDeadline", "atlas:currentDatePlusWorkingDays('yyyy-MM-dd', 5)");
        atlas().sendNotification("AKM-PV");
        workflow().waitForAkmPv();

        workflow().doWithWorkflow(AcceptanceTestWorkflow::sendRuemPv);
        atlas().receiveNotification("RUEM-PV");

        atlas().sendNotification("ABM-PV");
        workflow().waitForAbmPv();

        if (getWitaVersionForAcceptanceTest().isGreaterOrEqualThan(WitaCdmVersion.V2)) {
            atlas().sendNotification("VZM-PV");
            workflow().waitForVzmPv();
        }

        atlas().sendNotification("ERLM-PV");
        workflow().waitForErlmPv();

        atlas().sendNotification("ENTM-PV");
        workflow().waitForEntmPv();
    }


    /**
     * Zugang zum zertifizierenden Provider.
     */
    @CitrusTest(name = "TalProviderwechselPv305_KfTest")
    public void talProviderwechselPv305() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_05, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, ID_QSC)
                .withUserName(TAL_PROVIDERWECHSEL_PV3_05);

        workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL);
        atlas().receiveOrder()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE);

        atlas().sendNotificationWithNewVariables("QEB", VariableNames.CONTRACT_ID);
        workflow().waitForQEB();

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        atlas().sendNotification("ABBM");
        workflow().waitForNonClosingAbbm(AbbmMeldungsCode.ANBIETERWECHSEL_NUR_MIT_VBL);

        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    /**
     * Vorbedingung für Stornoauftrag. Zugang zum zertifizierenden Provider. Wechselanfrage bestätigt. Stornoauftrag
     * wird nach AKM-PV geschickt
     */
    @CitrusTest(name = "TalProviderwechselPv306_KfTest")
    public void talProviderwechselPv306() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_06, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, ID_QSC)
                .withUserName(TAL_PROVIDERWECHSEL_PV3_06);

        workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL);
        atlas().receiveOrder("PV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotificationWithNewVariables("QEB");
        workflow().waitForQEB();

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        workflow().sendStorno();
        atlas().receiveOrder("STORNO");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }

    // talProviderwechselPv307() wird von der DTAG getriggert -> wir müssen nur TEQs zurückschicken
    // Test der positiven TEQs mit de.mnet.wita.acceptance.GeschaeftsfallPvAccTest.talProviderwechselPv307()

    /**
     * Vorbedingung für TV-Auftrag. Zugang zum zertifizierenden Provider. Wechselanfrage bestätigt. TV-Auftrag wird nach
     * QEB geschickt.
     */
    @CitrusTest(name = "TalProviderwechselPv308_KfTest")
    public void talProviderwechselPv308() throws Exception {
        useCase(WitaAcceptanceUseCase.TAL_PROVIDERWECHSEL_PV3_08, getWitaVersionForAcceptanceTest());

        final AcceptanceTestWorkflow workflow = workflow().get();
        final AcceptanceTestDataBuilder testData = workflow().getTestDataBuilder()
                .withVorabstimmungEinzelanschluss(ProduktGruppe.TAL, ID_QSC)
                .withUserName(TAL_PROVIDERWECHSEL_PV3_08);

        workflow().select(workflow).send(workflow.createData(testData), CBVorgang.TYP_ANBIETERWECHSEL);
        atlas().receiveOrder("PV")
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_DATE.getXpath(), REQUESTED_CUSTOMER_DATE)
                .extractFromPayload(WitaLineOrderXpathExpressions.CONTRACT_ID.getXpath(), VariableNames.CONTRACT_ID);

        atlas().sendNotificationWithNewVariables("QEB");
        workflow().waitForQEB();

        // dynamic calculation of new delivery date based on order date
        // if the requested order-date is on the weekend, then the next week day is chosen
        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                // create new test variable for dynamic delivery date not on weekend
                context.setVariable("deliveryDate", calculateDeliveryDate(context.getVariable("requestedCustomerDate")));
            }
        });

        atlas().sendNotification("ABM");
        workflow().waitForABM();

        workflow().sendTv();
        atlas().receiveNotification()
                .extractFromPayload(WitaLineOrderXpathExpressions.REQUESTED_CUSTOMER_CHANGE_DATE.getXpath(), "requestedCustomerDate");

        atlas().sendNotification("ERLM");
        workflow().waitForERLM();

        atlas().sendNotification("ENTM");
        workflow().waitForENTM();

        workflow().doWithWorkflow(wf -> wf.closeCbVorgang());
        workflow().doWithWorkflow(wf -> wf.assertProduktBezeichnerEquals(Auftragsposition.ProduktBezeichner.HVT_2H));
    }


    /**
     * Liest den String im Format DATE_FORMAT ein und addiert den Offset als Tage hinzu. <br/> Berechnet dann daraus das
     * Delivery-Date <br/> Liefert das Ergebnis wieder als String im Format DATE_FORMAT
     *
     * @param dateValue Das Datum als String
     * @param offset    Der Offset in Tagen
     * @return Das Datum mit Offset in Tagen
     */
    private static String addOffsetAndCalculateDeliveryDate(String dateValue, int offset) {
        Calendar deliveryDateCal = Calendar.getInstance();

        try {
            deliveryDateCal.setTime(DATE_FORMAT.parse(dateValue));
            deliveryDateCal.add(Calendar.DAY_OF_WEEK, offset);
        }
        catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return calculateDeliveryDate(DATE_FORMAT.format(deliveryDateCal.getTime()));
    }

}