/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2007 13:33:57
 */
package de.augustakom.hurrican.model.reporting;

/**
 * Abbildung der Zuordnung von Reports zu Benutzern.
 *
 *
 */
public class Report2UserRole extends AbstractReportLongIdModel {

    private Long reportId = null;
    private Long roleId = null;

    /**
     * @return roleId
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId Festzulegender roleId
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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


