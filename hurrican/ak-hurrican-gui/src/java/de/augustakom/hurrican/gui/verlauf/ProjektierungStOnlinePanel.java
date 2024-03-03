/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2005 11:19:46
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Panel fuer die Darstellung der Projektierungen von ST Online.
 *
 *
 */
public class ProjektierungStOnlinePanel extends AbstractProjektierungPanel {

    /**
     * Default-Konstruktor.
     */
    public ProjektierungStOnlinePanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungStOnlinePanel.xml",
                Abteilung.ST_ONLINE, false, true);
        createGUI();
        super.loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
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

    /**
     * @see de.augustakom.hurrican.gui.verlauf.AbstractProjektierungPanel#showDetails4Verlauf(java.lang.Integer)
     */
    @Override
    protected void showDetails4Verlauf(Long verlaufId) {
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


