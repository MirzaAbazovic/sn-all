/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.11.2009 11:47:37
 */

package de.augustakom.hurrican.gui.auftrag.actions;

import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.service.cc.BAService;


/**
 *
 */
public class CreateProjektierungForZentraleDispoAction extends CreateProjektierungBaseAction {

    private static final Logger LOGGER = Logger.getLogger(CreateProjektierungForZentraleDispoAction.class);

    @Override
    protected void createProjektierung() {
        try {
            DialogResultSet result = showDialog();
            if (result != null) {
                if (auftragDaten.getVorgabeSCV() == null) {
                    MessageHelper.showErrorDialog(getMainFrame(),
                            new Exception("Bitte definieren Sie das Vorgabedatum fuer den Auftrag."));
                    return;
                }

                BAService baService = getCCService(BAService.class);
                baService.createProjektierungForZentraleDispo(auftragTechnik.getAuftragId(), result.auftragIdAlt,
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
