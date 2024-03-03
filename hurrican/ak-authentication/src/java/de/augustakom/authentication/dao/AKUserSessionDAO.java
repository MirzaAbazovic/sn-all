/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.05.2004 13:29:33
 */
package de.augustakom.authentication.dao;

import java.util.*;

import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;


/**
 * Interface definiert Methoden fuer DAO-Objekte zur Session-Verwaltung.
 */
public interface AKUserSessionDAO extends ByExampleDAO, StoreDAO {

    /**
     * Findet eine UserSession ueber die ID. <br> Ist die DeprecationTime der UserSession bereits abgelaufen, gibt die
     * Methode <code>null</code> zurueck.
     *
     * @param id ID der gesuchten UserSession
     * @return AKUserSession-Objekt, das der ID entspricht.
     */
    public AKUserSession findById(Long id);

    /**
     * Sucht nach allen Sessions einer bestimmten Applikation, deren Gueltigkeit zu <code>maxExpirationDate</code>
     * ablaeuft.
     *
     * @param applicationId     ID der Applikation
     * @param maxExpirationDate Datum, zu dem die gesuchten Sessions ablaufen.
     * @return Liste mit Objekten des Typs <code>AKUserSession</code> (never {@code null}).
     */
    public List<AKUserSession> findSessions(Long applicationId, Date maxExpirationDate);

    /**
     * Speicher die UserSession. <br>
     *
     * @param session UserSession, die angelegt werden soll.
     */
    public void saveUserSession(AKUserSession session);

    /**
     * Loescht die UserSession mit der angegebenen ID.
     *
     * @param sessionId ID der UserSession, die geloescht werden soll.
     */
    public void deleteUserSession(Long sessionId);

    /**
     * Liefert alle gueltigen UserSession-Objekte eines bestimmten Hosts
     *
     * @param hostName Host-Name
     * @return Liste mit UserSession-Objekten (never {@code null})
     */
    public List<AKUserSession> findAktUserSessionByHostName(String hostName);
}
