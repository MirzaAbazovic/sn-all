/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.08.2008 09:02:13
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.dao.cc.EndstelleLtgDatenDAO;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleAnsprechpartner;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.Leitungsart;
import de.augustakom.hurrican.model.cc.LeitungsartBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.SchnittstelleBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.tools.AuftragFttb_hSuFAsFttbBuilder;
import de.augustakom.hurrican.model.tools.AuftragFttb_hSuFAsFtthBuilder;
import de.augustakom.hurrican.model.tools.StandortFttb_hBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;

/**
 * Test-Case fuer <code>EndstellenService</code>.
 *
 *
 */
@Test(groups = BaseTest.SERVICE)
public class EndstellenServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(EndstellenServiceTest.class);

    @Autowired
    private EndstelleLtgDatenDAO endstelleLtgDatenDao;

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#createEndstellen(de.augustakom.hurrican.model.cc.AuftragTechnik,
     * java.lang.Integer)}.
     *
     * @throws StoreException
     */
    public void testCreateEndstellenNoEndstellen() throws StoreException {
        EndstellenService endstellenService = getEndstellenService();
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class).build();

        List<Endstelle> noEndstellen = endstellenService.createEndstellen(auftragTechnik,
                Produkt.ES_TYP_KEINE_ENDSTELLEN, -1L);

        assertNull(noEndstellen, "Es wurden Endstellen angelegt, obwohl dies nicht geschehen sollte!");
    }

    public void testCreateEndstellenAUndB() throws StoreException {
        EndstellenService endstellenService = getEndstellenService();
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class).build();

        List<Endstelle> endstellenAUndB = endstellenService.createEndstellen(auftragTechnik, Produkt.ES_TYP_A_UND_B,
                -1L);

        assertNotNull(endstellenAUndB, "Die Endstellen A+B wurden nicht angelegt!");
        assertEquals(endstellenAUndB.size(), 2, "Die Anzahl der angelegten Endstellen ist nicht ok! angelegt: "
                + endstellenAUndB.size() + " erwartet: 2");
        assertEquals(endstellenAUndB.get(0).getEndstelleTyp(),
                Endstelle.ENDSTELLEN_TYP_A, "Erste Endstelle ist nicht vom Typ 'A'!");
        assertEquals(endstellenAUndB.get(1).getEndstelleTyp(),
                Endstelle.ENDSTELLEN_TYP_B, "Erste Endstelle ist nicht vom Typ 'A'!");
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#saveEndstelle(de.augustakom.hurrican.model.cc.Endstelle)}.
     */
    public void testSaveEndstelleBasic() throws FindException, StoreException {
        Endstelle expectedEndstelle = getBuilder(EndstelleBuilder.class).build();

        String bemerkungStawa = "Bemerkung UNIT-TEST!";
        expectedEndstelle.setBemerkungStawa(bemerkungStawa);
        getEndstellenService().saveEndstelle(expectedEndstelle);

        Endstelle endstelle = getEndstellenService().findEndstelle(expectedEndstelle.getId());
        assertNotNull(endstelle, "gespeicherte Endstelle konnte nicht geladen werden!");
        assertEquals(endstelle.getBemerkungStawa(), expectedEndstelle.getBemerkungStawa());
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findEndstelle(java.lang.Integer)}.
     *
     * @throws FindException
     */
    public void testFindEndstelleInteger() throws FindException {
        Endstelle expectedEndstelle = getBuilder(EndstelleBuilder.class).build();
        Endstelle foundEndstelle = getEndstellenService().findEndstelle(expectedEndstelle.getId());
        assertNotNull(foundEndstelle, "Es wurde keine Endstelle mit der ID " + expectedEndstelle.getId() + " gefunden!");
    }

    public void testFindEndstelleByGeoId() throws FindException {
        GeoIdBuilder geoIdBuilder = getBuilder(GeoIdBuilder.class);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withGeoIdBuilder(geoIdBuilder);

        Endstelle endstelleEins = endstelleBuilder.build();
        Endstelle endstelleZwei = endstelleBuilder.build();
        List<Endstelle> endstellen = getEndstellenService().findEndstellenByGeoId(geoIdBuilder.get().getId());
        assertNotNull(endstellen, "Es wurden keine Endstellen gefunden!");
        assertTrue(endstellen.size() == 2, "Es müssen genau zwei Endstellen gefunden werden!");
        assertTrue(endstellen.get(0).getGeoId().equals(endstelleEins.getGeoId()), "Geo ID der Endstelle (eins) weicht von Erwartungswert ab!");
        assertTrue(endstellen.get(1).getGeoId().equals(endstelleZwei.getGeoId()), "Geo ID der Endstelle (zwei) weicht von Erwartungswert ab!");
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findEndstelle(java.lang.Integer,
     * java.lang.String)}.
     */
    public void testFindEndstelleIntegerString() throws FindException {
        Endstelle expectedEndstelle = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .build();
        Endstelle endstelle = getEndstellenService()
                .findEndstelle(expectedEndstelle.getEndstelleGruppeId(), Endstelle.ENDSTELLEN_TYP_B);
        assertNotNull(endstelle, "Endstelle B zur Endstellen-Gruppe wurde nicht gefunden.");
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findEndstellen4Auftrag(java.lang.Integer)}.
     */
    public void testFindEndstellen4Auftrag() throws FindException {

        EndstelleBuilder expectedEndstelleBuilder = getBuilder(EndstelleBuilder.class);
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(expectedEndstelleBuilder)
                .build();
        List<Endstelle> result = getEndstellenService().findEndstellen4Auftrag(auftragTechnik.getAuftragId());
        assertNotEmpty(result, "Es wurden keine Endstellen fuer den Auftrag " + auftragTechnik.getAuftragId() + " gefunden!");
        assertEquals(result.size(), 1, "Anzahl der ermittelten Endstellen nicht korrekt.");
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findEndstelle4Auftrag(java.lang.Integer,
     * java.lang.String)}.
     */
    public void testFindEndstelle4Auftrag() throws FindException {
        EndstelleBuilder expectedEndstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B);
        AuftragTechnik auftragTechnik = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(expectedEndstelleBuilder.getEndstelleGruppeBuilder())
                .build();

        Endstelle result = getEndstellenService()
                .findEndstelle4Auftrag(auftragTechnik.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B);

        assertNotNull(result, "Endstelle B zu Auftrag nicht gefunden!");
    }


    public void testFindEndstellenWithRangierId() throws FindException {
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class);
        Rangierung rangierung = rangierungBuilder.build();
        EndstelleBuilder expectedEndstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(rangierungBuilder);
        expectedEndstelleBuilder.build();

        List<Endstelle> result = getEndstellenService().findEndstellenWithRangierId(rangierung.getId());
        assertNotEmpty(result, "Endstellen zur Rangierung wurden nicht gefunden!");
        assertEquals(result.get(0).getId(), expectedEndstelleBuilder.get().getId());
    }


    public void testFindEndstellenWithRangierAdditionalId() throws FindException {
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class);
        Rangierung rangierung = rangierungBuilder.build();
        EndstelleBuilder expectedEndstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungAdditionalBuilder(rangierungBuilder);
        expectedEndstelleBuilder.build();

        List<Endstelle> result = getEndstellenService().findEndstellenWithRangierId(rangierung.getId());
        assertNotEmpty(result, "Endstellen zur (Zusatz-)Rangierung wurden nicht gefunden!");
        assertEquals(result.get(0).getId(), expectedEndstelleBuilder.get().getId());
    }


    public void testFindEndstellen4TalPortAenderung() throws FindException {
        // @formatter:off
        GeoIdBuilder geoIdBuilder = getBuilder(GeoIdBuilder.class);

        Carrierbestellung2EndstelleBuilder cb2EsBuilder = getBuilder(Carrierbestellung2EndstelleBuilder.class);
        CarrierbestellungBuilder cbBuilder = getBuilder(CarrierbestellungBuilder.class)
            .withCb2EsBuilder(cb2EsBuilder)
            .withCarrier(Carrier.ID_DTAG)
            .withLbz("lbz");

        EndstelleBuilder esBuilder = getBuilder(EndstelleBuilder.class)
            .withGeoIdBuilder(geoIdBuilder)
            .withCb2EsBuilder(cb2EsBuilder);

        AuftragDatenBuilder adBuilder = getBuilder(AuftragDatenBuilder.class)
                .withStatusId(AuftragStatus.KUENDIGUNG);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(adBuilder);
        AuftragTechnikBuilder atBuilder = getBuilder(AuftragTechnikBuilder.class)
            .withAuftragBuilder(auftragBuilder)
            .withVerbindungsBezeichnungBuilder(getBuilder(VerbindungsBezeichnungBuilder.class));

        esBuilder.getEndstelleGruppeBuilder().withAuftragTechnikBuilder(atBuilder);

        esBuilder.build();
        cbBuilder.build();
        // @formatter:on

        List<AuftragEndstelleView> result = getEndstellenService()
                .findEndstellen4TalPortAenderung(geoIdBuilder.get().getId(), esBuilder.get().getEndstelleTyp());

        assertEquals(Iterables.getOnlyElement(result).getEndstelleId(), esBuilder.get().getId());
    }


    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findEndstellen4Anschlussuebernahme(java.lang.Integer)}.
     */
    @Test(enabled = false)
    public void testFindEndstellen4Anschlussuebernahme() {
        try {
            Long esId = Long.valueOf(241998);

            List<AuftragEndstelleView> result = getEndstellenService().findEndstellen4Anschlussuebernahme(esId);
            assertNotEmpty(result, "keine AuftragEndstellenViews gefunden!");
            LOGGER.debug("Anzahl gefundener AuftragEndstellenViews: " + result.size());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findEndstellen4Wandel(java.lang.Integer,
     * java.lang.Boolean)}.
     */
    @Test(enabled = false)
    public void testFindEndstellen4Wandel() {
        try {
            List<AuftragEndstelleView> result =
                    getEndstellenService().findEndstellen4Wandel(Long.valueOf(166581), Boolean.FALSE);
            assertNotEmpty(result, "Keine Endstellen-Views fuer Wandel gefunden!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findESLtgDaten4ES(java.lang.Integer)}.
     */
    @Test(enabled = false)
    public void testFindESLtgDaten4ES() {
        try {
            EndstelleLtgDaten result = getEndstellenService().findESLtgDaten4ES(Long.valueOf(230095));
            assertNotNull(result, "Keine Leitungsdaten zur Endstelle gefunden!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#saveESLtgDaten(de.augustakom.hurrican.model.cc.EndstelleLtgDaten,
     * boolean)}.
     */
    @Test(enabled = false)
    public void testSaveESLtgDaten() {
        try {
            EndstelleLtgDaten ltg = getEndstellenService().findESLtgDaten4ES(Long.valueOf(230095));
            ltg.setSchnittstelleId(Long.valueOf(8));

            EndstelleLtgDaten result = getEndstellenService().saveESLtgDaten(ltg, true);
            assertNotNull(result, "Leitungs-Daten wurden nicht gespeichert/historisiert!");
            assertEquals(result.getGueltigBis(), DateTools.getHurricanEndDate(), "Gueltig-Bis Datum der Ltg-Daten ungueltig!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }


    public void testSaveESLtgDatenWithoutHistory() throws Exception, ValidationException {
        Date today = new Date();
        Endstelle endstelle = getBuilder(EndstelleBuilder.class).build();
        Leitungsart leitungsart = getBuilder(LeitungsartBuilder.class).withId(Long.MAX_VALUE).build();
        Schnittstelle schnittstelle = getBuilder(SchnittstelleBuilder.class).withId(Long.MAX_VALUE).build();
        EndstelleLtgDaten endstelleLtgDatenActual = new EndstelleLtgDaten();
        endstelleLtgDatenActual.setEndstelleId(endstelle.getId());
        endstelleLtgDatenActual.setLeitungsartId(leitungsart.getId());
        endstelleLtgDatenActual.setSchnittstelleId(schnittstelle.getId());

        endstelleLtgDatenActual = getEndstellenService().saveESLtgDaten(endstelleLtgDatenActual, false);
        assertTrue(DateTools.isHurricanEndDate((endstelleLtgDatenActual.getGueltigBis())));
        assertTrue(DateTools.isDateEqual(endstelleLtgDatenActual.getGueltigVon(), today));

        EndstelleLtgDaten endstelleLtgDatenNew = new EndstelleLtgDaten();
        endstelleLtgDatenNew.setEndstelleId(endstelle.getId());
        endstelleLtgDatenNew.setLeitungsartId(leitungsart.getId());
        endstelleLtgDatenNew.setSchnittstelleId(schnittstelle.getId());

        endstelleLtgDatenNew = getEndstellenService().saveESLtgDaten(endstelleLtgDatenNew, false);
        assertTrue(DateTools.isDateEqual(endstelleLtgDatenNew.getGueltigVon(), today));
        assertTrue(DateTools.isHurricanEndDate((endstelleLtgDatenNew.getGueltigBis())));

        EndstelleLtgDaten endstelleLtgDatenActualFromDb = endstelleLtgDatenDao.findById(
                endstelleLtgDatenActual.getId(), EndstelleLtgDaten.class);
        assertTrue(DateTools.isDateEqual(endstelleLtgDatenActualFromDb.getGueltigBis(), today));
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findESAnsp4ES(java.lang.Integer)}.
     */
    @Test
    public void testFindESAnsp4ES() {
        try {
            EndstelleAnsprechpartner result = getEndstellenService().findESAnsp4ES(1L);
            assertNotNull(result, "Zur Endstelle " + 1L + " wurde kein Ansprechpartner gefunden!");
            result.debugModel(LOGGER);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#hasAPAddressChanged(de.augustakom.hurrican.model.cc.AuftragDaten)}.
     */
    @Test(enabled = false)
    public void testHasAPAddressChanged() {
        try {
            CCAuftragService as = getCCService(CCAuftragService.class);
            AuftragDaten ad = as.findAuftragDatenByAuftragId(Long.valueOf(175783));

            Boolean result = getEndstellenService().hasAPAddressChanged(ad);
            assertTrue(result, "Endstellen-Adresse wurde geaendert!");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link de.augustakom.hurrican.service.cc.impl.EndstellenServiceImpl#findAnschlussadresse4Auftrag(java.lang.Integer)}.
     */
    @Test(enabled = false)
    public void testFindAnschlussadresse4Auftrag() throws FindException {
        AddressModel result =
                getEndstellenService().findAnschlussadresse4Auftrag(Long.valueOf(186462), Endstelle.ENDSTELLEN_TYP_B);
        assertNull(result, "Endstellen-Adresse nicht gefunden!");
    }

    public void testAnschlussartFttb_hAsFttb() throws StoreException, FindException {
        final StandortFttb_hBuilder standortFttb_hBuilder = new StandortFttb_hBuilder();
        final AuftragFttb_hSuFAsFttbBuilder auftragFttb_hSuFAsFttbBuilder = new AuftragFttb_hSuFAsFttbBuilder();

        final StandortFttb_hBuilder.StandortFttb_h standortFttb_h = standortFttb_hBuilder.prepare(this, null);
        final AuftragFttb_hSuFAsFttbBuilder.AuftragFttbSuF auftragFttbSuF = auftragFttb_hSuFAsFttbBuilder
                .prepare(this, standortFttb_h);

        auftragFttb_hSuFAsFttbBuilder.build(auftragFttbSuF);
        getEndstellenService().saveEndstelle(auftragFttbSuF.endstelleBuilder.get());

        Endstelle endstelle = getEndstellenService().findEndstelle(auftragFttbSuF.endstelleBuilder.get().getId());
        assertNotNull(endstelle, "gespeicherte Endstelle konnte nicht geladen werden!");
        assertEquals(endstelle.getAnschlussart(), Anschlussart.ANSCHLUSSART_FTTB);
    }

    public void testAnschlussartFttb_hAsFtth() throws StoreException, FindException {
        final StandortFttb_hBuilder standortFttb_hBuilder = new StandortFttb_hBuilder();
        final AuftragFttb_hSuFAsFtthBuilder auftragFttb_hSuFAsFtthBuilder = new AuftragFttb_hSuFAsFtthBuilder();

        final StandortFttb_hBuilder.StandortFttb_h standortFttb_h = standortFttb_hBuilder.prepare(this, null);
        final AuftragFttb_hSuFAsFtthBuilder.AuftragFtthSuF auftragFtthSuF = auftragFttb_hSuFAsFtthBuilder
                .prepare(this, standortFttb_h);

        auftragFttb_hSuFAsFtthBuilder.build(auftragFtthSuF);
        getEndstellenService().saveEndstelle(auftragFtthSuF.endstelleBuilder.get());

        Endstelle endstelle = getEndstellenService().findEndstelle(auftragFtthSuF.endstelleBuilder.get().getId());
        assertNotNull(endstelle, "gespeicherte Endstelle konnte nicht geladen werden!");
        assertEquals(endstelle.getAnschlussart(), Anschlussart.ANSCHLUSSART_FTTH);
    }

    /* Gibt eine Instanz des EndstellenService zurueck. */
    private EndstellenService getEndstellenService() {
        return getCCService(EndstellenService.class);
    }
}
