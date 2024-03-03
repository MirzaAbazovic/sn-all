/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2012 12:01:47
 */
package de.augustakom.hurrican.service.wholesale.impl;

import static de.augustakom.hurrican.model.cc.CCAddress.*;
import static de.augustakom.hurrican.model.wholesale.WholesaleContactPerson.*;
import static de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode.*;
import static de.mnet.common.tools.DateConverterUtils.*;
import static java.lang.String.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.lang.String;
import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.exceptions.LanguageException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.exceptions.HurricanConcurrencyException;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragAktion.AktionType;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleCancelModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleContactPerson;
import de.augustakom.hurrican.model.wholesale.WholesaleEkpFrameContract;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortReservationDateResponse;
import de.augustakom.hurrican.model.wholesale.WholesaleModifyPortResponse;
import de.augustakom.hurrican.model.wholesale.WholesalePbit;
import de.augustakom.hurrican.model.wholesale.WholesaleProduct;
import de.augustakom.hurrican.model.wholesale.WholesaleProductGroup;
import de.augustakom.hurrican.model.wholesale.WholesaleProductName;
import de.augustakom.hurrican.model.wholesale.WholesaleReleasePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortRequest;
import de.augustakom.hurrican.model.wholesale.WholesaleReservePortResponse;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCKundenService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;
import de.augustakom.hurrican.service.cc.ExterneAuftragsLeistungen;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.augustakom.hurrican.service.wholesale.Auftrag2EkpFrameContractNotFoundException;
import de.augustakom.hurrican.service.wholesale.ExecutionDateInPast;
import de.augustakom.hurrican.service.wholesale.ModifyPortReservationDateToEarlierDateException;
import de.augustakom.hurrican.service.wholesale.ProductGroupNotSupportedException;
import de.augustakom.hurrican.service.wholesale.TechnischNichtMoeglichException;
import de.augustakom.hurrican.service.wholesale.WholesaleException;
import de.augustakom.hurrican.service.wholesale.WholesaleException.WholesaleFehlerCode;
import de.augustakom.hurrican.service.wholesale.WholesaleService;
import de.augustakom.hurrican.service.wholesale.WholesaleServiceException;
import de.augustakom.hurrican.service.wholesale.WholesaleTechnicalException;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.common.tools.DateConverterUtils;

@ObjectsAreNonnullByDefault
@CcTxRequired
public class WholesaleServiceImpl extends AbstractWholesaleService implements WholesaleService {

    private static final Logger LOG = Logger.getLogger(WholesaleServiceImpl.class);

    private static final String WHOLESALE_USER = "wholesale";

    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService")
    private EkpFrameContractService ekpFrameContractService;
    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.VlanService")
    private VlanService vlanService;
    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;
    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    private AKUserService userService;
    @Resource(name = "de.augustakom.hurrican.dao.cc.HardwareDAO")
    private HardwareDAO hardwareDAO;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCKundenService")
    private CCKundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AnsprechpartnerService")
    private AnsprechpartnerService ansprechpartnerService;

    @Override
    public WholesaleReservePortResponse reservePort(WholesaleReservePortRequest request) {
        if (request.getDesiredExecutionDate().isBefore(LocalDate.now())) {
            throw new ExecutionDateInPast(request.getDesiredExecutionDate());
        }
        final Date execDate = asWorkingDay(request);

        try {
            final CreateHurricanOrderResult result = createHurricanOrder(request, execDate, null);
            final Optional<CCAddress> adresseEndstelleB =
                    createAnsprechpartner(request.getContactPersons(), request.getGeoId(), request.getLageTaeOnt(), result.auftrag.getAuftragId());
            setzeEndstelleBAdresse(result, adresseEndstelleB);
            createAuftragWholesale(request, result.auftrag.getAuftragId());
            if (WholesaleProductGroup.FTTH_BSA.equals(request.getProduct().getGroup())) {
                return reserveFtthPortResponse(execDate, result);
            }
            createVerlauf(request.getSessionId(), execDate, result.auftrag.getAuftragId());

            return reserveFttbPortResponse(execDate, result);
        }
        catch (FindException | StoreException | ValidationException e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(ERROR_RESERVING_PORT,
                    format("Error creating Hurrican order: %s", e.getMessage()), null, e);
        }
    }

    private void createAuftragWholesale(WholesaleReservePortRequest request, @CheckForNull Long auftragId) throws StoreException {

        final AuftragWholesale auftragWholesale = new AuftragWholesale();
        auftragWholesale.setAuftragId(auftragId);
        auftragWholesale.setWholesaleAuftragsId(request.getOrderId());
        auftragWholesale.setExecutionDate(request.getDesiredExecutionDate());
        auftragWholesale.setExecutionTimeBegin(request.getZeitFensterAnfang());
        auftragWholesale.setExecutionTimeEnd(request.getZeitfensterEnde());
        auftragService.saveAuftragWholesale(auftragWholesale);
    }

    private Endstelle setzeEndstelleBAdresse(CreateHurricanOrderResult result, Optional<CCAddress> adresseEndstelleB) throws StoreException {
        adresseEndstelleB.ifPresent(addr ->
                result.endstelleB.setAddressId(addr.getId())
        );
        return endstellenService.saveEndstelle(result.endstelleB);
    }

    private Optional<CCAddress> createAnsprechpartner(@Nullable List<WholesaleContactPerson> contactPersons, long geoId, String lageTaeOnt, @CheckForNull Long auftragId)
            throws StoreException, FindException, ValidationException {
        if (contactPersons == null) {
            return Optional.empty();
        }
        Optional<CCAddress> endstellenAdresse = Optional.empty();
        for (WholesaleContactPerson contactPerson : contactPersons) {
            final GeoId geoIdObj = availabilityService.findGeoId(geoId);
            Ansprechpartner ansprechpartner = new Ansprechpartner();
            ansprechpartner.setAuftragId(auftragId);
            ansprechpartner.setTypeRefId(toTypeRefId(contactPerson.getRole()));
            ansprechpartner.setPreferred(Boolean.FALSE);
            CCAddress address = new CCAddress();
            address.setAddressType(ADDRESS_TYPE_WHOLESALE_FFM_DATA);
            address.setFormatName(CCAddress.ADDRESS_FORMAT_RESIDENTIAL);
            /* Intentially leaving out salutation here, because we a) don't have a field in CCAddress and
             * b) FFM only has an optional field 'gender'. */
            address.setVorname(contactPerson.getFirstName());
            address.setName(contactPerson.getLastName());
            address.setEmail(contactPerson.getEmailAddress());
            address.setHandy(contactPerson.getMobilePhoneNumber());
            address.setFax(contactPerson.getFaxNumber());
            address.setTelefon(contactPerson.getPhoneNumber());

            if (ROLE_STANDORTA.equals(contactPerson.getRole())) {
                address.setStrasse(geoIdObj.getStreet());
                address.setNummer(geoIdObj.getHouseNum());
                address.setHausnummerZusatz(geoIdObj.getHouseNumExtension());
                address.setAddressType(CCAddress.ADDRESS_TYPE_WHOLESALE_FFM_DATA);
                address.setStrasse(geoIdObj.getStreet());
                address.setOrt(geoIdObj.getCity());
                address.setPlz(geoIdObj.getZipCode());
                address.setStrasseAdd(lageTaeOnt);
                address.setOrtsteil(geoIdObj.getDistrict());
                if (!endstellenAdresse.isPresent()) {
                    endstellenAdresse = Optional.of(address);
                }
            }

            ansprechpartner.setAddress(address);
            ansprechpartnerService.saveAnsprechpartner(ansprechpartner);
        }
        return endstellenAdresse;
    }

    private static
    @Nullable
    Long toTypeRefId(@Nullable String role) {
        if (role != null) {
            switch (role) {
                case ROLE_ENDKUNDE:
                    return Ansprechpartner.Typ.KUNDE.refId();
                case ROLE_AUFTRAGSMANAGEMENT:
                    return Ansprechpartner.Typ.HOTLINE_PARTNER.refId();
                case ROLE_TECHNIK:
                    return Ansprechpartner.Typ.TECH_SERVICE.refId();
                case ROLE_STANDORTA:
                    return Ansprechpartner.Typ.ENDSTELLE_B.refId();
                default:
                    throw new WholesaleServiceException(ERROR_RESERVING_PORT,
                            format("Received unexpected role '%s' for contact person. Expected values are: %s, %s, %s, %s.",
                                    role, ROLE_ENDKUNDE, ROLE_AUFTRAGSMANAGEMENT, ROLE_TECHNIK, ROLE_STANDORTA), null, null);
            }
        }
        return null;
    }

    private WholesaleReservePortResponse reservePortResponse(final Date execDate, final CreateHurricanOrderResult result) {
        final WholesaleReservePortResponse response = new WholesaleReservePortResponse();

        response.setLineId(result.lineId);
        response.setExecutionDate(DateConverterUtils.asLocalDate(execDate));
        response.setHurricanAuftragId(result.auftrag.getAuftragId().toString());

        return response;
    }

    private WholesaleReservePortResponse reserveFtthPortResponse(final Date execDate, final CreateHurricanOrderResult result) {
        final WholesaleReservePortResponse response = reservePortResponse(execDate, result);

        response.setManuellePortzuweisung(true);

        return response;
    }

    private WholesaleReservePortResponse reserveFttbPortResponse(Date execDate, CreateHurricanOrderResult result) throws StoreException, FindException {
        final HWRack rack = hardwareDAO.findRack4EqInOfRangierung(result.rangierung.getId());
        final HWOlt olt = hardwareDAO.findHwOltForRack(rack);
        final A10NspPort a10NspPort = ekpFrameContractService.findA10NspPort(result.ekpFrameContract, olt.getId());
        final WholesaleReservePortResponse response = reservePortResponse(execDate, result);

        updateAntragsdaten(result.auftrag.getAuftragId(), a10NspPort);

        response.setDpoNochNichtVerbaut((rack instanceof HWDpo) && ((HWDpo) rack).getSerialNo() == null);
        response.setA10nsp(a10NspPort.getA10Nsp().getName());
        response.setA10nspPort(a10NspPort.getVbz().getVbz());
        response.setSvlanEkp(String.valueOf(result.vlans.get(0).getSvlanEkp()));

        return response;
    }

    private void updateAntragsdaten(Long auftragId, A10NspPort a10NspPort) throws StoreException, FindException {

        AuftragDaten aDaten = auftragService.findAuftragDatenByAuftragIdTx(auftragId);
        if (aDaten != null && a10NspPort != null) {
            StringBuilder sb = new StringBuilder();
            if (isNotBlank(aDaten.getBemerkungen())) {
                sb.append(aDaten.getBemerkungen()).append(", ");
            }
            sb.append(format("A10nsp=%s, A10nspPort=%s", a10NspPort.getA10Nsp().getName(),
                    a10NspPort.getVbz().getVbz()));
            aDaten.setBemerkungen(sb.toString());
            auftragService.saveAuftragDaten(aDaten, false);
        }
    }

    private Date asWorkingDay(WholesaleReservePortRequest request) {
        return asDate(DateCalculationHelper.asWorkingDay(request.getDesiredExecutionDate()));
    }

    private void createVerlauf(Long sessionId, Date execDate, Long auftragId) throws StoreException, FindException {
        baService.createVerlauf(new CreateVerlaufParameter(auftragId, execDate,
                BAVerlaufAnlass.NEUSCHALTUNG, null, false, sessionId, new HashSet<>()));
    }

    private static final class CreateHurricanOrderResult {
        final String lineId;
        final Auftrag auftrag;
        final EkpFrameContract ekpFrameContract;
        final Rangierung rangierung;
        final Endstelle endstelleB;
        final List<EqVlan> vlans;

        CreateHurricanOrderResult(String lineId,
                Auftrag auftrag,
                EkpFrameContract ekpFrameContract,
                Rangierung rangierung,
                Endstelle endstelleB,
                List<EqVlan> vlans) {
            this.lineId = lineId;
            this.auftrag = auftrag;
            this.ekpFrameContract = ekpFrameContract;
            this.rangierung = rangierung;
            this.endstelleB = endstelleB;
            this.vlans = vlans;
        }
    }

    private CreateHurricanOrderResult createHurricanOrder(WholesaleReservePortRequest request, final Date execDate,
            @Nullable final AuftragAktion auftragAktion) throws StoreException, FindException {
        final Long produktId = getHurricanProduktId(request);
        final AuftragDaten auftragDaten = createAuftragDaten(request, execDate, produktId);
        final AuftragTechnik auftragTechnik = new AuftragTechnik();
        auftragTechnik.setAuftragsart(BAVerlaufAnlass.NEUSCHALTUNG);
        // AuftragsService legt Auftrag und VBZ an.
        // Synch der techn. Leistungen wird hier nicht durchgefuehrt, da kein Taifun-Auftrag hinterlegt ist!
        final Auftrag auftrag = auftragService.createAuftrag(null, auftragDaten, auftragTechnik, null, null);
        final WholesaleEkpFrameContract wholesaleEkpFrameContract = request.getEkpFrameContract();
        final EkpFrameContract ekpFrameContract = assignEkpFrameContract(auftrag, wholesaleEkpFrameContract.getEkpId(),
                wholesaleEkpFrameContract.getEkpFrameContractId(), LocalDate.now(), auftragAktion);
        final Endstelle endstelleB = createEndstelleB(auftragTechnik);
        final String lineId = getLineId(auftragTechnik);
        Rangierung rangierung = null;
        List<EqVlan> vlans = null;

        // technische Leistungen ermitteln und dem Auftrag zuordnen (muss VOR der Rangierungszuordnung erfolgen, da die
        // Rangierungszuordnung die Downstream-Bandbreite beruecksichtigt)
        synchTechLeistungen(request.getProduct(), auftrag, request.getDesiredExecutionDate(), false, auftragAktion);
        // Standort zu GeoId / Produkt ermitteln und der Endstelle (B) zuordnen
        assignTechLocation(request, endstelleB, produktId);
        // Niederlassung ID anhand der HVTGruppe der Endstelle ermitteln
        auftragTechnik.setNiederlassungId(getNiederlassungIdByEndstelle(endstelleB));

        if (!WholesaleProductGroup.FTTH_BSA.equals(request.getProduct().getGroup())) {
            rangierung = assignRangierung(endstelleB);
            // EqVlan berechnen und wenn notwendig speichern
            vlans = calculateAndSaveEqVlan(lineId, auftrag, produktId, request.getDesiredExecutionDate(), auftragAktion);
        }

        return new CreateHurricanOrderResult(lineId, auftrag, ekpFrameContract, rangierung, endstelleB, vlans);
    }

    private Endstelle createEndstelleB(AuftragTechnik auftragTechnik) throws StoreException {
        List<Endstelle> endstellen = endstellenService.createEndstellen(auftragTechnik, Produkt.ES_TYP_NUR_B, null);
        if (endstellen == null) {
            throw new WholesaleServiceException(ERROR_RESERVING_PORT,
                    "Access points for Hurrican order not created!", null, null);
        }
        Endstelle endstelleB = Endstelle.getEndstelleOfType(endstellen, Endstelle.ENDSTELLEN_TYP_B);
        if ((endstelleB == null) || endstellen.isEmpty()) {
            throw new WholesaleServiceException(ERROR_RESERVING_PORT,
                    "Access points for Hurrican order not created!", null, null);
        }
        return endstelleB;
    }

    private AuftragDaten createAuftragDaten(WholesaleReservePortRequest request, Date execDate, Long produktId) {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setWholesaleAuftragsId(request.getOrderId());
        auftragDaten.setProdId(produktId);
        auftragDaten.setStatusId(AuftragStatus.ERFASSUNG);
        auftragDaten.setBearbeiter(WHOLESALE_USER);
        auftragDaten.setBemerkungen(format("Wholesale Order - EKP=%s, ExtOrderId=%s, GeoId=%s, LageTaeOnt=%s, EKPRahmenvertragId=%s",
                request.getEkpFrameContract().getEkpId(), request.getExtOrderId(), request.getGeoId(), request.getLageTaeOnt(), request.getEkpFrameContract().getEkpFrameContractId()));
        auftragDaten.setVorgabeSCV(execDate);
        return auftragDaten;
    }

    private @Nullable Long getNiederlassungIdByEndstelle(Endstelle endstelle) throws FindException {
        HVTGruppe hvtGruppe4Standort = hvtService.findHVTGruppe4Standort(endstelle.getHvtIdStandort());
        return (hvtGruppe4Standort != null) ? hvtGruppe4Standort.getNiederlassungId() : null;
    }

    private String getLineId(Long auftragId) throws FindException {
        return getLineId(auftragService.findAuftragTechnikByAuftragIdTx(auftragId));
    }

    private String getLineId(AuftragTechnik auftragTechnik) throws FindException {
        VerbindungsBezeichnung vbz = physikService.findVerbindungsBezeichnungById(auftragTechnik.getVbzId());
        String lineId = (vbz != null) ? vbz.getVbz() : null;
        if (lineId == null) {
            throw new FindException("Unable to get lineId");
        }

        return lineId;
    }

    List<EqVlan> calculateAndSaveEqVlan(final String lineId, final Auftrag auftrag, final Long produktId,
            final LocalDate desiredExecutionDate, @Nullable final AuftragAktion auftragAktion) throws StoreException {
        try {
            Long auftragId = auftrag.getAuftragId();
            EkpFrameContract ekpFrameContract = ekpFrameContractService.findEkp4AuftragOrDefaultMnet(
                    auftrag.getAuftragId(), desiredExecutionDate, false);

            List<EqVlan> vlans = vlanService
                    .assignEqVlans(ekpFrameContract, auftragId, produktId, desiredExecutionDate, auftragAktion);

            if (vlans.isEmpty()) {
                throw new WholesaleServiceException(WholesaleFehlerCode.VLANS_NOT_EXIST,
                        "VLANs could not be calculated or found!", lineId, null);
            }

            return vlans;
        }
        catch (HurricanConcurrencyException e) {
            throw new WholesaleTechnicalException(format(
                    "Error calculating/assigning VLANs: %s Please try again!", e.getMessage()), e);
        }
        catch (Exception e) {
            throw new WholesaleServiceException(WholesaleFehlerCode.VLANS_NOT_EXIST, format(
                    "Error calculating VLANs: %s", e.getMessage()), null, e);
        }
    }


    private EkpFrameContract assignEkpFrameContract(Auftrag auftrag, String ekpId, String ekpFrameContractId,
            LocalDate desiredExecDate, @Nullable AuftragAktion auftragAktion) {
        final EkpFrameContract ekpFrameContract = findEkpFrameContract(ekpId, ekpFrameContractId);
        ekpFrameContractService.assignEkpFrameContract2Auftrag(
                ekpFrameContract,
                auftrag,
                desiredExecDate,
                auftragAktion);
        return ekpFrameContract;
    }

    private EkpFrameContract findEkpFrameContract(final String ekpId, final String ekpFrameContractId) {
        EkpFrameContract ekpFrameContract = ekpFrameContractService.findEkpFrameContract(ekpId, ekpFrameContractId);
        if (ekpFrameContract == null) {
            throw new WholesaleServiceException(WholesaleFehlerCode.EKP_FRAME_CONTRACT_NOT_EXIST,
                    format("EKP Frame Contract %s / %s does not exist!", ekpId,
                            ekpFrameContractId),
                    null, null
            );
        }
        return ekpFrameContract;
    }

    /*
     * Ermittelt die Differenz der vorhandenen zu den 'gewuenschten' technischen Leistungen des Auftrags und fuehrt den
     * Abgleich durch. Prueft, ob die benoetigte Downstream-Bandbreite auf dem Port technisch machbar ist.
     */
    private void synchTechLeistungen(WholesaleProduct product, Auftrag auftrag, LocalDate desiredExecutionDate,
            boolean checkForDownstream, @Nullable AuftragAktion auftragAktion)
            throws FindException, StoreException {
        WholesaleProductName productName = product.getName();
        ExterneAuftragsLeistungen externeAuftragsLeistungen = createExterneAuftragsLeistungen(product,
                desiredExecutionDate);

        List<LeistungsDiffView> diffs = ccLeistungsService.findLeistungsDiffs(auftrag.getId(), null,
                productName.hurricanProduktId, externeAuftragsLeistungen,
                Date.from(desiredExecutionDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        );

        if (checkForDownstream) {
            for (LeistungsDiffView diff : diffs) {
                TechLeistung techLeistung = ccLeistungsService.findTechLeistung(diff.getTechLeistungId());
                if (techLeistung.getTyp().equals(TechLeistung.TYP_DOWNSTREAM)
                        && !DateTools.isDateEqual(techLeistung.getGueltigBis(), techLeistung.getGueltigVon())) {
                    Rangierung rangierung = findRangierung(auftrag);
                    Bandwidth bandwidth = Bandwidth.create(techLeistung.getIntegerValue());
                    if (!rangierungsService.isBandwidthPossible4Rangierung(rangierung, bandwidth)) {
                        throw new TechnischNichtMoeglichException(
                                "Auf der vorhandenen technischen Infrastruktur ist das Produkt nicht bereitstellbar!");
                    }
                }
            }
        }

        ccLeistungsService.synchTechLeistung4Auftrag(auftrag.getId(), productName.hurricanProduktId,
                Date.from(desiredExecutionDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), true,
                null, diffs, auftragAktion);
    }

    private ExterneAuftragsLeistungen createExterneAuftragsLeistungen(WholesaleProduct product, LocalDate executionDate) {
        WholesaleProductName productName = product.getName();
        Set<Long> extLeistungNos = Sets.newHashSet();
        extLeistungNos.add(productName.extLeistung.getLeistungNo());

        Map<Long, BAuftragPos> positionen = Maps.newHashMap();
        ListMultimap<Long, BAuftragPos> extLeistNo2BAuftragPositionen = ArrayListMultimap.create();
        List<BAuftragLeistungView> leistungsViews = Lists.newArrayList();
        for (Long extLeistungNo : extLeistungNos) {
            BAuftragPos auftragPos = BAuftragPos.createBAuftragPos(executionDate);
            Leistung leistung = Leistung.createLeistung(extLeistungNo);
            BAuftragLeistungView auftragLeistungView = BAuftragLeistungView.createBAuftragLeistungView(auftragPos,
                    leistung);

            positionen.put(extLeistungNo, auftragPos);
            extLeistNo2BAuftragPositionen.put(extLeistungNo, auftragPos);
            leistungsViews.add(auftragLeistungView);
        }

        return new ExterneAuftragsLeistungen(positionen,
                extLeistNo2BAuftragPositionen, leistungsViews, leistungsViews);
    }

    /**
     * Ermittelt die Hurrican Produkt Id, die fuer das im {@link WholesaleReservePortRequest} angegebene Produkt
     * notwendig ist.
     *
     * @param request Daten für die Portreservierung
     * @return Hurrican Produkt Id
     */
    Long getHurricanProduktId(WholesaleReservePortRequest request) {
        Long produktId = request.getProduct().getName().hurricanProduktId;
        Log.info(format("Wholesale Product-Name=%s; Hurrican ProduktId=%s", request.getProduct().getName()
                .name(), produktId));
        if (produktId == null) {
            throw new WholesaleServiceException(ERROR_RESERVING_PORT,
                    "Hurrican product Id is not defined.", null, null);
        }
        return produktId;
    }

    /**
     * Ermittelt den passenden Standort fuer die Endstelle 'B' an Hand des Produkts sowie der GeoId des Anschlusses.
     *
     * @param request    Request-Objekt, das die GeoId enthaelt
     * @param endstelleB Endstelle B des Wholesale-Auftrags
     * @param produktId  Hurrican Produkt-Id des Wholesale-Auftrags
     */
    void assignTechLocation(WholesaleReservePortRequest request, Endstelle endstelleB, Long produktId) {
        try {
            // pruefen, ob GeoId im Cache vorhanden ist
            GeoId geoId = availabilityService.findGeoId(request.getGeoId());
            if (geoId == null) {
                throw new WholesaleServiceException(ERROR_RESERVING_PORT, format(
                        "The given GeoId %s is unknown!", request.getGeoId()), null, null);
            }

            List<GeoId2TechLocation> geoId2TechLocations = availabilityService.findPossibleGeoId2TechLocations(geoId,
                    produktId);
            if (CollectionTools.isEmpty(geoId2TechLocations)) {
                throw new WholesaleServiceException(ERROR_RESERVING_PORT, format(
                        "The given GeoId %s is not configured!", request.getGeoId()), null, null);
            }

            endstelleB.withGeoId(geoId.getId())
                    .withEndstelle(geoId.getStreetAndHouseNum())
                    .withPlz(geoId.getZipCode())
                    .withOrt(geoId.getCity())
                    .withHvtIdStandort(geoId2TechLocations.get(0).getHvtIdStandort())
                    // hoechst priorisierten Standort verwenden!
                    .withAnschlussart(
                            hvtService.findAnschlussart4HVTStandort(geoId2TechLocations.get(0).getHvtIdStandort()));

            if (endstelleB.getAnschlussart() == null) {
                throw new WholesaleServiceException(ERROR_RESERVING_PORT, format(
                        "Type of access point is not defined for Tech-Location (HVT) Id %s",
                        endstelleB.getHvtIdStandort()), null, null);
            }

            endstellenService.saveEndstelle(endstelleB);
        }
        catch (WholesaleServiceException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(ERROR_RESERVING_PORT, format(
                    "Assignment of technical location failed: %s", e.getMessage()), null, e);
        }
    }

    /**
     * Versucht der angegebenen Endstelle eine Rangierung zuzuordnen. <br> Falls keine passende freie Rangierung
     * gefunden wird, wird vom Rangierungs-Service eine FindException geworfen; sollte bei der Zuordnung der Rangierung
     * zur Endstelle ein Fehler auftreten (z.B. durch Concurrency-Probleme) wird eine andere Exception (StoreException)
     * generiert. <br> Abhaengig vom Fehler eine WholesaleServiceException (und somit ein fachlicher Fehler) oder eine
     * 'normale' RuntimeException (= technischer Fehler) generiert. Somit ist das Consumer-System (Tibco) in der Lage zu
     * entscheiden, den Call zu wiederholen oder auf Fehler zu gehen.
     */
    Rangierung assignRangierung(Endstelle endstelleB) {
        try {
            final Rangierung assignedRangierung = rangierungsService.assignRangierung2ES(endstelleB.getId(), null);
            if (assignedRangierung == null) {
                throw new WholesaleServiceException(ERROR_RESERVING_PORT, "Port not assigned!",
                        null, null);
            }
            return assignedRangierung;
        }
        catch (WholesaleException e) {
            throw e;
        }
        catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(WholesaleFehlerCode.KEIN_FREIER_PORT, format(
                    "Assignment of port failed: %s", e.getMessage()), null, e);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleTechnicalException(format("Error assigning port: %s; please try again!",
                    e.getMessage()), e);
        }
    }

    @Override
    public WholesaleModifyPortResponse modifyPort(WholesaleModifyPortRequest request) {
        boolean isPortChanged = false;
        String lineId = request.getLineId();
        final String requestedContractId = request.getEkpContractId();
        final String requestedEkpId = request.getEkpId();
        final LocalDate desiredExecutionDate = request.getDesiredExecutionDate();
        checkProductGroup(request.getProduct());
        checkModifyDate(desiredExecutionDate, lineId);

        try {
            Pair<Auftrag, AuftragDaten> order = findActiveOrderByLineIdAndCheckWholesaleProduct(lineId, LocalDate.now());
            Auftrag auftragToLog = order.getFirst();
            AuftragAktion auftragAktion = logAndCheckAktion(auftragToLog.getAuftragId(), null, AktionType.MODIFY_PORT,
                    request.getDesiredExecutionDate());
            Long auftragIdBeforePortChanged = null;

            try {
                synchTechLeistungen(request.getProduct(), order.getFirst(), request.getDesiredExecutionDate(), true, auftragAktion);
                modifyAuftrag2EkpFrameContractIfNecessary(lineId, requestedContractId, requestedEkpId,
                        desiredExecutionDate, order.getFirst(), auftragAktion);
                // EqVlan berechnen und wenn notwendig speichern
                calculateAndSaveEqVlan(lineId, order.getFirst(), order.getSecond().getProdId(),
                        request.getDesiredExecutionDate(), auftragAktion);
            }
            catch (TechnischNichtMoeglichException ex) {
                if (!request.isChangeOfPortAllowed()) {
                    throw ex;
                }
                try {
                    final CreateHurricanOrderResult createOrderResult = versuchePortwechselDurchReleaseUndReserve(request, order, auftragAktion);
                    final String newLineId = createOrderResult.lineId;
                    if (!newLineId.equals(lineId)) {
                        lineId = newLineId;
                        isPortChanged = true;
                    }
                    auftragToLog = createOrderResult.auftrag;
                    auftragIdBeforePortChanged = order.getFirst().getAuftragId();
                }
                catch (WholesaleException ex2) {
                    // KEIN_FREIER_PORT("HUR-1309") fangen und als TECHNISCH_NICHT_MOEGLICH("HUR-1300") weiter werfen
                    throw new TechnischNichtMoeglichException(ex2);
                }
            }

            if (auftragIdBeforePortChanged != null) {
                auftragService.moveModifyPortAktion(auftragAktion, auftragToLog.getAuftragId());
            }
        }
        catch (WholesaleException | WholesaleTechnicalException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_MODIFY_PORT, e.getMessage(), lineId, e);
        }

        WholesaleModifyPortResponse response = new WholesaleModifyPortResponse();
        response.setExecutionDate(request.getDesiredExecutionDate());
        response.setLineId(lineId);
        response.setPortChanged(isPortChanged);

        return response;
    }

    /**
     * Auftrag kuendigen, Neuen Auftrag anlegen und diese in ACTION_Tabelle vermerken. Achtung: kann ebenfalls zum
     * Fehler fuehren.
     *
     */
    private CreateHurricanOrderResult versuchePortwechselDurchReleaseUndReserve(WholesaleModifyPortRequest request,
            Pair<Auftrag, AuftragDaten> order, AuftragAktion auftragAktion)
            throws FindException, StoreException {
        Endstelle endstelle = findEndstelle(order.getFirst());
        //TODO: trenkerbe - check if it's needed!
        WholesaleReservePortRequest reservePortRequest = new WholesaleReservePortRequest();
        reservePortRequest.setDesiredExecutionDate(request.getDesiredExecutionDate());
        WholesaleEkpFrameContract ekpFrameContract = new WholesaleEkpFrameContract();
        ekpFrameContract.setEkpFrameContractId(request.getEkpContractId());
        ekpFrameContract.setEkpId(request.getEkpId());
        reservePortRequest.setEkpFrameContract(ekpFrameContract);
        reservePortRequest.setProduct(request.getProduct());
        reservePortRequest.setGeoId(endstelle.getGeoId());
        return createHurricanOrder(reservePortRequest, asDate(request.getDesiredExecutionDate()), auftragAktion);
    }

    /**
     * Aendert die Zuordnung des Auftrags zum EKP-Rahmenvertrag ab, sofern sie durch den modifyPort Request geaendert
     * werden soll.
     */
    final void modifyAuftrag2EkpFrameContractIfNecessary(final String lineId,
            final String requestedContractId, final String requestedEkpId, final LocalDate desiredExecutionDate,
            final Auftrag auftrag, @Nullable final AuftragAktion auftragAktion) {
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = ekpFrameContractService.findAuftrag2EkpFrameContract(
                auftrag.getAuftragId(), desiredExecutionDate);
        if (auftrag2EkpFrameContract == null) {
            throw new Auftrag2EkpFrameContractNotFoundException(lineId, auftrag.getAuftragId(), desiredExecutionDate);
        }

        assignEkpFrameContract(auftrag, requestedEkpId, requestedContractId, desiredExecutionDate, auftragAktion);
    }

    private void checkModifyDate(LocalDate desiredExecutionDate, final String lineId) {
        final LocalDate today = LocalDate.now();
        if (desiredExecutionDate.isBefore(today)) {
            throw new WholesaleServiceException(WholesaleFehlerCode.EXECUTION_DATE_IN_PAST,
                    "desired execution date must not be in past", lineId, null);
        }
    }

    private void checkProductGroup(WholesaleProduct product) {
        if (product.getGroup() == WholesaleProductGroup.FTTH_BSA) {
            throw new ProductGroupNotSupportedException(product.getGroup());
        }
    }

    /**
     * Logged die Aktion in DB und prueft dabei, ob diese Aktion erlaubt ist. Falls nicht, wird eine entsprechende
     * (Runtime-) Exception in der Servicemethode {@link CCAuftragService#saveAuftragAktion(AuftragAktion)} geworfen.
     */
    AuftragAktion logAndCheckAktion(Long auftragId, @Nullable Long prevAuftragId, AktionType aktionType,
            LocalDate desiredExecutionDate)
            throws StoreException {
        AuftragAktion aktion = new AuftragAktion();
        aktion.setAuftragId(auftragId);
        aktion.setAction(aktionType);
        aktion.setDesiredExecutionDate(desiredExecutionDate);
        aktion.setPreviousAuftragId(prevAuftragId);
        auftragService.saveAuftragAktion(aktion);
        return aktion;
    }

    @Override
    /**
     * Führt eine Terminverschiebung für folgende Fälle durch:
     *  - Neuschaltung
     *  - Leistungsmerkmaländerung
     *  - Kündigung
     *
     *  Folgende Exceptions werden im entsprechenden Fehlerfall geworfen:
     *  - LineIdNotFoundException: kein geschaltener Port wird für die LineId gefunden
     *  - NotAWholesaleProductException: der über die LineId referenzierte Port ist keinem Wholesaleauftrag zugeordnet
     *  - WholesaleServiceException: wenn keine noch ausstehenden Änderungen/Neuschaltung/Kündigung am Port existieren
     **/
    public WholesaleModifyPortReservationDateResponse modifyPortReservationDate(
            WholesaleModifyPortReservationDateRequest request) {
        try {
            final AuftragDaten auftragDaten = findOrderByLineIdAndStatus(request.getLineId(),
                    AuftragStatus.ERFASSUNG,
                    AuftragStatus.TECHNISCHE_REALISIERUNG, //Neuschaltung wird verschoben
                    AuftragStatus.IN_BETRIEB, //es gibt keinen Termin zu verschieben
                    //AuftragStatus.AENDERUNG_IM_UMLAUF, //Änderung am Auftrag wird verschoben: bei Realisierung LMAE relevant
                    AuftragStatus.KUENDIGUNG_TECHN_REAL //Kündigung wird verschoben: bei Kündigung relevant
            );

            // TODO check auf modifyPortAction kann entfernt werden,
            // falls keine LMAE implementiert werden müssen, anosnten muss hier noch ein Änderungsbauauftrag ggf verschoben werden
            AuftragAktion modifyPortAction = auftragService.getActiveAktion(auftragDaten.getAuftragId(),
                    AktionType.MODIFY_PORT);
            if (modifyPortAction != null) {
                aenderungenAnAktivemAuftragVerschieben(request, auftragDaten, modifyPortAction);
            }
            else if (AuftragStatus.ERFASSUNG.equals(auftragDaten.getAuftragStatusId())) {
                handleModifyPortReservationDateForStatusErfassung(request, auftragDaten);
            }
            else if (AuftragStatus.IN_BETRIEB.equals(auftragDaten.getAuftragStatusId())) {
                throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_MODIFY_PORT_RESERVATION_DATE,
                        "Es existieren keine ausstehenden Änderungen am Auftrag, das Realisierungsdatum kann deshalb nicht angepasst werden!",
                        request.getLineId(), null);
            }
            else if (AuftragStatus.TECHNISCHE_REALISIERUNG.equals(auftragDaten.getAuftragStatusId())) {
                auftragsRealisierungVerschieben(request, auftragDaten, true);
            }
            else if (AuftragStatus.KUENDIGUNG_TECHN_REAL.equals(auftragDaten.getAuftragStatusId())) {
                auftragsKuendigungVerschieben(request, auftragDaten);
            }

            return getWholesaleModifyPortReservationDateResponse(request);
        }
        catch (BAService.TerminverschiebungException e) {
            throw new ModifyPortReservationDateToEarlierDateException(e.getMessage());
        }
        catch (LanguageException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleModifyPortReservationDateForStatusErfassung(WholesaleModifyPortReservationDateRequest request,
            AuftragDaten auftragDaten) throws LanguageException {
        if (isStandort(auftragDaten.getAuftragId(), HVTStandort.HVT_STANDORT_TYP_FTTB)) {
            final Verlauf verlauf = baService.findActVerlauf4Auftrag(auftragDaten.getAuftragId(), false);
            if (verlauf == null) {
                createVerlauf(request.getSessionId(), auftragDaten.getVorgabeSCV(), auftragDaten.getAuftragId());
            }
            auftragsRealisierungVerschieben(request, auftragDaten, false);
        }
        else if (isStandort(auftragDaten.getAuftragId(), HVTStandort.HVT_STANDORT_TYP_FTTH)) {
            auftragsRealisierungVerschieben(request, auftragDaten, false);
        }
    }

    private boolean isStandort(Long auftragId, Long standort) throws FindException {
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        return standort.equals(getHvtStandortTypRefId(endstelle.getHvtIdStandort()));
    }

    private Long getHvtStandortTypRefId(final Long hvtStandortId) throws FindException {
        final HVTStandort hvtStandOrt = hvtService.findHVTStandort(hvtStandortId);
        if (hvtStandOrt == null) {
            throw new FindException("HvtStandort nicht gefunden.");
        }
        return hvtStandOrt.getStandortTypRefId();
    }

    private WholesaleModifyPortReservationDateResponse getWholesaleModifyPortReservationDateResponse(WholesaleModifyPortReservationDateRequest request) {
        WholesaleModifyPortReservationDateResponse response = new WholesaleModifyPortReservationDateResponse();
        response.setExecutionDate(request.getDesiredExecutionDate());
        response.setLineId(request.getLineId());
        return response;
    }

    private void auftragsRealisierungVerschieben(WholesaleModifyPortReservationDateRequest request,
            AuftragDaten auftragDaten, boolean isMoveEqVlans) throws LanguageException {
        final LocalDate originalDate = DateConverterUtils.asLocalDate(auftragDaten.getVorgabeSCV());
        auftragDaten.setVorgabeSCV(asDate(request.getDesiredExecutionDate()));
        auftragService.saveAuftragDaten(auftragDaten, false);
        modifyReservationDates(auftragDaten.getAuftragId(), originalDate, request.getDesiredExecutionDate(), null, isMoveEqVlans);
        final Auftrag2EkpFrameContract auftrag2EkpFrameContract = findAuftrag2EkpFrameContract(request.getLineId(),
                auftragDaten.getAuftragId(), originalDate);
        auftrag2EkpFrameContract.setAssignedFrom(request.getDesiredExecutionDate());
        ekpFrameContractService.saveAuftrag2EkpFrameContract(auftrag2EkpFrameContract);

        changeRealDate(request, auftragDaten);
    }

    private void auftragsKuendigungVerschieben(WholesaleModifyPortReservationDateRequest request, AuftragDaten auftragDaten) throws LanguageException {
        leistungenKuendigen(auftragDaten,
                DateConverterUtils.asLocalDate(auftragDaten.getKuendigung()).minusDays(1L),
                request.getDesiredExecutionDate());

        auftragDaten.setKuendigung(asDate(request.getDesiredExecutionDate()));
        auftragService.saveAuftragDaten(auftragDaten, false);

        changeRealDate(request, auftragDaten);
    }

    private void changeRealDate(WholesaleModifyPortReservationDateRequest request, AuftragDaten auftragDaten) throws LanguageException {
        final Verlauf verlauf = baService.findActVerlauf4Auftrag(auftragDaten.getAuftragId(), false);
        if (verlauf != null) {
            final AKUser loggedInUser = userService.findUserBySessionId(request.getSessionId());
            baService.changeRealDate(verlauf.getId(), asDate(request.getDesiredExecutionDate()), loggedInUser);
        }
    }

    private void aenderungenAnAktivemAuftragVerschieben(WholesaleModifyPortReservationDateRequest request, AuftragDaten auftragDaten,
            AuftragAktion modifyPortAction) throws StoreException, FindException {
        final LocalDate originalDate = modifyPortAction.getDesiredExecutionDate();
        final LocalDate modifiedDate = request.getDesiredExecutionDate();

        // Keine Aktion noetig, falls das Datum nicht verschoben wurde
        if (!originalDate.equals(modifiedDate)) {
            // offenen Action-Eintrag schliessen und neue modifyPort Action mit neuem Datum anlegen
            auftragService.cancelAuftragAktion(modifyPortAction);
            logAndCheckAktion(auftragDaten.getAuftragId(), modifyPortAction.getPreviousAuftragId(), AktionType.MODIFY_PORT, request.getDesiredExecutionDate());

            final Auftrag2EkpFrameContract beforeModifyEkpFrameContract;
            final Auftrag2EkpFrameContract afterModifyEkpFrameContract;

            if (modifyPortAction.isPortChanged() && modifyPortAction.getPreviousAuftragId() != null) {
                final Long previousAuftragId = modifyPortAction.getPreviousAuftragId();
                modifyPortReservationDates4PreviousOrder(originalDate, modifiedDate, previousAuftragId,
                        modifyPortAction);

                final LocalDate beforeModifyEkpValidDate = (Instant.ofEpochMilli(auftragDaten.getInbetriebnahme().getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
                        .isEqual(originalDate)) ? originalDate : originalDate.minusDays(1);

                beforeModifyEkpFrameContract =
                        findAuftrag2EkpFrameContract(null, previousAuftragId, beforeModifyEkpValidDate);
                afterModifyEkpFrameContract =
                        findAuftrag2EkpFrameContract(request.getLineId(), auftragDaten.getAuftragId(), originalDate);

                auftragDaten.setInbetriebnahme(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                auftragService.saveAuftragDaten(auftragDaten, false);
            }
            else {
                beforeModifyEkpFrameContract = findAuftrag2EkpFrameContractBeforeModifyNoPortChanged(request.getLineId(),
                        auftragDaten.getAuftragId(), auftragDaten.getInbetriebnahme(), originalDate);
                afterModifyEkpFrameContract =
                        findAuftrag2EkpFrameContract(request.getLineId(), auftragDaten.getAuftragId(), originalDate);
            }
            modifyAuftrag2EkpFrameContractAssignments(beforeModifyEkpFrameContract,
                    afterModifyEkpFrameContract, modifiedDate);
            modifyReservationDates(auftragDaten.getAuftragId(), originalDate, modifiedDate, modifyPortAction);
        }
    }

    private Auftrag2EkpFrameContract findAuftrag2EkpFrameContractBeforeModifyNoPortChanged(
            final String lineId, final Long auftragId, final Date inbetriebnahme,
            final LocalDate originalDate) {
        Auftrag2EkpFrameContract beforeModifyEkpFrameContract;
        // modify am gleichen Tag wie reservePort?
        if (originalDate.isEqual(Instant.ofEpochMilli(inbetriebnahme.getTime()).atZone(ZoneId.systemDefault()).toLocalDate())) {
            List<Auftrag2EkpFrameContract> auftrag2EkpFrameContracts =
                    ekpFrameContractService.findAuftrag2EkpFrameContract(auftragId, originalDate, originalDate);

            if (auftrag2EkpFrameContracts.isEmpty()) {
                throw new Auftrag2EkpFrameContractNotFoundException(lineId, auftragId, originalDate);
            }
            else if (auftrag2EkpFrameContracts.size() > 1) {
                throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_MODIFY_PORT_RESERVATION_DATE,
                        format("More than one previous Ekp-assignments found for order %s at %s",
                                auftragId, originalDate), null, null
                );
            }
            beforeModifyEkpFrameContract = auftrag2EkpFrameContracts.get(0);
        }
        else {
            beforeModifyEkpFrameContract =
                    findAuftrag2EkpFrameContract(lineId, auftragId,
                            originalDate.minusDays(1));
        }
        return beforeModifyEkpFrameContract;
    }

    void modifyPortReservationDates4PreviousOrder(final LocalDate originalDate,
            final LocalDate modifiedDate, final Long previousAuftragId, final AuftragAktion modifyPortAktion)
            throws StoreException, FindException {

        Rangierung rangierungOfPrevOrder = findRangierung4EndstelleBByAuftragId(previousAuftragId);
        rangierungOfPrevOrder.setFreigabeAb(Date.from(modifiedDate.plusDays(RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        rangierungsService.saveRangierung(rangierungOfPrevOrder, false);

        AuftragDaten previousAuftragDaten = auftragService.findAuftragDatenByAuftragIdTx(previousAuftragId);
        final LocalDate inbetriebnahme = Instant.ofEpochMilli(previousAuftragDaten.getInbetriebnahme().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        if (previousAuftragDaten.getKuendigung() != null) {
            previousAuftragDaten.setKuendigung(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            auftragService.saveAuftragDaten(previousAuftragDaten, false);
            modifyReservationDates(previousAuftragId, originalDate, modifiedDate, modifyPortAktion);
        }

        // reservePort und modify am gleichen tag? -> storno in kuendigung umwandeln
        else if (previousAuftragDaten.getStatusId().equals(AuftragStatus.STORNO)
                && originalDate.isEqual(inbetriebnahme)) {
            previousAuftragDaten.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
            previousAuftragDaten.setKuendigung(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            auftragService.saveAuftragDaten(previousAuftragDaten, false);
        }
        // reservePort in Vergangenheit, modifyPort zu heute
        else if (previousAuftragDaten.getStatusId().equals(AuftragStatus.STORNO)
                && originalDate.isBefore(inbetriebnahme)) {
            previousAuftragDaten.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
            previousAuftragDaten.setKuendigung(Date.from(modifiedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            auftragService.saveAuftragDaten(previousAuftragDaten, false);
            modifyReservationDates(previousAuftragId, originalDate, modifiedDate, modifyPortAktion);
        }
    }

    private Rangierung findRangierung4EndstelleBByAuftragId(final Long previousAuftragId) throws FindException {
        Rangierung[] rangierungen = rangierungsService.findRangierungenTx(previousAuftragId,
                Endstelle.ENDSTELLEN_TYP_B);
        if ((rangierungen != null) && (rangierungen.length > 0)) {
            return rangierungen[0];
        }
        else {
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_MODIFY_PORT_RESERVATION_DATE,
                    format("No Rangierung could be found for order with id %s", previousAuftragId),
                    null, null);
        }
    }

    protected final Auftrag2EkpFrameContract findAuftrag2EkpFrameContract(final @Nullable String lineId,
            final Long auftragId, final LocalDate when) {
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = ekpFrameContractService
                .findAuftrag2EkpFrameContract(auftragId, when);
        if (auftrag2EkpFrameContract == null) {
            throw new Auftrag2EkpFrameContractNotFoundException(lineId, auftragId, when);
        }
        return auftrag2EkpFrameContract;
    }

    /**
     * Modifiziert das {@code assignedTo} bzw. {@code assignedFrom} Datum der {@link Auftrag2EkpFrameContract}
     * Zuordnungen, die ueber das Datum {@code originalDate} bzw. {@code modifiedDate} ermittelt werden. Die Anpassung
     * erfolgt jeweils auf das Datum {@code modifiedDate}
     */
    private void modifyAuftrag2EkpFrameContractAssignments(
            final Auftrag2EkpFrameContract beforeModifyEkpFrameContract,
            final Auftrag2EkpFrameContract afterModifyEkpFrameContract, final LocalDate modifiedDate) {
        // TODO AuftragAktion mit beruecksichtigen!!!
        if (!beforeModifyEkpFrameContract.equals(afterModifyEkpFrameContract)) {
            beforeModifyEkpFrameContract.setAssignedTo(modifiedDate);
            afterModifyEkpFrameContract.setAssignedFrom(modifiedDate);
            ekpFrameContractService.saveAuftrag2EkpFrameContract(afterModifyEkpFrameContract);
            ekpFrameContractService.saveAuftrag2EkpFrameContract(beforeModifyEkpFrameContract);
        }
    }

    /**
     * Ermittelt alle relevanten Zuordnungen zu dem Auftrag, die zum Datum {@code originalDate} starten bzw. enden und
     * aendert deren Datum auf {@code modifiedDate} ab. <br> Darunter fallen zur Zeit <ul> <li>{@link
     * Auftrag2TechLeistung} Objekte <li>{@link Auftrag2DSLAMProfile} Objekte </ul>
     */
    void modifyReservationDates(Long auftragId, LocalDate originalDate, LocalDate modifiedDate,
            @Nullable AuftragAktion auftragAktion) throws StoreException, FindException {
        modifyReservationDates(auftragId, originalDate, modifiedDate, auftragAktion, true);
    }

    /**
     * Ermittelt alle relevanten Zuordnungen zu dem Auftrag, die zum Datum {@code originalDate} starten bzw. enden und
     * aendert deren Datum auf {@code modifiedDate} ab. <br> Darunter fallen zur Zeit <ul> <li>{@link
     * Auftrag2TechLeistung} Objekte <li>{@link Auftrag2DSLAMProfile} Objekte </ul>
     */
    private void modifyReservationDates(Long auftragId, LocalDate originalDate, LocalDate modifiedDate,
            @Nullable AuftragAktion auftragAktion, boolean isMoveEqVlans) throws StoreException, FindException {
        ccLeistungsService.modifyAuftrag2TechLeistungen(auftragId, originalDate, modifiedDate, auftragAktion);
        dslamService.modifyDslamProfiles4Auftrag(auftragId, originalDate, modifiedDate, auftragAktion);
        if (isMoveEqVlans) {
            vlanService.moveEqVlans4Auftrag(auftragId, originalDate, modifiedDate, auftragAktion);
        }
    }

    /**
     * Gibt den zu gegebener LineId Port frei.
     *
     * @param request Objekt mit Angaben zum Port (lineId) der freigegeben werden soll. orderId wird als
     *                wholesaleOrderId gespeichert und bei bauauftragUpdate an Hermes zurück gesendet.
     * @return Portfreigabe-Datum.
     */
    @Override
    public LocalDate releasePort(WholesaleReleasePortRequest request) {
        try {
            AuftragDaten auftragDaten = auftragService.findAuftragDatenByLineIdAndStatus(request.getLineId(),
                    AuftragStatus.IN_BETRIEB, AuftragStatus.TECHNISCHE_REALISIERUNG, AuftragStatus.ERFASSUNG);
            if (auftragDaten != null) {
                auftragDaten.setWholesaleAuftragsId(request.getOrderId());//Override the old wholesaleOrderId
            }
            else {
                throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_RELEASING_PORT, "Couldn't found AuftragDaten with", request.getLineId(), null);
            }
            return executeReleasePort(auftragDaten, request);
        }
        catch (WholesaleException we) {
            throw we;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_RELEASING_PORT, e.getMessage(), request.getLineId(), e);
        }
    }

    private LocalDate executeReleasePort(final AuftragDaten auftragDaten, final WholesaleReleasePortRequest request)
            throws FindException, StoreException {
        if (AuftragStatus.TECHNISCHE_REALISIERUNG.equals(auftragDaten.getAuftragStatusId())) {
            return executeReleasePort4Storno(auftragDaten, request);
        }
        else if (AuftragStatus.IN_BETRIEB.equals(auftragDaten.getAuftragStatusId())) {
            executeReleasePort4Kuendigung(auftragDaten, request);
            return request.getReleaseDate();
        }
        else if (AuftragStatus.ERFASSUNG.equals(auftragDaten.getAuftragStatusId())) {
            return executeReleasePort4AuftragStatusErfassung(auftragDaten);
        }
        else {
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_RELEASING_PORT, "Port couldn't be released for order with ",
                    request.getLineId(), null);
        }
    }

    /**
     * Pr&uuml;ft, ob zu den AuftragDaten eine Rangierung exisitert. Wenn ja, wird diese freigegeben. In jedem Fall wird
     * der AuftragStatus auf 'Absage' ge&auml;ndert.
     *
     * @param auftragDaten die AuftragDaten.
     * @return das Release Datum.
     * @throws FindException  wenn kein Auftrag gefunden werden konnte.
     * @throws StoreException wenn der Auftrag oder die Rangierung nicht gespeichert werden konnte.
     */
    private LocalDate executeReleasePort4AuftragStatusErfassung(AuftragDaten auftragDaten)
            throws FindException, StoreException {
        final LocalDate releaseDate = LocalDate.now();

        final Auftrag auftrag = auftragService.findAuftragById(auftragDaten.getAuftragId());
        try {
            Rangierung rangierung = findRangierung(auftrag);
            rangierungFreigeben(rangierung, auftragDaten, releaseDate);
        }
        catch (FindException e) {
            LOG.warn(format("No Rangierung found for auftragId '%s'", auftragDaten.getAuftragId()), e);
        }
        auftragDaten.setAuftragStatusId(AuftragStatus.ABSAGE);
        auftragService.saveAuftragDaten(auftragDaten, false);
        return releaseDate;
    }

    LocalDate executeReleasePort4Storno(AuftragDaten auftragDaten, WholesaleReleasePortRequest request) throws FindException, StoreException {
        LocalDate releaseDate = LocalDate.now();
        Verlauf verlauf = baService.findActVerlauf4Auftrag(auftragDaten.getAuftragId(), false);
        if (verlauf == null) {
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_RELEASING_PORT, "Couldn't found Verlauf for order with ", request.getLineId(), null);
        }
        //Rangierung sofort freigeben
        baService.verlaufStornieren(verlauf.getId(), false, request.getSessionId());
        rangierungFreigeben(auftragDaten, releaseDate);
        auftragDaten.setAuftragStatusId(AuftragStatus.ABSAGE);
        auftragService.saveAuftragDaten(auftragDaten, false);
        return releaseDate;
    }


    AuftragDaten executeReleasePort4Kuendigung(AuftragDaten auftragDaten, WholesaleReleasePortRequest request) throws FindException, StoreException {
        LocalDate releaseDate = request.getReleaseDate();
        setzeAuftragAufKuendigung(auftragDaten, request);
        kuendigungsBauauftragErstellen(auftragDaten, request, releaseDate);
        leistungenKuendigen(auftragDaten, releaseDate, releaseDate);

        rangierungFreigeben(auftragDaten, releaseDate);
        return auftragDaten;
    }

    private AuftragDaten setzeAuftragAufKuendigung(AuftragDaten auftragDaten, WholesaleReleasePortRequest request) throws StoreException {
        auftragDaten.setKuendigung(asDate(request.getReleaseDate()));
        auftragDaten.setStatusId(AuftragStatus.KUENDIGUNG);
        return auftragService.saveAuftragDaten(auftragDaten, false);
    }

    private void leistungenKuendigen(AuftragDaten auftragDaten, LocalDate gueltigAm, LocalDate releaseDate) {
        final List<Auftrag2TechLeistung> leistungenZumKuendigungszeitpunkt =
                ccLeistungsService.findAuftrag2TechLeistungen(auftragDaten.getAuftragId(), gueltigAm);

        leistungenZumKuendigungszeitpunkt.forEach(a2t -> {
            a2t.setAktivBis(asDate(releaseDate));
            try {
                ccLeistungsService.saveAuftrag2TechLeistung(a2t);
            }
            catch (StoreException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void kuendigungsBauauftragErstellen(AuftragDaten auftragDaten, WholesaleReleasePortRequest request, LocalDate releaseDate) {
        try {
            baService.createVerlauf(new CreateVerlaufParameter(auftragDaten.getAuftragId(),
                    asDate(releaseDate),
                    BAVerlaufAnlass.KUENDIGUNG,
                    null,
                    false,
                    request.getSessionId(),
                    new HashSet<>()));
        }
        catch (Exception e) {
            throw new WholesaleServiceException(WholesaleFehlerCode.TECHNISCH_NICHT_MOEGLICH, e.getMessage(), null, e);
        }
    }

    /**
     * Ermittelt die Rangierung des Auftrags und gibt diese frei. Die Freigabe erfolgt entweder zum angegebenen Datum
     * {@code when} oder zum angegebenen Datum + Karenzzeit.
     */
    void rangierungFreigeben(AuftragDaten auftragDaten, LocalDate when) throws FindException,
            StoreException {
        rangierungFreigeben(null, auftragDaten, when);
    }

    private void rangierungFreigeben(@Nullable final Rangierung rangierung, final AuftragDaten auftragDaten, final LocalDate when)
            throws FindException, StoreException {
        Auftrag auftrag = auftragService.findAuftragById(auftragDaten.getAuftragId());
        Rangierung currentRangierung = rangierung;
        if (currentRangierung == null) {
            currentRangierung = findRangierung(auftrag);
        }
        if (when.equals(LocalDate.now()) || (NumberTools.equal(auftragDaten.getStatusId(), AuftragStatus.STORNO))) {
            // zum angegebenen Datum ohne Karenzzeit
            LocalDate freigabeDate = when.plusDays(1);
            currentRangierung.setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
            currentRangierung.setFreigabeAb(Date.from(freigabeDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        else {
            // zum angegebenen Datum + Karenzzeit freigeben
            LocalDate freigabeDate = when.plusDays(RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE);
            currentRangierung.setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
            currentRangierung.setFreigabeAb(Date.from(freigabeDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        rangierungsService.saveRangierung(currentRangierung, false);
    }

    private Rangierung findRangierung(Auftrag auftrag) throws FindException {
        Endstelle endstelleB = findEndstelle(auftrag);

        Rangierung rangierung = rangierungsService.findRangierung(endstelleB.getRangierId());
        if (rangierung == null) {
            throw new FindException(format("Rangierung with id %s not found!", endstelleB.getRangierId()));
        }
        return rangierung;
    }

    @Override
    public WholesaleCancelModifyPortResponse cancelModifyPort(WholesaleCancelModifyPortRequest request) {
        try {
            Pair<Auftrag, AuftragDaten> order = findActiveOrderByLineIdAndCheckWholesaleProduct(request.getLineId(),
                    LocalDate.now());
            final AuftragDaten auftragDaten = order.getSecond();
            AuftragAktion modifyPortAction = auftragService.getActiveAktion(auftragDaten.getAuftragId(), AktionType.MODIFY_PORT);
            if (modifyPortAction == null) {
                throw new WholesaleServiceException(
                        ERROR_CANCEL_MODIFY_PORT,
                        format(
                                "No pending modify port found for LineId %s. Therefore the cancel operation can not be executed!",
                                request.getLineId()),
                        request.getLineId(), null
                );
            }

            String lineId = cancelModifications(modifyPortAction, request.getLineId(), order);

            cancelModifyPortAction(modifyPortAction, request.getLineId(), auftragDaten.getAuftragId());

            WholesaleCancelModifyPortResponse response = new WholesaleCancelModifyPortResponse();
            response.setPreviousLineId(lineId);
            return response;
        }
        catch (WholesaleException e) {
            throw e;
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(ERROR_CANCEL_MODIFY_PORT,
                    "Unexpected error during cancel modify port!", request.getLineId(), e);
        }
    }

    /*
     * vorgesehende Auftragsaenderungen werden wieder rueckgaengig gemacht.
     */
    private String cancelModifications(AuftragAktion aktion, String lineId, Pair<Auftrag, AuftragDaten> order)
            throws StoreException, FindException {
        AuftragDaten auftragDaten = order.getSecond();
        Long previousAuftragId = aktion.getPreviousAuftragId();

        if (previousAuftragId != null) {
            // modifyPort hatte einen Portwechsel ausgeloest

            // aktuellen Auftrag stornieren und aktuelle Rangierung freigeben
            auftragDaten.setStatusId(AuftragStatus.STORNO);
            rangierungFreigeben(auftragDaten, LocalDate.now());

            // alten Auftrag und alte Rangierung wieder aktivieren
            Auftrag previousAuftrag = auftragService.findAuftragById(previousAuftragId);
            AuftragDaten previousAuftragDaten = auftragService.findAuftragDatenByAuftragIdTx(previousAuftragId);
            previousAuftragDaten.setStatusId(AuftragStatus.IN_BETRIEB);
            previousAuftragDaten.setKuendigung(null);
            auftragService.saveAuftragDaten(previousAuftragDaten, false);
            Rangierung alteRangierung = findRangierung(previousAuftrag);
            Endstelle alteEndstelle = findEndstelle(previousAuftrag);
            alteRangierung.setEsId(alteEndstelle.getId());
            alteRangierung.setFreigabeAb(null);
            rangierungsService.saveRangierung(alteRangierung, false);
            return getLineId(previousAuftragId);
        }
        else {
            // Aenderungen von modifyPort am aktuellen Auftrag rueckgaengig machen
            Long auftragId = auftragDaten.getAuftragId();
            ccLeistungsService.cancelAuftrag2TechLeistungen(auftragId, aktion);
            dslamService.cancelAuftrag2DslamProfile(auftragId, aktion);
            ekpFrameContractService.cancelEkpFrameContractAssignment(auftragId, aktion);

            vlanService.cancelEqVlans(auftragId, aktion);
            return lineId;
        }
    }

    final AuftragAktion cancelModifyPortAction(AuftragAktion modifyPortAction, final String lineId,
            final Long auftragId) {
        AuftragAktion cancelModifyPortAction = new AuftragAktion();
        cancelModifyPortAction.setCancelled(true);
        cancelModifyPortAction.setAction(AktionType.CANCEL_MODIFY_PORT);
        cancelModifyPortAction.setAuftragId(auftragId);
        cancelModifyPortAction.setDesiredExecutionDate(LocalDate.now());
        try {
            auftragService.cancelAuftragAktion(modifyPortAction);
            auftragService.saveAuftragAktion(cancelModifyPortAction);
        }
        catch (StoreException e) {
            LOG.error(e.getMessage(), e);
            throw new WholesaleServiceException(ERROR_CANCEL_MODIFY_PORT,
                    "Unexpected error during cancel modify port!", lineId, e);
        }
        return cancelModifyPortAction;
    }

    /**
     * Ermittelt die {@link CVlan}s zu dem angegebenen Zeitpunkt {@code when} des Auftrags und generiert daraus die PBit
     * Werte.
     */
    final List<WholesalePbit> findPbits(Auftrag auftrag, LocalDate when) {
        Auftrag2EkpFrameContract auftrag2EkpFrameContract = ekpFrameContractService.findAuftrag2EkpFrameContract(
                auftrag.getAuftragId(), when);
        if (auftrag2EkpFrameContract == null) {
            throw new WholesaleServiceException(WholesaleFehlerCode.ERROR_GET_ORDER_PARAMETERS,
                    "EKP frame contract could not be found!", null, null);
        }
        else if ((auftrag2EkpFrameContract.getEkpFrameContract() != null)
                && (auftrag2EkpFrameContract.getEkpFrameContract().getCvlans() != null)) {
            List<CVlan> cvlans = auftrag2EkpFrameContract.getEkpFrameContract().getCvlans();
            return WholesalePbit.createPbitsFromCVlans(cvlans);
        }
        return Collections.emptyList();
    }
}
