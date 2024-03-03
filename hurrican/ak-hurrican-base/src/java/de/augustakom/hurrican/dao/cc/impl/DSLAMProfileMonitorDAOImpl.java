/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2012 12:55:32
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.common.tools.dao.hibernate.CriteriaHelper;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.DSLAMProfileMonitorDAO;
import de.augustakom.hurrican.model.cc.DSLAMProfileMonitor;

/**
 * Oracle Implementierung von {@link DSLAMProfileMonitorDAO}.
 *
 *
 * @since Release 11
 */
public class DSLAMProfileMonitorDAOImpl extends Hibernate4DAOImpl implements DSLAMProfileMonitorDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public DSLAMProfileMonitor findByAuftragId(Long auftragId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DSLAMProfileMonitor.class);
        CriteriaHelper.addExpression(criteria, CriteriaHelper.EQUAL, DSLAMProfileMonitor.AUFTRAG_ID, auftragId);
        @SuppressWarnings("unchecked")
        List<DSLAMProfileMonitor> result = criteria.list();

        if (result != null) {
            if (result.size() == 1) {
                return result.get(0);
            }
            else if (result.size() > 1) {
                final String errorMsg = String.format("Zu viele Ueberwachungen für Auftragsnummer = %s gefunden!",
                        auftragId);
                throw new IncorrectResultSizeDataAccessException(errorMsg, 1, result.size());
            }
        }
        return null;
    }

    @Override
    public Collection<Long> findCurrentlyMonitoredAuftragIds() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DSLAMProfileMonitor.class);
        criteria.setProjection(Projections.distinct(Projections.property(DSLAMProfileMonitor.AUFTRAG_ID)));
        criteria.add(Restrictions.eq(DSLAMProfileMonitor.DELETED, Boolean.FALSE));
        criteria.add(Restrictions.ge(DSLAMProfileMonitor.MONITORING_ENDS, new Date()));
        @SuppressWarnings("unchecked")
        Collection<Long> result = criteria.list();
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
