/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.11.2011 15:28:15
 */
package de.augustakom.hurrican.dao.exceptions.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.dao.exceptions.ExceptionLogEntryDAO;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;

@CcTxRequired
public class ExceptionLogEntryDAOImpl extends Hibernate4DAOImpl implements ExceptionLogEntryDAO, Serializable {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private static final long serialVersionUID = 6413554451153759854L;

    @Override
    public long getNewExceptionLogEntriesCount() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExceptionLogEntry.class);
        criteria.add(Restrictions.isNull(ExceptionLogEntry.SOLUTION_FIELD))
                .add(Restrictions.isNull(ExceptionLogEntry.BEARBEITER_FIELD));
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<ExceptionLogEntry> findNewExceptionLogEntries(ExceptionLogEntryContext context, int maxResults) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExceptionLogEntry.class);
        // @formatter:off
        criteria.add(Restrictions.eq(ExceptionLogEntry.CONTEXT, context.identifier))
                .add(Restrictions.isNull(ExceptionLogEntry.SOLUTION_FIELD))
                .add(Restrictions.isNull(ExceptionLogEntry.BEARBEITER_FIELD));
        criteria.setMaxResults(maxResults);
        criteria.addOrder(org.hibernate.criterion.Order.desc(ExceptionLogEntry.ID));
        // @formatter:on
        return criteria.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
