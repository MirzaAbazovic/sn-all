/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.2004 08:18:35
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.HVTTechnikDAO;
import de.augustakom.hurrican.model.cc.HVTStandort2Technik;
import de.augustakom.hurrican.model.cc.HVTTechnik;


/**
 * Hibernate DAO-Implementierung von <code>HVTTechnikDAO</code>.
 *
 *
 */
public class HVTTechnikDAOImpl extends Hibernate4DAOImpl implements HVTTechnikDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<HVTTechnik> findByHVTStandort(final Long hvtIdStandort) {
        final StringBuilder hql = new StringBuilder("select h2t.hvtTechnikId from ");
        hql.append(HVTTechnik.class.getName()).append(" ht, ");
        hql.append(HVTStandort2Technik.class.getName()).append(" h2t ");
        hql.append(" where h2t.hvtIdStandort= :hvtIdStd and h2t.hvtTechnikId=ht.id");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("hvtIdStd", hvtIdStandort.longValue());

        List result = q.list();
        if (result != null) {
            List<HVTTechnik> retVal = new ArrayList<HVTTechnik>();
            for (Object o : result) {
                Long technikId = (Long) o;
                HVTTechnik ht = (HVTTechnik) session.get(HVTTechnik.class, technikId);
                retVal.add(ht);
            }
            return retVal;
        }
        return null;
    }

    @Override
    public void deleteHVTTechniken4Standort(final Long hvtIdStandort) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("delete from " + HVTStandort2Technik.class.getName() + " h2t where h2t.hvtIdStandort=?");
        query.setLong(0, hvtIdStandort);
        query.executeUpdate();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


