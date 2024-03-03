/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.13
 */
package de.augustakom.common.service.holiday;

import static de.augustakom.common.service.holiday.HolidayHelper.*;

import java.time.*;
import java.time.temporal.*;
import javax.validation.constraints.*;

/**
 *
 */
public class DateCalculationHelper {

    /**
     * Enumeration which defines all possible calculation modes used by the calculation of the customer change date
     */
    public enum DateCalculationMode {
        ALL, // all days will be considered
        WEEKDAYS, // only weekdays (saturday and sunday are excluded) will be considered
        WORKINGDAYS // only working days (holidays, saturday and sunday are excluded) will be considered
    }

    /**
     * Retrieves the number of days between the provided dates based on the provided calculation mode. If the second
     * date is before the first date, 0 will be returned.
     */
    public static int getDaysBetween(LocalDate first, LocalDate second, @NotNull DateCalculationMode calculationMode) {
        int workingDaysDifference = 0;
        if (second.isAfter(first)) {
            int daysBetween = (int)ChronoUnit.DAYS.between(first, second);
            if (calculationMode == DateCalculationMode.ALL) {
                return daysBetween;
            }
            for (int i = 0; i < daysBetween; i++) {
                workingDaysDifference = workingDaysDifference
                        + (isValidDay(first.plusDays(i + 1), calculationMode) ? 1 : 0);
            }
        }
        return workingDaysDifference;
    }

    /**
     * Retrieves the number of days between the provided dates based on the provided calculation mode. If the second
     * date is before the first date, then returns the number of days between both dates but with negative sign.
     */
    public static int getDifferenceInDays(LocalDate first, LocalDate second, @NotNull DateCalculationMode calculationMode) {
        if (second.isAfter(first)) {
            return getDaysBetween(first, second, calculationMode);
        }
        else {
            return getDaysBetween(second, first, calculationMode) * (-1);
        }
    }

    /**
     * Gibt an, ob sich das zu ueberpruefende Datum in der uebergebenen Zeitspanne befindet. <br/> Wenn das
     * ueberpruefende Datum mit einem der "Grenztage" der Zeitspanne uebereinstimmt, ist der Tag in der Spanne enthalten
     * -> return true
     */
    public static boolean isDateBetween(LocalDate dateToCheck, LocalDate from, LocalDate to) {
        return (dateToCheck.isBefore(to) && dateToCheck.isAfter(from)) || dateToCheck.isEqual(to)
                || dateToCheck.isEqual(from);
    }

    /**
     * @return true if supplied {@code dateToCheck} is a weekday (Mo - Fr). Holidays are not considered!
     */
    public static boolean isWeekday(LocalDate dateToCheck) {
        return !(dateToCheck.getDayOfWeek().equals(DayOfWeek.SATURDAY)
                || dateToCheck.getDayOfWeek().equals(DayOfWeek.SUNDAY));
    }

    // Sonderbehandlung HUR-27489 24.3.2017 ist schaltfreierTag
    private static boolean isSchaltfreiday(LocalDate dateToCheck) {
        return LocalDate.of(2017,3,24).equals(dateToCheck);
    }

    /**
     * Checks whether the provided day is a working day (holidays, saturday and sunday are not working days)
     */
    public static boolean isWorkingDay(LocalDate date) {
        return isWeekday(date) && !isSchaltfreiday(date) && !isAugsburgHoliday(date);
    }

    /**
     * Checks whether the day after the provided day is not a holiday
     */
    public static boolean isNextDayNotAHoliday(LocalDate date) {
        return !isAugsburgHoliday(date.plusDays(1));
    }

    /**
     * @return the next Day based from the current date in x Days
     */
    public static LocalDateTime getDateInWorkingDaysFromNow(int days) {
        LocalDateTime baseDate = LocalDateTime.now();
        return getDateInWorkingDays(baseDate, days);
    }

    /**
     * @return the next Day based from the supplied date in x Days
     */
    private static LocalDateTime getDateInWorkingDays(final LocalDateTime baseDate, final int days) {
        LocalDateTime date = baseDate;
        while (!isWorkingDay(date.toLocalDate())) {
            date = addWorkingDays(date, 1);
        }
        return addWorkingDays(date, days);
    }

    static LocalDate getDateInWorkingDays(final LocalDate baseDate, final int days) {
        return getDateInWorkingDays(baseDate.atStartOfDay(), days).toLocalDate();
    }

    /**
     * Calculates the date using the current date plus the supplied number of workingdays. If the calculated date is a
     * working day and does not lie directly before a holiday, then this is returned. Otherwise the calculated date is
     * incremented until a valid working day is found.
     * @return the calculated date
     */
    public static LocalDate getDateInWorkingDaysFromNowAndNextDayNotHoliday(int workingdays) {
        return asWorkingDayAndNextDayNotHoliday(getDateInWorkingDaysFromNow(workingdays).toLocalDate());
    }

    /**
     * Calculates the date using the supplied {@code basedate} plus the supplied number of {@code workingdays}.
     * If the calculated date is a working day and does not lie directly before a holiday, then this is returned.
     * Otherwise the calculated date is incremented until a valid working day is found.
     * @return the calculated date
     */
    public static LocalDate getDateInWorkingDaysAndNextDayNotHoliday(LocalDate baseDate, int workingdays) {
        return asWorkingDayAndNextDayNotHoliday(getDateInWorkingDays(baseDate, workingdays));
    }

    /**
     * Add to a given Date a number of workdays.
     *
     * @param date base date
     * @param days count of workdays, which should be added to the date
     */
    public static LocalDateTime addWorkingDays(LocalDateTime date, int days) {
        int workDays = 0;
        int nonWorkDays = 0;

        final int absDays = Math.abs(days);
        final int sign = days != 0 ? days / absDays : 1;
        for (int i = 1; Math.abs(workDays) < absDays; i++) {
            if (isWorkingDay(date.toLocalDate().plusDays(i * sign))) {
                workDays = workDays + sign;
            }
            else {
                nonWorkDays = nonWorkDays + sign;
            }
        }
        return date.plusDays(workDays + nonWorkDays);
    }
    public static LocalDate addWorkingDays(LocalDate date, int days) {
        return addWorkingDays(date.atStartOfDay(), days).toLocalDate();
    }

    /**
     * Checks if the provided date is a working day. If so the date is returned, otherwise the next working day will be
     * retrieved and returned.
     *
     * @param dateToCheck  the date to check for saturday/sunday/holiday
     * @return Date of today, if day is working day or next working day
     */
    public static LocalDate asWorkingDay(LocalDate dateToCheck) {
        if (DateCalculationHelper.isWorkingDay(dateToCheck)) {
            return dateToCheck;
        }
        return asWorkingDay(dateToCheck.plusDays(1));
    }

    /**
     * Checks that the supplied date is a working day and the next day is <b>not</b> a holiday. If the check is
     * successful the supplied date is returned, otherwise the next valid working day is calculated and returned.
     *
     * @param dateToCheck the date to check for saturday/sunday/holiday
     * @return {@code dateToCheck} if the check is ok, otherwise the next valid working day
     */
    public static LocalDate asWorkingDayAndNextDayNotHoliday(LocalDate dateToCheck) {
        if (isWorkingDayAndNextDayNotHoliday(dateToCheck)) {
            return dateToCheck;
        }
        return asWorkingDayAndNextDayNotHoliday(dateToCheck.plusDays(1));
    }


    /**
     * Checks that the supplied date is a working day and the next day is <b>not</b> a holiday.
     * <br />
     * Note: Checking dates that lie on a Friday usually returns true as long as the Saturday is not a holiday.
     * @param dateToCheck  the date to check
     * @return true if the check succeeds, otherwise false
     */
    public static boolean isWorkingDayAndNextDayNotHoliday(LocalDate dateToCheck) {
        return isWorkingDay(dateToCheck) && isNextDayNotAHoliday(dateToCheck);
    }

    private static boolean isValidDay(LocalDate date, @NotNull DateCalculationMode calculationMode) {
        switch (calculationMode) {
            case WEEKDAYS:
                return isWeekday(date);
            case WORKINGDAYS:
                return isWorkingDay(date);
            default:
                return true;
        }
    }

}
