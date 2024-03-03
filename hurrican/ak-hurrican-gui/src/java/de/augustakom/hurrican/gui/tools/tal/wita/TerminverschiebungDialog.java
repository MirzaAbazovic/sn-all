/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.08.2011 16:29:04
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.TV_60_TAGE;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.springframework.transaction.UnexpectedRollbackException;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.exceptions.WitaUnexpectedRollbackException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Dialog zum Senden einer elektronischen Terminverschiebung über die WITA-Schnittstelle.
 */
public final class TerminverschiebungDialog extends BaseAenderungsKennzeichenDialog {

    private static final long serialVersionUID = -1039859335095963260L;
    private static final Logger LOGGER = Logger.getLogger(TerminverschiebungDialog.class);

    // GUI-Elemente
    private AKJDateComponent dcVorgabeMnet;
    private AKJTextArea taMontagehinweis;

    private final boolean completeUsertasks;
    private final Date vorgabeMnetDefault;

    private final boolean montagehinweisBearbeitbar;

    private Collection<WitaCBVorgang> terminverschobeneCBVorgaenge;
    private final List<WitaCBVorgang> cbVorgaenge;
    private String defaultMontagehinweis;
    private TamBearbeitungsStatus tamBearbeitungsStatus = TV_60_TAGE;

    public static TerminverschiebungDialog forCbPanel(AuftragDaten auftragDaten, List<WitaCBVorgang> cbVorgaenge) {
        TerminverschiebungDialog dialog = new TerminverschiebungDialog(auftragDaten, cbVorgaenge, TV_60_TAGE);
        dialog.initGui(null);
        return dialog;
    }

    public static TerminverschiebungDialog forTamVorgang(TamVorgang tamVorgang, boolean completeUsertasks) {
        return forTamVorgang(tamVorgang, completeUsertasks, null, TV_60_TAGE);
    }

    public static TerminverschiebungDialog forTamVorgang(TamVorgang tamVorgang, boolean completeUsertasks,
            Date vorgabeMnetDefault, TamBearbeitungsStatus tamBearbeitungsStatus) {
        TerminverschiebungDialog dialog = new TerminverschiebungDialog(tamVorgang.getCbVorgang(), completeUsertasks,
                vorgabeMnetDefault, tamBearbeitungsStatus);
        dialog.initGui(tamVorgang.getCbVorgang().getAuftragId());
        return dialog;
    }

    private TerminverschiebungDialog(AuftragDaten auftragDaten, List<WitaCBVorgang> cbVorgaenge, TamBearbeitungsStatus tamBearbeitungsStatus) {
        super("de/augustakom/hurrican/gui/tools/tal/wita/resources/TerminverschiebungDialog.xml", auftragDaten);
        this.cbVorgaenge = cbVorgaenge;
        this.tamBearbeitungsStatus = tamBearbeitungsStatus;
        this.completeUsertasks = true;
        this.vorgabeMnetDefault = null;
        this.montagehinweisBearbeitbar = false;
    }

    private TerminverschiebungDialog(WitaCBVorgang cbVorgang, boolean completeUsertasks, Date vorgabeMnetDefault, TamBearbeitungsStatus tamBearbeitungsStatus) {
        super("de/augustakom/hurrican/gui/tools/tal/wita/resources/TerminverschiebungDialog.xml");
        this.cbVorgaenge = ImmutableList.of(cbVorgang);
        this.completeUsertasks = completeUsertasks;
        this.vorgabeMnetDefault = vorgabeMnetDefault;
        this.tamBearbeitungsStatus = tamBearbeitungsStatus;
        this.montagehinweisBearbeitbar = true;

        Auftrag auftrag = mwfEntityService.getAuftragOfCbVorgang(cbVorgang.getId());
        if (auftrag != null) {
            Montageleistung montageleistung = auftrag.getGeschaeftsfall().getAuftragsPosition()
                    .getGeschaeftsfallProdukt().getMontageleistung();
            if (montageleistung != null) {
                defaultMontagehinweis = montageleistung.getMontagehinweis();
            }
        }
    }

    @Override
    protected final void createGUI() {
        setTitle("Terminverschiebung senden");
        configureButton(CMD_SAVE, "Senden", "Sendet die Terminverschiebung an die DTAG", true, true);

        AKJLabel lblVorgabeMnet = getSwingFactory().createLabel("vorgabe.mnet");
        dcVorgabeMnet = getSwingFactory().createDateComponent("vorgabe.mnet");
        AKJLabel lblMontagehinweis = getSwingFactory().createLabel("montagehinweis");
        taMontagehinweis = getSwingFactory().createTextArea("montagehinweis");
        taMontagehinweis.setText(defaultMontagehinweis);

        AKJScrollPane spMontagehinweis = new AKJScrollPane(taMontagehinweis);
        dcVorgabeMnet.setDate(vorgabeMnetDefault);
        subOrderPanel = new TalSubOrderPanel();

        AKJPanel child = getChildPanel();

        child.setLayout(new GridBagLayout());
        int actline = 0;

        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, actline++, 1, 1, GridBagConstraints.NONE));
        child.add(lblVorgabeMnet, GBCFactory.createGBC(0, 0, 2, actline, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(dcVorgabeMnet, GBCFactory.createGBC(0, 0, 3, actline++, 2, 1, GridBagConstraints.HORIZONTAL));
        if (montagehinweisBearbeitbar) {
            child.add(lblMontagehinweis, GBCFactory.createGBC(0, 0, 2, actline, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(spMontagehinweis, GBCFactory.createGBC(0, 0, 3, actline++, 2, 1, GridBagConstraints.HORIZONTAL));
        }
        child.add(subOrderPanel, GBCFactory.createGBC(0, 75, 2, actline++, 3, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, actline++, 1, 1, GridBagConstraints.NONE));
    }

    @Override
    protected void checkIfAuftragCanBeChanged(AuftragDaten auftragDaten) {
        if (!auftragDaten.isTerminverschiebungPossible()) {
            MessageHelper.showErrorDialog(getMainFrame(), new Exception(
                    "Für eine Terminverschiebung darf sich der Auftrag nicht "
                            + "in Realisierung befinden oder storniert oder abgesagt sein."
            ));
            closeDialog();
            return;
        }
    }

    @Override
    protected SubOrderWorker getSubOrderWorker(final AuftragDaten auftragDaten, AKJButton sendButton) {
        return new SubOrderWorker(sendButton) {

            @Override
            protected List<TalSubOrder> doInBackground() throws Exception {
                return talQueryService.findPossibleSubOrdersForTerminverschiebung(cbVorgaenge, auftragDaten);
            }
        };
    }

    @Override
    protected void doSave() {
        try {
            Date vorgabeDatum = dcVorgabeMnet.getDate(null);
            if (vorgabeDatum == null) {
                MessageHelper.showErrorDialog(getMainFrame(), new Exception(
                        "Bitte wählen Sie ein Vorgabedatum für die Terminverschiebung aus!"));
                return;
            }
            String montagehinweis = taMontagehinweis.getText(null);
            Set<Long> selectedCbVorgaenge = subOrderPanel.getSelectedCbVorgaenge();
            if (selectedCbVorgaenge.isEmpty()) {
                MessageHelper.showInfoDialog(getMainFrame(), "Es muss mindestens ein Vorgang ausgewählt werden.");
                return;
            }
            terminverschobeneCBVorgaenge = witaTalOrderService.doTerminverschiebung(selectedCbVorgaenge,
                    Instant.ofEpochMilli(vorgabeDatum.getTime()).atZone(ZoneId.systemDefault()).toLocalDate(), HurricanSystemRegistry.instance().getCurrentUser(),
                    completeUsertasks, montagehinweis, tamBearbeitungsStatus);
            List<Long> changedNotSentRequests = getAuftragIdsOfChangedNotSentRequests(terminverschobeneCBVorgaenge);
            if (changedNotSentRequests.isEmpty()) {
                MessageHelper.showInfoDialog(this, "Die Terminverschiebung(en) wurde(n) erfolgreich erzeugt "
                        + "und wird/werden demnächst an die Schnittstelle übermittelt.");
            }
            else {
                MessageHelper
                        .showInfoDialog(
                                this,
                                String.format(
                                        "Die Terminverschiebung(en) wurde(n) erfolgreich ausgelöst.%n"
                                                + "Für die Aufträge %s wurde ein Ursprungsauftrag bzw. eine Terminverschiebung zu der/den gerade "
                                                + "ausgelösten Terminverschiebung(en) wurde(n) noch nicht an die Telekom gesendet.%n"
                                                + "Für diese(n) Ursprungsauftrag/-aufträge bzw. Terminverschiebung(en) konnte der neue "
                                                + "Kundenwunschtermin ohne tatsächliche Terminverschiebung an DTAG geändert werden.",
                                        Joiner.on(",").join(changedNotSentRequests)
                                )
                        );
            }

            closeDialog();
        }
        catch (UnexpectedRollbackException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(null, new WitaUnexpectedRollbackException(e));
            super.closeDialog();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            super.closeDialog();
        }
    }

    @Override
    protected void closeDialog() {
        super.closeDialog();
        setValue(terminverschobeneCBVorgaenge);
    }

    @Override
    protected List<WitaCBVorgang> getSelectedSubOrders() {
        return cbVorgaenge;
    }

}
