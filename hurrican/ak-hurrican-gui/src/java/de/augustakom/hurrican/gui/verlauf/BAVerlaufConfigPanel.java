/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.12.2006 17:15:47
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.BAVerlaufAbtConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel fuer die Konfiguration der Bauauftragssteuerung.
 *
 *
 */
public class BAVerlaufConfigPanel extends AbstractServicePanel implements AKDataLoaderComponent, AKTableOwner, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(BAVerlaufConfigPanel.class);

    private AKJComboBox cbSearchProd = null;
    private AKJComboBox cbSearchAnlass = null;
    private AKJComboBox cbProdukt = null;
    private AKJComboBox cbBAAnlass = null;
    private AKJComboBox cbAbtConfig = null;
    private AKJCheckBox cbAutomatic = null;
    private AKJCheckBox cbCpsNecessary = null;
    private AKReferenceAwareTableModel<BAVerlaufConfig> tbMdlResult = null;
    private AKJPanel pnlConfig = null;

    private BAVerlaufConfig actConfig = null;
    private BAVerlaufZusatzPanel zusatzPnl = null;

    /**
     * Default-Konstruktor.
     */
    public BAVerlaufConfigPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BAVerlaufConfigPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblSearchProd = getSwingFactory().createLabel("search.produkt");
        AKJLabel lblSearchAnlass = getSwingFactory().createLabel("search.anlass");
        AKJLabel lblBAAnlass = getSwingFactory().createLabel("ba.anlass");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblAbtConfig = getSwingFactory().createLabel("abt.config");
        AKJLabel lblAutomatic = getSwingFactory().createLabel("automatic");
        AKJLabel lblCpsNecessary = getSwingFactory().createLabel("cps.necessary");

        AKJButton btnSearch = getSwingFactory().createButton("search", getActionListener());
        AKJButton btnSave = getSwingFactory().createButton("save.config", getActionListener(), null);
        AKJButton btnNew = getSwingFactory().createButton("new.config", getActionListener(), null);
        AKJButton btnDelete = getSwingFactory().createButton("delete.config", getActionListener(), null);

        ListCellRenderer baAnlassRenderer = new AKCustomListCellRenderer<>(BAVerlaufAnlass.class, BAVerlaufAnlass::getName);
        ListCellRenderer prodRenderer = new AKCustomListCellRenderer<>(Produkt.class, Produkt::getBezeichnungShort);
        cbSearchAnlass = getSwingFactory().createComboBox("search.anlass", baAnlassRenderer);
        cbSearchProd = getSwingFactory().createComboBox("search.produkt", prodRenderer);
        cbBAAnlass = getSwingFactory().createComboBox("ba.anlass", baAnlassRenderer);
        cbProdukt = getSwingFactory().createComboBox("produkt", prodRenderer);
        cbAbtConfig = getSwingFactory().createComboBox("abt.config",
                new AKCustomListCellRenderer<>(BAVerlaufAbtConfig.class, BAVerlaufAbtConfig::getName));
        cbAutomatic = getSwingFactory().createCheckBox("automatic");
        cbAutomatic.addItemListener(this);
        cbCpsNecessary = getSwingFactory().createCheckBox("cps.necessary", false);

        AKJPanel pnlSearch = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("search"));
        pnlSearch.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        pnlSearch.add(lblSearchProd, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlSearch.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlSearch.add(cbSearchProd, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlSearch.add(lblSearchAnlass, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlSearch.add(cbSearchAnlass, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlSearch.add(btnSearch, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL, 20));
        pnlSearch.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlResult = new AKReferenceAwareTableModel<BAVerlaufConfig>(
                new String[] { "BA-Anlass", "Produkt", "Abteilungen" },
                new String[] { "baVerlAnlass", "prodId", "abtConfigId" },
                new Class[] { String.class, String.class, String.class });
        AKJTable tbResult = new AKJTable(tbMdlResult, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbResult.attachSorter();
        tbResult.addTableListener(this);
        tbResult.fitTable(new int[] { 150, 150, 200 });
        AKJScrollPane spTable = new AKJScrollPane(tbResult, new Dimension(400, 300));

        zusatzPnl = new BAVerlaufZusatzPanel();

        pnlConfig = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("config"));
        pnlConfig.add(btnNew, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        pnlConfig.add(btnSave, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        pnlConfig.add(btnDelete, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        pnlConfig.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(lblProdukt, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        pnlConfig.add(cbProdukt, GBCFactory.createGBC(0, 0, 3, 1, 4, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(lblBAAnlass, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(cbBAAnlass, GBCFactory.createGBC(0, 0, 3, 2, 4, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(lblAbtConfig, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(cbAbtConfig, GBCFactory.createGBC(0, 0, 3, 3, 4, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(lblAutomatic, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(cbAutomatic, GBCFactory.createGBC(0, 0, 3, 4, 4, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(lblCpsNecessary, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(cbCpsNecessary, GBCFactory.createGBC(0, 0, 3, 5, 4, 1, GridBagConstraints.HORIZONTAL));
        pnlConfig.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 7, 6, 1, 2, GridBagConstraints.VERTICAL, 40));
        pnlConfig.add(zusatzPnl, GBCFactory.createGBC(0, 0, 8, 0, 1, 6, GridBagConstraints.BOTH));
        pnlConfig.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 9, 7, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new BorderLayout());
        this.add(pnlSearch, BorderLayout.NORTH);
        this.add(spTable, BorderLayout.CENTER);
        this.add(pnlConfig, BorderLayout.SOUTH);

        enableGUI(false);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            BAConfigService bas = getCCService(BAConfigService.class);
            List<BAVerlaufAnlass> anlaesse = bas.findBAVerlaufAnlaesse(Boolean.TRUE, null);
            cbSearchAnlass.addItems(anlaesse, true, BAVerlaufAnlass.class);
            cbBAAnlass.addItems(anlaesse, true, BAVerlaufAnlass.class);

            List<BAVerlaufAbtConfig> abtConfigs = bas.findBAVerlaufAbtConfigs();
            cbAbtConfig.addItems(abtConfigs, true, BAVerlaufAbtConfig.class);

            ProduktService ps = getCCService(ProduktService.class);
            List<Produkt> produkte = ps.findProdukte(false);
            cbSearchProd.addItems(produkte, true, Produkt.class);
            cbProdukt.addItems(produkte, true, Produkt.class);

            Map<Long, BAVerlaufAnlass> anlassMap = new HashMap<Long, BAVerlaufAnlass>();
            CollectionMapConverter.convert2Map(anlaesse, anlassMap, "getId", null);

            Map<Long, Produkt> produktMap = new HashMap<Long, Produkt>();
            CollectionMapConverter.convert2Map(produkte, produktMap, "getId", null);

            Map<Long, BAVerlaufAbtConfig> abtConfMap = new HashMap<Long, BAVerlaufAbtConfig>();
            CollectionMapConverter.convert2Map(abtConfigs, abtConfMap, "getId", null);

            tbMdlResult.addReference(0, anlassMap, "name");
            tbMdlResult.addReference(1, produktMap, "anschlussart");
            tbMdlResult.addReference(2, abtConfMap, "name");
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
        if ("search".equals(command)) {
            doSearch();
        }
        else if ("new.config".equals(command)) {
            createNewConfig();
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
        actConfig = null;
        if (details instanceof BAVerlaufConfig) {
            actConfig = (BAVerlaufConfig) details;
        }

        GuiTools.cleanFields(pnlConfig);
        if (actConfig != null) {
            cbBAAnlass.selectItem("getId", BAVerlaufAnlass.class, actConfig.getBaVerlAnlass());
            cbProdukt.selectItem("getId", Produkt.class, actConfig.getProdId());
            cbAbtConfig.selectItem("getId", BAVerlaufAbtConfig.class, actConfig.getAbtConfigId());
            cbAutomatic.setSelected(actConfig.getAutoVerteilen());
            cbCpsNecessary.setSelected(actConfig.getCpsNecessary());
            enableGUI(true);
        }
        else {
            enableGUI(false);
        }
        zusatzPnl.showDetails(details);
    }

    /* Fuehrt die Suche nach den Bauauftragskonfigurationen durch. */
    private void doSearch() {
        try {
            showDetails(null);

            BAConfigService bas = getCCService(BAConfigService.class);
            List<BAVerlaufConfig> configs = bas.findBAVerlaufConfigs(
                    (Long) cbSearchAnlass.getSelectedItemValue("getId", Long.class),
                    (Long) cbSearchProd.getSelectedItemValue("getId", Long.class));
            tbMdlResult.setData(configs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Legt eine neue Konfiguration an. */
    private void createNewConfig() {
        showDetails(new BAVerlaufConfig());
    }

    /* Speichert die aktuelle Konfiguration. */
    private void saveConfig() {
        try {
            if (actConfig == null) {
                throw new HurricanGUIException("Kein Konfigurationsobjekt ausgewaehlt!");
            }

            actConfig.setBaVerlAnlass((Long) cbBAAnlass.getSelectedItemValue("getId", Long.class));
            actConfig.setProdId((Long) cbProdukt.getSelectedItemValue("getId", Long.class));
            actConfig.setAbtConfigId((Long) cbAbtConfig.getSelectedItemValue("getId", Long.class));
            actConfig.setAutoVerteilen(cbAutomatic.isSelectedBoolean(Boolean.FALSE));
            actConfig.setCpsNecessary(cbCpsNecessary.isSelectedBoolean(Boolean.FALSE));

            BAConfigService bas = getCCService(BAConfigService.class);
            BAVerlaufConfig config =
                    bas.saveBAVerlaufConfig(actConfig, HurricanSystemRegistry.instance().getSessionId());

            List<BAVerlaufConfig> updatedData = tbMdlResult.getData().stream().map(c -> c == actConfig ? config : c).collect(Collectors.toList());
            tbMdlResult.setData(updatedData);

            showDetails(config);
            tbMdlResult.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Loescht die aktuelle Konfiguration. */
    private void deleteConfig() {
        try {
            if (actConfig == null) {
                throw new HurricanGUIException("Kein Konfigurationsobjekt ausgewaehlt!");
            }

            int option = MessageHelper.showYesNoQuestion(getMainFrame(), "Konfiguration löschen?", "Löschen?");
            if (option == JOptionPane.YES_OPTION) {
                BAConfigService bas = getCCService(BAConfigService.class);
                bas.deleteBAVerlaufConfig(actConfig, HurricanSystemRegistry.instance().getSessionId());
            }
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

    private void enableGUI(boolean enable) {
        GuiTools.enableComponents(
                new Component[] { cbBAAnlass, cbProdukt, cbAbtConfig }, enable, true);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cbAutomatic) {
            cbCpsNecessary.setEnabled(cbAutomatic.isSelected());
            cbCpsNecessary.setSelected(cbAutomatic.isSelected());
        }
    }
}


