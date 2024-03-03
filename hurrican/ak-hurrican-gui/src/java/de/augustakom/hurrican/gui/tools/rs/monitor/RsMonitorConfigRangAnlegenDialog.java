/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.2008 14:25:41
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Erfassung von Schwellwerten fuer den Ressourcenmonitor.
 *
 *
 */
public class RsMonitorConfigRangAnlegenDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(RsMonitorConfigRangAnlegenDialog.class);

    private AKJComboBox cbPT = null;
    private AKJComboBox cbPTAdd = null;
    private Long hvtStandortId = null;
    private String kvzNummer = null;
    private RSMonitorConfig monitorConfiguration = null;
    private RsMonitorConfigBasePanel basePanel = null;

    /**
     * Default-Const.
     */
    public RsMonitorConfigRangAnlegenDialog(Long hvtStandortId) {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorConfigRangAnlegenDialog.xml");
        this.hvtStandortId = hvtStandortId;
        this.monitorConfiguration = null;
        createGUI();
        loadData();
    }

    /**
     * Konstruktor
     */
    public RsMonitorConfigRangAnlegenDialog(RSMonitorConfig config) {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorConfigRangAnlegenDialog.xml");
        this.monitorConfiguration = config;
        if (config != null) {
            this.hvtStandortId = config.getHvtIdStandort();
            this.kvzNummer = config.getKvzNummer();
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

        AKJLabel lblPT = getSwingFactory().createLabel("physiktyp");
        AKJLabel lblPTAdd = getSwingFactory().createLabel("physiktyp.add");
        cbPT = getSwingFactory().createComboBox("physiktyp", new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName));
        cbPT.addItemListener(new PhysiktypItemListener());
        cbPTAdd = getSwingFactory().createComboBox("physiktyp.add", new AKCustomListCellRenderer<>(PhysikTyp.class, PhysikTyp::getName));

        AKJPanel center = new AKJPanel(new GridBagLayout());
        center.add(lblPT, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(cbPT, GBCFactory.createGBC(100, 0, 1, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        center.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        center.add(lblPTAdd, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        center.add(cbPTAdd, GBCFactory.createGBC(100, 0, 1, 1, 2, 1, GridBagConstraints.HORIZONTAL));

        basePanel = new RsMonitorConfigBasePanel();

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(center, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(basePanel, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    protected void validateSaveButton() {
    }

    @Override
    protected void execute(String command) {
    }

    /*
     * Laedt die Daten fuer die beide Comboboxen
     */
    private void loadData() {
        if (hvtStandortId != null) {
            try {
                PhysikService ps = getCCService(PhysikService.class);
                List<PhysikTyp> physiktypen = ps.findPhysikTypen();
                cbPT.addItems(physiktypen, true, PhysikTyp.class);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getParent(), e);
            }
        }
    }

    public void setModel(Observable model) {
        if ((model != null) && (model instanceof RSMonitorConfig)) {
            this.monitorConfiguration = (RSMonitorConfig) model;
            try {
                // Lade Physiktypen und selektiere betreffendes Objekt
                PhysikService ps = getCCService(PhysikService.class);
                PhysikTyp pt = ps.findPhysikTyp(monitorConfiguration.getPhysiktyp());
                PhysikTyp ptAdd = ps.findPhysikTyp(monitorConfiguration.getPhysiktypAdd());
                if (pt != null) {
                    cbPT.selectItem("getName", PhysikTyp.class, pt.getName());
                }
                if (ptAdd != null) {
                    cbPTAdd.selectItem("getName", PhysikTyp.class, ptAdd.getName());
                }

                // Setze Daten des BasePanels
                basePanel.setData(monitorConfiguration);

                // Sperre ComboBoxen
                cbPT.setEnabled(Boolean.FALSE);
                cbPTAdd.setEnabled(Boolean.FALSE);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getParent(), e);
            }
        }
    }

    public Object getModel() {
        return null;
    }

    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void update(Observable arg0, Object arg1) {
    }

    @Override
    protected void doSave() {
        try {
            if (hvtStandortId == null) {
                MessageHelper.showErrorDialog(getParent(), new HurricanGUIException("Kein HVT-Standort angegeben."));
            }

            monitorConfiguration = basePanel.getData();
            monitorConfiguration.setPhysiktyp(((PhysikTyp) cbPT.getSelectedItem()).getId());
            monitorConfiguration.setPhysiktypAdd(((PhysikTyp) cbPTAdd.getSelectedItem()).getId());
            monitorConfiguration.setHvtIdStandort(hvtStandortId);
            monitorConfiguration.setKvzNummer(kvzNummer);
            monitorConfiguration.setMonitorType(RSMonitorRun.RS_REF_TYPE_RANG_MONITOR);

            MonitorService ms = getCCService(MonitorService.class);
            ms.saveRsMonitorConfig(monitorConfiguration, HurricanSystemRegistry.instance().getSessionId());
            prepare4Close();
            setValue(monitorConfiguration);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /*
     * ItemListener fuer die Physiktyp-ComboBox. <br>
     * Je nach Auswahl von 'Physiktyp' werden die moeglichen Child-Physiktypen
     * geladen.
     */
    class PhysiktypItemListener implements ItemListener {
        /**
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            if ((e.getSource() == cbPT) && (e.getStateChange() == ItemEvent.SELECTED)) {
                cbPTAdd.removeAllItems();
                PhysikTyp pt = (PhysikTyp) cbPT.getSelectedItem();
                if ((pt != null) && (pt.getId() != null)) {
                    try {
                        PhysikService ps = getCCService(PhysikService.class);
                        List<Long> ptIds = new ArrayList<Long>();
                        ptIds.add(pt.getId());
                        List<PhysikTyp> childPTs = ps.findPhysikTypen4ParentPhysik(ptIds);
                        cbPTAdd.addItems(childPTs, true, PhysikTyp.class);
                    }
                    catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        MessageHelper.showErrorDialog(getMainFrame(), ex);
                    }
                }
            }
        }
    }
}


