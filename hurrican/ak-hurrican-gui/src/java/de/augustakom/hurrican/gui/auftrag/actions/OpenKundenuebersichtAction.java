/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2005 16:50:17
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import java.awt.event.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.auftrag.AuftragUebersichtFrame;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.shared.iface.KundenModel;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * Action, um eine Kundenuebersicht zu oeffnen.
 *
 *
 */
public class OpenKundenuebersichtAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(OpenKundenuebersichtAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object value = getValue(OBJECT_4_ACTION);
        if (value instanceof KundenModel) {
            try {
                KundeQuery query = new KundeQuery();
                query.setKundeNo(((KundenModel) value).getKundeNo());

                KundenService service = getBillingService(
                        KundenService.class.getName(), KundenService.class);
                List<KundeAdresseView> result = service.findKundeAdresseViewsByQuery(query);
                if (result != null && result.size() == 1) {
                    AuftragUebersichtFrame.showAuftragUebersicht(result.get(0));
                }
                else {
                    throw new HurricanGUIException("Kundendaten konnten nicht ermittelt werden!");
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

}


