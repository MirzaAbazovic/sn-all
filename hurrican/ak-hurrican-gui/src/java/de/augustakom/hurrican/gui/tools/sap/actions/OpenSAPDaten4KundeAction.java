/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2007 15:14:42
 */
package de.augustakom.hurrican.gui.tools.sap.actions;


import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceAction;
import de.augustakom.hurrican.gui.tools.sap.ChooseSAPDebitorNoDialog;
import de.augustakom.hurrican.gui.tools.sap.SAPDatenFrame;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.RInfo;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.model.shared.view.RInfoAdresseView;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.billing.RechnungsService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;


/**
 * Action, um die SAP-Daten eines Kunden anzuzeigen, eventl. vorher Auswahl der SAP-Debitorennummer.
 *
 *
 */
public class OpenSAPDaten4KundeAction extends AbstractServiceAction {

    private static final Logger LOGGER = Logger.getLogger(OpenSAPDaten4KundeAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        AuftragDatenView auftragDaten = null;
        Kunde kunde = null;
        String debNo = null;

        try {
            // Ermittle aktuellen Auftrag/Kunde
            if (getValue(OBJECT_4_ACTION) instanceof RInfoAdresseView) {
                debNo = ((RInfoAdresseView) getValue(OBJECT_4_ACTION)).getExtDebitorNo();
            }
            else if (getValue(OBJECT_4_ACTION) instanceof KundeAdresseView) {
                Long kundeNo = ((KundeAdresseView) getValue(OBJECT_4_ACTION)).getKundeNo();
                KundenService ks = BillingServiceFinder.instance().getBillingService(KundenService.class);
                kunde = ks.findKunde(kundeNo);
            }
            else if (getValue(OBJECT_4_ACTION) instanceof AuftragDatenView) {
                auftragDaten = (AuftragDatenView) getValue(OBJECT_4_ACTION);
            }

            // Prüfe Parameter
            if ((kunde == null) && (auftragDaten == null) && (debNo == null)) {
                LOGGER.error("Erforderliche Parameter konnten nicht ermittelt werden");
                MessageHelper.showMessageDialog(HurricanSystemRegistry.instance().getMainFrame(),
                        "Der Frame kann nicht angezeigt werden, da die\n" +
                                "erforderlichen Daten nicht ermittelt werden konnten!", "Abbruch", JOptionPane.WARNING_MESSAGE
                );
            }
            else {
                RechnungsService reService = BillingServiceFinder.instance().getBillingService(RechnungsService.class);

                // Zeige Frame mit SAP-Daten
                if (StringUtils.isNotBlank(debNo)) {
                    SAPDatenFrame.showSAPDaten(debNo);
                }
                else if (auftragDaten != null) {
                    // Ermittle zu Auftrag die aktuelle Rinfo
                    BillingAuftragService bService = BillingServiceFinder.instance().getBillingService(BillingAuftragService.class);
                    BAuftrag auftrag = bService.findAuftrag(auftragDaten.getAuftragNoOrig());

                    if (auftrag != null) {
                        // Bisher wurde die RInfo mit HistStatus=AKT verwendet
                        RInfo rinfo = reService.findRInfo(auftrag.getRechInfoNoOrig());
                        if (rinfo != null) {
                            debNo = rinfo.getExtDebitorId();
                            if (StringUtils.isNotBlank(debNo)) {
                                SAPDatenFrame.showSAPDaten(debNo);
                            }
                        }
                    }
                }
                else {
                    // Ermittle R-Infos zu Kunden
                    List<RInfo> list = reService.findRInfos4KundeNo(kunde.getKundeNo());
                    if (CollectionTools.isEmpty(list)) {
                        MessageHelper.showMessageDialog(HurricanSystemRegistry.instance().getMainFrame(),
                                "Konnte keine R-Info ermitteln!", "Abbruch", JOptionPane.WARNING_MESSAGE);
                    }
                    // Falls nur eine R-Info, zeige direkt SAP-Daten
                    else if (list.size() == 1) {
                        debNo = (list.get(0) != null) ? list.get(0).getExtDebitorId() : null;
                        if (StringUtils.isNotBlank(debNo)) {
                            SAPDatenFrame.showSAPDaten(debNo);
                        }
                    }
                    // Bei mehreren R-Infos, zeige Auswahldialog
                    else {
                        ChooseSAPDebitorNoDialog dialog = new ChooseSAPDebitorNoDialog(kunde.getKundeNo());
                        DialogHelper.showDialog(getMainFrame(), dialog, true, true);
                    }
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
        }
    }

}


