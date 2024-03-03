/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2007 16:29:22
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierService;


/**
 * Command prueft, ob der Carrier fuer eine el. TAL-Bestellung zugelassen ist und ob die fuer den Carrier notwendigen
 * Daten zur Verfuegung stehen.
 *
 *
 */
public class CheckCarrierTALCommand extends AbstractTALCommand {

    private static final Logger LOGGER = Logger.getLogger(CheckCarrierTALCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    public Object execute() throws Exception {
        try {
            CarrierService cs = (CarrierService) getCCService(CarrierService.class);
            Carrier carrier = cs.findCarrier(getCarrierbestellung().getCarrier());
            if (carrier == null || !carrier.isValid4ElTAL()) {
                throw new StoreException(
                        "Carrier konnte nicht ermittelt werden oder ist fuer el. TAL-Bestellung nicht zugelassen!");
            }

            CarrierKennung ck = (CarrierKennung) getPreparedValue(KEY_CARRIER_KENNUNG_ABS);
            if (ck == null || StringUtils.isBlank(ck.getElTalAbsenderId())) {
                throw new StoreException("Carrier-Kennung vom HVT konnte nicht ermittelt werden!");
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Fehler bei der Ueberpruefung des Carriers: " + e.getMessage(), getClass());
        }
    }

}


