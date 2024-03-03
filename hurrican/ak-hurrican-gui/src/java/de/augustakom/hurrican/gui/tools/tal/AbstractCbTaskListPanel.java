/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2011 12:49:44
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.mnet.common.tools.ReflectionTools;
import de.mnet.wita.model.CbTask;

abstract class AbstractCbTaskListPanel<T extends CbTask> extends AbstractServicePanel implements
        AKObjectSelectionListener, AKDataLoaderComponent, AKTableOwner {

    private static final long serialVersionUID = 609591383948905105L;
    private static final Logger LOGGER = Logger.getLogger(AbstractCbTaskListPanel.class);

    private static final String HISTORY = "history";
    private static final String TASK_FREIGEBEN = "task.freigeben";
    private static final String TASK_BEARBEITEN = "task.bearbeiten";
    private static final String ALLE_TASKS = "alle.tasks";
    private static final String EIGENE_TASKS = "eigene.tasks";
    private static final String WIEDERVORLAGE = "wiedervorlage";

    T selectedCbTask;
    private Class<T> currentClass;
    boolean showOnlyEigeneTasks = false;

    AKJButton btnWiedervorlage;
    AKJButton btnTaskBearbeiten;
    AKJButton btnTaskFreigeben;
    private AKJButton btnHistory;
    protected final Set<AKManageableComponent> managedComponents = new HashSet<>();



    AbstractCbTaskListPanel(String resource) {
        super(resource);
        initCurrentClass();
    }

    private void initCurrentClass() {
        this.currentClass = ReflectionTools.getTypeArgument(AbstractCbTaskListPanel.class, this.getClass());
    }

    /**
     * Action, um die Details einer Carrier-Bestellung ueber das Kontext-Menu der Tabelle zu oeffnen.
     */
    private class ShowHistoryAction extends AKAbstractAction {

        private static final long serialVersionUID = -3536713165709865942L;

        ShowHistoryAction() {
            super();
            setName("History...");
            setActionCommand("show.details");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                showCBVorgangHistoryDialog();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    protected final void createGUI() {
        createCbTaskTable();

        getCbTaskTable().attachSorter();
        getCbTaskTable().addTableListener(this);

        AKTableListener tableListener = new AKTableListener(this, false);
        getCbTaskTable().addKeyListener(tableListener);
        getCbTaskTable().addMouseListener(tableListener);

        List<Pair<String, TableCellRenderer>> renderers = createTableCellRenderer();
        setTableCellRenderers(getCbTaskTable(), renderers);
        getCbTaskTable().addPopupAction(new ShowHistoryAction());
        getCbTaskTable().addMouseListener(new AKTableDoubleClickMouseListener(this));
        getCbTaskTable().fitTable(getCbTaskTableSize());

        AKJScrollPane spCBVorgang = new AKJScrollPane(getCbTaskTable(), new Dimension(700, 300));

        AKJPanel btnPnl = createButtonPanel();

        AKJPanel detPnl = createDetailPanel();

        AKJPanel downPnl = new AKJPanel(new BorderLayout());
        downPnl.add(btnPnl, BorderLayout.WEST);
        downPnl.add(detPnl, BorderLayout.CENTER);

        AKJSplitPane split = new AKJSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        split.setTopComponent(spCBVorgang);
        split.setBottomComponent(new AKJScrollPane(downPnl));
        split.setDividerSize(2);
        split.setResizeWeight(1d); // Top-Panel erhaelt komplette Ausdehnung!

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);

        manageGUI(managedComponents);
        updateControls();
    }

    private void setTableCellRenderers(AKJTable cbTaskTable, List<Pair<String, TableCellRenderer>> renderers) {
        for (Pair<String, TableCellRenderer> renderer : renderers) {
            final TableColumn column = cbTaskTable.getColumn(renderer.getFirst());
            if (column != null) {
                column.setCellRenderer(renderer.getSecond());
            }
        }
    }

    /**
     * die abgeleiteten Panels koennen hier die benoetigten Renderer fuer die CbTask-Tabelle zurueckliefern. <br/>
     * Der Default ist eine leere Liste, d.h. es werden keine Renderer benoetigt
     * @return Die Renderer fuer die CbTask-Tabelle
     */
    protected List<Pair<String, TableCellRenderer>> createTableCellRenderer() {
        return Collections.emptyList();
    }

    protected abstract int[] getCbTaskTableSize();

    private AKJPanel createButtonPanel() {
        List<String> toggleButtonCommands = Lists.newArrayList();
        toggleButtonCommands.add(EIGENE_TASKS);
        toggleButtonCommands.add(ALLE_TASKS);
        EmbeddableTamFilter tamFilter = EmbeddableTamFilterBuilder.builder()
                .withActionListener(getActionListener())
                .withSwingFactory(getSwingFactory())
                .withToggleButtonCommands(toggleButtonCommands)
                .withAdditionalFilterComponent(createAdditionalFilterComponent().orElse(null))
                .build();
        AKJPanel filtersPnl = tamFilter.createFilterPanel();

        btnWiedervorlage = getSwingFactory().createButton(WIEDERVORLAGE, getActionListener());
        btnTaskBearbeiten = getSwingFactory().createButton(TASK_BEARBEITEN, getActionListener());
        btnTaskFreigeben = getSwingFactory().createButton(TASK_FREIGEBEN, getActionListener());
        btnHistory = getSwingFactory().createButton(HISTORY, getActionListener());

        managedComponents.addAll(Arrays.asList(btnTaskBearbeiten, btnTaskFreigeben));
        AKJPanel btnPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("functions"));
        int yBtnPnl = 0;
        // @formatter:off
        btnPnl.add(filtersPnl, GBCFactory.createGBC(  0,  0, 0,   yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnWiedervorlage  , GBCFactory.createGBC(  0,  0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        yBtnPnl = createExtraFilterButtons(btnPnl, yBtnPnl);
        btnPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.VERTICAL));
        btnPnl.add(btnTaskBearbeiten , GBCFactory.createGBC(  0,  0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnTaskFreigeben  , GBCFactory.createGBC(  0,  0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        yBtnPnl = createExtraButtons(btnPnl, yBtnPnl);
        btnPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnHistory        , GBCFactory.createGBC(  0,  0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,100, 0, ++yBtnPnl, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on
        return btnPnl;
    }


    /**
     * Zeigt die History zum ausgewählten UserTask an
     */
    private void showCBVorgangHistoryDialog() throws Exception {
        if (selectedCbTask != null) {
            AbstractServiceOptionDialog dlg;
            dlg = getHistoryDialog(selectedCbTask);
            DialogHelper.showDialog(getMainFrame(), dlg, false, true);
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Es ist kein Vorgang ausgewählt!", null, true);
        }
    }

    @Override
    public void objectSelected(Object selection) {
        try {
            if ((selection != null) && currentClass.isAssignableFrom(selection.getClass())) {
                CCAuftragModel auftragModel = getAuftragModelForSelection(currentClass.cast(selection));
                AuftragDataFrame.openFrame(auftragModel);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

    }

    void updateControls() {
        List<T> selected = getSelectedCbTasks();
        boolean someThingSelected = CollectionTools.isNotEmpty(selected);

        boolean atLeastOneTaskIsAlreadyAssigned = atLeastOneTaskHasAlreadyBeenAssigned(selected);
        btnTaskBearbeiten.setEnabled(someThingSelected && !atLeastOneTaskIsAlreadyAssigned);
        btnTaskFreigeben.setEnabled(someThingSelected && atLeastOneTaskIsAlreadyAssigned);

        if (!someThingSelected || (selected.size() != 1)) {
            setCbTaskControlsEnabled(false);
            btnHistory.setEnabled(false);
            clearDetails();
        }
        else { // genau ein Task selektiert
            btnHistory.setEnabled(true);
            if (getLoginCurrentUser().equalsIgnoreCase(selected.get(0).getTaskBearbeiter())) {
                setCbTaskControlsEnabled(true);
            }
            else {
                setCbTaskControlsEnabled(false);
            }
        }

        updateSpecificControls(selected);
    }

    protected void clearDetails() {
        this.selectedCbTask = null;
        GuiTools.cleanFields(this);
    }

    @SuppressWarnings("unchecked")
    List<T> getSelectedCbTasks() {
        List<T> cbTasks = Lists.newArrayList();
        AKTableSorter<T> model = (AKTableSorter<T>) getCbTaskTable().getModel();
        for (int row : getCbTaskTable().getSelectedRows()) {
            cbTasks.add(model.getDataAtRow(row));
        }
        return cbTasks;
    }

    /**
     * Wenn mindestens ein Task bereits in Arbeit ist, Button deaktivieren.
     */
    private boolean atLeastOneTaskHasAlreadyBeenAssigned(List<T> selectedTamVorgaenge) {
        boolean taskIsAlreadyAssigned = false;

        if (CollectionTools.isNotEmpty(selectedTamVorgaenge))
        {
            for (T tamVorgang : selectedTamVorgaenge) {
                if (StringUtils.isNotBlank(tamVorgang.getTaskBearbeiter())) {
                    taskIsAlreadyAssigned = true;
                    break;
                }
            }
        }

        return taskIsAlreadyAssigned;
    }

    /**
     * Mindestens ein selektierter Task muss in Arbeit sein, damit der Button aktiviert wird.
     */
    private void updateBtnTaskFreigeben(List<T> selectedCbTasks) {
        boolean enabled = false;
        for (T task : selectedCbTasks) {
            if (StringUtils.isNotBlank(task.getTaskBearbeiter())) {
                enabled = true;
            }
        }
        btnTaskFreigeben.setEnabled(enabled);
    }

    String getLoginCurrentUser() {
        return HurricanSystemRegistry.instance().getCurrentUser().getLoginName();
    }

    @Override
    public void showDetails(Object detail) {
        clearDetails();
        if ((detail != null) && currentClass.isAssignableFrom(detail.getClass())) {
            try {
                this.selectedCbTask = (currentClass.cast(detail));
                fillDetail(selectedCbTask);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        updateControls();
    }

    /**
     * @param detail Das Object, mit dem das DetailPanel gefüllt werden soll.
     */
    protected abstract void fillDetail(T detail) throws Exception;

    /**
     * Liefert eine Komponente um zusaetzliche Filtermoeglichkeiten anzuzeigen
     */
    protected abstract Optional<AKJComboBox> createAdditionalFilterComponent();


    /**
     * @param command Name der Button, die gedrueckt wurde
     */
    protected void executeSpecific(String command) throws Exception {
        // Do nothing extra by default
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    protected void execute(String command) {
        try {
            if (HISTORY.equals(command)) {
                showCBVorgangHistoryDialog();
            }
            else if (TASK_BEARBEITEN.equals(command)) {
                taskBearbeiten();
            }
            else if (TASK_FREIGEBEN.equals(command)) {
                taskFreigeben();
            }
            else if (EIGENE_TASKS.equals(command)) {
                eigeneTasks();
            }
            else if (ALLE_TASKS.equals(command)) {
                alleTasks();
            }
            else if (WIEDERVORLAGE.equals(command)) {
                wiedervorlage();
            }

            else {
                executeSpecific(command);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
            loadData();
        }
        getCbTaskTable().repaint();
        updateControls();
    }

    protected abstract void taskBearbeiten() throws Exception;

    protected abstract void taskFreigeben() throws Exception;

    protected abstract void eigeneTasks();

    protected abstract void alleTasks();

    protected abstract void wiedervorlage();

    protected abstract int createExtraFilterButtons(AKJPanel btnPnl, int yBtnPnl);

    protected abstract int createExtraButtons(AKJPanel btnPnl, int yBtnPnl);

    protected abstract AKJPanel createDetailPanel();

    protected abstract void createCbTaskTable();

    protected abstract AKJTable getCbTaskTable();

    protected abstract CCAuftragModel getAuftragModelForSelection(T selectedCbTask) throws Exception;

    protected abstract void setCbTaskControlsEnabled(boolean enabled);

    protected abstract AbstractServiceOptionDialog getHistoryDialog(T selectedCbTask) throws Exception;

    protected abstract void updateSpecificControls(List<T> selected);
}
