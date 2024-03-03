/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.03.2009 11:47:11
 */
package de.mnet.hurrican.webservice.test.alive;

import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.webservice.test.AbstractWebServiceClientTest;
import de.mnet.hurricanweb.alive.types.WSAliveRequest;
import de.mnet.hurricanweb.alive.types.WSAliveRequestDocument;

/**
 * Test-Client fuer den 'Alive'-WebService.
 *
 *
 */
@Test(groups = BaseTest.E2E)
public class AliveServiceTest extends AbstractWebServiceClientTest {

    private static final Logger LOGGER = Logger.getLogger(AliveServiceTest.class);

    /**
     * @see de.mnet.hurrican.webservice.test.AbstractWebServiceClientTest#getURISuffix()
     */
    protected String getURISuffix() {
        return "alive";
    }

    /**
     * @see de.mnet.hurrican.webservice.test.AbstractWebServiceClientTest#customSendAndReceive()
     */
    protected void customSendAndReceive(WebServiceTemplate wsTemplate) {
        try {
            WSAliveRequestDocument doc = WSAliveRequestDocument.Factory.newInstance();
            WSAliveRequest req = doc.addNewWSAliveRequest();
            req.setAlive("alive");

            LOGGER.debug("doc: " + doc.getClass().getName());

            Object result = wsTemplate.marshalSendAndReceive(doc);

            if (result instanceof WSAliveRequest) {
                WSAliveRequest retVal = (WSAliveRequest) result;
                LOGGER.debug("Request alive: " + retVal.getAlive());
            }
            else {
                LOGGER.debug("Result: " + result);
            }
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}


