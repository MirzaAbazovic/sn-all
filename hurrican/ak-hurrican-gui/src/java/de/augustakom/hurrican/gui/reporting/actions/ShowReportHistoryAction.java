/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.03.2007 12:18:42
 */
package de.augustakom.hurrican.gui.reporting.actions;


import java.awt.event.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.reporting.ReportHistoryFrame;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;


/**
 * Action, um die Report-History anzuzeigen.
 *
 *
 */
public class ShowReportHistoryAction extends AKAbstractAction {

    private static final Logger LOGGER = Logger.getLogger(ShowReportHistoryAction.class);
    private static final long serialVersionUID = 4171681196701903942L;

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        AuftragDaten auftragDaten;
        Kunde kunde;

        try {
            // Ermittle aktuellen Auftrag/Kunde
            auftragDaten = findModelByType(AuftragDaten.class);
            kunde = findModelByType(Kunde.class);
            Object value = getValue(MODEL_OWNER);
            if ((auftragDaten == null)
                    && (kunde == null)
                    && value instanceof AKModelOwner
                    && ((AKModelOwner) value).getModel() instanceof KundeAdresseView) {
                Long kundeNo = ((KundeAdresseView) ((AKModelOwner) value).getModel()).getKundeNo();
                KundenService ks = BillingServiceFinder.instance().getBillingService(KundenService.class);
                kunde = ks.findKunde(kundeNo);
            }

            // Prüfe Parameter
            if ((kunde == null) && (auftragDaten == null)) {
                LOGGER.error("Erforderliche Parameter konnten nicht ermittelt werden");
                MessageHelper.showMessageDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        "Der Frame kann nicht angezeigt werden, da die\n" +
                                "erforderlichen Daten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
                );
            }
            else {
                // Zeige Frame mit ReportHistory
                if (auftragDaten != null) {
                    Auftrag model = new Auftrag();
                    model.setAuftragId(auftragDaten.getAuftragId());
                    ReportHistoryFrame.openFrame(model);
                }
                else {
                    ReportHistoryFrame.openFrame(kunde);
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }

}
