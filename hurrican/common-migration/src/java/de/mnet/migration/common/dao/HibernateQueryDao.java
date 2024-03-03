/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2010 16:41:24
 */
package de.mnet.migration.common.dao;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;


/**
 * Dao for find operations, can be constructed by the Hibernate Dao.
 */
public class HibernateQueryDao<T> extends HibernateDao {
    private Class<T> persistentClass;


    @SuppressWarnings("unchecked")
    protected HibernateQueryDao(SessionFactory sessionFactory) {
        if (this.getClass().equals(HibernateDao.class)) {
            throw new RuntimeException("This constructor can only be used if HibernateDao is subclassed.");
        }
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        this.sessionFactory = sessionFactory;
    }

    protected HibernateQueryDao(SessionFactory sessionFactory, Class<T> persistentClass) {
        this.persistentClass = persistentClass;
        this.sessionFactory = sessionFactory;
    }


    @SuppressWarnings("unchecked")
    public T findById(Serializable id) {
        return (T) getSession().get(getPersistentClass(), id);
    }

    @SuppressWarnings("unchecked")
    public List<T> findByProperty(String propertyName, Object value) {
        Criteria crit = createCriteria();
        crit.add(Restrictions.eq(propertyName, value));
        return crit.list();
    }

    public List<T> findAll() {
        return findByCriteria();
    }

    @SuppressWarnings("unchecked")
    public List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = createCriteria();
        for (Criterion c : criterion) {
            if (c != null) {
                crit.add(c);
            }
        }
        return crit.list();
    }

    @SuppressWarnings("unchecked")
    public List<T> findByExample(T exampleInstance, String... excludeProperty) {
        Example example = exampleInstance instanceof Example ? (Example) exampleInstance : Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        Criteria crit = createCriteria();
        crit.add(example);
        return crit.list();
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public Criteria createCriteria() {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        crit.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return crit;
    }
}
