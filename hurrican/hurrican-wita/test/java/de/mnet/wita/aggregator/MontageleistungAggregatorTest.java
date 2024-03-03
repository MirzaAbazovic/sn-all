/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 13:43:39
 */
package de.mnet.wita.aggregator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Ansprechpartner.Typ;
import de.augustakom.hurrican.model.cc.AnsprechpartnerBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.wita.exceptions.WitaDataAggregationException;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.builder.auftrag.MontageleistungBuilder;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

/**
 * TestNG Klasse fuer {@link MontageleistungAggregator}.
 */
@Test(groups = BaseTest.UNIT)
public class MontageleistungAggregatorTest extends BaseTest {

    @Spy
    private MontageleistungAggregator underTest;
    @Mock
    private AnsprechpartnerService ansprechpartnerService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private WitaDataService witaDataService;

    private WitaCBVorgang cbVorgang;
    private Ansprechpartner ansprechpartner;
    private CCAddress address;

    @BeforeMethod
    public void setUp() {
        underTest = new MontageleistungAggregator();
        MockitoAnnotations.initMocks(this);
        underTest.ansprechpartnerService = ansprechpartnerService;
        underTest.witaDataService = witaDataService;
        underTest.ccAuftragService = auftragService;

        cbVorgang = new WitaCBVorgangBuilder().withAuftragId(Long.MAX_VALUE).withMontagehinweis("#OSL_Bereit_MM#")
                .setPersist(false).build();

        CCAddressBuilder addressBuilder = new CCAddressBuilder().withFormatName("Frau").setPersist(false);
        address = addressBuilder.build();
        ansprechpartner = new AnsprechpartnerBuilder().withAddressBuilder(addressBuilder).setPersist(false).build();

    }

    @Test(expectedExceptions = WitaDataAggregationException.class)
    public void aggregateWithMontageleistungWithoutAnsprechpartner() throws FindException {
        doReturn(Typ.ENDSTELLE_B).when(underTest).loadAnsprechpartnerTyp(cbVorgang);
        when(ansprechpartnerService.findAnsprechpartner(Typ.ENDSTELLE_B, cbVorgang.getAuftragId()))
                .thenReturn(null);
        underTest.aggregate(cbVorgang);
    }

    public void aggregateExpectNull() throws FindException {
        doReturn(Typ.ENDSTELLE_B).when(underTest).loadAnsprechpartnerTyp(cbVorgang);
        when(ansprechpartnerService.findAnsprechpartner(Typ.ENDSTELLE_B, cbVorgang.getAuftragId()))
                .thenReturn(null);

        cbVorgang.setMontagehinweis(null);
        Montageleistung result = underTest.aggregate(cbVorgang);

        assertNull(result);
        verify(ansprechpartnerService).findAnsprechpartner(Typ.ENDSTELLE_B, cbVorgang.getAuftragId());
    }

    public void aggregate() throws FindException {
        doReturn(Typ.ENDSTELLE_B).when(underTest).loadAnsprechpartnerTyp(cbVorgang);
        when(ansprechpartnerService.findAnsprechpartner(Typ.ENDSTELLE_B, cbVorgang.getAuftragId())).thenReturn(
                Arrays.asList(ansprechpartner));

        Montageleistung result = underTest.aggregate(cbVorgang);

        assertNotNull(result);
        Personenname personenname = result.getPersonenname();
        assertNotNull(personenname);
        assertEquals(personenname.getAnrede(), Anrede.FRAU);
        assertEquals(personenname.getNachname(), address.getName());
        assertEquals(personenname.getVorname(), address.getVorname());
        assertEquals(result.getTelefonnummer(), address.getTelefon());
        assertEquals(result.getEmailadresse(), address.getEmail());
        assertEquals(result.getMontagehinweis(), cbVorgang.getMontagehinweis());
    }

    public void aggregateLoadAnsprechpartnerFromAnotherOrder() throws FindException {
        doReturn(Typ.ENDSTELLE_B).when(underTest).loadAnsprechpartnerTyp(cbVorgang);

        AuftragDatenView adViewAdd = new AuftragDatenView();
        adViewAdd.setAuftragId(Long.valueOf(1));

        AuftragDatenView adViewOrig = new AuftragDatenView();
        adViewOrig.setAuftragId(cbVorgang.getAuftragId());

        AuftragDaten auftragDatenOrig = new AuftragDatenBuilder().setPersist(false).build();

        when(auftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDatenOrig);
        when(auftragService.findAuftragDatenViews(any(AuftragDatenQuery.class), eq(false))).thenReturn(Arrays.asList(adViewAdd, adViewOrig));

        when(ansprechpartnerService.findAnsprechpartner(Typ.ENDSTELLE_B, adViewAdd.getAuftragId())).thenReturn(
                Arrays.asList(ansprechpartner));

        Montageleistung result = underTest.aggregate(cbVorgang);

        verify(underTest).loadAnsprechpartnerFromWholeOrder(cbVorgang.getAuftragId(), Typ.ENDSTELLE_B);
        verify(ansprechpartnerService).findAnsprechpartner(Typ.ENDSTELLE_B, cbVorgang.getAuftragId());
        verify(ansprechpartnerService).findAnsprechpartner(Typ.ENDSTELLE_B, adViewAdd.getAuftragId());
        verify(ansprechpartnerService, times(2)).findAnsprechpartner(any(Typ.class), any(Long.class));
        assertNotNull(result);
    }

    @DataProvider
    public Object[][] endstelle4AnsprechpartnerTyp() {
        return new Object[][] {
                { new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).setPersist(false).build(),
                        Typ.ENDSTELLE_A },
                { new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).setPersist(false).build(),
                        Typ.ENDSTELLE_B }, };
    }

    @Test(dataProvider = "endstelle4AnsprechpartnerTyp")
    public void loadAnsprechpartnerTyp(Endstelle es, Typ expectedType) {
        when(witaDataService.loadEndstellen(cbVorgang)).thenReturn(Arrays.asList(es));

        Typ result = underTest.loadAnsprechpartnerTyp(cbVorgang);

        assertNotNull(result);
        assertEquals(result, expectedType);
    }


    @DataProvider
    public Object[][] emails() {
        return new Object[][] {
                { null, null },
                { "", null },
                { "valid@email.de", "valid@email.de" },
                { "invalidmail", null },
                { "invalid@mail", null },
                { "first@mail.de,second@mail.de", "first@mail.de" },
                { "first@mail.de;second@mail.de", "first@mail.de" },
                { "invalidmail.de,second@mail.de", "second@mail.de" },
                { "invalidmail.de;second@mail.de", "second@mail.de" },
                { "invalidmail.de,secondmail.de,third@mail.de,", "third@mail.de" },
        };
    }

    @Test(dataProvider = "emails")
    public void getFirstValidEMail(String mail, String expected) {
        String firstValid = underTest.getFirstValidEMail(mail);
        assertEquals(firstValid, expected);
    }

    public void appendVbzOfHvtOrderNoExecutionExpected() {
        cbVorgang.setCbVorgangRefId(null);

        String montagehinweis = "montagehinweis xyz";
        Montageleistung montageleistung = new MontageleistungBuilder()
                .withMontagehinweis(montagehinweis)
                .buildValid();

        underTest.appendVbzOfHvtOrder(montageleistung, cbVorgang);
        verify(witaDataService, times(0)).getCarrierbestellungReferencedByCbVorgang(anyLong());
        assertEquals(montageleistung.getMontagehinweis(), montagehinweis);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class, expectedExceptionsMessageRegExp =
            "Auf dem zu kündigenden HVt Auftrag konnte keine Leitungsbezeichnung ermittelt werden.(?s).*Diese ist für eine Umschaltung HVt -> KVz jedoch zwingend notwendig!")
    public void appendVbzOfHvtOrderNoCarrierbestellungFound() {
        cbVorgang.setTyp(CBVorgang.TYP_NEU);
        cbVorgang.setCbVorgangRefId(99L);

        String montagehinweis = "montagehinweis xyz";
        Montageleistung montageleistung = new MontageleistungBuilder()
                .withMontagehinweis(montagehinweis)
                .buildValid();
        when(witaDataService.getCarrierbestellungReferencedByCbVorgang(anyLong())).thenReturn(null);

        underTest.appendVbzOfHvtOrder(montageleistung, cbVorgang);
        verify(witaDataService, times(0)).getCarrierbestellungReferencedByCbVorgang(anyLong());
        assertEquals(montageleistung.getMontagehinweis(), montagehinweis);
    }

    @Test(expectedExceptions = WitaDataAggregationException.class, expectedExceptionsMessageRegExp =
            "Auf dem zu kündigenden HVt Auftrag konnte keine Leitungsbezeichnung ermittelt werden.(?s).*Diese ist für eine Umschaltung HVt -> KVz jedoch zwingend notwendig!")
    public void appendVbzOfHvtOrderNoLbz() {
        cbVorgang.setTyp(CBVorgang.TYP_NEU);
        cbVorgang.setCbVorgangRefId(99L);

        String montagehinweis = "montagehinweis xyz";
        Montageleistung montageleistung = new MontageleistungBuilder()
                .withMontagehinweis(montagehinweis)
                .buildValid();
        Carrierbestellung cb = new CarrierbestellungBuilder()
                .withLbz("")
                .setPersist(false)
                .build();
        when(witaDataService.getCarrierbestellungReferencedByCbVorgang(anyLong())).thenReturn(cb);

        underTest.appendVbzOfHvtOrder(montageleistung, cbVorgang);
        verify(witaDataService, times(0)).getCarrierbestellungReferencedByCbVorgang(anyLong());
        assertEquals(montageleistung.getMontagehinweis(), montagehinweis);
    }

    public void appendVbzOfHvtOrder() {
        cbVorgang.setTyp(CBVorgang.TYP_NEU);
        cbVorgang.setCbVorgangRefId(99L);

        String montagehinweis = "montagehinweis xyz";
        Montageleistung montageleistung = new MontageleistungBuilder()
                .withMontagehinweis(montagehinweis)
                .buildValid();

        Carrierbestellung cb = new CarrierbestellungBuilder()
                .withLbz("96W/821/821/123456")
                .setPersist(false)
                .build();
        when(witaDataService.getCarrierbestellungReferencedByCbVorgang(anyLong())).thenReturn(cb);

        underTest.appendVbzOfHvtOrder(montageleistung, cbVorgang);
        assertTrue(montageleistung.getMontagehinweis().contains(cb.getLbz()));
        assertEquals(montageleistung.getMontagehinweis(), "KUE TAL [96W/821/821/123456] montagehinweis xyz");
    }

}
