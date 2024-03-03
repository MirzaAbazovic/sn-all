/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2006 16:18:43
 */
package de.mnet.hurrican.scheduler.service.impl;

import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerStoreException;
import de.mnet.hurrican.scheduler.model.ExportedBillingFile;
import de.mnet.hurrican.scheduler.service.SchedulerLogService;

/**
 * Implementierung von <code>SchedulerLogService</code>.
 *
 *
 */
public class SchedulerLogServiceImpl extends BaseSchedulerService implements SchedulerLogService {

    private static final Logger LOGGER = Logger.getLogger(SchedulerLogServiceImpl.class);

    @Override
    public void logRechnungsExport(ExportedBillingFile toLog) throws AKSchedulerStoreException {
        try {
            ((StoreDAO) getDAO()).store(toLog);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerStoreException(AKSchedulerStoreException._UNEXPECTED_ERROR, e);
        }
    }


    @Override
    public boolean isLogged(String year, String month, String filename) throws AKSchedulerFindException {
        try {
            ExportedBillingFile ex = new ExportedBillingFile();
            ex.setBillingYear(year);
            ex.setBillingMonth(month);
            ex.setFilename(filename);

            List<ExportedBillingFile> result = ((ByExampleDAO) getDAO()).queryByExample(ex, ExportedBillingFile.class);
            return CollectionTools.isNotEmpty(result);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKSchedulerFindException(AKSchedulerFindException._UNEXPECTED_ERROR, e);
        }
    }

}
