/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 11:27:57
 */
package de.augustakom.hurrican.model.reporting;


/**
 * Modell-Klasse für die Daten eines Reports
 *
 *
 */
public class ReportData extends AbstractReportLongIdModel {

    private Long requestId = null;
    private String keyName = null;
    private String keyValue = null;


    /**
     * @return keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * @param keyName Festzulegender keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * @return keyValue
     */
    public String getKeyValue() {
        return keyValue;
    }

    /**
     * @param keyValue Festzulegender keyValue
     */
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    /**
     * @return requestId
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * @param requestId Festzulegender requestId
     */
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }


}
