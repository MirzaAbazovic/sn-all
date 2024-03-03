/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2010 14:08:08
 */
package de.augustakom.hurrican.service.cc.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.annotation.CcTxRequiresNew;
import de.augustakom.hurrican.service.cc.DatabaseTestTools;


/**
 * Tools, die erweitert werden koennen, um irgendeinen Mist auf der Datenbank zu tun...
 */
public class DatabaseTestToolsImpl implements DatabaseTestTools {

    @Qualifier("cc.sessionFactory")
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @CcTxRequiresNew
    public void executeHqlAndAutoCommit(String hql) {
        Session session = sessionFactory.getCurrentSession();
        Query queryObject = session.createQuery(hql);
        queryObject.executeUpdate();
    }

    @Override
    @CcTxRequired
    public void delete(final Object object) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(object);
    }

    @Override
    @CcTxRequired
    public void save(final Object object) {
        Session session = sessionFactory.getCurrentSession();
        session.save(object);
    }
}
