/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 11:00:10
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.log4j.Logger;

import de.augustakom.common.service.iface.IServiceCommand;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.cc.DSLAMProfileMonitorDAO;
import de.augustakom.hurrican.model.cc.DSLAMProfileMonitor;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.DSLAMProfileMonitorService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.augustakom.hurrican.service.cc.impl.command.dslamprofilemonitor.CheckNeedsDSLAMProfileMonitoringCommand;
import de.mnet.common.service.locator.ServiceLocator;

/**
 * Implementierung von AuftragsUeberwachungService
 *
 * @see de.augustakom.hurrican.service.cc.DSLAMProfileMonitorService
 */
@CcTxRequired
public class DSLAMProfileMonitorServiceImpl implements DSLAMProfileMonitorService {

    private static final Logger LOGGER = Logger.getLogger(DSLAMProfileMonitorServiceImpl.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.CPSService")
    private CPSService cpsService;

    @Resource(name = "de.mnet.common.service.locator.ServiceLocator")
    private ServiceLocator serviceLocator;

    @Resource(name = "dslamProfileMonitorDAO")
    private DSLAMProfileMonitorDAO dslamProfileMonitorDao;

    @Resource(name = "de.augustakom.hurrican.service.cc.RegistryService")
    private RegistryService registryService;

    @Override
    public void createDSLAMProfileMonitor(Long auftragId) throws StoreException {
        Integer monitoringDuration;
        try {
            monitoringDuration = findMonitoringDurationInDays();
        }
        catch (FindException fe) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR, fe);
        }

        DSLAMProfileMonitor monitor = new DSLAMProfileMonitor();
        monitor.setAuftragId(auftragId);
        monitor.setDeleted(false);
        monitor.setMonitoringSince(new Date());
        monitor.setMonitoringEnds(DateTools.plusWorkDays(monitoringDuration));

        try {
            dslamProfileMonitorDao.store(monitor);
        }
        catch (Exception e) {
            throw new StoreException(StoreException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public boolean needsMonitoring(Long ccAuftragId) throws FindException {
        if (ccAuftragId == null) {
            throw new FindException("ccAuftragId darf nicht null sein!");
        }
        try {
            IServiceCommand cmd = serviceLocator
                    .getCmdBean(CheckNeedsDSLAMProfileMonitoringCommand.class);
            cmd.prepare(CheckNeedsDSLAMProfileMonitoringCommand.CCAUFTRAG_ID, ccAuftragId);
            return (Boolean) cmd.execute();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Collection<Long> findCurrentlyMonitoredAuftragIds() throws FindException {
        Collection<Long> result;
        try {
            result = dslamProfileMonitorDao.findCurrentlyMonitoredAuftragIds();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
        return result;
    }

    @Override
    public Pair<Integer, Integer> cpsQueryAttainableBitrate(Long ccAuftragId, Long sessionId) throws FindException {
        if ((ccAuftragId == null) || (sessionId == null)) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        Pair<Integer, Integer> result = cpsService.queryAttainableBitrate(ccAuftragId, sessionId);
        return result;
    }

    @Override
    public void deactivateMonitoring(Long auftragId) throws FindException {
        if (auftragId == null) {
            throw new FindException(FindException.INVALID_FIND_PARAMETER);
        }
        try {
            DSLAMProfileMonitor monitorToDeactivate = dslamProfileMonitorDao.findByAuftragId(auftragId);
            if (monitorToDeactivate == null) {
                throw new FindException(String.format("Die DSLAMProfileMonitor Daten für den Auftrag mit der Id %d konnten nicht " +
                        "ermittelt werden!", auftragId));
            }
            monitorToDeactivate.setDeleted(true);
            dslamProfileMonitorDao.store(monitorToDeactivate);
        }
        catch (FindException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new FindException(FindException._UNEXPECTED_ERROR, e);
        }
    }

    private int findMonitoringDurationInDays() throws FindException {
        return getRegistryService().getIntValue(RegistryService.REGID_DSLAMPROFILEMONITOR_DURATION);
    }

    /**
     * @return Returns the registryService.
     */
    protected RegistryService getRegistryService() {
        return registryService;
    }
}
