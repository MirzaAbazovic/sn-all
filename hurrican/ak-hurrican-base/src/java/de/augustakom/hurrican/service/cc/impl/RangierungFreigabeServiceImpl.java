/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2012 15:23:52
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.PortGesamtDAO;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.RangierungFreigabeInfo;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.OEService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungFreigabeService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.SdslEquipmentService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.augustakom.hurrican.service.cc.impl.command.EWSDAutoFreigabeCommand;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Implementierung von {@link RangierungFreigabeService}
 */
@CcTxRequired
public class RangierungFreigabeServiceImpl extends DefaultCCService implements RangierungFreigabeService {

    private static final Logger LOGGER = Logger.getLogger(RangierungFreigabeServiceImpl.class);

    static final String ADSL2PLUS = "ADSL2+";

    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "portGesamtDAO")
    private PortGesamtDAO portGesamtDAO;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.billing.OEService")
    private OEService oeService;
    @Resource(name = "de.augustakom.hurrican.service.billing.KundenService")
    private KundenService kundenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierElTALService")
    private CarrierElTALService carrierElTalService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.fttx.VlanService")
    private VlanService vlanService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EQCrossConnectionService")
    private EQCrossConnectionService crossconnectionService;
    @Resource(name = "de.augustakom.hurrican.service.cc.SdslEquipmentService")
    private SdslEquipmentService sdslEquipmentService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Override
    public Map<Long, List<PhysikFreigebenView>> createPhysikFreigabeView(Date freigabeDatum,
            List<PhysikFreigebenView> freigabeList, Boolean onlyKlaerfaelle) throws FindException {
        try {
            Map<Long, List<PhysikFreigebenView>> rangierungRelationen = new HashMap<>();
            List<PhysikFreigebenView> freizugebendeRangierungen = portGesamtDAO
                    .createPhysikFreigabeView(DateUtils.truncate(freigabeDatum, Calendar.DAY_OF_MONTH), onlyKlaerfaelle);
            if (CollectionTools.isNotEmpty(freizugebendeRangierungen)) {
                Long rangierKey = null;
                List<PhysikFreigebenView> views = null;
                if (freigabeList != null) {
                    freigabeList.clear();
                }
                // Achtung: For Schleife geht davon aus, dass die Liste der Rangierungen nach RANGIER_ID
                // und AUFTRAG_ID sortiert ist!
                for (PhysikFreigebenView freigabeView : freizugebendeRangierungen) {
                    if (freigabeView.getAuftragNoOrig() != null) {
                        // ProduktBilling aus dem Billing-System laden
                        freigabeView.setBillingProduct(oeService.findProduktName4Auftrag(freigabeView
                                .getAuftragNoOrig()));
                    }
                    if (freigabeView.getKundenNo() != null) {
                        // Ist Kunde nach §95 TKG gesperrt?
                        Kunde kunde = kundenService.findKunde(freigabeView.getKundenNo());
                        boolean isLocked = (kunde != null) ? kunde.isLocked() : false;
                        freigabeView.setIsLocked(Boolean.valueOf(isLocked));
                    }
                    else {
                        freigabeView.setIsLocked(Boolean.FALSE);
                    }
                    if (rangierKey == null) {
                        rangierKey = freigabeView.getRangierId();
                        views = new ArrayList<>();
                        views.add(freigabeView);
                    }
                    else {
                        if (NumberTools.equal(rangierKey, freigabeView.getRangierId())) {
                            if (views != null) { // if avoids 'possible null pointer' warning
                                views.add(freigabeView);
                            }
                        }
                        else {
                            rangierungRelationen.put(rangierKey, views);
                            rangierKey = freigabeView.getRangierId();
                            views = new ArrayList<>();
                            views.add(freigabeView);
                        }
                    }
                    if (freigabeList != null) {
                        freigabeList.add(freigabeView);
                    }
                }
                rangierungRelationen.put(rangierKey, views);
            }

            return rangierungRelationen;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void rangierungenFreigeben(final Map<Long, List<PhysikFreigebenView>> rangierungRelationen)
            throws StoreException {
        if (MapUtils.isEmpty(rangierungRelationen)) {
            return;
        }

        Long rangierIdHelper = null;
        try {
            final Set<Long> keys = rangierungRelationen.keySet();
            if (CollectionTools.isNotEmpty(keys)) {
                for (Long rangierId : keys) {
                    rangierIdHelper = rangierId;
                    final Rangierung rangierung = rangierungsService.findRangierung(rangierId);
                    rangierungFreigeben(rangierungRelationen, rangierung);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_RANGIERUNG_FREIGEBEN, new Object[] { rangierIdHelper,
                    e.getMessage() }, e);
        }
    }

    private void rangierungFreigeben(final Map<Long, List<PhysikFreigebenView>> rangierungRelationen,
            final Rangierung rangierung)
            throws StoreException, FindException {
        final Long rangierId = rangierung.getId();
        List<PhysikFreigebenView> views = rangierungRelationen.get(rangierId);
        if (CollectionTools.isNotEmpty(views)) {
            boolean freigeben = isRangierungCleared(views);
            if (freigeben) {
                migrateAdsl2PlusFromH04ToH13(rangierung.getPhysikTypId(), rangierung.getEqOutId());
                freigebenRangierung(rangierung, null, false);
                migrateAtm2EfmIfPossible(rangierung);
                for (PhysikFreigebenView order : views) {
                    RangierungFreigabeInfo rangierungFreigabeInfo = findRangierungFreigabeInfo(rangierId,
                            order.getAuftragId());
                    if (rangierungFreigabeInfo != null) {
                        portGesamtDAO.deleteRangierungFreigabeInfoById(rangierungFreigabeInfo.getId());
                    }
                }
            }
        }
    }

    /**
     * ATM-Ports werden auf EFM umgestellt, wenn ein kompletter 4er-Block an Ports frei ist
     *
     * @param rangierung
     * @throws StoreException, FindException
     */
    public boolean migrateAtm2EfmIfPossible(final Rangierung rangierung) throws StoreException, FindException {
        // Prüfung eqInId auf null, da z.B. bei Gewofag beim Freigeben der Rangierung die EqInId auf null gesetzt wird
        if (rangierung.getEqInId() == null) {
            return false;
        }

        final List<Equipment> viererBlock =
                sdslEquipmentService.findViererBlockForSdslEquipmentOnAlcatelIpOrHuaweiDslam(rangierung.getEqInId());
        if (viererBlock.isEmpty() || !rangierungsService.isListeOfPortsFree(viererBlock)) {
            return false;
        }

        final Schicht2Protokoll schicht2Protokoll = viererBlock.get(0).getSchicht2ProtokollOrAtm();
        if (!schicht2Protokoll.equals(Schicht2Protokoll.ATM)) {
            return false;
        }

        for (final Equipment eq : viererBlock) {
            eq.setSchicht2Protokoll(Schicht2Protokoll.EFM);
            rangierungsService.saveEquipment(eq);
        }
        return true;
    }

    /**
     * Migration von H04 Uebertragungsverfahren bei gleichzeitiger Verwendung von Adsl2Plus auf H13.
     */
    private void migrateAdsl2PlusFromH04ToH13(Long physikTypId, Long eqOutId) throws FindException,
            StoreException {
        final Equipment equipmentOut = rangierungsService.findEquipment(eqOutId);

        if (rangierungIsH04AndAdsl2Plus(physikTypId, equipmentOut)) {
            equipmentOut.setUetv(Uebertragungsverfahren.H13);
            rangierungsService.saveEquipment(equipmentOut);
        }
    }

    private boolean rangierungIsH04AndAdsl2Plus(final Long physikTypId, final Equipment equipmentOut)
            throws FindException {
        return rangierungEqOutHasH04(equipmentOut) && rangierungIsAdsl2Plus(physikTypId);
    }

    private boolean rangierungEqOutHasH04(final Equipment equipmentOut) {
        boolean h04 = false;
        if (equipmentOut != null) {
            final Uebertragungsverfahren uebertragungsverfahren = equipmentOut.getUetv();
            h04 = (uebertragungsverfahren == Uebertragungsverfahren.H04);
        }
        return h04;
    }

    private boolean rangierungIsAdsl2Plus(final Long physikTypId) throws FindException {
        boolean adsl2Plus = false;
        // gueltige Rangierung muss einen Physiktyp haben, andere Faelle koennen ignoriert werden!
        if (physikTypId != null) {
            final PhysikTyp physikTyp = physikService.findPhysikTyp(physikTypId);
            final String transferMethod = physikTyp.getCpsTransferMethod();
            adsl2Plus = ADSL2PLUS.equals(Strings.nullToEmpty(transferMethod));
        }
        return adsl2Plus;
    }

    private boolean isRangierungCleared(List<PhysikFreigebenView> views) {
        if (CollectionTools.isNotEmpty(views)) {
            for (PhysikFreigebenView order : views) {
                if (!BooleanTools.nullToFalse(order.getFreigeben())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Rangierung freigebenRangierung(Rangierung rang, String bemerkung, boolean makeHistory) throws StoreException {
        Rangierung result = null;
        if (rang == null) {
            return null;
        }
        try {
            // Setzt das Flag 'manual_configuration' des verbundenen EQ_INs zurueck.
            if (rang.getEqInId() != null) {
                Equipment equipment = rangierungsService.findEquipment(rang.getEqInId());
                if ((equipment != null) && BooleanTools.nullToFalse(equipment.getManualConfiguration())) {
                    equipment.setManualConfiguration(Boolean.FALSE);
                    rangierungsService.saveEquipment(equipment);
                }
            }
            Rangierung editedRangierung = rang;
            // Pruefe Physiktyp
            if (NumberTools.isIn(editedRangierung.getPhysikTypId(),
                    new Long[] {PhysikTyp.PHYSIKTYP_FTTB_DPO_VDSL, PhysikTyp.PHYSIKTYP_FTTB_VDSL,
                            PhysikTyp.PHYSIKTYP_FTTB_POTS, PhysikTyp.PHYSIKTYP_FTTB_RF})) {
                // FTTB Rangierung freigeben
                editedRangierung.setEqOutId(null);
                editedRangierung.setEsId(null);
                editedRangierung.setFreigabeAb(null);
                editedRangierung.setBemerkung(bemerkung);
                removeCrossconnectionsAndVlans(editedRangierung);
                result = rangierungsService.saveRangierung(editedRangierung, makeHistory);
            }
            else if (NumberTools.isIn(editedRangierung.getPhysikTypId(),
                    new Long[] {PhysikTyp.PHYSIKTYP_FTTH_ETH, PhysikTyp.PHYSIKTYP_FTTH_POTS,
                            PhysikTyp.PHYSIKTYP_FTTH_RF})) {
                // FTTH_ETH, FTTH_POTS und FTTH_RF Rangierungen freigeben
                // Nach Klaerung mit Fachbereich soll die ONT zur Wiederverwendung nicht beendet werden ->
                // Rangierung 'aufraeumen' und freigeben
                editedRangierung.setEqOutId(null);
                editedRangierung.setEsId(null);
                editedRangierung.setFreigabeAb(null);
                editedRangierung.setBemerkung(bemerkung);
                removeCrossconnectionsAndVlans(editedRangierung);
                result = rangierungsService.saveRangierung(editedRangierung, makeHistory);
            }
            else if (NumberTools.isIn(editedRangierung.getPhysikTypId(),
                    new Long[] {PhysikTyp.PHYSIKTYP_FTTH,})) {
                // 'alte' FTTH Rangierung und Ports beenden
                // diese Ports sollten nicht mehr an aktiven Auftraegen haengen und schon gar nicht
                // wiederverwendet werden
                editedRangierung.setFreigegeben(Freigegeben.gesperrt);
                Date freigabeAb = rang.getFreigabeAb();
                editedRangierung.setGueltigBis(freigabeAb);
                editedRangierung.setEsId(null);
                editedRangierung.setFreigabeAb(null);
                editedRangierung.setBemerkung(bemerkung);
                result = rangierungsService.saveRangierung(editedRangierung, makeHistory);
                // Status des Equipment-Datensatzes aendern
                final Equipment equipmentIn = rangierungsService.findEquipment(editedRangierung.getEqInId());
                if (equipmentIn != null) {
                    equipmentIn.setStatus(EqStatus.locked);
                    equipmentIn.setGueltigBis(freigabeAb);
                    rangierungsService.saveEquipment(equipmentIn);
                }
            }
            else {
                // Pruefe, ob Rangierung aufgetrennt werden muss
                if (editedRangierung.getHvtIdStandort() != null) {
                    HVTStandort hvt = hvtService.findHVTStandort(editedRangierung.getHvtIdStandort());
                    // Sonderfall HVT-Sheridan bzw. GEWOFAG
                    if (BooleanTools.nullToFalse(hvt.getBreakRangierung()) && (editedRangierung.getEqOutId() != null)) {
                        // Falls EQ-Out verfuegbar -> auftrennen
                        Equipment equipmentOut = rangierungsService.findEquipment(editedRangierung.getEqOutId());
                        if (equipmentOut != null) {
                            // falls GEWOFAG-Standort: EQ-Out und EQ-In auftrennen; sonst nur EQ-Out (HVT-Sheridan)!
                            boolean breakEqIn = hvt.isStandortType(HVTStandort.HVT_STANDORT_TYP_GEWOFAG);
                            editedRangierung = rangierungsService.breakRangierung(editedRangierung, breakEqIn,
                                    true, true);
                        }
                    }
                }

                // Freigabe der Rangierung
                editedRangierung.setFreigabeAb(null);
                editedRangierung.setEsId(null);
                editedRangierung.setBemerkung(bemerkung);
                removeCrossconnectionsAndVlans(editedRangierung);
                result = rangierungsService.saveRangierung(editedRangierung, makeHistory);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_RANGIERUNG_FREIGEBEN, new Object[] {
                    rang.getId(), e.getMessage() }, e);
        }
        return result;
    }

    private void removeCrossconnectionsAndVlans(Rangierung rangierung) throws DeleteException {
        if ((rangierung == null) || (rangierung.getEqInId() == null)) {
            return;
        }
        crossconnectionService.deleteEQCrossConnectionsOfEquipment(rangierung.getEqInId());
        vlanService.removeEqVlans(rangierung.getEqInId());
    }

    @Override
    public AKWarnings saveRangierungFreigabeInfos(Map<Long, List<PhysikFreigebenView>> rangierungRelationen)
            throws StoreException {
        AKWarnings warnings = new AKWarnings();

        if (MapUtils.isNotEmpty(rangierungRelationen)) {
            Set<Long> keys = rangierungRelationen.keySet();
            if (CollectionTools.isNotEmpty(keys)) {
                for (Long rangierId : keys) {
                    saveRangierungFreigabeInfosHelper(rangierungRelationen, warnings, rangierId);
                }
            }
        }
        return warnings;
    }

    private void saveRangierungFreigabeInfosHelper(Map<Long, List<PhysikFreigebenView>> rangierungRelationen,
            AKWarnings warnings, Long rangierId) {
        List<PhysikFreigebenView> views = rangierungRelationen.get(rangierId);
        if (CollectionTools.isNotEmpty(views)) {
            for (PhysikFreigebenView order : views) {
                try {
                    RangierungFreigabeInfo rangierungFreigabeInfo = findRangierungFreigabeInfo(rangierId,
                            order.getAuftragId());
                    if (rangierungFreigabeInfo != null) {
                        if (order.getClarifyInfo() != null) {
                            rangierungFreigabeInfo.setInfo(order.getClarifyInfo());
                            rangierungFreigabeInfo.setInBearbeitung(order.getInBearbeitung());
                            saveRangierungFreigabeInfo(rangierungFreigabeInfo);
                        }
                        else {
                            portGesamtDAO.deleteRangierungFreigabeInfoById(rangierungFreigabeInfo.getId());
                        }
                    }
                    else if (order.getClarifyInfo() != null) {
                        rangierungFreigabeInfo = new RangierungFreigabeInfo();
                        rangierungFreigabeInfo.setRangierId(rangierId);
                        rangierungFreigabeInfo.setAuftragId(order.getAuftragId());
                        rangierungFreigabeInfo.setInfo(order.getClarifyInfo());
                        rangierungFreigabeInfo.setInBearbeitung(order.getInBearbeitung());
                        saveRangierungFreigabeInfo(rangierungFreigabeInfo);
                    }
                }
                catch (Exception e) {
                    warnings.addAKWarning(this, String.format(
                            "%sSpeichern der KlärfallInfo '%s' zum Auftrag %s fehlgeschlagen.",
                            SystemUtils.LINE_SEPARATOR, order.getClarifyInfo(), order.getAuftragId()));
                }
            }
        }
    }

    @Override
    public RangierungFreigabeInfo findRangierungFreigabeInfo(Long rangierId, Long auftragId) throws FindException {
        if (rangierId == null) {
            return null;
        }

        try {
            RangierungFreigabeInfo example = new RangierungFreigabeInfo();
            example.setRangierId(rangierId);
            example.setAuftragId(auftragId);

            List<RangierungFreigabeInfo> rangierungFreigabeInfos = portGesamtDAO.queryByExample(example,
                    RangierungFreigabeInfo.class);
            return ((rangierungFreigabeInfos != null) && (rangierungFreigabeInfos.size() == 1)) ? rangierungFreigabeInfos
                    .get(0)
                    : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }

    }

    @Override
    public void saveRangierungFreigabeInfo(RangierungFreigabeInfo toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            portGesamtDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AKWarnings prepareAutomaticClearance(Map<Long, List<PhysikFreigebenView>> rangierungRelationen)
            throws Exception {
        if (MapUtils.isEmpty(rangierungRelationen)) {
            return new AKWarnings();
        }

        IServiceCommand cmd = serviceLocator.getCmdBean(EWSDAutoFreigabeCommand.class);
        cmd.prepare(EWSDAutoFreigabeCommand.RANGIER_RELATIONEN_MAP, rangierungRelationen);
        AKWarnings warnings = (AKWarnings) cmd.execute();
        return warnings;
    }

    @Override
    public void removeRangierung(Endstelle endstelle, Rangierung rangierung, Rangierung rangierungAdd,
            Date freigabeAb, String bemerkung, Long sessionId) throws StoreException {
        if (endstelle == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            if ((rangierung == null) && (rangierungAdd == null)) {
                throw new StoreException("Keine Rangierung vorhanden, die entfernt werden könnte");
            }
            if (rangierung != null) {
                removeRangierungInt(endstelle, rangierung, freigabeAb, bemerkung, false, sessionId);
            }
            if (rangierungAdd != null) {
                removeRangierungInt(endstelle, rangierungAdd, freigabeAb, bemerkung, true, sessionId);
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Interne Funktion, die die übergebenen Rangierung von der Endstelle trennt, falls die Konsistenzprüfungen aus
     * {@link RangierungFreigabeService#removeRangierung(Endstelle, Rangierung, Rangierung, Date, String, Long)} erfüllt sind.
     *
     * @param endstelle        die Endstelle zur Rangierung (not null)
     * @param rangierung       die Rangierung zur Endstelle (not null)
     * @param isRangAdditional gibt an, ob rangierungAdditional oder nicht
     * @param freigabeAb       falls nicht null, gibt das Datum an, bis zu dem die Rangierung fuer die automatische
     *                         Vergabe gesperrt sein soll
     * @param bemerkung        Bemerkung, die auf die Rangierung geschrieben wird
     * @throws FindException            falls unerwarteter Fehler beim Suchen der notwendigen Daten für die Checks
     *                                  auftritt.
     * @throws ServiceNotFoundException falls ein benötiger Service nicht gefunden wird
     * @throws StoreException           falls eine Konsistenzprüfung oder ein unerwarteter Fehler auftritt.
     */
    private void removeRangierungInt(Endstelle endstelle, Rangierung rangierung, Date freigabeAb,
            String bemerkung, boolean isRangAdditional, Long sessionId) throws FindException, StoreException,
            ServiceNotFoundException {
        String rangStr = (isRangAdditional) ? "RangierungAdd" : "Rangierung";

        // Rangierung ist dem Auftrag bzw. der Endstelle zugeordnet und umgekehrt (bidirektionale Referenzierung)
        if (!((rangierung.getEsId() != null) && (rangierung.getEsId().equals(endstelle.getId())))) {
            throw new StoreException("Endstelle ist nicht der " + rangStr + " zugeordnet.");
        }
        Long endstelleRangId = (isRangAdditional) ? endstelle.getRangierIdAdditional() : endstelle.getRangierId();
        if (!((endstelle.hasRangierung()) && (endstelleRangId.equals(rangierung.getId())))) {
            throw new StoreException(rangStr + " ist nicht der Endstelle zugeordnet.");
        }

        // Rangierung ist freigegeben
        if (!rangierung.getFreigegebenBoolean()) {
            throw new StoreException(rangStr + " ist nicht freigegeben.");
        }

        // Rangierung ist nicht historisiert
        if (rangierungsService.findHistoryFrom(rangierung.getId()) != null) {
            throw new StoreException(rangStr + " ist historisiert.");
        }

        AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelleTx(endstelle.getId());
        if (auftragDaten == null) {
            throw new StoreException("Keine AuftragDaten zur Endstelle mit der Id '" + endstelle.getId()
                    + "' gefunden.");
        }
        // Auftragsstatus < "Projektierung"
        if (!(auftragDaten.getAuftragStatusId() < AuftragStatus.PROJEKTIERUNG_ERLEDIGT)) {
            throw new StoreException("In dem aktuellen Auftragsstatus darf die Rangierung nicht entfernt werden.");
        }
        // Physik-Übernahme darf nicht existieren
        if (physikService.findLastPhysikUebernahme(auftragDaten.getAuftragId()) != null) {
            throw new StoreException("Zum Auftrag der Endstelle existiert eine Physikübernahme-Protokollierung.");
        }

        // verbundene Carrierbestellung hat keine Leitungsbezeichnung sowie Vertragsnummer eingetragen
        // und es existiert kein elektronischer Vorgang
        List<Carrierbestellung> carrierbestellungen = carrierService.findCBs4EndstelleTx(endstelle.getId());
        for (Carrierbestellung carrierbestellung : carrierbestellungen) {
            if ((carrierbestellung.getLbz() != null) || (carrierbestellung.getVtrNr() != null)) {
                throw new StoreException("Carrierbestellung mit Id '" + carrierbestellung.getId()
                        + "' hat bereits eine Leitungsbezeichnung und/oder Vertragsnr.");
            }

            if (!carrierElTalService.findCBVorgaenge4CB(carrierbestellung.getId()).isEmpty()) {
                throw new StoreException("Für Carrierbestellung mit Id '" + carrierbestellung.getId()
                        + "' existiert bereits eine elektronische TAL-Bestellung.");
            }
        }

        // setze Daten
        if (isRangAdditional) {
            endstelle.setRangierIdAdditional(null);
        }
        else {
            endstelle.setRangierId(null);
        }

        if (StringUtils.isBlank(bemerkung)) {
            bemerkung = String.format("Physic removed from order at %s",
                    DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL));
        }
        if (freigabeAb != null) {
            rangierung.setBemerkung(bemerkung);
            rangierung.setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
            rangierung
                    .setFreigabeAb(new java.sql.Date(DateUtils.truncate(freigabeAb, Calendar.DAY_OF_MONTH).getTime()));
            rangierung.setUserW(getLoginNameSilent(sessionId));
            rangierungsService.saveRangierung(rangierung, false);
        }
        else {
            // Rangierung zu sofort freigeben
            rangierung
                    .setFreigabeAb(new java.sql.Date(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime()));
            freigebenRangierung(rangierung, bemerkung, false);
        }

        endstellenService.saveEndstelle(endstelle);
    }

    @Override
    public boolean rangierungenFreigeben(Long rangierId, Long rangierIdAdd, Long sessionId) throws StoreException {
        Assert.notNull(rangierId, "Keine Rangierung übergeben.");
        Assert.notNull(sessionId, "Keine SessionId übergeben.");

        final List<Rangierung> rangierungen = new ArrayList<>();
        try {
            final Rangierung rang = rangierungsService.findRangierung(rangierId);
            Assert.notNull(rang, "Keine Rangierung für übergeben rangierId gefunden.");
            rangierungen.add(rang);

            final Rangierung rangAdd = rangierungsService.findRangierung(rangierIdAdd);
            if (rangAdd != null) {
                rangierungen.add(rangAdd);

                Assert.isTrue((rang.getLeitungGesamtId() == null) ? rangAdd.getLeitungGesamtId() == null :
                        rang.getLeitungGesamtId().equals(rangAdd.getLeitungGesamtId())
                        , "Rangierungen haben unterschiedliche Leitung-Gesamt-Id");
            }

            boolean canFreigeben = true;
            for (Rangierung rangierung : rangierungen) {
                canFreigeben = canFreigeben && rangierung.isRangierungFreigabebereit()
                        && ((rangierung.getFreigabeAb() == null)
                        || DateTools.isDateBeforeOrEqual(rangierung.getFreigabeAb(), new Date()));
            }

            if (canFreigeben) {
                final AKUser user = getAKUserBySessionId(sessionId);

                final List<Rangierung> result = new ArrayList<>();
                for (Rangierung rangierung : rangierungen) {
                    rangierung.setUserW(user.getLoginName());
                    rangierung.setDateW(new Date());
                    freigebenRangierung(rangierung, null, false);
                    result.add(rangierung);
                }
                return true;
            }
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            throw new StoreException(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void freigabeMduDpuRangierungen(Long rackId, Date date) throws StoreException {
        if ((rackId == null) || (date == null)) {
            return;
        }
        try {
            // Ermittle Baugruppen
            List<HWBaugruppe> bgs = hwService.findBaugruppen4Rack(rackId);
            if (CollectionTools.isEmpty(bgs)) {
                return;
            }
            for (HWBaugruppe bg : bgs) {
                // Ermittle alle Equipments zur Baugruppe
                List<Equipment> eqs = rangierungsService.findEquipments4HWBaugruppe(bg.getId());
                if (CollectionTools.isNotEmpty(eqs)) {
                    for (Equipment eq : eqs) {
                        // Suche Rangierung zum Equipment
                        Rangierung rang = rangierungsService.findRangierung4Equipment(eq.getId(), true);
                        // Falls Gueltig_von aelter als date, wird Datum aktualisiert
                        if ((rang != null) && DateTools.isAfter(rang.getGueltigVon(), date)) {
                            rang.setGueltigVon(date);
                            rangierungsService.saveRangierung(rang, false);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            throw new StoreException(e.getMessage(), e);
        }
    }

    @Override
    public void beendenHwRackRangierungen(Long rackId, Date date, boolean reuseEquipment)
            throws FindException, StoreException {

        List<HWBaugruppe> baugruppen = hwService.findBaugruppen4Rack(rackId);
        if (baugruppen != null) {
            for (HWBaugruppe baugruppe : baugruppen) {
                List<Equipment> existingEquipments = rangierungsService.findEquipments4HWBaugruppe(baugruppe.getId());

                if (existingEquipments != null) {
                    for (Equipment equipment : existingEquipments) {
                        Rangierung existingRangierung = rangierungsService.findRangierung4Equipment(equipment.getId());
                        if (existingRangierung != null && DateTools.isAfter(existingRangierung.getGueltigBis(), date)) {
                            existingRangierung.setGueltigBis(date);
                            existingRangierung.setEsId(null);
                            existingRangierung.setFreigegeben(Freigegeben.gesperrt);
                            rangierungsService.saveRangierung(existingRangierung, false);
                        }
                        equipmentBeendenOderFreigaben(equipment, date, reuseEquipment);
                    }
                }
            }
        }
    }

    private void equipmentBeendenOderFreigaben(Equipment equipment, Date date, boolean reuse) throws StoreException {
        if (reuse) {
            // den Stift wiederverwenden (DPO)
            equipment.setHwEQN(null);
            equipment.setHwSchnittstelle(null);
            equipment.setStatus(EqStatus.frei);
            equipment.setHwBaugruppenId(null);
            rangierungsService.saveEquipment(equipment);
        }
        else {
            // den Port beenden (ONT)
            if (DateTools.isAfter(equipment.getGueltigBis(), date)) {
                equipment.setGueltigBis(date);
                rangierungsService.saveEquipment(equipment);
            }
        }
    }

}
