/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2007 15:26:15
 */
package de.augustakom.hurrican.dao.billing.impl;

import java.util.*;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.common.tools.dao.hibernate.Hibernate4FindDAOImpl;
import de.augustakom.common.tools.lang.ObjectTools;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.dao.billing.ArchPrintDAO;


/**
 * JDBC DAO-Implementierung von <code>ArchPrintDAO</code>
 *
 *
 */
public class ArchPrintDAOImpl extends Hibernate4FindDAOImpl implements ArchPrintDAO {

    @Autowired
    @Qualifier("billing.sessionFactory")
    protected SessionFactory sessionFactory;

    @Override
    public Integer[] sumPagesAndBills(Long printSetNo, String groupName) {
        SQLQuery sqlQuery = getSessionFactory().getCurrentSession().createSQLQuery(
                "SELECT sum(f.TOTAL_PRINT_PAGES) AS PAGES, sum(f.TOTAL_DOC) AS BILLS "
                        + "FROM ARCH_PRINT_FILE f WHERE f.ARCH_PRINT_SET_NO=? AND f.FILE_NAME LIKE ?");
        sqlQuery.setParameters(new Object[] { printSetNo, WildcardTools.replaceWildcards(groupName) }, new Type[] { new LongType(), new StringType() });
        List<Object[]> result = sqlQuery.list();
        if ((result != null) && (result.size() == 1)) {
            Object[] values = result.get(0);

            Integer pages = ObjectTools.getIntegerSilent(values, 0);
            Integer bills = ObjectTools.getIntegerSilent(values, 1);

            return new Integer[] { pages, bills };
        }

        return null;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


