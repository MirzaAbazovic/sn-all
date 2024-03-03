/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2009 08:50:28
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.file.FileFilterExtension;
import de.augustakom.common.tools.file.FileTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorDetails;
import de.augustakom.hurrican.model.cc.equipment.PortGeneratorImport;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PortGeneratorService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog, um Daten fuer eine Port-Generierung zu definieren.
 *
 *
 */
public class PortGenerationDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(PortGenerationDialog.class);

    private static final String RESOURCE_FILE = "de/augustakom/hurrican/gui/hvt/resources/PortGenerationDialog.xml";

    private static final String DIALOG_TITLE = "title";
    private static final String DETAIL_TITLE = "detail.title";
    private static final String HW_BAUGRUPPE = "hw.baugruppe";
    private static final String BG_TYP = "bg.typ";
    private static final String PORT_COUNT = "port.count";
    private static final String IMPORT_TITLE = "import.title";
    private static final String CREATE_COMBI_PHYSIC = "create.combi.physic";
    private static final String IMPORT_FILE = "import.file";
    private static final String FILE_CHOOSER = "file.chooser";

    private AKJTextField tfBaugruppe;
    private AKJTextField tfBgTyp;
    private AKJTextField tfPortCount;
    private AKJCheckBox chbCreateCombi;
    private AKJTextField tfImportFile;

    private PortGeneratorService portGeneratorService;
    private RangierungsService rangierungsService;

    private final HWBaugruppe hwBaugruppe;
    private File importFile;

    /**
     * Konstruktor fuer den Dialog mit Angabe der Baugruppe.
     *
     * @param hwBaugruppe
     */
    public PortGenerationDialog(HWBaugruppe hwBaugruppe) {
        super(RESOURCE_FILE);
        this.hwBaugruppe = hwBaugruppe;
        if ((hwBaugruppe == null) || (hwBaugruppe.getId() == null)) {
            throw new IllegalArgumentException("Keine Baugruppe angegeben oder Baugruppe noch nicht gespeichert!");
        }
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(DIALOG_TITLE));
        AKJLabel lblBaugruppe = getSwingFactory().createLabel(HW_BAUGRUPPE);
        AKJLabel lblBgTyp = getSwingFactory().createLabel(BG_TYP);
        AKJLabel lblPortCount = getSwingFactory().createLabel(PORT_COUNT);
        AKJLabel lblCreateCombi = getSwingFactory().createLabel(CREATE_COMBI_PHYSIC);
        AKJLabel lblImportFile = getSwingFactory().createLabel(IMPORT_FILE);

        tfBaugruppe = getSwingFactory().createTextField(HW_BAUGRUPPE, false);
        tfBgTyp = getSwingFactory().createTextField(BG_TYP, false);
        tfPortCount = getSwingFactory().createTextField(PORT_COUNT, false);
        chbCreateCombi = getSwingFactory().createCheckBox(CREATE_COMBI_PHYSIC);
        tfImportFile = getSwingFactory().createTextField(IMPORT_FILE);
        AKJButton btnChooseFile = getSwingFactory().createButton(FILE_CHOOSER, getActionListener());

        AKJPanel topPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(DETAIL_TITLE));
        topPnl.add(lblBaugruppe, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        topPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        topPnl.add(tfBaugruppe, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        topPnl.add(lblBgTyp, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        topPnl.add(tfBgTyp, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        topPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.NONE));
        topPnl.add(lblPortCount, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        topPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 1, 1, 1, GridBagConstraints.NONE));
        topPnl.add(tfPortCount, GBCFactory.createGBC(100, 0, 6, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel impPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(IMPORT_TITLE));
        impPnl.add(lblCreateCombi, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        impPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        impPnl.add(chbCreateCombi, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        impPnl.add(lblImportFile, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        impPnl.add(tfImportFile, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        impPnl.add(btnChooseFile, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.NONE));
        impPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 4, 2, 1, 1, GridBagConstraints.VERTICAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(topPnl, BorderLayout.NORTH);
        getChildPanel().add(impPnl, BorderLayout.CENTER);

        configureButton(CMD_SAVE, "Ports erstellen", "Generiert die Ports und zeigt sie vor dem Speichern an", true, true);
    }

    @Override
    public final void loadData() {
        try {
            HWService hwService = getCCService(HWService.class);
            portGeneratorService = getCCService(PortGeneratorService.class);
            rangierungsService = getCCService(RangierungsService.class);

            HWRack hwRack = hwService.findRackById(hwBaugruppe.getRackId());

            tfBaugruppe.setText(StringTools.join(new String[] { hwRack.getGeraeteBez(), hwBaugruppe.getModNumber() }, " - ", true));
            tfBgTyp.setText(hwBaugruppe.getHwBaugruppenTyp().getName());
            tfPortCount.setText(hwBaugruppe.getHwBaugruppenTyp().getPortCount());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if (FILE_CHOOSER.equals(command)) {
            final JFileChooser fileChooser = new JFileChooser(tfImportFile.getText(null));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new FileFilterExtension(FileFilterExtension.FILE_EXTENSION_EXCEL));
            fileChooser.setAcceptAllFileFilterUsed(Boolean.FALSE);

            if (fileChooser.showOpenDialog(HurricanSystemRegistry.instance().getMainFrame()) == JFileChooser.APPROVE_OPTION) {
                importFile = fileChooser.getSelectedFile();

                if (importFile != null) {
                    tfImportFile.setText(importFile.getAbsolutePath());
                }
            }
        }
    }

    @Override
    protected void doSave() {
        InputStream fileInputStream = null;
        try {
            if ((importFile == null) || !importFile.exists()) {
                throw new HurricanGUIException("Bitte waehlen Sie eine gueltige Datei aus!");
            }

            PortGeneratorDetails portGenerationDetails = new PortGeneratorDetails();
            portGenerationDetails.setCreatePortsForCombiPhysic(chbCreateCombi.isSelectedBoolean());
            portGenerationDetails.setHwBaugruppenId(hwBaugruppe.getId());

            fileInputStream = new FileInputStream(importFile);

            List<PortGeneratorImport> generatedPorts = portGeneratorService.retrievePorts(fileInputStream, portGenerationDetails);
            if (CollectionTools.isEmpty(generatedPorts)) {
                throw new HurricanGUIException("Das Import-File enthält keine Ports. Es wurden keine Ports generiert!");
            }

            if (confirmNotGeneratedPorts(generatedPorts)) {
                List<Equipment> equipments = portGeneratorService.generatePorts(
                        portGenerationDetails, generatedPorts, HurricanSystemRegistry.instance().getSessionId());

                /*
                    Ports in weiterem Dialog anzeigen;
                    abhaengig vom Result des Dialogs werden die Ports anschliessend gespeichert oder verworfen
                */
                DisplayGeneratedPortsDialog generatedPortsDlg = new DisplayGeneratedPortsDialog(equipments);
                Object result = DialogHelper.showDialog(this, generatedPortsDlg, true, true);
                if ((result instanceof Integer) && (((Integer) result) == JOptionPane.OK_OPTION)) {
                    rangierungsService.saveEquipments(equipments);

                    prepare4Close();
                    setValue(equipments);
                    return;
                }
            }

            prepare4Close();
            setValue(null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            FileTools.closeStreamSilent(fileInputStream);
        }
    }

    /**
     * Fall nicht alle Ports angelegt werden, wird der Benutzer befragt, ob fortgefahren werden soll, auch wenn
     * anzulegende Ports bereits existieren
     *
     * @return {@code true} falls alle Ports generiert werden oder User fortfahren will, sonst {@code false}
     */
    private boolean confirmNotGeneratedPorts(List<PortGeneratorImport> generatedPorts) {
        boolean showMessage = false;
        StringBuilder msg = new StringBuilder("Ports mit der folgenden HW_EQN existieren bereits und werden NICHT angelegt:\n");
        for (PortGeneratorImport portToGenerate : generatedPorts) {
            if (portToGenerate.getPortAlreadyExists()) {
                showMessage = true;
                msg.append("  - ").append(portToGenerate.getHwEqn()).append("\n");
            }
        }
        if (showMessage) {
            int msgResult = MessageHelper.showWarningDialog(this, msg.toString(),
                    "Wollen Sie trotzdem fortfahren?", YES_NO_OPTION);
            return (msgResult == JOptionPane.YES_OPTION);
        }
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /* Dialog zur Darstellung der generierten Ports. */
    private static class DisplayGeneratedPortsDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {
        private final List<Equipment> generatedPorts;
        private AKJTable tbPorts;
        private AKTableModelXML<Equipment> tbMdlPorts;

        public DisplayGeneratedPortsDialog(List<Equipment> generatedPorts) {
            super(null);
            this.generatedPorts = generatedPorts;
            createGUI();
            loadData();
        }

        @Override
        protected final void createGUI() {
            setTitle("generierte Ports");
            tbMdlPorts = new AKTableModelXML<Equipment>("de/augustakom/hurrican/gui/hvt/resources/GeneratedPortsTable.xml");
            tbPorts = new AKJTable(tbMdlPorts, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
            tbPorts.attachSorter();
            tbPorts.fitTable(tbMdlPorts.getFitList());

            AKJScrollPane spPorts = new AKJScrollPane(tbPorts, new Dimension(700, 400));
            getChildPanel().setLayout(new BorderLayout());
            getChildPanel().add(spPorts, BorderLayout.CENTER);

            configureButton(CMD_SAVE, "Speichern", "Speichert die dargestellten Ports", true, true);
        }

        @Override
        public final void loadData() {
            tbMdlPorts.setData(generatedPorts);
        }

        @Override
        protected void execute(String command) {
        }

        @Override
        protected void doSave() {
            prepare4Close();
            setValue(JOptionPane.OK_OPTION);
        }

        @Override
        public void update(Observable o, Object arg) {
        }
    }
}
