/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 10:54:42
 */
package de.augustakom.hurrican.service.billing;

import java.util.*;

import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Ansprechpartner;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.shared.view.DefaultSharedAuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Interface definiert spezielle Methoden fuer einen Service, der Objekte des Typs <code>Kunde</code> (und Modelle, die
 * davon abhaengig sind) verwaltet. <br>
 *
 *
 */
public interface KundenService extends IBillingService {

    /**
     * Sucht nach einem Kunden ueber die Kundennummer - nicht die original Kundennummer!
     *
     * @param kundeNo Kundennummer des gesuchten Kunden.
     * @return Instanz von <code>Kunde</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Kunde findKunde(Long kundeNo) throws FindException;

    /**
     * Sucht nach den Kunden mit den Kundennummern, die in <code>kundeNos</code> angegeben sind.
     *
     * @param kundeNos Liste mit den Kundennummern, der Kunden, die geladen werden sollen.
     * @return Liste mit Objekten des Typs <code>Kunde</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Kunde> findByKundeNos(List<Long> kundeNos) throws FindException;

    /**
     * Sucht nach allen Kunden, deren Kundennummern sich innerhalb des Arrays <code>kNoOrigs</code> befinden. <br> Als
     * Suchparameter wird ausserdem HistStatus=AKT verwendet.
     *
     * @param kNos Array mit den Kundennummern.
     * @return Map mit den Kundendaten. Als Key wird die (original) Kundennummer verwendet, als Value das
     * <code>Kunde</code-Objekt.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Map<Long, Kunde> findKundenByKundeNos(Long[] kNos) throws FindException;

    /**
     * Sucht nach allen Kunden und zugehoerigen Adressen, die mit den Query-Parametern uebereinstimmen.
     *
     * @param query the search query
     * @return Liste mit Objekten vom Typ <code>KundeAdresseView</code>
     * @throws FindException
     */
    public List<KundeAdresseView> findKundeAdresseViewsByQuery(KundeQuery query) throws FindException;

    /**
     * Sucht nach den Adressen zu den Kundennummern <code>kundeNos</code>.
     *
     * @param kundeNos Liste mit den Kunden-Nummern, deren Adressen ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>Adresse</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Adresse> findAdressen4Kunden(List<Long> kundeNos) throws FindException;

    /**
     * Ermittelt zu den angegebenen Kundennummern die zugehoerigen Ansprechpartner. <br> Wichtig: einem Kunden koennen
     * mehrere Ansprechpartner zugeordnet sein
     *
     * @param kundeNos Liste mit Kundennummern, deren Ansprechpartner gesucht werden.
     * @return Liste mit Objekten des Typs <code>Ansprechpartner</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Ansprechpartner> getAnsprechpartner4Kunden(List<Long> kundeNos) throws FindException;

    /**
     * Sucht nach der Adresse eines best. Kunden. Der Kunde wird ueber die Kunden-No. identifiziert.
     *
     * @param kundenNo Kundennummer des Kunden, dessen Adresse gesucht wird.
     * @return Adresse des Kunden oder <code>null</code>, wenn nicht gefunden.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Adresse getAdresse4Kunde(Long kundenNo) throws FindException;

    /**
     * @param adresse zu formatierende Adresse
     * @param type    Angabe des Typs, wie formatiert werden soll (Konstante aus AddressFormat)
     * @return Array mit den einzelnen Adress-Zeilen.
     * @throws FindException wenn bei der Formatierung ein Fehler auftritt.
     * @deprecated darf noch nicht verwendet werden!!! Es muessen erst noch die entsprechenden Methoden in
     * CCKundenService aufgeraeumt werden!
     * <p/>
     * Formatiert die uebergebene Adresse.
     */
    @Deprecated
    public String[] formatAddress(Adresse adresse, String type) throws FindException;

    /**
     * Sucht eine Adresse nach der Adress-No
     *
     * @param adressNo primary key of the address
     * @return Adresse
     * @throws FindException
     */
    public Adresse getAdresseByAdressNo(Long adressNo) throws FindException;

    /**
     * Laedt die Kundendaten fuer alle <code>AbstractSharedAuftragView</code>-Objekte, die sich innerhalb der Liste
     * <code>views</code> befinden. <br> Die Kundendaten werden dabei direkt in die Views geschrieben. Evtl. auftretende
     * Fehler innerhalb dieser Methode werden nur geloggt!
     *
     * @param views Liste mit Objekten des Typs <code>AbstractSharedAuftragView</code>.
     */
    public void loadKundendaten4AuftragViews(List<? extends DefaultSharedAuftragView> views);

    /**
     * Gibt eine Liste mit den (original) Kundennummern der Kunden zurueck, die einem best. Haupt-Kunden zugeordnet
     * sind.
     *
     * @param hauptKundeNo ID des Hauptkunden dessen zugeordnete Kunden gesucht werden.
     * @return Liste mit Integer-Objekten
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Long> findKundeNos4HauptKunde(Long hauptKundeNo) throws FindException;

    /**
     * Sucht nach den Kunden-Namen, die den Kundennummern entsprechen.
     *
     * @param kundeNos Liste mit den Kundennummern, zu denen die Namen ermittelt werden sollen
     * @return Instanz einer Map. Als Key werden die Kunde-Nos, als Value der zugehoerige Kunden-Name verwendet.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Map<Long, String> findKundenNamen(List<Long> kundeNos) throws FindException;

    /**
     * Ueberprueft, ob die Kundennummern identisch sind bzw. ob einer der beiden Kunden der Unterkunde zum anderen
     * Kunden ist.
     *
     * @param kundeNoA first customer no
     * @param kundeNoB second customer no
     * @return true, wenn sich die Kundennummern im gleichen Kundenkreis befinden.
     * @throws FindException wenn bei der Ueberpruefung ein Fehler auftritt.
     */
    public boolean isSameKundenkreis(Long kundeNoA, Long kundeNoB) throws FindException;

}


