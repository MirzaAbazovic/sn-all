/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.12.2005 08:43:44
 */
package de.mnet.hurrican.scheduler.job.base;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.augustakom.hurrican.service.reporting.IReportService;
import de.augustakom.hurrican.service.reporting.utils.ReportServiceFinder;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerFindException;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.model.JobError;
import de.mnet.hurrican.scheduler.service.SchedulerJobService;
import de.mnet.hurrican.scheduler.service.utils.SchedulerService;
import de.mnet.hurrican.scheduler.service.utils.SchedulerServiceFinder;
import de.mnet.hurrican.scheduler.utils.JobExecutionContextHelper;

/**
 * Default-Implementierung einer Quartz Job-Bean.
 *
 *
 */
public abstract class AKAbstractQuartzJob extends QuartzJobBean {

    private static final Logger LOGGER = Logger.getLogger(AKAbstractQuartzJob.class);

    /**
     * Default eMail-Adresse, an die Benachrichtigungen verschickt werden koennen, falls keine andere Adresse definiert
     * ist.
     */
    protected static final String DEFAULT_EMAIL = "IT-AnwendungsentwicklungAgb@m-net.de";

    protected Object getJobDataMapObject(JobExecutionContext context, String parameterName) {
        return JobExecutionContextHelper.getJobDataMapObject(context, parameterName);
    }

    protected Object getJobDataMapObjectFromTrigger(JobExecutionContext context, String parameterName) {
        return JobExecutionContextHelper.getJobDataMapObjectFromTrigger(context, parameterName);
    }

    /**
     * Ermittelt einen CC-Service vom Typ <code>type</code>.
     *
     * @param type
     * @return gewuenschter CC-Service
     * @throws ServiceNotFoundException
     */
    protected <T extends ICCService> T getCCService(Class<T> type) throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(type);
    }

    /**
     * Ermittelt einen Billing-Service vom Typ <code>type</code>.
     *
     * @return gewuenschter Billing-Service
     * @throws ServiceNotFoundException
     */
    protected <T extends IBillingService> T getBillingService(Class<T> type) throws ServiceNotFoundException {
        return BillingServiceFinder.instance().getBillingService(type);
    }

    /**
     * Ermittelt einen Report-Service vom Typ <code>type</code>.
     *
     * @return gewuenschter Report-Service
     */
    protected <T extends IReportService> T getReportService(Class<T> type) throws ServiceNotFoundException {
        return ReportServiceFinder.instance().getReportService(type);
    }

    /**
     * Ermittelt einen Scheduler-Service vom Typ <code>type</code>.
     *
     * @return gewuenschter Scheduler-Service
     */
    protected <T extends SchedulerService> T getSchedulerService(Class<T> type) throws ServiceNotFoundException {
        return SchedulerServiceFinder.instance().getSchedulerService(type);
    }

    /**
     * Sucht nach einem Authentication-Service und gibt diesen zurueck.
     *
     * @param name Name des gesuchten Services.
     * @param type Typ des gesuchten Services.
     * @return gesuchter Service vom Typ <code>type</code>
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    protected <T extends IServiceObject> T getAuthenticationService(String name, Class<T> type)
            throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        return locator.getService(name, type, null);
    }


    protected <T extends IServiceObject> T getService(Class<T> type) throws ServiceNotFoundException {
        return ServiceLocatorRegistry.instance().getServiceLocator().getService(type.getName(), type, null);
    }


    /**
     * Ermittelt alle JobErrors, die fuer den Job protokolliert wurden.
     *
     * @param context JobExecutionContext ueber den der aktuelle Job identifiziert werden kann.
     * @return Liste mit Objekten des Typs <code>JobError</code>.
     * @throws ServiceNotFoundException
     * @throws AKSchedulerFindException
     */
    protected List<JobError> getJobErrors(JobExecutionContext context) throws ServiceNotFoundException,
            AKSchedulerFindException {
        SchedulerJobService jobService = getSchedulerService(SchedulerJobService.class);
        List<JobError> result = jobService.getJobErrors(context);
        return result;
    }

    /**
     * Verschickt eine eMail mit Subject/Message an die EMail-Adressen 'emailReceiver' (durch Komma getrennt). <br>
     *
     * @param emailReceiver komma-getrennte Liste der Empfaenger-Adressen
     * @param subject       Subject fuer die eMail
     * @param message       Message-Text
     * @param jobExeCtx
     */
    protected void sendMail(String emailReceiver, String subject, String message, JobExecutionContext jobExeCtx) {
        sendMail(emailReceiver, ",", subject, message, jobExeCtx);
    }

    /**
     * @see sendMail(String, String, String, JobExecutionContext)
     */
    protected void sendMail(String emailReceiver, String emailSeperator, String subject, String message,
            JobExecutionContext jobExeCtx) {
        sendMailWithCc(emailReceiver, null, emailSeperator, subject, message, jobExeCtx);
    }

    /**
     * Funktionalitaet wie sendMail(String, String, String, JobExecutionContext), zusaetzlich Angabe einer Cc-Adresse
     * moeglich.
     */
    protected void sendMailWithCc(String emailReceiver, String emailCc, String emailSeperator, String subject,
            String message, JobExecutionContext jobExeCtx) {
        try {
            HurricanScheduler scheduler = HurricanScheduler.getInstance();
            JavaMailSender mailSender = scheduler.getBean("mailSender", JavaMailSender.class);

            SimpleMailMessage mm = new SimpleMailMessage();
            mm.setFrom("Hurrican-Scheduler");
            mm.setTo(StringUtils.split(emailReceiver, emailSeperator));
            if (StringUtils.isNotBlank(emailCc)) {
                mm.setCc(StringUtils.split(emailCc, emailSeperator));
            }
            mm.setSubject(StringUtils.trimToEmpty(subject));
            mm.setText(StringUtils.trimToEmpty(message));

            mailSender.send(mm);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new SendMailJobErrorHandler().handleError(jobExeCtx, e, null);
        }
    }

    /**
     * Ermittelt einen User an Hand der angegebenen Session-Id
     * @param sessionId
     * @return
     * @throws ServiceNotFoundException
     * @throws AKAuthenticationException
     */
    public AKUser getCurrentUser(Long sessionId) throws ServiceNotFoundException, AKAuthenticationException {
        AKUserService userService = getAuthenticationService(AKAuthenticationServiceNames.USER_SERVICE,
                AKUserService.class);
        return userService.findUserBySessionId(sessionId);
}
}
