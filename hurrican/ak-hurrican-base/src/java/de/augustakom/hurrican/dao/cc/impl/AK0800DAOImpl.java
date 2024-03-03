/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2004 09:13:48
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.AK0800DAO;
import de.augustakom.hurrican.model.cc.AK0800;


/**
 * Hibernate DAO-Implementierung von <code>AK0800DAO</code>.
 *
 *
 */
public class AK0800DAOImpl extends HurricanHibernateDaoImpl implements AK0800DAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<AK0800> findByAuftragId(final Long auftragId) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(AK0800.class.getName());
        hql.append(" ak where ak.auftragId= :aId and ak.gueltigVon<= :now and ak.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", auftragId.longValue());
        q.setDate("now", now);

        return q.list();
    }

    @Override
    public AK0800 update4History(AK0800 obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new AK0800(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


