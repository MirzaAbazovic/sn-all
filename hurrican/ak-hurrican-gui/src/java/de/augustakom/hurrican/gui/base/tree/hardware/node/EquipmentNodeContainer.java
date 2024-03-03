/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 01.07.2010 12:28:59
  */

package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.util.*;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * MarkerInterface für Nodes die EquipmentNodes als ChildNodes haben.
 */
public abstract class EquipmentNodeContainer extends DynamicTreeNode {
    private static final Logger LOGGER = Logger.getLogger(EquipmentNodeContainer.class);

    public EquipmentNodeContainer(DynamicTree tree) {
        super(tree);
    }

    public EquipmentNodeContainer(DynamicTree tree, boolean allowsChildren) {
        super(tree, allowsChildren);
    }


    public abstract Collection<Equipment> getEquipments();

    private Collection<EquipmentNodeInfo> getEquipmentNodeInfos() {
        List<EquipmentNodeInfo> result = new ArrayList<>();
        try {
            RangierungsService rangierungsService = CCServiceFinder.instance().getCCService(RangierungsService.class);
            PhysikService physikService = CCServiceFinder.instance().getCCService(PhysikService.class);
            CCAuftragService auftragService = CCServiceFinder.instance().getCCService(CCAuftragService.class);
            EndstellenService endstellenService = CCServiceFinder.instance().getCCService(EndstellenService.class);
            CarrierService carrierService = CCServiceFinder.instance().getCCService(CarrierService.class);

            Map<Long, PhysikTyp> physikTypen = new HashMap<>();
            for (PhysikTyp physikTyp : physikService.findPhysikTypen()) {
                physikTypen.put(physikTyp.getId(), physikTyp);
            }
            Collection<Equipment> equipments = getEquipments();
            Map<Long, Rangierung> rangierungen = rangierungsService.findRangierungen4Equipments(equipments);
            Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
            for (Equipment equipment : equipments) {
                PhysikTyp physikTyp = null;
                // Fuer Rangierung duplizierte Ports herausfiltern
                if (!Equipment.HW_SCHNITTSTELLE_ADSL_IN.equals(equipment.getRangSSType())) {
                    boolean talAktiv = false;
                    Rangierung rangierung = rangierungen.get(equipment.getId());

                    if (rangierung != null) {
                        // pruefen, ob Auftrag aktiv ist
                        talAktiv = hasActiveOrder(equipment, auftragService);
                        physikTyp = physikTypen.get(rangierung.getPhysikTypId());

                        if (!talAktiv) {
                            List<Endstelle> endstellen = new ArrayList<>();
                            if ((rangierung.getEsId() != null) && (rangierung.getEsId() > 0)) {
                                endstellen.add(endstellenService.findEndstelle(rangierung.getEsId()));
                            }
                            else {
                                endstellen.addAll(endstellenService.findEndstellenWithRangierId(rangierung.getId()));
                            }

                            talAktiv = checkCBs4EndstellenIfTalIsActive(today, carrierService, endstellen);
                        }
                    }
                    result.add(new EquipmentNodeInfo(equipment, physikTyp, talAktiv));
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error("Konnte Equipments nicht laden", ex);
        }
        return result;
    }

    private boolean hasActiveOrder(Equipment equipment, CCAuftragService auftragService) {
        try {
            List<AuftragDaten> auftragDatenList = auftragService.findAuftragDatenByEquipment(equipment.getId());
            if (CollectionTools.isNotEmpty(auftragDatenList)) {
                for (AuftragDaten auftragDaten : auftragDatenList) {
                    if (auftragDaten.isAuftragActive()) {
                        return true;
                    }
                }
            }
        }
        catch (FindException e) {
            LOGGER.info(e.getMessage());
        }

        return false;
    }

    /**
     * Prüft, ob mindestens eine Carrierbestellung aktiv ist. Wenn dies der Fall ist, bleibt auch die TAL aktiv.
     */
    private boolean checkCBs4EndstellenIfTalIsActive(Date today, CarrierService carrierService, List<Endstelle> endstellen) throws FindException {
        if (CollectionTools.isNotEmpty(endstellen)) {
            for (Endstelle endstelle : endstellen) {
                List<Carrierbestellung> cBs = carrierService.findCBs(endstelle.getCb2EsId());
                for (Carrierbestellung carrierbestellung : cBs) {
                    if ((carrierbestellung.getBereitstellungAm() != null)
                            && ((carrierbestellung.getKuendBestaetigungCarrier() == null) || carrierbestellung
                            .getKuendBestaetigungCarrier().after(today))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        Collection<EquipmentNodeInfo> equipmentNodeInfos = getEquipmentNodeInfos();

        List<DynamicTreeNode> result = new ArrayList<>();
        for (EquipmentNodeInfo equipmentNodeInfo : equipmentNodeInfos) {
            result.add(new EquipmentNode(tree, equipmentNodeInfo));
        }
        return result;
    }
}
