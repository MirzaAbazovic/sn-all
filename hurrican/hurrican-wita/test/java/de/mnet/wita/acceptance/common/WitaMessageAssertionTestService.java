/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 11:22:57
 */
package de.mnet.wita.acceptance.common;

import static de.augustakom.common.tools.matcher.RetryMatcher.*;
import static de.mnet.wita.acceptance.common.AbstractWitaAcceptanceBaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.time.*;
import java.util.*;
import com.google.common.base.Function;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.mnet.wita.dao.IoArchiveDao;
import de.mnet.wita.model.IoArchive;

/**
 * Service um in Tests verschiedene Sachen ueber Wita Nachrichten zu pruefen
 */
public class WitaMessageAssertionTestService {

    private class FindIoArchivesByExtOrderNo implements Function<Void, Collection<? extends IoArchive>> {
        private final CBVorgang cbVorgang;

        private FindIoArchivesByExtOrderNo(CBVorgang cbVorgang) {
            this.cbVorgang = cbVorgang;
        }

        @Override
        public Collection<? extends IoArchive> apply(Void input) {
            // IOArchiveService abfragen, ob Message archiviert wurde
            return ioArchiveDao.findIoArchivesForExtOrderNo(cbVorgang.getCarrierRefNr());
        }
    }

    @Qualifier("txIoArchiveDao")
    @Autowired
    IoArchiveDao ioArchiveDao;

    public void assertIoArchiveEntryReceived(final CBVorgang cbVorgang) throws Exception {
        assertThat("No IO archive entry for RefId " + cbVorgang.getCarrierRefNr() + " found!",
                overTime(new FindIoArchivesByExtOrderNo(cbVorgang)),
                eventually(not(Matchers.<IoArchive>hasSize(0))));
    }

    public void assertNoIoArchiveEntryReceived(CBVorgang cbVorgang2) throws InterruptedException {
        assertThat("IO Archive entry found but should not!",
                overTime(new FindIoArchivesByExtOrderNo(cbVorgang2))
                        .within(Duration.ofSeconds(5)),
                everyItem(Matchers.<IoArchive>hasSize(0))
        );
    }

}


