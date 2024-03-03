/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2004 11:57:10
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.SperreVerteilungDAO;
import de.augustakom.hurrican.model.cc.SperreVerteilung;


/**
 * Hibernate DAO-Implementierung von <code>SperreVerteilungDAO</code>.
 *
 *
 */
public class SperreVerteilungDAOImpl extends Hibernate4DAOImpl implements SperreVerteilungDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<SperreVerteilung> findByProduktId(Long prodId) {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(SperreVerteilung.class.getName());
        hql.append(" sv where sv.produktId=:produktId");

        Session session = getSessionFactory().getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setParameter("produktId", prodId);
        return (List<SperreVerteilung>) query.list();
    }

    @Override
    public void deleteVerteilungen4Produkt(final Long produktId) {
        Session session = getSessionFactory().getCurrentSession();
        Query query = session.createQuery("delete from " + SperreVerteilung.class.getName() + " sv where sv.produktId=:produktId");
        query.setParameter("produktId", produktId);
        query.executeUpdate();
    }

    @Override
    public void saveSperreVerteilungen(List<SperreVerteilung> toSave) {
        if (toSave != null) {
            for (SperreVerteilung sv : toSave) {
                store(sv);
            }
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


