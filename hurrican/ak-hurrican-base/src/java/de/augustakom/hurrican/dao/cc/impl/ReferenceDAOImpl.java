/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2006 08:36:58
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.dao.cc.ReferenceDAO;
import de.augustakom.hurrican.model.cc.Reference;


/**
 * Hibernate DAO-Implementierung von
 *
 *
 */
public class ReferenceDAOImpl extends Hibernate4DAOImpl implements ReferenceDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.cc.ReferenceDAO#findReferences(java.lang.String, java.lang.Boolean)
     */
    public List<Reference> findReferences(final String type, final Boolean onlyVisible) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Reference.class);
        criteria.add(Restrictions.eq("type", type));
        if (BooleanTools.nullToFalse(onlyVisible)) {
            criteria.add(Restrictions.eq("guiVisible", Boolean.TRUE));
        }
        criteria.addOrder(Order.asc("orderNo"));
         return criteria.list();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


