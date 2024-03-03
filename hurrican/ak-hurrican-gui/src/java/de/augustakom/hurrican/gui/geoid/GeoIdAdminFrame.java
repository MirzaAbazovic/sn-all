/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2011 09:43:33
 */

package de.augustakom.hurrican.gui.geoid;

import java.awt.*;
import java.util.*;
import javax.swing.event.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Admin-Frame fuer die Verwaltung der Geo IDs.
 */
public class GeoIdAdminFrame extends AbstractAdminFrame {

    private AKJTabbedPane tabbedPane = null;
    private GeoIdSearchPanel geoIdSearchPanel = null;
    private GeoIdClarificationPanel geoIdClarificationPanel = null;

    public GeoIdAdminFrame() {
        super("de/augustakom/hurrican/gui/geoid/resources/GeoIdAdminFrame.xml", false);
        createGUI();
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected AbstractAdminPanel[] getAdminPanels() {
        return null;
    }

    @Override
    protected void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        GeoIdAdminPanel geoIdAdminPanel = new GeoIdAdminPanel();
        geoIdSearchPanel = new GeoIdSearchPanel();
        geoIdSearchPanel.addObserver(geoIdAdminPanel);
        geoIdClarificationPanel = new GeoIdClarificationPanel();
        geoIdClarificationPanel.addObserver(geoIdAdminPanel);

        tabbedPane = new AKJTabbedPane();
        tabbedPane.addChangeListener(new TabbedPaneChangeListener());
        tabbedPane.addTab(getSwingFactory().getText("geoid.search.panel"), geoIdSearchPanel);
        tabbedPane.addTab(getSwingFactory().getText("geoid.clarification.panel"), geoIdClarificationPanel);

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(tabbedPane, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(geoIdAdminPanel, GBCFactory.createGBC(100, 100, 0, 1, 1, 1, GridBagConstraints.BOTH));
        pack();
    }

    @Override
    protected void execute(String command) {
    }

    class TabbedPaneChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (tabbedPane.getSelectedIndex() == 0) {
                geoIdSearchPanel.notifyObservers();
            }
            else if (tabbedPane.getSelectedIndex() == 1) {
                geoIdClarificationPanel.notifyObservers();
            }
        }
    }

}
