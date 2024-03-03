/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2005 09:53:37
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractInternalServiceFrame;
import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Frame fuer die Darstellung der Projektierungen fuer eine best. Abteilung.
 *
 *
 */
public class ProjektierungFrame extends AbstractInternalServiceFrame {

    private static final String UNIQUE_NAME = "projektierung.frame";
    private static final String UNIQUE_NAME_RL = "projektierung.rl.frame";
    private static final String ICON_PROJEKTIERUNG = "de/augustakom/hurrican/gui/images/check_out.gif";
    private static final String ICON_PROJEKTIERUNG_RL = "de/augustakom/hurrican/gui/images/check_in.gif";

    private Long abtId = null;
    private boolean showRuecklaeufer = false;

    private boolean showUniversalPanel = false;

    /**
     * @param abtId
     * @param showRuecklaeufer
     */
    public ProjektierungFrame(Long abtId, boolean showRuecklaeufer) {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungFrame.xml");
        this.abtId = abtId;
        this.showRuecklaeufer = showRuecklaeufer;
        if (this.abtId == null) {
            throw new IllegalArgumentException("Dem Projektierungs-Frame muss eine Abteilungs-ID uebergeben werden!");
        }
        createGUI();
    }

    public ProjektierungFrame() {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungFrame.xml");
        showUniversalPanel = true;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected void createGUI() {
        AbstractProjektierungPanel projPnl = null;
        if (showUniversalPanel) {
            setTitle(getSwingFactory().getText("title.universal"));
            projPnl = new ProjektierungUniversalPanel();
            setIcon(ICON_PROJEKTIERUNG);
        }
        else {
            if (!showRuecklaeufer) {
                setTitle(getSwingFactory().getText("title.abteilung." + abtId));
                setIcon(ICON_PROJEKTIERUNG);
            }
            else {
                setTitle(getSwingFactory().getText("title.ruecklaeufer." + abtId));
                setIcon(ICON_PROJEKTIERUNG_RL);
            }

            if (Abteilung.isDispoOrNP(abtId)) {
                if (!showRuecklaeufer) {
                    projPnl = new ProjektierungDispoPanel(abtId);
                }
                else {
                    projPnl = new ProjektierungDispoRLPanel(abtId);
                }
            }
            else if (NumberTools.equal(abtId, Abteilung.ST_VOICE)) {
                projPnl = new ProjektierungStVoicePanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.ST_CONNECT)) {
                projPnl = new ProjektierungStConnectPanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.FIELD_SERVICE)) {
                projPnl = new ProjektierungFieldServicePanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.ST_ONLINE)) {
                projPnl = new ProjektierungStOnlinePanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.AM)) {
                projPnl = new ProjektierungAmRlPanel();
            }
        }

        if (projPnl != null) {
            this.setLayout(new BorderLayout());
            this.add(new AKJScrollPane(projPnl), BorderLayout.CENTER);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJInternalFrame#getUniqueName()
     */
    @Override
    public String getUniqueName() {
        if (!showRuecklaeufer) {
            return UNIQUE_NAME + abtId;
        }
        else {
            return UNIQUE_NAME_RL + abtId;
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#execute(java.lang.String)
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


