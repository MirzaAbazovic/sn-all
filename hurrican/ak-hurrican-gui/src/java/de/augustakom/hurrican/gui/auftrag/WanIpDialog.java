/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2009 10:26:07
 */

package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponent;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponentFactory;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.EndgeraetIp.AddressType;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.IPAddressService;


/**
 * Dialog zur Auswahl einer WAN-IP fuer ein Endgeraet.
 *
 *
 */
public class WanIpDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(WanIpDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/WanIpDialog.xml";
    private static final String IP_ADDRESS_RESOURCE = "ip";

    private EndgeraetIp wanIp;
    private Long billingOrderNo;

    // GUI-Elemente
    private AKJTable tbIPs;
    private AKJIPAddressComponent ipcIp;
    private AKReflectionTableModel<IPAddress> tbModelIPs;

    // Services
    private IPAddressService ipAddressService;

    private boolean hasV4EndgeraetIp;
    private boolean hasV6EndgeraetIp;

    /**
     * Konstruktor mit Angabe des zu editierenden EndgeraetIp-Objekts und des EG2Auftrag Objekts.
     *
     * @param wanIp
     * @param billingOrderNo
     * @param hasV6EndgeraetIp
     * @param hasV4EndgeraetIp
     */
    public WanIpDialog(EndgeraetIp wanIp, Long billingOrderNo, boolean hasV4EndgeraetIp, boolean hasV6EndgeraetIp) {
        super(RESOURCE, RESOURCE_WITH_OK_BTN);

        this.billingOrderNo = billingOrderNo;
        this.hasV4EndgeraetIp = hasV4EndgeraetIp;
        this.hasV6EndgeraetIp = hasV6EndgeraetIp;
        this.wanIp = wanIp;

        createGUI();
        loadData();
        showValues();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        tbModelIPs = new AKReflectionTableModel<>(
                new String[] { "IP-Adresse" },
                new String[] { "address" },
                new Class[] { String.class });
        tbIPs = new AKJTable(tbModelIPs, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbIPs.fitTable(new int[] { 250 });
        tbIPs.addMouseListener(new AKTableDoubleClickMouseListener(this));

        AKJLabel labelIp = getSwingFactory().createLabel(IP_ADDRESS_RESOURCE);

        ipcIp = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(IP_ADDRESS_RESOURCE);
        ipcIp.setMinimumSize(new Dimension(200, ipcIp.getPreferredSize().height));
        ipcIp.setPreferredSize(new Dimension(300, ipcIp.getPreferredSize().height));

        AKJScrollPane spIPs = new AKJScrollPane(tbIPs, new Dimension(250, 150));

        AKJPanel availableIps = new AKJPanel(new GridBagLayout(), "Verfügbare IP-Adressen");
        availableIps.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        availableIps.add(spIPs, GBCFactory.createGBC(100, 100, 1, 0, 1, 3, GridBagConstraints.BOTH));

        AKJPanel selectedIp = new AKJPanel(new GridBagLayout(), "Zugeordnete IP-Adresse");
        selectedIp.add(labelIp, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        selectedIp.add(ipcIp, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(availableIps, GBCFactory.createGBC(1, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.VERTICAL));
        child.add(selectedIp, GBCFactory.createGBC(1, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 1, 2, 3, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ipAddressService = getCCService(IPAddressService.class);
            loadIpsForAuftrag();
            loadPrefixAddresses();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void loadIpsForAuftrag() {
        tbIPs.removeAll();
        try {
            List<IPAddress> ips = ipAddressService.findAssignedIPsOnly4BillingOrder(billingOrderNo);
            tbModelIPs.setData(ips);
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }

    }

    private void loadPrefixAddresses() throws FindException {
        List<IPAddress> prefixAddresses = ipAddressService.findV6PrefixesByBillingOrderNumber(billingOrderNo);
        ipcIp.setPrefixAddresses(prefixAddresses);
    }

    /* Zeigt die Daten des uebergebenen PortForwarding-Objekts an. */
    private void showValues() {
        GuiTools.cleanFields(getChildPanel());

        IPAddress ipAddressRefCopy = new IPAddress();
        ipAddressRefCopy.copy(wanIp.getIpAddressRef());
        ipcIp.setIPAddress(ipAddressRefCopy);
    }

    private void setValues() {
        if(ipcIp.getIPAddress() != null) {
            if(wanIp.getIpAddressRef() == null) {
                wanIp.setIpAddressRef(ipcIp.getIPAddress());
            } else {
                wanIp.getIpAddressRef().copy(ipcIp.getIPAddress());
            }
            if (wanIp.getIpAddressRef() != null) {
                wanIp.getIpAddressRef().setBillingOrderNo(billingOrderNo);
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            IPAddress ipAddress = ipcIp.getIPAddress();
            if ((ipAddress == null) || StringUtils.isBlank(ipAddress.getAddress())) {
                throw new HurricanGUIException("Es ist keine WAN IP Adresse angegeben!");
            }
            if ((ipAddress.isIPV4() && hasV4EndgeraetIp)
                    || (ipAddress.isIPV6() && hasV6EndgeraetIp)) {
                throw new HurricanGUIException(
                        "Es kann maximal eine IPV4 und eine IPV6 WAN IP Adresse angegeben werden");
            }
            setValues();

            prepare4Close();
            setValue(wanIp);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // do nothing
    }

    /* (non-Javadoc)
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof IPAddress) {
            selectIPAddress((IPAddress) selection);
        }
    }

    private void selectIPAddress(IPAddress ipAddress) {
        IPAddress ipAddressWAN = (wanIp.getIpAddressRef() != null) ? wanIp.getIpAddressRef() : new IPAddress();
        ipAddressWAN.copyAddressDataOnly(ipAddress);
        wanIp.setIpAddressRef(ipAddressWAN);
        wanIp.setAddressType(AddressType.WAN);
        showValues();
    }


}
