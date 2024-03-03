/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.05.2007 16:00:57
 */
package de.augustakom.hurrican.model.reporting;

/**
 * Abbildung der Zuordnung von Report zu Technischen Leistungen
 *
 *
 */
public class Report2TechLs extends AbstractReportLongIdModel {

    private Long report2ProdId = null;
    private Long techLsId = null;

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
     * @return techLsId
     */
    public Long getTechLsId() {
        return techLsId;
    }

    /**
     * @param techLsId Festzulegender techLsId
     */
    public void setTechLsId(Long techLsId) {
        this.techLsId = techLsId;
    }


}


