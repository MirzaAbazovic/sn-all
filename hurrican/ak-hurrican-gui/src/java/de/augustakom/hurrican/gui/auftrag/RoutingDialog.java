/*
# * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2006 07:52:58
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponent;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponentFactory;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Routing;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.IPAddressService;


/**
 * Dialog zum Editieren und Anlegen eines Routing-Objekts.
 *
 *
 */
public class RoutingDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(RoutingDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/RoutingDialog.xml";
    private static final String DEST_ADDRESS_RESOURCE = "destinationAdress";
    private static final String BEMERKUNG = "bemerkung";
    private static final String NEXT_HOP = "nextHop";

    /**
     * Das Routing-Objekt, das angelegt oder editiert werden soll.
     */
    private Routing routing = null;

    /**
     * Kopie des Routing Objekts.
     */
    private Routing copyOfRouting = null;

    private Long billingOrderNo = null;

    // GUI-Elemente
    private AKJIPAddressComponent ipcDestinationAdress = null;
    private AKJTextField tfNextHop = null;
    private AKJTextArea taBemerkung = null;

    public RoutingDialog(Routing routing, Long billingOrderNo) {
        super(RESOURCE, RESOURCE_WITH_OK_BTN);
        if (routing == null) {
            throw new IllegalArgumentException("Es wurde kein Routing-Objekt angegeben.");
        }
        this.routing = routing;
        this.copyOfRouting = new Routing();
        this.billingOrderNo = billingOrderNo;
        createGUI();
        loadData();
        showValues();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblDestinationAdress = getSwingFactory().createLabel(DEST_ADDRESS_RESOURCE);
        AKJLabel lblNextHop = getSwingFactory().createLabel(NEXT_HOP);
        AKJLabel lblBemerkung = getSwingFactory().createLabel(BEMERKUNG);

        ipcDestinationAdress = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(DEST_ADDRESS_RESOURCE);
        ipcDestinationAdress.setMinimumSize(new Dimension(200, ipcDestinationAdress.getPreferredSize().height));
        ipcDestinationAdress.setPreferredSize(new Dimension(300, ipcDestinationAdress.getPreferredSize().height));
        ipcDestinationAdress.addObserver(this);
        tfNextHop = getSwingFactory().createTextField(NEXT_HOP);
        taBemerkung = getSwingFactory().createTextArea(BEMERKUNG);
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung, new Dimension(150, 80));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());

        // @formatter:off
        child.add(new AKJPanel(),       GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblDestinationAdress, GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(),       GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(ipcDestinationAdress, GBCFactory.createGBC(  1,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblNextHop,           GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfNextHop,            GBCFactory.createGBC(  1,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblBemerkung,         GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spBemerkung,          GBCFactory.createGBC(  1,  0, 3, 4, 1, 2, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(),       GBCFactory.createGBC(  0,  1, 4, 6, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
    }


    @Override
    public final void loadData() {
        try {
            PropertyUtils.copyProperties(this.copyOfRouting, routing);
            if (routing.getDestinationAdressRef() != null) {
                IPAddress copyIpAddress = new IPAddress();
                PropertyUtils.copyProperties(copyIpAddress, routing.getDestinationAdressRef());
                this.copyOfRouting.setDestinationAdressRef(copyIpAddress);
            }

            IPAddressService ipAddressService = getCCService(IPAddressService.class);
            loadPrefixAddresses(ipAddressService);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void loadPrefixAddresses(IPAddressService ipAddressService) throws FindException {
        List<IPAddress> prefixAddresses = ipAddressService.findV6PrefixesByBillingOrderNumber(billingOrderNo);
        ipcDestinationAdress.setPrefixAddresses(prefixAddresses);
    }

    /**
     * Zeigt die Daten des uebergebenen Routing-Objekts an.
     */
    private void showValues() {
        GuiTools.cleanFields(getChildPanel());
        if (copyOfRouting != null) {
            ipcDestinationAdress.setIPAddress(copyOfRouting.getDestinationAdressRef());
            tfNextHop.setText(copyOfRouting.getNextHop());
            taBemerkung.setText(copyOfRouting.getBemerkung());
        }
    }

    /**
     * Speichert die eingetragenen Daten im Routing-Objekt.
     */
    private void setValues() {
        copyOfRouting.setDestinationAdressRef(ipcDestinationAdress.getIPAddress());
        if (copyOfRouting.getDestinationAdressRef() != null) {
            copyOfRouting.getDestinationAdressRef().setBillingOrderNo(billingOrderNo);
        }
        copyOfRouting.setNextHop(tfNextHop.getText());
        copyOfRouting.setBemerkung(taBemerkung.getText());
    }

    @Override
    protected void doSave() {
        try {
            if(ipcDestinationAdress.getIPAddress() == null) {
                throw new HurricanGUIException("Es ist keine Ziel IP Adresse angegeben!");
            }
            if(StringUtils.isEmpty(tfNextHop.getText())) {
                throw new HurricanGUIException("Es ist kein Next Hop angegeben!");
            }
            setValues();

            PropertyUtils.copyProperties(routing, copyOfRouting);

            prepare4Close();
            setValue(routing);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


