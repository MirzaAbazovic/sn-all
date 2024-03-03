/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.acceptance.receiving;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.ReceiveERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.ReceiveSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.WbciGeschaeftsfallKueMrnKftBuilder;
import de.mnet.wbci.acceptance.common.model.Carrier;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveABBM_TV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendAKMTR_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendTV_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.citrus.helper.WbciDateUtils;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAuf_StornoAenderung_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_01, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *     STRAEN   ->
     *              <-  ERLM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_02, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_ABG));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AEN_ABG));
    }

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_03, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        atlas().sendCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *     STRAEN   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_04, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_ABG));

        hurrican().createWbciMeldung(
                new AbbruchmeldungKftBuilder(wbciCdmVersion, geschaeftsfallTyp, IOType.OUT)
                        .withoutWechseltermin()
                        .withMeldungsCodes(MeldungsCode.STORNO_ABG)
                        .withBegruendung("Sag ich nicht")
                        .buildForStorno(RequestTyp.STR_AEN_ABG), RequestTyp.STR_AEN_ABG
        );
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, GeschaeftsfallTyp.VA_KUE_MRN, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests sending a Storno Aenderung from Donating Carrier (Atlas) before sending a RuemVA. It should be answered
     * with an ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     STRAEN   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_05, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_ABG)
                .withSkipStornoRequestChecks()
                .withSkipGeschaeftsfallChecks());

        atlas().receiveCarrierChangeUpdate("ABBM");

        hurrican().assertNumberOfRequests(StornoAenderungAbgAnfrage.class, 1);
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_VERSENDET);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ERLM. After sending the STR-AEN ERLM
     * Meldung a second GF (GF2) is created, which is then linked to the first GF (GF1). The second GF should be marked
     * as Klaerfall since the new porting date is a month later than the original one.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              ->  STRAEN
     *     ERLM     <-
     *              <-  KUEMRN
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_06, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new SendVA_TestBehavior().withVaLinkedToStrAenGf(true));

        // Wechseltermin auf heute plus 1 Jahr setzen. Der Geschaeftsfall sollte als Klaerfall markiert werden, weil
        // der neue Wechseltermin im Vergleich zum urspruenglichen Wechseltermin weit in der Zukunft liegt.
        hurrican().updateVariable(VariableNames.REQUESTED_CUSTOMER_DATE,
                WbciDateUtils.formatToWbciDate(LocalDateTime.now().plusYears(1)));
        applyBehavior(new ReceiveRUEMVA_TestBehavior()
                        .isKlaerfall(true)
                        .withKlaerfallGrund("(?s).*Der neue Wechseltermin \\d+.\\d+.\\d+ weicht mehr als \\d+ Arbeitstage vom " +
                                "urspruenglichen Wechseltermin \\d+.\\d+.\\d+ ab..*")
        );
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ERLM. After sending the STR-AEN ERLM
     * Meldung a second GF (GF2) is created, which is then linked to the first GF (GF1).
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              ->  STRAEN
     *     ERLM     <-
     *              <-  KUEMRN
     *     RUEMVA   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_07_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_07, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new SendVA_TestBehavior().withVaLinkedToStrAenGf(true));

        // Die zweite RuemVa kommt mit dem gleichen Wechseltermin an.
        applyBehavior(new ReceiveRUEMVA_TestBehavior());
    }

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ABBM that misses correct
     * StornoIdRef element. Result is a error service notification to Atlas and message is neither accepted nor stored.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ERLM     ->
     *              <-  ATLAS_ERROR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_08_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_08, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        atlas().sendCarrierChangeUpdate("ERLM");

        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VERSENDET);

        atlas().receiveErrorHandlingServiceMessage("ATLAS_ERROR");
    }

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ABBM that misses correct
     * StornoIdRef element. Result is a error service notification to Atlas and message is neither accepted nor stored.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  STRAEN
     *     ABBM     ->
     *              <-  ATLAS_ERROR
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_09_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_09, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        atlas().sendCarrierChangeUpdate("ABBM");

        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_VERSENDET);

        atlas().receiveErrorHandlingServiceMessage("ATLAS_ERROR");
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *     STRAEN   ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_10_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_10, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        hurrican().storeWitaCBVorgang(
                new WitaCBVorgangBuilder()
                        .withAuftragId(1L)
                        .withCarrierRefNr("020001")
                        .withTyp(WitaCBVorgang.TYP_REX_MK)
                        .build(),
                true
        );

        hurrican().storeWitaCBVorgang(
                new WitaCBVorgangBuilder()
                        .withAuftragId(2L)
                        .withCarrierRefNr("020002")
                        .withTyp(WitaCBVorgang.TYP_REX_MK)
                        .build(),
                true
        );

        // Wenn zu der VA ein WITA Vorgang existiert sollte bei Empfang von STR_AUFH kein Klaerfall-Flag gesetzt werden
        // Stattdessen soll ein Hinweis im zugehoerigen ERLM-Dialog angezeigt werden
        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_ABG).withExpectedKlaerfall(false));
    }

    /**
     * Tests the Storno Aenderung sent from Donating Carrier (Atlas) use case with an unknown VaRefId:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *     STRAEN   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_11_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_11, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        String vaRefId = hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG);
        String stornoId = hurrican().createPreAgreementId(RequestTyp.STR_AEN_ABG, Carrier.DTAG);

        variables().add(VariableNames.PRE_AGREEMENT_ID, vaRefId);
        variables().add(VariableNames.STORNO_ID, stornoId);
        variables().add(VariableNames.CARRIER_CODE_ABGEBEND, CarrierCode.DTAG.getITUCarrierCode());
        variables().add(VariableNames.CARRIER_CODE_AUFNEHMEND, CarrierCode.MNET.getITUCarrierCode());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_ABG)
                        .withSkipStornoRequestChecks()
                        .withSkipGeschaeftsfallChecks()
                        .withStornoId(stornoId)
                        .withPreAgreementId(vaRefId)
        );

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
    }

    /**
     * Tests the Storno Aenderung sent from Receiving Carrier (Hurrican) use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  AKM-TR
     *              <-  TV
     *     ABBM     ->
     *              <-  STRAEN
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAenderung_12_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAenderung_12, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        final LocalDate kundenwunschtermin =
                DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(14);
        applyBehavior(new SendVA_TestBehavior(
                new WbciGeschaeftsfallKueMrnKftBuilder(wbciCdmVersion).withKundenwunschtermin(kundenwunschtermin)));

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendAKMTR_TestBehavior());

        applyBehavior(new SendTV_TestBehavior(DateCalculationHelper.asWorkingDayAndNextDayNotHoliday(
                kundenwunschtermin.plusDays(7))));
        applyBehavior(new ReceiveABBM_TV_TestBehavior(MeldungsCode.TV_ABG));

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));
    }

}
