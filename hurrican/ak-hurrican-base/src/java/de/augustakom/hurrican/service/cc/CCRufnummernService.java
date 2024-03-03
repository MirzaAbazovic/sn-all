/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2005 09:12:42
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.dn.DNLeistungsView;
import de.augustakom.hurrican.model.cc.dn.Lb2Leistung;
import de.augustakom.hurrican.model.cc.dn.Leistung2DN;
import de.augustakom.hurrican.model.cc.dn.Leistung2Parameter;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel;
import de.augustakom.hurrican.model.cc.dn.Leistungsbuendel2Produkt;
import de.augustakom.hurrican.model.cc.view.LeistungInLeistungsbuendelView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.LeistungService;


/**
 * Service-Interface fuer die Verwaltung von Rufnummern-Daten in der CC-Datenbank.
 */
public interface CCRufnummernService extends ICCService {

    /**
     * Sucht nach allen Rufnummern-Leistungen, die den Rufnummern mit den IDs <code>dnNos</code> zugeordnet sind.
     *
     * @param dnNos              Liste mit den IDs der Rufnummern, deren Leistungen ermittelt werden sollen.
     * @param leistungsbuendelId (optional) ID des zugehoerigen Leistungsbuendels.
     * @return Liste mit Objekten des Typs <code>DNLeistungsView</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<DNLeistungsView> findDNLeistungen(List<Long> dnNos, Long leistungsbuendelId) throws FindException;

    /**
     * Sucht nach allen Leistungsbuendel.
     *
     * @return Liste mit Objekten des Typs <code>Leistungsbuendel</code>.
     * @throws FindException
     */
    public List<Leistungsbuendel> findLeistungsbuendel() throws FindException;

    /**
     * Sucht nach allen Leistungsbuendel, die einem bestimmten Produkt zugeordnet sind.
     *
     * @param prodLeistungNoOrig die Leistungs-ID der fuer externe Systeme relevanten Produkt-Leistung.
     * @return Liste mit Objekten des Typs <code>Leistungsbuendel2Produkt</code>.
     * @throws FindException
     */
    public List<Leistungsbuendel2Produkt> findLeistungsbuendel2Produkt(Long prodLeistungNoOrig) throws FindException;

    /**
     * Ermittelt das DN-Leistungsbuendel, das fuer einen Auftrag in Frage kommt.
     *
     * @param ccAuftragId CC-Auftrags ID, ueber die der Billing-Auftrag ermittelt werden kann.
     * @return Leistungsbuendel
     * @throws FindException wenn bei der Ermittlung des Buendels ein Fehler auftritt.
     */
    public Leistungsbuendel findLeistungsbuendel4Auftrag(Long ccAuftragId) throws FindException;

    /**
     * @param ccAuftragId
     * @param leistungService
     * @return
     * @throws FindException
     * @see {@link CCRufnummernService#findLeistungsbuendel4Auftrag(Long)}
     */
    public Leistungsbuendel findLeistungsbuendel4Auftrag(Long ccAuftragId, LeistungService leistungService) throws FindException;

    /**
     * Speichert Mappings von Leistungsbuendeln zu einem Produkt.
     *
     * @param productOeNoOrig Produkt-ID vom Billing-System
     * @param leistungNoOrig  ID der Produkt-Leistung
     * @param dnoe2p          Collection mit den Leistungsbuendel-IDs, die zugeordnet werden sollen.
     * @throws StoreException
     */
    public void saveLeistungsbuendel2Produkt(Long productOeNoOrig, Long leistungNoOrig, Collection<Long> dnoe2p)
            throws StoreException;

    /**
     * Sucht nach allen Leistungen, die Rufnummern zugeordnet werden koennen.
     *
     * @return Liste mit Objekten des Typs <code>Leistung4Dn</code>.
     * @throws FindException
     */
    public List<Leistung4Dn> findLeistungen() throws FindException;

    /**
     * Speichert das angegebene Mapping-Objekt (Leistungsbuendel-2-Leistung = Leistungsbuendeldefinition).
     *
     * @param lb2Leistung
     * @throws StoreException
     */
    public void saveLb2Leistung(Lb2Leistung lb2Leistung) throws StoreException;

    /**
     * Ermittelt alle Leistungsparameter.
     *
     * @return Liste mit allen LÖeistungsparametern
     */
    public List<LeistungParameter> findAllParameter() throws FindException;

    /**
     * Sucht alle Leistugsparameter zu einer bestimmten Leistung.
     *
     * @return Liste mit allen Leistungsparametern
     * @throws FindException
     */
    public List<Leistung2Parameter> findLeistung2Parameter(Long lId) throws FindException;

    /**
     * Erstellt Zuordnungen der Parameter-IDs zur angegebenen Leistung.
     *
     * @param leistungId
     * @param parameterIds
     * @throws StoreException
     */
    public void saveLeistung2Parameter(Long leistungId, Collection<Long> parameterIds) throws StoreException;

    /**
     * Speichert den angegebenen Leistungsparameter.
     *
     * @throws StoreException
     */
    public void saveLeistungParameter(LeistungParameter leistungParameter) throws StoreException;

    /**
     * Speichert neu angelegte Rufnummernleistung
     *
     * @param leistung4Dn
     * @throws StoreException
     */
    public void saveDNLeistung(Leistung4Dn leistung4Dn) throws StoreException;

    /**
     * Findet alle Rufnummern-Lesitungen die einem bestimmten Leistungsbuendel zugeordnet sind.
     *
     * @param lbId die Leistungsbuendel-ID
     * @return Liste mit den gefundenen Leistungen Leistung4Dn
     * @throws FindException
     */
    public List<Leistung4Dn> findDNLeistungen4Buendel(Long lbId) throws FindException;

    /**
     * Sucht nach den Leistungsbuendel-Leistungszuordnungen zu einer bestimmten Billing-Leistung. <br> Ueber den
     * Parameter <code>onlyDefault</code> kann bestimmt werden, ob nur nach Default-Leistungen fuer das Produkt gesucht
     * wird.
     *
     * @param leistungNoOrig (original) ID der Produkt-Leistung vom Billing-System
     * @param onlyDefault
     * @return Liste mit Objekten vom Typ Lb2Leistung.
     * @throws FindException
     */
    public List<Lb2Leistung> findLeistungsbuendelKonfig(Long lbId, boolean onlyDefault) throws FindException;

    /**
     * Findet die der Leistung zugeordneten Parameter in Klartext nach <code>leistungId</code>
     *
     * @param leistungId
     * @return Liste LeistungParameter
     * @throws FindException
     */
    public List<LeistungParameter> findSignedParameter2Leistung(Long leistungId) throws FindException;

    /**
     * Speichert die Leistungen zu einer Rufnummer Es wird anhand von <code>defaultLeistungen</code> überprüft ob alle
     * Standardleistungen zu einem bestimmten Leistungsbündel angelegt werden sollen.
     *
     * @param rufnummer          Rufnummer, zu der die Leistung gespeichert werden soll
     * @param leistungId         (optional) ID der zuzuordnenden Leistung
     * @param parameterText      (optional) Parameter fuer die Leistung
     * @param defaultLeistungen  Flag, ob alle Default-Leistungen (true) des Produkts zugeordnet werden sollen
     * @param leistungsbuendelId ID des zutreffenden Leistungsbuendels
     * @param realDate           Realisierungsdatum fuer die Leistungen
     * @param leistungParamId    (optional) ID des Leistungsparameters
     * @param sessionId          Session-ID des aktuellen Users
     * @throws StoreException
     */
    public void saveLeistung2DN(Rufnummer rufnummer, Long leistungId, String parameterText,
            boolean defaultLeistungen, Long leistungsbuendelId, Date realDate,
            Long leistungParamId, Long sessionId) throws StoreException;

    /**
     * Findet alle erstellten Leistung zu Rufnummer nach <code>example</code>
     *
     * @param example Objekt des Typs Leistung2DN
     * @return List Leistung2DN
     * @throws FindException
     */
    public List<Leistung2DN> findLeistung2DnByExample(Leistung2DN example) throws FindException;

    /**
     * Sucht nach allen Rufnummernleistungen, die den Rufnummern eines bestimmten Auftrags zugeordnet sind.
     *
     * @param auftragId Auftrags-ID
     * @return Liste mit Objekten des Typs <code>Leistung2DN</code>.
     * @throws FindException
     */
    public List<Leistung2DN> findDNLeistungen4Auftrag(Long auftragId) throws FindException;

    /**
     * Ermittelt zu einem Billing-Auftrag zugeordnete Rufnummernleistungen.
     *
     * @param dnNo         ID der Rufnummer
     * @param dnServiceIDs Liste mit den zu beruecksichtigenden Rufnummernleistungen
     * @param validDate    Datum, zu dem die Rufnummernleistung aktiv sein soll
     * @return Liste mit den zugeordneten Rufnummernleistungen
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<Leistung2DN> findLeistung2DN(Long dnNo, List<Long> dnServiceIDs, Date validDate) throws FindException;

    /**
     * Ermittelt alle Rufnummern-Leistungen, die noch nicht provisioniert sind (Einrichtung oder Kuendigung) und ein
     * geplantes Aenderungsdatum = provDate besitzen.
     *
     * @param provDate Pruef-Datum
     * @return Liste mit den noch nicht provisionierten Rufnummern-Leistungen
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<Leistung2DN> findUnProvisionedDNServices(Date provDate) throws FindException;

    /**
     * Findet eine erstellte Leistung zu Rufnummer nach <code>Id</code> der Leistung
     *
     * @param l2DnId
     * @return Leistung2DN
     * @throws FindException
     */
    public Leistung2DN findLeistung2DnById(Long l2DnId) throws FindException;

    /**
     * Ermittelt alle Rufnummernleistungen, die ueber eine bestimmte CPS-Transaction provisioniert (eingerichtet /
     * gekuendigt) wurden.
     *
     * @param cpsTxId ID der CPS-Transaction
     * @return Liste mit den Rufnummernleistungen, die durch die angegebene CPS-Tx provisioniert wurden
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    public List<Leistung2DN> findLeistung2DN4CPSTx(Long cpsTxId) throws FindException;

    /**
     * Speichert eine Rufnummernleistung.
     *
     * @param leistung Objekt vom Typ Leistung2DN
     * @throws StoreException
     */
    public void saveLeistung2DN(Leistung2DN leistung) throws StoreException;

    /**
     * Ermittelt die fuer den Auftrag (bzw. das Produkt) notwendigen Default Rufnummernleistungen und fuegt diese der
     * angegebenen Rufnummer hinzu. <br> ACHTUNG: die Zuordnung der Default DN-Services erfolgt nur dann, wenn zu der
     * Kombination Rufnummer/Rufnummernbuendel (fuer den aktuellen Auftrag bzw. Produkt) noch keine Rufnummernleistung
     * vorhanden ist! Ausserdem prueft die Methode, ob evtl. Default-Leistungen zugeordnet sind, diese aber keinen
     * Realisierungstermin besitzen. Ist dies der Fall, wird als Realisierungstermin (Vorgabe AM) das angegebene
     * REalisierungsdatum gesetzt.
     *
     * @param rufnummer
     * @param auftragId
     * @param realDate
     * @param sessionId
     * @throws StoreException
     */
    public void attachDefaultLeistungen2DN(Rufnummer rufnummer, Long auftragId, Date realDate, Long sessionId) throws StoreException;

    /**
     * Ueberprueft, ob fuer eine Rufnummer bereits Leistungen angelegt sind
     *
     * @return true, wenn der Rufnummer Leistungen zugeordnet sind.
     * @throws FindException
     */
    public Boolean hasLeistung(Rufnummer rufnummer) throws FindException;

    /**
     * Findet alle Leistungszuordnungen zu einem Leistungsbuendel
     *
     * @return List vom Typ Lb2Leistung
     */
    public List<LeistungInLeistungsbuendelView> findAllLb2Leistung(Long lbId) throws FindException;

    /**
     * Ueberschreibt die Datumsfelder gueltig_bid und verwenden_bis in der Tabelle LB2Leistung
     *
     * @param vBis
     * @throws StoreException
     */
    public void updateLb2Leistung(Date vBis, Long lb2LId) throws StoreException;

    /**
     * Speichert ein Leistungsbuendel
     *
     * @param lb
     * @throws StoreException
     */
    public void saveLeistungsbuendel(Leistungsbuendel lb) throws StoreException;

    /**
     * Liefert alle DN_NOs aus der Tabelle t_leistung_dn gruppiert
     *
     * @return Liste mit den Gruppierten DN_NOs
     * @throws FindException
     */
    public List<Long> groupedDnNos() throws FindException;

    /**
     * @param extProdID
     * @return
     */
    public Leistung4Dn findDNLeistungByExternLeistungNo(Long extProdID) throws FindException;

    /**
     * Ermittelt die in Hurrican fehlenden Rufnummernleistungen zu dem Auftrag.
     *
     * @throws FindException falls Auftrag und/oder Leistungen nicht gefunden werden
     */
    public List<Leistung4Dn> findMissingDnLeistungen(Long auftragId) throws FindException;


    /**
     * Setzt das Kuendigungsdatum fuer alle Zuordnungen einer Rufnummernleistung zu den angegeben Rufnummern und
     * speichert diese
     *
     * @param leistungId   ID der Rufnummerleistung die gekuendigt wird
     * @param kuendigungAm Datum zu dem die Leistung gekuendigt wird
     * @param username     Name des Users der die Leistung kuendigt
     * @param rufnummern   Liste der Rufnummern fuer die die Rufnummernleistung gekuendigt werden
     * @throws StoreException technischer Fehler
     */
    public List<Leistung2DN> kuendigeLeistung4Rufnummern(Long leistungId, Date kuendigungAm, String username,
            List<Rufnummer> rufnummern) throws StoreException;

    /**
     * Ermittelt die Leistungen4Dn aus der Liste <code>leistungen</code> die einen Parameter brauchen.
     *
     * @param leistungen
     * @return
     */
    public List<Leistung4Dn> findDNLeistungenWithParameter(List<Leistung4Dn> leistungen) throws FindException;

    /**
     * Ermittelt die Leistungen4Dn aus der Liste <code>leistungen</code> die keinen Parameter brauchen.
     *
     * @param leistungen
     * @return
     */
    public List<Leistung4Dn> findDNLeistungenWithoutParameter(List<Leistung4Dn> leistungen) throws FindException;

    /**
     * Kopiert Leistungen von der source nach target
     *  @param source    die Source-DN, von der die Leisungen kopiert werden
     * @param target    Die Ziel-DN, auf die die Leistungen kopiert werden
     * @param sessionId Die Web-Session-Id
     */
    void copyDnLeistung(Rufnummer source, Rufnummer target, Long sessionId) throws FindException, StoreException;
}


