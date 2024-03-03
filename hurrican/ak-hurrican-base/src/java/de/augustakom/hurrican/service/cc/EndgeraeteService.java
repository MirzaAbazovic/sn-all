/*

 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.10.2004 15:03:33
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.service.exceptions.ServiceCommandException;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.EG;
import de.augustakom.hurrican.model.cc.EG2Auftrag;
import de.augustakom.hurrican.model.cc.EGConfig;
import de.augustakom.hurrican.model.cc.EGType;
import de.augustakom.hurrican.model.cc.EndgeraetAcl;
import de.augustakom.hurrican.model.cc.EndgeraetPort;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt2EG;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Schicht2Protokoll;
import de.augustakom.hurrican.model.cc.Zugang;
import de.augustakom.hurrican.model.cc.view.EG2AuftragView;
import de.augustakom.hurrican.model.internet.IntEndgeraet;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;

/**
 * Service-Interface fuer die Verwaltung von Endgeraeten.
 *
 *
 */
public interface EndgeraeteService extends ICCService {

    /**
     * Sucht ein Endgeraet nach der vorgegebenen ID
     */
    EG findEgById(Long egId) throws FindException;

    /**
     * Sucht nach allen Zugangs-Definitionen, die einem best. CC-Auftrag zugeordnet sind. <br> Die Zugangs-Definitionen
     * enthalten Daten, um auf ein Endgeraet (z.B. Router) des Kunden zuzugreifen.
     *
     * @param ccAuftragId ID des CC-Auftrags
     * @return Liste mit Objekten des Typs <code>Zugang</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Zugang> findZugaenge4Auftrag(Long ccAuftragId) throws FindException;

    /**
     * Speichert das angegebene Objekt.
     *
     * @param toSave zu speicherndes Objekt.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveZugang(Zugang toSave) throws StoreException;

    /**
     * Ermittelt die aktuelle Endgeraete-Konfiguration zu dem Endgeraete-2-Auftrag Mapping mit der ID
     * <code>eg2AuftragId</code>. <br>Transactional Qualifier: PROPAGATION_SUPPORTS
     */
    EGConfig findEGConfig(Long eg2AuftragId) throws FindException;

    /**
     * Ermittelt das Schicht-2-Protokoll aus dem EQ-In-Port der ersten Rangierung einer Endstelle.<br>
     * <ul>
     * <li>Falls dem Endgeraet eine Enstelle zugeordnet ist -> verwende EQ-In-Port dieser Endstelle.</li>
     * <li>Falls dem Endgeraet keine Endstelle zugeornet ist - verwende EQ-In-Port der Endstelle B.</li>
     * <ul/>
     */
    Schicht2Protokoll getSchicht2Protokoll4Auftrag(EG2Auftrag eg2Auftrag) throws FindException;

    /**
     * siehe {@link EndgeraeteService#getSchicht2Protokoll4Auftrag(EG2Auftrag)}
     */
    Schicht2Protokoll getSchicht2Protokoll4Auftrag(Long auftragId, Long endstelleId) throws FindException;

    /**
     * Aktualisiert das Schicht2Protokol der Endgeraete Konfigurationen, welche zu der uebergebenen Endstelle
     * gehoeren. Siehe hierzu auch {@link EndgeraeteService#getSchicht2Protokoll4Auftrag(EG2Auftrag)}.
     */
    void updateSchicht2Protokoll4Endstelle(Endstelle endstelle) throws StoreException;

    /**
     * Aktualisiert das Schicht2Protokol der Endgeraete Konfigurationen, welche zu den uebergebenen Rangierungen
     * gehoeren. Siehe hierzu auch {@link EndgeraeteService#getSchicht2Protokoll4Auftrag(EG2Auftrag)}.
     */
    void updateSchicht2Protokoll4Rangierungen(List<Rangierung> rangierungen) throws StoreException;

    List<EGConfig> findEgConfigs4Auftrag(Long auftragId) throws FindException;

    /**
     * Speichert die angegebene Endgeraete-Konfiguration. Die Port-Forwardings des EGConfig-Objekts werden
     * automatisch/kaskadierend gespeichert.
     *
     * @param toSave    zu speicherndes Objekt
     * @param sessionId Session-ID des aktuellen Users
     * @return das gespeicherte (evtl. neu historisierte) Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt
     */
    EGConfig saveEGConfig(EGConfig toSave, Long sessionId) throws StoreException, ValidationException;

    /**
     * Findet alle Endgeraete mit angegebener Leistungskennzeichnung, die dem Hurrican-Produkt 'prodId' zugeordnet
     * sind.
     *
     * @param extLeistungNoOrig Leistungskennzeichnung der gesuchten Endgeraete.
     * @param prodId            Hurrican Produkt-ID, auf die die Suche beschraenkt werden soll
     * @return Liste mit Objekten des Typs 'EG'
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<EG> findValidEG(Long extLeistungNoOrig, Long prodId) throws FindException;

    /**
     * Sucht nach allen Endgeraeten mit dem angegebenen Typ.
     *
     * @param typ Typ der gesuchten Endgeraete (Konstante aus EG)
     * @return Liste mit den passenden EGs
     * @throws FindException
     */
    List<EG> findEGs(Long typ) throws FindException;

    /**
     * Ermittelt die Endgeraet-Zuordnungen zu einem (CC-)Auftrag.
     *
     * @param ccAuftragId CC-Auftrags ID
     * @return Liste mit Objekten des Typs <code>EG2Auftrag</code>
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    List<EG2Auftrag> findEGs4Auftrag(Long ccAuftragId) throws FindException;

    /**
     * Ueberprueft, ob dem Auftrag ein Endgeraet mit Montageart 'AKom' zugeordnet ist.
     *
     * @param ccAuftragId CC-Auftrags ID
     * @return true, wenn dem Auftrag mindestens ein Endgeraet mit Montageart 'AKom' zugeordnet ist - sonst false.
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    boolean hasEGWithMontageMnet(Long ccAuftragId) throws FindException;

    /**
     * Ermittelt Views mit wichtigen Daten einer Endgeraete-Zuordnung zu einem (CC-)Auftrag.
     *
     * @param ccAuftragId CC-Auftrags ID
     * @return Liste mit Objekten des Typs <code>EG2AuftragView</code>
     */
    List<EG2AuftragView> findEG2AuftragViews(Long ccAuftragId) throws FindException;

    /**
     * Emittelt das Mapping eines Endgeraetes zu einem Auftrag anhand der EG2Auftrag.ID
     */
    EG2Auftrag findEG2AuftragById(Long id) throws FindException;

    /**
     * Ermittelt die Endgeraete, die fuer ein bestimmtes Produkt vorgesehen sind. <br>
     *
     * @param prodId      Produkt-ID
     * @param onlyDefault Flag, ob nur nach den Default-Endgeraeten gesucht werden soll.
     * @return Liste mit Objekten des Typs <code>EG</code>
     */
    List<EG> findEGs4Produkt(Long prodId, boolean onlyDefault) throws FindException;

    /**
     * Ermittelt Endgeraete, die dem Auftrag mit der angegebenen ID zugeordnet werden sollen. <br> Ablauf: <br> -
     * Standardendgeraete ueber Billing-Auftrag ermitteln (keine Kaufgeraete!) <br> - falls ueber Billing keine EGs
     * gefunden werden, die Default-EGs zum Produkt laden <br> <br> Handelt es sich bei einem ermittelten EG um eine
     * Paketdefinition, werden die Endgeraete des Pakets ermittelt und diese zurueck gegeben. <br> <br> Wichtig: es
     * werden keine Mengenangaben vom Billing beruecksichtigt! <br>
     *
     * @param auftragId ID des Hurrican-Auftrags, zu dem die Standardendgeraete ermittelt werden sollen
     * @return Liste mit den zuzuordnenden (Standard-)Endgeraeten
     */
    List<EG> findDefaultEGs4Order(Long auftragId) throws FindException;

    /**
     * Prueft, ob fuer das Endgeraet ein Check-Command existiert und fuehrt dieses ggf. aus. <br> Liefert das Command
     * ein negatives Ergebnis, wird eine Exception generiert.
     *
     * @param eg        Endgeraet, zu dem ein evtl. vorhandenes Check-Command ausgefuehrt werden soll
     * @param auftragId ID des Auftrags, zu dem das Endgeraet gehoert bzw. dem das Endgeraet zugeordnet werden soll.
     */
    void executeEGCheckCommand(EG eg, Long auftragId) throws ServiceCommandException;

    /**
     * Ermittelt die Zuordnungsdatensaetze zwischen Produkt und EG.
     */
    List<Produkt2EG> findProdukt2EGs(Long prodId) throws FindException;

    /**
     * Speichert die Produkt2EG-Liste. <br>
     */
    void assignEGs2Produkt(Long prodId, List<Produkt2EG> toAssign) throws StoreException;

    /**
     * Loescht die EG-Zuordnungen zu dem Produkt.
     */
    void deleteEGs2Produkt(Long prodId) throws DeleteException;

    /**
     * Speichert das Mapping des Endgeraetes zum Auftrag
     */
    void saveEG2Auftrag(EG2Auftrag eg2a, Long sessionId) throws StoreException, ValidationException;

    /**
     * Aktualisiert oder erzeugt eine Endgeraet-Zuordnung zu einem Auftrag. <br> Achtung: hier wird lediglich eine Liste
     * mit View-Objekten angegeben. Die Methode ermittelt daraus das evtl. schon vorhandene Mapping-Objekt und
     * aktualisiert dieses oder erzeugt ein komplett neues Mapping-Objekt.
     */
    void saveEGs2Auftrag(List<EG2AuftragView> views, Long sessionId) throws StoreException;

    /**
     * Aktualisiert oder erzeugt eine Endgeraet-Zuordnung zu einem Auftrag. <br> Achtung: hier wird lediglich ein
     * View-Objekt uebergeben. Die Methode ermittelt daraus das evtl. schon vorhandene Mapping-Objekt und aktualisiert
     * dieses oder erzeugt ein komplett neues Mapping-Objekt.
     *
     * @param view      View mit EG und Auftragsdaten
     * @param sessionId Id der Session um User zu identifizieren
     */
    EG2AuftragView saveEG2AuftragView(EG2AuftragView view, Long sessionId) throws StoreException;

    /**
     * Entfernt eine Endgeraete-Zuordnung von einem Auftrag.
     */
    void deleteEG2Auftrag(Long eg2AuftragId, Long sessionId) throws DeleteException;

    /**
     * Funktion liefert alle gepflegten Endgeraet Acls
     */
    List<EndgeraetAcl> findAllEndgeraetAcls() throws FindException;

    /**
     * Funktion liefer ein EndgeraetAcl Object mit dem Namen name
     */
    EndgeraetAcl findEndgeraetAclByName(String name) throws FindException;

    /**
     * Funktion speichert eine ACL fuer ein Endgeraet
     */
    void saveEndgeraetAcl(EndgeraetAcl endgeraetAcl) throws StoreException;

    /**
     * Loescht eine ACL fuer ein Endgeraet
     */
    void deleteEndgeraetAcl(EndgeraetAcl endgeraetAcl);

    /**
     * Sucht nach Endgeraeten in der Monline-Datenbank, die noch nicht in Hurrican sind;
     */
    List<IntEndgeraet> findIntEndgeraeteNotInHurrican(Long auftragId) throws FindException;

    /**
     * Uebernimmt das Monline-Endgeraet nach Hurrican.
     *
     * @param auftragId Auftrag, der das Endgeaet bekommen soll.
     * @param eg        Monline-Endgeraet das uebernommen werden soll
     * @param sessionId sessionId des aktuellen Users
     */
    void importEg(Long auftragId, Long billingOrderNo, IntEndgeraet eg, Long sessionId) throws StoreException;

    /**
     * Ermittelt Liste aller Modelle des übergebenen Herstellers
     */
    List<String> getDistinctListOfModelsByManufacturer(String manufacturer) throws FindException;

    /**
     * Sucht Endgeräte-Typen (jeder Hersteller genau einmal)
     */
    List<String> getDistinctListOfEGManufacturer() throws FindException;

    /**
     * Sucht Endgeräte-Typen (jedes Modell genau einmal)
     */
    List<String> getDistinctListOfEGModels() throws FindException;

    /**
     * Speichert einen Endgerätetyp
     */
    void saveEGType(EGType toSave, Long sessionId) throws StoreException;

    /**
     * Listet alle Endgeratetypen sortiert nach {@code id} auf
     */
    List<EGType> findAllEGTypes();

    /**
     * Sucht alle Endgeraete, die einem bestimmten Endgeraetetyp noch nicht zugeordnet sind
     */
    List<EG> findPossibleEGs4EGType(Long egTypeId) throws FindException;

    /**
     * Sucht Endgeraetetyp fuer gegebenen Hersteller und Modell
     */
    EGType findEGTypeByHerstellerAndModell(String hersteller, String modell) throws FindException;

    /**
     * Erstellt eine Map mit den konfigurierten (count) Endgereate Ports. Als Key fungiert die {@link EndgeraetPort}
     * .number.
     */
    Map<Integer, EndgeraetPort> findDefaultEndgeraetPorts4Count(Integer count) throws FindException;

    /**
     * Ermittelt die maximale Anzahl konfigurierbarer standard Ports
     */
    Integer getMaxDefaultEndgeraetPorts() throws FindException;

}
