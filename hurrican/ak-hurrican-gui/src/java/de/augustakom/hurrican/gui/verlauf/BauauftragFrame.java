/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2005 09:46:49
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractInternalServiceFrame;
import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Frame zur Darstellung der offenen Bauauftraege einer Abteilung.
 *
 *
 */
public class BauauftragFrame extends AbstractInternalServiceFrame {

    private final static String UNIQUE_NAME = "bauauftrag.frame";
    private final static String UNIQUE_NAME_RL = "bauauftrag.rl.frame";
    private final static String ICON_BAUAUFTRAG = "de/augustakom/hurrican/gui/images/bauauftrag.gif";
    private final static String ICON_BAUAUFTRAG_RL = "de/augustakom/hurrican/gui/images/bauauftrag_rueck.gif";

    private Long abtId = null;
    private boolean showRuecklaeufer = false;

    private boolean showUniversalBaPanel = false;

    /**
     * Konstruktor mit Angabe der Abteilungs-ID, deren Bauauftraege angzeigt werden soll.
     *
     * @param abtId
     * @throws IllegalArgumentException wenn keine Abteilungs-ID angegeben wird.
     */
    public BauauftragFrame(Long abtId, boolean showRuecklaeufer) {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragFrame.xml");
        this.abtId = abtId;
        this.showRuecklaeufer = showRuecklaeufer;
        if (this.abtId == null) {
            throw new IllegalArgumentException("Dem Verlaufs-Frame muss eine Abteilungs-ID uebergeben werden!");
        }
        createGUI();
    }

    public BauauftragFrame() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragFrame.xml");
        showUniversalBaPanel = true;
        createGUI();
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractInternalFrame#createGUI()
     */
    @Override
    protected final void createGUI() {
        AbstractVerlaufPanel verlPnl = null;
        if (showUniversalBaPanel) {
            setTitle(getSwingFactory().getText("title.universal"));
            verlPnl = new BauauftragUniversalPanel();
        }
        else {
            if (!showRuecklaeufer) {
                setTitle(getSwingFactory().getText("title.abteilung." + abtId));
            }
            else {
                setTitle(getSwingFactory().getText("title.ruecklaeufer." + abtId));
            }

            if (NumberTools.equal(abtId, Abteilung.ST_CONNECT)) {
                verlPnl = new BauauftragStConnectPanel();
            }
            else if (Abteilung.isDispoOrNP(abtId)) {
                if (!showRuecklaeufer) {
                    verlPnl = new BauauftragDISPOPanel(abtId);
                }
                else {
                    verlPnl = new BauauftragDispoRLPanel(abtId);
                }
            }
            else if (NumberTools.equal(abtId, Abteilung.ST_VOICE)) {
                verlPnl = new BauauftragStVoicePanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.FIELD_SERVICE)) {
                verlPnl = new BauauftragFieldServicePanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.AM)) {
                verlPnl = new BauauftragAmRlPanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.ST_ONLINE)) {
                verlPnl = new BauauftragStOnlinePanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.MQUEUE)) {
                verlPnl = new BauauftragMQueuePanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.EXTERN)) {
                verlPnl = new BauauftragEXTPanel();
            }
            else if (NumberTools.equal(abtId, Abteilung.FFM)) {
                verlPnl = new BauauftragFfmPanel();
            }
        }

        if (showRuecklaeufer) {
            setIcon(ICON_BAUAUFTRAG_RL);
        }
        else {
            setIcon(ICON_BAUAUFTRAG);
        }

        if (verlPnl != null) {
            this.setLayout(new BorderLayout());
            this.add(verlPnl, BorderLayout.CENTER);
        }
    }

    @Override
    public String getUniqueName() {
        if (showUniversalBaPanel) {
            return UNIQUE_NAME+"universal";
        }
        else {
            if (!showRuecklaeufer) {
                return UNIQUE_NAME + abtId;
            }
            else {
                return UNIQUE_NAME_RL + abtId;
            }
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

}


