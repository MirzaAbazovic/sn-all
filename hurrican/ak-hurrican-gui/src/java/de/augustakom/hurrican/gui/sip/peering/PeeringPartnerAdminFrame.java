/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015
 */
package de.augustakom.hurrican.gui.sip.peering;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.hurrican.gui.base.AbstractAdminFrame;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;

public class PeeringPartnerAdminFrame extends AbstractAdminFrame {

    private static final long serialVersionUID = -330970721818186869L;

    public PeeringPartnerAdminFrame() {
        super(null, false);
        createGUI();
    }

    @Override
    protected void createGUI() {
        setTitle("SIP Peering Partner");
        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new PeeringPartnerAdminPanel(),
                GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        pack();
        setMinimumSize(getSize());
    }

    @Override
    protected AbstractAdminPanel[] getAdminPanels() {
        return null;
    }

    @Override
    protected void execute(String command) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
