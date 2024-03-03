/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2007 13:37:09
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


/**
 * Command-Klasse, um die notwendigen Auftragsdaten fuer eine VoIP-Leistung anzulegen, speziell fuer
 * Maxi-Deluxe-Produkte. <br> Es wird keine Benachrichtigung erzeugt. Das Command ermittelt folgende Daten: <ul>
 * <li>IP-Adresse fuer das IAD aus einem IP-Pool <li>Passworte fuer die Rufnummern </ul>
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.CreateVoIPDeluxeCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateVoIPDeluxeCommand extends AbstractVoIPCommand {

    private static final Logger LOGGER = Logger.getLogger(CreateVoIPDeluxeCommand.class);


    /**
     * @see de.augustakom.hurrican.service.cc.impl.command.AbstractServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            createAuftragVoIPDN();
            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(
                    "VoIP-Zusatz fuer den Auftrag konnte nicht generiert werden!\n" + e.getMessage(), e);
        }
    }


}
