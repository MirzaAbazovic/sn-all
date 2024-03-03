/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2005 14:36:46
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Panel fuer die Anzeige der Projektierungs-Ruecklaeufer fuer AM.
 *
 *
 */
public class ProjektierungAmRlPanel extends AbstractProjektierungPanel {

    private static final Logger LOGGER = Logger.getLogger(ProjektierungAmRlPanel.class);

    /**
     * Default-Konstruktor.
     */
    public ProjektierungAmRlPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungAmRlPanel.xml", Abteilung.AM, true, true);
        createGUI();
        super.loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJButton btnErledigen = getSwingFactory().createButton("erledigen", getActionListener());
        AKJButton btnBemerkungen = getSwingFactory().createButton("bemerkungen", getActionListener());
        AKJButton btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(btnErledigen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnBemerkungen, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnShowPorts, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 1, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnErledigen, btnBemerkungen, btnShowPorts);
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
        if ("bemerkungen".equals(command)) {
            showBemerkungen((getActView() != null) ? getActView().getVerlaufId() : null);
        }
        else if ("erledigen".equals(command)) {
            projektierungErledigen();
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getActView());
        }
    }

    /* Setzt die Projektierung auf 'erledigt'. */
    private void projektierungErledigen() {
        if (getActView() == null) {
            MessageHelper.showInfoDialog(getMainFrame(), NOTHING_SELECTED, null, true);
            return;
        }

        if (NumberTools.equal(getActView().getVerlaufAbtStatusId(), VerlaufStatus.STATUS_ERLEDIGT)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Projektierung wurde bereits erledigt!", null, true);
            return;
        }

        try {
            BAService bas = getCCService(BAService.class);
            Verlauf result = bas.amPrAbschliessen(
                    getActView().getVerlaufId(), HurricanSystemRegistry.instance().getSessionId());

            // Hinweis, dass Verlauf negativ --> Auftragsstatus auf Ursprung!!!
            if (BooleanTools.nullToFalse(result.getNotPossible())) {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Die Projektierung wurde als nicht realisierbar gekennzeichnet!\n" +
                                "Der Status des Auftrags wurde auf den Ursprungswert zurück gesetzt und " +
                                "die Projektierung beendet."
                );
            }
            else {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Die Projektierung wurde abgeschlossen.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }
}


