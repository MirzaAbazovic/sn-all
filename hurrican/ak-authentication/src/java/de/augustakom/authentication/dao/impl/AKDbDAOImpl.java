/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.2005 11:52:04
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKDbDAO;
import de.augustakom.authentication.model.AKDb;


/**
 * Hibernate DAO-Implementierung von <code>AKDbDAO</code>.
 *
 *
 */
public class AKDbDAOImpl implements AKDbDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.authentication.dao.AKDbDAO#findAll()
     */
    public List<AKDb> findAll() {
        StringBuilder hql = new StringBuilder("from ");
        hql.append(AKDb.class.getName());
        hql.append(" db order by db.name");
        @SuppressWarnings("unchecked")
        List<AKDb> result = (List<AKDb>) sessionFactory.getCurrentSession().createQuery(hql.toString()).list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKDbDAO#saveOrUpdate(de.augustakom.authentication.model.AKDb)
     */
    public void saveOrUpdate(AKDb db) {
        sessionFactory.getCurrentSession().saveOrUpdate(db);
    }

    /**
     * @see de.augustakom.authentication.dao.AKDbDAO#delete(java.lang.Long)
     */
    public void delete(final Long id) {
        String hql = "delete from " + AKDb.class.getName() + " db where db.id=:id";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("id", id);
        query.executeUpdate();
    }

}


