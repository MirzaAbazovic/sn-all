/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2004 14:47:18
 */
package de.augustakom.hurrican.service.cc.impl;

import java.io.*;
import java.text.*;
import java.util.*;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * JUnit-Test fuer Performance-Messungen.
 *
 *
 */
@Ignore
public class PerformanceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(PerformanceTest.class);

    private static StringBuilder logFile = null;
    private static boolean logFileCreated = false;
    private static String logFileName = null;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initLogFile4Performance();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        FileWriter fw = new FileWriter(new File(logFileName));
        fw.write(logFile.toString());
        fw.flush();
        fw.close();

        super.tearDown();
    }

    /* Erstellt ein File fuer Performance-Messungen. */
    private void initLogFile4Performance() throws IOException {
        if (!logFileCreated) {
            logFileCreated = true;
            String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File f = new File(SystemUtils.USER_HOME, "HurricanPerformace_" + date + ".log");
            logFileName = f.getAbsolutePath();
            FileWriter fw = new FileWriter(f);
            fw.flush();
            fw.close();
        }

        logFile = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(logFileName));
        String line = null;
        while ((line = reader.readLine()) != null) {
            logFile.append(line);
            logFile.append(SystemUtils.LINE_SEPARATOR);
        }
    }

    /**
     * Test, der nur die Hurrican-DB abfraegt.
     */
    public void testOnlyHurrican() {
        try {
            appendMessage("****************************************", true);
            appendMessage("Hurrican-DB abfragen:", true);
            ProduktService ps = getCCService(ProduktService.class);

            long time1 = 0;
            for (int i = 0; i < 5; i++) {
                long begin = System.currentTimeMillis();
                List produkte = ps.findProdukte(false);
                long diff = System.currentTimeMillis() - begin;
                assertNotEmpty("Es wurden keine Produkte gefunden!", produkte);
                appendMessage("Dauer 'Abfrage aller Produkte': " + diff + " ms", true);
                time1 += diff;
            }
            appendMessage("      Durchschnitt: " + (time1 / 5) + " ms", true);

            long time2 = 0;
            for (int i = 0; i < 5; i++) {
                Long auftragId = Long.valueOf(47225);
                long begin = System.currentTimeMillis();
                ProduktGruppe pg = ps.findPG4Auftrag(auftragId);
                long diff = System.currentTimeMillis() - begin;
                assertNotNull("ProduktGruppe fuer Auftrag '47225' nicht gefunden!", pg);
                appendMessage("Dauer 'ProduktGruppe fuer Auftrag' (47225): " + diff + " ms", true);
                time2 += diff;
            }
            appendMessage("      Durchschnitt: " + (time2 / 5) + " ms", true);
            newLine();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            appendMessage(e.getMessage(), true);
        }
    }

    /**
     * Test, der nur die Billing-DB abfraegt.
     */
    public void testOnlyBilling() {
        try {
            appendMessage("****************************************", true);
            appendMessage("Billing-DB abfragen:", true);

            KundenService ks = getBillingService(KundenService.class);

            long times = 0;
            for (int i = 0; i < 5; i++) {
                KundeQuery query = new KundeQuery();
                query.setName("Maier");
                long begin = System.currentTimeMillis();
                List kunden = ks.findKundeAdresseViewsByQuery(query);
                long diff = System.currentTimeMillis() - begin;
                assertNotEmpty("Es wurden keine Kunden mit Name 'beer' gefunden!", kunden);
                appendMessage("Dauer 'Kunden-Adressen suchen' - Name='Maier': " + diff + " ms", true);
                times += diff;
            }
            appendMessage("      Durchschnitt: " + (times / 5) + " ms", true);

            newLine();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            appendMessage(e.getMessage(), true);
        }
    }

    /**
     * Test, der sowohl die Billing- als auch die Hurrican-DB abfraegt.
     */
    public void testBillingAndHurrican() {
        try {
            appendMessage("****************************************", true);
            appendMessage("Billing- und Hurrican-DB abfragen:", true);

            CCAuftragService ccAS = getCCService(CCAuftragService.class);

            long times = 0;
            for (int i = 0; i < 5; i++) {
                AuftragEndstelleQuery query = new AuftragEndstelleQuery();
                query.setVbz("TR*-2004");
                long begin = System.currentTimeMillis();
                List result = ccAS.findAuftragEndstelleViews(query);
                long diff = System.currentTimeMillis() - begin;
                assertNotEmpty("Es wurden keine Auftrags-Endstellen-Daten gefunden!", result);
                appendMessage("Dauer 'Auftrags-Endstellen suchen' - VerbindungsBezeichnung='TR*-2004': " + diff + " ms", true);
                times += diff;
            }
            appendMessage("      Durchschnitt: " + (times / 5) + " ms", true);

            newLine();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            appendMessage(e.getMessage(), true);
        }
    }

    /* Haengt die Message an das Log-File an. */
    private void appendMessage(String text, boolean newLine) {
        logFile.append(text);
        if (newLine) {
            newLine();
        }
    }

    private void newLine() {
        logFile.append(SystemUtils.LINE_SEPARATOR);
    }
}


