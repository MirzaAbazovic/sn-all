/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2009 17:59:51
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;


/**
 *
 */
public class Client2SiteTokenAdminFrame extends AKJAbstractInternalFrame {

    public Client2SiteTokenAdminFrame() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        setTitle("Admin Maske für IPSecClient2Site Tokens");
        Client2SiteTokenAdminPanel client2SiteTokenAdminPanel = new Client2SiteTokenAdminPanel();
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(client2SiteTokenAdminPanel, BorderLayout.CENTER);
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
    @Override
    public void update(Observable o, Object arg) {
    }
}
