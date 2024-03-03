/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2009 08:35:00
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragIntern;
import de.augustakom.hurrican.model.cc.AuftragInternBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.AuftragInternService;

/**
 * TestCase fuer <code>AuftragInternService</code>.
 *
 *
 */
@Test(groups = { "service" })
public class AuftragInternServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(AuftragInternServiceTest.class);

    public void testFindByAuftragId() throws Exception {
        AuftragIntern auftragIntern = getBuilder(AuftragInternBuilder.class).withAuftragBuilder(
                getBuilder(AuftragBuilder.class)).withContactName("auftragInternTest").get();
        flushAndClear();

        AuftragInternService as = getCCService(AuftragInternService.class);
        AuftragIntern result = as.findByAuftragId(auftragIntern.getAuftragId());

        assertNotNull(result, "AuftragIntern not found!");
        assertEquals(auftragIntern.getAuftragId(), result.getAuftragId(),
                "AuftragIntern contactincorrect");
        assertEquals(auftragIntern.getContactName(), "auftragInternTest",
                "AuftragIntern contact name incorrect");
        assertEquals(auftragIntern.getGueltigBis(), result.getGueltigBis(),
                "AuftragIntern contact incorrect");
        assertEquals(auftragIntern.getGueltigVon(), result.getGueltigVon(),
                "AuftragIntern contact incorrect");
    }

    public void testSaveAuftragIntern() throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragIntern auftragIntern = getBuilder(AuftragInternBuilder.class).withAuftragBuilder(
                auftragBuilder).withDescription("UnitTest for Hurrican").setPersist(false).get();

        flushAndClear();

        AuftragInternService as = getCCService(AuftragInternService.class);
        assertEquals(as.saveAuftragIntern(auftragIntern, false), auftragIntern, "save result incorrect");

        AuftragIntern result = as.findByAuftragId(auftragIntern.getAuftragId());

        assertNotNull(result, "AuftragIntern not found");
        assertEquals(result.getGueltigBis(), DateTools.getHurricanEndDate(),
                "AuftragIntern date to incorrect");
        assertEquals(result.getDescription(), "UnitTest for Hurrican",
                "AuftragIntern description incorrect");
    }

}
