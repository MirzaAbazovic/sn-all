/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2011 14:01:23
 */
package de.augustakom.hurrican.gui.tools.tal.wita;

import java.awt.*;
import java.util.*;
import java.util.List;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.mnet.wita.model.TalSubOrder;
import de.mnet.wita.model.TamVorgang;
import de.mnet.wita.model.WitaCBVorgang;

/**
 * Dialog zum Senden eines elektronischen Erledigtmeldung über die WITA-Schnittstelle.
 */
public final class ErlmkDialog extends BaseAenderungsKennzeichenDialog {

    private static final long serialVersionUID = 6712152210064346372L;
    private static final Logger LOGGER = Logger.getLogger(ErlmkDialog.class);

    private Collection<WitaCBVorgang> erledigteCBVorgaenge;
    private final TamVorgang tamVorgang;

    public static ErlmkDialog forTamVorgang(TamVorgang tamVorgang) {
        ErlmkDialog dialog = new ErlmkDialog(tamVorgang);
        dialog.initGui(tamVorgang.getCbVorgang().getAuftragId());
        return dialog;
    }

    private ErlmkDialog(TamVorgang tamVorgang) {
        super(null);
        this.tamVorgang = tamVorgang;
    }

    @Override
    protected void doSave() {
        try {
            Set<Long> erledigteCBVorgaengIds = subOrderPanel.getSelectedCbVorgaenge();
            if (erledigteCBVorgaengIds.isEmpty()) {
                MessageHelper.showInfoDialog(getMainFrame(), "Es muss mindestens ein Vorgang ausgewählt werden.");
                return;
            }
            erledigteCBVorgaenge = witaTalOrderService.sendErlmks(erledigteCBVorgaengIds,
                    HurricanSystemRegistry.instance().getCurrentUser());
            MessageHelper.showInfoDialog(this, "Die Erledigtmeldung-Kunde (ERLMK) wurde erfolgreich erzeugt und wird demnächst an die Schnittstelle übermittelt.");
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
        setTitle("Erledigtmeldung senden");
        configureButton(CMD_SAVE, "Senden", "Sendet die Erledigtmeldung an die DTAG", true, true);

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
                return talQueryService.findPossibleSubOrdersForErlmk(
                        tamVorgang.getCbVorgang(),
                        auftragDaten);
            }
        };
    }

    @Override
    protected void closeDialog() {
        super.closeDialog();
        setValue(erledigteCBVorgaenge);
    }

    @Override
    protected List<WitaCBVorgang> getSelectedSubOrders() {
        return ImmutableList.of(tamVorgang.getCbVorgang());
    }

}
