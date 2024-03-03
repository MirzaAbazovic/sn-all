/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 17:11:41
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.*;
import java.util.List;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.tal.CBVorgangNiederlassung;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Dialog zum Senden eines elektronischen Stornos über die WITA-Schnittstelle.
 */
public final class StornoDialog extends BaseAenderungsKennzeichenDialog {

    private static final long serialVersionUID = 249073991769073719L;
    private static final Logger LOGGER = Logger.getLogger(StornoDialog.class);

    private Collection<WitaCBVorgang> stornierteCBVorgaenge;
    private final List<WitaCBVorgang> cbVorgaenge;

    public static StornoDialog forCbPanel(AuftragDaten auftragDaten, List<WitaCBVorgang> cbVorgaenge) {
        StornoDialog stornoDialog = new StornoDialog(auftragDaten, cbVorgaenge);
        stornoDialog.initGui(null);
        return stornoDialog;
    }

    public static StornoDialog forTamVorgang(TamVorgang tamVorgang) {
        StornoDialog stornoDialog = new StornoDialog(tamVorgang.getCbVorgang());
        stornoDialog.initGui(tamVorgang.getCbVorgang().getAuftragId());
        return stornoDialog;
    }

    public static StornoDialog forCbVorgangNiederlassung(CBVorgangNiederlassung cbVorgangNiederlassung) {
        CBVorgang cbVorgang = cbVorgangNiederlassung.getCbVorgang();
        if (cbVorgang instanceof WitaCBVorgang) {
            StornoDialog stornoDialog = new StornoDialog((WitaCBVorgang) cbVorgang);
            stornoDialog.initGui(cbVorgangNiederlassung.getAuftragId());
            return stornoDialog;
        }
        throw new IllegalArgumentException("WitaCbVorgang expected");
    }

    private StornoDialog(AuftragDaten auftragDaten, List<WitaCBVorgang> cbVorgaenge) {
        super(null, auftragDaten);
        this.cbVorgaenge = cbVorgaenge;
    }

    private StornoDialog(WitaCBVorgang cbVorgang) {
        super(null);
        this.cbVorgaenge = ImmutableList.of(cbVorgang);
    }

    @Override
    protected void doSave() {
        try {
            Set<Long> selectedCbVorgaenge = subOrderPanel.getSelectedCbVorgaenge();
            if (selectedCbVorgaenge.isEmpty()) {
                MessageHelper.showInfoDialog(getMainFrame(), "Es muss mindestens ein Vorgang ausgewählt werden.");
                return;
            }
            stornierteCBVorgaenge = witaTalOrderService.doStorno(selectedCbVorgaenge, HurricanSystemRegistry.instance()
                    .getCurrentUser());
            List<Long> stornierteNichtGesendeteAuftraege = getAuftragIdsOfChangedNotSentRequests(stornierteCBVorgaenge);
            if (stornierteNichtGesendeteAuftraege.isEmpty()) {
                MessageHelper.showInfoDialog(this, "Die Stornierung(en) wurde(n) erfolgreich erzeugt "
                        + "und wird/werden demnächst an die Schnittstelle übermittelt.");
            }
            else {
                MessageHelper
                        .showInfoDialog(
                                this,
                                String.format(
                                        "Folgende Aufträge konnten sofort storniert werden, da diese noch nicht an die Telekom gesendet wurden: %s"
                                                + ". Für alle anderen Aufträge (sofern vorhanden) wurde die Stornierung erfolgreich"
                                                + " erzeugt und wird demnächst an die Schnittstelle übermittelt.",
                                        Joiner.on(",").join(stornierteNichtGesendeteAuftraege)
                                )
                        );
            }
            closeDialog();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), e);
            super.closeDialog();
        }
    }

    @Override
    protected final void createGUI() {
        setTitle("Stornierung senden");
        configureButton(CMD_SAVE, "Senden", "Sendet die Stornierung an die DTAG", true, true);

        subOrderPanel = new TalSubOrderPanel();

        AKJPanel child = getChildPanel();

        child.setLayout(new GridBagLayout());
        int actline = 0;
        // @formatter:off
        child.add(new AKJPanel(), GBCFactory.createGBC(0,   0, 0, actline++, 1, 1, GridBagConstraints.NONE));
        child.add(subOrderPanel,  GBCFactory.createGBC(0,  75, 2, actline++, 1, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 3, actline++, 1, 1, GridBagConstraints.NONE));
        // @formatter:on
    }

    @Override
    protected SubOrderWorker getSubOrderWorker(final AuftragDaten auftragDaten, AKJButton sendButton) {
        return new SubOrderWorker(sendButton) {

            @Override
            protected List<TalSubOrder> doInBackground() throws Exception {
                return talQueryService.findPossibleSubOrdersForStorno(cbVorgaenge, auftragDaten);
            }
        };
    }

    @Override
    protected void closeDialog() {
        super.closeDialog();
        setValue(stornierteCBVorgaenge);
    }

    @Override
    protected List<WitaCBVorgang> getSelectedSubOrders() {
        return cbVorgaenge;
    }

}
