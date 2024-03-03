/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2009 08:30:43
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJRadioButton;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.IPSecSite2Site;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.IPSecService;


/**
 * Panel fuer die Darstellung / Verwaltung von IPSec Site-to-Site Daten.
 *
 *
 */
public class AuftragIPSecSiteToSitePanel extends AbstractAuftragPanel implements AKModelOwner {

    private static final String RESOURCE_FILE = "de/augustakom/hurrican/gui/auftrag/resources/AuftragIPSecSiteToSitePanel.xml";

    private static final String IPSEC_DETAILS = "ipsec.details";
    private static final String ACCESS = "access";
    private static final String CERTIFICATE = "certificate";
    private static final String PRESHARED_KEY = "preshared.key";
    private static final String ACCESS_ORDER = "access.order";
    private static final String ACCESS_TYPE = "access.type";
    private static final String ACCESS_BANDWIDTH = "access.bandwidth";
    private static final String ACCESS_CARRIER = "access.carrier";
    private static final String DESCRIPTION = "description";
    private static final String SPLIT_TUNNEL = "split.tunnel";
    private static final String DIALIN_NO = "dialin.no";
    private static final String HOSTNAME = "hostname";
    private static final String HOSTNAME_PASSIVE = "hostname.passive";
    private static final String WAN_IP = "wan.ip";
    private static final String WAN_SUBMASK = "wan.submask";
    private static final String VIRTUAL_LAN_SUBMASK = "virtual.lan.submask";
    private static final String VIRTUAL_LAN_TO_SCRAMBLE = "virtual.lan.to.scramble";
    private static final String VIRTUAL_LAN_IP = "virtual.lan.ip";
    private static final String LOOPBACK_IP = "loopback.ip";
    private static final String LOOPBACK_IP_PASSIVE = "loopback.ip.passive";
    private static final String WAN_GATEWAY = "wan.gateway";

    private static final Logger LOGGER = Logger.getLogger(AuftragIPSecSiteToSitePanel.class);

    /* Watch-Konstante fuer die Site-to-Site Daten. */
    private static final String WATCH_SITE_TO_SITE = "auftrag.ipsec.site.to.site";

    private boolean guiCreated = false;

    // GUI-Elemete
    private AKJTextField tfWanGateway;
    private AKJTextField tfLoopbackIp;
    private AKJTextField tfLoopbackIpPassive;
    private AKJTextField tfVirtualLanIp;
    private AKJTextField tfVirtualLan2Scramble;
    private AKJTextField tfVirtualLanSub;
    private AKJTextField tfWanIp;
    private AKJTextField tfWanSub;
    private AKJTextField tfHostname;
    private AKJTextField tfHostnamePassive;
    private AKJTextField tfDialinNo;
    private AKJCheckBox chbSplitTunnel;
    private AKJTextArea taDescription;
    private AKJTextField tfAccessCarrier;
    private AKJTextField tfAccessBandwidth;
    private AKJTextField tfAccessType;
    private AKJTextField tfAccessOrder;
    private AKJRadioButton rbPresharedKey;
    private AKJRadioButton rbCertificate;

    // Modelle
    private CCAuftragModel auftragModel;
    private IPSecSite2Site ipSecSiteToSite;

    // Services
    private IPSecService ipSecService;

    /**
     * Default-Const.
     */
    public AuftragIPSecSiteToSitePanel() {
        super(RESOURCE_FILE);
        init();
        createGUI();
    }

    private void init() {
        try {
            ipSecService = getCCService(IPSecService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblWanGateway = getSwingFactory().createLabel(WAN_GATEWAY);
        AKJLabel lblLoopbackIp = getSwingFactory().createLabel(LOOPBACK_IP);
        AKJLabel lblLoopbackIpPassive = getSwingFactory().createLabel(LOOPBACK_IP_PASSIVE);
        AKJLabel lblVirtualLanIp = getSwingFactory().createLabel(VIRTUAL_LAN_IP);
        AKJLabel lblVirtualLan2Scramble = getSwingFactory().createLabel(VIRTUAL_LAN_TO_SCRAMBLE);
        AKJLabel lblVirtualLanSub = getSwingFactory().createLabel(VIRTUAL_LAN_SUBMASK);
        AKJLabel lblWanIp = getSwingFactory().createLabel(WAN_IP);
        AKJLabel lblWanSub = getSwingFactory().createLabel(WAN_SUBMASK);
        AKJLabel lblHostname = getSwingFactory().createLabel(HOSTNAME);
        AKJLabel lblHostnamePassive = getSwingFactory().createLabel(HOSTNAME_PASSIVE);
        AKJLabel lblDialinNo = getSwingFactory().createLabel(DIALIN_NO);
        AKJLabel lblSplitTunnel = getSwingFactory().createLabel(SPLIT_TUNNEL);
        AKJLabel lblDescription = getSwingFactory().createLabel(DESCRIPTION);
        AKJLabel lblAccess = getSwingFactory().createLabel(ACCESS, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblAccessCarrier = getSwingFactory().createLabel(ACCESS_CARRIER);
        AKJLabel lblAccessBandwidth = getSwingFactory().createLabel(ACCESS_BANDWIDTH);
        AKJLabel lblAccessType = getSwingFactory().createLabel(ACCESS_TYPE);
        AKJLabel lblAccessOrder = getSwingFactory().createLabel(ACCESS_ORDER);
        AKJLabel lblPresharedKey = getSwingFactory().createLabel(PRESHARED_KEY);
        AKJLabel lblCertificate = getSwingFactory().createLabel(CERTIFICATE);

        tfHostname = getSwingFactory().createTextField(HOSTNAME);
        tfHostnamePassive = getSwingFactory().createTextField(HOSTNAME_PASSIVE);
        tfWanIp = getSwingFactory().createTextField(WAN_IP);
        tfWanGateway = getSwingFactory().createTextField(WAN_GATEWAY);
        tfLoopbackIp = getSwingFactory().createTextField(LOOPBACK_IP);
        tfLoopbackIpPassive = getSwingFactory().createTextField(LOOPBACK_IP_PASSIVE);
        tfVirtualLanIp = getSwingFactory().createTextField(VIRTUAL_LAN_IP);
        tfVirtualLan2Scramble = getSwingFactory().createTextField(VIRTUAL_LAN_TO_SCRAMBLE);
        tfVirtualLanSub = getSwingFactory().createTextField(VIRTUAL_LAN_SUBMASK);
        tfDialinNo = getSwingFactory().createTextField(DIALIN_NO);
        tfWanSub = getSwingFactory().createTextField(WAN_SUBMASK);
        chbSplitTunnel = getSwingFactory().createCheckBox(SPLIT_TUNNEL);
        taDescription = getSwingFactory().createTextArea(DESCRIPTION);
        AKJScrollPane spDescription = new AKJScrollPane(taDescription, new Dimension(250, 100));
        tfAccessCarrier = getSwingFactory().createTextField(ACCESS_CARRIER);
        tfAccessBandwidth = getSwingFactory().createTextField(ACCESS_BANDWIDTH);
        tfAccessType = getSwingFactory().createTextField(ACCESS_TYPE);
        tfAccessOrder = getSwingFactory().createTextField(ACCESS_ORDER);
        ButtonGroup buttonGroup = new ButtonGroup();
        rbPresharedKey = getSwingFactory().createRadioButton(PRESHARED_KEY, buttonGroup);
        rbCertificate = getSwingFactory().createRadioButton(CERTIFICATE, buttonGroup);

        AKJPanel leftPnl = new AKJPanel(new GridBagLayout());
        leftPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        leftPnl.add(lblHostname, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        leftPnl.add(tfHostname, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblHostnamePassive, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfHostnamePassive, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblWanIp, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfWanIp, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblWanSub, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfWanSub, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblWanGateway, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfWanGateway, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblLoopbackIp, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfLoopbackIp, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblLoopbackIpPassive, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfLoopbackIpPassive, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblVirtualLanIp, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfVirtualLanIp, GBCFactory.createGBC(100, 0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblVirtualLan2Scramble, GBCFactory.createGBC(0, 0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfVirtualLan2Scramble, GBCFactory.createGBC(100, 0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblVirtualLanSub, GBCFactory.createGBC(0, 0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfVirtualLanSub, GBCFactory.createGBC(100, 0, 3, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblDialinNo, GBCFactory.createGBC(0, 0, 1, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(tfDialinNo, GBCFactory.createGBC(100, 0, 3, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblSplitTunnel, GBCFactory.createGBC(0, 0, 1, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(chbSplitTunnel, GBCFactory.createGBC(100, 0, 3, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblPresharedKey, GBCFactory.createGBC(0, 0, 1, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(rbPresharedKey, GBCFactory.createGBC(100, 0, 3, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(lblCertificate, GBCFactory.createGBC(0, 0, 1, 13, 1, 1, GridBagConstraints.HORIZONTAL));
        leftPnl.add(rbCertificate, GBCFactory.createGBC(100, 0, 3, 13, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel rightPnl = new AKJPanel(new GridBagLayout());
        rightPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        rightPnl.add(lblDescription, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        rightPnl.add(spDescription, GBCFactory.createGBC(100, 0, 3, 0, 1, 2, GridBagConstraints.HORIZONTAL));
        rightPnl.add(lblAccess, GBCFactory.createGBC(0, 0, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(lblAccessCarrier, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(tfAccessCarrier, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(lblAccessBandwidth, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(tfAccessBandwidth, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(lblAccessType, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(tfAccessType, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(lblAccessOrder, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(tfAccessOrder, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        rightPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 7, 1, 1, GridBagConstraints.NONE));

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText(IPSEC_DETAILS)));
        this.add(leftPnl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(rightPnl, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 0, 1, 3, 1, GridBagConstraints.BOTH));

        guiCreated = true;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    @Override
    public void setModel(Observable model) throws AKGUIException {
        this.auftragModel = null;
        if (model instanceof CCAuftragModel) {
            this.auftragModel = (CCAuftragModel) model;
        }
        readModel();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    @Override
    public void readModel() throws AKGUIException {
        if (auftragModel != null) {
            try {
                setWaitCursor();

                ipSecSiteToSite = ipSecService.findIPSecSiteToSite(auftragModel.getAuftragId());
                addObjectToWatch(WATCH_SITE_TO_SITE, ipSecSiteToSite);
                showValues();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
    }

    /* Zeigt die IPSec Daten an */
    private void showValues() {
        GuiTools.cleanFields(this);
        if (ipSecSiteToSite != null) {
            tfWanGateway.setText(ipSecSiteToSite.getWanGateway());
            tfLoopbackIp.setText(ipSecSiteToSite.getLoopbackIp());
            tfLoopbackIpPassive.setText(ipSecSiteToSite.getLoopbackIpPassive());
            tfVirtualLanIp.setText(ipSecSiteToSite.getVirtualLanIp());
            tfVirtualLan2Scramble.setText(ipSecSiteToSite.getVirtualLan2Scramble());
            tfVirtualLanSub.setText(ipSecSiteToSite.getVirtualLanSubmask());
            tfWanIp.setText(ipSecSiteToSite.getVirtualWanIp());
            tfWanSub.setText(ipSecSiteToSite.getVirtualWanSubmask());
            tfHostname.setText(ipSecSiteToSite.getHostname());
            tfHostnamePassive.setText(ipSecSiteToSite.getHostnamePassive());
            tfDialinNo.setText(ipSecSiteToSite.getIsdnDialInNumber());
            chbSplitTunnel.setSelected(ipSecSiteToSite.getSplitTunnel());
            taDescription.setText(ipSecSiteToSite.getDescription());
            tfAccessCarrier.setText(ipSecSiteToSite.getAccessCarrier());
            tfAccessBandwidth.setText(ipSecSiteToSite.getAccessBandwidth());
            tfAccessType.setText(ipSecSiteToSite.getAccessType());
            tfAccessOrder.setText(ipSecSiteToSite.getAccessAuftragNr());
            rbPresharedKey.setSelected(BooleanTools.nullToFalse(ipSecSiteToSite.getHasPresharedKey()));
            rbCertificate.setSelected(BooleanTools.nullToFalse(ipSecSiteToSite.getHasCertificate()));
        }
    }

    /* Uebertraegt die Werte in das Modell. */
    private void setValues() {
        if (auftragModel == null) {
            return;
        }

        if (ipSecSiteToSite == null) {
            ipSecSiteToSite = new IPSecSite2Site();
            ipSecSiteToSite.setAuftragId(auftragModel.getAuftragId());
        }

        ipSecSiteToSite.setWanGateway(tfWanGateway.getText(null));
        ipSecSiteToSite.setLoopbackIp(tfLoopbackIp.getText(null));
        ipSecSiteToSite.setLoopbackIpPassive(tfLoopbackIpPassive.getText(null));
        ipSecSiteToSite.setVirtualLanIp(tfVirtualLanIp.getText(null));
        ipSecSiteToSite.setVirtualLan2Scramble(tfVirtualLan2Scramble.getText(null));
        ipSecSiteToSite.setVirtualLanSubmask(tfVirtualLanSub.getText(null));
        ipSecSiteToSite.setVirtualWanIp(tfWanIp.getText(null));
        ipSecSiteToSite.setVirtualWanSubmask(tfWanSub.getText(null));
        ipSecSiteToSite.setHostname(tfHostname.getText(null));
        ipSecSiteToSite.setHostnamePassive(tfHostnamePassive.getText(null));
        ipSecSiteToSite.setIsdnDialInNumber(tfDialinNo.getText(null));
        ipSecSiteToSite.setSplitTunnel(chbSplitTunnel.isSelectedBoolean());
        String bemerkungNeu = appendUserAndDateIfChanged(ipSecSiteToSite.getDescription(), taDescription.getText(null));
        ipSecSiteToSite.setDescription(bemerkungNeu);
        ipSecSiteToSite.setAccessCarrier(tfAccessCarrier.getText(null));
        ipSecSiteToSite.setAccessBandwidth(tfAccessBandwidth.getText(null));
        ipSecSiteToSite.setAccessType(tfAccessType.getText(null));
        ipSecSiteToSite.setAccessAuftragNr(tfAccessOrder.getText(null));
        ipSecSiteToSite.setHasCertificate(rbCertificate.isSelected());
        ipSecSiteToSite.setHasPresharedKey(rbPresharedKey.isSelected());
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    @Override
    public boolean hasModelChanged() {
        setValues();
        if (guiCreated && (ipSecSiteToSite != null)) {
            return hasChanged(WATCH_SITE_TO_SITE, ipSecSiteToSite);
        }
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    @Override
    public void saveModel() throws AKGUIException {
        try {
            setValues();
            showValues();
            ipSecService.saveIPSecSiteToSite(ipSecSiteToSite);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    @Override
    public Object getModel() {
        return null;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


