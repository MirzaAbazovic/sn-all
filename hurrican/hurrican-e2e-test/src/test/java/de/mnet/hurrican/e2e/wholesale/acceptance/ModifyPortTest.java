/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2012 10:38:42
 */
package de.mnet.hurrican.e2e.wholesale.acceptance;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.Auftrag2EkpFrameContractAssignedFromMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.Auftrag2EkpFrameContractAssignedToMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.Auftrag2EkpFrameContractEkpFrameContractMatcher.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.model.Produkt.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.not;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.Lists;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung.ExterneLeistung;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CVlan.CVlanProtocoll;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.mnet.hurrican.e2e.common.EkpDataBuilder.EkpData;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.Produkt;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersFault;
import de.mnet.hurrican.wholesale.workflow.ModifyPortFault;

/**
 * Acceptance-Tests fuer WHS10 - Leistungsaenderung
 */
@Test(groups = E2E, enabled = false)
public class ModifyPortTest extends BaseWholesaleE2ETest {

    public void ftthIstNichtErlaubt() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .reservePort(today().plusDays(2))
                .product(Produkt.ftth16());
        try {
            state.modifyPort();
        }
        catch (SoapFaultClientException e) {
            ModifyPortFault faultDetail = extractSoapFaultDetail(e);
            assertThat(faultDetail.getErrorCode(), containsString("HUR"));
            assertThat(faultDetail.getErrorDescription(), containsString("FTTH"));
            assertThat(faultDetail.getErrorDescription(), containsString("not supported"));
            return;
        }
        fail();

    }

    public void einfacherZuDoppeltemUpstream() throws Exception {
        LocalDate modifiedDate = LocalDate.now().plusDays(5);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb50())
                .reservePort()
                .product(Produkt.fttb50().withDU())
                .executionDate(modifiedDate)
                .modifyPort();
        assertThat(state.dslamProfileValidAt(modifiedDate), hasDownstream(50000));
        assertThat(state.dslamProfileValidAt(modifiedDate), hasUpstream(20000));
        assertThat(state.dslamProfileValidAt(modifiedDate.minusDays(1)), hasUpstream(10000));
    }

    public void doppelterZuEinfachemUpstream() throws Exception {
        LocalDate modifiedDate = LocalDate.now().plusDays(5);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb50().withDU())
                .reservePort()
                .product(Produkt.fttb50())
                .executionDate(modifiedDate)
                .modifyPort();
        assertThat(state.dslamProfileValidAt(modifiedDate), hasDownstream(50000));
        assertThat(state.dslamProfileValidAt(modifiedDate), hasUpstream(10000));
        assertThat(state.dslamProfileValidAt(modifiedDate.minusDays(1)), hasUpstream(20000));
    }

    public void fttb25ZuFttb50() throws Exception {
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        LocalDate modifiedDate = LocalDate.now().plusDays(5);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb25())
                .executionDate(reservationDate)
                .reservePort()
                .product(Produkt.fttb50())
                .executionDate(modifiedDate)
                .changeOfPortAllowed(true)
                .modifyPort();
        assertThat("Portwechsel darf nicht reportet werden", false, equalTo(state.isPortChanged));
        assertThat(state.dslamProfileValidAt(reservationDate), hasDownstream(25000));
        assertThat(state.dslamProfileValidAt(reservationDate), hasUpstream(5000));
        assertThat(state.dslamProfileValidAt(modifiedDate), hasDownstream(50000));
        assertThat(state.dslamProfileValidAt(modifiedDate), hasUpstream(10000));
    }

    public void modifyPortOnActiveModifyPortNotAllowed() throws Exception {
        LocalDate modifiedDate = LocalDate.now().plusDays(5);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb25())
                .reservePort()
                .product(Produkt.fttb50())
                .executionDate(modifiedDate)
                .modifyPort();

        try {
            state
                    .executionDate(modifiedDate.plusDays(1))
                    .modifyPort(); // zweiter modifyPort muss zu Fehler fuehren, sofern nicht am selben Tag!
        }
        catch (SoapFaultClientException e) {
            ModifyPortFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), "HUR-0010");
            return;
        }
        fail();
    }

    public void modifyPortOnExpiredModifyPortAllowed() throws Exception {
        LocalDate dateInPast = LocalDate.now().minusDays(1);
        LocalDate dateInFuture = LocalDate.now().plusDays(1);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb25())
                .reservePortInPast(dateInPast)
                .product(Produkt.fttb50())
                .modifyPortInPast(dateInPast)
                .product(Produkt.fttb50().withTP())
                .executionDate(dateInFuture)
                .modifyPort(); // zweiter modifyPort fuehrt nicht zu Fehler, da
        // erster bereits abgelaufen!
    }

    public void leistungszugangTp() throws Exception {
        final LocalDate executionDateInFuture = LocalDate.now().plusDays(5);
        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .reservePort()
                .executionDate(executionDateInFuture)
                .product(Produkt.fttb50().withTP())
                .modifyPort()
                .assertThatA2TlExists(TechLeistung.TYP_WHOLESALE, ExterneLeistung.TP.getLeistungNo(), executionDateInFuture, 1)
                .assertThatA2TlExists(TechLeistung.TYP_WHOLESALE, ExterneLeistung.TP.getLeistungNo(), today(), 0);
    }

    public void auftragAktionsIdIsDefinedAtModifyPort() throws Exception {
        final LocalDate executionDateInFuture = LocalDate.now().plusDays(5);
        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .reservePort()
                .executionDate(executionDateInFuture)
                .product(Produkt.fttb50().withDU())
                .modifyPort()
                .assertThatA2TlExists(TechLeistung.TYP_UPSTREAM, ExterneLeistung.UP_20000.getLeistungNo(), executionDateInFuture, 1)
                .assertThatA2TlExists(TechLeistung.TYP_UPSTREAM, ExterneLeistung.UP_20000.getLeistungNo(), today(), 0)
                .assertThatA2TlHasAuftragAktionsId(ExterneLeistung.UP_20000.getLeistungNo(), executionDateInFuture, null, true, false)  // 20.000 Upstream kommt hinzu und hat Aktions-ID
                .assertThatA2TlHasAuftragAktionsId(ExterneLeistung.UP_10000.getLeistungNo(), executionDateInFuture, null, false, true); // 10.000 Upstream wird deaktiviert und hat Aktions-ID
    }

    public void ekpAenderung() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        final LocalDate modifyDate = state.executionDate.plusDays(5);
        final LocalDate beforeModifyDate = modifyDate.minusDays(1);
        List<CVlan> afterCvlans = new ArrayList<CVlan>();
        afterCvlans.add(new CVlanBuilder().withTyp(CvlanServiceTyp.VOIP).withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build());
        afterCvlans.add(new CVlanBuilder().withTyp(CvlanServiceTyp.HSI).withProtocoll(CVlanProtocoll.PPPoE)
                .withValue(Integer.valueOf(2)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build());
        afterCvlans.add(new CVlanBuilder().withTyp(CvlanServiceTyp.MC).withProtocoll(CVlanProtocoll.IPoE)
                .withValue(Integer.valueOf(3)).withPbitLimit(Integer.valueOf(3)).setPersist(false).build());
        EkpData ekpDataAfterModify = ekpDataBuilderProvider.get().withCvlans(afterCvlans).getEkpData();
        EkpData ekpDataBeforeModify = state.ekpData;

        state
                .reservePort()
                .executionDate(modifyDate)
                .ekpData(ekpDataAfterModify)
                .modifyPort();

        assertThat(state.auftrag2EkpFrameContractAssignedAt(beforeModifyDate),
                hasEkpFrameContract(ekpDataBeforeModify.ekpFrameContract));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(beforeModifyDate),
                isAssignedTo(modifyDate));
        state.assertVlans(state.getOrderParameters(beforeModifyDate).getOrderParametersResponse.getVlans(),
                ekpDataBeforeModify.ekpFrameContract.getCvlans());

        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifyDate),
                hasEkpFrameContract(ekpDataAfterModify.ekpFrameContract));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifyDate), isAssignedFrom(modifyDate));
        assertThat(state.auftrag2EkpFrameContractAssignedAt(modifyDate),
                isAssignedTo(hurricanEndDate()));

        state.assertVlans(state.getOrderParameters(modifyDate).getOrderParametersResponse.getVlans(), afterCvlans);
    }

    public void downstreamBandbreite100OhnePortwechselNichtMoeglich() throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHwBaugruppeMaxBandwidth(50000)
                .getStandortData();

        LocalDate modifiedDate = LocalDate.now().plusDays(5);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb50())
                .reservePort()
                .product(Produkt.fttb100())
                .executionDate(modifiedDate);
        try {
            state.modifyPort();
        }
        catch (SoapFaultClientException e) {
            ModifyPortFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), "HUR-1300");
            return;
        }
        fail("max. Bandbreite der Baugruppe = 50000. ModifyPort Erhoehung auf 100000 darf nicht funktionieren.");
    }

    public void downstreamBandbreite100MitPortwechselNichtMoeglich() throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHwBaugruppeMaxBandwidth(50000)
                .getStandortData();

        LocalDate modifiedDate = LocalDate.now().plusDays(5);

        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(Produkt.fttb50())
                .reservePort()
                .product(Produkt.fttb100())
                .changeOfPortAllowed(true)
                .executionDate(modifiedDate);
        try {
            state.modifyPort();
        }
        catch (SoapFaultClientException e) {
            ModifyPortFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), "HUR-1300");
            return;
        }
        fail("max. Bandbreite der Baugruppe = 50000. ModifyPort Erhoehung auf 100000 darf auch mit erlaubten PortWechsel nicht funktionieren, da nur 50000 zur Verfuegung stehen.");
    }

    public void downstreamBandbreite100MitPortwechselNachInbetriebnahmeMoeglich() throws Exception {
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

        assertThat("Portwechsel hat nicht stattgefunden", newLineId, not(equalTo(oldLineId)));
        assertThat("Portwechsel muss auch reportet werden", true, equalTo(state.isPortChanged));
        state.lineId = newLineId;
        assertThat(100000, equalTo(state.getOrderParameters(modifyDate).getOrderParametersResponse.getDownStream()));
        state.lineId = oldLineId;
        assertThat(50000,
                equalTo(state.getOrderParameters(beforeModifyDate).getOrderParametersResponse.getDownStream()));
    }

    /*
     * Testfall ist eigentlich nicht notwendig, da S/PRI Schnittstelle einen modifyPort erst nach
     * der techn. Realisierung von reservePort zulaesst!
     */
    public void downstreamBandbreite100MitPortwechselVorInbetriebnahmeFuerSpaeterMoeglich() throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHwBaugruppeMaxBandwidth(50000)
                .withZweiteHwBaugruppeMaxBandwidth(100000)
                .getStandortData();

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate modifyDate = LocalDate.now().plusDays(5);
        LocalDate beforeModifyDate = modifyDate.minusDays(1);
        WholesaleOrderState state = getNewWholesaleOrderState();

        state
                .product(Produkt.fttb50())
                .reservePort(tomorrow);
        String oldLineId = state.lineId;

        state
                .product(Produkt.fttb100())
                .changeOfPortAllowed(true)
                .executionDate(modifyDate)
                .modifyPort();
        String newLineId = state.lineId;

        assertThat("Portwechsel hat nicht stattgefunden", newLineId, not(equalTo(oldLineId)));
        state.lineId = newLineId;
        assertThat(100000, equalTo(state.getOrderParameters(modifyDate).getOrderParametersResponse.getDownStream()));
        state.lineId = oldLineId;
        assertThat(50000,
                equalTo(state.getOrderParameters(beforeModifyDate).getOrderParametersResponse.getDownStream()));
    }

    /*
     * Testfall ist eigentlich nicht notwendig, da ein modifyPort mit min. 36 Std. Vorlauf in die S/PRI
     * eingestellt werden muss; dies ist auch erst nach der Erledigung / dem Abschluss von reservePort moeglich.
     * Somit kann der modifyPort erst zwei Tage NACH dem urspruenglichen Realisierungsdatum erfolgen.
     */
    public void downstreamBandbreite100MitPortwechselFuerInbetriebnahmedatumMoeglich() throws Exception {
        standortData = standortDataBuilderProvider.get()
                .withStandortTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withHwBaugruppeMaxBandwidth(50000)
                .withZweiteHwBaugruppeMaxBandwidth(100000)
                .getStandortData();

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate modifyDate = tomorrow;
        WholesaleOrderState state = getNewWholesaleOrderState();

        state
                .product(Produkt.fttb50())
                .reservePort(tomorrow);
        String oldLineId = state.lineId;

        state
                .product(Produkt.fttb100())
                .changeOfPortAllowed(true)
                .executionDate(modifyDate)
                .modifyPort();
        String newLineId = state.lineId;

        assertThat("Portwechsel hat nicht stattgefunden", newLineId, not(equalTo(oldLineId)));
        state.lineId = newLineId;
        assertThat(100000, equalTo(state.getOrderParameters(modifyDate).getOrderParametersResponse.getDownStream()));

        try {
            state.lineId = oldLineId;
            state.getOrderParameters(tomorrow);
        }
        catch (SoapFaultClientException e) {
            GetOrderParametersFault faultDetail = extractSoapFaultDetail(e);
            assertEquals(faultDetail.getErrorCode(), "HUR-0006"); // LINE_ID_DOES_NOT_EXIST
            return;
        }
        fail("Auftrag mit alter lineId wurde gecancelt. getOrderParamerters() darf nicht funktionieren.");
    }

    public void modifyPortFuerMorgenAufModifyPortFuerHeute() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        state
                .product(fttb25())
                .reservePort(today())
                .product(fttb50())
                .modifyPort()
                .executionDate(tomorrow())
                .product(fttb100())
                .modifyPort();
    }


    @DataProvider
    public Object[][] modifyPortZuHeuteFunktioniertDataProvider() {
        return new Object[][] {
                { today(), today(), today() },
                { today().minusDays(5), today(), today() },
                { today(), today(), null }
        };
    }

    @Test(dataProvider = "modifyPortZuHeuteFunktioniertDataProvider", enabled = false)
    public void modifyPortZuHeuteFunktioniert(LocalDate reservePortDate, LocalDate modifyDate1,
            LocalDate modifyDate2) throws Exception {
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

        EkpData ekpBeforeModify = ekpDataBuilderProvider
                .get()
                .withCvlans(Lists.newArrayList(vlanVoip))
                .build();
        EkpData ekpAfterModify = ekpDataBuilderProvider.get()
                .withCvlans(Lists.newArrayList(vlanHsi))
                .build();

        state
                .executionDate(today())
                .ekpData(ekpBeforeModify)
                .product(Produkt.fttb50());

        if (reservePortDate.isBefore(today())) {
            state.reservePortInPast(reservePortDate);
        }
        else {
            state.reservePort(reservePortDate);
        }

        state
                .product(Produkt.fttb16())
                .executionDate(modifyDate1)
                .modifyPort();

        if (modifyDate2 != null) {
            state
                    .ekpData(ekpAfterModify)
                    .product(Produkt.fttb25().withTP())
                    .executionDate(modifyDate2)
                    .modifyPort()
                    .assertThatA2TlExists(TechLeistung.TYP_WHOLESALE, TechLeistung.ExterneLeistung.TP.leistungNo,
                            today(),
                            1)
                    .getOrderParameters();

            assertThat(state.getOrderParametersResponse.getDownStream(), equalTo(25000));
            assertThat(state.getOrderParametersResponse.getUpStream(), equalTo(5000));
            assertThat(state.getOrderParametersResponse.getVlans(), hasSize(1));
            assertThat(state.getOrderParametersResponse.getVlans().get(0).getService(),
                    equalTo(CvlanServiceTyp.HSI.toString()));
            assertThat(state.ekpFrameContractAssignedAt(modifyDate2), equalTo(ekpAfterModify.ekpFrameContract));
        }
        else {
            state.getOrderParameters();
            assertThat(state.getOrderParametersResponse.getDownStream(), equalTo(16000));
            assertThat(state.getOrderParametersResponse.getUpStream(), equalTo(1000));
            assertThat(state.getOrderParametersResponse.getVlans(), hasSize(1));
            assertThat(state.getOrderParametersResponse.getVlans().get(0).getService(),
                    equalTo(CvlanServiceTyp.VOIP.toString()));
            assertThat(state.ekpFrameContractAssignedAt(modifyDate1), equalTo(ekpBeforeModify.ekpFrameContract));
        }
    }

    public void modifyPortZuHeuteMitPortwechselFunktioniert() throws Exception {
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

        EkpData ekpBeforeModify = ekpDataBuilderProvider.get()
                .withCvlans(Lists.newArrayList(vlanVoip))
                .build();
        EkpData ekpAfterModify = ekpDataBuilderProvider.get()
                .withCvlans(Lists.newArrayList(vlanHsi))
                .build();

        state
                .executionDate(today())
                .product(Produkt.fttb50())
                .ekpData(ekpBeforeModify)
                .reservePort();

        final String lineIdBeforePortChanged = state.lineId;

        state
                .changeOfPortAllowed(true)
                .ekpData(ekpAfterModify)
                .product(Produkt.fttb100().withTP())
                .modifyPort();

        final String lineIdAfterPortChanged = state.lineId;

        assertThat("Portwechsel nicht hat stattgefunden", lineIdBeforePortChanged, not(equalTo(lineIdAfterPortChanged)));

        state
                .assertThatA2TlExists(TechLeistung.TYP_WHOLESALE, TechLeistung.ExterneLeistung.TP.leistungNo, today(),
                        1)
                .assertThatA2TlExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_100000.leistungNo,
                        today(), 1)
                .assertThatA2TlNotExists(TechLeistung.TYP_DOWNSTREAM,
                        TechLeistung.ExterneLeistung.DOWN_50000.leistungNo, today())
                .assertThatDslamProfileValidFromMatches(today(), hasDownstream(100000))
                .assertThatDslamProfileValidFromMatches(today(), hasUpstream(10000))
                .getOrderParameters()
                        // bezieht sich auf den Auftrag der durch reservePort erzeugt wurde!
                .assertAuftragDatenStatus(AuftragStatus.STORNO)
                .assertThatPortIsMarked4Release();

        assertThat(state.getOrderParametersResponse.getUpStream(), equalTo(10000));
        assertThat(state.getOrderParametersResponse.getDownStream(), equalTo(100000));

        assertOrderParamatersContainsVlanOfService(state, today(), state.lineId, CvlanServiceTyp.HSI);
        assertOrderParamatersContainsNotVlanOfService(state, today(), state.lineId, CvlanServiceTyp.VOIP);

        assertThat(state.auftrag2EkpFrameContractAssignedAt(today()),
                hasEkpFrameContract(ekpAfterModify.ekpFrameContract));
    }

    @DataProvider
    public Object[][] modifyPortZuHeuteMitPortwechselAufModifyPortZuHeuteMitPortwechselFunktioniertDataProvider() {
        return new Object[][] {
                { true },
                { false }
        };
    }

    @Test(dataProvider = "modifyPortZuHeuteMitPortwechselAufModifyPortZuHeuteMitPortwechselFunktioniertDataProvider", enabled = false)
    public void modifyPortZuHeuteMitPortwechselAufModifyPortZuHeuteMitPortwechselFunktioniert(
            final boolean reservationDateInPast) throws Exception {
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
        CVlan vlanHsiPlus = new CVlanBuilder()
                .withTyp(CvlanServiceTyp.VOD)
                .withProtocoll(CVlanProtocoll.IPoE)
                .withValue(Integer.valueOf(1)).withPbitLimit(Integer.valueOf(3)).setPersist(false)
                .build();

        EkpData ekpBeforeModify = ekpDataBuilderProvider.get()
                .withCvlans(Lists.newArrayList(vlanVoip))
                .build();
        EkpData ekpAfterModify1 = ekpDataBuilderProvider.get()
                .withCvlans(Lists.newArrayList(vlanHsi))
                .build();
        EkpData ekpAfterModify2 = ekpDataBuilderProvider.get()
                .withCvlans(Lists.newArrayList(vlanHsiPlus))
                .build();

        state
                .product(Produkt.fttb50())
                .ekpData(ekpBeforeModify);

        if (reservationDateInPast) {
            state.reservePortInPast(today().minusDays(1));
        }
        else {
            state.reservePort(today());
        }

        final String lineIdBeforePortChanged = state.lineId;

        state
                .executionDate(today())
                .changeOfPortAllowed(true)
                .ekpData(ekpAfterModify1)
                .product(Produkt.fttb100().withTP())
                .modifyPort();

        state
                .ekpData(ekpAfterModify2)
                .product(Produkt.fttb25().withTP())
                .modifyPort();

        final String lineIdAfterPortChanged = state.lineId;

        assertThat("Portwechsel nicht hat stattgefunden", lineIdBeforePortChanged, not(equalTo(lineIdAfterPortChanged)));

        state
                .assertThatA2TlExists(TechLeistung.TYP_WHOLESALE, TechLeistung.ExterneLeistung.TP.leistungNo, today(),
                        1)
                .assertThatA2TlExists(TechLeistung.TYP_DOWNSTREAM, TechLeistung.ExterneLeistung.DOWN_25000.leistungNo,
                        today(), 1)
                .assertThatA2TlNotExists(TechLeistung.TYP_DOWNSTREAM,
                        TechLeistung.ExterneLeistung.DOWN_50000.leistungNo, today())
                .assertThatA2TlNotExists(TechLeistung.TYP_DOWNSTREAM,
                        TechLeistung.ExterneLeistung.DOWN_100000.leistungNo, today())
                .assertThatDslamProfileValidFromMatches(today(), hasDownstream(25000))
                .assertThatDslamProfileValidFromMatches(today(), hasUpstream(5000))
                .getOrderParameters();

        assertThat(state.getOrderParametersResponse.getUpStream(), equalTo(5000));
        assertThat(state.getOrderParametersResponse.getDownStream(), equalTo(25000));

        assertOrderParamatersContainsVlanOfService(state, today(), state.lineId, CvlanServiceTyp.VOD);
        assertOrderParamatersContainsNotVlanOfService(state, today(), state.lineId, CvlanServiceTyp.HSI);
        assertOrderParamatersContainsNotVlanOfService(state, today(), state.lineId, CvlanServiceTyp.VOIP);

        assertThat(state.auftrag2EkpFrameContractAssignedAt(today()),
                hasEkpFrameContract(ekpAfterModify2.ekpFrameContract));
    }
}


