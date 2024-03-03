/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2004 10:32:57
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKRoleDAO;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUserRole;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;


/**
 * Hibernate-Implementierung von AKRoleDAO. <br>
 *
 *
 */
public class AKRoleDAOImpl extends Hibernate4DAOImpl implements AKRoleDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#saveOrUpdate(de.augustakom.authentication.model.AKRole)
     */
    @Override
    public void saveOrUpdate(AKRole role) {
        getSession().saveOrUpdate(role);
    }

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#delete(java.lang.Long)
     */
    @Override
    public void delete(final Long id) {
        String hql = "delete from de.augustakom.authentication.model.AKRole r where r.id=:id";
        Query query = getSession().createQuery(hql);
        query.setLong("id", id);
        query.executeUpdate();
    }

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#findAll()
     */
    @Override
    public List<AKRole> findAll() {
        @SuppressWarnings("unchecked")
        List<AKRole> result = getSession().createCriteria(AKRole.class).list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#findByApplication(java.lang.Long)
     */
    @Override
    public List<AKRole> findByApplication(final Long applicationId) {
        String hql = "from AKRole as r where r.applicationId=:applicationId order by r.name";

        Query query = getSession().createQuery(hql);
        query.setLong("applicationId", applicationId);
        @SuppressWarnings("unchecked")
        List<AKRole> result = (List<AKRole>) query.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#findByUser(java.lang.Long)
     */
    @Override
    public List<AKRole> findByUser(final Long userId) {
        String hql = "select new AKRole(r.id, r.name, r.description, r.applicationId, r.admin) " +
                "from AKRole as r, AKUserRole as ur where ur.userId=:userId and r.id=ur.roleId";

        Query query = getSession().createQuery(hql);
        query.setLong("userId", userId);
        @SuppressWarnings("unchecked")
        List<AKRole> result = (List<AKRole>) query.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#findByUserAndApplication(java.lang.Long, java.lang.Long)
     */
    @Override
    public List<AKRole> findByUserAndApplication(final Long userId, final Long applicationId) {
        String hql = "select new AKRole(r.id, r.name, r.description, r.applicationId, r.admin) "
                + "from AKRole as r, AKUserRole as ur "
                + "where ur.userId=:userId and r.applicationId=:applicationId and r.id=ur.roleId";
        Query query = getSession().createQuery(hql);
        query.setLong("userId", userId);
        query.setLong("applicationId", applicationId);
        @SuppressWarnings("unchecked")
        List<AKRole> result = (List<AKRole>) query.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#findBySession(java.lang.Long)
     */
    @Override
    public List<AKRole> findBySession(final Long sessionId) {
        final StringBuilder hql = new StringBuilder("select new AKRole(r.id, r.name, r.applicationId, r.admin) ");
        hql.append("from AKRole as r, AKUserRole as ur, AKUserSession as s ");
        hql.append("where s.sessionId= :sessionId and s.userId=ur.userId and r.applicationId=s.applicationId ");
        hql.append("and ur.roleId=r.id");

        Query q = getSession().createQuery(hql.toString());
        q.setParameter("sessionId", sessionId);

        q.setCacheable(true);

        @SuppressWarnings("unchecked")
        List<AKRole> result = q.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#removeUserRole(java.lang.Long, java.lang.Long)
     */
    @Override
    public void removeUserRole(final Long userId, final Long roleId) {
        String hql = "delete from de.augustakom.authentication.model.AKUserRole ur " +
                "where ur.userId=:userId and ur.roleId=:roleId";
        Query query = getSession().createQuery(hql);
        query.setLong("userId", userId);
        query.setLong("roleId", roleId);
        query.executeUpdate();
    }

    /**
     * @see de.augustakom.authentication.dao.AKRoleDAO#addUserRole(java.lang.Long, java.lang.Long)
     */
    @Override
    public void addUserRole(final Long userId, final Long roleId) {
        AKUserRole userRole = new AKUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);

        getSession().save(userRole);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
