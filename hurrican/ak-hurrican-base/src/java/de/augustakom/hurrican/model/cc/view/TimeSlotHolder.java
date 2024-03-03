/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2015
 */
package de.augustakom.hurrican.model.cc.view;

import java.io.*;
import java.time.*;
import java.util.*;
import javax.annotation.*;

import de.augustakom.hurrican.model.billing.view.TimeSlotView;
import de.augustakom.hurrican.model.cc.tal.TalRealisierungsZeitfenster;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Eine View um einen TAL- und einen Billing-Timeslot zu halten. Die "getTimeSlotToUse..." Methoden geben, wenn
 * vorhanden immer die TAL vor dem Billingtimeslot zurueck.
 */
public class TimeSlotHolder implements Serializable {
    private TalRealisierungsZeitfenster talRealisierungsZeitfenster; //Carrierbestellung Hurrican
    private TimeSlotView timeSlotView; // Zeitfenster aus Taifun
    private LocalDate talRealisierungsDay;

    public enum Candidate {
        None,
        TAL,
        BILLING
    }

    public void setTimeSlotView(TimeSlotView billingTimeSlot) {
        this.timeSlotView = billingTimeSlot;
    }

    public void setTalRealisierungsZeitfenster(TalRealisierungsZeitfenster talRealisierungsZeitfenster) {
        this.talRealisierungsZeitfenster = talRealisierungsZeitfenster;
    }

    public void setTalRealisierungsDay(Date talRealisierungsDay) {
        this.talRealisierungsDay = DateConverterUtils.asLocalDate(talRealisierungsDay);
    }

    /**
     * @return true wenn ein ZF gesetzt ist (egal aus welcher Quelle)
     */
    public boolean isPresent() {
        return timeSlotView != null || (talRealisierungsDay != null && talRealisierungsZeitfenster != null);
    }

    public String getTimeSlotToUseAsString() {
        if (talRealisierungsZeitfenster != null) {
            return talRealisierungsZeitfenster.getMnetTechnikerDisplayText();
        }
        else if (timeSlotView != null) {
            return timeSlotView.getTimeSlotAsStringOnlyTime();
        }
        return null;
    }

    /**
     * Gleicht das Realisierungsdatum mit den TAL/Taifun Zeitfenster ab. Dabei muss das Realisierungsdatum mit mindestens
     * einem Zeitfenster uebereinstimmen. Sollten beide Zeitfenster uebereinstimmen, gewinnt TAL. Stimmt keines ueberein
     * bzw. existiert gar kein Zeitfenster, so liefert diese Methode {@code none}.
     */
    public Candidate matchCandidate(LocalDate realisierungDatum) {
        if (realisierungDatum != null) {
            // TAL zuerst pruefen
            if (talRealisierungsDay != null && talRealisierungsZeitfenster != null
                    && realisierungDatum.isEqual(talRealisierungsDay)) {
                return Candidate.TAL;
            }
            if (timeSlotView != null
                    && timeSlotView.getDate() != null
                    && timeSlotView.getDaytimeFrom() != null
                    && timeSlotView.getDaytimeTo() != null
                    && realisierungDatum.isEqual(DateConverterUtils.asLocalDate(timeSlotView.getDate()))) {
                return Candidate.BILLING;
            }
        }
        return Candidate.None;
    }

    @Nullable
    public LocalTime getTimeSlotToUseDaytimeFrom(Candidate candidate) {
        if (candidate != null) {
            if (candidate == Candidate.TAL) {
                return (talRealisierungsZeitfenster != null)
                        ? talRealisierungsZeitfenster.getMnetTechnikerVonZF(): null;
            }
            else if (candidate == Candidate.BILLING) {
                return (timeSlotView != null)
                        ? DateConverterUtils.asLocalTime(timeSlotView.getDaytimeFrom()) : null;
            }
        }
        return null;
    }

    @Nullable
    public LocalTime getTimeSlotToUseDaytimeTo(Candidate candidate) {
        if (candidate != null) {
            if (candidate == Candidate.TAL) {
                return (talRealisierungsZeitfenster != null)
                        ? talRealisierungsZeitfenster.getMnetTechnikerBisZF() : null;
            }
            else if (candidate == Candidate.BILLING) {
                return (timeSlotView != null) ? DateConverterUtils.asLocalTime(timeSlotView.getDaytimeTo()): null;
            }
        }
        return null;
    }
}
