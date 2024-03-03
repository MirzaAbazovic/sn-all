/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 13:56:16
 */
package de.mnet.wita.aggregator;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.AdresseBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EndstelleConnect;
import de.augustakom.hurrican.model.cc.EndstelleConnectBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.augustakom.hurrican.model.shared.view.AuftragDatenQuery;
import de.augustakom.hurrican.model.shared.view.AuftragDatenView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AvailabilityService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.ConnectService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.StandortKunde;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.impl.WitaDataService;

@Test(groups = UNIT)
public class StandortKundeAggregatorTest extends BaseTest {

    @InjectMocks
    private StandortKundeAggregator cut;
    private WitaCBVorgang cbVorgang;

    @Mock
    private EndstellenService endstellenServiceMock;
    @Mock
    private CarrierService carrierServiceMock;
    @Mock
    private AvailabilityService availabilityServiceMock;
    @Mock
    private WitaDataService witaDataService;
    @Mock
    private ConnectService connectService;
    @Mock
    private CCAuftragService auftragService;

    private Endstelle esB;

    @BeforeMethod
    public void setUp() throws Exception {
        cut = new StandortKundeAggregator();
        MockitoAnnotations.initMocks(this);

        cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();

        Carrierbestellung2EndstelleBuilder cb2EsBuilder = new Carrierbestellung2EndstelleBuilder().withRandomId()
                .setPersist(false);

        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withCb2EsBuilder(cb2EsBuilder)
                .setPersist(false).build();

        Endstelle esA = new EndstelleBuilder().withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_A).setPersist(false).build();
        esB = new EndstelleBuilder().withCb2EsBuilder(cb2EsBuilder).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .setPersist(false).build();

        when(carrierServiceMock.findCB(cbVorgang.getCbId())).thenReturn(carrierbestellung);
        when(endstellenServiceMock.findEndstellen4Auftrag(cbVorgang.getAuftragId()))
                .thenReturn(Arrays.asList(esA, esB));
    }

    public void aggregate() throws Exception {
        AddressModel address = new AdresseBuilder().setPersist(false).build();

        when(witaDataService.loadEndstellen(cbVorgang)).thenReturn(Arrays.asList(esB));
        when(endstellenServiceMock.findAnschlussadresse4Auftrag(cbVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(address);
        when(availabilityServiceMock.getDtagAddressForCb(esB.getGeoId(), address)).thenReturn(address);

        StandortKunde result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Kundenstandort wurde nicht generiert!");
        Kundenname kundenname = result.getKundenname();
        assertNotNull(kundenname);
        assertTrue(kundenname instanceof Personenname);
        assertEquals(kundenname.getAnrede(), Anrede.FRAU);
        assertEquals(((Personenname) kundenname).getNachname(), address.getName());
        assertEquals(((Personenname) kundenname).getVorname(), address.getVorname());
        assertEquals(result.getStrassenname(), address.getStrasse());
        assertEquals(result.getHausnummer(), address.getNummer());
        assertEquals(result.getHausnummernZusatz(), address.getHausnummerZusatz());
        assertEquals(result.getPostleitzahl(), address.getPlzTrimmed());
        assertEquals(result.getOrtsname(), address.getOrt());
        assertEquals(result.getOrtsteil(), address.getOrtsteil());
    }

    public void aggregateWithName2() throws Exception {
        AddressModel address = new AdresseBuilder().withName2("Name2").withVorname2("Vorname2").setPersist(false)
                .build();

        when(witaDataService.loadEndstellen(cbVorgang)).thenReturn(Arrays.asList(esB));
        when(endstellenServiceMock.findAnschlussadresse4Auftrag(cbVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(address);
        when(availabilityServiceMock.getDtagAddressForCb(esB.getGeoId(), address)).thenReturn(address);

        StandortKunde result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Kundenstandort wurde nicht generiert!");
        Kundenname kundenname = result.getKundenname();
        assertNotNull(kundenname);
        assertTrue(kundenname instanceof Personenname);
        assertEquals(kundenname.getAnrede(), Anrede.FRAU);
        assertEquals(((Personenname) kundenname).getNachname(), address.getName() + " und " + address.getName2());
        assertEquals(((Personenname) kundenname).getVorname(), address.getVorname() + " und " + address.getVorname2());
        assertEquals(result.getStrassenname(), address.getStrasse());
        assertEquals(result.getHausnummer(), address.getNummer());
        assertEquals(result.getHausnummernZusatz(), address.getHausnummerZusatz());
        assertEquals(result.getPostleitzahl(), address.getPlzTrimmed());
        assertEquals(result.getOrtsname(), address.getOrt());
    }

    public void aggregateLageTaeFromKundenuebergabeFromWholeOrder() throws Exception {
        AddressModel address = new AdresseBuilder().withFloor(null).setPersist(false).build();

        when(witaDataService.loadEndstellen(cbVorgang)).thenReturn(Arrays.asList(esB));
        when(endstellenServiceMock.findAnschlussadresse4Auftrag(cbVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(address);
        when(availabilityServiceMock.getDtagAddressForCb(esB.getGeoId(), address)).thenReturn(address);

        AuftragDaten auftragDaten = (new AuftragDatenBuilder()).build();
        AuftragDatenView auftragDatenView = new AuftragDatenView();
        auftragDatenView.setAuftragId(123456789L);
        when(auftragService.findAuftragDatenByAuftragId(cbVorgang.getAuftragId())).thenReturn(auftragDaten);
        when(auftragService.findAuftragDatenViews(any(AuftragDatenQuery.class), eq(false))).thenReturn(Arrays.asList(auftragDatenView));

        Endstelle esB1 = (new EndstelleBuilder()).withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B).build();
        when(endstellenServiceMock.findEndstelle4Auftrag(auftragDatenView.getAuftragId(), esB.getEndstelleTyp())).thenReturn(esB1);
        EndstelleConnect esConnect = (new EndstelleConnectBuilder()).withGebaude("Geb. 2").withEtage("3. Etage")
                .withRaum("234").build();
        when(connectService.findEndstelleConnectByEndstelle(esB1)).thenReturn(esConnect);

        StandortKunde result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Kundenstandort wurde nicht generiert!");
        Kundenname kundenname = result.getKundenname();
        assertNotNull(kundenname);
        assertTrue(kundenname instanceof Personenname);
        assertThat(result.getLageTAEDose(), equalTo("Geb. 2, 3. Etage, 234"));
    }

    public void aggregateLageTaeFromKundenuebergabe() throws Exception {
        AddressModel address = new AdresseBuilder().withFloor(null).setPersist(false).build();

        when(witaDataService.loadEndstellen(cbVorgang)).thenReturn(Arrays.asList(esB));
        when(endstellenServiceMock.findAnschlussadresse4Auftrag(cbVorgang.getAuftragId(), Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(address);
        when(availabilityServiceMock.getDtagAddressForCb(esB.getGeoId(), address)).thenReturn(address);
        EndstelleConnect esConnect = (new EndstelleConnectBuilder()).withGebaude("Geb. 1").withEtage("2. Etage")
                .withRaum("1234").build();
        when(connectService.findEndstelleConnectByEndstelle(esB)).thenReturn(esConnect);

        StandortKunde result = cut.aggregate(cbVorgang);
        assertNotNull(result, "Kundenstandort wurde nicht generiert!");
        Kundenname kundenname = result.getKundenname();
        assertNotNull(kundenname);
        assertTrue(kundenname instanceof Personenname);
        assertThat(result.getLageTAEDose(), equalTo("Geb. 1, 2. Etage, 1234"));
    }

    @DataProvider
    public Object[][] lageTaeDataProvider() {
        // @formatter:off
        return new Object[][] {
            { "gebäude", "etage", "raum", "gebäude, etage, raum"},
            { null, "etage", "raum", "etage, raum"},
            { "gebäude", "etage", null, "gebäude, etage"},
            { "gebäude", null, "raum", "gebäude, raum"},
            { "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789",
                "012345678901234567890123456789012345678901234567890123456789", "nicht mehr in result",
                "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789, 0123456789012345678901234567890123456789012345678901234..."},
        };
        // @formatter:on
    }

    @Test(dataProvider = "lageTaeDataProvider")
    public void getLageTae(String gebaeude, String etage, String raum, String expected) throws FindException {
        // @formatter:off
        EndstelleConnect esConnect = new EndstelleConnectBuilder()
            .withGebaude(gebaeude)
            .withEtage(etage)
            .withRaum(raum)
            .setPersist(false).build();
        // @formatter:on

        StandortKundeAggregator spy = spy(cut);
        doReturn(new EndstelleBuilder().setPersist(false).build()).when(spy).loadEndstelle(cbVorgang);
        when(connectService.findEndstelleConnectByEndstelle(any(Endstelle.class))).thenReturn(esConnect);

        String lageTae = spy.getLageTae(new CCAddressBuilder().withStrasseAdd(null).setPersist(false).build(),
                cbVorgang);
        assertNotNull(lageTae);
        assertThat(lageTae, equalTo(expected));
        assertTrue(lageTae.length() <= 160);
    }

    public void findAdresseStandortForAnbieterwechselWithDtag() throws FindException {
        cbVorgang.setTyp(CBVorgang.TYP_ANBIETERWECHSEL);
        cbVorgang.setCarrierId(Carrier.ID_DTAG);

        StandortKundeAggregator spy = spy(cut);
        CCAddress anschlussAddress = new CCAddress();
        doReturn(anschlussAddress).when(spy).loadAnschlussadresse(cbVorgang);

        AddressModel standortAddress = spy.findAdresseStandort(cbVorgang);
        assertEquals(standortAddress, anschlussAddress);
    }

}
