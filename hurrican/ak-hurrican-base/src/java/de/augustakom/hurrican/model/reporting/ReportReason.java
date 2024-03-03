/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2007 12:25:01
 */
package de.augustakom.hurrican.model.reporting;


/**
 * Model-Klasse für eine Zuordnung von Drucker zu Papierformat/Typ
 *
 *
 */
public class ReportReason extends AbstractReportIdModel {

    private String name = null;
    private String description = null;
    private Boolean toArchive = null;
    private Boolean onlyNotArchived = null;

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

    /**
     * @return toArchive
     */
    public Boolean getToArchive() {
        return toArchive;
    }

    /**
     * @param toArchive Festzulegender toArchive
     */
    public void setToArchive(Boolean toArchive) {
        this.toArchive = toArchive;
    }

    /**
     * @return onlyNotArchived
     */
    public Boolean getOnlyNotArchived() {
        return onlyNotArchived;
    }

    /**
     * @param onlyNotArchived Festzulegender onlyNotArchived
     */
    public void setOnlyNotArchived(Boolean onlyNotArchived) {
        this.onlyNotArchived = onlyNotArchived;
    }

}
