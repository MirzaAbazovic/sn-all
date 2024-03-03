/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.13
 */
package de.mnet.wbci.acceptance.donating;

import static de.mnet.wbci.acceptance.common.role.AtlasEsbTestRole.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.acceptance.AbstractWbciAcceptanceTestBuilder;
import de.mnet.wbci.acceptance.WbciSimulatorUseCase;
import de.mnet.wbci.acceptance.common.builder.RueckmeldungVorabstimmungKftBuilder;
import de.mnet.wbci.acceptance.common.builder.TechnischeRessourceKftBuilder;
import de.mnet.wbci.acceptance.donating.behavior.ReceiveVA_TestBehavior;
import de.mnet.wbci.acceptance.donating.behavior.SendRUEMVA_TestBehavior;
import de.mnet.wbci.citrus.VariableNames;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

/**
 * Test fuer WBCI Geschaeftsfall 'Kuendigung mit Rufnummernuebernahme - abgebend'. (M-net = abgebender EKP)
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAbgAutoProcessing_Test extends AbstractWbciAcceptanceTestBuilder {

    private static final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;
    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;
    private static final WbciVersion wbciVersion = WbciVersion.V2;

    /**
     * Tests duplicate VA-ID with resulting ABBM from the Donating Carrier (M-Net in this case):
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN   ->
     *     KUEMRN   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbgAutoProcessing_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbgAutoProcessing_01, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        variable(VariableNames.PRE_AGREEMENT_ID, "wbci:createVorabstimmungsId()");
        variable(VariableNames.CARRIER_CODE_ABGEBEND, CarrierCode.MNET.getITUCarrierCode());
        variable(VariableNames.CARRIER_CODE_AUFNEHMEND, CarrierCode.DTAG.getITUCarrierCode());
        variable(VariableNames.REQUESTED_CUSTOMER_DATE, "wbci:currentDatePlusWorkingDaysAndNextDayIsWorkingDay('yyyy-MM-dd', 14)");

        atlas().sendCarrierChangeRequest("VA_KUEMRN_ABG");
        atlas().receiveAnyLocationSearch(LOCATION_SEARCH_BUILDINGS);
        atlas().sendEmptyLocationSearchResponse();
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), RequestTyp.VA, 1, false);

        atlas().sendCarrierChangeRequest("VA_KUEMRN_ABG");
        atlas().receiveCarrierChangeUpdate("ABBM");

        // Check that neither the Duplicate VA nor the ABBM was recorded in the IOArchive
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), RequestTyp.VA, 1, false);
        hurrican().assertNoIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
    }

    /**
     * Tests the ABBM-TR when the receiving Carrier send an invalid AKM-TR with different Line-Ids as in the RUEMVA.
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN                           ->
     *                                      <-  RUEMVA (LineID = DEU.MNET.0001)
     *     AKMTR (LineID = DEU.MNET.XXXXX   ->
     *                                      <-  ABBMTR {@link MeldungsCode#LID_OVAID}
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbgAutoProcessing_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbgAutoProcessing_02, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withBuilder(new RueckmeldungVorabstimmungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withTechnischeRessourcen(null) //remove default techn. Ressource
                        .addTechnischeRessource(new TechnischeRessourceKftBuilder(WBCI_CDM_VERSION, IOType.OUT)
                                .withVertragsnummer(null)
                                .withLineId("DEU.MNET.0001").build())));

        receiveAndVerifyAKMTR();
        verifyReceiveABBMTR(MeldungsCode.LID_OVAID);
    }

    /**
     * Tests the ABBM-TR when the receiving Carrier sends an invalid AKM-TR with different Wita-Vertrags-Nr as in the
     * RUEMVA.
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Donating Carrier)
     *     KUEMRN                           ->
     *                                      <-  RUEMVA (VertragsNr = V0001)
     *     AKMTR (VertragsNr = VXXXX        ->
     *                                      <-  ABBMTR {@link MeldungsCode#WVNR_OVAID}
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAbgAutoProcessing_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbgAutoProcessing_03, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withBuilder(new RueckmeldungVorabstimmungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withTechnischeRessourcen(null) //remove default techn. Ressource
                        .addTechnischeRessource(new TechnischeRessourceKftBuilder(WBCI_CDM_VERSION, IOType.OUT)
                                .withVertragsnummer("V0001")
                                .withLineId(null).build())));

        receiveAndVerifyAKMTR();
        verifyReceiveABBMTR(MeldungsCode.WVNR_OVAID);
    }


    @CitrusTest
    public void VaKueMrnAbgAutoProcessing_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAbgAutoProcessing_04, WBCI_CDM_VERSION, wbciVersion, geschaeftsfallTyp);

        applyBehavior(new ReceiveVA_TestBehavior());

        applyBehavior(new SendRUEMVA_TestBehavior(MeldungsCode.ZWA)
                .withBuilder(new RueckmeldungVorabstimmungKftBuilder(WBCI_CDM_VERSION, geschaeftsfallTyp, IOType.OUT)
                        .withTechnologie(Technologie.FTTB)
                        .withTechnischeRessourcen(null)  //remove default techn. Ressource
                        .addTechnischeRessource(new TechnischeRessourceKftBuilder(WBCI_CDM_VERSION, IOType.OUT)
                                .withVertragsnummer(null)
                                .withLineId("DEU.MNET.0001").build())));

        receiveAndVerifyAKMTR();
        verifyReceiveABBMTR(MeldungsCode.UETN_NM);
    }


    private void verifyReceiveABBMTR(MeldungsCode... meldungsCodes) {
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM_TR);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertVaMeldungsCodes(meldungsCodes);
        hurrican().assertVaRequestStatus(WbciRequestStatus.ABBM_TR_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        atlas().receiveCarrierChangeUpdate("ABBMTR");
    }


    private void receiveAndVerifyAKMTR() {
        atlas().sendCarrierChangeUpdate("AKMTR");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, getGeschaeftsfallTyp(), MeldungTyp.AKM_TR, 1, false);
    }

}
