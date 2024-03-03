package de.mnet.wita.message.common;

/**
 * Legt den Status des SMS Versand fest.
 */
public enum SmsStatus {
    /**
     * default
     */
    OFFEN,

    /**
     * SMS wurde fuer den Auftrag versendet
     */
    GESENDET,

    /**
     * die Endstelle des Ansprechpartners enthaelt keine Mobil-Rufnummer.
     */
    KEINE_RN,

    /**
     * Schalttermin oder Inbetriebnahmedatum in der Vergangenheit.
     */
    VERALTET,

    /**
     * Meldung ungueltig, d.h. durch nachfolgende Meldung ersetzt.
     */
    UNGUELTIG,

    /**
     * SMS-Versand im Auftrag nicht gesetzt. D.h. es soll keine SMS versendet werden.
     */
    UNERWUENSCHT,

    /**
     * SMS-Versand nicht konfiguriert, Template leer/null. Dies ist kein Fehler, sondern per (nicht) Konfiguration so
     * erwuenscht.
     */
    KEINE_CONFIG,

    /**
     * Auftrag befindet sich zum Beispiel im Status gekuendigt oder storniert.
     */
    FALSCHER_AUFTRAGSTATUS
}
