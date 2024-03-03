/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.08.2010 13:33:38
 */

package de.mnet.migration.common.dao.impl;

import java.util.*;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.util.ReflectHelper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import de.mnet.migration.common.dao.DataLoader;
import de.mnet.migration.common.dao.HibernateDao;
import de.mnet.migration.common.dao.HibernateQueryDao;


public class HibernateDataLoader<TYPE> implements DataLoader<TYPE>, TransactionCallback<List<TYPE>> {
    private static final Logger LOGGER = Logger.getLogger(HibernateDataLoader.class);

    /**
     * The name of the Hibernate entity that should be loaded e.g. de.mnet.migration.taifun.model.auftrag.AuftragMapping
     */
    private String entityName;
    /**
     * Wrapper of the Hibernate session factory that is used for loading
     */
    private HibernateDao hibernateDao;
    /**
     * Used transaction manager
     */
    private PlatformTransactionManager txManager;

    private String filterColumn;
    private String filterValue;

    private String excludeColumn;
    private String excludeValue;


    @Override
    public List<TYPE> getSourceData() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        return txTemplate.execute(this);
    }

    @Override
    public List<TYPE> doInTransaction(TransactionStatus status) {
        try {
            @SuppressWarnings("unchecked")
            Class<TYPE> clazz = (Class<TYPE>) Class.forName(entityName);
            HibernateQueryDao<TYPE> hibernateQueryDao = hibernateDao.forClass(clazz);
            Criteria criteria = hibernateQueryDao.createCriteria();
            criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            if (filterColumn != null) {
                LOGGER.info("doInTransaction() - add filter for " + filterColumn + "=" + filterValue);
                if (filterValue != null) {
                    Object filterObject = castToColumnType(clazz, filterColumn, filterValue);
                    criteria.add(Restrictions.eq(filterColumn, filterObject));
                }
                else {
                    criteria.add(Restrictions.isNull(filterColumn));
                }
            }
            if (excludeColumn != null) {
                LOGGER.info("doInTransaction() - exclude with filter " + excludeColumn + "=" + excludeValue);
                if (excludeValue != null) {
                    Object excludeObject = castToColumnType(clazz, excludeColumn, excludeValue);
                    criteria.add(Restrictions.ne(excludeColumn, excludeObject));
                }
                else {
                    criteria.add(Restrictions.isNotNull(excludeColumn));
                }
            }
            @SuppressWarnings("unchecked")
            List<TYPE> resultList = criteria.list();
            LOGGER.info(resultList.size() + " entities for class = " + entityName + " loaded");
            return resultList;
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Object name is not a class name", e);
        }
    }

    private static Object castToColumnType(Class<?> clazz, String column, Object object) {
        Class<?> columnType = ReflectHelper.getGetter(clazz, column).getReturnType();
        if (!String.class.equals(columnType)) {
            try {
                object = columnType.getConstructor(String.class).newInstance(object);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("[HibernateDataLoader] Cannot convert filterValue '"
                        + object + "' to columnclass '" + columnType + "'");
            }
        }
        return object;
    }

    @Override
    public String getSourceObjectName() {
        return entityName;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setHibernateDao(HibernateDao hibernateDao) {
        this.hibernateDao = hibernateDao;
    }

    @Required
    public void setTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    /**
     * Injected but optional
     */
    public void setFilterColumn(String filterColumn) {
        this.filterColumn = filterColumn;
    }

    /**
     * Injected but optional
     */
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    /**
     * Injected but optional
     */
    public void setExcludeColumn(String excludeColumn) {
        this.excludeColumn = excludeColumn;
    }

    /**
     * Injected but optional
     */
    public void setExcludeValue(String excludeValue) {
        this.excludeValue = excludeValue;
    }

}
