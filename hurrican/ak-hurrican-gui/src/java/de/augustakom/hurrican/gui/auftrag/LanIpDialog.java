/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2006 07:52:58
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponent;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponentFactory;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.EndgeraetIp.AddressType;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.IPAddressService;

/**
 * Dialog zu editieren eines EndgeraetIp-Objekts. <br> Beim Speichern wird das urspruengliche Objekt historisiert und
 * ein neues(!) Objekt erzeugt. Dieses neue Objekt kann vom Caller ueber die Methode getValue() auf dem Dialog ermittelt
 * werden.
 *
 *
 */
public class LanIpDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(LanIpDialog.class);

    private static final long serialVersionUID = -3280970626084623214L;
    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/LanIpDialog.xml";
    private static final String RESOURCE_LAN_VRRP = "de/augustakom/hurrican/gui/auftrag/resources/LanVrrpIpDialog.xml";
    private static final String IP_ADDRESS_RESOURCE = "ip";

    private EndgeraetIp copyOfEndgeraetIp;
    private EndgeraetIp endgeraetIp;
    private final String resource;

    // GUI-Elemente
    private AKJIPAddressComponent ipcIP;

    // Sonstiges
    private Long billingOrderNo;

    /**
     * Konstruktor mit Angabe des zu editierenden EndgeraetIp-Objekts.
     */
    public LanIpDialog(EndgeraetIp endgeraetIp, Long billingOrderNo, AddressType addressType) {
        super(AddressType.LAN_VRRP == addressType ? RESOURCE_LAN_VRRP : RESOURCE, RESOURCE_WITH_OK_BTN);
        this.resource = AddressType.LAN_VRRP == addressType ? RESOURCE_LAN_VRRP : RESOURCE;
        if (endgeraetIp == null) {
            throw new IllegalArgumentException("Es wurde kein Endgeraet-Objekt angegeben.");
        }

        this.endgeraetIp = endgeraetIp;
        this.billingOrderNo = billingOrderNo;
        this.copyOfEndgeraetIp = new EndgeraetIp();

        createGUI();
        loadData();
        showValues();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel labelIp = getSwingFactory().createLabel(IP_ADDRESS_RESOURCE);
        ipcIP = AKJIPAddressComponentFactory.create(resource).createIPAddressComponent(IP_ADDRESS_RESOURCE);
        ipcIP.setMinimumSize(new Dimension(200, ipcIP.getPreferredSize().height));
        ipcIP.setPreferredSize(new Dimension(300, ipcIP.getPreferredSize().height));
        ipcIP.addObserver(this);

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        // @formatter:off
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(labelIp       , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  0, 2, 0, 1, 4, GridBagConstraints.NONE));
        child.add(ipcIP         , GBCFactory.createGBC(  1,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(  0,  1, 4, 3, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
    }

    @Override
    public final void loadData() {
        try {
            PropertyUtils.copyProperties(this.copyOfEndgeraetIp, endgeraetIp);
            if (endgeraetIp.getIpAddressRef() != null) {
                IPAddress copyIpAddress = new IPAddress();
                PropertyUtils.copyProperties(copyIpAddress, endgeraetIp.getIpAddressRef());
                this.copyOfEndgeraetIp.setIpAddressRef(copyIpAddress);
                loadPrefixAddresses();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void loadPrefixAddresses() throws FindException, ServiceNotFoundException {
        IPAddressService ipAddressService = getCCService(IPAddressService.class);
        List<IPAddress> prefixAddresses = ipAddressService.findV6PrefixesByBillingOrderNumber(billingOrderNo);
        ipcIP.setPrefixAddresses(prefixAddresses);
    }

    /* Zeigt die Daten des uebergebenen EndgeraetIp-Objekts an. */
    private void showValues() {
        GuiTools.cleanFields(getChildPanel());
        if (copyOfEndgeraetIp != null) {
            ipcIP.setIPAddress(copyOfEndgeraetIp.getIpAddressRef());
        }
    }

    /* Speichert die eingetragenen Daten in das EndgeraetIp-Objekt. */
    private void setValues() {
        copyOfEndgeraetIp.setIpAddressRef(ipcIP.getIPAddress());
        if (copyOfEndgeraetIp.getIpAddressRef() != null) {
            copyOfEndgeraetIp.getIpAddressRef().setBillingOrderNo(billingOrderNo);
        }
    }

    @Override
    protected void doSave() {
        try {
            setValues();
            PropertyUtils.copyProperties(endgeraetIp, copyOfEndgeraetIp);
            prepare4Close();
            setValue(endgeraetIp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // not needed for this panel
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed for this panel
    }

}
