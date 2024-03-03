/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.06.2004 13:32:02
 */
package de.augustakom.hurrican.service.billing.impl;

import java.util.*;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;
import org.junit.Ignore;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.billing.AddressFormat;
import de.augustakom.hurrican.model.billing.Adresse;
import de.augustakom.hurrican.model.billing.Ansprechpartner;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.billing.query.KundeQuery;
import de.augustakom.hurrican.model.billing.view.KundeAdresseView;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.model.shared.view.DefaultSharedAuftragView;
import de.augustakom.hurrican.service.AbstractHurricanServiceTestCase;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;


/**
 * TestCase fuer die Service-Implementierung <code>KundeServiceImpl</code>
 *
 *
 */
@Ignore
public class KundeServiceTest extends AbstractHurricanServiceTestCase {

    private static final Logger LOGGER = Logger.getLogger(KundeServiceTest.class);

    public KundeServiceTest() {
        super();
    }

    public KundeServiceTest(String name) {
        super(name);
    }

    /**
     * Suite-Definition fuer den TestCase
     *
     * @return
     *
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        //        suite.addTest(new KundeServiceTest("testFindKunde"));
        //        suite.addTest(new KundeServiceTest("testFindByKundeNos"));
        //        suite.addTest(new KundeServiceTest("testFindKundenByKundeNos"));
        //        suite.addTest(new KundeServiceTest("testFindKundeAdresseViewsByQuery"));
        //        suite.addTest(new KundeServiceTest("testFindAdressen4Kunden"));
        //        suite.addTest(new KundeServiceTest("testFormatAddress"));
        suite.addTest(new KundeServiceTest("testGetAnsprechpartner4Kunden"));
        //        suite.addTest(new KundeServiceTest("testFindKundeNos4HauptKunde"));
        //        suite.addTest(new KundeServiceTest("testFindKundenNamen"));
        //        suite.addTest(new KundeServiceTest("testGetAdresseByAdressNo"));
        //        suite.addTest(new KundeServiceTest("testLoadKundendaten4AuftragViews"));
        //        suite.addTest(new KundeServiceTest("testIsSameKundenkreis"));

        return suite;
    }

    /**
     * Test fuer die Methode KundenService#findKunde(java.lang.Long)
     */
    public void testFindKunde() {
        try {
            KundenService ks = getBillingService(KundenService.class);
            Kunde result = ks.findKunde(new Long(200000407));
            assertNotNull("Kunde nicht gefunden!", result);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode KundenService#findByKundeNos(List<Long>)
     */
    public void testFindByKundeNos() {
        try {
            KundenService ks = getBillingService(KundenService.class);
            List<Long> kNos = new ArrayList<Long>();
            kNos.add(new Long(100011475));
            kNos.add(new Long(100000014));
            kNos.add(new Long(100000012));
            kNos.add(new Long(100000009));
            kNos.add(new Long(100011475));  // doppelt aufgenommen fuer Test

            List<Kunde> result = ks.findByKundeNos(kNos);
            assertNotEmpty("Es wurden keine Kunden gefunden!", result);
            assertEquals("Die Anzahl der gefundenen Kunden stimmt nicht!", 4, result.size());

            for (Kunde k : result) {
                LOGGER.debug("Kunde: " + k.getKundeNo() + "  -  " + k.getName());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for KundenService#findKundenByKundeNos(java.lang.Long[])
     */
    public void testFindKundenByKundeNos() {
        try {
            Long[] kNoOrigs = new Long[2];
            kNoOrigs[0] = new Long(200000407);
            kNoOrigs[1] = new Long(200002541);

            KundenService ks = getBillingService(KundenService.class);
            Map<Long, Kunde> result = ks.findKundenByKundeNos(kNoOrigs);
            assertNotNull("Kein Result erhalten!", result);
            assertNotNull("Kein Result fuer Kunde erhalten: " + kNoOrigs[0], result.get(kNoOrigs[0]));
            assertNotNull("Kein Result fuer Kunde erhalten: " + kNoOrigs[1], result.get(kNoOrigs[1]));

            Kunde k1 = result.get(kNoOrigs[0]);
            Kunde k2 = result.get(kNoOrigs[1]);
            LOGGER.debug("200000407: " + k1.getKundeNo() + "  " + k1.getName());
            LOGGER.debug("200002541: " + k2.getKundeNo() + "  " + k2.getName());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode KundeServiceImpl#findKundeAdresseViewsByQuery(KundeQuery)
     */
    public void testFindKundeAdresseViewsByQuery() {
        try {
            KundenService service = getBillingService(KundenService.class);
            KundeQuery query = new KundeQuery();
            //query.setName("Beer");
            //query.setVorname("Alexander");
            query.setOrt("Augsburg");
            query.setStrasse("martini*");
            //query.setKundeNo(new Integer(200000407));

            List<KundeAdresseView> result = service.findKundeAdresseViewsByQuery(query);
            assertNotEmpty("Mit der Query-Definition konnten keine Kunden gefunden werden!", result);
            LOGGER.debug("---------> anzahl kunden: " + result.size());
            for (int i = 0; i < result.size(); i++) {
                ((DebugModel) result.get(i)).debugModel(LOGGER);
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * Method for KundenService#findAdressen4Kunden(List)
     */
    public void testFindAdressen4Kunden() {
        try {
            List<Long> kNoOrigs = new ArrayList<Long>();
            kNoOrigs.add(new Long(200000407));
            kNoOrigs.add(new Long(200002541));

            KundenService service = getBillingService(KundenService.class);
            List<Adresse> result = service.findAdressen4Kunden(kNoOrigs);
            assertNotEmpty("Keine Adressen gefunden!", result);

            for (Adresse adr : result) {
                adr.debugModel(LOGGER);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * Test fuer die Methode KundenService#formatAddress(..)
     */
    public void testFormatAddress() {
        try {
            KundenService service = getBillingService(KundenService.class);
            Adresse address = service.getAdresse4Kunde(new Long(400018275));
            String[] formatted = service.formatAddress(address, AddressFormat.FORMAT_DEFAULT);
            assertNotNull("Kein Result erhalten!", formatted);

            LOGGER.debug("************ ADRESSE ***************");
            for (int i = 0; i < formatted.length; i++) {
                LOGGER.debug(formatted[i]);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode KundeService#getAnsprechpartner4Kunde(Long)
     */
    public void testGetAnsprechpartner4Kunden() {
        try {
            KundenService service = getBillingService(KundenService.class);

            List<Long> kNoOrigs = new ArrayList<Long>();
            kNoOrigs.add(new Long(200000407));
            kNoOrigs.add(new Long(200002541));

            List<Ansprechpartner> result = service.getAnsprechpartner4Kunden(kNoOrigs);
            assertNotEmpty("Keine Ansprechpartner fuer die Kunden gefunden! K-No: " + kNoOrigs, result);
            assertEquals("Anzahl der gefunden Ansprechpartner ungueltig!", 2, result.size());

            for (Ansprechpartner ansp : result) {
                LOGGER.debug(ansp.getShortInfo());
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * Test fuer die Methode KundeService#getAdresse4Kunde(Long)
     */
    public void testGetAdresse4Kunde() {
        try {
            KundenService service = getBillingService(KundenService.class);
            Long kNo = new Long(200000407);
            Adresse result = service.getAdresse4Kunde(kNo);
            assertNotNull("Adresse fuer einen Kunden wurde nicht gefunden! K-No: " + kNo, result);
            LOGGER.debug("Adresse f. Kunde " + kNo);
            LOGGER.debug("   ad.-no.: " + result.getAdresseNo());
            LOGGER.debug("   strasse: " + result.getStrasse());

            Long kNoNoMatch = new Long(0);
            Adresse resultNoMatch = service.getAdresse4Kunde(kNoNoMatch);
            assertNull("Adresse fuer K.-No gefunden, die es nicht geben sollte!", resultNoMatch);
        }
        catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * Test fuer die Methode KundenService#findKundeNos4HauptKunde(Long)
     */
    public void testFindKundeNos4HauptKunde() {
        try {
            KundenService service = getBillingService(KundenService.class);
            Long hNo = new Long(100010659);
            List<Long> result = service.findKundeNos4HauptKunde(hNo);
            assertNotEmpty("Keine Kunden zu Haupt-Kunde " + 100010659 + " gefunden!!", result);
            LOGGER.debug("Count: " + result.size());
            for (Long hno : result) {
                LOGGER.debug("  KUNDE: " + hno);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * Test fuer die Methode KundenService#findKundenNamen(List<Long>)
     */
    public void testFindKundenNamen() {
        try {
            KundenService service = getBillingService(KundenService.class);

            List<Long> kNoOrigs = new ArrayList<Long>();
            kNoOrigs.add(new Long(200000407));
            kNoOrigs.add(new Long(100000015));
            kNoOrigs.add(new Long(500000007));
            kNoOrigs.add(new Long(400000171));
            kNoOrigs.add(new Long(200000832));

            Map result = service.findKundenNamen(kNoOrigs);
            assertNotNull("Es wurden keine Namen zu den Kunden-Nos gefunden!", result);

            Set keySet = result.keySet();
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                LOGGER.debug("Kunde__No: " + next + " - Name: " + result.get(next));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.billing.impl.KundenServiceImpl#getAdresseByAdressNo(java.lang.Long)}.
     */
    public void testGetAdresseByAdressNo() {
        try {
            KundenService ks = getBillingService(KundenService.class);
            Adresse result = ks.getAdresseByAdressNo(new Long(5976));

            assertNotNull("Adresse konnte nicht ermittelt werden!", result);
            result.debugModel(LOGGER);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.billing.impl.KundenServiceImpl#loadKundendaten4AuftragViews(java.util.List)}.
     */
    public void testLoadKundendaten4AuftragViews() {
        try {
            KundenService ks = getBillingService(KundenService.class);

            List<DefaultSharedAuftragView> views = new ArrayList<DefaultSharedAuftragView>();

            AuftragDatenView adv = new AuftragDatenView();
            adv.setKundeNo(new Long(200000407));
            views.add(adv);

            ks.loadKundendaten4AuftragViews(views);

            assertNotNull("Kundenname wurde nich geladen!", adv.getName());
            adv.debugModel(LOGGER);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.billing.impl.KundenServiceImpl#isSameKundenkreis(java.lang.Long,
     * java.lang.Long)}.
     */
    public void testIsSameKundenkreis() {
        try {
            KundenService ks = getBillingService(KundenService.class);

            Long kundeNoA = new Long(200002541);
            Long kundeNoB = new Long(100010659);  // Hauptkunde von A
            Long kundeNoC = new Long(100011368);  // keine Beziehung zu A

            boolean resultA = ks.isSameKundenkreis(kundeNoA, kundeNoB);
            boolean resultB = ks.isSameKundenkreis(kundeNoA, kundeNoC);

            assertTrue("Kundenkreis wurde nicht richtig ermittelt; Kunden: " + kundeNoA + "/" + kundeNoB, resultA);
            assertTrue("Kundenkreis wurde nicht richtig ermittelt; Kunden: " + kundeNoA + "/" + kundeNoC, !resultB);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}


