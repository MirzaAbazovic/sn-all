/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2006 17:45:36
 */
package de.mnet.hurrican.scheduler.service.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerStoreException;
import de.mnet.hurrican.scheduler.model.SignaturedFile;
import de.mnet.hurrican.scheduler.service.SignatureService;

/**
 * Implementierung von <code>SignatureService</code>.
 */
public class SignatureServiceImpl extends BaseSchedulerService implements SignatureService {

    private static final Logger LOGGER = Logger.getLogger(SignatureServiceImpl.class);

    @Override
    public void save(SignaturedFile toSave) throws AKSchedulerStoreException {
        try {
            StoreDAO dao = (StoreDAO) getDAO();
            dao.store(toSave);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerStoreException(AKSchedulerStoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public SignaturedFile findSignaturedFile(String filename, boolean isAbsolutePath) throws AKSchedulerFindException {
        try {
            SignaturedFile example = new SignaturedFile();
            if (isAbsolutePath) {
                example.setAbsolutePath(filename);
            }
            else {
                example.setFilename(filename);
            }

            List<SignaturedFile> result =
                    ((ByExampleDAO) getDAO()).queryByExample(example, SignaturedFile.class);
            if (result == null) {
                return null;
            }
            else if (result.size() == 1) {
                return result.get(0);
            }
            else if (result.size() > 1) {
                throw new AKSchedulerFindException(FindException.INVALID_RESULT_SIZE,
                        new Object[] { Integer.valueOf(1), Integer.valueOf(result.size()) });
            }
            return null;
        }
        catch (AKSchedulerFindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerFindException(AKSchedulerFindException._UNEXPECTED_ERROR, e);
        }
    }
}
