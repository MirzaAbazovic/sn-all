/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2004 11:59:49
 */
package de.augustakom.hurrican.gui.auftrag.vpn;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Dialog, um einen VPN-Auftrag zu editieren.
 *
 *
 */
public class VPNEditDialog extends AbstractServiceOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(VPNEditDialog.class);

    private Long vpnId = null;
    private VPN vpn = null;

    private AKJTextField tfVpnName = null;
    private AKJTextField tfRealm = null;
    private AKReferenceField rfVpnType = null;
    private AKReferenceField rfNiederlassung = null;
    private AKJDateComponent dcDatum = null;
    private AKJTextField tfEinwahl = null;
    private AKJTextField tfProjectLead = null;
    private AKJTextField tfSalesRep = null;
    private AKJTextArea taBemerkung = null;
    private AKJCheckBox cbQOS = null;

    /**
     * Konstruktor.
     *
     * @param vpnId ID des zu editierenden VPN-Auftrags.
     */
    public VPNEditDialog(Long vpnId) {
        super("de/augustakom/hurrican/gui/auftrag/vpn/resources/VPNEditDialog.xml");
        this.vpnId = vpnId;
        createGUI();
        load();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        String title = "VPN bearbeiten";
        if (vpnId != null) {
            title += " (VPN-Nr: " + vpnId + ")";
        }
        setTitle(title);
        setIconURL("de/augustakom/hurrican/gui/images/vpn.gif");

        AKJLabel lblVpnName = getSwingFactory().createLabel("vpn.name");
        AKJLabel lblRealm = getSwingFactory().createLabel("realm");
        AKJLabel lblVpnType = getSwingFactory().createLabel("vpn.type");
        AKJLabel lblNiederlassung = getSwingFactory().createLabel("niederlassung");
        AKJLabel lblDatum = getSwingFactory().createLabel("datum");
        AKJLabel lblEinwahl = getSwingFactory().createLabel("einwahl");
        AKJLabel lblProjectLead = getSwingFactory().createLabel("projectlead");
        AKJLabel lblSalesRep = getSwingFactory().createLabel("salesrep");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblQOS = getSwingFactory().createLabel("qos");

        tfVpnName = getSwingFactory().createTextField("vpn.name");
        tfRealm = getSwingFactory().createTextField("realm");
        rfVpnType = getSwingFactory().createReferenceField(
                "vpn.type", Reference.class, "id", "strValue", new Reference(Reference.REF_TYPE_VPNTYPE));
        rfNiederlassung = getSwingFactory().createReferenceField(
                "niederlassung", Niederlassung.class, "id", "name", new Niederlassung());
        dcDatum = getSwingFactory().createDateComponent("datum");
        tfEinwahl = getSwingFactory().createTextField("einwahl");
        tfProjectLead = getSwingFactory().createTextField("projectlead");
        tfSalesRep = getSwingFactory().createTextField("salesrep");
        taBemerkung = getSwingFactory().createTextArea("bemerkung");
        cbQOS = getSwingFactory().createCheckBox("qos");
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        spBemerkung.setPreferredSize(new Dimension(165, 50));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblVpnName, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(tfVpnName, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblRealm, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfRealm, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblVpnType, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(rfVpnType, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblDatum, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(dcDatum, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblEinwahl, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfEinwahl, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblQOS, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(cbQOS, GBCFactory.createGBC(100, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblNiederlassung, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(rfNiederlassung, GBCFactory.createGBC(100, 0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblProjectLead, GBCFactory.createGBC(0, 0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfProjectLead, GBCFactory.createGBC(100, 0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblSalesRep, GBCFactory.createGBC(0, 0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfSalesRep, GBCFactory.createGBC(100, 0, 3, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblBemerkung, GBCFactory.createGBC(0, 0, 1, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(spBemerkung, GBCFactory.createGBC(100, 100, 3, 10, 1, 2, GridBagConstraints.BOTH));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 100, 4, 12, 1, 1, GridBagConstraints.VERTICAL));
    }

    /* Laedt alle benoetigten Daten. */
    private void load() {
        try {
            setWaitCursor();
            QueryCCService qs = getCCService(QueryCCService.class);
            rfVpnType.setFindService(qs);
            rfNiederlassung.setFindService(qs);

            VPNService vpnService = getCCService(VPNService.class);
            vpn = vpnService.findVPN(vpnId);

            if (vpn != null) {
                tfVpnName.setText(vpn.getVpnName());
                tfRealm.setText(vpn.getRealm());
                rfVpnType.setReferenceId(vpn.getVpnType());
                dcDatum.setDate(vpn.getDatum());
                tfEinwahl.setText(vpn.getEinwahl());
                taBemerkung.setText(vpn.getBemerkung());
                rfNiederlassung.setReferenceId(vpn.getNiederlassungId());
                tfProjectLead.setText(vpn.getProjektleiter());
                tfSalesRep.setText(vpn.getSalesRep());
                cbQOS.setSelected(vpn.getQos());
            }
            else {
                MessageHelper.showInfoDialog(this,
                        "Die VPN-Daten konnten nicht ermittelt werden.", null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            if (vpn != null) {
                vpn.setVpnName(tfVpnName.getText(null));
                vpn.setRealm(tfRealm.getText(null));
                vpn.setVpnType(rfVpnType.getReferenceIdAs(Long.class));
                vpn.setDatum(dcDatum.getDate(new Date()));
                vpn.setEinwahl(tfEinwahl.getText(null));
                vpn.setBemerkung(taBemerkung.getText(null));
                vpn.setNiederlassungId(rfNiederlassung.getReferenceIdAs(Long.class));
                vpn.setProjektleiter(tfProjectLead.getText(null));
                vpn.setSalesRep(tfSalesRep.getText(null));
                vpn.setQos(cbQOS.isSelectedBoolean());

                VPNService vpnService = getCCService(VPNService.class);
                vpnService.saveVPN(vpn);

                prepare4Close();
                setValue(Integer.valueOf(OK_OPTION));
            }
            else {
                MessageHelper.showInfoDialog(this,
                        "Es ist kein VPN-Auftrag ausgewählt, der gespeichert werden könnte.", null, true);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

}


