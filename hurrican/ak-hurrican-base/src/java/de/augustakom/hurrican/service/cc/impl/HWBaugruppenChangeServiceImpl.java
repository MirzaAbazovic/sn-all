/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.04.2010 14:11:20
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.dao.cc.HWBaugruppenChangeDAO;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Rangierung.Freigegeben;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange.ChangeType;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeBgType;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeCard;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDlu;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangeDluV5;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortDetailView;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.impl.equipment.CancelHWBaugruppenChangeExecuter;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangeBgTypeExecuter;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangeCardExecuter;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangeCpsExecuter;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangeCpsReInitExecuter;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangeDluExecuter;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangeExecuter;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangeImportDluV5Executer;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangePortConcentrationExecuter;
import de.augustakom.hurrican.service.cc.impl.equipment.IstQuellRangierungFreiFuerPortkonzentrationImpl;
import de.augustakom.hurrican.service.cc.impl.equipment.IstZielRangierungFreiFuerPortkonzentrationImpl;
import de.augustakom.hurrican.tools.comparator.HwEqnComparator;
import de.augustakom.hurrican.validation.cc.HWBaugruppenChangeValidator;


/**
 * Implementierung von {@link HWBaugruppenChangeService}
 */
@CcTxRequired
public class HWBaugruppenChangeServiceImpl extends DefaultCCService implements
        HWBaugruppenChangeService {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;
    @Resource(name = "de.augustakom.hurrican.dao.cc.HWBaugruppenChangeDAO")
    private HWBaugruppenChangeDAO hwBaugruppenChangeDAO;
    @Resource(name = "de.augustakom.hurrican.validation.cc.HWBaugruppenChangeValidator")
    private HWBaugruppenChangeValidator hwBaugruppenChangeValidator;
    @Resource(name = "de.augustakom.hurrican.service.cc.EQCrossConnectionService")
    private EQCrossConnectionService eqCrossConnectionService;
    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;
    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;
    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;

    @Resource(name = "de.augustakom.hurrican.service.cc.DSLAMService")
    private DSLAMService dslamService;

    @Override
    public void saveHWBaugruppenChange(HWBaugruppenChange toSave) throws StoreException, ValidationException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }

        if (toSave.getId() == null) {
            toSave.setChangeState(getReferenceByChangeState(HWBaugruppenChange.ChangeState.CHANGE_STATE_PLANNING));
        }
        validateHWBaugruppenChange(toSave);

        try {
            hwBaugruppenChangeDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /* Ermittelt das Reference-Objekt fuer einen bestimmten Status. */
    private Reference getReferenceByChangeState(HWBaugruppenChange.ChangeState changeState) {
        try {
            return referenceService.findReference(changeState.refId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /* Fuehrt die Validierung des angegebenen Objekts durch. */
    private void validateHWBaugruppenChange(HWBaugruppenChange toValidate) throws ValidationException {
        ValidationException valEx = new ValidationException(toValidate, "HWBaugruppenChange");
        hwBaugruppenChangeValidator.validate(toValidate, valEx);
        if (valEx.hasErrors()) {
            throw valEx;
        }
    }

    @Override
    public void deleteHWBaugruppenChangeBgType(HWBaugruppenChangeBgType toDelete) throws DeleteException {
        try {
            hwBaugruppenChangeDAO.deleteHWBaugruppenChangeBgType(toDelete);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    @Transactional(
            value = "cc.hibernateTxManager",
            rollbackFor = de.augustakom.hurrican.service.base.exceptions.StoreException.class,
            noRollbackFor = de.augustakom.hurrican.service.base.exceptions.FindException.class,
            propagation = Propagation.REQUIRED
    )
    public List<HWBaugruppenChangePort2Port> createPort2Port4ChangeCard(HWBaugruppenChange hwBgChange) throws StoreException, FindException {
        if (hwBgChange == null || (!hwBgChange.isChangeType(HWBaugruppenChange.ChangeType.CHANGE_CARD)
                && !hwBgChange.isChangeType(HWBaugruppenChange.ChangeType.MERGE_CARDS))) {
            throw new StoreException("Die angegebene Planung ist fuer diese Art der Port-Ermittlung nicht geeignet!");
        }

        if (!hwBgChange.isPreparingAllowed()) {
            throw new StoreException("Im aktuellen Status darf das Port-Mapping nicht mehr ausgeführt werden!");
        }

        try {
            deletePort2Ports(hwBgChange);

            final List<HWBaugruppe> baugruppenNew = hwBgChange.getHwBgChangeCard().stream()
                    .flatMap(cc -> cc.getHwBaugruppenNew().stream())
                    .collect(Collectors.toList());

            if (CollectionTools.isEmpty(baugruppenNew)) {
                throw new FindException("Es wurden keine Ziel-Baugruppen gefunden.");
            }

            final List<HWBaugruppe> baugruppenNewSorted = sortBaugruppenByGerateBezAndModNr(baugruppenNew);

            List<HWBaugruppe> baugruppenOld = hwBgChange.getHWBaugruppen4ChangeCard();
            if (CollectionTools.isNotEmpty(baugruppenOld)) {
                final List<HWBaugruppe> baugruppenOldSorted = sortBaugruppenByGerateBezAndModNr(baugruppenOld);

                List<HWBaugruppenChangePort2Port> result = new ArrayList<>();
                for (HWBaugruppe baugruppe : baugruppenOldSorted) {
                    List<HWBaugruppenChangePort2Port> port2Port4Bg = createPort2Port4HwBaugruppe(baugruppe.getId());
                    result.addAll(port2Port4Bg);
                }
                hwBgChange.setPort2Port(new HashSet<>(result));

                // Ziel-Ports ermitteln u. mappen
                createPortMapping4ChangeCard(result, baugruppenNewSorted);

                saveHWBaugruppenChange(hwBgChange);
                return result;
            }
            else {
                throw new FindException("Baugruppen wurden nicht gefunden!");
            }
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Ermittlung der Ports: " + e.getMessage(), e);
        }
    }

    @Override
    @Nonnull
    @Transactional(
            value = "cc.hibernateTxManager",
            rollbackFor = de.augustakom.hurrican.service.base.exceptions.StoreException.class,
            noRollbackFor = de.augustakom.hurrican.service.base.exceptions.FindException.class,
            propagation = Propagation.REQUIRED
    )
    public Set<HWBaugruppenChangePort2Port> createPort2Port4PortConcentration(@Nonnull final HWBaugruppenChange hwBgChange)
            throws StoreException, FindException, DeleteException, ValidationException {

        final HWBaugruppenChangeCard hwBgChangeCard = validateInputForPortConcentration(hwBgChange);
        deletePort2Ports(hwBgChange); //analog zu einfacher Portschwenk

        final Pair<List<Equipment>, List<Equipment>> destPortsAndMatchingPorts = getDestPortsAndMatchingPorts(hwBgChangeCard);
        final List<Equipment> destPorts = destPortsAndMatchingPorts.getFirst(); // erste Rangierung, EQ_IN
        final List<Equipment> destMatchingAdslPorts = destPortsAndMatchingPorts.getSecond(); // zweite Rangierung, EQ_OUT

        final Set<HWBaugruppenChangePort2Port> result = getHwBaugruppenChangePort2Ports(hwBgChangeCard, destPorts, destMatchingAdslPorts);

        hwBgChange.setPort2Port(result);
        saveHWBaugruppenChange(hwBgChange);

        return ImmutableSet.copyOf(result);
    }

    private Set<HWBaugruppenChangePort2Port> getHwBaugruppenChangePort2Ports(HWBaugruppenChangeCard hwBgChangeCard,
            List<Equipment> destPorts, List<Equipment> destMatchingAdslPorts) throws FindException {
        final Set<HWBaugruppenChangePort2Port> result = Sets.newHashSet();
        final Iterator<Equipment> destPortIter = destPorts.iterator();
        final IstQuellRangierungFreiFuerPortkonzentrationImpl istRangierungFreiPredicate =
                new IstQuellRangierungFreiFuerPortkonzentrationImpl(auftragService);
        for (final HWBaugruppe src : sortBaugruppenByGerateBezAndModNr(hwBgChangeCard.getHwBaugruppenSource())) {
            //DSLAM-Ports haben immer eine BG-Id, DTAG-Stifte etc. ...
            final List<Equipment> equipmentsToSchwenk = rangierungsService.findEquipments4HWBaugruppe(src.getId());
            // Equipments nach HW_EQN sortieren
            Collections.sort(equipmentsToSchwenk, new HwEqnComparator());
            for (final Equipment equipmentToSchwenk : equipmentsToSchwenk) {
                if (!destPortIter.hasNext()) {
                    break;
                }
                //nur EQ-INs betrachten, WEPLA-gesperrte Ports ueberspringen
                if (!equipmentToSchwenk.isSecondDslPort() && !EqStatus.WEPLA.equals(equipmentToSchwenk.getStatus())) {
                    final Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipmentToSchwenk.getId());
                    if (!istRangierungFreiPredicate.apply(rangierung)) {
                        final HWBaugruppenChangePort2Port port2port = new HWBaugruppenChangePort2Port();
                        port2port.setEquipmentOld(equipmentToSchwenk);
                        port2port.setEquipmentOldIn(equipmentToSchwenk.getMatchingAdslPort(equipmentsToSchwenk));
                        port2port.setEqStateOrigOld(equipmentToSchwenk.getStatus());
                        port2port.setRangStateOrigOld(rangierung.getFreigegeben());
                        final Equipment nextDestPort = destPortIter.next();
                        port2port.setEquipmentNew(nextDestPort);
                        port2port.setEquipmentNewIn(nextDestPort.getMatchingAdslPort(destMatchingAdslPorts));
                        port2port.setEqStateOrigNew(nextDestPort.getStatus());
                        port2port.setRangStateOrigNew(Freigegeben.freigegeben);
                        result.add(port2port);
                    }
                }
            }
        }
        return result;
    }

    private HWBaugruppenChangeCard validateInputForPortConcentration(HWBaugruppenChange hwBgChange) throws StoreException {
        if ((hwBgChange == null) || !hwBgChange.isChangeType(ChangeType.PORT_CONCENTRATION)) {
            throw new StoreException("Die angegebene Planung ist fuer diese Art der Port-Ermittlung nicht geeignet!");
        }
        if (!hwBgChange.isPreparingAllowed()) {
            throw new StoreException("Im aktuellen Status darf das Port-Mapping nicht mehr ausgeführt werden!");
        }

        final HWBaugruppenChangeCard hwBgChangeCard = Iterables.getOnlyElement(hwBgChange.getHwBgChangeCard(), null);
        if (hwBgChangeCard == null || hwBgChangeCard.getHwBaugruppenSource().isEmpty() || hwBgChangeCard.getHwBaugruppenNew().isEmpty()) {
            throw new StoreException("Die Baugruppen für das Port-Mapping wurden nicht vollständig angegeben!");
        }
        return hwBgChangeCard;
    }


    /**
     * Diese Methode ermittelt alle Ports zu den neuen Baugruppen einer Portkonzentration. Aus diesem Pool filtert
     * die Methode nur die Ports aus, welche frei für den Schwenk sind. Rueckgabewert ist ein Paar mit den Listen
     * fuer first: ADSL-OUT Ports (erste Rangierung EQ_IN!), second: ADSL-IN Ports (zweite Rangierung EQ_OUT!).<br>
     * Filterkriterien:<br>
     * <ul>
     *   <li> Port (Equipment) steht NICHT auf Status WEPLA
     *   <li> Port hat KEINE Rangierung
     *   <li> EsId der Rangierung steht NICHT auf RANGIERUNG_NOT_ACTIVE (-1)
     *   <li> EsId der Rangierung ist NULL und Status der Rangierung steht NICHT auf WEPLA
     *   <li> Rangierung verweist auf eine Endstelle und Auftrag der Endstelle ist beendet (Absage, Storno, Gekuendigt)
     *   <li>
     * </ul>
     */
    private Pair<List<Equipment>, List<Equipment>> getDestPortsAndMatchingPorts(final HWBaugruppenChangeCard hwBgChangeCard) throws FindException {
        //auf unrangierte, nicht WEPLA - EQs filtern
        final List<Equipment> destPorts = Lists.newArrayList();
        final List<Equipment> destMatchingAdslPorts = Lists.newArrayList();
        for (final HWBaugruppe destBg : sortBaugruppenByGerateBezAndModNr(hwBgChangeCard.getHwBaugruppenNew())) {
            final List<Equipment> destPortsForBg = rangierungsService.findEquipments4HWBaugruppe(destBg.getId());

            Collections.sort(destPortsForBg, new HwEqnComparator());

            for (final Equipment destPortForBg : destPortsForBg) {
                if (EqStatus.WEPLA.equals(destPortForBg.getStatus())) {
                    continue;
                }
                final Rangierung rangierung = rangierungsService.findRangierung4Equipment(destPortForBg.getId());
                if (!new IstZielRangierungFreiFuerPortkonzentrationImpl(auftragService).apply(rangierung)) {
                    continue;
                }
                if (destPortForBg.isFirstDslPort()) {
                    destPorts.add(destPortForBg);
                }
                else {
                    destMatchingAdslPorts.add(destPortForBg);
                }
            }
        }
        return Pair.create(destPorts, destMatchingAdslPorts);
    }

    @Override
    public List<HWBaugruppe> sortBaugruppenByGerateBezAndModNr(final Iterable<HWBaugruppe> hwBaugruppen) {
        final Multimap<String, HWBaugruppe> bgsByRack = ArrayListMultimap.create();
        final SortedSet<String> geraeteBezs = new TreeSet<>();
        final List<HWBaugruppe> bgsSorted = Lists.newArrayList();
        for (final HWBaugruppe hwBaugruppe : hwBaugruppen) {
            final HWRack rack;
            try {
                rack = hwService.findRackForBaugruppe(hwBaugruppe.getId());
            }
            catch (FindException e) {
                throw new RuntimeException(e);
            }
            bgsByRack.put(rack.getGeraeteBez(), hwBaugruppe);
            geraeteBezs.add(rack.getGeraeteBez());
        }
        for (final String geraeteBez : geraeteBezs) {
            bgsSorted.addAll(Ordering.from(new HWBaugruppe.ModulNrComparator()).immutableSortedCopy(bgsByRack.get(geraeteBez)));
        }
        return bgsSorted;
    }

    @Override
    @Transactional(
            value = "cc.hibernateTxManager",
            rollbackFor = de.augustakom.hurrican.service.base.exceptions.StoreException.class,
            noRollbackFor = de.augustakom.hurrican.service.base.exceptions.FindException.class,
            propagation = Propagation.REQUIRED
    )
    public List<HWBaugruppenChangePort2Port> createPort2Port4ChangeBgType(HWBaugruppenChange hwBgChange) throws StoreException, FindException {
        if ((hwBgChange == null) || !hwBgChange.isChangeType(HWBaugruppenChange.ChangeType.CHANGE_BG_TYPE)) {
            throw new StoreException("Die angegebene Planung ist fuer diese Art der Port-Ermittlung nicht geeignet!");
        }

        if (!hwBgChange.isPreparingAllowed()) {
            throw new StoreException("Im aktuellen Status darf das Port-Mapping nicht mehr ausgeführt werden!");
        }

        try {
            deletePort2Ports(hwBgChange);

            List<HWBaugruppenChangePort2Port> port2Port4BgResult = new ArrayList<>();
            Set<HWBaugruppenChangeBgType> changeBgTypeSet = hwBgChange.getHwBgChangeBgType();
            for (HWBaugruppenChangeBgType changeBgType : changeBgTypeSet) {
                if (changeBgType.getHwBaugruppe() == null) {
                    throw new FindException("Es ist keine Baugruppe angegeben, deren Typ geaendert werden soll!");
                }
                else if (changeBgType.getHwBaugruppenTypNew() == null) {
                    throw new FindException("Der Ziel-Baugruppentyp ist nicht angegeben!");
                }

                List<HWBaugruppenChangePort2Port> port2Port4Bg = createPort2Port4HwBaugruppe(changeBgType.getHwBaugruppe().getId());
                port2Port4BgResult.addAll(port2Port4Bg);

                // Checks auf Port-Anzahl + BG-Typ
                if (CollectionTools.isEmpty(port2Port4Bg)) {
                    throw new FindException(
                            "Es wurden keine Ports fuer die angegebene Baugruppe gefunden!");
                }
                else if (NumberTools.isGreater(port2Port4Bg.size(), changeBgType.getHwBaugruppenTypNew().getPortCount())) {
                    throw new FindException(
                            "Die Anzahl der Ports der zu aendernden Baugruppe ist groesser, als die Port-Anzahl der neuen Baugruppe!");
                }

                hwBgChange.setPort2Port(new HashSet<>(port2Port4Bg));
                saveHWBaugruppenChange(hwBgChange);
            }
            return port2Port4BgResult;
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Ermittlung der Ports: " + e.getMessage(), e);
        }
    }


    /*
     * Ermittelt die Ports der angegebenen Baugruppe und erstellt {@link HWBaugruppenChangePort2Port} daraus.
     * @param hwBaugruppeId
     * @return Liste mit den generierten {@link HWBaugruppenChangePort2Port} Objekten.
     */
    List<HWBaugruppenChangePort2Port> createPort2Port4HwBaugruppe(Long hwBaugruppeId) throws StoreException {
        List<HWBaugruppenChangePort2Port> result = new ArrayList<>();
        try {
            List<Equipment> equipments = rangierungsService.findEquipments4HWBaugruppe(hwBaugruppeId);
            Collections.sort(equipments, new HwEqnComparator());

            if (CollectionTools.isNotEmpty(equipments)) {
                for (Equipment equipment : equipments) {
                    if (!equipment.isSecondDslPort()) {
                        HWBaugruppenChangePort2Port port2port = new HWBaugruppenChangePort2Port();
                        port2port.setEquipmentOld(equipment);
                        port2port.setEquipmentOldIn(equipment.getMatchingAdslPort(equipments));
                        port2port.setEqStateOrigOld(equipment.getStatus());

                        Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId());
                        if (rangierung != null) {
                            port2port.setRangStateOrigOld(rangierung.getFreigegeben());
                        }

                        result.add(port2port);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Ermittlung bzw. Generierung der Port-2-Port Mappings: " + e.getMessage(), e);
        }
        return result;
    }


    /*
     * Ermittelt die Ports der Ziel-Baugruppe und mappt diese auf die bereits ermittelten Ursprungs-Ports.
     * @param port2Ports Liste mit den bereits erstellten Port-2-Port Objekten, die die Ursprungs-Ports enthalten
     * @param hwBgDestination
     */
    void createPortMapping4ChangeCard(List<HWBaugruppenChangePort2Port> port2Ports, List<HWBaugruppe> hwBgDestination)
            throws FindException {
        try {
            final List<Equipment> destinationEquipments = new ArrayList<>();
            for (HWBaugruppe hwBaugruppe : hwBgDestination) {
                for (Equipment equipment : rangierungsService.findEquipments4HWBaugruppe(hwBaugruppe.getId())) {
                    destinationEquipments.add(equipment);
                }
            }

            Collections.sort(destinationEquipments, new HwEqnComparator());

            if (CollectionTools.isEmpty(destinationEquipments)) {
                throw new FindException("Fuer die Ziel-Baugruppe konnten keine Ports ermittelt werden!");
            }

            if (NumberTools.isGreater(port2Ports.size(), destinationEquipments.size())) {
                throw new FindException("Es sind mehr Ports abzuloesen, als die neue Baugruppe zur Verfuegung stellt!");
            }

            Map<Long, Equipment> usedEquipments = new java.util.HashMap<>();
            for (HWBaugruppenChangePort2Port port2port : port2Ports) {
                for (Equipment destinationEquipment : destinationEquipments) {
                    if (!usedEquipments.containsKey(destinationEquipment.getId())
                            && !destinationEquipment.isSecondDslPort()) {
                        port2port.setEquipmentNew(destinationEquipment);
                        port2port.setEquipmentNewIn(destinationEquipment.getMatchingAdslPort(destinationEquipments));
                        port2port.setEqStateOrigNew(destinationEquipment.getStatus());

                        Rangierung rangierung = rangierungsService.findRangierung4Equipment(destinationEquipment.getId());
                        if (rangierung != null) {
                            throw new FindException("Auf der Ziel-Baugruppe sind bereits Rangierungen vorhanden!");
                        }

                        // jeder Port darf nur einmal gemappt werden!
                        usedEquipments.put(destinationEquipment.getId(), destinationEquipment);

                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Fehler bei der Ermittlung der neuen Ports bzw. beim Port-Mapping: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePort2Ports(HWBaugruppenChange hwBgChange) throws DeleteException {
        try {
            if ((hwBgChange != null) && CollectionTools.isNotEmpty(hwBgChange.getPort2Port())) {
                for (HWBaugruppenChangePort2Port hwBaugruppenChangePort2Port : hwBgChange.getPort2Port()) {
                    hwBaugruppenChangeDAO.deletePort2Port(hwBaugruppenChangePort2Port);
                }

                hwBgChange.setPort2Port(null);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException("Port-Mappings konnten nicht geloescht werden: " + e.getMessage(), e);
        }
    }


    @Override
    public List<HWBaugruppenChangePort2Port> prepareHWBaugruppenChange(HWBaugruppenChange toPrepare) throws StoreException {
        if (toPrepare == null) { throw new StoreException("Keine Planung angegeben!"); }

        if (!toPrepare.isPreparingAllowed()) {
            throw new StoreException("Die Planung kann im aktuellen Status nicht vorbereitet werden!");
        }

        try {
            if (toPrepare.isChangeType(ChangeType.DLU_CHANGE)) {
                Set<HWBaugruppenChangeDlu> dluChangeSet = toPrepare.getHwBgChangeDlu();
                if ((dluChangeSet == null) || (dluChangeSet.size() != 1)) {
                    throw new StoreException("Anzahl der definierten DLU-Wechsel ist ungueltig!");
                }
                HWBaugruppenChangeDlu hwBgChangeDlu = dluChangeSet.iterator().next();
                if (!hwBgChangeDlu.isValid(findDluV5Mappings(hwBgChangeDlu.getId()))) {
                    throw new StoreException("Angegebene Daten fuer DLU-Schwenk sind ungueltig!");
                }
            }
            else if (CollectionTools.isNotEmpty(toPrepare.getPort2Port())) {
                Set<HWBaugruppenChangePort2Port> portMappingsWithInvalidCCs = checkCrossConnectionDefinitions(toPrepare.getPort2Port());
                if (CollectionTools.isNotEmpty(portMappingsWithInvalidCCs)) {
                    // ungueltige CrossConnections - Abbruch!
                    return new ArrayList<>(portMappingsWithInvalidCCs);
                }
                else {
                    for (HWBaugruppenChangePort2Port port2Port : toPrepare.getPort2Port()) {
                        changeEquipmentStateToWepla(port2Port.getEquipmentOld());
                        changeEquipmentStateToWepla(port2Port.getEquipmentOldIn());
                        changeEquipmentStateToWepla(port2Port.getEquipmentNew());
                        changeEquipmentStateToWepla(port2Port.getEquipmentNewIn());
                    }
                }
            }

            // Status der Planung auf <PREPARED> umsetzen
            toPrepare.setChangeState(getReferenceByChangeState(HWBaugruppenChange.ChangeState.CHANGE_STATE_PREPARED));
            saveHWBaugruppenChange(toPrepare);

            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Vorbereitung der Planung: " + e.getMessage(), e);
        }
    }


    /**
     * Aendert den Status des angegebenen Equipments (und der verbundenen Rangierung) auf <WEPLA>.
     *
     * @param equipment
     * @throws StoreException
     * @throws FindException
     */
    void changeEquipmentStateToWepla(Equipment equipment) throws StoreException, FindException {
        if (equipment != null) {
            equipment.setStatus(EqStatus.WEPLA);
            rangierungsService.saveEquipment(equipment);

            Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId());
            if (rangierung != null) {
                rangierung.setFreigegeben(Freigegeben.WEPLA);
                rangierungsService.saveRangierung(rangierung, false);
            }
        }
    }

    @Override
    public void isExecutionAllowed(HWBaugruppenChange toExecute) throws StoreException {
        if (!toExecute.isExecuteAllowed()) {
            // execute nur dann, wenn Status <PREPARED>
            throw new StoreException("Eine Ausfuehrung der Planung ist im aktuellen Status nicht erlaubt!");
        }
    }


    @Override
    public AKWarnings executeHWBaugruppenChange(HWBaugruppenChange toExecute,
            Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping, Long sessionId) throws StoreException {
        if (toExecute == null) { throw new StoreException("Keine Planung zur Ausfuehrung angegeben!"); }
        isExecutionAllowed(toExecute);

        try {
            HWBaugruppenChangeExecuter executer;
            if (toExecute.isChangeType(HWBaugruppenChange.ChangeType.CHANGE_CARD)
                    || toExecute.isChangeType(HWBaugruppenChange.ChangeType.MERGE_CARDS)) {
                executer = new HWBaugruppenChangeCardExecuter();
                ((HWBaugruppenChangeCardExecuter) executer).configure(
                        toExecute,
                        hwService,
                        this,
                        rangierungsService,
                        eqCrossConnectionService,
                        produktService,
                        dslamService,
                        physikService,
                        dslamProfileMapping,
                        sessionId,
                        getLoginNameSilent(sessionId));
            }
            else if (toExecute.isChangeType(HWBaugruppenChange.ChangeType.CHANGE_BG_TYPE)) {
                executer = new HWBaugruppenChangeBgTypeExecuter();
                ((HWBaugruppenChangeBgTypeExecuter) executer).configure(
                        toExecute,
                        hwService,
                        this,
                        rangierungsService,
                        physikService);
            }
            else if (toExecute.isChangeType(HWBaugruppenChange.ChangeType.DLU_CHANGE)) {
                executer = new HWBaugruppenChangeDluExecuter();
                ((HWBaugruppenChangeDluExecuter) executer).configure(
                        toExecute,
                        this,
                        hwService,
                        rangierungsService);
            }
            else if (toExecute.isChangeType(ChangeType.PORT_CONCENTRATION)) {
                final HWBaugruppenChangePortConcentrationExecuter pcExecuter =
                        new HWBaugruppenChangePortConcentrationExecuter();
                pcExecuter.configure(
                        toExecute,
                        hwService,
                        this,
                        rangierungsService,
                        eqCrossConnectionService,
                        produktService,
                        dslamService,
                        physikService,
                        dslamProfileMapping,
                        sessionId,
                        getLoginNameSilent(sessionId),
                        new IstQuellRangierungFreiFuerPortkonzentrationImpl(auftragService));
                executer = pcExecuter;
            }
            else {
                // TODO weitere Aenderungstypen aufnehmen!
                throw new StoreException("Execution of this change type currently not supported!");
            }

            executer.execute();

            AKWarnings warnings = (executer instanceof IWarningAware) ? ((IWarningAware) executer).getWarnings() : null;

            toExecute.setExecutedFrom(getUserNameAndFirstNameSilent(sessionId));
            toExecute.setExecutedAt(new Date());
            toExecute.setChangeState(getReferenceByChangeState(HWBaugruppenChange.ChangeState.CHANGE_STATE_EXECUTED));
            saveHWBaugruppenChange(toExecute);

            return warnings;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Ausfuehrung des Baugruppen-Schwenks: " + e.getMessage(), e);
        }
    }


    @Override
    public void cancelHWBaugruppenChange(HWBaugruppenChange toCancel, Long sessionId) throws StoreException {
        if (toCancel == null) { throw new StoreException("Keine Planung angegeben!"); }
        if (!toCancel.isCancelAllowed()) {
            throw new StoreException("Die Planung kann im aktuellen Status nicht storniert werden!");
        }

        try {
            CancelHWBaugruppenChangeExecuter executer = new CancelHWBaugruppenChangeExecuter();
            executer.configure(toCancel, rangierungsService);
            executer.execute();

            toCancel.setCancelledAt(new Date());
            toCancel.setCancelledFrom(getUserNameAndFirstNameSilent(sessionId));
            toCancel.setChangeState(getReferenceByChangeState(HWBaugruppenChange.ChangeState.CHANGE_STATE_CANCELLED));
            saveHWBaugruppenChange(toCancel);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Stornieren der Planung: " + e.getMessage(), e);
        }
    }


    @Override
    public void closeHWBaugruppenChange(HWBaugruppenChange toClose, Long sessionId) throws StoreException {
        if (toClose == null) { throw new StoreException("Keine Planung angegeben!"); }
        if (!toClose.isCloseAllowed()) {
            throw new StoreException("Die Planung kann im aktuellen Status nicht geschlossen werden!");
        }

        try {
            toClose.setClosedAt(new Date());
            toClose.setClosedFrom(getUserNameAndFirstNameSilent(sessionId));
            toClose.setChangeState(getReferenceByChangeState(HWBaugruppenChange.ChangeState.CHANGE_STATE_CLOSED));
            saveHWBaugruppenChange(toClose);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Schliessen der Planung: " + e.getMessage(), e);
        }
    }


    @Override
    public Set<HWBaugruppenChangePort2Port> checkCrossConnectionDefinitions(Set<HWBaugruppenChangePort2Port> portMappings) throws FindException {
        try {
            Set<HWBaugruppenChangePort2Port> invalidCrossConnections = new HashSet<>();
            if (CollectionTools.isNotEmpty(portMappings)) {
                for (HWBaugruppenChangePort2Port port2port : portMappings) {
                    if ((port2port.getEquipmentOld() != null)
                            && (port2port.getEquipmentNew() != null)
                            && BooleanTools.nullToFalse(port2port.getEquipmentOld().getManualConfiguration())
                            && !BooleanTools.nullToFalse(port2port.getEquipmentNew().getManualConfiguration())) {
                        invalidCrossConnections.add(port2port);
                    }
                }
            }

            return invalidCrossConnections;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Bei der Pruefung der CrossConnections ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }


    @Override
    public List<HWBaugruppenChange> findOpenHWBaugruppenChanges() throws FindException {
        try {
            return hwBaugruppenChangeDAO.findOpenHWBaugruppenChanges();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public List<HWBaugruppenChangePort2PortView> findPort2PortViews(HWBaugruppenChange hwBgChange) throws FindException {
        try {
            HWBaugruppenChangePort2PortView example = new HWBaugruppenChangePort2PortView();
            example.setHwBgChangeId(hwBgChange.getId());

            List<HWBaugruppenChangePort2PortView> result = hwBaugruppenChangeDAO.queryByExample(
                    example, HWBaugruppenChangePort2PortView.class);

            // Result Liste nach HW_EQN_OLD sortieren
            Collections.sort(result, new HwEqnComparator());

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public List<HWBaugruppenChangePort2PortDetailView> findPort2PortDetailViews(HWBaugruppenChange hwBgChange) throws FindException {
        try {
            HWBaugruppenChangePort2PortDetailView example = new HWBaugruppenChangePort2PortDetailView();
            example.setHwBgChangeId(hwBgChange.getId());

            List<HWBaugruppenChangePort2PortDetailView> result = hwBaugruppenChangeDAO.queryByExample(
                    example, HWBaugruppenChangePort2PortDetailView.class);

            // Result Liste nach HW_EQN_OLD sortieren
            Collections.sort(result, new HwEqnComparator());

            return result;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public HWBaugruppenChangePort2Port findPort2Port(Long port2PortId) throws FindException {
        try {
            return hwBaugruppenChangeDAO.findById(port2PortId, HWBaugruppenChangePort2Port.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public String createCpsTransactions(HWBaugruppenChange hwBgChange, boolean cpsInit, Long sessionId)
            throws StoreException {
        try {
            HWBaugruppenChangeCpsExecuter executer = new HWBaugruppenChangeCpsExecuter();
            executer.configure(hwBgChange, cpsInit, this, cpsService, auftragService, sessionId);
            executer.execute();

            StringBuilder warningsAndErrors = executer.getCpsWarningsAndErrors();
            return (warningsAndErrors != null) ? warningsAndErrors.toString() : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Erstellung der CPS-Transactions: " + e.getMessage(), e);
        }
    }


    @Override
    public String createCPSReInitTransactions(HWBaugruppenChange hwBgChange, Long sessionId) throws StoreException {
        try {
            if (!hwBgChange.isChangeType(HWBaugruppenChange.ChangeType.DLU_CHANGE)) {
                throw new StoreException("Fuer den angegebenen Typ ist kein CPS-ReInit vorgesehen!");
            }

            if ((hwBgChange.getHwBgChangeDlu() == null) || (hwBgChange.getHwBgChangeDlu().size() != 1)) {
                throw new StoreException("Anzahl der definierten DLU-Wechsel ist ungueltig!");
            }

            HWBaugruppenChangeDlu hwBgChangeDlu = hwBgChange.getHwBgChangeDlu().iterator().next();
            Reference hwBgChangeState = hwBgChange.getChangeState();

            HWBaugruppenChangeCpsReInitExecuter executer = new HWBaugruppenChangeCpsReInitExecuter();
            executer.configure(hwBgChangeDlu, rangierungsService, auftragService,
                    hwBgChangeState, cpsService, hwService, sessionId);
            executer.execute();

            return executer.getWarnings();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Erstellung der CPS-ReInit-Transaktionen: " + e.getMessage(), e);
        }
    }

    @Override
    public List<HWBaugruppenChangeDluV5> findDluV5Mappings(Long hwBgChangeDluId) throws FindException {
        if (hwBgChangeDluId == null) { return null; }
        try {
            HWBaugruppenChangeDluV5 example = new HWBaugruppenChangeDluV5();
            example.setHwBgChangeDluId(hwBgChangeDluId);

            return hwBaugruppenChangeDAO.queryByExample(example, HWBaugruppenChangeDluV5.class,
                    new String[] { HWBaugruppenChangeDluV5.ID }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public void importDluV5Mappings(HWBaugruppenChange hwBgChange, InputStream fileToImport, Long sessionId)
            throws StoreException {
        try {
            if (!hwBgChange.isChangeType(HWBaugruppenChange.ChangeType.DLU_CHANGE)) {
                throw new StoreException("Fuer den angegebenen Typ ist kein V5-Mapping vorgesehen!");
            }

            if (!hwBgChange.isPreparingAllowed()) {
                throw new StoreException("Der Import darf im aktuellen Status nicht mehr durchgefuehrt werden!");
            }

            if ((hwBgChange.getHwBgChangeDlu() == null) || (hwBgChange.getHwBgChangeDlu().size() != 1)) {
                throw new StoreException("Anzahl der definierten DLU-Wechsel ist ungueltig!");
            }

            HWBaugruppenChangeDlu hwBgChangeDlu = hwBgChange.getHwBgChangeDlu().iterator().next();

            // get a new version of this service because of transaction behavior!
            // (Tx definitions with REQUIRES_NEW aren`t working, if 'this' is passed to the executer!)
            HWBaugruppenChangeService hwBgChangeService = getCCService(HWBaugruppenChangeService.class);

            HWBaugruppenChangeImportDluV5Executer executer = new HWBaugruppenChangeImportDluV5Executer();
            executer.configure(hwBgChangeDlu, hwBgChangeService, hwService, rangierungsService, fileToImport, getAKUserBySessionIdSilent(sessionId));
            executer.execute();
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler beim Import der V5-Mappings: " + e.getMessage(), e);
        }
    }


    @Override
    public void saveHWBaugruppenChangeDluV5(HWBaugruppenChangeDluV5 toSave) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            hwBaugruppenChangeDAO.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    @CcTxRequiresNew
    public void deleteDluV5MappingsNewTx(Long hwBgChangeDluId) throws DeleteException {
        deleteDluV5Mappings(hwBgChangeDluId);
    }


    @Override
    public void deleteDluV5MappingsInTx(Long hwBgChangeDluId) throws DeleteException {
        deleteDluV5Mappings(hwBgChangeDluId);
    }


    private void deleteDluV5Mappings(Long hwBgChangeDluId) throws DeleteException {
        try {
            hwBaugruppenChangeDAO.deleteHWBaugruppenChangeDluV5(hwBgChangeDluId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new DeleteException(DeleteException._UNEXPECTED_ERROR, e);
        }
    }


    public void checkAndAddHwBaugruppe4Source(HWBaugruppenChangeCard hwBgChangeCard, HWBaugruppe hwBaugruppe)
            throws StoreException {
        if (hwBgChangeCard.getHwBaugruppenNew().contains(hwBaugruppe)) {
            throw new StoreException(String.format("Die ausgewählte Baugruppe ist bereits als Ziel-Baugruppe konfiguriert. "
                    + "Eine Baugruppe darf nie gleichzeitig als Quell- und Ziel-Baugruppe ausgewählt sein!"));
        }
        if (hwBgChangeCard.getHwBaugruppenSource().contains(hwBaugruppe)) {
            throw new StoreException(String.format("Die ausgewählte Baugruppe ist bereits als Quell-Baugruppe konfiguriert!"));
        }
        if (!hwBgChangeCard.addHwBaugruppeSource(hwBaugruppe)) {
            throw new StoreException(String.format("Das Einfügen der Baugruppe als Quell-Baugruppe ist fehlgeschlagen!"));
        }
    }

    public void checkAndAddHwBaugruppe4New(HWBaugruppenChangeCard hwBgChangeCard, HWBaugruppe hwBaugruppe)
            throws StoreException {
        if (hwBgChangeCard.getHwBaugruppenSource().contains(hwBaugruppe)) {
            throw new StoreException(String.format("Die ausgewählte Baugruppe ist bereits als Quell-Baugruppe konfiguriert. "
                    + "Eine Baugruppe darf nie gleichzeitig als Quell- und Ziel-Baugruppe ausgewählt sein!"));
        }
        if (hwBgChangeCard.getHwBaugruppenNew().contains(hwBaugruppe)) {
            throw new StoreException(String.format("Die ausgewählte Baugruppe ist bereits als Ziel-Baugruppe konfiguriert!"));
        }
        if (!hwBgChangeCard.addHwBaugruppeNew(hwBaugruppe)) {
            throw new StoreException(String.format("Das Einfügen der Baugruppe als Ziel-Baugruppe ist fehlgeschlagen!"));
        }
    }


    /**
     * Injected by Spring.
     */
    public void setHwBaugruppenChangeDAO(HWBaugruppenChangeDAO hwBaugruppenChangeDAO) {
        this.hwBaugruppenChangeDAO = hwBaugruppenChangeDAO;
    }

    /**
     * Injected by Spring.
     */
    public void setHwBaugruppenChangeValidator(HWBaugruppenChangeValidator hwBaugruppenChangeValidator) {
        this.hwBaugruppenChangeValidator = hwBaugruppenChangeValidator;
    }

    /**
     * Injected by Spring.
     */
    public void setReferenceService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    /**
     * Injected by Spring.
     */
    public void setRangierungsService(RangierungsService rangierungsService) {
        this.rangierungsService = rangierungsService;
    }

    /**
     * Injected by Spring.
     */
    public void setHwService(HWService hwService) {
        this.hwService = hwService;
    }

    /**
     * Injected by Spring.
     */
    public void setEqCrossConnectionService(EQCrossConnectionService eqCrossConnectionService) {
        this.eqCrossConnectionService = eqCrossConnectionService;
    }

    /**
     * Injected by Spring.
     */
    public void setProduktService(ProduktService produktService) {
        this.produktService = produktService;
    }

    /**
     * Injected by Spring.
     */
    public void setAuftragService(CCAuftragService auftragService) {
        this.auftragService = auftragService;
    }

    /**
     * Injected by Spring.
     */
    public void setCpsService(CPSService cpsService) {
        this.cpsService = cpsService;
    }

}
