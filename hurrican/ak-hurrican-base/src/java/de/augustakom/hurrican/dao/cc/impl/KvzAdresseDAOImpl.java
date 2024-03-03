/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.07.2012 09:02:50
 */
package de.augustakom.hurrican.dao.cc.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.KvzAdresseDAO;
import de.augustakom.hurrican.model.cc.KvzAdresse;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 *
 */
@ObjectsAreNonnullByDefault
public class KvzAdresseDAOImpl extends HurricanHibernateDaoImpl implements KvzAdresseDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public void delete(KvzAdresse kvzAdresse) {
        sessionFactory.getCurrentSession().delete(kvzAdresse);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


