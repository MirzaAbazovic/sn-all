/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.14
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static de.augustakom.hurrican.model.cc.ffm.FfmProductMapping.*;
import static de.augustakom.hurrican.service.cc.RegistryService.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.regex.*;
import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.RequestedTimeslotBuilder;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragWholesale;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.enums.FfmTyp;
import de.augustakom.hurrican.model.cc.view.TimeSlotHolder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.RegistryService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

// @formatter:off
/**
 * Command-Klasse, die fuer die 'Header' Daten einer FFM {@link WorkforceOrder} folgende Daten aggregiert: <br/>
 * <ul>
 *   <li>Timeslot aus Taifun</li>
 * </ul>
 */
// @formatter:on
@Component("de.augustakom.hurrican.service.cc.ffm.command.AggregateFfmHeaderTimeslotCommand")
@Scope("prototype")
public class AggregateFfmHeaderTimeslotCommand extends AbstractFfmCommand {

    private static final Logger LOGGER = Logger.getLogger(AggregateFfmHeaderTimeslotCommand.class);

    @Resource(name = "de.augustakom.hurrican.service.billing.BillingAuftragService")
    private BillingAuftragService billingAuftragService;
    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    private BAService baService;

    @Resource(name = "de.augustakom.hurrican.service.cc.RegistryService")
    private RegistryService registryService;

    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    private CCAuftragService auftragService;

    @Override
    public Object execute() throws Exception {
        try {
            //Fall Wholesale
            Optional<AuftragDaten> optionalAuftragDaten = Optional.ofNullable(getAuftragDaten());
            if (optionalAuftragDaten.isPresent() && optionalAuftragDaten.get().isWholesaleAuftrag()) {
                setWorkforceOrderTimeSlot4Wholesale(optionalAuftragDaten.get().getAuftragId());
            }
            else {

                Optional<Verlauf> bauauftrag = getBauauftrag();
                if (bauauftrag.isPresent()) {
                    Verlauf verlauf = bauauftrag.get();
                    VerlaufAbteilung verlaufAbteilungFfm = baService.findVerlaufAbteilung(verlauf.getId(), Abteilung.FFM);
                    if (verlaufAbteilungFfm.getRealisierungsdatum() != null
                            && !verlaufAbteilungFfm.getRealisierungsdatum().equals(verlauf.getRealisierungstermin())) {
                        setRequestedTimeSlot(DateConverterUtils.asLocalDateTime(verlaufAbteilungFfm.getRealisierungsdatum()),
                                REGID_FFM_TIMESLOT_MANUAL_FROM, REGID_FFM_TIMESLOT_MANUAL_TO);
                    }
                    else {
                        setWorkforceOrderTimeSlot();
                    }
                }
                else {
                    setWorkforceOrderTimeSlot();
                }

            }

            return ServiceCommandResult.createCmdResult(
                    ServiceCommandResult.CHECK_STATUS_OK, null, this.getClass());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ServiceCommandResult.createCmdResult(ServiceCommandResult.CHECK_STATUS_INVALID,
                    "Error loading FFM Header Timeslot Data: " + e.getMessage(), this.getClass());
        }
    }

    void setWorkforceOrderTimeSlot() throws FindException {
        FfmTyp baFfmTyp = getFfmProductMapping().getBaFfmTyp();
        if (FfmTyp.KUENDIGUNG == baFfmTyp) {
            setRequestedTimeSlot(REGID_FFM_TIMESLOT_KUENDIGUNG_FROM, REGID_FFM_TIMESLOT_KUENDIGUNG_TO);
        }
        else {
            setRequestedTimeSlotForNeuAndEntstoerung();
        }
    }

    /**
     * Das Realisierungsdatum des Bauauftrags hat Vorrang vor dem TAL/Taifun Datum. Sollte dann das TAL/Taifun Datum mit
     * dem Realisierungsdatum übereinstimmen (gleicher Tag), uebersteuern die TAL/Taifun TimeSlots die standardmaessigen
     * FFM TimeSlots. Sollte es sowohl TAL sowie Taifun Timeslots geben, bevorzugt Hurrican die Variante die mit dem
     * Realisierungsdatum match. Matchen TAL sowie Taifun wird die TAL bevorzugt.
     */
    void setRequestedTimeSlotForNeuAndEntstoerung() throws FindException {
        TimeSlotHolder timeSlotHolder = baService.getTimeSlotHolder(getAuftragDaten().getAuftragId());

        LocalDateTime realisierungsTermin = getReferenceDate();
        LocalDate realisierungsDatum = (realisierungsTermin != null) ? realisierungsTermin.toLocalDate() : null;
        final TimeSlotHolder.Candidate candidate = (timeSlotHolder.isPresent())
                ? timeSlotHolder.matchCandidate(realisierungsDatum) : TimeSlotHolder.Candidate.None;

        if (candidate != TimeSlotHolder.Candidate.None) {
            LocalDateTime from = LocalDateTime.of(realisierungsDatum, timeSlotHolder.getTimeSlotToUseDaytimeFrom(candidate));
            LocalDateTime to = LocalDateTime.of(realisierungsDatum, timeSlotHolder.getTimeSlotToUseDaytimeTo(candidate));
            setRequestedTimeSlot(from, to);
        }
        else {
            String ffmActivityType = getFfmProductMapping().getFfmActivityType();
            if (FFM_ACTIVITY_TYPE_NEU_IK.equalsIgnoreCase(ffmActivityType) ||
                    FFM_ACTIVITY_TYPE_INT.equalsIgnoreCase(ffmActivityType)) {
                setRequestedTimeSlot(REGID_FFM_TIMESLOT_INT_AND_IK_FROM, REGID_FFM_TIMESLOT_INT_AND_IK_TO);
            }
            else {
                setRequestedTimeSlot(REGID_FFM_TIMESLOT_DEFAULT_FROM, REGID_FFM_TIMESLOT_DEFAULT_TO);
            }
        }
    }

    void setRequestedTimeSlot(LocalDateTime referenceDate, Long registryIdFrom, Long registryIdTo) throws FindException {
        LocalDateTime refDate = referenceDate == null ? getReferenceDate() : referenceDate;
        LocalDateTime timeFrom = getTimeFromRegistry(registryIdFrom);
        LocalDateTime from = getReferenceDateWithTime(refDate, timeFrom);
        LocalDateTime timeTo = getTimeFromRegistry(registryIdTo);
        LocalDateTime to = getReferenceDateWithTime(refDate, timeTo);
        setRequestedTimeSlot(from, to);
    }

    void setRequestedTimeSlot(Long registryIdFrom, Long registryIdTo) throws FindException {
        setRequestedTimeSlot(null, registryIdFrom, registryIdTo);
    }

    void setRequestedTimeSlot(final LocalDateTime from, final LocalDateTime to) {
        // damit die Dispatch-Funktion im FFM korrekt funktioniert muss eine Minute abgezogen werden!
        LocalDateTime toFfm = to.minusMinutes(1);
        getWorkforceOrder().setRequestedTimeSlot(new RequestedTimeslotBuilder()
                .withEarliestStart(from)
                .withLatestStart(toFfm)
                .withLatestEnd(toFfm.plusMinutes(getPlannedDuration()))
                .build());
    }

    LocalDateTime getTimeFromRegistry(Long registryId) throws FindException {
        String stringValue = registryService.getStringValue(registryId);
        try {

            final Pattern p = Pattern.compile("^[0-9]:[0-9][0-9]$");
            if (p.matcher(stringValue).find()) {
                // the special case "8:00" transforming into "08:00"
                stringValue = "0".concat(stringValue);
            }

            // LocalDate.ofEpochDay(0) took it because of a case in UTs
            return LocalDateTime.of(LocalDate.ofEpochDay(0), LocalTime.parse(stringValue, DateTimeFormatter.ofPattern(DateTools.PATTERN_TIME)));
        }
        catch (DateTimeParseException e) {
            final String msg = String.format("cannot parse wrong time string: %s", stringValue);
            LOGGER.warn(msg, e);
            throw new IllegalArgumentException(msg, e);
        }
    }

    LocalDateTime getReferenceDateWithTime(LocalDateTime referenceDate, LocalDateTime time) {
        return referenceDate
                .withHour(time.getHour())
                .withMinute(time.getMinute())
                .withSecond(0)
                .withNano(0);
    }

    void setWorkforceOrderTimeSlot4Wholesale(Long auftragId) throws FindException {
        AuftragWholesale auftragWholesale = auftragService.findAuftragWholesaleByAuftragId(auftragId);
        if (auftragWholesale == null) {
            throw new FindException(String.format("Couldn't find AuftragWholesale for auftragId '%s'", auftragId));
        }
        LocalDate ffmExecutionDate = determineCurrentExecutiondate(auftragWholesale.getExecutionDate());
        LocalDateTime ffmBegin = LocalDateTime.of(ffmExecutionDate, auftragWholesale.getExecutionTimeBegin());
        LocalDateTime ffmEnd = LocalDateTime.of(ffmExecutionDate, auftragWholesale.getExecutionTimeEnd());
        setRequestedTimeSlot(ffmBegin, ffmEnd);
    }

    private LocalDate determineCurrentExecutiondate(LocalDate wsLocalDate) throws FindException {
        if (!getBauauftrag().isPresent()) {
            return wsLocalDate;
        }
        VerlaufAbteilung verlaufAbteilungFfm = baService.findVerlaufAbteilung(getBauauftrag().get().getId(), Abteilung.FFM);
        return (verlaufAbteilungFfm != null && verlaufAbteilungFfm.getRealisierungsdatum() != null
                && !DateConverterUtils.asLocalDate(verlaufAbteilungFfm.getRealisierungsdatum()).equals(wsLocalDate))
                ? DateConverterUtils.asLocalDate(verlaufAbteilungFfm.getRealisierungsdatum()) : wsLocalDate;
    }
}
