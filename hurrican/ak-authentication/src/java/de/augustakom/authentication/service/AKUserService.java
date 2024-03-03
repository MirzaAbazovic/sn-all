/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 10:27:41
 */
package de.augustakom.authentication.service;

import java.util.*;
import org.hibernate.criterion.MatchMode;

import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;

/**
 * Interface fuer einen UserService <br> Ueber den UserService koennen Informationen ueber einen Benutzer abgefragt
 * sowie Daten eines Users geaendert werden.
 *
 *
 */
public interface AKUserService extends IAuthenticationService {

    /**
     * Erzeugt oder aktualisiert den User-Eintrag.
     *
     * @param user zu speicherndes Objekt
     * @throws AKAuthenticationException wenn waehrend dem Speichern ein Fehler auftritt.
     */
    public void save(AKUser user) throws AKAuthenticationException;

    /**
     * Loescht den Benutzer mit der ID <code>userId</code>.
     *
     * @param userId ID des zu loeschenden Users.
     * @throws AKAuthenticationException wenn waehrend dem Loeschen ein Fehler auftritt.
     */
    public void delete(Long userId) throws AKAuthenticationException;

    /**
     * Sucht nach einem User ueber die Session-ID.
     *
     * @param sessionId ID der User-Session
     * @return Instanz von <code>AKUser</code> oder <code>null</code>
     * @throws AKAuthenticationException wenn bei der Abfrage ein Fehler auftritt.
     */
    public AKUser findUserBySessionId(Long sessionId) throws AKAuthenticationException;

    /**
     * Sucht nach allen Benutzern, die einer best. Abteilung zugeordnet sind.
     *
     * @param departmentId ID der Abteilung.
     * @return Liste von AKUser-Objekten.
     * @throws AKAuthenticationException
     */
    public List<AKUser> findByDepartment(Long departmentId) throws AKAuthenticationException;

    /**
     * Sucht nach allen Benutzern, die einem best. Team zugeordnet sind.
     */
    public List<AKUser> findByTeam(AKTeam team) throws AKAuthenticationException;

    /**
     * Sucht nach allen Benutzern, die einer bestimmten HURRICAN Abteilung zugeordnet sind.
     *
     * @param abteilungId
     * @return
     * @throws AKAuthenticationException
     */
    public List<AKUser> findByHurricanAbteilungId(Long hurricanAbteilungId) throws AKAuthenticationException;

    /**
     * Such nach Benutzer ueber den Namen.
     *
     * @param vorname  Vorname nach dem gesucht wird
     * @param nachname Nachname nach dem gesucht wird
     * @return Liste von AKUser-Objekten.
     * @throws AKAuthenticationException
     */
    public List<AKUser> findByName(String vorname, String nachname) throws AKAuthenticationException;

    /**
     * Sucht alle User, die als Projektleiter eingetragen werden duerfen.
     *
     * @return Liste von AKUser-Objekten.
     * @throws AKAuthenticationException
     */
    public List<AKUser> findAllProjektleiter() throws AKAuthenticationException;

    /**
     * Erstellt Mappings von User-Id->User-Name fuer alle Users.
     *
     * @return Liste mit Mappings.
     * @throws AKAuthenticationException
     */
    public Map<Long, String> findUserIdToNames() throws AKAuthenticationException;

    /**
     * Ermittelt alle User, die als Manager definiert sind.
     *
     * @return Liste von AKUser-Objekten.
     * @throws AKAuthenticationException
     */
    public List<AKUser> findManagers() throws AKAuthenticationException;

    /**
     * Sucht anhand des LoginNamens nach einem User
     *
     * @param loginName
     * @return
     * @throws AKAuthenticationException
     *
     */
    public AKUser findByLoginName(String loginName) throws AKAuthenticationException;

    /**
     * Suche nach User. Für String parameter wird mit Wildcard (siehe {@link MatchMode#ANYWHERE} gesucht.
     *
     * @param searchParams map mit name der property und suchwert
     * @return gefundene User sortiert nach loginName
     * @throws AKAuthenticationException
     */
    List<AKUser> findByCriteria(Map<String, Object> searchParams) throws AKAuthenticationException;

    /**
     * Sucht anhand der User-ID nach einem User
     *
     * @param userId
     * @return Objekt vom typ AKUser
     * @throws AKAuthenticationException
     *
     */
    public AKUser findById(Long userId) throws AKAuthenticationException;

    /**
     * Laedt alle AKRole-Objekte, die fuer einen User fuer eine best. Applikation definiert sind.
     *
     * @param userId        ID des Users, dessen Rollen gelesen werden sollen.
     * @param applicationId Applikations-ID zur Filterung der Rollen.
     * @return Liste von AKRole-Objekten.
     * @throws AKAuthenticationException wenn bei der Ermittlung der Rollen ein Fehler aufgetritt.
     */
    public List<AKRole> getRoles(Long userId, Long applicationId) throws AKAuthenticationException;

    /**
     * Laedt alle AKRole-Objekte, die einem best. User zugeordnet sind.
     *
     * @param userId ID des Users, dess Rollen gelesen werden sollen.
     * @return Liste von AKRole-Objekten
     * @throws AKAuthenticationException wenn bei der Ermittlung der Rollen ein Fehler aufgetritt.
     */
    public List<AKRole> getRoles(Long userId) throws AKAuthenticationException;

    /**
     * Sucht nach allen Benutzern, die einer bestimmten Rolle zugeordnet sind.
     *
     * @param roleId ID der Rolle, deren zugeordnete Benutzer gesucht werden sollen
     * @return Liste mit AKUser-Objekten
     * @throws AKAuthenticationException wenn bei der Ermittlung der User ein Fehler auftritt.
     */
    public List<AKUser> findByRole(Long roleId) throws AKAuthenticationException;

    /**
     * Ordnet dem Benutzer mit der ID <code>userId</code> alle Rollen zu, die in <code>roles</code> aufgefuehrt sind.
     * <br> Rollen, die bisher in der DB eingetragen waren, aber nicht in der Liste enthalten sind, werden von der
     * Zuordnung entfernt. Rollen, die dagegen bisher in der DB nicht fuer den Benutzer eingetragen waren, werden der
     * Zuordnung hinzugefuegt.
     *
     * @param userId ID des Benutzers
     * @param roles  Rollen, die der Benutzer erhalten soll.
     * @throws AKAuthenticationException wenn bei der Rollenzuordnung ein Fehler auftritt.
     */
    public void setRoles(Long userId, List<AKRole> roles) throws AKAuthenticationException;

    /**
     * Laedt alle AKAccount-Objekte, die einem best. User und einer best. Applikation zugeordnet sind.
     *
     * @param userId        ID des Users
     * @param applicationId ID der Application
     * @return Liste von AKAccount-Objekten
     * @throws AKAuthenticationException
     */
    public List<AKAccount> getDBAccounts(Long userId, Long applicationId) throws AKAuthenticationException;

    /**
     * Laedt alle AKAccount-Objekte, die einem best. User zugeordnet sind (unabhaengig von der Applikation).
     *
     * @param userId ID des Users
     * @return List mit AKAccount-Objekten.
     * @throws AKAuthenticationException
     */
    public List<AKAccount> getDBAccounts(Long userId) throws AKAuthenticationException;

    /**
     * Sucht nach allen Benutzern, die einem bestimmten Account zugeordnet sind.
     *
     * @param roleId ID des Accounts, dessen zugeordnete Benutzer gesucht werden sollen
     * @return Liste mit AKUser-Objekten
     * @throws AKAuthenticationException wenn bei der Ermittlung der User ein Fehler auftritt.
     */
    public List<AKUser> findByAccount(Long accountId) throws AKAuthenticationException;

    /**
     * Ordnet dem Benutzer mit der ID <code>userId</code> alle DB-Accounts zu, die in <code>accounts</code> aufgefuehrt
     * sind. <br> Accounts, die bisher in der DB eingetragen waren, aber nicht in der Liste enthalten sind, werden von
     * der Zuordnung entfernt. Accounts, die dagegen bisher in der DB nicht fuer den Benutzer eingetragen waren, werden
     * der Zuordnung hinzugefuegt.
     *
     * @param userId   ID des Benutzers
     * @param accounts Accounts, die der Benutzer erhalten soll.
     * @throws AKAuthenticationException wenn bei der Accountzuordnung ein Fehler auftritt.
     */
    public void setDBAccounts(Long userId, List<AKAccount> accounts) throws AKAuthenticationException;

    /**
     * Kopiert alle Rollen des Users mit der ID <code>origUser</code> auf den User mit der ID <code>newUser</code>.
     * Evtl. vorhandene Rollen von newUser werden vorher geloescht.
     *
     * @param origUser
     * @param newUser
     * @throws AKAuthenticationException
     */
    public void copyUserRoles(Long origUser, Long newUser) throws AKAuthenticationException;

    /**
     * Kopiert alle Accounts des Users mit der ID <code>origUser</code> auf den User mit der ID <code>newUser</code>.
     * Evtl. vorhandene Accounts von newUser werden vorher geloescht.
     *
     * @param origUser
     * @param newUser
     * @throws AKAuthenticationException
     */
    public void copyUserAccounts(Long origUser, Long newUser) throws AKAuthenticationException;

    /**
     * Funktion liefert alle noch gueltigen UserSessions zu einem bestimmten Host.
     *
     * @param hostName HostName
     * @return Liste mit UserSession-Objekten
     * @throws AKAuthenticationException Falls ein Fehler auftrat
     *
     */
    public List<AKUserSession> findAktUserSessionByHostName(String hostName) throws AKAuthenticationException;
}
