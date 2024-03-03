/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2010 11:28:30
 */
package de.mnet.migration.common.dao;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


/**
 * Refinement of the General Dao Pattern described in the Hibernate documentation. General Dao can perform
 * type-independent tasks, and construct a special Dao for find operations.
 *
 *
 */
public class HibernateDao {
    protected SessionFactory sessionFactory;

    public <T> HibernateQueryDao<T> forClass(Class<T> persistentClass) {
        return new HibernateQueryDao<T>(sessionFactory, persistentClass);
    }

    /**
     * Performs a save or (if entity is already persistent) update
     *
     * @return The now persistent entity (not a new instance or copy!)
     */
    public <T> T persist(T entity) {
        getSession().saveOrUpdate(entity);
        return entity;
    }

    /**
     * Performs a persist of a transient entity (not already stored in the database!)
     *
     * @return The now persistent entity (not a new instance or copy!)
     */
    public <T> T persistTransient(T entity) {
        getSession().persist(entity);
        return entity;
    }

    /**
     * Performs a refresh
     *
     * @return The now refreshed entity (not a new instance or copy!)
     */
    public <T> T refresh(T entity) {
        getSession().refresh(entity);
        return entity;
    }

    /**
     * Removes the entity from the hibernate session store
     *
     * @return The now transient entity (not a new instance or copy!)
     */
    public <T> T makeTransient(T entity) {
        getSession().evict(entity);
        return entity;
    }

    /**
     * Removes the entity from data store
     *
     * @return The now transient entity
     */
    public <T> T delete(T entity) {
        getSession().delete(entity);
        return entity;
    }

    /**
     * @see {@link Session#flush()}
     */
    public void flush() {
        getSession().flush();
    }

    /**
     * @see {@link Session#clear()}
     */
    public void clear() {
        getSession().clear();
    }

    /**
     * <em>Vorsichtig einsetzen!</em> Erstellt ein SQL Query Objekt fuer die gegebene Query und liefert es zurueck.
     */
    public SQLQuery createSqlQuery(String sql) {
        return getSession().createSQLQuery(sql);
    }


    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    /**
     * Injected by Spring
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
