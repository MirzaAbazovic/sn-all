/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.13
 */
package de.augustakom.hurrican.gui.tools.wbci;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.ScreenTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.tools.wbci.tables.DecisionTable;
import de.augustakom.hurrican.gui.tools.wbci.tables.DecisionTableModel;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.DecisionResult;
import de.mnet.wbci.model.DecisionVO;
import de.mnet.wbci.model.DecisionVOwithKuendigungsCheckVOHelper;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KuendigungsCheckVO;
import de.mnet.wbci.model.MeldungPositionTyp;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.service.WbciDecisionService;
import de.mnet.wbci.service.WbciKuendigungsService;

/**
 * Dialog zur Gegenueberstellung der Daten aus einer empfangenen Vorabstimmung (M-net = abgebender Provider) und den
 * Daten aus dem zugeordneten Taifun Auftrag.
 *
 *
 */
public class DecisionDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, TableModelListener {

    private static final Logger LOGGER = Logger.getLogger(DecisionDialog.class);
    public static final String RUEMVA_BTN = "ruemva.btn";
    public static final String ABBM_BTN = "abbm.btn";
    public static final String CANCEL_BTN = "cancel.btn";
    public static final String DIALOG_TITLE = "dialog.title";
    public static final String DETAILS_LBL = "details.label";
    public static final String TAIFUN_CANCELLATION_DATE_INFO_LBL = "taifun.cancellation.date.info";
    private static final long serialVersionUID = 5959490409926541173L;
    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/wbci/resources/DecisionDialog.xml";

    private static final int TABLE_HEIGHT_PER_ROW = 20;
    private static final int TABLE_PREFERRED_WIDTH = 920;

    private AKJLabel lblTaifunInfo = null;
    private AKJButton btnAbbm = null;
    private AKJButton btnRuemVa = null;
    private DecisionTable decisionTable;
    private AKJScrollPane spDecisionTable;
    private final WbciRequest wbciRequest;
    private WbciDecisionService wbciDecisionService;
    private WbciKuendigungsService wbciKuendigungsService;
    private KuendigungsCheckVO kuendigungsCheckVO = null;

    /**
     * Konstruktor mit Angabe des {@link WbciRequest}s, auf den sich die Anwort beziehen soll.
     *
     * @param wbciRequest current selected {@link WbciRequest}
     */
    public DecisionDialog(WbciRequest wbciRequest) {
        super(RESOURCE, true, false);
        this.wbciRequest = wbciRequest;

        try {
            initServices();
            createGUI();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    /**
     * Looks up to the CCServices and init the service.
     *
     * @throws de.augustakom.common.service.exceptions.ServiceNotFoundException if a service could not be looked up
     */
    protected void initServices() throws ServiceNotFoundException {
        wbciDecisionService = getCCService(WbciDecisionService.class);
        wbciKuendigungsService = getCCService(WbciKuendigungsService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle(String.format(getSwingFactory().getText(DIALOG_TITLE), wbciRequest.getWbciGeschaeftsfall()
                .getVorabstimmungsId()));

        AKJLabel lblDetails = getSwingFactory().createLabel(DETAILS_LBL, AKJLabel.LEFT, Font.BOLD);
        lblTaifunInfo = getSwingFactory().createLabel(TAIFUN_CANCELLATION_DATE_INFO_LBL, AKJLabel.CENTER);

        decisionTable = new DecisionTable(new DecisionTableModel(wbciRequest.getWbciGeschaeftsfall().getTyp()));
        decisionTable.getModel().addTableModelListener(this);
        decisionTable.setDefaultRenderer(Object.class, new DecisionTableCellRendererEditor());
        decisionTable.addMouseListener(new DecisionKwtSystemValueMouseListener(decisionTable));
        spDecisionTable = new AKJScrollPane(decisionTable, new Dimension(TABLE_PREFERRED_WIDTH, 300));

        AKJPanel dtlPnl = new AKJPanel(new GridBagLayout());
        // @formatter:off
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, 0,   1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblDetails       , GBCFactory.createGBC(  0,  0, 1, 1,   1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(spDecisionTable  , GBCFactory.createGBC(100,100, 1, 2,   1, 1, GridBagConstraints.BOTH));
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, 3,   1, 1, GridBagConstraints.NONE));
        dtlPnl.add(lblTaifunInfo    , GBCFactory.createGBC(  0,  0, 1, 4,   1, 1, GridBagConstraints.HORIZONTAL));
        dtlPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, 5,   1, 1, GridBagConstraints.NONE));
        // @formatter:on

        btnRuemVa = getSwingFactory().createButton(RUEMVA_BTN, getActionListener());
        btnAbbm = getSwingFactory().createButton(ABBM_BTN, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(CANCEL_BTN, getActionListener());

        // @formatter:off
        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnRuemVa      , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnAbbm        , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel() , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCancel      , GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        // @formatter:on

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(dtlPnl, BorderLayout.CENTER);
        getChildPanel().add(btnPanel, BorderLayout.SOUTH);
    }

    @Override
    public final void loadData() {
        try {
            decisionTable.getModel().removeAll();

            loadDecisionTable();

            // PreferredSize der Tabelle dynamisch anpassen - abhaengig von der Anzahl der dargestellten Zeilen
            int rowCount = decisionTable.getModel().getRowCount();
            rowCount = (rowCount > 0) ? rowCount : 5;
            int height = rowCount * TABLE_HEIGHT_PER_ROW;
            spDecisionTable.setPreferredSize(new Dimension(TABLE_PREFERRED_WIDTH, height + 20));
            ScreenTools.limitPreferredSizeIfToBig(spDecisionTable, 95, 85);
            revalidate();

            setButtonAndFieldsEnabledState();
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private void loadDecisionTable() {
        try {
            wbciKuendigungsService.getCheckedBillingAuftrag(wbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig());

            decisionTable.getModel()
                    .setData(wbciDecisionService.evaluateDecisionData(
                            wbciRequest.getWbciGeschaeftsfall().getVorabstimmungsId()));

            kuendigungsCheckVO = DecisionVOwithKuendigungsCheckVOHelper.getTaifunKuendigungsstatus(decisionTable
                    .getModel().getData());
        }
        catch (WbciServiceException e) {
            // Billing-Auftrag wurde hoechst wahrscheinlich nicht gefunden --> keine Error-Handling notwendig.
            // In diesem Fall wird nur ABBM erlaubt.
        }
    }

    @Override
    protected void execute(String command) {
        switch (command) {
            case ABBM_BTN:
                doAbbm();
                break;
            case RUEMVA_BTN:
                doRuemVa();
                break;
            case CANCEL_BTN:
                cancel();
                break;
            default:
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // intentionally left blank
    }

    @Override
    protected void doSave() {
        // do nothing
    }

    protected void doRuemVa() {
        try {
            RuemVaDialog ruemVaDialog = new RuemVaDialog(wbciRequest, new ArrayList<>(decisionTable.getModel()
                    .getData()));
            Object o = DialogHelper.showDialog(this, ruemVaDialog, true, true);
            if (!DialogHelper.wasDialogCancelled(o)) {
                prepare4Close();
                setValue(o);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    protected void doAbbm() {
        try {
            List<DecisionVO> decisionVOs = decisionTable.getModel().getData() != null
                    ? new ArrayList<>(decisionTable.getModel().getData())
                    : null;
            AbbmDialog abbmDialog = new AbbmDialog(wbciRequest, decisionVOs);
            Object o = DialogHelper.showDialog(this, abbmDialog, true, true);
            if (!DialogHelper.wasDialogCancelled(o)) {
                prepare4Close();
                setValue(o);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        setButtonAndFieldsEnabledState();
    }

    /**
     * Enable/Disable button for ABBM and RUEMVA according to final meldungsCodes in decision table.
     */
    private void setButtonAndFieldsEnabledState() {
        boolean ruemVaEnabled = true;
        boolean abbmEnabled = true;

        if (decisionTable.getModel().getData() != null && decisionTable.getModel().getData().size() > 0) {
            for (DecisionVO decisionVO : decisionTable.getModel().getData()) {
                if (decisionVO.getFinalMeldungsCode() != null &&
                        decisionVO.getFinalMeldungsCode().isValidForMeldungPosTyps(MeldungPositionTyp.ABBM)) {
                    ruemVaEnabled = false;
                }
                if (DecisionResult.MANUELL.equals(decisionVO.getFinalResult())) {
                    ruemVaEnabled = false;
                    abbmEnabled = false;
                }
            }
        }
        else {
            ruemVaEnabled = false;
        }
        
        // update states in case of an special Kuendigungsstatus
        if (kuendigungsCheckVO != null && kuendigungsCheckVO.getKuendigungsstatus() != null) {
            final GeschaeftsfallTyp gfTyp = wbciRequest.getWbciGeschaeftsfall().getTyp();
            StringBuilder infoText = new StringBuilder(kuendigungsCheckVO.getKuendigungsstatus().getInfoText(gfTyp));
            if (!kuendigungsCheckVO.getKuendigungsstatus().isGeschaeftsfallAllowed(gfTyp)) {
                ruemVaEnabled = false;
                infoText.append(" Der Geschäftsfall ")
                        .append(wbciRequest.getWbciGeschaeftsfall().getTyp().getShortName())
                        .append(" ist nicht erlaubt!");
            }
            lblTaifunInfo.setText(infoText.toString());
            lblTaifunInfo.convertCurrentTextToMultiline(150);
            lblTaifunInfo.setVisible(true);
        }
        else {
            lblTaifunInfo.setVisible(false);
        }

        btnRuemVa.setEnabled(ruemVaEnabled);
        btnAbbm.setEnabled(abbmEnabled);
    }

    /**
     * MouseListener, um bei Clicks in die Spalte mit dem errechneten Wechseltermin den KuendigungsCheck-Dialog zu
     * oeffnen.
     */
    class DecisionKwtSystemValueMouseListener extends MouseAdapter {
        private final JTable table;

        public DecisionKwtSystemValueMouseListener(JTable table) {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / table.getRowHeight();

            if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof Date) {
                    try {
                        CancellationCheckDialog dlg = new CancellationCheckDialog(
                                wbciRequest.getWbciGeschaeftsfall().getBillingOrderNoOrig(),
                                Instant.ofEpochMilli(wbciRequest.getCreationDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                true);
                        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                    }
                    catch (Exception ex) {
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }

    /**
     * CellRenderer, um bei dem vom System errechneten Wechseltermin zusaetzlich einen Button anzuzeigen.
     */
    class DecisionTableCellRendererEditor extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -2158287367569923547L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            if (value instanceof Date) {
                String date = DateTools.formatDate((Date) value, DateTools.PATTERN_DAY_MONTH_YEAR);
                JLabel lblDate = (JLabel) super.getTableCellRendererComponent(table, date, isSelected, hasFocus, row,
                        column);
                JButton btnShowCancelDialog = new JButton("...");
                btnShowCancelDialog.setPreferredSize(new Dimension(15, 15));

                JPanel kwtPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                kwtPanel.add(lblDate);
                kwtPanel.add(btnShowCancelDialog);
                return kwtPanel;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

}
