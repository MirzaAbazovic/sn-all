/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.02.2005 11:32:18
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.billing.WebgatePWDAO;
import de.augustakom.hurrican.model.billing.WebgatePW;


/**
 * Hibernate DAO-Implementierung von <code>WebgatePWDAO</code>.
 *
 *
 */
public class WebgatePWDAOImpl extends Hibernate4DAOImpl implements WebgatePWDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.billing.WebgatePWDAO#findFirstByKundeNo(java.lang.Long)
     */
    public WebgatePW findFirstByKundeNo(Long kundeNo) {
        if (kundeNo == null) {
            return null;
        }

        StringBuilder hql = new StringBuilder("from " + WebgatePW.class.getName() + " w");
        hql.append(" where w.kundeNo = ? ");
        hql.append(" order by w.pwNo");

        List<Object> params = new ArrayList<>();
        params.add(kundeNo);

        List result = find(hql.toString(), params.toArray());

        return (!result.isEmpty()) ? (WebgatePW) result.get(0) : null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


