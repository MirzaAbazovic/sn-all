/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2005 11:10:36
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Produkt;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel zur Konfiguration der moeglichen Ziel-Produkte zu einem best. Produkt.
 *
 *
 */
public class Produkt2ProduktPanel extends AbstractServicePanel implements AKDataLoaderComponent, AKTableOwner {

    private static final Logger LOGGER = Logger.getLogger(Produkt2ProduktPanel.class);

    private AKJComboBox cbProdDest = null;
    private AKJComboBox cbAendTyp = null;
    private AKJComboBox cbChain = null;
    private AKJTextArea taDesc = null;
    private P2PTableModel tbMdlP2PConfig = null;

    // Modelle
    private Produkt produkt = null;
    private Produkt2Produkt actConfig = null;
    private Map<Long, ServiceChain> serviceChainMap = null;
    private Map<Long, Produkt> produktMap = null;
    private Map<Long, PhysikaenderungsTyp> physikaendTypMap = null;

    /**
     * Konstruktor mit Angabe des zu konfigurierenden Produkts.
     *
     * @param produkt
     */
    public Produkt2ProduktPanel(Produkt produkt) {
        super("de/augustakom/hurrican/gui/stammdaten/resources/Produkt2ProduktPanel.xml");
        this.produkt = produkt;
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblProdDest = getSwingFactory().createLabel("prod.dest");
        AKJLabel lblAendTyp = getSwingFactory().createLabel("aend.typ");
        AKJLabel lblChain = getSwingFactory().createLabel("chain");
        AKJLabel lblDesc = getSwingFactory().createLabel("desc");

        AKJButton btnNew = getSwingFactory().createButton("new.config", getActionListener(), null);
        AKJButton btnSave = getSwingFactory().createButton("save.config", getActionListener(), null);
        AKJButton btnDelete = getSwingFactory().createButton("delete.config", getActionListener(), null);
        cbProdDest = getSwingFactory().createComboBox("prod.test",
                new AKCustomListCellRenderer<>(Produkt.class, Produkt::getBezeichnungShort));
        cbAendTyp = getSwingFactory().createComboBox("aend.typ",
                new AKCustomListCellRenderer<>(PhysikaenderungsTyp.class, PhysikaenderungsTyp::getName));
        cbChain = getSwingFactory().createComboBox("chain",
                new AKCustomListCellRenderer<>(ServiceChain.class, ServiceChain::getName));
        taDesc = getSwingFactory().createTextArea("desc");
        AKJScrollPane spDesc = new AKJScrollPane(taDesc, new Dimension(250, 40));

        AKTableListener tableListener = new AKTableListener(this);
        tbMdlP2PConfig = new P2PTableModel();
        AKJTable tbP2PConfig = new AKJTable(tbMdlP2PConfig, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbP2PConfig.addMouseListener(tableListener);
        tbP2PConfig.addKeyListener(tableListener);
        tbP2PConfig.attachSorter();
        tbP2PConfig.fitTable(new int[] { 180, 120, 200, 200 });
        AKJScrollPane spTable = new AKJScrollPane(tbP2PConfig, new Dimension(500, 300));

        AKJPanel detail = new AKJPanel(new GridBagLayout());
        detail.setBorder(BorderFactory.createTitledBorder("Konfiguration"));
        detail.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        detail.add(btnNew, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        detail.add(btnSave, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(btnDelete, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        detail.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblProdDest, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        detail.add(cbProdDest, GBCFactory.createGBC(0, 0, 3, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        detail.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 7, 1, 1, 1, GridBagConstraints.NONE));
        detail.add(lblDesc, GBCFactory.createGBC(0, 0, 8, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 9, 1, 1, 1, GridBagConstraints.NONE));
        detail.add(spDesc, GBCFactory.createGBC(0, 0, 10, 1, 1, 3, GridBagConstraints.BOTH));
        detail.add(lblAendTyp, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(cbAendTyp, GBCFactory.createGBC(0, 0, 3, 2, 4, 1, GridBagConstraints.HORIZONTAL));
        detail.add(lblChain, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        detail.add(cbChain, GBCFactory.createGBC(0, 0, 3, 3, 4, 1, GridBagConstraints.HORIZONTAL));
        detail.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 11, 4, 1, 1, GridBagConstraints.BOTH));

        this.setLayout(new BorderLayout());
        this.add(spTable, BorderLayout.CENTER);
        this.add(detail, BorderLayout.SOUTH);

        enableFields(false);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            produktMap = new HashMap<>();
            serviceChainMap = new HashMap<>();
            physikaendTypMap = new HashMap<>();

            PhysikService physService = getCCService(PhysikService.class);
            List<PhysikaenderungsTyp> ptypen = physService.findPhysikaenderungsTypen();
            CollectionMapConverter.convert2Map(ptypen, physikaendTypMap, "getId", null);

            ProduktService ps = getCCService(ProduktService.class);
            List<Produkt> produkte = ps.findProdukte(false);
            CollectionMapConverter.convert2Map(produkte, produktMap, "getId", null);

            ChainService cs = getCCService(ChainService.class);
            List<ServiceChain> serviceChains = cs.findServiceChains(ServiceChain.CHAIN_TYPE_PHYSIK);
            CollectionMapConverter.convert2Map(serviceChains, serviceChainMap, "getId", null);

            List<Produkt2Produkt> prod2ProdList = ps.findProdukt2Produkte(produkt.getId());
            tbMdlP2PConfig.setData(prod2ProdList);

            cbProdDest.addItems(produkte, true, Produkt.class);
            cbAendTyp.addItems(ptypen, true, PhysikaenderungsTyp.class);
            cbChain.addItems(serviceChains, true, ServiceChain.class);
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
        if ("new.config".equals(command)) {
            newConfig();
        }
        else if ("save.config".equals(command)) {
            saveConfig();
        }
        else if ("delete.config".equals(command)) {
            deleteConfig();
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        GuiTools.cleanFields(this);
        if (details instanceof Produkt2Produkt) {
            enableFields(true);
            actConfig = (Produkt2Produkt) details;
            cbProdDest.selectItem("getId", Produkt.class, actConfig.getProdIdDest());
            cbAendTyp.selectItem("getId", PhysikaenderungsTyp.class, actConfig.getPhysikaenderungsTyp());
            cbChain.selectItem("getId", ServiceChain.class, actConfig.getChainId());
            taDesc.setText(actConfig.getDescription());
        }
        else {
            enableFields(false);
        }
    }

    /* Erstellt eine neue Produkt2Produkt-Konfiguration */
    private void newConfig() {
        actConfig = new Produkt2Produkt();
        actConfig.setProdIdSrc(produkt.getId());
        showDetails(actConfig);
    }

    /* Speichert die aktuelle Produkt2Produkt-Konfiguration */
    private void saveConfig() {
        if (actConfig == null) {
            return;
        }

        boolean insert = (actConfig.getId() == null);
        try {
            ProduktService ps = getCCService(ProduktService.class);
            actConfig.setChainId((Long) cbChain.getSelectedItemValue("getId", Long.class));
            actConfig.setProdIdDest((Long) cbProdDest.getSelectedItemValue("getId", Long.class));
            actConfig.setPhysikaenderungsTyp((Long) cbAendTyp.getSelectedItemValue("getId", Long.class));
            actConfig.setDescription(taDesc.getText());

            ps.saveProdukt2Produkt(actConfig);

            if (insert) {
                tbMdlP2PConfig.addObject(actConfig);
            }
            else {
                tbMdlP2PConfig.fireTableDataChanged();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Loescht die aktuelle Produkt2Produkt-Konfiguration */
    private void deleteConfig() {
        if (actConfig == null) {
            return;
        }

        int opt = MessageHelper.showYesNoQuestion(this, getSwingFactory().getText("delete.msg"),
                getSwingFactory().getText("delete.title"));
        if (opt != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ProduktService ps = getCCService(ProduktService.class);
            ps.deleteProdukt2Produkt(actConfig.getId());

            tbMdlP2PConfig.removeObject(actConfig);
            actConfig = null;
            showDetails(null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    private void enableFields(boolean enable) {
        if (enable) {
            GuiTools.enableComponents(new Component[] { cbAendTyp, cbChain, cbProdDest, taDesc });
        }
        else {
            GuiTools.disableComponents(new Component[] { cbAendTyp, cbChain, cbProdDest, taDesc });
        }
    }

    /**
     * TableModel fuer die Darstellung der Produkt2Produkt-Konfigurationen.
     */
    class P2PTableModel extends AKTableModel<Produkt2Produkt> {
        private static final int COL_DEST = 0;
        private static final int COL_AENDTYP = 1;
        private static final int COL_CHAIN = 2;
        private static final int COL_DESC = 3;

        private static final int COL_COUNT = 4;

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
                case COL_DEST:
                    return "Ziel-Produkt";
                case COL_AENDTYP:
                    return "Änderungstyp";
                case COL_CHAIN:
                    return "Service-Chain";
                case COL_DESC:
                    return "Beschreibung";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Produkt2Produkt p2p = getDataAtRow(row);
            if (p2p != null) {
                switch (column) {
                    case COL_DEST:
                        Produkt tmpProdukt = produktMap.get(p2p.getProdIdDest());
                        return (tmpProdukt != null) ? tmpProdukt.getBezeichnungShort() : null;
                    case COL_AENDTYP:
                        PhysikaenderungsTyp tmpTyp =
                                physikaendTypMap.get(p2p.getPhysikaenderungsTyp());
                        return (tmpTyp != null) ? tmpTyp.getName() : null;
                    case COL_CHAIN:
                        ServiceChain tmpChain = serviceChainMap.get(p2p.getChainId());
                        return (tmpChain != null) ? tmpChain.getName() : null;
                    case COL_DESC:
                        return p2p.getDescription();
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
    }
}


