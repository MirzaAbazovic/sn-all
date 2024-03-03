/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2006 09:51:33
 */
package de.mnet.hurrican.scheduler.job.validation;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.temp.LeistungsDiffCheck;
import de.augustakom.hurrican.model.cc.view.LeistungsDiffView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungPredicate;
import de.mnet.hurrican.scheduler.HurricanScheduler;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;

/**
 * Job, um von den Auftraegen, die heute(!) realisiert werden, die Leistungs-Snapshots zu ueberpruefen. <br> Der Job
 * ruft fuer jeden Verlauf die Methoden <code>createLeistungSnapshots4Auftrag</code> und
 * <code>createLeistungsDiff</code> auf. <br> Dadurch werden evtl. Unstimmigkeiten zwischen den Eintraegen in der
 * Hurrican-DB und der Billing-DB abgeglichen. <br> <br> Da der Job nur die Verlaeufe des aktuellen Tags beachtet, ist
 * es wichtig, die Job-Ausfuehrung (Start-Zeit) richtig zu konfigurieren!
 *
 *
 */
public class ValidateLeistungSnapshotJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(ValidateLeistungSnapshotJob.class);

    private static final String VALIDATION_ERROR = "Fehler bei der Validierung der technischen Leistungen - Auftrag: {0}\nError: {1}";
    private static final String LEISTUNGS_DIFF_NOT_OK = "Die Leistungs-Differenz kann nicht abgeglichen werden - Auftrag: {0}\nMessages: {1}";

    private static final String LEISTUNGS_DIFF_ZUGANG = "Zugang";
    private static final String LEISTUNGS_DIFF_KUENDIGUNG = "Kuendigung";

    private static final String DIFF_MSG = "   Differenz: Leistung={0}; von={1}; bis={2}; Typ={3}";
    private static final String DIFF_VERLAUF_ADDED = "   Verlauf fuer Abteilung {0} hinzugefuegt.";

    /**
     * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            CCLeistungsService leistungsService = getCCService(CCLeistungsService.class);
            BAService baService = getCCService(BAService.class);
            List<Verlauf> verlaeufe = baService.findActVerlaeufe(new Date(), Boolean.FALSE);
            if (CollectionTools.isEmpty(verlaeufe)) {
                return;
            }

            CCAuftragService as = getCCService(CCAuftragService.class);
            StringBuilder notification = new StringBuilder();

            AKWarnings warnings = new AKWarnings();
            for (Verlauf verlauf : verlaeufe) {
                if (!verlauf.isKuendigung()) {
                    validateLeistungen(verlauf, warnings, notification, as, leistungsService);
                }
            }

            // Leistungsdifferenzen per eMail verschicken
            if (notification.length() > 0) {
                String eMails = (String) getJobDataMapObject(context, "emailTo");
                sendMail((eMails != null) ? eMails : DEFAULT_EMAIL, "Leistungsabgleich", notification.toString(),
                        context);
            }

            if (warnings.isNotEmpty()) {
                throw new Exception(warnings.getWarningsAsText());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new SendMailJobErrorHandler().handleError(context, e, null);
            new LogDBJobErrorHandler().handleError(context, e, null);
        }
    }

    /**
     * Fuehrt die Leistungsueberpruefung durch.
     *
     * @param verlauf          betroffener Verlauf
     * @param warnings         Objekt, um Warnungen zu speichern
     * @param notification     Variable, in der der eMail-Text fuer die Benachrichtigung gespeichert wird
     * @param as
     * @param leistungsService
     *
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "REC_CATCH_EXCEPTION", justification = "Catch all ist hier gewollt")
    private void validateLeistungen(Verlauf verlauf, AKWarnings warnings, StringBuilder notification,
            CCAuftragService as, CCLeistungsService leistungsService) {
        try {
            AuftragDaten auftragDaten = as.findAuftragDatenByAuftragId(verlauf.getAuftragId());
            if (!auftragDaten.isInKuendigung()) {
                List<LeistungsDiffView> diffs = leistungsService.findLeistungsDiffs(auftragDaten.getAuftragId(),
                        auftragDaten.getAuftragNoOrig(), auftragDaten.getProdId());
                if (CollectionUtils.isNotEmpty(diffs)) {
                    LeistungsDiffCheck check = leistungsService.checkLeistungsDiffs(diffs, HurricanScheduler.getSessionId());

                    if ((check == null) || check.isOk()) {
                        leistungsService.synchTechLeistungen4Auftrag(auftragDaten.getAuftragId(),
                                auftragDaten.getAuftragNoOrig(), auftragDaten.getProdId(), verlauf.getId(), true,
                                HurricanScheduler.getSessionId());
                    }
                    else {
                        warnings.addAKWarning(
                                this,
                                StringTools.formatString(LEISTUNGS_DIFF_NOT_OK, new Object[] { "" + verlauf.getAuftragId(),
                                        check.getMessagesAsString() }, null)
                        );
                    }

                    // Leistungs-Differenz sammeln
                    if (CollectionTools.isNotEmpty(diffs)) {
                        notification.append("Leistungsabgleich fuer Auftrag " + verlauf.getAuftragId() + "\n");
                        for (LeistungsDiffView diff : diffs) {
                            notification.append(StringTools.formatString(DIFF_MSG, new Object[] { diff.getTechLsName(),
                                    diff.getAktivVon(), diff.getAktivBis(),
                                    (diff.isZugang()) ? LEISTUNGS_DIFF_ZUGANG : LEISTUNGS_DIFF_KUENDIGUNG }, null)
                                    + "\n");
                        }

                        if ((check == null) || check.isOk()) {
                            validateBAVerteilung(verlauf, diffs, notification);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            warnings.addAKWarning(
                    this,
                    StringTools.formatString(VALIDATION_ERROR, new Object[] { "" + verlauf.getAuftragId(),
                            ExceptionUtils.getFullStackTrace(e) }, null)
            );
        }
    }

    /**
     * Ueberprueft, welche Abteilungen fuer die ermittelten Leistungsdifferenzen zustaendig sind. Anschliessend wird
     * geprueft, ob alle diese Abteilungen den BA erhalten haben. Ist dies nicht der Fall, wird der BA automatisch
     * zugeordnet.
     *
     * @param verlauf      betroffener Verlauf
     * @param diffs        Liste mit den abgeglichenen Differenzen
     * @param notification Variable, in der der eMail-Text fuer die Benachrichtigung gespeichert wird
     */
    private void validateBAVerteilung(Verlauf verlauf, List<LeistungsDiffView> diffs, StringBuilder notification)
            throws ServiceNotFoundException, FindException, StoreException {
        CCLeistungsService ccls = getCCService(CCLeistungsService.class);
        List<Long> necessaryAbtIds = ccls.findAbtIds4VerlaufTechLs(verlauf.getId());
        if (CollectionTools.isNotEmpty(necessaryAbtIds)) {
            BAService baService = getCCService(BAService.class);
            List<VerlaufAbteilung> verlaufAbteilungen = baService.findVerlaufAbteilungen(verlauf.getId());

            List<Long> abtIds2Add = new ArrayList<Long>();
            for (Long abtId : necessaryAbtIds) {
                boolean found = false;
                boolean containsMQueue = false;
                for (VerlaufAbteilung verlaufAbteilung : verlaufAbteilungen) {
                    if (NumberTools.equal(abtId, verlaufAbteilung.getAbteilungId())) {
                        found = true;
                        break;
                    }

                    if (NumberTools.equal(verlaufAbteilung.getAbteilungId(), Abteilung.MQUEUE)) {
                        containsMQueue = true;
                    }
                }

                if (!found) {
                    abtIds2Add.add(abtId);
                }

                if (containsMQueue) {
                    // ST Online/Voice wird entfernt, falls BA schon an M-Queue verteilt wurde
                    abtIds2Add.remove(Abteilung.ST_ONLINE);
                    abtIds2Add.remove(Abteilung.ST_VOICE);
                }
            }

            if (abtIds2Add.contains(Abteilung.FIELD_SERVICE)) {
                // Abteilung FieldService entfernen, falls FieldService schon im Verlauf vorhanden
                @SuppressWarnings("unchecked")
                Collection<VerlaufAbteilung> departmentFieldService = CollectionUtils.select(verlaufAbteilungen,
                        new VerlaufAbteilungPredicate(Abteilung.FIELD_SERVICE));
                if (CollectionTools.isNotEmpty(departmentFieldService)) {
                    abtIds2Add.remove(Abteilung.FIELD_SERVICE);
                }
            }

            // Niederlassung vom Auftrag ermitteln
            NiederlassungService niederlassungService = getCCService(NiederlassungService.class);
            Niederlassung niederlassung = niederlassungService.findNiederlassung4Auftrag(verlauf.getAuftragId());
            if (niederlassung == null) {
                throw new StoreException("Niederlassung fuer die Nachverteilung konnte nicht ermittelt werden!");
            }

            createVerlauf4AbtIds(verlauf, abtIds2Add, niederlassung, notification, baService);
        }
    }

    /**
     * Erzeugt fuer die angegebenen Abteilung-IDs zusaetzliche Verlaufs-Eintraege.
     *
     * @param verlauf      betroffener Verlauf
     * @param abtIds       IDs der Abteilungen, die den Verlauf zusaetzlich erhalten sollen
     * @param notification Variable, in der der eMail-Text fuer die Benachrichtigung gespeichert wird
     *
     */
    private void createVerlauf4AbtIds(Verlauf verlauf, List<Long> abtIds, Niederlassung niederlassung,
            StringBuilder notification, BAService bas) throws StoreException {
        if (CollectionTools.isNotEmpty(abtIds)) {
            for (Long abtId : abtIds) {
                VerlaufAbteilung va = new VerlaufAbteilung();
                va.setVerlaufId(verlauf.getId());
                va.setAbteilungId(abtId);
                va.setRealisierungsdatum(verlauf.getRealisierungstermin());
                va.setNiederlassungId(niederlassung.getId());
                va.setDatumAn(new Date());
                va.setVerlaufStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
                va.setBemerkung("System: Zuordnung wg. Leistungsabgleich.");

                bas.saveVerlaufAbteilung(va);

                notification.append(StringTools.formatString(DIFF_VERLAUF_ADDED, new Object[] { abtId }, null));
                notification.append(SystemUtils.LINE_SEPARATOR);
            }
        }
    }
}
