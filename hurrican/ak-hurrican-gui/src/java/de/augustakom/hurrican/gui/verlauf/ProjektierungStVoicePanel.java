/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2005 11:38:41
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Projektierungs-Panel fuer die Darstellung/Bearbeitung der Projektierungen für die Abteilung ST Voice.
 *
 *
 */
public class ProjektierungStVoicePanel extends AbstractProjektierungPanel {

    // GUI-Komponenten
    private AKJTextField tfBearbeiter = null;
    private AKJTextField tfStatus = null;

    public ProjektierungStVoicePanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungStVoicePanel.xml",
                Abteilung.ST_VOICE, false, true);
        createGUI();
        super.loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblStatus = getSwingFactory().createLabel("status");
        AKJLabel lblBearbeiter = getSwingFactory().createLabel("bearbeiter");

        tfStatus = getSwingFactory().createTextField("status", false);
        tfBearbeiter = getSwingFactory().createTextField("bearbeiter", false);

        AKJButton btnErledigen = getSwingFactory().createButton("erledigen", getActionListener());
        AKJButton btnUebernahme = getSwingFactory().createButton("uebernehmen", getActionListener());
        AKJButton btnPrint = getSwingFactory().createButton("print", getActionListener());
        AKJButton btnBemerkung = getSwingFactory().createButton("bemerkungen", getActionListener());
        AKJButton btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnErledigen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnUebernahme, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnPrint, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnBemerkung, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnShowPorts, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(lblStatus, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(tfStatus, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblBearbeiter, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfBearbeiter, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(btnPnl, GBCFactory.createGBC(100, 0, 0, 2, 4, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 3, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnPrint, btnBemerkung, btnErledigen, btnUebernahme, btnShowPorts);
    }

    /**
     * @see de.augustakom.hurrican.gui.verlauf.AbstractProjektierungPanel#showDetails4Verlauf(java.lang.Integer)
     */
    @Override
    protected void showDetails4Verlauf(Long verlaufId) {
        if (getActView() != null) {
            tfStatus.setText(getActView().getVerlaufAbtStatus());
            tfBearbeiter.setText(getActView().getBearbeiter());
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("print".equals(command)) {
            printProjektierung();
        }
        else if ("bemerkungen".equals(command)) {
            showBemerkungen((getActView() != null) ? getActView().getVerlaufId() : null);
        }
        else if ("erledigen".equals(command)) {
            projektierungAbschliessenTechnik();
        }
        else if ("uebernehmen".equals(command)) {
            projektierungUebernehmen();
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getActView());
        }
    }
}


