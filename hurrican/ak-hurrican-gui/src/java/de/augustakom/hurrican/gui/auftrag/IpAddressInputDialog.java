/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.03.2014
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponent;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponentFactory;
import de.augustakom.hurrican.model.cc.IPAddress;

public class IpAddressInputDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {
    private static final long serialVersionUID = 5175014342954132151L;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/IpAddressInputDialog.xml";
    private static final String IP_ADDRESS_RESOURCE = "ip";

    private final IPAddress ipAddress = new IPAddress();
    private AKJIPAddressComponent ipComponent;

    public IpAddressInputDialog(IPAddress ipAddress) {
        super(RESOURCE);

        this.ipAddress.copy(ipAddress);

        createGUI();
        loadData();
        showValues();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel labelIp = getSwingFactory().createLabel(IP_ADDRESS_RESOURCE);
        ipComponent = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(IP_ADDRESS_RESOURCE);
        ipComponent.setMinimumSize(new Dimension(200, ipComponent.getPreferredSize().height));
        ipComponent.setPreferredSize(new Dimension(300, ipComponent.getPreferredSize().height));
        ipComponent.addObserver(this);

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        // @formatter:off
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(labelIp       , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 2, 0, 1, 4, GridBagConstraints.NONE));
        child.add(ipComponent   , GBCFactory.createGBC(  1,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  1, 4, 3, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
    }

    @Override
    public final void loadData() {
    }

    private void showValues() {
        GuiTools.cleanFields(getChildPanel());
        ipComponent.setIPAddress(ipAddress);
    }

    private void setValues() {
        ipAddress.copy(ipComponent.getIPAddress());
    }

    @Override
    protected void doSave() {
        setValues();
        prepare4Close();
        setValue(ipAddress);
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
