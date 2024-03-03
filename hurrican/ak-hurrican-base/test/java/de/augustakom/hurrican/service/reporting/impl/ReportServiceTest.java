/* Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 10:05:50
 */
package de.augustakom.hurrican.service.reporting.impl;

import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.reporting.Report;
import de.augustakom.hurrican.model.reporting.ReportRequest;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.reporting.ReportService;

/**
 * Test fuer Hurrican-Reporting.
 *
 *
 */
@Test(groups = { "service" })
public class ReportServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(ReportServiceTest.class);

    @Test(expectedExceptions = { StoreException.class })
    public void testSaveReportRequestStoreException() throws Exception {
        ReportService rs = getReportService(ReportService.class);
        rs.saveReportRequest(null);
    }

    @Test(expectedExceptions = { StoreException.class })
    public void testSaveReportRequestReportException() throws Exception {
        ReportService rs = getReportService(ReportService.class);

        ReportRequest toSave = new ReportRequest();
        toSave.setRepId(Report.REPORT_TYPE_INTERN);

        rs.saveReportRequest(toSave);
    }

    @Test(enabled = false)
    public void testSaveReportRequest() throws Exception {

        Auftrag auftrag = getBuilder(AuftragBuilder.class).build();
        flushAndClear();

        ReportService rs = getReportService(ReportService.class);

        ReportRequest rr = new ReportRequest();
        rr.setRepId(Report.REPORT_TYPE_KUNDE);
        rr.setKundeNo(auftrag.getKundeNo());
        rr.setAuftragId(auftrag.getAuftragId());

        rr.setRequestAt(DateTools.getActualSQLDate());
        rr.setRequestFrom("testcase");

        rs.saveReportRequest(rr);

        flushAndClear(); //TODO bleibt hier stehen und wartet auf DB

        ReportRequest rq = rs.findReportRequest(rr.getId());

        assertEquals(rq.getRepId(), Report.REPORT_TYPE_KUNDE, "RepID incorrect");
        assertNull(rq.getOrderNoOrig(), "AuftragID incorrect");
        assertEquals(rq.getAuftragId(), auftrag.getAuftragId(), "AuftragID incorrect");
        assertNull(rq.getBuendelNo(), "AuftragID incorrect");
    }

    @Test(enabled = false)
    public void testCreateReportRequest() throws Exception {

        Auftrag auftrag = getBuilder(AuftragBuilder.class).build();

        ReportService rs = getReportService(ReportService.class);
        Long reportId = rs.createReportRequest(Report.REPORT_TYPE_AUFTRAG, getSessionId(), auftrag.getKundeNo(),
                null, auftrag.getAuftragId(), null, null, false);
        assertNotNull(reportId, "report id incorrect");

        flushAndClear(); //TODO bleibt hier stehen und wartet auf DB

        ReportRequest rq = rs.findReportRequest(reportId);

        assertEquals(rq.getRepId(), Report.REPORT_TYPE_AUFTRAG, "RepID incorrect");
        assertNull(rq.getOrderNoOrig(), "AuftragID incorrect");
        assertEquals(rq.getAuftragId(), auftrag.getAuftragId(), "AuftragID incorrect");
        assertNull(rq.getBuendelNo(), "AuftragID incorrect");
    }

    @Test(enabled = false)
    public void testDeleteOldReports() {
        try {
            ReportService rs = getReportService(ReportService.class);
            rs.deleteOldReports(new Integer(5), Boolean.TRUE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test(enabled = false)
    public void testDeleteOldReportData() {
        try {
            ReportService rs = getReportService(ReportService.class);
            rs.deleteOldReportData(new Integer(6));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
}
