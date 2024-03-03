/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2013 13:49:11
 */
package de.mnet.hurrican.scheduler.job.wita;

import java.util.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.elektra.ElektraFacadeService;
import de.mnet.hurrican.scheduler.job.base.AKAbstractQuartzJob;
import de.mnet.hurrican.scheduler.job.errorhandler.LogDBJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobErrorHandler;
import de.mnet.hurrican.scheduler.job.errorhandler.SendMailJobWarningsHandler;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaTalOrderService;

/**
 * Basis-Klasse fuer Scheduler-Jobs, ueber die WITA-Rueckmeldungen automatisch abgeschlossen werden.
 */
public abstract class AbstractProcessWitaResponseJob extends AKAbstractQuartzJob {

    private static final Logger LOGGER = Logger.getLogger(AbstractProcessWitaResponseJob.class);

    protected BillingAuftragService billingAuftragService;
    protected BAService baService;
    protected CCAuftragService auftragService;
    protected CarrierService carrierService;
    protected CarrierElTALService carrierElTalService;
    protected ElektraFacadeService elektraFacadeService;
    protected WitaTalOrderService witaTalOrderService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            initServices();
            processOrders(context);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            new LogDBJobErrorHandler().handleError(context, e, null);
            new SendMailJobErrorHandler().handleError(context, e, null);
        }
    }


    /**
     * Gibt die WITA-Vorgangstypen zurueck, die fuer den Automatismus beruecksichtigt werden sollen.
     *
     * @return
     */
    protected abstract Long[] getCbVorgangTypes();


    /**
     * Schliesst den WITA Vorgang ab und fuehrt abhaengig von der Implementierung die notwendigen Schritte aus um
     * entweder eine WITA Kuendigung oder eine WITA Bestellung (Neu|Aenderung|Anbieterwechsel) abzuschliessen.
     *
     * @param witaCbVorgang
     * @param context
     * @throws FindException
     */
    protected abstract void closeWitaAutomatically(WitaCBVorgang witaCbVorgang, JobExecutionContext context)
            throws FindException, StoreException, ValidationException;


    /**
     * Ermittelt alle WITA-Vorgaenge mit folgenden Kriterien und versucht, diese automatisch abzuschliessen: <ul>
     * <li>WITA-Vorgang ist vom Typ {@code AbstractProcessWitaResponseJob#getCbVorgangTypes()} <li>WITA-Vorgang hat
     * 'Automatismus-Flag' gesetzt und darf (immer noch) automatisch abgeschlossen werden <li>es wurde zu dem Vorgang
     * bisher noch kein Automation-Error protokolliert </ul>
     *
     * @param context
     * @throws FindException
     */
    void processOrders(JobExecutionContext context) throws FindException, StoreException, ValidationException {
        List<WitaCBVorgang> witaCbVorgaenge = witaTalOrderService
                .findWitaCBVorgaengeForAutomation(getCbVorgangTypes());

        StringBuilder automationNotAllowed = new StringBuilder();
        StringBuilder occuredErrors = new StringBuilder();

        for (WitaCBVorgang witaCbVorgang : witaCbVorgaenge) {
            try {
                final Long refId = getCbVorgangRefId(witaCbVorgang);
                boolean isHvtKvz = refId != null;
                if (isHvtKvz) {
                    // Fuer HVt nach KVz wird der Vorgang nur dann geschlossen, wenn auch der referenzierte Vorgang
                    // geschlossen werden kann oder bereits geschlossen wurde. Hier wird nur der eine Vorgang
                    // abgeschlossen. Das Abschließen des anderen Vorgangs passiert in dem anderen Job
                    // (siehe {@code de.mnet.hurrican.scheduler.job.wita.AutomaticallyProcessWitaOrdersJob}
                    // und {@code de.mnet.hurrican.scheduler.job.wita.AutomaticallyProcessWitaCancellationsJob}
                    final WitaCBVorgang witaCbVorgangRef = (WitaCBVorgang) carrierElTalService.findCBVorgang(refId);
                    if (canBothOrdersBeClosed(witaCbVorgang, witaCbVorgangRef)) {
                        closeWitaAutomatically(witaCbVorgang, context);
                    }
                    else {
                        automationNotAllowed
                                .append("Die HVt-KVz-Aufträge konnten nicht automatisch abgeschlossen werden: ")
                                .append(SystemUtils.LINE_SEPARATOR);
                        automationNotAllowed.append(String.format("Auftrags-Id: %s (CBVorgang-Id: %s)",
                                witaCbVorgang.getAuftragId(),
                                witaCbVorgang.getId()));
                        automationNotAllowed.append(SystemUtils.LINE_SEPARATOR);
                        automationNotAllowed.append(String.format("Auftrags-Id: %s (CBVorgang-Id: %s)",
                                witaCbVorgangRef.getAuftragId(),
                                witaCbVorgangRef.getId()));
                        automationNotAllowed.append(SystemUtils.LINE_SEPARATOR);
                    }
                }
                else if (witaTalOrderService.isAutomationAllowed(witaCbVorgang)) {
                    // Check auf hasAutomationErrors NICHT mit isAutomationAllowed kombinieren!
                    // Eintrag in eMail soll nur erfolgen, wenn der Vorgang nicht automatisiert abgeschlossen werden
                    // kann; nicht, wenn
                    // durch einen vorhergenden Abschluss schon Automatisierungsfehler vorhanden sind!
                    if (!witaCbVorgang.hasAutomationErrors()) {
                        closeWitaAutomatically(witaCbVorgang, context);
                    }
                }
                else {
                    automationNotAllowed.append(String.format("Auftrags-Id: %s (CBVorgang-Id: %s)",
                            witaCbVorgang.getAuftragId(),
                            witaCbVorgang.getId()));
                    automationNotAllowed.append(SystemUtils.LINE_SEPARATOR);
                }
            }
            catch (Exception e) {
                String msg = String.format("error closing WitaCbVorgang (Id=%s) - AuftragsId=%s, Typ=%s - Message: %s",
                        witaCbVorgang.getId(), witaCbVorgang.getAuftragId(), witaCbVorgang.getTyp(), e.getMessage());
                LOGGER.warn(msg, e);
                occuredErrors.append(String.format("%s%n", msg));
                occuredErrors.append(SystemUtils.LINE_SEPARATOR);
                writeAutomationError(witaCbVorgang, e, occuredErrors);
            }
        }

        if ((automationNotAllowed.length() > 0) || (occuredErrors.length() > 0)) {
            sendMailWithWarningsAndErrors(context, automationNotAllowed, occuredErrors, true);
        }
    }

    /**
     * Prüft, ob beide Vorgänge geschlossen werden können. Da die Abarbeitung der Kündigungen und Neubestellungen von
     * getrennten Jobs erledigt wird, kann das Schließen der beiden Vorgänge nicht innerhalb einer Transaktion
     * passieren. Deswegen kann es vorkommen, dass der referenzierte Vorgang bereits geschlossen wurde und das darf das
     * Abschließen vom aktuellen Vorgang nicht verhindern.
     *
     * @param witaCbVorgang
     * @param witaCbVorgangRef
     * @return
     * @throws FindException
     */
    boolean canBothOrdersBeClosed(WitaCBVorgang witaCbVorgang, WitaCBVorgang witaCbVorgangRef) throws FindException {
        return witaTalOrderService.isAutomationAllowed(witaCbVorgang)
                && !witaCbVorgang.hasAutomationErrors()
                && (CBVorgang.STATUS_CLOSED.equals(witaCbVorgangRef.getStatus())
                || (witaTalOrderService.isAutomationAllowed(witaCbVorgangRef)
                && !witaCbVorgangRef.hasAutomationErrors()));
    }

    Long getCbVorgangRefId(WitaCBVorgang cbVorgang) {
        if (CBVorgang.TYP_NEU.equals(cbVorgang.getTyp())) {
            return cbVorgang.getCbVorgangRefId();
        }
        else if (CBVorgang.TYP_KUENDIGUNG.equals(cbVorgang.getTyp())) {
            final WitaCBVorgang witaCBVorgangByRefId = witaTalOrderService.findWitaCBVorgangByRefId(cbVorgang.getId());
            return witaCBVorgangByRefId != null ? witaCBVorgangByRefId.getId() : null;
        }
        return null;
    }

    protected void writeAutomationError(WitaCBVorgang witaCbVorgang, Exception automationError, StringBuilder occuredErrors) {
        try {
            witaCbVorgang.addAutomationError(automationError);
            carrierElTalService.saveCBVorgang(witaCbVorgang);
        }
        catch (StoreException e) {
            LOGGER.error(e.getMessage(), e);
            occuredErrors.append(String.format("Error writing AutomationError for WitaCbVorgang with Id %s", witaCbVorgang.getId()));
            occuredErrors.append(SystemUtils.LINE_SEPARATOR);
            // do not throw any exceptions!
        }
    }


    protected void sendMailWithWarningsAndErrors(JobExecutionContext context, StringBuilder automationNotAllowed,
            StringBuilder occuredErrors, boolean cancellation) {
        String errors = String.format("Folgende Fehler traten auf:%n%s", occuredErrors);
        String warnings = String.format(
                "Folgende WitaCBVorgaenge konnten nicht automatisch %s werden (Automatisierung nicht erlaubt):%n%s ",
                cancellation ? "gekuendigt" : "abgeschlossen",
                automationNotAllowed.toString());
        StringBuilder mailText = new StringBuilder();
        mailText.append(String.format("Errors:%n"));
        mailText.append(String.format("=====================%n"));
        mailText.append(errors);
        mailText.append(String.format("Warnings:%n"));
        mailText.append(String.format("=====================%n"));
        mailText.append(warnings);

        new SendMailJobWarningsHandler().handleWarnings(context,
                new AKWarnings().addAKWarning(this, mailText.toString()));
    }

    private void initServices() throws JobExecutionException {
        try {
            billingAuftragService = getBillingService(BillingAuftragService.class);
            baService = getCCService(BAService.class);
            auftragService = getCCService(CCAuftragService.class);
            carrierService = getCCService(CarrierService.class);
            carrierElTalService = getService(CarrierElTALService.class);
            elektraFacadeService = getService(ElektraFacadeService.class);
            witaTalOrderService = getService(WitaTalOrderService.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new JobExecutionException(e.getMessage());
        }
    }
}
