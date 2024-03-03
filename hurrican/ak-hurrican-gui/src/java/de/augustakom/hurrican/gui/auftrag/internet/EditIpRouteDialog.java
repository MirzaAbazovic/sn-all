/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2010 13:14:01
 */
package de.augustakom.hurrican.gui.auftrag.internet;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponent;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponentFactory;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.IpRoute;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.IpRouteService;


/**
 * Dialog, um ein IpRoute Objekt anzulegen bzw. zu editieren.
 */
public class EditIpRouteDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(EditIpRouteDialog.class);

    private static final String DESCRIPTION = "description";
    private static final String METRIK = "metrik";
    private static final String IP_ADDRESS = "ip.address";

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/internet/resources/EditIpRouteDialog.xml";

    private AKJIPAddressComponent ipcIpAddress;
    private AKJFormattedTextField tfMetrik;
    private AKJTextArea taDescription;

    private final IpRoute ipRoute;
    private final AuftragDaten auftragDaten;

    private IPAddressService ipAddressService;

    /**
     * Konstruktor mit Angabe des IpRoute Objekts, das editiert werden soll.
     *
     * @param ipRoute
     */
    public EditIpRouteDialog(IpRoute ipRoute, AuftragDaten auftragDaten) {
        super(RESOURCE);
        this.ipRoute = ipRoute;
        this.auftragDaten = auftragDaten;
        if (this.ipRoute == null) {
            throw new IllegalArgumentException("No object defined!");
        }
        createGUI();
        init();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblIpAddress = getSwingFactory().createLabel(IP_ADDRESS);
        AKJLabel lblMetrik = getSwingFactory().createLabel(METRIK);
        AKJLabel lblDescription = getSwingFactory().createLabel(DESCRIPTION);

        ipcIpAddress = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(IP_ADDRESS, null);
        ipcIpAddress.setMinimumSize(new Dimension(200, ipcIpAddress.getPreferredSize().height));
        ipcIpAddress.setPreferredSize(new Dimension(300, ipcIpAddress.getPreferredSize().height));
        tfMetrik = getSwingFactory().createFormattedTextField(METRIK);
        taDescription = getSwingFactory().createTextArea(DESCRIPTION);
        AKJScrollPane spDescription = new AKJScrollPane(taDescription, new Dimension(200, 80));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblIpAddress, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(ipcIpAddress, GBCFactory.createGBC(1, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblMetrik, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfMetrik, GBCFactory.createGBC(1, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblDescription, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spDescription, GBCFactory.createGBC(1, 0, 3, 3, 1, 2, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 1, 4, 5, 1, 1, GridBagConstraints.NONE));
    }

    private void init() {
        try {
            ipAddressService = getCCService(IPAddressService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public final void loadData() {
        try {
            IPAddress ipAddressRef;
            if (ipRoute.getIpAddressRef() != null) {
                ipAddressRef = new IPAddress();
                PropertyUtils.copyProperties(ipAddressRef, ipRoute.getIpAddressRef());
            }
            else {
                ipAddressRef = null;
            }
            GuiTools.cleanFields(this);
            ipcIpAddress.setIPAddress(ipAddressRef);
            tfMetrik.setValue((ipRoute.getMetrik() != null) ? ipRoute.getMetrik() : 1);
            taDescription.setText(ipRoute.getDescription());
            update(null, ipRoute.getIpAddressRef());
            loadPrefixAddresses();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void loadPrefixAddresses() throws FindException {
        List<IPAddress> prefixAddresses = ipAddressService.
                findV6PrefixesByBillingOrderNumber(auftragDaten.getAuftragNoOrig());
        ipcIpAddress.setPrefixAddresses(prefixAddresses);
    }

    @Override
    protected void doSave() {
        try {
            ipRoute.setIpAddressRef(ipcIpAddress.getIPAddress());
            if (ipRoute.getIpAddressRef() != null) {
                ipRoute.getIpAddressRef().setBillingOrderNo(auftragDaten.getAuftragNoOrig());
            }
            ipRoute.setMetrik(tfMetrik.getValueAsLong(null));
            ipRoute.setDescription(taDescription.getText(null));

            IpRouteService service = getCCService(IpRouteService.class);
            service.saveIpRoute(ipRoute, HurricanSystemRegistry.instance().getSessionId());

            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void validateSaveButton() {
    }

}


