/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2007 13:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.Report2ProdStatiDAO;
import de.augustakom.hurrican.model.reporting.Report2ProdStati;

/**
 * Hibernate DAO-Implementierung fuer <code>Report2ProdStatiDAO</code>
 *
 *
 */
public class Report2ProdStatiDAOImpl extends Hibernate4DAOImpl implements Report2ProdStatiDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteReport2ProdStati(final Long report2ProdId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(Report2ProdStati.class.getName());
        hql.append(" r where r.report2ProdId = ?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, report2ProdId);
        query.executeUpdate();
    }

    @Override
    public Report2ProdStati findReport2ProdStatiByIds(Long rep2ProdId, Long statusId) {
        Report2ProdStati example = new Report2ProdStati();
        example.setReport2ProdId(rep2ProdId);
        example.setStatusId(statusId);

        List<Report2ProdStati> result = queryByExample(example, Report2ProdStati.class);

        if ((result != null) && (result.size() == 1)) {
            return result.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
