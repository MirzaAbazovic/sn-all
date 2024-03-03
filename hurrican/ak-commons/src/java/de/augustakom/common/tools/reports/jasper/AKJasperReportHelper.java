/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.09.2004 10:09:08
 */
package de.augustakom.common.tools.reports.jasper;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.sql.*;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;


/**
 * Hilfsklasse, um Jasper-Reports zu laden und mit Daten zu fuellen. Es wird ein Objekt vom Typ <code>JasperPrint</code>
 * zurueck geliefert, ueber das der Aufrufer einen Audruck oder Files erstellen kann. <br><br> Die Hilfsklasse sollte
 * gegenueber einem eigenen codieren der Aufrufe bevorzugt werden.
 *
 *
 */
public class AKJasperReportHelper {

    private static final Logger LOGGER = Logger.getLogger(AKJasperReportHelper.class);

    private static final String SUFFIX_JRXML = ".jrxml";
    private static final String SUFFIX_JASPER = ".jasper";

    /**
     * Wertet das JasperReportContext-Objekt aus, laedt den Report und fuellt ihn mit Daten.
     *
     * @param reportContext Objekt mit den Daten fuer den zu erstellenden Report
     * @return JasperPrint-Objekt, das fuer den Ausdruck oder fuer einen Export in eine Datei (PDF, Excel, XML etc.)
     * verwendet werden kann.
     * @throws AKReportException wenn bei der Report-Erstellung ein Fehler auftritt oder wenn 'falsche' Daten uebergeben
     *                           wurden.
     */
    public JasperPrint createReport(AKJasperReportContext reportContext) throws AKReportException {
        checkContext(reportContext);

        try {
            String reportName = getReportName(reportContext);
            InputStream is = getClass().getClassLoader().getResourceAsStream(reportName);
            if (is == null) {
                throw new AKReportException("Report konnte nicht erstellt werden! \n"
                        + "Grund: folgende Report-Datei konnte nicht gefunden werden: " + reportName);
            }

            Map params = (reportContext.getReportParameters() != null)
                    ? reportContext.getReportParameters()
                    : new HashMap();

            // Report 'fuellen'
            Object conn = reportContext.getConnection();
            if (conn instanceof DataSource) {
                return JasperFillManager.fillReport(is, params, ((DataSource) conn).getConnection());
            }
            else if (conn instanceof Connection) {
                return JasperFillManager.fillReport(is, params, (Connection) conn);
            }
            else if (conn instanceof JRDataSource) {
                return JasperFillManager.fillReport(is, params, (JRDataSource) conn);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Report konnte nicht erstellt werden! \nGrund: " + e.getMessage(), e);
        }

        throw new AKReportException("Report konnte aus unbekanntem Grund nicht erstellt werden!");
    }

    /* Ueberprueft, ob alle Daten uebergeben wurden. */
    private void checkContext(AKJasperReportContext reportContext) throws AKReportException {
        if (reportContext == null) {
            throw new AKReportException("Report konnte nicht erstellt werden! \n" +
                    "Es wurden keine Report-Details spezifiziert (JasperReportContext == null).");
        }

        if (StringUtils.isBlank(reportContext.getReportName())) {
            throw new AKReportException("Report konnte nicht erstellt werden! \n" +
                    "Grund: Der Reportname wurde nicht spezifiziert!");
        }

        if (reportContext.getConnection() == null) {
            throw new AKReportException("Report konnte nicht erstellt werden! \n" +
                    "Grund: Keine Connection fuer den Report vorhanden!");
        }
        else {
            if (!(reportContext.getConnection() instanceof Connection) &&
                    !(reportContext.getConnection() instanceof JRDataSource) &&
                    !(reportContext.getConnection() instanceof DataSource)) {
                throw new AKReportException("Report konnte nicht erstellt werden! \n" +
                        "Grund: Keine gültige Connection fuer den Report vorhanden!");
            }
        }
    }

    /* Gibt den Namen des Reports zurueck, der verwendet werden soll. */
    private String getReportName(AKJasperReportContext rc) {
        String defined = rc.getReportName();
        if (defined.endsWith(SUFFIX_JASPER)) {
            return defined;
        }
        else if (defined.endsWith(SUFFIX_JRXML)) {
            return StringUtils.replaceOnce(defined, SUFFIX_JRXML, SUFFIX_JASPER);
        }

        return defined + SUFFIX_JASPER;
    }

    /**
     * Fuegt alle Seiten des JasperPrint-Objekts <code>toAdd</code> dem JasperPrint-Objekt <code>base</code> hinzu.
     *
     * @param base
     * @param toAdd
     */
    public static void joinJasperPrints(JasperPrint base, JasperPrint toAdd) {
        if ((base != null) && (toAdd != null)) {
            List pages = toAdd.getPages();
            if (pages != null) {
                for (int i = 0; i < pages.size(); i++) {
                    base.addPage((JRPrintPage) pages.get(i));
                }
            }
        }
    }
}


