/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 15:32:08
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.tools.NodeProperty;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.iface.HwEqnAwareModel;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.augustakom.hurrican.tools.comparator.HwEqnComparator;


/**
 * Tree-Node fuer die Darstellung von Equipment-Objekten.
 */
public class EquipmentNode extends DynamicTreeNode {

    public static final NodeProperty<HwEqnAwareModel> PROP_EQN =
            NodeProperty.create(HwEqnAwareModel.class, propPath("equipment"), "EQN", new HwEqnComparator());

    public static final NodeProperty<String> PROP_STIFT =
            NodeProperty.create(String.class, propPath("equipment", Equipment.RANG_STIFT1), "Stift");

    private final Equipment equipment;
    private final PhysikTyp physikTyp;
    private final boolean talActive;

    public EquipmentNode(DynamicTree tree, EquipmentNodeInfo equipmentNodeInfo) {
        super(tree, true);
        this.equipment = equipmentNodeInfo.equipment;
        this.talActive = equipmentNodeInfo.talActive;
        this.physikTyp = equipmentNodeInfo.physikTyp;
    }

    public Equipment getEquipment() {
        return equipment;
    }


    @Override
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        CCAuftragService auftragService = CCServiceFinder.instance().getCCService(CCAuftragService.class);
        PhysikService physikService = CCServiceFinder.instance().getCCService(PhysikService.class);
        List<AuftragDaten> auftragDatenList = auftragService.findAuftragDatenByEquipment(equipment.getId());

        List<DynamicTreeNode> result = new ArrayList<>();
        for (AuftragDaten auftragDaten : auftragDatenList) {
            if (NumberTools.isLess(auftragDaten.getStatusId(), AuftragStatus.KONSOLIDIERT)) {
                VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragIdTx(auftragDaten.getAuftragId());
                result.add(new AuftragNode(tree, auftragDaten, verbindungsBezeichnung));
            }
        }
        return result;
    }


    @Override
    public String getDisplayName() {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(equipment.getHwEQN())) {
            stringBuilder.append("EQ: ");
            stringBuilder.append(equipment.getHwEQN());
        }
        else if (StringUtils.isNotBlank(equipment.getRangStift1())) {
            stringBuilder.append("Stift: ");
            stringBuilder.append(StringUtils.trimToEmpty(equipment.getRangLeiste1()));
            stringBuilder.append(" - ");
            stringBuilder.append(StringUtils.trimToEmpty(equipment.getRangStift1()));
        }
        else {
            stringBuilder.append("ID: ");
            stringBuilder.append(String.format("%d", equipment.getId()));
        }
        if ((physikTyp != null) ||
                ((equipment.getVerwendung() != null) && !StringUtils.isEmpty(equipment.getVerwendung().toString()))) {
            stringBuilder.append(" [");
            if (physikTyp != null) {
                stringBuilder.append(physikTyp.getName());
            }
            else {
                stringBuilder.append(equipment.getVerwendung().toString());
            }
            stringBuilder.append("]");
        }
        stringBuilder.append(" (");
        stringBuilder.append(equipment.getStatus());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public String getIcon() {
        if (talActive) {
            return IMAGE_BASE + "port.gif";
        }
        return IMAGE_BASE + "port_free.gif";
    }
}
