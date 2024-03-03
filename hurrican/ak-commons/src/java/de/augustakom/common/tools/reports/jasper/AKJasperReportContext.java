/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2004 10:09:41
 */
package de.augustakom.common.tools.reports.jasper;

import java.util.*;


/**
 * Klasse enthaelt Daten, die benoetigt werden, um einen JasperReport zu erzeugen.
 *
 *
 */
public class AKJasperReportContext {

    private String reportName = null;
    private Object connection = null;
    private Map<String, Object> reportParameters = null;

    /**
     * Standardkonstruktor.
     */
    public AKJasperReportContext() {
        init();
    }

    /**
     * Konstruktor mit Angabe aller Parameter.
     *
     * @param reportName       Name des aufzurufenden Reports.
     * @param reportParameters Map mit Parametern fuer den Report.
     * @param connection       Connection-Objekt fuer den Report. (Typ: <code>java.sql.Connection</code> oder
     *                         <code>net.sf.jasperreports.engine.JRDataSource</code>).
     */
    public AKJasperReportContext(String reportName, Map<String, Object> reportParameters, Object connection) {
        super();
        this.reportName = reportName;
        this.connection = connection;
        this.reportParameters = reportParameters;
    }

    /* Initialisiert das Objekt. */
    private void init() {
        reportParameters = new HashMap<String, Object>();
    }

    /**
     * @return Returns the connection.
     */
    public Object getConnection() {
        return connection;
    }

    /**
     * Uebergibt dem Context-Objekt die Connection fuer den Report. Die Connection muss vom Typ
     * <code>java.sql.Connection</code> oder <code>net.sf.jasperreports.engine.JRDataSource</code> sein.
     *
     * @param connection The connection to set.
     */
    public void setConnection(Object connection) {
        this.connection = connection;
    }

    /**
     * @return Returns the reportName.
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * Setzt den Namen des Reports. <br> Besitzt der angegebene Name keine Endung, wird nach der Datei reportName.jasper
     * gesucht. <br> Endert <code>reportName</code> mit '.jrxml' wird diese Endung durch '.jasper' ersetzt. <br>
     * Beispiele fuer <code>reportName</code>: <br> <ul> <li>de/augustakom/reports/MyReport.jasper (bevorzugt)
     * <li>de/augustakom/reports/MyReport <li>de/augustakom/reports/MyReport.jrxml </ul>
     *
     * @param reportName The reportName to set.
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    /**
     * @return Returns the reportParameters.
     */
    public Map<String, Object> getReportParameters() {
        return reportParameters;
    }

    /**
     * Uebergibt dem ReportContext die zu verwendenden Report-Parameter.
     *
     * @param params
     */
    public void setReportParameter(Map<String, Object> params) {
        this.reportParameters = params;
    }

    /**
     * Uebergibt dem ReportContext einen neuen Parameter.
     *
     * @param name  (eindeutiger) Name fuer den Parameter
     * @param value Wert fuer den Parameter
     */
    public void addReportParameter(String name, Object value) {
        reportParameters.put(name, value);
    }

}


