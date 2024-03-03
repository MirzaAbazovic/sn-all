/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.05.2009 10:25:27
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.time.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionPane;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;

/**
 * Admin-Panel fuer die Verwaltung von OLTs.
 */
public class HWOltAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(HWOltAdminPanel.class);

    private AKJTextField tfSerialNo = null;
    private AKJTextField tfQinqVon = null;
    private AKJTextField tfQinqBis = null;
    private AKJTextField tfIpNetzVon = null;
    // lfd. Nr. fuer VLAN Berechnung
    private AKJTextField tfOltNr = null;
    private AKJDateComponent dcVlanAb;
    private AKJFormattedTextField tfSvlanOffset = null;
    private AKJTextField tfOltType = null;

    private HWOlt rack = null;

    /**
     * Konstruktor
     */
    public HWOltAdminPanel(HWRack rack) {
        super("de/augustakom/hurrican/gui/hvt/resources/HWOltAdminPanel.xml");
        createGUI();
        loadData();
        showDetails(rack);
        manageGUI(dcVlanAb);
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblSerialNo = getSwingFactory().createLabel("serial.no");
        tfSerialNo = getSwingFactory().createTextField("serial.no");

        AKJLabel lblQinqVon = getSwingFactory().createLabel("qinq.von");
        tfQinqVon = getSwingFactory().createTextField("qinq.von");

        AKJLabel lblQinqBis = getSwingFactory().createLabel("qinq.bis");
        tfQinqBis = getSwingFactory().createTextField("qinq.bis");

        AKJLabel lblIpNetzVon = getSwingFactory().createLabel("ip.netz.von");
        tfIpNetzVon = getSwingFactory().createTextField("ip.netz.von");

        AKJLabel lblOltNr = getSwingFactory().createLabel("olt.nr");
        tfOltNr = getSwingFactory().createTextField("olt.nr");

        AKJLabel lbVlanAb = getSwingFactory().createLabel("migriert");
        dcVlanAb = getSwingFactory().createDateComponent("migriert");

        AKJLabel lbSvlanOffset = getSwingFactory().createLabel("svlan.offset");
        tfSvlanOffset = getSwingFactory().createFormattedTextField("svlan.offset");

        AKJLabel lbOltType = getSwingFactory().createLabel("olt.type");
        tfOltType = getSwingFactory().createTextField("olt.type");

        // @formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout(), getSwingFactory().getText("title"));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblSerialNo    , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(tfSerialNo     , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(lblQinqVon     , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 2, 1, 1, GridBagConstraints.NONE));
        left.add(tfQinqVon      , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(lblQinqBis     , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 3, 1, 1, GridBagConstraints.NONE));
        left.add(tfQinqBis      , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(lblIpNetzVon   , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 4, 1, 1, GridBagConstraints.NONE));
        left.add(tfIpNetzVon    , GBCFactory.createGBC(100,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(lblOltNr       , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 5, 1, 1, GridBagConstraints.NONE));
        left.add(tfOltNr        , GBCFactory.createGBC(100,  0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(lbVlanAb       , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 6, 1, 1, GridBagConstraints.NONE));
        left.add(dcVlanAb       , GBCFactory.createGBC(100,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(lbSvlanOffset  , GBCFactory.createGBC(  0,  0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 7, 1, 1, GridBagConstraints.NONE));
        left.add(tfSvlanOffset  , GBCFactory.createGBC(100,  0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(lbOltType      , GBCFactory.createGBC(  0,  0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0,  0, 2, 8, 1, 1, GridBagConstraints.NONE));
        left.add(tfOltType      , GBCFactory.createGBC(100,  0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        left.add(new AKJPanel() , GBCFactory.createGBC(  0,100, 3, 8, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        this.setLayout(new BorderLayout());
        this.add(left, BorderLayout.CENTER);
    }

    @Override
    public void showDetails(Object details) {
        if ((details != null) && (details instanceof HWOlt)) {
            rack = (HWOlt) details;
            tfSerialNo.setText(rack.getSerialNo());
            tfQinqVon.setText(rack.getQinqVon());
            tfQinqBis.setText(rack.getQinqBis());
            tfIpNetzVon.setText(rack.getIpNetzVon());
            tfOltNr.setText(rack.getOltNr());
            tfOltNr.setEditable(rack.getOltNr() == null);
            tfSvlanOffset.setValue(rack.getSvlanOffset());
            tfOltType.setText(rack.getOltType());
            showDetailsForVlanAb();
        }
        else {
            rack = null;
            GuiTools.cleanFields(this);
        }
    }

    private void showDetailsForVlanAb() {
        dcVlanAb.setDate(rack.getVlanAktivAb());
        final boolean dcVlanAbEditable = (rack.getVlanAktivAb() == null)
                || (Instant.ofEpochMilli(rack.getVlanAktivAb().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now()));
        dcVlanAb.setUsable(dcVlanAbEditable);
    }

    @Override
    public final void loadData() {
    }

    @Override
    public void createNew() {
    }

    @Override
    public void saveData() {
        boolean isNew = ((rack != null) && (rack.getId() == null));
        try {
            if (rack == null) {
                throw new HurricanGUIException("Hardware-Rack nicht gesetzt!");
            }

            rack.setSerialNo(tfSerialNo.getText(null));
            rack.setQinqVon(tfQinqVon.getText(null));
            rack.setQinqBis(tfQinqBis.getText(null));
            rack.setIpNetzVon(tfIpNetzVon.getText(null));
            rack.setOltNr(tfOltNr.getTextAsInt(null));
            rack.setSvlanOffset(tfSvlanOffset.getValueAsInt(null));
            rack.setOltType(tfOltType.getText(null));

            HWService service = getCCService(HWService.class);

            rack = service.saveHWRack(rack);
            saveVlanAbIfChanged();
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

    private void saveVlanAbIfChanged() throws ServiceNotFoundException, StoreException {
        final Date mandantenfaehigAb = dcVlanAb.getDate(null);
        final boolean vlanAbChanged = ((((mandantenfaehigAb != null) && (rack.getVlanAktivAb() != null))) && (!DateTools
                .isDateEqual(mandantenfaehigAb, rack.getVlanAktivAb())))
                || ((mandantenfaehigAb == null) && (rack.getVlanAktivAb() != null))
                || ((mandantenfaehigAb != null) && (rack.getVlanAktivAb() == null));

        if (vlanAbChanged) {
            final Date today = new Date();
            if (DateTools.isDateBefore(mandantenfaehigAb, today)) {
                MessageHelper.showWarningDialog(this,
                        "Das Datum \"Mandantenfähig ab\" kann nur in die Zukunft gesetzt werden!", true);
            }
            else {
                //@formatter:off
                final int confirmOption = MessageHelper
                        .showYesNoQuestion(
                                this,
                                "Eine Änderung des Datums \"Mandantenfähig ab\" der OLT führt zu einer Neuberechnung der VLANs " +
                                "aller betroffener Aufträge!\nBeim erstmaligen Setzen werden die VLans initial angelegt.\n" +
                                "Soll das Datum wirklich geändert werden?", "Neuberechnung aller Aufträge durchführen?");
                //@formatter:on
                if (confirmOption == AKJOptionPane.OK_OPTION) {
                    try {
                        setTopLevelWaitCursor(this);
                        VlanService vlanService = getCCService(VlanService.class);
                        Pair<HWOlt, Integer> retVal = vlanService.changeHwOltVlanAb(rack.getId(), mandantenfaehigAb);
                        rack = retVal.getFirst();
                        MessageHelper
                                .showInfoDialog(
                                        this,
                                        String.format(
                                                "Die Änderungen wurden erfolgreich abgeschlossen. Es wurden VLANs für %d Aufträge berechnet.",
                                                retVal.getSecond())
                                );
                    }
                    finally {
                        setTopLevelDefaultCursor(this);
                    }
                }
                else {
                    dcVlanAb.setDate(rack.getVlanAktivAb());
                }
            }
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}
