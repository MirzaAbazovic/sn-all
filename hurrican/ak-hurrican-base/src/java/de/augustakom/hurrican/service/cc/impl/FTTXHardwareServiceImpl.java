/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2010
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWDpu;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWOltChild;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.FTTBDpuImportView;
import de.augustakom.hurrican.model.cc.view.FTTBDpuKarteImportView;
import de.augustakom.hurrican.model.cc.view.FTTBDpuPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoImportView;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTBMduImportView;
import de.augustakom.hurrican.model.cc.view.FTTBMduPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntPortImportView;
import de.augustakom.hurrican.model.cc.view.FTTXStandortImportView;
import de.augustakom.hurrican.model.cc.view.OltChildImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Implementierung von <code>FTTXHardwareService</code>
 */
@CcTxRequired
public class FTTXHardwareServiceImpl extends DefaultCCService implements FTTXHardwareService {

    private static final Logger LOGGER = Logger.getLogger(FTTXHardwareServiceImpl.class);

    private static final String MSG_TEXT_STANDORTBEZ =     "Standort mit der Bezeichnung ";
    private static final String MSG_TEXT_EXISTIERT =       " existiert bereits!";
    private static final String MSG_TEXT_EXISTIERT_NICHT = " existiert nicht.";
    private static final String MSG_TEXT_NICHT_GEFUNDEN =  " nicht gefunden!";

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWSwitchService")
    private HWSwitchService hwSwitchService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungFreigabeService")
    private RangierungFreigabeService rangierungFreigabeService;
    @Resource(name = "de.augustakom.hurrican.service.cc.NiederlassungService")
    private NiederlassungService niederlasungService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    @Override
    public void generateFTTBStift(String hvtName, String leiste, String stift, RangSchnittstelle type) throws StoreException {
        if (StringUtils.isBlank(hvtName) || StringUtils.isBlank(leiste) || StringUtils.isBlank(stift) || (type == null)) {
            return;
        }
        try {
            HVTStandort hvtStandort = hvtService.findHVTStandortByBezeichnung(hvtName);
            // Equipment anlegen
            Equipment eq = rangierungsService.findEQByLeisteStift(hvtStandort.getId(), leiste, stift);
            if (eq == null) {
                eq = new Equipment();
                eq.setHvtIdStandort(hvtStandort.getId());
                eq.setCarrier(TNB.MNET.carrierNameUC);
                eq.setGueltigVon(new Date());
                eq.setGueltigBis(DateTools.getHurricanEndDate());
                eq.setRangLeiste1(leiste);
                eq.setRangStift1(stift);
                eq.setRangSchnittstelle(type);
                eq.setStatus(EqStatus.frei);
                rangierungsService.saveEquipment(eq);
            }
            else {
                throw new StoreException("Stift " + stift + " auf Leiste " + leiste + " bereits vorhanden.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public HWMdu generateFTTBMdu(FTTBMduImportView view, Long sessionId) throws StoreException {
        if ((view == null) || (sessionId == null)) {
            return null;
        }
        try {
            // Ermittle HVT-Standort
            HVTStandort hvtStandort = hvtService.findHVTStandortByBezeichnung(view.getStandort());
            if (hvtStandort == null) {
                throw new StoreException(MSG_TEXT_STANDORTBEZ + view.getStandort() + MSG_TEXT_EXISTIERT_NICHT);
            }

            // Pruefe, ob MDU bereits existiert
            if (hwService.findRackByBezeichnung(view.getBezeichnung()) != null) {
                throw new StoreException("MDU " + view.getBezeichnung() + MSG_TEXT_EXISTIERT);
            }

            // Ermittle Hersteller
            HVTTechnik hvtTechnik = hvtService.findHVTTechnikByHersteller(view.getHersteller());
            if (hvtTechnik == null) {
                throw new StoreException("Hersteller " + view.getHersteller() + MSG_TEXT_EXISTIERT_NICHT);
            }

            // HVT-Technik dem Standort zuordnen, falls noch nicht geschehen.
            assignHvtTechnikToStandort(hvtTechnik, hvtStandort);

            // OLT ermitteln
            HWRack oltRack = hwService.findRackByBezeichnung(view.getOlt());
            if (oltRack == null) {
                throw new StoreException("OLT " + view.getOlt() + " kann nicht ermittelt werden!");
            }

            // MDU-Datensatz anlegen
            HWMdu mdu = new HWMdu();
            mdu.setGeraeteBez(view.getBezeichnung());
            mdu.setGueltigVon(new Date());
            mdu.setGueltigBis(DateTools.getHurricanEndDate());
            mdu.setHvtIdStandort(hvtStandort.getId());
            mdu.setHwProducer(hvtTechnik.getId());
            mdu.setRackTyp(HWRack.RACK_TYPE_MDU);
            mdu.setSerialNo(view.getSeriennummer());
            mdu.setOltRackId(oltRack.getId());
            mdu.setOltFrame(view.getOltRack());
            mdu.setOltSubrack(view.getOltSubrack());
            mdu.setOltSlot(view.getOltSlot());
            mdu.setOltGPONPort(view.getOltPort());
            mdu.setOltGPONId(view.getGponId());
            mdu.setMduType(view.getModellnummer());

            //IP Adresse aktuell nur bei Huawei MDUs notwendig
            String ipAdresse = null;
            if (HVTTechnik.isIpRequired(hvtTechnik.getId())) {
                ipAdresse = generateIpMdu(mdu, oltRack.getId());
                if (StringUtils.isBlank(ipAdresse)) {
                    throw new StoreException("IP-Adresse konnte nicht berechnet werden.");
                }
            }
            mdu.setIpAddress(ipAdresse);

            // TV-Signal
            Boolean caTV = StringUtils.equals(view.getCatv(), "true") ? Boolean.TRUE : Boolean.FALSE;
            mdu.setCatvOnline(caTV);

            // HVT-Raum anlegen
            HVTRaum mduRaum = hvtService.findHVTRaumByName(hvtStandort.getId(), view.getRaum());
            if (mduRaum == null) {
                mduRaum = new HVTRaum();
                mduRaum.setHvtIdStandort(hvtStandort.getId());
                mduRaum.setRaum(view.getRaum());
                hvtService.saveHVTRaum(mduRaum);
            }
            mdu.setHvtRaumId(mduRaum.getId());

            hwService.saveHWRack(mdu);

            return mdu;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    private void assignHvtTechnikToStandort(HVTTechnik hvtTechnik, HVTStandort hvtStandort) throws FindException, StoreException {
        List<HVTTechnik> hvtTechniken = hvtService.findHVTTechniken4Standort(hvtStandort.getId());
        List<Long> hvtTechnikIds = new ArrayList<>();
        if (CollectionTools.isNotEmpty(hvtTechniken)) {
            for (HVTTechnik hvtTech : hvtTechniken) {
                hvtTechnikIds.add(hvtTech.getId());
            }
        }

        if (!hvtTechnikIds.contains(hvtTechnik.getId())) {
            hvtTechnikIds.add(hvtTechnik.getId());
            hvtService.saveHVTTechniken4Standort(hvtStandort.getId(), hvtTechnikIds);
        }
    }

    @Override
    public void generateFTTBMduPort(FTTBMduPortImportView view, Long sessionId) throws StoreException {
        if ((view == null) || (sessionId == null)) {
            return;
        }
        try {
            // User ermitteln
            AKUser user = checkUser(sessionId);

            // Ermittle Mdu
            HWMdu mdu = (HWMdu) hwService.findRackByBezeichnung(view.getMdu());
            if (mdu == null) {
                throw new StoreException(String.format("MDU mit der Bezeichnung %s existiert in Hurrican nicht.", view.getMdu()));
            }
            // Freigabedatum
            if (mdu.getFreigabe() != null) {
                view.setGueltigAb(DateTools.isAfter(mdu.getFreigabe(), new Date()) ? mdu.getFreigabe() : new Date());
            }
            else {
                view.setGueltigAb(DateTools.getHurricanEndDate());
            }

            // Ermittle zugehoerige Baugruppe
            HWBaugruppe hwBaugruppe = getHWBaugruppe(mdu, view.getBgTyp(), view.getSchnittstelle(), view.getModulNummer());

            // Physiktyp laden
            PhysikTyp fttxPhysikTyp = getPhysikTyp("FTTB_" + view.getSchnittstelle());

            if (!assertPortDoesNotAlreadyExist(view.getPort(), mdu, hwBaugruppe)) {
                throw new StoreException(String.format("Der Port %s %s", MSG_TEXT_EXISTIERT, view.getPort()));
            }

            Equipment mduEquipment;
            if (fttxPhysikTyp.isOfType(PhysikTyp.PHYSIKTYP_FTTB_RF)) {
                mduEquipment = new Equipment();
                mduEquipment.setHvtIdStandort(mdu.getHvtIdStandort());
            }
            else {
                mduEquipment = getEquipment(mdu.getHvtIdStandort(), view.getLeiste(), view.getStift());
            }

            definePortValues(mduEquipment, view.getPort(), view.getSchnittstelle(), hwBaugruppe, user, EqStatus.rang);
            createRangierung4Port(mduEquipment, view.getGueltigAb(), mdu, fttxPhysikTyp, user);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    /**
     * Schreibt die notwendigen Port-Informationen auf den Equipment-Datensatz {@code equipment}. Zuvor wird noch
     * ueberprueft, ob die Anzahl der Ports auf der Baugruppe die maximale Groesse noch nicht erreicht hat.
     *
     * @param equipment         Equipment
     * @param hwEqn             hwEqn
     * @param schnittstelle     Schnittstelle
     * @param hwBaugruppe       hwBaugruppe
     * @param user              User
     * @throws FindException    Exception, wenn Baugruppe für Equpment nicht gefunden wird
     * @throws StoreException wenn die Anzahl der vorhandenen Ports >= der maximalen Anzahl Ports fuer die Baugruppe
     *                        entspricht; wenn beim Speichern der Port-Informationen ein Fehler auftritt
     */
    void definePortValues(Equipment equipment, String hwEqn, String schnittstelle, HWBaugruppe hwBaugruppe,
            AKUser user, EqStatus eqStatus) throws FindException, StoreException {
        // Portanzahl ueberpruefen
        List<Equipment> equipments4hwBaugruppe = rangierungsService.findEquipments4HWBaugruppe(hwBaugruppe.getId());
        HWBaugruppenTyp baugruppenTyp = hwBaugruppe.getHwBaugruppenTyp();
        if (equipments4hwBaugruppe.size() >= baugruppenTyp.getPortCount()) {
            throw new StoreException(String.format("Die maximale Anzahl von %d Ports auf der Baugruppe %s vom Typ %s wurde bereits erreicht!",
                    baugruppenTyp.getPortCount(), hwBaugruppe.getModNumber(), baugruppenTyp.getName()));
        }

        equipment.setHwEQN(hwEqn);
        equipment.setHwSchnittstelle(schnittstelle);
        equipment.setHwBaugruppenId(hwBaugruppe.getId());
        equipment.setStatus(eqStatus);
        equipment.setUserW(user.getLoginName());
        equipment.setDateW(new Date());
        rangierungsService.saveEquipment(equipment);
    }

    /**
     * Erstellt fuer den angegebenen MDU Port eine Rangierung. Der Port wird dabei als EQ_IN der Rangierung uebergeben.
     *
     * @param equipment         Equipment
     * @param gueltigAb         Gültig Ab
     * @param hwRack            hwRack
     * @param fttxPhysikTyp     fttxPhysikTyp
     * @param user              User
     * @throws FindException    FindException
     * @throws StoreException   StoreException
     */
    void createRangierung4Port(Equipment equipment, Date gueltigAb,
            HWRack hwRack, PhysikTyp fttxPhysikTyp, AKUser user) throws FindException, StoreException {
        // Rangierung erstellen
        Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId(), true);
        if (rangierung != null) {
            throw new StoreException("Der EQ-In Stift ist bereits einer Rangierung zugeordnet");
        }
        rangierung = new Rangierung();
        rangierung.setHvtIdStandort(hwRack.getHvtIdStandort());
        rangierung.setPhysikTypId(fttxPhysikTyp.getId());
        rangierung.setFreigegeben(Freigegeben.freigegeben);
        rangierung.setGueltigVon(gueltigAb);
        rangierung.setGueltigBis(DateTools.getHurricanEndDate());
        rangierung.setUserW(user.getLoginName());
        rangierung.setDateW(new Date());
        rangierung.setEqInId(equipment.getId());
        rangierung.setLeitungGesamtId(rangierungsService.findNextLtgGesId4Rangierung());
        if (hwRack instanceof HWOnt) {
            HWOnt hwOnt = (HWOnt) hwRack;
            rangierung.setOntId(hwOnt.getGeraeteBez());
        }
        rangierungsService.saveRangierung(rangierung, false);
    }

    /**
     *  Prueft, ob der Port bereits existiert. Die Pruefung erfolgt ueber die Baugruppe und HW_EQN. Sollte der Port
     *  bereits vorhanden sein, wird ein {@link StoreException} gewerfen.
     */
    boolean assertPortDoesNotAlreadyExist(String hwEqn, HWRack hwRack, HWBaugruppe hwBaugruppe) throws FindException {
        Equipment existingEquipmentExample = new Equipment();
        existingEquipmentExample.setHwBaugruppenId(hwBaugruppe.getId());
        existingEquipmentExample.setHvtIdStandort(hwRack.getHvtIdStandort());
        existingEquipmentExample.setHwEQN(hwEqn);
        List<Equipment> existingEquipments = rangierungsService.findEquipments(existingEquipmentExample, (String[]) null);
        return !CollectionTools.isNotEmpty(existingEquipments);
    }

    private HWBaugruppe findOrCreateBaugruppe(String modulnummer, HWRack hwRack, HWBaugruppenTyp baugruppenTyp) throws Exception{
        return findOrCreateBaugruppe(modulnummer,hwRack,baugruppenTyp,null);
    }

    private HWBaugruppe findOrCreateBaugruppe(String modulnummer, HWRack hwRack, HWBaugruppenTyp baugruppenTyp, String modulname) throws Exception {

        checkParameterForFindOrCreateBaugruppe(modulnummer, hwRack, baugruppenTyp);

        HWBaugruppe example = new HWBaugruppe();
        example.setRackId(hwRack.getId());
        example.setModNumber(modulnummer);

        List<HWBaugruppe> baugruppen = hwService.findBaugruppen(example);
        if (baugruppen.size() > 1) {
            throw new StoreException(String.format("Baugruppe %s nicht eindeutig ermittelbar fuer Rack %s",
                    modulnummer, hwRack.getGeraeteBez()));
        }
        HWBaugruppe hwBaugruppe;
        if (baugruppen.isEmpty()) {
            hwBaugruppe = new HWBaugruppe();
            hwBaugruppe.setRackId(hwRack.getId());
            hwBaugruppe.setHwBaugruppenTyp(baugruppenTyp);
            hwBaugruppe.setModNumber(modulnummer);
            hwBaugruppe.setEingebaut(true);
            if(modulname != null){
                hwBaugruppe.setModName(modulname);
            }
            hwService.saveHWBaugruppe(hwBaugruppe);
        }
        else {
            hwBaugruppe = baugruppen.get(0);
            if (!baugruppenTyp.getName().equals(hwBaugruppe.getHwBaugruppenTyp().getName())) {
                throw new StoreException(String.format("Baugruppentyp %s entspricht nicht dem erwarteten Typ %s ",
                        hwBaugruppe.getHwBaugruppenTyp().getName(), baugruppenTyp.getName()));
            }
        }
        return hwBaugruppe;
    }

    private void checkParameterForFindOrCreateBaugruppe(String modulnummer, HWRack hwRack, HWBaugruppenTyp baugruppenTyp) throws StoreException{
        if (StringUtils.isEmpty(modulnummer)) {
            throw new StoreException("Modulnummer ist nicht angegeben!");
        }
        if (hwRack == null || hwRack.getId() == null) {
            throw new StoreException("Die ID des Racks ist nicht angegeben!");
        }
        if (baugruppenTyp == null) {
            throw new StoreException("Der Baugruppentyp ist nicht angegeben!");
        }
    }

    private HWBaugruppenTyp getBaugruppenTyp(String bgTyp, String byTypAlternativ) throws FindException, StoreException {
        HWBaugruppenTyp baugruppenTyp = hwService.findBaugruppenTypByName(bgTyp);
        if (baugruppenTyp == null) {
            baugruppenTyp = hwService.findBaugruppenTypByName(byTypAlternativ);
            if (baugruppenTyp == null) {
                throw new StoreException(String.format("Baugruppentyp %s sowie Alternative %s wurden nicht gefunden",
                        bgTyp, byTypAlternativ));
            }
        }
        return baugruppenTyp;
    }

    @Override
    public void generateFTTXStandort(FTTXStandortImportView view, Long sessionId) throws StoreException {
        if ((view == null) || (sessionId == null)) {
            return;
        }

        if (view.getGeoId() == null) {
            throw new StoreException("Standort kann nicht angelegt werden, da GeoId nicht definiert ist!");
        }
        else if (StringUtils.isBlank(view.getBezeichnung())) {
            throw new StoreException(
                    "Standort kann nicht angelegt werden, da Standortbezeichnung nicht definiert ist!");

        }

        // Erzeuge Standort
        if (!StringUtils.isBlank(view.getStandortTyp())) {
            switch (view.getStandortTyp().toUpperCase()) {
                case FTTXStandortImportView.STANDORT_TYP_FTTB_H:
                case FTTXStandortImportView.STANDORT_TYP_FTTB:
                case FTTXStandortImportView.STANDORT_TYP_FTTH:
                case FTTXStandortImportView.STANDORT_TYP_FC:
                case FTTXStandortImportView.STANDORT_TYP_BR:
                    generateStandort(view, sessionId);
                    break;
                case FTTXStandortImportView.STANDORT_TYP_MVG:
                    generateMVGStandort(view, sessionId);
                    break;
                default:
                    throw new StoreException("Falscher Standort-Typ: " + view.getStandortTyp());
            }
        }
        else {
            throw new StoreException("Standort-Typ darf nicht leer sein!");
        }
    }

    @Override
    public Pair<HWMdu, Boolean> updateMDU(String bezeichnung, String seriennummer, Long sessionId)
            throws StoreException {
        if (StringUtils.isBlank(bezeichnung) || StringUtils.isBlank(seriennummer)) {
            return null;
        }
        try {

            HWMdu mdu = (HWMdu) hwService.findRackByBezeichnung(bezeichnung);
            if (mdu == null) {
                throw new StoreException(String.format("MDU mit der Bezeichnung %s existiert in Hurrican nicht.", bezeichnung));
            }
            boolean serialNumberChanged = !StringUtils.equals(mdu.getSerialNo(), seriennummer);
            mdu.setSerialNo(seriennummer);
            hwService.saveHWRack(mdu);

            return Pair.create(mdu, serialNumberChanged);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage());
        }
    }

    private void generateStandort(FTTXStandortImportView view, Long sessionId) throws StoreException {
        if ((view == null) || (sessionId == null)) {
            return;
        }
        try {
            HVTGruppe hvtGruppe;
            HVTStandort hvtStandort = null;
            String[] fttxTechLocationPool = new String[] {
                    FTTXStandortImportView.STANDORT_TYP_FTTB,
                    FTTXStandortImportView.STANDORT_TYP_FTTH,
                    FTTXStandortImportView.STANDORT_TYP_FTTB_H};

            // Pruefe, ob HVT-Bezeichnung bereits existiert
            hvtGruppe = hvtService.findHVTGruppeByBezeichnung(view.getBezeichnung());
            if (hvtGruppe != null) {
                List<HVTStandort> hvtStandorte = hvtService.findHVTStandorte4Gruppe(hvtGruppe.getId(), true);
                if ((hvtStandorte != null) && (hvtStandorte.size() == 1)) {
                    hvtStandort = hvtStandorte.get(0);
                }
                Long[] currentTechLocation = new Long[] {
                        HVTStandort.HVT_STANDORT_TYP_FTTX_BR,
                        HVTStandort.HVT_STANDORT_TYP_FTTX_FC};
                // Pruefe, ob es sich um eine Aenderung BR->FTTX handelt
                if ((hvtStandort != null) &&
                        NumberTools.isIn(hvtStandort.getStandortTypRefId(), currentTechLocation)
                        && StringTools.isIn(view.getStandortTyp().toUpperCase(), fttxTechLocationPool)) {
                    hvtStandort.setStandortTypRefId(getHvtStandortTypRefId(view));
                    hvtStandort.setClusterId(view.getClusterId());
                    hvtService.saveHVTStandort(hvtStandort);
                    createGeoId2TechLocationMapping(view, hvtStandort, sessionId);
                    return;
                }
                // Standort bereits vorhanden -> Fehler
                else {
                    throw new StoreException(MSG_TEXT_STANDORTBEZ + view.getBezeichnung() + MSG_TEXT_EXISTIERT);
                }
            }

            // Ermittle zugehoerigen FC-Raum
            HVTStandort fcRaum = hvtService.findHVTStandortByBezeichnung(view.getFcRaum());
            if ((fcRaum == null) && !StringTools.isIn(view.getStandortTyp(),
                    new String[] { FTTXStandortImportView.STANDORT_TYP_FC, FTTXStandortImportView.STANDORT_TYP_BR })) {
                throw new StoreException("FC-Raum " + view.getFcRaum() + MSG_TEXT_NICHT_GEFUNDEN);
            }

            // Ermittle zugehoeriger Betriebsraum
            HVTStandort betriebsraum = hvtService.findHVTStandortByBezeichnung(view.getBetriebsraum());
            if ((betriebsraum == null) && !StringTools.isIn(view.getStandortTyp(),
                    new String[] { FTTXStandortImportView.STANDORT_TYP_FC, FTTXStandortImportView.STANDORT_TYP_BR })) {
                throw new StoreException("Betriebsraum " + view.getBetriebsraum() + MSG_TEXT_NICHT_GEFUNDEN);
            }

            // Ermittle zugehoerige Niederlassung
            Niederlassung nl = niederlasungService.findNiederlassungByName(view.getNiederlassung());
            if (nl == null) {
                throw new StoreException("Niederlassung " + view.getNiederlassung() + MSG_TEXT_NICHT_GEFUNDEN);
            }

            // HVT-Gruppe anlegen
            hvtGruppe = new HVTGruppe();
            hvtGruppe.setStrasse(view.getStrasse());
            hvtGruppe.setHausNr(StringTools.join(new String[] { view.getHausnummer(), view.getHausnummerZusatz() }, null, true));
            hvtGruppe.setPlz(view.getPlz());
            hvtGruppe.setOrt(view.getOrt());
            hvtGruppe.setOnkz(view.getOnkz());
            hvtGruppe.setOrtsteil(view.getBezeichnung());
            hvtGruppe.setNiederlassungId(nl.getId());
            hvtGruppe.setExport4Portal(Boolean.FALSE);
            hvtService.saveHVTGruppe(hvtGruppe);

            // HVT-Standort anlegen
            hvtStandort = new HVTStandort();
            hvtStandort.setHvtGruppeId(hvtGruppe.getId());
            hvtStandort.setBetriebsraumId((betriebsraum != null) ? betriebsraum.getId() : null);
            hvtStandort.setFcRaumId((fcRaum != null) ? fcRaum.getId() : null);
            hvtStandort.setAsb(hvtService.generateAsb4HVTStandort(view.getAsb()));
            hvtStandort.setGueltigVon(new Date());
            hvtStandort.setGueltigBis(DateTools.getHurricanEndDate());
            hvtStandort.setCarrierId(Carrier.ID_MNET_NGN);
            hvtStandort.setGesicherteRealisierung(HVTStandort.GESICHERT_IN_BETRIEB);
            hvtStandort.setCpsProvisioning(Boolean.TRUE);
            hvtStandort.setClusterId(view.getClusterId());
            if (NumberTools.equal(nl.getId(), Niederlassung.ID_AUGSBURG)) {
                hvtStandort.setCarrierKennungId(CarrierKennung.ID_MNET_AUGSBURG);
            }
            else {
                hvtStandort.setCarrierKennungId(CarrierKennung.ID_MNET_MUENCHEN);
            }
            hvtStandort.setStandortTypRefId(getHvtStandortTypRefId(view));

            hvtService.saveHVTStandort(hvtStandort);

            if (StringTools.isIn(view.getStandortTyp().toUpperCase(), fttxTechLocationPool)) {
                createGeoId2TechLocationMapping(view, hvtStandort, sessionId);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    private void generateMVGStandort(FTTXStandortImportView view, Long sessionId) throws StoreException {
        if ((view == null) || (sessionId == null)) {
            return;
        }
        try {
            // Pruefe, ob Standort bereits als versorgter Standort angelegt wurde
            HVTGruppe hvtGruppe = hvtService.findHVTGruppeByBezeichnung(view.getBezeichnung());
            if (hvtGruppe != null) {
                throw new StoreException("Der mitversorgte " + MSG_TEXT_STANDORTBEZ + view.getBezeichnung() + " ist bereits als versorgter Standort vorhanden.");
            }

            // HVTGruppe ermitteln
            hvtGruppe = hvtService.findHVTGruppeByBezeichnung(view.getVersorger());
            if (hvtGruppe == null) {
                throw new StoreException("Der versorgende "+ MSG_TEXT_STANDORTBEZ + view.getVersorger() + MSG_TEXT_EXISTIERT_NICHT);
            }
            HVTStandort hvtStandort;
            List<HVTStandort> hvtStandorte = hvtService.findHVTStandorte4Gruppe(hvtGruppe.getId(), true);
            if ((hvtStandorte != null) && (hvtStandorte.size() == 1)) {
                hvtStandort = hvtStandorte.get(0);
            }
            else {
                throw new StoreException("Der versorgende " + MSG_TEXT_STANDORTBEZ + view.getVersorger() + MSG_TEXT_EXISTIERT_NICHT);
            }

            if (StringUtils.equalsIgnoreCase(view.getStandortTyp(), FTTXStandortImportView.STANDORT_TYP_MVG)) {
                createGeoId2TechLocationMapping(view, hvtStandort, sessionId);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    Long getHvtStandortTypRefId(FTTXStandortImportView view) throws StoreException {
        if (StringUtils.equalsIgnoreCase(view.getStandortTyp(), FTTXStandortImportView.STANDORT_TYP_FTTB)) {
            return HVTStandort.HVT_STANDORT_TYP_FTTB;
        }
        else if (StringUtils.equalsIgnoreCase(view.getStandortTyp(), FTTXStandortImportView.STANDORT_TYP_FTTH)) {
            return HVTStandort.HVT_STANDORT_TYP_FTTH;
        }
        else if (StringUtils.equalsIgnoreCase(view.getStandortTyp(), FTTXStandortImportView.STANDORT_TYP_FTTB_H)) {
            return HVTStandort.HVT_STANDORT_TYP_FTTB_H;
        }
        else if (StringUtils.equalsIgnoreCase(view.getStandortTyp(), FTTXStandortImportView.STANDORT_TYP_FC)) {
            return HVTStandort.HVT_STANDORT_TYP_FTTX_FC;
        }
        else if (StringUtils.equalsIgnoreCase(view.getStandortTyp(), FTTXStandortImportView.STANDORT_TYP_BR)) {
            return HVTStandort.HVT_STANDORT_TYP_FTTX_BR;
        }
        else {
            throw new StoreException("Standort-Typ nicht bekannt: " + view.getStandortTyp());
        }
    }

    /**
     * Erstellt fuer die in {@link FTTXStandortImportView} angegebene GeoID das Mapping auf den angegebenen technischen
     * Standort. <br> Zusaetzlich wird auch die Rangierungsmatrix mit aufgebaut.
     *
     * @param view              FTTXStandortImportView
     * @param hvtStandort       HVTStandort
     * @param sessionId         sessionId
     * @throws StoreException   StoreException
     */
    private void createGeoId2TechLocationMapping(FTTXStandortImportView view, HVTStandort hvtStandort, Long sessionId)
            throws StoreException {
        if ((view == null) || (view.getGeoId() == null) || (sessionId == null) || (hvtStandort == null)) {
            return;
        }
        try {
            GeoId geoId = availabilityService.findOrCreateGeoId(view.getGeoId(), sessionId);
            if (geoId == null) {
                throw new StoreException(String.format("GeoID %s konnte nicht ermittelt werden!", view.getGeoId()));
            }

            GeoId2TechLocation result = availabilityService.findGeoId2TechLocation(view.getGeoId(), hvtStandort.getId());
            if (result == null) {
                GeoId2TechLocation geoId2TechLocation = new GeoId2TechLocation();
                geoId2TechLocation.setGeoId(view.getGeoId());
                geoId2TechLocation.setHvtIdStandort(hvtStandort.getId());
                geoId2TechLocation.setTalLength(1L);
                geoId2TechLocation.setTalLengthTrusted(Boolean.FALSE);
                availabilityService.saveGeoId2TechLocation(geoId2TechLocation, sessionId);
            }

            // Rangierungsmatrix anlegen
            String[] createRangMatrizen4TechLocationTypes = new String[] {
                    FTTXStandortImportView.STANDORT_TYP_FTTB,
                    FTTXStandortImportView.STANDORT_TYP_FTTB_H};
            if (StringTools.isIn(view.getStandortTyp().toUpperCase(), createRangMatrizen4TechLocationTypes)) {
                generateRM4FTTB(hvtStandort.getId(), sessionId);
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    /**
     * Generiert die Rangierungsmatrix fuer den angegebenen Standort fuer alle Produkte aus den Produktgruppen,
     * die in {@link ProduktGruppe#PRODUKTGRUPPEN_2_CREATE_RANG_MATRIX} definiert sind.
     *
     * @param hvtStandortId       hvtStandortId
     * @param sessionId           sessionId
     * @throws StoreException     StoreException
     */
    private void generateRM4FTTB(Long hvtStandortId, Long sessionId) throws StoreException {
        if (hvtStandortId == null) {
            throw new StoreException("Rangierungsmatrix kann nicht erzeugt werden, da kein Standort definiert ist.");
        }
        try {
            // Liste der Produkt Ids, fuer die eine Rangierungsmatrix erstellt
            List<Long> rmProduktIds = new ArrayList<>();
            List<Produkt> rmProdukte = produktService.findProdukte4PGs(ProduktGruppe.PRODUKTGRUPPEN_2_CREATE_RANG_MATRIX);
            if (CollectionTools.isNotEmpty(rmProdukte)) {
                for (Produkt produkt : rmProdukte) {
                    rmProduktIds.add(produkt.getId());
                }
            }

            List<UEVT> uevts = hvtService.findUEVTs4HVTStandort(hvtStandortId);
            UEVT uevt;
            if (CollectionTools.isEmpty(uevts)) {
                // UEVT anlegen
                uevt = new UEVT();
                uevt.setHvtIdStandort(hvtStandortId);
                uevt.setUevt("0001");
                hvtService.saveUEVT(uevt);
            }
            else {
                uevt = uevts.get(0);
            }

            // Rangierungsmatrix anlegen
            List<Long> uevtIds = new ArrayList<>();
            uevtIds.add(uevt.getId());
            List<Rangierungsmatrix> createdMatrix = rangierungsService.createMatrix(sessionId, uevtIds, rmProduktIds, null);

            if (CollectionTools.isEmpty(createdMatrix)) {
                throw new StoreException("Es konnten keine Rangierungsmatrizen angelegt werden.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    /**
     * Generiert eine IP Adresse fuer MDU bzw. ONT
     */
    private String generateIpMdu(HWOltChild oltChild, Long oltRackId) throws FindException {
        if ((oltRackId == null) || (oltChild == null)) {
            return null;
        }
        try {
            String ipAddress = null;

            // Olt laden
            HWRack model = hwService.findRackById(oltRackId);
            if ((model != null) && (model instanceof HWOlt)) {
                HWOlt olt = (HWOlt) model;
                ipAddress = hwService.generateOltChildIp(olt, oltChild);
            }
            return ipAddress;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
    }

    @Override
    public HWOnt generateFTTHOnt(FTTHOntImportView model, Long sessionId) throws StoreException {
        if ((model == null) || (sessionId == null)) {
            return null;
        }
        try{
            HWOnt ont = new HWOnt();
            ont.setOntType(model.getModellnummer());
            ont.setRackTyp(HWRack.RACK_TYPE_ONT);
            generateOltChildFromView(model, ont);
            return hwService.saveHWRack(ont);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public HWDpo generateFTTBHDpo(FTTBHDpoImportView model, Long sessionId) throws StoreException {
        if ((model == null) || (sessionId == null)) {
            return null;
        }
        try{
            HWDpo dpo = new HWDpo();
            dpo.setDpoType(model.getModellnummer());
            dpo.setRackTyp(HWRack.RACK_TYPE_DPO);
            dpo.setChassisIdentifier(model.getChassisIdentifier());
            dpo.setChassisSlot(model.getChassisSlot());
            generateOltChildFromView(model, dpo);
            return hwService.saveHWRack(dpo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    private void generateOltChildFromView(OltChildImportView model, HWOltChild hwOltChild) throws StoreException, FindException {
        // Ermittle HVT-Standort
        HVTStandort hvtStandort = hvtService.findHVTStandortByBezeichnung(model.getStandort());
        if (hvtStandort == null) {
            throw new StoreException(MSG_TEXT_STANDORTBEZ + model.getStandort() + MSG_TEXT_EXISTIERT_NICHT);
        }

        // Pruefe, ob OLT-Child bereits existiert
        if (hwService.findActiveRackByBezeichnung(model.getBezeichnung()) != null) {
            throw new StoreException("OLT_Child " + model.getBezeichnung() + MSG_TEXT_EXISTIERT);
        }

        // Ermittle Hersteller
        HVTTechnik hvtTechnik = hvtService.findHVTTechnikByHersteller(model.getHersteller());
        if (hvtTechnik == null) {
            throw new StoreException("Hersteller " + model.getHersteller() + MSG_TEXT_EXISTIERT_NICHT);
        }

        // HVT-Technik dem Standort zuordnen, falls noch nicht geschehen.
        assignHvtTechnikToStandort(hvtTechnik, hvtStandort);

        // OLT ermitteln
        HWRack oltOrGslam = hwService.findRackByBezeichnung(model.getOlt());
        if (oltOrGslam == null) {
            oltOrGslam = hwService.findGslamByAltBez(model.getOlt());
            if (oltOrGslam == null) {
                throw new StoreException("OLT/GSLAM " + model.getOlt() + " kann nicht ermittelt werden!");
            }
        }
        else if (hwService.findGslamByAltBez(model.getOlt()) != null) {
            throw new StoreException("Sowohl eine OLT als auch ein GSLAM existieren unter der Bezeichnung "
                    + model.getOlt() + "!");
        }

        // OltChild-Datensatz erzeugen
        hwOltChild.setGeraeteBez(model.getBezeichnung());
        hwOltChild.setGueltigVon(new Date());
        hwOltChild.setGueltigBis(DateTools.getHurricanEndDate());
        hwOltChild.setHvtIdStandort(hvtStandort.getId());
        hwOltChild.setHwProducer(hvtTechnik.getId());
        hwOltChild.setSerialNo(model.getSeriennummer());
        hwOltChild.setOltRackId(oltOrGslam.getId());
        hwOltChild.setOltFrame(model.getOltRack() == null ? null : StringUtils.trimToNull(model.getOltRack().toString()));
        hwOltChild.setOltSubrack(model.getOltSubrack() == null ? null : StringUtils.trimToNull(model.getOltSubrack().toString()));
        hwOltChild.setOltSlot(model.getOltSlot() == null ? null : StringUtils.trimToNull(model.getOltSlot().toString()));
        hwOltChild.setOltGPONPort(model.getOltPort() == null ? null : StringUtils.trimToNull(model.getOltPort().toString()));
        hwOltChild.setOltGPONId(model.getGponId() == null ? null : StringUtils.trimToNull(model.getGponId().toString()));

        // HVT-Raum anlegen
        if (StringUtils.isEmpty(model.getRaumbezeichung())) {
            throw new StoreException("Raumbezeichnung ist leer!");
        }
        HVTRaum ontRaum = hvtService.findHVTRaumByName(hvtStandort.getId(), model.getRaumbezeichung());
        if (ontRaum == null) {
            ontRaum = new HVTRaum();
            ontRaum.setHvtIdStandort(hvtStandort.getId());
            ontRaum.setRaum(model.getRaumbezeichung());
            hvtService.saveHVTRaum(ontRaum);
        }
        hwOltChild.setHvtRaumId(ontRaum.getId());
    }

    String getOntModulnummer(String port) {
        if (StringUtils.isEmpty(port)) {
            return null;
        }
        Equipment equipment = new Equipment();
        equipment.setHwEQN(port);
        String ssType = equipment.getHwEQNPart(Equipment.HWEQNPART_FTTX_ONT_PORT_SS);
        if (ssType != null) {
            return "0-" + ssType;
        }
        return null;
    }

    @Override
    public void generateFTTHOntPort(FTTHOntPortImportView view, Long sessionId) throws StoreException {
        if ((view == null) || (sessionId == null)) {
            return;
        }
        try {
            // User ermitteln
            AKUser user = checkUser(sessionId);

            // Ermittle ONT
            HWOnt ont = (HWOnt) hwService.findActiveRackByBezeichnung(view.getOltChild());
            if (ont == null) {
                throw new StoreException(String.format("ONT mit der Bezeichnung %s existiert in Hurrican nicht.",
                        view.getOltChild()));
            }

            // rangierung ab sofort freigeben
            final Date gueltigAb = new Date();

            // Ermittle zugehoerige Baugruppe
            String modulnummer = getOntModulnummer(view.getPort());
            HWBaugruppe hwBaugruppe = getHWBaugruppe(ont, ont.getOntType(), view.getSchnittstelle(), modulnummer);

            // Physiktyp laden
            PhysikTyp fttxPhysikTyp = getPhysikTyp("FTTH_" + view.getSchnittstelle());

            if (assertPortDoesNotAlreadyExist(view.getPort(), ont, hwBaugruppe)) {
                Equipment ontEquipment = new Equipment();
                ontEquipment.setHvtIdStandort(ont.getHvtIdStandort());
                definePortValues(ontEquipment, view.getPort(), view.getSchnittstelle(), hwBaugruppe, user, EqStatus.rang);
                createRangierung4Port(ontEquipment, gueltigAb, ont, fttxPhysikTyp, user);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public void generateFTTBHDpoPort(FTTBHDpoPortImportView view, Long sessionId) throws StoreException {
        if (view == null || sessionId == null) {
            return;
        }
        try {
            // User ermitteln
            AKUser user = checkUser(sessionId);

            // Ermittle DPO
            HWDpo dpo = (HWDpo) hwService.findActiveRackByBezeichnung(view.getOltChild());
            if (dpo == null) {
                throw new StoreException(String.format("DPO mit der Bezeichnung %s existiert in Hurrican nicht.",
                        view.getOltChild()));
            }

            // rangierung ab sofort freigeben
            final Date gueltigAb = new Date();

            // Ermittle zugehoerige Baugruppe
            HWBaugruppe hwBaugruppe = getHWBaugruppe(dpo, dpo.getDpoType(), view.getSchnittstelle(), getOntModulnummer(view.getPort()));

            // Physiktyp laden
            PhysikTyp fttbPhysikTyp = getPhysikTyp("FTTB_DPO_" + view.getSchnittstelle());

            if (assertPortDoesNotAlreadyExist(view.getPort(), dpo, hwBaugruppe)) {
                Equipment dpoEquipment = getEquipmentDpoDpu(dpo.getHvtIdStandort(), view.getLeiste(), view.getStift());
                definePortValues(dpoEquipment, view.getPort(), view.getSchnittstelle(), hwBaugruppe, user, EqStatus.rang);
                createRangierung4Port(dpoEquipment, gueltigAb, dpo, fttbPhysikTyp, user);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    /**
    * Equipment der Dpo/Dpu-Leiste ermitteln (ueber Leiste/Stift)
    * (Das Equipment muss mit der angegebenen Leiste/Stift bereits eingespielt sein, bevor die Dpo/Dpu-Information darauf gebucht wird.)
    */
    Equipment getEquipmentDpoDpu(Long hvtIdStandort, String viewLeiste, String viewStift)throws StoreException, FindException{
        if (StringUtils.isNotEmpty(viewLeiste) && StringUtils.isNotEmpty(viewStift)) {
            return getEquipment(hvtIdStandort, viewLeiste, viewStift);
        }
        else if (StringUtils.isNotEmpty(viewLeiste) || StringUtils.isNotEmpty(viewStift)) {
            throw new StoreException(String.format("Die Charactestics 'leiste' und 'stift' muessen entweder beide " +
                            "gesetzt oder beide leer sein. Folgende Werte wurden uebermittelt leiste=%s und stift=%s.",
                    viewLeiste, viewStift));
        }
        else {
            Equipment equipment = new Equipment();
            equipment.setHvtIdStandort(hvtIdStandort);
            return equipment;
        }
    }

    /**
     * Equipment der MDU-Leiste ermitteln (ueber Leiste/Stift)
     * (Das Equipment muss mit der angegebenen Leiste/Stift bereits eingespielt sein, bevor die MDU-Information darauf gebucht wird.)
     */
    Equipment getEquipment(Long hvtIdStandort, String leiste, String stift) throws FindException, StoreException {
        Equipment equipment;
        equipment = rangierungsService.findEQByLeisteStift(hvtIdStandort, leiste, stift);
        if (equipment == null) {
            equipment = rangierungsService.findEQByLeisteStift(hvtIdStandort, leiste, StringUtils.leftPad(stift, 2, '0'));
            if (equipment == null) {
                throw new StoreException(String.format("Der Equipment-Datensatz fuer Stift %s-%s ist nicht vorhanden!", leiste, stift));
            }
        }

        if (!EnumSet.of(RangSchnittstelle.FTTB_MNET,
                RangSchnittstelle.FTTB_LZV,
                RangSchnittstelle.FTTB_LVT).contains(equipment.getRangSchnittstelle())) {
            throw new StoreException(String.format("Der Equipment-Datensatz fuer Stift %s-%s ist keiner " +
                    "MNET-Leiste zugeordnet!", leiste, stift));
        }
        if (StringUtils.isNotBlank(equipment.getHwEQN())) {
            throw new StoreException(String.format("Dem Equipment-Datensatz %s ist bereits ein Port zugeordnet", equipment.getId()));
        }
        return equipment;
    }

    PhysikTyp getPhysikTyp(String pyhsikTypName) throws FindException, StoreException {
        PhysikTyp fttxPhysikTyp = physikService.findPhysikTypByName(pyhsikTypName);
        if (fttxPhysikTyp == null) {
            throw new StoreException(String.format("Physiktyp %s kann nicht ermittelt werden!", pyhsikTypName));
        }
        return fttxPhysikTyp;
    }

    HWBaugruppe getHWBaugruppe(HWRack hwRack, String bgTyp, String schnittstelle, String modulnummer) throws Exception {
        String bgTypAlternativ = bgTyp + "_" + schnittstelle;
        HWBaugruppenTyp baugruppenTyp = getBaugruppenTyp(bgTyp, bgTypAlternativ);
        return findOrCreateBaugruppe(modulnummer, hwRack, baugruppenTyp);
    }

    @Override
    public Set<AuftragDaten> findAuftraege4OltChild(HWOltChild hwOltChild) throws FindException {
        Set<AuftragDaten> auftragDatenSet = Sets.newHashSet();

        List<HWBaugruppe> baugruppen = hwService.findBaugruppen4Rack(hwOltChild.getId());
        if (baugruppen == null || baugruppen.isEmpty()) {
            return auftragDatenSet;
        }

        for (HWBaugruppe baugruppe : baugruppen) {
            List<Equipment> existingEquipments = rangierungsService.findEquipments4HWBaugruppe(baugruppe.getId());

            if (existingEquipments != null) {
                for (Equipment equipment : existingEquipments) {
                    Rangierung existingRangierung = rangierungsService.findRangierung4Equipment(equipment.getId());
                    if (existingRangierung != null) {
                        List<Endstelle> existingEndstellen = endstellenService.findEndstellenWithRangierId(existingRangierung.getId());
                        for (Endstelle endstelle : existingEndstellen) {
                            AuftragDaten existingAuftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
                            if (existingAuftragDaten != null) {
                                auftragDatenSet.add(existingAuftragDaten);
                            }
                        }
                    }
                }
            }
        }
        return auftragDatenSet;
    }

    @Override
    public boolean checkIfOntFieldsComplete(HWOnt hwOnt) {
        return checkIfOltChildFieldsComplete(hwOnt);

    }

    @Override
    public boolean checkIfDpoFieldsComplete(HWDpo hwDpo) {
        return checkIfOltChildFieldsComplete(hwDpo);
    }

    boolean checkIfOltChildFieldsComplete(HWOltChild hwOltChild) {
        return !(hwOltChild.getGeraeteBez() == null
                || hwOltChild.getHvtRaumId() == null
                || hwOltChild.getSerialNo() == null);
    }


    @Override
    public boolean isOntEqual(final FTTHOntImportView ontImportView, final HWOnt ont) throws FindException {
        return isOltChildEqual(ontImportView, ont) && compareStringField(ont.getOntType(), ontImportView.getModellnummer(), true);
    }

    @Override
    public boolean isDpoEqual(FTTBHDpoImportView dpoImportView, HWDpo dpo) throws FindException {
        return isOltChildEqual(dpoImportView, dpo) && compareStringField(dpo.getDpoType(), dpoImportView.getModellnummer(), true);
    }

    @Override
    public HWDpu generateFTTBDpu(FTTBDpuImportView model, Long sessionId) throws StoreException {
        if ((model == null) || (sessionId == null)) {
            return null;
        }
        try{
            HWDpu dpu = new HWDpu();
            dpu.setDpuType(model.getModellnummer());
            dpu.setRackTyp(HWRack.RACK_TYPE_DPU);
            dpu.setReversePower(model.getReversePower());
            dpu.setTddProfil(getDefaultTddProfile());
            generateOltChildFromView(model, dpu);
            return hwService.saveHWRack(dpu);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public void generateFTTBDpuPort(FTTBDpuPortImportView view, Long sessionId) throws StoreException {
        if (view == null || sessionId == null) {
            return;
        }
        try {
            // User ermitteln
            AKUser user = checkUser(sessionId);

            // Ermittle DPU
            HWDpu dpu = determineDpu(view);

            // Ermittle zugehoerige Baugruppe
            HWBaugruppe hwBaugruppe = findHwBaugruppe4DpuPort(view, dpu);

            // Physiktyp laden
            PhysikTyp fttbPhysikTyp = getPhysikTyp("FTTB_" + view.getSchnittstelle());

            // Rangierung noch nicht freigeben - Freigabe erfolgt in FinishCPSTxCommand#doTxSourceDependentActions
            final Date gueltigAb = (dpu.getFreigabe() == null)? DateTools.getHurricanEndDate() : new Date();

            if (assertPortDoesNotAlreadyExist(view.getPort(), dpu, hwBaugruppe)) {
                Equipment dpuEquipment = getEquipmentDpoDpu(dpu.getHvtIdStandort(), view.getLeiste(), view.getStift());
                definePortValues(dpuEquipment, view.getPort(), view.getSchnittstelle(), hwBaugruppe, user, EqStatus.rang);
                createRangierung4Port(dpuEquipment, gueltigAb, dpu, fttbPhysikTyp, user);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    private HWBaugruppe findHwBaugruppe4DpuPort(FTTBDpuPortImportView view, HWDpu dpu) throws Exception {
        HWBaugruppe hwBaugruppe;
        if("dpuport-1".equals(view.getResourceSpecId())){
            hwBaugruppe = getHWBaugruppe(dpu, dpu.getDpuType(), view.getSchnittstelle(), getOntModulnummer(view.getPort()));
        } else if ("dpuport-2".equals(view.getResourceSpecId())){
            hwBaugruppe = getHwBaugruppe4Karte(view.getParent());
        } else {
            throw new StoreException("ResourceSpec " + view.getResourceSpecId() + " kann nicht verarbeitet werden");
        }
        return hwBaugruppe;
    }

    private HWBaugruppe getHwBaugruppe4Karte(String parent) throws Exception {
        HWBaugruppe bg =  hwService.findBaugruppe4RackWithModName(parent);
        if (bg == null){
            throw new StoreException(String.format("Baugruppe mit ModName %s existiert in Hurrican nicht.", parent));
        }
        return bg;
    }

    private HWDpu determineDpu(FTTBDpuPortImportView view) throws FindException, StoreException {
        HWDpu dpu = (HWDpu) hwService.findActiveRackByBezeichnung(view.getOltChild().split("_")[0]);
        if (dpu == null) {
            throw new StoreException(String.format("DPU mit der Bezeichnung %s existiert in Hurrican nicht.",
                    view.getOltChild()));
        }
        return dpu;
    }

    private AKUser checkUser(Long sessionId) throws AKAuthenticationException, StoreException {
        AKUser user = getAKUserBySessionId(sessionId);
        if (user == null) {
            throw new StoreException("Kann User nicht ermitteln");
        }
        return user;
    }

    @Override
    public void generateFTTBDpuKarte(FTTBDpuKarteImportView view, Long sessionId) throws StoreException {
        try {
            HWRack dpuRack = hwService.findRackByBezeichnung(view.getDpu());
            HWBaugruppenTyp baugruppenTyp4Kartentyp = hwService.findBaugruppenTypByName(view.getKartentyp());
            String modNumber = view.getKarte();
            String modName= view.getKarteId();
            findOrCreateBaugruppe(modNumber,dpuRack,baugruppenTyp4Kartentyp,modName);
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public boolean checkIfDpuFieldsComplete(HWDpu hwDpu) {
        return checkIfOltChildFieldsComplete(hwDpu);
    }

    @Override
    public boolean isDpuEqual(FTTBDpuImportView dpuImportView, HWDpu dpu) throws FindException {
        return isOltChildEqual(dpuImportView, dpu) && compareStringField(dpu.getDpuType(), dpuImportView.getModellnummer(), true);
    }

    boolean isOltChildEqual(final OltChildImportView viewOltChild, final HWOltChild hwOltChild) throws FindException {
        final HVTRaum hvtRaum = (hwOltChild.getHvtRaumId() != null) ? hvtService.findHVTRaum(hwOltChild.getHvtRaumId()) : null;
        final HVTTechnik producer = hvtService.findHVTTechnik(hwOltChild.getHwProducer());
        final HVTGruppe hvtGruppe = hvtService.findHVTGruppe4Standort(hwOltChild.getHvtIdStandort());
        final HWRack hwRackOlt = hwService.findRackById(hwOltChild.getOltRackId());
        final HWOlt hwOlt = (hwRackOlt instanceof HWOlt) ? (HWOlt) hwRackOlt : null;
        final HWDslam hwDslam = (hwRackOlt instanceof HWDslam) ? (HWDslam) hwRackOlt : null;
        return compareStringField(hwOltChild.getGeraeteBez(), viewOltChild.getBezeichnung(), true)
                && compareStringField(hwOltChild.getOltFrame(), getLongAsString(viewOltChild.getOltRack()), true)
                && compareStringField(hwOltChild.getOltSubrack(), getLongAsString(viewOltChild.getOltSubrack()), true)
                && compareStringField(hwOltChild.getOltSlot(), getLongAsString(viewOltChild.getOltSlot()), true)
                && compareStringField(hwOltChild.getOltGPONPort(), getLongAsString(viewOltChild.getOltPort()), true)
                && compareStringField(hwOltChild.getOltGPONId(), getLongAsString(viewOltChild.getGponId()), true)
                && compareStringField((hvtRaum != null) ? hvtRaum.getRaum() : null, viewOltChild.getRaumbezeichung(), true)
                && compareStringField((producer != null) ? producer.getHersteller() : null, viewOltChild.getHersteller(), true)
                && ((hwOlt != null && compareStringField(hwOlt.getGeraeteBez(), viewOltChild.getOlt(), true))
                || (hwDslam != null && compareStringField(hwDslam.getAltGslamBez(), viewOltChild.getOlt(), true))
                || (hwOlt == null && hwDslam == null && viewOltChild.getOlt() == null))
                && compareStringField((hvtGruppe != null) ? hvtGruppe.getOrtsteil() : null, viewOltChild.getStandort(), true);
    }

    @Override
    public Long checkHwOltChildForActiveAuftraegeAndDelete(HWOltChild hwOltChild, boolean checkLastCpsTx, Long sessionId) throws FindException, StoreException, ValidationException {

        String activeAuftragIds = checkHwOltChildForActiveAuftraege(hwOltChild);
        if (activeAuftragIds != null) {
            String rackTyp = hwOltChild.getRackTyp();
            throw new FindException(String.format("%s Loeschung mit Geraetebezeichnung %s abgelehnt! " +
                            "Zur %s existieren folgende aktive Auftraege: %s",
                    rackTyp, hwOltChild.getGeraeteBez(), rackTyp, activeAuftragIds));
        }else {
            return deleteHwOltChildWithCpsTx(hwOltChild, checkLastCpsTx, sessionId);
        }

    }


    @Override
    public String checkHwOltChildForActiveAuftraege(HWOltChild hwOltChild) throws FindException {
        if (hwOltChild == null) {
            return null;
        }

        Set<AuftragDaten> auftragDatenResultSet = Sets.newHashSet();
        StringBuilder strBuffAuftragsId = new StringBuilder();

        auftragDatenResultSet.addAll(findAuftraege4OltChild(hwOltChild));

        for (AuftragDaten auftragDaten : auftragDatenResultSet) {
            if (!auftragDaten.isAuftragClosed()) {
                if (strBuffAuftragsId.length() > 0) {
                    strBuffAuftragsId.append(", ");
                }
                strBuffAuftragsId.append(auftragDaten.getAuftragId().toString());
            }
        }

        return (strBuffAuftragsId.length() > 0) ? strBuffAuftragsId.toString() : null;
    }

    @Override
    public Long deleteHwOltChildWithCpsTx(HWOltChild hwOltChild, boolean checkLastCpsTx, final Long sessionId)
            throws FindException, StoreException, ValidationException {

        deleteHwOltChild(hwOltChild);
        if (checkLastCpsTx) {
            boolean isDeleteExecuteable = cpsService.isCpsTxServiceOrderTypeExecuteable(hwOltChild.getId(),
                    CPSTransaction.SERVICE_ORDER_TYPE_DELETE_DEVICE);
            if (!isDeleteExecuteable) {
                List<CPSTransaction> pendingTransactions = cpsService
                        .findActiveCPSTransactions(null, hwOltChild.getId(), null);
                if (pendingTransactions.isEmpty()) {
                    return null;
                }
            }
        }
        return createAndSendCpsTxForOltChild(hwOltChild, CPSTransaction.SERVICE_ORDER_TYPE_DELETE_DEVICE,
                sessionId);
    }


    private String getLongAsString(Long in) {
        return (in != null) ? in.toString() : null;
    }

    private boolean compareStringField(String left, String right, boolean nullAllowed) {
        if (nullAllowed && right == null) {
            return true;
        }
        return StringUtils.equals(left, right);
    }

    @Override
    public void deleteHwOltChild(HWOltChild existingOltChild) throws FindException, StoreException, ValidationException {
        final Date now = new Date();

        if (DateTools.isAfter(existingOltChild.getGueltigBis(), now)) {
            existingOltChild.setGueltigBis(now);
            hwService.saveHWRack(existingOltChild);
        }

        rangierungFreigabeService.beendenHwRackRangierungen(existingOltChild.getId(), now,
                (existingOltChild instanceof HWDpo)? true: false);
    }

    private Long createAndSendCpsTxForOltChild(final HWOltChild oltChild,
            final Long serviceOrderType, final Long sessionId) throws StoreException {

        if (oltChild.getSerialNo() == null) {
            return null;
        }

        final CPSTransactionResult cpsTxResult =
                cpsService.createCPSTransaction4OltChild(oltChild.getId(), serviceOrderType, sessionId);
        if (CollectionTools.isNotEmpty(cpsTxResult.getCpsTransactions())) {
            final CPSTransaction cpsTx = cpsTxResult.getCpsTransactions().get(0);
            cpsService.sendCpsTx2CPSAsyncWithoutNewTx(cpsTx, sessionId);
            return cpsTx.getId();
        }
        return null;
    }

    private String getDefaultTddProfile() {
        List<Reference> referencesByType = null;
        try {
            referencesByType = referenceService.findReferencesByType(Reference.REF_TYPE_HW_RACK_DPU_TDD_PROFIL, true);
            if (referencesByType == null || referencesByType.isEmpty()) {
                return null;
            }
        }
        catch (Exception e) {
            return null;
        }
        return referencesByType.get(0).getStrValue();
    }
}
