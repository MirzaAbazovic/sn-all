/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2011 17:08:53
 */
package de.augustakom.hurrican.gui.tools.ipTest;

import java.awt.*;
import java.util.List;
import java.util.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponent;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponentFactory;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.cc.IPAddressService;


/**
 *
 */
public class IPTestPanel extends AbstractServicePanel {

    private static final String RESOURCE = "de/augustakom/hurrican/gui/tools/ipTest/resources/IPTestPanel.xml";

    private AKJIPAddressComponent ipcIPAddress = null;

    /**
     * Default-Konstruktor.
     */
    public IPTestPanel() {
        super(RESOURCE);
        createGUI();
        loadPrefixAddresses();
        ipcIPAddress.setIPAddress(new IPAddress());
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblIPAddress = getSwingFactory().createLabel("ipAddress");
        ipcIPAddress = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent("inetAddressField", null);

        AKJPanel panel = new AKJPanel(new GridBagLayout());
        ipcIPAddress.setMinimumSize(new Dimension(100, ipcIPAddress.getPreferredSize().height));
        ipcIPAddress.setPreferredSize(new Dimension(300, ipcIPAddress.getPreferredSize().height));
        panel.add(lblIPAddress, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(ipcIPAddress, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(panel, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
    }

    @Override
    protected void execute(String command) {
    }

    private void loadPrefixAddresses() {
        try {
            IPAddressService ipAddressService = getCCService(IPAddressService.class);
            // Billing Auftrag zu 417812
            List<IPAddress> prefixAddresses = ipAddressService
                    .findV6PrefixesByBillingOrderNumber(Long.valueOf(1243810));
            ipcIPAddress.setPrefixAddresses(prefixAddresses);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }
}
