/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.10.2009 17:57:17
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.hurrican.dao.cc.BrasPoolDAO;
import de.augustakom.hurrican.model.cc.BrasPool;


/**
 * Hibernate DAO-Implementierung von {@link BrasPoolDAO}
 *
 *
 */
public class BrasPoolDAOImpl extends Hibernate4FindDAOImpl implements BrasPoolDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public List<BrasPool> findByNamePrefix(final String string) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(BrasPool.class);
        criteria.add(Restrictions.like(BrasPool.NAME, string + '%'));

        @SuppressWarnings("unchecked")
        List<BrasPool> result = criteria.list();
        return result;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
