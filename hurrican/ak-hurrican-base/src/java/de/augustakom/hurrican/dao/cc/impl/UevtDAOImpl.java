/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 11:42:34
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.StoreDAO;
import de.augustakom.hurrican.dao.cc.UevtDAO;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.UEVT;
import de.augustakom.hurrican.model.cc.view.UevtBuchtView;

/**
 * Hibernate DAO-Implementierung fuer Objekte des Typs <code>UEVT</code>
 *
 *
 */
public class UevtDAOImpl extends Hibernate4DAOImpl implements UevtDAO, FindAllDAO, StoreDAO, Serializable {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    private static final long serialVersionUID = 1L;

    @Override
    public List<UEVT> findByHVTStandortId(Long hvtStandort) {
        return find("from " + UEVT.class.getName() + " u where u.hvtIdStandort=?",
                hvtStandort);
    }

    @Override
    public <T> List<T> findAll(Class<T> type) {
        return find("from " + UEVT.class.getName() + " u order by u.hvtIdStandort asc");
    }

    @Override
    public List<UevtBuchtView> findUevtBuchtenForUevt(final Long hvtIdStandort, final String uevt) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Equipment.class);
        criteria.add(Restrictions.eq(Equipment.HVT_ID_STANDORT, hvtIdStandort));
        criteria.add(Restrictions.eq(Equipment.RANG_VERTEILER, uevt));
        criteria.add(Restrictions.isNotNull(Equipment.RANG_LEISTE1));
        criteria.add(Restrictions.isNotNull(Equipment.CARRIER));
        criteria.setProjection(Projections.distinct(Projections.property(Equipment.RANG_LEISTE1)));
        criteria.setResultTransformer(new ResultTransformer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object transformTuple(Object[] tuple, String[] aliases) {
                UevtBuchtView view = new UevtBuchtView();
                view.setBucht((String) tuple[0]);
                view.setHvtIdStandort(hvtIdStandort);
                view.setUevt(uevt);
                return view;
            }

            @Override
            public List transformList(List collection) {
                return collection;
            }
        });
        return criteria.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
