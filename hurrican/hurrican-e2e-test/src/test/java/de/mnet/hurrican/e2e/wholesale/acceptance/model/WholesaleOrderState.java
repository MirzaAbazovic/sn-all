/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.03.2012 15:44:46
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.model;

import static com.google.common.base.Predicates.*;
import static de.augustakom.hurrican.model.cc.Auftrag2TechLeistung.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.Assert;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp.CVlanType;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.mnet.hurrican.e2e.common.EkpDataBuilder.EkpData;
import de.mnet.hurrican.e2e.common.StandortDataBuilder.StandortData;
import de.mnet.hurrican.e2e.wholesale.acceptance.matcher.DslamProfileDownstreamMatcher;
import de.mnet.hurrican.e2e.wholesale.acceptance.matcher.DslamProfileUpstreamMatcher;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeReason;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeVdslProfileRequest;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsRequest;
import de.mnet.hurrican.wholesale.fault.clearance.GetAvailablePortsResponse;
import de.mnet.hurrican.wholesale.fault.clearance.GetVdslProfilesRequest;
import de.mnet.hurrican.wholesale.fault.clearance.GetVdslProfilesResponse;
import de.mnet.hurrican.wholesale.fault.clearance.Port;
import de.mnet.hurrican.wholesale.workflow.CancelModifyPortResponse;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersRequest;
import de.mnet.hurrican.wholesale.workflow.GetOrderParametersResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateRequest;
import de.mnet.hurrican.wholesale.workflow.ModifyPortReservationDateResponse;
import de.mnet.hurrican.wholesale.workflow.ModifyPortResponse;
import de.mnet.hurrican.wholesale.workflow.ReleasePortRequest;
import de.mnet.hurrican.wholesale.workflow.ReleasePortResponse;
import de.mnet.hurrican.wholesale.workflow.ReservePortResponse;
import de.mnet.hurrican.wholesale.workflow.VLAN;

public class WholesaleOrderState {

    public Produkt produkt = Produkt.fttb50().withTP();
    public LocalDate executionDate = LocalDate.now().plusDays(2);
    public LocalDate desiredExecutionDate = LocalDate.now().plusDays(2);
    public StandortData standortData;
    public EkpData ekpData;
    public Long createdAuftragId;
    public GetOrderParametersResponse getOrderParametersResponse;
    public GetAvailablePortsResponse getAvailablePortsResponse;
    public GetVdslProfilesResponse getVdslProfilesResponse;
    public String lineId;
    public boolean isPortChanged = false;
    private boolean changeOfPortAllowed;

    @Resource(name = "wholesaleWebServiceTemplate")
    private WebServiceTemplate webServiceTemplate;
    @Autowired
    private CCAuftragService auftragService;
    @Autowired
    private EndstellenService endstellenService;
    @Autowired
    private CCLeistungsService leistungService;
    @Autowired
    private RangierungsService rangierungsService;
    @Autowired
    private DSLAMService dslamService;
    @Autowired
    private CCLeistungsService leistungsService;
    @Autowired
    private EkpFrameContractService ekpFrameContractService;
    @Autowired
    private VlanService vlanService;
    @Autowired
    private BAService baService;

    public WholesaleOrderState product(Produkt produkt) {
        this.produkt = produkt;
        return this;
    }

    public WholesaleOrderState executionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
        return this;
    }

    public WholesaleOrderState standortData(StandortData standortData) {
        this.standortData = standortData;
        return this;
    }

    public WholesaleOrderState ekpData(EkpData ekpData) {
        this.ekpData = ekpData;
        return this;
    }

    public WholesaleOrderState lineId(String lineId) {
        this.lineId = lineId;
        return this;
    }

    public WholesaleOrderState changeOfPortAllowed(boolean changeOfPortAllowed) {
        this.changeOfPortAllowed = changeOfPortAllowed;
        return this;
    }

    public WholesaleOrderState reservePort() throws Exception {
        return reservePort(executionDate);
    }

    public WholesaleOrderState reservePort(LocalDate desiredExecutionDate) throws Exception {
        this.desiredExecutionDate = desiredExecutionDate;
        ReservePortReq request = new ReservePortReq()
                .ekpId(ekpData.ekpFrameContract.getEkpId())
                .frameContractId(ekpData.ekpFrameContract.getFrameContractId())
                .produkt(produkt)
                .desiredExecutionDate(desiredExecutionDate)
                .geoId(geoId());
        ReservePortResponse response = (ReservePortResponse) webServiceTemplate
                .marshalSendAndReceive(request.toXmlBean());
        executionDate = response.getExecutionDate();
        lineId = response.getLineId();
        Auftrag auftrag = getAuftrag(lineId, AuftragStatus.TECHNISCHE_REALISIERUNG);
        createdAuftragId = (auftrag != null) ? auftrag.getAuftragId() : null;
        finishVerlauf4Abteilungen(auftrag);
        return this;
    }

    private void finishVerlauf4Abteilungen(Auftrag auftrag) throws Exception {
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftrag.getAuftragId());
        List<Verlauf> verlaeufe = baService.findAllActVerlauf4Auftrag(auftrag.getAuftragId());
        if (verlaeufe == null || auftragDaten == null) {
            return;
        }
        for (Verlauf verlauf : verlaeufe) {
            baService.dispoBAVerteilenAuto(verlauf.getId(), null);
            finishVerlauf(verlauf);
        }
    }

    private void finishVerlauf(final Verlauf verlauf) throws Exception {
        List<VerlaufAbteilung> abteilungen = baService.findVerlaufAbteilungen(verlauf.getId());
        if (abteilungen == null) {
            return;
        }
        for (VerlaufAbteilung abteilung : abteilungen) {
            baService.finishVerlauf4Abteilung(abteilung, "hurrican", null, verlauf.getRealisierungstermin(), null, null, false, null);
        }
    }

    public WholesaleOrderState reservePortInPast() throws Exception {
        if (!executionDate.isBefore(LocalDate.now())) {
            executionDate = LocalDate.now().minusDays(60);
        }
        return reservePortInPast(executionDate);
    }

    public WholesaleOrderState reservePortInPast(LocalDate desiredExecutionDate) throws Exception {
        if (!desiredExecutionDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Desired execution Date " + desiredExecutionDate + " is not in past!");
        }
        reservePort(LocalDate.now().plusDays(2));

        Auftrag auftrag = getAuftrag(lineId, AuftragStatus.TECHNISCHE_REALISIERUNG);

        // Datumswerte manuell in Vergangenheit setzen
        AuftragDaten auftragDaten = getAuftragDaten(auftrag.getAuftragId());
        auftragDaten.setInbetriebnahme(Date.from(desiredExecutionDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        auftragService.saveAuftragDaten(auftragDaten, false);
        leistungService.modifyAuftrag2TechLeistungen(auftrag.getAuftragId(), executionDate, desiredExecutionDate, null);
        dslamService.modifyDslamProfiles4Auftrag(auftrag.getAuftragId(), executionDate, desiredExecutionDate, null);
        vlanService.moveEqVlans4Auftrag(auftrag.getAuftragId(), executionDate, desiredExecutionDate, null);
        modifyAuftrag2EkpFrameContract(desiredExecutionDate, auftrag);

        this.desiredExecutionDate = desiredExecutionDate;
        executionDate = desiredExecutionDate;
        return this;
    }

    private void modifyAuftrag2EkpFrameContract(LocalDate desiredExecutionDate, Auftrag auftrag) {
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = ekpFrameContractService.findAuftrag2EkpFrameContract(
                auftrag.getAuftragId(), executionDate);
        auftrag2EkpFrameContract.setAssignedFrom(desiredExecutionDate);
        auftrag2EkpFrameContract = ekpFrameContractService.saveAuftrag2EkpFrameContract(auftrag2EkpFrameContract);
        this.desiredExecutionDate = desiredExecutionDate;
        executionDate = desiredExecutionDate;
    }


    public WholesaleOrderState releasePort() {
        ReleasePortRequest request = new ReleasePortRequest();
        request.setLineId(lineId);

        ReleasePortResponse response = (ReleasePortResponse) webServiceTemplate.marshalSendAndReceive(request);
        executionDate = response.getExecutionDate();

        return this;
    }


    public WholesaleOrderState modifyPortReservationDate() {
        return modifyPortReservationDate(executionDate);
    }

    public WholesaleOrderState modifyPortReservationDate(LocalDate desiredExecutionDate) {
        this.desiredExecutionDate = desiredExecutionDate;
        ModifyPortReservationDateRequest request = new ModifyPortReservationDateRequest();
        request.setLineId(lineId);
        request.setDesiredExecutionDate(desiredExecutionDate);

        ModifyPortReservationDateResponse response =
                (ModifyPortReservationDateResponse) webServiceTemplate.marshalSendAndReceive(request);
        executionDate = response.getExecutionDate();
        return this;
    }

    public WholesaleOrderState modifyPort() {
        this.desiredExecutionDate = executionDate;
        ModifyPortReq modifyPortReq = new ModifyPortReq()
                .changeOfPortAllowed(changeOfPortAllowed)
                .desiredExecutionDate(desiredExecutionDate)
                .ekpId(ekpData.ekpFrameContract.getEkpId())
                .ekpFrameId(ekpData.ekpFrameContract.getFrameContractId())
                .lineId(lineId)
                .produkt(produkt);
        ModifyPortResponse response = (ModifyPortResponse) webServiceTemplate.marshalSendAndReceive(modifyPortReq
                .toXmlBean());
        lineId = response.getLineId();
        isPortChanged = response.isPortChanged();
        executionDate = response.getExecutionDate();
        return this;
    }

    public WholesaleOrderState modifyPortInPast(LocalDate desiredExecutionDate) throws FindException, StoreException {
        executionDate = LocalDate.now().plusDays(2);
        modifyPort();

        Auftrag auftrag = getAuftragForLineId(lineId);

        // Datumswerte manuell in Vergangenheit setzen
        leistungService.modifyAuftrag2TechLeistungen(auftrag.getAuftragId(), executionDate, desiredExecutionDate, null);
        dslamService.modifyDslamProfiles4Auftrag(auftrag.getAuftragId(), executionDate, desiredExecutionDate, null);
        auftragService.modifyActiveAktion(auftrag.getAuftragId(), AuftragAktion.AktionType.MODIFY_PORT,
                desiredExecutionDate);
        modifyAuftrag2EkpFrameContract(desiredExecutionDate, auftrag);
        return this;
    }


    public WholesaleOrderState changePort(Port toUse, ChangeReason changeReason) {
        ChangePortReq changePortReq = new ChangePortReq()
                .lineId(lineId)
                .newPortId(toUse.getPortId())
                .changeReasonId(changeReason.getId());
        webServiceTemplate.marshalSendAndReceive(changePortReq.toXmlBean());
        return this;
    }


    public WholesaleOrderState modifyInbetriebnahme(Date inbetriebnahme) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten(createdAuftragId);
        auftragDaten.setInbetriebnahme(inbetriebnahme);

        auftragService.saveAuftragDaten(auftragDaten, false);
        return this;
    }

    public WholesaleOrderState modifyProdukt(Long produktId) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten(createdAuftragId);
        auftragDaten.setProdId(produktId);

        auftragService.saveAuftragDaten(auftragDaten, false);
        return this;
    }


    public WholesaleOrderState assertDesiredDateIsExecutionDate() {
        assertThat(executionDate, equalTo(desiredExecutionDate));
        return this;
    }

    public AuftragDaten getAuftragDaten() throws Exception {
        Auftrag auftrag = getAuftrag();
        return getAuftragDaten(auftrag.getAuftragId());
    }

    public Auftrag getAuftrag() throws Exception {
        Auftrag auftrag = getAuftragForLineId(lineId);
        if (auftrag == null) {
            auftrag = getAuftrag(lineId, AuftragStatus.TECHNISCHE_REALISIERUNG);
        }
        return auftrag;
    }

    public Auftrag getAuftrag(Long auftragId) throws FindException {
        Auftrag auftrag = auftragService.findAuftragById(auftragId);
        return auftrag;
    }

    public Auftrag getAuftrag(String lineId, Long auftragStatus) throws FindException {
        return auftragService.findOrderByLineIdAndAuftragStatus(lineId, auftragStatus);
    }

    private Auftrag getAuftragForLineId(String lineId) throws FindException {
        return auftragService.findActiveOrderByLineId(lineId, LocalDate.now());
    }

    private AuftragDaten getAuftragDaten(Long auftragId) throws FindException {
        return auftragService.findAuftragDatenByAuftragId(auftragId);
    }

    public WholesaleTechLeistungen getTechLeistungen() throws Exception {
        Auftrag auftrag = getAuftrag();
        List<TechLeistung> techLeistungen = leistungsService.findTechLeistungen4Auftrag(auftrag.getAuftragId(), null,
                true);
        WholesaleTechLeistungen techLeistungenForWholesale = new WholesaleTechLeistungen(techLeistungen);
        return techLeistungenForWholesale;
    }

    public Rangierung getRangierung() throws Exception {
        Auftrag auftrag = getAuftrag();
        Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(auftrag.getAuftragId(),
                Endstelle.ENDSTELLEN_TYP_B);
        assertNotNull(endstelleB, String.format("Endstelle B zum Auftrag %s nicht gefunden", auftrag.getAuftragId()));
        assertNotNull(endstelleB.getRangierId(), "Endstelle B hat keine Rangierung zugeordnet!");
        Rangierung assignedRangierung = rangierungsService.findRangierung(endstelleB.getRangierId());
        return assignedRangierung;
    }

    public WholesaleOrderState assertAuftragGueltigVon(LocalDate gueltigVon) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten();
        assertThat(auftragDaten.getStatusId(), equalTo(AuftragStatus.IN_BETRIEB));
        assertThat(auftragDaten.getInbetriebnahme(), equalTo(Date.from(gueltigVon.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        return this;
    }

    public WholesaleOrderState assertAuftragDatenStatus(Long expectedStatus) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten(createdAuftragId);
        assertThat(auftragDaten.getStatusId(), equalTo(expectedStatus));
        return this;
    }

    public WholesaleOrderState assertAuftragDatenKuendigung(Date expectedKuendigung) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten(createdAuftragId);
        assertThat(auftragDaten.getKuendigung(), equalTo(expectedKuendigung));
        return this;
    }

    public WholesaleOrderState assertThatPortIsReleased() throws Exception {
        Auftrag auftrag = getAuftrag(createdAuftragId);
        Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(auftrag.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        assertNotNull(endstelleB);
        assertNotNull(endstelleB.getRangierId());  // RangierId has to be defined!
        Rangierung rangierung = rangierungsService.findRangierung(endstelleB.getRangierId());
        assertNotNull(rangierung);
        assertNull(rangierung.getEsId());
        assertNull(rangierung.getFreigabeAb());
        return this;
    }

    public WholesaleOrderState assertThatPortIsMarked4Release() throws Exception {
        Auftrag auftrag = getAuftrag(createdAuftragId);
        Endstelle endstelleB = endstellenService.findEndstelle4Auftrag(auftrag.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);
        assertNotNull(endstelleB);
        assertNotNull(endstelleB.getRangierId());  // RangierId has to be defined!
        Rangierung rangierung = rangierungsService.findRangierung(endstelleB.getRangierId());
        assertNotNull(rangierung);
        assertThat(rangierung.getEsId(), equalTo(Rangierung.RANGIERUNG_NOT_ACTIVE));
        assertThat(rangierung.getFreigabeAb(), equalTo(Date.from(executionDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        return this;
    }

    public WholesaleOrderState assertThatA2TlExists(final String lsTyp, final Long extLeistungNo,
            final LocalDate validAt, int expectedSize) throws Exception {
        Collection<TechLeistung> techLeistungenActive = findAndFilterTechLs(lsTyp, extLeistungNo, validAt);
        assertThat(techLeistungenActive.size(), equalTo(expectedSize));
        return this;
    }

    private Collection<TechLeistung> findAndFilterTechLs(final String lsTyp, final Long extLeistungNo,
            final LocalDate validAt) throws Exception {
        final Long auftragId = getAuftrag().getAuftragId();
        List<TechLeistung> leistungen = leistungService.findTechLeistungen4Auftrag(auftragId,
                lsTyp, Date.from(validAt.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Collection<TechLeistung> techLeistungenActive = Collections2.filter(leistungen,
                new Predicate<TechLeistung>() {
                    @Override
                    public boolean apply(TechLeistung input) {
                        if (input.getExternLeistungNo() != null) {
                            return input.getExternLeistungNo().equals(extLeistungNo);
                        }
                        else {
                            return false;
                        }
                    }
                }
        );
        return techLeistungenActive;
    }

    public WholesaleOrderState assertThatA2TlNotExists(final String lsTyp, final Long extLeistungNo,
            final LocalDate validAt) throws Exception {
        return assertThatA2TlExists(lsTyp, extLeistungNo, validAt, 0);
    }

    public static Predicate<Auftrag2TechLeistung> a2tlValidFrom(LocalDate validFrom) {
        return dateGetterToPredicate(validFrom, GET_AKTIV_VON);
    }

    public static Predicate<Auftrag2TechLeistung> a2tlValidTo(LocalDate validFrom) {
        return dateGetterToPredicate(validFrom, GET_AKTIV_BIS);
    }

    public static Predicate<Auftrag2TechLeistung> a2tlValidFromTo(LocalDate validFrom, LocalDate validTo) {
        return Predicates.and(a2tlValidFrom(validFrom), a2tlValidTo(validTo));
    }

    public static <T> Predicate<T> dateGetterToPredicate(LocalDate validFrom, Function<T, Date> dateGetter) {
        return compose(Predicates.equalTo(validFrom),
                dateGetterToLocalDateGetter(dateGetter));
    }

    public static <T> Function<T, LocalDate> dateGetterToLocalDateGetter(Function<T, Date> input) {
        return Functions.compose(DATE_2_LOCALDATE, input);
    }

    public static Function<Date, LocalDate> DATE_2_LOCALDATE = new Function<Date, LocalDate>() {

        @Override
        public LocalDate apply(Date input) {
            return input == null ? null : Instant.ofEpochMilli(input.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
    };

    public List<Auftrag2TechLeistung> a2tls() throws Exception {
        return a2tls(Predicates.<Auftrag2TechLeistung>alwaysTrue());
    }

    public List<Auftrag2TechLeistung> a2tls(LocalDate validFrom, LocalDate validTo) throws Exception {
        return a2tls(a2tlValidFromTo(validFrom, validTo));
    }

    public WholesaleOrderState assertThatA2TlHasAuftragAktionsId(final Long extLeistungNo, LocalDate validFrom, LocalDate validTo,
            final boolean hasAktionsIdAdd, final boolean hasAktionsIdRemove) throws Exception {
        List<Auftrag2TechLeistung> a2tls = a2tls(validFrom, validTo);
        for (Auftrag2TechLeistung a2tl : a2tls) {
            TechLeistung techLeistung = leistungsService.findTechLeistung(a2tl.getTechLeistungId());
            if (NumberTools.equal(techLeistung.getExternLeistungNo(), extLeistungNo)) {
                if (hasAktionsIdAdd) {
                    assertNotNull(a2tl.getAuftragAktionsIdAdd());
                }
                if (hasAktionsIdRemove) {
                    assertNotNull(a2tl.getAuftragAktionsIdRemove());
                }
            }
        }
        return this;
    }

    public List<Auftrag2TechLeistung> a2tls(Predicate<Auftrag2TechLeistung> a2tlPredicate) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten();
        List<Auftrag2TechLeistung> a2tlResult = leistungService.findAuftrag2TechLeistungen(auftragDaten.getAuftragId(),
                null, false);
        List<Auftrag2TechLeistung> filtered = Lists.newArrayList(Iterables.filter(a2tlResult, a2tlPredicate));
        return filtered;
    }

    public List<Auftrag2DSLAMProfile> dslamProfiles() throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten();
        return dslamService.findAuftrag2DSLAMProfiles(auftragDaten.getAuftragId());
    }

    public Auftrag2DSLAMProfile dslamProfile() throws Exception {
        return Iterables.getOnlyElement(dslamProfiles());
    }

    public WholesaleOrderState assertDslamProfileValidFrom(LocalDate validFrom) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten();
        // DSLAM-Profil Zuordnung pruefen:
        List<Auftrag2DSLAMProfile> assignedProfiles = dslamService.findAuftrag2DSLAMProfiles(auftragDaten
                .getAuftragId());
        assertThat(assignedProfiles.size(), equalTo(Integer.valueOf(1)));
        assertThat(assignedProfiles.get(0).getGueltigVon(), equalTo(Date.from(validFrom.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        return this;
    }

    public static DslamProfileDownstreamMatcher hasDownstream(int downstream) {
        return new DslamProfileDownstreamMatcher(downstream);
    }

    public static DslamProfileUpstreamMatcher hasUpstream(int upstream) {
        return new DslamProfileUpstreamMatcher(upstream);
    }

    public DSLAMProfile dslamProfileValidAt(LocalDate validAt) throws Exception {
        final Long auftragId = getAuftragForLineId(lineId).getAuftragId();

        // DSLAM-Profil Zuordnung pruefen:
        DSLAMProfile assignedProfile = dslamService
                .findDSLAMProfile4Auftrag(
                        auftragId,
                        Date.from(validAt.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        false);
        return assignedProfile;
    }

    public WholesaleOrderState assertThatDslamProfileValidFromMatches(LocalDate validFrom, Matcher<DSLAMProfile> matcher)
            throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten();
        // DSLAM-Profil Zuordnung pruefen:
        DSLAMProfile assignedProfile = dslamService
                .findDSLAMProfile4Auftrag(
                        auftragDaten.getAuftragId(),
                        Date.from(validFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        false);
        assertThat(assignedProfile, matcher);
        return this;
    }

    public WholesaleOrderState cancelModifyPort() {
        this.desiredExecutionDate = executionDate;
        CancelModifyPortReq cancelModifyPortRequest = new CancelModifyPortReq();
        cancelModifyPortRequest.lineId(lineId);
        CancelModifyPortResponse response = (CancelModifyPortResponse) webServiceTemplate
                .marshalSendAndReceive(cancelModifyPortRequest
                        .toXmlBean());
        this.lineId = response.getPreviousLineId();
        return this;
    }

    public WholesaleOrderState getOrderParameters() {
        return getOrderParameters(executionDate);
    }

    public WholesaleOrderState getOrderParameters(final LocalDate executionDate) {
        GetOrderParametersRequest request = new GetOrderParametersRequest();
        request.setExecutionDate(executionDate);
        request.setLineId(this.lineId);
        this.getOrderParametersResponse = (GetOrderParametersResponse) webServiceTemplate
                .marshalSendAndReceive(request);
        return this;
    }

    public Long geoId() {
        return standortData.geoId.getId();
    }

    public Auftrag2EkpFrameContract auftrag2EkpFrameContractAssignedAt(LocalDate when) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten();
        Auftrag2EkpFrameContract auftrag2EkpFrameContractAssigned = this.ekpFrameContractService
                .findAuftrag2EkpFrameContract(auftragDaten.getAuftragId(), when);
        return auftrag2EkpFrameContractAssigned;
    }

    public EkpFrameContract ekpFrameContractAssignedAt(LocalDate when) throws Exception {
        AuftragDaten auftragDaten = getAuftragDaten();
        Auftrag2EkpFrameContract auftrag2EkpFrameContractAssigned = this.ekpFrameContractService
                .findAuftrag2EkpFrameContract(auftragDaten.getAuftragId(), when);
        EkpFrameContract ekpFrameContractAssigned = (auftrag2EkpFrameContractAssigned != null) ? auftrag2EkpFrameContractAssigned
                .getEkpFrameContract() : null;
        return ekpFrameContractAssigned;
    }


    public void assertVlans(List<VLAN> vlans, final List<CVlan> cvlans) {
        assertThat(vlans, Matchers.hasSize(cvlans.size()));
        HashMap<CvlanServiceTyp, CVlan> cvlanMap = new HashMap<CvlanServiceTyp, CVlan>();
        for (CVlan eqVlan : cvlans) {
            cvlanMap.put(eqVlan.getTyp(), eqVlan);
        }
        for (VLAN vlan : vlans) {
            CVlan cvlan = cvlanMap.remove(CvlanServiceTyp.valueOf(vlan.getService()));
            assertNotNull(cvlan);
            Assert.assertEquals(Integer.valueOf(vlan.getCvlan()), cvlan.getValue());
            Assert.assertEquals(CVlanType.valueOf(vlan.getType()), cvlan.getTyp().getType());
        }
    }


    public WholesaleOrderState getAvailablePorts() {
        GetAvailablePortsRequest request = new GetAvailablePortsRequest();
        request.setLineId(this.lineId);

        this.getAvailablePortsResponse = (GetAvailablePortsResponse) webServiceTemplate.marshalSendAndReceive(request);
        return this;
    }

    public WholesaleOrderState getVdslProfiles() {
        GetVdslProfilesRequest request = new GetVdslProfilesRequest();
        request.setLineId(lineId);

        getVdslProfilesResponse = (GetVdslProfilesResponse) webServiceTemplate.marshalSendAndReceive(request);
        return this;
    }

    public WholesaleOrderState changeVdslProfile(long newProfileId, long changeReasonId, LocalDate validFrom,
            String username, String comment) {
        ChangeVdslProfileRequest request = new ChangeVdslProfileRequest();
        request.setChangeReasonId(changeReasonId);
        request.setComment(comment);
        request.setLineId(lineId);
        request.setNewProfileId(newProfileId);
        request.setUsername(username);
        request.setValidFrom(validFrom);
        webServiceTemplate.marshalSendAndReceive(request);
        return this;
    }
}


