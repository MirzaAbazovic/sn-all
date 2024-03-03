/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 11:06:35
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.util.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.hardware.actions.CreatePdhLeisteAction;
import de.augustakom.hurrican.gui.base.tree.tools.NodeProperty;
import de.augustakom.hurrican.gui.base.tree.tools.TreeSortEntry;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;

/**
 * Node fuer den Hardware-Tree zur Darstellung von HVT-Standorten. Ermittelt als Child-Elemente die Racks, die dem
 * Standort zugeordnet sind.
 *
 *
 */
public class HvtNode extends DynamicTreeNode {

    public static final NodeProperty<String> PROP_NAME =
            NodeProperty.create(String.class, propPath("hvtGruppe", HVTGruppe.ORTSTEIL), "Name");
    public static final NodeProperty<?>[] NODE_PROPERTIES = new NodeProperty[] {
            PROP_NAME
    };

    private final HVTStandort hvtStandort;
    private final HVTGruppe hvtGruppe;


    public HvtNode(DynamicTree tree, HVTStandort hvtStandort, HVTGruppe hvtGruppe) {
        super(tree, true);
        this.hvtStandort = hvtStandort;
        this.hvtGruppe = hvtGruppe;
    }

    /**
     * Laedt die Children fuer ein bestimmtes Objekt nach und ordnet die Nodes dem Parent hinzu.
     */
    @Override
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        HWService hwService = CCServiceFinder.instance().getCCService(HWService.class);
        List<HWRack> racks = hwService.findRacks(hvtStandort.getId(), null, false);

        List<DynamicTreeNode> result = new ArrayList<DynamicTreeNode>();
        for (HWRack rack : racks) {
            result.add(new HwRackNode(tree, rack));
        }

        // Parallel zu Rack-Nodes einen speziellen 'P02' Node anzeigen (fuer PDH P02 Equipments)
        result.add(new HwPdhP02Node(tree, hvtStandort));

        HVTService hvtService = CCServiceFinder.instance().getCCService(HVTService.class);
        List<UEVT> uevts = hvtService.findUEVTs4HVTStandort(hvtStandort.getId());

        for (UEVT uevt : uevts) {
            result.add(new UevtNode(tree, uevt));
        }

        return result;
    }


    @Override
    public List<TreeSortEntry> getDefaultChildSort() {
        return Arrays.asList(new TreeSortEntry(HwRackNode.class, HwRackNode.PROP_RACKTYP, true),
                new TreeSortEntry(HwRackNode.class, HwRackNode.PROP_GERAETEBEZ, true),
                new TreeSortEntry(UevtNode.class, UevtNode.PROP_UEVT, true));
    }

    @Override
    public List<AKAbstractAction> getNodeActionsForContextMenu() {
        List<AKAbstractAction> actions = new ArrayList<AKAbstractAction>();
        actions.add(new CreatePdhLeisteAction(getMainFrame(), hvtStandort));
        return actions;
    }

    @Override
    public String getDisplayName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hvtGruppe.getOrtsteil());
        stringBuilder.append(" (");
        stringBuilder.append(hvtGruppe.getOnkz());
        stringBuilder.append(" - ");
        stringBuilder.append(hvtStandort.getAsb());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }


    @Override
    public String getIcon() {
        return IMAGE_BASE + "hvt.gif";
    }

}
