/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2008 11:49:07
 */
package de.augustakom.hurrican.service.cc.impl.command.eg;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragService;


/**
 * Command-Klasse prueft, ob fuer das hinzuzufuegende Endgeraet auch eine entsprechende Auftragsposition im
 * Billing-System vorhanden ist.
 *
 *
 */
@CcTxRequired
public class EGCheckExistingBillPosCommand extends AbstractEGCommand {

    private static final Logger LOGGER = Logger.getLogger(EGCheckExistingBillPosCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            EG eg = getEGToAdd();
            if (eg == null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Zu pruefendes Endgeraet ist nicht angegeben!", getClass());
            }

            if (eg.getExtLeistungNo() != null) {
                // pruefen, ob im Billing-Auftrag eine Leistung mit EXT_LEISTUNG__NO vorhanden ist

                CCAuftragService as = getCCService(CCAuftragService.class);
                AuftragDaten ad = as.findAuftragDatenByAuftragId(getAuftragId());
                if (ad.getAuftragNoOrig() != null) {
                    List<Long> lsIds = new ArrayList<Long>();
                    lsIds.add(eg.getExtLeistungNo());

                    BillingAuftragService bas = getBillingService(BillingAuftragService.class);
                    List<BAuftragPos> positionen =
                            bas.findAuftragPositionen(ad.getAuftragNoOrig(), false, lsIds);

                    if (CollectionTools.isEmpty(positionen)) {
                        StringBuilder sb = new StringBuilder("Endgeraet ");
                        sb.append(eg.getEgName());
                        sb.append(" kann nicht hinzugefuegt werden.\n");
                        sb.append("Im Billing-System fehlt die zugehoerige Leistung!");

                        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                                sb.toString(), getClass());
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "Fehler bei der Ueberpruefung des Endgeraets mit dem Billing-System: " + e.getMessage(), e);
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
    }

}


