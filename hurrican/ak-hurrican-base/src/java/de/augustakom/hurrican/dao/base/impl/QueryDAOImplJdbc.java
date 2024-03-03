/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2005 13:50:27
 */
package de.augustakom.hurrican.dao.base.impl;

import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.base.iface.QueryDAO;


/**
 * Allgmeine DAO-Klasse fuer beliebige SQL-Queries auf der CC-Datenbank
 *
 *
 */
public class QueryDAOImplJdbc extends Hibernate4DAOImpl implements QueryDAO {

    private static final Logger LOGGER = Logger.getLogger(QueryDAOImplJdbc.class);
    /**
     * Session factory to use
     */
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.base.iface.QueryDAO#query(java.lang.String, java.lang.Object[])
     */
    public List<Object[]> query(String sql, Object[] parameters) {
        LOGGER.info("SQL: " + sql);

        Session session = getSession();
        SQLQuery query = session.createSQLQuery(sql);
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i, parameters[i]);
            }
        }
        return query.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Sets the session factory for this dao.
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}


