/*
# * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.2006 07:52:58
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponent;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponentFactory;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.model.cc.PortForwarding.TransportProtocolType;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Dialog zum Editieren und Anlegen eines PortForwarding-Objekts.
 *
 *
 *
 */
public class PortForwardingDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(PortForwardingDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/PortForwardingDialog.xml";
    private static final String SOURCE_ADDRESS_RESOURCE = "sourceIpAddress";
    private static final String DEST_ADDRESS_RESOURCE = "destIpAddress";

    /**
     * ID des Reference-Eintrags fuer 'Sonstiger Port'
     */
    private static final Long REFERENCE_SONSTIGER_PORT_ID = 6013L;

    /**
     * Das PortForwarding-Object, das angelegt oder bearbeitet werden soll.
     */
    private PortForwarding portForwarding = null;

    /**
     * Kopie des portForwarding-Objekts
     */
    private PortForwarding copyOfPortForwarding = null;

    /**
     * Billing Auftragsnummer
     */
    private Long billingOrderNo = null;

    // GUI-Elemente
    private AKJComboBox cbTransportProtocol = null;
    private AKJIPAddressComponent ipcSourceIpAddress = null;
    private AKJComboBox cbSourcePort = null;
    private AKJFormattedTextField tfSourcePort = null;
    private AKJIPAddressComponent ipcDestIpAddress = null;
    private AKJComboBox cbDestPort = null;
    private AKJFormattedTextField tfDestPort = null;
    private AKJCheckBox chbActive = null;
    private AKJTextArea taBemerkung = null;

    /**
     * Auswahloptionen bei der Port-Auswahl-Combo-Box
     */
    private List<Reference> allPortReferences;
    private Reference sonstigerPortReference;

    /**
     * Konstruktor mit Angabe des zu editierenden PortForwarding-Objekts.
     *
     * @param portForwarding portForwarding
     * @param billingOrderNo billing order NO
     */
    public PortForwardingDialog(PortForwarding portForwarding, Long billingOrderNo) {
        super(RESOURCE, RESOURCE_WITH_OK_BTN);
        if (portForwarding == null) {
            throw new IllegalArgumentException("Es wurde kein PortForwarding-Objekt angegeben.");
        }

        this.portForwarding = portForwarding;
        this.copyOfPortForwarding = new PortForwarding();
        this.billingOrderNo = billingOrderNo;

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

        AKJLabel lbltransportProtocol = getSwingFactory().createLabel("transportProtocol");
        AKJLabel lblSourceIpAddress = getSwingFactory().createLabel(SOURCE_ADDRESS_RESOURCE);
        AKJLabel lblSourcePort = getSwingFactory().createLabel("sourcePort");
        AKJLabel lblDestIpAddress = getSwingFactory().createLabel(DEST_ADDRESS_RESOURCE);
        AKJLabel lblDestPort = getSwingFactory().createLabel("destPort");
        AKJLabel lblActive = getSwingFactory().createLabel("active");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");

        cbTransportProtocol = getSwingFactory().createComboBox("transportProtocol", new AKCustomListCellRenderer<>(TransportProtocolType.class, TransportProtocolType::toString));
        ipcSourceIpAddress = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(SOURCE_ADDRESS_RESOURCE);
        ipcSourceIpAddress.setMinimumSize(new Dimension(200, ipcSourceIpAddress.getPreferredSize().height));
        ipcSourceIpAddress.setPreferredSize(new Dimension(300, ipcSourceIpAddress.getPreferredSize().height));
        tfSourcePort = getSwingFactory().createFormattedTextField("sourcePort");
        tfSourcePort.setEnabled(false);
        cbSourcePort = getSwingFactory().createComboBox("sourcePort", new AKCustomListCellRenderer<>(Reference.class, Reference::getGuiText));
        cbSourcePort.addActionListener(new PortComboBoxActionListener(tfSourcePort, cbSourcePort));
        ipcDestIpAddress = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(DEST_ADDRESS_RESOURCE);
        tfDestPort = getSwingFactory().createFormattedTextField("destPort");
        tfDestPort.setEnabled(false);
        cbDestPort = getSwingFactory().createComboBox("destPort", new AKCustomListCellRenderer<>(Reference.class, Reference::getGuiText));
        cbDestPort.addActionListener(new PortComboBoxActionListener(tfDestPort, cbDestPort));
        chbActive = getSwingFactory().createCheckBox("active", true);
        taBemerkung = getSwingFactory().createTextArea("bemerkung");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung, new Dimension(150, 80));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());

        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lbltransportProtocol, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(cbTransportProtocol, GBCFactory.createGBC(1, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblSourceIpAddress, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(ipcSourceIpAddress, GBCFactory.createGBC(1, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblDestIpAddress, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(ipcDestIpAddress, GBCFactory.createGBC(1, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblSourcePort, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(cbSourcePort, GBCFactory.createGBC(1, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfSourcePort, GBCFactory.createGBC(1, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblDestPort, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(cbDestPort, GBCFactory.createGBC(1, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfDestPort, GBCFactory.createGBC(1, 0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblActive, GBCFactory.createGBC(0, 0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(chbActive, GBCFactory.createGBC(1, 0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblBemerkung, GBCFactory.createGBC(0, 0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(spBemerkung, GBCFactory.createGBC(1, 0, 3, 9, 1, 2, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 1, 4, 11, 1, 1, GridBagConstraints.NONE));
    }

    /**
     * Default-Daten fuer den Dialog laden
     */
    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            PropertyUtils.copyProperties(this.copyOfPortForwarding, portForwarding);
            if (portForwarding.getDestIpAddressRef() != null) {
                IPAddress copyIpAddress = new IPAddress();
                PropertyUtils.copyProperties(copyIpAddress, portForwarding.getDestIpAddressRef());
                this.copyOfPortForwarding.setDestIpAddressRef(copyIpAddress);
            }
            if (portForwarding.getSourceIpAddressRef() != null) {
                IPAddress copyIpAddress = new IPAddress();
                PropertyUtils.copyProperties(copyIpAddress, portForwarding.getSourceIpAddressRef());
                this.copyOfPortForwarding.setSourceIpAddressRef(copyIpAddress);
            }

            cbTransportProtocol.addItems(Arrays.asList(TransportProtocolType.values()), true, null);
            ReferenceService rs = getCCService(ReferenceService.class);
            allPortReferences = rs.findReferencesByType(Reference.REF_TYPE_INTERNET_PROTOCOL, null);
            for (Reference portReference : allPortReferences) {
                if (portReference.getId().equals(REFERENCE_SONSTIGER_PORT_ID)) {
                    sonstigerPortReference = portReference;
                }
            }
            cbSourcePort.addItems(allPortReferences, true, Reference.class);
            cbDestPort.addItems(allPortReferences, true, Reference.class);
            loadPrefixAddresses();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void loadPrefixAddresses() throws FindException, ServiceNotFoundException {
        IPAddressService ipAddressService = getCCService(IPAddressService.class);
        List<IPAddress> prefixAddresses = ipAddressService.findV6PrefixesByBillingOrderNumber(billingOrderNo);
        ipcSourceIpAddress.setPrefixAddresses(prefixAddresses);
        ipcDestIpAddress.setPrefixAddresses(prefixAddresses);
    }

    /**
     * Zeigt die Daten des uebergebenen PortForwarding-Objekts an.
     */
    private void showValues() {
        GuiTools.cleanFields(getChildPanel());
        if (copyOfPortForwarding != null) {
            if (copyOfPortForwarding.getTransportProtocol() != null) {
                cbTransportProtocol.selectItem("name", TransportProtocolType.class, copyOfPortForwarding.getTransportProtocol());
            }
            ipcSourceIpAddress.setIPAddress(copyOfPortForwarding.getSourceIpAddressRef());
            ipcDestIpAddress.setIPAddress(copyOfPortForwarding.getDestIpAddressRef());
            loadPortToGuiElements(copyOfPortForwarding.getSourcePort(), tfSourcePort, cbSourcePort);
            loadPortToGuiElements(copyOfPortForwarding.getDestPort(), tfDestPort, cbDestPort);
            chbActive.setSelected(copyOfPortForwarding.getActive());
            taBemerkung.setText(copyOfPortForwarding.getBemerkung());
        }
    }

    /**
     * Selektiert je nach uebergebenen Port das Element in der Combo-Box und aktualisiert das Textfeld.
     */
    private void loadPortToGuiElements(Integer port, AKJFormattedTextField tf, AKJComboBox cb) {
        if (port != null) {
            cb.setSelectedItem(sonstigerPortReference); // fallback selection
            tf.setText(port.toString());
            for (Reference portReference : allPortReferences) {
                if ((portReference.getIntValue() != null) && portReference.getIntValue().equals(port)) {
                    cb.setSelectedItem(portReference);
                }
            }
        }
    }

    /**
     * Speichert die eingetragenen Daten im PortForwarding-Objekt.
     */
    private void setValues() {
        if (cbTransportProtocol.getSelectedItem() != null) {
            TransportProtocolType transportProtocolType = (TransportProtocolType) cbTransportProtocol.getSelectedItem();
            copyOfPortForwarding.setTransportProtocol(transportProtocolType);
        }
        copyOfPortForwarding.setSourceIpAddressRef(ipcSourceIpAddress.getIPAddress());
        if (copyOfPortForwarding.getSourceIpAddressRef() != null) {
            copyOfPortForwarding.getSourceIpAddressRef().setBillingOrderNo(billingOrderNo);
        }
        copyOfPortForwarding.setDestIpAddressRef(ipcDestIpAddress.getIPAddress());
        if (copyOfPortForwarding.getDestIpAddressRef() != null) {
            copyOfPortForwarding.getDestIpAddressRef().setBillingOrderNo(billingOrderNo);
        }
        copyOfPortForwarding.setSourcePort(tfSourcePort.getValueAsInt(null));
        copyOfPortForwarding.setDestPort(tfDestPort.getValueAsInt(null));
        copyOfPortForwarding.setActive(chbActive.isSelectedBoolean());
        copyOfPortForwarding.setBemerkung(taBemerkung.getText());
        AKUser user = HurricanSystemRegistry.instance().getCurrentUser();
        copyOfPortForwarding.setBearbeiter((user != null) ? user.getLoginName() : "unknown");
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            if(ipcDestIpAddress.getIPAddress() == null) {
                throw new HurricanGUIException("Es ist keine Ziel IP Adresse angegeben!");
            }
            if(tfDestPort.getValueAsInt(null) == null) {
                throw new HurricanGUIException("Es ist keine Ziel Port angegeben!");
            }
            setValues();

            PropertyUtils.copyProperties(portForwarding, copyOfPortForwarding);

            prepare4Close();
            setValue(portForwarding);
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
    }

    /**
     * Je nach Auswahl der Port-Combo-Box wird das Textfeld aktualisiert und de/aktiviert.
     */
    private static class PortComboBoxActionListener implements ActionListener {
        private final AKJFormattedTextField portTextfield;
        private final AKJComboBox portComboBox;

        public PortComboBoxActionListener(AKJFormattedTextField portTextfield, AKJComboBox portComboBox) {
            this.portTextfield = portTextfield;
            this.portComboBox = portComboBox;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            portTextfield.setText("");
            Reference selectedReference = (Reference) portComboBox.getSelectedItem();
            if (selectedReference != null) {
                if ((selectedReference.getId() != null) && selectedReference.getId().equals(REFERENCE_SONSTIGER_PORT_ID)) {
                    portTextfield.setEnabled(true);
                }
                else if (selectedReference.getIntValue() != null) {
                    portTextfield.setEnabled(false);
                    portTextfield.setText(selectedReference.getIntValue().toString());
                }
            }
        }
    }
}


