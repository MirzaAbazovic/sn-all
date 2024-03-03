/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 09:12:46
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AbteilungBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.NiederlassungBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.AbstractBauauftragView;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.model.cc.view.SimpleVerlaufView;
import de.augustakom.hurrican.model.cc.view.VerlaufUniversalView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CreateVerlaufParameter;

/**
 * Unit-Test fuer BAServiceImpl.
 *
 *
 */
@Test
public class BAServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(BAServiceTest.class);

    @Test(enabled = false, groups = { BaseTest.SLOW })
    public void testCreateVerlaufNeu() throws Exception {

        NiederlassungBuilder niederlassungBuilder = getBuilder(NiederlassungBuilder.class);
        getBuilder(AbteilungBuilder.class)
                .withNiederlassungBuilder(niederlassungBuilder).build();
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragart(BAVerlaufAnlass.INTERN_WORK)
                .withNiederlassungBuilder(niederlassungBuilder);

        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withElVerlauf(true);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withAuftragNoOrig(null)
                .withProdBuilder(produktBuilder);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withAuftragDatenBuilder(auftragDatenBuilder);

        Verlauf verlauf = getBuilder(VerlaufBuilder.class).withAuftragBuilder(auftragBuilder)
                .setPersist(false).build();

        flushAndClear();

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, 5);

        BAService service = getCCService(BAService.class);
        Pair<Verlauf, AKWarnings> result = service.createVerlauf(new CreateVerlaufParameter(
                verlauf.getAuftragId(), cal.getTime(), verlauf.getAnlass(), null, false, getSessionId(), null));
        assertNotNull(result.getFirst(), "got empty result for creating Verlauf");
        assertTrue(result.getSecond().isEmpty(), "got warnings for creating Verlauf");

        flushAndClear();

        // TODO
        // 1. in NiederlasssungServiceImpl:156 unterstützt
        // "AuftragTechnik at = aService.findAuftragTechnikByAuftragId(auftragId);" keine Transaktionen
        // 2. findNL4Verteilung schlägt fehl
        // 3. Asserts einbauen
    }

    @Test(groups = { BaseTest.SLOW })
    public void testVerlaufStornieren() throws Exception {
        assertNotNull(getSessionId(), "keine SessionId vorhanden");

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withRandomId();
        getBuilder(AuftragDatenBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .build();

        Verlauf verlauf = getBuilder(VerlaufBuilder.class)
                .withAuftragBuilder(auftragBuilder)
                .withAkt(true).build();

        BAService service = getCCService(BAService.class);
        AKWarnings warnings = service.verlaufStornieren(verlauf.getId(), false, getSessionId());

        flushAndClear();

        Verlauf result = service.findVerlauf(verlauf.getId());

        assertNull(warnings, "Beim BA-Storno traten Warnungen auf!");
        assertEquals(result.getVerlaufStatusId(), VerlaufStatus.VERLAUF_STORNIERT,
                "Status des Verlaufs nicht richtig gesetzt");
    }

    @Test(groups = { BaseTest.SLOW })
    public void testObserveProcess() throws Exception {
        Verlauf verlauf = getBuilder(VerlaufBuilder.class).withAkt(true).build();

        BAService service = getCCService(BAService.class);
        service.observeProcess(verlauf.getId());

        flushAndClear();

        Verlauf result = service.findVerlauf(verlauf.getId());

        assertNotNull(result, "Verlauf wurde nicht gefunden!");
        assertEquals(result.getObserveProcess(), Boolean.TRUE,
                "Verlauf nicht als <observed> markiert!");
    }

    @Test(groups = { BaseTest.SLOW })
    public void testFindActVerlauf4Auftrag() throws Exception {
        Set<Long> subOrders = new HashSet<Long>();

        AuftragBuilder subOrderBuilder1 = getBuilder(AuftragBuilder.class).withRandomId();
        AuftragBuilder subOrderBuilder2 = getBuilder(AuftragBuilder.class).withRandomId();

        subOrders.add(subOrderBuilder1.get().getAuftragId());
        subOrders.add(subOrderBuilder2.get().getAuftragId());

        Verlauf verlauf = getBuilder(VerlaufBuilder.class).withAnlass(BAVerlaufAnlass.ABSAGE)
                .withSubAuftragsIds(subOrders)
                .withAkt(true).build();
        flushAndClear();

        BAService service = getCCService(BAService.class);
        Verlauf result = service.findActVerlauf4Auftrag(verlauf.getAuftragId(), false);

        assertNotNull(result, "Verlauf nicht gefunden!");
        assertEquals(result.getAkt(), Boolean.TRUE, "anlass nicht korrekt");
        assertEquals(result.getAnlass(), BAVerlaufAnlass.ABSAGE, "anlass nicht korrekt");
    }

    @Test(groups = { BaseTest.SLOW })
    public void testFindActVerlauf4AuftragInSubOrder() throws Exception {
        Set<Long> subOrders = new HashSet<Long>();

        AuftragBuilder subOrderBuilder1 = getBuilder(AuftragBuilder.class).withRandomId();
        AuftragBuilder subOrderBuilder2 = getBuilder(AuftragBuilder.class).withRandomId();

        subOrders.add(subOrderBuilder1.get().getAuftragId());
        subOrders.add(subOrderBuilder2.get().getAuftragId());

        getBuilder(VerlaufBuilder.class)
                .withAnlass(BAVerlaufAnlass.ABSAGE)
                .withSubAuftragsIds(subOrders)
                .withAkt(true).build();
        flushAndClear();

        BAService service = getCCService(BAService.class);
        Verlauf result = service.findActVerlauf4Auftrag(subOrderBuilder1.get().getAuftragId(), false);

        assertNotNull(result, "Verlauf nicht gefunden!");
        assertEquals(result.getAkt(), Boolean.TRUE, "anlass nicht korrekt");
        assertEquals(result.getAnlass(), BAVerlaufAnlass.ABSAGE, "anlass nicht korrekt");
        assertTrue(NumberTools.notEqual(result.getAuftragId(), subOrderBuilder1.get().getAuftragId()));
    }

    @Test(groups = { BaseTest.SLOW })
    public void testFindVerlaeufe4Auftrag() throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        Verlauf verlauf1 = getBuilder(VerlaufBuilder.class).withAuftragBuilder(auftragBuilder)
                .withAkt(true).build();
        Verlauf verlauf2 = getBuilder(VerlaufBuilder.class).withAuftragBuilder(auftragBuilder)
                .withAkt(false).build();
        flushAndClear();

        assertEquals(verlauf1.getAuftragId(), verlauf2.getAuftragId(), "AuftragsIds nicht gleich");

        BAService service = getCCService(BAService.class);
        List<Verlauf> result = service.findVerlaeufe4Auftrag(verlauf1.getAuftragId());
        assertNotEmpty(result, "kein Verlauf gefunden");
        assertEquals(result.size(), 2, "zwei Verläuf erwartet");

        Verlauf result1 = result.get(1);
        Verlauf result2 = result.get(0);
        assertEquals(result1.getId(), verlauf1.getId(), "Verläufe nicht identisch");
        assertEquals(result1.getAkt(), Boolean.TRUE, "Verlauf akt nicht ok");
        assertEquals(result2.getId(), verlauf2.getId(), "Verläufe nicht identisch");
        assertEquals(result2.getAkt(), Boolean.FALSE, "Verlauf akt nicht ok");
    }

    @Test(groups = { BaseTest.SLOW })
    public void testFindVerlaeufe4AuftragSubAuftraege() throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        Auftrag auftrag2 = getBuilder(AuftragBuilder.class).build();
        Verlauf verlauf1 = getBuilder(VerlaufBuilder.class).withAuftragBuilder(auftragBuilder)
                .withAkt(true).withSubAuftragsIds(ImmutableSet.of(auftrag2.getAuftragId())).build();
        Verlauf verlauf2 = getBuilder(VerlaufBuilder.class).withAuftragBuilder(auftragBuilder)
                .withAkt(false).build();
        flushAndClear();

        assertEquals(verlauf1.getAuftragId(), verlauf2.getAuftragId(), "AuftragsIds nicht gleich");

        BAService service = getCCService(BAService.class);
        List<Verlauf> result = service.findVerlaeufe4Auftrag(auftrag2.getAuftragId());
        assertNotEmpty(result, "kein Verlauf gefunden");

        Verlauf verlauf = Iterables.getOnlyElement(result);
        assertEquals(verlauf.getId(), verlauf1.getId(), "Verläufe nicht identisch");
        assertNotEquals(verlauf.getAuftragId(), auftrag2.getAuftragId());
    }

    @Test(groups = { BaseTest.SLOW })
    public void testFindActVerlaeufe() throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        Verlauf verlauf1 = getBuilder(VerlaufBuilder.class).withAuftragBuilder(auftragBuilder)
                .withAkt(true).withProjektierung(true).build();
        Verlauf verlauf2 = getBuilder(VerlaufBuilder.class).withAuftragBuilder(auftragBuilder)
                .withAkt(false).withProjektierung(true).build();
        flushAndClear();

        assertEquals(verlauf1.getAuftragId(), verlauf2.getAuftragId(), "AuftragsIds nicht gleich");

        BAService service = getCCService(BAService.class);
        List<Verlauf> result = service.findActVerlaeufe(null, true);
        assertNotEmpty(result, "kein Verlauf gefunden");
    }


    @DataProvider(name = "findBAVerlaufViews4AbtAktDP")
    public Object[][] findBAVerlaufViews4AbtAktDP() {
        return new Object[][] {
                { false, Abteilung.ST_CONNECT },
                { true, Abteilung.ZP_ZENTRALE_IP_SYSTEME },
        };
    }

    @Test(dataProvider = "findBAVerlaufViews4AbtAktDP", groups = BaseTest.SLOW)
    public void testFindBAVerlaufViews4AbtAkt(boolean useUniversalQuery, Long abteilungId) throws Exception {
        VerlaufAbteilungBuilder vaExpectedInResult = createVerlaufAbteilung(
                true, VerlaufStatus.STATUS_IM_UMLAUF, abteilungId, new Date());
        VerlaufAbteilungBuilder vaNotExpectedInResultBecauseNotBaActive = createVerlaufAbteilung(
                false, VerlaufStatus.STATUS_IM_UMLAUF, abteilungId, new Date());
        VerlaufAbteilungBuilder vaNotExpectedInResultBecauseOtherAbteilung = createVerlaufAbteilung(
                true, VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.DISPO, new Date());
        flushAndClear();

        List<AbstractBauauftragView> result = getCCService(BAService.class)
                .findBAVerlaufViews4Abt(useUniversalQuery, abteilungId, false, null, null);
        assertTrue(result.size() >= 1, "Mindestens EIN Bauauftrags-Verlauf erwartet");

        // check that all Verlauf-Abteilung are available or not
        boolean found = false;
        for (AbstractBauauftragView view : result) {
            if (equalsVerlaufAbteilung(vaExpectedInResult, view)) {
                found = true;
            }
            if ((equalsVerlaufAbteilung(vaNotExpectedInResultBecauseNotBaActive, view))
                    || (equalsVerlaufAbteilung(vaNotExpectedInResultBecauseOtherAbteilung, view))) {
                fail("found unexpected Verlauf-Abteilung in result");
            }

            if (useUniversalQuery) {
                assertTrue(view instanceof VerlaufUniversalView);
            }
        }
        assertTrue(found, "expected to found Verlauf-Abteilung in result");
    }

    @Test(groups = { BaseTest.SLOW })
    public void testFindBAVerlaufViews4AbtRealisiuerng() throws Exception {

        Date realisierungFrom = DateTools.createDate(2010, 5, 15);
        Date realisierungTo = DateTools.createDate(2010, 5, 20);

        Date realisierungsdatum1 = DateTools.createDate(2010, 5, 15);
        Date realisierungsdatum2 = DateTools.createDate(2010, 5, 20);
        Date realisierungsdatum3 = DateTools.createDate(2010, 5, 17);
        Date realisierungsdatum4 = DateTools.createDate(2010, 5, 21);

        VerlaufAbteilungBuilder verlaufAbteilungBuilder1 = createVerlaufAbteilung(
                true, VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.ST_ONLINE, realisierungsdatum1);
        VerlaufAbteilungBuilder verlaufAbteilungBuilder2 = createVerlaufAbteilung(
                false, VerlaufStatus.STATUS_IN_BEARBEITUNG, Abteilung.ST_ONLINE, realisierungsdatum2);
        VerlaufAbteilungBuilder verlaufAbteilungBuilder3 = createVerlaufAbteilung(
                false, VerlaufStatus.STATUS_ERLEDIGT, Abteilung.ST_ONLINE, realisierungsdatum3);
        VerlaufAbteilungBuilder verlaufAbteilungBuilder4 = createVerlaufAbteilung(
                false, VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.ST_ONLINE, realisierungsdatum1);
        VerlaufAbteilungBuilder verlaufAbteilungBuilder5 = createVerlaufAbteilung(
                true, VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.ST_ONLINE, realisierungsdatum4);
        VerlaufAbteilungBuilder verlaufAbteilungBuilder6 = createVerlaufAbteilung(
                true, VerlaufStatus.STATUS_IM_UMLAUF, Abteilung.FIELD_SERVICE, realisierungsdatum3);
        flushAndClear();

        List<VerlaufAbteilungBuilder> expectedToFound = new ArrayList<VerlaufAbteilungBuilder>();
        expectedToFound.add(verlaufAbteilungBuilder1);
        expectedToFound.add(verlaufAbteilungBuilder2);
        expectedToFound.add(verlaufAbteilungBuilder3);
        expectedToFound.add(verlaufAbteilungBuilder4);

        List<VerlaufAbteilungBuilder> expectedNotToFound = new ArrayList<VerlaufAbteilungBuilder>();
        expectedNotToFound.add(verlaufAbteilungBuilder5);
        expectedNotToFound.add(verlaufAbteilungBuilder6);

        List<AbstractBauauftragView> result = getCCService(BAService.class)
                .findBAVerlaufViews4Abt(false, Abteilung.ST_ONLINE, false, realisierungFrom, realisierungTo);
        assertTrue(result.size() >= expectedToFound.size(), "Mindestanzahl an Bauauftrags-Verläufe nicht erfüllt");

        // check that all Verlauf-Abteilung are available or not
        for (AbstractBauauftragView view : result) {
            for (Iterator<VerlaufAbteilungBuilder> iterator = expectedToFound.iterator(); iterator.hasNext(); ) {
                VerlaufAbteilungBuilder verlaufAbteilungBuilder = iterator.next();
                if (equalsVerlaufAbteilung(verlaufAbteilungBuilder, view)) {
                    iterator.remove();
                }
            }
            for (Iterator<VerlaufAbteilungBuilder> iterator = expectedToFound.iterator(); iterator.hasNext(); ) {
                VerlaufAbteilungBuilder verlaufAbteilungBuilder = iterator.next();
                if (equalsVerlaufAbteilung(verlaufAbteilungBuilder, view)) {
                    fail("found unexpected Verlauf-Abteilung in result");
                }
            }
        }
        assertEquals(expectedToFound.size(), 0, "expected to found more Verlauf-Abteilung(en) in result");
    }

    private VerlaufAbteilungBuilder createVerlaufAbteilung(Boolean akt, Long verlaufStatusId, Long abteilungId,
            Date realisierungsdatum) {
        VerlaufBuilder verlaufBuilder = getBuilder(VerlaufBuilder.class)
                .withAuftragBuilder(getCCService(AuftragBuilder.class).withAuftragTechnikBuilder(
                        getCCService(AuftragTechnikBuilder.class)))
                .withAkt(akt).withVerlaufStatusId(verlaufStatusId);
        VerlaufAbteilungBuilder verlaufAbteilungBuilder = getBuilder(VerlaufAbteilungBuilder.class)
                .withVerlaufBuilder(verlaufBuilder)
                .withAbteilungId(abteilungId).withRealisierungsdatum(realisierungsdatum)
                .withVerlaufStatusId(verlaufStatusId);
        verlaufAbteilungBuilder.build();
        return verlaufAbteilungBuilder;
    }

    private boolean equalsVerlaufAbteilung(VerlaufAbteilungBuilder vaBuilder, AbstractBauauftragView baView) {
        return (baView.getVerlaufId().equals(vaBuilder.get().getVerlaufId()))
                && (baView.getVerlaufAbtId().equals(vaBuilder.get().getId()))
                && (baView.getAuftragId().equals(vaBuilder.getVerlaufBuilder().get().getAuftragId()));
    }

    @Test(enabled = false, groups = { BaseTest.SLOW })
    // TODO rewrite Test
    public void testFindProjektierungen4Abt() throws Exception {
        BAService service = getCCService(BAService.class);

        Long abtId = Abteilung.DISPO;
        List<ProjektierungsView> result = service.findProjektierungen4Abt(abtId, false);
        assertNotEmpty(result, "Es wurden keine Projektierungs-Verlaeufe fuer Abt-ID " + abtId
                + " gefunden!");
        LOGGER.debug("Anzahl gefundener Projektierungen: " + result.size());

        for (ProjektierungsView view : result) {
            LOGGER.debug("  >> Verlauf-Id: " + view.getVerlaufId());
        }
    }

    @Test(groups = { BaseTest.SLOW })
    public void testFindSimpleVerlaufViews4Auftrag() throws Exception {
        Set<Long> subOrders = new HashSet<Long>();

        AuftragBuilder subOrderBuilder1 = getBuilder(AuftragBuilder.class).withRandomId();
        AuftragBuilder subOrderBuilder2 = getBuilder(AuftragBuilder.class).withRandomId();

        subOrders.add(subOrderBuilder1.get().getAuftragId());
        subOrders.add(subOrderBuilder2.get().getAuftragId());

        Verlauf verlauf = getBuilder(VerlaufBuilder.class)
                .withAnlass(BAVerlaufAnlass.ABSAGE)
                .withSubAuftragsIds(subOrders)
                .withAkt(true).build();
        flushAndClear();

        BAService service = getCCService(BAService.class);
        List<SimpleVerlaufView> result = service.findSimpleVerlaufViews4Auftrag(verlauf.getAuftragId());
        assertNotEmpty(result, "Keine Bauauftraege gefunden!");
        assertEquals(result.size(), 1, "Anzahl gefundener BAs nicht korrekt!");

        List<SimpleVerlaufView> resultSub = service.findSimpleVerlaufViews4Auftrag(subOrderBuilder2.get().getAuftragId());
        assertNotEmpty(resultSub, "Keine Bauauftraege gefunden!");
        assertEquals(resultSub.size(), 1, "Anzahl gefundener BAs nicht korrekt!");
    }

}
