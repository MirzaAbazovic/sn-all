/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2011 11:43:46
 */
package de.augustakom.hurrican.gui.tools.tal;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.Map.*;
import javax.swing.*;
import javax.swing.table.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.TextInputDialog;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.DateTimeTableCellRenderer;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.swing.table.FilterRelation;
import de.augustakom.common.gui.swing.table.FilterRelations;
import de.augustakom.common.gui.swing.table.ReflectionTableMetaData;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByExtOrderNoDialog;
import de.augustakom.hurrican.gui.tools.tal.ioarchive.HistoryByVertragsnummerDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.mnet.wita.model.AbgebendeLeitungenUserTask;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WorkflowInstanceDetailsDto;
import de.mnet.wita.model.WorkflowInstanceDto;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaAdminService;
import de.mnet.wita.service.WitaUsertaskService;

/**
 * Das Panel fuer die Klaerfalle im Wita Workflow
 */
public class WitaKlaerfaellePanel extends AbstractCbTaskListPanel<WorkflowInstanceDto> {

    private static final long serialVersionUID = 908166264500916556L;
    private static final Logger LOGGER = Logger.getLogger(WitaKlaerfaellePanel.class);

    private WitaAdminService witaAdminService;
    private WitaUsertaskService witaUsertaskService;
    private CarrierElTALService carrierElTALService;
    private CCAuftragService auftragService;
    private MwfEntityService mwfEntityService;

    private AKJTable tbKlaerfaelle;
    private AKReferenceAwareTableModel<WorkflowInstanceDto> tbMdlKlaerfaelle;
    private AKReferenceAwareTableModel<Entry<String, Object>> tbMdlVariables;
    private AKReferenceAwareTableModel<de.mnet.wita.model.WorkflowInstanceDetailsDto.ActivityData> tbMdlActivities;

    private AKJButton btnCorrect;
    private AKJButton btnErrorZustand;
    private AKJButton btnErrorWorkflows;
    private boolean showOnlyWorkflowErrors = true;

    private static final String FILTER_NAME = "tasks.filter";
    private static final String TASK_BEARBEITER_COLUMN_NAME = "Bearbeiter";
    private static final String ERROR_TIMESTAMP_COLUMN_NAME = "Fehler seit";
    private static final String START_TIMESTAMP_COLUMN_NAME = "Start";

    private static final String CORRECT = "correct";
    private static final String ERROR_ZUSTAND = "error.zustand.setzen";
    private static final String WORKFLOWS_SUCHEN = "workflows.suchen";
    private static final String ERROR_WORKFLOWS = "error.workflows";

    public WitaKlaerfaellePanel() {
        super("de/augustakom/hurrican/gui/tools/tal/resources/WitaKlaerfaellePanel.xml");
        createGUI();
        initServices();
    }

    @SuppressWarnings("serial")
    @Override
    protected void createCbTaskTable() {
        tbMdlKlaerfaelle = createKlaerfaelleTableModel();

        tbKlaerfaelle =
                new AKJTable(tbMdlKlaerfaelle, JTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    @Override
    protected List<Pair<String, TableCellRenderer>> createTableCellRenderer() {
        List<Pair<String, TableCellRenderer>> renderers = new LinkedList<>();
                renderers.add(new Pair<>(START_TIMESTAMP_COLUMN_NAME, new DateTimeTableCellRenderer()));
                renderers.add(new Pair<>(ERROR_TIMESTAMP_COLUMN_NAME, new DateTimeTableCellRenderer()));
        return renderers;
    }

    @Override
    protected int createExtraFilterButtons(AKJPanel btnPnl, int yBtnPnlIn) {
        int yBtnPnl = yBtnPnlIn;
        AKJButton btnWorkflowsSuchen = getSwingFactory().createButton(WORKFLOWS_SUCHEN, getActionListener());
        btnErrorWorkflows = getSwingFactory().createButton(ERROR_WORKFLOWS, getActionListener());

        // Wiedervorlage wird in Klaerfaellen nicht benoetigt
        btnPnl.remove(btnWiedervorlage);

        // @formatter: off
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.VERTICAL));
        btnPnl.add(btnErrorWorkflows, GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnWorkflowsSuchen, GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter: on

        return yBtnPnl;
    }

    @Override
    protected Optional<AKJComboBox> createAdditionalFilterComponent() {
        return Optional.empty();
    }

    @Override
    protected int createExtraButtons(AKJPanel btnPnl, int yBtnPnlIn) {
        int yBtnPnl = yBtnPnlIn;
        btnCorrect = getSwingFactory().createButton(CORRECT, getActionListener());
        btnErrorZustand = getSwingFactory().createButton(ERROR_ZUSTAND, getActionListener());
        btnErrorZustand.setEnabled(false);

        // @formatter: off
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.VERTICAL));
        btnPnl.add(btnCorrect, GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPnl.add(btnErrorZustand, GBCFactory.createGBC(0, 0, 0, ++yBtnPnl, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter: on

        return yBtnPnl;
    }

    @Override
    protected AKJPanel createDetailPanel() {
        // @formatter:off
        tbMdlVariables = new AKReferenceAwareTableModel<>(
                new String[] { "Variable", "Value" },
                new String[] { "key", "value"},
                new Class[] { String.class, Object.class});
        // @formatter:on
        AKJTable tbVariables = new AKJTable(tbMdlVariables, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVariables.attachSorter();
        tbVariables.fitTable(new int[] { 200, 400 });
        AKJScrollPane spVariables = new AKJScrollPane(tbVariables, new Dimension(500, 200));

        // @formatter:off
        tbMdlActivities = new AKReferenceAwareTableModel<>(
                new String[] { "Id", "Start", "End"},
                new String[] { "id", "startString", "endString"},
                new Class[] { String.class, LocalDateTime.class, LocalDateTime.class});
        // @formatter:on
        AKJTable tbActivities = new AKJTable(tbMdlActivities, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbActivities.attachSorter();
        tbActivities.fitTable(new int[] { 100, 149, 149 });
        AKJScrollPane spActivities = new AKJScrollPane(tbActivities, new Dimension(300, 200));

        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("details"));
        int xDetPnl = 0;
        // @formatter:off
        detPnl.add(spVariables  , GBCFactory.createGBC(100,100, xDetPnl++, 0, 1, 1, GridBagConstraints.BOTH));
        detPnl.add(spActivities , GBCFactory.createGBC(100,  0, xDetPnl++, 0, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
        return detPnl;
    }

    AKReferenceAwareTableModel<WorkflowInstanceDto> createKlaerfaelleTableModel() {
        // @formatter:off
        return new AKReferenceAwareTableModel<>(
                new ReflectionTableMetaData("BusinessKey", "businessKey", String.class),
                new ReflectionTableMetaData("Workflow Typ", "workflow", Enum.class),
                new ReflectionTableMetaData(START_TIMESTAMP_COLUMN_NAME, "start", LocalDateTime.class),
                new ReflectionTableMetaData(ERROR_TIMESTAMP_COLUMN_NAME, "lastErrorTaskStart", LocalDateTime.class),
                new ReflectionTableMetaData(TASK_BEARBEITER_COLUMN_NAME, "taskBearbeiter", String.class));
        }

    @Override
    public final void loadData() {
        tbMdlKlaerfaelle.removeAll();
        try {
            setWaitCursor();
            final SwingWorker<List<WorkflowInstanceDto>, Void> worker = createDataLoader();
            showProgressBar("Daten laden...");
            worker.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private SwingWorker<List<WorkflowInstanceDto>, Void> createDataLoader() {
        return new SwingWorker<List<WorkflowInstanceDto>, Void>() {

            @Override
            public List<WorkflowInstanceDto> doInBackground() throws Exception {
                return witaAdminService.findOpenWorkflowInstances(true);
            }

            @SuppressWarnings("Duplicates")
            @Override
            protected void done() {
                try {
                    tbMdlKlaerfaelle.setData(get());
                }
                catch (Exception e) {
                    LOGGER.error(e, e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                    stopProgressBar();
                }
            }
        };
    }

    private void initServices() {
        try {
            witaAdminService = getCCService(WitaAdminService.class);
            auftragService = getCCService(CCAuftragService.class);
            carrierElTALService = getCCService(CarrierElTALService.class);
            witaUsertaskService = getCCService(WitaUsertaskService.class);
            mwfEntityService = getCCService(MwfEntityService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected AbstractServiceOptionDialog getHistoryDialog(WorkflowInstanceDto selected) throws FindException {
        switch (selected.getWorkflow()) {
            case TAL_ORDER:
                WitaCBVorgang cbVorgang = loadWitaCbVorgang(selected);
                return new HistoryByExtOrderNoDialog(cbVorgang.getCarrierRefNr());
            case ABGEBEND_PV:
                String vertragsnummer = mwfEntityService.findVertragsnummerFor(selected.getBusinessKey());
                return new HistoryByVertragsnummerDialog(vertragsnummer);
            case KUE_DT:
                return new HistoryByExtOrderNoDialog(selected.getBusinessKey());
            default:
                return new HistoryByExtOrderNoDialog(selected.getBusinessKey());
        }
    }

    private WitaCBVorgang loadWitaCbVorgang(WorkflowInstanceDto task) throws FindException {
        WitaCBVorgang example = WitaCBVorgang.createCompletelyEmptyInstance();
        example.setCarrierRefNr(task.getBusinessKey());
        return Iterables.getOnlyElement(carrierElTALService.findCBVorgaengeByExample(example), null);
    }

    @Override
    protected CCAuftragModel getAuftragModelForSelection(WorkflowInstanceDto selected) throws Exception {
        AbgebendeLeitungenUserTask userTask = null;
        switch (selected.getWorkflow()) {
            case TAL_ORDER:
                return loadWitaCbVorgang(selected);
            case ABGEBEND_PV:
                userTask = witaUsertaskService.findAkmPvUserTask(selected.getBusinessKey());
                break;
            case KUE_DT:
                userTask = witaUsertaskService.findKueDtUserTask(selected.getBusinessKey());
                break;
            default:
                break;
        }

        if (userTask != null) {
            if (userTask.getAuftragIds().size() > 1) {

                MessageHelper.showInfoDialog(
                        getMainFrame(),
                        "Achtung! Der Vertragsnummer sind mehrere Aufträge zugeordnet: "
                                + Joiner.on(",").join(userTask.getAuftragIds()), null, true
                );
            }
            try {
                Long maxAuftragId = Collections.max(userTask.getAuftragIds());
                AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragIdTx(maxAuftragId);
                return auftragDaten;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
        throw new HurricanGUIException(
                "Es konnte kein Auftrag gefunden werden. Bitte lassen Sie die Meldung nochmals in den Workflow laufen"
                        + ", damit ein Usertask angelegt wird, über den dann der entsprechende Auftrag ermittelt werden kann."
        );
    }

    @Override
    protected void updateSpecificControls(List<WorkflowInstanceDto> selected) {
        if (showOnlyWorkflowErrors) {
            btnErrorWorkflows.setEnabled(false);
        }
        else {
            btnErrorWorkflows.setEnabled(true);
        }
    }

    @Override
    protected void setCbTaskControlsEnabled(boolean enabled) {
        btnCorrect.setEnabled(enabled);
        if (selectedCbTask == null) {
            btnTaskBearbeiten.setEnabled(false);
            btnTaskFreigeben.setEnabled(false);
            btnErrorZustand.setEnabled(false);
            btnCorrect.setEnabled(false);
        }
        else {
            if (selectedCbTask.isError()) {
                btnCorrect.setEnabled(enabled);
                btnErrorZustand.setEnabled(false);
            }
            else {
                btnTaskBearbeiten.setEnabled(false);
                btnTaskFreigeben.setEnabled(false);
                btnCorrect.setEnabled(false);
                // Task wird nicht über Activity kontrolliert -> immer auf true
                btnErrorZustand.setEnabled(true);
            }
        }
    }

    @Override
    protected AKJTable getCbTaskTable() {
        return tbKlaerfaelle;
    }

    @Override
    protected void fillDetail(final WorkflowInstanceDto instance) throws Exception {
        setWaitCursor();
        tbMdlVariables.removeAll();
        tbMdlActivities.removeAll();
        try {
            final SwingWorker<WorkflowInstanceDetailsDto, Void> worker = new SwingWorker<WorkflowInstanceDetailsDto, Void>() {

                @Override
                public WorkflowInstanceDetailsDto doInBackground() throws Exception {
                    return witaAdminService.getWorkflowInstanceDetails(instance.getBusinessKey());
                }

                @Override
                protected void done() {
                    try {
                        WorkflowInstanceDetailsDto details = get();
                        tbMdlVariables.setData(details.getWorkflowVariables().entrySet());
                        tbMdlActivities.setData(details.getTaskHistory());
                    }
                    catch (Exception e) {
                        LOGGER.error(e, e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        setDefaultCursor();
                    }
                }
            };

            setWaitCursor();
            worker.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void taskBearbeiten() throws Exception {
        AKUser currentUser = HurricanSystemRegistry.instance().getCurrentUser();
        for (WorkflowInstanceDto task : getSelectedCbTasks()) {
            witaAdminService.claimErrorTask(task.getBusinessKey(), currentUser);
            task.setTaskBearbeiter(currentUser.getLoginName());
        }
    }

    @Override
    protected void taskFreigeben() throws Exception {
        for (WorkflowInstanceDto task : getSelectedCbTasks()) {
            witaAdminService.claimErrorTask(task.getBusinessKey(), null);
            task.setTaskBearbeiter(null);
        }
    }

    @Override
    protected void eigeneTasks() {
        filter(true);
    }

    @Override
    protected void alleTasks() {
        filter(false);
    }

    private void errorWorkflows() {
        loadData();
        showOnlyWorkflowErrors = true;
        this.selectedCbTask = null;
        updateControls();
    }

    @Override
    protected void clearDetails() {
        this.selectedCbTask = null;
        tbMdlActivities.setData(null);
        tbMdlVariables.setData(null);
    }

    private void workflowsSuchen() {
        String extOrderNo = MessageHelper.showInputDialog(this.getMainFrame(), "BusinessKey:",
                "Workflow für BusinessKey suchen", JOptionPane.QUESTION_MESSAGE);
        WorkflowInstanceDto instance = null;
        if (StringUtils.isNotBlank(extOrderNo)) {
            try {
                instance = witaAdminService.getWorkflowInstance(extOrderNo);
            }
            catch (Exception e) {
                LOGGER.error(e);
            }
            if (instance == null) {
                MessageHelper.showErrorDialog(
                        getMainFrame(),
                        new HurricanGUIException(String.format("Keinen Workflow mit BusinessKey %s gefunden.",
                                extOrderNo))
                );
                return;
            }
            WorkflowInstanceDto foundTask = witaAdminService.getWorkflowInstance(extOrderNo);
            tbMdlKlaerfaelle.removeAll();
            tbMdlKlaerfaelle.addObject(foundTask);
            tbKlaerfaelle.repaint();
            showOnlyWorkflowErrors = false;
            updateControls();
        }
    }

    private void filter(boolean eigeneTasks) {
        showOnlyEigeneTasks = eigeneTasks;
        tbMdlKlaerfaelle.removeFilter(FILTER_NAME);

        if (eigeneTasks) {
            FilterRelation relation = new FilterRelation(FILTER_NAME, FilterRelations.AND);
            int idx = tbMdlKlaerfaelle.findColumn(TASK_BEARBEITER_COLUMN_NAME);
            relation.addChild(new FilterOperator(FilterOperators.EQ, getLoginCurrentUser(), idx));
            tbMdlKlaerfaelle.addFilter(relation);
        }
        else {
            loadData();
        }

    }

    @Override
    protected void executeSpecific(String command) throws Exception {
        if (ERROR_WORKFLOWS.equals(command)) {
            errorWorkflows();
        }
        else if (WORKFLOWS_SUCHEN.equals(command)) {
            workflowsSuchen();
        }
        else if (CORRECT.equals(command)) {
            correct();
        }
        if (ERROR_ZUSTAND.equals(command)) {
            setErrorZustand();
        }
    }

    @Override
    protected int[] getCbTaskTableSize() {
        return new int[] { 100, 100, 150, 150, 100 };
    }

    private void correct() throws HurricanGUIException {
        if (selectedCbTask == null) {
            throw new HurricanGUIException("Es ist kein Vorgang ausgewählt.");
        }
        WitaKlaerfaelleDialog dialog = new WitaKlaerfaelleDialog(selectedCbTask);
        DialogHelper.showDialog(getMainFrame(), dialog, true, true);

        reloadSelectedTask();
    }

    private void setErrorZustand() throws HurricanGUIException {
        if (selectedCbTask == null) {
            throw new HurricanGUIException("Es ist kein Vorgang ausgewählt.");
        }

        TextInputDialog dlg = new TextInputDialog("Grund für Error-Zustand",
                "Bitte kurz beschreiben, warum Error gesetzt werden soll.", "Grund:");
        Object errorReason = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        if (errorReason instanceof String) {
            int option = MessageHelper.showYesNoQuestion(this,
                    "Soll der aktuelle Workflow wirklich in den Error Zustand gesetzt werden?",
                    "Error Zustand setzten?");
            if (option == JOptionPane.YES_OPTION) {
                WorkflowInstanceDto updatedTask = witaAdminService.setErrorState(selectedCbTask.getBusinessKey(),
                        HurricanSystemRegistry.instance().getCurrentUser(), (String) errorReason);
                if (updatedTask != null) {
                    MessageHelper.showInfoDialog(this, "Der aktuelle Workflow wurde erfolgreich in den Error gesetzt.");
                }
                else {
                    MessageHelper.showInfoDialog(this, "Der aktuelle Workflow konnte nicht in Error gesetzt werden.");
                }
                reloadSelectedTask();
            }
        }
    }

    private void reloadSelectedTask() {
        // Remove old entry
        WorkflowInstanceDto removedTask = null;
        for (WorkflowInstanceDto task : tbMdlKlaerfaelle.getData()) {
            if (task.getBusinessKey().equals(selectedCbTask.getBusinessKey())) {
                removedTask = task;
                break;
            }
        }
        if (removedTask != null) {
            tbMdlKlaerfaelle.removeObject(removedTask);
        }

        // Reload DTO
        WorkflowInstanceDto updatedTask = witaAdminService.getWorkflowInstance(selectedCbTask.getBusinessKey());
        if (updatedTask != null) {
            tbMdlKlaerfaelle.addObject(updatedTask);
        }
        selectedCbTask = null;
    }

    @Override
    protected void wiedervorlage() {
        // TODO AbstractCbTaskListPanel<WorkflowInstanceDto>.wiedervorlage
    }
}
