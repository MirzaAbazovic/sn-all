/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2007 13:33:57
 */
package de.augustakom.hurrican.model.reporting;

/**
 * Abbildung der Zuordnung von Reports zu Produkten.
 *
 *
 */
public class Report2Prod extends AbstractReportLongIdModel {

    private Long reportId = null;
    private Long produktId = null;

    /**
     * @return produktId
     */
    public Long getProduktId() {
        return produktId;
    }

    /**
     * @param produktId Festzulegender produktId
     */
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    /**
     * @return reportId
     */
    public Long getReportId() {
        return reportId;
    }

    /**
     * @param reportId Festzulegender reportId
     */
    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

}


