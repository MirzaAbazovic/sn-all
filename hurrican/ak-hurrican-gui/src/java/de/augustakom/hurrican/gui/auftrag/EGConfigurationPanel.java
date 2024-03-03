/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2006 11:25:09
 */
package de.augustakom.hurrican.gui.auftrag;

import static de.augustakom.hurrican.model.cc.Ansprechpartner.Typ.*;
import static de.augustakom.hurrican.model.cc.EndgeraetIp.AddressType.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.exceptions.UpdateException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.Either;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.dao.cc.VrrpPriority;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.shared.PortForwardingTableModel;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.AnsprechpartnerField;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponent;
import de.augustakom.hurrican.gui.base.ip.AKJIPAddressComponentFactory;
import de.augustakom.hurrican.model.cc.AddressTypeEnum;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.EndgeraetIp;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.IPAddress;
import de.augustakom.hurrican.model.cc.PortForwarding;
import de.augustakom.hurrican.model.cc.Routing;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.ServiceVertrag;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.IPAddressService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;
import de.augustakom.hurrican.service.cc.utils.CalculatedSipDomain4VoipAuftrag;
import de.augustakom.hurrican.service.cc.utils.CalculatedSwitch4VoipAuftrag;

/**
 * Panel fuer die Konfiguration von einem Endgeraet.
 *
 *
 */
public class EGConfigurationPanel extends AbstractDataPanel implements AKObjectSelectionListener, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(EGConfigurationPanel.class);

    private static final int RANDOM_PASSWORD_LENGTH = 8;
    private static final String WATCH_EG_CONFIG = "watch.ip.eg.config";
    private static final String WATCH_EG_2_AUFTRAG = "watch.eg2auftrag";
    private static final String LAYER2_PROTOCOL = "layer2Protocol";
    private static final String SERVICE_VERTRAG = "servicevertrag";
    private static final String VRRP_PRIORITY = "vrrp.priority";
    private static final String TRIGGERPUNKT = "triggerpunkt";
    private static final String DNS_SERVER_IP = "dns.server.ip";
    private static final String DHCP_POOL_FROM = "dhcp.pool.from";
    private static final String DHCP_POOL_TO = "dhcp.pool.to";
    private static final String NEW_LAN_IP = "new.lan.ip";
    private static final String NEW_LAN_VRRP_IP = "new.lan.vrrp.ip";
    private static final String SELECT_WAN_IP = "select.wan.ip";
    private static final String DEL_ENDGERAET_IP = "del.endgeraet.ip";

    private static final String MTU = "mtu";
    private static final int TAB_INDEX_EG_IPS = 0;
    private static final int TAB_INDEX_PORTFORWARDINGS = 1;
    private static final int TAB_INDEX_ROUTINGS = 2;
    private static final int TAB_INDEX_S0BACKUP = 3;
    private static final long serialVersionUID = 815664875067871516L;
    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/EGConfigurationPanel.xml";
    private static final String ROUTING_MODEL_RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/EGConfigPanelRoutingModel.xml";
    // Sonstiges
    private final Long eg2AuftragId;
    private final List<AKManageableComponent> manageableComponents = new ArrayList<>();
    // GUI-Komponenten
    private AKJComboBox cbHersteller = null;
    private AKJComboBox cbModell = null;
    private AKJTextField tfModellZusatz = null;
    private AKJTextField tfSerialNo = null;
    private AKJCheckBox chbNAT = null;
    private AKJCheckBox chbDHCP = null;
    private AKJCheckBox chbWanIpFest = null;
    private AKJCheckBox chbQoSActive = null;
    private AKJIPAddressComponent ipcDhcpPoolFrom = null;
    private AKJIPAddressComponent ipcDhcpPoolTo = null;
    private AKJFormattedTextField tfMTU = null;
    private AKJTextField tfDNSServerIP = null;
    private AKJCheckBox chbDns = null;
    private AnsprechpartnerField tfAnspAdmin = null;
    private AnsprechpartnerField tfAnspTech = null;
    private AKJFormattedTextField tfWanVc = null;
    private AKJFormattedTextField tfWanVp = null;
    private AKJFormattedTextField tfIPCount = null;
    private AKJTextArea taBemerkung = null;
    private AKJTextArea taBemerkungKunde = null;
    private AKJTextField tfEGUser = null;
    private AKJTextField tfEGPassword = null;
    private AKJTable tbPortForwards = null;
    private AKJTable tbIPs = null;
    private AKJTable tbRoutings = null;
    private AKJTable tbAcls = null;
    private PortForwardingTableModel tbMdlPortForwardings = null;
    private AKReflectionTableModel<EndgeraetAcl> tbModelAcls = null;
    private AKTableModelXML<Routing> tbModelRoutings = null;
    private EndgeraetIpTableModel tbModelIPs = null;
    private AKJButton btnNewPF = null;
    private AKJButton btnDelPF = null;
    private AKJButton btnNewLanIp = null;
    private AKJButton btnNewLanVrrpIp = null;
    private AKJButton btnSelectWanIp = null;
    private AKJButton btnDelEGIp = null;
    private AKJButton btnNewRouting = null;
    private AKJButton btnDelRouting = null;
    private AKJButton btnCreatePwd = null;
    private AKJTabbedPane confPane = null;
    private AKJIPAddressComponent ipcTriggerpunkt = null;
    private AKJTextField tfCalledStationId = null;
    private AKJTextField tfCallingStationId = null;
    private AKJFormattedTextField tfIntervall = null;
    private AKJFormattedTextField tfReEnable = null;
    private AKJFormattedTextField tfIdleTimer = null;
    private AKJFormattedTextField tfAttempts = null;
    private AKJFormattedTextField tfFrequency = null;
    private AKJCheckBox chbBuendelung = null;
    private AKJComboBox cbEndstelle = null;
    private AKJComboBox cbLayer2Prot = null;
    private AKJComboBox cbServiceVertrag = null;
    private AKJComboBox cbVrrpPriority = null;
    private AKJCheckBox chbSnmpMNet = null;
    private AKJCheckBox chbSnmpCustomer = null;
    private AKJTextField tfDHCPv6_PD = null;
    // Modelle
    private EG eg;
    private EGConfig egConfig;
    private IPAddress DHCPv6_PD;
    private EG2Auftrag eg2Auftrag;
    private Long billingOrderNo;
    // Services
    private EndgeraeteService endgeraeteService;
    private AnsprechpartnerService ansprechpartnerService;
    private IPAddressService ipAddressService;
    private CCAuftragService auftragService;
    // Neue Config oder alte editieren?
    private boolean egConfigIsNew;
    private boolean herstellerChangeActive;
    private AKJTextField tfSoftwareStand;

    /**
     * Konstruktor mit Angabe der EG-2-Auftrag Mapping-ID auf die sich die Konfiguration bezieht.
     */
    public EGConfigurationPanel(Long eg2AuftragId) {
        super(RESOURCE);
        this.eg2AuftragId = eg2AuftragId;
        setupServices();
        createGUI();
        readModel();
    }

    private void setupServices() {
        try {
            endgeraeteService = getCCService(EndgeraeteService.class);
            ansprechpartnerService = getCCService(AnsprechpartnerService.class);
            ipAddressService = getCCService(IPAddressService.class);
            auftragService = getCCService(CCAuftragService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(),
                    new HurricanGUIException("Die Services konnten nicht geladen werden!", e));
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {

        int actLine = 0;
        AKJPanel left = new AKJPanel(new GridBagLayout());
        addHerstellerField(left, actLine++);
        addModellField(left, actLine++);
        addModellZusatzField(left, actLine++);
        addSerialNumberField(left, actLine++);
        addSoftwareversionField(left, actLine++);
        addWanIpFestCheckbox(left, actLine++);
        addNatCheckbox(left, actLine++);
        addDhcpCheckbox(left, actLine++);
        addDhcpPoolFromField(left, actLine++);
        addDhcpPoolToField(left, actLine++);
        addMTUField(left, actLine++);
        addServiceVertragField(left, actLine++);
        addVrrpPriorityField(left, actLine);

        AKJPanel right = new AKJPanel(new GridBagLayout());
        actLine = 0;
        addEndstelleField(right, actLine++);
        addWanVpVcField(right, actLine++);
        addZugangField(right, actLine++);
        addIpCountField(right, actLine++);
        addLayer2ProtocolField(right, actLine++);
        addAnspPartnerAdminField(right, actLine++);
        addAnspPartnerTechnikField(right, actLine++);
        addDnsCheckbox(right, actLine++);
        addDNSServerIP(right, actLine++);
        addSnmpMNetCheckbox(right, actLine++);
        addSnmpCustomerCheckbox(right, actLine++);
        addQoSCheckbox(right, actLine++);
        //noinspection UnusedAssignment
        actLine++;

        AKJPanel bottom = new AKJPanel(new GridBagLayout());
        actLine = 0;
        //noinspection UnusedAssignment
        addBemerkungField(bottom, actLine++);
        addBemerkungKundeField(bottom, actLine + 3);

        // @formatter:off
        AKJPanel top = new AKJPanel(new GridBagLayout(), "Endgeräte-Konfiguration");
        top.add(left            , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        top.add(right           , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.VERTICAL, 35));
        top.add(bottom          , GBCFactory.createGBC(  0,  0, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()  , GBCFactory.createGBC(100,100, 2, 2, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        confPane = new AKJTabbedPane();
        addIPTab(confPane);
        addPortForwardingTab(confPane);
        addStatischeRoutenTab(confPane);
        addSOBackupTab(confPane);
        addAclTab(confPane);

        AKJPanel center = new AKJPanel(new BorderLayout());
        center.add(confPane, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);

        manageGUI(manageableComponents.toArray(new AKManageableComponent[manageableComponents.size()]));
        enableFields(false);
    }

    private void addDnsCheckbox(AKJPanel panel, int line) {
        AKJLabel lblDnsActive = getSwingFactory().createLabel("dns.active");
        chbDns = getSwingFactory().createCheckBox("dns.active", false);
        chbDns.addItemListener(this);
        // @formatter:off
        panel.add(lblDnsActive  , GBCFactory.createGBC(  0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbDns        , GBCFactory.createGBC(  0, 0, 2, line, 1, 1, GridBagConstraints.WEST));
        panel.add(new AKJPanel(), GBCFactory.createGBC(  0, 0, 3, line, 1, 1, GridBagConstraints.REMAINDER));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, line, 1, 1, GridBagConstraints.REMAINDER));
        // @formatter:on
    }

    private void addSnmpMNetCheckbox(AKJPanel panel, int line) {
        AKJLabel lblSnmpMNet = getSwingFactory().createLabel("snmp.mnet");
        chbSnmpMNet = getSwingFactory().createCheckBox("snmp.mnet", false);
        chbSnmpMNet.addItemListener(this);
        //@formatter:off
        panel.add(lblSnmpMNet   , GBCFactory.createGBC(0  , 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbSnmpMNet   , GBCFactory.createGBC(0  , 0, 2, line, 1, 1, GridBagConstraints.WEST));
        panel.add(new AKJPanel(), GBCFactory.createGBC(0  , 0, 3, line, 1, 1, GridBagConstraints.REMAINDER));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, line, 1, 1, GridBagConstraints.REMAINDER));
        //@formatter:on
    }

    private void addSnmpCustomerCheckbox(AKJPanel panel, int line) {
        AKJLabel lblSnmpCustomer = getSwingFactory().createLabel("snmp.customer");
        chbSnmpCustomer = getSwingFactory().createCheckBox("snmp.customer", false);
        chbSnmpCustomer.addItemListener(this);
        //@formatter:off
        panel.add(lblSnmpCustomer, GBCFactory.createGBC(0  , 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbSnmpCustomer, GBCFactory.createGBC(0  , 0, 2, line, 1, 1, GridBagConstraints.WEST));
        panel.add(new AKJPanel() , GBCFactory.createGBC(0  , 0, 3, line, 1, 1, GridBagConstraints.REMAINDER));
        panel.add(new AKJPanel() , GBCFactory.createGBC(100, 0, 4, line, 1, 1, GridBagConstraints.REMAINDER));
        //@formatter:on
    }

    private void addDNSServerIP(AKJPanel panel, int line) {
        AKJLabel lblDNSServerIP = getSwingFactory().createLabel(DNS_SERVER_IP);
        tfDNSServerIP = getSwingFactory().createTextField(DNS_SERVER_IP);
        panel.add(lblDNSServerIP, GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfDNSServerIP, GBCFactory.createGBC(0, 0, 2, line, 5, 1, GridBagConstraints.HORIZONTAL));
    }

    private void addQoSCheckbox(AKJPanel panel, int line) {
        AKJLabel lblQoSActive = getSwingFactory().createLabel("qos.active");
        chbQoSActive = getSwingFactory().createCheckBox("qos.active", false);
        // @formatter:off
        panel.add(lblQoSActive  , GBCFactory.createGBC(  0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbQoSActive  , GBCFactory.createGBC(  0, 0, 2, line, 1, 1, GridBagConstraints.WEST));
        panel.add(new AKJPanel(), GBCFactory.createGBC(  0, 0, 3, line, 1, 1, GridBagConstraints.REMAINDER));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, line, 1, 1, GridBagConstraints.REMAINDER));
        // @formatter:on
    }

    private void addDhcpPoolFromField(AKJPanel panel, int line) {
        AKJLabel lblDhcpPoolFrom = getSwingFactory().createLabel(DHCP_POOL_FROM);
        ipcDhcpPoolFrom = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(DHCP_POOL_FROM);
        ipcDhcpPoolFrom.setMinimumSize(new Dimension(100, ipcDhcpPoolFrom.getPreferredSize().height));
        ipcDhcpPoolFrom.setPreferredSize(new Dimension(300, ipcDhcpPoolFrom.getPreferredSize().height));
        // @formatter:off
        panel.add(lblDhcpPoolFrom, GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(ipcDhcpPoolFrom, GBCFactory.createGBC(0, 0, 2, line, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addDhcpPoolToField(AKJPanel panel, int line) {
        AKJLabel lblDhcpPoolTo = getSwingFactory().createLabel(DHCP_POOL_TO);
        ipcDhcpPoolTo = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(DHCP_POOL_TO);
        ipcDhcpPoolTo.setMinimumSize(new Dimension(100, ipcDhcpPoolTo.getPreferredSize().height));
        ipcDhcpPoolTo.setPreferredSize(new Dimension(300, ipcDhcpPoolTo.getPreferredSize().height));
        // @formatter:off
        panel.add(lblDhcpPoolTo, GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(ipcDhcpPoolTo, GBCFactory.createGBC(0, 0, 2, line, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addStatischeRoutenTab(AKJTabbedPane tabbedPane) {
        tbModelRoutings = new AKTableModelXML<>(ROUTING_MODEL_RESOURCE);
        tbRoutings = new AKJTable(tbModelRoutings, JTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbRoutings.fitTable(new int[] { 150, 30, 150, 50, 150, 300 });
        tbRoutings.addMouseListener(new AKTableDoubleClickMouseListener(this));

        btnNewRouting = getSwingFactory().createButton("new.routing", getActionListener(), null);
        manageableComponents.add(btnNewRouting);
        btnDelRouting = getSwingFactory().createButton("del.routing", getActionListener(), null);
        manageableComponents.add(btnDelRouting);

        AKJScrollPane spRoutings = new AKJScrollPane(tbRoutings, new Dimension(700, 150));
        AKJPanel panelRoutings = new AKJPanel(new GridBagLayout(), "Statische Routen");
        // @formatter:off
        panelRoutings.add(btnNewRouting , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panelRoutings.add(btnDelRouting , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        panelRoutings.add(new AKJPanel(), GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        panelRoutings.add(spRoutings    , GBCFactory.createGBC(100,100, 1, 0, 1, 3, GridBagConstraints.BOTH));
        // @formatter:on

        tabbedPane.addTab("Statische Routen", panelRoutings);
    }

    private void addPortForwardingTab(AKJTabbedPane tabbedPane) {
        tbMdlPortForwardings = new PortForwardingTableModel();
        tbPortForwards = new AKJTable(tbMdlPortForwardings,
                JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbPortForwards.fitTable(new int[] { 30, 35, 100, 100, 40, 40, 100, 200 });
        tbPortForwards.addMouseListener(new AKTableDoubleClickMouseListener(this));

        btnNewPF = getSwingFactory().createButton("new.port.forward", getActionListener(), null);
        manageableComponents.add(btnNewPF);
        btnDelPF = getSwingFactory().createButton("del.port.forward", getActionListener(), null);
        manageableComponents.add(btnDelPF);

        AKJScrollPane spForwards = new AKJScrollPane(tbPortForwards, new Dimension(700, 150));
        AKJPanel panelPortForwarding = new AKJPanel(new GridBagLayout(), "Port-Forwardings");
        // @formatter:off
        panelPortForwarding.add(btnNewPF        , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panelPortForwarding.add(btnDelPF        , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        panelPortForwarding.add(new AKJPanel()  , GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        panelPortForwarding.add(spForwards      , GBCFactory.createGBC(100,100, 1, 0, 1, 3, GridBagConstraints.BOTH));
        // @formatter:on

        tabbedPane.addTab("Port-Forwarding", panelPortForwarding);
    }

    private void addAclTab(AKJTabbedPane tabbedPane) {
        tbModelAcls = new AKReflectionTableModel<>(
                new String[] { "Name", "Router Typ" },
                new String[] { "name", "routerTyp" },
                new Class[] { String.class, String.class, String.class });
        tbAcls = new AKJTable(tbModelAcls,
                JTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbAcls.attachSorter();

        AKJButton btnAddAcl = getSwingFactory().createButton("add.acl", getActionListener(), null);
        manageableComponents.add(btnAddAcl);
        AKJButton btnDelAcl = getSwingFactory().createButton("del.acl", getActionListener(), null);
        manageableComponents.add(btnDelAcl);

        AKJScrollPane spAcls = new AKJScrollPane(tbAcls, new Dimension(700, 150));
        AKJPanel panelAcls = new AKJPanel(new GridBagLayout(), "ACL");
        // @formatter:off
        panelAcls.add(btnAddAcl     , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panelAcls.add(btnDelAcl     , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        panelAcls.add(new AKJPanel(), GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        panelAcls.add(spAcls        , GBCFactory.createGBC(100,100, 1, 0, 1, 3, GridBagConstraints.BOTH));
        // @formatter:on

        tabbedPane.addTab("ACL", panelAcls);
    }

    private void addIPTab(AKJTabbedPane tabbedPane) {
        tbModelIPs = new EndgeraetIpTableModel();
        tbIPs = new AKJTable(tbModelIPs, JTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbIPs.fitTable(new int[] { 110, 30, 110, 50, 50 });
        tbIPs.addMouseListener(new AKTableDoubleClickMouseListener(this));

        AKJLabel lblDHCPv6_PD = getSwingFactory().createLabel("ip.dhcpv6_pd");
        tfDHCPv6_PD = getSwingFactory().createTextField("ip.dhcpv6_pd");
        tfDHCPv6_PD.setEditable(false);

        AKJPanel panelDHCPv6_PD = new AKJPanel(new GridBagLayout());
        //@formatter:off
        panelDHCPv6_PD.add(lblDHCPv6_PD  , GBCFactory.createGBC(  5,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelDHCPv6_PD.add(tfDHCPv6_PD   , GBCFactory.createGBC( 50,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelDHCPv6_PD.add(new AKJPanel(), GBCFactory.createGBC( 50,  0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        //@formatter:on

        btnNewLanIp = getSwingFactory().createButton(NEW_LAN_IP, getActionListener(), null);
        manageableComponents.add(btnNewLanIp);
        btnNewLanVrrpIp = getSwingFactory().createButton(NEW_LAN_VRRP_IP, getActionListener(), null);
        manageableComponents.add(btnNewLanVrrpIp);
        btnSelectWanIp = getSwingFactory().createButton(SELECT_WAN_IP, getActionListener(), null);
        manageableComponents.add(btnSelectWanIp);
        btnDelEGIp = getSwingFactory().createButton(DEL_ENDGERAET_IP, getActionListener(), null);
        manageableComponents.add(btnDelEGIp);

        AKJScrollPane spIPs = new AKJScrollPane(tbIPs, new Dimension(700, 150));
        AKJPanel panelEndgeraetIps = new AKJPanel(new GridBagLayout(), "IPs");
        //@formatter:off
        panelEndgeraetIps.add(panelDHCPv6_PD , GBCFactory.createGBC(100,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelEndgeraetIps.add(btnNewLanIp    , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        panelEndgeraetIps.add(btnNewLanVrrpIp, GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.NONE));
        panelEndgeraetIps.add(btnSelectWanIp , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.NONE));
        panelEndgeraetIps.add(btnDelEGIp     , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.NONE));
        panelEndgeraetIps.add(new AKJPanel() , GBCFactory.createGBC(  0,100, 0, 5, 1, 1, GridBagConstraints.VERTICAL));
        panelEndgeraetIps.add(spIPs          , GBCFactory.createGBC(100,100, 1, 1, 1, 5, GridBagConstraints.BOTH));
        //@formatter:on

        tabbedPane.addTab("IPs", panelEndgeraetIps);
    }

    private void addSOBackupTab(AKJTabbedPane tabbedPane) {
        AKJLabel lblTriggerpunkt = getSwingFactory().createLabel(TRIGGERPUNKT);
        ipcTriggerpunkt = AKJIPAddressComponentFactory.create(RESOURCE).createIPAddressComponent(TRIGGERPUNKT);
        ipcTriggerpunkt.setMinimumSize(new Dimension(200, ipcTriggerpunkt.getPreferredSize().height));
        ipcTriggerpunkt.setPreferredSize(new Dimension(300, ipcTriggerpunkt.getPreferredSize().height));
        AKJLabel lblCallingStationId = getSwingFactory().createLabel("calling.station.id");
        tfCalledStationId = getSwingFactory().createTextField("called.station.id");
        AKJLabel lblCalledStationId = getSwingFactory().createLabel("called.station.id");
        tfCallingStationId = getSwingFactory().createTextField("calling.station.id");
        AKJLabel lblIntervall = getSwingFactory().createLabel("intervall");
        tfIntervall = getSwingFactory().createFormattedTextField("intervall");
        AKJLabel lblReEnable = getSwingFactory().createLabel("re.enable");
        tfReEnable = getSwingFactory().createFormattedTextField("re.enable");
        AKJLabel lblIdleTimer = getSwingFactory().createLabel("idle.timer");
        tfIdleTimer = getSwingFactory().createFormattedTextField("idle.timer");
        AKJLabel lblAttemps = getSwingFactory().createLabel("attempts");
        tfAttempts = getSwingFactory().createFormattedTextField("attempts");
        AKJLabel lblFrequency = getSwingFactory().createLabel("frequency");
        tfFrequency = getSwingFactory().createFormattedTextField("frequency");
        AKJLabel lblBuendelung = getSwingFactory().createLabel("kanalbuendelung");
        chbBuendelung = getSwingFactory().createCheckBox("kanalbuendelung");

        AKJPanel panelS0Backup = new AKJPanel(new GridBagLayout());
        // @formatter:off
        panelS0Backup.add(lblTriggerpunkt       , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(ipcTriggerpunkt       , GBCFactory.createGBC(  0,  0, 2, 1, 6, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        panelS0Backup.add(lblCalledStationId    , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        panelS0Backup.add(tfCalledStationId     , GBCFactory.createGBC(  0,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 3, 0, 1, 1, GridBagConstraints.NONE));
        panelS0Backup.add(lblIdleTimer          , GBCFactory.createGBC(  0,  0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 5, 0, 1, 1, GridBagConstraints.NONE));
        panelS0Backup.add(tfIdleTimer           , GBCFactory.createGBC(  0,  0, 6, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(new AKJPanel()        , GBCFactory.createGBC(100,  0, 8, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(lblAttemps            , GBCFactory.createGBC(  0,  0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(tfAttempts            , GBCFactory.createGBC(  0,  0, 6, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(lblCallingStationId   , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(tfCallingStationId    , GBCFactory.createGBC(  0,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(lblFrequency          , GBCFactory.createGBC(  0,  0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(tfFrequency           , GBCFactory.createGBC(  0,  0, 6, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(lblIntervall          , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(tfIntervall           , GBCFactory.createGBC(  0,  0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(lblBuendelung         , GBCFactory.createGBC(  0,  0, 4, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(chbBuendelung         , GBCFactory.createGBC(  0,  0, 6, 5, 1, 1, GridBagConstraints.NONE));
        panelS0Backup.add(new AKJPanel()        , GBCFactory.createGBC(  0,  0, 7, 5, 1, 1, GridBagConstraints.NONE));
        panelS0Backup.add(lblReEnable           , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(tfReEnable            , GBCFactory.createGBC(  0,  0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        panelS0Backup.add(new AKJPanel()        , GBCFactory.createGBC(  0,100, 0, 6, 1, 1, GridBagConstraints.VERTICAL));
        tabbedPane.addTab("ISDN-Backup", panelS0Backup);
        // @formatter:on
    }

    private void addZugangField(AKJPanel panel, int line) {
        AKJLabel lblEGZugang = getSwingFactory().createLabel("eg.zugang");
        tfEGUser = getSwingFactory().createTextField("eg.user");
        manageableComponents.add(tfEGUser);
        tfEGPassword = getSwingFactory().createTextField("eg.password");
        manageableComponents.add(tfEGPassword);
        btnCreatePwd = getSwingFactory().createButton("create.password", getActionListener(), null);
        manageableComponents.add(btnCreatePwd);
        // @formatter:off
        panel.add(lblEGZugang   , GBCFactory.createGBC(  0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEGUser      , GBCFactory.createGBC(100, 0, 2, line, 3, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfEGPassword  , GBCFactory.createGBC(100, 0, 5, line, 3, 1, GridBagConstraints.HORIZONTAL));
        panel.add(btnCreatePwd  , GBCFactory.createGBC(  0, 0, 8, line, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
    }

    private void addBemerkungField(AKJPanel panel, int line) {
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        taBemerkung = getSwingFactory().createTextArea("bemerkung", true, true, true);
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung, new Dimension(600, 80));
        // @formatter:off
        panel.add(lblBemerkung  , GBCFactory.createGBC(  0,  0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(spBemerkung   , GBCFactory.createGBC(  0,  0, 1, line, 1, 2, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100,100, 2, line + 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    private void addBemerkungKundeField(AKJPanel panel, int line) {
        AKJLabel lblBemerkungKunde = getSwingFactory().createLabel("bemerkungKunde");
        taBemerkungKunde = getSwingFactory().createTextArea("bemerkungKunde", true, true, true);
        AKJScrollPane spBemerkungKunde = new AKJScrollPane(taBemerkungKunde, new Dimension(600, 80));
        // @formatter:off
        panel.add(lblBemerkungKunde  , GBCFactory.createGBC(  0,  0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(spBemerkungKunde   , GBCFactory.createGBC(  0,  0, 1, line, 1, 2, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(),       GBCFactory.createGBC(100,100, 2, line + 1, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }

    private void addIpCountField(AKJPanel panel, int line) {
        AKJLabel lblIPCount = getSwingFactory().createLabel("ip.count");
        tfIPCount = getSwingFactory().createFormattedTextField("ip.count");
        // @formatter:off
        panel.add(lblIPCount, GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfIPCount , GBCFactory.createGBC(0, 0, 2, line, 6, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addEndstelleField(AKJPanel panel, int line) {
        AKJLabel lblEndstelle = getSwingFactory().createLabel("endstelle");
        cbEndstelle = getSwingFactory().createComboBox("endstelle", new AKCustomListCellRenderer<>(Endstelle.class, Endstelle::getTypeWithName));
        cbEndstelle.addItemListener(this);
        // @formatter:off
        panel.add(lblEndstelle  , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbEndstelle   , GBCFactory.createGBC(0, 0, 2, line, 6, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addLayer2ProtocolField(AKJPanel panel, int line) {
        AKJLabel lblLayer2Prot = getSwingFactory().createLabel(LAYER2_PROTOCOL);
        cbLayer2Prot = getSwingFactory().createComboBox(LAYER2_PROTOCOL, new Layer2ProtComboBoxRenderer());
        // @formatter:off
        panel.add(lblLayer2Prot , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbLayer2Prot  , GBCFactory.createGBC(0, 0, 2, line, 6, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
        cbLayer2Prot.setEnabled(false);
    }

    private void addMTUField(AKJPanel panel, int line) {
        AKJLabel lblMTU = getSwingFactory().createLabel(MTU);
        tfMTU = getSwingFactory().createFormattedTextField(MTU);
        // @formatter:off
        panel.add(lblMTU, GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfMTU , GBCFactory.createGBC(0, 0, 2, line, 6, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addServiceVertragField(AKJPanel panel, int line) {
        AKJLabel lblServiceVertrag = getSwingFactory().createLabel(SERVICE_VERTRAG);
        cbServiceVertrag = getSwingFactory().createComboBox(SERVICE_VERTRAG, new ServiceVertragComboBoxRenderer());
        // @formatter:off
        panel.add(lblServiceVertrag , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbServiceVertrag  , GBCFactory.createGBC(0, 0, 2, line, 6, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addVrrpPriorityField(AKJPanel panel, int line) {
        AKJLabel lblVrrpPriority = getSwingFactory().createLabel(VRRP_PRIORITY);
        cbVrrpPriority = getSwingFactory().createComboBox(VRRP_PRIORITY, new VrrpPriorityComboBoxRenderer());
        // @formatter:off
        panel.add(lblVrrpPriority , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbVrrpPriority  , GBCFactory.createGBC(0, 0, 2, line, 6, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addWanVpVcField(AKJPanel panel, int line) {
        AKJLabel lblWanVpVc = getSwingFactory().createLabel("wan.vpvc");
        AKJLabel lblWanVp = getSwingFactory().createLabel("wan.vp", SwingConstants.RIGHT);
        AKJLabel lblWanVc = getSwingFactory().createLabel("wan.vc", SwingConstants.RIGHT);
        tfWanVc = getSwingFactory().createFormattedTextField("wan.vc");
        tfWanVp = getSwingFactory().createFormattedTextField("wan.vp");
        // @formatter:off
        panel.add(lblWanVpVc, GBCFactory.createGBC( 0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblWanVp  , GBCFactory.createGBC(50, 0, 2, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfWanVp   , GBCFactory.createGBC(50, 0, 3, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(lblWanVc  , GBCFactory.createGBC(50, 0, 5, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfWanVc   , GBCFactory.createGBC(50, 0, 6, line, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addAnspPartnerTechnikField(AKJPanel panel, int line) {
        AKJLabel lblAnspTech = getSwingFactory().createLabel("ansp.tech");
        tfAnspTech = new AnsprechpartnerField();
        tfAnspTech.setMaxContactLength(50);
        // @formatter:off
        panel.add(lblAnspTech   , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfAnspTech    , GBCFactory.createGBC(0, 0, 2, line, 5, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addAnspPartnerAdminField(AKJPanel panel, int line) {
        AKJLabel lblAnspAdmin = getSwingFactory().createLabel("ansp.admin");
        tfAnspAdmin = new AnsprechpartnerField();
        tfAnspAdmin.setMaxContactLength(50);
        // @formatter:off
        panel.add(lblAnspAdmin  , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfAnspAdmin   , GBCFactory.createGBC(0, 0, 2, line, 5, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addDhcpCheckbox(AKJPanel panel, int line) {
        AKJLabel lblDHCP = getSwingFactory().createLabel("dhcp");
        chbDHCP = getSwingFactory().createCheckBox("dhcp", false);
        chbDHCP.addItemListener(this);
        // @formatter:off
        panel.add(lblDHCP       , GBCFactory.createGBC(  0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbDHCP       , GBCFactory.createGBC(  0, 0, 2, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, line, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addWanIpFestCheckbox(AKJPanel panel, int line) {
        AKJLabel lblWanIpFest = getSwingFactory().createLabel("wan.ip.fest");
        chbWanIpFest = getSwingFactory().createCheckBox("wan.ip.fest", false);
        chbWanIpFest.addItemListener(this);
        // @formatter:off
        panel.add(lblWanIpFest  , GBCFactory.createGBC(  0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbWanIpFest  , GBCFactory.createGBC(  0, 0, 2, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, line, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addNatCheckbox(AKJPanel panel, int line) {
        AKJLabel lblNAT = getSwingFactory().createLabel("nat");
        chbNAT = getSwingFactory().createCheckBox("nat", true);
        chbNAT.addItemListener(this);
        // @formatter:off
        panel.add(lblNAT        , GBCFactory.createGBC(  0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(chbNAT        , GBCFactory.createGBC(  0, 0, 2, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(new JLabel()  , GBCFactory.createGBC(100, 0, 3, line, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addSerialNumberField(AKJPanel panel, int line) {
        AKJLabel lblSerialNo = getSwingFactory().createLabel("serial.no");
        tfSerialNo = getSwingFactory().createTextField("serial.no");
        // @formatter:off
        panel.add(lblSerialNo   , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfSerialNo    , GBCFactory.createGBC(0, 0, 2, line, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addSoftwareversionField(final AKJPanel panel, final int line) {
        final AKJLabel lblSwStand = getSwingFactory().createLabel("software.version");
        tfSoftwareStand = getSwingFactory().createTextField("software.version");
        // @formatter:off
        panel.add(lblSwStand        , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfSoftwareStand   , GBCFactory.createGBC(0, 0, 2, line, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addModellZusatzField(AKJPanel panel, int line) {
        AKJLabel lblModellZusatz = getSwingFactory().createLabel("modell.zusatz");
        tfModellZusatz = getSwingFactory().createTextField("modell.zusatz");
        // @formatter:off
        panel.add(lblModellZusatz   , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(tfModellZusatz    , GBCFactory.createGBC(0, 0, 2, line, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addModellField(AKJPanel panel, int line) {
        AKJLabel lblModell = getSwingFactory().createLabel("modell");
        cbModell = getSwingFactory().createComboBox("modell", new AKCustomListCellRenderer<>(String.class, String::toString));
        cbModell.setSize(new Dimension(120, 25));
        // @formatter:off
        panel.add(lblModell , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbModell  , GBCFactory.createGBC(0, 0, 2, line, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    private void addHerstellerField(AKJPanel panel, int line) {
        AKJLabel lblHersteller = getSwingFactory().createLabel("hersteller");
        cbHersteller = getSwingFactory().createComboBox("hersteller", new AKCustomListCellRenderer<>(String.class, String::toString));
        cbHersteller.setSize(new Dimension(120, 25));
        cbHersteller.addItemListener(this);
        // @formatter:off
        panel.add(lblHersteller , GBCFactory.createGBC(0, 0, 0, line, 1, 1, GridBagConstraints.HORIZONTAL));
        panel.add(cbHersteller  , GBCFactory.createGBC(0, 0, 2, line, 2, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on
    }

    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        if ("new.port.forward".equals(command)) {
            createPortForwarding();
        }
        else if ("del.port.forward".equals(command)) {
            deletePortForwarding();
        }
        else if (NEW_LAN_IP.equals(command)) {
            createIp4AddressType(LAN);
        }
        else if (NEW_LAN_VRRP_IP.equals(command)) {
            createIp4AddressType(LAN_VRRP);
        }
        else if (DEL_ENDGERAET_IP.equals(command)) {
            deleteEndgeraetIp();
        }
        else if ("new.routing".equals(command)) {
            createRouting();
        }
        else if ("del.routing".equals(command)) {
            deleteRouting();
        }
        else if ("add.acl".equals(command)) {
            addAcl();
        }
        else if ("del.acl".equals(command)) {
            delAcl();
        }
        else if ("create.password".equals(command)) {
            createPassword();
        }
        else if (SELECT_WAN_IP.equals(command)) {
            selectWanIp();
        }
    }

    @Override
    public final void readModel() {
        GuiTools.cleanFields(this);

        try {
            eg2Auftrag = endgeraeteService.findEG2AuftragById(eg2AuftragId);
            addObjectToWatch(WATCH_EG_2_AUFTRAG, eg2Auftrag);

            egConfig = eg2Auftrag.getEgConfigs().isEmpty() ? null : eg2Auftrag.getEgConfigs().iterator().next();
            if (egConfig == null) {
                egConfigIsNew = true;
                egConfig = new EGConfig();
                egConfig.setEg2AuftragId(eg2AuftragId);
                egConfig.setNatActive(Boolean.FALSE);
                egConfig.setDhcpActive(Boolean.FALSE);
                egConfig.setDnsServerActive(Boolean.FALSE);
                egConfig.setFirewallActive(Boolean.FALSE);
                egConfig.setWanIpFest(Boolean.FALSE);
                egConfig.setSchicht2Protokoll(endgeraeteService.getSchicht2Protokoll4Auftrag(eg2Auftrag));
                eg2Auftrag.setEgConfigs(Collections.singleton(egConfig));
            }
            addObjectToWatch(WATCH_EG_CONFIG, egConfig);

            enableDhcpRange(BooleanTools.nullToFalse(egConfig.getDhcpActive()));
            enableDNSSettings(BooleanTools.nullToFalse(egConfig.getDnsServerActive()));

            enableEndgeraetIPs(true);
            enableRoutings(true);

            if (eg2Auftrag != null) {
                eg = endgeraeteService.findEgById(eg2Auftrag.getEgId());
                if (eg2Auftrag.getAuftragId() != null) {
                    AuftragDaten auftragDaten = auftragService.findAuftragDatenByAuftragId(eg2Auftrag.getAuftragId());
                    if (auftragDaten != null) {
                        billingOrderNo = auftragDaten.getAuftragNoOrig();
                        DHCPv6_PD = ipAddressService.findDHCPv6PDPrefix(billingOrderNo);
                    }
                }
            }
            else {
                eg = null;
                DHCPv6_PD = null;
                billingOrderNo = null;
            }

            eg = (eg2Auftrag != null) ? endgeraeteService.findEgById(eg2Auftrag.getEgId()) : null;
            if (eg != null) {
                enablePortForwardings(BooleanTools.nullToFalse(eg.getConfPortforwarding()));
                enableS0Backup(BooleanTools.nullToFalse(eg.getConfS0backup()));
            }
            showModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private void showModel() {
        enableFields(true);
        tfSerialNo.setText(egConfig.getSerialNumber());
        chbNAT.setSelected(egConfig.getNatActive());
        chbDHCP.setSelected(egConfig.getDhcpActive());
        ipcDhcpPoolFrom.setIPAddress(egConfig.getDhcpPoolFromRef());
        ipcDhcpPoolTo.setIPAddress(egConfig.getDhcpPoolToRef());
        chbDns.setSelected(egConfig.getDnsServerActive());
        chbSnmpMNet.setSelected(egConfig.getSnmpMNet());
        chbSnmpCustomer.setSelected(egConfig.getSnmpCustomer());
        chbQoSActive.setSelected(egConfig.getQosActive());
        chbWanIpFest.setSelected(egConfig.getWanIpFest());
        tfIPCount.setValue(egConfig.getIpCount());
        tfWanVc.setValue(egConfig.getWanVc());
        tfWanVp.setValue(egConfig.getWanVp());
        taBemerkung.setText(egConfig.getBemerkung());
        taBemerkungKunde.setText(egConfig.getBemerkungKunde());
        tfEGUser.setText(egConfig.getEgUser());
        tfEGPassword.setText(egConfig.getEgPassword());
        tfMTU.setValue(egConfig.getMtu());
        tfDNSServerIP.setText(egConfig.getDnsServerIP());
        ipcTriggerpunkt.setIPAddress(egConfig.getTriggerpunktRef());
        tfCalledStationId.setText((egConfig.getCalledStationId()));
        tfCallingStationId.setText(egConfig.getCallingStationId());
        tfIntervall.setValue(egConfig.getIntervall());
        tfReEnable.setValue(egConfig.getReEnable());
        tfIdleTimer.setValue(egConfig.getIdleTimer());
        tfAttempts.setValue(egConfig.getAttemps());
        tfFrequency.setValue(egConfig.getFrequency());
        chbBuendelung.setSelected(egConfig.getKanalbuendelung());
        tfModellZusatz.setText(egConfig.getModellZusatz());
        tfSoftwareStand.setText(egConfig.getSoftwarestand());

        loadPortForwardings();
        loadEndgeraetIps();
        loadRoutings();

        try {
            loadEndgeraetAcls();
            loadAnsprechpartner();
            loadEndstellen();
            loadLayer2Prot();
            loadServiceVertrag();
            loadVrrpPriorities();
            loadHersteller();
            loadModelle();
            loadPrefixAddresses();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(),
                    new HurricanGUIException("Die Ansprechpartner konnten nicht ermittelt werden!", e));
        }
    }

    private void loadEndstellen() throws ServiceNotFoundException, FindException {
        if (eg2Auftrag != null) {
            EndstellenService endstellenService = getCCService(EndstellenService.class);
            List<Endstelle> possibleEndstellen = endstellenService.findEndstellen4Auftrag(eg2Auftrag.getAuftragId());
            cbEndstelle.removeItemListener(this);
            cbEndstelle.removeAllItems();
            cbEndstelle.addItems(possibleEndstellen, true, Endstelle.class);
            possibleEndstellen.stream()
                    .filter(endstelle -> endstelle.getId().equals(eg2Auftrag.getEndstelleId()))
                    .forEach(cbEndstelle::setSelectedItem);
            cbEndstelle.addItemListener(this);
        }
    }

    private void loadHersteller() throws FindException {
        herstellerChangeActive = true;
        if (CollectionUtils.isNotEmpty(eg.getEgTypes())) {
            loadConfiguredEGTypen("hersteller");
        }
        else {
            // Wenn kein Mapping vorhanden, dann alle Hersteller laden
            loadEGTypen("hersteller");
        }
        herstellerChangeActive = false;


    }

    private void loadModelle() throws FindException {
        herstellerChangeActive = true;
        if (CollectionUtils.isNotEmpty(eg.getEgTypes())) {
            loadConfiguredEGTypen("modell");
        }
        else {
            loadEGTypen("modell");
            cbModell.enableInputMethods(false);
        }
        herstellerChangeActive = false;
    }

    private void loadConfiguredEGTypen(String modellORHersteller) {
        List<EGType> possibleEGTypen;
        if (egConfig != null) {
            possibleEGTypen = eg.getEgTypes();
            if ((modellORHersteller != null) && modellORHersteller.equals("modell")) {
                List<String> possibleModell = new ArrayList<>(0);
                String hersteller = (egConfig.getEgType() != null) ? egConfig.getEgType().getHersteller()
                        : (String) cbHersteller.getSelectedItem();
                possibleEGTypen.stream()
                        .filter(egType -> StringUtils.equals(hersteller, egType.getHersteller())
                                && (possibleModell.indexOf(egType.getModell()) == -1))
                        .forEach(egType -> possibleModell.add(egType.getModell()));
                cbModell.removeAllItems();
                if (!possibleModell.isEmpty()) {
                    // Entweder ist EGConfig.EGType konfiguriert oder nicht gesetzt
                    Collections.sort(possibleModell, new StringComparator());
                    cbModell.addItems(possibleModell);
                }
                else if (egConfig.getEgType() != null) {
                    // EGConfig.EGType ist nicht konfiguriert
                    //noinspection unchecked
                    cbModell.addItem(egConfig.getEgType().getModell());
                }
            }
            else {
                List<String> possibleHersteller = new ArrayList<>(0);
                if (egConfig.getEgType() != null) {
                    possibleHersteller.add(egConfig.getEgType().getHersteller());
                }
                possibleEGTypen.stream()
                        .filter(egType -> possibleHersteller.indexOf(egType.getHersteller()) == -1)
                        .forEach(egType -> possibleHersteller.add(egType.getHersteller()));
                cbHersteller.removeAllItems();
                if (!possibleHersteller.isEmpty()) {
                    Collections.sort(possibleHersteller, new StringComparator());
                    cbHersteller.addItems(possibleHersteller);
                }
            }

            setExistingSelection(modellORHersteller);
        }
    }

    private void loadEGTypen(String modellORHersteller) throws FindException {
        List<String> possibleEGTypen;
        if (egConfig != null) {

            if ((modellORHersteller != null) && modellORHersteller.equals("modell")) {
                String hersteller = (egConfig.getEgType() != null) ? egConfig.getEgType().getHersteller()
                        : (String) cbHersteller.getSelectedItem();
                possibleEGTypen = endgeraeteService.getDistinctListOfModelsByManufacturer(hersteller);
                cbModell.removeAllItems();
                if (!possibleEGTypen.isEmpty()) {
                    Collections.sort(possibleEGTypen, new StringComparator());
                    cbModell.addItems(possibleEGTypen);
                }
            }
            else {
                possibleEGTypen = endgeraeteService.getDistinctListOfEGManufacturer();
                cbHersteller.removeAllItems();
                if (!possibleEGTypen.isEmpty()) {
                    Collections.sort(possibleEGTypen, new StringComparator());
                    cbHersteller.addItems(possibleEGTypen);
                }
            }

            setExistingSelection(modellORHersteller);
        }
    }

    private void setExistingSelection(String modellORHersteller) {
        // bestehende Auswahl setzen
        if (egConfig.getEgType() != null) {
            if ((modellORHersteller != null) && modellORHersteller.equals("modell")) {
                cbModell.setSelectedItem(egConfig.getEgType().getModell());
            }
            else {
                cbHersteller.setSelectedItem(egConfig.getEgType().getHersteller());
            }
        }
    }

    private void loadLayer2Prot() {
        if (egConfig != null) {
            List<Schicht2Protokoll> values = new ArrayList<>();
            values.add(null);
            values.addAll(Arrays.asList(Schicht2Protokoll.values()));
            cbLayer2Prot.removeAllItems();
            cbLayer2Prot.addItems(values, true);
            cbLayer2Prot.setSelectedItem(egConfig.getSchicht2Protokoll());
        }
    }

    private void loadServiceVertrag() {
        if (egConfig != null) {
            List<ServiceVertrag> values = new ArrayList<>();
            values.add(null);
            values.addAll(Arrays.asList(ServiceVertrag.values()));
            cbServiceVertrag.removeAllItems();
            cbServiceVertrag.addItems(values, true);
            cbServiceVertrag.setSelectedItem(egConfig.getServiceVertrag());
        }
    }

    private void loadVrrpPriorities() {
        if (egConfig != null) {
            List<VrrpPriority> values = new ArrayList<>();
            values.add(null);
            values.addAll(Arrays.asList(VrrpPriority.values()));
            cbVrrpPriority.removeAllItems();
            cbVrrpPriority.addItems(values, true);
            cbVrrpPriority.setSelectedItem(egConfig.getVrrpPriority());
        }
    }

    private void loadPrefixAddresses() throws FindException {
        List<IPAddress> prefixAddresses = ipAddressService.findV6PrefixesByBillingOrderNumber(billingOrderNo);
        ipcDhcpPoolFrom.setPrefixAddresses(prefixAddresses);
        ipcDhcpPoolTo.setPrefixAddresses(prefixAddresses);
        ipcTriggerpunkt.setPrefixAddresses(prefixAddresses);
    }

    /* techn. und administrativen Ansprechpartner laden */
    private void loadAnsprechpartner() throws FindException {
        Ansprechpartner anspTech = ansprechpartnerService.findPreferredAnsprechpartner(TECH_SERVICE, eg2Auftrag.getAuftragId());
        tfAnspTech.setAnsprechpartner(anspTech);

        Ansprechpartner anspAdmin = ansprechpartnerService.findPreferredAnsprechpartner(HOTLINE_SERVICE, eg2Auftrag.getAuftragId());
        tfAnspAdmin.setAnsprechpartner(anspAdmin);
    }

    /* Port-Forwardings laden */
    private void loadPortForwardings() {
        tbMdlPortForwardings.removeAll();
        tbMdlPortForwardings.setData(egConfig.getPortForwardings());
    }

    private void loadEndgeraetIps() {
        tbIPs.removeAll();
        tbModelIPs.setData(eg2Auftrag.getEndgeraetIps());
        tfDHCPv6_PD.setText((DHCPv6_PD != null) ? DHCPv6_PD.getAddress() : null);
    }

    private void loadEndgeraetAcls() {
        tbAcls.removeAll();
        tbModelAcls.setData(egConfig.getEndgeraetAcls());
    }

    private void loadRoutings() {
        tbRoutings.removeAll();
        tbModelRoutings.setData(eg2Auftrag.getRoutings());
    }

    @Override
    public void saveModel() throws AKGUIException {
        try {
            if (egConfigIsNew && StringUtils.isBlank(tfEGPassword.getText())) {
                int opt = MessageHelper.showConfirmDialog(
                        this,
                        "Sie haben das EG-Passwort nicht gesetzt. Soll es zufällig gewählt werden?",
                        "Passwort nicht gesetzt",
                        JOptionPane.YES_NO_OPTION);

                if (opt == JOptionPane.YES_OPTION) {
                    tfEGPassword.setText(RandomTools.createPassword(RANDOM_PASSWORD_LENGTH));
                }
            }
            Long sessionId = HurricanSystemRegistry.instance().getSessionId();
            if (egConfig != null && hasModelChanged() && egConfig.getEgType() == null) {
                MessageHelper.showErrorDialog(this, new HurricanGUIException("Auswahl des Endgerätetyps (Hersteller und Modell)"
                        + " nicht zulässig!", null));
                return;
            }
            saveEg2Auftrag(sessionId);
        }
        catch (ValidationException e) {
            throw new AKGUIException(e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void saveEg2Auftrag(Long sessionId) throws StoreException, ValidationException, UpdateException, FindException, ServiceNotFoundException {
        if (eg2Auftrag != null && hasChanged(WATCH_EG_2_AUFTRAG, eg2Auftrag)) {
            CCAuftragService ccAuftragService = getCCService(CCAuftragService.class);
            SIPDomainService sipDomainService = getCCService(SIPDomainService.class);

            Either<CalculatedSwitch4VoipAuftrag, Boolean> switchInitialState =
                    ccAuftragService.calculateSwitch4VoipAuftrag(eg2Auftrag.getAuftragId());
            CalculatedSipDomain4VoipAuftrag sipDomainInitialState =
                    sipDomainService.calculateSipDomain4VoipAuftrag(eg2Auftrag.getAuftragId());

            endgeraeteService.saveEG2Auftrag(eg2Auftrag, sessionId);

            if (switchInitialState.isLeft()) {
                updateSwitchAndSipDomains(eg2Auftrag.getAuftragId(), switchInitialState,
                        sipDomainInitialState);
            }
        }
    }

    private void updateSwitchAndSipDomains(Long auftragId, Either<CalculatedSwitch4VoipAuftrag, Boolean> switchInitialState,
            CalculatedSipDomain4VoipAuftrag sipDomainInitialState) throws ServiceNotFoundException, FindException {
        AKWarnings messages = new AKWarnings();

        // Switch
        HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
        Either<String, String> updateSwitch = hwSwitchService.updateHwSwitchBasedComponents(auftragId,
                switchInitialState.getLeft());

        if (updateSwitch.isRight()) {
            if (updateSwitch.getRight() != null) {
                MessageHelper.showInfoDialog(this, updateSwitch.getRight());
            }
            return;
        }
        if (updateSwitch.getLeft() != null) {
            messages.addAKWarning(null, updateSwitch.getLeft());
        }

        // SIP-Domaene
        VoIPService voIPService = getCCService(VoIPService.class);
        Either<String, String> updateSipDomain = voIPService.migrateSipDomainOfVoipDNs(auftragId,
                (updateSwitch.getLeft() != null), sipDomainInitialState);

        if (updateSipDomain.isRight() && updateSipDomain.getRight() != null) {
            MessageHelper.showInfoDialog(this, updateSwitch.getRight());
        }
        if (updateSipDomain.isLeft() && updateSipDomain.getLeft() != null) {
            messages.addAKWarning(null, updateSipDomain.getLeft());
        }

        // User benachrichtigen
        if (messages.getAKMessages().size() > 0) {
            StringBuilder message = new StringBuilder();
            message.append(messages.getWarningsAsText());
            message.append(SystemUtils.LINE_SEPARATOR);
            message.append("Der Auftrag muss neu provisioniert werden!");
            MessageHelper.showInfoDialog(this, message.toString());
        }
    }

    @Override
    public Object getModel() {
        return egConfig;
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        // not needed for this panel
    }

    /**
     * Uebergibt die eingetragenen Werte dem Modell.
     */
    private void setValues() {
        if (egConfig != null) {
            egConfig.setSerialNumber(tfSerialNo.getText(null));
            egConfig.setNatActive(chbNAT.isSelectedBoolean());
            egConfig.setWanIpFest(chbWanIpFest.isSelectedBoolean());
            egConfig.setDhcpActive(chbDHCP.isSelectedBoolean());
            egConfig.setDhcpPoolFromRef(ipcDhcpPoolFrom.getIPAddress());
            if (egConfig.getDhcpPoolFromRef() != null) {
                egConfig.getDhcpPoolFromRef().setBillingOrderNo(billingOrderNo);
            }
            egConfig.setDhcpPoolToRef(ipcDhcpPoolTo.getIPAddress());
            if (egConfig.getDhcpPoolToRef() != null) {
                egConfig.getDhcpPoolToRef().setBillingOrderNo(billingOrderNo);
            }
            egConfig.setDnsServerActive(chbDns.isSelectedBoolean());
            egConfig.setSnmpMNet(chbSnmpMNet.isSelectedBoolean());
            egConfig.setSnmpCustomer(chbSnmpCustomer.isSelectedBoolean());
            egConfig.setQosActive(chbQoSActive.isSelectedBoolean());
            egConfig.setIpCount(tfIPCount.getValueAsShort(null));
            egConfig.setWanVc(tfWanVc.getValueAsInt(null));
            egConfig.setWanVp(tfWanVp.getValueAsInt(null));
            egConfig.setBemerkung(taBemerkung.getText(null));
            egConfig.setBemerkungKunde(taBemerkungKunde.getText(null));
            egConfig.setEgUser(tfEGUser.getText(null));
            egConfig.setEgPassword(tfEGPassword.getText(null));
            egConfig.setTriggerpunktRef(ipcTriggerpunkt.getIPAddress());
            if (egConfig.getTriggerpunktRef() != null) {
                egConfig.getTriggerpunktRef().setBillingOrderNo(billingOrderNo);
            }
            egConfig.setCalledStationId(tfCalledStationId.getText(null));
            egConfig.setCallingStationId(tfCallingStationId.getText(null));
            egConfig.setIntervall(tfIntervall.getValueAsInt(null));
            egConfig.setReEnable(tfReEnable.getValueAsInt(null));
            egConfig.setIdleTimer(tfIdleTimer.getValueAsInt(null));
            egConfig.setAttemps(tfAttempts.getValueAsInt(null));
            egConfig.setFrequency(tfFrequency.getValueAsInt(null));
            egConfig.setKanalbuendelung(chbBuendelung.isSelectedBoolean());
            egConfig.setMtu(tfMTU.getValueAsInt(null));
            egConfig.setDnsServerIP(tfDNSServerIP.getText(null));
            egConfig.setModellZusatz(tfModellZusatz.getText(null));
            egConfig.setSoftwarestand(tfSoftwareStand.getText(null));

            Schicht2Protokoll layer2Prot = (Schicht2Protokoll) cbLayer2Prot.getSelectedItem();
            egConfig.setSchicht2Protokoll(layer2Prot);
            ServiceVertrag serviceVertrag = (ServiceVertrag) cbServiceVertrag.getSelectedItem();
            egConfig.setServiceVertrag(serviceVertrag);
            VrrpPriority vrrpPriority = (VrrpPriority) cbVrrpPriority.getSelectedItem();
            egConfig.setVrrpPriority(vrrpPriority);

            String selectedHersteller = (String) cbHersteller.getSelectedItem();
            String selectedModell = (String) cbModell.getSelectedItem();
            try {
                EGType egType = endgeraeteService.findEGTypeByHerstellerAndModell(selectedHersteller, selectedModell);
                egConfig.setEgType(egType);
            }
            catch (FindException e) {
                LOGGER.info(e.getMessage());
                // Nothing to do
            }
        }
        if (eg2Auftrag != null) {
            Endstelle selectedEndstelle = (Endstelle) cbEndstelle.getSelectedItem();
            eg2Auftrag.setEndstelleId(selectedEndstelle.getId());
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        setValues();
        return hasChanged(WATCH_EG_CONFIG, egConfig) || hasChanged(WATCH_EG_2_AUFTRAG, eg2Auftrag);
    }

    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof PortForwarding) {
            editPortForwarding(tbPortForwards.getSelectedRow(), (PortForwarding) selection);
        }
        if (selection instanceof EndgeraetIp) {
            EndgeraetIp selectedIp = (EndgeraetIp) selection;
            if (selectedIp.isLanIp() || selectedIp.isLanVRRPIp()) {
                editEndgeraetIp(tbIPs.getSelectedRow(), selectedIp);
                tbIPs.revalidate();
            }
            else {
                editWanIp(tbIPs.getSelectedRow(), selectedIp);
                tbIPs.revalidate();
            }
        }
        if (selection instanceof Routing) {
            editRouting(tbRoutings.getSelectedRow(), (Routing) selection);
            tbRoutings.revalidate();
        }

    }

    /* Erzeugt ein zufälliges EG-Passwort und schreibt es in das zugehörige Textfeld */
    private void createPassword() {
        if (StringUtils.isNotBlank(tfEGPassword.getText())) {
            int opt = MessageHelper.showYesNoQuestion(
                    this,
                    "Soll das Endgerät-Passwort wirklich durch ein zufälliges ersetzt werden?",
                    "Ersetzen?");

            if (opt != JOptionPane.YES_OPTION) {
                return;
            }
        }
        String passWd = RandomTools.createPassword(RANDOM_PASSWORD_LENGTH);
        tfEGPassword.setText(passWd);
    }

    /* Erzeugt ein neues PortForwarding-Objekt (Bearbeitung ueber einen Dialog) */
    private void createPortForwarding() {
        if (egConfig != null) {
            try {
                PortForwarding newPortForwarding = new PortForwarding();
                newPortForwarding.setActive(Boolean.TRUE);

                PortForwardingDialog dlg = new PortForwardingDialog(newPortForwarding, billingOrderNo);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof PortForwarding) {
                    PortForwarding portForwarding = (PortForwarding) result;
                    egConfig.addPortForwarding(portForwarding);
                    tbMdlPortForwardings.addObject(portForwarding);
                    tbPortForwards.updateUI();
                }
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void deletePortForwarding() {
        if (egConfig != null) {
            try {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<PortForwarding> tblModelPortForwarding = (AKMutableTableModel<PortForwarding>) tbPortForwards.getModel();
                PortForwarding selected = tblModelPortForwarding.getDataAtRow(tbPortForwards.getSelectedRow());

                if (selected == null) {
                    throw new HurricanGUIException("Bitte wählen Sie zuerst einen Datensatz aus.");
                }
                int opt = MessageHelper.showYesNoQuestion(this,
                        "Soll das Port-Forwarding wirklich entfernt werden?", "Löschen?");

                if (opt == JOptionPane.YES_OPTION) {
                    egConfig.removePortForwarding(selected);
                    tblModelPortForwarding.removeObject(selected);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /* Erzeugt ein neues EndgeraetIp-Objekt (Bearbeitung ueber einen Dialog) */
    private void createIp4AddressType(EndgeraetIp.AddressType addressType) {
        if (eg2Auftrag != null) {
            try {
                EndgeraetIp endgeraetIp = new EndgeraetIp();
                endgeraetIp.setAddressType(addressType);
                IPAddress ipAddress = new IPAddress();
                ipAddress.setIpType(AddressTypeEnum.IPV4);
                ipAddress.setAddress(System.getProperty("egconfig.defaultIP"));
                endgeraetIp.setIpAddressRef(ipAddress);

                LanIpDialog dlg = new LanIpDialog(endgeraetIp, billingOrderNo, addressType);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof EndgeraetIp) {
                    eg2Auftrag.addEndgeraetIp(endgeraetIp);
                    tbModelIPs.setData(eg2Auftrag.getEndgeraetIps());
                }
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void selectWanIp() {
        if (eg2Auftrag != null) {
            try {
                boolean hasV4EndgeraetIp = false, hasV6EndgeraetIp = false;
                for (EndgeraetIp egIp : eg2Auftrag.getEndgeraetIps()) {
                    if (egIp.isWanIp()) {
                        hasV4EndgeraetIp |= egIp.getIpAddressRef().isIPV4();
                        hasV6EndgeraetIp |= egIp.getIpAddressRef().isIPV6();
                    }
                }
                EndgeraetIp wanIp = new EndgeraetIp();
                wanIp.setAddressType(EndgeraetIp.AddressType.WAN);

                WanIpDialog dlg = new WanIpDialog(wanIp, billingOrderNo, hasV4EndgeraetIp, hasV6EndgeraetIp);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof EndgeraetIp) {
                    eg2Auftrag.addEndgeraetIp((EndgeraetIp) result);
                    tbModelIPs.setData(eg2Auftrag.getEndgeraetIps());
                }
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void editWanIp(int position, EndgeraetIp selectedIp) {
        if (eg2Auftrag != null) {
            try {
                WanIpDialog dlg = new WanIpDialog(selectedIp, billingOrderNo,
                        !selectedIp.getIpAddressRef().isIPV4(),
                        !selectedIp.getIpAddressRef().isIPV6());
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof EndgeraetIp) {
                    tbModelIPs.replaceObject(position, selectedIp);
                    tbIPs.updateUI();
                }
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void deleteEndgeraetIp() {
        if (eg2Auftrag != null) {
            try {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<EndgeraetIp> tblModelIPs = (AKMutableTableModel<EndgeraetIp>) tbIPs.getModel();
                EndgeraetIp selected = tblModelIPs.getDataAtRow(tbIPs.getSelectedRow());

                if (selected == null) {
                    throw new HurricanGUIException("Zum Löschen IP-Adresse auswählen!");
                }
                int opt = MessageHelper.showYesNoQuestion(this,
                        "Soll die IP-Adresse wirklich vom Endgerät entfernt werden?", "Löschen?");

                if (opt == JOptionPane.YES_OPTION) {
                    eg2Auftrag.removeEndgeraetIp(selected);
                    tbModelIPs.removeObject(selected);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void createRouting() {
        if (eg2Auftrag != null) {
            try {
                RoutingDialog dlg = new RoutingDialog(new Routing(), billingOrderNo);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof Routing) {
                    Routing routing = (Routing) result;
                    eg2Auftrag.addRouting(routing);
                    tbModelRoutings.addObject(routing);
                }
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void deleteRouting() {
        if (eg2Auftrag != null) {
            try {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<Routing> tblModelRoutings = (AKMutableTableModel<Routing>) tbRoutings.getModel();
                Routing selected = tblModelRoutings.getDataAtRow(tbRoutings.getSelectedRow());

                if (selected == null) {
                    throw new HurricanGUIException("Bitte wählen Sie zuerst einen Datensatz aus.");
                }
                int opt = MessageHelper.showYesNoQuestion(this,
                        "Soll die statische Route wirklich entfernt werden?", "Löschen?");

                if (opt == JOptionPane.YES_OPTION) {
                    eg2Auftrag.removeRouting(selected);
                    tblModelRoutings.removeObject(selected);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void editRouting(int position, Routing selection) {
        RoutingDialog dlg = new RoutingDialog(selection, billingOrderNo);
        Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        if (result instanceof Routing) {
            Routing routing = (Routing) result;
            tbModelRoutings.replaceObject(position, routing);
            tbRoutings.updateUI();
        }
        else {
            readModel();
        }
    }


    /* Editiert das angegebene PortForwarding-Objekt (ueber einen Dialog). */
    private void editPortForwarding(int position, PortForwarding pf) {
        PortForwardingDialog dlg = new PortForwardingDialog(pf, billingOrderNo);
        Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        if (result instanceof PortForwarding) {
            PortForwarding portForwarding = (PortForwarding) result;
            tbMdlPortForwardings.replaceObject(position, portForwarding);
            tbPortForwards.updateUI();
        }
        else {
            readModel();
        }
    }


    /* Editiert das angegebene EndgeraetIp-Objekt (ueber einen Dialog). */
    private void editEndgeraetIp(int position, EndgeraetIp endgerateIp) {
        LanIpDialog dlg = new LanIpDialog(endgerateIp, billingOrderNo,
                EndgeraetIp.AddressType.valueOf(endgerateIp.getAddressType()));
        Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        if (result instanceof EndgeraetIp) {
            EndgeraetIp endgeraetIp = (EndgeraetIp) result;
            tbModelIPs.replaceObject(position, endgeraetIp);
            tbIPs.updateUI();
        }
        else {
            readModel();
        }
    }

    private void addAcl() {
        if (egConfig != null) {
            try {
                EndgeraetAclDialog dlg = new EndgeraetAclDialog(egConfig);
                Object result = DialogHelper.showDialog(getMainFrame(), dlg, true, true);
                if (result instanceof Set<?>) {
                    @SuppressWarnings("unchecked")
                    Set<EndgeraetAcl> endgeraetAcl = (Set<EndgeraetAcl>) result;
                    tbModelAcls.setData(endgeraetAcl);
                    tbAcls.updateUI();
                }
            }
            catch (Exception e) {
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    private void delAcl() {
        if (egConfig != null) {
            try {
                @SuppressWarnings("unchecked")
                AKMutableTableModel<EndgeraetAcl> tblModelAcls = (AKMutableTableModel<EndgeraetAcl>) tbAcls.getModel();
                EndgeraetAcl selected = tblModelAcls.getDataAtRow(tbAcls.getSelectedRow());

                if (selected == null) {
                    throw new HurricanGUIException("Bitte wählen Sie zuerst einen Datensatz aus.");
                }
                egConfig.removeEndgeraetAcl(selected);
                tblModelAcls.removeObject(selected);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed for this panel
    }

    /* Setzt alle Felder auf enabled/disabled */
    private void enableFields(boolean enabled) {
        cbHersteller.setEnabled(enabled);
        cbModell.setEnabled(enabled);
        tfSerialNo.setEnabled(enabled);
        chbNAT.setEnabled(enabled);
        chbDHCP.setEnabled(enabled);
        chbWanIpFest.setEnabled(enabled);
        ipcDhcpPoolFrom.setEnabled(enabled);
        ipcDhcpPoolTo.setEnabled(enabled);
        chbDns.setEnabled(enabled);
        chbSnmpMNet.setEnabled(enabled);
        chbSnmpCustomer.setEnabled(enabled);
        chbQoSActive.setEnabled(enabled);
        tfIPCount.setEnabled(enabled);
        tfWanVp.setEnabled(enabled);
        tfWanVc.setEnabled(enabled);
        taBemerkung.setEnabled(enabled);
        taBemerkungKunde.setEnabled(enabled);
        tfEGUser.setEnabled(enabled);
        tfEGPassword.setEnabled(enabled);
        btnCreatePwd.setEnabled(enabled);
        cbEndstelle.setEnabled(enabled);
        tfMTU.setEnabled(enabled);
        tfDNSServerIP.setEnabled(enabled);
    }

    /* Setzt die PortForwardings enabled/disabled */
    private void enablePortForwardings(boolean enable) {
        confPane.setEnabledAt(TAB_INDEX_PORTFORWARDINGS, enable);
        btnNewPF.setEnabled(enable);
        btnDelPF.setEnabled(enable);
    }

    /* Setzt die PortForwardings enabled/disabled */
    private void enableEndgeraetIPs(@SuppressWarnings("SameParameterValue") boolean enable) {
        confPane.setEnabledAt(TAB_INDEX_EG_IPS, enable);
        btnNewLanIp.setEnabled(enable);
        btnNewLanVrrpIp.setEnabled(enable);
        btnSelectWanIp.setEnabled(enable);
        btnDelEGIp.setEnabled(enable);
    }

    /* Setzt die DhcpFrom/To enabled/disabled */
    private void enableDhcpRange(boolean enable) {
        ipcDhcpPoolFrom.setEnabled(enable);
        ipcDhcpPoolTo.setEnabled(enable);
    }

    /* Setzt die DNS Server Einstellungen */
    private void enableDNSSettings(boolean enable) {
        tfDNSServerIP.setEditable(enable);
        tfDNSServerIP.setEnabled(enable);
    }

    /* Setzt die Routings enabled/disabled */
    private void enableRoutings(@SuppressWarnings("SameParameterValue") boolean enable) {
        confPane.setEnabledAt(TAB_INDEX_ROUTINGS, enable);
        btnNewRouting.setEnabled(enable);
        btnDelRouting.setEnabled(enable);
    }

    /* Setzt die S0-Backup Konfiguration enabled/disabled */
    private void enableS0Backup(boolean enable) {
        confPane.setEnabledAt(TAB_INDEX_S0BACKUP, enable);
        GuiTools.enableComponents(new Component[] { ipcTriggerpunkt, tfCalledStationId,
                tfCallingStationId, tfIntervall, tfReEnable, tfIdleTimer,
                tfAttempts, tfFrequency, chbBuendelung }, enable, true);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == chbNAT) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                chbDHCP.setSelected(false);
            }
            else if (e.getStateChange() == ItemEvent.SELECTED) {
                chbDHCP.setSelected(true);
            }
        }
        if (e.getSource() == chbDHCP) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                enableDhcpRange(false);
            }
            else if (e.getStateChange() == ItemEvent.SELECTED) {
                enableDhcpRange(true);
            }
        }
        if (e.getSource() == chbDns) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                enableDNSSettings(false);
            }
            else if (e.getStateChange() == ItemEvent.SELECTED) {
                enableDNSSettings(true);
            }
        }
        if (e.getSource() == cbHersteller
                && (e.getStateChange() == ItemEvent.SELECTED)
                && (!herstellerChangeActive)) {
            herstellerChangeActive = true;
            removeNotConfiguredManufacturer((String) e.getItem());
            setModellAuswahlfuerHersteller((String) e.getItem());
            herstellerChangeActive = false;
        }
        if (e.getSource() == cbEndstelle
                && e.getStateChange() == ItemEvent.SELECTED) {
            Endstelle selectedEndstelle = (Endstelle) cbEndstelle.getSelectedItem();
            try {
                Schicht2Protokoll schicht2Protokoll = endgeraeteService
                        .getSchicht2Protokoll4Auftrag(eg2Auftrag.getAuftragId(), selectedEndstelle.getId());
                cbLayer2Prot.setSelectedItem(schicht2Protokoll);
            }
            catch (FindException ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    private void setModellAuswahlfuerHersteller(String hersteller) {
        try {
            if (StringUtils.isNotBlank(hersteller)) {
                List<String> modelle = new ArrayList<>(0);
                if (CollectionTools.isNotEmpty(eg.getEgTypes())) {
                    for (EGType egType : eg.getEgTypes()) {
                        if ((egType.getHersteller() != null)
                                && StringUtils.equals(egType.getHersteller(), hersteller)
                                && (modelle.indexOf(egType.getModell()) == -1)) {
                            modelle.add(egType.getModell());
                        }
                    }
                }
                else {
                    modelle = endgeraeteService.getDistinctListOfModelsByManufacturer(hersteller);
                }
                String aktModell = (String) cbModell.getSelectedItem();
                if (CollectionTools.isNotEmpty(modelle)) {
                    cbModell.enableInputMethods(true);
                    cbModell.removeAllItems();
                    cbModell.addItems(modelle);
                    if (modelle.contains(aktModell)) {
                        cbModell.setSelectedItem(aktModell);
                    }
                    else {
                        cbModell.setSelectedItem(modelle.get(0));
                    }
                }
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private synchronized void removeNotConfiguredManufacturer(String hersteller) {
        if ((egConfig.getEgType() == null || StringUtils.difference(egConfig.getEgType().getHersteller(), hersteller) != null)
                && CollectionTools.isNotEmpty(eg.getEgTypes())) {
            List<String> configuredHersteller = new ArrayList<>(0);
            Boolean setConfiguredHersteller = false;
            for (EGType egType : eg.getEgTypes()) {
                if (configuredHersteller.indexOf(egType.getHersteller()) == -1) {
                    configuredHersteller.add(egType.getHersteller());
                    if ((egType.getHersteller() != null) && StringUtils.equals(egType.getHersteller(), hersteller)) {
                        setConfiguredHersteller = true;
                    }
                }
            }
            if (setConfiguredHersteller) {
                cbHersteller.removeAllItems();
                cbHersteller.addItems(configuredHersteller);
                cbHersteller.setSelectedItem(hersteller);
            }
        }
    }

    static protected class Layer2ProtComboBoxRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = -2183062191286347689L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (comp instanceof JLabel) {
                if (value instanceof Schicht2Protokoll) {
                    Schicht2Protokoll layer2Prot = (Schicht2Protokoll) value;
                    if (layer2Prot.name() != null) {
                        ((JLabel) comp).setText(layer2Prot.name());
                    }
                }
                else if (value == null) {
                    ((JLabel) comp).setText(" ");
                }
            }
            return comp;
        }
    }

    static protected class ServiceVertragComboBoxRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 7236793507993803291L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (comp instanceof JLabel) {
                if (value instanceof ServiceVertrag) {
                    ServiceVertrag serviceVertrag = (ServiceVertrag) value;
                    if (serviceVertrag.name() != null) {
                        ((JLabel) comp).setText(serviceVertrag.name());
                    }
                }
                else if (value == null) {
                    ((JLabel) comp).setText(" ");
                }
            }
            return comp;
        }
    }

    private static class VrrpPriorityComboBoxRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -3951308072495235241L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (comp instanceof JLabel) {
                if (value instanceof VrrpPriority) {
                    VrrpPriority vrrpPriority = (VrrpPriority) value;
                    ((JLabel) comp).setText(vrrpPriority.getDisplayText());
                }
                else if (value == null) {
                    ((JLabel) comp).setText(" ");
                }
            }
            return comp;
        }
    }

    static protected class StringComparator implements Comparator<String>, Serializable {
        private static final long serialVersionUID = 1642071549199584284L;

        @SuppressWarnings("null")
        @Override
        public int compare(String o1, String o2) {
            return StringTools.compare(o1, o2, false);
        }
    }

    static protected class GenIpAddressTableModel<T> extends AKTableModel<T> {
        static final int COL_IP = 0;
        static final int COL_TYPE = 1;
        static final int COL_COUNT = 2;
        private static final long serialVersionUID = 8485941424551886849L;

        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_IP:
                    return "IP";
                case COL_TYPE:
                    return "Typ";
                default:
                    return " ";
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    }

    /**
     * TableModel fuer die LAN und WAN IP Adressen.
     */
    static protected class EndgeraetIpTableModel extends GenIpAddressTableModel<EndgeraetIp> {
        private static final long serialVersionUID = 8069328183425504936L;

        @Override
        public Object getValueAt(int row, int column) {
            EndgeraetIp endgeraetIp = getDataAtRow(row);
            if (endgeraetIp != null) {
                switch (column) {
                    case COL_IP:
                        if (endgeraetIp.getIpAddressRef() != null) {
                            return endgeraetIp.getIpAddressRef().getEgDisplayAddress();
                        }
                        break;
                    case COL_TYPE:
                        return endgeraetIp.getAddressTypeFormated();
                    default:
                        return null;
                }
            }
            return null;
        }
    }

}
