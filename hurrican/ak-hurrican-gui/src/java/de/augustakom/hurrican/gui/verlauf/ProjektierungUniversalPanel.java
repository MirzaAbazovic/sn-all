/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.2014
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;


/**
 * Panel fuer eine abteilungs-uebergreifende Projektierungs-GUI. <br/>
 * Die gewuenschte Abteilung kann ueber eine ComboBox selektiert werden; durch die Selektion werden die
 * Projektierungs-Datensaetze geladen.
 */
public class ProjektierungUniversalPanel extends AbstractProjektierungPanel {

    /**
     * Default-Konstruktor.
     */
    public ProjektierungUniversalPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungUniversalPanel.xml", true);
        createGUI();
    }

    @Override
    protected final void createGUI() {
        AKJButton btnErledigen = getSwingFactory().createButton("erledigen", getActionListener());
        AKJButton btnUebernahme = getSwingFactory().createButton("uebernehmen", getActionListener());
        AKJButton btnPrint = getSwingFactory().createButton("print", getActionListener());
        AKJButton btnBemerkungen = getSwingFactory().createButton("bemerkungen", getActionListener());
        AKJButton btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(btnErledigen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnUebernahme, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnPrint, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnBemerkungen, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnShowPorts, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 1, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnErledigen, btnUebernahme, btnPrint, btnBemerkungen, btnShowPorts);
    }

    @Override
    protected void showDetails4Verlauf(Long verlaufId) {
    }

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


