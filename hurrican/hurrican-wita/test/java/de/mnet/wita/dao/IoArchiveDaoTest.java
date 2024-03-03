/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.01.2012 17:08:54
 */
package de.mnet.wita.dao;

import java.util.*;
import javax.inject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.model.IoArchive;
import de.mnet.wita.model.IoArchiveBuilder;

@Test(groups = BaseTest.SERVICE)
public class IoArchiveDaoTest extends AbstractServiceTest {

    @Autowired
    private IoArchiveDao ioArchiveDao;
    @Autowired
    private Provider<IoArchiveBuilder> ioArchiveBuilderProvider;

    public void findIoArchivesByExtAuftragsnummer() {
        IoArchive archive = ioArchiveBuilderProvider.get().build();
        List<IoArchive> result = ioArchiveDao.findIoArchivesForExtOrderNo(archive.getWitaExtOrderNo());
        assertNotEmpty(result, "no archive data found!");
    }

    public void findIoArchivesByVertragsnummer() {
        IoArchive archive = ioArchiveBuilderProvider.get().withWitaVertragsnummer("12345").build();
        List<IoArchive> result = ioArchiveDao.findIoArchivesForVertragsnummer(archive.getWitaVertragsnummer());
        assertNotEmpty(result, "no archive data found!");
    }
}
