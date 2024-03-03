/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 11:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.ReportDataDAO;

/**
 * Hibernate DAO-Implementierung fuer <code>ReportDataDAO</code>
 *
 *
 */
public class ReportDataDAOImpl extends Hibernate4DAOImpl implements ReportDataDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteData4Request(Long requestId) {
        String query = "delete from t_report_data where REQ_ID = :requestId";
        sessionFactory.getCurrentSession().createSQLQuery(query).setLong("requestId", requestId).executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
