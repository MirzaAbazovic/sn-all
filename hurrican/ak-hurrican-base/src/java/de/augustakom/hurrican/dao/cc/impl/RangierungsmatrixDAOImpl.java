/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.11.2004 12:28:07
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.dao.cc.RangierungsmatrixDAO;
import de.augustakom.hurrican.model.cc.Rangierungsmatrix;
import de.augustakom.hurrican.model.cc.query.RangierungsmatrixQuery;


/**
 * Hibernate DAO-Implementierung von <code>RangierungsmatrixDAO</code>.
 *
 *
 */
public class RangierungsmatrixDAOImpl extends HurricanHibernateDaoImpl implements RangierungsmatrixDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<Rangierungsmatrix> findByQuery(final RangierungsmatrixQuery query) {
        if ((query == null) || query.isEmpty()) {
            return null;
        }

        final StringBuilder sql = new StringBuilder("select rm.* from ");
        sql.append(" T_RANGIERUNGSMATRIX rm ");
        sql.append(" inner join T_PRODUKT_2_PHYSIKTYP p2pt on rm.PRODUKT2PHYSIKTYP_ID=p2pt.ID ");
        if (query.getHvtIdStandort() != null) {
            sql.append(" inner join T_UEVT u on rm.UEVT_ID=u.UEVT_ID ");
            sql.append(" inner join T_HVT_STANDORT h on u.HVT_ID_STANDORT=h.HVT_ID_STANDORT ");
            sql.append(" where h.HVT_ID_STANDORT = :hId and ");
        }
        else {
            sql.append(" where ");
        }
        sql.append(" rm.GUELTIG_VON <= :now and rm.GUELTIG_BIS > :now ");

        if (query.getUevtId() != null) {
            sql.append(" and rm.UEVT_ID = :uId ");
        }
        if (query.getProduktId() != null) {
            sql.append(" and rm.PROD_ID = :pId ");
        }
        if ((query.getProdukt2PhysikTypIds() != null) && (!query.getProdukt2PhysikTypIds().isEmpty())) {
            sql.append(" and rm.PRODUKT2PHYSIKTYP_ID in (:p2pIds) ");
        }
        sql.append(" order by rm.PRIORITY desc NULLS LAST, p2pt.PRIORITY desc NULLS LAST ");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createSQLQuery(sql.toString()).addEntity("rm", Rangierungsmatrix.class);
        if (query.getHvtIdStandort() != null) {
            q.setLong("hId", query.getHvtIdStandort());
        }
        if (query.getUevtId() != null) {
            q.setLong("uId", query.getUevtId());
        }
        if (query.getProduktId() != null) {
            q.setLong("pId", query.getProduktId());
        }
        if ((query.getProdukt2PhysikTypIds() != null) && (!query.getProdukt2PhysikTypIds().isEmpty())) {
            q.setParameterList("p2pIds", query.getProdukt2PhysikTypIds(), StandardBasicTypes.LONG);
        }
        q.setDate("now", now);

        @SuppressWarnings("unchecked")
        List<Rangierungsmatrix> result = q.list();
        if ((result != null) && (!result.isEmpty())) {
            return result;
        }

        return null;
    }

    @Override
    public Rangierungsmatrix update4History(Rangierungsmatrix obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new Rangierungsmatrix(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


