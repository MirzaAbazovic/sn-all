/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2004 09:44:13
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.AK0800DAO;
import de.augustakom.hurrican.model.cc.AK0800;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.INRufnummerService;
import de.augustakom.hurrican.service.utils.HistoryHelper;


/**
 * Service-Implementierung von INRufnummerService.
 *
 *
 */
@CcTxRequired
public class INRufnummerServiceImpl extends DefaultCCService implements INRufnummerService {

    private static final Logger LOGGER = Logger.getLogger(INRufnummerServiceImpl.class);

    private AK0800DAO ak0800DAO = null;

    @Override
    public AK0800 findINDetails(Long auftragId) throws FindException {
        try {
            List<AK0800> result = getAk0800DAO().findByAuftragId(auftragId);
            return (CollectionTools.isNotEmpty(result)) ? result.get(0) : null;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AK0800 saveAK0800(AK0800 toSave, boolean makeHistory) throws StoreException {
        try {
            AK0800DAO dao = getAk0800DAO();
            if (makeHistory && (toSave.getId() != null)) {
                Date now = new Date();
                AK0800 new0800 = dao.update4History(toSave, toSave.getId(), now);
                return new0800;
            }
            else {
                HistoryHelper.checkHistoryDates(toSave);
                dao.store(toSave);
                return toSave;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @return Returns the ak0800DAO.
     */
    public AK0800DAO getAk0800DAO() {
        return ak0800DAO;
    }

    /**
     * @param ak0800DAO The ak0800DAO to set.
     */
    public void setAk0800DAO(AK0800DAO ak0800DAO) {
        this.ak0800DAO = ak0800DAO;
    }
}


