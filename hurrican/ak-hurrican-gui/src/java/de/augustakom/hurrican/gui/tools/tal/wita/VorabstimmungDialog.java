/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2011 17:55:46
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungAbgebend;
import de.mnet.wita.service.WitaVorabstimmungService;

/**
 *
 */
public class VorabstimmungDialog extends AbstractServiceOptionDialog {

    private static final long serialVersionUID = -5237120180919500024L;

    private static final Logger LOGGER = Logger.getLogger(VorabstimmungDialog.class);

    private static final String TITLE = "title";
    private static final String SUB_TITLE_VORABSTIMMUNG_ABGEBEND = "subtitle.vorabstimmung.abgebend";
    private static final String SUB_TITLE_VORABSTIMMUNG_AUFNEHMEND = "subtitle.vorabstimmung.aufnehmend";
    private final AuftragDaten auftragDaten;
    private final Endstelle endstelle;
    private VorabstimmungAufnehmendPanel vorabstimmungAufnehmendPnl;
    private VorabstimmungAbgebendPanel vorabstimmungAbgebendPnl;
    private final AKJTabbedPane vorabstimmungTabbedPane = new AKJTabbedPane();
    private WitaVorabstimmungService witaVorabstimmungService;

    public VorabstimmungDialog(Endstelle endstelle, AuftragDaten auftragDaten) {
        super("de/augustakom/hurrican/gui/tools/tal/wita/resources/VorabstimmungDialog.xml");
        this.endstelle = endstelle;
        this.auftragDaten = auftragDaten;
        try {
            createGUI();
            initServices();
            loadData();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }

    private void initServices() throws ServiceNotFoundException {
        witaVorabstimmungService = getCCService(WitaVorabstimmungService.class);
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText(TITLE));
        configureButton(CMD_SAVE, "Speichern", "Speichert die Vorabstimmungsdaten", true, true);

        vorabstimmungAufnehmendPnl = new VorabstimmungAufnehmendPanel(endstelle, auftragDaten);
        vorabstimmungAbgebendPnl = new VorabstimmungAbgebendPanel(endstelle, auftragDaten);
        vorabstimmungTabbedPane.addTab(getSwingFactory().getText(SUB_TITLE_VORABSTIMMUNG_AUFNEHMEND),
                vorabstimmungAufnehmendPnl);
        vorabstimmungTabbedPane.addTab(getSwingFactory().getText(SUB_TITLE_VORABSTIMMUNG_ABGEBEND),
                vorabstimmungAbgebendPnl);

        // @formatter:off
        AKJPanel child = getChildPanel();
        child.setLayout(new BorderLayout());
        child.add(vorabstimmungTabbedPane   , BorderLayout.CENTER);
        child.add(new AKJPanel()            , BorderLayout.SOUTH);
        // @formatter:on
    }

    @Override
    public void update(Observable o, Object arg) {
        // not needed
    }

    @Override
    protected void doSave() {
        if (vorabstimmungAufnehmendPnl.saveVorabstimmung() && vorabstimmungAbgebendPnl.saveVorabstimmung()) {
            prepare4Close();
            setValue(null);
        }
    }

    @Override
    protected void execute(String command) {
        // not used
    }

    private void loadData() {
        try {
            Vorabstimmung cbPv = witaVorabstimmungService.findVorabstimmung(endstelle, auftragDaten);
            vorabstimmungAufnehmendPnl.setModel(cbPv);
            VorabstimmungAbgebend vorabstimmungAbgebend = witaVorabstimmungService.findVorabstimmungAbgebend(
                    endstelle.getEndstelleTyp(),
                    auftragDaten.getAuftragId());
            vorabstimmungAbgebendPnl.setModel(vorabstimmungAbgebend);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
        }
    }
}
