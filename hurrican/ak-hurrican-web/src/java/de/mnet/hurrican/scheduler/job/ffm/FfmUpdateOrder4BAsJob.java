/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2015
 */
package de.mnet.hurrican.scheduler.job.ffm;

import java.util.*;
import javax.validation.constraints.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Job ermittelt Bauauftraege und triggert fuer diese ein FFM Update.
 */
public abstract class FfmUpdateOrder4BAsJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(FfmUpdateOrder4BAsJob.class);

    @Override
    protected abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

    protected void executeForDate(JobExecutionContext context, Date baDate) {
        try {
            AKWarnings result = doFfmUpdates(baDate);

            if (result != null && result.isNotEmpty()) {
                sendErrorMail(context, result);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }

    void sendErrorMail(JobExecutionContext context, AKWarnings result) {
        String eMails = (String) getJobDataMapObject(context, "emailTo");
        sendMail((eMails != null) ? eMails : DEFAULT_EMAIL, "Fehler beim FFM Update!",
                result.getWarningsAsText(), context);
    }

    /**
     * Ermittelt alle (aktiven) Bauauftraege zum angegebenen Datum und ruft fuer die Bauauftraege mit aktivem
     * FFM-Datensatz die Methode {@link FFMService#updateAndSendOrder(Verlauf)} auf. <br/> Evtl. auftretende Exceptions
     * werden abgefangen und in dem {@link AKWarnings} Objekt als einfacher Text an den Client gemeldet.
     *
     * @param realDate Datum, das zur Ermittlung der Bauauftraege dient.
     * @return
     */
    public AKWarnings doFfmUpdates(@NotNull Date realDate) {
        AKWarnings warnings = new AKWarnings();
        try {
            final BAService baService = getCCService(BAService.class);
            final FFMService ffmService = getCCService(FFMService.class);
            List<AbstractBauauftragView> baViews = baService.findBAVerlaufViews4Abt(
                    true, Abteilung.FFM, false, realDate, realDate);

            if (CollectionUtils.isNotEmpty(baViews)) {
                baViews.stream()
                        .map(view -> loadBauauftrag(view.getVerlaufId(), baService))
                        .filter(bauauftrag -> bauauftrag.isPresent())
                        .filter(bauauftrag -> ffmService.hasActiveFfmRecord(bauauftrag.get(), true))
                        .forEach(bauauftrag -> {
                            try {
                                ffmService.updateAndSendOrder(bauauftrag.get());
                            }
                            catch (FFMServiceException e) {
                                warnings.addAKWarning(this, String.format(
                                        "Fehler beim FFM Update für Auftrag %s", bauauftrag.get().getAuftragId()));
                            }
                        });
            }
        }
        catch (Exception e) {
            warnings.addAKWarning(this, String.format("Fehler beim FFM Update für Bauaufträge: %s",
                    ExceptionUtils.getFullStackTrace(e)));
        }
        return warnings;
    }

    Optional<Verlauf> loadBauauftrag(final Long verlaufId, final BAService baService) {
        try {
            return Optional.of(baService.findVerlauf(verlaufId));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
}
