package de.mnet.hurrican.wholesale.service.impl;

import java.util.*;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.hurrican.wholesale.dao.WholesaleAuditDAO;
import de.mnet.hurrican.wholesale.model.WholesaleAudit;
import de.mnet.hurrican.wholesale.service.WholesaleAuditService;

/**
 * Created by vergaragi on 03.02.2017.
 */
@Component
public class WholesaleAuditServiceImpl implements WholesaleAuditService {

    private static final Logger LOGGER = Logger.getLogger(WholesaleAuditServiceImpl.class);

    @Autowired
    private WholesaleAuditDAO wholesaleAuditDAO;

    @Override
    public WholesaleAudit findById(Long id) throws FindException {
        return wholesaleAuditDAO.findById(id);
    }

    @Override
    public List<WholesaleAudit> findByVorabstimmungsId(String vorabstimmungsId) throws FindException {
        if (vorabstimmungsId == null) {
            return Lists.newArrayList();
        }
        return wholesaleAuditDAO.findByVorabstimmungsId(vorabstimmungsId);
    }


    public void saveWholesaleAudit(WholesaleAudit wholesaleAudit) throws StoreException {

        try {
            wholesaleAuditDAO.store(wholesaleAudit);
            wholesaleAuditDAO.flushSession();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }

    }


}
