/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2004 12:05:18
 */
package de.augustakom.hurrican.service.cc.impl;

import static java.lang.String.*;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.base.AKServiceCommandChain;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Function2;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.PhysikUebernahmeDAO;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.dao.cc.RangierungsmatrixDAO;
import de.augustakom.hurrican.exceptions.HurricanConcurrencyException;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Bandwidth;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.query.RangierungQuery;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;
import de.augustakom.hurrican.model.cc.temp.CarrierEquipmentDetails;
import de.augustakom.hurrican.model.cc.view.EquipmentInOutView;
import de.augustakom.hurrican.model.cc.view.HardwareEquipmentView;
import de.augustakom.hurrican.model.cc.view.RangierungWithEquipmentView;
import de.augustakom.hurrican.model.cc.view.RangierungsEquipmentView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.impl.command.AbstractAssignRangierungCommand;
import de.augustakom.hurrican.service.cc.impl.command.AssignRangierung2ESCommand;
import de.augustakom.hurrican.service.cc.impl.command.AssignRangierungByCarrierDetailsCommand;
import de.augustakom.hurrican.service.cc.impl.command.CreateMatrixCommand;
import de.augustakom.hurrican.service.cc.impl.command.FindRangierung4ESCommand;
import de.augustakom.hurrican.service.cc.impl.command.PhysiktypConsistenceCheckCommand;
import de.augustakom.hurrican.service.cc.impl.command.physik.AbstractPhysikCommand;
import de.augustakom.hurrican.service.cc.impl.ports.ChangeEqOutPortTool;
import de.augustakom.hurrican.service.utils.HistoryHelper;
import de.augustakom.hurrican.validation.cc.RangierungsmatrixValidator;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Service-Implementierung von <code>RangierungsService</code>.
 *
 *
 */
@CcTxRequired
public class RangierungsServiceImpl extends DefaultCCService implements RangierungsService {
    private static final Logger LOGGER = Logger.getLogger(RangierungsServiceImpl.class);

    private RangierungDAO rangierungDAO = null;
    private EquipmentDAO equipmentDAO = null;
    private PhysikUebernahmeDAO physikUebernahmeDAO = null;
    private RangierungsmatrixDAO rangierungsmatrixDAO = null;
    private RangierungsmatrixValidator rangierungsmatrixValidator = null;

    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;
    @Resource(name = "de.augustakom.hurrican.service.cc.AvailabilityService")
    private AvailabilityService availabilityService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ChainService")
    private ChainService chainService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CarrierService")
    private CarrierService carrierService;
    @Autowired
    private ServiceLocator serviceLocator;

    private PhysikService physikService;

    public void init() throws Exception {
        // Do not initialize PhysikService and CarrierElTALService here, since it is in another service registry which
        // might not be started and prevent this bean to be initialized
        // setCarrierElTalService(getCCService(CarrierElTALService.class));
        // setPhysikService(getCCService(PhysikService.class));
    }

    @Override
    public void rangierungFreigabebereit(Endstelle endstelle, Date datumFreigabe) throws StoreException {
        if ((endstelle == null) || !endstelle.hasRangierung()) {
            return;
        }
        try {
            Long[] rangierIds = endstelle.getRangierIds();
            RangierungDAO dao = getRangierungDAO();

            for (Long rangierId : rangierIds) {
                Rangierung rangierung = dao.findById(rangierId, Rangierung.class);
                if (rangierung != null) {
                    validateRangierung(endstelle, rangierung);

                    Date date = (datumFreigabe != null) ? datumFreigabe : new Date();
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(date);
                    cal.add(Calendar.DATE, DELAY_4_RANGIERUNGS_FREIGABE);

                    rangierung.setFreigabeAb(cal.getTime());
                    rangierung.setEsId(Rangierung.RANGIERUNG_NOT_ACTIVE);
                    rangierung.setBemerkung("gekündigt am " + DateTools.formatDate(new Date(), DateTools.PATTERN_DAY_MONTH_YEAR));
                    if (BooleanTools.nullToFalse(rangierung.getLeitungLoeschen())) {
                        rangierung.setLeitungGesamtId(null);
                        rangierung.setLeitungLfdNr(null);
                    }

                    dao.store(rangierung);
                }
                else {
                    throw new StoreException(
                            "Es konnte keine Rangierung gefunden werden, die freigegeben werden kann.");
                }
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    private void validateRangierung(Endstelle endstelle, Rangierung r) throws StoreException {
        if (!endstelle.getId().equals(r.getEsId()) &&
                NumberTools.notEqual(r.getEsId(), Rangierung.RANGIERUNG_NOT_ACTIVE)) {
            StringBuilder msg = new StringBuilder();
            msg.append("Die Rangierung ist einer anderen Endstelle zugeordnet als angegeben!\n");
            msg.append("Rangier-ID: {0}\nES-ID in Rangierung: {1}\ngefundene Endstellen-ID: {2}");
            String rId = (r.getId() != null) ? r.getId().toString() : "<null>";
            String rEsId = (r.getEsId() != null) ? r.getEsId().toString() : "<null>";
            String esId = (endstelle.getId() != null) ? endstelle.getId().toString() : "<null>";
            throw new StoreException(StringTools.formatString(msg.toString(),
                    new Object[] { rId, rEsId, esId }, null));
        }
    }

    @Override
    public Rangierung saveRangierung(Rangierung toSave, boolean makeHistory) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            RangierungDAO dao = getRangierungDAO();

            if (makeHistory && (toSave.getId() != null)) {
                Rangierung newRang = dao.update4History(toSave, toSave.getId(), new Date());

                newRang.setHistoryFrom(toSave.getId());
                newRang.setHistoryCount(NumberTools.add(newRang.getHistoryCount(), 1));
                dao.store(newRang);
                return newRang;
            }
            if (toSave.getId() != null) { // re-attach object to session to avoid NonUniqueObjectException
                Rangierung oldObject = dao.findById(toSave.getId(), Rangierung.class);
                PropertyUtils.copyProperties(oldObject, toSave);
                toSave = oldObject;
            }
            HistoryHelper.checkHistoryDates(toSave);
            dao.store(toSave);
            return toSave;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void assignEquipment2Rangierung(Rangierung toUse, Equipment eqIn, Equipment eqOut, boolean changeEqStatus,
            boolean makeHistory, boolean allowReplace) throws StoreException {
        if (toUse == null) {
            throw new StoreException("Keine Rangierung angegeben!");
        }
        if ((eqIn == null) && (eqOut == null)) {
            throw new StoreException("Es muss min. ein Equipment angegeben werden!");
        }

        boolean modified = false;

        try {
            if ((eqIn != null) && NumberTools.notEqual(toUse.getEqInId(), eqIn.getId())) {
                if ((toUse.getEqInId() != null) && !allowReplace) {
                    throw new StoreException("Rangierung besitzt bereits ein EQ_IN Equipment!");
                }
                getEquipmentDAO().refresh(eqIn);
                Rangierung existing = findRangierung4Equipment(eqIn.getId(), true);
                if ((existing != null) && !existing.equals(toUse)) {
                    throw new StoreException(
                            "Das Equipment " + eqIn.getHwEQN() + " ist bereits einer Rangierung zugeordnet!");
                }

                toUse.setEqInId(eqIn.getId());
                modified = true;
                if (changeEqStatus) {
                    Equipment previous = toUse.getEqInId() == null ? null : findEquipment(toUse.getEqInId());
                    if (previous != null) {
                        previous.setStatus(EqStatus.frei);
                        saveEquipment(previous);
                    }
                    eqIn.setStatus(EqStatus.rang);
                    saveEquipment(eqIn);
                }
            }

            if ((eqOut != null) && NumberTools.notEqual(toUse.getEqOutId(), eqOut.getId())) {
                if ((toUse.getEqOutId() != null) && !allowReplace) {
                    throw new StoreException("Rangierung besitzt bereits ein EQ_OUT Equipment!");
                }
                getEquipmentDAO().refresh(eqOut);
                Rangierung existing = findRangierung4Equipment(eqOut.getId(), false);
                if ((existing != null) && !existing.equals(toUse)) {
                    throw new StoreException(
                            "Das Equipment " + eqOut.getHwEQN() + " ist bereits einer Rangierung zugeordnet!");
                }

                toUse.setEqOutId(eqOut.getId());
                modified = true;
                if (changeEqStatus) {
                    Equipment previous = toUse.getEqOutId() == null ? null : findEquipment(toUse.getEqOutId());
                    if (previous != null) {
                        previous.setStatus(EqStatus.frei);
                        saveEquipment(previous);
                    }
                    eqOut.setStatus(EqStatus.rang);
                    saveEquipment(eqOut);
                }
            }

            if (modified) {
                saveRangierung(toUse, makeHistory);
            }
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Equipment-Zuordnung zu einer Rangierung: " + e.getMessage(), e);
        }
    }

    @Override
    public Rangierung breakRangierung(Rangierung toChange, boolean breakEqIn, boolean breakEqOut, boolean makeHistory)
            throws StoreException {
        if (toChange == null) {
            throw new StoreException("Keine Rangierung angegeben!");
        }
        try {
            boolean changed = false;
            if (breakEqIn && (toChange.getEqInId() != null)) {
                Equipment eqIn = findEquipment(toChange.getEqInId());
                if (EqStatus.rang.equals(eqIn.getStatus())) {
                    eqIn.setStatus(EqStatus.frei);
                }
                saveEquipment(eqIn);

                toChange.setEqInId(null);
                changed = true;
            }

            if (breakEqOut && (toChange.getEqOutId() != null)) {
                Equipment eqOut = findEquipment(toChange.getEqOutId());
                if (EqStatus.rang.equals(eqOut.getStatus())) {
                    eqOut.setStatus(EqStatus.frei);
                }
                saveEquipment(eqOut);

                toChange.setEqOutId(null);
                changed = true;
            }

            if (changed) {
                // bei einem Aufbruch wird die Rangierung historisiert!
                return saveRangierung(toChange, makeHistory);
            }
            return toChange;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Aufbrechen der Rangierung: " + e.getMessage(), e);
        }
    }

    @Override
    public Rangierung breakRangierungUsingNewEqStatus(Rangierung toChange, EqStatus newEqStatus, Long sessionId)
            throws StoreException {
        Assert.notNull(toChange, "Keine Rangierung angegeben.");
        Assert.notNull(newEqStatus, "Keine neuer EQ-Status angegeben.");
        Assert.notNull(sessionId, "Keine SessionId übergeben.");

        if (toChange.getEsId() != null) {
            throw new StoreException("Rangierung ist nicht frei (EsId != null) und kann nicht aufgebrochen werden.");
        }

        try {
            final AKUser user = getAKUserBySessionId(sessionId);

            if (toChange.getEqInId() != null) {
                Equipment eqIn = findEquipment(toChange.getEqInId());
                eqIn.setStatus(newEqStatus);
                eqIn.setUserW(user.getLoginName());
                eqIn.setDateW(new Date());
                saveEquipment(eqIn);
            }

            if (toChange.getEqOutId() != null) {
                Equipment eqOut = findEquipment(toChange.getEqOutId());
                eqOut.setStatus(newEqStatus);
                eqOut.setUserW(user.getLoginName());
                eqOut.setDateW(new Date());
                saveEquipment(eqOut);
            }

            toChange.setGueltigBis(DateTools.getActualSQLDate());
            toChange.setUserW(user.getLoginName());
            toChange.setDateW(new Date());

            return saveRangierung(toChange, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Aufbrechen der Rangierung: " + e.getMessage(), e);
        }
    }

    @Override
    public void reAttachAccessPoint(Rangierung rangierungToReAttach, boolean isDefaultRang) throws StoreException {
        try {
            if ((rangierungToReAttach.getEsId() != null) && !rangierungToReAttach.isRangierungFreigabebereit()) {
                Endstelle endstelle = endstellenService.findEndstelle(rangierungToReAttach.getEsId());

                if (isDefaultRang && NumberTools.notEqual(rangierungToReAttach.getId(), endstelle.getRangierId())) {
                    endstelle.setRangierId(rangierungToReAttach.getId());
                }
                else if (!isDefaultRang
                        && NumberTools.notEqual(rangierungToReAttach.getId(), endstelle.getRangierIdAdditional())) {
                    endstelle.setRangierIdAdditional(rangierungToReAttach.getId());
                }

                endstellenService.saveEndstelle(endstelle);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Neuzuordnung der Endstelle zur Rangierung: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Rangierung> breakAndDeactivateRangierung(List<Rangierung> toDeactivate, boolean makeHistory,
            Long sessionId) throws StoreException {
        if (CollectionTools.isEmpty(toDeactivate)) {
            throw new StoreException("Keine Rangierung angegeben!");
        }
        try {
            String userW = getLoginNameSilent(sessionId);

            List<Rangierung> result = new ArrayList<>();
            for (Rangierung rangierung : toDeactivate) {
                Rangierung changedRangierung = breakRangierung(rangierung, true, true, makeHistory);
                changedRangierung.setFreigegeben(Freigegeben.deactivated);
                changedRangierung.setGueltigBis(new Date());
                changedRangierung.setBemerkung("Rangierung wurde deaktiviert!");
                changedRangierung.setUserW(userW);
                changedRangierung.setPhysikTypId(null);
                saveRangierung(changedRangierung, false);

                result.add(changedRangierung);
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Aufbrechen der Rangierung: " + e.getMessage(), e);
        }
    }

    @Override
    public void bundleRangierungen(List<Rangierung> toBundle) throws StoreException {
        if (CollectionTools.isEmpty(toBundle) || (toBundle.size() < 2)) {
            throw new StoreException("Es muessen mindestens 2 Rangierungen zum Buendeln angegeben werden!");
        }

        try {
            Integer ltgGesId = null;
            for (int i = 0; i < toBundle.size(); i++) {
                Rangierung rangierung = toBundle.get(i);
                if ((ltgGesId == null) && (rangierung.getLeitungGesamtId() != null)) {
                    ltgGesId = rangierung.getLeitungGesamtId();
                }
                else {
                    ltgGesId = getRangierungDAO().getNextLtgGesId();
                }

                if (rangierung.getLeitungGesamtId() != null) {
                    rangierung.setLeitungGesamtId(ltgGesId);
                    rangierung.setLeitungLfdNr(i + 1);
                    saveRangierung(rangierung, false);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Buendelung der Rangierungen: " + e.getMessage(), e);
        }
    }

    @Override
    public Rangierung findRangierung(Long rangierId) throws FindException {
        return findRangierungTx(rangierId);
    }

    @Override
    public Rangierung findRangierungTx(Long rangierId) throws FindException {
        if (rangierId == null) {
            return null;
        }
        try {
            return getRangierungDAO().findById(rangierId, Rangierung.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Rangierung findRangierung4Equipment(Long eqId, boolean findEqIn) throws FindException {
        if (eqId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            Rangierung ex = new Rangierung();
            ex.setGueltigBis(DateTools.getHurricanEndDate());
            if (findEqIn) {
                ex.setEqInId(eqId);
            }
            else {
                ex.setEqOutId(eqId);
            }

            List<Rangierung> rangierungen = getRangierungDAO().queryByExample(ex, Rangierung.class);
            if (CollectionTools.isNotEmpty(rangierungen)) {
                if (rangierungen.size() == 1) {
                    return rangierungen.get(0);
                }
                throw new FindException(FindException.INVALID_RESULT_SIZE, new Object[] {
                        1, rangierungen.size() });
            }
            return null;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Rangierung findRangierung4Equipment(Long eqId) throws FindException {
        Rangierung rangierung = findRangierung4Equipment(eqId, true);
        rangierung = (rangierung == null) ? findRangierung4Equipment(eqId, false) : rangierung;
        return rangierung;
    }

    @Override
    public Map<Long, Rangierung> findRangierungen4Equipments(Collection<Equipment> equipments) throws FindException {
        if ((equipments == null) || equipments.isEmpty()) {
            return new HashMap<>();
        }
        try {
            List<Long> eqIds = new ArrayList<>();
            for (Equipment equipment : equipments) {
                eqIds.add(equipment.getId());
            }
            return rangierungDAO.findForEquipments(eqIds);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    @CcTxRequired
    public Rangierung findRangierungWithEQ(Long rangierId) throws FindException {
        Rangierung rang = findRangierung(rangierId);
        if (rang != null) {
            Equipment eqIn = (rang.getEqInId() != null) ? findEquipment(rang.getEqInId()) : null;
            Equipment eqOut = (rang.getEqOutId() != null) ? findEquipment(rang.getEqOutId()) : null;

            rang.setEquipmentIn(eqIn);
            rang.setEquipmentOut(eqOut);
        }
        return rang;
    }

    @Override
    public Rangierung findFreieRangierung(RangierungQuery query, Endstelle endstelle, boolean checkLtgGesId,
            Long childPhysiktypId, Integer uevtClusterNo, Uebertragungsverfahren uetv, Bandwidth bandwidth)
            throws FindException {
        if (query.isEmpty()) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            int maxCount = (checkLtgGesId) ? -1 : 1;
            List<Rangierung> rangierungen = filterAndSortRangierungenByMaxBandwidth(
                    getRangierungDAO().findFreiByQuery(query, -1), bandwidth);

            if (!CollectionUtils.isEmpty(rangierungen)) {
                filterRangierungenByUevtClusterNo(rangierungen, uevtClusterNo);
                filterRangierungenByCorrectUetv(rangierungen, uetv);

                if (maxCount == 1) {
                    Rangierung result = null;
                    for (Rangierung rangierung : rangierungen) {
                        if (doesKvzNrMatch(rangierung, endstelle)) {
                            result = rangierung;
                            break;
                        }
                    }
                    return result;
                }
                Rangierung result = null;
                for (Rangierung rangierung : rangierungen) {
                    if (rangierungenFrei(rangierung.getLeitungGesamtId(), false)
                            && doesKvzNrMatch(rangierung, endstelle)) {
                        if (childPhysiktypId != null) {
                            if (hasPhysiktyp(rangierung.getLeitungGesamtId(), childPhysiktypId)) {
                                result = rangierung;
                                break;
                            }
                        }
                        else {
                            result = rangierung;
                            break;
                        }
                    }
                    LOGGER.info("Rangierung nicht komplett frei oder zug. Physiktyp nicht ok (LtgGes-ID): "
                            + rangierung.getLeitungGesamtId());
                    LOGGER.info(" --> suche weitere Rangierung");
                }
                return result;
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Ueberprueft im Falle eines KVZ Standorts, ob die KVZ Nr des EQ-OUT Ports mit der KVZ Nr der Strasse
     * uebereinstimmt.
     *
     * @param rangierung zu pruefende Rangierung
     * @param endstelle  die Endstelle, der die Rangierung zugeordnet werden soll
     * @return {@code true} wenn der Standort kein(!) KVZ ist bzw. die KVZ Nr der Strasse und des Ports uebereinstimmen;
     * ansonsten {@code false}
     */
    boolean doesKvzNrMatch(Rangierung rangierung, Endstelle endstelle) throws FindException {
        try {
            HVTStandort hvtStandort = hvtService.findHVTStandort(rangierung.getHvtIdStandort());
            if ((hvtStandort != null) && (hvtStandort.isStandortType(HVTStandort.HVT_STANDORT_TYP_FTTC_KVZ))) {
                GeoId assignedGeoId = availabilityService.findGeoId(endstelle.getGeoId());
                if (assignedGeoId == null) {
                    throw new FindException("Die zugehoerige GeoID konnte nicht ermittelt werden!");
                }

                GeoId2TechLocation kvzAssignment =
                        availabilityService.findGeoId2TechLocation(assignedGeoId.getId(), endstelle.getHvtIdStandort());
                if (kvzAssignment == null) {
                    throw new FindException("Die Zuordnung der GeoID zum KVZ-Standort konnte nicht ermittelt werden!");
                }

                if (StringUtils.isBlank(kvzAssignment.getKvzNumber())) {
                    throw new FindException(
                            "Der hinterlegte Standort besitzt keine KVZ-Nr - Port-Zuordnung daher nicht moeglich!");
                }

                Equipment eqOut = findEquipment(rangierung.getEqOutId());
                if (eqOut == null) {
                    throw new FindException(
                            "EQ-OUT Port von Rangierung nicht gefunden! Pruefung auf KVZ Daten nicht moeglich!");
                }
                else if (!StringUtils.equalsIgnoreCase(eqOut.getKvzNummer(), kvzAssignment.getKvzNumber())) {
                    return false;
                }
            }

            return true;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ueberpruefung der KVZ Daten: " + e.getMessage(), e);
        }
    }

    /**
     * Falls die übergebene Uevt-Cluster-No nicht {@code null} ist, werden alle übergebenen Rangierungen ausgefiltert,
     * die nicht die übergebene Uevt-Cluster-No haben (auch Rangierungen mit EqOut-Port Uevt-Cluster-No == {@code null}
     * ).
     */
    private List<Rangierung> filterRangierungenByUevtClusterNo(List<Rangierung> rangierungen, Integer uevtClusterNo)
            throws FindException {
        if (uevtClusterNo != null) {
            Iterator<Rangierung> iterator = rangierungen.iterator();
            while (iterator.hasNext()) {
                Rangierung rangierung = iterator.next();

                Equipment eqOut = findEquipment(rangierung.getEqOutId());
                if (!uevtClusterNo.equals(eqOut.getUevtClusterNo())) {
                    iterator.remove();
                }
            }
        }
        return rangierungen;
    }

    /**
     * Falls das übergebene Übertragungsverfahren nicht {@code null} ist, werden alle übergebenen Rangierungen
     * ausgefiltert, deren Eq-Out-Stift nicht dieses Übertragungsverfahren hat.
     */
    private List<Rangierung> filterRangierungenByCorrectUetv(List<Rangierung> rangierungen, Uebertragungsverfahren uetv)
            throws FindException {
        if (uetv != null) {
            Iterator<Rangierung> iterator = rangierungen.iterator();
            while (iterator.hasNext()) {
                Rangierung rangierung = iterator.next();

                Equipment eqOut = findEquipment(rangierung.getEqOutId());
                if ((eqOut == null) || !uetv.equals(eqOut.getUetv())) {
                    iterator.remove();
                }
            }
        }
        return rangierungen;
    }

    /**
     * Ermittelt fuer alle Rangierungen die kleinste erlaubte Bandbreite ueber Physiktyp und Baugruppentyp.
     * Kann keine Bandbreite ermittelt werden ist der Wert {@code null}. Bandbreiten {@code != null && <
     * bandwidth} werden entfernt.
     *
     * @return Liste der Rangierungen aufsteigend sortiert nach Bandbreite. Zu allerletzt die Rangierungen ohne
     * Bandbreite.
     */
    @Nonnull
    List<Rangierung> filterAndSortRangierungenByMaxBandwidth(@Nonnull List<Rangierung> rangierungen,
            @CheckForNull final Bandwidth requiredMinBandwidth) throws FindException, ServiceNotFoundException {
        final Function<Rangierung, Bandwidth> maxBandwidthOfRangierung = new Function<Rangierung, Bandwidth>() {
            @Nullable
            @Override
            public Bandwidth apply(Rangierung input) {
                try {
                    return getMaxBandwidth4Rangierung(input);
                }
                catch (Exception e) {
                    return null;
                }
            }
        };
        final Iterable<Rangierung> gefiltert = Iterables.filter(rangierungen, new Predicate<Rangierung>() {
            @Override
            public boolean apply(@Nullable Rangierung input) {
                Bandwidth maxBandwidth = maxBandwidthOfRangierung.apply(input);
                return requiredMinBandwidth == null || requiredMinBandwidth.isWithin(maxBandwidth);
            }
        });
        return Ordering.natural().nullsLast().onResultOf(maxBandwidthOfRangierung).sortedCopy(gefiltert);
    }

    /**
     * Ermittelt die maximal moegliche Bandbreite fuer eine Rangierung auf dem Physiktyp UND dem Baugruppentyp. Die
     * Bandbreite mit dem kleineren Wert von beiden wird genommen oder {@code null}, falls keine Bandbreite hinterlegt
     * ist.
     */
    @CheckForNull
    Bandwidth getMaxBandwidth4Rangierung(@Nonnull Rangierung rangierung) throws FindException, ServiceNotFoundException {
        Bandwidth physikTypBandwidth = null;
        if (rangierung.getPhysikTypId() != null) {
            initPhysikService();
            PhysikTyp physikTyp = physikService.findPhysikTyp(rangierung.getPhysikTypId());
            physikTypBandwidth = (physikTyp != null) ? physikTyp.getMaxBandwidth() : null;
        }
        Bandwidth equipmentBandwidth = null;
        if (rangierung.getEqInId() != null) {
            Equipment equipment = findEquipment(rangierung.getEqInId());
            HWBaugruppe hwBaugruppe = ((equipment != null) && (equipment.getHwBaugruppenId() != null) ? hwService
                    .findBaugruppe(equipment.getHwBaugruppenId()) : null);
            equipmentBandwidth = ((hwBaugruppe != null) && (hwBaugruppe.getHwBaugruppenTyp() != null)) ? hwBaugruppe
                    .getHwBaugruppenTyp().getMaxBandwidth() : null;
        }
        return Ordering.natural().nullsLast().min(physikTypBandwidth, equipmentBandwidth);
    }

    @Override
    public List<Rangierung> findFreieRangierungen(RangierungQuery query, boolean checkLtgGesId, Long childPhysiktypId,
            Bandwidth bandwidth) throws FindException {
        if (query.isEmpty()) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            List<Rangierung> rangierungen = filterAndSortRangierungenByMaxBandwidth(
                    getRangierungDAO().findFreiByQuery(query, -1), bandwidth);

            List<Rangierung> result = new ArrayList<>();
            if ((!rangierungen.isEmpty())) {
                for (Rangierung rangierung : rangierungen) {
                    if (checkLtgGesId && (rangierung.getLeitungGesamtId() != null)) {
                        if (rangierungenFrei(rangierung.getLeitungGesamtId(),
                                BooleanTools.nullToFalse(query.getIncludeFreigabebereit()))
                                ||
                                (BooleanTools.nullToFalse(query.getIncludeDefekt()) && rangierung.isRangierungDefekt())) {
                            if (childPhysiktypId != null) {
                                if (hasPhysiktyp(rangierung.getLeitungGesamtId(), childPhysiktypId)) {
                                    result.add(rangierung);
                                }
                            }
                            else {
                                result.add(rangierung);
                            }
                        }
                    }
                    else {
                        result.add(rangierung);
                    }
                }
            }
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<RangierungsEquipmentView> createRangierungsEquipmentView(List<Rangierung> rangierungen, String kvzNummer)
            throws FindException {
        if (CollectionTools.isEmpty(rangierungen)) {
            return new ArrayList<>();
        }
        try {
            initPhysikService();

            List<RangierungsEquipmentView> result = new ArrayList<>();
            for (Rangierung rangierung : rangierungen) {
                Equipment eqIn = findEquipment(rangierung.getEqInId());
                Equipment eqOut = findEquipment(rangierung.getEqOutId());
                // Rangierungen filtern, wenn kvzNummer angegeben ist
                if ((kvzNummer == null) || (eqOut.getKvzNummer() == null)
                        || StringUtils.equals(kvzNummer, eqOut.getKvzNummer())) {
                    PhysikTyp physikTyp = physikService.findPhysikTyp(rangierung.getPhysikTypId());
                    HWBaugruppe bgEqIn = ((eqIn != null) && (eqIn.getHwBaugruppenId() != null)
                            ? hwService.findBaugruppe(eqIn.getHwBaugruppenId()) : null);
                    HWBaugruppe bgEqOut = ((eqOut != null) && (eqOut.getHwBaugruppenId() != null)
                            ? hwService.findBaugruppe(eqOut.getHwBaugruppenId()) : null);
                    HWRack rackEqIn = (bgEqIn != null) ? hwService.findRackById(bgEqIn.getRackId()) : null;
                    HWRack rackEqOut = (bgEqOut != null) ? hwService.findRackById(bgEqOut.getRackId()) : null;

                    // Daten von evtl. vorhandener Zusatz-Rangierung laden
                    Rangierung rangierungAdd = null;
                    Equipment eqInAdd = null;
                    Equipment eqOutAdd = null;
                    HWBaugruppe bgEqInAdd = null;
                    HWBaugruppe bgEqOutAdd = null;
                    HWRack rackEqInAdd = null;
                    HWRack rackEqOutAdd = null;
                    PhysikTyp physikTypAdd = null;
                    if (rangierung.getLeitungGesamtId() != null) {
                        rangierungAdd = findRangierungsMatch(rangierung);
                        if ((rangierungAdd != null) && (rangierungAdd.getEqInId() != null)) {
                            eqInAdd = findEquipment(rangierungAdd.getEqInId());
                            eqOutAdd = findEquipment(rangierungAdd.getEqOutId());
                            bgEqInAdd = ((eqInAdd != null) && (eqInAdd.getHwBaugruppenId() != null)
                                    ? hwService.findBaugruppe(eqInAdd.getHwBaugruppenId()) : null);
                            bgEqOutAdd = ((eqOutAdd != null) && (eqOutAdd.getHwBaugruppenId() != null)
                                    ? hwService.findBaugruppe(eqOutAdd.getHwBaugruppenId()) : null);
                            rackEqInAdd = (bgEqInAdd != null) ? hwService.findRackById(bgEqInAdd.getRackId()) : null;
                            rackEqOutAdd = (bgEqOutAdd != null) ? hwService.findRackById(bgEqOutAdd.getRackId()) : null;
                            physikTypAdd = physikService.findPhysikTyp(rangierungAdd.getPhysikTypId());
                        }
                    }

                    final HVTRaum raum = findHvtRaum(rackEqIn);

                    RangierungsEquipmentView view = new RangierungsEquipmentView(rangierung, eqOut, bgEqOut, rackEqOut,
                            eqIn, bgEqIn, rackEqIn, raum, physikTyp, rangierungAdd, eqOutAdd, bgEqOutAdd, rackEqOutAdd,
                            eqInAdd, bgEqInAdd, rackEqInAdd, physikTypAdd);
                    result.add(view);
                }
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der Equipment-Daten: " + e.getMessage(), e);
        }
    }

    private HVTRaum findHvtRaum(HWRack rackEqIn) throws FindException {
        final HVTRaum raum;
        if (rackEqIn != null && rackEqIn.getHvtRaumId() != null) {
            raum = hvtService.findHVTRaum(rackEqIn.getHvtRaumId());
        }
        else {
            raum = null;
        }
        return raum;
    }

    /* Ermittelt das HW-Rack zu einem Port. */
    @Override
    @CheckForNull
    public HWRack getHWRackForEquipment(Equipment equipment, HWService hwService) throws FindException {
        if ((equipment != null) && (equipment.getHwBaugruppenId() != null)) {
            HWBaugruppe baugruppeEqIn = hwService.findBaugruppe(equipment.getHwBaugruppenId());
            return hwService.findRackById(baugruppeEqIn.getRackId());
        }
        return null;
    }

    /* Ermittelt den Hardware-Hersteller fuer einen Physiktyp. */
    private Long getHvtTechnikId4PhysikTyp(Long physikTypId) throws FindException, ServiceNotFoundException {
        initPhysikService();
        PhysikTyp physikTyp = physikService.findPhysikTyp(physikTypId);

        HVTTechnik hvtTechnik = hvtService.findHVTTechnik(physikTyp.getHvtTechnikId());
        return (hvtTechnik != null) ? hvtTechnik.getId() : null;
    }

    @Override
    @CcTxRequiresNew
    public Rangierung[] findRangierungen(Long auftragId, String esTyp) throws FindException {
        return findRangierungenTx(auftragId, esTyp);
    }

    @Override
    public Rangierung[] findRangierungenTx(final Long auftragId, final String esTyp) throws FindException {
        return findRangierungen(auftragId, esTyp, new Function2<Endstelle, Long, String>() {
            @Override
            public Endstelle apply(final Long aLong, final String s) throws Exception {
                return endstellenService.findEndstelle4Auftrag(auftragId, esTyp);
            }
        });
    }

    @Override
    @CheckForNull
    public Rangierung[] findRangierungenTxWithoutExplicitFlush(final Long auftragId, final String esTyp)
            throws FindException {
        return findRangierungen(auftragId, esTyp, new Function2<Endstelle, Long, String>() {
            @Override
            public Endstelle apply(final Long aLong, final String s) throws Exception {
                return endstellenService.findEndstelle4AuftragWithoutExplicitFlush(auftragId, esTyp);
            }
        });
    }

    @Override
    @CheckForNull
    public Rangierung[] findRangierungen(Long auftragId, String esTyp,
            Function2<Endstelle, Long, String> findEs4Auftrag) throws FindException {
        if ((auftragId == null) || StringUtils.isBlank(esTyp)) {
            return null;
        }
        try {
            Endstelle es = findEs4Auftrag.apply(auftragId, esTyp);
            if ((es != null) && (es.getRangierId() != null)) {
                Rangierung rangierung = findRangierung(es.getRangierId());
                Rangierung rangierungAdd = (es.getRangierIdAdditional() != null)
                        ? findRangierung(es.getRangierIdAdditional()) : null;

                int size = (rangierungAdd != null) ? 2 : 1;
                Rangierung[] result = new Rangierung[size];
                result[0] = rangierung;
                if (size == 2) {
                    result[1] = rangierungAdd;
                }
                return result;
            }
            return null;
        }
        catch (HurricanConcurrencyException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Rangierung findKreuzung(Long rangierId, boolean filterWithEqOut) throws FindException {
        if (rangierId == null) {
            return null;
        }
        try {
            Rangierung rangierungAct = findRangierung(rangierId);
            if ((rangierungAct != null) && (rangierungAct.getHistoryFrom() != null)) {
                Rangierung rangOld = findRangierung(rangierungAct.getHistoryFrom());
                if (rangOld == null) {
                    return null;
                }

                // aktuelle Rangierung zu EQ-Out oder EQ-In suchen
                Rangierung example = new Rangierung();
                if (filterWithEqOut) {
                    example.setEqOutId(rangOld.getEqOutId());
                }
                else {
                    example.setEqInId(rangOld.getEqInId());
                }
                example.setGueltigBis(DateTools.getHurricanEndDate());

                List<Rangierung> result =
                        getRangierungDAO().queryByExample(example, Rangierung.class, new String[] { "id" }, null);
                if ((result != null) && (!result.isEmpty())) {
                    return result.get(0);
                }
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Rangierung findHistoryFrom(Long rangierungId) throws FindException {
        if (rangierungId == null) {
            return null;
        }
        try {
            Rangierung example = new Rangierung();
            example.setHistoryFrom(rangierungId);
            example.setGueltigBis(DateTools.getHurricanEndDate());

            List<Rangierung> result = getRangierungDAO().queryByExample(example, Rangierung.class);
            if ((result != null) && (!result.isEmpty())) {
                if (result.size() == 1) {
                    return result.get(0);
                }
                throw new FindException("Es konnte keine eindeutige Historisierung der Ursprungs-Rangierung " +
                        rangierungId + " gefunden werden!");
            }
            return null;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Prueft, ob ALLE Rangierungen mit einer best. Ltg-Ges-ID 'frei' sind.
     *
     * @param ltgGesId             LeitungGesamt-ID der zu pruefenden Rangierungen
     * @param acceptFreigabebereit Flag, ob auch freigabebereite Rangierungen (ES_ID=-1) akzeptiert werden sollen.
     */
    private boolean rangierungenFrei(Integer ltgGesId, boolean acceptFreigabebereit) throws FindException {
        boolean frei = true;
        List<Rangierung> rangierungen = findByLtgGesId(ltgGesId);
        if (rangierungen != null && !rangierungen.isEmpty()) {
            for (Rangierung rang2Check : rangierungen) {
                if (!rang2Check.isRangierungFrei(acceptFreigabebereit)) {
                    frei = false;
                    break;
                }
            }
        }
        else {
            frei = false;
        }

        return frei;
    }

    /**
     * Prueft, ob eine Rangierung der Gruppierung (ueber ltgGesId) vom Physiktyp 'physiktypId' ist.
     */
    private boolean hasPhysiktyp(Integer ltgGesId, Long physiktypId) throws FindException {
        List<Rangierung> rangierungen = findByLtgGesId(ltgGesId);
        if (rangierungen != null && !rangierungen.isEmpty()) {
            for (Rangierung rang2Check : rangierungen) {
                if (NumberTools.equal(rang2Check.getPhysikTypId(), physiktypId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Rangierung> findByLtgGesId(Integer ltgGesId) throws FindException {
        if (ltgGesId == null) {
            return new ArrayList<>();
        }
        try {
            return getRangierungDAO().findByLtgGesId(ltgGesId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Rangierung findRangierungsMatch(Rangierung rangierung4Match) throws FindException {
        try {
            if ((rangierung4Match != null) && (rangierung4Match.getLeitungGesamtId() != null)) {
                Rangierung matchingRangierung = null;
                List<Rangierung> rangierungen = findByLtgGesId(rangierung4Match.getLeitungGesamtId());
                if (CollectionTools.isNotEmpty(rangierungen)) {
                    for (Rangierung rangierung : rangierungen) {
                        if (NumberTools.notEqual(rangierung4Match.getId(), rangierung.getId())) {
                            matchingRangierung = rangierung;
                            break;
                        }
                    }
                }
                return matchingRangierung;
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der zugeordneten Rangierung: " + e.getMessage(), e);
        }
    }

    @Override
    public Rangierung assignRangierung2ES(Long endstelleId, IServiceCallback serviceCallback) throws FindException,
            StoreException {
        return assignRangierung2ES(endstelleId, null, serviceCallback);
    }

    @Override
    public Rangierung assignRangierung2ES(Long endstelleId, CarrierEquipmentDetails ceDetails,
            IServiceCallback serviceCallback) throws FindException, StoreException {
        if (endstelleId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        Object result;

        try {
            if (ceDetails == null) {
                // Rangierungsermittlung und -zuordnung ueber Rangierungsmatrix
                IServiceCommand cmd = serviceLocator.getCmdBean(AssignRangierung2ESCommand.class);
                cmd.prepare(AbstractAssignRangierungCommand.ENDSTELLE_ID, endstelleId);
                cmd.prepare(AbstractAssignRangierungCommand.SERVICE_CALLBACK, serviceCallback);
                result = cmd.execute();
            }
            else {
                // Rangierungsermittlung und -zuordnung ueber Details aus Carrier-Equipment
                IServiceCommand cmd = serviceLocator.getCmdBean(AssignRangierungByCarrierDetailsCommand.class);
                cmd.prepare(AbstractAssignRangierungCommand.ENDSTELLE_ID, endstelleId);
                cmd.prepare(AssignRangierungByCarrierDetailsCommand.CARRIER_EQUIPMENT_DETAILS, ceDetails);
                cmd.prepare(AbstractAssignRangierungCommand.SERVICE_CALLBACK, serviceCallback);
                result = cmd.execute();
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_ASSIGN_RANGIERUNG_4_ES,
                    new Object[] { endstelleId.toString(), e.getMessage() }, e);
        }

        if (result instanceof Rangierung) {
            Rangierung rangierung = (Rangierung) result;
            if (NumberTools.equal(rangierung.getEsId(), endstelleId)) {
                return rangierung;
            }
            else {
                String rId = (rangierung.getId() != null) ? "" + rangierung.getId() : "?";
                throw new StoreException(StoreException.RANGIERUNG_NOT_ASSIGNED,
                        new Object[] { endstelleId.toString(), rId, "unbekannt" });
            }
        }
        else {
            throw new FindException(FindException.RANGIERUNG_4_ES_NOT_FOUND);
        }
    }

    @Override
    public Pair<Rangierung, Rangierung> findPossibleRangierung(Long endstelleId, Long hvtIdStandort) throws FindException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(FindRangierung4ESCommand.class);
            cmd.prepare(FindRangierung4ESCommand.ENDSTELLE_ID, endstelleId);
            cmd.prepare(FindRangierung4ESCommand.HVT_ID_STANDORT, hvtIdStandort);
            Object result = cmd.execute();

            if (result instanceof Pair) {
                return (Pair<Rangierung, Rangierung>) result;
            }
            return Pair.create(null, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(
                    String.format("Fehler bei der Ermittlung der Rangierungen fuer die Endstelle %s", endstelleId),
                    new Object[] { endstelleId.toString(), e.getMessage() }, e);
        }
    }

    private Pair<AKWarnings, Collection<AuftragDaten>> singleWarningWithEmptyCollection(final String txt) {
        return Pair.create(
                new AKWarnings().addAKWarning(this, txt),
                ((Collection<AuftragDaten>) Collections.<AuftragDaten>emptyList()));
    }

    @Override
    public void attachRangierung2Endstelle(Rangierung rangierung, Rangierung rangierungAdd, Endstelle endstelle)
            throws StoreException, ValidationException {
        if ((rangierung == null) || (endstelle == null)) {
            throw new StoreException(StoreException.ERROR_ASSIGN_RANGIERUNG_4_ES,
                    new Object[] {
                            format("%s", (endstelle != null) ? endstelle.getId() : ""),
                            "Rangierung und/oder Endstelle nicht angegeben!" }
            );
        }

        try {
            hvtService.validateKvzSperre(endstelle);
            rangierung.setEsId(endstelle.getId());
            saveRangierung(rangierung, false);

            endstelle.setRangierId(rangierung.getId());
            if (rangierungAdd != null) {
                rangierungAdd.setEsId(endstelle.getId());
                endstelle.setRangierIdAdditional(rangierungAdd.getId());
                saveRangierung(rangierungAdd, false);
            }

            if (NumberTools.notEqual(rangierung.getHvtIdStandort(), endstelle.getHvtIdStandort())) {
                endstelle.setHvtIdStandort(rangierung.getHvtIdStandort());
            }

            endstellenService.saveEndstelle(endstelle);
        } catch (ValidationException v) {
            throw v;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public AKWarnings physikAenderung(Long strategy, Long auftragIdSrc, Long auftragIdDest,
            IServiceCallback serviceCallback, Long sessionId) throws StoreException {
        if ((strategy <= 0) || (auftragIdSrc == null) || (auftragIdDest == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            Produkt prodSrc = produktService.findProdukt4Auftrag(auftragIdSrc);
            Produkt prodDest = produktService.findProdukt4Auftrag(auftragIdDest);
            if ((prodSrc == null) || (prodDest == null)) {
                throw new StoreException("Die Produkte der beteiligten Auftraege konnten nicht ermittelt werden!\n" +
                        "Die Physik-Aenderung wurde nicht durchgefuehrt.");
            }

            Produkt2Produkt p2p = produktService.findProdukt2Produkt(strategy, prodSrc.getId(), prodDest.getId());
            if ((p2p == null) || (p2p.getId() == null)) {
                throw new StoreException("Die gewuenschte Aenderung konnte nicht durchgefuehrt werden, " +
                        "da keine zugehoerige Konfiguration gefunden wurde.\nProdukte: " + prodSrc.getId() +
                        " --> " + prodDest.getId());
            }

            List<ServiceCommand> commands =
                    chainService.findServiceCommands4Reference(p2p.getChainId(), ServiceChain.class, null);
            LOGGER.info("Chain-ID fuer Physik-Aenderung: " + p2p.getChainId());
            if ((commands == null) || (commands.isEmpty())) {
                throw new StoreException("Fuer die beteiligten Auftraege konnten keine Commands ermittelt werden!");
            }

            AKServiceCommandChain chain = new AKServiceCommandChain();
            for (ServiceCommand cmd : commands) {
                IServiceCommand serviceCmd = serviceLocator.getCmdBean(cmd.getClassName());
                if (serviceCmd == null) {
                    throw new StoreException("Fuer das definierte ServiceCommand " + cmd.getName() +
                            " konnte kein Objekt geladen werden!\nPhysik-Aenderung wurde nicht durchgefuehrt.");
                }
                serviceCmd.prepare(AbstractPhysikCommand.KEY_SESSION_ID, sessionId);
                serviceCmd.prepare(AbstractPhysikCommand.KEY_STRATEGY, strategy);
                serviceCmd.prepare(AbstractPhysikCommand.KEY_AUFTRAG_ID_SRC, auftragIdSrc);
                serviceCmd.prepare(AbstractPhysikCommand.KEY_AUFTRAG_ID_DEST, auftragIdDest);
                serviceCmd.prepare(AbstractPhysikCommand.KEY_SERVICE_CALLBACK, serviceCallback);

                chain.addCommand(serviceCmd);
            }
            chain.executeChain();
            if (chain.getWarnings() != null) {
                LOGGER.warn(chain.getWarnings().getWarningsAsText());
            }

            return chain.getWarnings();
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.ERROR_AT_PHYSIK_UEBERNAHME, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public void loadEquipments(Rangierung rangierung) throws FindException {
        if (rangierung != null) {
            try {
                rangierung.setEquipmentIn(
                        (rangierung.getEqInId() != null) ? findEquipment(rangierung.getEqInId()) : null);
                rangierung.setEquipmentOut(
                        (rangierung.getEqOutId() != null) ? findEquipment(rangierung.getEqOutId()) : null);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new FindException(FindException._UNEXPECTED_ERROR, e);
            }
        }
    }

    @Override
    public Equipment findEquipment(Long eqId) throws FindException {
        if (eqId == null) {
            return null;
        }
        try {
            return getEquipmentDAO().findById(eqId, Equipment.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Equipment findEquipment(Long rackId, String hwEQN, String rangSSType) throws FindException {
        if ((rackId == null) || StringUtils.isBlank(hwEQN)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }

        try {
            List<Equipment> eq = getEquipmentDAO().findEquipment(rackId, hwEQN, rangSSType);
            if (CollectionTools.isNotEmpty(eq)) {
                if (eq.size() == 1) {
                    return eq.get(0);
                }
                throw new FindException(FindException.INVALID_RESULT_SIZE,
                        new Object[] { 1, eq.size() });
            }
            return null;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Equipment findEquipmentByBaugruppe(Long hwBaugruppeId, String hwEQN, String rangSSType)
            throws FindException {
        try {
            return getEquipmentDAO().findEquipmentByBaugruppe(hwBaugruppeId, hwEQN, rangSSType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Equipment> findEquipments(Equipment example, String... orderParams) throws FindException {
        if (example == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            return getEquipmentDAO().findEquipments(example, orderParams);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Equipment findEquipment4Endstelle(Endstelle endstelle, boolean findEquipmentAdditional, boolean findEqOut)
            throws FindException {
        if (endstelle == null) {
            return null;
        }
        try {
            Rangierung rangierung = (findEquipmentAdditional)
                    ? findRangierung(endstelle.getRangierIdAdditional())
                    : findRangierung(endstelle.getRangierId());
            if (rangierung != null) {
                return (findEqOut)
                        ? findEquipment(rangierung.getEqOutId())
                        : findEquipment(rangierung.getEqInId());
            }

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<HardwareEquipmentView> findEquipmentViews(Equipment example, String[] orderParams) throws FindException {
        try {
            List<Equipment> equipments = findEquipments(example, orderParams);

            List<HardwareEquipmentView> result = new ArrayList<>();
            if (CollectionTools.isNotEmpty(equipments)) {
                for (Equipment eq : equipments) {
                    HardwareEquipmentView view = new HardwareEquipmentView();
                    view.setEquipment(eq);

                    if (eq.getHwBaugruppenId() != null) {
                        HWBaugruppe baugruppe = hwService.findBaugruppe(eq.getHwBaugruppenId());
                        HWBaugruppenTyp baugruppenTyp = (baugruppe != null ? baugruppe.getHwBaugruppenTyp() : null);
                        HWRack rack = (baugruppe != null) ? hwService.findRackById(baugruppe.getRackId()) : null;

                        view.setHwBaugruppe(baugruppe);
                        view.setHwBaugruppenTyp(baugruppenTyp);
                        view.setHwRack(rack);
                    }

                    result.add(view);
                }
            }

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EquipmentInOutView> findEqInOutViews(Equipment example) throws FindException {
        if (example == null) {
            throw new FindException(FindException.EMPTY_FIND_PARAMETER);
        }
        try {
            return getEquipmentDAO().findEqInOutViews(example);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Equipment> findEquipments4HWBaugruppe(Long baugruppeId) throws FindException {
        if (baugruppeId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            Equipment ex = new Equipment();
            ex.setHwBaugruppenId(baugruppeId);

            return getEquipmentDAO().queryByExample(ex, Equipment.class, new String[] { "id" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Map<String, Integer> getEquipmentCount(Long hvtIdStandort, String rangVerteiler, String rangLeiste1)
            throws FindException {
        if ((hvtIdStandort == null) || StringUtils.isBlank(rangVerteiler) || StringUtils.isBlank(rangLeiste1)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            return getEquipmentDAO().getEquipmentCount(hvtIdStandort, rangVerteiler, rangLeiste1);
        }
        catch (IncorrectResultSizeDataAccessException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(e.getMessage(), e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Equipment saveEquipment(Equipment toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            toSave.setDateW(new Date());
            HistoryHelper.checkHistoryDates(toSave);
            return getEquipmentDAO().store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveEquipments(Collection<Equipment> equipmentsToSave) throws StoreException {
        if (CollectionTools.isEmpty(equipmentsToSave)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            for (Equipment equipmentToSave : equipmentsToSave) {
                saveEquipment(equipmentToSave);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Rangierungsmatrix> findMatrix(RangierungsmatrixQuery query) throws FindException {
        if (query.isEmpty()) {
            return null;
        }
        try {
            return getRangierungsmatrixDAO().findByQuery(query);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Rangierungsmatrix saveMatrix(Rangierungsmatrix toSave, Long sessionId, boolean makeHistory)
            throws ValidationException, StoreException {
        if ((toSave == null) || (sessionId == null)) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            AKUser user = getAKUserBySessionId(sessionId);
            if (user != null) {
                toSave.setBearbeiter(user.getName());
            }

            Rangierungsmatrix modelToSave;
            RangierungsmatrixDAO dao = getRangierungsmatrixDAO();
            if (makeHistory && (toSave.getId() != null)) {
                Date now = new Date();

                modelToSave = dao.update4History(toSave, toSave.getId(), now);
            }
            else {
                HistoryHelper.checkHistoryDates(toSave);
                modelToSave = toSave;
            }

            AbstractValidator val = getRangierungsmatrixValidator();
            ValidationException valEx = new ValidationException(modelToSave, "Rangierungsmatrix");
            val.validate(modelToSave, valEx);
            if (valEx.hasErrors()) {
                throw valEx;
            }

            dao.store(modelToSave);
            return modelToSave;
        }
        catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteMatrix(Rangierungsmatrix toDelete, Long sessionId) throws DeleteException {
        if ((toDelete == null) || (toDelete.getId() == null) || (sessionId == null)) {
            throw new DeleteException(DeleteException.INVALID_PARAMETERS);
        }

        try {
            AKUser user = getAKUserBySessionId(sessionId);
            if (user != null) {
                toDelete.setBearbeiter(user.getName());
            }
            Date now = new Date();
            RangierungsmatrixDAO dao = getRangierungsmatrixDAO();

            toDelete.setGueltigBis(now);
            dao.store(toDelete);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR);
        }
    }

    @Override
    public List<Rangierungsmatrix> createMatrix(Long sessionId, List<Long> uevtIds,
            List<Long> produktIds, List<Long> physikTypIds) throws StoreException {
        if (sessionId == null) {
            throw new StoreException(StoreException.INVALID_SESSION_ID);
        }
        if ((uevtIds == null) || (uevtIds.isEmpty()) || (produktIds == null) || (produktIds.isEmpty())) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE, new Object[] { uevtIds,
                    produktIds });
        }

        try {
            // Command-Klasse ueber Spring erzeugen, um TX-Handling zu erhalten
            IServiceCommand cmd = serviceLocator.getCmdBean(CreateMatrixCommand.class);
            cmd.prepare(CreateMatrixCommand.SESSION_ID, sessionId);
            cmd.prepare(CreateMatrixCommand.UEVT_IDS, uevtIds);
            cmd.prepare(CreateMatrixCommand.PRODUKT_IDS, produktIds);
            cmd.prepare(CreateMatrixCommand.PHYSIKTYP_IDS, physikTypIds);
            Object result = cmd.execute();
            return (result instanceof List<?>) ? (List<Rangierungsmatrix>) result : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException.UNABLE_TO_CREATE_MATRIX, new Object[] { e.getMessage() }, e);
        }
    }

    @Override
    public List<Rangierung> changeEqOut(Reference changeReason, Endstelle endstelle,
            Rangierung rangierungToChange, Rangierung rangierungAddToChange,
            Rangierung rangierungToUse, Rangierung rangierungAddToUse, Long sessionId) throws StoreException, ValidationException {
        try {
            AKUser user = getAKUserBySessionIdSilent(sessionId);

            // EqOutPortTool kills the Context when Autowired
            ChangeEqOutPortTool changeEqOutPortTool = serviceLocator.getBean("changeEqOutPortTool",
                    ChangeEqOutPortTool.class);

            return changeEqOutPortTool.changeEqOut(
                    changeReason, endstelle,
                    rangierungToChange,
                    rangierungAddToChange,
                    rangierungToUse,
                    rangierungAddToUse,
                    user,
                    sessionId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Kreuzen des EQ-OUT Ports!", e);
        }
    }

    @Override
    public List<Rangierung> rangierungenKreuzen(Rangierung rangSrc, Rangierung rangDest, boolean eqOut, boolean eqIn,
            boolean updateES, boolean switchCB, boolean ignorePhysiktyp, Long physikaenderung) throws StoreException {
        if ((rangSrc == null) || (rangDest == null)) {
            throw new StoreException("Keine Rangierungen fuer die Kreuzung angegeben!");
        }

        try {
            if (NumberTools.notEqual(rangSrc.getHvtIdStandort(), rangDest.getHvtIdStandort())) {
                throw new StoreException("Die HVT-IDs der beiden Rangierungen sind unterschiedlich.\n" +
                        "Kreuzung kann nicht durchgefuehrt werden!");
            }

            if (!ignorePhysiktyp && NumberTools.notEqual(rangSrc.getPhysikTypId(), rangDest.getPhysikTypId())) {
                throw new StoreException("Die Physiktypen der Rangierungen sind unterschiedlich.\n" +
                        "Kreuzung kann nicht durchgefuehrt werden!");
            }

            if (eqOut) {
                Long eqOutSrc = rangSrc.getEqOutId();
                Long eqOutDest = rangDest.getEqOutId();
                rangSrc.setEqOutId(eqOutDest);
                rangDest.setEqOutId(eqOutSrc);
            }

            if (eqIn) {
                Long eqInSrc = rangSrc.getEqInId();
                Long eqInDest = rangDest.getEqInId();
                rangSrc.setEqInId(eqInDest);
                rangDest.setEqInId(eqInSrc);
            }

            Rangierung newRangSrc = saveRangierung(rangSrc, true);
            newRangSrc.setEsId(rangDest.getEsId());
            saveRangierung(newRangSrc, false);

            Rangierung newRangDest = saveRangierung(rangDest, true);
            newRangDest.setEsId(rangSrc.getEsId());
            saveRangierung(newRangDest, false);

            Endstelle esSrc = null;
            Endstelle esDest = null;
            AuftragTechnik auftragTechnikSrc = null;
            AuftragTechnik auftragTechnikDest = null;

            if (updateES || (physikaenderung != null)) {
                esSrc = ((newRangSrc.getEsId() != null) && (newRangSrc.getEsId() > 0))
                        ? endstellenService.findEndstelle(newRangSrc.getEsId()) : null;
                esDest = ((newRangDest.getEsId() != null) && (newRangDest.getEsId() > 0))
                        ? endstellenService.findEndstelle(newRangDest.getEsId()) : null;

                auftragTechnikSrc = ccAuftragService.findAuftragTechnik4ESGruppe(
                        (esSrc != null) ? esSrc.getEndstelleGruppeId() : null);
                auftragTechnikDest = ccAuftragService.findAuftragTechnik4ESGruppe(
                        (esDest != null) ? esDest.getEndstelleGruppeId() : null);
            }

            if (updateES) {
                // den Endstellen werden die neuen Rangierung-IDs zugeordnet
                Long cb2esSrc = (esSrc != null) ? esSrc.getCb2EsId() : null;
                if (esDest != null) {
                    esDest.setRangierId(newRangDest.getId());
                    if ((cb2esSrc != null) && switchCB) {
                        esDest.setCb2EsId(cb2esSrc);
                    }
                    endstellenService.saveEndstelle(esDest);
                }
                else if (esSrc != null) {
                    // Bei Kreuzung auf gleichem Auftrag...
                    esSrc.setRangierId(newRangDest.getId());
                    endstellenService.saveEndstelle(esSrc);
                }
            }

            // Bei einer Kreuzung auf einem Auftrag kann nur ein Auftrag ermittelt werden.
            // Der fehlende Auftrag wird nachfolgend auf den gefundenen Auftrag gesetzt
            // (wird fuer die Protokollierung der PhysikUebernahme benoetigt).
            auftragTechnikSrc = (auftragTechnikSrc != null) ? auftragTechnikSrc : auftragTechnikDest;
            auftragTechnikDest = (auftragTechnikDest != null) ? auftragTechnikDest : auftragTechnikSrc;

            if ((physikaenderung != null) && (auftragTechnikSrc != null) && (auftragTechnikDest != null)) {
                logPhysikUebernahme(auftragTechnikSrc.getAuftragId(), auftragTechnikDest.getAuftragId(),
                        physikaenderung);
            }

            List<Rangierung> retVal = new ArrayList<>();
            retVal.add(newRangSrc);
            retVal.add(newRangDest);
            return retVal;
        }
        catch (StoreException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Beim Kreuzen der Rangierungen ist ein Fehler aufgetreten!");
        }
    }

    @Override
    public void logPhysikUebernahme(Long auftragIdOld, Long auftragIdNew, Long aenderungstyp) {
        try {
            if ((auftragIdOld != null) && (auftragIdNew != null)) {
                PhysikUebernahme old2new = new PhysikUebernahme();
                old2new.setKriterium(PhysikUebernahme.KRITERIUM_ALT_NEU);
                old2new.setAuftragIdA(auftragIdOld);
                old2new.setAuftragIdB(auftragIdNew);
                old2new.setAenderungstyp(aenderungstyp);

                PhysikUebernahme new2old = new PhysikUebernahme();
                new2old.setKriterium(PhysikUebernahme.KRITERIUM_NEU_ALT);
                new2old.setAuftragIdA(auftragIdNew);
                new2old.setAuftragIdB(auftragIdOld);
                new2old.setAenderungstyp(aenderungstyp);

                PhysikUebernahmeDAO dao = getPhysikUebernahmeDAO();
                Long vorgangId = dao.getMaxVorgangId();
                vorgangId = (vorgangId != null) ? Long.valueOf(vorgangId + 1) : Long.valueOf(1);

                old2new.setVorgang(vorgangId);
                new2old.setVorgang(vorgangId);

                dao.store(old2new);
                dao.store(new2old);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public Map<Long, String> checkPhysiktypen(Long startAuftragId, Long endAuftragId) throws FindException {
        try {
            IServiceCommand cmd = serviceLocator.getCmdBean(PhysiktypConsistenceCheckCommand.class);
            cmd.prepare(PhysiktypConsistenceCheckCommand.START_AUFTRAG_ID, startAuftragId);
            cmd.prepare(PhysiktypConsistenceCheckCommand.END_AUFTRAG_ID, endAuftragId);
            return (Map<Long, String>) cmd.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<Equipment> findEQByLeiste(Long hvtId, String leiste) throws FindException {
        return findEQByLeisteStiftInt(hvtId, null, leiste, null);
    }

    @Override
    public Equipment findEQByLeisteStift(Long hvtId, String leiste, String stift) throws FindException {
        if ((hvtId == null) || StringUtils.isBlank(leiste) || StringUtils.isBlank(stift)) {
            return null;
        }
        List<Equipment> eqs = findEQByLeisteStiftInt(hvtId, null, leiste, stift);
        return ((eqs != null) && (eqs.size() == 1)) ? eqs.get(0) : null;
    }

    @Override
    public Equipment findEQByVerteilerLeisteStift(@Nonnull Long hvtId, @Nonnull String verteiler,
            @Nonnull String leiste, @Nonnull String stift) throws FindException {
        if ((hvtId == null) || StringUtils.isBlank(verteiler) || StringUtils.isBlank(leiste)
                || StringUtils.isBlank(stift)) {
            return null;
        }
        List<Equipment> eqs = findEQByLeisteStiftInt(hvtId, verteiler, leiste, stift);
        return ((eqs != null) && (eqs.size() == 1)) ? eqs.get(0) : null;
    }

    @Override
    public Equipment findEQByKVZNrKVZDA(@Nonnull Long hvtId, @Nonnull String kvzNr, @Nonnull String kvzDA)
            throws FindException {
        if ((hvtId == null) || StringUtils.isBlank(kvzNr) || StringUtils.isBlank(kvzDA)) {
            return null;
        }
        try {
            Equipment example = new Equipment();
            example.setHvtIdStandort(hvtId);
            example.setKvzDoppelader(kvzDA);
            example.setKvzNummer(kvzNr);
            List<Equipment> eqs = getEquipmentDAO().queryByExample(example, Equipment.class);
            return ((eqs != null) && (eqs.size() == 1)) ? eqs.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Interne Funktion für das Auffinden einer Endstelle, mit oder ohne Stift
     *
     * @param hvtId  Id des HVT
     * @param leiste die Bezeichnung der Leiste
     * @param stift  die Angabe eines Stift ist optinal
     * @return eine Liste mit gefundene PDHLeisten (falls Stift gegeben mit max. einem Eintrag)
     * @throws FindException falls ein Fehler auftritt
     */
    private List<Equipment> findEQByLeisteStiftInt(Long hvtId, String verteiler, String leiste, String stift)
            throws FindException {
        if ((hvtId == null) || StringUtils.isBlank(leiste)) {
            return new ArrayList<>();
        }
        try {
            Equipment example = new Equipment();
            example.setHvtIdStandort(hvtId);
            if (!StringUtils.isBlank(verteiler)) {
                example.setRangVerteiler(verteiler);
            }
            example.setRangLeiste1(leiste);
            if (!StringUtils.isBlank(stift)) {
                example.setRangStift1(stift);
            }

            return getEquipmentDAO().queryByExample(example, Equipment.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void createPdhLeisten(HVTStandort hvtStandort, String leiste, Integer stifte, Long sessionId)
            throws StoreException {
        if ((hvtStandort == null) || StringUtils.isBlank(leiste) || (stifte == null) || (sessionId == null)) {
            throw new StoreException("Eingabedaten für das Speichern der Daten fehlen.");
        }
        try {
            if (!findEQByLeiste(hvtStandort.getId(), leiste).isEmpty()) {
                throw new StoreException("Leiste '" + leiste + "' ist für diesen HVT bereits vorhanden.");
            }

            AKUser user = getAKUserBySessionIdSilent(sessionId);
            if (user == null) {
                throw new StoreException("Angemeldeter Benutzer ist nicht gültig für diese Operation");
            }
            for (int stift = 1; stift <= stifte; stift++) {
                Equipment pdhLeiste = new Equipment();
                pdhLeiste.setUserW(user.getLoginName());
                pdhLeiste.setHvtIdStandort(hvtStandort.getId());
                pdhLeiste.setCarrier(TNB.MNET.carrierNameUC);
                pdhLeiste.setRangLeiste1(leiste);
                pdhLeiste.setRangStift1(format("%02d", stift));
                pdhLeiste.setHwSchnittstelle(Equipment.HW_SCHNITTSTELLE_PDH_OUT);
                pdhLeiste.setRangSSType(Equipment.HW_SCHNITTSTELLE_PDH_OUT);
                pdhLeiste.setStatus(EqStatus.frei);
                saveEquipment(pdhLeiste);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Integer findNextLtgGesId4Rangierung() throws FindException {
        try {
            return getRangierungDAO().getNextLtgGesId();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }

    }

    @Override
    public boolean isPortInADslam(Equipment port) throws FindException {
        try {
            if ((port == null) || (port.getHwBaugruppenId() == null)) {
                return false;
            }

            HWBaugruppe hwBaugruppe = hwService.findBaugruppe(port.getHwBaugruppenId());
            if (hwBaugruppe == null) {
                throw new FindException("Der Port ist keiner Bauguppe zugeordnet.");
            }
            HWRack rack = hwService.findRackById(hwBaugruppe.getRackId());
            return rack instanceof HWDslam;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean isListeOfPortsFree(List<Equipment> ports) throws FindException {

        if ((ports == null) || (ports.isEmpty())) {
            return false;
        }

        for (Equipment port : ports) {
            if (!isPortFree(port)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isPortFree(Equipment port) throws FindException {

        try {
            if (port == null) {
                return false;
            }

            Rangierung rangierung = findRangierung4Equipment(port.getId());

            if (rangierung != null) {
                final Date now = new Date();
                return (rangierung.getEsId() == null)
                        && (DateTools.isDateBeforeOrEqual(rangierung.getGueltigVon(), now)
                        && DateTools.isAfter(rangierung.getGueltigBis(), now));
            }
            else {
                return true;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }



    @Override
    @Nonnull
    public List<Equipment> findConsecutivePorts(@Nonnull Equipment firstPort, int anzahl) throws FindException {
        HWBaugruppe baugruppe = hwService.findBaugruppe(firstPort.getHwBaugruppenId());
        HVTTechnik hvtTechnik = baugruppe.getHwBaugruppenTyp().getHvtTechnik();
        String einbauPlatz = null;
        String eqnFormat = null;
        String alternateEqnFormat = null;
        int slot = 0, port = 0;
        String newEQN;
        String newEQNAlt;
        if (HVTTechnik.isHVTTechnikAGB(hvtTechnik.getId())) {
            einbauPlatz = firstPort.getHwEQNPart(Equipment.HWEQNPART_DSLAM_EINBAU);
            slot = firstPort.getHwEQNPartAsInt(Equipment.HWEQNPART_DSLAM_SLOT);
            port = firstPort.getHwEQNPartAsInt(Equipment.HWEQNPART_DSLAM_PORT);
            eqnFormat = "%s%s%03d%s%02d";
        }
        else if (HVTTechnik.isHVTTechnikMUC(hvtTechnik.getId())) {
            einbauPlatz = firstPort.getHwEQNPart(Equipment.HWEQNPART_DSLAM_EINBAU_ALCATEL);
            slot = firstPort.getHwEQNPartAsInt(Equipment.HWEQNPART_DSLAM_SLOT_ALCATEL);
            port = firstPort.getHwEQNPartAsInt(Equipment.HWEQNPART_DSLAM_PORT_ALCATEL);
            eqnFormat = "%s%s%d%s%d";
            alternateEqnFormat = "%s%s%d%s%02d";
        }
        if ((einbauPlatz != null) && (slot >= 0) && (port >= 0)) {
            List<Equipment> result = new ArrayList<>();
            result.add(firstPort);
            for (int i = 1; i < anzahl; i++) {
                newEQNAlt = null;
                int newPort = port + i;
                newEQN = format(eqnFormat,
                        einbauPlatz, Equipment.HW_EQN_PART_SEPARATOR, slot, Equipment.HW_EQN_PART_SEPARATOR, newPort);
                Equipment nextPort = findEquipment(baugruppe.getRackId(), newEQN, firstPort.getRangSSType());
                if (nextPort == null && StringUtils.isNotBlank(alternateEqnFormat)) {
                    newEQNAlt = format(alternateEqnFormat,
                            einbauPlatz, Equipment.HW_EQN_PART_SEPARATOR, slot, Equipment.HW_EQN_PART_SEPARATOR,
                            newPort);
                    nextPort = findEquipment(baugruppe.getRackId(), newEQNAlt, firstPort.getRangSSType());
                }

                if (nextPort == null) {
                    throw new FindException(format(
                            "Der Port %s (alternativ: %s) konnte nicht ermittelt werden!", newEQN, newEQNAlt));
                }

                result.add(nextPort);
            }
            return result;
        }
        throw new FindException(format("Suchparameter inkorrekt: Einbauplatz %s, Slot %d oder Port %d!",
                einbauPlatz, slot, port));
    }

    @Override
    public List<Equipment> findConsecutiveUEVTStifte(@Nonnull Equipment ersterStift, int anzahl) throws FindException {
        // Preconditions
        Preconditions.checkNotNull(ersterStift, "Der erste Stift muss angegeben sein!");
        Preconditions.checkArgument(anzahl > 0, "Die Anzahl muss >0 sein!");
        Long hvtIdStandort = ersterStift.getHvtIdStandort();
        String verteiler = ersterStift.getRangVerteiler();
        String leiste = ersterStift.getRangLeiste1();
        int stift = Integer.parseInt(ersterStift.getRangStift1()) + 1;
        List<Equipment> result = new ArrayList<>();
        result.add(ersterStift);
        for (int i = 1; i < anzahl; i++, stift++) {
            String nextRangStift = format("%02d", stift);
            Equipment nextStift = findEQByVerteilerLeisteStift(hvtIdStandort, verteiler, leiste, nextRangStift);
            if (nextStift == null) {
                throw new FindException(format("Der UeVT Stift %s %s %s konnte nicht ermittelt werden!",
                        verteiler, leiste, nextRangStift));
            }
            result.add(nextStift);
        }
        return result;
    }

    @Override
    public void setLayer2ProtocolForPorts(List<Equipment> consecutivePorts, Schicht2Protokoll protocol)
            throws StoreException {
        for (Equipment equipment : consecutivePorts) {
            equipment.setSchicht2Protokoll(protocol);
            saveEquipment(equipment);
        }
    }

    @Override
    public List<RangierungWithEquipmentView> findRangierungWithEquipmentViews(Set<Long> rangierungIds) {
        return getRangierungDAO().findRangierungWithEquipmentViews(rangierungIds);
    }

    @Override
    public Equipment findCorrespondingEquipment(Equipment eq) {
        return getEquipmentDAO().findCorrespondingEquipment(eq);
    }

    @Override
    public UEVT findUevt(Rangierung rangierung) throws FindException {
        Assert.notNull(rangierung, "keine Rangierung angegeben");

        Equipment eqOut = findEquipment(rangierung.getEqOutId());
        if (eqOut == null) {
            throw new FindException("Keinen EqOut-Stift für die übergebene Rangierung gefunden.");
        }

        String uevtName = eqOut.getRangVerteiler();
        if (StringUtils.isBlank(uevtName)) {
            throw new FindException("Der EqOut-Stift der übergebenen Rangierung hat keinen Üvt gesetzt.");
        }
        return hvtService.findUEVT(rangierung.getHvtIdStandort(), uevtName);
    }

    @Override
    public boolean isBandwidthPossible4Rangierung(@CheckForNull Rangierung rangierung,
            @CheckForNull Bandwidth requiredBandwidth) throws FindException {
        // Precondition(s)
        if (rangierung == null) {
            throw new FindException(
                    "Bandbreite für Rangierung kann nicht überprüft werden, da Rangierung nicht gesetzt!");
        }
        // Optionalen Parameter behandeln
        if (requiredBandwidth == null) {
            return true;
        }
        try {
            // Check durchfuehren
            Bandwidth maxBandwidth = getMaxBandwidth4Rangierung(rangierung);
            return maxBandwidth == null || requiredBandwidth.isWithin(maxBandwidth);
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            throw new FindException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasRangierung(Long auftragId, String esTyp) throws FindException {
        try {
            Endstelle endstelle4Auftrag = endstellenService.findEndstelle4Auftrag(auftragId, esTyp);
            return endstelle4Auftrag.hasRangierung();
        }
        catch (Exception e) {
            throw new FindException(format("Waehrend der Ermittlung der Endstelle für die " +
                    "Auftragsnummern '%s' ist ein unerwarteter Fehler aufgetreten.", auftragId), e);
        }
    }

    /**
     * @return Returns the rangierungDAO.
     */
    private RangierungDAO getRangierungDAO() {
        return rangierungDAO;
    }

    /**
     * Injected
     */
    public void setRangierungDAO(RangierungDAO rangierungDAO) {
        this.rangierungDAO = rangierungDAO;
    }

    /**
     * @return Returns the equipmentDAO.
     */
    private EquipmentDAO getEquipmentDAO() {
        return equipmentDAO;
    }

    /**
     * Injected
     */
    public void setEquipmentDAO(EquipmentDAO equipmentDAO) {
        this.equipmentDAO = equipmentDAO;
    }

    /**
     * @return Returns the rangierungsmatrixDAO.
     */
    private RangierungsmatrixDAO getRangierungsmatrixDAO() {
        return rangierungsmatrixDAO;
    }

    /**
     * Injected
     */
    public void setRangierungsmatrixDAO(RangierungsmatrixDAO rangierungsmatrixDAO) {
        this.rangierungsmatrixDAO = rangierungsmatrixDAO;
    }

    /**
     * @return Returns the rangierungsmatrixValidator.
     */
    public RangierungsmatrixValidator getRangierungsmatrixValidator() {
        return rangierungsmatrixValidator;
    }

    /**
     * Injected
     */
    public void setRangierungsmatrixValidator(RangierungsmatrixValidator rangierungsmatrixValidator) {
        this.rangierungsmatrixValidator = rangierungsmatrixValidator;
    }

    /**
     * @return Returns the physikUebernahmeDAO.
     */
    private PhysikUebernahmeDAO getPhysikUebernahmeDAO() {
        return physikUebernahmeDAO;
    }

    /**
     * Injected
     */
    public void setPhysikUebernahmeDAO(PhysikUebernahmeDAO physikUebernahmeDAO) {
        this.physikUebernahmeDAO = physikUebernahmeDAO;
    }

    /**
     * Injected
     */
    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    /**
     * Injected
     */
    public void setCcAuftragService(CCAuftragService ccAuftragService) {
        this.ccAuftragService = ccAuftragService;
    }

    /**
     * Injected
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Injected
     */
    public void setChainService(ChainService chainService) {
        this.chainService = chainService;
    }

    /**
     * Injected
     */
    public void setHvtService(HVTService hvtService) {
        this.hvtService = hvtService;
    }

    /**
     * Injected
     */
    public void setEndstellenService(EndstellenService endstellenService) {
        this.endstellenService = endstellenService;
    }

    private void initPhysikService() throws ServiceNotFoundException {
        if (physikService == null) {
            setPhysikService(getCCService(PhysikService.class));
        }
    }

    /**
     * Injected
     */
    public void setPhysikService(PhysikService physikService) {
        this.physikService = physikService;
    }

    /**
     * Injected
     */
    public void setCarrierService(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    /**
     * Injected
     */
    public void setAvailabilityService(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

}
