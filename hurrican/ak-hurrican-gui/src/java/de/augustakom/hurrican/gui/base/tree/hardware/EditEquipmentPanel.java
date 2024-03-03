/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 01.07.2010 12:12:09
  */

package de.augustakom.hurrican.gui.base.tree.hardware;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.tree.hardware.node.EquipmentNodeContainer;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.tools.comparator.HwEqnComparator;

/**
 *
 */
public class EditEquipmentPanel extends AbstractServicePanel {
    private static final Logger LOGGER = Logger.getLogger(EditEquipmentPanel.class);

    private static final String BTN_SAVE = "save";
    private AKTableModelXML<Equipment> tbMdlEquipments;

    public EditEquipmentPanel(List<EquipmentNodeContainer> nodes) {
        super("de/augustakom/hurrican/gui/base/tree/hardware/resources/EditEquipmentPanel.xml");
        createGUI();
        loadData(nodes);
    }

    private void loadData(List<EquipmentNodeContainer> nodes) {
        List<Equipment> equipmentsOfNodes = new ArrayList<Equipment>();
        for (EquipmentNodeContainer container : nodes) {
            equipmentsOfNodes.addAll(container.getEquipments());
        }

        Collections.sort(equipmentsOfNodes, new HwEqnComparator());
        tbMdlEquipments.addObjects(equipmentsOfNodes);
    }

    @Override
    protected final void createGUI() {
        AKJButton btnSave = getSwingFactory().createButton(BTN_SAVE, getActionListener());

        tbMdlEquipments = new AKTableModelXML<>("de/augustakom/hurrican/gui/base/tree/hardware/resources/EquipmentTableModel.xml");
        AKJTable tbEquipments = new AKJTable(tbMdlEquipments);
        tbEquipments.attachSorter();
        tbEquipments.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbEquipments.fitTable(new int[] { 50, 50, 100, 100, 50, 50, 50, 50, 50, 50, 50, 50, 50, 75, 50, 50, 75, 100, 50, 50, 50, 50, 100, 100, 100, 100 });
        AKJScrollPane spEquipments = new AKJScrollPane(tbEquipments);
        spEquipments.setPreferredSize(new Dimension(400, 400));
        setLayout(new GridBagLayout());

        add(spEquipments, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        add(btnSave, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnSave);
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void execute(String command) {
        if (BTN_SAVE.equals(command)) {
            try {
                Collection<Equipment> data = tbMdlEquipments.getModified();
                if (!data.isEmpty()) {
                    AKUserService us = getAuthenticationService(AKUserService.class);
                    AKUser user = us.findUserBySessionId(HurricanSystemRegistry.instance().getSessionId());
                    for (Equipment equipment : data) {
                        equipment.setUserW(user.getLoginName());
                    }
                    RangierungsService rangierungsService = getCCService(RangierungsService.class);
                    rangierungsService.saveEquipments(data);
                    MessageHelper.showInfoDialog(this, "Equipments erfolgreich gespeichert", null, true);
                }
            }
            catch (Exception ex) {
                LOGGER.error("Konnte Equipments nicht speichern", ex);
                MessageHelper.showErrorDialog(this, ex);
            }
        }
    }

    @Override
    public String getName() {
        return getSwingFactory().getText("border.title");
    }
}
