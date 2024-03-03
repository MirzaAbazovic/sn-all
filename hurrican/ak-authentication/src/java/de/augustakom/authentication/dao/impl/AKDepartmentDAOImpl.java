/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 15:33:32
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKDepartmentDAO;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKExtServiceProviderView;
import de.augustakom.authentication.model.AKNiederlassungView;
import de.augustakom.common.tools.dao.hibernate.Hibernate4DAOImpl;


/**
 * Hibernate-Implementierung von AKDepartmentDAO.
 *
 *
 */
public class AKDepartmentDAOImpl extends Hibernate4DAOImpl implements AKDepartmentDAO {
    
    @Autowired
    @Qualifier("authentication.sessionFactory")
    private SessionFactory sessionFactory;
    
    /**
     * @see de.augustakom.authentication.dao.AKDepartmentDAO#findAll()
     */
    @Override
    public List<AKDepartment> findAll() {
        Criteria criteria = getSession().createCriteria(AKDepartment.class);
        criteria.addOrder(Order.asc("name"));
        @SuppressWarnings("unchecked")
        List<AKDepartment> result = criteria.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKDepartmentDAO#findById(java.lang.Long)
     */
    @Override
    public AKDepartment findById(final Long id) {
        StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AKDepartment.class.getName());
        hql.append(" a where a.id=(:id)");

        Query query = getSession().createQuery(hql.toString());
        query.setParameter("id", id);

        @SuppressWarnings("unchecked")
        List<AKDepartment> result = query.list();

        AKDepartment department = ((result.size() == 1) ? result.get(0) : null);
        return department;
    }

    /**
     * @see de.augustakom.authentication.dao.AKDepartmentDAO#findAllNiederlassungen()
     */
    @Override
    public List<AKNiederlassungView> findAllNiederlassungen() {
        Criteria criteria = getSession().createCriteria(AKNiederlassungView.class);
        criteria.addOrder(Order.asc("name"));
        @SuppressWarnings("unchecked")
        List<AKNiederlassungView> result = criteria.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKDepartmentDAO#findNiederlassungById(java.lang.Long)
     */
    @Override
    public AKNiederlassungView findNiederlassungById(final Long id) {
        StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AKNiederlassungView.class.getName());
        hql.append(" a where a.id=(:id)");

        Query query = getSession().createQuery(hql.toString());
        query.setParameter("id", id);

        @SuppressWarnings("unchecked")
        List<AKNiederlassungView> result = query.list();

        AKNiederlassungView nl = ((result.size() == 1) ? result.get(0) : null);
        return nl;
    }

    /**
     * @see de.augustakom.authentication.dao.AKDepartmentDAO#findAllExtServiceProvider()
     */
    @Override
    public List<AKExtServiceProviderView> findAllExtServiceProvider() {
        Criteria criteria = getSession().createCriteria(AKExtServiceProviderView.class);
        criteria.addOrder(Order.asc("name"));
        @SuppressWarnings("unchecked")
        List<AKExtServiceProviderView> result = criteria.list();
        return result;
    }

    /**
     * @see de.augustakom.authentication.dao.AKDepartmentDAO#findExtServiceProviderById(java.lang.Long)
     */
    @Override
    public AKExtServiceProviderView findExtServiceProviderById(final Long id) {
        StringBuilder hql = new StringBuilder();
        hql.append("from ");
        hql.append(AKExtServiceProviderView.class.getName());
        hql.append(" a where a.id=(:id)");

        Query query = getSession().createQuery(hql.toString());
        query.setParameter("id", id);

        @SuppressWarnings("unchecked")
        List<AKExtServiceProviderView> result = query.list();

        AKExtServiceProviderView sp = ((result.size() == 1) ? result.get(0) : null);
        return sp;
    }

    @Override
    public void saveOrUpdate(AKDepartment department) {
        sessionFactory.getCurrentSession().saveOrUpdate(department);
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
