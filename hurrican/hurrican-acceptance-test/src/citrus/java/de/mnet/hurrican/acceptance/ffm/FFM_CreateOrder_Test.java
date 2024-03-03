/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.mnet.hurrican.acceptance.ffm;

import java.time.*;
import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HWDpoBuilder;
import de.augustakom.hurrican.model.cc.HWDpuBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;
import de.mnet.hurrican.ffm.citrus.actions.CreateAndSendOrderServiceAction;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class FFM_CreateOrder_Test extends AbstractFfmTestBuilder {

    // @formatter:off
    /**
     * Tests triggers save order FFM operation on Atlas ESB.
     * <p/>
     * <p/>
     * <pre>
     *     Hurrican                         AtlasESB
     *     createOrder         ->
     *       |
     *       --> throw FFMServiceException (invalid data because 'Bauauftrag' is null)
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_01_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_01);

        //check if an FFMServiceException have been thrown
        action(new CreateAndSendOrderServiceAction(hurrican().getFFMService(), new Verlauf())
                .expectException(FFMServiceException.class));
    }

    // @formatter:off
    /**
     * Tests triggers save order FFM operation on Atlas ESB.
     * <p/>
     * <p/>
     * <pre>
     *     Hurrican                         AtlasESB
     *     createOrder         ->
     *       |
     *       --> throw FFMServiceException (invalid data because 'Bauauftrag' is null)
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_02_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_02);

        //check if an FFMServiceException have been thrown
        action(new CreateAndSendOrderServiceAction(hurrican().getFFMService(), new Verlauf())
                .expectException(FFMServiceException.class));
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican 'Bauauftag' with sub order and triggers
     * the {@link FFMService#createAndSendOrder(Verlauf)} method. <br/>
     * Aggregates only the 'header' data with fixed time slot (HVT/FTTC) and a bunch of technical data.
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_03_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_03);

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();

        final Pair<Auftrag, AuftragDaten> subOrder = hurrican().getHurricanAuftragBuilder()
                .buildHurricanAuftrag(
                        createdData.auftragDaten.getProdId(),
                        createdData.auftrag.getKundeNo(),
                        createdData.auftragDaten.getAuftragNoOrig(),
                        HVTStandort.HVT_STANDORT_TYP_HVT,
                        false, false, false);

        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setSubAuftragsIds(new HashSet<>(Collections.singletonList(subOrder.getFirst().getAuftragId())));
        hurrican().getAuftragDAO().store(bauauftrag);

        hurrican().createAndSendOrder(bauauftrag);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican order and triggers the {@link FFMService#createAndSendOrder(Verlauf)}
     * method. <br/>
     * Aggregates the header data AND technical parameters (FTTB/H).
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_04_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_04);

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withVpn(true)
                .buildBauauftrag();

        hurrican().createAndSendOrder(createdData.verlauf);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican housing order and triggers the
     * {@link FFMService#createAndSendOrder(Verlauf)} method. <br/>
     * Aggregates the header data AND technical parameters (Housing).
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_05_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_05);

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(370L)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_ABSTRACT)
                .withCountOfDialNumbers(0)
                .withHousing(true)
                .buildBauauftrag();

        hurrican().createAndSendOrder(createdData.verlauf);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican 'Bauauftag' with sub order and triggers
     * the {@link FFMService#createAndSendOrder(Verlauf)} method. <br/>
     * Aggregates only the 'header' data with fixed time slot (HVT/FTTC) and a bunch of technical data.
     * This time also additional ffm qualifications from technische Leistungen are configured, so planned duration
     * list of qualifications are adjusted.
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_06_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_06);

        VerlaufTestBuilder testBuilder = new VerlaufTestBuilder();
        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();

        final Pair<Auftrag, AuftragDaten> subOrder = hurrican().getHurricanAuftragBuilder()
                .buildHurricanAuftrag(
                        createdData.auftragDaten.getProdId(),
                        createdData.auftrag.getKundeNo(),
                        createdData.auftragDaten.getAuftragNoOrig(),
                        HVTStandort.HVT_STANDORT_TYP_HVT,
                        false, false, false);

        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setSubAuftragsIds(new HashSet<>(Collections.singletonList(subOrder.getFirst().getAuftragId())));
        hurrican().getAuftragDAO().store(bauauftrag);

        Auftrag2TechLeistung downstream = new Auftrag2TechLeistungBuilder()
                .withTechLeistungId(301L)         // = VOIP_TK
                .withAktivVon(bauauftrag.getRealisierungstermin())
                .withVerlaufIdReal(bauauftrag.getId())
                .setPersist(false).build();
        downstream.setAuftragId(bauauftrag.getAuftragId());
        hurrican().getAuftragDAO().store(downstream);

        hurrican().createAndSendOrder(bauauftrag);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican 'Kündigung-Bauauftag' with sub order and triggers
     * the {@link FFMService#createAndSendOrder(Verlauf)} method. <br/>
     * Aggregates only the 'header' data with fixed time slot (HVT/FTTC) and a bunch of technical data.
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_07_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_07);

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withBaVerlaufAnlass(BAVerlaufAnlass.KUENDIGUNG)
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();

        final Pair<Auftrag, AuftragDaten> subOrder = hurrican().getHurricanAuftragBuilder()
                .buildHurricanAuftrag(
                        createdData.auftragDaten.getProdId(),
                        createdData.auftrag.getKundeNo(),
                        createdData.auftragDaten.getAuftragNoOrig(),
                        HVTStandort.HVT_STANDORT_TYP_HVT,
                        false, false, false);

        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setSubAuftragsIds(new HashSet<>(Collections.singletonList(subOrder.getFirst().getAuftragId())));
        hurrican().getAuftragDAO().store(bauauftrag);

        hurrican().createAndSendOrder(bauauftrag);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican 'Kündigung-Bauauftag' with sub order and triggers
     * the {@link FFMService#createAndSendOrder(Verlauf)} method. <br/>
     * Aggregates only the 'header' data with fixed time slot (HVT/FTTC) and a bunch of technical data.
     * <p/>
     * Da das realisierungsdatum des Bauftrags in der VerlaufAbteilung manuell überschrieben wird,
     * soll das Zeitfenster 8:00 - 18:00 zu FFM geschickt werden.
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_08_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_08);

        Date verlAbtRealisierungsdatum = Date.from(LocalDate.now().plusDays(10).atStartOfDay(ZoneId.systemDefault()).toInstant());
        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withBauauftragRealisierungsdatum(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withVerlaufAbteilungRealisierungsdatum(verlAbtRealisierungsdatum)
                .withBaVerlaufAnlass(BAVerlaufAnlass.KUENDIGUNG)
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();

        variable(VariableNames.REALISIERUNGSTERMIN,
                DateTools.formatDate(verlAbtRealisierungsdatum, DateTools.PATTERN_YEAR_MONTH_DAY));

        final Pair<Auftrag, AuftragDaten> subOrder = hurrican().getHurricanAuftragBuilder()
                .buildHurricanAuftrag(
                        createdData.auftragDaten.getProdId(),
                        createdData.auftrag.getKundeNo(),
                        createdData.auftragDaten.getAuftragNoOrig(),
                        HVTStandort.HVT_STANDORT_TYP_HVT,
                        false, false, false);

        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setSubAuftragsIds(new HashSet<>(Collections.singletonList(subOrder.getFirst().getAuftragId())));
        hurrican().getAuftragDAO().store(bauauftrag);

        hurrican().createAndSendOrder(bauauftrag);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican 'Bauauftag' with sub order and triggers
     * the {@link FFMService#createAndSendOrder(Verlauf)} method. <br/>
     * Aggregates only the 'header' data with fixed time slot (HVT/FTTC) and a bunch of technical data.
     * This time also additional ffm qualifications from technische Leistungen are configured, so planned duration
     * list of qualifications are adjusted.
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_09_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_09);

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_SDSL_10000)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_KVZ)
                .buildBauauftrag();

        final Pair<Auftrag, AuftragDaten> subOrder = hurrican().getHurricanAuftragBuilder()
                .buildHurricanAuftrag(
                        createdData.auftragDaten.getProdId(),
                        createdData.auftrag.getKundeNo(),
                        createdData.auftragDaten.getAuftragNoOrig(),
                        HVTStandort.HVT_STANDORT_TYP_KVZ,
                        false, false, false);

        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setSubAuftragsIds(new HashSet<>(Collections.singletonList(subOrder.getFirst().getAuftragId())));
        hurrican().getAuftragDAO().store(bauauftrag);

        hurrican().createAndSendOrder(bauauftrag);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican order and triggers the {@link FFMService#createAndSendOrder(Verlauf)}
     * method. <br/>
     * Aggregates the header data AND technical parameters (FTTB/H).
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_10_Test() throws Exception {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_10);

        String chassisBezeichnung = "CH-12";
        setVariable(VariableNames.CHASSIS_BEZEICHNUNG, chassisBezeichnung);
        String chassisPosition = "1";
        setVariable(VariableNames.CHASSIS_POSITION, chassisPosition);
        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withHwOltChild(
                        new HWDpoBuilder()
                                .withSerialNo("serial-no")
                                .withChassisIdentifier(chassisBezeichnung)
                                .withChassisSlot(chassisPosition)
                                .withRackTyp(HWRack.RACK_TYPE_DPO)
                                .setPersist(false)
                                .build()
                )
                .withProdId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withVoIP(true)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withVpn(true)
                .buildBauauftrag();

        hurrican().assignSwitchToAuftragTechnik(createdData.auftrag.getAuftragId());

        hurrican().createAndSendOrder(createdData.verlauf);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican order 'Interne Arbeit' and triggers the
     * {@link FFMService#createAndSendOrder(Verlauf)} method. <br/>
     * Aggregates the header data (Interne Arbeit).
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_11_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_11);

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_INTERNE_ARBEIT)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .withInterneArbeit(true)
                .buildBauauftrag();

        hurrican().createAndSendOrder(createdData.verlauf);

        atlas().receiveCreateOrder("createOrder");
    }

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican order and triggers the {@link FFMService#createAndSendOrder(Verlauf)}
     * method. <br/>
     * Aggregates the header data AND technical parameters (FTTB/H).
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     createOrder       ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_CreateOrder_12_Dpu_With_Reverse_Power_Test() throws Exception {
        simulatorUseCase(SimulatorUseCase.FFM_CreateOrder_12);

        String chassisBezeichnung = "CH-12";
        setVariable(VariableNames.CHASSIS_BEZEICHNUNG, chassisBezeichnung);
        String chassisPosition = "1";
        setVariable(VariableNames.CHASSIS_POSITION, chassisPosition);
        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withHwOltChild(
                        new HWDpuBuilder()
                                .withSerialNo("serial-no")
                                .withReversePower(Boolean.TRUE)
                                .withRackTyp(HWRack.RACK_TYPE_DPU)
                                .setPersist(false)
                                .build()
                )
                .withProdId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .buildBauauftrag();

        hurrican().createAndSendOrder(createdData.verlauf);

        atlas().receiveCreateOrder("createOrder");
    }

}
