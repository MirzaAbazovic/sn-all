/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 10:50:00
 */
package de.augustakom.hurrican.service.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.ServiceValue;
import de.augustakom.hurrican.model.billing.query.KundeLeistungQuery;
import de.augustakom.hurrican.model.billing.view.KundeLeistungView;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Service-Interface fuer Objekte des Typs <code>Leistung</code>
 *
 *
 */
public interface LeistungService extends IBillingService {

    /**
     * Sucht nach einer bestimmten Leistung ueber die angegebene (original) Leistungsnummer.
     *
     * @param leistungNoOrig
     * @param value          (optional) zugehoeriger Value (fuer den Rechnungstext)
     * @return Instanz von <code>Leistung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public Leistung findLeistung(Long leistungNoOrig) throws FindException;

    /**
     * Sucht nach den Leistungen eines best. Kunden.
     *
     * @param query Query-Objekt mit den Filter-Parametern.
     * @return Liste mit Objekten des Typs <code>KundeLeistungView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<KundeLeistungView> findLeistungen4Kunde(KundeLeistungQuery query) throws FindException;

    /**
     * Ermittelt den Rechnungstext einer Leistung.
     *
     * @param leistungNoOrig Leistungsnummer
     * @param value          (optional) Parameter der Leistung bzw. Auftragsposition
     * @param language       (optional - Standard: german) gesuchte Sprache
     * @return gefundener Rechnungstext
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public String findRechnungstext(Long leistungNoOrig, String value, String language) throws FindException;

    /**
     * Sucht nach allen (aktiven) Leistungen, die einem bestimmten Auftrag zugeordnet sind. <br> Jede Leistung wird
     * dabei nur einmal(!) ermittelt - egal, wie oft sie dem Auftrag zugeordnet ist.
     *
     * @param auftragNoOrig (original) ID des Billing-Auftrags, dessen Leistungen ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>Leistung</code>.
     * @throws FindException wenn bei der Ermittlung der Leistungen ein Fehler auftritt.
     */
    public List<Leistung> findLeistungen4Auftrag(Long auftragNoOrig) throws FindException;

    /**
     * Ermittelt die sog. Produkt-Leistung zu dem Auftrag. <br> Die Produkt-Leistung ist die Leistung, die fuer die
     * externen Systeme relevant ist (deren EXT_PRODUCT__NO gesetzt ist). <br> Es wird immer versucht, die
     * Produkt-Leistung zu ermitteln die fuer den CC-Auftrag relevant ist bzw. war. <br><br> Sollte das CC-Produkt ein
     * kombiniertes Produkt sein (z.B. DSL + Phone) kann ueber die Angabe von <code>productType</code> definiert werden,
     * nach welchem Leistungsanteil gesucht wird (z.B. Phone oder DSL).
     *
     * @param ccAuftragId Auftrags-ID vom CC-System
     * @param productType (optional) Angabe des gesuchten Leistungs-Typs. Die moeglichen Werte sind als Konstante im
     *                    Modell <code>ProduktMapping</code> definiert.
     * @return Instanz von <code>Leistung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public Leistung findProductLeistung4Auftrag(Long ccAuftragId, String productType)
            throws FindException;

    /**
     * Ermittelt den UDR Tariftyp (z.B. Flat, Volumen, Zeit) von einem Auftrag zum angegebenen Datum. <br>
     *
     * @param auftragNoOrig (original) Auftragsnummer
     * @param validDate     Datum, zu dem der Tarif ermittelt werden soll
     * @return Konstante aus Leistung (z.B. LEISTUNG_VOL_TYPE_FLAT) oder ein Wert < 0, falls keine entsprechende UDR
     * Volumenleistung ermittelt werden konnte.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public int getUDRTarifType4Auftrag(Long auftragNoOrig, Date validDate)
            throws FindException;

    /**
     * Ermittelt die Leistungen, die einem Billing-Produkt zugeordnet sind.
     *
     * @param oeNoOrig                  (original) Produktnummer vom Billing-System
     * @param onlyProdLeistungen4Extern Flag, ob nur Leistungen ermittelt werden sollen, die eine Produktkennzeichnung
     *                                  fuer externe Systeme haben.
     * @return Liste mit Objekten vom Typ <code>Leistung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public List<Leistung> findLeistungen4OE(Long oeNoOrig, boolean onlyProdLeistungen4Extern) throws FindException;

    /**
     * Ermittelt den Leistungs-Wert zu der angegebenen Leistung und dem Werte-Namen. <br> WICHTIG: Es wird der
     * Leistungs-Wert der aktuellen (HistStatus=AKT) Leistung ermittelt!
     *
     * @param leistungNoOrig (original) Leistungsnummer
     * @param value          Werte-Name
     * @return ermittelter Leistungs-Wert
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public ServiceValue findServiceValue(Long leistungNoOrig, String value) throws FindException;

    /**
     * Ermittelt den aktuell gueltigen Mehrwertsteuersatz anhand der VatCodeId
     *
     * @param vatCodeId Id des gesuchten Mehrwertsteuersatzes
     * @return Gesuchter MwSt-Satz
     * @throws FindException
     *
     */
    public Float findVatRate(String vatCodeId) throws FindException;
}


