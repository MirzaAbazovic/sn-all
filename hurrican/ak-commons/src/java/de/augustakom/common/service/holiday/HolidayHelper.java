/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.13
 */
package de.augustakom.common.service.holiday;

import java.time.*;
import java.util.*;
import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;

/**
 * Holiday check based on the Jollyday API
 *
 *
 */
public final class HolidayHelper {

    private HolidayHelper() {
        throw new IllegalAccessError("Utility class");
    }

    private static HolidayManager getHolidayManager() {
        // since manager caching is enabled per default, we don't additionally need to cache the manager instances
        return HolidayManager.getInstance(HolidayCalendar.GERMANY);
    }

    /**
     * @return true if supplied {@code date} is a german holiday.
     */
    static boolean isGermanHoliday(LocalDate date) {
        return containsDate(getHolidayManager().getHolidays(date.getYear()), date);
    }

    /**
     * @return true if supplied {@code date} is a holiday in Bayern. We have to use Munich as configuration property,
     * because 15.08 (Mariahimmelfahrt) is not a bayern holiday according to joliday, which in fact is not correct.
     */
    static boolean isBayernHoliday(LocalDate date) {
        return containsDate(getHolidayManager().getHolidays(date.getYear(), "by", "mu"), date);
    }

    /**
     * @return true if supplied {@code date} is a holiday in Bayern AND Augsburg. The Augsburger Friedensfest (08.08) is
     * also considered!
     * HUR-27489 zusätzlicher Schaltungsfreiertag: 24.03.2017, Reformationsfest 31.10 ist 2017 ebenfalls deutschlandweit Feiertag
     */
    public static boolean isAugsburgHoliday(LocalDate date) {
        return containsSpecialDate(date) || containsDate(getHolidayManager().getHolidays(date.getYear(), "by", "ag"), date);
    }

    private static boolean containsDate(Set<Holiday> holidays, LocalDate date) {
        for (Holiday holiday : holidays) {
            if (holiday.getDate().compareTo(date) == 0) {
                return true;
            }
        }
        return false;
    }

    // Sonderbehandlung HUR-27489: Refomationstag ist 2017 in ganz Deutschland Feiertag
    private static boolean containsSpecialDate(LocalDate date) {
        return LocalDate.of(2017,10,31).equals(date) ;
    }

}
