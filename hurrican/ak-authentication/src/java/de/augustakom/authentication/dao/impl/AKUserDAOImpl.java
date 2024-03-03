/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 12:02:22
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import java.util.Map.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKUserDAO;
import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.WildcardTools;

/**
 * Hibernate DAO-Implementierung von UserDAO. <br>
 *
 *
 */
public class AKUserDAOImpl extends Hibernate4FindDAOImpl implements AKUserDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.authentication.dao.AKUserDAO#findByDepartment(java.lang.Long)
     */
    @Override
    public List<AKUser> findByDepartment(final Long id) {
        Criteria criteria = getSession().createCriteria(AKUser.class);
        criteria.add(Restrictions.eq("departmentId", id));
        criteria.addOrder(Order.asc("name"));
        criteria.addOrder(Order.asc("firstName"));
        @SuppressWarnings("unchecked")
        List<AKUser> result = criteria.list();
        return result;
    }

    @Override
    public List<AKUser> findByTeam(final AKTeam team) {
        Criteria criteria = getSession().createCriteria(AKUser.class);
        criteria.add(Restrictions.eq("team", team));
        criteria.addOrder(Order.asc("name"));
        criteria.addOrder(Order.asc("firstName"));
        @SuppressWarnings("unchecked")
        List<AKUser> result = criteria.list();
        return result;
    }

    @Override
    public List<AKUser> findByName(final String vorname, final String nachname) {
        Criteria criteria = getSession().createCriteria(AKUser.class);
        criteria.add(Restrictions.eq("firstName", vorname));
        criteria.add(Restrictions.eq("name", nachname));
        @SuppressWarnings("unchecked")
        List<AKUser> result = criteria.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserDAO#findByRole(java.lang.Long)
     */
    @Override
    public List<AKUser> findByRole(Long roleId) {
        String hql = "select new AKUser(u.id, u.loginName, u.name, u.firstName, " +
                "u.email, u.phone, u.phoneNeutral, u.fax, u.departmentId, u.active) " +
                "from AKUser u, AKUserRole ur where u.id=ur.userId and ur.roleId=:roleId " +
                "order by u.name, u.firstName";

        Query query = getSession().createQuery(hql);
        query.setLong("roleId", roleId);
        @SuppressWarnings("unchecked")
        List<AKUser> result = (List<AKUser>) query.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserDAO#findByAccount(java.lang.Long)
     */
    @Override
    public List<AKUser> findByAccount(Long accountId) {
        String hql = "select new AKUser(u.id, u.loginName, u.name, u.firstName, " +
                "u.email, u.phone, u.phoneNeutral, u.fax, u.departmentId, u.active) " +
                "from AKUser u, AKUserAccount ua where u.id=ua.userId and ua.accountId=:accountId";

        Query query = getSession().createQuery(hql);
        query.setLong("accountId", accountId);
        @SuppressWarnings("unchecked")
        List<AKUser> result = (List<AKUser>) query.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserDAO#findUser(java.lang.String, java.lang.String)
     */
    @Override
    public AKUser findUser(final String loginName, final String password) {
        Criteria criteria = getSession().createCriteria(AKUser.class);
        criteria.add(Restrictions.ilike("loginName", loginName.toLowerCase(), MatchMode.EXACT));
        criteria.add(Restrictions.eq("password", password));
        @SuppressWarnings("unchecked")
        List<AKUser> result = criteria.list();
        return ((result != null) && (!result.isEmpty())) ? result.get(0) : null;
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserDAO#findUserByLogin(java.lang.String)
     */
    @Override
    public AKUser findUserByLogin(final String loginName) {
        Criteria criteria = getSession().createCriteria(AKUser.class);
        criteria.add(Restrictions.eq("loginName", loginName).ignoreCase());
        @SuppressWarnings("unchecked")
        List<AKUser> result = criteria.list();
        return ((result != null) && (!result.isEmpty())) ? result.get(0) : null;
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserDAO#saveOrUpdate(de.augustakom.authentication.model.AKUser)
     */
    @Override
    public void saveOrUpdate(final AKUser user) {
        getSession().saveOrUpdate(user);
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserDAO#delete(java.lang.Long)
     */
    @Override
    public void delete(final Long userId) {
        String hql = "delete from de.augustakom.authentication.model.AKUser where id=:userId";
        Query query = getSession().createQuery(hql);
        query.setLong("userId", userId);
        query.executeUpdate();
    }

    @Override
    public List<AKUser> findByCriteria(Map<String, Object> searchParams) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AKUser.class);
        for (Entry<String, Object> param : searchParams.entrySet()) {
            if (StringUtils.isBlank(param.getKey()) || (param.getValue() == null)) {
                continue;
            }
            if (param.getValue() instanceof String) {
                criteria.add(Restrictions.ilike(param.getKey(),
                        WildcardTools.replaceWildcards((String) param.getValue()), MatchMode.EXACT));
            }
            else {
                criteria.add(Restrictions.eq(param.getKey(), param.getValue()));
            }
        }
        criteria.addOrder(Order.asc(AKUser.LOGIN_NAME));

        @SuppressWarnings("unchecked")
        List<AKUser> users = (List<AKUser>) criteria.getExecutableCriteria(getSession()).list();
        return users;
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
