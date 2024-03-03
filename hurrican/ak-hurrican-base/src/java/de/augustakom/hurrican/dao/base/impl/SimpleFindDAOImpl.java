/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.03.2007 08:07:18
 */
package de.augustakom.hurrican.dao.base.impl;

import org.hibernate.SessionFactory;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;

/**
 * Hibernate DAO-Implementierung fuer einfache Find-Abfragen.
 *
 *
 */
public class SimpleFindDAOImpl extends Hibernate4FindDAOImpl {

    /** Related session factory */
    protected SessionFactory sessionFactory;

    /**
     * Sets the session factory for this dao.
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


