/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.07.2004 13:36:43
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.Anrede;
import de.augustakom.hurrican.model.cc.KundeNbz;
import de.augustakom.hurrican.model.cc.KundeNbzBuilder;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.model.shared.view.AnschriftView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCKundenService;

/**
 * Test fuer CCKundenService.
 */
@Test(groups = { "service" })
public class CCKundenServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CCKundenServiceTest.class);

    /**
     * Test fuer die Methode CCKundenService#findKundeAuftragViews4Kunde(Long, boolean)
     */
    @Test(enabled = false)
    public void testFindKundeAuftragViews4Kunde() {
        try {
            CCKundenService service = getCCKundenService();

            Long kNo = Long.valueOf(400008634);
            List<CCKundeAuftragView> views = service.findKundeAuftragViews4Kunde(kNo, true);
            assertNotEmpty(views, "Keine Auftragsdaten gefunden fuer K-No (orig) " + kNo);
            LOGGER.debug("Anzahl gefundener CC-Auftraege fuer Kunde " + kNo + ": " + views.size());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode CCKundenService#findAnrede(Integer, Integer)
     */
    @Test(enabled = false)
    public void testFindAnrede() {
        try {
            String key = Anrede.KEY_HERR;
            Long art = Long.valueOf(1);
            Anrede a1 = getCCKundenService().findAnrede(key, art);
            assertNotNull(a1, "Anrede wurde nicht gefunden!");
            assertEquals(a1.getAnrede(), "Falsche Anrede gefunden!", "Sehr geehrter Herr");

            art = Long.valueOf(2);
            Anrede a2 = getCCKundenService().findAnrede(key, art);
            assertNotNull(a2, "Anrede wurde nicht gefunden!");
            assertEquals(a2.getAnrede(), "Herrn", "Falsche Anrede gefunden!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test(enabled = false)
    public void testFindAnschrift() {
        try {
            AnschriftView av = getCCKundenService().findAnschrift(new Long(500008200));
            assertNotNull(av, "Keine AnschriftView gefunden!");

            av.debugModel(LOGGER);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode CCKundenService#findREAnschrift(Long, Long)
     */
    @Test(enabled = false)
    public void testFindREAnschrift() {
        try {
            AnschriftView result = getCCKundenService().findREAnschrift(
                    new Long(200000407), new Long(200000407));
            assertNotNull(result, "Keine AnschriftView gefunden!");

            result.debugModel(LOGGER);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testSaveKundeNbz() throws StoreException {
        KundeNbz kundeNbz = getBuilder(KundeNbzBuilder.class).withNbz("tes123").setPersist(false).build();

        getCCKundenService().saveKundeNbz(kundeNbz);
        flushAndClear();

        assertNotNull(kundeNbz, "kunde nbz is null after save");
        assertNotNull(kundeNbz.getId(), "no id after save");
    }

    public void testFindKundeNbzByNbzEmpty() throws FindException {
        assertEmpty(getCCKundenService().findKundeNbzByNbz("blupp"), "result incorrect");
    }

    public void testFindKundeNbzByNbz() throws FindException {
        KundeNbz kundeNbz1 = getBuilder(KundeNbzBuilder.class).withNbz("tes124").build();
        KundeNbz kundeNbz2 = getBuilder(KundeNbzBuilder.class).withNbz("tes124").build();
        flushAndClear();

        List<KundeNbz> result = getCCKundenService().findKundeNbzByNbz(kundeNbz1.getNbz());
        assertNotNull(result, "result incorrect");
        assertEquals(result.size(), 2, "2 items expected");
        for (KundeNbz kundeNbz : result) {
            KundeNbz expected = null;
            if (kundeNbz.getId().equals(kundeNbz1.getId())) {
                expected = kundeNbz1;
            }
            else if (kundeNbz.getId().equals(kundeNbz2.getId())) {
                expected = kundeNbz2;
            }

            if (expected == null) {
                fail("expected item not in list");
            }
            else {
                assertEquals(kundeNbz.getKundeNo(), expected.getKundeNo(), "kundeNo incorrect");
                assertEquals(kundeNbz.getNbz(), expected.getNbz(), "Nbz incorrect");
            }
        }
        ;
    }

    public void testFindKundeNbzByNo() throws FindException {
        KundeNbz kundeNbz = getBuilder(KundeNbzBuilder.class).withNbz("tes125").build();
        flushAndClear();

        KundeNbz result = getCCKundenService().findKundeNbzByNo(kundeNbz.getKundeNo());
        assertNotNull(result, "result incorrect");
        assertEquals(kundeNbz.getId(), result.getId(), "Id incorrect");
        assertEquals(kundeNbz.getKundeNo(), result.getKundeNo(), "kundeNo incorrect");
        assertEquals(kundeNbz.getNbz(), result.getNbz(), "Nbz incorrect");
    }

    public void testRemoveKundeNbz() throws FindException, DeleteException {
        KundeNbz kundeNbz = getBuilder(KundeNbzBuilder.class).withNbz("tes126").build();
        flushAndClear();

        getCCKundenService().removeKundeNbz(kundeNbz.getId());
        flushAndClear();

        KundeNbz result = getCCKundenService().findKundeNbzByNo(kundeNbz.getKundeNo());
        assertNull(result, "result incorrect");
    }

    private CCKundenService getCCKundenService() {
        return getCCService(CCKundenService.class);
    }
}


