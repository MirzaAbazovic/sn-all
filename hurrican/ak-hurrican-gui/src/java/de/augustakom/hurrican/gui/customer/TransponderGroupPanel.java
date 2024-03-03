/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2011 14:22:08
 */
package de.augustakom.hurrican.gui.customer;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.housing.Transponder;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HousingService;

/**
 * Panel zur Administration der Transponder-Gruppen.
 */
public class TransponderGroupPanel extends AbstractServicePanel implements AKDataLoaderComponent, ListSelectionListener, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(TransponderGroupPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/customer/resources/TransponderGroupPanel.xml";

    private static final String REMOVE_TRANSPONDER = "remove.transponder";
    private static final String ADD_TRANSPONDER = "add.transponder";
    private static final String REMOVE_GROUP = "remove.group";
    private static final String RENAME_GROUP = "rename.group";
    private static final String ADD_GROUP = "add.group";
    private static final String TRANSPONDER = "transponder";
    private static final String TRANSPONDER_GROUPS = "transponder.groups";

    private final Long kundeNo;
    private TransponderGroup selectedTransponderGroup;

    private HousingService housingService;

    private AKJList lsTransponderGroups;
    private AKJTable tbTransponder;
    private AKMutableTableModel<Transponder> tbMdlTransponder;

    public TransponderGroupPanel(Long kundeNo) {
        super(RESOURCE);
        this.kundeNo = kundeNo;

        doInit();
        createGUI();
        loadData();
    }

    private void doInit() {
        try {
            housingService = getCCService(HousingService.class);
        }
        catch (ServiceNotFoundException e) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Fehler bei der Initialisierung: " + ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblTransponderGroups = getSwingFactory().createLabel(TRANSPONDER_GROUPS, AKJLabel.LEFT, Font.BOLD);

        AKJButton btnAddGroup = getSwingFactory().createButton(ADD_GROUP, getActionListener());
        AKJButton btnRemoveGroup = getSwingFactory().createButton(REMOVE_GROUP, getActionListener());
        AKJButton btnRenameGroup = getSwingFactory().createButton(RENAME_GROUP, getActionListener());
        lsTransponderGroups = new AKJList();
        lsTransponderGroups.setCellRenderer(new AKCustomListCellRenderer<>(TransponderGroup.class, TransponderGroup::getTransponderDescription));
        lsTransponderGroups.addListSelectionListener(this);
        AKJScrollPane spGroups = new AKJScrollPane(lsTransponderGroups, new Dimension(150, 300));

        AKJLabel lblTransponder = getSwingFactory().createLabel(TRANSPONDER, AKJLabel.LEFT, Font.BOLD);
        AKJButton btnAddTransponder = getSwingFactory().createButton(ADD_TRANSPONDER, getActionListener());
        AKJButton btnRemoveTransponder = getSwingFactory().createButton(REMOVE_TRANSPONDER, getActionListener());
        tbMdlTransponder = new AKReflectionTableModel<Transponder>(
                new String[] { "Transponder ID", "Vorname", "Nachname" },
                new String[] { Transponder.TRANSPONDER_ID, Transponder.CUSTOMER_FIRST_NAME, Transponder.CUSTOMER_LAST_NAME },
                new Class[] { Long.class, String.class, String.class });
        tbTransponder = new AKJTable(tbMdlTransponder, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbTransponder.fitTable(new int[] { 90, 150, 150 });
        tbTransponder.addMouseListener(new AKTableDoubleClickMouseListener(this));
        AKJScrollPane spTransponder = new AKJScrollPane(tbTransponder, new Dimension(410, 300));

        // @formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblTransponderGroups   , GBCFactory.createGBC(100,  0, 0, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        left.add(btnAddGroup            , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        left.add(btnRemoveGroup         , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        left.add(btnRenameGroup         , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(new AKJPanel()         , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(spGroups               , GBCFactory.createGBC(100,100, 0, 2, 4, 1, GridBagConstraints.BOTH));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblTransponder        , GBCFactory.createGBC(100,  0, 0, 0, 3, 1, GridBagConstraints.HORIZONTAL));
        right.add(btnAddTransponder     , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        right.add(btnRemoveTransponder  , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.NONE));
        right.add(new AKJPanel()        , GBCFactory.createGBC(100,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(spTransponder         , GBCFactory.createGBC(100,100, 0, 2, 3, 1, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(left                   , GBCFactory.createGBC(  0,100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(right                  , GBCFactory.createGBC(100,100, 1, 0, 1, 1, GridBagConstraints.BOTH, 25));
        // @formatter:on
    }

    @Override
    public final void loadData() {
        try {
            lsTransponderGroups.removeAllItems();

            List<TransponderGroup> transponderGroups = housingService.findTransponderGroups(kundeNo);
            lsTransponderGroups.addItems(transponderGroups, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if (ADD_GROUP.equals(command)) {
            addTransponderGroup();
        }
        else if (REMOVE_GROUP.equals(command)) {
            removeTransponderGroup();
        }
        else if (RENAME_GROUP.equals(command)) {
            renameTransponderGroup();
        }
        else if (ADD_TRANSPONDER.equals(command)) {
            addTransponder();
        }
        else if (REMOVE_TRANSPONDER.equals(command)) {
            removeTransponder();
        }
    }


    private void addTransponderGroup() {
        String groupName = MessageHelper.showInputDialog(getMainFrame(), "Name der neuen Transponder-Gruppe:",
                "neue Transponder-Gruppe", AKJOptionDialog.PLAIN_MESSAGE);
        if (StringUtils.isNotBlank(groupName)) {
            TransponderGroup newGroup = new TransponderGroup();
            newGroup.setKundeNo(kundeNo);
            newGroup.setTransponderDescription(groupName);

            try {
                housingService.saveTransponderGroup(newGroup);
                lsTransponderGroups.addItems(Arrays.asList(newGroup));
            }
            catch (StoreException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void removeTransponderGroup() {
        if (selectedTransponderGroup != null) {
            int result = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Soll die Transponder-Gruppe wirklich entfernt werden?\n" +
                            "Damit gehen auch alle hinterlegten Transponder-IDs verloren.\n" +
                            "(Die Gruppe kann nur entfernt werden, wenn sie keinem Auftrag\n" +
                            "mehr zugeordnet ist!)",
                    "Gruppe entfernen"
            );
            if (result == AKJOptionDialog.OK_OPTION) {
                try {
                    housingService.deleteTransponderGroup(selectedTransponderGroup);
                    loadData();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
        }
    }

    private void renameTransponderGroup() {
        try {
            if (selectedTransponderGroup == null) {
                throw new HurricanGUIException("Bitte zuerst eine Transponder-Gruppe auswählen!");
            }
            String groupName = MessageHelper.showInputDialog(getMainFrame(), "Neuer Name der Transponder-Gruppe:",
                    "Transponder-Gruppe umbenennen", AKJOptionDialog.PLAIN_MESSAGE);
            if (StringUtils.isNotBlank(groupName)) {
                selectedTransponderGroup.setTransponderDescription(groupName);
                housingService.saveTransponderGroup(selectedTransponderGroup);

                loadData();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void addTransponder() {
        showTransponderDialog(new Transponder());
    }

    private void showTransponderDialog(Transponder transponder) {
        try {
            if (selectedTransponderGroup == null) {
                throw new HurricanGUIException("Bitte zuerst eine Transponder-Gruppe wählen!");
            }

            TransponderDialog dlg = new TransponderDialog(selectedTransponderGroup, transponder);
            Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (result != null) {
                housingService.saveTransponderGroup(selectedTransponderGroup);
                tbMdlTransponder.setData(selectedTransponderGroup.getTransponders());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void removeTransponder() {
        if (tbTransponder.getSelectedRow() > -1) {
            int result = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Soll der Transponder wirklich aus der Gruppe entfernt werden?",
                    "Transponder entfernen");
            if (result == AKJOptionDialog.OK_OPTION) {
                try {
                    Transponder toDelete = tbMdlTransponder.getDataAtRow(tbTransponder.getSelectedRow());
                    selectedTransponderGroup.getTransponders().remove(toDelete);
                    housingService.saveTransponderGroup(selectedTransponderGroup);

                    tbMdlTransponder.setData(selectedTransponderGroup.getTransponders());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        selectedTransponderGroup = null;
        tbMdlTransponder.setData(null);
        if ((e.getSource() == lsTransponderGroups) && (lsTransponderGroups.getSelectedValue() instanceof TransponderGroup)) {
            selectedTransponderGroup = (TransponderGroup) lsTransponderGroups.getSelectedValue();
            tbMdlTransponder.setData(selectedTransponderGroup.getTransponders());
        }
    }

    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof Transponder) {
            showTransponderDialog((Transponder) selection);
        }
    }

}



