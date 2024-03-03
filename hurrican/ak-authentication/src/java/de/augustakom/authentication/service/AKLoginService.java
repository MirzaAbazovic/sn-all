/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 12:53:44
 */
package de.augustakom.authentication.service;

import java.util.*;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;


/**
 * Interface fuer einen LoginService. <br> Ueber den LoginService kann sich ein Benutzer an einer bestimmten Applikation
 * an- und abmelden.
 *
 *
 */
public interface AKLoginService extends IAuthenticationService {

    /**
     * Die Session mit der angegebenen ID wird entfernt. Dadurch ist der User abgemeldet.
     *
     * @param sessionId Session-ID, die entfernt werden soll.
     * @throws AKAuthenticationException wenn die Session-ID ungueltig war oder beim Logout ein Fehler auftritt.
     */
    public void logout(Long sessionId) throws AKAuthenticationException;

    AKLoginContext ldapLogin(final String user, final String passwd, final String appName, final String version, final boolean noDeprecation)
            throws AKAuthenticationException, AKPasswordException;

    AKLoginContext ldapLogin(final String user, final String passwd, final String appName, final String version)
            throws AKAuthenticationException, AKPasswordException;

    AKLoginContext windowsLogin(String appName, String version)
            throws AKAuthenticationException, AKPasswordException;

    /**
     * Entfernt abgelaufene Sessions aus der Authentication-Datenbank. <br>
     *
     * @param applicationId  (optional) bei Angabe von <code>applicationId</code> werden nur Sessions von der
     *                       entsprechenden Applikation entfernt. Wird <code>null</code> uebergeben, werden abgelaufene
     *                       Sessions aller Applikationen entfernt.
     * @param expirationDate (optional) Datum, ab dem eine Session ungueltig ist. Das Datum darf allerdings nicht in der
     *                       Zukunft liegen!
     * @return Liste mit den entfernten User-Sessions (never {@code null}).
     * @throws AKAuthenticationException wenn beim Entfernen der Sessions ein Fehler auftritt oder das angegebene
     *                                   <code>expirationDate</code> in der Zukunft liegt.
     */
    public List<AKUserSession> removeExpiredSessions(Long applicationId, Date expirationDate) throws AKAuthenticationException;

    /**
     * Ueber diese Methode wird dem Service mitgeteilt, nach wie vielen Minuten eine Session ungueltig werden soll.
     *
     * @param minutes Anzahl Minuten, nach denen eine Session ungueltig sein soll.
     */
    public void setSessionDeprecationMinutes(int minutes);

}
