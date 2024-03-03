/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:26:19
 */
package de.augustakom.hurrican.service.cc;

import java.util.*;

import de.augustakom.hurrican.model.cc.Abt2NL;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;


/**
 * Service-Definition, um Niederlassungen und Abteilungen zu verwalten.
 *
 *
 */
public interface NiederlassungService extends ICCService {

    /**
     * Sucht nach allen Niederlassungen.
     *
     * @return Liste mit Objekten des Typs <code>Niederlassung</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Niederlassung> findNiederlassungen() throws FindException;

    /**
     * Sucht nach einer best. Abteilung ueber deren ID.
     *
     * @param id ID der gesuchten Abteilung
     * @return Instanz von Abteilung oder <code>null</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public Abteilung findAbteilung(Long id) throws FindException;

    /**
     * Sucht nach allen Abteilungen.
     *
     * @return Liste mit Objekten des Typs <code>Abteilung</code>
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Abteilung> findAbteilungen() throws FindException;

    /**
     * Sucht nach den Abteilungen, die den IDs <code>abtIds</code> entsprechen.
     *
     * @param abtIds Liste mit den IDs der gesuchten Abteilungen.
     * @return Liste mit Objekten des Typs <code>Abteilung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Abteilung> findAbteilungen(Collection<Long> abtIds) throws FindException;

    /**
     * Sucht nach allen Abteilungen, die in den el. Verlauf für die Projektierung eingebunden sind. <br> Die Sortierung
     * der Abteilungen erfolgt ueber die ID (aufsteigend).
     *
     * @return Liste mit Objekten des Typs <code>Abteilung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Abteilung> findAbteilungen4Proj() throws FindException;

    /**
     * Sucht nach allen Abteilungen, die in den el. Verlauf für den Bauauftrag eingebunden sind. <br> Die Sortierung der
     * Abteilungen erfolgt ueber die ID (aufsteigend).
     *
     * @return Liste mit Objekten des Typs <code>Abteilung</code>.
     * @throws FindException wenn bei der Abfrage ein Fehler auftritt.
     */
    public List<Abteilung> findAbteilungen4Ba() throws FindException;

    /**
     * Ermittelt alle Abteilungen, die fuer die 'universelle' Bauauftrags-GUI konfiguriert sind.
     * @return
     * @throws FindException
     */
    public List<Abteilung> findAbteilungen4UniversalGui() throws FindException;

    /**
     * Funktion liefert die Niederlassung anhand des Kunden-Resellers
     *
     * @param kundeNo Kundennummer
     * @return Gesuchte Niederlassung
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public Niederlassung findNiederlassung4Kunde(Long kundeNo) throws FindException;

    /**
     * Funktion liefert die Niederlassung zu einem Auftrag
     *
     * @param auftragId Auftragsnummer
     * @return Gesuchte Niederlassung
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public Niederlassung findNiederlassung4Auftrag(Long auftragId) throws FindException;

    /**
     * Liefert ein Niederlassungsobjekt anhand der ID
     *
     * @param niederlassungId Id der gesuchten Niederlassung
     * @return Gesuchte Niederlassung
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public Niederlassung findNiederlassung(Long niederlassungId) throws FindException;

    /**
     * Niederlassung wird anhand des Benutzers ermittelt mit Hilfe der SessionId des jeweiligen Benutzers.
     *
     * @param sessionId SessionId des Benutzers
     * @return Niederlassungs-Objekt
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public Niederlassung findNiederlassung4User(Long sessionId) throws FindException;

    /**
     * Liefert alle Niederlassungen, die eine best. Abteilung besitzen
     *
     * @param abtId Id der Abteilung
     * @return Liste mit Niederlassungen
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public List<Niederlassung> findNL4Abteilung(Long abtId) throws FindException;

    /**
     * Findet ein Abt2Nl-Objekt anhand der Abteilungs- und Niederlassungs-Id
     *
     * @param abtId Id der Abteilung
     * @param nlId  Id der Niederlassung
     * @return Abt2Nl-Objekt
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public Abt2NL findAbt2NL(Long abtId, Long nlId) throws FindException;

    /**
     * Ordnet dem Auftrag eine Niederlassung nach folgendem Schema zu: 1. Falls Endstelle A+B erzeugt wird ->
     * Niederlassung Zentral 2. Falls nur Endstelle B -> Niederlassung anhand HVT 3. Falls keine Endstelle ->
     * Niederlassung ueber Reseller des Kunden
     *
     * @param auftragId Id des Hurrican- Auftrags
     * @throws StoreException Falls ein Fehler auftrat
     *
     */
    public void setNiederlassung4Auftrag(Long auftragId) throws StoreException;

    /**
     * Liefert eine Niederlassung anhand der Niederlassungs-Bezeichnung.
     *
     * @param name Name/Bezeichnung der Niederlassung
     * @return Gesuchte Niederlassung
     * @throws FindException Falls ein Fehler auftrat
     *
     */
    public Niederlassung findNiederlassungByName(String name) throws FindException;

}


