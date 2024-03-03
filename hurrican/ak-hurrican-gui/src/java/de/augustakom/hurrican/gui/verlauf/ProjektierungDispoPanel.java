/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2005 11:53:27
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.temp.SelectAbteilung4BAModel;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Panel fuer die Anzeige/Bearbeitungen der Projektierungen fuer DISPO bzw. Netzplanung.
 *
 *
 */
public class ProjektierungDispoPanel extends AbstractProjektierungPanel {

    private static final long serialVersionUID = 3492964977130845365L;
    private static final Logger LOGGER = Logger.getLogger(ProjektierungDispoPanel.class);

    /**
     * Konstruktor mit Angabe der Abteilungs-ID (Dispo oder Netzplanung), deren Projektierungen angezeigt werden
     * sollen.
     *
     * @param abteilungId
     */
    public ProjektierungDispoPanel(Long abteilungId) {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungDispoPanel.xml", abteilungId, false, true);
        createGUI();
        super.loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        String className = getClassName();
        AKJButton btnVerteilen = getSwingFactory().createButton("verteilen", getActionListener());
        btnVerteilen.setParentClassName(className);
        AKJButton btnMoveToRL = getSwingFactory().createButton("move.to.rl", getActionListener());
        btnMoveToRL.setParentClassName(className);
        AKJButton btnEingabe = getSwingFactory().createButton("eingabe", getActionListener());
        btnEingabe.setParentClassName(className);
        AKJButton btnPrint = getSwingFactory().createButton("print", getActionListener());
        AKJButton btnBemerkung = getSwingFactory().createButton("bemerkungen", getActionListener());
        AKJButton btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(btnVerteilen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(btnMoveToRL, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(btnEingabe, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(btnPrint, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(btnBemerkung, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(btnShowPorts, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 6, 1, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnPrint, btnBemerkung, btnShowPorts);
        manageGUI(className, btnVerteilen, btnEingabe, btnMoveToRL);
    }

    @Override
    public String getClassName() {
        if (Abteilung.NP.equals(abteilungId)) {
            return this.getClass().getName() + ".NP";
        }
        return this.getClass().getName();
    }

    /**
     * @see {@link de.augustakom.hurrican.gui.verlauf.AbstractProjektierungPanel#showDetails4Verlauf(java.lang.Long)}
     */
    @Override
    protected void showDetails4Verlauf(Long verlaufId) {
        // intentionally left blank
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
        else if ("verteilen".equals(command)) {
            if (!ensureProjektierungIsSelected()) {
                return;

            }
            if (isVerlaufAtZentraleDispo(getActView().getVerlaufId())) {
                anNetzplanungenVerteilen(getActView());
            }
            else {
                projektierungVerteilen();
            }
        }
        else if ("move.to.rl".equals(command)) {
            moveToRL();
        }
        else if ("eingabe".equals(command)) {
            datenEingabe();
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getActView());
        }
    }

    /**
     * Verteilt die Projektierung an best. Abteilungen.
     */
    private void projektierungVerteilen() {
        if (verteilenErlaubt(getActView())) {
            try {
                setWaitCursor();
                VerlaufAbtAuswahlDialog dlg = new VerlaufAbtAuswahlDialog(false, getActView().getVerlaufId());
                Object value = DialogHelper.showDialog(this, dlg, true, true);
                if (value instanceof List<?>) {
                    if (!((List<?>) value).isEmpty()) {
                        BAService bas = getCCService(BAService.class);
                        List<VerlaufAbteilung> vaVerteilt = bas.dispoVerteilenManuell(
                                getActView().getVerlaufId(), getActView().getVerlaufAbtId(), (List<SelectAbteilung4BAModel>) value,
                                Collections.<Pair<byte[], String>>emptyList(), HurricanSystemRegistry.instance().getSessionId());

                        if ((vaVerteilt == null) || (vaVerteilt.isEmpty())) {
                            throw new HurricanGUIException("Projektierung wurden aus unbekanntem Grund nicht verteilt!");
                        }
                        getActView().setVerlaufAbtStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                        getActView().notifyObservers(true);
                    }
                    else {
                        MessageHelper.showInfoDialog(this,
                                "Projektierung wurden nicht verschickt, da keine Abteilung ausgewählt wurde.", null, true);
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
    }


    /**
     * Verschiebt die aktuelle Projektierung in die Ruecklaeufer.
     */
    private void moveToRL() {
        if (verteilenErlaubt(getActView())) {
            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    getSwingFactory().getText("move.to.rl.msg"), getSwingFactory().getText("move.to.rl.title"));
            if (option == JOptionPane.YES_OPTION) {
                try {
                    AKLoginContext logCtx = (AKLoginContext) HurricanSystemRegistry.instance()
                            .getValue(HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT);
                    if (logCtx != null) {
                        BAService bas = getCCService(BAService.class);
                        Verlauf verlauf = bas.findVerlauf(getActView().getVerlaufId());
                        verlauf.setVerlaufStatusId(VerlaufStatus.RUECKLAEUFER_DISPO);

                        VerlaufAbteilung va = bas.findVerlaufAbteilung(getActView().getVerlaufAbtId());
                        va.setVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                        va.setBearbeiter(logCtx.getUser().getLoginName());

                        bas.saveVerlaufAbteilung(va);
                        bas.saveVerlauf(verlauf);

                        getActView().setVerlaufAbtStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                        getActView().notifyObservers(true);
                    }
                    else {
                        throw new HurricanGUIException("Aktueller Benutzer konnte nicht ermittelt werden!");
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(this, e);
                }
            }
        }
    }


    /**
     * Oeffnet einen Dialog, um best. Daten fuer die Projektierung zu erfassen.
     */
    private void datenEingabe() {
        if (getActView() == null) {
            MessageHelper.showInfoDialog(this, "Bitte wählen Sie zuerst eine Projektierung aus.", null, true);
            return;
        }

        ProjektierungDispoEingabeDialog dlg = new ProjektierungDispoEingabeDialog(getActView());
        DialogHelper.showDialog(this, dlg, true, true);
        getActView().notifyObservers(true);
    }

    private boolean ensureProjektierungIsSelected() {
        if (getActView() == null) {
            MessageHelper.showInfoDialog(this, "Bitte wählen Sie zuerst eine Projektierung aus.", null, true);
            return false;
        }
        return true;
    }

}


