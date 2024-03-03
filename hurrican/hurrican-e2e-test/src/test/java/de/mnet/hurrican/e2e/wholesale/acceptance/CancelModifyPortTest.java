/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2012 15:08:35
 */
package de.mnet.hurrican.e2e.wholesale.acceptance;

import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.Auftrag2EkpFrameContractAssignedToMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.Auftrag2EkpFrameContractEkpFrameContractMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CVlan.CVlanProtocoll;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode;
import de.mnet.hurrican.e2e.common.EkpDataBuilder.EkpData;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.Produkt;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;
import de.mnet.hurrican.wholesale.workflow.CancelModifyPortFault;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersFault;
import de.mnet.hurrican.wholesale.workflow.VLAN;

@Test(groups = BaseTest.E2E, enabled = false)
public class CancelModifyPortTest extends BaseWholesaleE2ETest {

    @BeforeMethod
    protected void prepare() {
    }

    public void cancelModify() throws Exception {
        final WholesaleOrderState orderState = getNewWholesaleOrderState();
        orderState
                .product(Produkt.fttb16())
                .reservePort(today())
                .product(Produkt.fttb25().withDU())
                .executionDate(today().plusDays(30))
                .modifyPort()
                .getOrderParameters(orderState.executionDate);
        assertThat(orderState.getOrderParametersResponse.getUpStream(), equalTo(Produkt.fttb25().withDU().upstream()));

        orderState.getOrderParameters(orderState.executionDate.minusDays(1));
        assertThat(orderState.getOrderParametersResponse.getUpStream(), equalTo(Produkt.fttb16().upstream()));

        orderState.getOrderParameters(today().plusDays(1));
        assertThat(orderState.getOrderParametersResponse.getUpStream(), equalTo(Produkt.fttb16().upstream()));

        orderState
                .cancelModifyPort()
                .getOrderParameters(orderState.executionDate);
        assertThat(orderState.getOrderParametersResponse.getUpStream(), equalTo(Produkt.fttb16().upstream()));
        assertThat(orderState.dslamProfileValidAt(orderState.executionDate), hasDownstream(16000));
        assertThat(orderState.dslamProfileValidAt(orderState.executionDate), hasUpstream(1000));
    }

    public void cancelModifyWithEkpFrameContractChange() throws Exception {
        final WholesaleOrderState orderState = getNewWholesaleOrderState();
        final LocalDate modifyDate = orderState.executionDate.plusDays(5);
        EkpData ekpDataAfterModifyReservationDate = ekpDataBuilderProvider.get().getEkpData();
        EkpData ekpDataBeforeModify = orderState.ekpData;
        orderState
                .reservePort()
                .ekpData(ekpDataAfterModifyReservationDate)
                .executionDate(orderState.executionDate.plusDays(5))
                .modifyPort()
                .cancelModifyPort();
        assertThat(orderState.auftrag2EkpFrameContractAssignedAt(modifyDate),
                hasEkpFrameContract(ekpDataBeforeModify.ekpFrameContract));
        assertThat(orderState.auftrag2EkpFrameContractAssignedAt(modifyDate),
                isAssignedTo(hurricanEndDate()));
    }

    public void modifyCancelModify() throws Exception {
        final WholesaleOrderState orderState = getNewWholesaleOrderState();
        orderState
                .product(Produkt.fttb16())
                .reservePort(today())
                .product(Produkt.fttb25())
                .executionDate(today().plusDays(1))
                .modifyPort()
                .cancelModifyPort()
                .product(Produkt.fttb50())
                .executionDate(today().plusDays(1))
                .modifyPort()
                .getOrderParameters(orderState.executionDate);
        assertThat(orderState.getOrderParametersResponse.getUpStream(), equalTo(Produkt.fttb50().upstream()));
    }


    public void cancelModifyPortWithoutExistingModifyYieldsException() throws Exception {
        final WholesaleOrderState orderState = getNewWholesaleOrderState();
        orderState
                .product(Produkt.fttb50())
                .reservePort(today());
        try {
            orderState.cancelModifyPort();
        }
        catch (SoapFaultClientException e) {
            CancelModifyPortFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), WholesaleFehlerCode.ERROR_CANCEL_MODIFY_PORT.code);
            assertThat(faultDetail.getErrorDescription(), containsString("No pending modify port found"));
            return;
        }
        fail();
    }

    public void cancelModifyPortAfterCancelModifyPortYieldsException() throws Exception {
        final WholesaleOrderState orderState = getNewWholesaleOrderState();
        orderState
                .product(Produkt.fttb50())
                .reservePort(today())
                .executionDate(today().plusDays(30))
                .modifyPort()
                .cancelModifyPort();
        try {
            orderState.cancelModifyPort();
        }
        catch (SoapFaultClientException e) {
            CancelModifyPortFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), WholesaleFehlerCode.ERROR_CANCEL_MODIFY_PORT.code);
            assertThat(faultDetail.getErrorDescription(), containsString("No pending modify port found"));
            return;
        }
        fail();
    }

    public void cancelExpiredModifyPortYieldsException() throws Exception {
        final WholesaleOrderState orderState = getNewWholesaleOrderState();
        orderState
                .product(Produkt.fttb25())
                .reservePortInPast(today().minusDays(2))
                .product(Produkt.fttb50())
                .modifyPortInPast(today().minusDays(1));

        try {
            orderState.cancelModifyPort();
        }
        catch (SoapFaultClientException e) {
            CancelModifyPortFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), WholesaleFehlerCode.ERROR_CANCEL_MODIFY_PORT.code);
            assertThat(faultDetail.getErrorDescription(), containsString("No pending modify port found"));
            return;
        }
        fail();
    }

    /**
     * Wechselt mit modifyPort den EKP, bei welchem andere CVlans konfiguriert sind.
     *
     * @throws Exception
     */
    public void vlansWerdenZurueckgesetzt() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        final LocalDate modifyDate = state.executionDate.plusDays(5);
        final LocalDate beforeModifyDate = modifyDate.minusDays(1);

        List<CVlan> afterCvlans = ImmutableList.of(
                new CVlanBuilder().withTyp(CvlanServiceTyp.VOIP).withProtocoll(CVlanProtocoll.PPPoE)
                        .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build(),
                new CVlanBuilder().withTyp(CvlanServiceTyp.HSI).withProtocoll(CVlanProtocoll.PPPoE)
                        .withValue(Integer.valueOf(2)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build(),
                new CVlanBuilder().withTyp(CvlanServiceTyp.MC).withProtocoll(CVlanProtocoll.IPoE)
                        .withValue(Integer.valueOf(3)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build()
        );
        EkpData ekpDataAfterModify = ekpDataBuilderProvider.get().withCvlans(afterCvlans).getEkpData();

        state
                .reservePort()
                .executionDate(modifyDate)
                .ekpData(ekpDataAfterModify)
                .modifyPort();

        List<VLAN> vlansBeforeModify = vlans(state, beforeModifyDate);
        List<VLAN> vlansAfterModify = vlans(state, modifyDate);
        assertTrue(!equals(vlansBeforeModify, vlansAfterModify), "ekp-Aenderung muss vlans aendern");

        state
                .cancelModifyPort();

        List<VLAN> vlansAfterCancel = vlans(state, modifyDate);
        assertTrue(equals(vlansBeforeModify, vlansAfterCancel), "cancel setzt vlans nicht zurueck");
    }

    private List<VLAN> vlans(WholesaleOrderState state, final LocalDate modifyDate) {
        return state.getOrderParameters(modifyDate).getOrderParametersResponse.getVlans();
    }

    private boolean equals(List<VLAN> vlans1, List<VLAN> vlans2) {
        if (vlans1.size() != vlans2.size()) {
            return false;
        }
        outer:
        for (VLAN vlan1 : vlans1) {
            for (VLAN vlan2 : vlans2) {
                if (EqualsBuilder.reflectionEquals(vlan1, vlan2)) {
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }

    public void cancelNachPortwechselDurchModify() throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHwBaugruppeMaxBandwidth(50000)
                .withZweiteHwBaugruppeMaxBandwidth(100000)
                .getStandortData();

        LocalDate dateInPast = LocalDate.now().minusDays(1);
        LocalDate modifyDate = LocalDate.now().plusDays(5);
        LocalDate beforeModifyDate = modifyDate.minusDays(1);
        WholesaleOrderState state = getNewWholesaleOrderState();

        state
                .product(Produkt.fttb50())
                .reservePortInPast(dateInPast);

        String oldLineId = state.lineId;

        state
                .product(Produkt.fttb100())
                .changeOfPortAllowed(true)
                .executionDate(modifyDate)
                .modifyPort();

        String newLineId = state.lineId;

        assertThat("lineId hat sich nicht geaendert, somit kein Portwechsel erfolgt.", oldLineId,
                not(equalTo(newLineId)));

        state
                .cancelModifyPort();

        assertThat("lineId muss nach cancel wieder die Gleiche wie vor dem PortWechsel sein", oldLineId,
                equalTo(state.lineId));

        assertThat(50000,
                equalTo(state.getOrderParameters(beforeModifyDate).getOrderParametersResponse.getDownStream()));
        assertThat(50000,
                equalTo(state.getOrderParameters(modifyDate).getOrderParametersResponse.getDownStream()));

        try {
            state.lineId = newLineId;
            state.getOrderParameters(modifyDate);
        }
        catch (SoapFaultClientException e) {
            GetOrderParametersFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), "HUR-0006"); // LINE_ID_DOES_NOT_EXIST
            return;
        }
        fail("Modify wurde cacancelt. getOrderParamerters() darf mit neuer lineId nicht funktionieren.");
    }

    public void cancelModifyPortNachModifyPortZuHeuteIstNurEinmalErlaubt() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();

        state
                .product(Produkt.fttb50())
                .reservePort(today())
                .product(Produkt.fttb25())
                .modifyPort();
    }

    public void cancelModifyPortAufZweiModifyPortZuHeute() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();

        CVlan vlanVoip = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.VOIP)
                .withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        CVlan vlanHsi = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.HSI)
                .withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        EkpData ekpBeforeModify = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanVoip)).build();
        EkpData ekpToCancel = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanHsi)).build();

        state
                .product(Produkt.fttb50())
                .ekpData(ekpBeforeModify)
                .executionDate(today())
                .reservePort()
                .product(Produkt.fttb25().withTP())
                .modifyPort()
                .product(Produkt.fttb16())
                .ekpData(ekpToCancel)
                .modifyPort()
                .cancelModifyPort()
                .getOrderParameters()
                .assertThatA2TlExists(TechLeistung.TYP_WHOLESALE, TechLeistung.ExterneLeistung.TP.leistungNo,
                        state.executionDate, 1)
                .assertThatA2TlExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_25000.leistungNo,
                        state.executionDate, 1)
                .assertThatA2TlNotExists(TechLeistung.TYP_DOWNSTREAM,
                        TechLeistung.ExterneLeistung.DOWN_16000.leistungNo,
                        state.executionDate)
                .assertThatDslamProfileValidFromMatches(state.executionDate, hasDownstream(25000))
                .assertThatDslamProfileValidFromMatches(state.executionDate, hasUpstream(5000));

        assertThat(state.ekpFrameContractAssignedAt(state.desiredExecutionDate),
                equalTo(ekpBeforeModify.ekpFrameContract));

        assertOrderParamatersContainsVlanOfService(state, state.desiredExecutionDate, state.lineId,
                CvlanServiceTyp.VOIP);
        assertOrderParamatersContainsNotVlanOfService(state, state.desiredExecutionDate, state.lineId,
                CvlanServiceTyp.HSI);
    }

    public void cancelModifyPortAufModifyPortZuHeute() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();

        CVlan vlanVoip = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.VOIP)
                .withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        CVlan vlanHsi = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.HSI)
                .withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        EkpData ekpBeforeModify = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanVoip)).build();
        EkpData ekpToCancel = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanHsi)).build();

        state
                .product(Produkt.fttb50())
                .ekpData(ekpBeforeModify)
                .executionDate(today())
                .reservePort()
                .ekpData(ekpToCancel)
                .product(Produkt.fttb25().withTP())
                .modifyPort()
                .cancelModifyPort()
                .getOrderParameters()
                .assertThatA2TlNotExists(TechLeistung.TYP_WHOLESALE, TechLeistung.ExterneLeistung.TP.leistungNo,
                        state.executionDate)
                .assertThatA2TlExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_50000.leistungNo,
                        state.executionDate, 1)
                .assertThatA2TlNotExists(TechLeistung.TYP_DOWNSTREAM,
                        TechLeistung.ExterneLeistung.DOWN_25000.leistungNo,
                        state.executionDate)
                .assertThatDslamProfileValidFromMatches(state.executionDate, hasDownstream(50000))
                .assertThatDslamProfileValidFromMatches(state.executionDate, hasUpstream(10000));

        assertThat(state.ekpFrameContractAssignedAt(state.desiredExecutionDate),
                equalTo(ekpBeforeModify.ekpFrameContract));

        assertOrderParamatersContainsVlanOfService(state, state.desiredExecutionDate, state.lineId,
                CvlanServiceTyp.VOIP);
        assertOrderParamatersContainsNotVlanOfService(state, state.desiredExecutionDate, state.lineId,
                CvlanServiceTyp.HSI);
    }

    public void cancelModifyPortAufCancelModifyPortAufZweiModifyPortZuHeuteIstNichtErlaubt() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();

        state
                .reservePortInPast()
                .product(Produkt.fttb16())
                .executionDate(LocalDate.now())
                .modifyPort()
                .product(Produkt.fttb25())
                .modifyPort()
                .cancelModifyPort();

        try {
            state.cancelModifyPort();
        }
        catch (SoapFaultClientException e) {
            e.printStackTrace();
            return;
        }
        fail("zweiter Aufruf von cancelModifyPort sollte einen Fehler verursachen!");
    }

    @DataProvider
    public Object[][] cancelModifyPortAufModifyPortMitPortwechselZuHeuteDataProvider() {
        return new Object[][] {
                { true },
                { false }
        };
    }

    @Test(dataProvider = "cancelModifyPortAufModifyPortMitPortwechselZuHeuteDataProvider", enabled = false)
    public void cancelModifyPortAufModifyPortMitPortwechselZuHeute(boolean reservePortInPast) throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHwBaugruppeMaxBandwidth(50000)
                .withZweiteHwBaugruppeMaxBandwidth(100000)
                .getStandortData();

        WholesaleOrderState state = getNewWholesaleOrderState();

        CVlan vlanVoip = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.VOIP)
                .withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        CVlan vlanHsi = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.HSI)
                .withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        EkpData ekpBeforeModify = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanVoip)).build();
        EkpData ekpToCancel = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanHsi)).build();

        state
                .changeOfPortAllowed(true)
                .product(Produkt.fttb50())
                .ekpData(ekpBeforeModify);

        if (reservePortInPast) {
            state.reservePortInPast(today().minusDays(1));
        }
        else {
            state.reservePort(today());
        }

        // @formatter:off
        state
            .executionDate(today())
            .ekpData(ekpToCancel)
            .product(Produkt.fttb100().withTP())
            .modifyPort()
            .cancelModifyPort()
            .getOrderParameters()
            .assertThatA2TlNotExists(TechLeistung.TYP_WHOLESALE, TechLeistung.ExterneLeistung.TP.leistungNo, state.executionDate)
            .assertThatA2TlExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_50000.leistungNo, state.executionDate, 1)
            .assertThatA2TlNotExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_100000.leistungNo, state.executionDate)
            .assertThatDslamProfileValidFromMatches(state.executionDate, hasDownstream(50000))
            .assertThatDslamProfileValidFromMatches(state.executionDate, hasUpstream(10000));

        assertThat(state.ekpFrameContractAssignedAt(state.desiredExecutionDate), equalTo(ekpBeforeModify.ekpFrameContract));
        assertOrderParamatersContainsVlanOfService(state, state.desiredExecutionDate, state.lineId, CvlanServiceTyp.VOIP);
        assertOrderParamatersContainsNotVlanOfService(state, state.desiredExecutionDate, state.lineId, CvlanServiceTyp.HSI);
        // @formatter:on
    }


    @DataProvider
    public Object[][] cancelModifyPortAufModifyPortZuHeuteNachBereitstellungZuHeuteDataProvider() {
        return new Object[][] {
                { true },
                { false }
        };
    }

    @Test(dataProvider = "cancelModifyPortAufModifyPortZuHeuteNachBereitstellungZuHeuteDataProvider", enabled = false)
    public void cancelModifyPortAufModifyPortZuHeuteNachBereitstellungZuHeute(boolean changeOfPortAllowed)
            throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();

        CVlan vlanVoip = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.VOIP)
                .withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        EkpData ekp = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanVoip)).build();

        state.ekpData(ekp);

        reserveAndModifyPortTodayAndGetOrderParameters(state, changeOfPortAllowed);

        state
                .assertThatA2TlExists(TechLeistung.TYP_UPSTREAM, TechLeistung.ExterneLeistung.UP_10000.leistungNo,
                        state.desiredExecutionDate, 1)
                .assertThatDslamProfileValidFromMatches(state.desiredExecutionDate, hasUpstream(10000))
                .assertThatDslamProfileValidFromMatches(state.desiredExecutionDate, hasDownstream(50000))
                .assertVlans(state.getOrderParametersResponse.getVlans(), ekp.ekpFrameContract.getCvlans());

        assertThat(state.ekpFrameContractAssignedAt(state.desiredExecutionDate),
                equalTo(state.ekpData.ekpFrameContract));
    }

    private void reserveAndModifyPortTodayAndGetOrderParameters(final WholesaleOrderState state,
            final boolean changeOfPortAllowed) throws Exception {
        state
                .changeOfPortAllowed(changeOfPortAllowed)
                .product(Produkt.fttb50())
                .reservePort(today())
                .getOrderParameters()
                .product(Produkt.fttb100())
                .modifyPort()
                .getOrderParameters()
                .cancelModifyPort()
                .getOrderParameters();
    }
}


