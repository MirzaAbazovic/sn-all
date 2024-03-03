/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.14 09:31
 */
package de.mnet.hurrican.webservice.resource.report;

import static java.lang.String.*;

import java.io.*;
import javax.annotation.*;
import de.m_net.hurrican.hurricanweb.resource.resourcereportingservice.v1.GetTechnicalReportRequest;
import de.m_net.hurrican.hurricanweb.resource.resourcereportingservice.v1.GetTechnicalReportResponse;
import de.m_net.hurrican.hurricanweb.resource.resourcereportingservice.v1.ResourceReportingFault;
import de.m_net.hurrican.hurricanweb.resource.resourcereportingservice.v1.ResourceReportingFault_Exception;
import de.m_net.hurrican.hurricanweb.resource.resourcereportingservice.v1.ResourceReportingService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.log4j.Logger;

import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;

/**
 * liefert einen technischen Report als pdf
 */
public class ResourceReportServiceProvider implements ResourceReportingService {

    public static final String CODE_UNEXPECTED_ERROR = "HUR-001";
    public static final String CODE_TECHNICAL_ERROR = "HUR-100";
    private static final Logger LOGGER = Logger.getLogger(ResourceReportServiceProvider.class);
    @Resource(name = "de.augustakom.hurrican.service.cc.BAService")
    BAService baService;
    @Resource(name = "de.augustakom.hurrican.service.cc.CCAuftragService")
    CCAuftragService auftragService;

    @Override
    public GetTechnicalReportResponse getTechnicalReport(final GetTechnicalReportRequest getTechnicalReportRequest)
            throws ResourceReportingFault_Exception {
        try {
            LOGGER.trace(format("GetTechnicalReportRequest with auftragId %s ",
                    getTechnicalReportRequest.getTechnicalOrderId()));
            final GetTechnicalReportResponse response = new GetTechnicalReportResponse();

            if (auftragService.findAuftragById(getTechnicalReportRequest.getTechnicalOrderId()) == null) {
                response.setReport(new byte[0]);
                LOGGER.trace(format(
                        "GetTechnicalReportResponse for auftragId %s is empty, because id could not be found",
                        response.getReport().length));
            }
            else {
                final JasperPrint jasperPrint = baService.reportVerlauf4Auftrag(
                        getTechnicalReportRequest.getTechnicalOrderId(), getSessionId(), false, true);

                final JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                    exporter.exportReport();
                    response.setReport(out.toByteArray());
                    LOGGER.trace(format("GetTechnicalReportResponse for auftragId %s with report of size %s bytes",
                            getTechnicalReportRequest.getTechnicalOrderId(), response.getReport().length));
                }
                catch (IOException e) {
                    LOGGER.error(format("IOException during report generation for auftragId %s",
                            getTechnicalReportRequest.getTechnicalOrderId()));
                    final ResourceReportingFault fault = new ResourceReportingFault();
                    fault.setErrorCode(CODE_TECHNICAL_ERROR);
                    fault.setErrorMessage(format("I/O-problem during report generation: %s", e.getMessage()));
                    throw new ResourceReportingFault_Exception(e.getMessage(), fault, e);
                }
            }
            return response;
        }
        catch (AKReportException | FindException | JRException e) {
            LOGGER.error(format("IOException during report generation for auftragId %s",
                    getTechnicalReportRequest.getTechnicalOrderId()));
            final ResourceReportingFault fault = new ResourceReportingFault();
            fault.setErrorCode(CODE_UNEXPECTED_ERROR);
            fault.setErrorMessage(e.getMessage());
            throw new ResourceReportingFault_Exception(e.getMessage(), fault, e);
        }
    }

    private long getSessionId() {
        return -1;
    }
}
