/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.09.2004 07:58:12
 */
package de.augustakom.hurrican.dao.billing.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.hurrican.dao.billing.PersonDAO;


/**
 * Hibernate DAO-Implementierung von <code>PersonDAO</code>.
 *
 *
 */
public class PersonDAOImpl extends Hibernate4FindDAOImpl implements PersonDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


