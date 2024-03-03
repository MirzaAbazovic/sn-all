package de.augustakom.hurrican.dao.cc.errorlog;

import static org.testng.Assert.*;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.ErrorLogEntry;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

/**
 * Unit test for {@link ErrorLogDAOImpl}
 */
@Test(groups = BaseTest.SERVICE)
public class ErrorLogDAOImplTest  extends AbstractHurricanBaseServiceTest {

    public static final String SERVICE = "service";

    @Autowired
    ErrorLogDAO errorLogDAO;

    @Test
    public void testStoreAndFind() throws Exception {
        final ErrorLogEntry errorLogEntry = new ErrorLogEntry("test error name", "test error desc", "service", "stacktrace");
        assertNotNull(errorLogEntry.getCreatedAt());

        final ErrorLogEntry storedEntry = errorLogDAO.store(errorLogEntry);
        assertNotNull(storedEntry.getId());
        assertEquals(storedEntry.getErrorName(), "test error name");
        assertEquals(storedEntry.getErrorDescription(), "test error desc");
        assertEquals(storedEntry.getService(), SERVICE);
        assertEquals(storedEntry.getStacktrace(), "stacktrace");

        final ErrorLogEntry foundById = errorLogDAO.findById(storedEntry.getId(), ErrorLogEntry.class);
        assertNotNull(foundById);
        assertEquals(foundById, storedEntry);

        final List<ErrorLogEntry> entriesByParams = errorLogDAO.findByService(SERVICE);
        assertNotNull(entriesByParams);
        assertFalse(entriesByParams.isEmpty());
    }

    @Test
    public void testEntity() throws Exception {
        final ErrorLogEntry errorLogEntryNulls = new ErrorLogEntry(null, null, null, null);
        assertNotNull(errorLogEntryNulls.getCreatedAt());
        assertNull(errorLogEntryNulls.getErrorDescription());
        assertNull(errorLogEntryNulls.getErrorName());
        assertNull(errorLogEntryNulls.getId());
        assertNull(errorLogEntryNulls.getService());
        assertNull(errorLogEntryNulls.getStacktrace());

        final String a5000 = StringUtils.rightPad("", 5000, "a");
        final ErrorLogEntry errorLogEntryLargeStacttrace = new ErrorLogEntry(null, null, null, a5000);
        assertEquals(errorLogEntryLargeStacttrace.getStacktrace(), StringUtils.substring(a5000, 0, 2000));
    }

}