/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 10:52:24
 */
package de.mnet.wita.model;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.inject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.dao.IoArchiveDao;

@Test(groups = SERVICE)
public class IoArchiveServiceTest extends AbstractServiceTest {

    private static final char[] symbols;

    static {
        symbols = new char[41];
        for (int idx = 0; idx < 10; ++idx) {
            symbols[idx] = (char) ('0' + idx);
        }
        for (int idx = 10; idx < 36; ++idx) {
            symbols[idx] = (char) ('a' + idx - 10);
        }
        symbols[36] = 'ä';
        symbols[37] = 'ö';
        symbols[38] = 'ü';
        symbols[39] = 'ß';
        symbols[40] = '<';
    }

    @Autowired
    private Provider<IoArchiveBuilder> ioArchiveBuilderProvider;
    @Autowired
    private IoArchiveDao ioArchiveDao;

    @DataProvider
    public Object[][] dataProviderPersist() {
        return new Object[][] { { 10 }, { 100 }, { 1000 }, { 10000 }, { 100000 }, };
    }

    @Test(dataProvider = "dataProviderPersist")
    public void testPersist(int requestXmlLength) throws Exception {
        String requestXml = generateStringOfLength(requestXmlLength);

        IoArchive ioArchive = ioArchiveBuilderProvider.get().withRequestXml(requestXml)
                .withTimestampSent(LocalDateTime.of(2011, 05, 29, 9, 28, 10, 0))
                .withRequestTimestamp(LocalDateTime.of(2011, 05, 29, 9, 28, 13, 0)).build();
        assertNotNull(ioArchive.getId());
        assertEquals(ioArchive.getRequestXml(), requestXml);

        flushAndClear();

        IoArchive ioArchiveFromDb = ioArchiveDao.findById(ioArchive.getId(), IoArchive.class);
        assertEquals(ioArchiveFromDb.getRequestXml(), ioArchive.getRequestXml());
        assertTrue(com.google.common.base.Objects.equal(ioArchiveFromDb, ioArchive));
    }

    private String generateStringOfLength(int n) {
        Random random = new Random();
        char[] buf = new char[n];

        for (int idx = 0; idx < n; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }
}
