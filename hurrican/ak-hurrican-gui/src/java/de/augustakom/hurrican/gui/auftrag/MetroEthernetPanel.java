/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2004 16:23:59
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * Panel fuer die Darstellung von Connect Basic
 *
 *
 */
public class MetroEthernetPanel extends AbstractDataPanel {

    private static final Logger LOGGER = Logger.getLogger(MetroEthernetPanel.class);

    private static final String VPLS_ID = "vplsId";
    private static final String VLAN = "vlan";

    private AKJTextField tfVplsId = null;
    private AKJTextField tfVlan = null;

    private Endstelle endstelle = null;
    private AuftragTechnik auftragTechnik = null;

    /**
     * Konstruktor.
     */
    public MetroEthernetPanel() {
        super("de/augustakom/hurrican/gui/auftrag/resources/MetroEthernetPanel.xml");
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJPanel fieldPanel = new AKJPanel(new GridBagLayout());
        fieldPanel.setBorder(BorderFactory.createEtchedBorder());
        fieldPanel.setLayout(new GridBagLayout());

        AKJLabel lblVplsId = getSwingFactory().createLabel(VPLS_ID);
        AKJLabel lblVlan = getSwingFactory().createLabel(VLAN);
        tfVplsId = getSwingFactory().createTextField(VPLS_ID);
        tfVlan = getSwingFactory().createTextField(VLAN);

        fieldPanel.add(lblVplsId, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        fieldPanel.add(lblVlan, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        fieldPanel.add(tfVplsId, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        fieldPanel.add(tfVlan, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(fieldPanel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel(), GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel(), GBCFactory.createGBC(0, 100, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
    }

    @Override
    public void setModel(Observable model) {
        if (model instanceof Endstelle) {
            this.endstelle = (Endstelle) model;
        }
        else {
            this.endstelle = null;
        }
        readModel();
    }

    @Override
    public void readModel() {
        try {
            setWaitCursor();

            if (endstelle != null) {
                tfVlan.setText(endstelle.getVlan());

                CCAuftragService as = getCCService(CCAuftragService.class);
                tfVplsId.setText(auftragTechnik.getVplsId());
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

    @Override
    public void saveModel() throws AKGUIException {
        if ((endstelle != null) && (tfVlan != null)) {
            endstelle.setVlan(tfVlan.getText(null));
        }

        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            auftragTechnik.setVplsId(tfVplsId.getText(null));
            as.saveAuftragTechnik(auftragTechnik, false);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @param auftragId the auftragId to set
     */
    public void setAuftragTechnik(AuftragTechnik auftragTechnik) {
        this.auftragTechnik = auftragTechnik;
    }
}
