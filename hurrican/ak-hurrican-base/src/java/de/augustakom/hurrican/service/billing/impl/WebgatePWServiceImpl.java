/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2005 12:02:54
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.dao.billing.WebgatePWDAO;
import de.augustakom.hurrican.model.billing.WebgatePW;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.WebgatePWService;


/**
 * Service-Implementierung von <code>WebgatePWService</code>.
 *
 *
 */
@BillingTx
public class WebgatePWServiceImpl extends DefaultBillingService implements WebgatePWService {

    private static final Logger LOGGER = Logger.getLogger(WebgatePWServiceImpl.class);

    @Override
    public WebgatePW findFirstWebgatePW4Kunde(Long kundeNo) throws FindException {
        try {
            return ((WebgatePWDAO) getDAO()).findFirstByKundeNo(kundeNo);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public WebgatePW findWebgatePW4RInfo(Long rInfoNo) throws FindException {
        if (rInfoNo == null) { return null; }
        try {
            WebgatePW example = new WebgatePW();
            example.setRinfoNo(rInfoNo);
            List<WebgatePW> result = ((WebgatePWDAO) getDAO()).queryByExample(example, WebgatePW.class);

            if (result != null) {
                if (result.size() == 1) {
                    return result.get(0);
                }
                else {
                    throw new FindException("Anzahl der ermittelten WebgatePWs zu RInfo " + rInfoNo + " ungueltig!");
                }
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException("Password für R_Info__NO " + rInfoNo + " konnte nicht gefunden werden!", e);
        }
    }

}


