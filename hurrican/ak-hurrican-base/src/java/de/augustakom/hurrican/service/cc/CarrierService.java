/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2004 10:10:54
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.validation.constraints.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierContact;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.CarrierMapping;
import de.augustakom.hurrican.model.cc.CarrierVaModus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.cc.view.AQSView;
import de.augustakom.hurrican.model.cc.view.CuDAVorschau;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.mnet.wbci.model.CarrierCode;


/**
 * Service-Interface fuer Carrier-Daten.
 *
 *
 */
@SuppressWarnings("deprecation")
public interface CarrierService extends ICCService {

    /**
     * Service-Callback um zu fragen, ob der Account des alten Auftrags auf den neuen Auftrag uebernommen werden soll.
     * <br> Als Result wird ein Objekt vom Typ <code>Boolean</code> erwartet (TRUE, wenn der Account uebernommen werden
     * soll; sonst FALSE).
     */
    public static final int CALLBACK_ASK_ACCOUNTUEBERNAHME = 500;

    /**
     * Sucht nach allen Carrierbestellungen, die einer best. Endstelle zugeordnet sind. <br> Die Carrierbestellungen
     * werden absteigend (nach der ID) sortiert. <br> WICHTIG: die Methode unterstuetzt keine Transaktionen!!!
     *
     * @param endstelleId ID der Endstelle gesucht werden.
     * @return Liste mit Objekten des Typs <code>Carrierbestellung</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Carrierbestellung> findCBs4Endstelle(Long endstelleId) throws FindException;

    /**
     * Diese Methode unterstuetzt bestehende Transaktionen!!!
     *
     * @see #findCBs4Endstelle(Long)
     */
    public List<Carrierbestellung> findCBs4EndstelleTx(Long endstelleId) throws FindException;

    /**
     * Sucht nach der letzten/aktuellsten(!) Carrierbestellung zu einer best. Endstelle.
     *
     * @param endstelleId ID der Endstelle, deren letzte/aktuellste Carrierbestellung gesucht wird.
     * @return Instanz von <code>Carrierbestellung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Carrierbestellung findLastCB4Endstelle(Long endstelleId) throws FindException;

    /**
     * Sucht nach allen Carrierbestellungen mit der angegebenen CB-2-ES-ID. <br> WICHTIG: die Methode unterstuetzt keine
     * Transaktionen!!! Die Sortierung erfolgt in aufsteigender(!) Reihenfolge der Carrierbestellung-ID.
     *
     * @param cb2esId
     * @return Liste mit Objekte des Typs <code>Carrierbestellung</code>, nie {@code null}
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Carrierbestellung> findCBs(Long cb2esId) throws FindException;

    /**
     * Sucht mit Unschaerfe nach allen Carrierbestellungen fuer eine Vertragsnummer. Falls keine Carrierbestellungen
     * fuer die exakte Nummer gefunden werden, versuche auch mit fuehrenden Nullen oder in Kurzform, z.B. 123A000456
     * statt 123A456.
     *
     * @param vertragsnummer
     * @return Liste mit Objekten des Typs <code>Carrierbestellung</code>, leer aber nicht null
     */
    List<Carrierbestellung> findCBsByNotExactVertragsnummer(String vertragsnummer);

    /**
     * @see CarrierService#findCBs Diese Methode unterstuetzt bestehende Transaktionen!!!
     */
    public List<Carrierbestellung> findCBsTx(Long cb2esId) throws FindException;

    /**
     * Sucht nach einer best. Carrierbestellung.
     *
     * @param cbId ID der Carrierbestellung
     * @return Instanz von <code>Carrierbestellung</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Carrierbestellung findCB(Long cbId) throws FindException;

    /**
     * Sucht nach Carrierbestellungen fuer eine LBZ.
     */
    public List<Carrierbestellung> findCBs4LBZ(String lbz) throws FindException;

    /**
     * Ermittelt eine View mit Aderquerschnitten und Leitungslaengen, die fuer die angegebene Geo-ID bisher erfasst
     * wurden.
     *
     * @param geoId GeoID
     * @return Liste mit {@link AQSView} Objekten
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<AQSView> findAqsLL4GeoId(Long geoId) throws FindException;

    /**
     * Sucht nach den Carrierbestellungen fuer einen best. Carrier. <br> Ueber die Parameter <code>maxResults</code> und
     * <code>beginWith</code> kann bestimmt werden, ab welchem Datensatz das Ergebnis beginnen soll und wie viele
     * Datensaetze maximal ausgelesen werden.
     *
     * @param carrierId  ID des Carriers
     * @param maxResults Anzahl der maximal gelieferten Datensaetze (Angabe von -1 bedeutet, dass alle Datensaetze
     *                   geliefert werden sollen).
     * @param beginWith  Datensatz, ab dem das Ergebnis beginnen soll
     * @return Liste mit Objekten des Typs <code>Carrierbestellung</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Carrierbestellung> findCBs4Carrier(Long carrierId, int maxResults, int beginWith)
            throws FindException;

    /**
     * Setzt eine Carrierbestellung auf gekuendigt.
     */
    public void cbKuendigen(Long cbId) throws StoreException;

    public void cbKuendigenNewTx(Long cbId) throws StoreException;

    /**
     * Sucht nach allen vorhandenen Carrier-Eintraegen.
     *
     * @return Liste mit Objekten des Typs <code>Carrier</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Carrier> findCarrier() throws FindException;

    /**
     * Sucht nach allen vorhandenen Carrier-Eintraegen, die fuer aufnehmenden Anbieterwechsel erlaubt sind.
     *
     * @return Liste mit Objekten des Typs <code>Carrier</code>, sortiert nach Namen.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Carrier> findCarrierForAnbieterwechsel() throws FindException;

    /**
     * Sucht nach einem best. Carrier.
     *
     * @param carrierId ID des Carriers.
     * @return Instanz von <code>Carrier</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Carrier findCarrier(Long carrierId) throws FindException;

    /**
     * Sucht nach einem Carrier mit best. Vorabstimmungsmodus.
     *
     * @param modus Vorabstimmungsmodus des Carriers.
     * @return Instanz von <code>Carrier</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Carrier> findCarrier(CarrierVaModus modus) throws FindException;

    /**
     * Sucht nach einer bestimmten Carrier-Kennung.
     *
     * @param ckId ID der gesuchten Carrier-Kennung.
     * @return Instanz einer CarrierKennung.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public CarrierKennung findCarrierKennung(Long ckId) throws FindException;

    /**
     * Ermittelt die CarrierKennung ueber eine angegebene Portierungskennung.
     *
     * @param portierungsKennung die Portierungskennung, zu der die CarrierKennung gesucht werden soll
     * @return Instanz von {@link CarrierKennung}
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public CarrierKennung findCarrierKennung(String portierungsKennung) throws FindException;

    /**
     * Sucht nach allen Carrier-Kennungen.
     *
     * @return Liste mit Objekten des Typs <code>CarrierKennung</code>.
     * @throws FindException wenn bei der Abfrage der Daten ein Fehler auftritt.
     */
    public List<CarrierKennung> findCarrierKennungen() throws FindException;

    /**
     * Ermittelt die CarrierKennung, die einem best. HVT-Standort zugeordnet ist.
     *
     * @param hvtStandortId ID des HVT-Standorts.
     * @return zugeordnete CarrierKennung.
     * @throws FindException wenn bei der Abfrage der Daten ein Fehler auftritt.
     *
     */
    public CarrierKennung findCarrierKennung4Hvt(Long hvtStandortId) throws FindException;

    /**
     * Ermittelt die HVT-Standort ID zu einer bestimmten Carrierbestellung.
     *
     * @param cbId ID der Carrierbestellung zu der der HVT-Standort ermittelt werden soll
     * @return ID des zugehoerigen HVT-Standorts.
     * @throws FindException wenn bei der Abfrage der Daten ein Fehler auftritt.
     *
     */
    public Long findHvtStdId4Cb(Long cbId) throws FindException;

    /**
     * Sucht nach dem Carrier, der fuer einen best. HVT benoetigt wird.
     *
     * @param hvtId ID des HVT-Standorts
     * @return Objekt vom Typ <code>Carrier</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Carrier findCarrier4HVT(Long hvtId) throws FindException;

    /**
     * Ueberprueft, ob die angegebene LBZ fuer den Carrier mit der Id <code>carrierId</code> gueltig ist. <br> Ist die
     * LBZ nicht gueltig, wird eine ValidationException erzeugt.
     *
     * @param carrierId Id des Carriers
     * @param lbz       zu pruefende LBZ.
     * @throws ValidationException wenn die LBZ nicht gueltig ist.
     */
    public void validateLbz(Long carrierId, String lbz) throws ValidationException;

    /**
     * Erstellt eine Basis-LBZ fuer einen best. Carrier und eine best. Endstelle.
     *
     * @param carrierId   ID des Carriers, fuer den die Endstelle verwendet wird.
     * @param endstelleId ID der Endstelle, fuer die die Carrierbestellung ist.
     * @return String mit der Basis-LBZ
     */
    public String createLbz(Long carrierId, Long endstelleId);

    /**
     * Speichert die angegebene Carrierbestellung und ordnet sie der Endstelle <code>endstelle4CB</code> hinzu. <br>
     * <br> Der Carrierbestellung wird die Standortadresse als Anschlussinhaberadresse zugeordnet, wenn folgende Punkte
     * erfuellt sind: <br> <ul> <li>es handelt sich um eine neue Carrierbestellung (ID==null) <li>die Endstelle ist
     * einem HVT zugeordnet </ul>
     *
     * @param toSave       zu speichernde Carrierbestellung
     * @param endstelle4CB Endstelle, zu der die CB gehoert.
     * @return die gespeicherte CB
     * @throws StoreException      wenn beim Speichern oder bei der Zuordnung ein Fehler auftritt.
     * @throws ValidationException wenn ungueltige Daten uebergeben wurden.
     */
    public Carrierbestellung saveCB(Carrierbestellung toSave, Endstelle endstelle4CB) throws StoreException,
            ValidationException;

    /**
     * Speichert die angegebene Carrierbestellung und ordnet sie der Endstelle <code>endstelle4CB</code> hinzu. <br>
     * <br> Die Anschlussinhaberadresse wird dabei nicht verändert.
     *
     * @param carrierBestellung zu speichernde Carrierbestellung
     * @param endstelle         Endstelle, zu der die CB gehoert.
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt
     * @throws ValidationException wenn ungueltige Daten uebergeben wurden.
     */
    public void saveCBWithoutAddress(Carrierbestellung carrierBestellung, Endstelle endstelle) throws StoreException, ValidationException;

    /**
     * Speichert die angegebene Carrierbestellung.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveCB(Carrierbestellung toSave) throws StoreException;

    /**
     * Loescht die angegebene Carrierbestellung. Dies funktioniert nur, wenn es fuer die Carrierbestellung keine
     * CB-Vorgaenge gibt und keine relevanten Daten wie z.B. Rueckmeldedaten Daten auf der Carrierbestellung sind.
     *
     * @param toDelete zu loeschende Carrierbestellung
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt oder Loeschen nicht erlaubt ist
     */
    public void deleteCB(Carrierbestellung toDelete) throws DeleteException;


    /**
     * Speichert die eventuell aus Abschnitten aufsummierte Leitungslänge auf den Kombinationen aus GeoId/HVT Standort.
     */
    public void saveCBDistance2GeoId2TechLocations(Carrierbestellung carrierBestellung, Long sessionId) throws StoreException;

    /**
     * Speichert den angegebenen Carrier
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId ID der aktuellen User-Session
     * @throws StoreException wenn beim Speicher ein Fehler auftritt.
     */
    public void saveCarrier(Carrier toSave, Long sessionId) throws StoreException;

    /**
     * Speichert den angegeben Carrier-Kontakt.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId ID der aktuellen User-Session
     * @throws StoreException wenn beim Speicher ein Fehler auftritt.
     */
    public void saveCarrierContact(CarrierContact toSave, Long sessionId) throws StoreException;

    /**
     * Speichert die angegebene Carrier-Kennung.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId ID der aktuellen User-Session
     * @throws StoreException wenn beim Speicher ein Fehler auftritt.
     */
    public void saveCarrierIdentifier(CarrierKennung toSave, Long sessionId) throws StoreException;

    /**
     * Speichert das angegebene Carrier-Mapping.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId ID der aktuellen User-Session
     * @throws StoreException wenn beim Speicher ein Fehler auftritt.
     */
    public void saveCarrierMapping(CarrierMapping toSave, Long sessionId) throws StoreException;

    /**
     * Ermittelt die Anzahl der durchgefuehrten CuDA-Bestellungen fuer Auftraege, deren Schaltungstermin > vorgabeSCV
     * ist.
     *
     * @param vorgabeSCV
     * @return Liste mit Objekten des Typs <code>CuDAVorschau</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<CuDAVorschau> createCuDAVorschau(Date vorgabeSCV) throws FindException;

    /**
     * Sucht nach den Auftrags-Daten, die eine best. Carrier-Bestellung besitzen.
     *
     * @param cbId ID der Carrierbestellung
     * @return Liste mit Objekten des Typs <code>AuftragDaten</code>, die die angegebene Carrier-Bestellung besitzen
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<AuftragDaten> findAuftragDaten4CB(Long cbId) throws FindException;

    /**
     * Ermittelt fuer einen {@link CBVorgang} mit Typ 'Portwechsel' den referenzierten Auftrag.
     *
     * @param cbVorgang
     * @return referenzierter Auftrag
     */
    public AuftragDaten findReferencingOrder(@NotNull CBVorgang cbVorgang) throws FindException;

    /**
     * Erstellt den Report fuer die Kuendigung einer best. CuDA auf einer best. Endstelle.
     *
     * @param cbId      ID der zu kuendigenden Carrierbestellung
     * @param esId      ID der Endstelle, auf die auf die CB verweist.
     * @param sessionId Session-ID des Users.
     * @return JasperPrint-Objekt
     * @throws AKReportException wenn beim Erstellen des Report ein Fehler auftritt.
     */
    public JasperPrint reportCuDAKuendigung(Long cbId, Long esId, Long sessionId) throws AKReportException;

    /**
     * Speichert eine Carrierbestellung
     */
    void storeCarrierbestellung(Carrierbestellung carierBestellung);

    /**
     * Sucht nach allen Carrier-Eintraegen, die mit M-net direkt via WBCI oder über das WBCI-Portal verbunden sind.
     *
     * @return Liste mit Objekten des Typs <code>Carrier</code>, sortiert nach Namen.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Carrier> findWbciAwareCarrier() throws FindException;

    /**
     * Sucht nach Carrier mit angegebenem ITU Carrier Code.
     *
     * @return <code>Carrier</code> mit ITU Carrier Code.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Carrier findCarrierByCarrierCode(CarrierCode carrierCode) throws FindException;

    /**
     * Sucht nach Carrier mit der angegebenen Portierungskennung.
     *
     * @return <code>Carrier</code> mit der angegebenen Portierungskennung.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Carrier findCarrierByPortierungskennung(String portierungskennung) throws FindException;

}
