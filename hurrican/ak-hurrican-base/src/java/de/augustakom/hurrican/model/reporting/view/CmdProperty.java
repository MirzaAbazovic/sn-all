/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2007 10:56:28
 */
package de.augustakom.hurrican.model.reporting.view;

import de.augustakom.hurrican.model.reporting.AbstractReportModel;

/**
 * View für ein Property einer Command-Klasse
 *
 *
 */
public class CmdProperty extends AbstractReportModel {

    private String key = null;
    private String value = null;

    /**
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key Festzulegender key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value Festzulegender value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
