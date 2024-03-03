/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 10:55:23
 */
package de.augustakom.authentication.service.impl;

import java.io.*;
import java.util.*;
import javax.annotation.*;
import javax.security.auth.*;
import javax.security.auth.login.*;
import com.sun.security.auth.NTDomainPrincipal;
import com.sun.security.auth.NTUserPrincipal;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import de.augustakom.authentication.dao.AKAccountDAO;
import de.augustakom.authentication.dao.AKUserSessionDAO;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.authentication.service.AKApplicationService;
import de.augustakom.authentication.service.AKDbService;
import de.augustakom.authentication.service.AKLoginService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.authentication.service.exceptions.AKPasswordException;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.system.SystemInformation;

/**
 * Implementierung eines LoginServices. <br> <br> Ablauf des Logins: <br> 1. Applikation ueber den Namen suchen <br> 2.
 * User ueber Loginname/Passwort suchen <br> 3. Gueltigkeit des Passworts pruefen <br> 4. DB-Accounts des Users fuer die
 * Applikation suchen 5. User-Session anlegen <br> <br> Ablauf des Logouts: <br> 1. User-Session loeschen
 *
 *
 */
@AuthenticationTx
public class AKLoginServiceImpl implements AKLoginService {

    private static final Logger LOGGER = Logger.getLogger(AKLoginServiceImpl.class);

    private static final int DEFAULT_DEPRECATION_MINUTES = 1440;

    /* Anzahl Minuten, nach der eine Session ungueltig sein soll */
    private int deprecationMinutes = 0;

    /* DAOs */
    @Resource(name = "de.augustakom.authentication.dao.AKAccountDAO")
    private AKAccountDAO accountDao;
    @Resource(name = "de.augustakom.authentication.dao.AKUserSessionDAO")
    private AKUserSessionDAO userSessionDao;

    /* Services */
    @Resource(name = "de.augustakom.authentication.service.AKApplicationService")
    private AKApplicationService appService;
    @Resource(name = "de.augustakom.authentication.service.AKUserService")
    private AKUserService userService;
    @Resource(name = "de.augustakom.authentication.service.AKDbService")
    private AKDbService dbService;
    @Resource(name = "ldapActiveDirectoryAuthProvider")
    private AuthenticationProvider authenticationProvider;

    @Override
    public void logout(Long sessionId) throws AKAuthenticationException {
        try {
            userSessionDao.deleteUserSession(sessionId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException.MSG_LOGOUT_FAILES, e);
        }
    }

    @Override
    public AKLoginContext ldapLogin(final String user, final String passwd, final String appName, final String version)
            throws AKAuthenticationException, AKPasswordException {
        return this.ldapLogin(user, passwd, appName, version, false);
    }

    @Override
    @AuthenticationTx
    public AKLoginContext ldapLogin(final String user, final String passwd, final String appName, final String version, final boolean noDeprecation)
            throws AKAuthenticationException, AKPasswordException {
        final AKLoginContext ctx = createAkLoginContext(user, passwd, appName, version, noDeprecation);

        return login(ctx, new LoginStrategy() {
            @Override
            public String login() throws AKAuthenticationException {
                final Authentication authentication;
                try {
                    authentication = authenticationProvider
                            .authenticate(
                                    new UsernamePasswordAuthenticationToken(user, passwd));
                    return authentication.getName();
                }
                catch (AuthenticationException e) {
                    throw new AKAuthenticationException(
                            AKAuthenticationException.MSG_INVALID_USER_OR_PASSWORD,
                            new Object[] { e.getMessage() });
                }
            }
        });
    }

    @Override
    public AKLoginContext windowsLogin(final String appName, final String version)
            throws AKAuthenticationException, AKPasswordException {
        final AKLoginContext ctx = createAkLoginContext(null, null, appName, version, false);
        return login(ctx, new LoginStrategy() {
            @Override
            public String login() throws AKAuthenticationException {
                final String loginConfFilename;
                try {
                    loginConfFilename = new File("config/login.conf").getAbsolutePath();
                    System.setProperty("java.security.auth.login.config", loginConfFilename);
                    // create a LoginContext based on the entry in the login.conf file
                    final LoginContext lc = new LoginContext("SignedOnUserLoginContext");
                    // login (effectively populating the Subject)
                    lc.login();
                    // get the Subject that represents the signed-on user
                    final Subject signedOnUserSubject = lc.getSubject();
                    final String username = getUsername(signedOnUserSubject);
                    checkSubjectInNtMnetDomain(signedOnUserSubject, username);
                    return username;
                }
                catch (LoginException e) {
                    throw new AKAuthenticationException(e.getMessage(), e);
                }
            }

            private String getUsername(final Subject subject) throws AKAuthenticationException {
                final Set<NTUserPrincipal> userPrincipals = subject.getPrincipals(NTUserPrincipal.class);

                if (userPrincipals.isEmpty()) {
                    throw new AKAuthenticationException(String.format("Der aktuell angemeldete Windows-Benutzer konnte nicht ermittelt werden"));
                }
                else {
                    return userPrincipals.iterator().next().getName();
                }
            }

            private void checkSubjectInNtMnetDomain(final Subject subject, final String username) throws AKAuthenticationException {
                final Set<NTDomainPrincipal> domainPrincipals = subject.getPrincipals(NTDomainPrincipal.class);
                boolean ntmnetDomainNotFound = true;
                for (final NTDomainPrincipal domainPrincipal : domainPrincipals) {
                    if ("NTMNET".equals(domainPrincipal.getName())) {
                        ntmnetDomainNotFound = false;
                        break;
                    }
                }
                if (ntmnetDomainNotFound) {
                    throw new AKAuthenticationException(String.format("Benutzer '%s' ist nicht in der Domäne 'NTMNET'!", username));
                }
            }
        });
    }

    interface LoginStrategy {
        String login() throws AKAuthenticationException;
    }

    private AKLoginContext createAkLoginContext(final String username, final String password, final String appName,
            final String version, final boolean noDeprecation) {
        AKLoginContext login = new AKLoginContext();
        login.setUserName(username);
        login.setPassword(password);
        login.setApplicationName(appName);
        login.setHostUser(SystemUtils.USER_NAME);
        login.setHostName(SystemInformation.getLocalHostName());
        login.setIpAddress(SystemInformation.getLocalHostAddress());
        login.setApplicationVersion(version);
        login.setNoDeprecation(noDeprecation);
        return login;
    }

    @AuthenticationTx
    protected final AKLoginContext login(final AKLoginContext loginContext, final LoginStrategy loginStrategy)
            throws AKAuthenticationException, AKPasswordException {
        if ((loginContext == null) || (loginContext.getApplicationName() == null)) {
            throw new AKAuthenticationException(AKAuthenticationException.MSG_NO_LOGIN_DATA);
        }

        try {
            // Application ueberpruefen
            AKApplication application = findApplication(loginContext.getApplicationName());
            loginContext.setApplication(application);

            final String username = loginStrategy.login();
            loginContext.setUserName(username);

            // User ueberpruefen
            AKUser user = findUser(username);
            if (!user.isActive()) {
                throw new AKAuthenticationException(
                        AKAuthenticationException.MSG_USER_INACTIVE,
                        new Object[] { loginContext.getUserName() });
            }

            loginContext.setUser(user);

            // Rollen pruefen
            checkUserRoles4Appliation(loginContext, user, application);

            // DBs und DB-Accounts auslesen
            Map<Long, AKDb> dbs = readDBs();
            loginContext.setDatabases(dbs);
            List<AKAccount> dbAccounts = getDBAccounts(loginContext);
            loginContext.setAccounts(dbAccounts);

            // UserSession anlegen
            AKUserSession userSession = createUserSession(loginContext);
            userSessionDao.flushSession();
            loginContext.setUserSession(userSession);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }

        LOGGER.info("Login erfolgreich fuer User: " + loginContext.getUserName());
        return loginContext;
    }

    @Override
    public List<AKUserSession> removeExpiredSessions(Long applicationId, Date expirationDate)
            throws AKAuthenticationException {
        Date expirationDate1 = (expirationDate != null) ? expirationDate : new Date();
        if (DateTools.isAfter(expirationDate1, new Date())) {
            throw new AKAuthenticationException();
        }

        try {
            List<AKUserSession> sessions = userSessionDao.findSessions(applicationId, expirationDate1);
            if (sessions != null) {
                List<AKUserSession> retVal = new ArrayList<>();
                for (AKUserSession toDelete : sessions) {
                    userSessionDao.deleteUserSession(toDelete.getSessionId());
                    retVal.add(toDelete);
                }
                return retVal;
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
        return Collections.emptyList();
    }

    /**
     * Liest alle Datenbank-Definitionen aus und speichert uebertraegt diese in eine Map. Als Key wird die ID der
     * Datenbank verwendet, als Value das Objekt selbst.
     *
     * @throws AKAuthenticationException
     * @throws ServiceNotFoundException
     */
    private Map<Long, AKDb> readDBs() throws AKAuthenticationException, ServiceNotFoundException {
        List<AKDb> dbs = dbService.findAll();
        if (dbs != null) {
            Map<Long, AKDb> dbMap = new HashMap<>();
            for (AKDb db : dbs) {
                dbMap.put(db.getId(), db);
            }
            return dbMap;
        }

        return Collections.emptyMap();
    }

    /**
     * Laedt ein AKApplication-Objekt ueber den angegebenen Namen. <br> Wird keine Applikation mit dem definierten Namen
     * gefunden, wird eine AKAuthenticationException geworfen.
     *
     * @param name Name der gesuchten Applikation.
     * @return
     * @throws AKAuthenticationException
     * @throws ServiceNotFoundException
     */
    private AKApplication findApplication(String name) throws AKAuthenticationException, ServiceNotFoundException {
        AKApplication app = appService.findByName(name);
        if (app == null) {
            throw new AKAuthenticationException(
                    AKAuthenticationException.MSG_INVALID_APPLICATION_NAME, new Object[] { name });
        }

        return app;
    }

    /**
     * Laedt einen AKUser-Objekt ueber die Kombination von Loginname und Passwort.
     *
     * @param name Loginname des Users.
     * @return AKUser-Objekt
     * @throws AKAuthenticationException
     */
    private AKUser findUser(String name) throws AKAuthenticationException {
        final AKUser user = userService.findByLoginName(name);
        if (user == null) {
            throw new AKAuthenticationException(
                    AKAuthenticationException.MSG_USER_NOT_EXIST,
                    new Object[] { name });
        }

        return user;
    }

    /**
     * Ueberprueft, ob dem Benutzer fuer die Applikation mindestens eine Rolle zugeordnet ist. <br>
     *
     * @param loginContext
     * @param user         Benutzer, dessen Rollen ueberprueft werden sollen
     * @param application  Applikation, fuer die die Rollen ueberprueft werden sollen
     * @throws AKAuthenticationException wenn dem Benutzer fuer die Applikation keine Rolle zugeordnet ist.
     */
    private void checkUserRoles4Appliation(AKLoginContext loginContext, AKUser user, AKApplication application)
            throws AKAuthenticationException {
        List<AKRole> roles = userService.getRoles(user.getId(), application.getId());
        loginContext.setRoles(roles);
        if ((roles == null) || (roles.isEmpty())) {
            throw new AKAuthenticationException(
                    AKAuthenticationException.MSG_USER_HAS_NO_ROLES, new Object[] { application.getName() });
        }
    }

    /**
     * Liest die DB-Accounts fuer einen Benutzer und eine Applikation aus und gibt diese in einer List zurueck.
     *
     * @param loginContext
     * @return
     * @throws AKAuthenticationException
     */
    private List<AKAccount> getDBAccounts(AKLoginContext loginContext) throws AKAuthenticationException {
        try {
            return accountDao.findAccounts(loginContext.getUser().getId(), loginContext.getApplication().getId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * Erzeugt eine neue UserSession. <br> Das erzeugte UserSession-Objekt wird in das Objekt <code>loginContext</code>
     * geschrieben.
     *
     * @param loginContext Daten fuer den Login und die Session.
     * @return AKUserSession-Objekt
     * @throws AKAuthenticationException wenn die UserSession nicht angelegt werden konnte.
     */
    private AKUserSession createUserSession(AKLoginContext loginContext) throws AKAuthenticationException {
        AKUserSession us = new AKUserSession();
        try {
            us.setApplicationId(loginContext.getApplication().getId());
            us.setUserId(loginContext.getUser().getId());
            us.setHostUser(loginContext.getHostUser());
            us.setHostName(loginContext.getHostName());
            us.setIpAddress(loginContext.getIpAddress());
            us.setApplicationVersion(loginContext.getApplicationVersion());
            us.setLoginTime(new Date());

            if (loginContext.isNoDeprecation()) {
                us.setDeprecationTime(DateTools.getHurricanEndDate());
            }
            else {
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(GregorianCalendar.MINUTE,
                        (deprecationMinutes > 0) ? deprecationMinutes : DEFAULT_DEPRECATION_MINUTES);
                us.setDeprecationTime(gc.getTime());
            }

            userSessionDao.saveUserSession(us);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException.MSG_USERSESSION_NOT_CREATED, e);
        }

        if ((us.getSessionId() == null) || (us.getSessionId() <= 0)) {
            throw new AKAuthenticationException(AKAuthenticationException.MSG_USERSESSION_NOT_CREATED);
        }

        return us;
    }

    @Override
    public void setSessionDeprecationMinutes(int minutes) {
        this.deprecationMinutes = minutes;
    }
}
