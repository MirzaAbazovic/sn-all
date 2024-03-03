/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2005 15:34:51
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.StringUtils;


/**
 * Modell zur Abbildung eines Query-Modells. <br> Im Unterschied zu den herkoemmlichen Query-Objekten ist dieses Objekt
 * nicht dazu gedacht, diverse Parameter fuer die Abfrage von Datensaetzen zu definieren, sondern ist eine Abbildung der
 * Tabelle T_DB_QUERIES.
 *
 *
 */
public class DBQueryDef extends AbstractCCIDModel {

    private static final long serialVersionUID = 4549499868072899221L;

    /**
     * Wert fuer <code>service</code> definiert, dass das Query auf dem Billing-System ausgefuehrt werden soll.
     */
    public static final String SERVICE_BILLING = "BILLING";
    /**
     * Wert fuer <code>service</code> definiert, dass das Query auf dem CC-System ausgefuehrt werden soll.
     */
    public static final String SERVICE_CC = "CC";

    public static final String PARAM_SEPARATOR = ",";
    public static final String PARAM_NAME_TYPE_SEPARATOR = "=";
    public static final String PARAM_TYPE_DATE = "DATE";
    public static final String PARAM_TYPE_STRING = "STRING";
    public static final String PARAM_TYPE_INTEGER = "INTEGER";

    private String name = null;
    private String description = null;
    private String sqlQuery = null;
    private String service = null;
    private String params = null;
    private Boolean notForTest = null;

    /**
     * Constructs column names from given sql stmt.
     * @return Returns the column names.
     */
    public String[] getColumnNames() {
        if (sqlQuery != null) {
            String sqlWithoutLineBreaks = sqlQuery.replaceAll("\n", " ");
            String[] columnNames = sqlWithoutLineBreaks.substring("SELECT".length(), sqlWithoutLineBreaks.toUpperCase().indexOf(" FROM ")).split(",");

            for (int i = 0; i < columnNames.length; i++) {
                String columnName = columnNames[i].trim().toUpperCase();

                if (columnName.contains("AS ")) {
                    columnName = columnName.substring(columnName.toUpperCase().indexOf("AS ") + 3);
                } else if (columnName.contains(".")) {
                    columnName = columnName.substring(columnName.indexOf('.') + 1);
                }

                columnNames[i] = columnName;
            }

            return columnNames;
        }

        return null;
    }

    /**
     * Ueberprueft, ob es sich bei der Query-Definition um eine Billing-Abfrage handelt.
     *
     * @return
     */
    public boolean isBillingQuery() {
        return StringUtils.equals(service, SERVICE_BILLING);
    }

    /**
     * Ueberprueft, ob es sich bei der Query-Definition um eine CC-Abfrage handelt.
     *
     * @return
     */
    public boolean isCCQuery() {
        return StringUtils.equals(service, SERVICE_CC);
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the sqlQuery.
     */
    public String getSqlQuery() {
        return sqlQuery;
    }

    /**
     * @param sqlQuery The sqlQuery to set.
     */
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    /**
     * @return Returns the service.
     */
    public String getService() {
        return service;
    }


    /**
     * @param service The service to set.
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return Returns the params.
     */
    public String getParams() {
        return this.params;
    }

    /**
     * Definition von Parametern, die fuer die Abfrage benoetigt werden. <br> Die Angabe erfolgt in der Form
     * <code>name=type,name2=type2</code>. <code>name</code> steht dabei fuer den Parameternamen, <code>type</code> fuer
     * die Art des Parameters (z.B. DATE, INTEGER, STRING etc.)
     *
     * @param params The params to set.
     */
    public void setParams(String params) {
        this.params = params;
    }

    public Boolean getNotForTest() {
        return notForTest;
    }

    public void setNotForTest(Boolean notForTest) {
        this.notForTest = notForTest;
    }

}


