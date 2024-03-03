/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2004 13:44:50
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.dao.cc.AuftragDatenDAO;
import de.augustakom.hurrican.dao.cc.AuftragTechnikDAO;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HVTGruppeBuilder;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDslamBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.Leitungsnummer;
import de.augustakom.hurrican.model.cc.Leitungsnummer.Typ;
import de.augustakom.hurrican.model.cc.LeitungsnummerBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppeBuilder;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountQuery;
import de.augustakom.hurrican.model.cc.view.AuftragIntAccountView;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierQuery;
import de.augustakom.hurrican.model.shared.view.AuftragCarrierView;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEndstelleView;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentQuery;
import de.augustakom.hurrican.model.shared.view.AuftragEquipmentView;
import de.augustakom.hurrican.model.shared.view.AuftragVorlaufView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HWSwitchService;

/**
 * Service-Tests fuer CCAuftragService.
 */
@Test(groups = { BaseTest.SLOW })
public class CCAuftragServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CCAuftragServiceTest.class);

    @Autowired
    private AuftragDatenDAO auftragDatenDAO;
    @Autowired
    private AuftragTechnikDAO auftragTechnikDAO;

    public void testCreateAuftrag() throws Exception {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).setPersist(
                false).withStatusId(AuftragStatus.BESTELLUNG_CUDA).withAuftragNoOrig(null);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .setPersist(false).withAuftragart(BAVerlaufAnlass.INTERN_WORK).withGueltigBis(
                        new Date());
        Auftrag auftrag = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                auftragDatenBuilder).withAuftragTechnikBuilder(auftragTechnikBuilder).setPersist(
                false).build();

        AuftragDaten auftragDaten = auftragDatenBuilder.get();
        AuftragTechnik auftragTechnik = auftragTechnikBuilder.get();

        CCAuftragService service = getCCService(CCAuftragService.class);
        Auftrag result = service.createAuftrag(auftrag.getKundeNo(), auftragDaten, auftragTechnik,
                getSessionId(), null);
        assertNotSame(result, auftrag, "Falsches Ergebnis von createAuftrag");
        assertNotNull(result.getAuftragId(), "keine AuftragId generiert von createAuftrag");

        flushAndClear();

        Auftrag resultAuftrag = service.findAuftragById(result.getAuftragId());
        AuftragDaten resultAuftragDaten = service.findAuftragDatenByAuftragIdTx(result
                .getAuftragId());
        AuftragTechnik resultAuftragTechnik = service.findAuftragTechnikByAuftragIdTx(result
                .getAuftragId());

        assertNotNull(resultAuftrag, "Der CC-Auftrag konnte nicht angelegt werden!");
        assertEquals(resultAuftrag.getId(), result.getAuftragId(), "Auftrag-Objekt ID falsch");
        assertEquals(resultAuftragDaten.getId(), auftragDaten.getId(),
                "AuftragDaten-Objekt ID falsch");
        assertEquals(resultAuftragDaten.getStatusId(), auftragDaten.getStatusId(),
                "AuftragDaten-Objekt StatusID falsch");
        assertEquals(resultAuftragTechnik.getId(), auftragTechnik.getId(),
                "AuftragTechnik-Objekt ID falsch");
        assertEquals(resultAuftragTechnik.getAuftragsart(), auftragTechnik.getAuftragsart(),
                "AuftragTechnik-Objekt auftragsart falsch");
        assertEquals(resultAuftragTechnik.getGueltigBis(), auftragTechnik.getGueltigBis(),
                "AuftragTechnik-Objekt gültig bis falsch");
    }

    public void testFindAuftragDatenViews() throws Exception {
        Auftrag auftrag = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                getBuilder(AuftragDatenBuilder.class).withStatusId(AuftragStatus.IN_BETRIEB))
                .withAuftragTechnikBuilder(getBuilder(AuftragTechnikBuilder.class)).build();
        flushAndClear();
        CCAuftragService service = getCCService(CCAuftragService.class);

        AuftragDatenQuery query = new AuftragDatenQuery();
        query.setAuftragId(auftrag.getAuftragId());
        List<AuftragDatenView> result = service.findAuftragDatenViews(query, true);
        assertNotEmpty(result, "Es wurden keine Auftragsdaten gefunden!");
        assertEquals(result.size(), 1, "size nicht korrekt");
        assertEquals(result.get(0).getAuftragId(), auftrag.getAuftragId(), "auftragid size nicht korrekt");
        assertEquals(result.get(0).getAuftragStatusId(), AuftragStatus.IN_BETRIEB, "auftragstatus nicht korrekt");
    }

    public void testFindParentAuftragDaten() throws Exception {
        ProduktBuilder produktBuilder = getBuilder(ProduktBuilder.class).withIsParent(true);
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withBuendelNr(20001).withBuendelNrHerkunft("HerkunftTest").withStatusId(
                        AuftragStatus.IN_BETRIEB).withProdBuilder(produktBuilder);
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                auftragDatenBuilder);
        Auftrag auftrag = auftragBuilder.build();
        AuftragDaten auftragDaten = auftragDatenBuilder.get();
        flushAndClear();

        CCAuftragService service = getCCService(CCAuftragService.class);

        AuftragDaten result = service.findParentAuftragDaten(auftrag.getKundeNo(), auftragDaten
                .getBuendelNr(), auftragDaten.getBuendelNrHerkunft());
        assertNotNull(result, "Parent-AuftragDaten zu Buendel wurden nicht gefunden!");
        assertEquals(result.getBuendelNr(), auftragDaten.getBuendelNr(), "BuendelNr nicht korrekt");
        assertEquals(result.getBuendelNrHerkunft(), auftragDaten.getBuendelNrHerkunft(),
                "BuendelNrHerkunft nicht korrekt");
    }

    @Test(groups = BaseTest.SLOW)
    public void testFindAuftragCarrierViews() throws Exception {
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class)
                .withLbzKunde("1234567890");
        Carrierbestellung2EndstelleBuilder carrierbestellung2EndstelleBuilder = getBuilder(Carrierbestellung2EndstelleBuilder.class);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class));

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(auftragDatenBuilder)
                .withAuftragTechnikBuilder(auftragTechnikBuilder);

        getBuilder(EndstelleBuilder.class)
                .withCb2EsBuilder(carrierbestellung2EndstelleBuilder)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();

        Carrierbestellung carrierbestellung = getBuilder(CarrierbestellungBuilder.class)
                .withCb2EsBuilder(carrierbestellung2EndstelleBuilder)
                .withLbz("999999991")
                .build();

        flushAndClear();

        CCAuftragService service = getCCService(CCAuftragService.class);

        AuftragCarrierQuery query = new AuftragCarrierQuery();
        query.setLbz(carrierbestellung.getLbz());
        List<AuftragCarrierView> result = service.findAuftragCarrierViews(query);

        assertNotEmpty(result, "Es wurden keine Auftrags-/Carrierdaten gefunden!");
        assertEquals(result.size(), 1, "size nicht korrekt");
        assertEquals(result.get(0).getAuftragId(), auftragBuilder.get().getAuftragId(), "auftragId falsch");
        assertEquals(result.get(0).getLbz(), carrierbestellung.getLbz(), "lbz falsch");
    }

    //TODO should be enabled after db performance issues have been fixed
    @Test(enabled = false)
    public void testFindAuftragEndstelleViews() throws Exception {
        AuftragTechnik2EndstelleBuilder auftragTechnik2EsBuilder = getBuilder(AuftragTechnik2EndstelleBuilder.class);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleGruppeBuilder(auftragTechnik2EsBuilder);
        Endstelle endstelle = endstelleBuilder.get();

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(auftragTechnik2EsBuilder)
                .withAuftragBuilder(getBuilder(AuftragBuilder.class));
        AuftragTechnik auftragTechnik = auftragTechnikBuilder.get();

        getBuilder(AuftragBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .build();
        CCAuftragService service = getCCService(CCAuftragService.class);
        flushAndClear();

        AuftragEndstelleQuery query = new AuftragEndstelleQuery();
        query.setEndstelleId(endstelleBuilder.get().getId());

        List<AuftragEndstelleView> result = service.findAuftragEndstelleViews(query);
        assertNotEmpty(result, "Es wurden keine Auftrags-/Endstellendaten gefunden!");
        assertEquals(result.size(), 1, "size nicht korrekt");
        assertEquals(result.get(0).getAuftragId(), auftragTechnik.getAuftragId(), "Auftrags-ID falsch");
        assertEquals(result.get(0).getEndstelleId(), endstelle.getId(), "Endstellen-ID nicht identisch!");
    }

    //TODO should be enabled after db performance issues have been fixed
    @Test(enabled = false)
    public void testFindAuftragEndstelleViews4VPN() throws Exception {
        VPNBuilder vpnBuilder = getBuilder(VPNBuilder.class);
        getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                getBuilder(AuftragDatenBuilder.class).withStatusId(AuftragStatus.INTERNET_AUFTRAG))
                .withAuftragTechnikBuilder(
                        getBuilder(AuftragTechnikBuilder.class).withVPNBuilder(vpnBuilder)).build();
        VPN vpn = vpnBuilder.get();
        flushAndClear();

        CCAuftragService service = getCCService(CCAuftragService.class);
        List<AuftragEndstelleView> result = service
                .findAuftragEndstelleViews4VPN(vpn.getId(), null);
        assertNotEmpty(result, "Es konnten keine Auftraege fuer das VPN ermittelt werden!");
        assertEquals(result.size(), 1, "size nicht korrekt");
        assertEquals(result.get(0).getAuftragStatusId(), AuftragStatus.INTERNET_AUFTRAG, "auftragstatus falsch");
    }

    @Test(enabled = false)
    public void testFindAuftragAccountViews() throws Exception {
        Auftrag auftrag = getBuilder(AuftragBuilder.class).build();
        flushAndClear();

        CCAuftragService service = getCCService(CCAuftragService.class);

        AuftragIntAccountQuery query = new AuftragIntAccountQuery();
        query.setKundeNo(auftrag.getKundeNo());

        List<AuftragIntAccountView> result = service.findAuftragAccountViews(query);
        assertNotEmpty(result, "Es wurden keine Auftrag-Account Views gefunden!");
        assertEquals(result.size(), 1, "size nicht korrekt");
    }

    public void testFindAuftragDaten4Sperre() throws Exception {
        Auftrag auftrag = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                getBuilder(AuftragDatenBuilder.class).withStatusId(AuftragStatus.AENDERUNG))
                .build();
        flushAndClear();
        CCAuftragService service = getCCService(CCAuftragService.class);

        List<AuftragDaten> result = service.findAuftragDaten4Sperre(auftrag.getKundeNo());
        assertNotEmpty(result,
                "Es wurden keine sperr-relevanten Auftraege fuer den Kunden gefunden!");
        assertEquals(result.size(), 1, "size nicht korrekt");
        assertEquals(result.get(0).getAuftragId(), auftrag.getAuftragId(), "auftragid size nicht korrekt");
        assertEquals(result.get(0).getStatusId(), AuftragStatus.AENDERUNG, "auftragstatus nicht korrekt");
    }

    //TODO should be enabled after db performance issues have been fixed
    @Test(enabled = false)
    public void testFindAuftragsVorlauf() throws Exception {
        CCAuftragService service = getCCService(CCAuftragService.class);
        List<AuftragVorlaufView> result = service.findAuftragsVorlauf();
        assertNotEmpty(result, "Es wurden keine Auftrags-Vorlaeufer gefunden!");
    }

    @Test
    public void testFindAuftragEquipmentViews() {
        try {
            AuftragEquipmentQuery query = new AuftragEquipmentQuery();
            query.setHvtIdStandort(1L);
            query.setEqSwitch("*02");
            query.setEqHwEqn("0020-01-00-00");
            query.setOnlyActive(true);
            // query.setEqBucht("0180");
            // query.setEqLeiste1("02");
            // query.setEqStift1("25");

            CCAuftragService service = getCCService(CCAuftragService.class);
            List<AuftragEquipmentView> result = service.findAuftragEquipmentViews(query);
            assertNotEmpty(result, "Es wurden keine Equipment-Daten gefunden!");
            LOGGER.debug("-------> size: " + result.size());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    @Test(enabled = false) // im CopyAuftragCommand greifen die finds an der Transaktion vorbei :(
    public void testCopyAuftrag() throws Exception {
        Auftrag auftrag = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                getBuilder(AuftragDatenBuilder.class).withStatusId(
                        AuftragStatus.AUS_TAIFUN_UEBERNOMMEN)
        )
                .withAuftragTechnikBuilder(
                        getBuilder(AuftragTechnikBuilder.class).withAuftragart(
                                BAVerlaufAnlass.INTERN_WORK)
                ).build();
        flushAndClear();

        CCAuftragService service = getCCService(CCAuftragService.class);
        List<Auftrag> result = service.copyAuftrag(getSessionId(), auftrag.getAuftragId(), null,
                123, "herkunft", 2, new Date(), true);
        assertEquals(result.size(), 2, "zwei Einträge erwartet");
        flushAndClear();

        for (Auftrag a : result) {
            AuftragDaten daten = service.findAuftragDatenByAuftragIdTx(a.getAuftragId());
            assertEquals(daten.getStatusId(), AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, "auftragstatus falsch");
            assertEquals(daten.getBuendelNr(), Integer.valueOf(123), "bundelnr falsch");
            assertEquals(daten.getBuendelNrHerkunft(), "herkunft", "bundelnr-herkunt falsch");
            AuftragTechnik technik = service.findAuftragTechnikByAuftragIdTx(a.getAuftragId());
            assertEquals(technik.getAuftragsart(), BAVerlaufAnlass.INTERN_WORK, "auftragart falsch");
        }
    }

    public void testSaveAuftragDaten() throws Exception {
        Auftrag auftrag = getBuilder(AuftragBuilder.class).withAuftragDatenBuilder(
                getBuilder(AuftragDatenBuilder.class)
                        .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
        ).build();
        flushAndClear();

        CCAuftragService service = getCCService(CCAuftragService.class);
        AuftragDaten ad = service.findAuftragDatenByAuftragIdTx(auftrag.getAuftragId());

        assertNotNull(ad, "Auftrag nicht gefunden");
        ad.setBearbeiter("XXX");
        AuftragDaten result = service.saveAuftragDaten(ad, true);
        assertNotSame(result, ad, "kopie erwartet");
        assertEquals(result.getStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT, "status falsch");
        assertEquals(result.getBearbeiter(), "XXX", "bearbeiter falsch");
        flushAndClear();

        AuftragDaten auftragDaten = service.findAuftragDatenByAuftragIdTx(ad.getAuftragId());
        assertNotNull(auftragDaten, "AuftragDaten nicht gefunden");
        assertEquals(auftragDaten.getBearbeiter(), "XXX", "flascher Bearbeiter");
        assertEquals(result.getStatusId(), AuftragStatus.AUFTRAG_GEKUENDIGT, "status falsch");
    }

    public void testSaveAuftragDatenWithHistory() throws Exception {
        Date today = new Date();
        AuftragDaten auftragDatenToHistorize = getBuilder(AuftragDatenBuilder.class).build();
        auftragDatenToHistorize.setBemerkungen("testbemerkung um Historisierung testen zu können.");
        CCAuftragService service = getCCService(CCAuftragService.class);
        AuftragDaten auftragDatenNewVersion = service.saveAuftragDaten(auftragDatenToHistorize, true);
        AuftragDaten auftragDatenHistorized = auftragDatenDAO.findById(auftragDatenToHistorize.getId(),
                AuftragDaten.class);
        assertTrue(DateTools.isDateEqual(auftragDatenHistorized.getGueltigBis(), today));
        assertTrue(DateTools.isDateEqual(auftragDatenHistorized.getGueltigVon(),
                auftragDatenToHistorize.getGueltigVon()));
        assertNotEquals(auftragDatenHistorized.getBemerkungen(), auftragDatenToHistorize.getBemerkungen());
        assertTrue(DateTools.isHurricanEndDate(auftragDatenNewVersion.getGueltigBis()));
        assertTrue(DateTools.isDateEqual(auftragDatenNewVersion.getGueltigVon(),
                today));
        assertEquals(auftragDatenNewVersion.getBemerkungen(), auftragDatenToHistorize.getBemerkungen());
    }

    public void testSaveAuftragDatenWithoutHistory() throws Exception {
        AuftragDaten auftragDatenToSave = getBuilder(AuftragDatenBuilder.class).build();
        auftragDatenToSave.setBemerkungen("testbemerkung um Historisierung testen zu können.");
        CCAuftragService service = getCCService(CCAuftragService.class);
        service.saveAuftragDaten(auftragDatenToSave, false);
        AuftragDaten auftragDatenSaved = auftragDatenDAO.findById(auftragDatenToSave.getId(),
                AuftragDaten.class);
        assertTrue(DateTools.isHurricanEndDate(auftragDatenSaved.getGueltigBis()));
        assertTrue(DateTools.isDateEqual(auftragDatenSaved.getGueltigVon(),
                auftragDatenToSave.getGueltigVon()));
        assertEquals(auftragDatenSaved.getBemerkungen(), auftragDatenToSave.getBemerkungen());
    }

    public void testSaveAuftragTechnikWithHistory() throws Exception {
        Date today = new Date();
        AuftragTechnik auftragTechnikToHistorize = getBuilder(AuftragTechnikBuilder.class).build();
        auftragTechnikToHistorize.setProjektierung(Boolean.TRUE);
        CCAuftragService service = getCCService(CCAuftragService.class);
        AuftragTechnik auftragTechnikNewVersion = service.saveAuftragTechnik(auftragTechnikToHistorize, true);
        AuftragTechnik auftragTechnikHistorized = auftragTechnikDAO.findById(auftragTechnikToHistorize.getId(),
                AuftragTechnik.class);
        assertTrue(DateTools.isDateEqual(auftragTechnikHistorized.getGueltigBis(), today));
        assertTrue(DateTools.isDateEqual(auftragTechnikHistorized.getGueltigVon(),
                auftragTechnikToHistorize.getGueltigVon()));
        assertNotEquals(auftragTechnikHistorized.getProjektierung(), auftragTechnikToHistorize.getProjektierung());
        assertTrue(DateTools.isHurricanEndDate(auftragTechnikNewVersion.getGueltigBis()));
        assertTrue(DateTools.isDateEqual(auftragTechnikNewVersion.getGueltigVon(),
                today));
        assertEquals(auftragTechnikNewVersion.getProjektierung(), auftragTechnikToHistorize.getProjektierung());
    }

    public void testSaveAuftragTechnikWithoutHistory() throws Exception {
        AuftragTechnik auftragTechnikToSave = getBuilder(AuftragTechnikBuilder.class).build();
        auftragTechnikToSave.setProjektierung(Boolean.TRUE);
        CCAuftragService service = getCCService(CCAuftragService.class);
        service.saveAuftragTechnik(auftragTechnikToSave, false);
        AuftragTechnik auftragTechnikSaved = auftragTechnikDAO.findById(auftragTechnikToSave.getId(),
                AuftragTechnik.class);
        assertEquals(auftragTechnikSaved.getProjektierung(), auftragTechnikToSave.getProjektierung());
        assertTrue(DateTools.isHurricanEndDate(auftragTechnikSaved.getGueltigBis()));
        assertTrue(DateTools.isDateEqual(auftragTechnikSaved.getGueltigVon(),
                auftragTechnikToSave.getGueltigVon()));
    }

    @Test(enabled = false)
    public void testReportAccounts() {
        try {
            CCAuftragService service = getCCService(CCAuftragService.class);
            JasperPrint jp = service.reportAccounts(105582L, getSessionId());

            JasperPrintManager.printReport(jp, true);

            Thread.sleep(10000);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testGetMaxAuftragId() throws Exception {
        Auftrag auftrag1 = getBuilder(AuftragBuilder.class).withId(Long.MAX_VALUE - (new Random()).nextInt(1000))
                .build();
        Auftrag auftrag2 = getBuilder(AuftragBuilder.class).withId(Long.MAX_VALUE - (new Random()).nextInt(1000))
                .build();
        flushAndClear();

        Long higher = Math.max(auftrag1.getAuftragId(), auftrag2.getAuftragId());

        Long maxAuftragId = getCCService(CCAuftragService.class).findMaxAuftragId();

        assertEquals(maxAuftragId, higher);
    }

    /* Tests and Helper for Leitungsnummer */

    public void testFindLeitungsnummerById() throws Exception {
        Leitungsnummer leitungsnummer = getBuilder(LeitungsnummerBuilder.class)
                .withTyp(Typ.NUE_LBZ).withLeitungsnummer("Testobjekt 1").build();
        flushAndClear();

        Leitungsnummer result = getCCService(CCAuftragService.class).findLeitungsnummerById(leitungsnummer.getId());
        assertLeitungsnummer(result, leitungsnummer);
    }

    public void testFindLeitungsnummerByEndstelleId() throws Exception {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        Leitungsnummer leitungsnummer1 = getBuilder(LeitungsnummerBuilder.class).withAuftragBuilder(auftragBuilder)
                .withTyp(Typ.NUE_LBZ).withLeitungsnummer("Testobjekt 1").build();
        Leitungsnummer leitungsnummer2 = getBuilder(LeitungsnummerBuilder.class).withAuftragBuilder(auftragBuilder)
                .withTyp(Typ.SONST).withLeitungsnummer("Testobjekt 2").build();
        flushAndClear();

        List<Leitungsnummer> result = getCCService(CCAuftragService.class).findLeitungsnummerByAuftrag(auftragBuilder.get());

        assertNotNull(result, "result should not be null");
        assertEquals(result.size(), 2, "size incorrect");

        assertLeitungsnummer(result.get(0), leitungsnummer1);
        assertLeitungsnummer(result.get(1), leitungsnummer2);
    }

    public void testSaveLeitungsnummer() throws Exception {
        Leitungsnummer leitungsnummer = getBuilder(LeitungsnummerBuilder.class)
                .withTyp(Typ.NUE_LBZ).withLeitungsnummer("Testobjekt 1").setPersist(false)
                .build();
        flushAndClear();

        CCAuftragService service = getCCService(CCAuftragService.class);
        service.saveLeitungsnummer(leitungsnummer);

        Leitungsnummer result = service.findLeitungsnummerById(leitungsnummer.getId());
        assertNotNull(result, "result should not be null");

        assertLeitungsnummer(result, leitungsnummer);
    }

    public void testDeleteLeitungsnummer() throws Exception {
        Leitungsnummer leitungsnummer = getBuilder(LeitungsnummerBuilder.class).withTyp(
                Typ.NUE_LBZ).withLeitungsnummer("Testobjekt 1").build();
        flushAndClear();

        CCAuftragService service = getCCService(CCAuftragService.class);
        Leitungsnummer result = service.findLeitungsnummerById(leitungsnummer.getId());
        assertNotNull(result, "leitungsnummer does not exist in DB");

        service.deleteLeitungsnummer(result);

        Leitungsnummer find = service.findLeitungsnummerById(leitungsnummer.getId());
        assertNull(find, "result should be deleted therefore null");
    }

    /* helper methods for asserting Leitungsnummer objects */

    private void assertLeitungsnummer(Leitungsnummer actual, Leitungsnummer expected) {
        assertLeitungsnummerWithParams(actual, expected.getId(), expected.getAuftragId(),
                expected.getTyp(), expected.getLeitungsnummer());
    }

    private void assertLeitungsnummerWithParams(Leitungsnummer actual, Long id, Long endstelleId,
            Typ typ, String lnBezeichnung) {
        assertNotNull(actual, "actual leitungsnummer should not be null");
        assertEquals(actual.getId(), id, "id of leitungsnummer incorrect");
        assertEquals(actual.getAuftragId(), endstelleId, "endstelleId of leitungsnummer incorrect");
        assertEquals(actual.getTyp(), typ, "typ of leitungsnummer incorrect");
        assertEquals(actual.getLeitungsnummer(), lnBezeichnung, "leitungsnummer incorrect");
    }


    @Test(expectedExceptions = FindException.class)
    public void testFindMainOrder4SIPCustomerExpectException() throws FindException {
        CCAuftragService service = getCCService(CCAuftragService.class);
        service.findMainOrder4SIPCustomer(-9L);
    }


    public void testFindActiveOrderByLineId() throws FindException {
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("my.line.id");
        Auftrag auftrag = buildAuftrag(vbzBuilder, AuftragStatus.IN_BETRIEB);
        buildAuftrag(vbzBuilder, AuftragStatus.AUFTRAG_GEKUENDIGT);
        buildAuftrag(vbzBuilder, AuftragStatus.STORNO);

        CCAuftragService service = getCCService(CCAuftragService.class);
        Auftrag result = service.findActiveOrderByLineId(vbzBuilder.get().getVbz(), LocalDate.now());
        assertNotNull(result);
        assertEquals(result.getId(), auftrag.getId());
    }

    @Test(expectedExceptions = FindException.class)
    public void testFindActiveOrderByLineIdTwoActiveOrdersYieldsFindException() throws FindException {
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("my.line.id");
        buildAuftrag(vbzBuilder, AuftragStatus.IN_BETRIEB);
        buildAuftrag(vbzBuilder, AuftragStatus.IN_BETRIEB);

        CCAuftragService service = getCCService(CCAuftragService.class);
        service.findActiveOrderByLineId(vbzBuilder.get().getVbz(), LocalDate.now());
    }

    private Auftrag buildAuftrag(VerbindungsBezeichnungBuilder vbzBuilder, Long auftragStatusId) {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);
        AuftragDatenBuilder adBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragBuilder(auftragBuilder).withStatusId(auftragStatusId);
        AuftragTechnikBuilder atBuilder = getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragBuilder).withVerbindungsBezeichnungBuilder(vbzBuilder);
        atBuilder.build();
        adBuilder.build();
        return auftragBuilder.get();
    }

    public void testFindAktiveAuftragDatenByBaugruppe() throws FindException {
        HWMduBuilder mduBuilder = getBuilder(HWMduBuilder.class);
        HWBaugruppeBuilder baugruppeBuilder = getBuilder(HWBaugruppeBuilder.class).withRackBuilder(mduBuilder);

        AuftragDaten auftragDatenAktiv = createAuftragDatenAtBaugruppe(DateTools.getHurricanEndDate(),
                baugruppeBuilder, DateTools.getHurricanEndDate());
        createAuftragDatenAtBaugruppe(new Date(), baugruppeBuilder, DateTools.getHurricanEndDate());
        createAuftragDatenAtBaugruppe(DateTools.getHurricanEndDate(), baugruppeBuilder, new Date());
        AuftragDaten auftragDatenRangierungNichtAktuell = createAuftragDatenAtBaugruppeWithRangierungNichtAktuell(
                DateTools.getHurricanEndDate(), baugruppeBuilder);

        CCAuftragService service = getCCService(CCAuftragService.class);
        List<AuftragDaten> auftragDaten = service.findAktiveAuftragDatenByBaugruppe(baugruppeBuilder.get()
                .getId());

        assertEquals(auftragDaten.size(), 2);
        assertThat(auftragDaten, Matchers.containsInAnyOrder(auftragDatenAktiv, auftragDatenRangierungNichtAktuell));
    }

    public void testFindAktiveAuftragDatenByOrtsteilAndProduktGroup() throws FindException {
        Date validDate = DateTools.getHurricanEndDate();
        Date expiredDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        String ortsteil = "someOrtsteil";
        String produktGruppe = "someGroup";

        AuftragDaten shouldFind1 = createAuftragDatenWithOrtsteilAndProduktGroup(ortsteil, produktGruppe, validDate);
        AuftragDaten shouldFind2 = createAuftragDatenWithOrtsteilAndProduktGroup(ortsteil, produktGruppe, validDate);
        createAuftragDatenWithOrtsteilAndProduktGroup("SomeOtherOrtsteil", produktGruppe, validDate);
        createAuftragDatenWithOrtsteilAndProduktGroup(ortsteil, "someOtherGroup", validDate);
        createAuftragDatenWithOrtsteilAndProduktGroup(ortsteil, produktGruppe, expiredDate);

        CCAuftragService service = getCCService(CCAuftragService.class);

        List<AuftragDaten> result = service.findAktiveAuftragDatenByOrtsteilAndProduktGroup(ortsteil, produktGruppe);

        assertEquals(result.size(), 2);
        assertThat(result, Matchers.containsInAnyOrder(shouldFind1, shouldFind2));
    }

    private AuftragDaten createAuftragDatenAtBaugruppe(Date auftragGueltigBis, HWBaugruppeBuilder baugruppeBuilder,
            Date rangierungGueltigBis) {
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(baugruppeBuilder);
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class)
                .withEqInBuilder(equipmentBuilder)
                .withGueltigBis(rangierungGueltigBis);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withRangierungBuilder(rangierungBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withGueltigBis(auftragGueltigBis)
                .withEndstelleBuilder(endstelleBuilder);
        auftragTechnikBuilder.getAuftragBuilder().build();
        auftragTechnikBuilder.get();
        return auftragTechnikBuilder.getAuftragBuilder().getAuftragDatenBuilder().get();
    }

    private AuftragDaten createAuftragDatenAtBaugruppeWithRangierungNichtAktuell(Date auftragGueltigBis,
            HWBaugruppeBuilder baugruppeBuilder) {
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class).withBaugruppeBuilder(baugruppeBuilder);
        RangierungBuilder rangierungBuilder = getBuilder(RangierungBuilder.class).withEqInBuilder(equipmentBuilder)
                .withEndstelleBuilder(getBuilder(EndstelleBuilder.class).withId(Rangierung.RANGIERUNG_NOT_ACTIVE));
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class).withRangierungBuilder(rangierungBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withGueltigBis(auftragGueltigBis)
                .withEndstelleBuilder(endstelleBuilder);
        auftragTechnikBuilder.getAuftragBuilder().build();
        auftragTechnikBuilder.get();
        return auftragTechnikBuilder.getAuftragBuilder().getAuftragDatenBuilder().get();
    }

    private AuftragDaten createAuftragDatenWithOrtsteilAndProduktGroup(String ortsteil, String produktGruppe, Date auftragGueltigBis) {
        ProduktGruppeBuilder produktGruppeBuilder = getBuilder(ProduktGruppeBuilder.class)
                .withProduktGruppe(produktGruppe);

        HVTGruppeBuilder hvtGruppeBuilder = getBuilder(HVTGruppeBuilder.class)
                .withOrtsteil(ortsteil);
        HVTStandortBuilder hvtStandortBuilder = getBuilder(HVTStandortBuilder.class)
                .withHvtGruppeBuilder(hvtGruppeBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withHvtStandortBuilder(hvtStandortBuilder);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withGueltigBis(auftragGueltigBis)
                .withEndstelleBuilder(endstelleBuilder);
        auftragTechnikBuilder.getAuftragBuilder().getAuftragDatenBuilder().getProdBuilder().withProduktGruppeBuilder(produktGruppeBuilder);

        auftragTechnikBuilder.getAuftragBuilder().build();
        auftragTechnikBuilder.get();
        return auftragTechnikBuilder.getAuftragBuilder().getAuftragDatenBuilder().get();
    }

    public void testFindAuftragDatenByKundeNo() throws FindException {
        final Long kundeNo = 599999999L;
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class);
        getBuilder(AuftragBuilder.class).withKundeNo(kundeNo)
                .withAuftragDatenBuilder(auftragDatenBuilder).build();

        CCAuftragService service = getCCService(CCAuftragService.class);
        List<AuftragDaten> auftragDaten = service.findAuftragDatenByKundeNo(kundeNo);

        assertTrue(!auftragDaten.isEmpty());
        assertThat(auftragDaten, Matchers.containsInAnyOrder(auftragDatenBuilder.get()));
    }

    @Test(groups = BaseTest.SLOW)
    public void testFindAuftragDatenByEquipmentSwitchHwEQN() throws Exception {
        String switchAK = "AUG01";
        String hwEQN = "1-03-04-05";
        HWSwitchService service = getCCService(HWSwitchService.class);
        HWSwitch hwSwitch = service.findSwitchByName(switchAK);
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class).withHwEQN(hwEQN).withHwSwitch(hwSwitch);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        getBuilder(RangierungBuilder.class).withEndstelleBuilder(endstelleBuilder).withEqInBuilder(equipmentBuilder)
                .build();
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragBuilder(getBuilder(AuftragBuilder.class));
        getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withEndstelleBuilder(endstelleBuilder).build();

        List<AuftragDaten> foundAuftragDaten = getCCService(CCAuftragService.class).findAuftragDatenByEquipment(
                switchAK, hwEQN, new Date());

        Assert.assertTrue(foundAuftragDaten.contains(auftragDatenBuilder.get()));
    }

    public void testFindAuftragDatenByRackAndEqn() throws Exception {
        String dslamBez = "DSL0001-ABC";
        String hwEQN = "1-03-04-05";
        HWBaugruppeBuilder hwBaugruppeBuilder = getBuilder(HWBaugruppeBuilder.class).withRackBuilder(
                getBuilder(HWDslamBuilder.class).withGeraeteBez(dslamBez));
        EquipmentBuilder equipmentBuilder = getBuilder(EquipmentBuilder.class).withHwEQN(hwEQN).withBaugruppeBuilder(
                hwBaugruppeBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class);
        getBuilder(RangierungBuilder.class).withEndstelleBuilder(endstelleBuilder).withEqInBuilder(equipmentBuilder)
                .build();
        AuftragDatenBuilder auftragDatenBuilder = getBuilder(AuftragDatenBuilder.class).withAuftragBuilder(
                getBuilder(AuftragBuilder.class));
        getBuilder(AuftragTechnikBuilder.class).withAuftragBuilder(auftragDatenBuilder.getAuftragBuilder())
                .withEndstelleBuilder(endstelleBuilder).build();

        List<AuftragDaten> foundAuftragDaten = getCCService(CCAuftragService.class).findAuftragDatenByRackAndEqn(
                dslamBez, hwEQN, new Date());

        Assert.assertTrue(foundAuftragDaten.contains(auftragDatenBuilder.get()));
    }
}
