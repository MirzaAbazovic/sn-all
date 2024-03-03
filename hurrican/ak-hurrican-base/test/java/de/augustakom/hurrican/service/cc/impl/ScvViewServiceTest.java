/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2005 08:39:06
 */
package de.augustakom.hurrican.service.cc.impl;

import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.cc.ScvViewService;


/**
 * JUnit Test-Case fuer <code>ScvViewService</code>.
 *
 *
 */
@Ignore
public class ScvViewServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(ScvViewServiceTest.class);

    /**
     * Test fuer die Methode ScvViewService#findByParam(short, Object[])
     */
    public void testFindByParam() {
        try {
            ScvViewService service = (ScvViewService) getCCService(ScvViewService.class);
            service.findByParam((short) 0, null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


