/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.04.2004
 */
package de.augustakom.common.tools.lang;

import java.text.*;
import java.time.*;
import java.util.*;
import javax.validation.constraints.*;
import org.apache.commons.lang.time.DateUtils;

/**
 * Hilfsklasse fuer Datums-Operationen.
 *
 *
 */
public class DateTools {

    /**
     * Format-Pattern fuer das Datums-Format Jahr-Monat-Tag.
     */
    public static final String PATTERN_YEAR_MONTH_DAY = "yyyy-MM-dd";

    /**
     * Format-Pattern um Tag/Monat/Jahr eines Date-Objekts darzustellen.
     */
    public static final String PATTERN_DAY_MONTH_YEAR = "dd.MM.yyyy";

    /**
     * Format-Pattern um Tag/Monat/Jahr eines Date-Objekts darzustellen.
     */
    public static final String PATTERN_DAY_MONTH_YEAR_HYPHEN = "dd-MM-yyyy";

    /**
     * Format-Pattern, um Tag/Monat/Jahr Stunde/Minute eines Date-Objekts darzustellen.
     */
    public static final String PATTERN_DATE_TIME = "dd.MM.yyyy HH:mm";

    /**
     * Format-Pattern, um Tag/Monat/Jahr Stunde/Minute/Sekunde darzustellen.
     */
    public static final String PATTERN_DATE_TIME_LONG = "dd.MM.yyyy HH:mm:ss";

    /**
     * Format-Pattern, um Tag/Monat/Jahr Stunde/Minute/Sekunde/Millisekunde darzustellen.
     */
    public static final String PATTERN_DATE_TIME_FULL = "dd.MM.yyyy HH:mm:ss:SSS";

    /**
     * Format-Pattern, um Stunde/Minute darzustellen.
     */
    public static final String PATTERN_TIME = "HH:mm";

    /**
     * Format-Pattern, um Stunde/Minute/Sekunde darzustellen.
     */
    public static final String PATTERN_TIME_LONG = "HH:mm:ss";

    /**
     * Format-Pattner, um Stunde/Minute/Sekunde/Millisekunde darzustellen.
     */
    public static final String PATTERN_TIME_FULL = "HH:mm:ss:SSS";

    /**
     * Von Jahr bis Sekunde, ohne weitere Formatierungszeichen (also z.B. 20120715105334).
     */
    public static final String PATTERN_DATE_TIME_FULL_CHAR14 = "yyyyMMddHHmmss";

    /**
     * Zeitzone "Europe/Berlin".
     */
    public static final ZoneId TZ_BERLIN = ZoneId.of("Europe/Berlin");

    private static Date hurricanEndDate;
    private static Date billingEndDate;

    /**
     * Ueberprueft, ob die Datumswerte (nicht die Uhrzeit!) des angegebenen Datums dem Hurrican-Ende Datum (01.01.2200)
     * entspricht.
     *
     * @param toCheck
     * @return
     */
    public static boolean isHurricanEndDate(Date toCheck) {
        return isDateEqual(toCheck, getHurricanEndDate());
    }

    /**
     * Ueberprueft, ob die Datumswerte (nicht die Uhrzeit!) des angegebenen Datums dem Hurrican-Ende Datum (01.01.2200)
     * entspricht.
     *
     * @param toCheck
     * @return
     */
    public static boolean isHurricanEndDate(@NotNull LocalDate toCheck) {
        return isDateEqual(Date.from(toCheck.atStartOfDay(ZoneId.systemDefault()).toInstant()), getHurricanEndDate());
    }

    /**
     * Gibt das Datum zurueck, das in Hurrican als 'unendlich' angesehen wird (01.01.2200).
     */
    public static Date getHurricanEndDate() {
        if (hurricanEndDate == null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(Calendar.YEAR, 2200);
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            hurricanEndDate = DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
        }

        return new Date(hurricanEndDate.getTime());
    }

    /**
     * Gibt das Datum zurueck, das im Billing-System als 'unendlich' angesehen wird (31.12.9999).
     *
     * @return
     *
     */
    public static final Date getBillingEndDate() {
        if (billingEndDate == null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(Calendar.YEAR, 9999);
            cal.set(Calendar.MONTH, 11);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            billingEndDate = cal.getTime();
        }

        return new Date(billingEndDate.getTime());
    }

    /**
     * Ueberprueft, ob das Datum <code>toCheck</code> vor <code>reference</code> liegt. Ist dies der Fall, wird
     * <code>true</code> zurueck geliefert - sonst <code>false</code>
     *
     * @param toCheck   zu pruefendes Datum
     * @param reference Referenz-Datum
     * @return true, wenn <code>reference</code> vor <code>toCheck</code> liegt.
     */
    public static boolean isBefore(Date toCheck, Date reference) {
        if ((reference != null) && (toCheck != null)) {
            return toCheck.before(reference);
        }
        return false;
    }

    /**
     * Ueberprueft, ob das Datum <code>toCheck</code> nach <code>reference</code> liegt. Ist dies der Fall, wird
     * <code>true</code> zurueck geliefert - sonst <code>false</code>
     *
     * @param toCheck   zu pruefendes Datum
     * @param reference Referenz-Datum
     * @return true, wenn <code>reference</code> nach <code>toCheck</code> liegt.
     */
    public static boolean isAfter(Date toCheck, Date reference) {
        if ((reference != null) && (toCheck != null)) {
            return toCheck.after(reference);
        }
        return false;
    }

    /**
     * Ueberprueft, ob das Datum <code>toCheck</code> vor dem Datum von <code>reference</code> liegt. <br> In dieser
     * Methode wird nur das Datum, nicht die Uhrzeit verglichen!
     *
     * @param toCheck
     * @param reference
     * @return true, wenn <code>toCheck</code> vor <code>reference</code> liegt.
     */
    public static boolean isDateBefore(Date toCheck, Date reference) {
        if ((toCheck != null) && (reference != null)) {
            Date toCheckTrunc = DateUtils.truncate(toCheck, Calendar.DAY_OF_MONTH);
            Date refTrunc = DateUtils.truncate(reference, Calendar.DAY_OF_MONTH);
            return (toCheckTrunc.getTime() < refTrunc.getTime());
        }
        return false;
    }

    /**
     * Vergleicht das Datum zwei Daten miteinander. Die Uhrzeit wird nicht verglichen!
     *
     * @param toCheck   zu pruefendes Datum
     * @param reference Referenz-Datum
     * @return 0 falls {@code toCheck} das gleiche Datum enthaelt, -1 falls {@code toCheck} vor {@code reference} liegt,
     * 1 falls {@code toCheck} nach {@code reference} liegt.
     */
    public static int compareDates(Date toCheck, Date reference) {
        if ((toCheck != null) && (reference != null)) {
            Date toCheckTrunc = DateUtils.truncate(toCheck, Calendar.DAY_OF_MONTH);
            Date refTrunc = DateUtils.truncate(reference, Calendar.DAY_OF_MONTH);
            return toCheckTrunc.compareTo(refTrunc);
        }
        return 0;
    }

    /**
     * Ueberprueft, ob das Datum <code>toCheck</code> vor dem Datum von <code>reference</code> liegt oder identisch.
     * <br> In dieser Methode wird nur das Datum, nicht die Uhrzeit verglichen!
     *
     * @param toCheck
     * @param reference
     * @return true, wenn <code>toCheck</code> vor <code>reference</code> liegt oder identisch ist.
     */
    public static boolean isDateBeforeOrEqual(Date toCheck, Date reference) {
        if ((toCheck != null) && (reference != null)) {
            Date toCheckTrunc = DateUtils.truncate(toCheck, Calendar.DAY_OF_MONTH);
            Date refTrunc = DateUtils.truncate(reference, Calendar.DAY_OF_MONTH);
            return (toCheckTrunc.getTime() <= refTrunc.getTime());
        }
        return false;
    }

    /**
     * Ueberprueft, ob das Datum <code>toCheck</code> nach dem Datum von <code>reference</code> liegt. <br> In dieser
     * Methode wird nur das Datum, nicht die Uhrzeit verglichen!
     *
     * @param toCheck
     * @param reference
     * @return true, wenn <code>toCheck</code> nach <code>reference</code> liegt.
     */
    public static boolean isDateAfter(Date toCheck, Date reference) {
        if ((toCheck != null) && (reference != null)) {
            Date toCheckTrunc = DateUtils.truncate(toCheck, Calendar.DAY_OF_MONTH);
            Date refTrunc = DateUtils.truncate(reference, Calendar.DAY_OF_MONTH);
            return (toCheckTrunc.getTime() > refTrunc.getTime());
        }
        return false;
    }

    /**
     * Ueberprueft, ob das Datum <code>toCheck</code> nach dem Datum von <code>reference</code> liegt oder identisch
     * ist. <br> In dieser Methode wird nur das Datum, nicht die Uhrzeit verglichen!
     *
     * @param toCheck
     * @param reference
     * @return true, wenn <code>toCheck</code> nach <code>reference</code> liegt oder identisch ist.
     */
    public static boolean isDateAfterOrEqual(Date toCheck, Date reference) {
        if ((toCheck != null) && (reference != null)) {
            Date toCheckTrunc = DateUtils.truncate(toCheck, Calendar.DAY_OF_MONTH);
            Date refTrunc = DateUtils.truncate(reference, Calendar.DAY_OF_MONTH);
            return (toCheckTrunc.getTime() >= refTrunc.getTime());
        }
        return false;
    }

    /**
     * Prueft, ob das Datum von <code>toCheck</code> und <code>reference</code> identisch ist. In dieser Methode wird
     * nur das Datum, nicht die Uhrzeit verglichen!
     *
     * @param toCheck
     * @param reference
     * @return true, wenn <code>toCheck</code> identisch mit <code>reference</code> ist.
     *
     */
    public static boolean isDateEqual(Date toCheck, Date reference) {
        if ((toCheck != null) && (reference != null)) {
            Date toCheckTrunc = DateUtils.truncate(toCheck, Calendar.DAY_OF_MONTH);
            Date refTrunc = DateUtils.truncate(reference, Calendar.DAY_OF_MONTH);
            return (toCheckTrunc.getTime() == refTrunc.getTime());
        }
        return false;
    }

    /**
     * Ueberprueft, ob sich das Datum <code>toCheck</code> zwischen <code>from</code> und <code>to</code> befindet. Dies
     * ist dann der Fall, wenn {@code toCheck >= from} und {@code toCheck < to} ist. (Anders ausgedrueck: {@code from <=
     * toCheck && to > toCheck}) <br> In dieser Methode wird nur das Datum, nicht die Uhrzeit verglichen!
     *
     * @return true, wenn {@code toCheck} zwischen {@code from} und {@code to} liegt, sonst false
     *
     */
    public static boolean isDateBetween(Date toCheck, Date from, Date to) {
        if ((toCheck != null) && (from != null) && (to != null) && DateTools.isDateAfterOrEqual(toCheck, from)
                && DateTools.isDateBefore(toCheck, to)) {
            return true;
        }
        return false;
    }

    /**
     * Erstellt einen formatierten String aus einem Date-Objekt.
     *
     * @param toFormat Date-Objekt
     * @param pattern  Format-Pattern.
     * @return formatierter String
     */
    public static String formatDate(Date toFormat, String pattern) {
        if (toFormat != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(toFormat);
        }
        return "";
    }

    /**
     * Gibt die aktuelle Zeit als <code>java.sql.Date</code>-Objekt zurureck.
     *
     * @return java.sql.Date
     */
    public static java.sql.Date getActualSQLDate() {
        return new java.sql.Date(new java.util.Date().getTime());
    }

    /**
     * Gibt das aktuelle Jahr als String zurueck.
     */
    public static String getYearAsString() {
        GregorianCalendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);
        return "" + year;
    }

    /**
     * Gibt den aktuellen Monat als String zurueck. <br> Januar = 01, Februar=02 etc.
     */
    public static String getMonth() {
        GregorianCalendar cal = new GregorianCalendar();
        int month = cal.get(Calendar.MONTH);
        ++month;
        return (month < 10) ? "0" + month : "" + month;
    }

    /**
     * Gibt die Anzahl der Tage des Monats zurueck, das vom Objekt <code>date</code> dargestellt wird.
     *
     * @return Anzahl der Tage im Monat
     */
    public static int getDaysOfMonth(Date date) {
        if (date == null) {
            return -1;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Gibt den Tag des Monats aus dem Datumsobjekt zurueck (beginnend bei 1).
     */
    public static int getDayOfMonth(Date date) {
        if (date == null) {
            return -1;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Gibt den Wochentag zurueck. (Sonntag=1, Montag=2, ... Samstag=6)
     */
    public static int getDayOfWeek(Date date) {
        if (date == null) {
            return -1;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Gibt die Stunde des Tages aus dem Datumsobjekt zurueck.
     */
    public static int getHourOfDay(Date date) {
        if (date == null) {
            return -1;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Gibt die Minute aus dem Datumsobjekt zurueck.
     */
    public static int getMinute(Date date) {
        if (date == null) {
            return -1;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    /**
     * Gibt die Sekunde aus dem Datumsobjekt zurueck.
     */
    public static int getSecond(Date date) {
        if (date == null) {
            return -1;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }

    /**
     * Gibt das Jahr aus dem Datumsobjekt zurueck.
     */
    public static int getYear(Date date) {
        if (date == null) {
            return -1;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * Gibt den Monat aus dem Datumsobjekt zurueck.
     */
    public static int getMonth(Date date) {
        if (date == null) {
            return -1;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    /**
     * Aendert ein bestimmtes Feld von <code>toChange</code> um den Wert <code>value</code> ab (addieren oder
     * subtrahieren). <br> Das zu aendernde Feld <code>field</code> ist aus dem <code>Calendar</code>-Objekt zu
     * beziehen. <br> <br> Wichtig: Das Objekt <code>toChange</code> wird nicht direkt veraendert, sondern nur eine
     * Kopie von dem Objekt!
     *
     * @param toChange zu aenderndes Datum
     * @param field    zu aendernds Feld
     * @param value    Wert, um den das Feld geaendert werden soll.
     */
    public static Date changeDate(Date toChange, int field, int value) {
        if (toChange == null) {
            return null;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(toChange);

        int oldValue = cal.get(field);
        cal.set(field, (oldValue + value));
        return cal.getTime();
    }

    /**
     * Erzeugt ein neues Date-Objekt und addiert die angegebene Anzahl an Arbeitstagen(!) dazu. <br> Bsp.:
     * heute=Donnerstag, 27.07.2006; workDaysIn=3 --> Ergebnis=Dienstag, 01.08.2006
     *
     * @param workDaysIn
     * @return
     *
     */
    public static Date plusWorkDays(int workDaysIn) {
        int workDays = workDaysIn;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        for (int i = 0; i < workDays; i++) {
            cal.add(Calendar.DATE, 1);
            if (!isWorkDay(cal.getTime())) {
                workDays++;
            }
        }

        return cal.getTime();
    }

    /**
     * Erzeugt ein neues Date-Objekt und subtrahiert die angegebene Anzahl an Arbeitstagen(!) . <br> Bsp.:
     * heute=Donnerstag, 27.07.2006; workDaysIn=4 --> Ergebnis=Freitag, 21.07.2006
     *
     * @param workDaysIn
     * @return
     *
     */
    public static Date minusWorkDays(int workDaysIn) {
        int workDays = workDaysIn;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        for (int i = 0; i < workDays; i++) {
            cal.add(Calendar.DATE, -1);
            if (!isWorkDay(cal.getTime())) {
                workDays++;
            }
        }
        return cal.getTime();
    }

    /**
     * Ueberprueft, ob das angegebene Datum ein Arbeitstag ist. <br> Arbeitstage sind Montag-Freitag. <br> Achtung:
     * Feiertage werden nicht beruecksichtigt!
     *
     * @param date
     * @return true wenn es sich bei dem angegebenen Datum um einen Arbeitstag handelt.
     *
     */
    public static boolean isWorkDay(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);

        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        return ((weekDay >= Calendar.MONDAY) && (weekDay <= Calendar.FRIDAY));
    }

    /**
     * Ueberprueft, ob das Datum <code>toCheck</code> dem heutigen Tag oder dem naechsten Werktag (Mo.-Fr.) entspricht.
     *
     * @param toCheck zu pruefendes Datum.
     * @return <code>true</code>, wenn <code>toCheck</code> heute oder der naechste Werktag ist.
     */
    public static boolean isTodayOrNextWorkDay(Date toCheck) {
        Date rd = DateUtils.truncate(toCheck, Calendar.DAY_OF_MONTH);
        Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = DateUtils.truncate(cal, Calendar.DAY_OF_MONTH).getTime();

        if (rd.equals(now) || rd.equals(tomorrow)) { // Datum ist heute oder morgen
            return true;
        }
        // Datum ist Montag und heute ist der Freitag davor
        GregorianCalendar rdCal = new GregorianCalendar();
        rdCal.setTime(rd);

        GregorianCalendar nextMondeyCal = new GregorianCalendar();
        nextMondeyCal.add(Calendar.DAY_OF_MONTH, 3);
        Date nextMonday = DateUtils.truncate(nextMondeyCal, Calendar.DAY_OF_MONTH).getTime();

        if ((rdCal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) && rd.equals(nextMonday)) {
            return true;
        }
        return false;
    }

    /**
     * Generiert ein Datum mit dem angegebenen Jahr/Monat/Tag.
     *
     * @param year        gewuenschtes Jahr (z.B. '2006')
     * @param monthOfYear gewuenschter Monat - beginnend mit 0 (=Januar)
     * @param dayOfMonth  gewuenschter Tag des Monats - beginnend bei 1
     * @return Date-Instanz
     *
     */
    public static Date createDate(int year, int monthOfYear, int dayOfMonth) {
        return DateUtils.truncate(createDate(year, monthOfYear, dayOfMonth, 0, 0, 0, 0), Calendar.DAY_OF_MONTH);
    }

    /**
     * Generiert ein Datum mit dem angegebenen Jahr/Monat/Tag/Stunde/Minute.
     *
     * @param year        gewuenschtes Jahr (z.B. '2006')
     * @param monthOfYear gewuenschter Monat - beginnend mit 0 (=Januar)
     * @param dayOfMonth  gewuenschter Tag des Monats - beginnend bei 1
     * @param hourOfDay   gewuenschte Stunde (z.B. 14)
     * @param minute      gewuenschte Minute (z.B. 33)
     * @return Date-Instanz
     *
     */
    public static Date createDate(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
        return DateUtils.truncate(createDate(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0, 0), Calendar.MINUTE);
    }

    /**
     * Generiert ein Datum mit dem angegebenen Jahr/Monat/Tag/Stunde/Minute.
     *
     * @param year        gewuenschtes Jahr (z.B. '2006')
     * @param monthOfYear gewuenschter Monat - beginnend mit 0 (=Januar)
     * @param dayOfMonth  gewuenschter Tag des Monats - beginnend bei 1
     * @param hourOfDay   gewuenschte Stunde (z.B. 14)
     * @param minute      gewuenschte Minute (z.B. 33)
     * @param second      gewuenschte Sekunde (z.B. 59)
     * @param millis      gewuenschte Millisekunde (z.B. 798)
     * @return Date-Instanz
     *
     */
    public static Date createDate(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, int second,
            int millis) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millis);
        return cal.getTime();
    }

    /**
     * Ermittelt das letzte Datum fuer einen bestimmten Monat.
     *
     * @param year        gewuenschtes Jahr (z.B. '2006')
     * @param monthOfYear gewuenschter Monat - beginnend mit 0 (=Januar)
     * @return Date-Instanz, das das letzte Datum des Monats darstellt
     *
     */
    public static Date getLastDateOfMonth(int year, int monthOfYear) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);

        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, max);

        return DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
    }

    /**
     * Erstellt aus einem Date-Objekt ein entsprechendes Calendar-Objekt.
     */
    public static Calendar getCalendarForDate(Date date) {
        if (date == null) {
            return null;
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        return gc;
    }

    /**
     * Gibt das Datum vom Vortag zurueck
     */
    public static Date getPreviousDay(Date base) {
        Date previousDay = DateTools.changeDate(DateUtils.truncate(base, Calendar.DAY_OF_MONTH), Calendar.DAY_OF_YEAR,
                -1);
        return previousDay;
    }

    /**
     * Berechnet die Differenz zwischen einem Start- und einem Endedatum in Tagen. Winter- bzw. Sommerzeit sind nicht
     * berücksichtigt. Dadurch koennen nicht ganz exakte Ergebnisse entstehen.
     *
     * @param start Unteres Datum (start <= stop)
     * @param stop  Oberes Datum (stop >= start)
     * @return Differenz in Tagen (negativ wenn start und stop vertauscht)
     */
    public static long getDiffInDays(Date start, Date stop) {
        // Calculate difference in milliseconds
        long diff = getCalendarForDate(stop).getTimeInMillis() - getCalendarForDate(start).getTimeInMillis();
        // Calculate difference in days
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffDays;
    }

    /**
     * @see {@link SimpleDateFormat#parse(String)} - allerdings Fail-Save!
     */
    public static Date parse(String format, String dateString, Date defaultDate) {
        if (dateString == null) {
            return defaultDate;
        }
        try {
            return new SimpleDateFormat(format).parse(dateString);
        }
        catch (ParseException e) {
            return defaultDate;
        }
    }

    /**
     * vergleicht nur Datum, Zeit wird ignoriert
     *
     * @return wenn beide Parameter nicht null sind das groeßere, ist eines von beiden null, das nicht-null Datum, sind
     * beide null dann null
     */
    public static Date maxDate(Date date1, Date date2) {
        Date max = null;
        if (isDateAfterOrEqual(date1, date2) || ((date1 != null) && (date2 == null))) {
            max = date1;
        }
        else if (isDateAfter(date2, date1) || ((date2 != null) && (date1 == null))) {
            max = date2;
        }

        return max;
    }

    /**
     * vergleicht nur Datum, Zeit wird ignoriert
     *
     * @return wenn beide Parameter nicht null sind das kleinere, ist eines von beiden null, das nicht-null Datum, sind
     * beide null dann null
     */
    public static Date minDate(Date date1, Date date2) {
        Date min = null;
        if (isDateBeforeOrEqual(date1, date2) || ((date1 != null) && (date2 == null))) {
            min = date1;
        }
        else if (isDateBefore(date2, date1) || ((date2 != null) && (date1 == null))) {
            min = date2;
        }
        return min;
    }

    /**
     * @return removes the hours and minutes from the {@link java.time.LocalDateTime} object.
     */
    public static LocalDateTime stripTimeFromDate(LocalDateTime date) {
        if (date != null) {
            return date.toLocalDate().atStartOfDay();
        }
        return null;
    }

}
