/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2007 16:14:47
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;


/**
 * Command-Klasse prueft, ob zu der Carrierbestellung ein aktiver el. Bestellvorgang existiert.
 *
 *
 */
public class CheckActiveCbvTALCommand extends AbstractTALCommand implements Serializable {

    private static final long serialVersionUID = -6105126806200933300L;
    private static final Logger LOGGER = Logger.getLogger(CheckActiveCbvTALCommand.class);

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        try {
            // pruefen, ob noch ein offener CBVorgang zu der CB-ID besteht. Wenn ja, Exception.
            CarrierElTALService talService = getCCService(CarrierElTALService.class);
            List<CBVorgang> oldCBVs = talService.findCBVorgaenge4CB(getCarrierbestellung().getId());
            if (CollectionTools.isNotEmpty(oldCBVs)) {
                for (CBVorgang oldCBV : oldCBVs) {
                    if (NumberTools.isLess(oldCBV.getStatus(), CBVorgang.STATUS_CLOSED)) {
                        throw new StoreException(String.format("Zur Carrierbestellung mit Auftrag-ID: %s gibt es noch einen offenen elektronischen Vorgang.", oldCBV.getAuftragId()));
                    }
                }
            }

            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_OK, null, getClass());
        }
        catch (StoreException e) {
            // Fachlichen Fehler nicht auf Error loggen
            LOGGER.info(e.getMessage(), e);
            return handleException(e);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return handleException(e);
        }
    }

    public Object handleException(Exception e) {
        return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                "Fehler bei der Ueberpruefung auf aktive Bestellvorgaenge: " + e.getMessage(), getClass());
    }

}


