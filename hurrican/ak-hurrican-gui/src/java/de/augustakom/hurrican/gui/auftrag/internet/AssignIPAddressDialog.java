/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2011 13:25:02
 */
package de.augustakom.hurrican.gui.auftrag.internet;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.net.IPToolsV4;
import de.augustakom.common.tools.net.IPToolsV6;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.view.IPAddressPanelView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.service.cc.utils.NiederlassungPerEndstellenResolver;

/**
 * Dieser Dialog laesst den Benutzer ein IP-Netz fuer den aktuellen Auftrag zuweisen. Anhand von Adresstypen
 * (IPv4/IPv6), einem Standort und der Netzgroeße wird eines angefordert und zugewiesen.
 *
 *
 * @since Release 10
 */
public class AssignIPAddressDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(EditIpRouteDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/internet/resources/AssignIPAddressDialog.xml";
    private static final String NORTH_PANEL_TITLE = "north.panel.title";
    private static final String CENTER_PANEL_TITLE = "center.panel.title";
    private static final String TF_NETMASK_SIZE = "tf.netmask.size";
    private static final String BTN_EXECUTE = "btn.execute";
    private static final String CB_PURPOSE = "cb.purpose";
    private static final String DIALOG_TITLE = "title";
    private static final String CB_SITE = "cb.site";
    private static final String RB_IPV6 = "rb.ipv6";
    private static final String RB_IPV4 = "rb.ipv4";

    private AKJFormattedTextField tfNetmaskSize = null;
    private AKJComboBox cbSite = null;
    private AKJComboBox cbPurpose = null;
    private AKJRadioButton rbIpV6 = null;
    private AKJRadioButton rbIpV4 = null;
    private CCAuftragModel model = null;
    private boolean isVpnPresent = false;

    /**
     * Das Produkt, das zu dem aktuellen Auftrag gehoert.
     */
    private Produkt produkt;

    public AssignIPAddressDialog(CCAuftragModel model) {
        super(RESOURCE);
        this.model = model;
        createGUI();
        loadData();
    }

    private RegistryService getRegistryService() throws ServiceNotFoundException {
        return getCCService(RegistryService.class);
    }

    /**
     * @return Returns the produktService.
     * @throws ServiceNotFoundException
     */
    private ProduktService getProduktService() throws ServiceNotFoundException {
        return getCCService(ProduktService.class);
    }

    /**
     * @return Returns the referenceService.
     * @throws ServiceNotFoundException
     */
    private ReferenceService getReferenceService() throws ServiceNotFoundException {
        return getCCService(ReferenceService.class);
    }

    /**
     * @return Returns the ipAddressService.
     * @throws ServiceNotFoundException
     */
    private IPAddressService getIpAddressService() throws ServiceNotFoundException {
        return getCCService(IPAddressService.class);
    }

    private VPNService getVpnService() throws ServiceNotFoundException {
        return getCCService(VPNService.class);
    }

    /**
     * @return Returns the produkt.
     */
    private Produkt getProdukt() {
        return produkt;
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(DIALOG_TITLE));
        ButtonGroup bgProtocolVersion = new ButtonGroup();

        AKJButton btnSave = getButton(CMD_SAVE);
        if (btnSave != null) {
            btnSave.setText(getSwingFactory().getText(BTN_EXECUTE));
        }

        AKJLabel lblPurpose = getSwingFactory().createLabel(CB_PURPOSE);
        AKJLabel lblSite = getSwingFactory().createLabel(CB_SITE);
        AKJLabel lblNetmaskSize = getSwingFactory().createLabel(TF_NETMASK_SIZE);

        rbIpV6 = getSwingFactory().createRadioButton(RB_IPV6, getActionListener(), true, bgProtocolVersion);
        rbIpV6.setEnabled(getIPV6AssignmentFlag());
        rbIpV4 = getSwingFactory().createRadioButton(RB_IPV4, getActionListener(), false, bgProtocolVersion);
        rbIpV4.setSelected(true);
        cbPurpose = getSwingFactory().createComboBox(CB_PURPOSE,
                new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue), false);
        cbSite = getSwingFactory().createComboBox(CB_SITE,
                new AKCustomListCellRenderer<>(Reference.class, Reference::getStrValue));
        tfNetmaskSize = getSwingFactory().createFormattedTextField(TF_NETMASK_SIZE);

        AKJPanel northPanel = new AKJPanel(new GridBagLayout());
        northPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText(NORTH_PANEL_TITLE)));
        northPanel.add(rbIpV6, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        northPanel.add(rbIpV4, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        northPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.BOTH));

        AKJPanel centerPanel = new AKJPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText(CENTER_PANEL_TITLE)));
        centerPanel.add(lblPurpose, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        centerPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        centerPanel.add(cbPurpose, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        centerPanel.add(lblSite, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        centerPanel.add(cbSite, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        centerPanel.add(lblNetmaskSize, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        centerPanel.add(tfNetmaskSize, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        centerPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.VERTICAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(northPanel, BorderLayout.NORTH);
        getChildPanel().add(centerPanel, BorderLayout.CENTER);

        manageGUI(new AKManageableComponent[] {/* rbIpV4, */cbPurpose });
    }

    /**
     * Wenn der Eintrag in der Registry fehlt oder != 0 ist darf der IPV6 Radiobutton aktiv sein. Zur Einfuehrung ist
     * IPV6 noch deaktiviert und somit muss ein Eintrag == 0 existieren. Danach kann der Datensatz komplett entfernt
     * oder != 0 gesetzt werden.
     */
    private boolean getIPV6AssignmentFlag() {
        try {
            Integer flag = getRegistryService().getIntValue(RegistryService.REGID_IPV6_ASSIGNMENT_FLAG);
            return ((flag == null) || !NumberTools.equal(flag, Integer.valueOf(0))) ? true : false;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return true;
    }

    private void loadVPN() throws FindException, ServiceNotFoundException {
        VPN vpn = getVpnService().findVPNByAuftragId(model.getAuftragId());
        isVpnPresent = (vpn != null) ? true : false;
    }

    private void loadIpPurposes() throws FindException, ServiceNotFoundException {
        String purpose = rbIpV4.isSelected() ? Reference.REF_TYPE_IP_PURPOSE_TYPE_V4
                : Reference.REF_TYPE_IP_PURPOSE_TYPE_V6;
        List<Reference> ipPurposes = getReferenceService().findReferencesByType(purpose, true);
        cbPurpose.removeAllItems();
        if (CollectionTools.isNotEmpty(ipPurposes)) {
            cbPurpose.addItems(ipPurposes);
            cbPurpose.setSelectedIndex(0);
        }
    }

    private void loadBackboneSites() throws FindException, ServiceNotFoundException {
        List<Reference> ipSites = getReferenceService().findReferencesByType(
                Reference.REF_TYPE_IP_BACKBONE_LOCATION_TYPE, true);
        cbSite.removeAllItems();
        if (CollectionTools.isNotEmpty(ipSites)) {
            cbSite.addItems(ipSites);
            selectSite();
        }
    }

    private void loadProdukt(Long auftragId) throws FindException, ServiceNotFoundException {
        produkt = getProduktService().findProdukt4Auftrag(auftragId);
    }

    @Override
    public final void loadData() {
        try {
            GuiTools.cleanFields(this);
            loadVPN();
            loadIpPurposes();
            loadBackboneSites();
            loadProdukt(model.getAuftragId());
            manipulateIpPurpose(getProdukt());
            manipulateIpNetmaskSize(getProdukt());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Aktiviert oder deaktiviert die ComboBox des Verwendungszwecks. Ausnahme: Auftrag ist einem VPN zugeordnet. Hier
     * uebersteuert VPN, die ComboBox ist aktiv.
     */
    private void setCbPurposeEnabled(boolean enabled) {
        cbPurpose.setEnabled((isVpnPresent) ? true : enabled);
    }

    /**
     * <ul> <li>IPv4</li> <ul> <li>ist der Verwendungszweck auf dem Produkt nicht konfiguriert -> Combobox
     * editierbar</li> <li>ist der Verwendungszweck auf dem Produkt konfiguriert</li> <ul> <li>konfigurierten
     * Verwendungszweck in Combobox automatisch voreinstellen</li> <li>Combobox sperren wenn in Produktkonfiguration so
     * eingestellt</li> </ul> </ul> <li>IPv6</li> <ul> <li>Combobox immer aktiv</li> </ul> </ul>
     */
    private void manipulateIpPurpose(Produkt produkt) {
        try {
            loadIpPurposes();
            if (isIPV4Selected()) {
                Reference ipPurpose = produkt.getIpPurposeV4();
                if (ipPurpose != null) {
                    boolean ipPurposeEditable = (produkt.getIpPurposeV4Editable() != null) ? produkt
                            .getIpPurposeV4Editable() : false;
                    cbPurpose.selectItem("getStrValue", Reference.class, ipPurpose.getStrValue());
                    setCbPurposeEnabled(ipPurposeEditable);
                }
                else {
                    setCbPurposeEnabled(true);
                }
            }
            else if (isIPV6Selected()) {
                setCbPurposeEnabled(true);
            }
            else {
                throw new IllegalStateException("Internal error: neither IPv4 nor IPv6 selected");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Aktiviert oder deaktiviert das Textfeld der NetzmaskSize. Ausnahme: Auftrag ist einem VPN zugeordnet. Hier
     * uebersteuert VPN, das Textfeld ist aktiv.
     */
    private void setTfNetmaskSizeEnabled(boolean enabled) {
        tfNetmaskSize.setEnabled((isVpnPresent) ? true : enabled);
    }

    /**
     * <ul> <li>ist die Netzmasken Größe auf dem Produkt nicht konfiguriert -> Textfeld editierbar <li>ist die
     * Netzmasken Größe auf dem Produkt konfiguriert <ul> <li>Wert im Textfeld voreinstellen <li>Textfeld sperren wenn
     * in Produktkonfiguration so eingestellt </ul> </ul>
     */
    private void manipulateIpNetmaskSize(Produkt produkt) {
        Integer size = (isIPV4Selected()) ? produkt.getIpNetmaskSizeV4() : produkt.getIpNetmaskSizeV6();
        if (size != null) {
            boolean netmaskSizeEnabled = (produkt.getIpNetmaskSizeEditable() != null) ? produkt
                    .getIpNetmaskSizeEditable() : false;
            setTfNetmaskSizeEnabled(netmaskSizeEnabled);
            tfNetmaskSize.setText("" + size);

        }
        else {
            setTfNetmaskSizeEnabled(true);
            tfNetmaskSize.setText(null);
        }
    }

    void selectSite() {
        try {
            Niederlassung niederlassung = new NiederlassungPerEndstellenResolver().findSite4Auftrag(model
                    .getAuftragId());
            if ((niederlassung != null) && (niederlassung.getIpLocation() != null)) {
                cbSite.selectItem("getStrValue", Reference.class, niederlassung.getIpLocation().getStrValue());
                cbSite.setEnabled(false);
            }
            else {
                cbSite.setEnabled(true);
                cbSite.setSelectedIndex(0);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void execute(String command) {
        if (StringUtils.equals(RB_IPV4, command)) {
            manipulateIpPurpose(getProdukt());
            manipulateIpNetmaskSize(getProdukt());
        }
        else if (StringUtils.equals(RB_IPV6, command)) {
            manipulateIpPurpose(getProdukt());
            manipulateIpNetmaskSize(getProdukt());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    private boolean isIPV4Selected() {
        return rbIpV4.isSelected();
    }

    private boolean isIPV6Selected() {
        return rbIpV6.isSelected();
    }

    private void validateNetmaskSize(Integer netmaskSize) throws GUIException {
        if (netmaskSize == null) {
            throw new GUIException("Die Größe der Netzmaske ist nicht gesetzt!");
        }
        Integer maxBits = null;
        if (isIPV4Selected()) {
            maxBits = IPToolsV4.instance().getMaximumBits();
        }
        else if (isIPV6Selected()) {
            maxBits = IPToolsV6.instance().getMaximumBits();
        }
        else {
            throw new GUIException("Die Größe der Netzmaske ist für das selektierte IP Protokoll nicht validierbar!");
        }
        if (NumberTools.isGreater(netmaskSize, maxBits) || NumberTools.isLess(netmaskSize, Integer.valueOf(0))) {
            throw new GUIException("Die Größe der Netzmaske ist ausserhalb des erlaubten Wertebereiches!");
        }
    }

    @Override
    protected void doSave() {
        try {
            Reference site = (Reference) cbSite.getSelectedItem();
            Integer netmaskSize = tfNetmaskSize.getValueAsInt(null);
            Reference purpose = (Reference) cbPurpose.getSelectedItem();
            Long sessionId = HurricanSystemRegistry.instance().getSessionId();

            validateNetmaskSize(netmaskSize);
            assignAndSaveIpAddress(model.getAuftragId(), purpose, netmaskSize, site, sessionId);

            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void assignAndSaveIpAddress(Long auftragId, Reference purpose, Integer netmaskSize, Reference site,
            Long sessionId) throws StoreException, GUIException, ServiceNotFoundException {
        IPAddress assignedIPAddress = null;
        if (isIPV4Selected()) {
            assignedIPAddress = getIpAddressService().assignIPV4(model.getAuftragId(), purpose, netmaskSize, site,
                    sessionId);
        }
        else if (isIPV6Selected()) {
            validateNumberOfAlreadyAssignedIPV6Prefix();
            assignedIPAddress = getIpAddressService().assignIPV6(model.getAuftragId(), purpose, netmaskSize, site,
                    sessionId);
        }
        else {
            throw new GUIException("Die Reservierung ist für das selektierte IP Protokoll nicht ausführbar!");
        }

        if (assignedIPAddress == null) {
            throw new GUIException("Es konnte keine gültige IP Adresse reserviert werden!");
        }
        getIpAddressService().saveIPAddress(assignedIPAddress, sessionId);
    }

    private void validateNumberOfAlreadyAssignedIPV6Prefix() throws ServiceNotFoundException, GUIException {
        try {
            AuftragDaten auftragDaten = getCCService(CCAuftragService.class).findAuftragDatenByAuftragId(
                    this.model.getAuftragId());
            List<IPAddressPanelView> allIPAs = getIpAddressService().findAllIPAddressPanelViews(
                    auftragDaten.getAuftragNoOrig());
            for (IPAddressPanelView ipView : allIPAs) {
                IPAddress ip = ipView.getIpAddress();
                if (isIPV6Selected() && ip.isIPV6() && ip.isPrefixAddress()
                        && DateTools.isDateAfter(ip.getGueltigBis(), new Date())) {
                    throw new GUIException("Es kann nur ein DHCPv6-PD Präfix reserviert werden");
                }
            }
        }
        catch (FindException e) {
            throw new GUIException("Fehler beim Laden der Daten", e);
        }

    }

    @Override
    protected void validateSaveButton() {
        // verhindert die Überprüfung des Save Buttons auf Berechtigungen
    }
}
