/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2007 13:32:57
 */
package de.augustakom.hurrican.model.reporting;

/**
 * Abbildung der Zuordnung von Report und Produkten zu Auftragsstati.
 *
 *
 */
public class Report2ProdStati extends AbstractReportLongIdModel {

    private Long report2ProdId = null;
    private Long statusId = null;

    /**
     * @return report2ProdId
     */
    public Long getReport2ProdId() {
        return report2ProdId;
    }

    /**
     * @param report2ProdId Festzulegender report2ProdId
     */
    public void setReport2ProdId(Long report2ProdId) {
        this.report2ProdId = report2ProdId;
    }

    /**
     * @return statusId
     */
    public Long getStatusId() {
        return statusId;
    }

    /**
     * @param statusId Festzulegender statusId
     */
    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }


}


