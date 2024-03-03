/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 11:33:33
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.validation.AbstractValidator;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.BrasPoolDAO;
import de.augustakom.hurrican.dao.cc.EQCrossConnectionDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.IpMode;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.RegularExpressionService;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcCalculator;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcHelper;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcStrategyType;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcTechLeistungStrategy;
import de.augustakom.hurrican.service.cc.impl.crossconnect.CcTechLeistungStrategyTyp;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Service-Implementierung von {@link EQCrossConnectionService}
 */
@CcTxRequired
public class EQCrossConnectionServiceImpl extends DefaultCCService implements EQCrossConnectionService {
    private static final Logger LOGGER = Logger.getLogger(EQCrossConnectionServiceImpl.class);
    private static final Set<Integer> EMPTY_INT_SET = new HashSet<Integer>(0);
    private static final List<EQCrossConnection> NO_CROSS_CONNECTIONS = Collections.emptyList();

    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;
    @Resource(name = "de.augustakom.hurrican.dao.cc.BrasPoolDAO")
    private BrasPoolDAO brasPoolDao;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.validation.cc.EQCrossConnectionValidator")
    private AbstractValidator eqCrossConnectionValidator;
    @Resource(name = "de.augustakom.hurrican.service.cc.RegularExpressionService")
    private RegularExpressionService regularExpressionService;
    @Resource(name = "de.augustakom.hurrican.service.cc.FeatureService")
    private FeatureService featureService;

    @Override
    public List<EQCrossConnection> findEQCrossConnections(Long equipmentId, Date validDate) throws FindException {
        try {
            return ((EQCrossConnectionDAO) getDAO()).findEQCrossConnections(equipmentId, validDate);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<EQCrossConnection> findEQCrossConnections(Long equipmentId) throws FindException {
        try {
            List<EQCrossConnection> ccList = ((EQCrossConnectionDAO) getDAO()).findEQCrossConnections(equipmentId);
            if (ccList == null) {
                return new ArrayList<EQCrossConnection>();
            }
            return ccList;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveEQCrossConnection(EQCrossConnection toSave) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }
        try {
            validateCrossConnection(toSave);
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveEQCrossConnections(List<EQCrossConnection> toSave) throws StoreException, ValidationException {
        try {
            validateCrossConnectionDoNotOverlap(toSave);
            for (EQCrossConnection cc : toSave) {
                validateCrossConnection(cc);
                ((StoreDAO) getDAO()).store(cc);
            }
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

    /**
     * Validates that the gueltigVon and gueltigBis dates of Cross Connections of the same type do not overlap.
     */
    private void validateCrossConnectionDoNotOverlap(List<EQCrossConnection> crossConnectionsToValidate)
            throws ValidationException {
        for (EQCrossConnection toCheck : crossConnectionsToValidate) {
            for (EQCrossConnection checkAgainst : crossConnectionsToValidate) {
                if ((toCheck != checkAgainst) && toCheck.hasSameTypeAs(checkAgainst)) {
                    Date toCheckValidFrom = toCheck.getGueltigVon();
                    Date toCheckValidTo = toCheck.getGueltigBis();
                    Date checkAgainstValidFrom = checkAgainst.getGueltigVon();
                    Date checkAgainstValidTo = checkAgainst.getGueltigBis();
                    if (DateTools.isDateBetween(toCheckValidFrom, checkAgainstValidFrom, checkAgainstValidTo) ||
                            DateTools.isDateBetween(toCheckValidTo, checkAgainstValidFrom, checkAgainstValidTo)) {
                        ValidationException validationException = new ValidationException(toCheck, "toCheck");
                        validationException.rejectValue("gueltigVon", "invalid",
                                "Cross Connections gleichen Typs dürfen nicht gleichzeitig gültig sein.");
                        throw validationException;
                    }
                }
            }
        }
    }

    private void validateCrossConnection(EQCrossConnection toValidate) throws ValidationException {
        ValidationException valEx = new ValidationException(toValidate, "EQCrossConnection");
        eqCrossConnectionValidator.validate(toValidate, valEx);
        if (valEx.hasErrors()) {
            throw valEx;
        }
    }

    @Override
    public List<EQCrossConnection> calculateDefaultCcs(Equipment port, String userW, @Nonnull Date when, Long auftragId)
            throws FindException {
        boolean hasVoip = false;
        boolean hasBusinessCPE = false;
        boolean hasIpV6 = false;
        try {
            if (auftragId != null) {
                hasVoip = leistungsService.hasVoipLeistungFromThenOn(auftragId, when);
                Set<Long> techLsNo = leistungsService.findTechLeistungenNo4Auftrag(auftragId,
                        DateConverterUtils.asLocalDate(when));
                if (techLsNo.isEmpty()) {
                    Date firstFutureTechLsDate = leistungsService.getFirstFutureTechLsDate(auftragId);
                    if (firstFutureTechLsDate != null) {
                        techLsNo = leistungsService.findTechLeistungenNo4Auftrag(auftragId,
                                DateConverterUtils.asLocalDate(firstFutureTechLsDate));
                    }
                }
                hasBusinessCPE = techLsNo.contains(TechLeistung.ID_BUSINESS_CPE);
                hasIpV6 = IpMode.fromLeistungen(techLsNo).hasIpV6();
            }
        }
        catch (FindException e) {
            LOGGER.trace("calculateDefaultCcs() - findException for VoIP, does not exist");
        }
        return calculateDefaultCcs(port, userW, when, hasVoip, hasBusinessCPE, hasIpV6);
    }

    @Override
    public List<EQCrossConnection> calculateDefaultCcs(Equipment port, String userW, @Nonnull Date activationDate,
            boolean hasVoip, boolean hasBusinessCPE, boolean hasIpV6)
            throws FindException {
        try {
            Pair<CcStrategyType, CcCalculator> configuration = configureCalculator(port, hasVoip, hasIpV6);
            if (configuration == null) {
                return NO_CROSS_CONNECTIONS;
            }

            List<CcStrategyType.CcType> strategyTypes;
            strategyTypes = CcTechLeistungStrategy.get(CcTechLeistungStrategyTyp.create(hasVoip, hasBusinessCPE,
                    hasIpV6));

            // 03.10.2010: Nuernberger Siemens-DSLAMs besitzen keine CrossConnections, da sie nicht automatisiert
            // provisioniert werden koennen.
            if (CcStrategyType.NlType.NBG.equals(configuration.getFirst().getNlType()) &&
                    CcStrategyType.TechType.SIE.equals(configuration.getFirst().getTechType())) {
                return NO_CROSS_CONNECTIONS;
            }
            // MKK nur VDSL2
            if (CcStrategyType.NlType.MKK.equals(configuration.getFirst().getNlType())
                    && !CcStrategyType.DslType.VDSL2.equals(configuration.getFirst().getDslType())) {
                return NO_CROSS_CONNECTIONS;
            }

            // 03.10.2010: In Nuernberger ISAMs (Nbg: Alcatel nur ISAMs) soll keine CPE-CC angelegt werden,
            // wenn OuterTagCpeMgmt nicht gesetzt ist, da keine CPE-CC existiert und die Geraete sonst einen
            // Fehler liefern. Eine nachtraegliche Umstellung soll durch Setzen des OuterTagCpeMgmt durchgefuehrt
            // werden koennen, dann sollen die Default-CCs fuer neu angelegte CCs die CPE-CC wieder enthalten.
            if (!hasVoip &&
                    CcStrategyType.NlType.NBG.equals(configuration.getFirst().getNlType()) &&
                    CcStrategyType.TechType.ALC.equals(configuration.getFirst().getTechType()) &&
                    CcStrategyType.HwType.IP.equals(configuration.getFirst().getHwType()) &&
                    (configuration.getSecond().getDslam().getOuterTagCpeMgmt() == null)) {
                strategyTypes.remove(CcStrategyType.CcType.CPE);
            }

            // Huawei haben nur HSI bei ADSL ohne IPv6 und ohne Voip
            if (!hasIpV6 && !hasVoip &&
                    CcStrategyType.TechType.HUA.equals(configuration.getFirst().getTechType()) &&
                    CcStrategyType.HwType.IP.equals(configuration.getFirst().getHwType()) &&
                    CcStrategyType.DslType.ADSL.equals(configuration.getFirst().getDslType())) {
                strategyTypes.retainAll(Arrays.asList(CcStrategyType.CcType.HSI));
            }

            List<EQCrossConnection> result = doCalculation(port, userW, activationDate,
                    configuration.getFirst(), configuration.getSecond(), strategyTypes);
            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean isCrossConnectionEnabled(Equipment port) throws FindException {
        if ((port == null) || (port.getHwBaugruppenId() == null)) {
            return false;
        }
        HWBaugruppe hwBaugruppe = hwService.findBaugruppe(port.getHwBaugruppenId());
        if (hwBaugruppe == null) {
            throw new FindException("Der Port ist keiner Baugruppe zugeordnet.");
        }
        final HWBaugruppenTyp hwBaugruppenTyp = hwBaugruppe.getHwBaugruppenTyp();
        if ((hwBaugruppenTyp.getTunneling() != null) &&
                !HWBaugruppenTyp.Tunneling.CC.equals(hwBaugruppenTyp.getTunneling())) {
            return false;
        }

        HWRack rack = hwService.findRackById(hwBaugruppe.getRackId());

        return isCrossConnectionEnabled(rack);
    }

    protected boolean isCrossConnectionEnabled(HWRack rack) throws FindException {
        // keine Berechnung fuer Siemens
        if (HVTTechnik.SIEMENS.equals(rack.getHwProducer())) {
            return false;
        }
        return true;
    }

    /**
     * Ermittelt aus dem Port die noetigen Informationen fuer die Berechnung der CrossConnections. Erstellt einen neuen
     * Calculator fuer die CC-Berechnung und eine neue Strategie fuer diesen Calculator.
     *
     * @return das strategyType/calculator pair order null wenn keine CC berechnet werden sollen
     */
    private Pair<CcStrategyType, CcCalculator> configureCalculator(Equipment port, boolean hasVoip, boolean hasIpV6)
            throws FindException {
        HWBaugruppe baugruppe = hwService.findBaugruppe(port.getHwBaugruppenId());
        HWSubrack subrack = hwService.findSubrackById(baugruppe.getSubrackId());
        HWRack rack = hwService.findRackById(baugruppe.getRackId());
        if (!(rack instanceof HWDslam)) {
            throw new FindException("The given port is not a DSLAM port.");
        }
        HWDslam dslam = (HWDslam) rack;

        if (!isCrossConnectionEnabled(port)) {
            return null;
        }

        List<HWSubrack> subrackList = hwService.findSubracksForRack(baugruppe.getRackId());
        Collections.sort(subrackList, HWSubrack.SUBRACK_COMPARATOR);

        HVTStandort hvtStandort = hvtService.findHVTStandort(dslam.getHvtIdStandort());
        if (hvtStandort == null) {
            throw new FindException("Could not determine HVT Standort for port.");
        }
        HVTGruppe hvtGruppe = hvtService.findHVTGruppeById(hvtStandort.getHvtGruppeId());
        if (hvtGruppe == null) {
            throw new FindException("Could not determine HVT Gruppe for port.");
        }

        CcStrategyType strategyType = new CcStrategyType();
        strategyType.setVoip(Boolean.valueOf(hasVoip));
        strategyType.setIpV6(Boolean.valueOf(hasIpV6));
        strategyType.setModelType(dslam.getDslamType());
        strategyType.setNlType(CcHelper.getNlType(hvtGruppe.getNiederlassungId()));
        strategyType.setDslType(CcHelper.getDslType(port));
        strategyType.setTechType(CcHelper.getTechType(dslam));
        strategyType.setHwType(CcHelper.getHwType(dslam));

        CcCalculator calculator = new CcCalculator();
        calculator.configure(this, regularExpressionService, port, baugruppe, dslam, subrack, subrackList,
                baugruppe.getHwBaugruppenTyp());
        return Pair.create(strategyType, calculator);
    }

    /**
     * Erstellt fuer die gegebenen Werte die CrossConnections und liefert eine Liste der CrossConnections zurueck.
     *
     * @return Nicht-leere Liste von CrossConnections
     */
    private List<EQCrossConnection> doCalculation(Equipment port, String userW, Date activationDate,
            CcStrategyType strategyType, CcCalculator calculator, List<CcStrategyType.CcType> strategyTypes) {
        List<EQCrossConnection> result = new ArrayList<>();
        if (CollectionTools.isNotEmpty(strategyTypes)) {
            for (CcStrategyType.CcType ccType : strategyTypes) {
                strategyType.setCcType(ccType);
                calculator.determineStrategies(strategyType);

                EQCrossConnection crossConnection = new EQCrossConnection();
                crossConnection.setEquipmentId(port.getId());
                crossConnection.setLtOuter(calculator.getLtOuter());
                crossConnection.setLtInner(calculator.getLtInner());
                crossConnection.setNtOuter(calculator.getNtOuter());
                crossConnection.setNtInner(calculator.getNtInner());
                crossConnection.setBrasOuter(calculator.getBrasOuter());
                crossConnection.setBrasInner(calculator.getBrasInner());
                crossConnection.setBrasPool(calculator.getBrasPool());
                crossConnection.setCrossConnectionTypeRefId(calculator.getCcRefId(strategyType));
                crossConnection.setGueltigVon(activationDate);
                crossConnection.setGueltigBis(DateTools.getHurricanEndDate());
                crossConnection.setUserW(userW);

                result.add(crossConnection);
            }
        }
        return result;
    }

    @Override
    public Integer getVcFromPool(BrasPool brasPool) throws FindException {
        return getVcFromPool(brasPool, null, null, EMPTY_INT_SET);
    }

    @Override
    public Integer getVcFromPool(BrasPool brasPool, Date from, Date till) throws FindException {
        return getVcFromPool(brasPool, from, till, null);
    }

    @Override
    public Integer getVcFromPool(BrasPool brasPool, Date from, Date till, Set<Integer> skip) throws FindException {
        if (brasPool == null) {
            throw new IllegalArgumentException("Bras Pool cannot be null");
        }
        List<Integer> used;
        try {
            used = ((EQCrossConnectionDAO) getDAO()).findUsedBrasVcs(brasPool);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        used.addAll(skip);
        Collections.sort(used);

        if (used.isEmpty()) {
            return brasPool.getVcMin();
        }

        int next = brasPool.getVcMin();
        Iterator<Integer> iterator = used.iterator();
        while (iterator.hasNext() && (next <= brasPool.getVcMax())) {
            int u = (iterator.next()).intValue();
            if (u > next) {
                return next;
            }
            next = next + 1;
        }

        if (next <= brasPool.getVcMax()) {
            return next;
        }
        // pool exhausted, return null
        return null;
    }

    @Override
    public List<BrasPool> getAllBrasPools() throws FindException {
        try {
            return brasPoolDao.findAll(BrasPool.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public BrasPool findBrasPoolById(Long brasPoolId) throws FindException {
        try {
            return brasPoolDao.findById(brasPoolId, BrasPool.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<BrasPool> findBrasPoolByNamePrefix(String poolPrefix) throws FindException {
        try {
            return brasPoolDao.findByNamePrefix(poolPrefix);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteEQCrossConnectionsOfEquipment(Long equipmentId) throws DeleteException {
        try {
            ((EQCrossConnectionDAO) getDAO()).deleteEQCrossConnectionsOfEquipment(equipmentId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void defineDefaultCrossConnections4Port(Equipment equipment, Long auftragId,
            Date changeDate, Boolean vierDrahtProdukt, Long sessionId) throws StoreException {
        defineDefaultCrossConnections4Port(equipment, auftragId, changeDate, vierDrahtProdukt, sessionId,
                new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public void defineDefaultCrossConnections4Port(Equipment equipment, Long auftragId,
            Date changeDate, Boolean vierDrahtProdukt, Long sessionId, List<EQCrossConnection> crossConnectionsToSet,
            List<EQCrossConnection> crossConnectionsToDeactivate) throws StoreException {
        try {

            List<EQCrossConnection> calculatedCrossConnections =
                    calculateDefaultCcs(equipment, getLoginNameSilent(sessionId), changeDate, auftragId);
            if (CollectionTools.isEmpty(calculatedCrossConnections) &&
                    !(calculatedCrossConnections == NO_CROSS_CONNECTIONS)) {
                throw new HurricanServiceCommandException(
                        "Could not calculate the default cross connections for the port!");
            }

            List<EQCrossConnection> existingCrossConnections =
                    findEQCrossConnections(equipment.getId(), changeDate);

            if (Boolean.TRUE.equals(vierDrahtProdukt)) {
                crossConnectionsToDeactivate = existingCrossConnections;
            }
            else if (CollectionTools.isNotEmpty(existingCrossConnections)) {
                // check differences in cross connections
                boolean isOverwritten = areCrossConnectionsOverwritten(calculatedCrossConnections,
                        existingCrossConnections);
                if (isOverwritten) {
                    crossConnectionsToSet.addAll(calculatedCrossConnections);
                    crossConnectionsToDeactivate.addAll(existingCrossConnections);
                }
            }
            else {
                crossConnectionsToSet.addAll(calculatedCrossConnections);
                crossConnectionsToDeactivate.addAll(getAllActiveCrossConnections(equipment, changeDate));
            }

            // Cross-Connections deaktivieren bzw. definieren
            deactivateCrossConnections(crossConnectionsToDeactivate, changeDate);

            if (CollectionTools.isNotEmpty(crossConnectionsToSet)) {
                for (EQCrossConnection toSet : crossConnectionsToSet) {
                    saveEQCrossConnection(toSet);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(
                    "Error validating or defining the cross connections: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt die Rangierung und den fuer die CC Berechnung relevanten Port.
     * @return falls Rangierung oder Port nicht ermittelt werden koennen, sind diese {@code null}!
     */
    @Nonnull
    Pair<Rangierung, Equipment> findCcPhysicsByEndstelle(@Nonnull final Endstelle endstelle) throws FindException {
        if (endstelle != null && endstelle.getRangierId() != null) {
            Rangierung rangierung = rangierungsService.findRangierung(endstelle.getRangierId());
            if (rangierung != null) {
                Equipment eqInPort = rangierungsService.findEquipment(rangierung.getEqInId());
                return Pair.create(rangierung, eqInPort);
            }
        }
        return Pair.empty();
    }

    @Override
    @Nonnull
    public Pair<Rangierung, Equipment> deactivateCrossConnections4Endstelle(@Nonnull final Endstelle endstelle,
            @Nonnull final Date changeDate) throws FindException, StoreException {
        Pair<Rangierung, Equipment> ccPhysics = findCcPhysicsByEndstelle(endstelle);
        Equipment eqInPort = ccPhysics.getSecond();
        if (eqInPort != null) {
            deactivateCrossConnections(findEQCrossConnections(eqInPort.getId(), changeDate), changeDate);
        }
        return ccPhysics;
    }

    @Override
    @CheckForNull
    public String checkCcsAllowed(@Nonnull final Endstelle endstelle) throws FindException {
        Pair<Rangierung, Equipment> ccPhysics = findCcPhysicsByEndstelle(endstelle);
        Rangierung rangierung = ccPhysics.getFirst();
        Equipment eqInPort = ccPhysics.getSecond();
        if (rangierung == null) {
            return "Die Endstelle hat keine Rangierung.";
        }

        if (eqInPort == null) {
            return "Die Rangierung hat keinen EQ-IN Port.";
        }

        AuftragDaten auftragDaten = auftragService.findAuftragDatenByEndstelle(endstelle.getId());
        Produkt produkt = produktService.findProdukt4Auftrag(auftragDaten.getAuftragId());
        if (BooleanTools.nullToFalse(produktService.isVierDrahtProdukt(produkt.getId()))) {
            return "Die Cross Connections können nicht für 4 Draht-Optionen festgelegt werden.";
        }

        if (!rangierungsService.isPortInADslam(eqInPort)) {
            return "Die zugehörige Baugruppe muss in einem DSLAM sein, um die Cross Connection festzulegen.";
        }

        if (!isCrossConnectionEnabled(eqInPort)) {
            return "Die Cross Connection Berechnung ist für den Port nicht aktiv.";
        }
        return null;
    }

    /**
     * Ermittelt alle noch aktiven CrossConnections des angegebenen Ports. (Teilweise notwendig, falls Bauauftrag
     * storniert wurde und mit frueherem Datum erneut erstellt wird. In diesem Fall wuerde die Ermittlung der
     * CrossConnections mit dem Real-Date kein Ergebnis liefern, es waeren aber evtl. CrossConnections fuer die Zukunft
     * vorhanden. Diese muessen deaktiviert werden!)
     */
    List<EQCrossConnection> getAllActiveCrossConnections(Equipment equipment, Date changeDate) throws FindException {
        List<EQCrossConnection> activeCrossConnections = new ArrayList<EQCrossConnection>();
        List<EQCrossConnection> crossConnections = findEQCrossConnections(equipment.getId());
        if (CollectionTools.isNotEmpty(crossConnections)) {
            for (EQCrossConnection crossConn : crossConnections) {
                if (DateTools.isDateAfter(crossConn.getGueltigBis(), changeDate)) {
                    activeCrossConnections.add(crossConn);
                }
            }
        }
        return activeCrossConnections;
    }

    /**
     * Deaktiviert die angegebenen CrossConnections zum Realisierungsdatum-1.
     */
    void deactivateCrossConnections(List<EQCrossConnection> crossConnectionsToDeactivate, Date changeDate)
            throws StoreException {
        if (CollectionTools.isNotEmpty(crossConnectionsToDeactivate)) {
            for (EQCrossConnection toDeactivate : crossConnectionsToDeactivate) {
                toDeactivate.setGueltigBis(DateTools.changeDate(changeDate, Calendar.DATE, -1));
                if (DateTools.isDateAfter(toDeactivate.getGueltigVon(), toDeactivate.getGueltigBis())) {
                    // sicherstellen, dass GueltigVon <= GueltigBis
                    toDeactivate.setGueltigVon(toDeactivate.getGueltigBis());
                }

                saveEQCrossConnection(toDeactivate);
            }
        }
    }

    /**
     * Ueberprueft, ob die beiden Listen Unterschiede in den CrossConnections aufweisen.
     *
     * @param calculatedCrossConnections
     * @param existingCrossConnections
     * @return true wenn die beiden Listen unterschiedliche Werte aufweisen
     */
    boolean areCrossConnectionsOverwritten(
            List<EQCrossConnection> calculatedCrossConnections,
            List<EQCrossConnection> existingCrossConnections) {
        Collections.sort(calculatedCrossConnections, EQCrossConnection.CC_TYPE_COMPARATOR);
        Collections.sort(existingCrossConnections, EQCrossConnection.CC_TYPE_COMPARATOR);

        boolean isOverwritten = false;
        if (calculatedCrossConnections.size() == existingCrossConnections.size()) {
            for (int i = 0; (i < existingCrossConnections.size()) && !isOverwritten; i++) {
                if (!existingCrossConnections.get(i).crossConnectionEqual(calculatedCrossConnections.get(i))) {
                    isOverwritten = true;
                }
            }
        }
        else {
            isOverwritten = true;
        }
        return isOverwritten;
    }
}
