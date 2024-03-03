/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.02.14
 */
package de.augustakom.hurrican.service.cc.impl;

import static java.lang.String.*;
import static java.util.Collections.*;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Function2;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.EquipmentDAO;
import de.augustakom.hurrican.dao.cc.RangierungDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.SdslNdraht;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.SdslEquipmentService;
import de.augustakom.hurrican.tools.comparator.HwEqnComparator;

@CcTxRequired
public class SdslEquipmentServiceImpl extends DefaultCCService implements SdslEquipmentService {

    @Resource(name = "equipmentDAO")
    private EquipmentDAO equipmentDao;

    @Resource(name = "rangierungDAO")
    private RangierungDAO rangierungDao;

    @Resource(name = "de.augustakom.hurrican.service.cc.HWService")
    private HWService hwService;

    @Resource(name = "de.augustakom.hurrican.service.cc.HVTService")
    private HVTService hvtService;

    @Resource(name = "de.augustakom.hurrican.service.cc.RangierungsService")
    private RangierungsService rangierungsService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService ccAuftragService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ProduktService")
    private ProduktService produktService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndstellenService")
    private EndstellenService endstellenService;

    @Resource(name = "de.augustakom.hurrican.service.cc.EndgeraeteService")
    private EndgeraeteService endgeraeteService;

    @Resource(name = "de.augustakom.hurrican.service.cc.PhysikService")
    private PhysikService physikService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCLeistungsService")
    private CCLeistungsService leistungsService;

    @Override
    @Nonnull
    public List<Equipment> findFreeEquipmentsByStandortSorted(Long hvtStandortId, List<Long> physikTypIds)
            throws FindException {
        final List<Equipment> result = Lists.newArrayList();

        final List<HWDslam> dslams = hwService.findRacks(hvtStandortId, HWDslam.class, true);
        Collections.sort(dslams, new DslamGeraeteBezComparator());

        for (final HWDslam dslam : dslams) {
            final List<Equipment> equipmentsInDslam = Lists.newArrayList();
            for (final HWBaugruppe baugruppe : hwService.findBaugruppen4Rack(dslam.getId())) {
                equipmentsInDslam.addAll(findFreeEquipmentsByBaugruppe(baugruppe.getId(), physikTypIds));
            }
            Collections.sort(equipmentsInDslam, new HwEqnComparator());
            result.addAll(equipmentsInDslam);
        }
        return result;
    }

    @Override
    @Nonnull
    public Map<Schicht2Protokoll, List<Equipment>> groupBySchicht2Protokoll(List<Equipment> equipments) {
        final Map<Schicht2Protokoll, List<Equipment>> result = Maps.newHashMap();

        for (final Equipment equipment : equipments) {
            final Schicht2Protokoll protokoll = equipment.getSchicht2ProtokollOrAtm();
            if (result.containsKey(protokoll)) {
                result.get(protokoll).add(equipment);
            }
            else {
                result.put(protokoll, Lists.newArrayList(equipment));
            }
        }
        return result;
    }

    @Nonnull
    private List<Equipment> findFreeEquipmentsByBaugruppe(Long hwBaugruppenId, List<Long> physikTypIds)
            throws FindException {
        final List<Equipment> result = Lists.newArrayList();
        final List<Equipment> equipments = equipmentDao.findEquipmentsByBaugruppe(hwBaugruppenId);
        for (final Equipment equipment : equipments) {
            final Rangierung rangierung = findRangierung4Equipment(equipment.getId());
            if (rangierung != null && physikTypIds.contains(rangierung.getPhysikTypId())
                    && rangierung.isRangierungFrei(false)) {
                result.add(equipment);
            }
        }
        return result;
    }

    private Rangierung findRangierung4Equipment(final long eqInId) {
        final Rangierung example = new Rangierung();
        example.setEqInId(eqInId);
        example.setGueltigBis(DateTools.getHurricanEndDate());
        final List<Rangierung> results = rangierungDao.queryByExample(example, Rangierung.class);
        return (results.isEmpty()) ? null : results.get(0);
    }

    @Override
    @Nonnull
    public AKWarnings assignSdslNdraht(long esId, @Nonnull final Date today) throws ValidationException {
        try {
            final AKWarnings warningsFound = new AKWarnings();

            final Endstelle es = this.endstellenService.findEndstelle(esId);
            hvtService.validateKvzSperre(es);

            final AuftragDaten ad = this.ccAuftragService.findAuftragDatenByEndstelleTx(esId);
            final Produkt produkt = this.produktService.findProdukt(ad.getProdId());

            // SDSL n-Draht TAL
            final Pair<AKWarnings, Collection<AuftragDaten>> resultFromAnzNdrahtCheck =
                    checkAnzahlNdrahtAuftraege(ad.getAuftragId());
            final AKWarnings anzAuftraegeWarnings = resultFromAnzNdrahtCheck.getFirst();

            if (anzAuftraegeWarnings.isEmpty()) {
                final AKWarnings auftraegeRangiertWarnings =
                        checkAuftraegeNichtRangiert(resultFromAnzNdrahtCheck.getSecond(), today);
                if (auftraegeRangiertWarnings.isEmpty()) {
                    final int portsNeeded = resultFromAnzNdrahtCheck.getSecond().size();
                    List<Equipment> equipments = findFreeEquipmentsByStandortSorted(
                            es.getHvtIdStandort(), physikService.findPhysikTypen4Produkt(ad.getProdId()));

                    List<Equipment> block4Assignment = null;
                    if (SdslNdraht.OPTIONAL_BONDING.equals(produkt.getSdslNdraht())) {
                        // SDSL n-Draht 'bonding' Option --> Ports von gleicher Baugruppe, die als 'bonding-faehig'
                        // markiert ist ermitteln (Ports muessen nicht zusammen haengend sein!)
                        final List<List<Equipment>> bondingPorts = groupEquipmentsByBondingCapableBaugruppe(equipments);
                        block4Assignment = findPorts4Assignment(bondingPorts, null, portsNeeded, ad.getAuftragId(), today);
                    }
                    else {
                        // 'normale' SDSLer: Ports muessen zusammen haengend sein!
                        final Map<Schicht2Protokoll, List<Equipment>> equipmentsByL2Protocol =
                                groupBySchicht2Protokoll(equipments);

                        final List<List<Equipment>> efmBloecke = (equipmentsByL2Protocol.get(Schicht2Protokoll.EFM) == null)
                                ? Collections.<List<Equipment>>emptyList()
                                : groupEquipmentsByCorresponding4erBloecke(
                                equipmentsByL2Protocol.get(Schicht2Protokoll.EFM));
                        final List<List<Equipment>> atmBloecke = (equipmentsByL2Protocol.get(Schicht2Protokoll.ATM) == null)
                                ? Collections.<List<Equipment>>emptyList()
                                : groupEquipmentsByCorresponding4erBloecke(
                                equipmentsByL2Protocol.get(Schicht2Protokoll.ATM));
                        block4Assignment =
                                findPorts4Assignment(efmBloecke, atmBloecke, portsNeeded, ad.getAuftragId(), today);
                    }

                    if (block4Assignment.isEmpty()) {
                        throw new RuntimeException(
                                String.format("Es ist kein Block mit genügend freien Ports (%s Stück) vorhanden",
                                        portsNeeded)
                        );
                    }

                    assignRangierungAndEndstelle(resultFromAnzNdrahtCheck.getSecond(), block4Assignment);
                }
                else {
                    warningsFound.addAKWarnings(auftraegeRangiertWarnings);
                }
            }
            else {
                warningsFound.addAKWarnings(anzAuftraegeWarnings);
            }

            return warningsFound;
        } catch (ValidationException v) {
            throw v;
        } catch (FindException | StoreException e) {
            throw new RuntimeException(e);
        }
    }

    private void assignRangierungAndEndstelle(Collection<AuftragDaten> auftragDatenColl, List<Equipment> equipmentList)
            throws FindException, StoreException {
        final Iterator<Equipment> equipmentIt = equipmentList.iterator();
        for (final AuftragDaten ad : auftragDatenColl) {
            if (!equipmentIt.hasNext()) {
                break;
            }

            final Rangierung rangierung =
                    rangierungsService.findRangierung4Equipment(equipmentIt.next().getId());
            final Endstelle esForAuftrag =
                    endstellenService.findEndstelle4Auftrag(ad.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);

            rangierung.setEsId(esForAuftrag.getId());
            rangierungsService.saveRangierung(rangierung, false);

            esForAuftrag.setRangierId(rangierung.getId());
            endstellenService.saveEndstelle(esForAuftrag);
            endgeraeteService.updateSchicht2Protokoll4Endstelle(esForAuftrag);
        }
    }

    /**
     * @param firstListOfBloecke erste Liste mit freien Bloecken. Wenn vorhanden werden die benoetigten Ports aus dieser Liste gezogen
     * @param secondListOfBloecke (optional) zweite Liste mit freien Bloecken. Wird betrachtet wenn:
     *                            - sich kein Treffer aus der ersten Liste ergeben hat
     *                            - der Auftrag nicht die LAYER2 Leistung TechLeistung.TECH_LEISTUNG_EFM gebucht hat
     * @param portsNeeded Anzahl der mind. benoetigten freien Ports in einem Block
     * @param auftragId
     * @param today       Datum vom gewuenschtem 'Heute'
     * @return die erste gefundene Gruppe an Ports mit
     */
    final List<Equipment> findPorts4Assignment(final List<List<Equipment>> firstListOfBloecke,
            final List<List<Equipment>> secondListOfBloecke,
            final int portsNeeded, final long auftragId, final Date today) {
        try {
            List<Equipment> block4Assignment =
                    findFirstBlockByNoOfFreePorts(portsNeeded, firstListOfBloecke);
            if (block4Assignment.isEmpty() && secondListOfBloecke != null) {
                final TechLeistung layer2TechLs =
                        this.leistungsService.findTechLeistung4Auftrag(auftragId, TechLeistung.TYP_LAYER2, today);
                if (layer2TechLs == null || !layer2TechLs.getId().equals(TechLeistung.TECH_LEISTUNG_EFM)) {
                    block4Assignment = findFirstBlockByNoOfFreePorts(portsNeeded, secondListOfBloecke);
                }
            }
            return block4Assignment;
        }
        catch (final FindException e) {
            throw new RuntimeException();
        }
    }

    private List<Equipment> findFirstBlockByNoOfFreePorts(final int noOfPortsNeeded, final List<List<Equipment>> bloecke) {
        List<Equipment> block4Assignment = emptyList();
        for (final List<Equipment> block : bloecke) {
            if (block.size() >= noOfPortsNeeded) {
                block4Assignment = block;
                break;
            }
        }
        return block4Assignment;
    }

    final Pair<AKWarnings, Collection<AuftragDaten>> checkAnzahlNdrahtAuftraege(Long auftragId) {
        final Pair<CCAuftragService.CheckAnzNdrahtResult, Collection<AuftragDaten>> checkOfNDrahtOption =
                this.ccAuftragService.checkAnzahlNdrahtOptionAuftraege(auftragId);
        switch (checkOfNDrahtOption.getFirst()) {
            case NO_NDRAHT_CONFIG:
                return singleWarningWithEmptyCollection("Für das Produkt des Auftrags ist keine N-Draht Option konfiguriert");
            case LESS_THAN_EXPECTED:
                return singleWarningWithEmptyCollection("Es sind weniger N-Draht-Option-Aufträge vorhanden als es die Produktkonfiguration vor sieht");
            case MORE_THAN_EXPECTED:
                return singleWarningWithEmptyCollection("Es sind mehr N-Draht-Option-Aufträge vorhanden als es die Produktkonfiguration vor sieht");
            case AS_EXPECTED:
                return Pair.create(new AKWarnings(), checkOfNDrahtOption.getSecond());
            default:
                throw new RuntimeException(String.format("unerwarteter Wert: %s", checkOfNDrahtOption.getFirst()));
        }
    }

    private Pair<AKWarnings, Collection<AuftragDaten>> singleWarningWithEmptyCollection(final String txt) {
        return Pair.create(
                new AKWarnings().addAKWarning(this, txt),
                ((Collection<AuftragDaten>) Collections.<AuftragDaten>emptyList()));
    }

    final AKWarnings checkAuftraegeNichtRangiert(final Collection<AuftragDaten> auftragDatens, final Date today)
            throws FindException {
        final AKWarnings warningsFound = new AKWarnings();
        for (final AuftragDaten auftragDaten : auftragDatens) {
            for (final String esTyp : Endstelle.ES_TYPEN) {
                final AKWarnings warningsForEsTyp = checkAufragNichtRangiertForEsTyp(today, auftragDaten, esTyp);
                if (warningsForEsTyp.isNotEmpty()) {
                    warningsFound.addAKMessages(warningsForEsTyp);
                }
            }
        }
        return warningsFound;
    }

    private AKWarnings checkAufragNichtRangiertForEsTyp(final Date today, final AuftragDaten auftragDaten,
            final String esTyp) throws FindException {
        final AKWarnings akWarnings = new AKWarnings();
        final Endstelle endstelle =
                endstellenService.findEndstelle4AuftragWithoutExplicitFlush(auftragDaten.getAuftragId(), esTyp);
        if (endstelle != null) {
            final Rangierung[] rangierungs =
                    rangierungsService.findRangierungen(auftragDaten.getAuftragId(), esTyp,
                            new Function2<Endstelle, Long, String>() {
                                @Override
                                // Endstelle schon bekannt, muss nicht erneut ermittelt werden
                                public Endstelle apply(final Long auftragId, final String esTyp) throws Exception {
                                    return endstelle;
                                }
                            }
                    );
            if (rangierungs != null) {
                for (final Rangierung rangierung : rangierungs) {
                    if (endstelle.getId().equals(rangierung.getEsId())
                            && DateTools.isDateAfter(rangierung.getGueltigBis(), today)) {
                        akWarnings.addAKWarning(this,
                                format("dem Auftrag %s ist bereits eine Rangierung (id=%s) zugeordnet!",
                                        auftragDaten.getAuftragId(), rangierung.getId())
                        );
                    }
                }
            }
        }
        return akWarnings;
    }

    /**
     * Sucht nach zusammenhaengenden Viererbloecken in einer Liste von Equipments. Beachtet dabei die
     * herstellerabhaengige zaehlweise.
     *
     * @param in eine nach HwEqn vorsortierte Liste von Equipments
     * @return Liste von vollständigen und unvollständigen Viererbloecken. Die inneren Listen können ein bis vier
     * Equipments enthalten.
     */
    @Nonnull
    final List<List<Equipment>> groupEquipmentsByCorresponding4erBloecke(@Nonnull final List<Equipment> in) {
        final ImmutableList.Builder<List<Equipment>> toReturn = ImmutableList.builder();
        List<Equipment> portsGroupedByViererBlock = Lists.newArrayListWithCapacity(4);
        Long bgId = null;
        PortNrOfBlockStartStrategy portNrOfBlockStartStrategy = null;
        List<Integer> portNrsAllowed = Lists.newArrayListWithCapacity(4);
        for (final Equipment equipment : in) {
            if (!equipment.getHwBaugruppenId().equals(bgId)) {
                portNrOfBlockStartStrategy = getPortNrOfBlockStartStrategy(equipment);
            }
            final int currentPortNr = portNrOfBlockStartStrategy.getPortNrFromHwEqn(equipment);
            if (portNrsAllowed.contains(currentPortNr) && equipment.getHwBaugruppenId().equals(bgId)) {
                portsGroupedByViererBlock.add(equipment);
            }
            else {
                final int blockStart = portNrOfBlockStartStrategy.get(equipment);
                portNrsAllowed = ImmutableList.of(blockStart, blockStart + 1, blockStart + 2, blockStart + 3);
                bgId = equipment.getHwBaugruppenId();
                if (!portsGroupedByViererBlock.isEmpty()) {
                    toReturn.add(portsGroupedByViererBlock);
                }
                portsGroupedByViererBlock = Lists.newArrayListWithCapacity(4);
                portsGroupedByViererBlock.add(equipment);
            }
        }
        if (!portsGroupedByViererBlock.isEmpty()) {
            toReturn.add(portsGroupedByViererBlock);
        }
        return toReturn.build();
    }


    /**
     * Gruppiert die angegebenen Equipments nach der Baugruppe und filtert die Equipments heraus, deren Baugruppe als
     * nicht 'bonding faehig' markiert sind.
     *
     * @param in
     * @return Liste von Equipments auf bonding-faehigen Baugruppen; die innere Liste enthaelt jeweils alle freien
     * Ports einer Baugruppe.
     */
    final List<List<Equipment>> groupEquipmentsByBondingCapableBaugruppe(@Nonnull final List<Equipment> in) {
        Map<Long, List<Equipment>> equipmentsByBgMap =
                in.stream()
                        .filter(eq -> isBgBondingCapable(eq.getHwBaugruppenId()))
                        .collect(Collectors.groupingBy(Equipment::getHwBaugruppenId));

        return equipmentsByBgMap.entrySet().stream()
                .map(i -> i.getValue())
                .collect(Collectors.toList());
    }


    boolean isBgBondingCapable(Long bgId) {
        try {
            HWBaugruppe hwBaugruppe = this.hwService.findBaugruppe(bgId);
            return (hwBaugruppe != null && hwBaugruppe.getHwBaugruppenTyp() != null)
                    ? hwBaugruppe.getHwBaugruppenTyp().getBondingCapable() : false;
        }
        catch (Exception e) {
            return false;
        }
    }


    @Override
    public List<Equipment> findViererBlockForSdslEquipmentOnAlcatelIpOrHuaweiDslam(final long equipmentId) {
        final Set<Long> alcatelSdslIpPhysikTypen =
                ImmutableSet.of(PhysikTyp.PHYSIKTYP_SDSL_IP_ALCATEL, PhysikTyp.PHYSIKTYP_SHDSL_IP_ALCATEL);
        List<Equipment> result = emptyList();
        try {
            final Equipment equipment = rangierungsService.findEquipment(equipmentId);
            if (equipment == null) {
                throw new RuntimeException(format("Equipment mit Id %s wurde nicht gefunden", equipmentId));
            }
            if (Equipment.HW_SCHNITTSTELLE_SDSL_OUT.equals(equipment.getHwSchnittstelle())) {
                final Rangierung rangierung = rangierungsService.findRangierung4Equipment(equipmentId);
                if (rangierung != null) {
                    final PhysikTyp physikTyp = physikService.findPhysikTyp(rangierung.getPhysikTypId());

                    if (HVTTechnik.ALCATEL.equals(physikTyp.getHvtTechnikId())
                            && alcatelSdslIpPhysikTypen.contains(physikTyp.getId())) {
                        result = find4erBlock(equipment, PortNoOfBlockStartStrategyAlcatel.instance);
                    }
                    else if (HVTTechnik.HUAWEI.equals(physikTyp.getHvtTechnikId())) {
                        result = find4erBlock(equipment, PortNoOfBlockStartStrategyHuawei.instance);
                    }
                }
            }
            return result;
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }
    }

    final List<Equipment> find4erBlock(final Equipment equipment, final PortNrOfBlockStartStrategy blockStart) {
        final List<Equipment> result = Lists.newArrayListWithCapacity(4);
        final int blockStartPort = blockStart.get(equipment);
        for (int i = 0; i < 4; i++) {
            result.add(findPortOfBlock(equipment, blockStartPort + i, blockStart.getHwEqnPart()));
        }
        return result;
    }

    final Equipment findPortOfBlock(final Equipment equipment, final int blockPortNr, final int hwEqnPart) {
        final int portStringLength = equipment.getHwEQNPart(hwEqnPart).length();
        final String hwEqnBase =
                equipment.getHwEQN().substring(0, equipment.getHwEQN().length() - portStringLength);

        final Equipment example = new Equipment();
        example.setHwBaugruppenId(equipment.getHwBaugruppenId());
        example.setGueltigBis(DateTools.getHurricanEndDate());
        example.setHwEQN(hwEqnBase + String.valueOf(blockPortNr));

        List<Equipment> found = equipmentDao.queryByExample(example, Equipment.class);
        // Datenanalyse hat ergeben, dass die Ports die auf EFM umgestellt werden sollen entweder eine oder keine
        // fuehrende
        // '0' im Port-Teil des HwEqn haben. Dreistellige Port-Anteile gab es nicht in dieser Menge (Stand 08/01/2014)
        if (found.isEmpty() && blockPortNr < 10) {
            example.setHwEQN(hwEqnBase + "0" + blockPortNr);
            found = equipmentDao.queryByExample(example, Equipment.class);
        }
        if (found.size() != 1) {
            throw new RuntimeException(
                    format("Es wurden keine oder mehrere Elemente mit HW-EQN %s für Baugruppe %s gefunden",
                            example.getHwEQN(), example.getHwBaugruppenId())
            );
        }
        return found.get(0);
    }

    /**
     * Filtert eine Liste von Listen von Equipments, deren Equipments alle das gleiche Schicht2Protokoll haben. Der Wert
     * <code>null</code> wird als equivalent zu ATM betrachtet. Auch leere Liste sind in der Ergebnismenge enthalten.
     *
     * @param bloecke eingngswert
     * @return die gefiltertete Liste
     */
    @Nonnull
    final List<List<Equipment>> filterBloeckeWithMixedLayer2Protocol(@Nonnull final List<List<Equipment>> bloecke) {
        final ImmutableList.Builder<List<Equipment>> filtered = ImmutableList.builder();
        for (final List<Equipment> block : bloecke) {
            final Iterator<Equipment> blockIter = block.iterator();
            boolean consistenBlock = true;
            if (blockIter.hasNext()) {
                final Schicht2Protokoll l2Protocol = blockIter.next().getSchicht2ProtokollOrAtm();
                while (blockIter.hasNext() && consistenBlock) {
                    consistenBlock = l2Protocol.equals(blockIter.next().getSchicht2ProtokollOrAtm());
                }
            }
            if (consistenBlock) {
                filtered.add(block);
            }
        }
        return filtered.build();
    }

    @CheckForNull
    private PortNrOfBlockStartStrategy getPortNrOfBlockStartStrategy(@Nonnull final Equipment eq) {
        final HWRack hwRack;
        try {
            hwRack = rangierungsService.getHWRackForEquipment(eq, hwService);
        }
        catch (FindException e) {
            throw new RuntimeException(e);
        }
        return (hwRack == null) ? null : PortNrOfBlockStartStrategy.instanceByHvtTechnik(hwRack.getHwProducer());
    }

    abstract static class PortNrOfBlockStartStrategy {

        @CheckForNull
        public static PortNrOfBlockStartStrategy instanceByHvtTechnik(@Nonnull final Long hvtTechnikId) {
            if (HVTTechnik.ALCATEL.equals(hvtTechnikId)) {
                return PortNoOfBlockStartStrategyAlcatel.instance;
            }
            else if (HVTTechnik.HUAWEI.equals(hvtTechnikId)) {
                return PortNoOfBlockStartStrategyHuawei.instance;
            }
            else {
                return null;
            }
        }

        /**
         * @param equipment
         * @return liefert zum Parameter passende erste Port-Nr (Anteil des HwEqn) des 4er-Blocks in dem es sich
         * befindet
         */
        public abstract int get(@Nonnull final Equipment equipment);

        public abstract int getHwEqnPart();

        final int getPortNrFromHwEqn(final Equipment equipment) {
            final int portNr = equipment.getHwEQNPartAsInt(getHwEqnPart());
            if (portNr == -1) {
                throw new RuntimeException(format("Fehler beim Parsen des HW-EQNs '%s' bei Port mit Id %s",
                        equipment.getHwEQN(), equipment.getId()));
            }
            return portNr;
        }
    }

    static final class PortNoOfBlockStartStrategyAlcatel extends PortNrOfBlockStartStrategy {
        static final PortNoOfBlockStartStrategyAlcatel instance = new PortNoOfBlockStartStrategyAlcatel();

        private PortNoOfBlockStartStrategyAlcatel() {
        }

        @Override
        public int get(@Nonnull final Equipment equipment) {
            final int portNr = getPortNrFromHwEqn(equipment);
            if (portNr == 0) {
                throw new IllegalStateException("Port-Anteil eines Alcatel-HW-Eqn darf nicht '0' bzw. '00' sein");
            }
            int result = portNr;
            // Nummerierung 1 - 4, 5 - 8, usw...
            while (((result - 1) % 4) != 0) {
                result--;
            }
            return result;
        }

        @Override
        public int getHwEqnPart() {
            return Equipment.HWEQNPART_DSLAM_PORT_ALCATEL;
        }
    }

    static final class PortNoOfBlockStartStrategyHuawei extends PortNrOfBlockStartStrategy {
        static final PortNoOfBlockStartStrategyHuawei instance = new PortNoOfBlockStartStrategyHuawei();

        private PortNoOfBlockStartStrategyHuawei() {
        }

        @Override
        public int get(@Nonnull final Equipment equipment) {
            int result = getPortNrFromHwEqn(equipment);
            // Nummerierung 0 - 3, 4 - 7, usw...
            while ((result % 4) != 0) {
                result--;
            }
            return result;
        }

        @Override
        public int getHwEqnPart() {
            return Equipment.HWEQNPART_DSLAM_PORT;
        }
    }

    private class DslamGeraeteBezComparator implements Comparator<HWDslam> {
        @Override
        public int compare(HWDslam dslam1, HWDslam dslam2) {
            return StringTools.compare(dslam1.getGeraeteBez(), dslam2.getGeraeteBez(), true);
        }
    }
}
