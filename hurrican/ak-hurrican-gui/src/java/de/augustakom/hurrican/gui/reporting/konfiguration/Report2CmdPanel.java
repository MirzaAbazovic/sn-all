/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.03.2007 15:20:18
 */
package de.augustakom.hurrican.gui.reporting.konfiguration;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.reporting.actions.ShowCommandPropertiesAction;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.reporting.ReportConfigService;


/**
 * Panel fuer die Zuordnung von Commands zu einem ausgewählten Report.
 *
 *
 */
public class Report2CmdPanel extends AbstractAdminPanel implements AKModelOwner,
        AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(Report2CmdPanel.class);

    private AKJButton btnSave = null;
    private AKJButton btnDelete = null;
    private AKJButton btnAddCommand = null;
    private AKJButton btnRemCommand = null;

    private AKJTable tbCommands = null;
    private AKReflectionTableModel<ServiceCommand> tbMdlCommands = null;
    private AKJList lsAssigned = null;
    private DefaultListModel lsMdlAssigned = null;

    private Report detail = null;

    /**
     * Standardkonstruktor
     */
    public Report2CmdPanel() {
        super("de/augustakom/hurrican/gui/reporting/konfiguration/resources/Report2CmdPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        btnDelete = getSwingFactory().createButton("delete.config", getActionListener(), null);
        btnSave = getSwingFactory().createButton("save.config", getActionListener(), null);
        btnAddCommand = getSwingFactory().createButton("add.command", getActionListener(), null);
        btnRemCommand = getSwingFactory().createButton("remove.command", getActionListener(), null);

        tbMdlCommands = new AKReflectionTableModel<ServiceCommand>(
                new String[] { "Name", "Beschreibung", "Klasse" },
                new String[] { "name", "description", "className" },
                new Class[] { String.class, String.class, String.class });
        tbCommands = new AKJTable(tbMdlCommands, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbCommands.attachSorter();
        ShowCommandPropertiesAction propAction = new ShowCommandPropertiesAction(tbCommands);
        propAction.setParentClass(this.getClass());
        tbCommands.addPopupAction(propAction);
        tbCommands.fitTable(new int[] { 150, 250, 250 });
        AKJScrollPane spTable = new AKJScrollPane(tbCommands, new Dimension(670, 400));

        lsMdlAssigned = new DefaultListModel();
        lsAssigned = new AKJList(lsMdlAssigned);
        lsAssigned.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lsAssigned.setCellRenderer(new AKCustomListCellRenderer<>(ServiceCommand.class, ServiceCommand::getName));
        AKJScrollPane spList = new AKJScrollPane(lsAssigned, new Dimension(180, 10));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("assigned.commands")));
        left.add(spList, GBCFactory.createGBC(100, 100, 0, 1, 1, 3, GridBagConstraints.BOTH));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(btnSave, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        mid.add(btnDelete, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));
        mid.add(btnAddCommand, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 5, 2)));
        mid.add(btnRemCommand, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 7, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("available.commands")));
        right.add(spTable, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(left, GBCFactory.createGBC(20, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        center.add(mid, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        center.add(right, GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.BOTH));

        setGuiEnabled(false);

        this.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        try {
            setWaitCursor();

            lsMdlAssigned.removeAllElements();

            // zugeordnete Commands laden
            if (detail != null) {
                ChainService cs = getCCService(ChainService.class);
                List<ServiceCommand> cmds = cs.findServiceCommands4Reference(detail.getId(), Report.class, null);
                if (cmds != null) {
                    for (ServiceCommand cmd : cmds) {
                        lsMdlAssigned.addElement(cmd);
                    }
                }
                // Aktiviere GUI-Elemente, falls Report bereits gespeichert wurde.
                setGuiEnabled(detail.getId() != null);

                // Lösche Auswahl in Tabellen
                tbCommands.clearSelection();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.detail = (model instanceof Report) ? (Report) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("save.config".equals(command)) {
            saveConfig();
        }
        else if ("add.command".equals(command)) {
            addCommand();
        }
        else if ("remove.command".equals(command)) {
            removeCommand();
        }
        else if ("delete.config".equals(command)) {
            deleteCommand();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ReportConfigService rs = getReportService(ReportConfigService.class);
            List<ServiceCommand> commands = rs.findReportCommands();
            tbMdlCommands.setData(commands);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
    }

    /* Speichert die aktuelle Konfiguration. */
    private void saveConfig() {
        if (detail == null) {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte zuerst einen Report auswaehlen!", null, true);
            return;
        }

        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.save.msg"), getSwingFactory().getText("ask.save.title"));
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            setWaitCursor();
            if (detail != null) {
                // zugeordnete Commands auslesen
                List<ServiceCommand> commands = new ArrayList<>();
                for (int i = 0; i < lsMdlAssigned.getSize(); i++) {
                    commands.add((ServiceCommand) lsMdlAssigned.get(i));
                }
                // Commands speichern
                ChainService cs = getCCService(ChainService.class);
                cs.saveCommands4Reference(detail.getId(), Report.class, commands);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Fuegt die selektierten Commands der Liste hinzu. */
    private void addCommand() {
        int[] selection = tbCommands.getSelectedRows();
        AKMutableTableModel tbMdl = (AKMutableTableModel) tbCommands.getModel();
        for (int i = 0; i < selection.length; i++) {
            ServiceCommand rc2Add = (ServiceCommand) tbMdl.getDataAtRow(selection[i]);
            if (!lsAssigned.containsItem("getId", ServiceCommand.class, rc2Add.getId(), true)) {
                lsMdlAssigned.addElement(rc2Add);
            }
        }
    }

    /* Entfernt die selektierten Commands aus der Liste. */
    private void removeCommand() {
        Object[] selection = lsAssigned.getSelectedValues();
        if (selection != null) {
            for (int i = 0; i < selection.length; i++) {
                lsMdlAssigned.removeElement(selection[i]);
            }
        }
    }

    /* Entfernt alle Commands aus der Liste. */
    private void deleteCommand() {
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.delete.msg"), getSwingFactory().getText("ask.delete.title"));
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        else {
            try {
                setWaitCursor();

                // Lösche alle Daten aus View
                lsMdlAssigned.removeAllElements();

                // Speichere in DB
                if (detail != null) {
                    ChainService cs = getCCService(ChainService.class);
                    cs.deleteCommands4Reference(detail.getId(), Report.class);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /*
     * Setzt die Gui-Elemente editierbar
     */
    private void setGuiEnabled(boolean enable) {
        btnSave.setEnabled(enable);
        btnDelete.setEnabled(enable);
        btnAddCommand.setEnabled(enable);
        btnRemCommand.setEnabled(enable);
        tbCommands.setEnabled(enable);
        lsAssigned.setEnabled(enable);
    }


}
