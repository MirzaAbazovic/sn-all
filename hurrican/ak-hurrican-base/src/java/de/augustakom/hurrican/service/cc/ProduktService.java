/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 10:51:38
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Produkt;
import de.augustakom.hurrican.model.cc.Produkt2Schnittstelle;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationType;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.shared.view.Billing2HurricanProdMapping;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition fuer die Verwaltung von Produkten.
 *
 *
 */
public interface ProduktService extends ICCService {

    /**
     * Sucht nach allen ProduktGruppen.
     *
     * @return Liste mit Objekten des Typs <code>ProduktGruppe</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<ProduktGruppe> findProduktGruppen() throws FindException;

    /**
     * Sucht nach allen ProduktGruppen, deren Produkte in Hurrican neu angelegt werden koennen. <br> Dies ist dann der
     * Fall, wenn bei einem zugehoerigen Produkt das Flag <code>auftragserstellung</code> auf <code>true</code> gesetzt
     * ist.
     *
     * @return Liste mit Objekten des Typs <code>ProduktGruppe</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<ProduktGruppe> findProduktGruppen4Hurrican() throws FindException;

    /**
     * Sucht nach einer ProduktGruppe ueber die ID.
     *
     * @param produktGruppeId ID der gesuchten Produkt-Gruppe.
     * @return Instanz von <code>ProduktGruppe</code> oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    ProduktGruppe findProduktGruppe(Long produktGruppeId) throws FindException;

    /**
     * Sucht nach allen Produkten (sortiert nach der Produkt-ID).
     *
     * @param onlyActual Flag, ob nach allen (false) oder nur nach aktuellen/gueltigen (true) Produkten gesucht werden
     *                   soll.
     * @return Liste mit Objekten des Typs <code>Produkt</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Produkt> findProdukte(boolean onlyActual) throws FindException;

    /**
     * Sucht nach einem Produkt ueber dessen ID.
     *
     * @param produktId ID des gesuchten Produkts.
     * @return Instanz von Produkt oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Produkt findProdukt(Long produktId) throws FindException;

    /**
     * Sucht nach dem Produkt, das einem best. Auftrag zugeordnet ist.
     *
     * @param auftragId ID des Auftrags dessen Produkt gesucht wird.
     * @return Instanz von Produkt oder <code>null</code>.
     * @throws FindException wenn bei der Suche ein Fehler auftritt.
     */
    Produkt findProdukt4Auftrag(Long auftragId) throws FindException;

    /**
     * Sucht nach der Produkt-ID des Produkts, das einem best. Auftrag zugeordnet ist.
     *
     * @param auftragId ID des Auftrags, dessen Produkt-ID gesucht wird
     * @return ID des zugeordneten Produkts
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Long findProduktId4Auftrag(Long auftragId) throws FindException;

    /**
     * Erzeugt den Produkt-Namen fuer den Auftrag mit der ID <code>auftragId</code>. Der Produkt-Name kann ueber
     * verschiedene Parameter erzeugt werden (definiert unter Produkt.productNamePattern). <br>
     *
     * @param auftragId Hurrican Auftrags-ID
     * @return erzeugter Produkt-Name
     * @throws FindException wenn das Produkt nicht ermittelt werden konnte
     *
     */
    String generateProduktName4Auftrag(Long auftragId) throws FindException;

    /**
     * Erzeugt den Produkt-Namen fuer den Auftrag mit der ID <code>auftragId</code>. Der Produkt-Name kann ueber
     * verschiedene Parameter erzeugt werden (definiert unter Produkt.productNamePattern). <br>
     *
     * @param auftragId Auftrags-ID
     * @param produkt Produkt des Auftrags
     * @return erzeugter Produkt-Name
     * @throws FindException
     *
     */
    String generateProduktName4Auftrag(Long auftragId, Produkt produkt) throws FindException;

    /**
     * Erzeugt den Produkt-Namen fuer das Produkt anhand der gegebenen technischen Leistungen
     *
     * @param produkt        Das Produkt
     * @param techLeistungen Die technischen Leistungen
     * @return erzeugter Produkt-Name
     */
    String generateProduktName(Produkt produkt, List<TechLeistung> techLeistungen);

    /**
     * Sucht nach der ProduktGruppe, zu der ein best. Auftrag gehoert.
     *
     * @param auftragId ID des Auftrags
     * @return Instanz von ProduktGruppe oder <code>null</code>
     * @throws FindException wenn bei der Suche ein Fehler auftritt.
     */
    ProduktGruppe findPG4Auftrag(Long auftragId) throws FindException;

    /**
     * Sucht nach allen Produkten einer ProduktGruppe, die in Hurrican neu angelegt werden koennen. <br> Produkte
     * koennen in Hurrican neu angelegt werden, wenn das Flag <code>auftragserstellung</code> auf <code>true</code>
     * gesetzt ist.
     *
     * @param produktGruppeId ID der ProduktGruppe
     * @return Liste mit Objekten des Typs <code>Produkt</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Produkt> findProdukte4PGAndHurrican(Long produktGruppeId) throws FindException;

    /**
     * Sucht nach allen Produkten zu der angegebenen Produkt-Gruppe.
     *
     * @param produktGruppenIds IDs der Produkt-Gruppen
     * @return Liste mit Objekten des Typs <code>Produkt</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Produkt> findProdukte4PGs(Long... produktGruppenIds) throws FindException;

    /**
     * Ermittelt die moeglichen Child-Produkte eines best. Parent-Produkts.
     *
     * @param parentProdId ID des Parent-Produkts, dessen Child-Produkte ermittelt werden sollen.
     * @return Liste mit Objekten des Typs <code>Produkt</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Produkt> findChildProdukte(Long parentProdId) throws FindException;

    /**
     * Speichert das Produkt ab.
     *
     * @param toSave zu speicherndes Produkt.
     * @throws StoreException      wenn beim Speichern ein Fehler auftritt.
     * @throws ValidationException wenn das zu speichernde Produkt ungueltige Daten besitzt.
     */
    void saveProdukt(Produkt toSave) throws StoreException, ValidationException;

    /**
     * Speichert die Produktgruppe ab.
     */
    void saveProduktGruppe(ProduktGruppe toSave) throws StoreException;

    /**
     * Gibt eine Liste aller definierten (Hardware-)Schnittstellen zurueck.
     *
     * @return Liste mit Objekten des Typs <code>Schnittstelle</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Schnittstelle> findSchnittstellen() throws FindException;

    /**
     * Sucht nach einer best. Schnittstelle.
     *
     * @param id ID der gesuchten Schnittstelle.
     * @return Schnittstelle oder <code>null</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    Schnittstelle findSchnittstelle(Long id) throws FindException;

    /**
     * Sucht nach allen Mappings zwischen Produkt und Schnittstelle.
     *
     * @param produktId ID des Produkts
     * @return Liste mit Objekten des Typs <code>Produkt2Schnittstelle</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Produkt2Schnittstelle> findSchnittstellenMappings4Produkt(Long produktId) throws FindException;

    /**
     * Sucht nach allen (Hardware-)Schnittstellen, die einem best. Produkt zugeordnet sind.
     *
     * @param produktId ID des Produkts
     * @return Liste mit Objekten des Typs <code>Schnittstelle</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Schnittstelle> findSchnittstellen4Produkt(Long produktId) throws FindException;

    /**
     * Ordnet dem Produkt mit der ID <code>produktId</code> die Schnittstellen <code>schnittstellenIds</code> zu.
     *
     * @param produktId         ID des Produkts
     * @param schnittstellenIds Collection mit ID-Objekten (Long) der Schnittstellen, die dem Produkt zugeordnet werden
     *                          sollen.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveSchnittstellen4Produkt(Long produktId, Collection<Long> schnittstellenIds) throws StoreException;

    /**
     * Sucht nach allen verfuegbaren Leitungsarten.
     *
     * @return Liste mit Objekten des Typs <code>Leitungsart</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Leitungsart> findLeitungsarten() throws FindException;

    /**
     * Sucht nach einer Produkt2Produkt-Konfiguration fuer einen best. Physikaenderungstyp (z.B. Bandbreitenaenderung)
     * fuer die Produkte mit den IDs <code>prodIdSrc</code> und <code>prodIdDest</code>.
     *
     * @param physikaendTyp Typ der gewuenschten Physikaenderung (Konstante aus <code>PhysikaenderungsTyp</code>).
     * @param prodIdSrc     Produkt-ID des Ursprungs-Auftrags
     * @param prodIdDest    Produkt-ID des zukuenftigen Auftrags
     * @return Produkt2Produkt Konfiguration
     * @throws FindException wenn bei der Ermittlung ein Fehler auftritt.
     */
    Produkt2Produkt findProdukt2Produkt(Long physikaendTyp, Long prodIdSrc, Long prodIdDest)
            throws FindException;

    /**
     * Sucht nach allen Produkt2Produkt-Konfigurationen fuer ein best. Quell-Produkt. Die Sortierung der Ergebnisliste
     * erfolgt aufsteigend nach der ID des Ziel-Produkts.
     *
     * @param prodIdSrc ID des Basis-Produkts.
     * @return Liste mit Objekten des Typs <code>Produkt2Produkt</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Produkt2Produkt> findProdukt2Produkte(Long prodIdSrc) throws FindException;

    /**
     * Sucht nach allen Produkt2Produkt-Konfigurationen fuer ein best. Ziel-Produkt. Die Sortierung der Ergebnisliste
     * erfolgt aufsteigend nach der ID des Ziel-Produkts.
     *
     * @param prodIdDest ID des Basis-Produkts.
     * @return Liste mit Objekten des Typs <code>Produkt2Produkt</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    List<Produkt2Produkt> findProdukt2ProdukteByDest(Long prodIdDest) throws FindException;

    /**
     * Sucht nach allen Produkt2Produkt2-Konfigurationen fuer das angegebene Quell- und Ziel-Produkt.
     *
     * @param prodIdSrc  ID des Quell-Produkts
     * @param prodIdDest ID des Ziel-Produkts
     * @return Liste mit den moeglichen Produkt2Produkt-Konfigurationen
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<Produkt2Produkt> findProdukt2Produkt(Long prodIdSrc, Long prodIdDest) throws FindException;

    /**
     * Speichert die angegebene Produkt2Produkt-Konfiguration.
     *
     * @param toSave zu speicherndes Objekt
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    void saveProdukt2Produkt(Produkt2Produkt toSave) throws StoreException;

    /**
     * Loescht die Produkt2Produkt-Konfiguration mit der ID <code>p2pId</code>.
     *
     * @param p2pId ID der zu loeschenden Produkt2Produkt-Konfiguration
     * @throws DeleteException wenn beim Loeschen ein Fehler auftritt.
     */
    void deleteProdukt2Produkt(Long p2pId) throws DeleteException;

    /**
     * Ermittelt alle konfigurierten Produkt-Mapping-Eintraege.
     *
     * @return Liste mit Objekten des Typs <code>ProduktMapping</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<ProduktMapping> findProduktMappings() throws FindException;

    /**
     * Ermittelt alle konfigurierten Produkt-Mappings fuer die angegebenen Parameter.
     *
     * @param prodId          Hurrican Produkt-ID
     * @param mappingPartType gesuchter Anteil des Produkt-Mappings (moegliche Werte als Konstante im Modell
     *                        ProduktMapping definiert)
     * @return Liste mit den ermittelten Produkt-Mappings.
     * @throws FindException
     *
     */
    List<ProduktMapping> findProduktMappings(Long prodId, String mappingPartType) throws FindException;

    /**
     * Ermittelt die Hurrican Produkt-IDs der angegebenen Mapping-Gruppen; sortiert nach der Prioritaet aus dem
     * Mapping.
     *
     * @param mappingGroups
     * @return
     */
    List<Long> findProduktIdsSortedByPrio(List<Long> mappingGroups);

    /**
     * Ermittelt die moeglichen EXT_PROD__NOs zu einem Hurrican-Produkt. <br>
     *
     * @param prodId          Hurrican Produkt-ID
     * @param mappingPartType gesuchter Anteil des Produkt-Mappings (moegliche Werte als Konstante im Modell
     *                        ProduktMapping definiert)
     * @return Liste mit den moeglichen EXT_PROD__NOs
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<Long> findExtProdNos(Long prodId, String mappingPartType) throws FindException;

    /**
     * Ermittelt aus den uebergebenen Leistungs-Views die notwendigen Hurrican-Produkte.
     *
     * @param views
     * @return Liste mit Objekten des Typs <code>Billing2HurricanProdMapping</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    List<Billing2HurricanProdMapping> doProductMapping(List<BAuftragLeistungView> views) throws FindException;

    /**
     * Funktion fuehrt das Produkmapping fuer einen bestimmten Taifun-Auftrag durch
     *
     * @param auftragNo Auftragsnummer
     * @return Produkt-Objekt
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    Produkt doProduktMapping4AuftragNo(Long auftragNo) throws FindException;

    /**
     * Funktion ermittelt, ob es sich um ein Produkt mit Vier-Draht Physik handelt.
     *
     * @param prodId Produkt-ID
     * @return True, falls Produkt == Vier-Draht
     * @throws FindException
     */
    Boolean isVierDrahtProdukt(Long prodId) throws FindException;

    /**
     * Speichert alle Objekte innerhalb der angegebenen Liste. Die Priorisierung der einzelnen {@link
     * Produkt2TechLocationType} Objekte wird ueber ihre Position in der Liste vorgenommen!
     *
     * @param listToSave erwartet alle! Einträge zu dem Produkt
     * @param sessionId
     * @throws StoreException
     */
    void saveProdukt2TechLocationTypes(Long produktId, List<Produkt2TechLocationType> listToSave, Long sessionId)
            throws StoreException;

    /**
     * Ermittelt alle {@link Produkt2TechLocationType} Eintraege fuer das angegebene Produkt. Die Sortierung erfolgt
     * aufsteigend ueber das 'priority' Feld.
     *
     * @param prodId Produkt-ID
     * @return Liste mit Objekten des Typs {@link Produkt2TechLocationType}
     * @throws FindException
     */
    List<Produkt2TechLocationType> findProdukt2TechLocationTypes(Long prodId) throws FindException;

    /**
     * Sucht nach allen Produkten mit den angegebenen technischen Leistungen.
     *
     * @param techLeistungTypes Liste mit TechLeistung-Typen.
     * @return Liste von Produkten oder leere Liste
     */
    List<Produkt> findProductsByTechLeistungType(String... techLeistungTypes) throws FindException;

}
