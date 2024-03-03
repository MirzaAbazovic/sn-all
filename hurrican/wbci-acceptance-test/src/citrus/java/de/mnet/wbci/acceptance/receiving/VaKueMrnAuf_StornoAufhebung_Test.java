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
import de.mnet.wbci.acceptance.receiving.behavior.ReceiveRUEMVA_TestBehavior;
import de.mnet.wbci.acceptance.receiving.behavior.SendVA_TestBehavior;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.WbciVersion;

@Test(groups = BaseTest.ACCEPTANCE)
public class VaKueMrnAuf_StornoAufhebung_Test extends AbstractWbciAcceptanceTestBuilder {

    private final WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;
    private final WbciVersion wbciVersion = WbciVersion.V2;
    private final GeschaeftsfallTyp geschaeftsfallTyp = VA_KUE_MRN;

    /**
     * Tests the Storno Aufhebung sent from Receiving Carrier (Hurrican) use case with ERLM:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  STRAUF
     *     ERLM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAufhebung_01_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAufhebung_01, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        applyBehavior(new ReceiveERLM_STR_TestBehavior(RequestTyp.STR_AUFH_AUF));
    }

    /**
     * Tests the Storno Aufhebung sent from Donating Carrier (Atlas) use case with ERLM:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *     STRAUF   ->
     *              <-  ERLM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAufhebung_02_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAufhebung_02, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_ABG));

        applyBehavior(new SendERLM_STR_TestBehavior(RequestTyp.STR_AUFH_ABG));
    }

    /**
     * Tests the Storno Aufhebung sent from Receiving Carrier (Hurrican) use case with ABBM:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *              <-  STRAUF
     *     ABBM     ->
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAufhebung_03_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAufhebung_03, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new SendSTR_TestBehavior(RequestTyp.STR_AUFH_AUF));

        atlas().sendCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.IN, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_EMPFANGEN);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

    /**
     * Tests the Storno Aufhebung sent from Donating Carrier (Atlas) use case with ABBM:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *     STRAUF   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAufhebung_04_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAufhebung_04, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_ABG));

        hurrican().createWbciMeldung(
                new AbbruchmeldungKftBuilder(wbciCdmVersion, geschaeftsfallTyp, IOType.OUT)
                        .withoutWechseltermin()
                        .withMeldungsCodes(MeldungsCode.STORNO_ABG)
                        .withBegruendung("Sag ich nicht")
                        .buildForStorno(RequestTyp.STR_AUFH_ABG), RequestTyp.STR_AUFH_ABG
        );
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertStornoMeldungsCodes(MeldungsCode.STORNO_ABG);
        hurrican().assertStornoRequestStatus(WbciRequestStatus.STORNO_ABBM_VERSENDET);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

        atlas().receiveCarrierChangeUpdate("ABBM");
    }

    /**
     * Tests sending a Storno Aufhebung from Donating Carrier (Atlas) before sending a RuemVA. It should be answered
     * with an ABBM:
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     STRAUF   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAufhebung_05_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAufhebung_05, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_ABG)
                .withSkipStornoRequestChecks()
                .withSkipGeschaeftsfallChecks());

        atlas().receiveCarrierChangeUpdate("ABBM");

        hurrican().assertNumberOfRequests(StornoAufhebungAbgAnfrage.class, 1);
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, getGeschaeftsfallTyp(), MeldungTyp.ABBM);
        hurrican().assertVaRequestStatus(WbciRequestStatus.VA_VERSENDET);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);

    }


    /**
     * Tests the Storno Aufhebung sent from Donating Carrier use case with ABBM because of frist:
     * <p/>
     * <p/>
     * <pre>
     *     AtlasESB     Hurrican (Receiving Carrier)
     *              <-  KUEMRN
     *     RUEMVA   ->
     *     STRAUF   ->
     *              <-  ABBM
     * </pre>
     */
    @CitrusTest
    public void VaKueMrnAuf_StornoAufhebung_06_Test() {
        simulatorUseCase(WbciSimulatorUseCase.VaKueMrnAuf_StornoAufhebung_06, wbciCdmVersion, wbciVersion,
                geschaeftsfallTyp);

        applyBehavior(new SendVA_TestBehavior());

        applyBehavior(new ReceiveRUEMVA_TestBehavior());

        // Datum von RUEM-VA bzw. GF vorziehen, damit Storno als zu kurzfristig erkannt wird
        hurrican().changeWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(1).atStartOfDay());

        applyBehavior(new ReceiveSTR_TestBehavior(RequestTyp.STR_AUFH_ABG)
                .withSkipStornoRequestChecks()
                .withSkipGeschaeftsfallChecks());

        atlas().receiveCarrierChangeUpdate("ABBM");
        hurrican().assertIoArchiveEntryCreated(IOType.OUT, geschaeftsfallTyp, MeldungTyp.ABBM);
        hurrican().assertVaRequestStatus(WbciRequestStatus.RUEM_VA_EMPFANGEN);
        hurrican().assertKlaerfallStatus(false, null);
        hurrican().assertGfStatus(WbciGeschaeftsfallStatus.ACTIVE);
    }

}
