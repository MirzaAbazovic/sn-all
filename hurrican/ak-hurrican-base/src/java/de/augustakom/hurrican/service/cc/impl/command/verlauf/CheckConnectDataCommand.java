/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.03.2008 15:39:07
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragConnect;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Command-Klasse prueft, ob im Billing Auftragszusatz 'CONNECT' die Endstellen und die Verbindungsbezeichnung M-net
 * eingetragen sind. <br>
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckConnectDataCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckConnectDataCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckConnectDataCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    public Object execute() throws Exception {
        try {
            BAuftrag ba = getBillingAuftrag();
            if (ba != null) {
                BillingAuftragService bas = (BillingAuftragService) getBillingService(BillingAuftragService.class);
                BAuftragConnect connect = bas.findAuftragConnectByAuftragNo(ba.getAuftragNo());
                if (connect != null) {
                    PhysikService ps = (PhysikService) getCCService(PhysikService.class);
                    VerbindungsBezeichnung verbindungsBezeichnung = ps.findVerbindungsBezeichnungByAuftragId(getAuftragId());

                    if (verbindungsBezeichnung == null) {
                        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                                "Der Auftrag hat keine Verbinungsnummer in Hurrican.",
                                getClass());
                    }

                    String lbzBilling = StringUtils.trimToNull(connect.getLbzMnet());
                    if (StringUtils.isBlank(lbzBilling)) {
                        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                                "Die Verbindungsbezeichnung fehlt im Billing-System.",
                                getClass());
                    }
                }
                else {
                    throw new Exception("Connect-Zusatz konnte im Billing-System nicht ermittelt werden!");
                }
            }
            else {
                throw new Exception("Billing-Auftrag konnte nicht ermittelt werden!");
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Fehler beim Ermitteln der Connect-Daten: " + e.getMessage(), getClass());
        }
    }

}


