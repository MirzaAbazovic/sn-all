/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.09.2011 12:11:39
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.TV_60_TAGE;

import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.CBVorgangSelectionPanel;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Dialog zur Auswahl zwischen verschiedenen CbVorgaengen eines Auftrags (Wird zur Zeit benutzt damit der Benutzer bei
 * Storno oder Terminverschiebung konkret zwischen verschiedenen CBVorgaengen unterscheiden kann zb. Neubestellung oder
 * REX-MK)
 */
public class CbVorgangSelectorDialog extends AbstractServiceOptionDialog {

    private static final long serialVersionUID = -7744399730453387520L;
    public static final String CB_VORGANG_SELECTION_STORNO = "Storno";
    public static final String CB_VORGANG_SELECTION_TV = "Terminverschiebung";

    // GUI-Elemente
    CBVorgangSelectionPanel cbVorgangSelectionPanel;
    private AKJDateComponent dcVorgabeMnet;

    private final List<WitaCBVorgang> cbVorgaenge;
    private final String action;

    public CbVorgangSelectorDialog(List<WitaCBVorgang> cbVorgaenge, String action) {
        super("de/augustakom/hurrican/gui/tools/tal/wita/resources/CbVorgangSelectorDialog.xml");
        this.cbVorgaenge = cbVorgaenge;
        this.action = action;
        createGUI();
    }

    @Override
    protected final void createGUI() {
        setTitle("Vorgang für " + action);
        configureButton(CMD_SAVE, action + " ausführen", "Führt die " + action + " für den selektierten Vorgang aus",
                true, true);

        cbVorgangSelectionPanel = new CBVorgangSelectionPanel(cbVorgaenge);

        AKJPanel child = getChildPanel();

        child.setLayout(new GridBagLayout());
        int actline = 0;
        // @formatter:off
        child.add(new AKJPanel(),               GBCFactory.createGBC(0,   0, 0, actline++, 1, 1, GridBagConstraints.NONE));
        if (action.equals(CB_VORGANG_SELECTION_TV)) {
            AKJLabel lblVorgabeMnet = getSwingFactory().createLabel("vorgabe.mnet");
            dcVorgabeMnet = getSwingFactory().createDateComponent("vorgabe.mnet");

            child.add(lblVorgabeMnet,           GBCFactory.createGBC(0,   0, 2, actline, 1, 1, GridBagConstraints.HORIZONTAL));
            child.add(dcVorgabeMnet,            GBCFactory.createGBC(0,   0, 3, actline++, 2, 1, GridBagConstraints.HORIZONTAL));
        }
        child.add(cbVorgangSelectionPanel,  GBCFactory.createGBC(0,   75,2, actline++, 3, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(),           GBCFactory.createGBC(100, 0, 3, actline++, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
    }

    @Override
    protected void doSave() {
        Set<Long> cbVorgangIds = cbVorgangSelectionPanel.getSelectedCbVorgangIds();

        if ((cbVorgangIds == null) || cbVorgangIds.isEmpty()) {
            MessageHelper.showErrorDialog(getMainFrame(), new Exception(
                    "Bitte wählen Sie einen Vorgang aus!"));
            return;
        }

        try {
            WitaTalOrderService witaTalOrderService = getCCService(WitaTalOrderService.class);
            MwfEntityService mwfEntityService = getCCService(MwfEntityService.class);
            if (action.equals(CB_VORGANG_SELECTION_STORNO)) {
                Collection<WitaCBVorgang> stornierteCBVorgaenge = witaTalOrderService.doStorno(cbVorgangIds, HurricanSystemRegistry.instance().getCurrentUser());
                List<Long> stornierteNichtGesendeteAuftraege = new ArrayList<>();
                String message = "Folgende Aufträge wurden storniert, indem sie gar nicht an die Telekom geschickt werden: ";
                if (!stornierteCBVorgaenge.isEmpty()) {
                    for (WitaCBVorgang vorgang : stornierteCBVorgaenge) {
                        MnetWitaRequest mnetWitaRequet = mwfEntityService.findUnsentRequest(vorgang.getId());
                        if (mnetWitaRequet != null) {
                            stornierteNichtGesendeteAuftraege.add(vorgang.getAuftragId());
                            message = message.concat(Long.toString(vorgang.getAuftragId())).concat(", ");
                        }
                    }
                }
                if (!stornierteNichtGesendeteAuftraege.isEmpty()) {
                    MessageHelper
                            .showInfoDialog(
                                    this,
                                    message.substring(0, message.length() - 2)
                                            + ". Die Stornierung wurde erfolgreich erzeugt."
                            );
                }
                else {
                    MessageHelper
                            .showInfoDialog(this,
                                    "Die Stornierung wurde erfolgreich erzeugt und wird demnächst an die Schnittstelle übermittelt.");
                }
            }
            else if (action.equals(CB_VORGANG_SELECTION_TV)) {
                Date vorgabeDatum = dcVorgabeMnet.getDate(null);
                if (vorgabeDatum == null) {
                    MessageHelper.showErrorDialog(getMainFrame(), new Exception(
                            "Bitte wählen Sie ein Vorgabedatum für die Terminverschiebung aus!"));
                    return;
                }
                witaTalOrderService.doTerminverschiebung(
                        cbVorgangIds, Instant.ofEpochMilli(vorgabeDatum.getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),
                        HurricanSystemRegistry.instance().getCurrentUser(), true, null, TV_60_TAGE);
                MessageHelper
                        .showInfoDialog(this,
                                "Die Terminverschiebung wurde erfolgreich erzeugt und wird demnächst an die Schnittstelle übermittelt.");
            }
            prepare4Close();
            setValue(null);
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(this, e);
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        // not needed
    }

    @Override
    protected void execute(String command) {
        // not needed
    }

}

