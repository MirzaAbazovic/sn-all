/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.2008 14:19:09
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.beans.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * Panel fuer die Konfiguration des Ressourcenmonitors.
 *
 *
 */
public class RsMonitorConfigPanel extends AbstractServicePanel implements PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(RsMonitorConfigPanel.class);

    private AKReferenceField rfHvt = null;
    private AKJTabbedPane tabbedPane = null;


    /**
     * Default-Konstruktor.
     */
    public RsMonitorConfigPanel() {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorConfigPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblHvt = getSwingFactory().createLabel("hvt");
        rfHvt = getSwingFactory().createReferenceField("hvt");
        rfHvt.addPropertyChangeListener(this);

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder("Suchparameter"));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(lblHvt, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        top.add(rfHvt, GBCFactory.createGBC(100, 0, 3, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(40, 40, 6, 2, 1, 1, GridBagConstraints.BOTH));

        RsMonitorConfigEQPanel eqPanel = new RsMonitorConfigEQPanel();
        RsMonitorConfigRangPanel rangPanel = new RsMonitorConfigRangPanel();

        tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab("Port-Überwachung", eqPanel);
        tabbedPane.addTab("Rangierungs-Überwachung", rangPanel);

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /*
     *  FindService dem Reference-Field zuordnen
     */
    private void loadData() {
        try {
            ISimpleFindService sfs = getCCService(QueryCCService.class);
            rfHvt.setFindService(sfs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Long hvtGruppeId = rfHvt.getReferenceIdAs(Long.class);
        if (hvtGruppeId != null) {
            try {
                HVTService hs = getCCService(HVTService.class);
                List<HVTStandort> list = hs.findHVTStandorte4Gruppe(hvtGruppeId, Boolean.TRUE);
                if (CollectionTools.isEmpty(list) || (list.size() > 1)) {
                    MessageHelper.showErrorDialog(getParent(), new HurricanGUIException("HVT-Standort kann nicht eindeutig ermittelt werden"));
                }
                Component[] panels = tabbedPane.getComponents();
                for (Component comp : panels) {
                    AbstractDataPanel panel = (AbstractDataPanel) comp;
                    if (panel != null) {
                        panel.setModel(list.get(0));
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
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }
}


