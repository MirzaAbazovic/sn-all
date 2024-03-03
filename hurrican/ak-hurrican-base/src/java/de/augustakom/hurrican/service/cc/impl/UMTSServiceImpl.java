/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2010 08:37:41
 */

package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AuftragUMTSDAO;
import de.augustakom.hurrican.model.cc.AuftragUMTS;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.UMTSService;
import de.augustakom.hurrican.service.utils.HistoryHelper;

/**
 *
 */
@CcTxRequired
public class UMTSServiceImpl extends DefaultCCService implements UMTSService {

    private static final Logger LOGGER = Logger.getLogger(HousingServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.dao.cc.AuftragUMTSDAO")
    private AuftragUMTSDAO auftragUMTSDAO;

    @Override
    public AuftragUMTS saveAuftragUMTS(AuftragUMTS toSave, Long sessionId) throws StoreException {
        if (toSave == null) {
            throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
        }

        try {
            AKUser user = getAKUserBySessionId(sessionId);

            if (toSave.getId() != null) {
                Date now = new Date();
                AuftragUMTS newAU = auftragUMTSDAO.update4History(toSave, toSave.getId(), now);
                newAU.setUserW(user.getLoginName());
                auftragUMTSDAO.store(newAU);
                return newAU;
            }
            else {
                HistoryHelper.checkHistoryDates(toSave);
                toSave.setUserW(user.getLoginName());
                auftragUMTSDAO.store(toSave);
                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AuftragUMTS findAuftragUMTS(Long auftragId) throws FindException,
            IncorrectResultSizeDataAccessException {
        try {
            return auftragUMTSDAO.findAuftragUMTS(auftragId);
        }
        catch (IncorrectResultSizeDataAccessException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Injected
     */
    public void setAuftragUMTSDAO(AuftragUMTSDAO auftragUMTSDAO) {
        this.auftragUMTSDAO = auftragUMTSDAO;
    }
}
