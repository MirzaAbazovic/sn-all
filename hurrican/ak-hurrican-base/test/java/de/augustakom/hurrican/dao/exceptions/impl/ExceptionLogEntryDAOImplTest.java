package de.augustakom.hurrican.dao.exceptions.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.exceptions.ExceptionLogEntryContext;
import de.augustakom.hurrican.dao.exceptions.ExceptionLogEntryDAO;
import de.augustakom.hurrican.model.cc.ExceptionLogEntryBuilder;
import de.augustakom.hurrican.model.exceptions.ExceptionLogEntry;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class ExceptionLogEntryDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    private ExceptionLogEntryDAO exceptionLogEntryDAO;

    private final static String HOST = "Host";
    private final static String ERR_MSG = "Error";
    private final static ExceptionLogEntryContext CONTEXT = ExceptionLogEntryContext.HURRICAN_SERVER;

    @Test
    public void testPersistExceptionLogEntry() throws Exception {
        ExceptionLogEntry exception = createLogEntry(HOST, CONTEXT);
        exceptionLogEntryDAO.store(exception);
        ExceptionLogEntry foundException = exceptionLogEntryDAO.findById(exception.getId(), ExceptionLogEntry.class);

        assertNotNull(foundException);
        assertEquals(foundException.getErrorMessage(), ERR_MSG);
    }

    @Test
    public void testGetNewExceptionLogEntriesCount() throws Exception {
        ExceptionLogEntry exception = createLogEntry(HOST, CONTEXT);
        exceptionLogEntryDAO.store(exception);

        long countNew = exceptionLogEntryDAO.getNewExceptionLogEntriesCount();
        assertTrue(countNew > 0);
    }

    @Test
    public void testFindNewExceptionLogEntries() throws Exception {
        ExceptionLogEntry exception = createLogEntry(HOST, CONTEXT);
        exceptionLogEntryDAO.store(exception);

        List<ExceptionLogEntry> newExceptionLogEntries = exceptionLogEntryDAO.findNewExceptionLogEntries(CONTEXT, 10);

        Matcher withException = hasProperty("id", IsEqual.equalTo(exception.getId()));
        assertThat(newExceptionLogEntries, hasItem(withException));
    }

    private ExceptionLogEntry createLogEntry(String host, ExceptionLogEntryContext context) {
        // @formatter:off
        return getBuilder(ExceptionLogEntryBuilder.class)
                .withHost(host)
                .withContext(context.identifier)
                .setPersist(false)
                .build();
        // @formatter:on
    }

}