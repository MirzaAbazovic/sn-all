/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2009 11:43:17
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.EQCrossConnectionDAO;
import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.EQCrossConnection;


/**
 * Hibernate DAO-Implementierung von {@link EQCrossConnection}
 *
 *
 *
 */
public class EQCrossConnectionDAOImpl extends Hibernate4DAOImpl implements EQCrossConnectionDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<EQCrossConnection> findEQCrossConnections(final Long equipmentId, final Date validDate) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(EQCrossConnection.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, EQCrossConnection.EQUIPMENT_ID, equipmentId);
        criteria.add(Restrictions.le(EQCrossConnection.GUELTIG_VON, validDate));
        criteria.add(Restrictions.ge(EQCrossConnection.GUELTIG_BIS, validDate));
        @SuppressWarnings("unchecked")
        List<EQCrossConnection> list = criteria.list();
        return list;
    }

    @Override
    public List<EQCrossConnection> findEQCrossConnections(final Long equipmentId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(EQCrossConnection.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, EQCrossConnection.EQUIPMENT_ID, equipmentId);
        @SuppressWarnings("unchecked")
        List<EQCrossConnection> list = criteria.list();
        return list;
    }

    @Override
    public List<Integer> findUsedBrasVcs(BrasPool brasPool) {
        return findUsedBrasVcs(brasPool, null, null);
    }

    @Override
    public List<Integer> findUsedBrasVcs(final BrasPool brasPool, final Date from, final Date till) {
        final Date fromDate;
        final Date tillDate;

        if (from == null) {
            fromDate = new Date();
        } else {
            fromDate = from;
        }

        if (till == null) {
            tillDate = DateTools.getHurricanEndDate();
        } else {
            tillDate = till;
        }

        if (fromDate.after(tillDate)) {
            throw new IllegalArgumentException("From (" + from.toString() + ") is after till (" + till.toString() + ")");
        }

        final StringBuilder stringBuilder = new StringBuilder("select cc.");
        stringBuilder.append(EQCrossConnection.BRAS_INNER);
        stringBuilder.append(" from ");
        stringBuilder.append(EQCrossConnection.class.getName());
        stringBuilder.append(" cc where cc.");
        stringBuilder.append(EQCrossConnection.BRAS_POOL_ID);
        stringBuilder.append(" = :pool and cc.");
        stringBuilder.append(EQCrossConnection.BRAS_INNER);
        stringBuilder.append(" is not null and (");
        stringBuilder.append(" cc.");
        stringBuilder.append(EQCrossConnection.GUELTIG_VON);
        stringBuilder.append(" between :from and :till or cc.");
        stringBuilder.append(EQCrossConnection.GUELTIG_BIS);
        stringBuilder.append(" between :from and :till or ");
        stringBuilder.append(" :from between cc.");
        stringBuilder.append(EQCrossConnection.GUELTIG_VON);
        stringBuilder.append(" and cc.");
        stringBuilder.append(EQCrossConnection.GUELTIG_BIS);
        stringBuilder.append(" or :till between cc.");
        stringBuilder.append(EQCrossConnection.GUELTIG_VON);
        stringBuilder.append(" and cc.");
        stringBuilder.append(EQCrossConnection.GUELTIG_BIS);
        stringBuilder.append(") order by cc.");
        stringBuilder.append(EQCrossConnection.BRAS_INNER);
        stringBuilder.append(" asc");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(stringBuilder.toString());
        query.setLong("pool", brasPool.getId());
        query.setDate("from", fromDate);
        query.setDate("till", tillDate);

        @SuppressWarnings("unchecked")
        List<Integer> result = query.list();
        return result;
    }

    @Override
    public void deleteEQCrossConnectionsOfEquipment(final Long equipmentId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(EQCrossConnection.class.getName());
        hql.append(" e where e.equipmentId=?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, equipmentId);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
