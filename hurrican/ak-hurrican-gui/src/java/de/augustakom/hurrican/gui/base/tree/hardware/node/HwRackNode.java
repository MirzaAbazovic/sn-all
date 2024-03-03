/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 11:45:48
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.awt.event.*;
import java.util.*;
import javax.swing.tree.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.tools.NodeProperty;
import de.augustakom.hurrican.gui.base.tree.tools.TreeSortEntry;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * Node fuer die Darstellung von HW-Racks innerhalb des Hardware-Trees. Ermittelt als Child-Elemente die dem Rack
 * zugeordneten Baugruppen.
 *
 *
 */
public class HwRackNode extends DynamicTreeNode {
    public static final NodeProperty<String> PROP_RACKTYP =
            NodeProperty.create(String.class, propPath("rackModel", HWRack.RACK_TYP), "Rack Typ");
    public static final NodeProperty<String> PROP_ANLAGENBEZ =
            NodeProperty.create(String.class, propPath("rackModel", HWRack.ANLAGEN_BEZ), "Anlagenbezeichnung");
    public static final NodeProperty<String> PROP_GERAETEBEZ =
            NodeProperty.create(String.class, propPath("rackModel", HWRack.GERAETE_BEZ), "Gerätebezeichnung");
    public static final NodeProperty<String> PROP_MANAGEMENTBEZ =
            NodeProperty.create(String.class, propPath("rackModel", HWRack.MANAGEMENT_BEZ), "Managementbezeichnung");
    public static final NodeProperty<?>[] NODE_PROPERTIES = new NodeProperty[] {
            PROP_RACKTYP, PROP_ANLAGENBEZ, PROP_GERAETEBEZ, PROP_MANAGEMENTBEZ
    };


    private final HWRack rackModel;
    private final List<Pair<Integer, HwBaugruppeNode>> hiddenChilds = new ArrayList<>();


    public HwRackNode(DynamicTree tree, HWRack rackModel) {
        super(tree, true);
        this.rackModel = rackModel;
    }


    public HWRack getRackModel() {
        return rackModel;
    }


    @Override
    protected List<DynamicTreeNode> doLoadChildren() throws Exception {
        HWService hwService = CCServiceFinder.instance().getCCService(HWService.class);
        List<HWBaugruppe> baugruppen = hwService.findBaugruppen4Rack(rackModel.getId());

        List<DynamicTreeNode> result = new ArrayList<>();
        for (HWBaugruppe baugruppe : baugruppen) {
            result.add(new HwBaugruppeNode(tree, baugruppe));
        }
        return result;
    }


    @Override
    public List<TreeSortEntry> getDefaultChildSort() {
        return Arrays.asList(new TreeSortEntry(HwBaugruppeNode.class, HwBaugruppeNode.PROP_MOD_NUMBER, true));
    }


    @Override
    public String getDisplayName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rackModel.getClass().getSimpleName().substring(2).toUpperCase());
        if (!StringUtils.isEmpty(rackModel.getAnlagenBez())) {
            stringBuilder.append(": ");
            stringBuilder.append(rackModel.getAnlagenBez());
        }
        stringBuilder.append(" (");
        stringBuilder.append(rackModel.getGeraeteBez());
        stringBuilder.append(")");
        if (!StringUtils.isEmpty(rackModel.getManagementBez())) {
            stringBuilder.append(" [");
            stringBuilder.append(rackModel.getManagementBez());
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }


    @Override
    public String getIcon() {
        return IMAGE_BASE + "dslam.gif";
    }


    @Override
    public List<AKAbstractAction> getNodeActionsForContextMenu() {
        List<AKAbstractAction> actions = new ArrayList<>();
        actions.add(new ToggleEingebautAction());
        return actions;
    }


    /**
     * Action, um nicht eingebaute Baugruppe zu verstecken / anzuzeigen.
     */
    class ToggleEingebautAction extends AKAbstractAction {
        private static final String TOGGLE_EINGEBAUT_TITLE = "Verstecke/zeige nicht eingebaute";
        private static final String TOGGLE_EINGEBAUT = "toggle.eingebaut";

        ToggleEingebautAction() {
            setName(TOGGLE_EINGEBAUT_TITLE);
            setActionCommand(TOGGLE_EINGEBAUT);
            setTooltip("Versteckt / zeigt Baugruppen, die nicht als 'eingebaut' markiert sind.");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (hiddenChilds.isEmpty()) {
                int i = 0;
                while (i < getChildCount()) {
                    TreeNode child = getChildAt(i);
                    if (child instanceof HwBaugruppeNode) {
                        HwBaugruppeNode bgNode = (HwBaugruppeNode) child;
                        if (!Boolean.TRUE.equals(bgNode.getBaugruppe().getEingebaut())) {
                            hiddenChilds.add(Pair.create(i, bgNode));
                            remove(i);
                        }
                        else {
                            i += 1;
                        }
                    }
                }
            }
            else {
                Collections.reverse(hiddenChilds); // wg indizes in umgekehrter reihenfolge wieder einfuegen
                for (Pair<Integer, HwBaugruppeNode> entry : hiddenChilds) {
                    insert(entry.getSecond(), entry.getFirst());
                }
                hiddenChilds.clear();
            }
            tree.getModel().nodeStructureChanged(HwRackNode.this);
        }
    }
}
