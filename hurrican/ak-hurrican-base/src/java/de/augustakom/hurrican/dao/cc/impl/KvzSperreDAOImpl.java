/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.07.2012 09:02:50
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.KvzSperreDAO;
import de.augustakom.hurrican.model.cc.KvzSperre;
import de.mnet.annotation.ObjectsAreNonnullByDefault;

/**
 *
 */
public class KvzSperreDAOImpl extends HurricanHibernateDaoImpl implements KvzSperreDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    @ObjectsAreNonnullByDefault
    public KvzSperre find(String onkz, Integer asb, String kvzNummer) {
        Query query = sessionFactory.getCurrentSession().createQuery("from " + KvzSperre.class.getName()
                + " where onkz = :onkz and asb = :asb and kvzNummer = :kvzNummer");
        query.setParameter("onkz", onkz);
        query.setParameter("asb", asb);
        query.setParameter("kvzNummer", kvzNummer);
        return (KvzSperre) query.uniqueResult();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void deleteById(Serializable id) {
        Session session = sessionFactory.getCurrentSession();
        KvzSperre toDelete = (KvzSperre) session.get(KvzSperre.class, id);
        session.delete(toDelete);
    }
}


