/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2007 09:09:41
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Command-Klasse prueft, ob die Portierungsdaten (kommend) in den Rufnummern des Auftrags gesetzt sind.
 *
 *
 */
@Component("de.augustakom.hurrican.service.cc.impl.command.verlauf.CheckDNPortierungCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckDNPortierungCommand extends AbstractVerlaufCheckCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckDNPortierungCommand.class);

    private AuftragDaten auftragDaten = null;
    private BAuftrag billingAuftrag = null;

    @Override
    public Object execute() throws Exception {
        try {
            loadRequiredData();
            if (auftragDaten.getAuftragNoOrig() == null) { // Bug-ID: 38
                throw new FindException("Der Auftrag ist keinem Taifun-Auftrag zugeordnet. Rufnummern koennen " +
                        "deshalb nicht ueberprueft werden.");
            }

            if (billingAuftrag == null) {
                throw new FindException("Billing-Auftrag konnte nicht ermittelt werden!");
            }

            List<Rufnummer> rufnummern = getRufnummern();

            if (BooleanTools.nullToFalse(getProdukt().needsDn()) && CollectionTools.isEmpty(rufnummern)) {
                throw new FindException("Es konnten keine Rufnummern im Taifun ermittelt werden.");
            }

            // Portierungsdaten (ankommend) pruefen
            for (Rufnummer rn : rufnummern) {
                if (rn.getRealDate() == null) {
                    throw new FindException("Portierungstermin der Rufnummer "
                            + rn.getDnBase() + " ist nicht gefüllt.");
                }

                if (rn.getPortierungVon() != null) {
                    Long portierungsart = rn.getPortingTimeFrame();

                    // Portierungsart als Result der CommandChain uebergeben
                    getServiceCommandChain().addCommandResult(RESULT_KEY_PORTIERUNGSART, portierungsart);
                }
            }
        }
        catch (FindException e) {
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    e.getMessage(), getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Bei der Überprüfung der Rufnummerndaten ist ein Fehler aufgetreten.", getClass());
        }

        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
    }

    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#loadRequiredData()
     */
    @Override
    protected void loadRequiredData() throws FindException {
        auftragDaten = getAuftragDatenTx(getAuftragId());
        billingAuftrag = getBillingAuftrag();
    }

}


