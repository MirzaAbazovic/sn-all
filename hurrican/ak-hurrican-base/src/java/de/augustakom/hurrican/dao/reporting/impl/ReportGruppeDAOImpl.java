/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2007 09:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import java.io.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.ReportGruppeDAO;
import de.augustakom.hurrican.model.reporting.ReportGruppe;

/**
 * Hibernate DAO-Implementierung fuer <code>ReportGruppeDAO</code>
 *
 *
 */
public class ReportGruppeDAOImpl extends Hibernate4DAOImpl implements ReportGruppeDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteById(final Serializable id) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(ReportGruppe.class.getName());
        hql.append(" g where g.id = ?");

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

