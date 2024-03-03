/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2007 13:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.Report2UserRoleDAO;
import de.augustakom.hurrican.model.reporting.Report2UserRole;

/**
 * Hibernate DAO-Implementierung fuer <code>Report2UserRoleDAO</code>
 *
 *
 */
public class Report2UserRoleDAOImpl extends Hibernate4DAOImpl implements Report2UserRoleDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteRoles4Report(final Long reportId) {
        if (reportId == null) {
            return;
        }
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Report2UserRole.class.getName());
        hql.append(" r where r.reportId = ?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, reportId);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
