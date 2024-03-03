/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2005 07:55:23
 */
package de.augustakom.common.tools.reports.jasper;

import java.util.*;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;


/**
 *
 */
public class AKJasperExporter implements AKJasperExportTypes {

    private static final Logger LOGGER = Logger.getLogger(AKJasperExporter.class);

    /**
     * Exportiert den Report <code>jasperPrint</code> in ein gewuenschtes Format.
     *
     * @param jasperPrint      zu exportierender Report.
     * @param exportType       Export-Typ (angegeben in Interface <code>AKJasperExportTypes</code>.
     * @param outputFileName   Dateiname fuer die Ziel-Datei (optional).
     * @param additionalParams Map mit zusaetzlichen Parametern fuer den Exporter.
     */
    public static void exportReport(JasperPrint jasperPrint, int exportType, String outputFileName, Map additionalParams)
            throws AKReportException {
        if (jasperPrint == null) {
            throw new AKReportException("Report kann nicht exportiert werden, da leer!");
        }

        try {
            JRExporter exporter = getExporter(exportType);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            if (StringUtils.isNotBlank(outputFileName)) {
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
            }

            if (additionalParams != null && !additionalParams.isEmpty()) {
                exporter.setParameters(additionalParams);
            }

            exporter.exportReport();
        }
        catch (AKReportException e) {
            throw e;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKReportException("Beim Exportieren des Reports ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    /**
     * Ermittelt anhand des Parameters <code>exporterType</code> die zu benoetigte Exporter-Klasse und gibt eine Instanz
     * von dieser zurueck.
     *
     * @param exporterType gesuchter/gewuenschter Export-Type.
     * @return Instanz des gesuchten Export-Types.
     * @throws AKReportException wenn der Export-Type nicht unterstuetzt wird.
     */
    protected static JRExporter getExporter(int exporterType) throws AKReportException {
        switch (exporterType) {
            case EXPORT_TYPE_PRINT:
                return new JRPrintServiceExporter();
            case EXPORT_TYPE_PDF:
                return new JRPdfExporter();
            case EXPORT_TYPE_HTML:
                return new JRHtmlExporter();
            case EXPORT_TYPE_EXCEL:
                return new JRXlsExporter();
            case EXPORT_TYPE_CSV:
                return new JRCsvExporter();
            case EXPORT_TYPE_XML:
                return new JRXmlExporter();
            default:
                throw new AKReportException("Der angegebene Export-Typ wird nicht unterstuetzt! Export-Type: " + exporterType);
        }

    }
}


