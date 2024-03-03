/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2005 09:28:45
 */
package de.augustakom.common.tools.dao.hibernate;

import java.util.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


/**
 * Hilfsklasse, um ueber Hibernate ein Select-Statemtent mit IN-Parameter auszufuehren.
 *
 *
 */
public class HibernateInClauseHelper {

    private static final int MAX_IN_LENGTH = 1000;

    /**
     * Methode, um ein beliebiges hql-Statement mit einem IN-Parameter mit mehr als 1000 Werten auszufuehren.
     *
     * @param sessionFactory          to create and execute query
     * @param hql                     the hql select with '%s' where the in part should be
     * @param params                  of the {@code selectFromWhere} part of the hql statement
     * @param inColumn                the hql column name
     * @param inValues                the list which contains the values for the in clauses
     * @return a list containing an array of {@link Object}s for every row
     */
    public static <T> List<T> list(SessionFactory sessionFactory, final String hql, final Map<String, Object> params, final String inColumn, final List<?> inValues) {
        if (inValues.isEmpty()) {
            return new ArrayList<T>();
        }
        final int packages = ((inValues.size() - 1) / MAX_IN_LENGTH) + 1; // -1: consider case size == MAX_IN_LENGTH
        final List<T> combinedResult = new ArrayList<T>(inValues.size());
        Session session = sessionFactory.getCurrentSession();
        for (int idx = 0; idx < packages; idx++) {
            StringBuilder inHql = new StringBuilder("(").append(inColumn).append(" in (:values" + idx + ") )");
            Query query = session.createQuery(String.format(hql, inHql.toString()));
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            int fromIndex = idx * MAX_IN_LENGTH;
            int toIndex = fromIndex + MAX_IN_LENGTH;
            if (toIndex > inValues.size()) {
                toIndex = inValues.size();
            }
            query.setParameterList("values" + idx, inValues.subList(fromIndex, toIndex));

            @SuppressWarnings("unchecked")
            List<T> result = query.list();
            combinedResult.addAll(result);
        }

        return combinedResult;
    }
}
