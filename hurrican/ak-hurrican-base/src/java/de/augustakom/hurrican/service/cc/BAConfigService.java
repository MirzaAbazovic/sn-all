/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.12.2006 15:37:08
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.BAVerlaufAG2Produkt;
import de.augustakom.hurrican.model.cc.BAVerlaufAbtConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufAenderungGruppe;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufZusatz;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.view.VerlaufAbteilungView;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service zur Konfiguration von Bauauftraegen.
 *
 *
 */
public interface BAConfigService extends ICCService {

    /**
     * Sucht nach einem bestimmten Bauauftrags-Anlass.
     *
     * @param anlassId ID des gesuchten Anlasses.
     * @return Objekt vom Typ <code>BAVerlaufAnlass</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public BAVerlaufAnlass findBAVerlaufAnlass(Long anlassId) throws FindException;

    /**
     * Such einen BA-Verlauf-Anlass anhand dessen Namen.
     *
     * @param name Name des Anlasses
     * @return Objekt vom Typ <code>BAVerlaufAnlass</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    BAVerlaufAnlass findBAVerlaufAnlass(String name) throws FindException;

    /**
     * Sucht nach allen Bauauftrags-Anlaessen.
     *
     * @param onlyAct         (optional) Flag, ob alle (null), nur aktive (true) oder inaktive (false) Verlaufs-Anlaesse
     *                        ermittelt werden sollen.
     * @param onlyAuftragsart (optional) Flag, nach welchen Anlaessen gefiltert werden soll: <ul> <li>null = alle
     *                        Anlaesse <li>TRUE = nur Anlaesse, die eine Auftragsart (z.B. Neuschaltung) darstellen
     *                        <li>FALSE = nur Anlaesse, die keine Auftragsart darstellen. </ul>
     * @return Liste mit Objekten des Typs <code>BAVerlaufAnlass</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<BAVerlaufAnlass> findBAVerlaufAnlaesse(Boolean onlyAct, Boolean onlyAuftragsart) throws FindException;

    /**
     * Sucht nach einer bestimmten Verlaufs-Konfiguration ueber den Anlass und das Produkt. <br>
     *
     * @param anlass         Bauauftrags-Anlass
     * @param prodId         betroffenes Produkt
     * @param switch2Default Flag, ob die Default-Konfiguration (ohne Produkt-ID) ermittelt werden soll, falls mit der
     *                       angegebenen Kombinationa aus Anlass und Produkt nichts gefunden wird.
     * @return Objekt vom Typ <code>BAVerlaufConfig</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public BAVerlaufConfig findBAVerlaufConfig(Long anlass, Long prodId, boolean switch2Default)
            throws FindException;

    /**
     * Sucht nach den Verlaufs-Konfigurationen die dem Anlass und/oder Produkt zugeordnet sind.
     *
     * @param anlass (optional) Anlass-ID
     * @param prodId (optional) Produkt-ID
     * @return Liste mit Objekten des Typs <code>BAVerlaufConfig</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<BAVerlaufConfig> findBAVerlaufConfigs(Long anlass, Long prodId) throws FindException;

    /**
     * Speichert das angegebene Modell ab. <br> Sollte es bereits persistent sein, wird eine Historisierung erzeugt und
     * das neue Modell zurueck geliefert.
     *
     * @param toSave
     * @param sessionId
     * @throws StoreException
     *
     */
    public BAVerlaufConfig saveBAVerlaufConfig(BAVerlaufConfig toSave, Long sessionId) throws StoreException;

    /**
     * 'Loescht' die angegebene Bauauftragskonfiguration. <br> Das Modell wird nicht persistent geloescht, sondern nur
     * historisiert.
     *
     * @param toDelete
     * @throws DeleteException
     *
     */
    public void deleteBAVerlaufConfig(BAVerlaufConfig toDelete, Long sessionId) throws DeleteException;

    /**
     * Sucht nach allen definierten Abteilungskombinationen. <br>
     *
     * @return Liste mit Objekten des Typs <code>BAVerlaufAbtConfig</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<BAVerlaufAbtConfig> findBAVerlaufAbtConfigs() throws FindException;

    /**
     * Ermittelt alle Abteilung-IDs, die einer bestimmten Verlaufs-Konfiguration zugeordnet sind.
     *
     * @param abtConfigId ID der Abteilungs-Konfiguration, deren zugeordnete Abteilungen ermittelt werden sollen.
     * @return Liste mit den zugehoerigen Abteilungs-IDs.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<Long> findAbteilungen4BAVerlaufConfig(Long abtConfigId) throws FindException;

    /**
     * Ermittelt alle Views mit Abteilung- und Niederlassungs-IDs, die einer bestimmten Verlaufs-Konfiguration
     * zugeordnet sind.
     *
     * @param abtConfigId ID der Abteilungs-Konfiguration, deren zugeordnete Abteilungen ermittelt werden sollen.
     * @param nlId        Id der Niederlassung
     * @return Liste mit Views der zugehoerigen Abteilungs- und Niederlassungs-Id
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<VerlaufAbteilungView> findAbteilungView4BAVerlaufConfig(Long abtConfigId, Long nlId) throws FindException;

    /**
     * Ermittelt alle Verlaufs-Zusaetze fuer eine bestimmte Verlaufs-Konfiguration.
     *
     * @param baVerlConfigId ID der BAVerlaufConfig, deren Zusaetze ermittelt werden sollen.
     * @param hvtGruppeId       (optional) ID der HVT-Gruppe
     * @param standortTypRefId  (optional) ID des StandortTyps (Reference)
     * @return Liste mit den Verlaufs-Zusaetzen zu einer Konfiguration.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<BAVerlaufZusatz> findBAVerlaufZusaetze4BAVerlaufConfig(Long baVerlConfigId, Long hvtGruppeId,
            Long standortTypRefId) throws FindException;

    /**
     * Ermittelt alle verfuegbaren Verlaufs-Gruppen.
     *
     * @return Liste mit Objekten des Typs <code>BAVerlaufAenderungGruppe</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<BAVerlaufAenderungGruppe> findBAVerlaufAenderungGruppen() throws FindException;

    /**
     * Sucht nach allen Bauauftragsanlaessen, die fuer ein bestimmtes Produkt moeglich sind.
     *
     * @param produktId       ID des Produkts
     * @param onlyAuftragsart Angabe ob nach Auftragsarten (TRUE), Aenderungsanlaessen (FALSE) oder allen Anlaessen
     *                        (null) gesucht werden soll.
     * @return Liste mit Objekten des Typs <code>BAVerlaufAenderung</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     *
     */
    public List<BAVerlaufAnlass> findPossibleBAAnlaesse4Produkt(Long produktId, Boolean onlyAuftragsart) throws FindException;

    /**
     * Speichert einen Zusatz zu einer Bauauftragskonfiuguration. Es wird eine Historisierung erzeugt und das neue
     * Objekt zurueck geliefert.
     *
     * @param toSave    zu speicherndes Objekt.
     * @param sessionId
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public BAVerlaufZusatz saveBAVerlaufZusatz(BAVerlaufZusatz toSave, Long sessionId) throws StoreException;

    /**
     * Loescht den Bauauftrags-Zusatz. <br> Das Objekt wird nicht persisitent geloescht, sondern nur historisiert.
     *
     * @param toDelete
     * @param sessionId
     * @throws DeleteException
     *
     */
    public void deleteBAVerlaufZusatz(BAVerlaufZusatz toDelete, Long sessionId) throws DeleteException;

    /**
     * Sucht nach allen Mappings zwischen Produkt und BA-Verlauf Aenderungsgruppen fuer ein best. Produkt.
     *
     * @param produktId ID des Produkts
     * @return Liste mit Objekten vom Typ <code>BAVerlaufAG2Produkt</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<BAVerlaufAG2Produkt> findBAVAG4Produkt(Long produktId) throws FindException;

    /**
     * Ordnet dem Produkt mit der Id <code>produktId</code> die Aenderungsgruppen <code>bavagIds</code> zu.
     *
     * @param produktId Produkt-ID
     * @param bavagIds  Collection mit den IDs (Long) der BAVerlaufAenderungGruppen, die dem Produkt zugeordnet werden
     *                  sollen.
     * @throws StoreException wenn beim Speichern ein Fehler auftritt.
     */
    public void saveBAVAG4Produkt(Long produktId, Collection<Long> bavagIds) throws StoreException;

    /**
     * Funktion liefert die verantwortliche Niederlassung für eine Abteilung innerhalb der BA-Verteilung
     *
     * @param abtId ID der Abteilung
     * @param nlId  Id Niederlassung des Auftrags
     * @return Niederlassung, deren Abteilung für die Realisierung verantwortlich ist
     * @throws FindException
     *
     */
    public Niederlassung findNL4Verteilung(Long abtId, Long nlId) throws FindException;

}


