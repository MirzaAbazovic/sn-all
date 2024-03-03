/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2010 10:58:09
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.DeleteDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.cc.AuftragSIPInterTrunk;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.SIPInterTrunkService;


/**
 * Service-Implementierung von {@link SIPInterTrunkService}
 */
@CcTxRequired
public class SIPInterTrunkServiceImpl extends DefaultCCService implements SIPInterTrunkService {

    private static final Logger LOGGER = Logger.getLogger(SIPInterTrunkServiceImpl.class);

    @Override
    public List<AuftragSIPInterTrunk> findSIPInterTrunks4Order(Long auftragId) throws FindException {
        if (auftragId == null) { throw new FindException(FindException.INVALID_FIND_PARAMETER); }
        try {
            AuftragSIPInterTrunk example = new AuftragSIPInterTrunk();
            example.setAuftragId(auftragId);

            return ((ByExampleDAO) getDAO()).queryByExample(example, AuftragSIPInterTrunk.class,
                    new String[] { AuftragSIPInterTrunk.ID }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void saveSIPInterTrunk(AuftragSIPInterTrunk toSave, Long sessionId) throws StoreException {
        try {
            if ((toSave == null) || (sessionId == null)) {
                throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
            }

            toSave.setUserW(getUserNameAndFirstNameSilent(sessionId));
            ((StoreDAO) getDAO()).store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void deleteSIPInterTrunk(AuftragSIPInterTrunk toDelete) throws StoreException {
        try {
            if ((toDelete == null)) {
                throw new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE);
            }

            ((DeleteDAO) getDAO()).deleteById(toDelete.getId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

}


