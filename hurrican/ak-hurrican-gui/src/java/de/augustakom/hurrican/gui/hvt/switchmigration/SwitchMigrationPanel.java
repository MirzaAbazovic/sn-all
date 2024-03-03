/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2011 14:11:37
 */
package de.augustakom.hurrican.gui.hvt.switchmigration;

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKProgressMonitorWithoutCancel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.hvt.switchmigration.util.DialNumberUtil;
import de.augustakom.hurrican.gui.hvt.switchmigration.worker.DialNumberCountWorker;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.SwitchMigrationLog;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationSearchCriteria;
import de.augustakom.hurrican.model.shared.view.SwitchMigrationView;
import de.augustakom.hurrican.service.billing.RufnummerService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.ProduktService;

public class SwitchMigrationPanel extends AbstractServicePanel {

    private static final Logger LOGGER = Logger.getLogger(SwitchMigrationPanel.class);

    private static final String TB_ORDERS = "tb.orders";
    private static final String TB_DIALNUMBERCOUNT = "tb.dialnumbercount";
    private static final String DIALNUMBERCOUNT_TEMPLATE = "Anzahl ausgewählter Rufnummern: {0}";
    private static final String SELECT_ALL_ORDERS = "select.all.orders";
    private static final String DC_CPS_EXECUTE_DATE = "dc.cps.execute.date";
    private static final String DC_CPS_EXECUTE_TIME = "dc.cps.execute.time";
    private static final String CB_CPS_EXECUTE_NOW = "cb.cps.execute.now";
    private static final String BTN_EXECUTE_MIGRATION = "btn.execute.migration";
    private static final String BTN_SEARCH = "btn.search";
    private static final int COLUMN_NO_SELECTION = 0;
    public static final long MAX_SEARCH_LIMIT = 1000L;
    /* Begrenzung der möglichen IN-Argumnete in Oracle */
    public static final long MAX_SIZE_IN_ARGUMENTS = 1000L;

    private AKJComboBox cbSwitchFrom;
    private AKJComboBox cbSwitchTo;
    private AKJTextField tfBillingOrderNo;
    private AKJDateComponent dcBaDatumVon;
    private AKJDateComponent dcBaDatumBis;
    private AKJComboBox cbProducts;
    private AKJDateComponent dcInbetriebnahmedatumVon;
    private AKJDateComponent dcInbetriebnahmedatumBis;
    private AKJButton btnSearch;
    private AKJButton btnExecuteMigration;
    private AKJDateComponent dcCPSExecuteDate;
    private AKJDateComponent dcCPSExecuteTime;
    private AKJFormattedTextField tfSearchLimit;
    private AKJCheckBox cbCPSExecuteNow;
    private AKJCheckBox cbSelectAllOrders;
    private AKJLabel lblDialNumCount;

    private AKReflectionTableModel<SwitchMigrationViewObject> tbMdlOrders;
    private List<SwitchMigrationViewObject> switchMigrationViews;

    private HWSwitchService hwSwitchService;
    private ProduktService produktService;

    public SwitchMigrationPanel() {
        super("de/augustakom/hurrican/gui/hvt/switchmigration/resources/SwitchMigrationPanel.xml");
        initServices();
        createGUI();
        loadData();
    }

    private void initServices() {
        try {
            hwSwitchService = getCCService(HWSwitchService.class);
            produktService = getCCService(ProduktService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @return Returns all the switchMigrationViews.
     */
    private List<SwitchMigrationView> getSelectedSwitchMigrationViews() {
        return (switchMigrationViews == null)
                ? Collections.emptyList()
                : switchMigrationViews.stream()
                .filter(SwitchMigrationViewObject::getSelected)
                .map(SwitchMigrationViewObject::getSwitchMigrationView)
                .collect(Collectors.toList());
    }

    private ListCellRenderer createHwSwitchRenderer() {
        return new AKCustomListCellRenderer<>(HWSwitch.class, HWSwitch::getName);
    }

    @Override
    protected final void createGUI() {
        tbMdlOrders = new AKReflectionTableModel<SwitchMigrationViewObject>(
                new String[] { "", "Techn. AuftragNr.", "Billing AuftragNr.", "VBZ", "Produkt", "Status",
                        "BA Realisierung", "Inbetriebnahme", "Switch", "Standort", "Anzahl Rufnummern" },
                new String[] { "selected", "auftragId", "billingAuftragId", "vbz", "produkt", "auftragStatus",
                        "baRealisierung", "inbetriebnahme", "switchKennung", "techLocation", "dialNumberCount" },
                new Class[] { Boolean.class, Long.class, Long.class, String.class, String.class, String.class,
                        Date.class, Date.class, String.class, String.class, Integer.class }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return (column == COLUMN_NO_SELECTION);
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == COLUMN_NO_SELECTION && aValue instanceof Boolean) {
                    SwitchMigrationViewObject rowData = getDataAtRow(row);
                    rowData.setSelected((Boolean) aValue);
                }
                super.setValueAt(aValue, row, column);
                setDialNumberCount();
            }
        };

        this.setLayout(new GridBagLayout());
        // @formatter:off
        this.add(createSearchPanel(),    GBCFactory.createGBC(  0,   0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(createRightPanel(),     GBCFactory.createGBC(100, 100, 4, 0, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on


        cbSwitchTo.setEnabled(false);
        btnExecuteMigration.setEnabled(false);
        cbSelectAllOrders.setEnabled(false);
    }

    private void setDialNumberCount(){
        setDialNumberCount(DialNumberUtil.calculateDialNumberSum(tbMdlOrders.getData()));
    }

    private void setDialNumberCount(int dialNumberCount) {
        lblDialNumCount.setText(MessageFormat.format(DIALNUMBERCOUNT_TEMPLATE,
                dialNumberCount));
    }

    private AKJPanel createRightPanel() {
        AKJTable tbOrders = new AKJTable(tbMdlOrders, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbOrders.attachSorter();
        tbOrders.fitTable(new int[] { 30, 80, 80, 90, 110, 150, 90, 90, 60, 150 });
        AKJScrollPane spOrders = new AKJScrollPane(tbOrders, new Dimension(950, 600));

        AKJPanel rightPanel = new AKJPanel(new GridBagLayout());

        AKJLabel lblOrders = getSwingFactory().createLabel(TB_ORDERS);
        lblDialNumCount = getSwingFactory().createLabel(TB_DIALNUMBERCOUNT);
        AKJLabel lblSelectAllOrders = getSwingFactory().createLabel(SELECT_ALL_ORDERS);
        cbSelectAllOrders = getSwingFactory().createCheckBox(SELECT_ALL_ORDERS, event -> {
            Boolean selected = cbSelectAllOrders.isSelected();
            if (tbMdlOrders.getData() != null) {
                tbMdlOrders.getData().stream().forEach(order -> order.setSelected(selected));
                setDialNumberCount();
                tbMdlOrders.fireTableDataChanged();
            }
        }, true);

        AKJLabel lblSwitchTo = getSwingFactory().createLabel("switch.to");
        cbSwitchTo = getSwingFactory().createComboBox("switch.to", createHwSwitchRenderer());
        cbSwitchTo.setSize(new Dimension(120, 25));

        AKJLabel lblCPSExecute = getSwingFactory().createLabel(DC_CPS_EXECUTE_DATE);
        AKJLabel lblCPSExecuteNow = getSwingFactory().createLabel(CB_CPS_EXECUTE_NOW);
        cbCPSExecuteNow = getSwingFactory().createCheckBox(CB_CPS_EXECUTE_NOW, event -> {
            // if checkbox is selected, disable date and time picker!
            enableCPSDateTimeFields(!cbCPSExecuteNow.isSelected());
        }, true);
        cbCPSExecuteNow.setEnabled(false);
        dcCPSExecuteDate = getSwingFactory().createDateComponent(DC_CPS_EXECUTE_DATE, true);
        dcCPSExecuteTime = getSwingFactory().createDateComponent(DC_CPS_EXECUTE_TIME, false);
        enableCPSDateTimeFields(false);

        btnExecuteMigration = getSwingFactory().createButton(BTN_EXECUTE_MIGRATION, getActionListener());

        //@formatter:off
        int y = 0;
        rightPanel.add(lblDialNumCount,     GBCFactory.createGBC(100,   0, 5,   y, 5, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(lblOrders,           GBCFactory.createGBC(100,   0, 0, y++, 5, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(cbSelectAllOrders,   GBCFactory.createGBC(0  ,   0, 0,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(lblSelectAllOrders,  GBCFactory.createGBC(0  ,   0, 1, y++, 4, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(spOrders,            GBCFactory.createGBC(100, 100, 0, y++, 7, 1, GridBagConstraints.BOTH));
        rightPanel.add(new AKJPanel(),      GBCFactory.createGBC(0  ,   0, 0, y++, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(new AKJPanel(),      GBCFactory.createGBC(60 ,   0, 0,   y, 2, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(lblSwitchTo,         GBCFactory.createGBC(10 ,   0, 2,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(cbSwitchTo,          GBCFactory.createGBC(30 ,   0, 3, y++, 4, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(lblCPSExecute,       GBCFactory.createGBC(0  ,   0, 2,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(cbCPSExecuteNow,     GBCFactory.createGBC(0  ,   0, 3,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(lblCPSExecuteNow,    GBCFactory.createGBC(10 ,   0, 4,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(dcCPSExecuteDate,    GBCFactory.createGBC(10 ,   0, 5,   y, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(dcCPSExecuteTime,    GBCFactory.createGBC(10 ,   0, 6, y++, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPanel.add(btnExecuteMigration, GBCFactory.createGBC(0  ,   0, 3,   y, 4, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on
        return rightPanel;
    }

    private AKJPanel createSearchPanel() {
        AKJPanel leftPanel = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("search.criteria"));
        AKJLabel lblSwitchFrom = getSwingFactory().createLabel("switch.from");
        cbSwitchFrom = getSwingFactory().createComboBox("switch.from", createHwSwitchRenderer());
        cbSwitchFrom.setSize(new Dimension(120, 25));
        leftPanel.add(lblSwitchFrom, GBCFactory.createGBC(0, 0, 0, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        leftPanel.add(cbSwitchFrom, GBCFactory.createGBC(0, 0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));

        AKJLabel lblBillingOrderNo = getSwingFactory().createLabel("billing.auftrag.id");
        tfBillingOrderNo = getSwingFactory().createTextField("billing.auftrag.id", true);
        leftPanel.add(lblBillingOrderNo, GBCFactory.createGBC(0, 0, 0, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        leftPanel.add(tfBillingOrderNo, GBCFactory.createGBC(0, 0, 0, 3, 2, 1, GridBagConstraints.HORIZONTAL));

        AKJLabel lblProducts = getSwingFactory().createLabel("products");
        cbProducts = getSwingFactory().createComboBox("products", new AKCustomListCellRenderer<>(Produkt.class, Produkt::getAnschlussart));
        cbProducts.setSize(new Dimension(120, 25));
        leftPanel.add(lblProducts, GBCFactory.createGBC(0, 0, 0, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        leftPanel.add(cbProducts, GBCFactory.createGBC(0, 0, 0, 5, 2, 1, GridBagConstraints.HORIZONTAL));

        AKJLabel lblBaDatumVon = getSwingFactory().createLabel("ba.datum");
        leftPanel.add(lblBaDatumVon, GBCFactory.createGBC(0, 0, 0, 6, 2, 1, GridBagConstraints.HORIZONTAL));
        dcBaDatumVon = getSwingFactory().createDateComponent("ba.datum.von");
        leftPanel.add(dcBaDatumVon, GBCFactory.createGBC(50, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        dcBaDatumBis = getSwingFactory().createDateComponent("ba.datum.bis");
        leftPanel.add(dcBaDatumBis, GBCFactory.createGBC(50, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJLabel lblStartdatumVon = getSwingFactory().createLabel("inbetriebnahme");
        leftPanel.add(lblStartdatumVon, GBCFactory.createGBC(0, 0, 0, 8, 2, 1, GridBagConstraints.HORIZONTAL));
        dcInbetriebnahmedatumVon = getSwingFactory().createDateComponent("inbetriebnahme.von");
        leftPanel.add(dcInbetriebnahmedatumVon, GBCFactory.createGBC(50, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        dcInbetriebnahmedatumBis = getSwingFactory().createDateComponent("inbetriebnahme.bis");
        leftPanel.add(dcInbetriebnahmedatumBis, GBCFactory.createGBC(50, 0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJLabel lblSearchLimit = getSwingFactory().createLabel("search.limit");
        tfSearchLimit = getSwingFactory().createFormattedTextField("search.limit", true);
        tfSearchLimit.setValue(MAX_SEARCH_LIMIT);
        tfSearchLimit.setToolTipText(String.format("Die Suche wird auf die eingestellte Maximalanzahl begrenzt (max %d)", MAX_SEARCH_LIMIT));
        tfSearchLimit.addPropertyChangeListener("value", evt -> tfSearchLimit.setValue(getValidSearchLimit((Long) evt.getNewValue())));
        leftPanel.add(lblSearchLimit, GBCFactory.createGBC(0, 0, 0, 10, 2, 1, GridBagConstraints.HORIZONTAL));
        leftPanel.add(tfSearchLimit, GBCFactory.createGBC(0, 0, 0, 11, 2, 1, GridBagConstraints.HORIZONTAL));

        leftPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        btnSearch = getSwingFactory().createButton(BTN_SEARCH, getActionListener());
        leftPanel.add(btnSearch, GBCFactory.createGBC(0, 0, 0, 13, 1, 1, GridBagConstraints.HORIZONTAL));


        leftPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 14, 2, 1, GridBagConstraints.VERTICAL));
        return leftPanel;
    }

    private void loadData() {
        try {
            List<HWSwitch> switches = hwSwitchService.findSwitchesByType(HWSwitchType.IMS_OR_NSP);
            cbSwitchFrom.addItems(switches);
            cbSwitchTo.addItems(switches);

            List<Produkt> products = produktService
                    .findProductsByTechLeistungType(TechLeistung.TYP_VOIP, TechLeistung.TYP_VOIP_ADD);
            Produkt emptyProduct = new Produkt() {
                @Override
                public String getAnschlussart() {
                    return " ";
                }
            };
            cbProducts.addItem(emptyProduct);
            cbProducts.addItems(products);

            Date nextWorkDay = createDateNextWorkday();
            dcCPSExecuteDate.setDate(nextWorkDay);
            dcCPSExecuteTime.setDate(nextWorkDay);
            cbSwitchTo.setEnabled(false);
            btnExecuteMigration.setEnabled(false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * erzeugt ein {@link Date} fuer das naechste Wartungsfenster (2 Uhr).
     */
    private Date createDateNextWorkday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTools.plusWorkDays(1));
        return DateTools.createDate(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 2, 0);
    }

    private HWSwitch getSourceSwitchFromCombobox() {
        return (HWSwitch) this.cbSwitchFrom.getSelectedItem();
    }

    private HWSwitch getDestinationSwitchFromCombobox() {
        return (HWSwitch) this.cbSwitchTo.getSelectedItem();
    }

    @Override
    @SuppressWarnings("squid:S1186")
    public void update(Observable o, Object arg) {
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        if (StringUtils.equals(BTN_SEARCH, command)) {
            searchOrders();
        }
        else if (StringUtils.equals(BTN_EXECUTE_MIGRATION, command)) {
            if (getSelectedSwitchMigrationViews().isEmpty()) {
                MessageHelper.showMessageDialog(this, "Kein Auftrag selektiert, bitte mindestens einen Auftrag selektieren!");
            }
            else if (getSourceSwitchFromCombobox().equals(getDestinationSwitchFromCombobox())) {
                MessageHelper.showMessageDialog(this, "Alter und neuer Switch dürfen nicht identisch sein!");
            }
            else {
                Date execDateAndTime = checkExecutionDateAndTime();
                if (execDateAndTime != null) {
                    executeMigration(execDateAndTime);
                }
            }
        }
    }

    private void executeMigration(Date execDateAndTime) {
        try {
            String message = "Sollen die ausgewählten Aufträge migriert werden?";
            int result = MessageHelper.showConfirmDialog(this, message, "Migration durchführen?",
                    AKJOptionDialog.YES_NO_OPTION, AKJOptionDialog.QUESTION_MESSAGE);

            if (result != AKJOptionDialog.YES_OPTION) {
                return;
            }

            try {
                setWaitCursor();
                if (!validateForCertifiedSwitches()) {
                    return;
                }
            }
            finally {
                setDefaultCursor();
            }

            final ProgressMonitor progressMonitor = new AKProgressMonitorWithoutCancel(this, "Aufträge migrieren", "", 0, 100);
            progressMonitor.setMillisToPopup(0);
            progressMonitor.setMillisToDecideToPopup(0);

            final SwingWorker<byte[], Integer> worker = new SwingWorker<byte[], Integer>() {
                @Override
                protected byte[] doInBackground() throws Exception {
                    final Long sessionId = HurricanSystemRegistry.instance().getSessionId();
                    final SwitchMigrationLog migrationLog = hwSwitchService.createMigrationLog(
                            getSourceSwitchFromCombobox(), getDestinationSwitchFromCombobox(),
                            sessionId, execDateAndTime);

                    final List<SwitchMigrationView> migrationViews = getSelectedSwitchMigrationViews();
                    int processedCount = 0;
                    setProgress(processedCount);
                    final int totalCount = migrationViews.size();
                    for (SwitchMigrationView switchMigrationView : migrationViews) {
                        processedCount++;
                        publish(processedCount);
                        setProgress(100 * processedCount / totalCount);
                        hwSwitchService.moveOrderToSwitch(switchMigrationView, migrationLog, sessionId);
                    }
                    return hwSwitchService.createMigrationLogXls(migrationLog);
                }

                @Override
                protected void process(List<Integer> list) {
                    Collections.reverse(list);
                    final Integer last = list.stream().findFirst().orElse(0);
                    progressMonitor.setNote(String.format("%s von %s", last,
                            getSelectedSwitchMigrationViews().size()));
                }

                @Override
                protected void done() {
                    try {
                        final byte[] xlsBytes = get();
                        File xlsFile = new File(System.getProperty("user.home"), "SwitchMigration_"
                                + DateTools.formatDate(new Date(), DateTools.PATTERN_DATE_TIME_FULL_CHAR14) + ".xlsx");
                        try (FileOutputStream stream = new FileOutputStream(xlsFile)) {
                            stream.write(xlsBytes);
                        }
                        MessageHelper.showInfoDialog(SwitchMigrationPanel.this,
                                String.format("Switch Migration abgeschlossen!\n Bitte Protokolldatei '%s' auf eventuelle Fehler prüfen!",
                                        xlsFile.getCanonicalPath()));

                        //Restart Session
                        switchMigrationViews = null;
                        tbMdlOrders.setData(null);
                        setInputComponentsEnabled(true);
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

            worker.addPropertyChangeListener(event -> {
                if ("progress".equals(event.getPropertyName())) {
                    int progress = (Integer) event.getNewValue();
                    progressMonitor.setProgress(progress);
                }
            });

            setWaitCursor();
            setExecComponentsEnabled(false);
            setInputComponentsEnabled(false);
            worker.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void setInputComponentsEnabled(boolean enabled) {
        cbSwitchFrom.setEnabled(enabled);
        tfBillingOrderNo.setEnabled(enabled);
        cbProducts.setEnabled(enabled);
        dcBaDatumVon.setEnabled(enabled);
        dcBaDatumBis.setEnabled(enabled);
        dcInbetriebnahmedatumVon.setEnabled(enabled);
        dcInbetriebnahmedatumBis.setEnabled(enabled);
        tfSearchLimit.setEnabled(enabled);
        btnSearch.setEnabled(enabled);
    }

    private boolean validateForCertifiedSwitches() throws ServiceNotFoundException {
        final List<Long> auftragIds = getSelectedSwitchMigrationViews().stream()
                .map(SwitchMigrationView::getAuftragId)
                .collect(Collectors.toList());
        final AKWarnings warnings =
                hwSwitchService.checkAuftraegeForCertifiedSwitchesAsWarnings(auftragIds, getDestinationSwitchFromCombobox());

        if (warnings.isNotEmpty()) {
            int continueProcessing = MessageHelper.showYesNoQuestion(getMainFrame(),
                    warnings.getWarningsAsText() + "\nWollen Sie trotzdem weitermachen?", "Warnung");
            return continueProcessing == AKJOptionDialog.YES_OPTION;
        }

        return true;
    }

    private List<SwitchMigrationViewObject> createSwitchMigrationViews(SwitchMigrationSearchCriteria searchCriteria)
            throws ServiceNotFoundException {
        HWService hwService = getCCService(HWService.class);
        return hwService.createSwitchMigrationViews(searchCriteria)
                .stream()
                .map(SwitchMigrationViewObject::new)
                .collect(Collectors.toList());
    }

    private void searchOrders() {
        String billingAuftragIds = tfBillingOrderNo.getText(null);
        List<Integer> auftragIdList = null;
        try {
            if (billingAuftragIds != null) {
                try {
                    auftragIdList = toIntegerList(billingAuftragIds, ";");
                } catch (NumberFormatException nfe) {
                    MessageHelper.showMessageDialog(getParent(), "Billing Auftragsnummer(n) nicht numerisch oder nicht mit ';' getrennt.");
                    return;
                }

                if (MAX_SIZE_IN_ARGUMENTS < auftragIdList.size()) {
                    MessageHelper.showMessageDialog(getParent(), MessageFormat.format("Es können maximal {0} ({1} eingegeben) Billing Auftragsnummern verarbeitet werden.",MAX_SIZE_IN_ARGUMENTS, auftragIdList.size() ));
                    return;
                }
            }
            setDialNumberCount(0);
            btnSearch.setEnabled(false);
            setExecComponentsEnabled(false);
            tbMdlOrders.setData(null);
            setWaitCursor();
            showProgressBar("laden...");

            final SwingWorker<List<SwitchMigrationViewObject>, Void> worker = new SwingWorker<List<SwitchMigrationViewObject>, Void>() {
                @Override
                protected List<SwitchMigrationViewObject> doInBackground() throws Exception {
                    SwitchMigrationSearchCriteria searchCriteria = new SwitchMigrationSearchCriteria();
                    String billingAuftragIds = tfBillingOrderNo.getText(null);
                    List<Integer> auftragIdList = null;
                    if (billingAuftragIds != null) {
                        auftragIdList = toIntegerList(billingAuftragIds, ";");
                    }
                    searchCriteria.setBillingAuftragIdList(auftragIdList != null ? auftragIdList : null);

                    Produkt produkt = (Produkt) cbProducts.getSelectedItem();
                    searchCriteria.setProdId(produkt != null && produkt.getProdId() != null ? produkt.getProdId() : null);
                    searchCriteria.setBaRealisierungVon(dcBaDatumVon.getDate(null));
                    searchCriteria.setBaRealisierungBis(dcBaDatumBis.getDate(null));
                    searchCriteria.setInbetriebnahmeVon(dcInbetriebnahmedatumVon.getDate(null));
                    searchCriteria.setInbetriebnahmeBis(dcInbetriebnahmedatumBis.getDate(null));
                    searchCriteria.setHwSwitch(getSourceSwitchFromCombobox());
                    searchCriteria.setLimit(getSearchLimit());
                    return createSwitchMigrationViews(searchCriteria);
                }

                @Override
                protected void done() {
                    try {
                        switchMigrationViews = get();
                        if (CollectionTools.isEmpty(switchMigrationViews)) {
                            MessageHelper.showMessageDialog(getParent(), "Es wurden keine Aufträge gefunden.");
                            resetLoadingComponents();
                        }
                        else {
                            tbMdlOrders.setData(switchMigrationViews);
                            new DialNumberCountWorker(
                                    getBillingService(RufnummerService.class),
                                    tbMdlOrders,
                                    () -> {
                                        btnSearch.setEnabled(false);
                                        setExecComponentsEnabled(false);
                                    },
                                    () -> {
                                        setDialNumberCount();
                                        setExecComponentsEnabled(true);
                                        resetLoadingComponents();
                                    }).execute();
                        }
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                        resetLoadingComponents();
                    }
                }

            };
            worker.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private List<Integer> toIntegerList(String input, String delimiter) {
        if (input == null) {
            return null;
        }
        return Arrays.stream(input.split(delimiter)).map(Integer::valueOf).collect(Collectors.toList());
    }

    private void resetLoadingComponents() {
        btnSearch.setEnabled(true);
        stopProgressBar();
        setDefaultCursor();
    }

    private Date checkExecutionDateAndTime() {
        Date execDate = (cbCPSExecuteNow.isSelected()) ? DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH) : null;
        if (execDate == null) {
            // ExecutionTime aus Datum/Zeit erstellen
            execDate = dcCPSExecuteDate.getDate(new Date());
            Date time = dcCPSExecuteTime.getDate(new Date());
            GregorianCalendar timeCal = new GregorianCalendar();
            timeCal.setTime(time);

            GregorianCalendar at = new GregorianCalendar();
            at.setTime(execDate);
            at.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            at.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));

            execDate = at.getTime();

            //check date and time before now!
            if (execDate.before(new Date())) {
                MessageHelper.showMessageDialog(this, "Bitte definieren Sie ein gueltiges CPS-Realisierungs-Zeitpunkt (>= jetzt)");
                return null;
            }
        }
        //check only the date not the time!
        else if (DateTools.isDateBefore(execDate, new Date())) {
            MessageHelper.showMessageDialog(this, "Bitte definieren Sie ein gueltiges CPS-Realisierungs-Datum (>= heute)");
            return null;
        }
        return execDate;
    }

    private void setExecComponentsEnabled(boolean enabled) {
        cbSwitchTo.setEnabled(enabled);
        btnExecuteMigration.setEnabled(enabled);
        cbSelectAllOrders.setEnabled(enabled);
        cbCPSExecuteNow.setEnabled(enabled);
        if (!enabled) {
            //reset some fields
            enableCPSDateTimeFields(false);
            cbSelectAllOrders.setSelected(true);
        }
    }

    private void enableCPSDateTimeFields(boolean enableDateFields) {
        cbCPSExecuteNow.setSelected(!enableDateFields);
        dcCPSExecuteDate.setEnabled(enableDateFields);
        if (!enableDateFields) {
            dcCPSExecuteTime.setEnabled(false);
        }
        else {
            dcCPSExecuteTime.setEnabled(true);
            dcCPSExecuteTime.setEditable(true);
            dcCPSExecuteTime.setDatePickerEnabled(false);
        }
    }

    private Long getSearchLimit() {
        return getValidSearchLimit(tfSearchLimit.getValueAsLong(MAX_SEARCH_LIMIT));
    }

    private Long getValidSearchLimit(Long value) {
        if(value == null || value <= 0 || value > MAX_SEARCH_LIMIT) {
            return MAX_SEARCH_LIMIT;
        }
        return value;
    }

    public static class SwitchMigrationViewObject {
        private Boolean selected = true;
        private Integer dialNumberCount = null;
        private final SwitchMigrationView switchMigrationView;

        public SwitchMigrationViewObject(SwitchMigrationView switchMigrationView) {
            this.selected = true;
            assert switchMigrationView != null;
            this.switchMigrationView = switchMigrationView;
        }

        public Integer getDialNumberCount() {
            return dialNumberCount;
        }

        public void setDialNumberCount(Integer dialNumberCount) {
            this.dialNumberCount = dialNumberCount;
        }

        public Boolean getSelected() {
            return selected;
        }

        public SwitchMigrationView getSwitchMigrationView() {
            return switchMigrationView;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public Long getAuftragId() {
            return switchMigrationView.getAuftragId();
        }

        public String getProdukt() {
            return switchMigrationView.getProdukt();
        }

        public String getAuftragStatus() {
            return switchMigrationView.getAuftragStatus();
        }

        public String getVbz() {
            return switchMigrationView.getVbz();
        }

        public Long getBillingAuftragId() {
            return switchMigrationView.getBillingAuftragId();
        }

        public Date getBaRealisierung() {
            return switchMigrationView.getBaRealisierung();
        }

        public String getSwitchKennung() {
            return switchMigrationView.getSwitchKennung();
        }

        public String getTechLocation() {
            return switchMigrationView.getTechLocation();
        }

        public Date getInbetriebnahme() {
            return switchMigrationView.getInbetriebnahme();
        }
    }

}
