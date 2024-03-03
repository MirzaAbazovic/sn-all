/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.05.2004 08:49:41
 */
package de.augustakom.authentication.dao.impl;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.authentication.dao.AKAccountDAO;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKUserAccount;


/**
 * Hibernate-Implementierung fuer AKAccountDAO.
 */
public class AKAccountDAOImpl implements AKAccountDAO {

    @Autowired
    @Qualifier("authentication.sessionFactory")
    protected SessionFactory sessionFactory;

    /**
     * @see de.augustakom.authentication.dao.AKAccountDAO#findAccounts(java.lang.Long, java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<AKAccount> findAccounts(final Long userId, final Long applicationId) {
        final String hql = "select new AKAccount(acc.id, acc.dbId, acc.applicationId, acc.name, "
                + "acc.accountUser, acc.accountPassword, acc.maxActive, acc.maxIdle, "
                + "acc.description) "
                + "from AKAccount as acc, AKUserAccount ua "
                + "where ua.userId=:userId and acc.applicationId=:applicationId and acc.id=ua.accountId";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("userId", userId);
        query.setLong("applicationId", applicationId);
        return query.list();
    }

    /**
     * @see de.augustakom.authentication.dao.AKAccountDAO#findAccounts(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<AKAccount> findAccounts(Long userId) {
        final String hql = "select new AKAccount(acc.id, acc.dbId, acc.applicationId, acc.name, " +
                "acc.accountUser, acc.accountPassword, acc.maxActive, acc.maxIdle, " +
                "acc.description) " +
                "from AKAccount as acc, AKUserAccount ua where ua.userId=:userId and acc.id=ua.accountId";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("userId", userId);
        return query.list();
    }

    /**
     * @see de.augustakom.authentication.dao.AKAccountDAO#findByDB(java.lang.Long)
     */
    @SuppressWarnings("unchecked")
    public List<AKAccount> findByDB(Long dbId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from " + AKAccount.class.getName()
                + " acc where acc.dbId=:dbId ");
        query.setLong("dbId", dbId);
        return query.list();
    }

    /**
     * @see de.augustakom.authentication.dao.AKAccountDAO#saveOrUpdate(de.augustakom.authentication.model.AKAccount)
     */
    public void saveOrUpdate(AKAccount account) {
        sessionFactory.getCurrentSession().saveOrUpdate(account);
    }

    /**
     * @see de.augustakom.authentication.dao.AKAccountDAO#delete(java.lang.Long)
     */
    public void delete(final Long id) {
        String hql = "delete from de.augustakom.authentication.model.AKAccount acc where acc.id=:id";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("id", id);
        query.executeUpdate();
    }

    /**
     * @see de.augustakom.authentication.dao.AKAccountDAO#findAll()
     */
    @SuppressWarnings("unchecked")
    public List<AKAccount> findAll() {
        Query query = sessionFactory.getCurrentSession().createQuery("from " + AKAccount.class.getName()
                + " acc order by acc.name");
        return query.list();
    }

    /**
     * @see de.augustakom.authentication.dao.AKAccountDAO#removeUserAccount(java.lang.Long, java.lang.Long)
     */
    public void removeUserAccount(final Long userId, final Long accountId) {
        String hql = "delete from de.augustakom.authentication.model.AKUserAccount ua "
                + "where ua.userId=:userId and ua.accountId=:accountId";
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setLong("userId", userId);
        query.setLong("accountId", accountId);
        query.executeUpdate();
    }

    /**
     * @see de.augustakom.authentication.dao.AKAccountDAO#addUserAccount(java.lang.Long, java.lang.Long, java.lang.Long)
     */
    public void addUserAccount(final Long userId, final Long accountId, final Long dbId) {
        AKUserAccount userAcc = new AKUserAccount();
        userAcc.setUserId(userId);
        userAcc.setAccountId(accountId);
        userAcc.setDbId(dbId);

        sessionFactory.getCurrentSession().save(userAcc);
    }

}
