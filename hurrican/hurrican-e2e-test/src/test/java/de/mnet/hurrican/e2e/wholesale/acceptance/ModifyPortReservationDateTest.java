/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.03.2012 12:04:19
 */
package de.mnet.hurrican.e2e.wholesale.acceptance;


import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.Auftrag2EkpFrameContractAssignedFromMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.Auftrag2EkpFrameContractAssignedToMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.Auftrag2EkpFrameContractEkpFrameContractMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.DslamProfileValidFromMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.model.Produkt.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CVlan.CVlanProtocoll;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode;
import de.mnet.hurrican.e2e.common.EkpDataBuilder.EkpData;
import de.mnet.hurrican.e2e.common.StandortDataBuilder.StandortData;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.Produkt;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateFault;
import de.mnet.hurrican.wholesale.workflow.VLAN;

@Test(groups = BaseTest.E2E, enabled = false)
public class ModifyPortReservationDateTest extends BaseWholesaleE2ETest {
    public void modifyPortReservationDateAfterReservePort() throws Exception {
        LocalDate modifiedDate = today().plusDays(10);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .reservePort(today().plusDays(2))
                .modifyPortReservationDate(modifiedDate)

                .assertDesiredDateIsExecutionDate()
                .assertAuftragGueltigVon(modifiedDate);
        assertThat(state.a2tls(modifiedDate, null), hasSize(3));
        assertThat(state.dslamProfile(), dslamProfileValidFrom(modifiedDate));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifiedDate), isAssignedFrom(modifiedDate));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifiedDate), isAssignedTo(hurricanEndDate()));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifiedDate.minusDays(1)), equalTo(null));
    }

    public void modifyPortReservationDateAfterModifyPort() throws Exception {
        LocalDate createdDate = today().minusDays(60);
        LocalDate modifiedDate1 = today().plusDays(2);
        LocalDate modifiedDate2 = today().plusDays(10);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb50().withTP())
                .reservePortInPast(createdDate)

                .executionDate(modifiedDate1)
                .product(fttb100().withDU())
                .modifyPort()

                .modifyPortReservationDate(modifiedDate1)
                .modifyPortReservationDate(modifiedDate2)
                .modifyPortReservationDate(modifiedDate2)

                .assertAuftragGueltigVon(createdDate);
        assertThat(state.a2tls(createdDate, modifiedDate2), hasSize(3));
        assertThat(state.a2tls(modifiedDate2, null), hasSize(2));
        assertThat(state.dslamProfiles(), hasSize(2));
        assertThat(state.dslamProfileValidAt(modifiedDate2), hasDownstream(100000));
        assertThat(state.dslamProfileValidAt(createdDate), hasDownstream(50000));
    }

    public void funktioniertNichtAufInDerVergangenheitRealisiertenAuftraegen() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();

        state.reservePortInPast();
        try {
            state.modifyPortReservationDate(tomorrow());
        }
        catch (SoapFaultClientException e) {
            ModifyPortReservationDateFault faultDetail = extractSoapFaultDetail(e);
            assertThat(faultDetail.getErrorCode(), equalTo(WholesaleFehlerCode.ERROR_MODIFY_PORT_RESERVATION_DATE.code));
            return;
        }
        fail();
    }

    public void modifyPortReservationDateAfterModifyPortWithNewEkp() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        final LocalDate modifyDate = state.executionDate;
        final LocalDate modifyReservationDate = modifyDate.plusDays(5);
        EkpData ekpDataAfterModifyReservationDate = ekpDataBuilderProvider.get().getEkpData();
        EkpData ekpDataBeforeModify = state.ekpData;

        state
                .reservePortInPast()
                .executionDate(modifyDate)
                .ekpData(ekpDataAfterModifyReservationDate)
                .modifyPort()
                .modifyPortReservationDate(modifyReservationDate)
                .getOrderParameters(modifyReservationDate);

        assertAuftrag2EkpFrameContractAssignments(state, modifyReservationDate, ekpDataAfterModifyReservationDate,
                ekpDataBeforeModify);
    }


    private void assertAuftrag2EkpFrameContractAssignments(WholesaleOrderState state, LocalDate modifyDate,
            EkpData ekpDataAfterModify, EkpData ekpDataBeforeModify) throws Exception {
        assertAuftrag2EkpFrameContractAssignments(state, modifyDate, ekpDataAfterModify, ekpDataBeforeModify,
                state.lineId, state.lineId);
    }

    public void vlansWerdenBeiNochNichtAktivenAuftragVerschoben() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();

        state
                .reservePort(today().plusDays(2));

        List<VLAN> vlansAfterReserve = vlans(state, today().plusDays(2));

        state
                .modifyPortReservationDate(today().plusDays(5));

        try {
            vlans(state, today().plusDays(4));
            fail("Es duerfen keine VLANS zum angegebenen Datum gefunden werden");
        }
        catch (Exception e) {
        }

        List<VLAN> vlansAfterModify = vlans(state, today().plusDays(5));
        assertTrue(equals(vlansAfterReserve, vlansAfterModify),
                "vlans duerfen sich bei Verschiebung des Aktivierungstags nicht aendern");
    }

    public void vlanAenderungzeitpunktWirdVerschoben() throws Exception {
        final LocalDate reserveDate = today().plusDays(2);
        final LocalDate oldModifyDate = today().plusDays(4);
        final LocalDate newModifyDate = today().plusDays(6);
        WholesaleOrderState state = getNewWholesaleOrderState();

        state
                .reservePort(reserveDate)
                .executionDate(oldModifyDate)
                .ekpData(otherEkp())
                .modifyPort();

        List<VLAN> vlansBeforeOldModifyDate = vlans(state, oldModifyDate.minusDays(1));
        List<VLAN> vlansAfterOldModifyDate = vlans(state, oldModifyDate);
        assertTrue(!equals(vlansBeforeOldModifyDate, vlansAfterOldModifyDate),
                "Test precondition: vlans muessen sich bei ekp-Aenderung aendern");

        state
                .modifyPortReservationDate(newModifyDate);

        List<VLAN> vlansBeforeNewModifyDate = vlans(state, newModifyDate.minusDays(1));
        List<VLAN> vlansAfterNewModifyDate = vlans(state, newModifyDate);
        assertTrue(!equals(vlansBeforeNewModifyDate, vlansAfterNewModifyDate),
                "Test precondition: vlans muessen sich bei ekp-Aenderung aendern");

        assertTrue(equals(vlansBeforeOldModifyDate, vlansBeforeNewModifyDate),
                "vlans duerfen sich durch Aenderungszeitpunktverschiebung nicht aendern");
        assertTrue(equals(vlansAfterOldModifyDate, vlansAfterNewModifyDate),
                "vlans duerfen sich durch Aenderungszeitpunktverschiebung nicht aendern");
    }

    private EkpData otherEkp() throws Exception {
        List<CVlan> afterCvlans = ImmutableList.of(
                new CVlanBuilder().withTyp(CvlanServiceTyp.VOIP).withProtocoll(CVlanProtocoll.PPPoE)
                        .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build(),
                new CVlanBuilder().withTyp(CvlanServiceTyp.HSI).withProtocoll(CVlanProtocoll.PPPoE)
                        .withValue(Integer.valueOf(2)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build(),
                new CVlanBuilder().withTyp(CvlanServiceTyp.MC).withProtocoll(CVlanProtocoll.IPoE)
                        .withValue(Integer.valueOf(3)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build()
        );
        EkpData ekpDataAfterModify = ekpDataBuilderProvider.get().withCvlans(afterCvlans).getEkpData();
        return ekpDataAfterModify;
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


    /**
     * Test im Zuge von WHO-1623 erstellt. Ablauf: <br> <ul> <li>Port zu heute reservieren <li>modifyPort mit neuem EKP
     * zu morgen (inkl. Port-Wechsel) <li>modifyPortReservationDate zu heute </ul> Altes Verhalten: getOrderParameters
     * auf neuer LineId zu heute hat einen Fehler erzeugt, da auf den zum Auftrag gehoerenden Daten (technische
     * Leistungen, EKP-Zuordnung, EqVlans, DSLAM-Profil) das Datum nicht auf 'heute' angepasst wurde. <br> Der Test
     * prueft jetzt genau diesen Ablauf und fuehrt ein getOrderParameters auf der neuen LineId zu 'heute' durch.
     */
    public void reservePortTodayModifyPortForTomorrowWithPortChangeAndModifyPortReservationDateToToday() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        final LocalDate modifyPortDate = tomorrow();
        final LocalDate modifyReservationDate = modifyPortDate.minusDays(1);

        final Integer expectedDownstreamBeforePortChanged = 16000;
        final Integer expectedDownstreamAfterPortChanged = 100000;

        final EkpData ekpDataAfterModifyPort = ekpDataBuilderProvider.get().getEkpData();
        final StandortData standortData = standortDataWithBandwiths(expectedDownstreamBeforePortChanged, expectedDownstreamAfterPortChanged);
        state.standortData(standortData);
        state
                .product(fttb16())
                .reservePort(today());
        final String previousLineId = state.lineId;

        state
                .product(fttb100().withTP())
                .changeOfPortAllowed(true)
                .executionDate(modifyPortDate)
                .ekpData(ekpDataAfterModifyPort)
                .modifyPort()
                .modifyPortReservationDate(modifyReservationDate);
        final String newLineId = state.lineId;

        assertThat(newLineId, not(equalTo(previousLineId)));
        assertOrderParamatersContainsVlanOfService(state, modifyReservationDate, newLineId, CvlanServiceTyp.HSI);
        assertOrderParametersHasDownstream(state, modifyReservationDate, expectedDownstreamAfterPortChanged, newLineId);
    }


    public void modifyPortReservationDateNachPortwechsel() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        final LocalDate modifyPortDate = tomorrow();
        final LocalDate modifyReservationDate = modifyPortDate.plusDays(1);
        final Integer expectedDownstreamBeforePortChanged = 50000;
        final Integer expectedDownstreamAfterPortChanged = 100000;

        final EkpData ekpDataAfterModifyReservationDate = ekpDataBuilderProvider.get().getEkpData();
        final EkpData ekpDataBeforeModify = state.ekpData;

        final StandortData standortData = standortDataWithBandwiths(expectedDownstreamBeforePortChanged, expectedDownstreamAfterPortChanged);

        state.standortData(standortData);

        state
                .product(fttb50())
                .reservePort(today());
        final String previousLineId = state.lineId;

        state
                .product(fttb100().withTP())
                .changeOfPortAllowed(true)
                .executionDate(modifyPortDate)
                .ekpData(ekpDataAfterModifyReservationDate)
                .modifyPort()
                .modifyPortReservationDate(modifyPortDate)
                .modifyPortReservationDate(modifyReservationDate)
                        // HUR-11341: 2ter Aufruf mit gleichem Datum muss ignoriert werden und darf nicht zu Exception fuehren
                .modifyPortReservationDate(modifyReservationDate);
        final String newLineId = state.lineId;

        assertThat(newLineId, not(equalTo(previousLineId)));
        assertOrderParametersHasDownstream(state, modifyReservationDate.minusDays(1), expectedDownstreamBeforePortChanged,
                previousLineId);
        assertOrderParametersHasDownstream(state, modifyReservationDate, expectedDownstreamAfterPortChanged, newLineId);

        assertAuftrag2EkpFrameContractAssignments(state, modifyReservationDate, ekpDataAfterModifyReservationDate,
                ekpDataBeforeModify, previousLineId, newLineId);
        assertDslamProfileAssignments(state, modifyReservationDate, expectedDownstreamBeforePortChanged,
                expectedDownstreamAfterPortChanged, previousLineId, newLineId);

        // CVLANS pruefen -> MC nur in neuem Auftrag
        assertOrderParamatersContainsVlanOfService(state, modifyReservationDate, newLineId, CvlanServiceTyp.MC);
        assertOrderParamatersContainsNotVlanOfService(state, modifyReservationDate.minusDays(1), previousLineId,
                CvlanServiceTyp.MC);

        state.lineId(previousLineId);
        state.assertThatA2TlNotExists(TechLeistung.TYP_WHOLESALE, ExterneLeistung.TP.getLeistungNo(),
                modifyReservationDate.minusDays(1));
        state.lineId(newLineId);
        state.assertThatA2TlExists(TechLeistung.TYP_WHOLESALE, ExterneLeistung.TP.getLeistungNo(),
                modifyReservationDate, 1);
    }

    private void assertOrderParametersHasDownstream(WholesaleOrderState state, final LocalDate when,
            final Integer expectedDownstream, final String lineId) {
        final String lineIdTmp = state.lineId;
        state.lineId(lineId);
        GetOrderParametersResponse orderParamsAfterChangeOfPort = getOrderParametersResponse(state, when, lineId);
        assertThat(orderParamsAfterChangeOfPort.getDownStream(), equalTo(expectedDownstream));
        state.lineId(lineIdTmp);
    }

    private void assertDslamProfileAssignments(WholesaleOrderState state, final LocalDate modifyReservationDate,
            final Integer expectedDownstreamBeforePortChanged, final Integer expectedDownstreamAfterPortChanged,
            final String previousLineId, final String newLineId) throws Exception {
        final String lineIdTmp = state.lineId;
        state.lineId(previousLineId);
        assertThat(state.dslamProfileValidAt(modifyReservationDate.minusDays(1)),
                hasDownstream(expectedDownstreamBeforePortChanged));
        state.lineId(newLineId);
        assertThat(state.dslamProfileValidAt(modifyReservationDate),
                hasDownstream(expectedDownstreamAfterPortChanged));
        state.lineId(lineIdTmp);
    }

    private void assertAuftrag2EkpFrameContractAssignments(WholesaleOrderState state,
            final LocalDate modifyDate, final EkpData ekpDataAfterModify,
            final EkpData ekpDataBeforeModify, final String lineIdBeforeModify, final String lineIdAfterModify)
            throws Exception {
        final String lineIdTmp = state.lineId;

        // @formatter:off
        state.lineId(lineIdBeforeModify);
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifyDate.minusDays(1)), hasEkpFrameContract(ekpDataBeforeModify.ekpFrameContract));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifyDate.minusDays(1)), isAssignedTo(modifyDate));

        state.lineId(lineIdAfterModify);
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifyDate), hasEkpFrameContract(ekpDataAfterModify.ekpFrameContract));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifyDate), isAssignedFrom(modifyDate));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifyDate), isAssignedTo(hurricanEndDate()));
        // @formatter:on

        state.lineId(lineIdTmp);
    }

    private StandortData standortDataWithBandwiths(final Integer downstream1, final Integer downstream2)
            throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHwBaugruppeMaxBandwidth(downstream1)
                .withZweiteHwBaugruppeMaxBandwidth(downstream2)
                .getStandortData();

        return standortData;
    }

    public void modifyPortReservationDateAufModifyPortZuHeute() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();

        CVlan vlanVoip = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.VOIP)
                .withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        CVlan vlanHsi = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.HSI)
                .withProtocoll(CVlanProtocoll.IPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        EkpData ekpBeforeModify = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanVoip)).build();
        EkpData ekpAfterModify = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanHsi)).build();

        final LocalDate reservationAndModifyDate = today();
        final LocalDate modifyPortReseravtionDate = tomorrow();

        final int expectedDownStreamBeforeModify = 50000;
        final int expectedDownStreamAfterModify = 25000;

        // @formatter:off
        state
                .executionDate(reservationAndModifyDate)
                .product(Produkt.fttb50())
                .ekpData(ekpBeforeModify)
                .reservePort()
                .product(Produkt.fttb25().withTP())
                .ekpData(ekpAfterModify)
                .modifyPort()
                .modifyPortReservationDate(modifyPortReseravtionDate)
                .assertThatA2TlExists(TechLeistung.TYP_WHOLESALE, TechLeistung.ExterneLeistung.TP.leistungNo, modifyPortReseravtionDate, 1)
                .assertThatA2TlNotExists(TechLeistung.TYP_WHOLESALE, TechLeistung.ExterneLeistung.TP.leistungNo, reservationAndModifyDate)
                .assertThatA2TlExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_50000.leistungNo, reservationAndModifyDate, 1)
                .assertThatA2TlNotExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_50000.leistungNo, modifyPortReseravtionDate)
                .assertThatA2TlExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_25000.leistungNo, modifyPortReseravtionDate, 1);
        assertDslamProfileAssignments(state, modifyPortReseravtionDate, expectedDownStreamBeforeModify, expectedDownStreamAfterModify, state.lineId, state.lineId);
        assertAuftrag2EkpFrameContractAssignments(state, modifyPortReseravtionDate, ekpAfterModify, ekpBeforeModify);
        assertOrderParamatersContainsVlanOfService(state, reservationAndModifyDate, state.lineId, CvlanServiceTyp.VOIP);
        assertOrderParamatersContainsVlanOfService(state, modifyPortReseravtionDate, state.lineId, CvlanServiceTyp.HSI);
        // @formatter:on
    }

    @DataProvider
    public Object[][] modifyPortReservationDateAufModifyPortZuHeuteMitPortwechselDataProvider() {
        return new Object[][] {
                { true },
                { false }
        };
    }

    @Test(dataProvider = "modifyPortReservationDateAufModifyPortZuHeuteMitPortwechselDataProvider", enabled = false)
    public void modifyPortReservationDateAufModifyPortZuHeuteMitPortwechsel(final boolean reservePortToday)
            throws Exception {
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
                .withProtocoll(CVlanProtocoll.IPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        EkpData ekpBeforeModify = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanVoip)).build();
        EkpData ekpAfterModify = ekpDataBuilderProvider.get().withCvlans(Lists.newArrayList(vlanHsi)).build();

        final LocalDate modifyDate = today();
        final LocalDate modifyPortReseravtionDate = tomorrow();

        final int expectedDownStreamBeforeModify = 50000;
        final int expectedDownStreamAfterModify = 100000;

        state
                .product(Produkt.fttb50())
                .ekpData(ekpBeforeModify);

        if (reservePortToday) {
            state.reservePort(today());
        }
        else {
            state.reservePortInPast(today().minusDays(1));
        }

        final String lineIdBeforePortChange = state.lineId;

        state
                .executionDate(modifyDate)
                .product(Produkt.fttb100().withTP())
                .ekpData(ekpAfterModify)
                .changeOfPortAllowed(true)
                .modifyPort()
                .modifyPortReservationDate(modifyPortReseravtionDate);

        final String lineIdAfterPortChange = state.lineId;

        // @formatter:off
        state
                .lineId(lineIdAfterPortChange)
                .assertThatA2TlNotExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_50000.leistungNo, modifyPortReseravtionDate)
                .assertThatA2TlExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_100000.leistungNo, modifyPortReseravtionDate, 1);

        state
                .lineId(lineIdBeforePortChange)
                .assertAuftragDatenStatus(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .assertAuftragDatenKuendigung(Date.from(modifyPortReseravtionDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        assertDslamProfileAssignments(state, modifyPortReseravtionDate, expectedDownStreamBeforeModify,
                expectedDownStreamAfterModify, lineIdBeforePortChange, lineIdAfterPortChange);
        assertAuftrag2EkpFrameContractAssignments(state, modifyPortReseravtionDate, ekpAfterModify, ekpBeforeModify,
                lineIdBeforePortChange, lineIdAfterPortChange);

        assertOrderParamatersContainsVlanOfService(state, modifyDate, lineIdBeforePortChange, CvlanServiceTyp.VOIP);
        assertOrderParamatersContainsVlanOfService(state, modifyPortReseravtionDate, lineIdAfterPortChange, CvlanServiceTyp.HSI);
        // @formatter:on
    }

}


