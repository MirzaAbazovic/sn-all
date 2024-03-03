/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2010 14:26:28
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

import de.augustakom.hurrican.dao.cc.AuftragUMTSDAO;
import de.augustakom.hurrican.model.cc.AuftragUMTS;


/**
 *
 */
public class AuftragUMTSDAOImpl extends HurricanHibernateDaoImpl implements AuftragUMTSDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public AuftragUMTS findAuftragUMTS(final Long auftragId) throws IncorrectResultSizeDataAccessException {
        final StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AuftragUMTS.class.getName());
        hql.append(" au where au.auftragId= :aId and trunc(au.gueltigBis) > :now");

        Session session = sessionFactory.getCurrentSession();
        Query q = session.createQuery(hql.toString());
        q.setLong("aId", auftragId.longValue());
        q.setDate("now", new Date());

        @SuppressWarnings("unchecked")
        List<AuftragUMTS> result = q.list();
        if ((result == null) || (result.isEmpty())) {
            return null;
        }
        if (result.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, result.size());
        }
        return result.get(0);
    }

    @Override
    public AuftragUMTS update4History(AuftragUMTS obj4History, Serializable id, Date gueltigBis) {
        return update4History(obj4History, new AuftragUMTS(), id, gueltigBis);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
