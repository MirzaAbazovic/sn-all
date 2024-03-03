/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2008 12:24:04
 */
package de.augustakom.hurrican.model.billing.history;

import java.util.*;
import org.apache.commons.lang.StringUtils;


/**
 * Modell für die ModificationLog-Tabelle. Enthaelt die zurueckliegenden Aenderungen der Billing-Objekte.
 *
 *
 */
public class ModificationLog extends AbstractChangeModel {

    private Long id = null;
    private String tableName = null;
    private Long recordId = null;
    private String fieldName = null;
    private String oldValue = null;
    private String newValue = null;
    private Date modDate = null;

    /**
     * @return modDate
     */
    public Date getModDate() {
        return modDate;
    }

    /**
     * @param modDate Festzulegender modDate
     */
    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    /**
     * @return fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName Festzulegender fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Festzulegender id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return newValue
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * @param newValue Festzulegender newValue
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    /**
     * @return oldValue
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * @param oldValue Festzulegender oldValue
     */
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    /**
     * @return recordId
     */
    public Long getRecordId() {
        return recordId;
    }

    /**
     * @param recordId Festzulegender recordId
     */
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    /**
     * @return tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName Festzulegender tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Sorgt fuer das Mapping zwischen Spalten- und Variablennamen.
     *
     * @param fieldName
     * @return
     *
     */
    public static String getMapping(String fieldName) {
        if (StringUtils.equals(fieldName, "DESCRIPTION")) {
            return "description";
        }
        if (StringUtils.equals(fieldName, "CUST_NO")) {
            return "kundeNo";
        }
        if (StringUtils.equals(fieldName, "INV_ADDR_NO")) {
            return "adresseNo";
        }
        if (StringUtils.equals(fieldName, "EXT_DEBITOR_ID")) {
            return "extDebitorId";
        }
        if (StringUtils.equals(fieldName, "INV_ELECTRONIC")) {
            return "invElectronic";
        }
        if (StringUtils.equals(fieldName, "INV_MAXI")) {
            return "invMaxi";
        }
        if (StringUtils.equals(fieldName, "DEBIT_ACCOUNT_NO")) {
            return "finanzNo";
        }
        return null;
    }
}
