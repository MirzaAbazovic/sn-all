/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2005 08:39:09
 */
package de.augustakom.common.tools.dao.hibernate;

import java.lang.reflect.*;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


/**
 * Abstrakte Implementierung eines AOP-Advice´s, um eine Hibernate-Session zu flushen.
 *
 *
 */
public abstract class AbstractFlushHibernateSessionAdvice {

    private static final Logger LOGGER = Logger.getLogger(AbstractFlushHibernateSessionAdvice.class);

    private static final String INFO_MSG = "Flushing Hibernate-Session from Advice...";

    private SessionFactory hibernateSessionFactory = null;

    /**
     * Ueber diese Methode wird auf der Hibernate-Session 'flush' aufgerufen.
     *
     * @param method
     * @param target
     * @throws Throwable
     */
    protected void flushHibernateSession(Method method, Object target) throws Throwable {
        if (getHibernateSessionFactory() != null) {
            Session session = getHibernateSessionFactory().getCurrentSession();
            if (session != null) {
                LOGGER.info(INFO_MSG);
                session.flush();
            }
        }
    }

    /**
     * @return Returns the hibernateSessionFactory.
     */
    public SessionFactory getHibernateSessionFactory() {
        return hibernateSessionFactory;
    }

    /**
     * @param hibernateSessionFactory The hibernateSessionFactory to set.
     */
    public void setHibernateSessionFactory(SessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

}


