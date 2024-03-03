/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 15:30:28
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.augustakom.authentication.dao.AKUserSessionDAO;
import de.augustakom.authentication.model.AKUserSession;
import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;


/**
 * Hibernate DAO-Implementierung fuer AKUserSessionDAO.
 *
 *
 */
public class AKUserSessionDAOImpl extends Hibernate4DAOImpl implements AKUserSessionDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.authentication.dao.AKUserSessionDAO#findById(java.lang.Long)
     */
    @Override
    public AKUserSession findById(Long id) {
        Object result = getSession().get(AKUserSession.class, id);

        if (result instanceof AKUserSession) {
            AKUserSession us = (AKUserSession) result;
            if (us.getDeprecationTime().after(new Date())) {
                return us;
            }
        }
        return null;
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserSessionDAO#findSessions(java.lang.Long, java.util.Date)
     */
    @Override
    public List<AKUserSession> findSessions(final Long applicationId, final Date maxExpirationDate) {
        Criteria crit = getSession().createCriteria(AKUserSession.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "applicationId", applicationId);
        CriteriaHelper.addExpression(crit, CriteriaHelper.LESS_EQUAL, "deprecationTime", maxExpirationDate);
        @SuppressWarnings("unchecked")
        List<AKUserSession> result = crit.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserSessionDAO#createUserSession(de.augustakom.authentication.model.AKUserSession)
     */
    @Override
    public void saveUserSession(AKUserSession session) {
        getSession().save(session);
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserSessionDAO#deleteUserSession(java.lang.Long)
     */
    @Override
    public void deleteUserSession(final Long sessionId) {
        String hql = "delete from de.augustakom.authentication.model.AKUserSession us where us.sessionId=:sessionId";
        Query query = getSession().createQuery(hql);
        query.setLong("sessionId", sessionId);
        query.executeUpdate();
    }

    /**
     * @see de.augustakom.authentication.dao.AKUserSessionDAO#findAktUserSessionByHostName(java.lang.String)
     */
    @Override
    public List<AKUserSession> findAktUserSessionByHostName(final String hostName) {
        Criteria crit = getSession().createCriteria(AKUserSession.class);
        CriteriaHelper.addExpression(crit, CriteriaHelper.EQUAL, "hostName", hostName);
        CriteriaHelper.addExpression(crit, CriteriaHelper.GREATER_EQUAL, "deprecationTime", new Date());
        crit.addOrder(Order.desc("loginTime"));
        @SuppressWarnings("unchecked")
        List<AKUserSession> result = crit.list();
        return result;
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
