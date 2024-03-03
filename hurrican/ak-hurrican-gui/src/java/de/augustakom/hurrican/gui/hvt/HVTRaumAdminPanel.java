/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 11:10:26
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.HVTRaum;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Admin-Panel fuer die Verwaltung der HVT-Raeume
 *
 *
 */
public class HVTRaumAdminPanel extends AbstractAdminPanel implements AKNavigationBarListener {

    private static final Logger LOGGER = Logger.getLogger(HVTRaumAdminPanel.class);

    private AKJTextField tfRaum = null;
    private AKJNavigationBar navBar = null;

    private HVTStandort hvtStandort = null;
    private HVTRaum hvtRaumModel = null;
    private boolean guiCreated = false;

    /**
     * Konstruktor
     */
    public HVTRaumAdminPanel() {
        super("de/augustakom/hurrican/gui/hvt/resources/HVTRaumAdminPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        navBar = new AKJNavigationBar(this, true, true);

        AKJLabel lblRaum = getSwingFactory().createLabel("raum");
        tfRaum = getSwingFactory().createTextField("raum");

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        left.add(navBar, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblRaum, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        left.add(tfRaum, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 2, 1, 1, GridBagConstraints.VERTICAL));

        this.setLayout(new GridBagLayout());
        this.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.VERTICAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 1, 1, 1, GridBagConstraints.BOTH));

        guiCreated = true;
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof HVTStandort) {
            this.hvtStandort = (HVTStandort) details;
            loadData();
        }
        else {
            this.hvtStandort = null;
            clear();
            navBar.setData(null);
        }
    }

    /* 'Loescht' alle Felder */
    private void clear() {
        if (guiCreated) {
            GuiTools.cleanFields(this);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        if (hvtStandort != null) {
            try {
                HVTService service = getCCService(HVTService.class);
                List<HVTRaum> raeume = service.findHVTRaeume4Standort(hvtStandort.getHvtIdStandort());
                navBar.setData(raeume);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        if (hvtStandort != null) {
            HVTRaum raum = new HVTRaum();
            raum.setHvtIdStandort(hvtStandort.getHvtIdStandort());
            showNavigationObject(raum, 0);
        }
        else {
            MessageHelper.showInfoDialog(this, "Bitte zuerst einen HVT-Standort auswählen!");
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            if (hvtStandort == null) {
                MessageHelper.showInfoDialog(this, "Bitte zuerst einen HVT-Standort auswählen!");
                return;
            }
            if (hvtRaumModel == null) {
                hvtRaumModel = new HVTRaum();
            }
            boolean isNew = (hvtRaumModel.getId() == null) ? true : false;

            hvtRaumModel.setHvtIdStandort(hvtStandort.getHvtIdStandort());
            hvtRaumModel.setRaum(tfRaum.getText());

            HVTService service = getCCService(HVTService.class);
            service.saveHVTRaum(hvtRaumModel);

            if (isNew) {
                navBar.addNavigationObject(hvtRaumModel);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKNavigationBarListener#showNavigationObject(java.lang.Object, int)
     */
    public void showNavigationObject(Object obj, int number) {
        if (guiCreated) {
            if (obj instanceof HVTRaum) {
                hvtRaumModel = (HVTRaum) obj;
                tfRaum.setText(hvtRaumModel.getRaum());
            }
            else {
                hvtRaumModel = null;
                clear();
            }
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
    public void update(Observable o, Object arg) {
    }

}


