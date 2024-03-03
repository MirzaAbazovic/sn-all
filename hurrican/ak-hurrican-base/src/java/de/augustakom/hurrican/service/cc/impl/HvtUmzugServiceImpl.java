/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2015
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.HvtUmzugDAO;
import de.augustakom.hurrican.exceptions.HvtUmzugException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.DSLAMProfileChangeReason;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzug;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetailView;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugMasterView;
import de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugStatus;
import de.augustakom.hurrican.service.base.exceptions.ExportException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HvtUmzugService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Implementierung von {@link de.augustakom.hurrican.service.cc.HvtUmzugService}
 */
@CcTxRequired
public class HvtUmzugServiceImpl extends DefaultCCService implements HvtUmzugService {

    static final String RANGIERUNG_LOCK_MESSAGE = "Gesperrt wg. HVT-Umzug; reserviert fuer Auftrag %s";
    static final String EXECUTION_NOT_POSSIBLE = "Der angegebene HVT-Umzug befindet sich nicht in einem Status, in dem er ausgeführt werden kann!";
    static final int COL_LSZ = 2;
    static final int COL_ORD_NR = 3;
    static final int COL_ONKZ_A = 4;
    static final int COL_ONKZ_B = 5;
    static final int COL_LBZ = 6;
    public static final int COL_UEVT_STIFT_ALT = 8;
    static final int COL_UVT = 11;
    static final int COL_EVS_NR = 12;
    static final int COL_DOPPELADER = 13;
    static final int COL_BEMERKUNG = 14;
    public static final int COL_CARRIER_ID = 18;
    private static final Logger LOGGER = Logger.getLogger(HvtUmzugServiceImpl.class);
    @Autowired
    private HvtUmzugDAO hvtUmzugDAO;
    @Autowired
    private HVTService hvtService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EQCrossConnectionService")
    private EQCrossConnectionService ccService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;
    @Resource(name = "de.augustakom.hurrican.service.cc.DSLAMService")
    private DSLAMService dslamService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;

    @Override
    public void matchHurricanOrders4HvtUmzug(HvtUmzug hvtUmzug) throws StoreException {
        try {
            Set<HvtUmzugDetail> currentDetails = hvtUmzug.getHvtUmzugDetails();
            List<Pair<Long, String>> affectedHurricanOrders = loadAffectedHurricanOrdersByHvtUmzug(hvtUmzug);

            for (Pair<Long, String> hurricanOrder : affectedHurricanOrders) {
                if (currentDetails.stream().filter(d -> hurricanOrder.getFirst().equals(d.getAuftragId())).findAny().isPresent()) {
                    // Auftrag bereits einem Detail zugeordnet
                    continue;
                }
                Optional<Pair<Rangierung[], Equipment[]>> dtagPort = loadPhysics(hurricanOrder.getFirst(), hurricanOrder.getSecond());
                if (dtagPort.isPresent() && dtagPort.get().getFirst() != null
                        && dtagPort.get().getSecond() != null && dtagPort.get().getSecond()[1] != null) {
                    Equipment eqIn = dtagPort.get().getSecond()[0];  // ADSL-OUT
                    Equipment eqOut = dtagPort.get().getSecond()[1]; // Uevt Stift (DTAG)

                    String uevtStift = getUevtStiftFormatted(eqOut);
                    Optional<HvtUmzugDetail> matchingDetail = currentDetails.stream()
                            .filter(d -> StringUtils.equals(d.getUevtStiftAlt(), uevtStift))
                            .findFirst();
                    if (!matchingDetail.isPresent()) {
                        matchingDetail = Optional.of(createAdditionalHvtUmzugDetail(
                                hurricanOrder.getFirst(), hurricanOrder.getSecond(), uevtStift));
                        currentDetails.add(matchingDetail.get());
                    }

                    AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(hurricanOrder.getFirst());

                    HvtUmzugDetail umzugDetail = matchingDetail.get();
                    umzugDetail.setAuftragId(hurricanOrder.getFirst());
                    umzugDetail.setAuftragNoOrig(auftragDaten.getAuftragNoOrig());
                    umzugDetail.setProduktId(auftragDaten.getProdId());
                    umzugDetail.setEndstellenTyp(hurricanOrder.getSecond());
                    umzugDetail.setManualCc((eqIn != null) ? eqIn.getManualConfiguration() : null);
                }
            }

            hvtUmzugDAO.store(hvtUmzug);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(String.format(
                    "Bei der Auftragsermittlung bzw -zuordnung zum HVT-Umzug ist ein Fehler aufgetreten: %s",
                    e.getMessage()), e);
        }
    }

    String getUevtStiftFormatted(Equipment equipment) {
        String rangVerteiler = equipment.getRangVerteiler();
        String leiste1 = StringUtils.removeStart(equipment.getRangLeiste1(), "0");
        // DTAG erwartet fuer '100' die '00' - in diesem fall die fuehrenden nullen nicht entfernen
        String stift1 = "00".equals(Equipment.getDtagValue4Port(equipment.getRangStift1()))
                ? "00"
                : StringUtils.removeStart(Equipment.getDtagValue4Port(equipment.getRangStift1()), "0");

        String uevtFormatted = String.format("%s-%s-%s",
                rangVerteiler != null ? rangVerteiler : "XXXX",
                leiste1 != null ? leiste1 : "XX",
                stift1 != null ? stift1 : "XX");
        return StringUtils.substring(uevtFormatted, 0, 10);
    }

    /**
     * Ermittelt alle (aktiven) Hurrican-Auftraege, die an GeoIds geschalten sind, die von dem HVT-Umzug betroffen
     * sind.
     *
     * @param hvtUmzug der HVT-Umzug
     * @return Liste mit Pairs aus Auftrags-Id und Endstellen-Typ
     */
    List<Pair<Long, String>> loadAffectedHurricanOrdersByHvtUmzug(HvtUmzug hvtUmzug) {
        return hvtUmzugDAO.findAuftraegeAndEsTypForHvtUmzug(hvtUmzug.getId());
    }

    /**
     * Ermittelt zu einem Auftrag und der Endstelle die Rangierungen und zur ersten Rangierung die Ports(Equipment).
     *
     * @return Rangierungen: [0] = erste Rangierung, [1] = Rangierung Add; Equipment: [0] = EQ_IN (ADSL-OUT), [1] =
     * EQ_OUT(UeVT)
     */
    Optional<Pair<Rangierung[], Equipment[]>> loadPhysics(Long auftragId, String endstellenTyp) throws FindException {
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, endstellenTyp);
        if (endstelle != null) {
            Rangierung[] rangierungen = new Rangierung[2];
            Equipment[] ports = new Equipment[2];
            if (endstelle.getRangierId() != null) {
                rangierungen[0] = rangierungsService.findRangierung(endstelle.getRangierId());
                ports[0] = (rangierungen[0].getEqInId() != null)
                        ? rangierungsService.findEquipment(rangierungen[0].getEqInId()) : null;
                ports[1] = (rangierungen[0].getEqOutId() != null)
                        ? rangierungsService.findEquipment(rangierungen[0].getEqOutId()) : null;
            }
            if (endstelle.getRangierIdAdditional() != null) {
                rangierungen[1] = rangierungsService.findRangierung(endstelle.getRangierIdAdditional());
            }
            return Optional.of(Pair.create(rangierungen, ports));
        }
        return Optional.empty();
    }

    @Override
    public HvtUmzug createHvtUmzug(HvtUmzug hvtUmzug) throws StoreException {
        if (hvtUmzug == null || hvtUmzug.getHvtStandort() == null || hvtUmzug.getKvzNr() == null) {
            throw new StoreException("HVT-Umzug konnte nicht erzeugt werden, die Felder 'HVT-Standort' und 'KVZ-Nr.' dürfen nicht leer sein");
        }
        if (HvtUmzugStatus.OFFEN.equals(hvtUmzug.getStatus())) {
            try {
                final HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(hvtUmzug.getHvtStandort());
                String comment = String.format("HVT-Umzug für '%s', KVZ '%s'", hvtGruppe.getOrtsteil(), hvtUmzug.getKvzNr());
                KvzSperre kvzSperre = hvtService.createKvzSperre(hvtUmzug.getHvtStandort(), hvtUmzug.getKvzNr(), comment);
                hvtUmzug.setKvzSperre(kvzSperre);
            }
            catch (FindException e) {
                throw new StoreException("HVT-Umzug konnte nicht gespeichert werden, weil keine KVZ-Sperre erzeugt werden konnte!", e);
            }
        }
        return hvtUmzugDAO.store(hvtUmzug);
    }

    @Override
    public HvtUmzug saveHvtUmzug(HvtUmzug hvtUmzug) {
        return hvtUmzugDAO.store(hvtUmzug);
    }

    @NotNull
    @Override
    public List<HvtUmzugMasterView> loadHvtMasterData(HvtUmzugStatus... hvtUmzugStatuses) {
        return hvtUmzugDAO.findHvtUmzuegeWithStatus(hvtUmzugStatuses).stream()
                .map(hvtUmzug ->
                                new HvtUmzugMasterView(
                                        hvtUmzug,
                                        getStandortName(hvtUmzug.getHvtStandort()),
                                        getStandortName(hvtUmzug.getHvtStandortDestination())
                                )
                )
                .collect(Collectors.toList());
    }

    private String getStandortName(final Long hvtStandortId) {
        try {
            final HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(hvtStandortId);
            return hvtGruppe == null ? null : hvtGruppe.getOrtsteil();
        }
        catch (Exception e) {
            LOGGER.error("unable to resolve HVT detail information for HVT-Id: " + hvtStandortId, e);
        }
        return null;
    }

    @NotNull
    @Override
    public List<HvtUmzugDetailView> loadHvtUmzugDetailData(Long hvtUmzugId) throws FindException {
        final HvtUmzug hvtUmzug = findById(hvtUmzugId);
        if (hvtUmzug != null) {
            final Map<Long, String> auftragStati =
                    auftragService.findAuftragStati()
                            .stream()
                            .collect(Collectors.toMap(AuftragStatus::getId, AuftragStatus::getStatusText));
            final Map<Long, String> produkte =
                    produktService.findProdukte(false)
                            .stream()
                            .collect(Collectors.toMap(Produkt::getProdId, Produkt::getAnschlussart));

            return hvtUmzug.getHvtUmzugDetails()
                    .parallelStream() //use parallel stream get more performance
                    .filter(Objects::nonNull)
                    .map(detail -> new HvtUmzugDetailView().withHvtUmzugDetail(hvtUmzug, detail))  // map basic data from detail
                    .map(view -> enrichHvtUmzugDetailView(view, auftragStati, produkte))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Nonnull
    private HvtUmzugDetailView enrichHvtUmzugDetailView(@Nonnull final HvtUmzugDetailView hvtUmzugDetailView,
            @Nonnull final Map<Long, String> auftragStati, @Nonnull final Map<Long, String> produkts) {
        //try to enrich some more data if possible
        if (hvtUmzugDetailView.getAuftragId() == null) { return hvtUmzugDetailView;}
        try {
            final AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(hvtUmzugDetailView.getAuftragId());
            final CPSProvisioningAllowed cpsProvisioningAllowed = cpsService.isCPSProvisioningAllowed(hvtUmzugDetailView.getAuftragId(),
                    CPSServiceOrderData.LazyInitMode.noInitialLoad, true, true, true);

            String cpsStatus = null;
            if (hvtUmzugDetailView.getCpsTxId() != null) {
                CPSTransaction cpsTx = cpsService.findCPSTransactionById(hvtUmzugDetailView.getCpsTxId());
                Reference cpsStatusRef = referenceService.findReference(cpsTx.getTxState());
                cpsStatus = (cpsStatusRef != null) ? cpsStatusRef.getStrValue() : null;
            }

            final VerbindungsBezeichnung vbz =
                    physikService.findVerbindungsBezeichnungByAuftragId(hvtUmzugDetailView.getAuftragId());

            return hvtUmzugDetailView
                    .withAuftragStatus(auftragStati.get(auftragDaten.getAuftragStatusId()))
                    .withProdukt(produkts.get(auftragDaten.getProdId()))
                    .withCpsAllowed(cpsProvisioningAllowed.isProvisioningAllowed())
                    .withCpsStatus(cpsStatus)
                    .withVbz((vbz != null) ? vbz.getVbz() : null);
        }
        catch (FindException e) {
            LOGGER.error("error during enrich data for HVT-Umzug-Details Id: " + hvtUmzugDetailView.getId(), e);
            //still try to generate as much data as possible
            final String NO_DATA_FOUND = "NO AUFTRAG DATA FOUND";
            return hvtUmzugDetailView
                    .withAuftragStatus(NO_DATA_FOUND)
                    .withProdukt(NO_DATA_FOUND);
        }
    }

    @Override
    public HvtUmzug findById(Long id) {
        return hvtUmzugDAO.findById(id, HvtUmzug.class);
    }

    @Override
    public List<HvtUmzug> findForCurrentDay() {
        List<HvtUmzug> hvtUmzugList = hvtUmzugDAO.findHvtUmzuegeWithStatus(HvtUmzugStatus.PLANUNG_VOLLSTAENDIG);
        return hvtUmzugList.stream().filter(h -> LocalDate.now().equals(h.getSchalttag()))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Long> findAffectedStandorte4Umzug() {
        return hvtUmzugDAO.findAffectedStandorte4UmzugWithoutStatus(HvtUmzugStatus.DEAKTIVIERT);
    }

    @Override
    public Set<Long> findKvz4UmzugWithStatusUmgezogen(HVTStandort standort, String kvzNr) {
        return hvtUmzugDAO.findKvz4HvtUmzugWithStatusUmgezogen(standort, kvzNr);
    }

    @Override
    public HvtUmzugDetail findDetailById(Long id) {
        return hvtUmzugDAO.findById(id, HvtUmzugDetail.class);
    }

    private HvtUmzugDetail createAdditionalHvtUmzugDetail(Long auftragId, String esTyp, String uevtStift) throws FindException {
        AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(auftragId);
        Endstelle endstelle = endstellenService.findEndstelle4Auftrag(auftragId, esTyp);
        Carrierbestellung lastCb = (endstelle != null) ? carrierService.findLastCB4Endstelle(endstelle.getId()) : null;

        HvtUmzugDetail newDetail = new HvtUmzugDetail();
        newDetail.setAdditionalOrder(Boolean.TRUE);
        newDetail.setAuftragId(auftragId);
        newDetail.setAuftragNoOrig(auftragDaten.getAuftragNoOrig());
        newDetail.setEndstellenTyp(esTyp);
        newDetail.setUevtStiftAlt(uevtStift);
        newDetail.setProduktId(auftragDaten.getProdId());
        if (lastCb != null) {
            Date witaDate = (lastCb.getBereitstellungAm() != null) ? lastCb.getBereitstellungAm() : lastCb.getVorgabedatum();
            newDetail.setWitaBereitstellungAm((witaDate != null) ? DateConverterUtils.asLocalDate(witaDate) : null);
            newDetail.setLbz(lastCb.getLbz());
        }
        return newDetail;
    }

    @Override
    public HvtUmzug disableUmzug(final Long id) {
        final Optional<HvtUmzug> umzug = Optional.ofNullable(findById(id));
        return umzug.filter(HvtUmzug::isDisableAllowed)
                .map(u -> {
                    u.setStatus(HvtUmzugStatus.DEAKTIVIERT);
                    hvtService.deleteKvzSperre(u.getKvzSperre().getId());
                    u.setKvzSperre(null);
                    unlockRangierungen(u);
                    return saveHvtUmzug(u);
                })
                .orElse(null);
    }

    @Override
    public void deleteHvtUmzugDetail(@Nonnull HvtUmzugDetail toDelete) {
        unlockRangierung(toDelete, false);
        hvtUmzugDAO.deleteHvtUmzugDetail(toDelete);
    }

    @Override
    public AKWarnings manuellePortPlanung(Long hvtUmzugId, Long hvtUmzugDetailId, Long rangierId, boolean isDefault, boolean isNew) {
        final AKWarnings warnings = new AKWarnings();
        HvtUmzugDetail hvtUmzugDetail = null;

        try {
            hvtUmzugDetail = findDetailById(hvtUmzugDetailId);
            if (hvtUmzugDetail == null) {
                warnings.addAKWarning(null, "Fehler bei Rangierungs-Zuordnung. "
                        + "Die Daten zum Umzug Detail konnten nicht ermittelt werden!");
                return warnings;
            }

            final HvtUmzug umzug = findById(hvtUmzugId);
            if (umzug == null) {
                warnings.addAKWarning(null, "Fehler bei Rangierungs-Zuordnung. "
                        + String.format("Die Daten zum Umzug mit Id=%s konnten nicht ermittelt werden!", hvtUmzugId));
                return warnings;
            }

            if (isDefault && !manuellePortplanungAllowed(hvtUmzugId, hvtUmzugDetailId)) {
                warnings.addAKWarning(null, "eine manuelle Portplanung kann nur im Status 'offen' durchgefuehrt werden!");
                return warnings;
            }

            if (!verifySchalttagAfterWitaBereitstellung(hvtUmzugDetail, umzug)) {
                warnings.addAKWarning(null, String.format(
                        "Fehler bei Rangierungs-Zuordnung. Der Wita Bereitstellung liegt nach dem geplanten Schalttag des KVZ!%n"
                                + "WITA Vorgang muss storniert und nach Umchaltung vom KVZ neuer WITA Vorgang ausgelöst werden!"));
                return warnings;
            }

            if (isNew) {
                hvtUmzugDetail.setRangNeuErzeugt(true);
            }

            Rangierung rangierung = rangierungsService.findRangierung(rangierId);
            lockRangierung(rangierung, hvtUmzugDetail);

            if (isDefault) {
                transferUetv(rangierung, hvtUmzugDetail);
            }

            if (hvtUmzugDetail.getRangierIdNeu() == null && isDefault) {
                attachPorts(hvtUmzugDetail, rangierung, null);
            }

            if (hvtUmzugDetail.getRangierAddIdNeu() == null && !isDefault) {
                attachPorts(hvtUmzugDetail, null, rangierung);
            }
        }
        catch (Exception e) {
            if (hvtUmzugDetail != null) {
                warnings.addAKWarning(null, String.format(
                        "Fehler bei Rangierungs-Zuordnung fuer Auftrag %s (Endstelle %s): %s",
                        hvtUmzugDetail.getAuftragId(), hvtUmzugDetail.getEndstellenTyp(), e.getMessage()));
            }
            else {
                warnings.addAKWarning(null, String.format("Fehler bei Rangierungs-Zuordnung. "
                        + "Die Daten zum Umzug Detail konnten nicht ermittelt werden! %s", e.getMessage()));
            }
        }

        return warnings;
    }

    @Override
    public boolean manuellePortplanungAllowed(Long umzugId, Long umzugDetailId) {
        if (umzugId == null || umzugDetailId == null) {
            return false;
        }
        HvtUmzug hvtUmzug = findById(umzugId);
        HvtUmzugDetail hvtUmzugDetail = findDetailById(umzugDetailId);
        return hvtUmzug != null && hvtUmzugDetail != null && hvtUmzug.getStatus() == HvtUmzugStatus.OFFEN
                && hvtUmzugDetail.getAuftragId() != null;
    }

    @Override
    public AKWarnings automatischePortPlanung(Long hvtUmzugId) {
        AKWarnings warnings = new AKWarnings();
        HvtUmzug hvtUmzug = findById(hvtUmzugId);
        if (hvtUmzug == null) {
            warnings.addAKWarning(null, "Die Daten zum Umzug konnten nicht ermittelt werden!");
            return warnings;
        }
        if (hvtUmzug.getHvtUmzugDetails() == null) {
            warnings.addAKWarning(null, "Zum Umzug liegen keine Aufträge vor!");
            return warnings;
        }
        if (!hvtUmzug.isAutomatischePortplanungAllowed()) {
            warnings.addAKWarning(null, "eine automatische Portplanung kann nur im Status 'offen' durchgeführt werden!");
            return warnings;
        }

        for (HvtUmzugDetail detail : hvtUmzug.getHvtUmzugDetails()) {
            try {
                if (detail.getRangierIdNeu() == null) {
                    if (verifySchalttagAfterWitaBereitstellung(detail, hvtUmzug)) {
                        // Achtung: Aufruf zur Port-Suche ueber eigenen Tx-Proxy, obwohl gleicher Service; ist notwendig,
                        // da ansonsten die CcTxRequiresNew auf 'reserveRangierung4HvtUmzugDetail' nicht beruecksichtigt wird!
                        // (Ohne den Umweg ueber den Proxy wuerde die Methode in CcTxRequired statt CcTxRequiresNew
                        // ausgefuehrt werden! Limitation of Spring...)
                        HvtUmzugService txProxy = getCCServiceRE(HvtUmzugService.class);
                        txProxy.reserveRangierung4HvtUmzugDetail(hvtUmzug, detail);
                    }
                    else {
                        warnings.addAKWarning(detail, String.format(
                                "Fehler bei Rangierungs-Zuordnung für Auftrag %s (Endstelle %s): %s",
                                detail.getAuftragId(), detail.getEndstellenTyp(), "Das Datum der Portplanung liegt nicht vor dem Datum der Wita Bereitstellung!"));
                    }
                }
            }
            catch (Exception e) {
                warnings.addAKWarning(detail, String.format(
                        "Fehler bei Rangierungs-Zuordnung für Auftrag %s (Endstelle %s): %s",
                        detail.getAuftragId(), detail.getEndstellenTyp(), e.getMessage()));
            }
        }
        saveHvtUmzug(hvtUmzug);

        return warnings;
    }

    /**
     * Checks that HvtUmzug Schalttag is after Wita Bereitstellung. In case no Wita Bereitstellung is availabe the check
     * is positive.
     *
     * @param detail
     * @param hvtUmzug
     * @return
     */
    private boolean verifySchalttagAfterWitaBereitstellung(HvtUmzugDetail detail, HvtUmzug hvtUmzug) {
        if (detail.getWitaBereitstellungAm() != null) {
            // Wita Bereitstellung muss vor dem HvtUmzug Schalttag liegen
            return detail.getWitaBereitstellungAm().isBefore(hvtUmzug.getSchalttag());
        }
        else {
            // Keine Wita Bereitstellung verfuegbar also ist Check positiv
            return true;
        }
    }

    /**
     * Ermittelt alle neuen Rangierungen, die in der Planung hinterlegt sind und entsperrt diese.
     */
    void unlockRangierungen(final HvtUmzug hvtUmzug) {
        hvtUmzug.getHvtUmzugDetails().stream()
                .forEach(d -> unlockRangierung(d, false));
    }


    @Override
    public void unlockRangierung(final HvtUmzugDetail hvtUmzugDetail, boolean removeFromDetail) {
        try {
            Set<Long> rangierIds = hvtUmzugDetail.getRangierIdsNeu();

            for (Long rangierId : rangierIds) {
                Rangierung toUnlock = rangierungsService.findRangierung(rangierId);
                if (BooleanTools.nullToFalse(hvtUmzugDetail.getRangNeuErzeugt())) {
                    // Rangierung komplett deaktivieren
                    toUnlock.setFreigegeben(Rangierung.Freigegeben.deactivated);
                    toUnlock.setGueltigBis(Date.from(ZonedDateTime.now().toInstant()));
                    rangierungsService.saveRangierung(toUnlock, false);

                    rangierungsService.loadEquipments(toUnlock);
                    setEquipmentToStatusFrei((toUnlock.getEquipmentIn() != null)
                            ? Optional.of(toUnlock.getEquipmentIn()) : Optional.empty());
                    setEquipmentToStatusFrei((toUnlock.getEquipmentOut() != null)
                            ? Optional.of(toUnlock.getEquipmentOut()) : Optional.empty());
                }
                else {
                    // Rangierung freigeben
                    toUnlock.setFreigegeben(Rangierung.Freigegeben.freigegeben);
                    toUnlock.setBemerkung(null);
                    rangierungsService.saveRangierung(toUnlock, false);
                }
            }

            if (removeFromDetail) {
                hvtUmzugDetail.setRangierIdNeu(null);
                hvtUmzugDetail.setRangierAddIdNeu(null);
                hvtUmzugDetail.setUevtStiftNeu(null);
                hvtUmzugDAO.store(hvtUmzugDetail);
            }
        }
        catch (Exception e) {
            throw new HvtUmzugException(String.format(
                    "Fehler beim Entsperren der Rangierung zum zu Auftrag %s", hvtUmzugDetail.getAuftragId()), e);
        }
    }

    void setEquipmentToStatusFrei(Optional<Equipment> toChange) throws StoreException {
        if (toChange.isPresent()) {
            toChange.get().setStatus(EqStatus.frei);
            rangierungsService.saveEquipment(toChange.get());
        }
    }

    /**
     * Sperrt die angegebene Rangierung, um eine weitere Vergabe zu verhindern.
     */
    void lockRangierung(final Rangierung toLock, final HvtUmzugDetail hvtUmzugDetail) throws StoreException {
        if (toLock == null) {
            return;
        }

        try {
            toLock.setFreigegeben(Rangierung.Freigegeben.gesperrt);
            toLock.setBemerkung(String.format(
                    RANGIERUNG_LOCK_MESSAGE, hvtUmzugDetail.getAuftragId()));
            rangierungsService.saveRangierung(toLock, false);
        }
        catch (Exception e) {
            throw new StoreException(String.format("Fehler beim Sperren der Rangierung %s", toLock.getId()), e);
        }
    }


    /**
     * Uebernimmt das Uebertragungsverfahren (=UETV) des alten DTAG-Ports auf den DTAG-Port der neuen Rangierung.
     */
    void transferUetv(final Rangierung toLock, final HvtUmzugDetail hvtUmzugDetail) throws StoreException {
        try {
            if (toLock != null && toLock.getEqOutId() != null) {
                Endstelle endstelle = endstellenService.findEndstelle4Auftrag(
                        hvtUmzugDetail.getAuftragId(), hvtUmzugDetail.getEndstellenTyp());
                if (endstelle != null && endstelle.getRangierId() != null) {
                    Rangierung rangierungOld = rangierungsService.findRangierung(endstelle.getRangierId());
                    Equipment eqOutOld = (rangierungOld != null && rangierungOld.getEqOutId() != null)
                            ? rangierungsService.findEquipment(rangierungOld.getEqOutId())
                            : null;

                    if (eqOutOld != null && eqOutOld.isManagedByDtag()) {
                        Equipment eqOutNew = rangierungsService.findEquipment(toLock.getEqOutId());
                        eqOutNew.setUetv(eqOutOld.getUetv());
                        rangierungsService.saveEquipment(eqOutNew);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new StoreException(
                    String.format("Fehler beim Übertragen des UETVs der Rangierung %s", toLock.getId()), e);
        }
    }


    @Override
    @CcTxRequiresNew
    public void reserveRangierung4HvtUmzugDetail(final HvtUmzug hvtUmzug,
            final HvtUmzugDetail hvtUmzugDetail) throws StoreException, FindException {
        if (allowedForAutomaticPortAssignment(hvtUmzugDetail.getAuftragId())) {
            Rangierung[] portPlanningResult = findAndLockNewRangierung(hvtUmzug, hvtUmzugDetail);
            attachPorts(hvtUmzugDetail, portPlanningResult);
        }
    }

    private void attachPorts(final HvtUmzugDetail hvtUmzugDetail, final Rangierung... rangierungenNeu) throws FindException {
        if (rangierungenNeu != null) {
            if (rangierungenNeu.length >= 1 && rangierungenNeu[0] != null) {
                hvtUmzugDetail.setRangierIdNeu(rangierungenNeu[0].getId());
                Equipment uevtStiftNeu = rangierungsService.findEquipment(rangierungenNeu[0].getEqOutId());
                if (uevtStiftNeu != null) {
                    hvtUmzugDetail.setUevtStiftNeu(getUevtStiftFormatted(uevtStiftNeu));
                }
            }
            if (rangierungenNeu.length >= 2 && rangierungenNeu[1] != null) {
                hvtUmzugDetail.setRangierAddIdNeu(rangierungenNeu[1].getId());
            }
        }
    }

    /**
     * Ermittelt an dem Ziel-Standort eine neue Rangierung fuer die ueber {@link HvtUmzugDetail}s definierte Endstelle.
     *
     * @return Rangierungen Rangierungen: Index 0 = 1. Rangierung, Index 1 = Zusatz-Rangierung (falls vorhanden)
     */
    @Nonnull
    Rangierung[] findAndLockNewRangierung(HvtUmzug hvtUmzug, HvtUmzugDetail hvtUmzugDetail) throws FindException, StoreException {
        Endstelle endstelle = endstellenService.findEndstelle4AuftragWithoutExplicitFlush(
                hvtUmzugDetail.getAuftragId(), hvtUmzugDetail.getEndstellenTyp());
        Pair<Rangierung, Rangierung> result =
                rangierungsService.findPossibleRangierung(endstelle.getId(), hvtUmzug.getHvtStandortDestination());

        Rangierung[] rangierungen = new Rangierung[2];
        rangierungen[0] = result.getFirst();
        rangierungen[1] = result.getSecond();

        lockRangierung(result.getFirst(), hvtUmzugDetail);
        transferUetv(result.getFirst(), hvtUmzugDetail);
        lockRangierung(result.getSecond(), hvtUmzugDetail);

        return rangierungen;
    }


    /**
     * Ueberprueft, ob das Produkt fuer eine automatische Port-Planung erlaubt ist (Result=true) oder ob dies manuell
     * (Result=false) geschehen muss.
     * <p>
     * Erlaubt ist die automatische Port-Planung, wenn das Produkt ein Physiktyp-Mapping besitzt und die Produkt-Gruppe
     * weder "Connect*", "Centrex", "Partnerprodukte", "VPN" noch "SDSL" ist.
     */
    boolean allowedForAutomaticPortAssignment(Long auftragId) {
        if (auftragId == null) {
            return false;
        }

        try {
            Produkt produkt = produktService.findProdukt4Auftrag(auftragId);
            ProduktGruppe produktGruppe = produktService.findProduktGruppe(produkt.getProduktGruppeId());

            if (produktGruppe.getProduktGruppe().toLowerCase().startsWith("connect")
                    || produktGruppe.getProduktGruppe().toLowerCase().startsWith("centrex")
                    || produktGruppe.getProduktGruppe().toLowerCase().startsWith("partner")
                    || produktGruppe.getProduktGruppe().toLowerCase().startsWith("vpn")
                    || produktGruppe.getProduktGruppe().toLowerCase().startsWith("sdsl")) {
                return false;
            }

            Optional<List<Produkt2PhysikTyp>> physikTypMappings =
                    Optional.of(physikService.findP2PTs4Produkt(produkt.getProdId(), null));
            return physikTypMappings.isPresent();
        }
        catch (Exception e) {
            return false;
        }
    }


    @Override
    public AKWarnings executePlanning(HvtUmzug hvtUmzug, Long sessionId) {
        if (hvtUmzug == null || !hvtUmzug.isExecutePlanningAllowed()) {
            throw new HvtUmzugException(EXECUTION_NOT_POSSIBLE);
        }

        Set<HvtUmzugDetail> details = hvtUmzug.getHvtUmzugDetails()
                .stream().filter(d -> d.getAuftragId() != null && d.getEndstellenTyp() != null)
                .collect(Collectors.toSet());

        try {
            AKWarnings warnings = validateEndstellen(hvtUmzug, sessionId, details);

            availabilityService.moveKvzLocationsToHvt(
                    hvtUmzug.getKvzSperre(),
                    hvtUmzug.getHvtStandort(),
                    hvtUmzug.getHvtStandortDestination(),
                    sessionId);

            hvtUmzug.setStatus(HvtUmzugStatus.AUSGEFUEHRT);
            saveHvtUmzug(hvtUmzug);

            hvtService.deleteKvzSperre(hvtUmzug.getKvzSperre().getId());
            //Kvz-Sperre muss hier auf 'null' gesetzt werden, da sonst beim flush eine Exception fliegt
            //Hintergrund: das Cascading würde die Sperre hier wieder mitpersisitieren
            hvtUmzug.setKvzSperre(null);

            return warnings;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HvtUmzugException(
                    String.format("Fehler bei der Ausführung des HVT-Umzugs: %s", e.getMessage()), e);
        }
    }

    private AKWarnings validateEndstellen(HvtUmzug hvtUmzug, Long sessionId, Set<HvtUmzugDetail> details) {
        AKWarnings warnings = new AKWarnings();
        for (HvtUmzugDetail detail : details) {
            try {
                Endstelle endstelle = endstellenService.findEndstelle4AuftragNewTx(detail.getAuftragId(), detail.getEndstellenTyp());
                if (endstelle == null) {
                    throw new HvtUmzugException(String.format(
                            "Endstelle %s zu Auftrag %s nicht ermittelbar! Auftrag nicht umgeschrieben!",
                            detail.getEndstellenTyp(), detail.getAuftragId()));
                }
                warnings.addAKWarnings(transferRangierungen(hvtUmzug, detail, endstelle, sessionId));
            }
            catch (Exception e) {
                warnings.addAKWarning(new AKWarning(detail, e.getMessage()));
            }
        }
        return warnings;
    }

    @Nonnull
    @Override
    public AKWarnings sendCpsModifies(HvtUmzug hvtUmzug, boolean simulate, Long sessionId) {
        AKWarnings warnings = new AKWarnings();
        hvtUmzug.getHvtUmzugDetails().stream()
                .filter(d -> d.getAuftragId() != null && d.getUevtStiftNeu() != null && d.getCpsTxId() == null)
                .forEach(hvtUmzugDetail -> {
                    try {
                        Produkt produkt = produktService.findProdukt4Auftrag(hvtUmzugDetail.getAuftragId());
                        if (produkt != null && StringUtils.isNotEmpty(produkt.getCpsProductName())) {
                            // CPS-Tx generieren
                            Long cpsServiceOrderType = (simulate)
                                    ? CPSTransaction.SERVICE_ORDER_TYPE_GET_SODATA
                                    : CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_SUB;

                            CreateCPSTransactionParameter cpsTxParams = new CreateCPSTransactionParameter(
                                    hvtUmzugDetail.getAuftragId(),
                                    null,
                                    cpsServiceOrderType,
                                    CPSTransaction.TX_SOURCE_HURRICAN_ORDER,
                                    CPSTransaction.SERVICE_ORDER_PRIO_DEFAULT,
                                    new Date(),
                                    null, null, null, null, false, false, sessionId);
                            CPSTransactionResult cpsTxResult = cpsService.createCPSTransaction(cpsTxParams);

                            if ((cpsTxResult.getCpsTransactions() != null) && (cpsTxResult.getCpsTransactions().size() == 1)) {
                                CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);

                                if (simulate) {
                                    cpsTx.setTxState(CPSTransaction.TX_STATE_SUCCESS);
                                    cpsService.saveCPSTransaction(cpsTx, sessionId);
                                }
                                else {
                                    // CPS-Tx an CPS senden
                                    cpsService.sendCPSTx2CPS(cpsTx, sessionId);

                                    hvtUmzugDetail.setCpsTxId(cpsTx.getId());
                                    hvtUmzugDAO.store(hvtUmzugDetail);
                                }
                            }

                            if (cpsTxResult.getWarnings() != null && cpsTxResult.getWarnings().isNotEmpty()) {
                                warnings.addAKWarning(hvtUmzugDetail, String.format(
                                        "Es sind CPS Warnungen/Fehler fuer Auftrag %s aufgetreten: %s",
                                        hvtUmzugDetail.getAuftragId(), cpsTxResult.getWarnings().getWarningsAsText()));
                            }
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        warnings.addAKWarning(hvtUmzugDetail, String.format("Fehler bei CPS modifySubscriber fuer Auftrag %s: %s",
                                hvtUmzugDetail.getAuftragId(), e.getMessage()));
                    }
                });

        return warnings;
    }

    @Override
    public Either<AKWarnings, HvtUmzug> closeHvtUmzug(Long hvtUmzugId) {
        final AKWarnings warnings = new AKWarnings();
        final Optional<HvtUmzug> hvtUmzug = Optional.ofNullable(findById(hvtUmzugId));
        if (!hvtUmzug.isPresent()) {
            warnings.addAKWarning(null, String.format("Es wurden keine Umzugsdaten mit der ID %s gefunden", hvtUmzugId));
            return Either.left(warnings);
        }

        if (!hvtUmzug.get().isCloseHvtUmzugAllowed()) {
            final String msg = String.format("Ein Umzug kann nur im Status '%s' geschlossen werden, aktueller Status ist '%s'",
                    HvtUmzugStatus.AUSGEFUEHRT.getDisplayName(), hvtUmzug.get().getStatus().getDisplayName());
            warnings.addAKWarning(null, msg);
            return Either.left(warnings);
        }

        return Either.right(hvtUmzug.map(umzug -> {
            umzug.setStatus(HvtUmzugStatus.BEENDET);
            return saveHvtUmzug(umzug);
        }).get());
    }


    @Override
    public Either<AKWarnings, HvtUmzug> markHvtUmzugAsDefined(Long hvtUmzugId) {
        final AKWarnings warnings = new AKWarnings();
        final Optional<HvtUmzug> hvtUmzug = Optional.ofNullable(findById(hvtUmzugId));
        if (!hvtUmzug.isPresent()) {
            warnings.addAKWarning(null, String.format("Es wurden keine Umzugsdaten mit der ID %s gefunden", hvtUmzugId));
            return Either.left(warnings);
        }

        if (!hvtUmzug.get().isOffen()) {
            final String msg = String.format(
                    "Ein Umzug kann nur im Status '%s' auf '%s' umgestellt werden! Aktueller Status ist '%s'",
                    HvtUmzugStatus.OFFEN.getDisplayName(), HvtUmzugStatus.PLANUNG_VOLLSTAENDIG,
                    hvtUmzug.get().getStatus().getDisplayName());
            warnings.addAKWarning(null, msg);
            return Either.left(warnings);
        }
        else if (hasDetailWithoutNewPlanning(hvtUmzug.get())) {
            warnings.addAKWarning(null,
                    "Der Umzug ist noch nicht vollständig geplant; es existiert min. ein alter Port ohne neue Port-Planung!");
            return Either.left(warnings);
        }

        return Either.right(hvtUmzug.map(umzug -> {
            umzug.setStatus(HvtUmzugStatus.PLANUNG_VOLLSTAENDIG);
            return saveHvtUmzug(umzug);
        }).get());
    }


    /**
     * Prueft, ob es zu dem HvtUmzug min. einen Detail-Datensatz gibt, bei dem zu einem alten Port keine neue Rangierung
     * hinterlegt ist (sofern ein Auftrag zugeordnet ist).
     *
     * @param hvtUmzug
     * @return
     */
    boolean hasDetailWithoutNewPlanning(HvtUmzug hvtUmzug) {
        return hvtUmzug.getHvtUmzugDetails().stream()
                .filter(d -> d.getAuftragId() != null)
                .filter(d -> d.getRangierIdNeu() == null)
                .findFirst()
                .isPresent();
    }


    @Override
    public byte[] exportPortsForHvtUmzug(Long hvtUmzugId) throws FindException, ExportException {
        HvtUmzug hvtUmzug = findById(hvtUmzugId);
        if (hvtUmzug == null) {
            throw new FindException(
                    String.format("Die Daten zum Umzug mit der Id '%s' konnten nicht ermittelt werden!", hvtUmzugId));
        }
        Set<HvtUmzugDetail> currentDetails = hvtUmzug.getHvtUmzugDetails();
        byte[] excelBlob = hvtUmzug.getExcelBlob();
        Workbook workbook = loadExcelFile(excelBlob);
        Sheet sheet = workbook.getSheetAt(0);
        int maxRows = sheet.getLastRowNum();
        for (int rowIndex = 1; rowIndex <= maxRows; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            // skip empty rows ...
            if (XlsPoiTool.isNotEmpty(row, COL_CARRIER_ID)) {
                try {
                    final String uevtStiftAlt = XlsPoiTool.getContentAsString(row, COL_UEVT_STIFT_ALT).trim();
                    final String lbz = XlsPoiTool.getContentAsString(row, COL_CARRIER_ID).trim();

                    //find matching data record with LBZ and uevtStiftALt and set if present the new uevtStiftNeu
                    currentDetails.stream()
                            .filter(hvtUmzugDetail -> !hvtUmzugDetail.getAdditionalOrder())
                            .filter(detail -> lbz.equals(detail.getLbz()) && uevtStiftAlt.equals(detail.getUevtStiftAlt()))
                            .findFirst()
                            .ifPresent(detail -> setUevtStiftNeu(row, detail.getUevtStiftNeu(), true));
                }
                catch (Exception e) {
                    final int xlsRowIndex = rowIndex + 1; // XLS Zeilennummer startet mit 1
                    LOGGER.error("Fehler in Zeile " + xlsRowIndex, e);
                    throw new ExportException(String.format("Fehler in Zeile %d!%nDetails: %s", xlsRowIndex,
                            e.getMessage()));
                }
            }
        }

        //add additional orders
        currentDetails.stream()
                .filter(HvtUmzugDetail::getAdditionalOrder)
                .forEach(o -> createAdditionalOrderEntry(workbook, sheet, o));
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            sheet.getWorkbook().write(bos);
            return bos.toByteArray();
        }
        catch (IOException e) {
            throw new ExportException("Fehler beim Erzeugen des Excel-Sheets", e);
        }
    }

    private Workbook loadExcelFile(byte[] xlsData) throws ExportException {
        try {
            return XlsPoiTool.loadExcelFile(xlsData);
        }
        catch (InvalidFormatException | IOException e) {
            throw new ExportException("Fehler beim Laden der XLS Daten", e);
        }
    }

    void createAdditionalOrderEntry(Workbook workbook, Sheet sheet, HvtUmzugDetail additionalOrder) {
        int destinationRowNum = sheet.getLastRowNum() + 1;
        XlsPoiTool.copyRowWithoutContent(workbook, sheet, sheet.getLastRowNum(), destinationRowNum);
        Row row = sheet.getRow(destinationRowNum);
        Cell cell = row.getCell(COL_UEVT_STIFT_ALT);
        cell.setCellValue(additionalOrder.getUevtStiftAlt());
        cell = row.getCell(COL_LBZ);
        String lbz = additionalOrder.getLbz();
        cell.setCellValue(lbz);
        if (StringUtils.isNotEmpty(lbz)) {
            String[] lbzSplitted = lbz.split("/");
            XlsPoiTool.setContent(row, COL_LSZ, lbzSplitted[0], true);
            XlsPoiTool.setContent(row, COL_ONKZ_A, removeLeadingNulls(lbzSplitted[1]), true);
            XlsPoiTool.setContent(row, COL_ONKZ_B, removeLeadingNulls(lbzSplitted[2]), true);
            XlsPoiTool.setContent(row, COL_ORD_NR, removeLeadingNulls(lbzSplitted[3]), true);
        }
        cell = row.getCell(COL_BEMERKUNG);
        cell.setCellValue("zusätzlicher Auftrag");
        setUevtStiftNeu(row, additionalOrder.getUevtStiftNeu(), true);
    }

    void setUevtStiftNeu(Row row, String uevtStiftNeu, boolean cellExists) {
        if (uevtStiftNeu != null) {
            String[] uevtStiftSplitted = uevtStiftNeu.split("-");
            XlsPoiTool.setContent(row, COL_UVT, uevtStiftSplitted[0], cellExists);
            XlsPoiTool.setContent(row, COL_EVS_NR, removeLeadingNullsUevt(uevtStiftSplitted[1]), cellExists);
            XlsPoiTool.setContent(row, COL_DOPPELADER, removeLeadingNullsUevt(uevtStiftSplitted[2]), cellExists);
        }
        else {
            Cell cell = row.getCell(COL_BEMERKUNG);
            cell.setCellValue("UEVT-Stift nicht zugeordnet");
        }
    }

    private String removeLeadingNulls(String value) {
        if (StringUtils.isNotEmpty(value)) {
            return value.replaceAll("^0*", "");
        }
        return "";
    }

    private String removeLeadingNullsUevt(String value) {
        if (StringUtils.isNotEmpty(value)) {
            // DTAG erwartet fuer '100' die '00' - in diesem fall die fuehrenden nullen nicht entfernen
            return "00".equals(value) ? "00" : value.replaceAll("^0*", "");
        }
        return "";
    }

    // @formatter:off
    /**
     * Schreibt die Rangierungen, Cross Connections und DSLAM Profile aus der Planung um. <br/>
     * Aktionen:
     * <ul>
     *     <li>alte CCs fuer die alten Rangierungen beenden</li>
     *     <li>alte Rangierungen: ES_ID auf NULL</li>
     *     <li>neue Rangierungen: ES_ID setzen und Status auf 'freigegeben'</li>
     *     <li>Endstelle: neue Rangier-IDs eintragen</li>
     *     <li>DSLAM Profil umschreiben</li>
     *     <li>neue CCs fuer neue Rangierungen berechnen</li>
     * </ul>
     * CCs und DSLAM Profile laufen jeweils in eigenen 'Nested Transactions' ab. Das Umschreiben der Rangierung muss
     * durchgeführt werden, weil sonst der User keine Moeglichkeit mehr hat, den Umzug manuell vorzunehmen. Bei CCs und
     * DSLAM Profilen, werden Fehler lediglich geloggt, da der User im Nachgang den Umzug in der GUI bearbeiten kann.
     */
    // @formatter:on
    @Nonnull
    AKWarnings transferRangierungen(@Nonnull final HvtUmzug hvtUmzug, @Nonnull final HvtUmzugDetail detail,
            @Nonnull final Endstelle endstelle, @Nonnull final Long sessionId) {
        AKWarnings warnings = new AKWarnings();
        if (CollectionUtils.isEmpty(detail.getRangierIdsNeu()) || CollectionUtils.isEmpty(endstelle.getRangierIdsAsSet())) {
            throw new HvtUmzugException(String.format(
                    "Es sind keine alten und/oder neuen Rangierungen angegeben. Planung für UEVT Stift %s kann "
                            + "somit nicht durchgeführt werden!", detail.getUevtStiftAlt()));
        }

        try {
            // CCs der alten Physik beenden
            warnings.addAKWarnings(deactivateCcs4EndstelleWithNestedTx(endstelle, detail));

            Rangierung rangierungOrig = rangierungsService.findRangierung(endstelle.getRangierId());
            final Long currentEsId = rangierungOrig.getEsId();
            final Date currentFreigabeAb = rangierungOrig.getFreigabeAb();

            // alte Rangierungen deaktivieren
            unAttachRangierung(endstelle.getRangierId());
            unAttachRangierung(endstelle.getRangierIdAdditional());

            // neue Rangierungen der Endstelle zuordnen und aktivieren
            endstelle.setRangierId(detail.getRangierIdNeu());
            endstelle.setRangierIdAdditional((detail.getRangierAddIdNeu() != null) ? detail.getRangierAddIdNeu() : null);
            endstelle.setHvtIdStandort(hvtUmzug.getHvtStandortDestination());
            endstellenService.saveEndstelle(endstelle);
            endgeraeteService.updateSchicht2Protokoll4Endstelle(endstelle);

            // Endstellen-Id und Freigabe-Datum von alter Rangierung verwenden, um gleichen Status der Rangierung zu erhalten
            activateRangierung4Es(detail.getRangierIdNeu(), currentEsId, currentFreigabeAb);
            activateRangierung4Es(detail.getRangierAddIdNeu(), currentEsId, currentFreigabeAb);

            transferDSLAMProfilWithNestedTx(hvtUmzug, detail, sessionId);

            // CrossConnections berechnen
            warnings.addAKWarnings(calculateDefaultCcsWithNestedTx(hvtUmzug, detail, endstelle, sessionId));
            return warnings;

        }
        catch (Exception e) {
            throw new HvtUmzugException(String.format(
                    "Fehler beim Umschreiben der Rangierung(en) zum UEVT-Stift: %s. %s",
                    detail.getUevtStiftAlt(), e.getMessage()), e);
        }

    }

    @Nonnull
    AKWarnings deactivateCcs4EndstelleWithNestedTx(@Nonnull final Endstelle endstelle,
            @Nonnull final HvtUmzugDetail detail) {
        AKWarnings warnings = new AKWarnings();
        try {
            HvtUmzugService txProxy = getCCServiceRE(HvtUmzugService.class);
            txProxy.deactivateCcs4Endstelle(endstelle);
        }
        catch (Exception e) {
            LOGGER.error(String.format("Fehler beim Löschen der alten CrossConnections für Auftrag %s, HvtUmzugDetail %s", detail.getAuftragId(), detail.getId()), e);
            warnings.addAKWarning(new AKWarning(null, String.format("Fehler beim Löschen der alten Cross Connections "
                    + "zum UEVT-Stift: %s. Grund: %s", detail.getUevtStiftAlt(), e.getMessage())));
        }
        return warnings;
    }

    void transferDSLAMProfilWithNestedTx(@Nonnull final HvtUmzug hvtUmzug, @Nonnull final HvtUmzugDetail detail,
            @Nonnull final Long sessionId) {
        HvtUmzugService txProxy = getCCServiceRE(HvtUmzugService.class);
        txProxy.transferDSLAMProfil(hvtUmzug, detail, sessionId);
    }

    @Nonnull
    AKWarnings calculateDefaultCcsWithNestedTx(@Nonnull final HvtUmzug hvtUmzug, @Nonnull final HvtUmzugDetail detail,
            @Nonnull final Endstelle endstelle, @Nonnull final Long sessionId) {
        AKWarnings warnings = new AKWarnings();
        try {
            HvtUmzugService txProxy = getCCServiceRE(HvtUmzugService.class);
            warnings.addAKWarnings(txProxy.calculateDefaultCcs(hvtUmzug, detail, endstelle, sessionId));
        }
        catch (Exception e) {
            LOGGER.error(String.format("Fehler beim Anlegen der Default-CrossConnections für Auftrag %s, HvtUmzugDetail %s", detail.getAuftragId(), detail.getId()), e);
            warnings.addAKWarning(new AKWarning(null, String.format("Fehler beim anlegen der neuen Default Cross "
                    + "Connections zum UEVT-Stift: %s. Grund: %s", detail.getUevtStiftAlt(), e.getMessage())));
        }
        return warnings;
    }

    @Override
    @CcTxRequiresNew
    public void deactivateCcs4Endstelle(Endstelle endstelle) throws StoreException, FindException {
        ccService.deactivateCrossConnections4Endstelle(endstelle, new Date());
    }

    @Override
    @CcTxRequiresNew
    public void transferDSLAMProfil(HvtUmzug hvtUmzug, HvtUmzugDetail detail, Long sessionId) {
        try {
            DSLAMProfile oldDSLAMProfile = dslamService.findDSLAMProfile4AuftragNoEx(detail.getAuftragId(),
                    Date.from(hvtUmzug.getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()), true);
            if (oldDSLAMProfile != null) {
                Uebertragungsverfahren uetv = null;
                if (oldDSLAMProfile.getUetv() != null) {
                    uetv = Uebertragungsverfahren.getUetvAndAcceptDslamProfileUetvNames(oldDSLAMProfile.getUetv());
                }

                Long dslamBgTypNew = getHwBaugruppenTypEqIn(detail);

                DSLAMProfile newDSLAMProfile = dslamService.findNewDSLAMProfileMatch(
                        dslamBgTypNew,
                        oldDSLAMProfile.getId(), uetv);

                if (newDSLAMProfile != null) {
                    AKUser user = getAKUserBySessionIdSilent(sessionId);
                    dslamService.changeDSLAMProfile(detail.getAuftragId(),
                            newDSLAMProfile.getId(), Date.from(hvtUmzug.getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()), (user != null ? user.getLoginName()
                                    : "unknown"), DSLAMProfileChangeReason.CHANGE_REASON_ID_AUTOMATIC_SYNC, null);
                }
                else {
                    LOGGER.info(String.format("Für Auftrag mit Id %s wurde beim HVT-Umzug %s kein DSLAM-Profilwechsel durchgeführt (Profil: %s, neuer BG-Typ: %s)", detail.getAuftragId(), hvtUmzug.getId(), oldDSLAMProfile.getId(), dslamBgTypNew));
                }
            }
        }
        catch (Exception e) {
            LOGGER.warn(String.format("Fehler beim automatischen Umschreiben des DSLAM Profils für Auftrag %s zum "
                    + "UEVT-Stift: %s. %s", detail.getAuftragId(), detail.getUevtStiftAlt(), e.getMessage()), e);
        }
    }

    @Override
    @Nonnull
    @CcTxRequiresNew
    public AKWarnings calculateDefaultCcs(@Nonnull final HvtUmzug hvtUmzug, @Nonnull final HvtUmzugDetail detail,
            @Nonnull final Endstelle endstelle, @Nonnull final Long sessionId)
            throws FindException, StoreException {
        // eventuell existierende CCs der neuen Physik im Vorfeld beenden
        Pair<Rangierung, Equipment> ccPhysics = ccService.deactivateCrossConnections4Endstelle(endstelle, new Date());
        Equipment eqInPort = ccPhysics.getSecond();
        Produkt produkt = produktService.findProdukt4Auftrag(detail.getAuftragId());

        AKWarnings warnings = new AKWarnings();
        if (eqInPort != null) {
            if (ccService.checkCcsAllowed(endstelle) == null) {
                ccService.defineDefaultCrossConnections4Port(eqInPort, detail.getAuftragId(), Date.from(hvtUmzug.getSchalttag().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        produktService.isVierDrahtProdukt(produkt.getId()), sessionId);
                eqInPort.setManualConfiguration(Boolean.FALSE);
                rangierungsService.saveEquipment(eqInPort);

                if (BooleanTools.nullToFalse(detail.getManualCc())) {
                    //@formatter:off
                    warnings.addAKWarning(null, String.format("Für die neue Physik des Auftrags %s [UEVT-Stift(alt)=%s, "
                            + "Produkt=%s] sind neue Default CrossConnections berechnet worden. Die alte Physik "
                            + "hat allerdings manuell konfigurierte CrossConnections!", detail.getAuftragId(),
                            detail.getUevtStiftAlt(), produkt.getAnschlussart()));
                    //@formatter:on
                }
            }
        }
        else {
            //@formatter:off
            warnings.addAKWarning(null, String.format("Der für die CrossConnection Berechnung nötige Port fehlt "
                    + "[AuftragId=%s, UEVT-Stift(alt)=%s, Produkt=%s]!", detail.getAuftragId(),
                    detail.getUevtStiftAlt(), produkt.getAnschlussart()));
            //@formatter:on
        }
        return warnings;
    }

    void activateRangierung4Es(final Long rangierId, final Long endstelleId, final Date freigabeAb) {
        if (rangierId == null) {
            return;
        }

        try {
            Rangierung rangierung = rangierungsService.findRangierung(rangierId);
            rangierung.setEsId(endstelleId);
            rangierung.setFreigabeAb(freigabeAb);
            rangierung.setFreigegeben(Rangierung.Freigegeben.freigegeben);
            rangierung.setBemerkung(null);
            rangierungsService.saveRangierung(rangierung, false);
        }
        catch (Exception e) {
            throw new HvtUmzugException(String.format(
                    "Fehler bei der Freigabe der Rangierung %s: %s", rangierId, e.getMessage()), e);
        }
    }

    void unAttachRangierung(final Long rangierId) {
        if (rangierId == null) {
            return;
        }

        try {
            Rangierung rangierung = rangierungsService.findRangierung(rangierId);
            rangierung.setEsId(null);
            rangierungsService.saveRangierung(rangierung, false);
        }
        catch (Exception e) {
            throw new HvtUmzugException(String.format(
                    "Fehler bei der Freigabe der Rangierung %s: %s", rangierId, e.getMessage()), e);
        }
    }


    /**
     * Ermittelt von {@link de.augustakom.hurrican.model.cc.hvt.umzug.HvtUmzugDetail#rangierIdNeu} den Baugruppen-Typ
     * vom EQ-IN.
     *
     * @return ID des zugehoerigen Baugruppen-Typs
     */
    Long getHwBaugruppenTypEqIn(@Nonnull HvtUmzugDetail detail) {
        if (detail.getRangierIdNeu() == null) {
            return null;
        }

        try {
            Rangierung rangierung = rangierungsService.findRangierung(detail.getRangierIdNeu());
            Equipment eqIn = (rangierung != null && rangierung.getEqInId() != null)
                    ? rangierungsService.findEquipment(rangierung.getEqInId()) : null;
            HWBaugruppe hwBaugruppe = (eqIn != null && eqIn.getHwBaugruppenId() != null)
                    ? hwService.findBaugruppe(eqIn.getHwBaugruppenId()) : null;

            return (hwBaugruppe != null) ? hwBaugruppe.getHwBaugruppenTyp().getId() : null;
        }
        catch (Exception e) {
            throw new HvtUmzugException(String.format(
                    "Fehler bei der Ermittlung des neuen Baugruppen-Typs zu Auftrag %s: %s",
                    detail.getAuftragId(), e.getMessage()), e);
        }
    }

}
