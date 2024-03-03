/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2009 15:28:37
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import javax.swing.event.*;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Frame fuer die Administration der Carrier-Daten.
 *
 *
 */
public class CarrierDataAdminFrame extends AbstractAdminFrame {

    private final static String CARRIER = "Carrier";
    private final static String CARRIER_CONTACT = "Carrier-Kontakt";
    private final static String CARRIER_KENNUNG = "Carrier-Kennung";
    private final static String CARRIER_MAPPING = "Carrier-Mapping";
    private final static String CARRIER_DATEN = "Carrier-Daten";

    private AKJTabbedPane tabbedPane = null;
    private CarrierDataAdminPanel carrierDataAdminPanel = null;
    private CarrierContactAdminPanel carrierContactAdminPanel = null;
    private CarrierIdentifierAdminPanel carrierIdentifierAdminPanel = null;
    private CarrierMappingAdminPanel carrierMappingAdminPanel = null;

    private boolean guiCreated = false;

    /**
     * Standardkonstruktor.
     */
    public CarrierDataAdminFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminFrame#getAdminPanels()
     */
    protected AbstractAdminPanel[] getAdminPanels() {
        if (tabbedPane.getSelectedIndex() == 0) {
            return new AbstractAdminPanel[] { carrierDataAdminPanel };
        }
        else if (tabbedPane.getSelectedIndex() == 1) {
            return new AbstractAdminPanel[] { carrierContactAdminPanel };
        }
        else if (tabbedPane.getSelectedIndex() == 2) {
            return new AbstractAdminPanel[] { carrierIdentifierAdminPanel };
        }
        else if (tabbedPane.getSelectedIndex() == 3) {
            return new AbstractAdminPanel[] { carrierMappingAdminPanel };
        }

        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    protected final void createGUI() {
        setTitle(CARRIER_DATEN);

        carrierDataAdminPanel = new CarrierDataAdminPanel();
        carrierContactAdminPanel = new CarrierContactAdminPanel();
        carrierIdentifierAdminPanel = new CarrierIdentifierAdminPanel();
        carrierMappingAdminPanel = new CarrierMappingAdminPanel();

        getSaveButton().getAccessibleContext().setAccessibleName("save.carrier"); // Default
        manageGUI(new AKManageableComponent[] { btnSave, btnNew });

        tabbedPane = new AKJTabbedPane();
        tabbedPane.addChangeListener(new TabbedPaneChangeListener());
        tabbedPane.addTab(CARRIER, carrierDataAdminPanel);
        tabbedPane.addTab(CARRIER_CONTACT, carrierContactAdminPanel);
        tabbedPane.addTab(CARRIER_KENNUNG, carrierIdentifierAdminPanel);
        tabbedPane.addTab(CARRIER_MAPPING, carrierMappingAdminPanel);

        getChildPanel().add(tabbedPane, BorderLayout.CENTER);
        pack();
        guiCreated = true;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
     */
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
                if (tabbedPane.getSelectedComponent() instanceof AKDataLoaderComponent) {
                    ((AKDataLoaderComponent) tabbedPane.getSelectedComponent()).loadData();
                }

                if (tabbedPane.getSelectedIndex() == 0) {
                    getSaveButton().getAccessibleContext().setAccessibleName("save.carrier");
                }
                else if (tabbedPane.getSelectedIndex() == 1) {
                    getSaveButton().getAccessibleContext().setAccessibleName("save.carrier.contact");
                }
                else if (tabbedPane.getSelectedIndex() == 2) {
                    getSaveButton().getAccessibleContext().setAccessibleName("save.carrier.kennung");
                }
                else if (tabbedPane.getSelectedIndex() == 3) {
                    getSaveButton().getAccessibleContext().setAccessibleName("save.carrier.mapping");
                }

                manageGUI(new AKManageableComponent[] { getSaveButton() });
            }
        }
    }
}
