/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.2006 15:43:51
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


/**
 * Dieses Command leitet von dem entsprechenden Check-Command fuer die Leistung ab und wertet das
 * CheckCommandResult-Objekt aus. Command bezieht sich auf die in der DB hinterlegten technische Leistungen
 * (LS_ZUGANG).
 */
@CcTxRequired
@Component("de.augustakom.hurrican.service.cc.impl.command.leistung.ZugangBandwithCommand")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ZugangBandwithCommand extends CheckZugangBandwidthCommand {

    private static final Logger LOGGER = Logger.getLogger(ZugangBandwithCommand.class);

    @Override
    public ServiceCommandResult execute() throws Exception {
        try {
            ServiceCommandResult res = super.execute();
            if (res == null) {
                throw new HurricanServiceCommandException("Check-Command lieferte kein Ergebnis!");
            }
            if ((getRangierung() != null) && !res.isOk()) {
                throw new HurricanServiceCommandException(res.getMessage());
            }
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (FindException e) {
            // Rangierung konnte in super.loadRequiredData() nicht ermittelt
            // werden -> nothing to do...
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (HurricanServiceCommandException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanServiceCommandException(String.format("Fehler in der Bandbreitenprüfung:%n%s",
                    e.getMessage()), e);
        }
    }

}


