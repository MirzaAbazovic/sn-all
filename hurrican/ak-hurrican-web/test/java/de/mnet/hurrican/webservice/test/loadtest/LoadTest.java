/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2009 10:19:10
 */
package de.mnet.hurrican.webservice.test.loadtest;

import java.io.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.mnet.hurrican.webservice.test.AbstractWebServiceClientTest;
import de.mnet.hurricanweb.loadtest.types.LoadTestRequest;
import de.mnet.hurricanweb.loadtest.types.LoadTestRequestDocument;


/**
 * Unit-Test, um einen LoadTest WebService auszufuehren.
 *
 *
 */
public class LoadTest extends AbstractWebServiceClientTest {

    private static final Logger LOGGER = Logger.getLogger(LoadTest.class);

    private String defaultURI = null;

    // Default Werte
    private Long customerNo = new Long(200000407);
    private Long orderNo = new Long(894874);
    private String reqType = "createSubscriber";

    /**
     * Main-Methode fuer den Start als 'herkoemmlichen' Java Client
     */
    public static void main(String[] args) {
        if (args.length > 1) {
            // Dauer-Test
            LoadTest tester = new LoadTest();
            tester.doThreadTest(args);
        }
        else {
            LoadTest tester = new LoadTest();
            if (args.length > 0) {
                tester.defaultURI = args[0];
            }

            // einfacher Test
            tester.test();
        }
    }

    /* Fuert den Test  */
    private void doThreadTest(String[] args) {
        while (true) {
            try {
                InputStream is = LoadTest.class.getResourceAsStream(
                        "/de/mnet/hurrican/webservice/test/loadtest/cps_sample_data.txt");

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();   // erste Zeile == Header
                while ((line = reader.readLine()) != null) {
                    String[] params = StringUtils.split(line, ";");

                    LoadTest tester = new LoadTest();
                    tester.defaultURI = args[0];
                    tester.customerNo = new Long(params[0]);
                    tester.orderNo = new Long(params[1]);
                    tester.reqType = params[2];

                    LoadTestThread thread = new LoadTestThread(tester);
                    thread.run();
                }

                Thread.sleep(new Integer(args[1]).intValue());
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /**
     * @see de.mnet.hurrican.webservice.test.AbstractWebServiceClientTest#customSendAndReceive(org.springframework.ws.client.core.WebServiceTemplate)
     */
    @Override
    protected void customSendAndReceive(WebServiceTemplate wsTemplate) {
        try {
            LoadTestRequestDocument doc = LoadTestRequestDocument.Factory.newInstance();
            LoadTestRequest req = doc.addNewLoadTestRequest();
            req.setCustomerNo(customerNo);
            req.setOrderNo(orderNo);
            req.setRequestType(reqType);

            Object result = wsTemplate.marshalSendAndReceive(doc);
            if (result instanceof LoadTestRequestDocument) {
                LoadTestRequestDocument reqDoc = (LoadTestRequestDocument) result;
                LoadTestRequest reqResult = reqDoc.getLoadTestRequest();
                LOGGER.debug("Tx-ID: " + reqResult.getTransactionId());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * @see de.mnet.hurrican.webservice.test.AbstractWebServiceClientTest#getDefaultURI()
     */
    @Override
    protected String getDefaultURI() {
        return defaultURI;
    }

    /**
     * @see de.mnet.hurrican.webservice.test.AbstractWebServiceClientTest#getURISuffix()
     */
    @Override
    protected String getURISuffix() {
        return "loadtest";
    }

    static class LoadTestThread implements Runnable {

        private LoadTest loadTest = null;

        public LoadTestThread(LoadTest tester) {
            this.loadTest = tester;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            loadTest.test();
        }

    }
}


