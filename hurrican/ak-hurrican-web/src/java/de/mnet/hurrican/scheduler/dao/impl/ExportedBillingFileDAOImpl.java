/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2006 16:22:18
 */
package de.mnet.hurrican.scheduler.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.mnet.hurrican.scheduler.dao.ExportedBillingFileDAO;

/**
 * Hibernate DAO-Implementierung von <code>ExportedBillingFileDAO</code>.
 *
 *
 */
public class ExportedBillingFileDAOImpl extends Hibernate4DAOImpl implements ExportedBillingFileDAO {
    // marker interface

    @Autowired
    @Qualifier("scheduler.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
