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
public class Printer2Paper extends AbstractReportLongIdModel {

    private Long printerId = null;
    private Long paperId = null;
    private String trayName = null;

    /**
     * @return paperId
     */
    public Long getPaperId() {
        return paperId;
    }

    /**
     * @param paperId Festzulegender paperId
     */
    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    /**
     * @return printerId
     */
    public Long getPrinterId() {
        return printerId;
    }

    /**
     * @param printerId Festzulegender printerId
     */
    public void setPrinterId(Long printerId) {
        this.printerId = printerId;
    }

    /**
     * @return trayName
     */
    public String getTrayName() {
        return trayName;
    }

    /**
     * @param trayName Festzulegender trayName
     */
    public void setTrayName(String trayName) {
        this.trayName = trayName;
    }


}
