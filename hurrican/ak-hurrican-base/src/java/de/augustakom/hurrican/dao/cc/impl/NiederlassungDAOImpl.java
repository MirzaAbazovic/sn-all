/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2004 13:34:49
 */
package de.augustakom.hurrican.dao.cc.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.NiederlassungDAO;


/**
 * Hibernate DAO-Implementierung fuer Objekte des Typs <code>Niederlassung</code>
 *
 *
 */
public class NiederlassungDAOImpl extends Hibernate4DAOImpl implements NiederlassungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}


