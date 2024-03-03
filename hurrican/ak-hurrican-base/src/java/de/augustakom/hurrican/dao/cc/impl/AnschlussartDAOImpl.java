/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2004 08:43:15
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateObjectRetrievalFailureException;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.model.cc.Anschlussart;


/**
 * Hibernate DAO-Implementierung, um Objekte des Typs <code>Anschlussart</code> zu verwalten.
 *
 *
 */
public class AnschlussartDAOImpl extends Hibernate4DAOImpl {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private static final Logger LOGGER = Logger.getLogger(AnschlussartDAOImpl.class);

    @Override
    public <T extends Serializable> T store(T toStore) {
        if (toStore instanceof Anschlussart) {
            Anschlussart anschlussart = (Anschlussart) toStore;
            if (anschlussart.getId() != null) {
                Object existing = null;
                try {
                    existing = sessionFactory.getCurrentSession().get(Anschlussart.class, anschlussart.getId());
                }
                catch (HibernateObjectRetrievalFailureException e) {
                    LOGGER.trace("store() - no object found");
                }

                if (existing == null) {
                    sessionFactory.getCurrentSession().save(toStore);
                }
                else {
                    sessionFactory.getCurrentSession().evict(existing); // geladenes Objekt muss aus Session entfernt werden!
                    sessionFactory.getCurrentSession().update(toStore);
                }
            }
            else {
                super.store(toStore);
            }
        }
        else {
            super.store(toStore);
        }
        return toStore;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


