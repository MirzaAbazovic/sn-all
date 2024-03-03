/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.01.2006 09:13:16
 */
package de.mnet.hurrican.scheduler.job.base;

import java.util.*;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKLoginService;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Job, um abgelaufene UserSessions aus der Autentication-DB zu entfernen.
 *
 *
 */
public class DeleteExpiredSessionsJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(DeleteExpiredSessionsJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            AKLoginService loginService = getAuthenticationService(AKAuthenticationServiceNames.LOGIN_SERVICE,
                    AKLoginService.class);
            loginService.removeExpiredSessions(null, new Date());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

}
