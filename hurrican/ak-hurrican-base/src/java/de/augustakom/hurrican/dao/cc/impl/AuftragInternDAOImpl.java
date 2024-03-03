/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 08:23:04
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.io.*;
import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.augustakom.hurrican.dao.cc.AuftragInternDAO;
import de.augustakom.hurrican.model.cc.AuftragIntern;


/**
 * Hibernate DAO-Implementierung von <code>AuftragInternDAO</code>.
 *
 *
 */
public class AuftragInternDAOImpl extends HurricanHibernateDaoImpl implements AuftragInternDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AuftragIntern findByAuftragId(final Long auftragId) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AuftragIntern.class.getName());
        hql.append(" ai where ai.auftragId= :aId and ai.gueltigVon<= :now  and ai.gueltigBis> :now");

        Session session = sessionFactory.getCurrentSession();
        Date now = new Date();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", auftragId.longValue());
        q.setDate("now", now);

        List result = q.list();
        if ((result != null) && (!result.isEmpty())) {
            if (result.size() == 1) {
                return (AuftragIntern) result.get(0);
            }
            else {
                throw new IncorrectResultSizeDataAccessException(
                        "Count of AuftragIntern not valid!", 1, result.size());
            }
        }

        return null;
    }

    @Override
    public AuftragIntern update4History(AuftragIntern obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new AuftragIntern(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


