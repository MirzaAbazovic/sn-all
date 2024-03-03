/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2008 15:45:26
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Admin-Panel fuer die Verwaltung der HW-DSLAMs
 */
public class HWDslamAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(HWDslamAdminPanel.class);

    private AKReferenceField rfErxStandort = null;
    private AKReferenceField rfPhysikArt = null;
    private AKJTextField tfDhcp82Name = null;
    private AKJTextField tfIpAdresse = null;
    private AKJTextField tfEinbauplatz = null;
    private AKJTextField tfAnschluss = null;
    private AKJTextField tfSlotPort = null;
    private AKJTextField tfAnsArt = null;
    private AKJTextField tfSoftwareVersion = null;
    private AKJTextField tfErxIface = null;
    private AKJTextField tfPhysikWert = null;
    private AKJTextField tfAltGslamBez = null;
    private AKJFormattedTextField tfVpiUbrAdsl = null;
    private AKJFormattedTextField tfVpiSdsl = null;
    private AKJFormattedTextField tfVpiCpeMgmt = null;
    private AKJFormattedTextField tfOuterTagAdsl = null;
    private AKJFormattedTextField tfOuterTagSdsl = null;
    private AKJFormattedTextField tfOuterTagVoip = null;
    private AKJFormattedTextField tfOuterTagCpeMgmt = null;
    private AKJFormattedTextField tfOuterTagIadMgmt = null;
    private AKJFormattedTextField tfBrasVpiAdsl = null;
    private AKJFormattedTextField tfBrasOuterTagAdsl = null;
    private AKJFormattedTextField tfBrasOuterTagSdsl = null;
    private AKJFormattedTextField tfBrasOuterTagVoip = null;
    private AKJFormattedTextField tfCcOffset = null;

    private AKJFormattedTextField tfSvlan;

    private AKJCheckBox cbSchmKonzept = null;
    private AKJTextField tfAtmPattern = null;

    private AKReferenceField rfDslamType = null;

    private HWDslam rack = null;

    /**
     * Konstruktor
     */
    public HWDslamAdminPanel(HWRack rack) {
        super("de/augustakom/hurrican/gui/hvt/resources/HWDslamAdminPanel.xml");
        createGUI();
        loadData();
        showDetails(rack);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblErxStandort = getSwingFactory().createLabel("erx.standort");
        AKJLabel lblPhysikArt = getSwingFactory().createLabel("physik.art");
        AKJLabel lblDhcp82 = getSwingFactory().createLabel("dhcp82");
        AKJLabel lblIpAdresse = getSwingFactory().createLabel("ip.address");
        AKJLabel lblEinbauplatz = getSwingFactory().createLabel("einbauplatz");
        AKJLabel lblAnschluss = getSwingFactory().createLabel("anschluss");
        AKJLabel lblSlotPort = getSwingFactory().createLabel("slot.port");
        AKJLabel lblAnsArt = getSwingFactory().createLabel("ans.art");
        AKJLabel lblSoftwareVersion = getSwingFactory().createLabel("software.version");
        AKJLabel lblErxIface = getSwingFactory().createLabel("erx.iface");
        AKJLabel lblPhysikWert = getSwingFactory().createLabel("physik.wert");
        AKJLabel lblAltGslamBez = getSwingFactory().createLabel("alt.gslam.bez");
        AKJLabel lblVpiUbrAdsl = getSwingFactory().createLabel("vpi.ubr.adsl");
        AKJLabel lblVpiSdsl = getSwingFactory().createLabel("vpi.sdsl");
        AKJLabel lblVpiCpeMgmt = getSwingFactory().createLabel("vpi.cpe.mgmt");
        AKJLabel lblOuterTagAdsl = getSwingFactory().createLabel("outer.tag.adsl");
        AKJLabel lblOuterTagSdsl = getSwingFactory().createLabel("outer.tag.sdsl");
        AKJLabel lblOuterTagVoip = getSwingFactory().createLabel("outer.tag.voip");
        AKJLabel lblOuterTagCpeMgmt = getSwingFactory().createLabel("outer.tag.cpe.mgmt");
        AKJLabel lblOuterTagIadMgmt = getSwingFactory().createLabel("outer.tag.iad.mgmt");
        AKJLabel lblBrasVpiAdsl = getSwingFactory().createLabel("bras.vpi.adsl");
        AKJLabel lblBrasOuterTagAdsl = getSwingFactory().createLabel("bras.outer.tag.adsl");
        AKJLabel lblBrasOuterTagSdsl = getSwingFactory().createLabel("bras.outer.tag.sdsl");
        AKJLabel lblBrasOuterTagVoip = getSwingFactory().createLabel("bras.outer.tag.voip");
        AKJLabel lblCcOffset = getSwingFactory().createLabel("cc.offset");
        AKJLabel lblSchKonzept = getSwingFactory().createLabel("schmidt.konzept");
        AKJLabel lblAtmPattern = getSwingFactory().createLabel("atm.pattern");
        AKJLabel lblSvlan = getSwingFactory().createLabel("svlan");
        AKJLabel lblDslamType = getSwingFactory().createLabel("type");

        rfErxStandort = getSwingFactory().createReferenceField("erx.standort");
        rfPhysikArt = getSwingFactory().createReferenceField("physik.art");
        tfDhcp82Name = getSwingFactory().createTextField("dhcp82");
        tfIpAdresse = getSwingFactory().createTextField("ip.address");
        tfEinbauplatz = getSwingFactory().createTextField("einbauplatz");
        tfAnschluss = getSwingFactory().createTextField("anschluss");
        tfSlotPort = getSwingFactory().createTextField("slot.port");
        tfAnsArt = getSwingFactory().createTextField("ans.art");
        tfSoftwareVersion = getSwingFactory().createTextField("software.version");
        tfErxIface = getSwingFactory().createTextField("erx.iface");
        tfPhysikWert = getSwingFactory().createTextField("physik.wert");
        tfAltGslamBez = getSwingFactory().createTextField("alt.gslam.bez");
        tfVpiUbrAdsl = getSwingFactory().createFormattedTextField("vpi.ubr.adsl");
        tfVpiSdsl = getSwingFactory().createFormattedTextField("vpi.sdsl");
        tfVpiCpeMgmt = getSwingFactory().createFormattedTextField("vpi.cpe.mgmt");
        tfOuterTagAdsl = getSwingFactory().createFormattedTextField("outer.tag.adsl");
        tfOuterTagSdsl = getSwingFactory().createFormattedTextField("outer.tag.sdsl");
        tfOuterTagVoip = getSwingFactory().createFormattedTextField("outer.tag.voip");
        tfOuterTagCpeMgmt = getSwingFactory().createFormattedTextField("outer.tag.cpe.mgmt");
        tfOuterTagIadMgmt = getSwingFactory().createFormattedTextField("outer.tag.iad.mgmt");
        tfBrasVpiAdsl = getSwingFactory().createFormattedTextField("bras.vpi.adsl");
        tfBrasOuterTagAdsl = getSwingFactory().createFormattedTextField("bras.outer.tag.adsl");
        tfBrasOuterTagSdsl = getSwingFactory().createFormattedTextField("bras.outer.tag.sdsl");
        tfBrasOuterTagVoip = getSwingFactory().createFormattedTextField("bras.outer.tag.voip");
        tfCcOffset = getSwingFactory().createFormattedTextField("cc.offset");
        cbSchmKonzept = getSwingFactory().createCheckBox("schmidt.konzept");
        tfAtmPattern = getSwingFactory().createTextField("atm.pattern");
        tfSvlan = getSwingFactory().createFormattedTextField("svlan");
        rfDslamType = getSwingFactory().createReferenceField("type");

        AKJPanel topLeft = new AKJPanel(new GridBagLayout());
        topLeft.add(lblDslamType,       GBCFactory.createGBC(0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(new AKJPanel(),     GBCFactory.createGBC(0,   0, 1, 0, 1, 1, GridBagConstraints.NONE));
        topLeft.add(rfDslamType,        GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        topLeft.add(lblDhcp82,          GBCFactory.createGBC(0,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(tfDhcp82Name,       GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(lblAtmPattern,      GBCFactory.createGBC(0,   0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(tfAtmPattern,       GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(lblSoftwareVersion, GBCFactory.createGBC(0,   0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(tfSoftwareVersion,  GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(lblIpAdresse,       GBCFactory.createGBC(0,   0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(tfIpAdresse,        GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(lblPhysikArt,       GBCFactory.createGBC(0,   0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(rfPhysikArt,        GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(lblPhysikWert,      GBCFactory.createGBC(0,   0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(tfPhysikWert,       GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(lblAltGslamBez,     GBCFactory.createGBC(0,   0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        topLeft.add(tfAltGslamBez,      GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel topRight = new AKJPanel(new GridBagLayout());
        topRight.add(lblEinbauplatz,    GBCFactory.createGBC(0,   0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(new AKJPanel(),    GBCFactory.createGBC(0,   0, 1, 0, 1, 1, GridBagConstraints.NONE));
        topRight.add(tfEinbauplatz,     GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(lblErxStandort,    GBCFactory.createGBC(0,   0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(rfErxStandort,     GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(lblErxIface,       GBCFactory.createGBC(0,   0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(tfErxIface,        GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(lblAnschluss,      GBCFactory.createGBC(0,   0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(tfAnschluss,       GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(lblSlotPort,       GBCFactory.createGBC(0,   0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(tfSlotPort,        GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(lblAnsArt,         GBCFactory.createGBC(0,   0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(tfAnsArt,          GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(lblSchKonzept,     GBCFactory.createGBC(0,   0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(cbSchmKonzept,     GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        topRight.add(new AKJLabel(" "), GBCFactory.createGBC(0,   0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        topRight.add(new AKJLabel(" "), GBCFactory.createGBC(0,   0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(topLeft, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(topRight, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel bottom = new AKJPanel(new GridBagLayout());
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.NONE));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 9, 0, 1, 1, GridBagConstraints.NONE));

        bottom.add(lblVpiUbrAdsl, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfVpiUbrAdsl, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblVpiSdsl, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfVpiSdsl, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblVpiCpeMgmt, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfVpiCpeMgmt, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblBrasVpiAdsl, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfBrasVpiAdsl, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        bottom.add(lblOuterTagAdsl, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfOuterTagAdsl, GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblOuterTagSdsl, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfOuterTagSdsl, GBCFactory.createGBC(100, 0, 6, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblOuterTagVoip, GBCFactory.createGBC(0, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfOuterTagVoip, GBCFactory.createGBC(100, 0, 6, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblOuterTagCpeMgmt, GBCFactory.createGBC(0, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfOuterTagCpeMgmt, GBCFactory.createGBC(100, 0, 6, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblOuterTagIadMgmt, GBCFactory.createGBC(0, 0, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfOuterTagIadMgmt, GBCFactory.createGBC(100, 0, 6, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        bottom.add(lblBrasOuterTagAdsl, GBCFactory.createGBC(100, 0, 8, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfBrasOuterTagAdsl, GBCFactory.createGBC(100, 0, 10, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblBrasOuterTagSdsl, GBCFactory.createGBC(0, 0, 8, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfBrasOuterTagSdsl, GBCFactory.createGBC(100, 0, 10, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblBrasOuterTagVoip, GBCFactory.createGBC(0, 0, 8, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfBrasOuterTagVoip, GBCFactory.createGBC(100, 0, 10, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblCcOffset, GBCFactory.createGBC(0, 0, 8, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfCcOffset, GBCFactory.createGBC(100, 0, 10, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(lblSvlan, GBCFactory.createGBC(0, 0, 8, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bottom.add(tfSvlan, GBCFactory.createGBC(0, 0, 10, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder("DSLAM"));
        this.add(top, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(bottom, GBCFactory.createGBC(100, 0, 0, 1, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if ((details != null) && (details instanceof HWDslam)) {
            rack = (HWDslam) details;
            rfErxStandort.setReferenceId(rack.getErxStandort());
            rfPhysikArt.setReferenceId(rack.getPhysikArt());
            tfDhcp82Name.setText(rack.getDhcp82Name());
            tfIpAdresse.setText(rack.getIpAdress());
            tfEinbauplatz.setText(rack.getEinbauplatz());
            tfAnschluss.setText(rack.getAnschluss());
            tfSlotPort.setText(rack.getAnsSlotPort());
            tfAnsArt.setText(rack.getAnsArt());
            tfSoftwareVersion.setText(rack.getSoftwareVersion());
            tfErxIface.setText(rack.getErxIfaceDaten());
            tfPhysikWert.setText(rack.getPhysikWert());
            tfAltGslamBez.setText(rack.getAltGslamBez());
            tfVpiUbrAdsl.setValue(rack.getVpiUbrADSL());
            tfVpiSdsl.setValue(rack.getVpiSDSL());
            tfVpiCpeMgmt.setValue(rack.getVpiCpeMgmt());
            tfBrasVpiAdsl.setValue(rack.getBrasVpiADSL());
            tfOuterTagAdsl.setValue(rack.getOuterTagADSL());
            tfOuterTagSdsl.setValue(rack.getOuterTagSDSL());
            tfOuterTagVoip.setValue(rack.getOuterTagVoip());
            tfOuterTagCpeMgmt.setValue(rack.getOuterTagCpeMgmt());
            tfOuterTagIadMgmt.setValue(rack.getOuterTagIadMgmt());
            tfBrasOuterTagAdsl.setValue(rack.getBrasOuterTagADSL());
            tfBrasOuterTagSdsl.setValue(rack.getBrasOuterTagSDSL());
            tfBrasOuterTagVoip.setValue(rack.getBrasOuterTagVoip());
            tfCcOffset.setValue(rack.getCcOffset());
            cbSchmKonzept.setSelected(rack.getSchmidtschesKonzept());
            tfAtmPattern.setText(rack.getAtmPattern());
            tfSvlan.setValue(rack.getSvlan());
            rfDslamType.setReferenceId(rack.getDslamType());
        }
        else {
            rack = null;
            GuiTools.cleanFields(this);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        try {
            ReferenceService refService = getCCService(ReferenceService.class);
            ISimpleFindService sfs = getCCService(QueryCCService.class);

            rfErxStandort.setFindService(sfs);
            List<Reference> refs = refService.findReferencesByType(Reference.REF_TYPE_HW_ERX_STANDORT, Boolean.TRUE);
            rfErxStandort.setReferenceList(refs);

            rfPhysikArt.setFindService(sfs);
            refs = refService.findReferencesByType(Reference.REF_TYPE_HW_PHYSIK_ART, Boolean.TRUE);
            rfPhysikArt.setReferenceList(refs);

            refs = refService.findReferencesByType(Reference.REF_TYPE_HW_DSLAM_TYPE, Boolean.TRUE);
            rfDslamType.setReferenceList(refs);
            rfDslamType.setFindService(sfs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        boolean isNew = ((rack != null) && (rack.getId() == null));
        try {
            if (rack == null) {
                throw new HurricanGUIException("Hardware-Rack nicht gesetzt!");
            }

            rack.setErxStandort(rfErxStandort.getReferenceIdAs(String.class));
            rack.setPhysikArt(rfPhysikArt.getReferenceIdAs(String.class));
            rack.setDhcp82Name(tfDhcp82Name.getText());
            rack.setIpAdress(tfIpAdresse.getText());
            rack.setEinbauplatz(tfEinbauplatz.getText());
            rack.setAnschluss(tfAnschluss.getText());
            rack.setAnsSlotPort(tfSlotPort.getText());
            rack.setAnsArt(tfAnsArt.getText());
            rack.setSoftwareVersion(tfSoftwareVersion.getText());
            rack.setErxIfaceDaten(tfErxIface.getText());
            rack.setPhysikWert(tfPhysikWert.getText());
            rack.setAltGslamBez(tfAltGslamBez.getText());
            rack.setVpiUbrADSL(tfVpiUbrAdsl.getValueAsInt(null));
            rack.setVpiSDSL(tfVpiSdsl.getValueAsInt(null));
            rack.setVpiCpeMgmt(tfVpiCpeMgmt.getValueAsInt(null));
            rack.setBrasVpiADSL(tfBrasVpiAdsl.getValueAsInt(null));
            rack.setOuterTagADSL(tfOuterTagAdsl.getValueAsInt(null));
            rack.setOuterTagSDSL(tfOuterTagSdsl.getValueAsInt(null));
            rack.setOuterTagVoip(tfOuterTagVoip.getValueAsInt(null));
            rack.setOuterTagCpeMgmt(tfOuterTagCpeMgmt.getValueAsInt(null));
            rack.setOuterTagIadMgmt(tfOuterTagIadMgmt.getValueAsInt(null));
            rack.setBrasOuterTagADSL(tfBrasOuterTagAdsl.getValueAsInt(null));
            rack.setBrasOuterTagSDSL(tfBrasOuterTagSdsl.getValueAsInt(null));
            rack.setBrasOuterTagVoip(tfBrasOuterTagVoip.getValueAsInt(null));
            rack.setCcOffset(tfCcOffset.getValueAsInt(null));
            rack.setSchmidtschesKonzept(cbSchmKonzept.isSelectedBoolean());
            rack.setAtmPattern(tfAtmPattern.getText());
            rack.setSvlan(tfSvlan.getValueAsInt(null));
            rack.setDslamType(rfDslamType.getReferenceIdAs(String.class));

            HWService service = getCCService(HWService.class);
            service.saveHWRack(rack);
        }
        catch (Exception e) {
            if (isNew) {
                // Wenn bspw. ein Constraint zuschlägt, Datensatz wieder als 'transient' markieren
                rack.setId(null);
            }

            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
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

}
