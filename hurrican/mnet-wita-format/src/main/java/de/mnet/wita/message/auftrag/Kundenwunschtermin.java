/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2011 15:46:43
 */
package de.mnet.wita.message.auftrag;

import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MwfEntity;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_KUNDENWUNSCHTERMIN")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_KUNDENWUNSCHTERMIN_0", allocationSize = 1)
public class Kundenwunschtermin extends MwfEntity {

    private static final long serialVersionUID = 1010180306071035285L;

    public static final int KWT_MONDAY = 1;
    public static final int KWT_TUESDAY = 2;
    public static final int KWT_WEDNESDAY = 3;
    public static final int KWT_THURSDAY = 4;
    public static final int KWT_FRIDAY = 5;

    /**
     * Moegliche Werte fuer die Zeitfenster der TAL-Realisierung. (Die genaue Zeit hinter jedem Wert ist vertraglich mit
     * DTAG geregelt.)
     */
    public enum Zeitfenster {
        // @formatter:off
        /**
         * nur fuer Portierungen
         */
        SLOT_1("1", "6:00 - 8:00 h, Montag-Freitag (Portierungszeitfenster, nur REX-MK)",
                "zwischen 6 und 8 Uhr",
                LocalTime.of(6, 0),
                KWT_MONDAY,
                KWT_FRIDAY,
                GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG),

        /** Standardzeitfenster fuer TAL-Bestellungen */
        SLOT_2("2", "8:00 - 12:00, Montag-Freitag (Standardzeitfenster KUE-KD)",
                "zwischen 8 und 12 Uhr",
                LocalTime.of(8, 0),
                KWT_MONDAY,
                KWT_FRIDAY,
                GeschaeftsfallTyp.KUENDIGUNG_KUNDE),

        /** only for TK-Anlagenanschlüsse und PMXer, incl. one month leadtime */
        SLOT_3("3", "20:00 - 22:00, Dienstag (kostenpflichtig)",
                "zwischen 20 und 22 Uhr",
                LocalTime.of(20, 0),
                KWT_TUESDAY,
                KWT_TUESDAY,
                GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, GeschaeftsfallTyp.VERBUNDLEISTUNG),

        /** only for TK-Anlagenanschlüsse und PMXer, incl. one month leadtime */
        SLOT_4("4", "16:00 - 18:00, Freitag (kostenpflichtig)",
                "zwischen 16 und 18 Uhr",
                LocalTime.of(16, 0),
                KWT_FRIDAY,
                KWT_FRIDAY,
                GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, GeschaeftsfallTyp.VERBUNDLEISTUNG),

        /** only for TK-Anlagenanschlüsse und PMXer, incl. one month leadtime */
        SLOT_6("6", "5:00 - 7:00, Mittwoch (kostenpflichtig)",
                "zwischen 5 und 7 Uhr",
                LocalTime.of(5, 0),
                KWT_WEDNESDAY,
                KWT_WEDNESDAY,
                GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, GeschaeftsfallTyp.VERBUNDLEISTUNG),

        /** zukuenftig Standardzeitfenster */
        SLOT_7("7", "12:00 - 16:00, Montag-Freitag (kostenpflichtig)",
                "zwischen 12 und 16 Uhr",
                LocalTime.of(12, 0),
                KWT_MONDAY,
                KWT_FRIDAY,
                GeschaeftsfallTyp.BEREITSTELLUNG, GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG,
                GeschaeftsfallTyp.PORTWECHSEL, GeschaeftsfallTyp.PROVIDERWECHSEL, GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG,
                GeschaeftsfallTyp.VERBUNDLEISTUNG),

        // do not use because they are not stated in TAL-Vertrag
        /** only for TK-Anlagenanschlüsse und PMXer, incl. one month leadtime */
        // SLOT_5("5", "9:00 - 12:00, Samstag", new LocalTime(9, 0)),

        SLOT_8("8", "8:00 - 12:00, Montag - Freitag (Standardzeitfenster für SMS- und Mail-Versand)",
                "zwischen 8 und 12 Uhr",
                LocalTime.of(8, 0),
                KWT_MONDAY,
                KWT_FRIDAY,
                null),

        SLOT_9("9", "8:00 - 12:00, Montag - Freitag (Standardzeitfenster TAL)",
                "zwischen 8 und 12 Uhr",
                LocalTime.of(8, 0),
                KWT_MONDAY,
                KWT_FRIDAY,
                GeschaeftsfallTyp.BEREITSTELLUNG, GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, GeschaeftsfallTyp.LEISTUNGSMERKMAL_AENDERUNG,
                GeschaeftsfallTyp.PORTWECHSEL, GeschaeftsfallTyp.PROVIDERWECHSEL, GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG,
                GeschaeftsfallTyp.VERBUNDLEISTUNG)

        ;
        // @formatter:on

        public final String witaZeitfenster;
        private final String description;
        private final String shortDescription;
        public final LocalTime beginnZeitfenster;
        public final int fromDayOfWeek;
        public final int toDayOfWeek;
        public final GeschaeftsfallTyp[] geschaeftsfallTypen;

        /**
         * @param witaZeitfenster     Name des Zeitfensters, wie er in der WITA-SST angegeben werden muss
         * @param description         Text beschreibt das ZF
         * @param shortDescription    Zeitfensterbeschreibung in kurzform
         * @param beginnZeitfenster   Uhrzeit (Stunde), zu der das ZF beginnt
         * @param fromDayOfWeek       erster Wochentag, an dem das ZF gueltig ist
         * @param toDayOfWeek         letzter Wochentag, an dem das ZF gueltig ist
         * @param geschaeftsfallTypen Angabe der WITA Geschaeftsfall-Typen, fuer die das Zeitfenster gueltig ist
         */
        private Zeitfenster(String witaZeitfenster, String description, String shortDescription, LocalTime beginnZeitfenster,
                int fromDayOfWeek, int toDayOfWeek, GeschaeftsfallTyp... geschaeftsfallTypen) {
            this.witaZeitfenster = witaZeitfenster;
            this.description = description;
            this.shortDescription = shortDescription;
            this.beginnZeitfenster = beginnZeitfenster;
            this.fromDayOfWeek = fromDayOfWeek;
            this.toDayOfWeek = toDayOfWeek;
            this.geschaeftsfallTypen = geschaeftsfallTypen;
        }

        /**
         * Ueberprueft, ob das aktuelle Zeitfenster fuer min. einen der angegebenen {@link GeschaeftsfallTyp}en erlaubt
         * ist. <br/>
         *
         * @return true, wenn das Zeitfenster fuer min. einen der angegebenen {@link GeschaeftsfallTyp}en erlaubt ist
         */
        public boolean isPossibleForAtLeastOneOf(GeschaeftsfallTyp[] gfsToCheck) {
            if (geschaeftsfallTypen != null) {
                for (GeschaeftsfallTyp gfTyp : gfsToCheck) {
                    if (Arrays.asList(geschaeftsfallTypen).contains(gfTyp)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Gibt das Default-Zeitfenster fuer einen GeschaeftsfallTyp zurück.
         *
         * @param gfTyp
         * @return
         */
        public static
        @NotNull
        Zeitfenster getDefaultZeitfenster(GeschaeftsfallTyp gfTyp) {
            if (GeschaeftsfallTyp.KUENDIGUNG_KUNDE.equals(gfTyp)) {
                return SLOT_2;
            }
            return SLOT_9;
        }

        /**
         * Gibt das Default-Zeitfenster fuer die SMS- und Mailbenachrichtigung zurueck.
         *
         * @return
         */
        public static
        @NotNull
        Zeitfenster getDefaultSmsMailZeitfenster() {
            return SLOT_8;
        }

        public String getDescription() {
            return description;
        }

        public String getShortDescription() {
            return shortDescription;
        }

    }

    /**
     * Only the date is required, not the time *
     */
    private LocalDate datum;
    private Zeitfenster zeitfenster;

    @Override
    public String toString() {
        String timeSlot = (zeitfenster != null) ? zeitfenster.name() : "default";
        return "Termin [datum=" + datum + ", zeitfenster=" + timeSlot + "]";
    }

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDate")
    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 6, columnDefinition = "varchar2(6)")
    public Zeitfenster getZeitfenster() {
        return zeitfenster;
    }

    public void setZeitfenster(Zeitfenster zeitfenster) {
        this.zeitfenster = zeitfenster;
    }
}
