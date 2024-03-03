/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2004 12:57:34
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.view.EndstelleAnsprechpartnerView;
import de.augustakom.hurrican.model.cc.Anrede;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.KundeNbz;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.model.shared.view.Anschrift4ExportView;
import de.augustakom.hurrican.model.shared.view.AnschriftView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * KundenService-Interface fuer die Kundendaten aus dem CustomerCare-Bereich.
 * <p/>
 * TODO Kunden-Methoden mit Kundenmethoden in {@link AnsprechpartnerService} in eigenen Service
 *
 *
 */
public interface CCKundenService extends ICCService {

    /**
     * Sucht nach allen wichtigen Auftragsdaten aus dem CC-System eines bestimmten Kunden.
     *
     * @param kundeNo             Kundennummer, dessen CC-Auftragsdaten gesucht werden.
     * @param excludeKonsolidiert Flag, ob konsolidierte Auftraege beruecksichtigt werden sollen.
     * @return Liste mit Objekten des Typs <code>CCKundeAuftragView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<CCKundeAuftragView> findKundeAuftragViews4Kunde(Long kundeNo, boolean excludeKonsolidiert) throws FindException;

    /**
     * @param kundeNo             Kundennummer, dessen CC-Auftragsdaten gesucht werden.
     * @param excludeInvalid
     * @param excludeKonsolidiert Flag, ob konsolidierte Auftraege beruecksichtigt werden sollen.
     * @return Liste mit Objekten des Typs <code>CCKundeAuftragView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     * @see findKundeAuftragViews4Kunde(java.lang.Long, boolean) Ueber das Flag <code>excludeInvalid</code> koennen Auftraege
     * mit Status 'storno', 'Absage' oder 'Kuendigung' heraus gefiltert werden.
     */
    public List<CCKundeAuftragView> findKundeAuftragViews4Kunde(Long kundeNo, boolean excludeInvalid,
            boolean excludeKonsolidiert) throws FindException;

    /**
     * Ueberprueft, ob der Kunde einen Auftrag mit der Produkt-ID <code>prodId</code> besitzt.
     *
     * @param kundeNo Kundennummer
     * @param prodId  Produkt-ID
     * @return true wenn der Kunde min. einen Auftrag des Produkts besitzt.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public boolean hasAuftrag(Long kundeNo, Long prodId) throws FindException;

    /**
     * Sucht alle aktiven (oder in der Realisierung befindenden) Auftraege zu einem Kunden mit den Produkt-Gruppen IDs
     * aus <code>produktGruppen</code>.
     *
     * @param kundeNo        Kundennummer
     * @param produktGruppen Liste mit den zu pruefenden Produkt-Gruppen
     * @return Liste der Auftrags Ids
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    public List<Long> findActiveAuftragInProdGruppe(Long kundeNo, List<Long> produktGruppen)
            throws FindException;

    /**
     * Ueberprueft, ob der Kunden einen aktiven (oder in der Realisierung befindenden) Auftrag mit einer Produkt-Gruppen
     * ID aus <code>produktGruppen</code> besitzt.
     *
     * @param kundeNo        Kundennummer
     * @param produktGruppen Liste mit den zu pruefenden Produkt-Gruppen
     * @return true wenn der Kunde einen aktiven (oder in Bearbeitung befindenden) Auftrag in einer der angegebenen
     * ProduktGruppen besitzt
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    public boolean hasActiveAuftragInProdGruppe(Long kundeNo, List<Long> produktGruppen)
            throws FindException;

    /**
     * Erstellt ein JasperPrint-Objekt mit den Auftragsdaten zu einem bestimmten Kunden.
     *
     * @param kundeNo Auftragsnummer des Kunden
     * @return JasperPrint-Objekt
     * @throws AKReportException wenn der Report nicht erstellt werden konnte.
     */
    public JasperPrint reportKundeAuftragViews(Long kundeNo) throws AKReportException;

    /**
     * Sucht nach einer Anrede.
     *
     * @param anredeKey Anrede-Key fuer den Kunden (definiert in Billing-DB)
     * @param anredeArt Anrede-Art die gesucht wird (definiert als Konstante in de.augustakom.hurrican.model.cc.Anrede).
     * @return Instanz von <code>Anrede</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Anrede findAnrede(String anredeKey, Long anredeArt) throws FindException;

    /**
     * Erstellt ein Anschrifts-Objekt fuer einen best. Kunden. <br>
     *
     * @param kundeNo Kundennummer
     * @return AnschriftView-Objekt aus der Kundenadresse
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    public AnschriftView findAnschrift(Long kundeNo) throws FindException;

    /**
     * Ermittelt die Rechnungsanschrift eines best. Kunden.
     *
     * @param kundeNo     Kundennummer
     * @param rinfoNoOrig ID der Rechnungsinformation
     * @return AnschriftView-Objekt aus der Rechnungsanschrift
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt
     */
    public AnschriftView findREAnschrift(Long kundeNo, Long rinfoNoOrig) throws FindException;

    /**
     * Ermittelt View der Kundenanschrift mit Anrede Wird fuer Datenexport fuer das Kundenportal Muenchen verwendet
     *
     * @param kunde das Kundenobjekt, zu dem die Anschrift ermittelt werden soll.
     * @return Objekt vom Typ Anschrift4ExportView
     * @throws FindException
     */
    public Anschrift4ExportView findAnschrift4Export(Kunde kunde) throws FindException;

    /**
     * Erstellt eine druckbare Anschrift aus einer uebergebene Adresse
     *
     * @param adresse Adresse, die umgewandelt werden soll
     * @return AnschriftView-Objekt, das aus <code>adresse</code> generiert wurde
     * @throws FindException
     */
    public AnschriftView createAnschriftView(Adresse adresse) throws FindException;

    /**
     * Speichert das angegebene Adress-Objekt ab.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt
     * @throws ValidationException wenn das Adress-Objekt ungueltige Daten besitzt
     *
     */
    public CCAddress saveCCAddress(CCAddress toSave) throws StoreException, ValidationException;

    /**
     * Sucht nach einer bestimmten Adresse ueber die ID.
     *
     * @param id ID der gesuchten Adresse.
     * @return Objekt vom Typ <code>CCAddress</code> oder <code>null</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public CCAddress findCCAddress(Long id) throws FindException;

    /**
     * Konvertiert eine Billing-Adresse in eine CC-Adresse
     *
     * @param bAdresse BillingAdresse
     * @return CCAddress-Objekt
     * @throws FindException
     *
     */
    public CCAddress getCCAddress4BillingAddress(Adresse bAdresse) throws FindException;

    /**
     * Generiert eine AnsprechpartnerAdresse abhängig davon, ob GK- bzw. PK-Kunde
     * @param address Adresse mit den Basis-Daten, die in ein neues Adress-Objekt eingetragen werden sollen
     * @param auftragAnsprechpartner Informationen zum Ansprechpartner
     * @return ein neu(!) erzeugtes {@link de.augustakom.hurrican.model.cc.CCAddress} Objekt mit den Daten aus
     * {@code address} und {@code auftragAnsprechpartner}
     */
    public CCAddress getCCAddressAnsprechpartner(CCAddress address, EndstelleAnsprechpartnerView auftragAnsprechpartner);

    /**
     * Sucht nach Adressen. Die gegebenen Namen werden in beiden Namensfeldern gesucht und vorne und hinten mit
     * Wildcards versehen. Falls sowohl name als auch vorname {@code null} sind, wird eine leere Ergebnisliste zurueck
     * geliefert!
     *
     * @param type     Adresstyp
     * @param vorname  Teil des Vornamens
     * @param nachname Teil des Nachnamens
     * @param strasse  Teil der Straße
     * @param ort      Teil des Ortes
     * @return Eine Liste von Adressen, die den Suchkriterien entsprechen
     * @throws FindException falls bei der Suche ein Fehler aufgetreten ist
     */
    List<CCAddress> findCCAddresses(Long type, String vorname, String nachname, String ort, String strasse) throws FindException;

    /**
     * Speichert eine Kunde-Nutzerbezeichnung Zuordnung
     *
     * @param kundeNbz KundeNbz-Objekt
     * @throws StoreException
     */
    public void saveKundeNbz(KundeNbz kundeNbz) throws StoreException;

    /**
     * Sucht nach allen KundeNo, die einer Nutzerbezeichnung zugeordnet sind
     *
     * @param nbz Nutzerbezeichnung
     * @throws FindException
     */
    public List<KundeNbz> findKundeNbzByNbz(String nbz) throws FindException;

    /**
     * Sucht nach einer Kunde-Nutzerbezeichnung Zuordnung in Abhängigkeit der kundeNo
     *
     * @param kundeNo Kundennummer
     * @throws FindException
     */
    public KundeNbz findKundeNbzByNo(Long kundeNo) throws FindException;

    /**
     * Entfernt eine Kunde-Nutzerbezeichnung Zuordnung für eine Kundennummer
     *
     * @param id Id
     * @throws DeleteException
     */
    public void removeKundeNbz(Long id) throws DeleteException;

    /**
     * Findet alle Kunden-Nummern und Hurrican-Aufträge denen ein bestimmtes Adressobjekt zugeordnet ist.
     *
     * @param addressId ID des Adressobjekts
     * @throws FindException
     */
    public List<CCKundeAuftragView> findKundeAuftragViews4Address(Long addressId) throws FindException;
}
