/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 14:06:50
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.Inhouse;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Montageart;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.PhysikUebernahme;
import de.augustakom.hurrican.model.cc.PhysikaenderungsTyp;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery;
import de.augustakom.hurrican.model.cc.view.VerbindungsBezeichnungHistoryView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Interface fuer die Verwaltung von Physikdaten (ausser z.B. Rangierungen, Endstellen).
 *
 *
 */
public interface PhysikService extends ICCService {

    /**
     * Sucht nach allen PhysikTypen, die z.Z. existieren.
     *
     * @return Liste mit Objekten vom Typ <code>PhysikTyp</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<PhysikTyp> findPhysikTypen() throws FindException;

    /**
     * Sucht nach allen Physiktypen, die best. Parent-Physiktypen zugeordnet sind.
     *
     * @param parentPhysikIds Liste mit den IDs der Parent-Physiktypen.
     * @return Liste mit den Physiktypen, die als Parent-Physik eine ID aus der Liste besitzen.
     * @throws FindException
     */
    public List<PhysikTyp> findPhysikTypen4ParentPhysik(List<Long> parentPhysikIds) throws FindException;

    /**
     * Sucht nach einem PhysikTyp ueber dessen ID:
     *
     * @param physikTypId ID des gesuchten PhysikTyps
     * @return PhysikTyp oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public PhysikTyp findPhysikTyp(Long physikTypId) throws FindException;

    /**
     * Erzeugt oder aktualisiert einen PhysikTyp.
     *
     * @param toSave PhysikTyp der erzeugt/aktualisiert werden soll
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void savePhysikTyp(PhysikTyp toSave) throws StoreException;

    /**
     * Ermittelt alle {@link PhysikTyp}en, die zu dem angegebenen example passen.
     * @param example
     * @return
     * @throws FindException
     */
    public List<PhysikTyp> findPhysikTypen(PhysikTyp example) throws FindException;

    /**
     * Ermittelt einen PhysikTyp mit identischer HW-Schnittstelle wie in {@code oldPhysikTyp}, jedoch
     * vom angegebenen Hersteller {@code newManufacturer}.
     * Methode ist vorgesehen, um bei Baugruppen-Schwenks den passenden Physiktyp der Zusatz-Rangierung (z.B.
     * "ADSL-UK0 (H)") zu ermitteln. <br/>
     * <b>Hintergrund:</b> bei einem Baugruppen-Wechsel zwischen verschiedenen Herstellern (z.B. Siemens -> Huawei)
     * muss auch der Physiktyp der Zusatz-Rangierung geaendert werden, damit diese Rangierungen in der Port-Zuordnung
     * zu Auftraegen ermittelt werden koennen.
     * @param oldPhysikTyp
     * @param newManufacturer
     * @return
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt bzw. kein passender/eindeutiger
     *         PhysikTyp zu dem neuen Hersteller gefunden werden kann.
     */
    public PhysikTyp findCorrespondingPhysiktyp(PhysikTyp oldPhysikTyp, Long newManufacturer) throws FindException;

    /**
     * Prueft, ob sich der HW-Hersteller geaendert hat.
     * @param oldPhysikTyp
     * @param newPhysikTyp
     * @return true, wenn beide Physiktypen angegeben sind und sich der Hersteller unterscheidet;
     * sonst 'false'
     */
    public boolean manufacturerChanged(PhysikTyp oldPhysikTyp, PhysikTyp newPhysikTyp);

    /**
     * Sucht die einem Produkt zugeordneten einfachen Physiktypen. Einfach heisst, Physiktypen, die keinen Additional
     * oder Parent Physiktyp konfiguriert haben.
     */
    public List<Produkt2PhysikTyp> findSimpleP2PTs4Produkt(Long produktId) throws FindException;

    /**
     * Sucht nach allen Produkt-PhysikTyp-Zuordnungen zu einem best. Produkt.
     *
     * @param produktId ID des Produkts dessen PhysikTyp-Zuordnungen gesucht werden.
     * @param useInRangMatrix (optionales) Flag gibt an, ob nur {@link Produkt2PhysikTyp}en ermittelt werden sollen,
     *                        bei denen die Rangierungsmatrix erstellt werden soll (TRUE) oder nicht (FALSE). Die
     *                        Angabe von {@code null} bedeutet, dass das Flag nicht ausgewertet wird.
     * @return Liste mit Objekten des Typs <code>Produkt2PhysikTyp</code> oder <code>null</code> falls
     * <code>produktId</code> auch <code>null</code> ist.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Produkt2PhysikTyp> findP2PTs4Produkt(Long produktId, Boolean useInRangMatrix) throws FindException;

    /**
     * Sucht nach allen Physiktypen zu einem bestehenden Produkt.
     */
    public List<Long> findPhysikTypen4Produkt(Long produktId) throws FindException;

    /**
     * Sucht nach allen Produkt-PhysikTyp-Zuordnungen ueber ein Query-Objekt.
     *
     * @param query Query-Objekt mit den Suchparametern
     * @return Liste mit Objekten des Typs <code>Produkt2PhysikTyp</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Produkt2PhysikTyp> findP2PTsByQuery(Produkt2PhysikTypQuery query) throws FindException;

    /**
     * Sucht nach einer best. Produkt-Physiktyp-Zuordnung ueber die ID.
     *
     * @param p2ptId ID der Produkt-Physiktyp-Zuordnung.
     * @return die Produkt-Physiktyp-Zuordnung
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Produkt2PhysikTyp findP2PT(Long p2ptId) throws FindException;

    /**
     * Erstellt Zuordnungen zwischen einem best. Produkt und Physik-Typen.
     *
     * @param produktId ID des Produkts
     * @param toSave    Liste der zu speichernden Mapping-Objekte.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveP2PTs4Produkt(Long produktId, List<Produkt2PhysikTyp> toSave) throws StoreException;

    /**
     * Berechnet die Verbindungsbezeichnung fuer das angegebene Produkt u. den angegebenen Auftrag. <br> Die Methode
     * berechnet lediglich die Verbindungsbezeichnung - sie wird nicht gespeichert!
     *
     * @param productId           ID des Produkts
     * @param taifunAuftragNoOrig (optional) ID des zugehoerigen Taifun Auftrags
     * @return die generierte Verbindungsbezeichnung
     * @throws FindException wenn bei der Berechnung der VerbindungsBezeichnung ein Fehler auftritt
     */
    public VerbindungsBezeichnung calculateVerbindungsBezeichnung(Long productId, Long taifunAuftragNoOrig) throws FindException;

    /**
     * Erzeugt eine neue Verbindungsbezeichnung. <br> (Je nach Produktkonfiguration kann auch eine bestehende
     * VerbindungsBezeichnung zurueck geliefert werden. Dies ist dann der Fall, wenn das Produkt mit
     * vbzUseFromMaster=true konfiguriert ist.)
     *
     * @param productId           ID des Produkts
     * @param taifunAuftragNoOrig (optional) ID des zugehoerigen Taifun Auftrags
     * @return die generierte Verbindungsbezeichnung
     * @throws StoreException wenn bei der Anlage der Verbindungsbezeichnung ein Fehler auftritt
     */
    public VerbindungsBezeichnung createVerbindungsBezeichnung(Long productId, Long taifunAuftragNoOrig) throws StoreException;

    /**
     * Erzeugt eine neue Verbindungsbezeichnung.
     *
     * @param kindOfUseProduct
     * @param kindOfUseType
     * @return
     * @throws StoreException
     */
    VerbindungsBezeichnung createVerbindungsBezeichnung(String kindOfUseProduct, String kindOfUseType)
            throws StoreException;

    /**
     * Ermittelt den 'Master' Auftrag zu der angegebenen Auftrags-ID und gibt die VerbindungsBezeichnung von eben diesem
     * Master-Auftrag zurueck. <br> Als Master-Auftrag wird ein Auftrag angesehen, der folgende Bedingungen erfuellt: -
     * gleicher Taifun Auftrag - Auftrag ist nicht gekuendigt / storniert - Produkt des Auftrags ist mit
     * VerbindungsBezeichnung-Prefix konfiguriert
     *
     * @param taifunAuftragNoOrig
     * @return die VerbindungsBezeichnung des Master-Auftrags oder <code>null</code>, wenn nicht ermittelbar
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt
     */
    public VerbindungsBezeichnung getVerbindungsBezeichnungFromMaster(Long taifunAuftragNoOrig) throws FindException;

    /**
     * "Berechnet" die Verbindungsbezeichnung fuer den angegebenen Auftrag neu. <br> Bei der Berechnung werden die
     * beiden Felder fuer die Nutzungsart abhaengig von der aktuellen Auftragskonfiguration ermittelt. <br> Dies
     * geschieht jedoch nur, wenn die VerbindungsBezeichnung nicht als 'ueberschrieben' gekennzeichnet ist.
     *
     * @param auftragId ID des techn. Auftrags, dessen Verbindungsbezeichnung neu berechnet werden soll
     * @throws StoreException wenn bei der Berechnung ein Fehler auftritt.
     */
    public void reCalculateVerbindungsBezeichnung(Long auftragId) throws StoreException;

    /**
     * Speichert die angegebene Verbindungsbezeichnung ab.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public VerbindungsBezeichnung saveVerbindungsBezeichnung(VerbindungsBezeichnung toSave) throws StoreException, ValidationException;

    /**
     * Ermittelt fuer einen Auftrag und eine bestimmte Endstelle die gegenueber anderen Carriern zu verwendende
     * Verbindungsbezeichnung. <br> Abhaengig vom Format der VerbindungsBezeichnung wird folgendes zurueck geliefert: -
     * UniqueCode gesetzt --> <NA><EC>_<TAL_ID> - kein UniqueCode  -->  <VerbindungsBezeichnung>_<TAL_ID>
     * <p/>
     * Die TAL_ID setzt sich wie folgt zusammen: ES_TYP + "_" + STIFT
     *
     * @param auftragId
     * @param endstelleTyp
     * @return
     * @throws FindException
     */
    public String getVbzValue4TAL(Long auftragId, String endstelleTyp) throws FindException;

    /**
     * Sucht nach einem VerbindungsBezeichnung-Objekt ueber die Verbindungsbezeichnung.
     *
     * @param vbz Verbindungsbezeichnung
     * @return das zugehoerige VerbindungsBezeichnung-Objekt oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public VerbindungsBezeichnung findVerbindungsBezeichnung(String vbz) throws FindException;

    /**
     * Sucht nach allen Verbindungsbezeichnungen, die den String <code>vbz</code> beinhalten.
     *
     * @param vbz Gesamte oder Teil einer VerbindungsBezeichnung.
     * @return Liste mit Objekten des Typs <code>VerbindungsBezeichnung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<VerbindungsBezeichnung> findVerbindungsBezeichnungLike(String vbz) throws FindException;

    /**
     * Sucht nach einer VerbindungsBezeichnung ueber die ID.
     *
     * @param vbzId ID der gesuchten VerbindungsBezeichnung.
     * @return die VerbindungsBezeichnung oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public VerbindungsBezeichnung findVerbindungsBezeichnungById(Long vbzId) throws FindException;

    /**
     * Sucht nach der VerbindungsBezeichnung, die einem best. Auftrag zugeordnet ist. <br> WICHTIG: die Methode
     * unterstuetzt keine Transaktionen!!!
     *
     * @param ccAuftragId ID des CC-Auftrags, dessen VerbindungsBezeichnung gesucht wird.
     * @return VerbindungsBezeichnung oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public VerbindungsBezeichnung findVerbindungsBezeichnungByAuftragId(Long ccAuftragId) throws FindException;

    /**
     * @see #findVerbindungsBezeichnungByAuftragId(Long) Diese Methode unterstuetzt bestehende Transaktionen!!!
     */
    public VerbindungsBezeichnung findVerbindungsBezeichnungByAuftragIdTx(Long ccAuftragId) throws FindException;

    /**
     * Sucht nach einer bestehenden Verbindungsbezeichnung und generiert wenn es nötig ist eine zugehörige {@link
     * VerbindungsBezeichnung#wbciLineId}.
     *
     * @param auftragId Id des Hurrican-Auftrags, dessen Verbindungsbezeichnung gesucht wird.
     * @return null oder eine {@link VerbindungsBezeichnung} mit gültiger WBCI-Line-Id.
     * @throws FindException
     * @throws StoreException
     * @throws ValidationException
     */
    VerbindungsBezeichnung findOrCreateVerbindungsBezeichnungForWbci(Long auftragId) throws FindException, StoreException, ValidationException;

    /**
     * Liefert alle VerbindungsBezeichnungen zu einem Kunden
     *
     * @param kundeNo Kundennummer, zu der die VerbindungsBezeichnungen ermittelt werden sollen
     * @return Liste mit Objekten des Typs <code>VerbindungsBezeichnung</code>.
     *
     */
    public List<VerbindungsBezeichnung> findVerbindungsBezeichnungenByKundeNoOrig(Long kundeNo) throws FindException;

    /**
     * Sucht nach allen Auftraegen, die eine best. VerbindungsBezeichnung besitzen.
     *
     * @param vbz Bezeichnung der VerbindungsBezeichnung.
     * @return Liste mit Objekten des Typs <code>VerbindungsBezeichnungHistoryView</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<VerbindungsBezeichnungHistoryView> findVerbindungsBezeichnungHistory(String vbz) throws FindException;

    /**
     * Funktion liefert eine Leitungsart anhand des Namens. Es wird nur genau ein Ergebnis geliefert, sonst wird null
     * geliefert.
     *
     * @param name Name der gesuchten Leitungsart
     * @return Leitungsart
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     *
     */
    public Leitungsart findLeitungsartByName(String name) throws FindException;

    /**
     * Sucht nach allen vorhandenen Montagearten.
     *
     * @return Liste mit Objekten des Typs <code>Montageart</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Montageart> findMontagearten() throws FindException;

    /**
     * Sucht nach allen Inhouse-Daten fuer eine bestimmte Endstelle.
     *
     * @param esId ID der Endstelle deren Inhouse-Daten gesucht werden.
     * @return Liste mit Objekten des Typs <code>Inhouse</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Inhouse> findInhouses4ES(Long esId) throws FindException;

    /**
     * Sucht nach den aktuellen(!) Inhouse-Daten fuer eine best. Endstelle.
     *
     * @param esId ID der Endstelle deren aktuelle Inhouse-Daten gesucht werden.
     * @return Inhouse oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Inhouse findInhouse4ES(Long esId) throws FindException;

    /**
     * Speichert die Inhouse-Daten ab. <br> Ueber das Flag <code>makeHistory</code> kann definiert werden, ob ein
     * bereits bestehender Datensatz historisiert werden soll.
     *
     * @param toSave      zu speicherndes Objekt
     * @param makeHistory Flag bestimmt, ob ein bereits bestehender Datensatz historisiert wird.
     * @return Abhaengig von makeHistory wird <code>toSave</code> oder eine neue Instanz von Inhouse zurueck gegeben.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public Inhouse saveInhouse(Inhouse toSave, boolean makeHistory) throws StoreException;

    /**
     * Sucht nach der Leitungsart, die einer best. Endstelle zugeordnet ist.
     *
     * @param esId ID der Endstelle, deren Leitungsart gesucht wird.
     * @return Instanz von <code>Leitungsart</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Leitungsart findLeitungsart4ES(Long esId) throws FindException;

    /**
     * Sucht nach der Schnittstelle, die einer best. Endstelle zugeordnet ist.
     *
     * @param esId ID der Endstelle, deren Schnittstelle gesucht wird.
     * @return Instanz von <code>Schnittstelle</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Schnittstelle findSchnittstelle4ES(Long esId) throws FindException;

    /**
     * Laedt eine Anschlussart ueber deren ID. <br> Die Anschlussart-ID ist z.B. einem HVT-Standort zugeordnet. Darueber
     * wird bestimmt, wie ein HVT-Standort realisiert ist.
     *
     * @param anschlussArtId ID der zu ladenden Anschlussart.
     * @return Anschlussart mit der gesuchten ID oder <code>null</code>
     * @throws FindException wenn bei der Suche ein Fehler auftritt
     */
    public Anschlussart findAnschlussart(Long anschlussArtId) throws FindException;

    /**
     * Sucht nach allen Anschlussarten.
     *
     * @return Liste mit Objekten vom Typ <code>Anschlussart</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Anschlussart> findAnschlussarten() throws FindException;

    /**
     * Erzeugt oder aktualisiert eine Anschlussart.
     *
     * @param toSave zu speichernde Anschlussart
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveAnschlussart(Anschlussart toSave) throws StoreException;

    /**
     * Sucht nach dem letzten PhysikUebernahme-Eintrag fuer einen bestimmten Auftrag.
     *
     * @param auftragId ID des Auftrags, dessen letzter PhysikUebernahme-Eintrag gesucht wird.
     * @return Instanz von PhysikUebernahme oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public PhysikUebernahme findLastPhysikUebernahme(Long auftragId) throws FindException;

    /**
     * Sucht nach einem PhysikUebernahme-Eintrag fuer einen bestimmten Verlauf.
     *
     * @param auftragId ID des Auftrags fuer die Physikuebernahme (wird als Auftrag A interpretiert)
     * @param verlaufId ID des Verlaufs, fuer den ein PhysikUebernahme-Eintrag gesucht wird.
     * @return Instanz von <code>PhysikUebernahme</code> oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public PhysikUebernahme findPhysikUebernahme4Verlauf(Long auftragId, Long verlaufId) throws FindException;

    /**
     * Sucht nach allen PhysikUebernahmen zu einem bestimmten Verlauf.
     *
     * @param verlaufId
     * @return Liste mit Objekten des Typs {@link PhysikUebernahme}
     * @throws FindException
     */
    public List<PhysikUebernahme> findPhysikUebernahmen4Verlauf(Long verlaufId) throws FindException;

    /**
     * Ermittelt eine bestimmte Physikuebernahme ueber die Vorgangs-ID und das Kriterium.
     *
     * @param vorgang   ID des Vorgangs
     * @param kriterium Art des Kriteriums
     * @return Instanz von <code>PhysikUebernahme</code> oder <code>null</code>
     * @throws FindException
     *
     */
    public PhysikUebernahme findPhysikUebernahme(Long vorgang, Integer kriterium) throws FindException;

    /**
     * Gibt eine Liste mit allen definierten Physikaenderungs-Typen zurueck.
     *
     * @return Liste mit Objekten des Typs <code>PhysikaenderungsTyp</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<PhysikaenderungsTyp> findPhysikaenderungsTypen() throws FindException;

    /**
     * Liefert alle moeglichen Physiktyp-Kombinationen
     *
     * @return Liste mit Arrays, die den Physiktyp und eventl. zugehoerigen Physiktyp enthalten
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public List<Object[]> findPhysiktypKombinationen() throws FindException;

    /**
     * Liefert eine bestimmten Physiktyp anhand des Namens.
     *
     * @param name Name des Physiktyps
     * @return Gesuchter Physiktyp
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public PhysikTyp findPhysikTypByName(String name) throws FindException;

    /**
     * Loescht die uebergebene Verbindungsbezeichnung.
     *
     * @param vbz
     */
    public void deleteVerbindungsBezeichnung(VerbindungsBezeichnung vbz);
}


