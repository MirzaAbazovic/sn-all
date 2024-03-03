/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2008 14:00:08
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import javax.annotation.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.base.iface.StoreService;

/**
 * Service fuer die Verwaltung von Endstellen.
 *
 *
 */
public interface EndstellenService extends ICCService, StoreService {

    /**
     * Legt Endstellen an und ordnet diese der AuftragTechnik zu. <br> Abhaengig von <code>esTyp</code> wird keine oder
     * beide Endstellen (A+B) angelegt. <br> <br> Fuer die angelegten Endstellen wird immer eine neue Gruppen-ID
     * erzeugt. Diese wird der AuftragTechnik zugeordnet. Hatte die AuftragTechnik bereits eine ES-Gruppen-ID, so wird
     * der bisherige AuftragTechnik-Datensatz historisiert.
     *
     * @param auftragTechnik AuftragTechnik, der die Endstellen zugeordnet werden sollen.
     * @param esTyp          Endstellen-Typen, die angelegt werden sollen. Die moeglichen Werte sind in dem Modell
     *                       <code>Produkt</code> definiert.
     * @param sessionId
     * @return Liste mit den angelegten Endstellen (Objekte vom Typ <code>Endstelle</code>).
     * @throws StoreException wenn beim Anlegen der Endstellen ein Fehler auftritt.
     */
    @CheckForNull
    List<Endstelle> createEndstellen(AuftragTechnik auftragTechnik, Integer esTyp, Long sessionId)
            throws StoreException;

    /**
     * Speichert die Endstelle <code>toSave</code>. <br> Bei dieser Methode wird die Endstelle mit dem aktuellen Datum
     * fuer die letzte Aenderung versehen. Zusaetzlich wird die Endstelle mit der Anschlussart versehen.
     * <br><br>
     * <b>Hinweis:</b> Sollte die Rangierung der Endstelle sich aendern, ist es sinnvoll nach der Endstelle die
     * zuegehoerigen Endgeraete Konfiguration zu aktualisieren. Siehe hierzu auch
     * {@link EndgeraeteService#updateSchicht2Protokoll4Endstelle(Endstelle)} und HUR-23209.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    Endstelle saveEndstelle(Endstelle toSave) throws StoreException;

    /**
     * Kopiert die Daten der Endstelle 'src' auf 'dest'. <br> Besitzt die Endstelle eine Adresse, wird auch diese
     * kopiert und die neue Adresse auf der Ziel-Endstelle verwendet.
     *
     * @param src            Ursprungs-Endstelle, von der Daten kopiert werden sollen.
     * @param dest           Ziel-Endstelle
     * @param copyProperties Angabe der zu kopierenden Endstellen-Felder.
     * @throws StoreException wenn beim Kopieren ein Fehler auftritt.
     *
     */
    void copyEndstelle(Endstelle src, Endstelle dest, List<String> copyProperties) throws StoreException;

    /**
     * Sucht nach einer best. Endstelle ueber deren ID.
     *
     * @param esId ID der gesuchten Endstelle.
     * @return Instanz von <code>Endstelle</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Endstelle findEndstelle(Long esId) throws FindException;

    /**
     * Sucht nach einer best. Endstelle einer Endstellen-Gruppe.
     *
     * @param esGruppe ID der Endstellen-Gruppe
     * @param esTyp    gesuchter Endstellen-Typ (A oder B)
     * @return Instanz von <code>Endstelle</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Endstelle findEndstelle(Long esGruppe, String esTyp) throws FindException;

    /**
     * Sucht nach allen Endstellen, die einem best. Auftrag zugeordnet sind. Die Endstellen sind nach der ID in
     * <b>umgekehrter</b> Reihenfolge (desc) sortiert. (Endstelle 'B' wird also zuerst angezeigt.) <br> Die Methode
     * unterstuetzt bestehende Transaktionen!!
     *
     * @param auftragId ID des Auftrags.
     * @return Liste mit Objekten des Typs <code>Endstelle</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Endstelle> findEndstellen4Auftrag(Long auftragId) throws FindException;

    /**
     * Im Gegensatz zu {@link EndstellenService#findEndstellen4Auftrag(Long)} werden in dieser Methode nur die
     * Endstellen zurueck geliefert, die lt. Produkt-Konfiguration des Auftrags relevant sind. Es wird also
     * {@link Produkt#endstellenTyp} ausgewertet.
     *
     * @see EndstellenService#findEndstellen4Auftrag(Long)
     * @param auftragId
     * @return
     * @throws FindException
     */
    List<Endstelle> findEndstellen4AuftragBasedOnProductConfig(Long auftragId) throws FindException;

    List<Endstelle> findEndstellen4AuftragWithoutExplicitFlush(Long auftragId) throws FindException;

    /**
     * Sucht nach einer best. Endstelle fuer den Auftrag mit der ID <code>auftragId</code>. Die Methode unterstuetzt
     * bestehende Transaktionen!!
     *
     * @param auftragId ID des Auftrags
     * @param esTyp     Typ der gesuchten Endstelle
     * @return Instanz von <code>Endstelle</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Endstelle findEndstelle4Auftrag(Long auftragId, String esTyp) throws FindException;

    /**
     * Ausfuehrung in NEUER Transaktion!
     * @see de.augustakom.hurrican.service.cc.EndstellenService#findEndstelle4Auftrag(Long, String)
     * @param auftragId
     * @param esTyp
     * @return
     * @throws FindException
     */
    Endstelle findEndstelle4AuftragNewTx(Long auftragId, String esTyp) throws FindException;

    /**
     * Ermittelt alle Endstellen, die die angegebene Rangier-ID besitzen.
     *
     * @param rangierId
     * @return
     * @throws FindException
     */
    List<Endstelle> findEndstellenWithRangierId(Long rangierId) throws FindException;

    /**
     * Sucht nach Endstellen fuer eine {@code geoId}. Die Methode unterstuetzt bestehende Transaktionen!!
     *
     * @return Instanz(en) von {@code Endstelle} oder {@code null}.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Endstelle> findEndstellenByGeoId(Long geoId) throws FindException;

    /**
     * Sucht nach allen Endstellen fuer eine {@code Carrierbestellung}. Die Methode unterstuetzt bestehende
     * Transaktionen!!
     */
    List<Endstelle> findEndstellen4Carrierbestellung(Carrierbestellung carrierbestellung) throws FindException;

    /**
     * Sucht nach einer Endstelle fuer eine {@code Carrierbestellung} im angegebenen {@link Auftrag}. Die Methode
     * unterstuetzt bestehende Transaktionen!!
     */
    Endstelle findEndstelle4CarrierbestellungAndAuftrag(Long cbId, Long auftragId) throws FindException;

    /**
     * Sucht nach den Auftrags/Endstellendaten, die fuer eine Anschlussuebernahme an eine best. Endstelle (
     * <code>endstelleId</code>) in Frage kommen.
     *
     * @param endstelleId ID der Endstelle, an die eine Physik uebernommen werden soll.
     * @return Liste mit den gefundenen Auftrags/Endstellendaten.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragEndstelleView> findEndstellen4Anschlussuebernahme(Long endstelleId) throws FindException;

    /**
     * Sucht nach den Auftrags/Endstellendaten, die fuer eine Bandbreitenaenderung in Frage kommen.
     *
     * @param endstelleId ID der Endstelle, auf die eine best. Physik uebernommen werden soll.
     * @return Liste mit den gefundenen Auftrags/Endstellendaten.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragEndstelleView> findEndstellen4Bandbreite(Long endstelleId) throws FindException;

    /**
     * Sucht nach den Auftrags/Endstellendaten, die fuer einen Wandel Analog-->ISDN an eine best. Endstelle (
     * <code>endstelleId</code>) in Frage kommen.
     *
     * @param endstelleId ID der Endstelle, auf die eine Physik uebernommen werden soll.
     * @param analog2isdn Flag, ob nach Endstellen fuer die Wandlung von Analog auf ISDN (true) oder von ISDN auf Analog
     *                    (false) gesucht wird. Wird fuer den Parameter <code>null</code> angegeben, werden alle
     *                    gekuendigten Auftraege des Kunden gesucht - unabhaengig vom Produkt.
     * @return Liste mit den gefundenen Auftrags/Endstellendaten.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<AuftragEndstelleView> findEndstellen4Wandel(Long endstelleId, Boolean analog2isdn) throws FindException;

    /**
     * Sucht nach allen Endstellen (bzw. Auftraegen) zu einer best. GeoID, die fuer eine Port-Aenderung (WITA: LAE,
     * AEN-LMAE, SER-POW) in Frage kommen <br> Die in Frage kommenden Auftraege muessen folgende Bedingungen erfuellen:
     * <ul> <li>Endstelle befindet sich an angegebener GeoID <li>Auftrag befindet sich in einem Kuendigungs-Status
     * </ul>
     *
     * @param geoId ID der GeoId, an der sich der zu ermittelnde Auftrag befinden soll
     * @param esTyp Typ der Endstelle
     * @return Liste mit Objekten des Typs {@link AuftragEndstelleView}
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    List<AuftragEndstelleView> findEndstellen4TalPortAenderung(Long geoId, String esTyp) throws FindException;

    /**
     * @param endstellenId ID der Endstelle, deren Ansprechpartner gesucht wird.
     * @return Endstellen-Ansprechpartner oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     * @deprecated use {@link AnsprechpartnerService#findPreferredAnsprechpartner(de.augustakom.hurrican.model.cc.Ansprechpartner.Typ,
     * Integer)} Sucht nach dem aktuellen Endstellen-Ansprechpartner, der einer best. Endstelle zugeordnet ist. <br>
     * WICHTIG: diese Methode unterstuetzt keine Transaktionen!!
     */
    @Deprecated
    EndstelleAnsprechpartner findESAnsp4ES(Long endstellenId) throws FindException;

    /**
     * @see findESAnsp4ES Diese Methode unterstuetzt bestehende Transaktionen!!!
     * @deprecated use {@link AnsprechpartnerService#findPreferredAnsprechpartner(de.augustakom.hurrican.model.cc.Ansprechpartner.Typ,
     * Integer)}
     */
    @Deprecated
    EndstelleAnsprechpartner findESAnsp4ESTx(Long endstellenId) throws FindException;

    /**
     * Sucht nach den aktuellen Leitungsdaten zu einer Endstelle. <br> WICHTIG: Transaktionen werden in dieser Methode
     * ausgesetzt!
     *
     * @param esId ID der Endstelle, deren Leitungsdaten gesucht werden.
     * @return EndstelleLtgDaten oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    EndstelleLtgDaten findESLtgDaten4ES(Long esId) throws FindException;

    /**
     * @see findESLtgDaten4ES Diese Methode unterstuetzt bestehende Transaktionen!!!
     */
    EndstelleLtgDaten findESLtgDaten4ESTx(Long esId) throws FindException;

    /**
     * Speichert ein Leitungsdaten-Objekt. <br> Ueber das Flag <code>makeHistory</code> kann definiert werden, ob ein
     * bereits bestehender Datensatz historisiert werden soll.
     *
     * @param toSave      zu speicherndes Objekt
     * @param makeHistory Angabe, ob ein bestehender DS historisiert werden soll.
     * @return Abhaengig von makeHistory wird <code>toSave</code> oder eine neue Instanz von
     * <code>EndstelleLtgDaten</code> zurueck gegeben.
     * @throws FindException wenn beim Speichern ein Fehler auftritt.
     */
    EndstelleLtgDaten saveESLtgDaten(EndstelleLtgDaten toSave, boolean makeHistory) throws StoreException,
            ValidationException;

    /**
     * Die Funktion ermittelt die im Billing-System zu diesem Auftrag angegebene(n) Anschlussadresse(n) und uebernimmt
     * diese Daten auf die Endstelle(n) des angegebenen Auftrags. <br>
     *
     * @param auftragDaten AuftragDaten-Objekt des Hurrican-Auftrags
     * @param sessionId    ID der aktuellen UserSession
     * @throws FindException  wenn bei der Ermittlung der Daten ein Fehler auftritt
     * @throws StoreException wenn beim Speichern der Daten ein Fehler auftritt
     *
     */
    void copyAPAddress(AuftragDaten auftragDaten, Long sessionId) throws FindException, StoreException;

    /**
     * Funktion prueft, ob Aenderungen zwischen der Hurrican-Endstelle und der Taifun-AP-Adresse vorhanden sind. <br> Es
     * werden beide Endstellen, entsprechend der Produkt-Konfiguration beruecksichtigt.
     *
     * @param ad AuftragDaten-Objekt des Hurrican-Auftrags
     * @return true falls Adressen nicht(!) identisch sind
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    Boolean hasAPAddressChanged(AuftragDaten ad) throws FindException;

    /**
     * Funktion prueft, ob Aenderungen zwischen der Hurrican-Endstelle und der Taifun-AP-Adresse vorhanden sind. <br> Es
     * wird nur der angegebene Endstellen-Typ beruecksichtigt.
     *
     * @param ad    AuftragDaten-Objekt des Hurrican-Auftrags
     * @param esTyp Endtsellen-Typ
     * @return true falls Adressen nicht(!) identisch sind
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    Boolean hasAPAddressChanged(AuftragDaten ad, String esTyp) throws FindException;

    /**
     * Funktion liefert die Anschlussadresse der Endstelle A oder B zu einem bestimmten Auftrag. Es wird unterschieden,
     * ob fuer den betroffenen Auftrag die Hurrican-Endstellenadresse oder die Taifun-Anschlussadresse verwendet werden
     * muss. <br>
     *
     * @param auftragId Auftragsnummer zu dem die Anschlussadresse gesucht wird
     * @param esTyp     Endstelle A oder B
     * @return Adress-Objekt mit der gesuchten Anschlussadresse
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    AddressModel findAnschlussadresse4Auftrag(Long auftragId, String esTyp) throws FindException;

    /**
     * Sucht nach den Endstellen-Daten eines zugehoerigen Buendel-Auftrags und kopiert deren Daten in die Endstellen
     * <code>endstellen</code>. <br> Ablauf: <br> 1. Alle Auftraege des Buendels suchen <br> 2. Endstellen der einzelnen
     * Auftraege suchen <br> 3. Falls Endstelle vorhanden, werden Endstellendaten kopiert und gespeichert <br> 4.
     * Ansprechpartner fuer Endstelle kopieren
     *
     * @param endstellen
     * @param buendelNr
     * @param buendelNrHerkunft
     * @throws FindException
     *
     */
    void transferBuendel2Endstellen(List<Endstelle> endstellen, Integer buendelNr, String buendelNrHerkunft)
            throws FindException;

    /**
     * Ersetzt übergebene alte GeoId-Referenz mit neuer Endstellenebene
     */
    void replaceGeoIdsOfEnstellen(Long geoId, Long replacedByGeoId) throws StoreException, FindException;

    Endstelle findEndstelle4AuftragWithoutExplicitFlush(Long auftragId, String esTyp) throws FindException;
}
