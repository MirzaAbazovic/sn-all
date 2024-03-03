package de.augustakom.hurrican.service.cc;

/**
 * Service zur Abfrage von Werten aus dem Taifun Doc-Modul (u.a.)
 */
public interface ReportingService extends ICCService {

    static final String STRING_DELIMITER = " / ";
    static final String STRING_IP_DELIMITER = " | ";

    /**
     * Liefert Wert für gegebene Taifun-Auftragsnummer und Key
     *
     * @return {@code null} Wenn für diese Auftragsnummer kein Wert gefunden werden konnte oder Key nicht bekannt
     */
    String getValue(Long orderNo, String reportingKeyName);

}
