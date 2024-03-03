/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.2005 10:45:16
 */
package de.mnet.hurrican.scheduler.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.mnet.hurrican.scheduler.dao.JobErrorDAO;

/**
 * Hibernate DAO-Implementierung von <code>JobErrorDAO</code>.
 *
 *
 */
public class JobErrorDAOImpl extends Hibernate4DAOImpl implements JobErrorDAO {
    // marker interface

    @Autowired
    @Qualifier("scheduler.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
