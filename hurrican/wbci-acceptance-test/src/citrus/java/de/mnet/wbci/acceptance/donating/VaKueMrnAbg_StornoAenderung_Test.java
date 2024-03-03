/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.factory.GeneratedTaifunData;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.behavior.ReceiveERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.ReceiveSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendERLM_STR_TestBehavior;
import de.mnet.wbci.acceptance.common.behavior.SendSTR_TestBehavior;
import de.mnet.wbci.acceptance.common.builder.AbbruchmeldungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.RueckmeldungVorabstimmungKftBuilder;
import de.mnet.wbci.acceptance.common.model.Carrier;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAbg_StornoAenderung_Test extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests the Storno Aenderung sent from the Receiving Carrier use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     STRAEN   ->
     *              <-  ERLM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAenderung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAenderung_01, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF));
    }

    /**
     * Tests the Storno Aenderung sent from the Donating Carrier use case with ERLM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *              <-  STRAEN
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAenderung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAenderung_02, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_ABG));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AEN_ABG));
    }

    /**
     * Tests the Storno Aenderung sent from the Receiving Carrier use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *     STRAEN   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAenderung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAenderung_03, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_AUF));

        hurrican().createWbciMeldung(
                new AbbruchmeldungKftBuilder(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN, IOType.OUT)
                        .withoutWechseltermin()
                        .withMeldungsCodes(MeldungsCode.STORNO_ABG)
                        .withBegruendung("Storno")
                        .buildForStorno(RequestTyp.STR_AEN_AUF), RequestTyp.STR_AEN_AUF
        );
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, GeschaeftsfallTyp.VA_KUE_MRN, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        atlas().receiveCarrierChangeUpdate("ABBM");
    }

    /**
     * Tests the Storno Aenderung sent from the Donating Carrier use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *              <-  RUEMVA
     *              ->  STRAEN
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAenderung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAenderung_04, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA));

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AEN_ABG));

        atlas().sendCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, GeschaeftsfallTyp.VA_KUE_MRN, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }


    /**
     * Tests the Storno Aenderung sent from the Donating Carrier use case with ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     RRNP     ->
     *              <-  RUEM-VA
     *     STRAEN   ->
     *              <- ERLM
     *     KUE-MRN  ->
     *              <-  ABBM (wg. GF-Wechsel RRNP zu MRN nach STR-AEN)
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAenderung_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAenderung_05, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        String rrnpVaId = hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG);
        variables().add(VariableNames.PRE_AGREEMENT_ID, rrnpVaId);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        Rufnummer dialNumber = generatedTaifunData.getDialNumbers().get(0);

        applyBehavior(new ReceiveVA_TestBehavior(VA_RRNP)
                .withGeneratedTaifunData(generatedTaifunData).withSearchBuildings(false, false));
        hurrican().assertVaRequestAssignedToOrder(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withExplicitGeschaeftsfallTyp(VA_RRNP)
                .withBuilder(new RueckmeldungVorabstimmungKftBuilder(WBCI_CDM_VERSION, VA_RRNP, IOType.IN))
                .addEinzelrufnummer(dialNumber.getOnKz(), dialNumber.getDnBase(), "D052"));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_AUF).withExplicitGeschaeftsfallTyp(VA_RRNP));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF).withExplicitGeschaeftsfallTyp(VA_RRNP));

        // neue VA-Id erzeugen
        variables().add(VariableNames.PRE_AGREEMENT_ID, hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG));

        // VA KUE-MRN erstellen
        applyBehavior(new ReceiveVA_TestBehavior(GeschaeftsfallTyp.VA_KUE_MRN)
                .withExpectedWbciGeschaeftsfallStatus(WbciGeschaeftsfallStatus.COMPLETE)
                .withExpectedWbciRequestStatus(WbciRequestStatus.ABBM_VERSENDET)
                .withGeneratedTaifunData(generatedTaifunData)
                .withSearchBuildings(false, false));
        
        // ABBM von Hurrican pruefen
        atlas().receiveCarrierChangeUpdate("ABBM");

        doFinally(hurrican().closeWbciGeschaeftsfallAction().withVorabstimmungsId(rrnpVaId));
        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }

    /**
     * Tests the Storno Aenderung sent from the Donating Carrier use case with successful change of the {@link
     * WbciGeschaeftsfall}:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUE-ORN   ->
     *              <-  RUEM-VA
     *     STRAEN   ->
     *              <- ERLM
     *     KUE-MRN  ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAenderung_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAenderung_06, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        String vaKueOrnVaId = hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG);
        variables().add(VariableNames.PRE_AGREEMENT_ID, vaKueOrnVaId);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();

        applyBehavior(new ReceiveVA_TestBehavior(VA_KUE_ORN)
                .withGeneratedTaifunData(generatedTaifunData).withSearchBuildings(false, false));
        hurrican().assertVaRequestAssignedToOrder(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withExplicitGeschaeftsfallTyp(VA_KUE_ORN));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_AUF).withExplicitGeschaeftsfallTyp(VA_KUE_ORN));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF).withExplicitGeschaeftsfallTyp(VA_KUE_ORN));

        // neue VA-Id erzeugen
        variables().add(VariableNames.PRE_AGREEMENT_ID, hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG));

        // VA KUE-MRN erstellen
        applyBehavior(new ReceiveVA_TestBehavior(GeschaeftsfallTyp.VA_KUE_MRN)
                .withExpectedWbciGeschaeftsfallStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withExpectedWbciRequestStatus(WbciRequestStatus.VA_EMPFANGEN)
                .withGeneratedTaifunData(generatedTaifunData)
                .withSearchBuildings(false, false));

        hurrican().assertLinkedStrAenGfStatus(WbciGeschaeftsfallStatus.COMPLETE);

        doFinally(hurrican().closeWbciGeschaeftsfallAction().withVorabstimmungsId(vaKueOrnVaId));
        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }


    /**
     * Tests the Storno Aenderung sent from the Donating Carrier use case with successful change of the {@link
     * WbciGeschaeftsfall}; the second VA (KUE-MRN) is sent after the first GF status is changed to NEW_VA_EXPIRED
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     RRNP     ->
     *              <-  RUEM-VA
     *     STRAEN   ->
     *              <- ERLM
     *                 (GF Status is changed to NEW_VA_EXPIRED)
     *     KUE-MRN  ->
     *                 expected: NO order assignment (because of NEW_VA_EXPIRED status) and NO ABBM sent
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbg_StornoAenderung_07_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbg_StornoAenderung_07, WBCI_CDM_VERSION, wbciVersion,
                geschaeftsfallTyp);

        String rrnpVaId = hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG);
        variables().add(VariableNames.PRE_AGREEMENT_ID, rrnpVaId);

        GeneratedTaifunData generatedTaifunData = getNewTaifunDataFactory().surfAndFonWithDns(1).persist();
        Rufnummer dialNumber = generatedTaifunData.getDialNumbers().get(0);

        applyBehavior(new ReceiveVA_TestBehavior(VA_RRNP)
                .withGeneratedTaifunData(generatedTaifunData).withSearchBuildings(false, false));
        hurrican().assertVaRequestAssignedToOrder(generatedTaifunData.getBillingAuftrag().getAuftragNoOrig());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withExplicitGeschaeftsfallTyp(VA_RRNP)
                .withBuilder(new RueckmeldungVorabstimmungKftBuilder(WBCI_CDM_VERSION, VA_RRNP, IOType.IN))
                .addEinzelrufnummer(dialNumber.getOnKz(), dialNumber.getDnBase(), "D052"));

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AEN_AUF).withExplicitGeschaeftsfallTyp(VA_RRNP));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AEN_AUF).withExplicitGeschaeftsfallTyp(VA_RRNP));

        // status change to NEW_VA_EXPIRED
        hurrican().changeGfStatus(WbciGeschaeftsfallStatus.NEW_VA_EXPIRED);

        // neue VA-Id erzeugen
        variables().add(VariableNames.PRE_AGREEMENT_ID, hurrican().createPreAgreementId(RequestTyp.VA, Carrier.DTAG));

        // VA KUE-MRN erstellen
        applyBehavior(new ReceiveVA_TestBehavior(GeschaeftsfallTyp.VA_KUE_MRN)
                .withExpectedWbciGeschaeftsfallStatus(WbciGeschaeftsfallStatus.ACTIVE)
                .withExpectedWbciRequestStatus(WbciRequestStatus.VA_EMPFANGEN)
                .withGeneratedTaifunData(generatedTaifunData)
                .withSearchBuildings(false, false));

        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
        hurrican().assertVaRequestNotAssignedToOrder();

        doFinally(hurrican().closeWbciGeschaeftsfallAction().withVorabstimmungsId(rrnpVaId));
        doFinally(hurrican().closeWbciGeschaeftsfallAction());
    }
    
}
