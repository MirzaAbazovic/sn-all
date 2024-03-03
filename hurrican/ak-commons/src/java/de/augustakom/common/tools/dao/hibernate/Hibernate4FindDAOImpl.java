/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.01.2015
 */
package de.augustakom.common.tools.dao.hibernate;

import java.io.*;
import java.util.*;
import org.apache.commons.collections.MapUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import de.augustakom.common.tools.dao.FilterByProperty;
import de.augustakom.common.tools.dao.OrderByProperty;
import de.augustakom.common.tools.dao.iface.ByExampleDAO;
import de.augustakom.common.tools.dao.iface.FindAllDAO;
import de.augustakom.common.tools.dao.iface.FindDAO;

public abstract class Hibernate4FindDAOImpl implements FindDAO, FindAllDAO, ByExampleDAO {

    abstract protected SessionFactory getSessionFactory();

    private Hibernate4ByExampleDAOHelper hibernate4ByExampleDAOHelper;

    protected Session getSession() {
        return getSessionFactory().getCurrentSession();
    }

    protected ByExampleDAO getByExampleDAO() {
        if (hibernate4ByExampleDAOHelper == null) {
            hibernate4ByExampleDAOHelper = new Hibernate4ByExampleDAOHelper(getSessionFactory());
        }
        return hibernate4ByExampleDAOHelper;
    }
    
    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type) {
        return getByExampleDAO().queryByExample(example, type);
    }

    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type, boolean enableLike) {
        return getByExampleDAO().queryByExample(example, type, enableLike);
    }

    @Override
    public <T> List<T> queryByExample(Object example, Class<T> type, String[] orderParamsAsc, String[] orderParamsDesc) {
        return getByExampleDAO().queryByExample(example, type, orderParamsAsc, orderParamsDesc);
    }

    @Override
    public <T, V extends T> List<T> queryByCreatedExample(Criterion example, Class<V> type) {
        return getByExampleDAO().queryByCreatedExample(example, type);
    }

    @Override
    public <T> List<T> find(String hqlQuery, Integer maxResultSize, Object ... parameters) {
        Session session = getSession();
        Query query = session.createQuery(hqlQuery);
        if (maxResultSize != null) {
            query.setMaxResults(maxResultSize);
        }

        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i, parameters[i]);
            }
        }

        return query.list();
    }


    @Override
    public <T> List<T> find(String hqlQuery, Object ... parameters) {
        return find(hqlQuery, null, parameters);
    }

    @Override
    public <T> List<T> find(int maxResult, String hqlQuery, Object... parameters) {
        Session session = getSession();
        Query query = session.createQuery(hqlQuery);

        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i, parameters[i]);
            }
        }

        if (maxResult > 0) {
            query.setMaxResults(maxResult);
        }

        return query.list();
    }

    @Override
    public <T> List<T> find(int maxResult, String hqlQuery, Map<String, Object> params) {
        final Session session = getSession();
        final Query query = session.createQuery(hqlQuery);
        if (MapUtils.isNotEmpty(params)) {
            query.setProperties(params);
        }
        if (maxResult > 0) {
            query.setMaxResults(maxResult);
        }
        return query.list();
    }

    @Override
    public <T> List<T> findAll(Class<T> type) {
        Criteria criteria = getSession().createCriteria(type);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    public <T> List<T> findAllOrdered(Class<T> type, OrderByProperty... orderByProperties) {
        return findFilteredAndOrdered(type, null, orderByProperties);
    }

    @Override
    public <T> T findById(Serializable id, Class<T> type) {
        return (T)getSession().get(type, id);
    }

    @Override
    public <T> List<T> findFilteredAndOrdered(Class<T> type, List<FilterByProperty> filterByProperties, OrderByProperty... orderByProperties) {
        Criteria criteria = getSession().createCriteria(type);
        if (filterByProperties != null) {
            for (FilterByProperty filterByProperty : filterByProperties) {
                CriteriaHelper.addExpression(criteria,
                        CriteriaHelper.EQUAL,
                        filterByProperty.getPropertyName(),
                        filterByProperty.getFilterValue());
            }
        }

        if (orderByProperties != null) {
            for (OrderByProperty orderByProperty : orderByProperties) {
                CriteriaHelper.addOrder(criteria, orderByProperty);
            }
        }
        @SuppressWarnings("unchecked")
        List<T> result = criteria.list();
        return result;
    }

    @Override
    public <T> List<T> findByProperty(Class<T> type, String property, Object value) {
        Criteria criteria = getSession().createCriteria(type);
        if (value != null) {
            criteria.add(Restrictions.eq(property, value));
        }
        else {
            criteria.add(Restrictions.isNull(property));
        }
        return findByCriteria(criteria);
    }

    private <T> List<T> findByCriteria(Criteria criteria) {
        @SuppressWarnings("unchecked")
        List<T> result = criteria.list();
        return result;
    }

    public void flushSession() {
        HibernateSessionHelper.flushSession(getSessionFactory());
    }

    @Override
    public void refresh(Object entity) {
        getSession().refresh(entity);
    }

}
