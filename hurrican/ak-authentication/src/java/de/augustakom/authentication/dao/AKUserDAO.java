/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2004 13:21:55
 */
package de.augustakom.authentication.dao;

import java.util.*;
import org.hibernate.criterion.MatchMode;

import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;

/**
 * Interface zur Definition von Methoden zum Arbeiten mit User-Objekten.
 */
public interface AKUserDAO extends FindDAO, ByExampleDAO {

    /**
     * Sucht nach allen Benutzern, die einer best. Abteilung angehoeren. <br>
     *
     * @param departmentId ID der Abteilung
     * @return Liste von AKUser-Objekten (never {@code null}).
     */
    public List<AKUser> findByDepartment(Long departmentId);

    /**
     * Sucht nach allen Benutzern, die einem best. Team angehoeren. <br>
     */
    public List<AKUser> findByTeam(AKTeam team);

    /**
     * Sucht nach allen Benutzern, die einer bestimmten Rolle zugeordnet sind. <br>
     *
     * @param roleId ID der Rolle
     * @return List von AKUser-Objekten (never {@code null}).
     */
    public List<AKUser> findByRole(Long roleId);

    /**
     * Sucht ueber Vorname und Nachname <br>
     *
     * @return List von AKUser-Objekten (never {@code null}).
     */
    public List<AKUser> findByName(String vorname, String nachname);

    /**
     * Sucht nach allen Benutzern, die einem bestimmten Account zugeordnet sind. <br>
     *
     * @param accountId ID des Accounts
     * @return List von AKUser-Objekten (never {@code null})
     */
    public List<AKUser> findByAccount(Long accountId);

    /**
     * Sucht nach einem Benutzer mit der angegebenen Kombination von Login-Name und Passwort.
     *
     * @param loginName Login-Name des Benutzers.
     * @param password  Passwort (verschluesselt) des Benutzers.
     * @return AKUser User-Objekt mit der Kombination Login-Name und Passwort.
     */
    public AKUser findUser(String loginName, String password);

    /**
     * Sucht anhand des LoginNames einen User
     */
    public AKUser findUserByLogin(String loginName);

    /**
     * Erzeugt oder aktualisiert den DB-Eintrag des Users.
     */
    public void saveOrUpdate(final AKUser user);

    /**
     * Loescht den Benutzer mit der ID <code>userId</code>.
     */
    public void delete(final Long userId);

    /**
     * Suche nach User. Für String parameter wird mit Wildcard (siehe {@link MatchMode#ANYWHERE} gesucht.
     *
     * @param searchParams map mit name der property und suchwert
     * @return gefundene User sortiert nach loginName
     */
    List<AKUser> findByCriteria(Map<String, Object> searchParams);
}
