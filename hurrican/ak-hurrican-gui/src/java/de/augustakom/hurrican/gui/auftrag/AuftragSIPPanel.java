/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 11:20:00
 */

package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunk;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.SIPInterTrunkService;

/**
 * Panel fuer die Definition von Switch / TrunkGroup Zuordnungen.
 */
public class AuftragSIPPanel extends AbstractAuftragPanel implements AKModelOwner,
        AKDataLoaderComponent, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(AuftragSIPPanel.class);

    private static final String ADD_DATA = "add.data";

    private AKReferenceAwareTableModel<AuftragSIPInterTrunk> tbMdlAssociations = null;


    // Modelle
    private CCAuftragModel auftragModel = null;

    private SIPInterTrunkService sipService;

    public AuftragSIPPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragSIPPanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJButton btnAddData = getSwingFactory().createButton(ADD_DATA, getActionListener(), null);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddData, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));

        tbMdlAssociations = new AKReferenceAwareTableModel<>(
                new String[] { "Switch", "TrunkGroup" },
                new String[] { "hwSwitch.id", "trunkGroup" },
                new Class[] { HWSwitch.class, String.class });
        AKJTable tbAssociations = new AKJTable(tbMdlAssociations, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbAssociations.attachSorter();
        tbAssociations.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbAssociations.fitTable(new int[] { 100, 150 });
        AKJScrollPane spAssociations = new AKJScrollPane(tbAssociations, new Dimension(300, 180));

        this.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.title")));
        this.setLayout(new GridBagLayout());
        this.add(btnPnl, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(spAssociations, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 1, 1, 1, GridBagConstraints.BOTH));

        AKManageableComponent[] managedComponents = new AKManageableComponent[] { btnAddData };
        manageGUI(managedComponents);
    }

    @Override
    public final void loadData() {
        try {
            HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
            sipService = getCCService(SIPInterTrunkService.class);

            List<HWSwitch> switchTypes = hwSwitchService.findAllSwitches();
            Map<Long, HWSwitch> switchTypeMap =
                    switchTypes
                            .stream()
                            .collect(Collectors.toMap(hwSwitch -> hwSwitch.getId(), hwSwitch -> hwSwitch));
            tbMdlAssociations.addReference(0, switchTypeMap, "name");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        try {
            auftragModel = null;
            if (model instanceof CCAuftragModel) {
                auftragModel = (CCAuftragModel) model;
            }
            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException("Error loading UMTS data: " + e.getMessage(), e);
        }
    }

    @Override
    public void readModel() throws AKGUIException {
        if (auftragModel != null) {
            try {
                List<AuftragSIPInterTrunk> sipList = sipService.findSIPInterTrunks4Order(auftragModel.getAuftragId());
                tbMdlAssociations.setData(sipList);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        //not needed in this panel
    }

    @Override
    public void saveModel() throws AKGUIException {
        //not needed in this panel
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        if (StringUtils.equals(command, ADD_DATA)) {
            addAssociation();
        }
    }

    /**
     * Fuegt eine Zuordnung hinzu
     */
    private void addAssociation() {
        try {
            AuftragSIPInterTrunk association = new AuftragSIPInterTrunk();
            association.setAuftragId(auftragModel.getAuftragId());

            EditSipInterTrunkDialog dlg = new EditSipInterTrunkDialog(association);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if ((result instanceof Integer) && ((Integer) result == JOptionPane.OK_OPTION)) {
                tbMdlAssociations.addObject(association);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof AuftragSIPInterTrunk) {
            EditSipInterTrunkDialog dlg = new EditSipInterTrunkDialog((AuftragSIPInterTrunk) selection);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if ((result instanceof Integer) && ((Integer) result == JOptionPane.OK_OPTION)) {
                tbMdlAssociations.fireTableDataChanged();
            }
        }
    }

}
