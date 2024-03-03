/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2011 09:43:33
 */

package de.augustakom.hurrican.gui.wholesale;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;


/**
 * Admin-Frame fuer die Verwaltung der Geo IDs.
 */
public class WholesaleAdminFrame extends AbstractAdminFrame {

    public WholesaleAdminFrame() {
        super("de/augustakom/hurrican/gui/wholesale/resources/WholesaleAdminFrame.xml", false);
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

        EkpAdminPanel ekpAdminPanel = new EkpAdminPanel();
        A10nspAdminPanel a10nspAdminPanel = new A10nspAdminPanel();

        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab(getSwingFactory().getText("wholesale.ekp.panel"), ekpAdminPanel);
        tabbedPane.addTab(getSwingFactory().getText("wholesale.a10nsp.panel"), a10nspAdminPanel);

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(tabbedPane, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        pack();
    }

    @Override
    protected void execute(String command) {
    }

}
