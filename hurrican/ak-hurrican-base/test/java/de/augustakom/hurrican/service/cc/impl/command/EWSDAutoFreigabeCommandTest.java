/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2011 11:00:08
 */
package de.augustakom.hurrican.service.cc.impl.command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.lang.time.DateUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Rangierung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.PhysikFreigebenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * TestNG Klasse fuer {@link EWSDAutoFreigabeCommand}
 */
@Test(groups = { BaseTest.UNIT })
public class EWSDAutoFreigabeCommandTest extends BaseTest {

    EWSDAutoFreigabeCommand cut;
    @Mock
    BAService baServiceMock;
    @Mock
    CarrierService carrierServiceMock;
    @Mock
    ProduktService produktServiceMock;
    @Mock
    CCAuftragService auftragServiceMock;
    @Mock
    RangierungsService rangierungsServiceMock;
    PhysikFreigebenView physikFreigebenView;
    List<Verlauf> verlaeufe;
    List<Carrierbestellung> bestellungen;

    @BeforeMethod
    public void setUp(Method method) throws FindException, StoreException {
        cut = new EWSDAutoFreigabeCommand();
        MockitoAnnotations.initMocks(this);

        cut.setAuftragService(auftragServiceMock);
        cut.setBaService(baServiceMock);
        cut.setCarrierService(carrierServiceMock);
        cut.setProduktService(produktServiceMock);
        cut.setRangierungsService(rangierungsServiceMock);

        physikFreigebenView = new PhysikFreigebenView();

        verlaeufe = new ArrayList<Verlauf>();
        bestellungen = new ArrayList<Carrierbestellung>();
    }

    @Test
    public void testProcessPortGesamtWithValidPGS() {
        physikFreigebenView.setPortGesamtStatus(EWSDAutoFreigabeCommand.PORT_GESAMT_FREE);
        assertTrue(cut.processPortGesamt(physikFreigebenView), "Ergebnis muss 'true' sein!");
    }

    @Test
    public void testProcessPortGesamtWithNullPGS() {
        physikFreigebenView.setPortGesamtStatus(null);
        assertTrue(cut.processPortGesamt(physikFreigebenView), "Ergebnis muss 'true' sein!");
    }

    @Test
    public void testProcessPortGesamtWithInvalidPGS() {
        physikFreigebenView.setPortGesamtStatus("SUB");
        assertFalse(cut.processPortGesamt(physikFreigebenView), "Ergebnis muss 'false' sein!");
    }

    @Test
    public void testProcessOrderADStatusStornoOrAbsage() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftragDaten = new AuftragDaten();
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(auftragDaten);

        auftragDaten.setStatusId(AuftragStatus.STORNO);
        assertTrue(cut.processOrder(physikFreigebenView), "Auftrag Status 'STORNO' muss akzeptiert werden!");

        auftragDaten.setStatusId(AuftragStatus.ABSAGE);
        assertTrue(cut.processOrder(physikFreigebenView), "Auftrag Status 'ABSAGE' muss akzeptiert werden!");
    }

    @Test
    public void testProcessOrderADStatusInBetrieb() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftragDaten = new AuftragDaten();
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(auftragDaten);

        auftragDaten.setStatusId(AuftragStatus.IN_BETRIEB);
        assertFalse(cut.processOrder(physikFreigebenView), "Auftrag Status 'IN_BETRIEB' darf NICHT akzeptiert werden!");
    }

    @Test
    public void testProcessOrderNoBA() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(auftragDaten);
        when(baServiceMock.findVerlaeufe4Auftrag(any(Long.class))).thenReturn(verlaeufe);
        Produkt produkt = new Produkt();
        produkt.setElVerlauf(Boolean.TRUE);
        when(produktServiceMock.findProdukt(any(Long.class))).thenReturn(produkt);

        assertFalse(cut.processOrder(physikFreigebenView), "Auftrag ohne Kündigungs-BA darf NICHT akzeptiert werden!");
    }

    @Test
    public void testProcessOrderNoBAButNotNecessary() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(auftragDaten);
        when(baServiceMock.findVerlaeufe4Auftrag(any(Long.class))).thenReturn(verlaeufe);
        Produkt produkt = new Produkt();
        produkt.setElVerlauf(Boolean.FALSE);
        when(produktServiceMock.findProdukt(any(Long.class))).thenReturn(produkt);

        assertTrue(cut.processOrder(physikFreigebenView),
                "Auftrag ohne Kündigungs-BA und Produkt ELVerlauf == false muss akzeptiert werden (Wholesale Auftrag)!");
    }

    @Test
    public void testProcessOrderBAIsNoTerm() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftragDaten = new AuftragDaten();
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(auftragDaten);
        auftragDaten.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
        when(baServiceMock.findVerlaeufe4Auftrag(any(Long.class))).thenReturn(verlaeufe);
        Verlauf verlauf = new Verlauf();
        verlauf.setVerlaufStatusId(VerlaufStatus.BEI_TECHNIK);
        verlaeufe.add(verlauf);

        assertFalse(cut.processOrder(physikFreigebenView),
                "Verlauf ist kein Kündigungs-BA, darf folglich NICHT akzeptiert werden!");
    }

    @Test
    public void testProcessOrderBAInvalidStatus() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftragDaten = new AuftragDaten();
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(auftragDaten);
        auftragDaten.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
        when(baServiceMock.findVerlaeufe4Auftrag(any(Long.class))).thenReturn(verlaeufe);
        Verlauf verlauf = new Verlauf();
        verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_DISPO);
        verlaeufe.add(verlauf);

        assertFalse(cut.processOrder(physikFreigebenView),
                "Kündigungs-BA ist nicht abgeschlossen, darf folglich NICHT "
                        + "akzeptiert werden!"
        );
    }

    @Test
    public void testProcessOrderBAValidStatus() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftragDaten = new AuftragDaten();
        when(auftragServiceMock.findAuftragDatenByAuftragId(any(Long.class))).thenReturn(auftragDaten);
        auftragDaten.setStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
        when(baServiceMock.findVerlaeufe4Auftrag(any(Long.class))).thenReturn(verlaeufe);
        Verlauf verlauf = new Verlauf();
        verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN);
        verlaeufe.add(verlauf);
        verlaeufe.add(new Verlauf()); // Erster Verlauf, z.B. Neuschaltung

        assertTrue(cut.processOrder(physikFreigebenView),
                "Kündigungs-BA ist abgeschlossen, muss folglich akzeptiert werden!");
    }

    @Test
    public void testProcessCBNoCBs() throws FindException, HurricanServiceCommandException {
        when(carrierServiceMock.findCBs4EndstelleTx(any(Long.class))).thenReturn(bestellungen);
        cut.setNow(new Date());

        assertTrue(cut.processCB(physikFreigebenView), "Keine CBs, muss folglich akzeptiert werden!");
    }

    @Test
    public void testProcessCBMNetOnly() throws FindException, HurricanServiceCommandException {
        when(carrierServiceMock.findCBs4EndstelleTx(any(Long.class))).thenReturn(bestellungen);
        Carrierbestellung mnet = new Carrierbestellung();
        mnet.setCarrier(Carrier.ID_MNET);
        bestellungen.add(mnet);
        cut.setNow(new Date());

        assertTrue(cut.processCB(physikFreigebenView), "Lediglich MNet CB, muss folglich akzeptiert werden!");
    }

    @Test
    public void testProcessCBNoTermDate() throws FindException, HurricanServiceCommandException {
        when(carrierServiceMock.findCBs4EndstelleTx(any(Long.class))).thenReturn(bestellungen);
        Carrierbestellung mnet = new Carrierbestellung();
        mnet.setCarrier(Carrier.ID_MNET);
        Carrierbestellung dtag = new Carrierbestellung();
        dtag.setCarrier(Carrier.ID_DTAG);
        bestellungen.add(mnet);
        bestellungen.add(dtag);
        cut.setNow(new Date());
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
        cut.setAuftragDaten(auftragDaten);

        assertFalse(cut.processCB(physikFreigebenView), "DTAG CB hat kein TermDate, darf folglich NICHT akzeptiert werden!");
    }

    @Test
    public void testProcessCBInvalidTermDate() throws FindException, HurricanServiceCommandException {
        Date termDate = DateUtils.truncate(DateTools.changeDate(new Date(), Calendar.DATE, +1), Calendar.DAY_OF_MONTH);
        when(carrierServiceMock.findCBs4EndstelleTx(any(Long.class))).thenReturn(bestellungen);
        Carrierbestellung mnet = new Carrierbestellung();
        mnet.setCarrier(Carrier.ID_MNET);
        Carrierbestellung dtag = new Carrierbestellung();
        dtag.setKuendBestaetigungCarrier(termDate);
        dtag.setCarrier(Carrier.ID_DTAG);
        bestellungen.add(mnet);
        bestellungen.add(dtag);
        cut.setNow(new Date());
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
        cut.setAuftragDaten(auftragDaten);

        assertFalse(cut.processCB(physikFreigebenView), "DTAG CB hat kein gültiges TermDate, darf folglich NICHT "
                + "akzeptiert werden!");
    }

    @Test
    public void testProcessCBInvalidTermDateAndWithLBZ() throws FindException, HurricanServiceCommandException {
        Date termDate = DateUtils.truncate(DateTools.changeDate(new Date(), Calendar.DATE, +1), Calendar.DAY_OF_MONTH);
        when(carrierServiceMock.findCBs4EndstelleTx(any(Long.class))).thenReturn(bestellungen);
        Carrierbestellung mnet = new Carrierbestellung();
        mnet.setCarrier(Carrier.ID_MNET);
        Carrierbestellung dtag = new Carrierbestellung();
        dtag.setKuendBestaetigungCarrier(termDate);
        dtag.setCarrier(Carrier.ID_DTAG);
        dtag.setLbz("123");
        bestellungen.add(mnet);
        bestellungen.add(dtag);
        cut.setNow(new Date());
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragStatusId(AuftragStatus.STORNO); // Storno oder Absage -> LBZ hinterlegt?
        cut.setAuftragDaten(auftragDaten);

        assertFalse(cut.processCB(physikFreigebenView), "DTAG CB hat kein gültiges TermDate und eine LBZ hinterlegt, "
                + "darf folglich NICHT akzeptiert werden!");
    }

    @Test
    public void testProcessCBInvalidTermDateAndWithoutLBZ() throws FindException, HurricanServiceCommandException {
        Date termDate = DateUtils.truncate(DateTools.changeDate(new Date(), Calendar.DATE, +1), Calendar.DAY_OF_MONTH);
        when(carrierServiceMock.findCBs4EndstelleTx(any(Long.class))).thenReturn(bestellungen);
        Carrierbestellung mnet = new Carrierbestellung();
        mnet.setCarrier(Carrier.ID_MNET);
        Carrierbestellung dtag = new Carrierbestellung();
        dtag.setKuendBestaetigungCarrier(termDate);
        dtag.setCarrier(Carrier.ID_DTAG);
        bestellungen.add(mnet);
        bestellungen.add(dtag);
        cut.setNow(new Date());
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragStatusId(AuftragStatus.STORNO); // Storno oder Absage -> LBZ hinterlegt?
        cut.setAuftragDaten(auftragDaten);

        assertTrue(cut.processCB(physikFreigebenView), "DTAG CB hat keine LBZ hinterlegt, muss folglich "
                + "akzeptiert werden!");
    }

    @Test
    public void testProcessCBValidTermDate() throws FindException, HurricanServiceCommandException {
        Date termDate = DateUtils.truncate(DateTools.changeDate(new Date(), Calendar.DATE, -1), Calendar.DAY_OF_MONTH);
        when(carrierServiceMock.findCBs4EndstelleTx(any(Long.class))).thenReturn(bestellungen);
        Carrierbestellung mnet = new Carrierbestellung();
        mnet.setCarrier(Carrier.ID_MNET);
        Carrierbestellung dtag = new Carrierbestellung();
        dtag.setKuendBestaetigungCarrier(termDate);
        dtag.setCarrier(Carrier.ID_DTAG);
        bestellungen.add(mnet);
        bestellungen.add(dtag);
        cut.setNow(new Date());
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT);
        cut.setAuftragDaten(auftragDaten);

        assertTrue(cut.processCB(physikFreigebenView),
                "DTAG CB hat ein gültiges TermDate, muss folglich akzeptiert werden!");
    }

    @Test
    public void testProcessOrderTermDateNull() throws FindException, HurricanServiceCommandException {
        AuftragDaten auftragDaten = new AuftragDaten();
        cut.setAuftragDaten(auftragDaten);
        cut.setNow(new Date());

        assertFalse(cut.processOrderTermDate(physikFreigebenView), "Auftrag Kündigungsdatum==null, darf folglich NICHT"
                + " akzeptiert werden!");
    }

    @Test
    public void testProcessOrderTermDateFuture() throws FindException, HurricanServiceCommandException {
        Date termDate = DateUtils.truncate(DateTools.changeDate(new Date(), Calendar.DATE, +1), Calendar.DAY_OF_MONTH);
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setKuendigung(termDate);
        cut.setAuftragDaten(auftragDaten);
        cut.setNow(new Date());

        assertFalse(cut.processOrderTermDate(physikFreigebenView), "Auftrag Kündigungsdatum liegt in der Zukunft"
                + ", darf folglich NICHT akzeptiert werden!");
    }

    @Test
    public void testProcessOrderTermDatePast() throws FindException, HurricanServiceCommandException {
        Date termDate = DateUtils.truncate(DateTools.changeDate(new Date(), Calendar.DATE,
                -(RangierungsService.DELAY_4_RANGIERUNGS_FREIGABE + 1)), Calendar.DAY_OF_MONTH);
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setKuendigung(termDate);
        cut.setAuftragDaten(auftragDaten);
        cut.setNow(new Date());

        assertTrue(cut.processOrderTermDate(physikFreigebenView), "Auftrag Kündigungsdatum liegt in der Vergangenheit"
                + ", muss folglich akzeptiert werden!");
    }

    private Rangierung createRang(Long rangierId, Date gueltigBis) {
        Rangierung rangierung = new Rangierung();
        rangierung.setId(rangierId);
        rangierung.setGueltigBis(gueltigBis);
        return rangierung;
    }

    @DataProvider
    public Object[][] dataProviderIsOrderCancled() {
        Long rId0 = null;
        Long rId1 = Long.valueOf(1);
        Long rId2 = Long.valueOf(2);
        Long rId3 = Long.valueOf(3);
        Long rId4 = Long.valueOf(4);
        PhysikFreigebenView testView = new PhysikFreigebenView();
        testView.setRangierId(rId1);
        Date endDate = DateTools.getHurricanEndDate();
        Date now = new Date();
        // @formatter:off
        return new Object[][] {
                { null, null, testView, false },
                { null, new Rangierung[]{}, testView, false },
                { new Rangierung[]{}, null, testView, false },
                { new Rangierung[]{}, new Rangierung[]{}, testView, false },

                { null, new Rangierung[]{createRang(rId0, null), createRang(rId0, null)}, testView, false },
                { new Rangierung[]{createRang(rId0, null), createRang(rId0, null)}, null, testView, false },
                { new Rangierung[]{createRang(rId0, null), createRang(rId0, null)}, new Rangierung[]{createRang(rId0, null), createRang(rId0, null)}, testView, false },

                { new Rangierung[]{createRang(rId2, null), createRang(rId3, now)}, null, testView, false },
                { new Rangierung[]{createRang(rId3, now), createRang(rId2, null)}, null, testView, false },
                { null, new Rangierung[]{createRang(rId2, null), createRang(rId3, now)}, testView, false },
                { null, new Rangierung[]{createRang(rId3, now), createRang(rId2, null)}, testView, false },

                { new Rangierung[]{createRang(rId1, null), createRang(rId2, endDate)}, null, testView, false },
                { new Rangierung[]{createRang(rId2, endDate), createRang(rId1, null)}, null, testView, false },
                { null, new Rangierung[]{createRang(rId1, null), createRang(rId2, endDate)}, testView, false },
                { null, new Rangierung[]{createRang(rId2, endDate), createRang(rId1, null)}, testView, false },

                { new Rangierung[]{createRang(rId1, null), createRang(rId2, now)}, null, testView, true },
                { new Rangierung[]{createRang(rId2, now), createRang(rId1, null)}, null, testView, true },
                { null, new Rangierung[]{createRang(rId1, null), createRang(rId2, now)}, testView, true },
                { null, new Rangierung[]{createRang(rId2, now), createRang(rId1, null)}, testView, true },
                { new Rangierung[]{createRang(rId1, null), createRang(rId2, now)}, new Rangierung[]{createRang(rId3, null), createRang(rId4, endDate)}, testView,true },
                { new Rangierung[]{createRang(rId3, null), createRang(rId4, endDate)}, new Rangierung[]{createRang(rId1, null), createRang(rId2, now)}, testView,true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIsOrderCancled")
    public void testIsOrderCancled(Rangierung[] rangierungenESTypB, Rangierung[] rangierungenESTypA,
            PhysikFreigebenView testView, boolean expectedResult) throws FindException {
        when(rangierungsServiceMock.findRangierungenTx(any(Long.class), eq(Endstelle.ENDSTELLEN_TYP_A))).thenReturn(
                rangierungenESTypA);
        when(rangierungsServiceMock.findRangierungenTx(any(Long.class), eq(Endstelle.ENDSTELLEN_TYP_B))).thenReturn(
                rangierungenESTypB);

        boolean result = cut.isOrderCanceled(testView);

        assertEquals(result, expectedResult);
    }
}
