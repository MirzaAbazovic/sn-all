/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.2007 11:53:07
 */
package de.mnet.hurrican.scheduler.job.errorhandler;

import org.springframework.mail.SimpleMailMessage;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.service.utils.SchedulerService;
import de.mnet.hurrican.scheduler.service.utils.SchedulerServiceFinder;

/**
 * Abstrakte Klasse fuer Job-Handler.
 *
 *
 */
public abstract class AbstractJobHandler {

    protected static HurricanScheduler getAKScheduler() {
        return HurricanScheduler.getInstance();
    }

    protected <T extends SchedulerService> T getSchedulerService(Class<T> type) throws ServiceNotFoundException {
        return SchedulerServiceFinder.instance().getSchedulerService(type);
    }

    /**
     * Ueberprueft, ob in der MailMessage mindestens ein Receiver eingetragen ist.
     *
     * @param smm
     * @return
     */
    protected boolean hasMailReceiver(SimpleMailMessage smm) {
        if (smm != null) {
            String[] receiver = smm.getTo();
            return ((receiver != null) && (receiver.length > 0)) ? true : false;
        }
        return false;
    }
}
