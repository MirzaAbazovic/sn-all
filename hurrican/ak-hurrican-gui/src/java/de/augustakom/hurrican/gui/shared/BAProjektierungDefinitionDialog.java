/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.09.2010 09:25:09
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableSorter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;


/**
 * Dialog fuer die Erstellung einer Projektierung.
 *
 *
 */
public class BAProjektierungDefinitionDialog extends AbstractServiceOptionDialog {
    // String Konstanten
    private static final String RESOURCE = "de/augustakom/hurrican/gui/shared/resources/BAProjektierungDefinitionDialog.xml";

    // String Konstanten GUI
    private static final String TITLE = "title";
    private static final String CHECKBOX_REFERTOORDER = "checkbox.referToOrder";

    // Daten
    private Long auftragId = null;
    private Long billingAuftragId = null;
    private ViewTableModel tbMdlViews = null;
    private List<AuftragDatenView> views = null;

    // GUI
    private AKJTable tbViews = null;
    private AKJCheckBox cbReferToOrder = null;
    private BASelectSubOrdersPanel subOrdersPanel = null;

    public BAProjektierungDefinitionDialog(List<AuftragDatenView> views, Long auftragId, Long billingAuftragId) {
        super(RESOURCE, true);
        this.views = views;
        this.auftragId = auftragId;
        this.billingAuftragId = billingAuftragId;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        this.setTitle(getSwingFactory().getText(TITLE));
        AKJLabel lblReferToOrder = getSwingFactory().createLabel(CHECKBOX_REFERTOORDER, AKJLabel.LEFT, Font.BOLD);

        cbReferToOrder = getSwingFactory().createCheckBox(CHECKBOX_REFERTOORDER, getActionListener(), false);

        tbMdlViews = new ViewTableModel();
        tbViews = new AKJTable(tbMdlViews, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbViews.attachSorter();

        AKJScrollPane scrollPane = new AKJScrollPane(tbViews);
        scrollPane.setPreferredSize(new Dimension(450, 100));
        subOrdersPanel = new BASelectSubOrdersPanel(auftragId, billingAuftragId, new Dimension(450, 100));

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        panel.add(subOrdersPanel, GBCFactory.createGBC(100, 100, 0, 0, 2, 1, GridBagConstraints.BOTH));
        panel.add(cbReferToOrder, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblReferToOrder, GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        panel.add(scrollPane, GBCFactory.createGBC(100, 100, 0, 2, 2, 1, GridBagConstraints.BOTH));

        AKJPanel child = getChildPanel();
        child.setLayout(new BorderLayout());
        child.add(panel, BorderLayout.CENTER);

        toggleTableViews();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        if (cbReferToOrder.isSelected()) {
            Object selection = null;
            if (tbViews.getSelectedRow() >= 0) {
                @SuppressWarnings("unchecked")
                AKTableSorter<AuftragDatenView> sorter = (AKTableSorter<AuftragDatenView>) tbViews.getModel();
                selection = ((ViewTableModel) sorter.getModel()).getDataAtRow(tbViews.getSelectedRow());
            }

            if ((selection != null) && (selection instanceof AuftragDatenView)) {
                prepare4Close();
                setValue(selection);
            }
            else {
                MessageHelper.showInfoDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        "Bitte wählen Sie einen Auftrag bzw. eine Verbindungsbezeichnung aus oder verlassen Sie den Dialog über 'Abbrechen'.",
                        null, true);
            }
        }
        else {
            prepare4Close();
            setValue(OK_OPTION);
        }
    }

    protected void toggleTableViews() {
        if (cbReferToOrder.isSelected()) {
            tbMdlViews.setData(views);
        }
        else {
            tbMdlViews.setData(null);
        }
        tbViews.fitTable(new int[] { 80, 80, 80, 80, 120, 100 });
    }

    @Override
    protected void execute(String command) {
        if (StringUtils.equals(command, CHECKBOX_REFERTOORDER)) {
            toggleTableViews();
        }
    }

    public Set<Long> getSelectedSubOrders() {
        return subOrdersPanel.getSelectedSubOrders();
    }

    /**
     * Table Model zur Selektierung eines alten Auftrages
     */
    static class ViewTableModel extends AKTableModel<AuftragDatenView> {
        static final int COL_VBZ = 0;
        static final int COL_AUFTRAG_ID = 1;
        static final int COL_AUFTRAG__NO = 2;
        static final int COL_INBETRIEBNAHME = 3;
        static final int COL_ANSCHLUSSART = 4;
        static final int COL_STATUS = 5;

        static final int COL_COUNT = 6;

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_VBZ:
                    return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
                case COL_AUFTRAG_ID:
                    return "Auftrag-ID (CC)";
                case COL_AUFTRAG__NO:
                    return "Order__No";
                case COL_INBETRIEBNAHME:
                    return "Inbetriebnahme";
                case COL_ANSCHLUSSART:
                    return "Anschlussart";
                case COL_STATUS:
                    return "Status";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof AuftragDatenView) {
                AuftragDatenView view = (AuftragDatenView) o;
                switch (column) {
                    case COL_VBZ:
                        return view.getVbz();
                    case COL_AUFTRAG_ID:
                        return view.getAuftragId();
                    case COL_AUFTRAG__NO:
                        return view.getAuftragNoOrig();
                    case COL_INBETRIEBNAHME:
                        return view.getInbetriebnahme();
                    case COL_ANSCHLUSSART:
                        return view.getAnschlussart();
                    case COL_STATUS:
                        return view.getAuftragStatusText();
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int ci) {
            switch (ci) {
                case COL_INBETRIEBNAHME:
                    return Date.class;
                case COL_ANSCHLUSSART:
                case COL_VBZ:
                case COL_STATUS:
                    return String.class;
                case COL_AUFTRAG_ID:
                case COL_AUFTRAG__NO:
                    return Long.class;
                default:
                    return Integer.class;
            }
        }
    }

}
