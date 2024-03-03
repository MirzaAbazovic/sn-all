/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2004 13:39:14
 */
package de.augustakom.authentication.dao;

import java.util.*;

import de.augustakom.authentication.model.AKRole;
import de.augustakom.common.tools.dao.iface.FindDAO;

/**
 * Interface definiert Methoden fuer DAO-Objekte zur Rollen-Verwaltung.
 */
public interface AKRoleDAO extends FindDAO {

    /**
     * Erzeugt oder aktualisiert das AKRole-Objekt.
     */
    public void saveOrUpdate(AKRole role);

    /**
     * Loescht die Rolle mit der angegebenen ID.
     */
    public void delete(final Long id);

    /**
     * Sucht nach allen AKRole-Objekten.
     *
     * @return Liste mit AKRole-Objekten (never {@code null}).
     */
    public List<AKRole> findAll();

    /**
     * Sucht nach allen AKRole-Objekten, die einem Benutzer zugeordnet sind.
     *
     * @param userId ID des Users, dessen Rollen geladen werden sollen.
     * @return Liste mit AKRole-Objekten (never {@code null})
     */
    public List<AKRole> findByUser(final Long userId);

    /**
     * Sucht nach allen AKRole-Objekten, die einer bestimmten Applikation zugeordnet sind.
     *
     * @param applicationId ID der Application
     * @return Liste mit AKRole-Objekten (never {@code null})
     */
    public List<AKRole> findByApplication(final Long applicationId);

    /**
     * Sucht nach allen AKRole-Objekten, die einer best. Applikation und einem best. Benutzer zugeordnet sind.
     *
     * @param userId ID des Users, dessen Rollen geladen werden sollen
     * @param appId  ID der Applikation, denen die zu ladenden Rollen zugeordnet sein sollen.
     * @return Liste mit AKRole-Objekten (never {@code null})
     */
    public List<AKRole> findByUserAndApplication(final Long userId, final Long appId);

    /**
     * Sucht nach allen AKRole-Objekten, die einem best. Benutzer bei einer best. Applikation zugeordnet sind. <br> Der
     * Benutzer und die Applikation wird dabei ueber die Session-ID ermittelt.
     *
     * @param sessionId Session-ID ueber die der Benutzer und die Applikation ermittelt werden soll.
     * @return Liste mit AKRole-Objekten (never {@code null})
     */
    public List<AKRole> findBySession(final Long sessionId);

    /**
     * Entfernt einen Eintrag aus der User-Rollen-Zuordnung. <br> Wichtig: in dieser Methode wird nicht die Rolle selbst
     * geloescht, sondern nur die Zuordnung zu einem bestimmten Benutzer.
     *
     * @param userId ID des Users, dem die Rolle 'weggenommen' werden soll
     * @param roleId ID der Rolle, die dem Benutzer 'weggenommen' werden soll
     */
    public void removeUserRole(final Long userId, final Long roleId);

    /**
     * Erzeugt einen neuen Eintrag in der User-Rollen-Zuordnung. <br> Wichtig: in dieser Methode wird keine neue Rolle
     * angelegt, sondern nur eine Zuordnung zwischen einem Benutzer und einer Rolle hergestellt.
     *
     * @param userId ID des Users, dem eine Rolle hinzugefuegt werden soll
     * @param roleId ID der Rolle, die dem Benutzer hinzugefuegt werden soll
     */
    public void addUserRole(final Long userId, final Long roleId);
}
