/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.10.2006 08:50:13
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.hurrican.dao.billing.AccountDAO;
import de.augustakom.hurrican.model.billing.Account;


/**
 * Hibernate DAO-Implementierung von <code>AccountDAOImpl</code>.
 *
 *
 */
public class AccountDAOImpl implements AccountDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    public List<Account> findAccounts4Auftrag(final Long auftragNo, final String account) {
        StringBuilder hql = new StringBuilder("select acc.auftragNo, acc.accountId, acc.accountName, acc.password ");
        hql.append(" from ").append(Account.class.getName()).append(" acc where acc.auftragNo= :orderNo ");
        if (StringUtils.isNotEmpty(account)) {
            hql.append(" and trim(acc.accountId)= :accId");
        }

        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        query.setLong("orderNo", auftragNo);
        if (StringUtils.isNotEmpty(account)) {
            query.setString("accId", account);
        }

        List<Object[]> result = query.list();
        if (result != null) {
            List<Account> retVal = new ArrayList<>();
            for (Object[] values : result) {
                Account acc = new Account();
                acc.setAuftragNo(ObjectTools.getLongSilent(values, 0));
                acc.setAccountId(StringUtils.trim(ObjectTools.getStringSilent(values, 1)));
                acc.setAccountName(StringUtils.trim(ObjectTools.getStringSilent(values, 2)));
                acc.setPassword(StringUtils.trim(ObjectTools.getStringSilent(values, 3)));
                retVal.add(acc);
            }
            return retVal;
        }
        return null;
    }

    public void saveAccount(Long auftragNo, String accountName, String password) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(
                "INSERT INTO ACCOUNT (AUFTRAG_NO, ACCOUNT_ID, ACCOUNTNAME, PASSWORD, USERW, DATEW) "
                        + " VALUES (?, ?, ?, ?, ?, ?)");
        sqlQuery.setParameter(0, auftragNo);
        sqlQuery.setParameter(1, accountName);
        sqlQuery.setParameter(2, accountName);
        sqlQuery.setParameter(3, password);
        sqlQuery.setParameter(4, "HURRICAN");
        sqlQuery.setParameter(5, new Date());

        sqlQuery.executeUpdate();
    }

    public void updatePassword(Long auftragNo, String accountName, String password) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(
                "UPDATE ACCOUNT SET PASSWORD=?, USERW=?, DATEW= ? "
                        + "WHERE AUFTRAG_NO = ? AND trim(ACCOUNT_ID) = ?");
        //Parameter
        sqlQuery.setParameter(0, password);
        sqlQuery.setParameter(1, "HURRICAN");
        sqlQuery.setParameter(2, new Date());
        sqlQuery.setParameter(3, auftragNo);
        sqlQuery.setParameter(4, accountName);

        sqlQuery.executeUpdate();
    }
}


