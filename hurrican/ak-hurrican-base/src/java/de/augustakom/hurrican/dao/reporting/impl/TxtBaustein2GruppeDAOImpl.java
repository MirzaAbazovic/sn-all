/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.05.2007 10:44:38
 */
package de.augustakom.hurrican.dao.reporting.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.reporting.TxtBaustein2GruppeDAO;
import de.augustakom.hurrican.model.reporting.TxtBaustein2Gruppe;

/**
 * Hibernate DAO-Implementierung fuer <code>TxtBaustein2GruppeDAO</code>
 *
 *
 */
public class TxtBaustein2GruppeDAOImpl extends Hibernate4DAOImpl implements TxtBaustein2GruppeDAO {

    @Autowired
    @Qualifier("reporting.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void deleteByBausteinGruppeId(final Long bausteinGruppeId) {
        StringBuilder hql = new StringBuilder();
        hql.append("delete from ");
        hql.append(TxtBaustein2Gruppe.class.getName());
        hql.append(" r where r.txtBausteinGruppeId = ?");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setLong(0, bausteinGruppeId);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
