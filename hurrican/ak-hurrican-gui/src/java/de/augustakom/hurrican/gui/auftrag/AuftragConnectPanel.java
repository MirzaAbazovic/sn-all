/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2010 14:56:28
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.model.cc.AuftragConnect;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.ConnectService;

/**
 * Auftrags-Panel fuer Connect-Daten.
 */
public class AuftragConnectPanel extends AbstractAuftragPanel implements AKModelOwner {

    private static final Logger LOGGER = Logger.getLogger(AuftragConnectPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/AuftragConnectPanel.xml";

    // Modelle
    private CCAuftragModel auftragModel = null;

    // Felder
    AKJTextField tfProduktcode = null;
    AKJTextField tfProduktspezifikation = null;
    AKJTextField tfProjektleiter = null;
    AKJTextField tfProjektleiterKuendigung = null;
    AKJTextField tfTts = null;
    AKJTextField tfVerfuegbarkeit = null;

    /**
     * Default-Const.
     */
    public AuftragConnectPanel() {
        super(RESOURCE);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblProduktcode = getSwingFactory().createLabel("produktcode");
        tfProduktcode = getSwingFactory().createTextField("produktcode", false);
        AKJLabel lblProduktspezifikation = getSwingFactory().createLabel("produktspezifikation");
        tfProduktspezifikation = getSwingFactory().createTextField("produktspezifikation", false);
        AKJLabel lblProjektleiter = getSwingFactory().createLabel("projektleiter");
        tfProjektleiter = getSwingFactory().createTextField("projektleiter", false);
        AKJLabel lblProjektleiterKuendigung = getSwingFactory().createLabel("projektleiterKuendigung");
        tfProjektleiterKuendigung = getSwingFactory().createTextField("projektleiterKuendigung", false);
        AKJLabel lblTts = getSwingFactory().createLabel("tts");
        tfTts = getSwingFactory().createTextField("tts", false);
        AKJLabel lblVerfuegbarkeit = getSwingFactory().createLabel("verfuegbarkeit");
        tfVerfuegbarkeit = getSwingFactory().createTextField("verfuegbarkeit", false);

        // Panels fuer die Aufrag-Connect Daten
        AKJPanel basePnl = new AKJPanel(new GridBagLayout());
        basePnl.setBorder(BorderFactory.createTitledBorder("Connect-Auftragdaten"));

        basePnl.add(lblProduktcode, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        basePnl.add(tfProduktcode, GBCFactory.createGBC(100, 0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblProduktspezifikation, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(tfProduktspezifikation, GBCFactory.createGBC(100, 0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblProjektleiter, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(tfProjektleiter, GBCFactory.createGBC(100, 0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblProjektleiterKuendigung, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(tfProjektleiterKuendigung, GBCFactory.createGBC(100, 0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblTts, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(tfTts, GBCFactory.createGBC(100, 0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(lblVerfuegbarkeit, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        basePnl.add(tfVerfuegbarkeit, GBCFactory.createGBC(100, 0, 2, 5, 2, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(basePnl, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(200, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 500, 0, 1, 1, 1, GridBagConstraints.NONE));
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        try {
            if (model instanceof CCAuftragModel) {
                this.auftragModel = (CCAuftragModel) model;
            }
            else {
                this.auftragModel = null;
            }

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException("Error loading connect data: " + e.getMessage(), e);
        }
    }

    @Override
    public void readModel() throws AKGUIException {
        try {
            setWaitCursor();
            GuiTools.cleanFields(this);
            if (auftragModel != null) {
                AuftragConnect auftragConnect = (getCCService(ConnectService.class)).findAuftragConnectByAuftrag(auftragModel);
                if (auftragConnect != null) {
                    tfProduktcode.setText(auftragConnect.getProduktcode());
                    tfProduktspezifikation.setText(auftragConnect.getProduktspezifikation());
                    tfProjektleiter.setText(auftragConnect.getProjektleiter());
                    tfProjektleiterKuendigung.setText(auftragConnect.getProjektleiterKuendigung());
                    tfTts.setText(auftragConnect.getTts());
                    tfVerfuegbarkeit.setText(auftragConnect.getVerfuegbarkeit());
                }
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
    protected void execute(String command) {
    }

    @Override
    public boolean hasModelChanged() {
        return false;
    }

    @Override
    public void saveModel() throws AKGUIException {
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
