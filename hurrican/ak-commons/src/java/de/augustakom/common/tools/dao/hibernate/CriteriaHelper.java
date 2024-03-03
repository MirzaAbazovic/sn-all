/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 08:04:30
 */
package de.augustakom.common.tools.dao.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.augustakom.common.tools.dao.OrderByProperty;
import de.augustakom.common.tools.lang.WildcardTools;

/**
 * Hilfsklasse, um ein Objekt vom Typ <code>org.hibernate.Criteria</code> zu modifizieren.
 *
 *
 */
public class CriteriaHelper {

    /**
     * Operator: =
     */
    public static final int EQUAL = 0;

    /**
     * Operator: like
     */
    public static final int LIKE = 1;

    /**
     * Operator: like (case insensitive)
     */
    public static final int ILIKE = 2;

    /**
     * Operator: >
     */
    public static final int GREATER = 3;

    /**
     * Operator: >=
     */
    public static final int GREATER_EQUAL = 4;

    /**
     * Operator: <
     */
    public static final int LESS = 5;

    /**
     * Operator: <=
     */
    public static final int LESS_EQUAL = 6;

    /**
     * Fuegt dem Hibernate-Criteria <code>criteria</code> eine neue Expression hinzu. Ueber den Operator
     * <code>operator</code> kann angegeben werden, um welche Expression-Art (z.B. equal oder greaterThan) es sich
     * handeln soll. <br>
     * <p/>
     * Handelt es sich bei <code>value</code> um ein Objekt vom Typ java.lang.String, so werden evtl. vorhandene
     * Wildcards (*) durch Wildcards ersetzt, die fuer die DB lesbar sind (%). <br>
     * <p/>
     * Die Expression wird grundsaetzlich <strong>nicht</strong> hinzugefuegt, wenn das Object <code>value</code>
     * <code>null</code> oder leer (=Leerstring) ist.
     *
     * @param criteria Criteria-Objekt, dem eine Expression hinzugefuegt werden soll
     * @param operator Operator, der zu verwenden ist. Unterstuetzt werden folgende Operatoren: Konstanten, die in
     *                 dieser Klasse definiert sind.
     * @param param    Parametername fuer die Expression
     * @param value    Wert fuer die Expression
     */
    public static void addExpression(Criteria criteria, int operator, String param, Object value) {
        if (value != null) {
            boolean addExpression = true;
            Object expValue = value;
            if (value instanceof String) {
                if (StringUtils.isNotBlank((String) value)) {
                    expValue = WildcardTools.replaceWildcards((String) value);
                }
                else {
                    addExpression = false;
                }
            }

            if (addExpression) {
                switch (operator) {
                    case EQUAL:
                        criteria.add(Restrictions.eq(param, expValue));
                        break;
                    case LIKE:
                        criteria.add(Restrictions.like(param, expValue));
                        break;
                    case ILIKE:
                        criteria.add(Restrictions.ilike(param, expValue));
                        break;
                    case GREATER:
                        criteria.add(Restrictions.gt(param, expValue));
                        break;
                    case GREATER_EQUAL:
                        criteria.add(Restrictions.ge(param, expValue));
                        break;
                    case LESS:
                        criteria.add(Restrictions.lt(param, expValue));
                        break;
                    case LESS_EQUAL:
                        criteria.add(Restrictions.le(param, expValue));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Fuegt dem Criteria 'criteria' eine <code>Expression.eq</code> oder <code>Expression.isNull</code> hinzu -
     * abhaengig von <code>value</code>.
     *
     * @param criteria Das Criteria
     * @param param    Name des Attributs
     * @param value    Wert des Attributs
     */
    public static void addExpEqualOrIsNull(Criteria criteria, String param, String value) {
        if (StringUtils.isBlank(value)) {
            criteria.add(Restrictions.isNull(param));
        }
        else {
            criteria.add(Restrictions.eq(param, value));
        }
    }

    /**
     * Fuegt dem Criteria 'criteria' ein Attribut nach dem die Criteria-Abfrage sortiert werden muss.
     *
     * @param criteria        Das Criteria
     * @param orderByProperty Attribut nach dem sortiert wird
     */
    public static void addOrder(Criteria criteria, OrderByProperty orderByProperty) {
        assert orderByProperty != null;
        switch (orderByProperty.getOrderBy()) {
            case ASC:
                criteria.addOrder(Order.asc(orderByProperty.getPropertyName()));
                break;
            case DESC:
                criteria.addOrder(Order.desc(orderByProperty.getPropertyName()));
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported OrderBy '%s'.", orderByProperty.getOrderBy()));
        }
    }

}
