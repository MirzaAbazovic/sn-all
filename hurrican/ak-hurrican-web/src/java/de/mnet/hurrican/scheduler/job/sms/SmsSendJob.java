package de.mnet.hurrican.scheduler.job.sms;

import java.util.*;
import com.google.common.collect.ListMultimap;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.messages.AKWarnings;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.service.CustomerNotificationService;
import de.mnet.wita.message.meldung.Meldung;

public class SmsSendJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(SmsSendJob.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        try {
            final CustomerNotificationService smsService = getSchedulerService(CustomerNotificationService.class);

            ListMultimap<String, Meldung<?>> meldungGroups = smsService
                    .groupByExterneAuftragId(smsService.findOffeneMeldungenEmail());

            processMessages(context, meldungGroups, new SendParameter() {
                @Override
                public String getIdentifier() {
                    return "Email";
                }

                @Override
                public void send(List<Meldung<?>> group) throws Exception {
                    smsService.sendEmailForGroup(group);
                }
            });

            meldungGroups = smsService
                    .groupByExterneAuftragId(smsService.findOffeneMeldungenSms());
            processMessages(context, meldungGroups, new SendParameter() {
                @Override
                public String getIdentifier() {
                    return "Sms";
                }

                @Override
                public void send(List<Meldung<?>> group) throws Exception {
                    smsService.sendSmsForGroup(group);
                }
            });

        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

    private void processMessages(JobExecutionContext context, ListMultimap<String, Meldung<?>> meldungGroups, SendParameter senderParameter) {
        try {
            AKWarnings warnings = new AKWarnings();

            Set<String> keys = meldungGroups.keySet();
            for (String key : keys) {
                try {
                    List<Meldung<?>> group = meldungGroups.get(key);
                    senderParameter.send(group);
                }
                catch (Exception e) {
                    LOGGER.error("Fehler beim Versenden der " + senderParameter.getIdentifier() + " mit externeAuftragId=" + key, e);
                    warnings.addAKWarning(CustomerNotificationService.class, e.getMessage());
                }
            }
            if (warnings.isNotEmpty()) {
                throw new SchedulerException("Während des " + senderParameter.getIdentifier() + " Versandes sind folgende Fehler aufgetreten:\n"
                        + warnings.getWarningsAsText());
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

    interface SendParameter {
        String getIdentifier();

        void send(List<Meldung<?>> group) throws Exception;
    }

}
