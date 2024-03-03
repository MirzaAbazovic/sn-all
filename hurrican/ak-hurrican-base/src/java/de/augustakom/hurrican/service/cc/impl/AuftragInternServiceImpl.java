/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2009 17:07:14
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragInternDAO;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AuftragInternService;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Service-Implementierung von <code>AuftragInternService</code>.
 *
 *
 */
@CcTxRequired
public class AuftragInternServiceImpl implements AuftragInternService {

    private static final Logger LOGGER = Logger.getLogger(AuftragInternServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.dao.cc.AuftragInternDAO")
    private AuftragInternDAO auftragInternDao;


    @Override
    public AuftragIntern findByAuftragId(Long auftragId) throws FindException {
        if (auftragId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            return auftragInternDao.findByAuftragId(auftragId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragIntern saveAuftragIntern(AuftragIntern toSave, boolean makeHistory) throws StoreException {
        if (toSave == null) { throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE); }
        try {
            if (makeHistory && (toSave.getId() != null)) {
                Date now = new Date();
                AuftragIntern newAD = auftragInternDao.update4History(toSave, toSave.getId(), now);
                return newAD;
            }
            else {
                HistoryHelper.checkHistoryDates(toSave);
                auftragInternDao.store(toSave);
                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

}


