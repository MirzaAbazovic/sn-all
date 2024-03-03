/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.10.2008 10:53:24
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.cc.QueryCCService;


/**
 * JUnit TestCase fuer 'QueryCCService'
 *
 *
 */
@Ignore
public class QueryCCServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(QueryCCServiceTest.class);

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.QueryCCServiceImpl#findByExample(java.lang.Object,
     * java.lang.Class)}.
     */
    public void testFindByExample() {
        try {
            HVTGruppe example = new HVTGruppe();
            example.setOrtsteil("HVT%");

            ISimpleFindService service = (ISimpleFindService) getCCService(QueryCCService.class);
            List result = service.findByExample(example, example.getClass());

            assertNotEmpty("Keine HVTs gefunden!", result);
            for (Object o : result) {
                HVTGruppe hvt = (HVTGruppe) o;
                LOGGER.debug(hvt.getId() + " - " + hvt.getOrtsteil());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


