package de.augustakom.hurrican.dao.reporting.impl;

import static de.augustakom.common.BaseTest.SERVICE;
import static org.testng.Assert.*;

import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.hurrican.dao.reporting.ReportDataDAO;
import de.augustakom.hurrican.dao.reporting.ReportRequestDAO;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = SERVICE)
public class ReportDataDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    ReportDataDAO reportDataDAO;

    @Test
    public void testDeleteData4Request() throws Exception {
        try {
            // This test is a little bit unusual in that it verifies that a native SQL query is valid
            // by checking for the presence of a certain Exception - SQLGrammarException with the Error-Code 'ORA-01031'
            // Since the read-only database user 'REPORTUSER' is used in service tests when accessing the reporting
            // tables, the SQL delete causes a 'ORA-01031' exception (insufficient rights)
            // Note: this test will fail if later on a database user with write-access is used!
            reportDataDAO.deleteData4Request(-1000L);
            reportDataDAO.flushSession();
            fail("Was expecting a SQLGrammarException with a nested SQL 'ORA-01031' exception");
        }
        catch (SQLGrammarException e) {
            assertTrue(e.getCause().getMessage().indexOf("ORA-01031") != -1);
        }
    }
}