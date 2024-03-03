/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 09:30:06
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.cc.CCLeistungsService;


/**
 * Panel, um die technischen Leistungen fuer ein Produkt zu konfigurieren.
 *
 *
 */
public class Produkt2TechLsPanel extends AbstractServicePanel implements AKModelOwner,
        AKDataLoaderComponent, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(Produkt2TechLsPanel.class);

    private AKJComboBox cbTechLs = null;
    private AKJComboBox cbTechLsDep = null;
    private AKJCheckBox chbDefault = null;
    private AKJFormattedTextField tfPriority;
    private P2TLsTableModel tbMdlTechLs = null;

    private Map<Long, TechLeistung> techLsMap = null;
    private Produkt produkt = null;
    private Produkt2TechLeistung selectedP2TL = null;

    /**
     * Konstruktor.
     */
    public Produkt2TechLsPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/Produkt2TechLsPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTechLs = getSwingFactory().createLabel("tech.leistung");
        AKJLabel lblTechLsDep = getSwingFactory().createLabel("tech.leistung.dep");
        AKJLabel lblDefault = getSwingFactory().createLabel("is.default");
        AKJLabel lblPriority = getSwingFactory().createLabel("priority");

        AKJButton btnAddTechLs = getSwingFactory().createButton("add.p2tls", getActionListener(), null);
        AKJButton btnDelTechLs = getSwingFactory().createButton("del.p2tls", getActionListener(), null);
        AKJButton btnSaveTechLs = getSwingFactory().createButton("save.p2tls", getActionListener(), null);
        cbTechLs = getSwingFactory().createComboBox("tech.leistung",
                new AKCustomListCellRenderer<>(TechLeistung.class, TechLeistung::getName));
        cbTechLsDep = getSwingFactory().createComboBox("tech.leistung.dep",
                new AKCustomListCellRenderer<>(TechLeistung.class, TechLeistung::getName));
        chbDefault = getSwingFactory().createCheckBox("is.default");
        tfPriority = getSwingFactory().createFormattedTextField("priority");

        tbMdlTechLs = new P2TLsTableModel();
        AKJTable tbTechLs = new AKJTable(tbMdlTechLs, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbTechLs.attachSorter();
        tbTechLs.fitTable(new int[] { 200, 150, 50, 100 });
        tbTechLs.addTableListener(this);
        AKJScrollPane spTable = new AKJScrollPane(tbTechLs, new Dimension(500, 100));

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddTechLs, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnDelTechLs, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnSaveTechLs, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(btnPnl, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblTechLs, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        top.add(cbTechLs, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblTechLsDep, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(cbTechLsDep, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblDefault, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(chbDefault, GBCFactory.createGBC(0, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(lblPriority, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfPriority, GBCFactory.createGBC(0, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(spTable, BorderLayout.CENTER);

        // Bitte alle GUI Komponenten auf Rechte prüfen, da diverse User nur auf wenige Komponenten rechte haben!
        manageGUI(btnAddTechLs, btnDelTechLs, btnSaveTechLs, cbTechLs,
                cbTechLsDep, chbDefault, tfPriority, tbTechLs);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            CCLeistungsService ls = getCCService(CCLeistungsService.class);
            List<TechLeistung> techLeistungen = ls.findTechLeistungen(false);

            techLsMap = new HashMap<Long, TechLeistung>();
            CollectionMapConverter.convert2Map(techLeistungen, techLsMap, "getId", null);

            cbTechLs.addItems(techLeistungen, true, TechLeistung.class);
            cbTechLsDep.addItems(techLeistungen, true, TechLeistung.class);
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
        if ("add.p2tls".equals(command)) {
            newConfig();
        }
        else if ("del.p2tls".equals(command)) {
            deleteConfig();
        }
        else if ("save.p2tls".equals(command)) {
            saveModel();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.produkt = (model instanceof Produkt) ? (Produkt) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        try {
            List<Produkt2TechLeistung> assignedTechLs = null;
            selectedP2TL = null;
            GuiTools.cleanFields(this);

            if (produkt != null) {
                CCLeistungsService ls = getCCService(CCLeistungsService.class);
                assignedTechLs = ls.findProd2TechLs(produkt.getId(), null, null);
            }

            tbMdlTechLs.setData(assignedTechLs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        selectedP2TL = null;
        if (details instanceof Produkt2TechLeistung) {
            selectedP2TL = (Produkt2TechLeistung) details;
            cbTechLs.selectItem("getId", TechLeistung.class, selectedP2TL.getTechLeistung().getId());
            cbTechLsDep.selectItem("getId", TechLeistung.class, selectedP2TL.getTechLeistungDependency());
            chbDefault.setSelected(selectedP2TL.getDefaultLeistung());
            tfPriority.setValue(selectedP2TL.getPriority());
        }
    }

    /* Legt eine neue Produkt-2-Leistungskonfiguration an */
    private void newConfig() {
        selectedP2TL = new Produkt2TechLeistung();
        GuiTools.cleanFields(this);
    }

    /* Loescht die aktuell ausgewaehlte Konfiguration */
    private void deleteConfig() {
        if ((selectedP2TL == null) || (selectedP2TL.getId() == null)) {
            MessageHelper.showMessageDialog(getMainFrame(),
                    "Keine Konfiguration gewählt - löschen nicht möglich!",
                    "Keine Selektion", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            try {

                CCLeistungsService ls = getCCService(CCLeistungsService.class);
                ls.deleteProdukt2TechLeistung(selectedP2TL.getId());

                tbMdlTechLs.removeObject(selectedP2TL);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() {
        try {
            if (selectedP2TL == null) {
                selectedP2TL = new Produkt2TechLeistung();
            }
            selectedP2TL.setProdId(produkt.getId());
            selectedP2TL.setTechLeistung((TechLeistung) cbTechLs.getSelectedItem());
            selectedP2TL.setTechLeistungDependency((Long) cbTechLsDep.getSelectedItemValue("getId", Long.class));
            selectedP2TL.setDefaultLeistung(chbDefault.isSelectedBoolean());
            selectedP2TL.setPriority(tfPriority.getValueAsInt(null));

            boolean add2Table = (selectedP2TL.getId() == null) ? true : false;

            CCLeistungsService ls = getCCService(CCLeistungsService.class);
            ls.saveProdukt2TechLeistung(selectedP2TL);

            if (add2Table) {
                tbMdlTechLs.addObject(selectedP2TL);
            }
            else {
                tbMdlTechLs.fireTableDataChanged();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /* TableModel fuer die Darstellung der techn. Leistungen, die dem Produkt zugeordnet sind. */
    class P2TLsTableModel extends AKTableModel<Produkt2TechLeistung> {
        private static final int COL_TECH_LS = 0;
        private static final int COL_TECH_LS_DEP = 1;
        private static final int COL_DEFAULT = 2;
        private static final int COL_TYP = 3;
        private static final int COL_PIORITY = 4;

        private static final int COL_COUNT = 5;

        /**
         * @see javax.swing.table.DefaultTableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_TECH_LS:
                    return "techn. Leistung";
                case COL_TECH_LS_DEP:
                    return "abhängige Leistung";
                case COL_DEFAULT:
                    return "default";
                case COL_TYP:
                    return "Typ";
                case COL_PIORITY:
                    return "Priorität";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object tmp = getDataAtRow(row);
            if (tmp instanceof Produkt2TechLeistung) {
                Produkt2TechLeistung p2tl = (Produkt2TechLeistung) tmp;
                TechLeistung techLs = p2tl.getTechLeistung();
                switch (column) {
                    case COL_TECH_LS:
                        return (techLs != null) ? techLs.getName() : null;
                    case COL_TECH_LS_DEP:
                        TechLeistung tlDep = techLsMap.get(p2tl.getTechLeistungDependency());
                        return (tlDep != null) ? tlDep.getName() : null;
                    case COL_DEFAULT:
                        return p2tl.getDefaultLeistung();
                    case COL_TYP:
                        return techLs.getTyp();
                    case COL_PIORITY:
                        return p2tl.getPriority();
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_DEFAULT) ? Boolean.class : String.class;
        }

    }
}


