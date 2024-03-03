/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.10.2006 13:17:52
 */
package de.augustakom.hurrican.gui.tools.physik;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.iface.IServiceCallback;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Dialog, um eine Anschlussuebernahme manuell durchzufuehren. <br> Dies ist dann notwendig, wenn es sich bei dem
 * Anschluss um einen Direktanschluss handelt. Grund: die Ermittlung der moeglichen Auftraege fuer eine
 * Anschlussuebernahme erfolgt ueber die Strassen-ID. Bei Direktanschluessen ist die Strasse jedoch nicht gefuellt.
 *
 *
 */
public class AnschlussuebernahmeManuellDialog extends AbstractServiceOptionDialog implements IServiceCallback {

    private static final Logger LOGGER = Logger.getLogger(AnschlussuebernahmeManuellDialog.class);

    private AKJFormattedTextField tfOldAuftrag = null;
    private AKJFormattedTextField tfNewAuftrag = null;

    /**
     * Default-Const.
     */
    public AnschlussuebernahmeManuellDialog() {
        super("de/augustakom/hurrican/gui/tools/physik/resources/AnschlussuebernahmeManuellDialog.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, "Übernehmen", "Führt eine Anschlussübernahme durch", true, true);

        AKJLabel lblOldAuftrag = getSwingFactory().createLabel("auftrags.id.old");
        AKJLabel lblNewAuftrag = getSwingFactory().createLabel("auftrags.id.new");

        tfOldAuftrag = getSwingFactory().createFormattedTextField("auftrags.id.old");
        tfNewAuftrag = getSwingFactory().createFormattedTextField("auftrags.id.new");

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(lblOldAuftrag, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        child.add(tfOldAuftrag, GBCFactory.createGBC(0, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(lblNewAuftrag, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(tfNewAuftrag, GBCFactory.createGBC(0, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 4, 3, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            Long idOld = tfOldAuftrag.getValueAsLong(null);
            if (!getAuftragDaten(idOld).isInKuendigung()) {
                throw new HurricanGUIException("Ursprungsauftrag befindet sich nicht in Kündigung!");
            }

            Long idNew = tfNewAuftrag.getValueAsLong(null);
            if (NumberTools.isGreater(getAuftragDaten(idNew).getStatusId(), AuftragStatus.ERFASSUNG_SCV)) {
                throw new HurricanGUIException("Auftragsstatus des neuen Auftrags ist zu weit fortgeschritten!");
            }

            String msg = StringTools.formatString(getSwingFactory().getText("uebernahme.question"),
                    new Object[] { "" + idOld, "" + idNew }, null);

            int selection = MessageHelper.showYesNoQuestion(getMainFrame(),
                    msg, getSwingFactory().getText("uebernahme.title"));
            if (selection == JOptionPane.YES_OPTION) {
                RangierungsService rs = getCCService(RangierungsService.class);
                rs.physikAenderung(PhysikaenderungsTyp.STRATEGY_ANSCHLUSSUEBERNAHME,
                        idOld, idNew, this, HurricanSystemRegistry.instance().getSessionId());

                MessageHelper.showMessageDialog(getMainFrame(),
                        "Bitte Auftrag neu laden und kontrollieren (Physik A+B, Verbindungsbezeichnung etc).",
                        "Anschlussübernahme abgeschlossen", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /*
     * Ermittelt die AuftragsDaten zu der Auftrags-ID
     */
    private AuftragDaten getAuftragDaten(Long auftragId) {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragId(auftragId);
            if (ad == null) {
                throw new HurricanGUIException("Auftrag konnte nicht ermittelt werden!");
            }

            return ad;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return null;
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceCallback#doServiceCallback(java.lang.Object, int, java.util.Map)
     */
    @Override
    public Object doServiceCallback(Object source, int callbackAction, Map<String, ?> parameters) {
        if (callbackAction == RangierungsService.CALLBACK_ASK_4_ACCOUNT_UEBERNAHME) {
            int selection = MessageHelper.showYesNoQuestion(getMainFrame(),
                    "Soll der Account des Ursprungsauftrags übernommen werden?",
                    "Account uebernehmen?");
            return (selection == JOptionPane.YES_OPTION) ? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
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


