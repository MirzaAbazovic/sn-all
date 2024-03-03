/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.07.2005 08:58:21
 */
package de.augustakom.hurrican.gui.tools.physik.actions;

import java.awt.event.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.physik.RevokeOrderActionDialog;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.temp.RevokeCreationModel;


/**
 * Action, um den Dialog fuer das Rückgängig machen einer Inbetriebnahme zu oeffnen.
 *
 *
 */
public class RevokeCreationAction extends AbstractRevokeAction {

    private static final Logger LOGGER = Logger.getLogger(RevokeCreationAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        RevokeCreationModel model = new RevokeCreationModel();

        try {
            writeDataFromOrderToModel(model);

            RevokeOrderActionDialog dialog = new RevokeOrderActionDialog(model);
            DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), dialog, true, true);

            refreshFrame();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }

    /*
     * Ermittelt die Auftrags-Id des aktuell geöffneten Auftrags
     */
    public void writeDataFromOrderToModel(RevokeCreationModel model) {
        AuftragDaten auftragDaten = findActiveAuftragDaten();
        if (auftragDaten != null) {
            model.setAuftragId(auftragDaten.getAuftragId());
        }
    }

}
