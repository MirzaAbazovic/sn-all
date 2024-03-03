/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.11.2004 08:27:50
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.service.cc.BAService;


/**
 * Action, um eine Projektierung fuer den aktuellen Auftrag zu erstellen.
 *
 *
 *
 */
public class CreateProjektierungAction extends CreateProjektierungBaseAction {

    private static final Logger LOGGER = Logger.getLogger(CreateProjektierungAction.class);

    @Override
    protected void createProjektierung() {
        try {
            if (auftragTechnik.getNiederlassungId() == null) {
                MessageHelper.showErrorDialog(getMainFrame(),
                        new Exception("Bitte definieren Sie die zustaendige Niederlassung fuer den Auftrag."));
                return;
            }

            DialogResultSet result = showDialog();
            if (result != null) {
                BAService baService = getCCService(BAService.class);
                baService.createProjektierung(auftragTechnik.getAuftragId(), result.auftragIdAlt,
                        HurricanSystemRegistry.instance().getSessionId(), result.subAuftragsIds);
                MessageHelper.showMessageDialog(getMainFrame(), "Projektierung wurde erstellt.", "Abgeschlossen", JOptionPane.INFORMATION_MESSAGE);
                refreshFrame();
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(getMainFrame(), ex);
        }
    }
}


