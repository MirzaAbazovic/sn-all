/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.08.13
 */
package de.augustakom.common.tools.dao;

/**
 * Hilfsklasse, um Filter-Kriterien fuer DB-Suchoperationen zu definieren.
 */
public class FilterByProperty {

    public FilterByProperty(String propertyName, Object filterValue) {
        this.propertyName = propertyName;
        this.filterValue = filterValue;
    }

    private String propertyName;
    private Object filterValue;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(Object filterValue) {
        this.filterValue = filterValue;
    }

}
