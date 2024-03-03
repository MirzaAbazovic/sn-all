/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2011 16:17:40
 */
package de.mnet.wita.service.impl;

import java.time.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.config.WitaConstants;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.service.WitaConfigService;

/**
 * Hilfsklasse fuer DateTime Objekte.
 */
public class DateTimeCalculationService implements WitaConstants {

    /**
     * public for testing
     */
    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    public ReferenceService referenceService;

    @Autowired
    protected WitaConfigService witaConfigService;

    /**
     * Prueft, ob ein Storno zum aktuellen Zeitpunkt noch moeglich ist.
     */
    public boolean isStornoPossible(Date realisierungsDate, Zeitfenster zeitFenster) {
        return isStornoOrTvPossible(realisierungsDate, zeitFenster);
    }

    /**
     * Prueft, ob die Terminverschiebung zum aktuellen Zeitpunkt noch moeglich ist.
     */
    public boolean isTerminverschiebungPossible(Date realisierungsDate, Zeitfenster zeitFenster) {
        return isStornoOrTvPossible(realisierungsDate, zeitFenster);
    }

    private boolean isStornoOrTvPossible(Date realisierungsDate, Zeitfenster zeitFenster) {
        boolean possible = false;
        if (realisierungsDate != null) {
            possible = isTimelyBeforeVLT(LocalDateTime.now(), Instant.ofEpochMilli(realisierungsDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    zeitFenster,
                    getPufferzeitInHours());
        }
        return possible;
    }

    /**
     * Ermittelt, ob sich der Wunschtermin in dem Zeitfenster befindet (bezogen auf den Wochentag)
     */
    public boolean isKwtDayInZeitfenster(Date realisierungsDate, Zeitfenster zeitFenster) {
        LocalDate realsierung = DateConverterUtils.asLocalDate(realisierungsDate);

        if (zeitFenster == null) {
            return true;
        }
        if ((realisierungsDate != null) && (realsierung.getDayOfWeek().getValue() >= zeitFenster.fromDayOfWeek)
                && (realsierung.getDayOfWeek().getValue() <= zeitFenster.toDayOfWeek)) {
            return true;
        }
        return false;
    }

    /**
     * Ermittelt die zusaetzliche Pufferzeit zu den 36h aus der T_REGISTRY.
     */
    public int getPufferzeitInHours() {
        try {
            Reference witaVltPufferzeitReference = Iterables.getOnlyElement(referenceService.findReferencesByType(
                    Reference.REF_TYPE_WITA_VLT_PUFFERZEIT, Boolean.FALSE));
            return witaVltPufferzeitReference.getIntValue();
        }
        catch (FindException e) {
            throw new WitaBaseException(e);
        }
    }

    /**
     * Prueft, ob der gewuenschte neue Termin ({@code terminVerschiebung}) gueltig ist; dies ist dann der Fall, wenn die
     * Mindestvorlaufzeit eingehalten wird und der gewuenschte Tag im definierten Zeitfenster ist.
     */
    public boolean isTerminverschiebungValid(LocalDate neuerTermin, Zeitfenster zeitfenster, boolean hasTam,
            GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId, boolean isHvtToKvz) {
        if (neuerTermin != null) {
            return isKwtDayInZeitfenster(Date.from(neuerTermin.atStartOfDay(ZoneId.systemDefault()).toInstant()), zeitfenster)
                    && isKundenwunschTerminValid(LocalDateTime.now(), DateConverterUtils.asLocalDateTime(neuerTermin),
                    hasTam, Boolean.TRUE, geschaeftsfallTyp, vorabstimmungsId, isHvtToKvz);
        }
        return false;
    }

    /**
     * Prueft, ob eine Aktion (TV/Storno) rechtzeitig vor dem verbindlichen Liefertermin durchgefuehrt wird. Dies ist
     * der Fall, wenn der VLT min. 36 Stunden (abzgl. Sa/So/Feiertage) in der Zukunft liegt (Zeitfenster wird beachtet).
     * Fuer die Berechnung der Stunden werden nur Arbeitstage verwendet.
     */
    boolean isTimelyBeforeVLT(LocalDateTime ausloeseZeitpunkt, LocalDateTime verbindlicherLieferTermin, Zeitfenster zeitFenster,
            int puffer) {
        int weekDaysInFuture = getWorkingDaysBetween(ausloeseZeitpunkt, verbindlicherLieferTermin);
        int fullDaysBetween = weekDaysInFuture - 1;

        long hoursBetween = fullDaysBetween * 24;
        if (DateCalculationHelper.isWorkingDay(ausloeseZeitpunkt.toLocalDate())) {
            hoursBetween += 24 - ausloeseZeitpunkt.getHour();
        }
        if (DateCalculationHelper.isWorkingDay(verbindlicherLieferTermin.toLocalDate()) && (zeitFenster != null)) {
            hoursBetween += zeitFenster.beginnZeitfenster.getHour();
        }

        return hoursBetween > (36 + puffer);
    }

    /**
     * Prueft, ob der angegebene Kundenwunschtermin i.O. ist. Dies ist dann der Fall, wenn der Termin ein Arbeitstag ist
     * und min. X Tage in der Zukunft liegt. X ist dabei abhaengig davon, ob das Flag {@code hasTam} gesetzt ist oder
     * nicht. Falls checkNextDay gesetzt ist, muss zusaetzlich auch der darauffolgende Tag ein Arbeitstag sein, damit
     * der angegebene Kundenwunschtermin gueltig ist. <br/> <br/> Ausserdem wird in bestimmten Faellen noch geprueft, ob
     * der Tag nach dem KWT ein Arbeitstag ist (z.B. bei Neuschaltungen mit WBCI VA-Id bzw. bei
     * Anbieterwechsel-Faellen).
     * <p/>
     * Flag=false: X = {@link WitaConstants#MINDESTVORLAUFZEIT} Flag=true: X = {@link
     * WitaConstants#MINDESTVORLAUFZEIT_NACH_TAM}
     */
    public boolean isKundenwunschTerminValid(LocalDateTime now, LocalDateTime kundenwunschtermin, Boolean hasTam, Boolean isTv,
            GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId, boolean isHvtToKvz) {
        int daysToCheck = getMindestvorlaufzeit(hasTam, isTv, isHvtToKvz);
        final LocalDate kwtAsLocalDate = kundenwunschtermin.toLocalDate();

        boolean isKwtValid = DateCalculationHelper.isWorkingDay(kwtAsLocalDate)
                && (getWorkingDaysBetween(now, kundenwunschtermin) >= daysToCheck);

        if (isKwtValid && hasToCheckNextDay(geschaeftsfallTyp, vorabstimmungsId)) {
            return DateCalculationHelper.isNextDayNotAHoliday(kwtAsLocalDate);
        }
        return isKwtValid;
    }

    private int getMindestvorlaufzeit(boolean hasTam, boolean isTv, boolean isHvtToKvz) {
        if (BooleanTools.nullToFalse(hasTam)) {
            return MINDESTVORLAUFZEIT_NACH_TAM;
        }
        else if (isTv) {
            return MINDESTVORLAUFZEIT;
        }
        else {
            return isHvtToKvz ? MINDESTVORLAUFZEIT_HVT_NACH_KVZ : MINDESTVORLAUFZEIT;
        }
    }

    /**
     * Ermittlet, ob der darauffolgende Tag auch auf Arbeitstag geprüft werden muss. Das ist der Fall für PV, VBL,
     * KUENDIGUNG und BEREITSTELLUNG. Bei KUENDIGUNG und BEREITSTELLUNG muss zusaetzlich ein VorabstimmungsId gesetzt
     * werden.
     *
     * @param geschaeftsfallTyp Der Geschaeftsfalltyp
     * @param vorabstimmungsId  Die VorabstimmungsId, falls eine vorhanden ist.
     * @return
     */
    boolean hasToCheckNextDay(GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId) {
        switch (geschaeftsfallTyp) {
            case PROVIDERWECHSEL:
            case VERBUNDLEISTUNG:
                return true;
            case KUENDIGUNG_KUNDE:
            case BEREITSTELLUNG:
                return !StringUtils.isEmpty(vorabstimmungsId);
            default:
                return false;
        }
    }

    public LocalDateTime asWorkingDay(LocalDateTime dateToCheck) {
        return DateCalculationHelper.asWorkingDay(dateToCheck.toLocalDate()).atStartOfDay();
    }

    public LocalDateTime getVorgabeDatumTv60(LocalDateTime now, GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId) {
        Preconditions.checkNotNull(now);
        boolean checkNextDayToo = hasToCheckNextDay(geschaeftsfallTyp, vorabstimmungsId);
        LocalDateTime vorgabeDatum = now.plusDays(60);
        return getNextValidWorkingDay(vorgabeDatum, checkNextDayToo);
    }

    public LocalDateTime getVorgabeDatumTv30(LocalDateTime now, GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId) {
        Preconditions.checkNotNull(now);
        boolean checkNextDayToo = hasToCheckNextDay(geschaeftsfallTyp, vorabstimmungsId);
        LocalDateTime vorgabeDatum = now.plusDays(30);
        return getNextValidWorkingDay(vorgabeDatum, checkNextDayToo);
    }

    private LocalDateTime getNextValidWorkingDay(LocalDateTime vorgabeDatum, boolean checkNextDayToo) {
        boolean validDate = false;
        LocalDateTime calculatedDate = vorgabeDatum;
        while (!validDate) {
            validDate = DateCalculationHelper.isWorkingDay(calculatedDate.toLocalDate());
            if (checkNextDayToo) {
                validDate = validDate && DateCalculationHelper.isNextDayNotAHoliday(calculatedDate.toLocalDate());
            }
            if (!validDate) {
                calculatedDate = calculatedDate.plusDays(1);
            }
        }
        return calculatedDate;
    }

    int getWorkingDaysBetween(LocalDateTime today, LocalDateTime dayToCheck) {
        return DateCalculationHelper.getDaysBetween(today.toLocalDate(), dayToCheck.toLocalDate(),
                DateCalculationHelper.DateCalculationMode.WORKINGDAYS);
    }

}
