/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2007 08:12:06
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.Carrierbestellung;


/**
 * Command-Klasse, um die Daten einer Carrierbestellung fuer den Vorgang 'Neubestellung' zu pruefen. <br> Sind bereits
 * Ergebniswerte (z.B. Bereitstellungstermin, LBZ etc.) in der Carrierbestellung vorhanden, liefert das Command ein
 * negatives Ergebnis.
 *
 *
 */
public class Check4NeuTALCommand extends AbstractTALCommand {

    private static final Logger LOGGER = Logger.getLogger(Check4NeuTALCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            Carrierbestellung cb = getCarrierbestellung();
            if (cb.getKuendigungAnCarrier() != null) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Die TAL wurde bereits gekuendigt. Eine Neubestellung kann nicht ausgefuehrt werden!", getClass());
            }

            if (StringUtils.isNotBlank(cb.getLbz()) || StringUtils.isNotBlank(cb.getVtrNr())
                    || (cb.getBereitstellungAm() != null)) {
                return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                        "Neubestellung nicht moeglich, da bereits Daten zurueck gemeldet sind!", getClass());
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Fehler bei der Ueberpruefung der Carrierbestellungsdaten: " + e.getMessage(), getClass());
        }
    }

}


