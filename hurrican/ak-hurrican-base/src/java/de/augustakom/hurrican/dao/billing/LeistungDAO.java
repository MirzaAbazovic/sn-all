/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 14:09:08
 */
package de.augustakom.hurrican.dao.billing;

import java.util.*;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.ServiceBlockPrice;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Interface definiert Methoden fuer DAOs, die Objekte vom Typ <code>Leistung</code> verwalten.
 *
 *
 */
public interface LeistungDAO extends ByExampleDAO {

    /**
     * Sucht nach der (aktiven!) Leistung, die die originale Leistungs-No <code>leistungNoOrig</code> besitzt. <br>
     *
     * @param origNo originale Leistungs-No
     * @param value  (optional) zur Verknuepfung mit dem Rechnungstext
     * @return Liste von Leistung-Objekten oder <code>null</code>
     */
    public Leistung findLeistungByNoOrig(Long leistungNoOrig);

    /**
     * Sucht nach den (aktiven) Leistungen zu den angegebenen originalen Leistungsnummern.
     *
     * @param leistungNoOrigs Liste mit den IDs der gesuchten Leistungen.
     * @return Liste mit Objekten des Typs <code>Leistung</code>
     *
     */
    public List<Leistung> findLeistungen(List<Long> leistungNoOrigs);

    /**
     * Ermittelt die aktive Leistung zu der angegebenen original Leistungs-No. <br> In dieser Methode wird die
     * zugehoerige Language-Tabelle nicht ausgelesen - das Bezeichnungs-Feld im Result-Objekt ist also auf jeden Fall
     * leer.
     *
     * @param leistungNoOrig (original) Leistungs-No
     * @return die aktive Leistung zu der angegebenen No
     *
     */
    public Leistung findLeistungByNoOrigWithoutLang(Long leistungNoOrig);

    /**
     * Ermittelt den Rechnungstext einer Leistung.
     *
     * @param leistungNo Leistungsnummer
     * @param value      (optional) Parameter der Leistung bzw. Auftragsposition
     * @param language   (optional - Standard: german) gesuchte Sprache
     * @return gefundener Rechnungstext
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public String findRechnungstext(Long leistungNo, String value, String language);

    /**
     * Ermittelt den aktuell gueltigen MWST-Satz anhand einer vatCodeId
     *
     * @param vatCodeId Id des gesuchten MwSt-Satzes
     * @return Gesuchter MwSt-Satz
     *
     */
    public Float findVatRate(String vatCodeId);

    /**
     * Ermittelt UDR Volumenleistungen eines Auftrags zum angegebenen Datum. <br> Es handelt sich dann um eine UDR
     * Volumenleistung, wenn folgende Bedingungen erfuellt sind: <br> <ul> <li>'leistungKat' der Leistung = 'VOL-QTY'
     * <li>Billing-Code ist 'UDR*' </ul>
     *
     * @param orderNoOrig (original) Auftragsnummer, zu der die Volumenleistungen ermittelt werden sollen
     * @param validDate   Datum, zu der die gesuchte Volumenleistung(en) aktiv sein soll(en).
     * @return Liste mit den zutreffenden UDR Volumenleistungen
     *
     */
    public List<Leistung> findUDRServices4Order(Long orderNoOrig, Date validDate);

    /**
     * Ermittelt alle ServiceBlockPrice-Definitionen zu einer bestimmten Leistung.
     *
     * @param leistungNo ID der Leistung
     * @return Liste mit den definierten ServiceBlockPrices.
     *
     */
    public List<ServiceBlockPrice> findServiceBlockPrices(Long leistungNo);

}


