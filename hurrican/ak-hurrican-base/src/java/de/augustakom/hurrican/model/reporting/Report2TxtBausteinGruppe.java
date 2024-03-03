/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.05.2007 17:33:57
 */
package de.augustakom.hurrican.model.reporting;

/**
 * Abbildung der Zuordnung von Reports zu Txt-Baustein-Gruppen.
 *
 *
 */
public class Report2TxtBausteinGruppe extends AbstractReportLongIdModel {

    private Long reportId = null;
    private Long txtBausteinGruppeId = null;
    private Long orderNo = null;

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

    /**
     * @return txtBausteinGruppeId
     */
    public Long getTxtBausteinGruppeId() {
        return txtBausteinGruppeId;
    }

    /**
     * @param txtBausteinGruppeId Festzulegender txtBausteinGruppeId
     */
    public void setTxtBausteinGruppeId(Long txtBausteinGruppeId) {
        this.txtBausteinGruppeId = txtBausteinGruppeId;
    }

    /**
     * @return orderNo
     */
    public Long getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo Festzulegender orderNo
     */
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }


}


