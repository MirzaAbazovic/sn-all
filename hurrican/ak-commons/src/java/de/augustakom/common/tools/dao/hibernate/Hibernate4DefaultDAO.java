/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.01.2015
 */
package de.augustakom.common.tools.dao.hibernate;

import org.hibernate.SessionFactory;

/**
 * Created by maherma on 28.01.2015.
 */
public class Hibernate4DefaultDAO extends Hibernate4FindDAOImpl {

    /** Session factory to use */
    private SessionFactory sessionFactory;

    public void store(Object toStore) {
        getSession().saveOrUpdate(toStore);
    }

    @Override
    protected SessionFactory getSessionFactory() {
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
