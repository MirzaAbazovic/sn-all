/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.07.13
 */
package de.augustakom.common.tools.dao;

/**
 *
 */
public class OrderByProperty {

    public enum OrderBy {
        ASC, DESC
    }

    private String propertyName;
    private OrderBy orderBy;

    public OrderByProperty(String propertyName, OrderBy orderBy) {
        assert propertyName != null;
        assert orderBy != null;
        this.propertyName = propertyName;
        this.orderBy = orderBy;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderByProperty)) {
            return false;
        }

        OrderByProperty that = (OrderByProperty) o;

        if (orderBy != that.orderBy) {
            return false;
        }
        if (!propertyName.equals(that.propertyName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = propertyName.hashCode();
        result = 31 * result + orderBy.hashCode();
        return result;
    }

}
