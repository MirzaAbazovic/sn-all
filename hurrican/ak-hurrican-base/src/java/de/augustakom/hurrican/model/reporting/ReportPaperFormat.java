/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2007 09:00:01
 */
package de.augustakom.hurrican.model.reporting;


/**
 * Model-Klasse für das Papierformat
 *
 *
 */
public class ReportPaperFormat extends AbstractReportLongIdModel {


    private String name = null;
    private String description = null;

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Festzulegender description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Festzulegender name
     */
    public void setName(String name) {
        this.name = name;
    }


}
