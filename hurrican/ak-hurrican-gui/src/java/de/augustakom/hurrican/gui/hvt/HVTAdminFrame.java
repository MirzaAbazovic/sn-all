/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 15:28:37
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.event.*;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;

/**
 * Frame fuer die Administration der HVTs.
 *
 *
 */
public class HVTAdminFrame extends AbstractAdminFrame {

    private AKJTabbedPane tabbedPane = null;
    private HVTGruppenAdminPanel hvtGruppenAdminPanel = null;
    private HVTStandortAdminPanel hvtStandortAdminPanel = null;

    private boolean guiCreated = false;

    /**
     * Standardkonstruktor.
     */
    public HVTAdminFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminFrame#getAdminPanels()
     */
    @Override
    protected AbstractAdminPanel[] getAdminPanels() {
        if (tabbedPane.getSelectedIndex() == 0) {
            return new AbstractAdminPanel[] {hvtStandortAdminPanel};
        }
        else if (tabbedPane.getSelectedIndex() == 1) {
            return new AbstractAdminPanel[] {hvtGruppenAdminPanel};
        }

        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle("HVTs");

        hvtGruppenAdminPanel = new HVTGruppenAdminPanel();
        hvtStandortAdminPanel = new HVTStandortAdminPanel(hvtGruppenAdminPanel);
        hvtGruppenAdminPanel.setHvtStandortAdminPanel(hvtStandortAdminPanel);
        tabbedPane = new AKJTabbedPane();
        tabbedPane.addChangeListener(new TabbedPaneChangeListener());
        tabbedPane.addTab("HVT-Standorte", hvtStandortAdminPanel);
        tabbedPane.addTab("HVT-Gruppen", hvtGruppenAdminPanel);

        final HVTAdminSearchPanel searchPanel = new HVTAdminSearchPanel() {
            @Override
            public void onSearchResultFetched(Map<Long, HVTGruppe> hvtGruppenMap, List<HVTGruppe> hvtGruppenList,
                    List<HVTStandort> hvtStandorts) {
                hvtStandortAdminPanel.updateData(hvtGruppenMap, hvtGruppenList, hvtStandorts);
                hvtGruppenAdminPanel.updateData(hvtGruppenMap, hvtGruppenList, hvtStandorts);
            }
        };
        final AKJPanel leftPanel = new AKJPanel(new BorderLayout());
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        getChildPanel().add(tabbedPane, BorderLayout.CENTER);
        getChildPanel().add(leftPanel, BorderLayout.WEST);

        pack();
        guiCreated = true;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * Listener fuer die Tabbed-Pane.
     */
    class TabbedPaneChangeListener implements ChangeListener {
        /**
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        public void stateChanged(ChangeEvent e) {
            if (guiCreated) {
                if (tabbedPane.getSelectedIndex() == 0) {
                    getSaveButton().getAccessibleContext().setAccessibleName("save.hvt.standort");
                    hvtStandortAdminPanel.loadData();
                }
                if (tabbedPane.getSelectedIndex() == 1) {
                    getSaveButton().getAccessibleContext().setAccessibleName("save");
                    hvtGruppenAdminPanel.loadData();
                }
                manageGUI(new AKManageableComponent[] {getSaveButton()});
            }
        }
    }

    /**
     * @see InternalFrameListener#internalFrameOpened(InternalFrameEvent)
     */
    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
        // avoid automatic data load, it must be done by the button
    }
}


