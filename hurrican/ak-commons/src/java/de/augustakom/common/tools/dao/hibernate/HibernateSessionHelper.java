/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.07.2007 12:04:40
 */
package de.augustakom.common.tools.dao.hibernate;

import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.tools.collections.CollectionTools;
import de.mnet.common.service.locator.ServiceLocator;


/**
 * Hilfsklasse fuer die Arbeit mit Hibernate-Sessions.
 *
 *
 */
public class HibernateSessionHelper {

    private static final Logger LOGGER = Logger.getLogger(HibernateSessionHelper.class);

    interface SessionFactoryCallback {
        void doWithSessionFactory(SessionFactory sessionFactory);
    }

    /**
     * Ueber diese Methode wird auf der Hibernate-Session 'clear' aufgerufen.
     *
     * @param sf
     *
     */
    public static void clearSession(SessionFactory sf) {
        try {
            Session session = sf.getCurrentSession();
            if (session != null) {
                session.clear();
            }
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }

    /**
     * Ueber diese Methode wird auf der Hibernate-Session 'flush' aufgerufen.
     *
     * @param sf
     *
     */
    public static void flushSession(SessionFactory sf) {
        try {
            flushSessionLoud(sf);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }

    /**
     * @param sf
     * @throws HibernateException
     *
     */
    public static void flushSessionLoud(SessionFactory sf) throws HibernateException {
        Session session = sf.getCurrentSession();
        if (session != null) {
            session.flush();
        }
    }

    /**
     * Ueber diese Methode wird auf der Hibernate-Session 'clear' und 'flush' aufgerufen.
     *
     * @param sf
     *
     */
    public static void clearAndFlushSession(SessionFactory sf) {
        try {
            clearSession(sf);
            flushSession(sf);
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage());
            LOGGER.debug(e.getMessage(), e);
        }
    }

    /**
     * Ermittelt aus dem angegebenen ApplicationContext alle Hibernate-Sessions der aktuellen Transaktion (durch Springs
     * SessionFactoryUtils sichergestellt) und fuehrt darauf 'flush' und anschliessend 'clear' aus. <br> Durch die
     * Reihenfolge von flush/clear ist sichergestellt, dass evtl. offene Modell-Aenderungen auch wirklich persistiert
     * werden (natuerlich immer noch abhaengig von den vorhandenen Transactions).
     *
     * @param applicationContext
     */
    public static void flushAndClearAllSessions(ApplicationContext applicationContext) {
        if (applicationContext != null) {
            Map<String, LocalSessionFactoryBean> sfBeans = applicationContext.getBeansOfType(LocalSessionFactoryBean.class);
            if (sfBeans != null) {
                Collection<LocalSessionFactoryBean> sessionFactories = sfBeans.values();
                if (CollectionTools.isNotEmpty(sessionFactories)) {
                    for (LocalSessionFactoryBean sfTmp : sessionFactories) {
                        SessionFactory sf = sfTmp.getObject();
                        HibernateSessionHelper.flushSession(sf);
                        HibernateSessionHelper.clearSession(sf);
                    }
                }
            }
        }
    }

    /**
     * Entfernt das angegebene Objekt <code>toEvict</code> aus der Hibernate-Session.
     *
     * @param sl
     * @param toEvict
     */
    public static void evictObject(IServiceLocator sl, Object toEvict) {
        visitSessionFactories(sl, sf -> {
            try {
                Session session = sf.getCurrentSession();
                if (session != null) {
                    session.evict(toEvict);
                }
            }
            catch (Exception e) {
                LOGGER.warn(e.getMessage());
                LOGGER.debug(e.getMessage(), e);
            }
        });
    }

    /**
     * Entfernt das angegebene Objekt <code>toEvict</code> aus der Hibernate-Session.
     *
     * @param sl
     * @param toEvict
     */
    public static void evictObject(ServiceLocator sl, Object toEvict) {
        visitSessionFactories(sl, sf -> {
            try {
                Session session = sf.getCurrentSession();
                if (session != null) {
                    session.evict(toEvict);
                }
            }
            catch (Exception e) {
                LOGGER.warn(e.getMessage());
                LOGGER.debug(e.getMessage(), e);
            }
        });
    }

    /**
     * @param sl
     *
     */
    public static void clearAndFlushSession(IServiceLocator sl) {
        visitSessionFactories(sl, sf -> {
            HibernateSessionHelper.clearAndFlushSession(sf);
        });
    }

    /**
     * @param sl
     *
     */
    public static void clearAndFlushSession(ServiceLocator sl) {
        visitSessionFactories(sl, sf -> {
            HibernateSessionHelper.clearAndFlushSession(sf);
        });
    }

    /**
     * Ueber diese Methode wird auf der Hibernate-Session 'flush' aufgerufen.
     *
     * @param sl ServiceLocator
     *
     */
    public static void flushSession(IServiceLocator sl) {
        visitSessionFactories(sl, sf -> {
            HibernateSessionHelper.flushSession(sf);
        });
    }

    /**
     * Ueber diese Methode wird auf der Hibernate-Session 'flush' aufgerufen.
     *
     * @param sl ServiceLocator
     *
     */
    public static void flushSession(ServiceLocator sl) {
        visitSessionFactories(sl, sf -> {
            HibernateSessionHelper.flushSession(sf);
        });
    }

    /**
     * @param sl
     * @throws HibernateException
     *
     */
    public static void flushSessionLoud(IServiceLocator sl) throws HibernateException {
        visitSessionFactories(sl, sf -> {
            HibernateSessionHelper.flushSessionLoud(sf);
        });
    }

    /**
     * @param sl
     * @throws HibernateException
     *
     */
    public static void flushSessionLoud(ServiceLocator sl) throws HibernateException {
        visitSessionFactories(sl, sf -> {
            HibernateSessionHelper.flushSessionLoud(sf);
        });
    }

    private static void visitSessionFactories(ServiceLocator serviceLocator, SessionFactoryCallback callback) {
        Map<String, LocalSessionFactoryBean> sfBeans = serviceLocator.getBeansOfType(LocalSessionFactoryBean.class);
        if (sfBeans != null) {
            Collection<LocalSessionFactoryBean> sessions = sfBeans.values();
            if (CollectionTools.isNotEmpty(sessions)) {
                for (LocalSessionFactoryBean session : sessions) {
                    SessionFactory sf = session.getObject();
                    callback.doWithSessionFactory(sf);
                }
            }
        }
    }

    private static void visitSessionFactories(IServiceLocator serviceLocator, SessionFactoryCallback callback) {
        Map<String, LocalSessionFactoryBean> sfBeans = serviceLocator.getBeansOfType(LocalSessionFactoryBean.class);
        if (sfBeans != null) {
            Collection<LocalSessionFactoryBean> sessions = sfBeans.values();
            if (CollectionTools.isNotEmpty(sessions)) {
                for (LocalSessionFactoryBean session : sessions) {
                    SessionFactory sf = session.getObject();
                    callback.doWithSessionFactory(sf);
                }
            }
        }
    }

}


