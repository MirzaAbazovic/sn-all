package de.augustakom.hurrican.dao.reporting.impl;

import static de.augustakom.common.BaseTest.*;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.hurrican.dao.reporting.ReportRequestDAO;
import de.augustakom.hurrican.model.reporting.view.ReportRequestView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = SERVICE)
public class ReportRequestDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    ReportRequestDAO reportRequestDAO;

    @Test
    public void testFindAllRequestsByKundeNoAndAuftragId() throws Exception {
        // This test may become *FLOPPY* over time -> it tests an SQL query using existing data in the database. If
        // this data changes or is deleted over time then the test WILL fail. Creating test data was not an option
        // since a read-only database user is used (REPORTUSER) for accessing the reporting tables.

        Long kundeNo = 500121439L;
        Long auftragId = 193841L;
        Date dateFrom = Date.from(LocalDate.parse("2007-05-01").atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateTo = Date.from(LocalDate.parse("2007-06-01").atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<ReportRequestView> found = reportRequestDAO.findAllRequestsByKundeNoAndAuftragId(kundeNo, auftragId, dateFrom, dateTo);
        Assert.assertEquals(found.size(), 1);
        Assert.assertEquals(found.get(0).getAuftragId(), auftragId);
        Assert.assertEquals(found.get(0).getKundeNo(), kundeNo);
    }

}