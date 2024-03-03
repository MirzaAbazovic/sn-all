/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.06.2009 11:48:51
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.command.ServiceChain;
import de.augustakom.hurrican.model.cc.cps.CPSDataChainConfig;
import de.augustakom.hurrican.service.cc.CPSConfigService;
import de.augustakom.hurrican.service.cc.ChainService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Panel fuer die Verwaltung der CPS-Steuerung des Produkts.
 *
 *
 */
public class Produkt2CPSPanel extends AbstractServicePanel implements AKModelOwner,
        AKDataLoaderComponent, FocusListener {

    private static final Logger LOGGER = Logger.getLogger(Produkt2CPSPanel.class);

    private AKJCheckBox chbCpsProv = null;
    private AKJTextField tfCpsProdName = null;
    private AKJTextField tfCpsAccountType = null;
    private AKJCheckBox chbCpsAutoCreation = null;
    private AKJCheckBox chbCpsDSLProduct = null;
    private AKJCheckBox chbCpsMultiDraht = null;
    private AKReferenceAwareTableModel<CPSDataChainConfig> tbMdlConfig = null;
    private AKJTable tbConfig = null;
    private AKJCheckBox chbCpsIPDefault = null;

    /* Aktuelles Modell, das ueber die Methode showDetails(Object) gesetzt wird. */
    private Produkt model = null;

    /**
     * Default-Const.
     */
    public Produkt2CPSPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/Produkt2CPSPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblCpsProv = getSwingFactory().createLabel("cps.provisioning");
        AKJLabel lblCpsProdName = getSwingFactory().createLabel("cps.product.name");
        AKJLabel lblCpsAccountType = getSwingFactory().createLabel("cps.account.type");
        AKJLabel lblCpsAutoCreation = getSwingFactory().createLabel("cps.auto.creation");
        AKJLabel lblCpsDSLProduct = getSwingFactory().createLabel("cps.dsl.product");
        AKJLabel lblCPSMultiDraht = getSwingFactory().createLabel("cps.multi.draht");
        AKJLabel lblCpsIPDefault = getSwingFactory().createLabel("cps.ip.default");

        chbCpsProv = getSwingFactory().createCheckBox("cps.provisioning");
        tfCpsProdName = getSwingFactory().createTextField("cps.product.name");
        tfCpsProdName.addFocusListener(this);
        tfCpsAccountType = getSwingFactory().createTextField("cps.account.type");
        chbCpsAutoCreation = getSwingFactory().createCheckBox("cps.auto.creation");
        chbCpsDSLProduct = getSwingFactory().createCheckBox("cps.dsl.product");
        chbCpsMultiDraht = getSwingFactory().createCheckBox("cps.multi.draht");
        chbCpsIPDefault = getSwingFactory().createCheckBox("cps.ip.default");
        tbMdlConfig = new AKReferenceAwareTableModel<CPSDataChainConfig>(
                new String[] { "SO-Type", "Chain" },
                new String[] { "serviceOrderTypeRefId", "serviceChainId" },
                new Class[] { String.class, String.class });
        tbConfig = new AKJTable(tbMdlConfig, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbConfig.fitTable(new int[] { 100, 200 });

        AKJButton btnAddConfig = getSwingFactory().createButton("add.cps.config", getActionListener(), null);
        AKJButton btnDelConfig = getSwingFactory().createButton("del.cps.config", getActionListener(), null);

        AKJPanel left = new AKJPanel(new GridBagLayout(), "CPS-Provisionierung");
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblCpsProv, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        left.add(chbCpsProv, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCpsProdName, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfCpsProdName, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCpsAccountType, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfCpsAccountType, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCpsAutoCreation, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbCpsAutoCreation, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCpsDSLProduct, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbCpsDSLProduct, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCPSMultiDraht, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbCpsMultiDraht, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCpsIPDefault, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(chbCpsIPDefault, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 7, 1, 1, GridBagConstraints.VERTICAL));

        AKJScrollPane spTable = new AKJScrollPane(tbConfig, new Dimension(330, 150));
        AKJPanel right = new AKJPanel(new GridBagLayout(), "CPS-Konfig");
        right.add(spTable, GBCFactory.createGBC(100, 100, 0, 0, 1, 3, GridBagConstraints.BOTH));
        right.add(btnAddConfig, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(btnDelConfig, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.NONE));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 2, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(right, GBCFactory.createGBC(0, 100, 1, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        // Bitte alle GUI Komponenten auf Rechte prüfen, da diverse User nur auf wenige Komponenten rechte haben!
        manageGUI(chbCpsProv, tfCpsProdName, tfCpsProdName, tfCpsAccountType,
                chbCpsAutoCreation, chbCpsDSLProduct, chbCpsMultiDraht, chbCpsIPDefault,
                tbConfig, btnAddConfig, btnDelConfig);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ReferenceService rs = getCCService(ReferenceService.class);
            List<Reference> soTypeRefs = rs.findReferencesByType(
                    Reference.REF_TYPE_CPS_SERVICE_ORDER_TYPE, Boolean.FALSE);
            tbMdlConfig.addReference(0, CollectionMapConverter.convert2Map(soTypeRefs, "getId", null), "strValue");

            ChainService chs = getCCService(ChainService.class);
            List<ServiceChain> chains = chs.findServiceChains(ServiceChain.CHAIN_TYPE_CPS);
            tbMdlConfig.addReference(1, CollectionMapConverter.convert2Map(chains, "getId", null), "name");
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
        if ("add.cps.config".equals(command)) {
            addCPSConfig();
        }
        else if ("del.cps.config".equals(command)) {
            deleteCPSConfig();
        }
    }

    /* Oeffnet einen Dialog, ueber den ein neues CPSDataChainConfig-Objekt angelegt werden kann. */
    private void addCPSConfig() {
        try {
            ProduktCPSDataChainConfigDialog dlg = new ProduktCPSDataChainConfigDialog(model);
            Object config = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            if (config instanceof CPSDataChainConfig) {
                tbMdlConfig.addObject((CPSDataChainConfig) config);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Loescht die ausgewaehlte CPS-Config. */
    private void deleteCPSConfig() {
        try {
            Object selection = ((AKMutableTableModel) tbConfig.getModel())
                    .getDataAtRow(tbConfig.getSelectedRow());
            if (selection instanceof CPSDataChainConfig) {
                int choose = MessageHelper.showYesNoQuestion(getMainFrame(),
                        "Selektierte CPS-Konfiguration löschen?", "Löschen?");
                if (choose == JOptionPane.YES_OPTION) {
                    CPSConfigService ccs = getCCService(CPSConfigService.class);
                    ccs.deleteCPSDataChainConfig((CPSDataChainConfig) selection);

                    tbMdlConfig.removeObject(selection);
                }
            }
            else {
                throw new HurricanGUIException("Keine CPS-Konfiguration gewaehlt.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) {
        this.model = (model instanceof Produkt) ? (Produkt) model : null;
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() {
        GuiTools.cleanFields(this);

        // Lade Daten aus Model in GUI-Komponenten
        if (model != null) {
            chbCpsProv.setSelected(model.getCpsProvisioning());
            tfCpsProdName.setText(model.getCpsProductName());
            tfCpsAccountType.setText(model.getCpsAccountType());
            chbCpsAutoCreation.setSelected(model.getCpsAutoCreation());
            chbCpsDSLProduct.setSelected(model.getCpsDSLProduct());
            chbCpsMultiDraht.setSelected(model.getCpsMultiDraht());
            chbCpsIPDefault.setSelected(model.getCpsIPDefault());

            try {
                CPSConfigService ccs = getCCService(CPSConfigService.class);
                List<CPSDataChainConfig> configs = null;
                if (model.getId() != null) {
                    configs = ccs.findCPSDataChainConfigs(model.getId());
                }
                tbMdlConfig.setData(configs);
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
    public void saveModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        model.setCpsProvisioning(chbCpsProv.isSelectedBoolean());
        model.setCpsProductName(tfCpsProdName.getText(null));
        model.setCpsAccountType(tfCpsAccountType.getText(null));
        model.setCpsAutoCreation(chbCpsAutoCreation.isSelectedBoolean());
        model.setCpsDSLProduct(chbCpsDSLProduct.isSelectedBoolean());
        model.setCpsMultiDraht(chbCpsMultiDraht.isSelectedBoolean());
        model.setCpsIPDefault(chbCpsIPDefault.isSelectedBoolean());
        return model;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    @Override
    public void focusGained(FocusEvent e) {
    }

    /**
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    @Override
    public void focusLost(FocusEvent e) {
        // Whitespaces aus CPS Produkt-Namen entfernen
        if (e.getSource() == tfCpsProdName) {
            tfCpsProdName.setText(StringUtils.deleteWhitespace(tfCpsProdName.getText()));
        }
    }

}


