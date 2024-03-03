/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.02.2009 09:11:57
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.math.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.CPSTransactionDAO;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.cps.CPSDataChainConfig;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;


/**
 * Hibernate DAO-Implementierung von <code>CPSTransactionDAO</code>.
 */
public class CPSTransactionDAOImpl extends Hibernate4DAOImpl implements CPSTransactionDAO {

    private static final Logger LOGGER = Logger.getLogger(CPSTransactionDAOImpl.class);
    /* SQL-Statement, um den Next-Value der Sequence S_T_CPS_TX_STACK_SEQUENCE_0 zu ermitteln. */
    private static final String SQL_NEXTVAL_STACK_SEQ =
            "SELECT S_T_CPS_TX_STACK_SEQUENCE_0.NEXTVAL FROM DUAL";
    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public Long getNextCPSStackSequence() {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery(SQL_NEXTVAL_STACK_SEQ);
        return ((BigDecimal) sqlQuery.uniqueResult()).longValue();
    }

    @Override
    public void updateCPSTransaction(CPSTransaction cpsTransaction) {
        sessionFactory.getCurrentSession().update(cpsTransaction);
    }

    @Override
    public List<CPSTransactionExt> findOpenTransactions(final Integer limit) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CPSTransactionExt.class);

        /*
         *  <li> (
         *  <li>Tx-Status >= CPSTransaction.TX_STATE_IN_PREPARING
         *  <li> AND
         *  <li>Tx-Status < CPSTransaction.TX_STATE_FAILURE_CLOSED
         *  <li> )
         *  <li> AND
         *  <li>Tx-Status <> CPSTransaction.TX_STATE_CANCELLED
         *  <li> AND
         *  <li>SERVICE_ORDER_TYPE <> CPSTransaction.SERVICE_ORDER_TYPE_QUERY
         */
        SimpleExpression greaterEqualCondition = Restrictions.ge(CPSTransactionExt.TX_STATE, CPSTransactionExt.TX_STATE_IN_PREPARING);
        SimpleExpression lessCondition = Restrictions.lt(CPSTransactionExt.TX_STATE, CPSTransactionExt.TX_STATE_FAILURE_CLOSED);
        Criterion andExpression = Restrictions.and(greaterEqualCondition, lessCondition);

        criteria.add(andExpression);
        criteria.add(Restrictions.not(Restrictions.eq(CPSTransactionExt.TX_STATE, CPSTransaction.TX_STATE_CANCELLED)));
        criteria.add(Restrictions.not(Restrictions.eq(CPSTransactionExt.SERVICE_ORDER_TYPE, CPSTransaction.SERVICE_ORDER_TYPE_QUERY)));
        criteria.add(Restrictions.not(Restrictions.eq(CPSTransactionExt.SERVICE_ORDER_TYPE,
                CPSTransaction.SERVICE_ORDER_TYPE_QUERY_HARDWARE)));
        criteria.add(Restrictions.not(Restrictions.eq(CPSTransactionExt.SERVICE_ORDER_TYPE,
                CPSTransaction.SERVICE_ORDER_TYPE_GET_SODATA)));
        criteria.addOrder(Order.asc("id"));

        if (null != limit) {
            criteria.setMaxResults(limit);
        }

        @SuppressWarnings("unchecked")
        List<CPSTransactionExt> result = criteria.list();
        return result;
    }

    @Override
    public List<CPSTransactionExt> findExpiredTransactions(final Date expiredDate) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CPSTransactionExt.class);

        /*
         *  <li> (
         *  <li>Tx-Status = CPSTransaction.TX_STATE_IN_PREPARING
         *  <li> OR
         *  <li>Tx-Status = CPSTransaction.TX_STATE_IN_PREPARING_FAILURE
         *  <li> )
         *  <li> AND
         *  <li>Tx-Status <> CPSTransaction.TX_STATE_CANCELLED
         *  <li> AND
         *  <li>ServiceOrderType <> CPSTransaction.SERVICE_ORDER_TYPE_QUERY
         *  <li> AND
         *  <li>ESTIMATED_EXEC_TIME < expiredDate
         */
        SimpleExpression prepairingCondition = Restrictions.eq(CPSTransactionExt.TX_STATE, CPSTransactionExt.TX_STATE_IN_PREPARING);
        SimpleExpression prepairingFailureCondition = Restrictions.eq(CPSTransactionExt.TX_STATE, CPSTransactionExt.TX_STATE_IN_PREPARING_FAILURE);
        Criterion orExpression = Restrictions.or(prepairingCondition, prepairingFailureCondition);

        criteria.add(orExpression);
        criteria.add(Restrictions.not(Restrictions.eq(CPSTransactionExt.TX_STATE, CPSTransaction.TX_STATE_CANCELLED)));
        criteria.add(Restrictions.not(Restrictions.eq(CPSTransactionExt.SERVICE_ORDER_TYPE, CPSTransaction.SERVICE_ORDER_TYPE_QUERY)));
        criteria.add(Restrictions.not(Restrictions.eq(CPSTransactionExt.SERVICE_ORDER_TYPE,
                CPSTransaction.SERVICE_ORDER_TYPE_QUERY_HARDWARE)));
        criteria.add(Restrictions.not(Restrictions.eq(CPSTransactionExt.SERVICE_ORDER_TYPE,
                CPSTransaction.SERVICE_ORDER_TYPE_GET_SODATA)));
        criteria.add(Restrictions.lt(CPSTransactionExt.ESTIMATED_EXEC_TIME, expiredDate));
        criteria.addOrder(Order.asc("id"));

        @SuppressWarnings("unchecked")
        List<CPSTransactionExt> result = criteria.list();
        return result;
    }

    @Override
    public List<CPSTransaction> findCPSTransactions4TechOrder(final Long auftragId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CPSTransaction.class);
        criteria.add(Restrictions.eq("auftragId", auftragId));
        criteria.addOrder(Order.asc("id"));

        @SuppressWarnings("unchecked")
        List<CPSTransaction> result = criteria.list();

        return result;
    }

    @Override
    public List<CPSTransaction> findCPSTransactions4TechOrder(final Long auftragId, Collection<Long> txStates, Collection<Long> serviceOrderTypes) {
        return findCPSTransactions4TechOrder(auftragId, txStates, serviceOrderTypes, true, false, null);
    }

    @Override
    public List<CPSTransaction> findCPSTransactions4TechOrder(final Long auftragId, Collection<Long> txStates, Collection<Long> serviceOrderTypes,
            boolean ascIdOrder, boolean descIdOrder, Integer maxRecordsLimit) {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria criteria = session.createCriteria(CPSTransaction.class);
        criteria.add(Restrictions.eq("auftragId", auftragId));
        if (CollectionUtils.isNotEmpty(txStates)) {
            criteria.add(Restrictions.in("txState", txStates));
        }
        if (CollectionUtils.isNotEmpty(serviceOrderTypes)) {
            criteria.add(Restrictions.in("serviceOrderType", serviceOrderTypes));
        }
        if(ascIdOrder) {
            criteria.addOrder(Order.asc("id"));
        } else if (descIdOrder) {
            criteria.addOrder(Order.desc("id"));
        }
        if (maxRecordsLimit != null && maxRecordsLimit > 0) {
            criteria.setMaxResults(maxRecordsLimit);
        }

        @SuppressWarnings("unchecked")
        final List<CPSTransaction> result = criteria.list();
        return result;
    }

    @Override
    public List<CPSTransaction> findCPSTransactions4BillingOrder(final Long billingOrderNoOrig) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(CPSTransaction.class);
        criteria.add(Restrictions.eq("orderNoOrig", billingOrderNoOrig));
        criteria.addOrder(Order.asc("id"));

        @SuppressWarnings("unchecked")
        List<CPSTransaction> result = criteria.list();

        return result;
    }

    @Override
    public List<CPSTransactionExt> findActiveCPSTransactions4BillingOrder(final Long billingOrderNoOrig) {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria criteria = session.createCriteria(CPSTransaction.class);
        criteria.add(Restrictions.eq("orderNoOrig", billingOrderNoOrig));
        // active by status, see CPSTransaction#isActive()
        criteria.add(Restrictions.or(Restrictions.eq(CPSTransactionExt.TX_STATE, CPSTransactionExt.TX_STATE_IN_PREPARING),
                Restrictions.eq(CPSTransactionExt.TX_STATE, CPSTransactionExt.TX_STATE_IN_PROVISIONING)));
        criteria.addOrder(Order.asc("id"));

        @SuppressWarnings("unchecked")
        final List<CPSTransactionExt> result = criteria.list();

        return result;
    }

    @Override
    public void deleteById(final Serializable id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(CPSDataChainConfig.class.getName());
        hql.append(" c where c.id=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameter(0, id);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


