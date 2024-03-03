/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2010 11:43:36
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.file.FileFilterExtension;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.ImportException;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.impl.equipment.HWBaugruppenChangeDslamProfileReader;


/**
 * Basis-Panel fuer die Erstellung / Bearbeitung von Baugruppen-Schwenks.
 */
public class HWBaugruppenChangePanel extends AbstractServicePanel implements AKDataLoaderComponent,
        AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangePanel.class);

    private static final String CLOSE_CHANGE = "close.change";
    private static final String CANCEL_CHANGE = "cancel.change";
    private static final String NEW_CHANGE = "new.change";
    private static final String PREPARE_CHANGE = "prepare.change";
    private static final String EXECUTE_CHANGE = "execute.change";

    private static final int COLUMN_STANDORT = 0;
    private static final int COLUMN_PHYSIKTYP = 3;

    private AKJTable tbChanges;
    private AKReferenceAwareTableModel<HWBaugruppenChange> tbMdlChanges;
    private AKJPanel bottomPanel;
    private boolean guiCreated = false;

    private HWBaugruppenChangeService hwBaugruppenChangeService;

    private HWBaugruppenChange selectedHwBaugruppenChange;

    public HWBaugruppenChangePanel() {
        super("de/augustakom/hurrican/gui/hvt/hardware/resources/HWBaugruppenChangePanel.xml");
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        tbMdlChanges = new AKReferenceAwareTableModel<>(
                new String[] { "Standort", "Termin", "Typ",
                        "Physiktyp neu", "geplant von", "Status",
                        "ausgefuehrt am", "ausgefuehrt von" },
                new String[] { HWBaugruppenChange.HVT_STANDORT_GRUPPEN_ID, HWBaugruppenChange.PLANNED_DATE, HWBaugruppenChange.CHANGE_TYPE_NAME,
                        HWBaugruppenChange.PHYSIKTYP_NEW_ID, HWBaugruppenChange.PLANNED_FROM, HWBaugruppenChange.CHANGE_STATE_NAME,
                        HWBaugruppenChange.EXECUTED_AT, HWBaugruppenChange.EXECUTED_FROM },
                new Class[] { String.class, Date.class, String.class,
                        String.class, String.class, String.class,
                        Date.class, String.class }
        );
        tbChanges = new AKJTable(tbMdlChanges, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbChanges.attachSorter();
        tbChanges.fitTable(new int[] { 180, 75, 150, 100, 100, 100, 90, 100 });
        tbChanges.addTableListener(this);
        AKJScrollPane spChanges = new AKJScrollPane(tbChanges, new Dimension(700, 120));

        AKJButton btnNew = getSwingFactory().createButton(NEW_CHANGE, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(CANCEL_CHANGE, getActionListener());
        AKJButton btnClose = getSwingFactory().createButton(CLOSE_CHANGE, getActionListener());
        AKJButton btnPrepare = getSwingFactory().createButton(PREPARE_CHANGE, getActionListener());
        AKJButton btnExecute = getSwingFactory().createButton(EXECUTE_CHANGE, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnNew, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE, 10));
        btnPanel.add(btnClose, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE, 10));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnPrepare, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnExecute, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE, 10));

        AKJPanel topPanel = new AKJPanel(new BorderLayout(), getSwingFactory().getText("title.plannings"));
        topPanel.add(spChanges, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        bottomPanel = new AKJPanel(new BorderLayout(), getSwingFactory().getText("title.details"));

        AKJSplitPane splitPane = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);
        splitPane.setDividerSize(2);
        splitPane.setResizeWeight(0d);  // Bottom-Panel erhaelt komplette Ausdehnung!

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        guiCreated = true;
    }

    @Override
    public final void loadData() {
        try {
            if (guiCreated) {
                bottomPanel.removeAll();
            }

            HVTService hvtService = getCCService(HVTService.class);
            hwBaugruppenChangeService = getCCService(HWBaugruppenChangeService.class);
            PhysikService physikService = getCCService(PhysikService.class);

            List<HVTGruppe> hvtGruppen = hvtService.findHVTGruppen();
            tbMdlChanges.addReference(
                    COLUMN_STANDORT, CollectionMapConverter.convert2Map(hvtGruppen, "getId", null), HVTGruppe.ORTSTEIL);

            List<PhysikTyp> physikTypen = physikService.findPhysikTypen();
            tbMdlChanges.addReference(
                    COLUMN_PHYSIKTYP, CollectionMapConverter.convert2Map(physikTypen, "getId", null), PhysikTyp.NAME);

            List<HWBaugruppenChange> openChanges = hwBaugruppenChangeService.findOpenHWBaugruppenChanges();
            tbMdlChanges.setData(openChanges);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if (NEW_CHANGE.equals(command)) {
            createNewHWBaugruppenChange();
        }
        else if (CANCEL_CHANGE.equals(command)) {
            cancelHWBaugruppenChange();
        }
        else if (CLOSE_CHANGE.equals(command)) {
            closeHWBaugruppenChange();
        }
        else if (PREPARE_CHANGE.equals(command)) {
            prepareChange();
        }
        else if (EXECUTE_CHANGE.equals(command)) {
            executeChange();
        }
    }

    /* Oeffnet einen Dialog, um einen neue Planung fuer Baugruppen-Schwenks anzulegen. */
    private void createNewHWBaugruppenChange() {
        CreateHWBaugruppenChangeDialog dlg = new CreateHWBaugruppenChangeDialog();
        Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        if (result instanceof HWBaugruppenChange) {
            tbMdlChanges.addObject((HWBaugruppenChange) result);
        }
    }

    /* Storniert die aktuell selektierte Planung. */
    private void cancelHWBaugruppenChange() {
        try {
            setWaitCursor();
            if (selectedHwBaugruppenChange == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst eine Planung aus!");
            }

            hwBaugruppenChangeService.cancelHWBaugruppenChange(
                    selectedHwBaugruppenChange, HurricanSystemRegistry.instance().getSessionId());

            selectedHwBaugruppenChange.notifyObservers(true);
            MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("cancel.done"), null, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Schliesst die aktuell selektierte Planung / markiert sie als erledigt. */
    private void closeHWBaugruppenChange() {
        try {
            setWaitCursor();
            if (selectedHwBaugruppenChange == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst eine Planung aus!");
            }

            hwBaugruppenChangeService.closeHWBaugruppenChange(
                    selectedHwBaugruppenChange, HurricanSystemRegistry.instance().getSessionId());

            selectedHwBaugruppenChange.notifyObservers(true);
            MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("close.done"), null, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Markiert die aktuelle Planung als vorbereitet. */
    private void prepareChange() {
        try {
            setWaitCursor();

            if (selectedHwBaugruppenChange == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst eine Planung aus!");
            }

            int option = MessageHelper.showYesNoQuestion(this,
                    getSwingFactory().getText("prepare.message"), getSwingFactory().getText("prepare.title"));
            if (option == JOptionPane.YES_OPTION) {
                List<HWBaugruppenChangePort2Port> portMappingsWithInvalidCCs =
                        hwBaugruppenChangeService.prepareHWBaugruppenChange(selectedHwBaugruppenChange);

                if (CollectionTools.isNotEmpty(portMappingsWithInvalidCCs)) {
                    alertInvalidCrossConnections(portMappingsWithInvalidCCs);
                }
                else {
                    selectedHwBaugruppenChange.notifyObservers(true);
                    MessageHelper.showInfoDialog(getMainFrame(), getSwingFactory().getText("prepare.done"), null, true);
                }
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

    /* Zeigt einen Message-Dialog an; die Message enthaelt die Ports mit ungueltigen CrossConnection-Definitionen */
    private void alertInvalidCrossConnections(List<HWBaugruppenChangePort2Port> portMappingsWithInvalidCCs) {
        StringBuilder builder = new StringBuilder();
        builder.append(getSwingFactory().getText("invalid.cross.connections"));
        builder.append(SystemUtils.LINE_SEPARATOR);
        for (HWBaugruppenChangePort2Port port2port : portMappingsWithInvalidCCs) {
            if (port2port.getEquipmentOld() != null) {
                builder.append(port2port.getEquipmentOld().getHwEQN());
                builder.append(SystemUtils.LINE_SEPARATOR);
            }
        }

        MessageHelper.showInfoDialog(getMainFrame(), builder.toString(), null, true);
    }

    /* Fuehrt die Planung aus. */
    private void executeChange() {
        try {
            setWaitCursor();
            if (selectedHwBaugruppenChange == null) {
                throw new HurricanGUIException("Bitte wählen Sie zuerst eine Planung aus!");
            }

            int option = MessageHelper.showYesNoQuestion(this,
                    getSwingFactory().getText("execute.message"), getSwingFactory().getText("execute.title"));
            if (option == JOptionPane.YES_OPTION) {
                hwBaugruppenChangeService.isExecutionAllowed(selectedHwBaugruppenChange);
                Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> dslamProfileMapping = loadDslamProfile();
                AKWarnings warnings = hwBaugruppenChangeService.executeHWBaugruppenChange(
                        selectedHwBaugruppenChange, dslamProfileMapping, HurricanSystemRegistry.instance().getSessionId());
                selectedHwBaugruppenChange.notifyObservers(true);

                final String message = (warnings == null || warnings.isEmpty()) ? getSwingFactory().getText("execute.done") :
                        getSwingFactory().getText("execute.done.with.warnings");
                MessageHelper.showInfoDialog(getMainFrame(), message, (warnings == null) ? "" : warnings.getWarningsAsText(), true);
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

    private Multimap<Pair<Long, Uebertragungsverfahren>, DSLAMProfile> loadDslamProfile()
            throws HurricanGUIException, de.augustakom.common.service.exceptions.ServiceNotFoundException,
            ImportException, FindException {
        boolean changeTypePortConcentration = selectedHwBaugruppenChange
                .isChangeType(HWBaugruppenChange.ChangeType.PORT_CONCENTRATION);
        boolean changeTypeChangeCard = selectedHwBaugruppenChange
                .isChangeType(HWBaugruppenChange.ChangeType.CHANGE_CARD);
        boolean changeTypeMergeCard = selectedHwBaugruppenChange
                .isChangeType(HWBaugruppenChange.ChangeType.MERGE_CARDS);
        if (changeTypePortConcentration || changeTypeChangeCard || changeTypeMergeCard) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("DSLAM-Profil Mapping auswählen");
            fileChooser.addChoosableFileFilter(new FileFilterExtension(FileFilterExtension.FILE_EXTENSION_EXCEL_ALL));
            fileChooser.setAcceptAllFileFilterUsed(Boolean.FALSE);
            if (fileChooser.showOpenDialog(HurricanSystemRegistry.instance().getMainFrame())
                    != JFileChooser.APPROVE_OPTION
                    && changeTypePortConcentration) {
                throw new HurricanGUIException("Keine DSLAM-Profile Mapping Datei ausgewählt.");
            }

            HWBaugruppenChangeDslamProfileReader reader = new HWBaugruppenChangeDslamProfileReader(getCCService(DSLAMService.class));
            return (fileChooser.getSelectedFile() != null)
                    ? reader.loadDslamProfilesFromXls(fileChooser.getSelectedFile())
                    : ArrayListMultimap.<Pair<Long, Uebertragungsverfahren>, DSLAMProfile>create();
        }
        return ArrayListMultimap.create();
    }

    @Override
    public void showDetails(Object details) {
        selectedHwBaugruppenChange = null;
        bottomPanel.removeAll();
        if (details instanceof HWBaugruppenChange) {
            selectedHwBaugruppenChange = (HWBaugruppenChange) details;
            selectedHwBaugruppenChange.deleteObservers();
            selectedHwBaugruppenChange.addObserver(this);

            HWBaugruppenChangeDetailPanel hwBaugruppenChangeDetailPanel = new HWBaugruppenChangeDetailPanel(selectedHwBaugruppenChange);
            bottomPanel.add(hwBaugruppenChangeDetailPanel);
            bottomPanel.revalidate();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof HWBaugruppenChange) {
            int selectedRow = tbChanges.getSelectedRow();
            tbMdlChanges.fireTableDataChanged();
            tbChanges.setRowSelectionInterval(selectedRow, selectedRow);

            showDetails(o);
        }
    }
}


