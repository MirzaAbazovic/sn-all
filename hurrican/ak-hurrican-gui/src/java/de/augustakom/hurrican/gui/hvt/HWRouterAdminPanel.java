/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2008 15:45:26
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWRouter;
import de.augustakom.hurrican.service.cc.HWService;


/**
 * Admin-Panel fuer die Verwaltung der HW-DLUs
 */
public class HWRouterAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(HWRouterAdminPanel.class);

    private AKJTextField tfSerialNumber = null;
    private AKJTextField tfType = null;

    private HWRouter rack = null;

    /**
     * Konstruktor
     */
    public HWRouterAdminPanel(HWRack rack) {
        super("de/augustakom/hurrican/gui/hvt/resources/HWRouterAdminPanel.xml");
        createGUI();
        loadData();
        showDetails(rack);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblSerialNumber = getSwingFactory().createLabel("number");
        AKJLabel lblType = getSwingFactory().createLabel("type");

        tfType = getSwingFactory().createTextField("type");
        tfSerialNumber = getSwingFactory().createTextField("number");

        AKJPanel left = new AKJPanel(new GridBagLayout(), "Router");
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(lblType, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(tfType, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblSerialNumber, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfSerialNumber, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 3, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new BorderLayout());
        this.add(left, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if ((details != null) && (details instanceof HWRouter)) {
            rack = (HWRouter) details;
            tfSerialNumber.setText(rack.getSerialNumber());
            tfType.setText(rack.getRouterTyp());
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

            rack.setRouterTyp(tfType.getText());
            rack.setSerialNumber(tfSerialNumber.getText());

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
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

}


