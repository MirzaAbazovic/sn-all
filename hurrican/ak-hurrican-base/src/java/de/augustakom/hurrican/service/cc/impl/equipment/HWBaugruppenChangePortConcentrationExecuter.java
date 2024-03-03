/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014 14:21
 */
package de.augustakom.hurrican.service.cc.impl.equipment;

import java.util.*;
import javax.annotation.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.messages.IWarningAware;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;

/**
 *
 */
public class HWBaugruppenChangePortConcentrationExecuter implements HWBaugruppenChangeExecuter, IWarningAware {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangePortConcentrationExecuter.class);

    /**
     *
     */
    static final class AllPortsAreAbgebautPredicate implements Predicate<HWBaugruppe> {

        AllPortsAreAbgebautPredicate(final RangierungsService rangierungsService) {
            this.rangierungsService = rangierungsService;
        }

        final RangierungsService rangierungsService;

        @Override
        public boolean apply(@Nullable HWBaugruppe input) {
            boolean result = true;
            if (input == null) {
                result = false;
            }
            else {
                try {
                    for (final Equipment equipment : rangierungsService.findEquipments4HWBaugruppe(input.getId())) {
                        if (!equipment.getStatus().equals(EqStatus.abgebaut)) {
                            result = false;
                            break;
                        }
                    }
                }
                catch (FindException e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        }
    }

    AKWarnings warnings;
    HwBaugruppenChangeCardHelper helper;

    HWService hwService;
    HWBaugruppenChange toExecute;
    RangierungsService rangierungsService;
    DSLAMService dslamService;
    Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping;

    AllPortsAreAbgebautPredicate allPortsAreAbgebautPredicate;
    IstRangierungFreiFuerPortkonzentration rangierungIstFrei;

    Map<Long, Rangierung> aktualisierteRangierungen = Maps.newHashMap();

    /**
     * Uebergibt dem Executer die notwendigen Modelle u. Services.
     *
     * @param toExecute
     * @param hwService
     * @param hwBaugruppenChangeService
     * @param rangierungsService
     * @param eqCrossConnectionService
     * @param produktService
     * @param dslamService
     * @param dslamProfileMapping
     * @param sessionId
     * @param userW
     * @param rangierungIstFrei
     */
    public void configure(HWBaugruppenChange toExecute,
            HWService hwService,
            HWBaugruppenChangeService hwBaugruppenChangeService,
            RangierungsService rangierungsService,
            EQCrossConnectionService eqCrossConnectionService,
            ProduktService produktService,
            DSLAMService dslamService,
            PhysikService physikService,
            Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping,
            Long sessionId,
            String userW,
            IstRangierungFreiFuerPortkonzentration rangierungIstFrei) {
        this.warnings = new AKWarnings();
        this.hwService = hwService;
        this.toExecute = toExecute;
        this.rangierungsService = rangierungsService;
        this.dslamService = dslamService;
        this.dslamProfileMapping = dslamProfileMapping;

        this.helper = new HwBaugruppenChangeCardHelper(eqCrossConnectionService, hwService, hwBaugruppenChangeService,
                produktService, rangierungsService, physikService, toExecute, sessionId, dslamService, userW);
        this.allPortsAreAbgebautPredicate = new AllPortsAreAbgebautPredicate(rangierungsService);
        this.rangierungIstFrei = rangierungIstFrei;
    }

    @Override
    public AKWarnings getWarnings() {
        return warnings;
    }

    /**
     * Fuehrt den Baugruppen-Schwenk durch
     *
     * @throws StoreException
     */
    @Override
    public void execute() throws StoreException {
        try {
            Date executionDate = new Date();
            helper.checkData();
            helper.loadPortMappingViews();
            unravelRangierungenFromDestBaugruppe();
            aktualisierteRangierungen = helper.movePorts();
            helper.declarePortsAsRemoved();
            helper.declareNewPortsAndCardAsBuiltIn();
            helper.calculateCrossConnections(executionDate);
            warnings.addAKWarnings(helper.changeDslamProfiles(executionDate, dslamProfileMapping, aktualisierteRangierungen));
            unravelUnmappedSourceRangierungen(); // "freie" Rangierungen auftrennen
            //Erst am Schluss ausfuehren, wenn "freie" Rangierungen aufgetrennt wurden
            helper.declareCardsAsRemoved(this.allPortsAreAbgebautPredicate);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException("Fehler bei der Ausfuehrung des Baugruppen-Schwenks: " + e.getMessage(), e);
        }
    }

    /**
     * 'Trennt' alle NICHT im Portmapping beruecksichtigten Rangierungen aus den Quellbaugruppen auf. Voraussetzung ist
     * allerdings, dass die Rangierung 'frei' ist. Dazu wird der DLU-Port aus der Rangierung entfernt und der Status auf
     * 'frei' gesetzt. Der DSLAM-Port und die Rangierung selbst werden beendet.
     */
    void unravelUnmappedSourceRangierungen() throws FindException, StoreException {
        final Map<HWBaugruppe, List<Equipment>> toBreak = findSourcePortsToUnravel();
        for (HWBaugruppe hwBaugruppe : toBreak.keySet()) {
            unravelRangierungen4SrcBaugruppe(hwBaugruppe, toBreak.get(hwBaugruppe));
        }
    }

    /**
     * 'Trennt' alle im Portmapping beruecksichtigten Rangierungen aus den Zielbaugruppen auf. Dazu wird der DLU-Port
     * aus der Rangierung entfernt und der Status auf 'frei' gesetzt. Die Rangierung wird historisiert und beendet.
     */
    void unravelRangierungenFromDestBaugruppe() throws FindException, StoreException {
        final List<Equipment> toBreak = findDestPortsToUnravel();
        for (final Equipment equipment : toBreak) {
            final Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId());
            if (rangierung != null) {
                final Rangierung unraveledRangierung = unravelRangierung(equipment, rangierung);
                unraveledRangierung.setGueltigBis(new Date());
                rangierungsService.saveRangierung(unraveledRangierung, false);
            }
        }
    }

    void unravelRangierungen4SrcBaugruppe(HWBaugruppe hwBaugruppe, List<Equipment> equipments)
            throws FindException, StoreException {
        boolean bgInUse = false;
        for (Equipment equipment : equipments) {
            final Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipment.getId());
            if (rangierung != null) {
                if (!rangierungIstFrei.apply(rangierung)) {
                    bgInUse = true;
                    warnings.addAKWarning(this,
                            String.format("Die Rangierung des Ports %s auf der Baugruppe %s - %s konnte" +
                                            " nicht aufgebrochen werden!", equipment.getHwEQN(),
                                    hwBaugruppe.getHwBaugruppenTyp().getName(),
                                    hwBaugruppe.getModNumber()));
                }
                else {
                    final Rangierung unraveledRangierung = unravelRangierung(equipment, rangierung);
                    rangierungsService.saveRangierung(unraveledRangierung, false);
                    helper.modifyEquipmentState(equipment, EqStatus.abgebaut);
                }
            }
        }
        if (bgInUse) {
            warnings.addAKWarning(this, String.format("Die Ports der Baugruppe %s - %s konnten nicht vollstaendig " +
                    "abgebaut werden!", hwBaugruppe.getHwBaugruppenTyp().getName(), hwBaugruppe.getModNumber()));
        }
    }

    private Rangierung unravelRangierung(final Equipment equipment, final Rangierung rangierung) throws FindException, StoreException {
        if (rangierung.getEqInId() != null && rangierung.getEqInId().equals(equipment.getId())) {
            Equipment eqUevt = rangierungsService.findEquipment(rangierung.getEqOutId());
            if (eqUevt != null) {
                helper.modifyEquipmentState(eqUevt, EqStatus.frei);
            }
        }
        else if (rangierung.getEqOutId() != null && rangierung.getEqOutId().equals(equipment.getId())) {
            Equipment eqDlu = rangierungsService.findEquipment(rangierung.getEqInId());
            if (eqDlu != null) {
                helper.modifyEquipmentState(eqDlu, EqStatus.frei);
            }
        }
        rangierung.setFreigegeben(Rangierung.Freigegeben.deactivated);
        return rangierung;
    }

    Map<HWBaugruppe, List<Equipment>> findSourcePortsToUnravel() throws FindException {
        final Map<HWBaugruppe, List<Equipment>> toBreak = new HashMap<>();
        for (HWBaugruppe hwBaugruppe : toExecute.getHWBaugruppen4ChangeCard()) {
            final List<Equipment> equipmentsToBreak = new ArrayList<>();
            List<Equipment> equipments = rangierungsService.findEquipments4HWBaugruppe(hwBaugruppe.getId());
            for (Equipment equipment : equipments) {
                boolean unravelThis = true;
                for (HWBaugruppenChangePort2Port port2Port : toExecute.getPort2Port()) {
                    if (equipment.getId().equals(port2Port.getEquipmentOld().getId())
                            || equipment.getId().equals(port2Port.getEquipmentOldIn().getId())) {
                        unravelThis = false;
                    }
                }
                if (unravelThis) {
                    equipmentsToBreak.add(equipment);
                }
            }
            toBreak.put(hwBaugruppe, equipmentsToBreak);
        }
        return toBreak;
    }

    private List<Equipment> findDestPortsToUnravel() {
        final List<Equipment> equipmentsToBreak = Lists.newArrayListWithCapacity(toExecute.getPort2Port().size());
        for (HWBaugruppenChangePort2Port port2Port : toExecute.getPort2Port()) {
            equipmentsToBreak.add(port2Port.getEquipmentNew());
            if (port2Port.getEquipmentNewIn() != null) {
                equipmentsToBreak.add(port2Port.getEquipmentNewIn());
            }
        }
        return equipmentsToBreak;
    }

}
