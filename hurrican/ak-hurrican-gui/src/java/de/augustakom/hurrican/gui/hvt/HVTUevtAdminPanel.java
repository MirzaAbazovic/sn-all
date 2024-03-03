/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.06.2004 11:52:33
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.shared.HVTStandortListCellRenderer;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.UEVT2Ziel;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Admin-Panel fuer die Verwaltung der UEVTs.
 *
 *
 */
public class HVTUevtAdminPanel extends AbstractAdminPanel implements AKNavigationBarListener {

    private static final Logger LOGGER = Logger.getLogger(HVTUevtAdminPanel.class);

    // GUI-Komponenten
    private AKJNavigationBar navBar = null;
    private AKReferenceField rfRack = null;
    private AKJTextField tfHVTStId = null;
    private AKJTextField tfUevt = null;
    private AKJTextField tfSchwellwert = null;
    private AKJCheckBox chbProjektierung = null;
    private AKJComboBox cbProdukte = null;
    private AKJComboBox cbHVTZiel = null;
    private HVTStandortListCellRenderer cbHVTRenderer = null;

    private HVTStandort hvtStandort = null;
    private UEVT uevtModel = null;
    private UEVT2ZielTableModel tbMdlUEVTZiel = null;
    private UEVT2Ziel uevt2Ziel = null;

    // Sonstiges
    private Map<Long, Produkt> produkteMap = new HashMap<>();
    private Map<Long, HVTGruppe> hvtGruppenMap = new HashMap<>();
    private Map<Long, HVTStandort> hvtStdMap = new HashMap<>();
    private boolean guiCreated = false;
    private boolean enableGUI = false;

    /**
     * Konstruktor
     */
    public HVTUevtAdminPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTUevtAdminPanel.xml");
        createGUI();
        loadDefaultData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        navBar = new AKJNavigationBar(this, true, true);

        AKJLabel lblRack = getSwingFactory().createLabel("rack");
        AKJLabel lblHVTStId = getSwingFactory().createLabel("uevt.hvt.standort");
        AKJLabel lblUevt = getSwingFactory().createLabel("uevt");
        AKJLabel lblSchwellwert = getSwingFactory().createLabel("uevt.schwellwert");
        AKJLabel lblProjektierung = getSwingFactory().createLabel("uevt.projektierung");
        AKJLabel lblProdukte = getSwingFactory().createLabel("produkte");
        AKJLabel lblHVTZiel = getSwingFactory().createLabel("hvtziel");

        Dimension cbDim = new Dimension(140, 22);
        CBItemListener cbItemListener = new CBItemListener();
        rfRack = getSwingFactory().createReferenceField("rack");
        tfHVTStId = getSwingFactory().createTextField("uevt.hvt.standort");
        tfHVTStId.setEditable(false);
        tfUevt = getSwingFactory().createTextField("uevt");
        tfSchwellwert = getSwingFactory().createTextField("uevt.schwellwert");
        chbProjektierung = getSwingFactory().createCheckBox("uevt.projektierung");
        AKJButton btnCreateMatrix = getSwingFactory().createButton("create.matrix", getActionListener());
        cbProdukte = getSwingFactory().createComboBox("produkte");
        cbProdukte.addItemListener(cbItemListener);
        cbProdukte.setRenderer(new AKCustomListCellRenderer<>(Produkt.class, Produkt::getBezeichnung));
        cbProdukte.setPreferredSize(cbDim);
        cbProdukte.setEnabled(false);
        cbHVTZiel = getSwingFactory().createComboBox("hvtziel");
        cbHVTZiel.addItemListener(cbItemListener);
        cbHVTZiel.setEnabled(false);
        cbHVTRenderer = new HVTStandortListCellRenderer();
        cbHVTZiel.setRenderer(cbHVTRenderer);
        cbHVTZiel.setPreferredSize(cbDim);
        AKJButton btnAddZiel = getSwingFactory().createButton("add.ziel", getActionListener());
        btnAddZiel.setBorder(null);
        AKJButton btnRemoveZiel = getSwingFactory().createButton("remove.ziel", getActionListener());
        btnRemoveZiel.setBorder(null);

        tbMdlUEVTZiel = new UEVT2ZielTableModel();
        AKJTable tbUEVTZiel = new AKJTable(tbMdlUEVTZiel, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbUEVTZiel.addMouseListener(getTableListener());
        tbUEVTZiel.addKeyListener(getTableListener());
        tbUEVTZiel.fitTable(new int[] { 170, 170 });
        AKJScrollPane spTable = new AKJScrollPane(tbUEVTZiel);
        spTable.setPreferredSize(new Dimension(300, 100));

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(navBar, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblHVTStId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(tfHVTStId, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblRack, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(rfRack, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblUevt, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfUevt, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSchwellwert, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfSchwellwert, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProjektierung, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbProjektierung, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(btnCreateMatrix, GBCFactory.createGBC(0, 0, 3, 7, 1, 1, GridBagConstraints.NONE));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 8, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel tbPnl = new AKJPanel(new GridBagLayout());
        tbPnl.add(spTable, GBCFactory.createGBC(100, 100, 0, 0, 1, 3, GridBagConstraints.BOTH));
        tbPnl.add(btnAddZiel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        tbPnl.add(btnRemoveZiel, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        tbPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 2, 2, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("uevt2ziel")));
        right.add(lblProdukte, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(cbProdukte, GBCFactory.createGBC(20, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblHVTZiel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(cbHVTZiel, GBCFactory.createGBC(20, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tbPnl, GBCFactory.createGBC(100, 100, 0, 2, 4, 3, GridBagConstraints.BOTH));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(right, GBCFactory.createGBC(100, 100, 2, 0, 1, 1, GridBagConstraints.BOTH));

        guiCreated = true;
        manageGUI(btnCreateMatrix, btnAddZiel, btnRemoveZiel);

        enableGUI(enableGUI);
    }

    public void updateData(Map<Long, HVTGruppe> hvtGrpMap, List<HVTGruppe> hvtGruppenList, List<HVTStandort> hvtStandortsList) {
        CollectionMapConverter.convert2Map(hvtGruppenList, hvtGruppenMap, "getId", null);
        cbHVTRenderer.setHVTGruppen(hvtGruppenMap);

        cbHVTZiel.addItems(hvtStandortsList, true, HVTStandort.class);
        CollectionMapConverter.convert2Map(hvtStandortsList, hvtStdMap, "getId", null);
    }

    /* Laedt die Standart-Daten fuer das Panel */
    private void loadDefaultData() {
        try {
            // Produkte laden und in Map speichern
            ProduktService ps = getCCService(ProduktService.class);
            List<Produkt> produkteList = ps.findProdukte(false);
            cbProdukte.addItems(produkteList, true, Produkt.class);
            CollectionMapConverter.convert2Map(produkteList, produkteMap, "getId", null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        clear(true);
        if (details instanceof HVTStandort) {
            this.hvtStandort = (HVTStandort) details;
            loadData();
        }
        else if (details instanceof UEVT2Ziel) {
            clearUEVT2Ziel();
            this.uevt2Ziel = (UEVT2Ziel) details;
            cbProdukte.setEnabled(true);
            cbHVTZiel.setEnabled(true);
            cbProdukte.selectItem("getId", Produkt.class, uevt2Ziel.getProduktId());
            cbHVTZiel.selectItem("getId", HVTStandort.class, uevt2Ziel.getHvtStandortIdZiel());
        }
        else {
            this.hvtStandort = null;
            navBar.setData(null);
        }
        enableGUI = true;
        enableGUI(enableGUI);
    }

    /* 'Loescht' alle Felder */
    private void clear(boolean clearHVT) {
        if (guiCreated) {
            if (clearHVT) {
                tfHVTStId.setText("");
            }
            else {
                // Füge aktuelle HVT-Standort-ID ein
                tfHVTStId.setText(hvtStandort.getHvtIdStandort());
            }
            rfRack.clearReference();
            tfUevt.setText("");
            tfSchwellwert.setText("");
            chbProjektierung.setSelected(false);
            tbMdlUEVTZiel.setData(null);
            clearUEVT2Ziel();
        }
    }

    /* Loescht die ComboBoxen fuer die UEVT2Ziel-Definition. */
    private void clearUEVT2Ziel() {
        uevt2Ziel = null;
        cbHVTZiel.setSelectedIndex(-1);
        cbProdukte.setSelectedIndex(-1);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        if (hvtStandort != null) {
            try {
                setWaitCursor();
                clear(true);
                //ReferenceField fuer Rack vorbelegen
                ISimpleFindService sfs = getCCService(QueryCCService.class);
                rfRack.setFindService(sfs);

                HWService hwService = getCCService(HWService.class);
                List<HWRack> racks = hwService.findRacks(hvtStandort.getHvtIdStandort());
                rfRack.setReferenceList(racks);

                // Lade UEVTs
                HVTService service = getCCService(HVTService.class);
                List<UEVT> uevts4HVT = service.findUEVTs4HVTStandort(hvtStandort.getId());
                navBar.setData(uevts4HVT);

                // ComboBoxes fuer Ziel-HVT auf disabled setzen
                cbProdukte.setEnabled(false);
                cbHVTZiel.setEnabled(false);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        if (hvtStandort != null) {
            clear(false);
            this.uevtModel = null;

            enableGUI = true;
            enableGUI(enableGUI);
        }
        else {
            MessageHelper.showInfoDialog(this, "Bitte zuerst einen HVT-Standort auswählen!");
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            if (StringUtils.isBlank(tfUevt.getText())) {
                throw new HurricanGUIException("Bitte geben Sie eine nicht-leer Bezeichnung für den UEVT an.");
            }

            boolean isNew = false;
            if (uevtModel == null) {
                uevtModel = new UEVT();
                isNew = true;
            }

            uevtModel.setHvtIdStandort(tfHVTStId.getTextAsLong(null));
            uevtModel.setUevt(tfUevt.getText(null));
            uevtModel.setSchwellwert(tfSchwellwert.getTextAsInt(null));
            uevtModel.setProjektierung(chbProjektierung.isSelectedBoolean());
            uevtModel.setRackId(rfRack.getReferenceIdAs(Long.class));

            try {
                HVTService service = getCCService(HVTService.class.getName(), HVTService.class);
                service.saveUEVT(uevtModel, (List<UEVT2Ziel>) tbMdlUEVTZiel.getData());
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                MessageHelper.showErrorDialog(this, ex);
            }

            if (isNew) {
                navBar.addNavigationObject(uevtModel);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    @Override
    public void showNavigationObject(Object obj, int number) {
        if (guiCreated) {
            if (obj instanceof UEVT) {
                uevtModel = (UEVT) obj;
                tfHVTStId.setText(uevtModel.getHvtIdStandort());
                tfUevt.setText(uevtModel.getUevt());
                tfSchwellwert.setText(uevtModel.getSchwellwert());
                chbProjektierung.setSelected(uevtModel.getProjektierung());
                rfRack.setReferenceId(uevtModel.getRackId());

                // UEVT-Ziele zum UEVT laden
                try {
                    HVTService hvts = getCCService(HVTService.class);
                    List<UEVT2Ziel> uevtZiele = hvts.findUEVTZiele(uevtModel.getId());
                    tbMdlUEVTZiel.setData(uevtZiele);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
            }
            else {
                uevtModel = null;
                clear(false);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("create.matrix".equals(command)) {
            openDialog4Matrix();
        }
        else if ("add.ziel".equals(command)) {
            this.uevt2Ziel = new UEVT2Ziel();
            tbMdlUEVTZiel.addObject(uevt2Ziel);
            tbMdlUEVTZiel.fireTableDataChanged();
            showDetails(uevt2Ziel);
        }
        else if ("remove.ziel".equals(command)) {
            tbMdlUEVTZiel.removeObject(uevt2Ziel);
            tbMdlUEVTZiel.fireTableDataChanged();
            clearUEVT2Ziel();
        }
    }

    /* Oeffnet einen Dialog, um die Rangierungsmatrix anzulegen. */
    private void openDialog4Matrix() {
        if (uevtModel != null) {
            CreateMatrix4UevtDialog dlg = new CreateMatrix4UevtDialog(uevtModel);
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        }
        else {
            MessageHelper.showMessageDialog(this,
                    "Bitte wählen Sie zuerst einen UEVT aus.", "Kein UEVT ausgewählt",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    // Funktion setzt das editable-Attribut aller GUI-Elemente
    private void enableGUI(boolean enable) {
        tfUevt.setEditable(enable);
        tfSchwellwert.setEditable(enable);
        chbProjektierung.setEnabled(enable);
    }

    /**
     * TableModel fuer die Darstellung der UEVT2Ziel-Objekte.
     */
    class UEVT2ZielTableModel extends AKTableModel<UEVT2Ziel> {
        static final int COL_PRODUKT = 0;
        static final int COL_HVTZIEL = 1;

        static final int COL_COUNT = 2;

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
                case COL_PRODUKT:
                    return "Produkt";
                case COL_HVTZIEL:
                    return "Ziel-HVT";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            UEVT2Ziel u2z = getDataAtRow(row);
            switch (column) {
                case COL_PRODUKT:
                    Produkt p = produkteMap.get(u2z.getProduktId());
                    return (p != null) ? p.getBezeichnung() : null;
                case COL_HVTZIEL:
                    HVTStandort std = hvtStdMap.get(u2z.getHvtStandortIdZiel());
                    if (std != null) {
                        HVTGruppe g = hvtGruppenMap.get(std.getHvtGruppeId());
                        if (g != null) {
                            StringBuilder hvt = new StringBuilder();
                            hvt.append(g.getOrtsteil());
                            hvt.append(" - ");
                            hvt.append(std.getId());
                            return hvt.toString();
                        }
                    }
                    return null;
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    /**
     * Item-Listener fuer die ComboBoxes der UEVT2Ziel-Definition.
     */
    class CBItemListener implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (uevt2Ziel != null) {
                if (e.getSource() == cbProdukte) {
                    uevt2Ziel.setProduktId((cbProdukte.getSelectedItem() instanceof Produkt)
                            ? ((Produkt) cbProdukte.getSelectedItem()).getId() : null);
                }
                else if (e.getSource() == cbHVTZiel) {
                    uevt2Ziel.setHvtStandortIdZiel((cbHVTZiel.getSelectedItem() instanceof HVTStandort)
                            ? ((HVTStandort) cbHVTZiel.getSelectedItem()).getId() : null);
                }
                tbMdlUEVTZiel.fireTableDataChanged();
            }
        }
    }
}


