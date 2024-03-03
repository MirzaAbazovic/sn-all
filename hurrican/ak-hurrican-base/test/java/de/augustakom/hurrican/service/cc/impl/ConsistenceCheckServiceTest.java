/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2005 11:18:20
 */
package de.augustakom.hurrican.service.cc.impl;

import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.common.concurrent.MethodProgressEvent;
import de.augustakom.common.concurrent.iface.IMethodProgressListener;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;


/**
 * JUnit-Test fuer <code>ConsistenceCheckService</code>.
 *
 *
 */
@Ignore
public class ConsistenceCheckServiceTest extends AbstractHurricanServiceTestCase implements IMethodProgressListener {

    private static final Logger LOGGER = Logger.getLogger(ConsistenceCheckServiceTest.class);

    public ConsistenceCheckServiceTest() {
        super();
    }

    public ConsistenceCheckServiceTest(String name) {
        super(name);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ConsistenceCheckServiceTest("testCheckAuftragStatus"));
        return suite;
    }

    /**
     * @see de.augustakom.common.concurrent.iface.IMethodProgressListener#methodProgress(de.augustakom.common.concurrent.MethodProgressEvent)
     */
    public void methodProgress(MethodProgressEvent event) {
        LOGGER.debug("EVENT: " + event.getProgressInfo());
    }

    /**
     * @see de.augustakom.common.concurrent.iface.IMethodProgressListener#methodFinished()
     */
    public void methodFinished() {
    }
}


