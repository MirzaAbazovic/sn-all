/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2004 10:43:59
 */
package de.augustakom.hurrican.dao.cc.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;
import de.augustakom.hurrican.dao.cc.CounterDAO;
import de.augustakom.hurrican.model.cc.Counter;


/**
 * Hibernate DAO-Implementierung von <code>CounterDAO</code>.
 *
 *
 */
public class CounterDAOImpl extends Hibernate4DAOImpl implements CounterDAO {

    @Autowired
    @Qualifier("cc.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.hurrican.dao.cc.CounterDAO#incrementIntValue(java.lang.String)
     */
    public Integer incrementIntValue(String counterName) {
        Counter counter = (Counter) sessionFactory.getCurrentSession().get(Counter.class, counterName);
        if (counter != null) {
            if (counter.getIntValue() != null) {
                int value = counter.getIntValue().intValue();
                counter.setIntValue(Integer.valueOf(++value));
            }
            else {
                counter.setIntValue(Integer.valueOf(1));
            }
            sessionFactory.getCurrentSession().update(counter);
            return counter.getIntValue();
        }

        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


