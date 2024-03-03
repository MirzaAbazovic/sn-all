/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.05.2004 13:47:47
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKApplicationDAO;
import de.augustakom.authentication.model.AKApplication;


/**
 * Hibernate DAO-Implementierung von AKApplicationDAO. <br>
 *
 *
 */
public class AKApplicationDAOImpl implements AKApplicationDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.authentication.dao.AKApplicationDAO#findAll()
     */
    @SuppressWarnings("unchecked")
    public List<AKApplication> findAll() {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AKApplication.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.addOrder(Order.asc("id"));  // to locate 1=Hurrican application on the top
        return criteria.list();
    }

    /**
     * @see de.augustakom.authentication.dao.AKApplicationDAO#findApplicationByName(java.lang.String)
     */
    public AKApplication findApplicationByName(final String name) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AKApplication.class);
        criteria.add(Restrictions.eq("name", name));
        @SuppressWarnings("unchecked")
        List<AKApplication> result = criteria.list();
        return ((result != null) && (!result.isEmpty())) ? result.get(0) : null;
    }

}
