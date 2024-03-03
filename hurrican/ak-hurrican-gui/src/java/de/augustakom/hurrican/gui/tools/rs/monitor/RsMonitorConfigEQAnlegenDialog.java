/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2008 14:25:41
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HVTToolService;
import de.augustakom.hurrican.service.cc.MonitorService;


/**
 * Erfassung von Schwellwerten fuer den Ressourcenmonitor.
 *
 *
 */
public class RsMonitorConfigEQAnlegenDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(RsMonitorConfigEQAnlegenDialog.class);

    private AKJComboBox cbUEVT = null;
    private AKJComboBox cbRangSS = null;
    private Long hvtStandortId = null;
    private RSMonitorConfig config = null;
    private RsMonitorConfigBasePanel basePanel = null;

    /**
     * Default-Const.
     */
    public RsMonitorConfigEQAnlegenDialog(Long hvtStandortId) {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorConfigEQAnlegenDialog.xml");
        this.hvtStandortId = hvtStandortId;
        this.config = null;
        createGUI();
        loadData();
    }

    /**
     * Konstruktor
     */
    public RsMonitorConfigEQAnlegenDialog(RSMonitorConfig config) {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorConfigEQAnlegenDialog.xml");
        this.config = config;
        if (config != null) {
            this.hvtStandortId = config.getHvtIdStandort();
        }
        createGUI();
        loadData();

        try {
            setModel(config);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getParent(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblUevt = getSwingFactory().createLabel("uevt");
        AKJLabel lblSSType = getSwingFactory().createLabel("rang.ss");
        cbUEVT = getSwingFactory().createComboBox("uevt");
        cbUEVT.setRenderer(new AKCustomListCellRenderer<>(UEVT.class, UEVT::getUevt));

        cbRangSS = getSwingFactory().createComboBox("rang.ss");

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(lblUevt, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(cbUEVT, GBCFactory.createGBC(100, 0, 1, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        center.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        center.add(lblSSType, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(cbRangSS, GBCFactory.createGBC(100, 0, 1, 1, 2, 1, GridBagConstraints.HORIZONTAL));

        basePanel = new RsMonitorConfigBasePanel();

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(center, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(basePanel, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#validateSaveButton()
     */
    @Override
    protected void validateSaveButton() {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /*
     * Laedt die Daten fuer die beide Comboboxen
     */
    private void loadData() {
        if (hvtStandortId != null) {
            try {
                // Setze Uevt-ComboBox
                HVTService hs = getCCService(HVTService.class);
                List<UEVT> uevts = hs.findUEVTs4HVTStandort(hvtStandortId);
                if (CollectionTools.isNotEmpty(uevts)) {
                    cbUEVT.addItems(uevts);
                    if (uevts.size() == 1) {
                        cbUEVT.selectItem("getUevt", UEVT.class, uevts.get(0));
                    }
                }

                // Setze SSType-ComboBox
                HVTToolService hts = getCCService(HVTToolService.class);
                List<RangSchnittstelle> sstypes = hts.findAvailableSchnittstellen4HVT(hvtStandortId);
                if (CollectionTools.isNotEmpty(sstypes)) {
                    cbRangSS.addItems(sstypes);
                    if (sstypes.size() == 1) {
                        cbRangSS.selectItem(sstypes.get(0));
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getParent(), e);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) {
        if ((model != null) && (model instanceof RSMonitorConfig)) {
            this.config = (RSMonitorConfig) model;
            cbUEVT.selectItem("getUevt", UEVT.class, config.getEqUEVT());
            cbRangSS.setSelectedItem(RangSchnittstelle.valueOf(config.getEqRangSchnittstelle()));

            // Setze Daten des BasePanel
            basePanel.setData(config);

            // Deaktiviere ComboBoxes
            cbUEVT.setEnabled(Boolean.TRUE);
            cbRangSS.setEnabled(Boolean.TRUE);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            if (hvtStandortId == null) {
                MessageHelper.showErrorDialog(getParent(), new HurricanGUIException("Kein HVT-Standort angegeben."));
            }

            config = basePanel.getData();
            config.setEqRangSchnittstelle((cbRangSS.getSelectedItem() != null)
                    ? ((RangSchnittstelle) cbRangSS.getSelectedItem()).name() : null);
            config.setEqUEVT(((UEVT) cbUEVT.getSelectedItem()).getUevt());
            config.setHvtIdStandort(hvtStandortId);
            config.setMonitorType(RSMonitorRun.RS_REF_TYPE_EQ_MONITOR);

            MonitorService ms = getCCService(MonitorService.class);
            ms.saveRsMonitorConfig(config, HurricanSystemRegistry.instance().getSessionId());
            prepare4Close();
            setValue(Integer.valueOf(OK_OPTION));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

}


