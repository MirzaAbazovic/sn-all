/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2009 10:46:32
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxMandatory;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusAccountData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusData;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


/**
 * Command-Klasse um die notwendigen Radius-Daten fuer IPSec-Auftraege zu ermitteln.
 *
 *
 */
public class CPSGetRadiusIPSecC2SDataCommand extends CPSGetRadiusDataCommand {

    private static final Logger LOGGER = Logger.getLogger(CPSGetRadiusIPSecC2SDataCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    @CcTxMandatory
    public Object execute() throws Exception {
        try {
            // Produktkonfiguration ermitteln
            setTechOrderId(getCPSTransaction().getAuftragId());
            Produkt produkt = getProdukt4Auftrag(getTechOrderId());

            List<IntAccount> accounts = getAccounts();
            if (CollectionTools.isEmpty(accounts)) {
                throw new HurricanServiceCommandException("No accounts for order found!");
            }
            else {
                Date execDate = getCPSTransaction().getEstimatedExecTime();
                CPSRadiusAccountData radiusAcc = new CPSRadiusAccountData();
                radiusAcc.setPortId("TO_BE_DEFINED");

                IntAccount account = accounts.get(0);  // nur ersten Account verwenden!
                defineAccountData(radiusAcc, account);
                defineRealm(radiusAcc, produkt);

                // Account-Typ setzen
                defineAccountType(radiusAcc, produkt);

                // Fixed-IPs laden
                loadIP(radiusAcc, produkt, execDate);

                // VPN laden
                loadVPN(radiusAcc);

                // notwendige Daten pruefen
                checkData(radiusAcc);

                // Radius-Daten an ServiceOrder-Data uebergeben
                CPSRadiusData radius = new CPSRadiusData();
                radius.setRadiusAccount(radiusAcc);
                getServiceOrderData().setRadius(radius);
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK,
                    null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading Radius-Data: " + e.getMessage(), this.getClass());
        }
    }

    /* Prueft, ob die fuer IPSec C2S Auftraege notwendigen Daten gesetzt sind. */
    private void checkData(CPSRadiusAccountData radiusAcc) throws HurricanServiceCommandException {
        if (StringUtils.isBlank(radiusAcc.getUserName())) {
            throw new HurricanServiceCommandException("Account is not defined!");
        }
        else if (StringUtils.isBlank(radiusAcc.getAccountType())) {
            throw new HurricanServiceCommandException("Account-Type is not defined!");
        }
        else if (StringUtils.isBlank(radiusAcc.getVpnId())) {
            throw new HurricanServiceCommandException("VPN ID is not defined!");
        }
        else if (radiusAcc.getIpv4() == null) {
            throw new HurricanServiceCommandException("IP address is not defined!");
        }
    }
}


