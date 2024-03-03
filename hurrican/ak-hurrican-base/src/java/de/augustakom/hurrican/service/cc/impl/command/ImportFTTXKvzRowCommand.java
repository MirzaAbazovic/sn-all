/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 12:09:00
 */
package de.augustakom.hurrican.service.cc.impl.command;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.poi.XlsPoiTool;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortTechType;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationType;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.view.XlsImportResultView.SingleRowResult;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.ImportException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Command Impl. um einen FTTX KVZ (genau eine Zeile des Imports) zu importieren. Läuft in einer eigenen Transaktion.
 *
 *
 */
public class ImportFTTXKvzRowCommand extends AbstractXlsImportCommand {

    private static final int KVZ_HVTGRUPPE_START_COLUMN = 0;
    private static final int KVZ_HVTSTANDORT_START_COLUMN = 10;
    private static final int KVZ_UEVT_START_COLUMN = 15;
    private static final int KVZ_RACK_START_COLUMN = 16;
    private static final int KVZ_SUBRACK_START_COLUMN = 28;
    private static final int KVZ_TECHTYPE_COLUMN = 30;

    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;

    @Resource(name = "de.augustakom.hurrican.service.cc.HWSwitchService")
    private HWSwitchService hwSwitchService;

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Resource(name = "de.augustakom.hurrican.service.cc.QueryCCService")
    private QueryCCService queryService;

    @Resource(name = "de.augustakom.hurrican.service.cc.NiederlassungService")
    private NiederlassungService niederlassungService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;

    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;

    @Override
    @CcTxRequiresNew
    public Object execute() throws Exception {
        Row row = getPreparedValue(PARAM_IMPORT_ROW, Row.class, false, "Keine zu parsende Zeile übergeben");
        Long sessionId = getPreparedValue(PARAM_SESSION_ID, Long.class, false, "Keine SessionId übergeben");
        SingleRowResult importResult = new SingleRowResult();

        HVTGruppe hvtGruppe = readHVTGruppe(row, importResult);
        HVTStandort hvtStandort = readHVTStandort(row, importResult, hvtGruppe);

        String uevtStr = XlsPoiTool.getContentAsString(row, KVZ_UEVT_START_COLUMN);
        if (StringUtils.isEmpty(uevtStr)) {
            importResult.addWarning("Kein UEVT im XLS vorhanden");
            return importResult;
        }

        readUEVT(row, sessionId, importResult, hvtStandort);

        String dslamGeraeteBez = XlsPoiTool.getContentAsString(row, KVZ_RACK_START_COLUMN);
        if (StringUtils.isEmpty(dslamGeraeteBez)) {
            importResult.addWarning("Kein DSLAM (Rack/Subrack) im XLS vorhanden");
            return importResult;
        }

        HWRack dslamByBez = hwService.findRackByBezeichnung(dslamGeraeteBez);
        if (dslamByBez != null) {
            if ((dslamByBez instanceof HWDslam) && dslamByBez.getHvtIdStandort().equals(hvtStandort.getId())) {
                importResult.addWarning("DSLAM '" + dslamGeraeteBez + "' bereits angelegt");
            }
            else {
                throw new FindException("Für die Gerätebez. '" + dslamGeraeteBez
                        + "' existiert bereits ein Gerät vom Typ '" + dslamByBez.getRackTyp() + "'");
            }
        }
        else {
            HWDslam dslam = readDslamAndSubrack(row, hvtStandort);
            // HVTStandort2Technik aus DLSAM
            hvtService.saveHVTTechniken4Standort(hvtStandort.getId(), Collections.singletonList(dslam.getHwProducer()));
        }

        readHVTStandortTechType(row, importResult, hvtStandort, sessionId);

        return importResult;
    }

    /**
     * @param row
     * @param importResult
     * @param hvtStandort
     * @param sessionId
     * @throws FindException
     * @throws StoreException
     */
    private void readHVTStandortTechType(Row row, SingleRowResult importResult, HVTStandort hvtStandort,
            Long sessionId) throws FindException, StoreException {
        // Technologie fix VDSL2
        Reference vdsl2 = referenceService.findReference(HVTStandortTechType.TechnologyType.VDSL2.getRefId());

        List<HVTStandortTechType> techTypes4HVTStandort = hvtService.findTechTypes4HVTStandort(hvtStandort.getId());
        for (HVTStandortTechType hvtStandortTechType : techTypes4HVTStandort) {
            if (hvtStandortTechType.getTechnologyTypeReference().equals(vdsl2)) {
                importResult.addWarning("HVT-Standort-TechType bereits angelegt");
                return;
            }
        }

        HVTStandortTechType standortTechType = new HVTStandortTechType();
        Date avFromDate = XlsPoiTool.getContentAsDate(row, KVZ_TECHTYPE_COLUMN);
        if (avFromDate == null) {
            importResult.addWarning("HVT-Standort-TechType nicht angelegt, kein Verfügbar-von Datum im XLS vorhanden");
            return;
        }
        standortTechType.setAvailableFrom(avFromDate);
        standortTechType.setAvailableTo(DateTools.getHurricanEndDate());
        standortTechType.setHvtIdStandort(hvtStandort.getId());
        standortTechType.setTechnologyTypeReference(vdsl2);
        hvtService.saveTechType(standortTechType, sessionId);
    }

    /**
     * Liest DSLAM und Subrack. Prüfung auf eindeutige Gerätebezeichnung muss vorher erfolgen.
     *
     * @param row
     * @param hvtStandort
     * @return
     * @throws StoreException
     * @throws ValidationException
     * @throws FindException
     * @throws ImportException
     */
    private HWDslam readDslamAndSubrack(Row row, HVTStandort hvtStandort)
            throws StoreException, ValidationException, FindException, ImportException {
        int col = KVZ_RACK_START_COLUMN;
        HWDslam dslam = new HWDslam();
        dslam.setGeraeteBez(XlsPoiTool.getContentAsString(row, col++));
        dslam.setManagementBez(XlsPoiTool.getContentAsString(row, col++));
        dslam.setAnlagenBez(XlsPoiTool.getContentAsString(row, col++));
        String hvtTechnikHersteller = XlsPoiTool.getContentAsString(row, col++);
        if (StringUtils.isEmpty(hvtTechnikHersteller)) {
            throw new ImportException("HVTTechnik (Hersteller für DSLAM) muss angegeben werden");
        }
        HVTTechnik hvtTechnik = hvtService.findHVTTechnikByHersteller(hvtTechnikHersteller);
        if (hvtTechnik != null) {
            dslam.setHwProducer(hvtTechnik.getId());
        }
        else {
            throw new FindException("HVTTechnik (Hersteller für DSLAM) '" + hvtTechnikHersteller + "' nicht gefunden");
        }
        dslam.setIpAdress(XlsPoiTool.getContentAsString(row, col++));
        dslam.setOuterTagADSL(XlsPoiTool.getContentAsInt(row, col++));
        dslam.setBrasOuterTagADSL(XlsPoiTool.getContentAsInt(row, col++));
        dslam.setOuterTagVoip(XlsPoiTool.getContentAsInt(row, col++));
        dslam.setBrasOuterTagVoip(XlsPoiTool.getContentAsInt(row, col++));
        dslam.setOuterTagCpeMgmt(XlsPoiTool.getContentAsInt(row, col++));
        dslam.setOuterTagIadMgmt(XlsPoiTool.getContentAsInt(row, col++));
        String dslamType = XlsPoiTool.getContentAsString(row, col++);
        if(StringUtils.isNotEmpty(dslamType)) {
            assertValidDslamType(dslamType);
            dslam.setDslamType(dslamType);
        }
        dslam.setRackTyp(HWRack.RACK_TYPE_DSLAM);
        dslam.setGueltigVon(new Date());
        dslam.setGueltigBis(DateTools.getHurricanEndDate());
        dslam.setPhysikArt("VLAN");
        dslam.setHvtIdStandort(hvtStandort.getId());
        dslam = hwService.saveHWRack(dslam);

        HWSubrack hwSubrack = new HWSubrack();
        col = KVZ_SUBRACK_START_COLUMN;
        hwSubrack.setSubrackTyp(findSubrackTyp(dslam.getRackTyp(), XlsPoiTool.getContentAsString(row, col++)));
        String subRackModulNumber = XlsPoiTool.getContentAsString(row, col++);
        if (StringUtils.isEmpty(subRackModulNumber)) {
            throw new ImportException("ModulNummer des Subracks muss angegeben werden");
        }
        hwSubrack.setModNumber(subRackModulNumber);
        hwSubrack.setRackId(dslam.getId());
        hwService.saveHWSubrack(hwSubrack);
        return dslam;
    }

    protected void assertValidDslamType(String dslamType) throws FindException {
        List<Reference> validDslamTypes = referenceService.findReferencesByType(Reference.REF_TYPE_HW_DSLAM_TYPE, false);
        for (Reference reference :validDslamTypes  ) {
            if(dslamType.equals(reference.getStrValue())) {
                return;
            }
        }
        throw new FindException(String.format("DSLAM Modell '%s' ist nicht bekannt", dslamType));
    }

    /**
     * @param row
     * @param sessionId
     * @param importResult
     * @param hvtStandort
     * @throws FindException
     * @throws StoreException
     */
    private void readUEVT(Row row, Long sessionId, SingleRowResult importResult, HVTStandort hvtStandort)
            throws FindException, StoreException {
        UEVT uevt = new UEVT();
        uevt.setUevt(XlsPoiTool.getContentAsString(row, KVZ_UEVT_START_COLUMN));
        uevt.setHvtIdStandort(hvtStandort.getId());
        try {
            List<UEVT> uevts = queryService.findByExample(uevt, UEVT.class);
            if (!uevts.isEmpty()) {
                importResult.addWarning("UEVT '" + uevt.getUevt() + "' bereits angelegt");
                return;
            }
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }

        uevt = hvtService.saveUEVT(uevt);

        // Rangierungsmatrix erstellen
        List<Produkt> produkte = produktService.findProdukte(true);
        List<Long> produktIds = new ArrayList<>(produkte.size());
        for (Produkt produkt : produkte) {
            List<Produkt2TechLocationType> produkt2TechLocationTypes = produktService
                    .findProdukt2TechLocationTypes(produkt.getId());
            if (Produkt2TechLocationType.containsTechLocationTypeRefId(produkt2TechLocationTypes,
                    HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                produktIds.add(produkt.getId());
            }
        }
        rangierungsService.createMatrix(sessionId, Collections.singletonList(uevt.getId()), produktIds, null);
    }

    /**
     * @param row
     * @param importResult
     * @param hvtGruppe
     * @return
     * @throws FindException
     * @throws StoreException
     * @throws ImportException
     */
    private HVTStandort readHVTStandort(Row row, SingleRowResult importResult, HVTGruppe hvtGruppe)
            throws FindException, StoreException, ImportException {
        int col = KVZ_HVTSTANDORT_START_COLUMN;
        Integer asb = XlsPoiTool.getContentAsInt(row, col++);
        if (asb == null) {
            throw new ImportException("ASB des HVT Standortes fehlt");
        }
        List<HVTStandort> existingStandorte = hvtService.findHVTStandorte4Gruppe(hvtGruppe.getId(), true);
        for (HVTStandort existingStandort : existingStandorte) {
            if (existingStandort.getStandortTypRefId().equals(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ)) {
                importResult.addWarning("HVTStandort für Gruppe '" + hvtGruppe.getOrtsteil()
                        + "' bereits angelegt");
                return existingStandort;
            }
            else {
                throw new ImportException("vorhandene Standort für  Gruppe '" + hvtGruppe.getOrtsteil() + " mit ASB '"
                        + existingStandort.getAsb() + "' hat falschen StandortTyp");
            }
        }

        HVTStandort hvtStandort = new HVTStandort();
        hvtStandort.setAsb(hvtService.generateAsb4HVTStandort(asb));
        hvtStandort.setBeschreibung(XlsPoiTool.getContentAsString(row, col++));
        hvtStandort.setCarrierId(Carrier.ID_DTAG);
        String carrierKennBez = XlsPoiTool.getContentAsString(row, col++);
        CarrierKennung carrierKennung = findCarrierKennung(hvtStandort.getCarrierId(), carrierKennBez);
        if (carrierKennung != null) {
            hvtStandort.setCarrierKennungId(carrierKennung.getId());
        }
        else {
            importResult.addWarning("CarrierKennung '" + carrierKennBez + "' nicht gefunden");
        }
        String carrierKontaktBez = XlsPoiTool.getContentAsString(row, col++);
        CarrierContact carrierContact = findCarrierContact(hvtStandort.getCarrierId(), carrierKontaktBez);
        if (carrierContact != null) {
            hvtStandort.setCarrierContactId(carrierContact.getId());
        }
        else {
            importResult.addWarning("CarrierContact '" + carrierKontaktBez + "' nicht gefunden");
        }
        String betriebsraumBez = XlsPoiTool.getContentAsString(row, col++);
        if (StringUtils.isEmpty(betriebsraumBez)) {
            throw new ImportException("HVT des MFG muss angegeben werden");
        }
        else {
            HVTStandort hvtBetriebsraum = hvtService.findHVTStandortByBezeichnung(betriebsraumBez);
            if (hvtBetriebsraum == null) {
                throw new FindException("HVT des MFG '" + betriebsraumBez + "' nicht gefunden");
            }
            else if (!hvtBetriebsraum.getStandortTypRefId().equals(HVTStandort.HVT_STANDORT_TYP_HVT)) {
                throw new ImportException("HVT des MFG '" + betriebsraumBez + "' muss ein Standorttyp HVT sein");
            }
            else {
                hvtStandort.setBetriebsraumId(hvtBetriebsraum.getId());
            }
        }
        hvtStandort.setHvtGruppeId(hvtGruppe.getId());
        hvtStandort.setStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ);
        hvtStandort.setGesicherteRealisierung(HVTStandort.GESICHERT_IN_BETRIEB);
        hvtStandort.setGueltigVon(new Date());
        hvtStandort.setGueltigBis(DateTools.getHurricanEndDate());
        hvtStandort.setEwsdOr1(0);
        hvtStandort.setCpsProvisioning(Boolean.TRUE);
        hvtStandort.setAutoVerteilen(Boolean.TRUE);
        hvtStandort = hvtService.saveHVTStandort(hvtStandort);
        return hvtStandort;
    }

    /**
     * @param row
     * @param importResult
     * @return
     * @throws FindException
     * @throws StoreException
     * @throws ImportException
     */
    private HVTGruppe readHVTGruppe(Row row, SingleRowResult importResult) throws FindException, StoreException,
            ImportException {
        int col = KVZ_HVTGRUPPE_START_COLUMN;
        String onkz = XlsPoiTool.getContentAsString(row, col++);
        String hvtName = XlsPoiTool.getContentAsString(row, col++);
        if (StringUtils.isEmpty(hvtName)) {
            throw new ImportException("HVT-Name der Gruppe fehlt");
        }

        HVTGruppe existingHvtGruppe = hvtService.findHVTGruppeByBezeichnung(hvtName);
        if (existingHvtGruppe != null) {
            importResult.addWarning("HVTGruppe '" + hvtName + "' bereits angelegt");
            return existingHvtGruppe;
        }

        HVTGruppe hvtGruppe = new HVTGruppe();
        hvtGruppe.setOnkz(onkz);
        hvtGruppe.setOrtsteil(hvtName);
        hvtGruppe.setStrasse(XlsPoiTool.getContentAsString(row, col++));
        hvtGruppe.setHausNr(XlsPoiTool.getContentAsString(row, col++));
        hvtGruppe.setPlz(XlsPoiTool.getContentAsString(row, col++));
        hvtGruppe.setOrt(XlsPoiTool.getContentAsString(row, col++));
        String switchName = XlsPoiTool.getContentAsString(row, col++);
        if (switchName != null) {
            HWSwitch hwSwitch = hwSwitchService.findSwitchByName(switchName);
            hvtGruppe.setHwSwitch(hwSwitch);
            if (hwSwitch == null) {
                importResult.addWarning("Switch '" + switchName + "' nicht gefunden");
            }
        }
        String nlName = XlsPoiTool.getContentAsString(row, col++);
        Niederlassung nl = niederlassungService.findNiederlassungByName(nlName);
        if (nl != null) {
            hvtGruppe.setNiederlassungId(nl.getId());
        }
        else {
            importResult.addWarning("Niederlassung '" + nlName + "' nicht gefunden");
        }
        hvtGruppe.setKostenstelle(XlsPoiTool.getContentAsString(row, col++));
        hvtGruppe.setInnenauftrag(XlsPoiTool.getContentAsString(row, col++));
        hvtGruppe.setExport4Portal(Boolean.FALSE);
        hvtGruppe = hvtService.saveHVTGruppe(hvtGruppe);
        return hvtGruppe;
    }

    /**
     * @param carrierId
     * @param branchOffice
     * @return
     * @throws FindException
     */
    private CarrierContact findCarrierContact(Long carrierId, String branchOffice) throws FindException {
        CarrierContact contact = new CarrierContact();
        contact.setCarrierId(carrierId);
        contact.setBranchOffice(branchOffice);
        try {
            List<CarrierContact> contacts = queryService.findByExample(contact, CarrierContact.class);
            if (!contacts.isEmpty()) {
                return contacts.get(0);
            }
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return null;
    }

    /**
     * @param carrierId
     * @param bezeichnung
     * @return
     * @throws FindException
     */
    private CarrierKennung findCarrierKennung(Long carrierId, String bezeichnung) throws FindException {
        CarrierKennung kennung = new CarrierKennung();
        kennung.setCarrierId(carrierId);
        kennung.setBezeichnung(bezeichnung);
        try {
            List<CarrierKennung> kennungen = queryService.findByExample(kennung, CarrierKennung.class);
            if (!kennungen.isEmpty()) {
                return kennungen.get(0);
            }
        }
        catch (Exception e) {
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return null;
    }

    /**
     * Sucht einen Subrack Typ anhand des RackTyp und Subrack Name.
     *
     * @param rackType
     * @param name
     * @return
     * @throws FindException wenn kein passender Subrack Typ gefunden wird
     */
    private HWSubrackTyp findSubrackTyp(String rackType, String name) throws FindException {
        List<HWSubrackTyp> subrackTypes = hwService.findAllSubrackTypes(rackType);
        for (HWSubrackTyp subrackTyp : subrackTypes) {
            if (subrackTyp.getName().equals(name)) {
                return subrackTyp;
            }
        }
        throw new FindException("Kein Subrack vom Typ '" + name + "' für Rack '" + rackType + "' gefunden");
    }

}
