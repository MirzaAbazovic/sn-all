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
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
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
import de.augustakom.hurrican.model.cc.hardware.HWLtg;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.HWSwitchService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Admin-Panel fuer die Verwaltung der HW-DLUs
 *
 *
 */
public class HWLtgAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(HWLtgAdminPanel.class);

    private AKReferenceField rfType = null;
    private AKJTextField tfNumber = null;
    private AKJTextField tfLdarp = null;
    private AKJTextField tfSwitch = null;
    private AKJTextField tfMgName = null;

    private HWLtg rack = null;

    /**
     * Konstruktor
     */
    public HWLtgAdminPanel(HWRack rack) {
        super("de/augustakom/hurrican/gui/hvt/resources/HWLtgAdminPanel.xml");
        createGUI();
        loadData();
        showDetails(rack);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblNumber = getSwingFactory().createLabel("number");
        AKJLabel lblType = getSwingFactory().createLabel("type");
        AKJLabel lblLdarp = getSwingFactory().createLabel("ldarp");
        AKJLabel lblSwitch = getSwingFactory().createLabel("switch");
        AKJLabel lblMgName = getSwingFactory().createLabel("mg.name");

        tfNumber = getSwingFactory().createTextField("number");
        tfLdarp = getSwingFactory().createTextField("ldarp");
        rfType = getSwingFactory().createReferenceField("type");
        tfSwitch = getSwingFactory().createTextField("switch");
        tfMgName = getSwingFactory().createTextField("mg.name");

        AKJPanel left = new AKJPanel(new GridBagLayout(), "LTG");
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblType, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(rfType, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblNumber, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfNumber, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblLdarp, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfLdarp, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSwitch, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfSwitch, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblMgName, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfMgName, GBCFactory.createGBC(100, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 6, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new BorderLayout());
        this.add(left, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if ((details != null) && (details instanceof HWLtg)) {
            rack = (HWLtg) details;
            tfNumber.setText(rack.getLtgNumber());
            tfLdarp.setText(rack.getLtgLDARP());
            rfType.setReferenceId(rack.getLtgType());
            tfSwitch.setText(rack.getHwSwitch().getName());
            tfMgName.setText(rack.getMediaGatewayName());
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

            List<Reference> refs = refService.findReferencesByType(Reference.REF_TYPE_HW_LTG_TYPE, Boolean.TRUE);
            rfType.setReferenceList(refs);
            rfType.setFindService(sfs);
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
        // not needed for this panel
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        boolean isNew = (rack != null) && (rack.getId() == null);
        try {
            if (rack == null) {
                throw new HurricanGUIException("Hardware-Rack nicht gesetzt!");
            }

            rack.setLtgNumber(tfNumber.getText());
            rack.setLtgType(rfType.getReferenceIdAs(String.class));
            rack.setLtgLDARP(tfLdarp.getText());
            HWSwitchService hwSwitchService = getCCService(HWSwitchService.class);
            rack.setHwSwitch(hwSwitchService.findSwitchByName(tfSwitch.getText()));
            rack.setMediaGatewayName(tfMgName.getText());

            HWService service = getCCService(HWService.class);
            service.saveHWRack(rack);
        }
        catch (Exception e) {
            if (isNew) {
                //Wenn bspw. ein Constraint zuschlägt, Datensatz wieder als 'transient' markieren
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
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // not needed for this panel
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // not needed for this panel
    }

}
