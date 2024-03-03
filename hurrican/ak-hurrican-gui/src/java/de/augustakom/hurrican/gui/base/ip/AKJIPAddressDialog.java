/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.2011 17:57:36
 */
package de.augustakom.hurrican.gui.base.ip;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKColorChangeableComponent;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.validation.cc.IPAddressValid.IPAddressValidator;

/**
 * Dialog zur Auswahl von IP-Adressen. Je nach Kontext koennen hier Adressen vom Typ IPv4, IPv6 und Variationen davon
 * definiert werden. Die definierten Adressen werden auch gleich auf Gueltigkeit validiert.
 *
 *
 *
 * @since Release 10
 */
public class AKJIPAddressDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent,
        AKColorChangeableComponent, PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(AKJIPAddressDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/base/ip/resources/AKJIPAddressDialog.xml";
    private static final String BTN_IP_CANCEL = "btn.ip.cancel";
    private static final String BTN_IP_SAVE = "btn.ip.save";
    private static final String BTN_IP_DELETE = "btn.ip.delete";
    private static final String ACTION_COMMAND_INIT = "INIT";
    private static final long serialVersionUID = -4170991643136183960L;

    private IPAddress origIpAddress = null;
    private IPAddress ipAddress = null;
    private String dialogTitle = null;

    private ProtocolVersionSelectionPanel protocolVersionSelectionPanel = null;
    private AddressSelectionPanel addressSelectionPanel = null;

    /**
     * Liste an Pr&auml;fix-Adressen f&uml;r den akutellen Auftrag.
     */
    private List<IPAddress> prefixAddresses = null;
    /**
     * IP Typen fuer die kontextabhaengige Erfassung
     */
    private List<AddressTypeEnum> allowedIpTypes = null;

    private final IPAddressValidator ipAddrValidator = new IPAddressValidator();

    /**
     * erzeugt eine neue Instanz des Auswahldialogs.
     */
    public AKJIPAddressDialog(IPAddress ipAddress, List<IPAddress> prefixAddresses, String dialogTitle,
            List<AddressTypeEnum> allowedIpTypes) {
        super(RESOURCE, false, false);
        this.prefixAddresses = prefixAddresses;
        this.allowedIpTypes = allowedIpTypes;
        this.ipAddress = ipAddress;
        this.origIpAddress = new IPAddress();
        this.origIpAddress.copy(ipAddress);
        this.dialogTitle = dialogTitle;
        createGUI();
        showValues();
    }

    @Override
    protected final void createGUI() {
        if (StringUtils.isBlank(dialogTitle)) {
            dialogTitle = getSwingFactory().getText("ip.title");
        }
        setTitle(dialogTitle);

        AKJButton btnSave = getSwingFactory().createButton(BTN_IP_SAVE, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(BTN_IP_CANCEL, getActionListener());
        AKJButton btnDelete = getSwingFactory().createButton(BTN_IP_DELETE, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        // @formatter:off
        btnPanel.add(btnSave        , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnCancel      , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(btnDelete      , GBCFactory.createGBC(  0,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        btnPanel.add(new AKJPanel() , GBCFactory.createGBC(100,  0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        protocolVersionSelectionPanel = new ProtocolVersionSelectionPanel("ip.protocol.version", getActionListener(),
                getSwingFactory(), allowedIpTypes);
        addressSelectionPanel = new AddressSelectionPanel("ip.address");

        // @formatter:off
        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(protocolVersionSelectionPanel, BorderLayout.NORTH);
        getChildPanel().add(addressSelectionPanel        , BorderLayout.CENTER);
        getChildPanel().add(btnPanel                     , BorderLayout.SOUTH);
        // @formatter:on

        addPropertyChangeListener(this);
    }

    @Override
    /**
     * Mit 'X' geschlossene Dialoge  muessen sich so verhalten, als waeren sie via 'Abbrechen' Schaltflaeche beendet worden.
     * */
    public void propertyChange(PropertyChangeEvent evt) {
        if (isVisible() && (evt.getSource() == this) && StringUtils.equals(evt.getPropertyName(), VALUE_PROPERTY)
                && (evt.getNewValue() == null) && ipAddress != null) {
            ipAddress.copy(origIpAddress);
        }
    }

    @Override
    protected void execute(String command) {
        addressSelectionPanel.tfInetAddress.requestFocus();
        if (BTN_IP_SAVE.equals(command)) {
            doSave();
        }
        else if (BTN_IP_CANCEL.equals(command)) {
            prepare4Close();
            setValue(null); // Call 'propertyChange()' -> newValue == null
        }
        else if (BTN_IP_DELETE.equals(command)) {
            setModelToNull();
        }
        else {
            togglePrefixSelection();
            try {
                validateInetAddress();
                addressSelectionPanel.resetFeedbackText();
            }
            catch (HurricanGUIException e) {
                addressSelectionPanel.setFeedbackText(e.getMessage());
            }
            finally {
                final AddressTypeEnum selectedType = protocolVersionSelectionPanel.getSelectedAddressType();
                if ((allowedIpTypes != null) && allowedIpTypes.contains(selectedType)) {
                    addressSelectionPanel.setEnabled(true);
                }
            }
        }
    }

    private void togglePrefixSelection() {
        if (protocolVersionSelectionPanel.requiresPrefix()) {
            addressSelectionPanel.enablePrefixSelection();
        }
        else {
            if (addressSelectionPanel.cbPrefix.isEnabled()) {
                addressSelectionPanel.disablePrefixSelection(false);
            }
        }
    }

    private void toggleSelectionPanel() {
        final boolean isValidSelection = protocolVersionSelectionPanel.setSelection(ipAddress.getIpType().name());
        addressSelectionPanel.setEnabled(isValidSelection);
    }

    /**
     * Setzt die Werte aus dem Interface {@link IPAddress} in die noetigen Anzeigefelder.
     */
    private void showValues() {
        if (ipAddress != null) {
            if (ipAddress.getIpType() != null) {
                toggleSelectionPanel();
                togglePrefixSelection();
            }
            // Praefix vor der Adresse setzen!
            if (ipAddress.getPrefixRef() != null) {
                addressSelectionPanel.setPrefixSelection(ipAddress.getPrefixRef());
            }
            if (StringUtils.isNotBlank(ipAddress.getAddress())) {
                addressSelectionPanel.setInetAddressTextField(ipAddress.getAddress());
            }
        }
    }

    @Override
    public final void loadData() {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void doSave() {
        try {
            if (!protocolVersionSelectionPanel.isSelectedAddressTypeEnabled()) {
                throw new HurricanGUIException(
                        "Der gewählte Adresstyp ist in diesem Kontext nicht erlaubt!");
            }
            validateInetAddress();
            prepare4Close();
            setValue(getInetAddress()); // liefert die gespeicherten Werte an
            // das aufrufende Panal zurueck.
        }
        catch (HurricanGUIException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    private String getNormalizedInetAddress(String address, AddressTypeEnum addressType) {
        String normalizedAddress = address;
        switch (addressType) {
            case IPV4:
            case IPV4_prefix:
            case IPV4_with_prefixlength:
                normalizedAddress = IPToolsV4.instance().normalizeValidAddress(address);
                break;
            case IPV6_full:
            case IPV6_prefix:
            case IPV6_relative:
            case IPV6_full_eui64:
            case IPV6_relative_eui64:
            case IPV6_with_prefixlength:
                normalizedAddress = IPToolsV6.instance().normalizeValidAddress(address);
                break;
            default:
                MessageHelper.showMessageDialog(getMainFrame(), "Adress-Typ not supported!");
        }
        return normalizedAddress;
    }

    /**
     * schreibt die in der Anzeige enthaltenen Werte in das Model {@link #ipAddress}.
     */
    private void setModelFromGui() {
        AddressTypeEnum selectedAddressType = protocolVersionSelectionPanel.getSelectedAddressType();
        IPAddress inetAddress = getInetAddress();
        inetAddress.setIpType(selectedAddressType);
        inetAddress.setAddress(addressSelectionPanel.getInetAddressTextFieldInput());
        IPAddress prefix = addressSelectionPanel.getSelectedPrefix();
        inetAddress.setPrefixRef(prefix);
    }

    /**
     * schreibt NULL-Werte in das Model {@link #ipAddress}.
     */
    private void setModelToNull() {
        IPAddress inetAddress = getInetAddress();
        inetAddress.setAddress(null);
        inetAddress.setIpType(null);
        inetAddress.setPrefixRef(null);
        prepare4Close();
        setValue(getInetAddress());
    }

    private void validateInetAddress() throws HurricanGUIException {
        setModelFromGui();
        IPAddress ipAddress = getInetAddress();
        AKWarning warning = ipAddrValidator.validate(ipAddress);
        if (warning != null) {
            throw new HurricanGUIException(warning.getMessage());
        }
        else {
            String normalizedAddress = getNormalizedInetAddress(ipAddress.getAddress(), ipAddress.getIpType());
            ipAddress.setAddress(normalizedAddress);
        }
    }

    private List<IPAddress> getPrefixAddresses() {
        return prefixAddresses;
    }

    private IPAddress getInetAddress() {
        if (ipAddress == null) {
            ipAddress = new IPAddress();
        }
        return ipAddress;
    }

    @Override
    public void setActiveColor(Color activeColor) {
        if (addressSelectionPanel != null) {
            addressSelectionPanel.setActiveColor(activeColor);
        }
    }

    @Override
    public Color getActiveColor() {
        if (addressSelectionPanel != null) {
            return addressSelectionPanel.getActiveColor();
        }
        return null;
    }

    @Override
    public void setInactiveColor(Color inactiveColor) {
        if (addressSelectionPanel != null) {
            addressSelectionPanel.setInactiveColor(inactiveColor);
        }
    }

    @Override
    public Color getInactiveColor() {
        if (addressSelectionPanel != null) {
            return addressSelectionPanel.getInactiveColor();
        }
        return null;
    }

    public void setSelectionColor(Color selectionColor) {
        if (addressSelectionPanel != null) {
            addressSelectionPanel.setSelectionColor(selectionColor);
        }
    }

    public void setSelectedTextColor(Color selectionTextColor) {
        if (addressSelectionPanel != null) {
            addressSelectionPanel.setSelectedTextColor(selectionTextColor);
        }
    }

    public void setColumns(int tfColumns) {
        if (addressSelectionPanel != null) {
            addressSelectionPanel.setColumns(tfColumns);
        }
    }

    private class ComboBoxListener implements ActionListener {

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (!StringUtils.equals(ACTION_COMMAND_INIT, e.getActionCommand())) {
                execute("ComboBoxChangeSelection");
            }
        }

    }

    private class InetAddressValidationListener extends KeyAdapter implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            execute(DocumentEvent.EventType.INSERT.toString());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            execute(DocumentEvent.EventType.REMOVE.toString());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            execute(DocumentEvent.EventType.CHANGE.toString());
        }

        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                execute(BTN_IP_SAVE);
            }
        }

    } // end

    /**
     * Panel auf dem die eigentliche Eingabe der IP-Adresse(n) stattfindet.
     */
    private final class AddressSelectionPanel extends AKJPanel implements AKColorChangeableComponent {

        private static final long serialVersionUID = 7415455401477685693L;
        private FeedbackPanel feedbackPanel = null;
        private AKJTextField tfInetAddress = null;
        private AKJComboBox cbPrefix = null;
        private AKJLabel lbPrefix = null;
        private AKJLabel lbInetAddress;

        private static final String LB_INET_ADDR_NAME = "IP-Adresse:";
        private static final String LB_PREFIX_ADDR_NAME = "Offset:";

        public AddressSelectionPanel(String titleKey) {
            setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText(titleKey)));
            setPreferredSize(new Dimension(400, 125));
            addComponents();
        }

        private void addComponents() {
            lbInetAddress = getSwingFactory().createLabel("tf.ip.address");
            lbPrefix = getSwingFactory().createLabel("cb.ip.prefix");

            InetAddressValidationListener validationListener = new InetAddressValidationListener();
            tfInetAddress = getSwingFactory().createTextField("tf.ip.address", true, true, validationListener);
            tfInetAddress.getDocument().addDocumentListener(validationListener);
            tfInetAddress.setPreferredSize(new Dimension(300, 23));
            ComboBoxListener boxListener = new ComboBoxListener();
            cbPrefix = getSwingFactory().createComboBox("cb.ip.prefix");
            cbPrefix.addActionListener(boxListener);

            feedbackPanel = new FeedbackPanel();
            setLayout(new GridBagLayout());
            add(lbPrefix, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            add(cbPrefix, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
            add(lbInetAddress, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            add(tfInetAddress, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
            add(feedbackPanel, GBCFactory.createGBC(0, 0, 0, 3, 2, 1, GridBagConstraints.HORIZONTAL));

            disablePrefixSelection(true);
        }

        @Override
        public void setEnabled(boolean enabled) {
            tfInetAddress.setEnabled(enabled);
        }

        void setInetAddressTextField(String text) {
            tfInetAddress.setText(text);
        }

        String getInetAddressTextFieldInput() {
            return tfInetAddress.getText(null);
        }

        IPAddress getSelectedPrefix() {
            return (IPAddress) cbPrefix.getSelectedItemValue();
        }

        void setPrefixSelection(IPAddress prefix) {
            String actionCommand = cbPrefix.getActionCommand();
            cbPrefix.setActionCommand(ACTION_COMMAND_INIT);
            cbPrefix.setSelectedItem(prefix.getAddress());
            cbPrefix.setActionCommand(actionCommand);
        }

        void enablePrefixSelection() {
            if (!cbPrefix.isVisible() && !cbPrefix.isEnabled()) {
                fillPrefixAdresses();
                cbPrefix.setVisible(true);
                cbPrefix.setEnabled(true);
                lbPrefix.setVisible(true);
                lbInetAddress.setText(LB_PREFIX_ADDR_NAME);
                revalidate();
                repaint();
            }
        }

        void disablePrefixSelection(boolean initialState) {
            try {
                if (cbPrefix.isVisible() && cbPrefix.isEnabled()) {
                    cbPrefix.removeAllItems();
                    if (!initialState) {
                        getInetAddress().setPrefixRef(null);
                    }
                    cbPrefix.setVisible(false);
                    cbPrefix.setEnabled(false);
                    lbPrefix.setVisible(false);
                    lbInetAddress.setText(LB_INET_ADDR_NAME);
                    revalidate();
                    repaint();
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }

        /**
         * Befuellt die Auswahl an IPv6-Prefixen mit Daten aus ...
         */
        private void fillPrefixAdresses() {
            String actionCommand = cbPrefix.getActionCommand();
            cbPrefix.setActionCommand(ACTION_COMMAND_INIT);
            cbPrefix.removeAllItems();
            for (IPAddress ipAddress : getPrefixAddresses()) {
                cbPrefix.addItem(ipAddress.getAddress(), ipAddress);
            }
            cbPrefix.setActionCommand(actionCommand);
        }

        void setFeedbackText(String text) {
            feedbackPanel.setFeedbackText(text);
        }

        void resetFeedbackText() {
            feedbackPanel.resetFeedbackText();
        }

        @Override
        public void setActiveColor(Color activeColor) {
            if (tfInetAddress != null) {
                tfInetAddress.setActiveColor(activeColor);
            }
        }

        @Override
        public Color getActiveColor() {
            if (tfInetAddress != null) {
                return tfInetAddress.getActiveColor();
            }
            return null;
        }

        @Override
        public void setInactiveColor(Color inactiveColor) {
            if (tfInetAddress != null) {
                tfInetAddress.setInactiveColor(inactiveColor);
            }
        }

        @Override
        public Color getInactiveColor() {
            if (tfInetAddress != null) {
                return tfInetAddress.getInactiveColor();
            }
            return null;
        }

        public void setSelectionColor(Color selectionColor) {
            if (tfInetAddress != null) {
                tfInetAddress.setSelectionColor(selectionColor);
            }
        }

        public void setSelectedTextColor(Color selectionTextColor) {
            if (tfInetAddress != null) {
                tfInetAddress.setSelectedTextColor(selectionTextColor);
            }
        }

        public void setColumns(int tfColumns) {
            if (tfInetAddress != null) {
                tfInetAddress.setColumns(tfColumns);
            }
        }
    }

} // end
