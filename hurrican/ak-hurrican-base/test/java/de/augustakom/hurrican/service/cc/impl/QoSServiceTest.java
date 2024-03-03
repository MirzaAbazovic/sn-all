/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.02.2008 15:32:03
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.model.cc.AuftragQoS;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.cc.QoSService;


/**
 * JUnit TestCase fuer <code>QoSService</code>.
 *
 *
 */
@Ignore
public class QoSServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(QoSServiceTest.class);

    public QoSServiceTest() {
        super();
    }

    public QoSServiceTest(String arg0) {
        super(arg0);
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        //suite.addTest(new QoSServiceTest("testAddDefaultQoS2Auftrag"));
        //suite.addTest(new QoSServiceTest("testSaveAuftragQoS"));
        suite.addTest(new QoSServiceTest("testFindQoS4Auftrag"));
        return suite;
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.QoSServiceImpl#addDefaultQoS2Auftrag(java.lang.Integer,
     * java.lang.Integer)}.
     */
    public void testAddDefaultQoS2Auftrag() {
        try {
            QoSService service = getCCService(QoSService.class);
            List<AuftragQoS> result = service.addDefaultQoS2Auftrag(Long.valueOf(178269), null, getSessionId());
            assertNotEmpty("Es wurden keine default QoS-Eintraege erstellt!", result);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.QoSServiceImpl#saveAuftragQoS(de.augustakom.hurrican.model.cc.AuftragQoS,
     * java.lang.Integer)}.
     */
    public void testSaveAuftragQoS() {
        try {
            QoSService service = getCCService(QoSService.class);

            AuftragQoS qos = new AuftragQoS();
            qos.setAuftragId(new Long(178269));
            qos.setQosClassRefId(new Long(10000));
            qos.setPercentage(new Integer(30));

            service.saveAuftragQoS(qos, getSessionId());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.QoSServiceImpl#findQoS4Auftrag(java.lang.Integer,
     * boolean)}.
     */
    public void testFindQoS4Auftrag() {
        try {
            QoSService service = getCCService(QoSService.class);
            List<AuftragQoS> result = service.findQoS4Auftrag(Long.valueOf(178269), false);
            assertNotEmpty("Es konnten keine QoS-Daten zum Auftrag ermittelt werden!", result);

            for (AuftragQoS qos : result) {
                LOGGER.debug("QoS ID: " + qos.getId() + " - Percentage: " + qos.getPercentage());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


