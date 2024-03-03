/**
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 10:56:29
 */
package de.augustakom.authentication.service.impl;

import java.util.*;
import javax.annotation.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.dao.AKAccountDAO;
import de.augustakom.authentication.dao.AKDepartmentDAO;
import de.augustakom.authentication.dao.AKRoleDAO;
import de.augustakom.authentication.dao.AKUserDAO;
import de.augustakom.authentication.dao.AKUserSessionDAO;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.AuthenticationTx;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;

/**
 * Implementierung eines UserServices.
 */
@AuthenticationTx
public class AKUserServiceImpl implements AKUserService {

    private static final Logger LOGGER = Logger.getLogger(AKUserServiceImpl.class);

    /* DAOs */
    @Resource(name = "de.augustakom.authentication.dao.AKUserDAO")
    protected AKUserDAO userDao;
    @Resource(name = "de.augustakom.authentication.dao.AKRoleDAO")
    private AKRoleDAO roleDao;
    @Resource(name = "de.augustakom.authentication.dao.AKDepartmentDAO")
    private AKDepartmentDAO departmentDao;
    @Resource(name = "de.augustakom.authentication.dao.AKAccountDAO")
    private AKAccountDAO accountDao;
    @Resource(name = "de.augustakom.authentication.dao.AKUserSessionDAO")
    private AKUserSessionDAO userSessionDao;

    @Override
    public void save(AKUser user) throws AKAuthenticationException {
        try {
            userDao.saveOrUpdate(user);
            user.notifyObservers(true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void delete(Long userId) throws AKAuthenticationException {
        try {
            userDao.delete(userId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException.MSG_ERROR_DELETING_USER,
                    new Object[] { userId }, e);
        }
    }

    @Override
    public AKUser findUserBySessionId(Long sessionId) throws AKAuthenticationException {
        if (sessionId == null) {
            return null;
        }
        AKUserSession session = null;
        try {
            session = userSessionDao.findById(sessionId);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        if (session != null) {
            if (session.getDeprecationTime().before(new Date())) {
                throw new AKAuthenticationException(AKAuthenticationException.MSG_SESSION_TIMED_OUT,
                        new Object[] { sessionId.toString() });
            }

            try {
                AKUser user = userDao.findById(session.getUserId(), AKUser.class);
                return user;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new AKAuthenticationException(AKAuthenticationException.MSG_USER_NOT_FOUND);
            }
        }
        throw new AKAuthenticationException(AKAuthenticationException.MSG_INVALID_SESSION_ID,
                new Object[] { sessionId.toString() });
    }

    @Override
    public List<AKUser> findByName(String vorname, String nachname)
            throws AKAuthenticationException {
        try {
            return userDao.findByName(vorname, nachname);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKUser> findByDepartment(Long departmentId) throws AKAuthenticationException {
        try {
            return userDao.findByDepartment(departmentId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKUser> findByTeam(AKTeam team) throws AKAuthenticationException {
        try {
            return userDao.findByTeam(team);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKUser> findByHurricanAbteilungId(Long hurricanAbteilungId) throws AKAuthenticationException {
        try {
            AKDepartment example = new AKDepartment();
            example.setHurricanAbteilungId(hurricanAbteilungId);

            List<AKUser> userOfDepartments = new ArrayList<AKUser>();
            List<AKDepartment> departments = departmentDao.queryByExample(example, AKDepartment.class);
            if (CollectionTools.isNotEmpty(departments)) {
                for (AKDepartment department : departments) {
                    userOfDepartments.addAll(findByDepartment(department.getId()));
                }
            }

            return userOfDepartments;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKUser> findAllProjektleiter() throws AKAuthenticationException {
        try {
            AKUser example = new AKUser();
            example.setProjektleiter(true);
            example.setActive(true);

            return userDao.queryByExample(example, AKUser.class, new String[] { "name", "firstName" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public Map<Long, String> findUserIdToNames() throws AKAuthenticationException {
        try {
            AKUser example = new AKUser();
            example.setProjektleiter(null);
            example.setManager(null);

            List<AKUser> allUsers = userDao.queryByExample(example, AKUser.class);
            return CollectionMapConverter.convert2Map(allUsers, "getId", "getNameAndFirstName");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKUser> findManagers() throws AKAuthenticationException {
        try {
            AKUser example = new AKUser();
            example.setManager(true);
            example.setActive(true);
            example.setProjektleiter(null); // is false by default, but it must be ignored

            return userDao.queryByExample(example, AKUser.class, new String[] { "name", "firstName" }, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AKUser findByLoginName(String loginName) throws AKAuthenticationException {
        try {
            return userDao.findUserByLogin(loginName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKUser> findByCriteria(Map<String, Object> searchParams) throws AKAuthenticationException {
        try {
            return userDao.findByCriteria(searchParams);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public AKUser findById(Long userId) throws AKAuthenticationException {
        try {
            return userDao.findById(userId, AKUser.class);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    /**
     * @see de.augustakom.authentication.service.AKUserService#findByRole(java.lang.Long)
     */
    @Override
    public List<AKUser> findByRole(Long roleId) throws AKAuthenticationException {
        try {
            return userDao.findByRole(roleId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKRole> getRoles(Long userId, Long applicationId) throws AKAuthenticationException {
        try {
            return roleDao.findByUserAndApplication(userId, applicationId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKRole> getRoles(Long userId) throws AKAuthenticationException {
        try {
            return roleDao.findByUser(userId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void setRoles(Long userId, List<AKRole> roles) throws AKAuthenticationException {
        if ((userId == null) || (roles == null)) {
            throw new AKAuthenticationException(AKAuthenticationException.MSG_NO_USER_OR_ROLES);
        }

        try {
            List<AKRole> currentRoles = getRoles(userId);

            if (currentRoles != null) {
                List<AKRole> toRemove = getRoleDifferences(currentRoles, roles);
                List<AKRole> toAdd = getRoleDifferences(roles, currentRoles);

                for (int i = 0; i < toRemove.size(); i++) {
                    roleDao.removeUserRole(userId, (toRemove.get(i)).getId());
                }

                for (int i = 0; i < toAdd.size(); i++) {
                    roleDao.addUserRole(userId, (toAdd.get(i)).getId());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException.MSG_ERROR_SETTING_ROLES, e);
        }
    }

    /**
     * Sucht nach Differenzen in den beiden Listen. <br> Objekte, die in <code>preference</code> vorhanden sind, nicht
     * jedoch in <code>toFilter</code> werden in eine Liste eingetragen und diese zurueck gegeben.
     *
     * @param preference Liste mit gegebenen AKRole-Objekten
     * @param toFilter   Liste mit AKRole-Objekten, die gegen <code>preference</code> geprueft werden soll.
     * @return Liste mit den AKRole-Objekten, die zwar in <code>preference</code>, nicht jedoch in <code>toFilter</code>
     * enthalten sind.
     */
    private List<AKRole> getRoleDifferences(List<AKRole> preference, List<AKRole> toFilter) {
        List<AKRole> result = new ArrayList<AKRole>();

        for (int i = 0; i < preference.size(); i++) {
            AKRole role = preference.get(i);

            boolean roleFound = false;
            for (int k = 0; k < toFilter.size(); k++) {
                if (role.getId().equals((toFilter.get(k)).getId())) {
                    roleFound = true;
                }
            }

            if (!roleFound) {
                result.add(role);
            }
        }

        return result;
    }

    @Override
    public List<AKUser> findByAccount(Long accountId) throws AKAuthenticationException {
        try {
            return userDao.findByAccount(accountId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKAccount> getDBAccounts(Long userId, Long applicationId) throws AKAuthenticationException {
        try {
            return accountDao.findAccounts(userId, applicationId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public List<AKAccount> getDBAccounts(Long userId) throws AKAuthenticationException {
        try {
            return accountDao.findAccounts(userId);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void setDBAccounts(Long userId, List<AKAccount> accounts) throws AKAuthenticationException {
        if ((userId == null) || (accounts == null)) {
            throw new AKAuthenticationException(AKAuthenticationException.MSG_NO_USER_OR_ACCOUNTS);
        }

        try {
            List<AKAccount> currentAccounts = getDBAccounts(userId);

            if (currentAccounts != null) {
                List<AKAccount> toRemove = getAccountDifferences(currentAccounts, accounts);
                List<AKAccount> toAdd = getAccountDifferences(accounts, currentAccounts);

                for (int i = 0; i < toRemove.size(); i++) {
                    accountDao.removeUserAccount(userId, (toRemove.get(i)).getId());
                }

                for (int i = 0; i < toAdd.size(); i++) {
                    AKAccount acc = toAdd.get(i);
                    accountDao.addUserAccount(userId, acc.getId(), acc.getDbId());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException.MSG_ERROR_SETTING_ROLES, e);
        }
    }

    /**
     * Sucht nach Differenzen in den beiden Listen. <br> Objekte, die in <code>preference</code> vorhanden sind, nicht
     * jedoch in <code>toFilter</code> werden in eine Liste eingetragen und diese zurueck gegeben.
     *
     * @param preference Liste mit gegebenen AKRole-Objekten
     * @param toFilter   Liste mit AKRole-Objekten, die gegen <code>preference</code> geprueft werden soll.
     * @return Liste mit den AKRole-Objekten, die zwar in <code>preference</code>, nicht jedoch in <code>toFilter</code>
     * enthalten sind.
     */
    private List<AKAccount> getAccountDifferences(List<AKAccount> preference, List<AKAccount> toFilter) {
        List<AKAccount> result = new ArrayList<AKAccount>();

        for (int i = 0; i < preference.size(); i++) {
            AKAccount account = preference.get(i);

            boolean accountFound = false;
            for (int k = 0; k < toFilter.size(); k++) {
                if (account.getId().equals((toFilter.get(k)).getId())) {
                    accountFound = true;
                }
            }

            if (!accountFound) {
                result.add(account);
            }
        }

        return result;
    }

    @Override
    public void copyUserRoles(Long origUser, Long newUser) throws AKAuthenticationException {
        if ((origUser == null) || (newUser == null)) {
            throw new AKAuthenticationException(AKAuthenticationException.MSG_USER_NOT_FOUND);
        }

        try {
            List<AKRole> roles = getRoles(origUser);
            setRoles(newUser, roles);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

    @Override
    public void copyUserAccounts(Long origUser, Long newUser) throws AKAuthenticationException {
        if ((origUser == null) || (newUser == null)) {
            throw new AKAuthenticationException(AKAuthenticationException.MSG_USER_NOT_FOUND);
        }
        try {
            List<AKAccount> accs = getDBAccounts(origUser);
            setDBAccounts(newUser, accs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }

    }

    @Override
    public List<AKUserSession> findAktUserSessionByHostName(String hostName) throws AKAuthenticationException {
        if (StringUtils.isBlank(hostName)) {
            throw new AKAuthenticationException(AKAuthenticationException.MSG_INVALID_HOST_NAME);
        }

        try {
            return userSessionDao.findAktUserSessionByHostName(hostName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKAuthenticationException(AKAuthenticationException._UNEXPECTED_ERROR, e);
        }
    }

}
