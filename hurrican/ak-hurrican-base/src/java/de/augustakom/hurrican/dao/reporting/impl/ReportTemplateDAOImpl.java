/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2007 11:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.reporting.ReportTemplateDAO;
import de.augustakom.hurrican.model.reporting.ReportTemplate;

/**
 * Hibernate DAO-Implementierung fuer <code>ReportTemplateDAO</code>
 *
 *
 */
public class ReportTemplateDAOImpl extends Hibernate4DAOImpl implements ReportTemplateDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public ReportTemplate findTemplate4Report(final Long reportId) {
        if (reportId == null) {
            return null;
        }

        final StringBuilder hql = new StringBuilder("from ");
        hql.append(ReportTemplate.class.getName());
        hql.append(" r where r.reportId = :Id ");
        hql.append(" and r.gueltigVon <= :Date ");
        hql.append(" and r.gueltigBis >= :Date");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("Id", reportId);
        q.setTimestamp("Date", DateTools.getActualSQLDate());

        if (q.list().size() == 1) {
            return (ReportTemplate) q.list().get(0);
        }

        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
