/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2010 12:02:37
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.GewofagDao;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.GewofagWohnung;


/**
 *
 */
public class GewofagDaoImpl extends Hibernate4DAOImpl implements GewofagDao {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public GewofagWohnung findGewofagWohnung(final Equipment equipment) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(GewofagWohnung.class.getName());
        hql.append(" g where g.");
        hql.append(GewofagWohnung.EQUIPMENT);
        hql.append(" = :equipment");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setEntity("equipment", equipment);

        GewofagWohnung result = (GewofagWohnung) query.uniqueResult();
        return result;
    }


    @Override
    public List<GewofagWohnung> findGewofagWohnungenByGeoId(final GeoId geoId) {
        final StringBuilder hql = new StringBuilder("from ");
        hql.append(GewofagWohnung.class.getName());
        hql.append(" g where g.");
        hql.append(GewofagWohnung.GEO_ID);
        hql.append(" = :geoId");

        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(hql.toString());
        query.setEntity("geoId", geoId);

        @SuppressWarnings("unchecked")
        List<GewofagWohnung> result = query.list();
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
