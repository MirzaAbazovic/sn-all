package de.augustakom.hurrican.dao.reporting.impl;

import static de.augustakom.common.BaseTest.SERVICE;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.hurrican.dao.reporting.Report2ProdDAO;
import de.augustakom.hurrican.model.reporting.view.Report2ProdView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = SERVICE)
public class Report2ProdDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    Report2ProdDAO report2ProdDAO;

    @Test
    public void testFindReport2ProdView4Report() throws Exception {
        // This test may become *FLOPPY* over time -> it tests an SQL query using existing data in the database. If
        // this data changes or is deleted over time then the test WILL fail. Creating test data was not an option
        // since a read-only database user is used (REPORTUSER) for accessing the reporting tables.
        Long reportId = 6L;
        List<Report2ProdView> res = report2ProdDAO.findReport2ProdView4Report(reportId);
        Assert.assertNotNull(res);
        if(!res.isEmpty()) {
            Assert.assertEquals(res.get(0).getReportId(), reportId);
        }
    }
}