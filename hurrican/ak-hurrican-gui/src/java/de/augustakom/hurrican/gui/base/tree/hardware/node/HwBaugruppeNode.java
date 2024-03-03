/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 15:23:28
 */
package de.augustakom.hurrican.gui.base.tree.hardware.node;

import java.awt.event.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.tools.NodeProperty;
import de.augustakom.hurrican.gui.base.tree.tools.TreeSortEntry;
import de.augustakom.hurrican.gui.hvt.PortGenerationDialog;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;


/**
 * Tree-Node fuer die Darstellung von HW-Baugruppen.
 *
 *
 */
public class HwBaugruppeNode extends EquipmentNodeContainer {
    public static final NodeProperty<String> PROP_MOD_NUMBER =
            NodeProperty.create(String.class, propPath("baugruppe", HWBaugruppe.MOD_NUMBER), "Modul-Nummer");
    public static final NodeProperty<String> PROP_BAUGRUPPEN_TYP =
            NodeProperty.create(String.class, propPath("baugruppenTyp", HWBaugruppenTyp.NAME), "Baugruppentyp");
    public static final NodeProperty<?>[] NODE_PROPERTIES = new NodeProperty[] {
            PROP_MOD_NUMBER, PROP_BAUGRUPPEN_TYP
    };

    private static final Logger LOGGER = Logger.getLogger(HwBaugruppeNode.class);

    private final HWBaugruppe baugruppe;


    public HwBaugruppeNode(DynamicTree tree, HWBaugruppe baugruppe) {
        super(tree, true);
        this.baugruppe = baugruppe;
    }


    public HWBaugruppe getBaugruppe() {
        return baugruppe;
    }


    @Override
    public Collection<Equipment> getEquipments() {
        try {
            RangierungsService rangierungsService = CCServiceFinder.instance().getCCService(RangierungsService.class);
            return rangierungsService.findEquipments4HWBaugruppe(baugruppe.getId());
        }
        catch (Exception ex) {
            LOGGER.error("Konnte Equipments nicht laden", ex);
        }
        return null;
    }


    @Override
    public List<TreeSortEntry> getDefaultChildSort() {
        return Arrays.asList(new TreeSortEntry(EquipmentNode.class, EquipmentNode.PROP_EQN, true));
    }


    @Override
    public List<AKAbstractAction> getNodeActionsForContextMenu() {
        List<AKAbstractAction> actions = new ArrayList<AKAbstractAction>();
        actions.add(new GeneratePortsAction());
        return actions;
    }

    @Override
    public String getDisplayName() {
        return "BG: " + baugruppe.getHwBaugruppenTyp().getName() + " (" + baugruppe.getModNumber() + ")";
    }

    @Override
    public String getIcon() {
        if (Boolean.TRUE.equals(baugruppe.getEingebaut())) {
            return IMAGE_BASE + "baugruppe.gif";
        }
        else {
            return IMAGE_BASE + "baugruppe_inaktiv.gif";
        }
    }

    @Override
    public String getTooltip() {
        if (((baugruppe.getHwBaugruppenTyp() != null) && StringUtils.isNotBlank(baugruppe.getHwBaugruppenTyp().getDescription()))
                || StringUtils.isNotBlank(baugruppe.getBemerkung())) {
            StringBuilder tooltip = new StringBuilder();
            if (StringUtils.isNotBlank(baugruppe.getHwBaugruppenTyp().getDescription())) {
                tooltip.append("BG-Typ: ").append(baugruppe.getHwBaugruppenTyp().getDescription());
            }
            if (StringUtils.isNotBlank(baugruppe.getBemerkung())) {
                if (tooltip.length() > 0) {
                    tooltip.append("; ");
                }
                tooltip.append("Bemerkung: ").append(baugruppe.getBemerkung());
            }
            return tooltip.toString();
        }
        else {
            return super.getTooltip();
        }
    }

    /**
     * Action, um einen Dialog fuer die Port-Generierung zu oeffnen.
     */
    class GeneratePortsAction extends AKAbstractAction {
        private static final String GENERATE_PORTS_TITLE = "Ports generieren...";
        private static final String GENERATE_PORTS = "generate.ports";

        GeneratePortsAction() {
            setName(GENERATE_PORTS_TITLE);
            setActionCommand(GENERATE_PORTS);
            setTooltip("Öffnet einen Dialog, ueber den Ports generiert/importiert werden koennen");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                PortGenerationDialog portDlg = new PortGenerationDialog(baugruppe);
                DialogHelper.showDialog(getMainFrame(), portDlg, true, true);
            }
            catch (IllegalArgumentException ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }
}
