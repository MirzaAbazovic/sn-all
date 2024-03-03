/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.04.2010 11:49:47
 */
package de.augustakom.hurrican.gui.hvt.hardware;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.CrossConnectionDialog;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChange;
import de.augustakom.hurrican.model.cc.equipment.HWBaugruppenChangePort2Port;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortDetailView;
import de.augustakom.hurrican.model.cc.view.HWBaugruppenChangePort2PortView;
import de.augustakom.hurrican.service.cc.HWBaugruppenChangeService;


/**
 * Panel, auf dem die Details eines Baugruppen-Schwenks dargestellt werden bzw. ueber das die Details festgelegt werden.
 * Es handelt sich hier um das Basis-Panel, das die allgemeinen Komponenten fuer jeden Baugruppenschwenk-Typ darstellt.
 * Je nach gewaehltem Typ werden noch weitere Komponenten ueber Sub-Panels dargestellt.
 */
public class HWBaugruppenChangeDetailPanel extends AbstractServicePanel implements AKDataLoaderComponent {


    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenChangeDetailPanel.class);

    private static final String AFFECTED_PORTS_AND_ORDERS = "affected.ports.and.orders";
    private static final String CPS_INIT = "cps.init";
    private static final String CPS_MODIFY = "cps.modify";
    private static final String PRINT_PORTMAPPING = "print.portmapping";

    private AKJTable tbAffectedPortsAndOrders;
    private AKReferenceAwareTableModel<HWBaugruppenChangePort2PortView> tbMdlAffectedPortsAndOrders;

    // Modelle
    protected final HWBaugruppenChange hwBaugruppenChange;

    // Services
    private HWBaugruppenChangeService hwBaugruppenChangeService;


    public HWBaugruppenChangeDetailPanel(HWBaugruppenChange hwBaugruppenChange) {
        super("de/augustakom/hurrican/gui/hvt/hardware/resources/HWBaugruppenChangeDetailPanel.xml");
        this.hwBaugruppenChange = hwBaugruppenChange;
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AbstractHWBaugruppenChangeDefinitionPanel changeDefinitionPanel = createDetailPanel();

        this.setLayout(new BorderLayout());
        this.add(changeDefinitionPanel, BorderLayout.NORTH);

        if (hwBaugruppenChange.needsPort2PortMapping()) {
            tbMdlAffectedPortsAndOrders = new AKReferenceAwareTableModel<HWBaugruppenChangePort2PortView>(
                    HWBaugruppenChangePort2PortView.TABLE_COLUMN_NAMES,
                    HWBaugruppenChangePort2PortView.TABLE_PROPERTY_NAMES,
                    HWBaugruppenChangePort2PortView.TABLE_CLASS_TYPES);
            tbAffectedPortsAndOrders = new AKJTable(tbMdlAffectedPortsAndOrders, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
            tbAffectedPortsAndOrders.fitTable(HWBaugruppenChangePort2PortView.TABLE_FIT);
            tbAffectedPortsAndOrders.addPopupAction(new TableOpenOrderAction());
            tbAffectedPortsAndOrders.addPopupSeparator();
            tbAffectedPortsAndOrders.addPopupAction(new OpenCrossConnectionDialogAction());
            AKJScrollPane spTable = new AKJScrollPane(tbAffectedPortsAndOrders, new Dimension(700, 130));

            AKJButton btnCpsInit = getSwingFactory().createButton(CPS_INIT, getActionListener(), null);
            AKJButton btnCpsModify = getSwingFactory().createButton(CPS_MODIFY, getActionListener(), null);
            AKJButton btnPrint = getSwingFactory().createButton(PRINT_PORTMAPPING, getActionListener(), null);

            AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
            btnPanel.add(btnCpsInit, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            btnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
            btnPanel.add(btnCpsModify, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
            btnPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
            btnPanel.add(btnPrint, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));

            AKJPanel tablePanel = new AKJPanel(new BorderLayout(), getSwingFactory().getText(AFFECTED_PORTS_AND_ORDERS));
            tablePanel.add(spTable, BorderLayout.CENTER);
            tablePanel.add(btnPanel, BorderLayout.EAST);

            this.add(tablePanel, BorderLayout.CENTER);
        }
    }

    /* Erstellt je nach Typ des Baugruppen-Schwenks das notwendige Panel. */
    private AbstractHWBaugruppenChangeDefinitionPanel createDetailPanel() {
        if (hwBaugruppenChange != null) {
            switch (hwBaugruppenChange.getChangeTypeValue()) {
                case CHANGE_BG_TYPE:
                    return new HWBaugruppenChangeBgTypPanel(hwBaugruppenChange, this);
                case CHANGE_CARD:
                    return new HWBaugruppenChangeCardPanel(hwBaugruppenChange, this);
                case MERGE_CARDS:
                    return new HWBaugruppenChangeCardPanel(hwBaugruppenChange, this);
                case DSLAM_SPLIT:
                    // TODO DSLAM Split
                    return new EmptyHWBaugruppenChangePanel();
                case DLU_CHANGE:
                    // RDLU-Schwenk
                    return new HWBaugruppenChangeDluPanel(hwBaugruppenChange);
                case PORT_CONCENTRATION:
                    return new HWBaugruppenChangeCardPanel(hwBaugruppenChange, this);
                default:
                    break;
            }
        }

        return null;
    }

    @Override
    public final void loadData() {
        try {
            if (hwBaugruppenChangeService == null) {
                hwBaugruppenChangeService = getCCService(HWBaugruppenChangeService.class);
            }

            if (tbMdlAffectedPortsAndOrders != null) {
                tbMdlAffectedPortsAndOrders.removeAll();
                if (hwBaugruppenChange != null) {
                    List<HWBaugruppenChangePort2PortView> port2PortViews = hwBaugruppenChangeService.findPort2PortViews(hwBaugruppenChange);
                    tbMdlAffectedPortsAndOrders.setData(port2PortViews);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if (CPS_INIT.equals(command)) {
            createCpsTransactions(true);
        }
        else if (CPS_MODIFY.equals(command)) {
            createCpsTransactions(false);
        }
        else if (PRINT_PORTMAPPING.equals(command)) {
            showPortMappingDetails();
        }
    }


    /* Erstellt fuer die betroffenen Auftraege CPS-Transactions. */
    private void createCpsTransactions(boolean init) {
        try {
            setWaitCursor();

            String message = (init) ? getSwingFactory().getText("cps.init.msg") : getSwingFactory().getText("cps.modify.msg");
            String title = (init) ? getSwingFactory().getText("cps.init.title") : getSwingFactory().getText("cps.modify.title");
            int option = MessageHelper.showYesNoQuestion(getMainFrame(), message, title);

            if (option == JOptionPane.YES_OPTION) {
                String warningsAndErrors = hwBaugruppenChangeService.createCpsTransactions(
                        hwBaugruppenChange,
                        init,
                        HurricanSystemRegistry.instance().getSessionId());

                MessageHelper.showInfoDialog(
                        getMainFrame(),
                        getSwingFactory().getText("cps.executed"),
                        new Object[] { warningsAndErrors },
                        true);

                loadData();
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


    /*
     * Zeigt einen Dialog mit Detail-Angaben der Port-2-Port Mappings an.
     * Dieser Dialog kann dazu verwendet werden, einen Ausdruck zu erstellen.
     */
    private void showPortMappingDetails() {
        try {
            List<HWBaugruppenChangePort2PortDetailView> detailViews =
                    hwBaugruppenChangeService.findPort2PortDetailViews(hwBaugruppenChange);
            if (CollectionTools.isNotEmpty(detailViews)) {
                AKReferenceAwareTableModel<HWBaugruppenChangePort2PortDetailView> tbMdlDetails =
                        new AKReferenceAwareTableModel<HWBaugruppenChangePort2PortDetailView>(
                                HWBaugruppenChangePort2PortDetailView.TABLE_COLUMN_NAMES,
                                HWBaugruppenChangePort2PortDetailView.TABLE_PROPERTY_NAMES,
                                HWBaugruppenChangePort2PortDetailView.TABLE_CLASS_TYPES);
                tbMdlDetails.setData(detailViews);

                AKJTable tbDetails = new AKJTable(tbMdlDetails, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
                tbDetails.fitTable(HWBaugruppenChangePort2PortDetailView.TABLE_FIT);
                tbDetails.attachSorter();
                AKJScrollPane spTable = new AKJScrollPane(tbDetails, new Dimension(780, 200));

                MessageHelper.showMessageDialog(getMainFrame(), spTable, "Port-2-Port Details", JOptionPane.PLAIN_MESSAGE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    public void update(Observable o, Object arg) {
    }


    /**
     * Oeffnet den CrossConnection-Dialog fuer den aktuell gewaehlten Auftrag und den im Port-Mapping definierten NEUEN
     * Port.
     *
     *
     */
    class OpenCrossConnectionDialogAction extends AKAbstractAction {
        public OpenCrossConnectionDialogAction() {
            super();
            setName("CrossConnection definieren");
            setTooltip("Dialog, um die Cross-Conn. fuer den NEUEN Port zu definieren");
            setActionCommand("open.crossconn.dialog");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object actionSrc = getValue(ACTION_SOURCE);
            if ((actionSrc instanceof MouseEvent) && (((MouseEvent) actionSrc).getSource() instanceof AKJTable)) {
                try {
                    HWBaugruppenChangePort2PortView p2pView = tbAffectedPortsAndOrders.getTableSelection(
                            (MouseEvent) getValue(ACTION_SOURCE), HWBaugruppenChangePort2PortView.class);

                    HWBaugruppenChangePort2Port port2port = hwBaugruppenChangeService.findPort2Port(p2pView.getPort2PortId());

                    CrossConnectionDialog ccDialog = new CrossConnectionDialog(port2port.getEquipmentNew(), p2pView.getAuftragId());
                    DialogHelper.showDialog(getMainFrame(), ccDialog, true, true);

                    p2pView.setEqNewManualConfiguration(port2port.getEquipmentNew().getManualConfiguration());

                    int selectedRow = tbAffectedPortsAndOrders.getSelectedRow();
                    tbMdlAffectedPortsAndOrders.fireTableDataChanged();
                    tbAffectedPortsAndOrders.setRowSelectionInterval(selectedRow, selectedRow);
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    MessageHelper.showErrorDialog(getMainFrame(), ex);
                }
            }
        }
    }
}


