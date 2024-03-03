/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2005 10:48:00
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.command.ServiceCommand;
import de.augustakom.hurrican.service.cc.ChainService;


/**
 * Panel fuer die Konfiguration einer ServiceChain.
 *
 *
 */
public class ServiceChainConfigPanel extends AbstractServicePanel implements AKDataLoaderComponent,
        AKNavigationBarListener {

    private static final Logger LOGGER = Logger.getLogger(ServiceChainConfigPanel.class);
    private static final long serialVersionUID = 171423638456737185L;

    private AKJComboBox cbChainType = null;
    private ItemListener cbChainTypeItemListener = null;
    private AKJNavigationBar navBar = null;
    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextArea taDesc = null;

    private AKJTable tbCommands = null;
    private AKReflectionTableModel<ServiceCommand> tbMdlCommands = null;
    private AKJList lsAssigned = null;
    private DefaultListModel lsMdlAssigned = null;

    private boolean guiCreated = false;
    private ServiceChain actNavObject = null;

    /**
     * Default-Konstruktor.
     */
    public ServiceChainConfigPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ServiceChainConfigPanel.xml");
        createGUI();
        loadDefaultData();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        cbChainTypeItemListener = new ChainTypeSelectionListener();
        cbChainType = getSwingFactory().createComboBox("chain.type");
        cbChainType.addItemListener(cbChainTypeItemListener);

        AKJButton btnDelete = getSwingFactory().createButton("delete.config", getActionListener(), null);
        AKJButton btnNew = getSwingFactory().createButton("new.config", getActionListener(), null);
        AKJButton btnSave = getSwingFactory().createButton("save.config", getActionListener(), null);
        AKJButton btnAddCommand = getSwingFactory().createButton("add.command", getActionListener(), null);
        AKJButton btnRemCommand = getSwingFactory().createButton("remove.command", getActionListener(), null);
        AKJButton btnMoveUp = getSwingFactory().createButton("move.up", getActionListener(), null);
        AKJButton btnMoveDown = getSwingFactory().createButton("move.down", getActionListener(), null);
        navBar = getSwingFactory().createNavigationBar("chain.nav", this, true, true);

        AKJLabel lblChainType = getSwingFactory().createLabel("chain.type");
        AKJLabel lblId = getSwingFactory().createLabel("chain.id");
        AKJLabel lblName = getSwingFactory().createLabel("chain.name");
        AKJLabel lblDesc = getSwingFactory().createLabel("chain.desc");

        tfId = getSwingFactory().createFormattedTextField("chain.id", false);
        tfName = getSwingFactory().createTextField("chain.name");
        taDesc = getSwingFactory().createTextArea("chain.desc");
        AKJScrollPane spDesc = new AKJScrollPane(taDesc);

        tbMdlCommands = new AKReflectionTableModel<ServiceCommand>(
                new String[] { "Name", "Beschreibung", "Klasse" },
                new String[] { "name", "description", "className" },
                new Class[] { String.class, String.class, String.class });
        tbCommands = new AKJTable(tbMdlCommands, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbCommands.attachSorter();
        tbCommands.fitTable(new int[] { 150, 350, 200 });
        AKJScrollPane spTable = new AKJScrollPane(tbCommands, new Dimension(670, 400));

        lsMdlAssigned = new DefaultListModel();
        lsAssigned = new AKJList(lsMdlAssigned);
        lsAssigned.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsAssigned.setCellRenderer(new AKCustomListCellRenderer<>(ServiceCommand.class, ServiceCommand::getName));
        AKJScrollPane spList = new AKJScrollPane(lsAssigned, new Dimension(180, 10));

        AKJPanel filter = new AKJPanel(new GridBagLayout(), "Chain-Filter");
        filter.add(lblChainType, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filter.add(cbChainType, GBCFactory.createGBC(20, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, 15));
        filter.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(filter, GBCFactory.createGBC(0, 0, 0, 0, 10, 1, GridBagConstraints.HORIZONTAL));
        top.add(btnDelete, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.NONE));
        top.add(btnNew, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.NONE));
        top.add(navBar, GBCFactory.createGBC(0, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblId, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        top.add(tfId, GBCFactory.createGBC(0, 0, 3, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 2, 1, 1, GridBagConstraints.NONE));
        top.add(lblDesc, GBCFactory.createGBC(0, 0, 7, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 8, 2, 1, 1, GridBagConstraints.NONE));
        top.add(spDesc, GBCFactory.createGBC(0, 0, 9, 2, 1, 2, GridBagConstraints.BOTH));
        top.add(lblName, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfName, GBCFactory.createGBC(0, 0, 3, 3, 3, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 10, 5, 1, 1, GridBagConstraints.BOTH));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("assigned.commands")));
        left.add(spList, GBCFactory.createGBC(100, 100, 0, 1, 1, 3, GridBagConstraints.BOTH));
        left.add(btnMoveUp, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        left.add(btnMoveDown, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.NONE));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 3, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(btnSave, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        mid.add(btnAddCommand, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE, new Insets(2, 2, 5, 2)));
        mid.add(btnRemCommand, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("available.commands")));
        right.add(spTable, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(left, GBCFactory.createGBC(20, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        center.add(mid, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        center.add(right, GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);

        guiCreated = true;
        enableFields(false);
    }

    /* Laedt die Default-Daten fuer das Panel */
    private void loadDefaultData() {
        for (String type : ServiceChain.CHAIN_TYPES) {
            cbChainType.addItem(type);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();

            String chainType = cbChainType.getSelectedItem().toString();

            ChainService cs = getCCService(ChainService.class);
            navBar.setData(cs.findServiceChains(chainType));

            //            String cmdType = (StringUtils.equals(chainType, ServiceCommand.COMMAND_TYPE_PHYSIK))
            //                ? ServiceCommand.COMMAND_TYPE_PHYSIK : ServiceCommand.COMMAND_TYPE_VERLAUF_CHECK;
            String cmdType = chainType;
            List<ServiceCommand> serviceCommands = cs.findServiceCommands(cmdType);
            tbMdlCommands.setData(serviceCommands);
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
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        if (!guiCreated) { return; }
        lsMdlAssigned.removeAllElements();
        try {
            if (obj instanceof ServiceChain) {
                enableFields(true);
                actNavObject = (ServiceChain) obj;
                tfId.setValue(actNavObject.getId());
                tfName.setText(actNavObject.getName());
                taDesc.setText(actNavObject.getDescription());

                // zugeordnete Commands laden
                ChainService cs = getCCService(ChainService.class);
                List<ServiceCommand> commands =
                        cs.findServiceCommands4Reference(actNavObject.getId(), ServiceChain.class, null);
                if (commands != null) {
                    for (ServiceCommand cmd : commands) {
                        lsMdlAssigned.addElement(cmd);
                    }
                }
            }
            else {
                enableFields(false);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("delete.config".equals(command)) {
            deleteConfig();
        }
        else if ("new.config".equals(command)) {
            newConfig();
        }
        else if ("save.config".equals(command)) {
            saveConfig();
        }
        else if ("add.command".equals(command)) {
            addCommand();
        }
        else if ("remove.command".equals(command)) {
            removeCommand();
        }
        else if ("move.up".equals(command)) {
            moveUp();
        }
        else if ("move.down".equals(command)) {
            moveDown();
        }
    }

    /* Erstellt eine neue Produkt2Produkt-Konfiguration. */
    private void newConfig() {
        int selType = cbChainType.getSelectedIndex();
        GuiTools.cleanFields(this);

        cbChainType.removeItemListener(cbChainTypeItemListener);
        cbChainType.setSelectedIndex(selType);
        cbChainType.addItemListener(cbChainTypeItemListener);

        lsMdlAssigned.removeAllElements();

        ServiceChain newChain = new ServiceChain();

        navBar.addNavigationObject(newChain);
        navBar.navigateToLast();

        tfName.requestFocus();
    }

    /* Speichert die aktuelle Konfiguration. */
    private void saveConfig() {
        if (actNavObject == null) {
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Bitte zuerst eine ServiceChain-Konfiguration anlegen/auswaehlen!", null, true);
            return;
        }

        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.save.msg"), getSwingFactory().getText("ask.save.title"));
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            setWaitCursor();
            ChainService cs = getCCService(ChainService.class);
            actNavObject.setName(tfName.getText());
            actNavObject.setType(cbChainType.getSelectedItem().toString());
            actNavObject.setDescription(taDesc.getText());

            cs.saveServiceChain(actNavObject);
            tfId.setValue(actNavObject.getId());

            // zugeordnete Commands auslesen
            List<ServiceCommand> commands = new ArrayList<>();
            for (int i = 0, x = lsMdlAssigned.getSize(); i < x; i++) {
                commands.add((ServiceCommand) lsMdlAssigned.get(i));
            }
            cs.saveCommands4Reference(actNavObject.getId(), ServiceChain.class, commands);
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
            ServiceCommand sc2Add = (ServiceCommand) tbMdl.getDataAtRow(selection[i]);
            if (!lsAssigned.containsItem("getId", ServiceCommand.class, sc2Add.getId(), true)) {
                lsMdlAssigned.addElement(sc2Add);
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

    /* Verschiebt das selektierte Command nach oben. */
    private void moveUp() {
        int index = lsAssigned.getSelectedIndex();
        lsAssigned.switchItems(index, --index);
    }

    /* Verschiebt das selektierte Command nach unten. */
    private void moveDown() {
        int index = lsAssigned.getSelectedIndex();
        lsAssigned.switchItems(index, ++index);
    }

    /* Loescht die aktuell ausgewaehlte Konfiguration. */
    private void deleteConfig() {
        if (actNavObject != null) {
            if (actNavObject.getId() != null) {
                int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                        getSwingFactory().getText("ask.delete.msg"), getSwingFactory().getText("ask.delete.title"));
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                try {
                    ChainService cs = getCCService(ChainService.class);
                    cs.deleteServiceChain(actNavObject.getId());

                    loadData();
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
            else {
                navBar.removeNavigationObject(navBar.getNavPosition());
            }
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* Setzt die Felder 'tfName' und 'taDesc' enabled/disabled. */
    private void enableFields(boolean enable) {
        if (enable) {
            GuiTools.enableComponents(new Component[] { tfName, taDesc });
        }
        else {
            GuiTools.disableComponents(new Component[] { tfName, taDesc });
        }
    }

    /* ComboBox-Listener fuer die Auswahl des Chain-Types. */
    class ChainTypeSelectionListener implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if ((e.getSource() == cbChainType) && (e.getStateChange() == ItemEvent.SELECTED)) {
                loadData();
            }
        }
    }
}


