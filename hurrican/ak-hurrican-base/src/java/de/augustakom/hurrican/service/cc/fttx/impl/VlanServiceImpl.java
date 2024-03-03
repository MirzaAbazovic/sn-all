/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 08:57:20
 */
package de.augustakom.hurrican.service.cc.fttx.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.fttx.VlanDao;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.fttx.Produkt2Cvlan;
import de.augustakom.hurrican.model.cc.fttx.TechLeistung2Cvlan;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.augustakom.hurrican.service.cc.impl.DefaultCCService;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Implementierung von {@link VlanService}
 */
@CcTxRequired
public class VlanServiceImpl extends DefaultCCService implements VlanService {
    private static final Logger LOG = Logger.getLogger(VlanServiceImpl.class);
    private static final String WRONG_RACK_TYP_FOR_VLAN_CALC = "VLAN Berechnung ist fehlgeschlagen, da der Racktyp %s nicht dafür vorgesehen ist";
    private static final String ERROR_VLAN_CALC_FOR_DSLAM = "VLAN Berechnung für Rack '%s' ist fehlgeschlagen, da das S-VLAN des DSLAMs nicht gesetzt ist!";
    private static final String ERROR_VLAN_CALC_FOR_OLT = "VLAN Berechnung für Rack '%s' ist fehlgeschlagen, da die Lfd. Nr. des OLTs nicht gesetzt ist!";
    private static final String RACK_TYPE_FOR_EQVLAN_CALC_NOT_SUPPORTED = "calculateEqVlans() racktyp wird für EqVlan Berechnung nicht unterstützt typ=%s";

    @Autowired
    private VlanDao vlanDao;
    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService")
    private EkpFrameContractService ekpFrameContractService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService ccLeistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;

    private List<Produkt2Cvlan> findProdukt2Cvlans(Long prodId, Long techLocationTypeRefId, Long hvtTechnikId) {
        Produkt2Cvlan example = new Produkt2Cvlan();
        example.setProduktId(prodId);

        List<Produkt2Cvlan> cvlans = vlanDao.queryByExample(example, Produkt2Cvlan.class);
        Map<CvlanServiceTyp, Produkt2Cvlan> cvlanByServiceTyp = new HashMap<>();
        for (Produkt2Cvlan prod2Cvlan : cvlans) {
            if (((prod2Cvlan.getTechLocationTypeRefId() != null) && !prod2Cvlan.getTechLocationTypeRefId().equals(
                    techLocationTypeRefId))
                    || ((prod2Cvlan.getHvtTechnikId() != null) && !prod2Cvlan.getHvtTechnikId().equals(hvtTechnikId))) {
                continue; // passt nicht
            }
            Produkt2Cvlan currentProd2Cvlan = cvlanByServiceTyp.get(prod2Cvlan.getCvlanTyp());
            if ((currentProd2Cvlan == null) || prod2Cvlan.isSubsetOf(currentProd2Cvlan)) {
                cvlanByServiceTyp.put(prod2Cvlan.getCvlanTyp(), prod2Cvlan);
            }
        }
        return new ArrayList<>(cvlanByServiceTyp.values());
    }

    /**
     * Ermittelt die {@link CvlanServiceTyp}es zu einer techn. Leistung.
     *
     * @param techLeistungId ID der techn. Leistung
     * @param removeLogic    siehe TechLeistung2Cvlan#removeLogic
     * @return
     */
    private Set<CvlanServiceTyp> findCvlanServiceTypes4TechLsId(Long techLeistungId, boolean removeLogic) {
        TechLeistung2Cvlan example = new TechLeistung2Cvlan();
        example.setTechLeistungId(techLeistungId);
        example.setRemoveLogic(removeLogic);
        List<TechLeistung2Cvlan> techLeistung2Cvlans = vlanDao.queryByExample(example, TechLeistung2Cvlan.class);

        Set<CvlanServiceTyp> cvlanServiceTypes4TechLs = new HashSet<>();
        for (TechLeistung2Cvlan techLs2Cvlan : techLeistung2Cvlans) {
            cvlanServiceTypes4TechLs.add(techLs2Cvlan.getCvlanTyp());
        }
        return cvlanServiceTypes4TechLs;
    }

    @Override
    public Set<CvlanServiceTyp> getNecessaryCvlanServiceTypes4Auftrag(Long auftragId, Long prodId,
            EkpFrameContract ekpFrameContract, Long techLocationTypeRefId, Long hvtTechnikId, LocalDate when) {
        Set<CvlanServiceTyp> cvlanServiceTypes = new HashSet<>();
        Set<CvlanServiceTyp> possibleCvlans4Produkt = new HashSet<>();

        List<Produkt2Cvlan> produktCvlans = findProdukt2Cvlans(prodId, techLocationTypeRefId, hvtTechnikId);
        for (Produkt2Cvlan produkt2Cvlan : produktCvlans) {
            possibleCvlans4Produkt.add(produkt2Cvlan.getCvlanTyp());
            if (BooleanTools.nullToFalse(produkt2Cvlan.getIsMandatory())) {
                cvlanServiceTypes.add(produkt2Cvlan.getCvlanTyp());
            }
        }

        Set<CvlanServiceTyp> possibleCvlans4TechLs = new HashSet<>();
        Set<CvlanServiceTyp> removeCvlans4TechLs = new HashSet<>();
        List<Auftrag2TechLeistung> assignedTechLs = ccLeistungsService.findAuftrag2TechLeistungen(auftragId, when);
        for (Auftrag2TechLeistung assigned : assignedTechLs) {
            possibleCvlans4TechLs.addAll(findCvlanServiceTypes4TechLsId(assigned.getTechLeistungId(), false));
            removeCvlans4TechLs.addAll(findCvlanServiceTypes4TechLsId(assigned.getTechLeistungId(), true));
        }

        cvlanServiceTypes.addAll(Sets.intersection(possibleCvlans4Produkt, possibleCvlans4TechLs));
        cvlanServiceTypes.removeAll(removeCvlans4TechLs);
        return filterCvlanServiceTypes4EkpFrameContract(cvlanServiceTypes, ekpFrameContract.getCvlanServiceTypes());
    }

    /**
     * Filtert die angegebenen {@link CvlanServiceTyp}es auf die Typen, die dem EKP-Rahmenvertrag zugeordnet sind.
     *
     * @param cvlanServiceTypes
     * @param cvlanServiceTypesForEkp
     * @return
     */
    Set<CvlanServiceTyp> filterCvlanServiceTypes4EkpFrameContract(Set<CvlanServiceTyp> cvlanServiceTypes,
            final Set<CvlanServiceTyp> cvlanServiceTypesForEkp) {
        return Sets.intersection(cvlanServiceTypes, cvlanServiceTypesForEkp);
    }

    @Override
    public List<EqVlan> calculateEqVlans(Long auftragId, Long prodId, LocalDate when) throws FindException {
        EkpFrameContract ekpFrameContract = ekpFrameContractService.findEkp4AuftragOrDefaultMnet(
                auftragId, LocalDate.now(), true);

        if (ekpFrameContract == null) {
            throw new FindException(
                    String.format("EkpFrameContract konnte für den Auftrag %d nicht ermittelt werden", auftragId));
        }

        return calculateEqVlans(ekpFrameContract, auftragId, prodId, when);
    }

    @Override
    public List<EqVlan> calculateEqVlans(final EkpFrameContract ekpFrameContract, final Long auftragId,
            final Long prodId,
            final LocalDate when)
            throws FindException {
        return calculateEqVlans(ekpFrameContract, auftragId, prodId, when, new Function<Long, Equipment>() {
            @Override
            @Nullable
            public Equipment apply(final Long input) {
                try {
                    return findEquipmentOnEndstelleB4Auftrag(auftragId);
                }
                catch (FindException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    private List<EqVlan> calculateEqVlans(EkpFrameContract ekpFrameContract, Long auftragId, Long prodId,
            LocalDate when,
            Function<Long, Equipment> findEqOnEsB)
            throws FindException {
        if (ekpFrameContract == null) {
            throw new FindException(
                    "VLAN Berechnung ist fehlgeschlagen, da der EKP Frame Contract nicht definiert ist!");
        }

        // Equipment, MDU, OLT und A10NSP ermitteln
        Equipment equipmentIn = findEqOnEsB.apply(auftragId);
        HWBaugruppe hwBaugruppe = hwService.findBaugruppe(equipmentIn.getHwBaugruppenId());

        HWRack rack = hwService.findRackById(hwBaugruppe.getRackId());
        final Long techLocationTypeRefId;
        List<EqVlan> eqVlans = new ArrayList<>();

        VlanCalculationParameter vlanCalculationParameter = new VlanCalculationParameter();
        if (Arrays.asList(HWRack.RACK_TYPE_OLT, HWRack.RACK_TYPE_MDU, HWRack.RACK_TYPE_ONT, HWRack.RACK_TYPE_DPO, HWRack.RACK_TYPE_DPU).contains(rack.getRackTyp())) {
            // VLAN for OLT rack
            if (HWRack.RACK_TYPE_OLT.equals(rack.getRackTyp())) {
                final HWOlt olt = (HWOlt) rack; // FTTH Standort (alt, OLT nicht in Hurrican)
                setVlanCalculationParameterForOlt(vlanCalculationParameter, olt,
                        Integer.valueOf(equipmentIn.getHwEQNPart(Equipment.HWEQNPART_FTTX_OLT_GPON_SLOT)));
            }
            // VLAN for MDU, ONT, DPO rack
            else {
                HWOltChild oltChild = (HWOltChild) rack;
                final HWRack oltOrGslam = hwService.findRackById(oltChild.getOltRackId());

                //FTTB/FTTH Standort connected over DSLAM
                if (HWRack.RACK_TYPE_DSLAM.equals(oltOrGslam.getRackTyp())) {
                    final Integer dslamSvlan = ((HWDslam) oltOrGslam).getSvlan();
                    if (dslamSvlan == null) {
                        throw new FindException(String.format(ERROR_VLAN_CALC_FOR_DSLAM, rack.getId()));
                    }
                    setVlanCalculationParameterForDslam(hwBaugruppe, rack, vlanCalculationParameter, (HWDslam) oltOrGslam);
                }
                //FTTB/FTTH Standort connected over OLT
                else if (HWRack.RACK_TYPE_OLT.equals(oltOrGslam.getRackTyp())) {
                    final HWOlt olt = (HWOlt) oltOrGslam;
                    if (olt.getOltNr() == null) {
                        throw new FindException(String.format(ERROR_VLAN_CALC_FOR_OLT, olt.getId()));
                    }
                    setVlanCalculationParameterForOlt(vlanCalculationParameter, olt, oltChild.getOltSlotAsInteger());
                }
                else {
                    throw new FindException(String.format(WRONG_RACK_TYP_FOR_VLAN_CALC, oltOrGslam.getRackTyp()));
                }
            }
        }
        else {
            LOG.debug(String.format(RACK_TYPE_FOR_EQVLAN_CALC_NOT_SUPPORTED, rack.getRackTyp()));
            return eqVlans;
        }

        if (vlanCalculationParameter.vlanAktivAb == null) {
            LOG.info("calculateEqVlans() vlanAktivAb ist null. Keine EqVlans werden berechnet. RackId=" + rack.getId());
            return eqVlans;
        }

        techLocationTypeRefId = hvtService.findHVTStandort(rack.getHvtIdStandort()).getStandortTypRefId();
        A10NspPort a10NspPort = ekpFrameContractService.findA10NspPort(ekpFrameContract, vlanCalculationParameter.oltId);
        if (a10NspPort == null) {
            throw new FindException("VLAN Berechnung ist fehlgeschlagen, da die A10NSP nicht definiert ist!");
        }
        vlanCalculationParameter.a10NspNummer = a10NspPort.getA10Nsp().getNummer();

        LocalDate oltAktivAb = DateConverterUtils.asLocalDate(vlanCalculationParameter.vlanAktivAb);
        LocalDate eqVlanGueltigVon = when.isAfter(oltAktivAb) ? when : oltAktivAb;
        Set<CvlanServiceTyp> cvlanServiceTypes = getNecessaryCvlanServiceTypes4Auftrag(auftragId, prodId,
                ekpFrameContract, techLocationTypeRefId, vlanCalculationParameter.hwProducer, eqVlanGueltigVon);

        for (CvlanServiceTyp cvlanServiceTyp : cvlanServiceTypes) {
            EqVlan eqVlan = new EqVlan();
            eqVlan.setEquipmentId(equipmentIn.getId());
            eqVlan.setCvlanTyp(cvlanServiceTyp);
            CVlan cvlanEkp = ekpFrameContract.getCvlanOfType(cvlanServiceTyp);
            if (cvlanEkp == null) {
                throw new IllegalArgumentException(String.format(
                        "Der CVLAN Typ <%s> konnte nicht berechnet werden, da der EKP diesen Typ nicht definiert!",
                        cvlanServiceTyp.toString()));
            }

            vlanCalculationParameter.calculateSvlans(eqVlan, eqVlanGueltigVon, cvlanEkp, ekpFrameContract.getSvlanFaktor());
            eqVlans.add(eqVlan);
        }
        return eqVlans;
    }

    private void setVlanCalculationParameterForDslam(HWBaugruppe hwBaugruppe, HWRack rack, VlanCalculationParameter vlanCalculationParameter, HWDslam oltOrGslam) {
        vlanCalculationParameter.oltNr = null;
        vlanCalculationParameter.oltId = null;
        vlanCalculationParameter.oltSvlanOffset = null;
        vlanCalculationParameter.hwProducer = rack.getHwProducer();
        vlanCalculationParameter.vlanAktivAb = (hwBaugruppe.getEingebaut()) ? rack.getGueltigVon() : null;
        vlanCalculationParameter.svlanCalculatorStrategy = VlanCalculatorStrategy.get((HWDslam) oltOrGslam);
    }

    private void setVlanCalculationParameterForOlt(VlanCalculationParameter vlanCalculationParameter, HWOlt olt, Integer oltSlot) {
        vlanCalculationParameter.oltSlot = oltSlot;
        vlanCalculationParameter.svlanCalculatorStrategy = VlanCalculatorStrategy.get(olt);
        vlanCalculationParameter.oltId = olt.getId();
        vlanCalculationParameter.oltNr = olt.getOltNr();
        vlanCalculationParameter.oltSvlanOffset = olt.getSvlanOffset();
        vlanCalculationParameter.vlanAktivAb = olt.getVlanAktivAb();
        vlanCalculationParameter.hwProducer = olt.getHwProducer();
    }

    private Equipment findEquipmentOnEndstelleB4Auftrag(Long auftragId) throws FindException {
        Equipment equipmentIn = null;
        Rangierung[] rangierungen = rangierungsService.findRangierungenTx(auftragId, Endstelle.ENDSTELLEN_TYP_B);
        if ((rangierungen != null) && (rangierungen.length > 0)) {
            equipmentIn = rangierungsService.findEquipment(rangierungen[0].getEqInId());
        }
        if (equipmentIn == null) {
            throw new FindException("No Equipment found for Auftrag!");
        }
        return equipmentIn;
    }

    @Override
    public List<EqVlan> findEqVlans(Long equipmentId) {
        return vlanDao.findEqVlans(equipmentId);
    }

    @Override
    public List<EqVlan> findEqVlans(Long equipmentId, Date validDate) {
        return vlanDao.findEqVlans(equipmentId, validDate);
    }

    @Override
    public List<EqVlan> addEqVlans(List<EqVlan> eqVlans, @Nullable AuftragAktion auftragAktion) throws StoreException {
        if (eqVlans.isEmpty()) {
            return ImmutableList.of();
        }
        Long auftragAktionsId = (auftragAktion != null) ? auftragAktion.getId() : null;
        Long equipmentId = eqVlans.get(0).getEquipmentId();
        Date validDate = eqVlans.get(0).getGueltigVon();
        for (EqVlan eqVlan : eqVlans) {
            if (!equipmentId.equals(eqVlan.getEquipmentId())
                    || !DateTools.isDateEqual(validDate, eqVlan.getGueltigVon())) {
                LOG.error("addEqVlans() alle EqVlan müssen identische equipmentId und Gültigkeit haben");
                throw new StoreException(StoreException.UNABLE_TO_CREATE_HISTORY);
            }
        }
        List<EqVlan> oldEqVlans = vlanDao.findEqVlansForFuture(equipmentId, validDate);
        if (equalsEqVlansAndValid(oldEqVlans, eqVlans, validDate)) {
            return oldEqVlans;
        }

        // alte Einträge historisieren und neue speichern
        for (EqVlan oldEqVlan : oldEqVlans) {
            oldEqVlan.setGueltigBis(validDate);
            oldEqVlan.setAuftragAktionsIdRemove(auftragAktionsId);
            vlanDao.store(oldEqVlan);
        }
        List<EqVlan> storedEqVlans = new ArrayList<>();
        for (EqVlan eqVlan : eqVlans) {
            eqVlan.setAuftragAktionsIdAdd(auftragAktionsId);
            storedEqVlans.add(vlanDao.store(eqVlan));
        }
        return storedEqVlans;
    }

    /**
     * Prueft ob der Startzeitpunkt der VLANs vor dem Aktivierungszeitpunkt der OLT liegt. Wenn ja, fliegt eine
     * Exception weil das natuerlich nicht sein darf. Sollte fuer Wholesale Auftraege eigentlich gar nicht passieren.
     * Betrifft Retail Auftraege nicht.
     */
    void checkOltVlanAktivAb(Equipment equipment, LocalDate newValidFrom) throws FindException {
        HWBaugruppe hwBaugruppe = (equipment != null) ? hwService.findBaugruppe(equipment.getHwBaugruppenId()) : null;
        HWRack rack = (hwBaugruppe != null) ? hwService.findRackById(hwBaugruppe.getRackId()) : null;
        HWOlt olt = null;
        if (rack != null) {
            if (HWRack.RACK_TYPE_OLT.equals(rack.getRackTyp())) {
                olt = (HWOlt) rack;
            }
            else if (HWRack.RACK_TYPE_MDU.equals(rack.getRackTyp())
                    || HWRack.RACK_TYPE_ONT.equals(rack.getRackTyp())
                    || HWRack.RACK_TYPE_DPO.equals(rack.getRackTyp())
                    || HWRack.RACK_TYPE_DPU.equals(rack.getRackTyp())) {
                HWOltChild oltChild = (HWOltChild) rack;
                olt = (HWOlt) hwService.findRackById(oltChild.getOltRackId());
            }
        }
        if (olt == null) {
            throw new FindException("Es konnte keine OLT ermittelt werden!");
        }
        else if ((olt.getVlanAktivAb() == null) || newValidFrom.isBefore(DateConverterUtils.asLocalDate(olt.getVlanAktivAb()))) {
            throw new FindException(
                    "VLAN Berechnung für OLT nicht aktiv oder Startzeitpunkt der VLANs liegen vor dem OLT Datum!");
        }
    }

    @Override
    public List<EqVlan> moveEqVlans4Auftrag(Long auftragId, LocalDate originalDate, LocalDate newValidFrom,
            @Nullable AuftragAktion auftragAktion) throws StoreException, FindException {
        Date originalGueltigVon = Date.from(originalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Equipment equipment = findEquipmentOnEndstelleB4Auftrag(auftragId);
        if (findEqVlanByGueltigVon(originalGueltigVon, equipment.getId()).isEmpty()) {
            LOG.info(String.format("moveEqVlans4Auftrag(), keine EqVlans gefunden zu gueltigVon=%s, EquipmentId=%s",
                    originalGueltigVon, equipment.getId()));
            List<EqVlan> eqVlans4Auftrag = findEqVlans4Auftrag(auftragId, originalDate);
            LocalDate currentGueltigVon = DateConverterUtils.asLocalDate(eqVlans4Auftrag.get(0).getGueltigVon());
            if (newValidFrom.isBefore(currentGueltigVon)) {
                // Sonderfall: verschieben vor Beginn der akt. Gueltigkeit, wobei es zum originalDate zu keiner
                // Neuberechnung kam (weil bereits EqVlans vorhanden waren).
                // => Evtl. muss die Gültigkeit angepasst werden
                LOG.info("moveEqVlans4Auftrag(), verschieben in Vergangenheit, gueltigVon anpassen für: "
                        + eqVlans4Auftrag);
                originalGueltigVon = eqVlans4Auftrag.get(0).getGueltigVon();
            }
            else {
                return eqVlans4Auftrag;
            }
        }

        List<EqVlan> eqVlans = findEqVlans(equipment.getId());
        // Gueltigkeit verschieben
        for (EqVlan eqVlan : eqVlans) {
            boolean isModified = false;
            if (auftragAktion != null) { // nur EqVlans mit verlinkter AuftragAktion beruecksichtigen
                if (auftragAktion.isAuftragAktionAddFor(eqVlan)) {
                    eqVlan.setGueltigVon(Date.from(newValidFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    isModified = true;
                }
                else if (auftragAktion.isAuftragAktionRemoveFor(eqVlan)) {
                    eqVlan.setGueltigBis(Date.from(newValidFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    isModified = true;
                }
            }
            else {
                if (DateTools.isDateEqual(eqVlan.getGueltigVon(), originalGueltigVon)) {
                    eqVlan.setGueltigVon(Date.from(newValidFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    isModified = true;
                }
                else if (DateTools.isDateEqual(eqVlan.getGueltigBis(), originalGueltigVon)) {
                    eqVlan.setGueltigBis(Date.from(newValidFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    isModified = true;
                }
            }

            if (isModified) {
                vlanDao.store(eqVlan);
            }
        }
        return findEqVlans4Auftrag(auftragId, newValidFrom);
    }

    @Override
    @Nonnull
    public List<EqVlan> cancelEqVlans(Long auftragId, AuftragAktion auftragAktion) throws StoreException,
            FindException {
        Long equipmentId = findEquipmentOnEndstelleB4Auftrag(auftragId).getId();
        List<EqVlan> vlansOfEquipment = findEqVlans(equipmentId);

        List<EqVlan> oldEqVlans = new ArrayList<>();
        for (EqVlan eqVlan : vlansOfEquipment) {
            if (auftragAktion.isAuftragAktionAddFor(eqVlan)) {
                vlanDao.delete(eqVlan);
            }
            else if (auftragAktion.isAuftragAktionRemoveFor(eqVlan)) {
                // deaktiverte EqVlan wieder aktivieren
                eqVlan.setGueltigBis(DateTools.getHurricanEndDate());
                eqVlan.setAuftragAktionsIdRemove(null);
                vlanDao.store(eqVlan);
                oldEqVlans.add(eqVlan);
            }
        }

        return oldEqVlans;
    }

    @Override
    public void removeEqVlans(long equipmentId) throws DeleteException {
        try {
            List<EqVlan> vlans = findEqVlans(equipmentId);

            for (EqVlan vlan : vlans) {
                vlanDao.delete(vlan);
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }

    }

    private List<EqVlan> findEqVlanByGueltigVon(Date gueltigVon, Long equipmentId) throws StoreException {
        EqVlan example = new EqVlan();
        example.setEquipmentId(equipmentId);
        example.setGueltigVon(gueltigVon);

        return vlanDao.queryByExample(example, EqVlan.class);
    }

    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    private boolean equalsEqVlansAndValid(List<EqVlan> oldEqVlans, List<EqVlan> eqVlans, Date validDate) {
        if (oldEqVlans.size() != eqVlans.size()) {
            return false;
        }
        outer:
        for (EqVlan old : oldEqVlans) {
            if (validDate.before(old.getGueltigVon()) || !DateTools.isHurricanEndDate(old.getGueltigBis())) {
                return false;
            }
            for (EqVlan eqVlan : eqVlans) {
                if (eqVlan.equalsAll(old)) {
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public List<EqVlan> findEqVlans4Auftrag(Long auftragId, LocalDate when) throws FindException {
        Equipment equipment = findEquipmentOnEndstelleB4Auftrag(auftragId);
        return findEqVlans(equipment.getId(), Date.from(when.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public List<EqVlan> assignEqVlans(EkpFrameContract ekpFrameContract, Long auftragId, Long productId,
            LocalDate when, AuftragAktion auftragAktion) throws FindException, StoreException {
        if (ekpFrameContract == null) {
            throw new FindException("EKP frame contract is not defined!");
        }

        List<EqVlan> vlans = calculateEqVlans(ekpFrameContract, auftragId, productId, when);
        return addEqVlans(vlans, auftragAktion);
    }


    @Override
    public Pair<HWOlt, Integer> changeHwOltVlanAb(@Nonnull Long hwOltId, @CheckForNull Date vlanAktivAb)
            throws StoreException {
        try {
            HWOlt olt = validateParams4ChangeHwOltVlanAb(hwOltId, vlanAktivAb);

            final boolean doesVlanAktivAbChange = !(((olt.getVlanAktivAb() == null) && (vlanAktivAb == null))
                    || DateTools.isDateEqual(olt.getVlanAktivAb(), vlanAktivAb));

            int auftragCount = 0;
            if (doesVlanAktivAbChange) {
                olt.setVlanAktivAb(vlanAktivAb);
                hwService.saveHWRack(olt);
                List<HWBaugruppe> baugruppen = findBaugruppen4OltAndItsMdusAndDpos(hwOltId);
                for (HWBaugruppe baugruppe : baugruppen) {
                    // alle Vlans für die Baugruppe löschen
                    List<EqVlan> oldVlans = vlanDao.findEqVlansByBaugruppe(baugruppe.getId());
                    for (EqVlan eqVlan : oldVlans) {
                        vlanDao.delete(eqVlan);
                    }
                    // und neu berechnen wenn Datum gesetzt ist
                    if (vlanAktivAb != null) {
                        auftragCount = vlanNeuberechnung(vlanAktivAb, auftragCount, baugruppe);
                    }
                }
            }

            return new Pair<>(olt, auftragCount);
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private int vlanNeuberechnung(@CheckForNull Date vlanAktivAb, int auftragCount, HWBaugruppe baugruppe) throws StoreException {
        int auftragCount1 = auftragCount;
        List<AuftragDaten> auftragDatenList = auftragService
                .findAktiveAuftragDatenByBaugruppe(baugruppe
                        .getId());
        for (AuftragDaten auftragDaten : auftragDatenList) {
            try {
                Date adStartFrom = auftragDaten.getInbetriebnahme() != null ? auftragDaten
                        .getInbetriebnahme() : auftragDaten.getVorgabeSCV();
                if (adStartFrom == null) {
                    LOG.warn(String
                            .format("Auftrag mit ID %d hat weder Inbetriebname, noch VorgabeSCV Datum. Es werden keine VLANS berechnet!",
                                    auftragDaten.getAuftragId()));
                    continue;
                }
                Date calcDate = vlanAktivAb.after(adStartFrom) ? vlanAktivAb : adStartFrom;

                EkpFrameContract ekpFrameContract = ekpFrameContractService
                        .findEkp4AuftragOrDefaultMnet(auftragDaten.getAuftragId(),
                                DateConverterUtils.asLocalDate(calcDate), true);
                if (ekpFrameContract == null) {
                    throw new StoreException("EKP not found!");
                }

                assignEqVlans(ekpFrameContract, auftragDaten.getAuftragId(), auftragDaten.getProdId(),
                        DateConverterUtils.asLocalDate(calcDate), null);
                auftragCount1++;
            }
            catch (Exception e) {
                throw new StoreException(String.format(
                        "Fehler beim Berechnen der VLans für den Auftrag mit Id %s: %s",
                        auftragDaten.getAuftragId(), e.getMessage()), e);
            }
        }
        return auftragCount1;
    }

    protected List<HWBaugruppe> findBaugruppen4OltAndItsMdusAndDpos(Long oltId) throws FindException {
        List<HWOltChild> oltChildRacks = new ArrayList<>();
        oltChildRacks.addAll(hwService.findHWOltChildByOlt(oltId, HWMdu.class));
        oltChildRacks.addAll(hwService.findHWOltChildByOlt(oltId, HWDpo.class));
        List<HWBaugruppe> baugruppen = new LinkedList<>();
        for (HWOltChild rack : oltChildRacks) {
            List<HWBaugruppe> baugruppen4Rack = hwService.findBaugruppen4Rack(rack.getId());
            baugruppen.addAll(baugruppen4Rack);
        }
        baugruppen.addAll(hwService.findBaugruppen4Rack(oltId));
        return baugruppen;
    }

    protected HWOlt validateParams4ChangeHwOltVlanAb(Long hwOltId, Date vlanAktivAb) throws StoreException,
            FindException {
        final boolean vlanAktivAbNotInFuture = (vlanAktivAb != null)
                && DateConverterUtils.asLocalDate(vlanAktivAb).isBefore(LocalDate.now());
        if (vlanAktivAbNotInFuture) {
            throw new StoreException("vlanAktivAb darf leer sein oder muss heute bzw. in der Zukunft liegen!");
        }

        HWOlt olt;
        HWRack oltAsRack = hwService.findRackById(hwOltId);
        if (oltAsRack == null) {
            throw new StoreException(String.format("OLT mit Id %d konnte nicht gefunden werden", hwOltId));
        }
        else if (oltAsRack instanceof HWOlt) {
            olt = (HWOlt) oltAsRack;
            final boolean oltVlanAktivAbIsNotInFuture = (olt.getVlanAktivAb() != null)
                    && DateConverterUtils.asLocalDate(olt.getVlanAktivAb()).isBefore(LocalDate.now());
            if (oltVlanAktivAbIsNotInFuture) {
                throw new StoreException(
                        "Der bisherige Wert für vlanAktivAb kann nicht mehr geändert werden, da er in der Vergangenheit liegt!");
            }
        }
        else {
            throw new StoreException(String.format("Rack mit Id %d ist keine OLT!", hwOltId));
        }
        return olt;
    }


    public EqVlan saveEqVlan(@Nonnull EqVlan toSave) {
        return vlanDao.store(toSave);
    }


    class VlanCalculationParameter {
        int a10NspNummer;
        Integer oltSlot = 0;
        Integer oltSvlanOffset;
        Integer oltNr;
        Long oltId;
        long hwProducer;
        Date vlanAktivAb;
        VlanCalculatorStrategy svlanCalculatorStrategy;

        void calculateSvlans(EqVlan eqVlan, LocalDate eqVlanGueltigVon, CVlan cvlanEkp, int svlanEkpFaktor) {
            calculateSvlanEkp(eqVlan, eqVlanGueltigVon, cvlanEkp, svlanEkpFaktor);
            calculateSvlanOlt(eqVlan, eqVlanGueltigVon, cvlanEkp);
            calculateSvlanMdu(eqVlan, eqVlanGueltigVon, cvlanEkp);
        }

        void calculateSvlanEkp(EqVlan eqVlan, LocalDate eqVlanGueltigVon, CVlan cvlanEkp, int svlanEkpFaktor) {
            eqVlan.setSvlanEkp(svlanCalculatorStrategy.calculateSvlanEkp(a10NspNummer, oltNr, oltSlot, cvlanEkp, svlanEkpFaktor));

            eqVlan.setCvlan(cvlanEkp.getValue());
            eqVlan.setGueltigVon(Date.from(eqVlanGueltigVon.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            eqVlan.setGueltigBis(DateTools.getHurricanEndDate());
        }

        void calculateSvlanOlt(EqVlan eqVlan, LocalDate eqVlanGueltigVon, CVlan cvlanEkp) {
            eqVlan.setSvlanOlt(svlanCalculatorStrategy.calculateSvlanOlt(a10NspNummer, oltNr, oltSlot, oltSvlanOffset, cvlanEkp));

            eqVlan.setCvlan(cvlanEkp.getValue());
            eqVlan.setGueltigVon(Date.from(eqVlanGueltigVon.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            eqVlan.setGueltigBis(DateTools.getHurricanEndDate());
        }

        void calculateSvlanMdu(EqVlan eqVlan, LocalDate eqVlanGueltigVon, CVlan cvlanEkp) {
            eqVlan.setSvlanMdu(svlanCalculatorStrategy.calculateSvlanMdu(a10NspNummer, oltNr, oltSlot, cvlanEkp));

            eqVlan.setCvlan(cvlanEkp.getValue());
            eqVlan.setGueltigVon(Date.from(eqVlanGueltigVon.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            eqVlan.setGueltigBis(DateTools.getHurricanEndDate());
        }

    }

}
