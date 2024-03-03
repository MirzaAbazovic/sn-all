/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.01.2015
 */
package de.augustakom.common.tools.dao.hibernate;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import de.augustakom.common.tools.dao.iface.ByExampleDAO;

public class Hibernate4ByExampleDAOHelper implements ByExampleDAO {

    private SessionFactory sessionFactory;

    public Hibernate4ByExampleDAOHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type) {
        return queryByExample(example, type, false);
    }

    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type, boolean enableLike) {
        Example createdExample = example instanceof Example ? (Example) example : Example.create(example);
        if (enableLike) {
            createdExample.enableLike(MatchMode.ANYWHERE);
        }
        return queryByCreatedExample(createdExample, type);
    }

    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type, String[] ascOrder, String[] descOrder) {
        Example createdExample = example instanceof Example ? (Example) example : Example.create(example);
        return queryByCreatedExample(createdExample, type, ascOrder, descOrder);
    }

    @Override
    public <T, V extends T> List<T> queryByCreatedExample(final Criterion example, final Class<V> type) {
        return queryByCreatedExample(example, type, null, null);
    }

    private <T, V extends T> List<T> queryByCreatedExample(final Criterion example, final Class<V> type,
            String[] ascOrder, String[] descOrder) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(type);
        criteria.add(example);
        if (ascOrder != null) {
            for (String asc : ascOrder) {
                criteria.addOrder(Order.asc(asc));
            }
        }
        if (descOrder != null) {
            for (String desc : descOrder) {
                criteria.addOrder(Order.desc(desc));
            }
        }
        @SuppressWarnings("unchecked")
        List<T> result = criteria.list();
        return result;
    }
}
