/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2005 11:58:33
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungPredicate;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechPredicate;


/**
 * Panel fuer die Anzeige der Projektierungs-Ruecklaeufer der Abteilung DISPO oder Netzplanung.
 *
 *
 */
public class ProjektierungDispoRLPanel extends AbstractProjektierungPanel {

    private static final Logger LOGGER = Logger.getLogger(ProjektierungDispoRLPanel.class);
    private static final long serialVersionUID = -7583981657654554308L;

    private VerlaufAbtTableModel tbMdlVerlAbt = null;
    private AKJButton btnEingabe = null;
    private AKJButton btnErledigen = null;

    /**
     * Konstruktor mit Angabe der Abteilungs-ID (Dispo oder Netzplanung), deren Projektierungs-Ruecklaeufer angezeigt
     * werden sollen.
     */
    public ProjektierungDispoRLPanel(Long abteilungId) {
        super("de/augustakom/hurrican/gui/verlauf/resources/ProjektierungDispoRLPanel.xml", abteilungId, true, true);
        createGUI();
        super.loadData();
        loadDefaultData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblTable = getSwingFactory().createLabel("table");
        tbMdlVerlAbt = new VerlaufAbtTableModel();
        AKJTable tbVerlaufAbts = new VerlaufAbtTable(tbMdlVerlAbt, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbVerlaufAbts.fitTable(new int[] { 90, 90, 70, 70, 70, 80, 70 });
        AKJScrollPane spTable = new AKJScrollPane(tbVerlaufAbts);
        spTable.setPreferredSize(new Dimension(600, 95));

        String className = getClassName();
        btnEingabe = getSwingFactory().createButton("eingabe", getActionListener());
        btnEingabe.setParentClassName(className);
        btnErledigen = getSwingFactory().createButton("erledigen", getActionListener());
        btnErledigen.setParentClassName(className);
        AKJButton btnPrint = getSwingFactory().createButton("print", getActionListener());
        AKJButton btnBemerkungen = getSwingFactory().createButton("bemerkungen", getActionListener());
        AKJButton btnStorno = getSwingFactory().createButton("stornieren", getActionListener());
        btnStorno.setParentClassName(className);
        AKJButton btnUebernahme = getSwingFactory().createButton("uebernehmen", getActionListener());
        btnUebernahme.setParentClassName(className);
        AKJButton btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnErledigen, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnUebernahme, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnEingabe, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnPrint, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(btnBemerkungen, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.BOTH));
        btnPnl.add(btnStorno, GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.BOTH));
        btnPnl.add(btnShowPorts, GBCFactory.createGBC(0, 0, 6, 0, 1, 1, GridBagConstraints.BOTH));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(lblTable, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 1, 1, 1, 1, GridBagConstraints.VERTICAL));
        child.add(spTable, GBCFactory.createGBC(0, 0, 2, 0, 1, 2, GridBagConstraints.HORIZONTAL));
        child.add(btnPnl, GBCFactory.createGBC(0, 0, 0, 2, 3, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 3, 1, 1, GridBagConstraints.BOTH));

        ManuellVerteilenAction mvAction = new ManuellVerteilenAction("Nachverteilen");
        mvAction.setParentClassName(className);
        getTable().addPopupAction(mvAction);

        manageGUI(btnPrint, btnBemerkungen, btnShowPorts);
        manageGUI(className, btnErledigen, btnEingabe, btnStorno, mvAction);
    }

    @Override
    public String getClassName() {
        if (Abteilung.NP.equals(abteilungId)) {
            return this.getClass().getName() + ".NP";
        }
        return this.getClass().getName();
    }

    /**
     * Laedt die Standard-Daten fuer das Panel.
     */
    private void loadDefaultData() {
        try {
            NiederlassungService nls = getCCService(NiederlassungService.class);
            List<Abteilung> abteilungen = nls.findAbteilungen4Proj();
            tbMdlVerlAbt.setAbteilungen(abteilungen);

            List<Niederlassung> niederlassungen = nls.findNiederlassungen();
            tbMdlVerlAbt.setNiederlassungen(niederlassungen);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.verlauf.AbstractProjektierungPanel#showDetails4Verlauf(java.lang.Integer)
     */
    @Override
    protected void showDetails4Verlauf(Long verlaufId) {
        if (verlaufId != null) {
            boolean enabled = true;
            try {
                BAService bas = getCCService(BAService.class);
                List<VerlaufAbteilung> vas = bas.findVerlaufAbteilungen(verlaufId);
                if (vas != null) {
                    VerlaufAbteilung vaDispo = (VerlaufAbteilung) CollectionUtils.find(
                            vas, new VerlaufAbteilungPredicate(Abteilung.DISPO));
                    if ((vaDispo != null) && NumberTools.equal(vaDispo.getVerlaufStatusId(), VerlaufStatus.STATUS_ERLEDIGT)) {
                        enabled = false;
                    }

                    VerlaufAbteilungTechPredicate predicate = new VerlaufAbteilungTechPredicate(vas, abteilungId, niederlassung.getId());
                    CollectionUtils.filter(vas, predicate);
                    tbMdlVerlAbt.setData(vas);
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                btnErledigen.setEnabled(enabled);
                btnEingabe.setEnabled(enabled);
            }
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
            projektierungErledigen();
        }
        else if ("eingabe".equals(command)) {
            datenEingabe();
        }
        else if ("stornieren".equals((command))) {
            projektierungStornieren();
        }
        else if ("uebernehmen".equals(command)) {
            verlaufUebernehmen();
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getActView());
        }
    }

    /**
     * Setzt die Projektierung auf 'storno'
     */
    private void projektierungStornieren() {
        if (getActView() == null) {
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Bitte wählen Sie zuerst eine Projektierung aus.", null, true);
            return;
        }
        if (NumberTools.equal(getActView().getVerlaufAbtStatusId(), VerlaufStatus.STATUS_ERLEDIGT)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Projektierung ist bereits abgeschlossen!", null, true);
            return;
        }
        try {
            setWaitCursor();
            BAService bas = getCCService(BAService.class);
            bas.verlaufStornieren(getActView().getVerlaufId(),
                    false, HurricanSystemRegistry.instance().getSessionId());

            MessageHelper.showMessageDialog(getMainFrame(), "Projektierung wurde storniert.");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }

    }

    /**
     * Setzt die Projektierung auf 'erledigt'.
     */
    private void projektierungErledigen() {
        if (getActView() == null) {
            MessageHelper.showInfoDialog(getMainFrame(),
                    "Bitte wählen Sie zuerst eine Projektierung aus.", null, true);
            return;
        }

        if (NumberTools.equal(getActView().getVerlaufAbtStatusId(), VerlaufStatus.STATUS_ERLEDIGT)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Projektierung ist bereits abgeschlossen!", null, true);
            return;
        }

        try {
            setWaitCursor();
            BAService bas = getCCService(BAService.class);
            List<VerlaufAbteilung> verlAbts = bas.findVerlaufAbteilungen(getActView().getVerlaufId(), getActView().getVerlaufAbtId());
            List<Long> abtIdsNotFinished = new ArrayList<>();
            for (VerlaufAbteilung va : verlAbts) {
                if (va.getDatumErledigt() == null) {
                    abtIdsNotFinished.add(va.getAbteilungId());
                }
            }

            if (!abtIdsNotFinished.isEmpty()) {
                StringBuilder sb = new StringBuilder("Die Projektierung kann nicht abgeschlossen werden, ");
                sb.append("da noch nicht alle Abteilungen die Projektierung erledigt haben.");
                MessageHelper.showInfoDialog(this, sb.toString(), "Abschluss nicht möglich", null, true);
                return;
            }

            if (getActView().getVorgabeAm() == null) {
                MessageHelper.showInfoDialog(this, "Bitte füllen Sie das Datum <Vorgabe AM>.",
                        "Abschluss nicht möglich", null, true);
                return;
            }

            if ((getEndstellen() != null) && !isHousing() && !isInterneArbeit()) {
                for (Endstelle es : getEndstellen()) {
                    if (es.isEndstelleB() && (es.getAnschlussart() == null)) {
                        MessageHelper.showInfoDialog(this, "Bitte tragen Sie die Anschlussart für die Endstellen ein.",
                                "Abschluss nicht möglich", null, true);
                        return;
                    }
                }
            }

            Boolean amAbschluss = null;
            if ((getActView().getVerlaufAbtId() != null)) {
                VerlaufAbteilung va = bas.findVerlaufAbteilung(getActView().getVerlaufAbtId());
                VerlaufAbteilung parentVa = bas.findVerlaufAbteilung(va.getParentVerlaufAbteilungId());
                // Entweder alt/ohne Parent ODER weder bei NP -> Zentrale Dispo noch bei Zentrale Dispo -> AM
                if ((parentVa == null) ||
                        (Abteilung.AM.equals(parentVa.getAbteilungId()) &&
                                !(Abteilung.DISPO.equals(va.getAbteilungId()) && Niederlassung.ID_ZENTRAL.equals(va.getNiederlassungId())))) {
                    amAbschluss = askForAmAbschluss();
                }
            }
            else if (NumberTools.equal(abteilungId, Abteilung.NP)) {
                amAbschluss = askForAmAbschluss();
            }

            bas.dispoVerlaufAbschluss(getActView().getVerlaufId(), getActView().getVerlaufAbtId(), null,
                    HurricanSystemRegistry.instance().getSessionId(), (amAbschluss != null) ? !amAbschluss : null);
            getActView().setVerlaufAbtStatusId(VerlaufStatus.STATUS_ERLEDIGT);
            getActView().notifyObservers(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Ueberprueft, ob es sich bei dem Produkt um ein Housing-Produkt handelt. */
    private boolean isHousing() {
        try {
            ProjektierungsView prView = getActView();
            ProduktService produktService = getCCService(ProduktService.class);
            Produkt produkt = produktService.findProdukt(prView.getProduktId());
            if (NumberTools.equal(produkt.getProduktGruppeId(), ProduktGruppe.HOUSING)) {
                return true;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return false;
    }


    /* Ueberprueft, ob es sich bei dem Produkt um eine interne Arbeit handelt. */
    private boolean isInterneArbeit() {
        try {
            ProjektierungsView prView = getActView();
            ProduktService produktService = getCCService(ProduktService.class);
            Produkt produkt = produktService.findProdukt(prView.getProduktId());
            if (NumberTools.equal(produkt.getProduktGruppeId(), ProduktGruppe.AK_INTERN_WORK)) {
                return true;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return false;
    }


    /**
     * Zeigt einen Dialog, der den Nutzer fragt, oder der AM Abschluss gleich erledigt werden soll
     */
    private boolean askForAmAbschluss() {
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.rl.am.msg"), getSwingFactory().getText("ask.rl.am.title"));
        return option != JOptionPane.YES_OPTION;
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

    /**
     * Ordnet den ausgewaehlten Verlauf dem aktuellen Benutzer zu.
     */
    private void verlaufUebernehmen() {
        int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                getSwingFactory().getText("ask.uebernehmen.msg"), getSwingFactory().getText("ask.uebernehmen.title"));
        if (option == JOptionPane.YES_OPTION) {
            super.projektierungUebernehmen();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.verlauf.AbstractProjektierungPanel#clear()
     */
    @Override
    protected void clear() {
        super.clear();
        if (tbMdlVerlAbt != null) {
            tbMdlVerlAbt.setData(null);
        }
    }
}


