/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2011 14:09:42
 */
package de.mnet.wita.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.model.TNB;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ProduktGruppe;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wita.dao.AnbieterwechselConfigDao;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.common.Carrier;
import de.mnet.wita.model.AnbieterwechselConfig;
import de.mnet.wita.model.AnbieterwechselConfig.NeuProdukt;
import de.mnet.wita.model.Vorabstimmung;
import de.mnet.wita.model.VorabstimmungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.WitaWbciServiceFacade;

@Test(groups = UNIT)
public class TalAnbieterwechseltypServiceImplTest extends BaseTest {

    @Spy
    @InjectMocks
    private TalAnbieterwechseltypServiceImpl underTest;

    @Mock
    private WitaDataService witaDataService;
    @Mock
    private AnbieterwechselConfigDao anbieterwechselConfigDao;
    @Mock
    private WitaWbciServiceFacade witaWbciServiceFacade;

    private WitaCBVorgang witaCbVorgang;


    @BeforeMethod
    public void setup() {
        underTest = new TalAnbieterwechseltypServiceImpl();
        MockitoAnnotations.initMocks(this);

        witaCbVorgang = new WitaCBVorgangBuilder().withAuftragId(123L).build();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNull() {
        underTest.determineGeschaeftsfall(null, witaCbVorgang);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testPvNull() {
        underTest.determineGeschaeftsfall(new CarrierbestellungBuilder().setPersist(false).build(), witaCbVorgang);
    }

    public void testAnbieterwechselForCarrierNotOnWita() throws FindException {
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().setPersist(false).build();
        when(witaDataService.loadVorabstimmung(witaCbVorgang)).thenReturn(new VorabstimmungBuilder().withCarrierNotOnWita().build());

        when(witaDataService.loadEquipments(carrierbestellung, witaCbVorgang.getAuftragId())).thenReturn(
                new Pair<>(null, new Equipment()));

        GeschaeftsfallTyp geschaeftsfallTyp = underTest.determineGeschaeftsfall(carrierbestellung, witaCbVorgang);
        assertEquals(geschaeftsfallTyp, GeschaeftsfallTyp.BEREITSTELLUNG);
    }

    public void verifyInteractionsWithServices() throws FindException {
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().setPersist(false).build();
        when(witaDataService.loadVorabstimmung(witaCbVorgang)).thenReturn(new VorabstimmungBuilder().build());

        when(witaDataService.loadEquipments(carrierbestellung, witaCbVorgang.getAuftragId())).thenReturn(
                new Pair<>(null, new Equipment()));
        when(anbieterwechselConfigDao.findConfig(any(Carrier.class), any(ProduktGruppe.class), any(NeuProdukt.class)))
                .thenReturn(null);

        underTest.determineGeschaeftsfall(carrierbestellung, witaCbVorgang);

        verify(witaDataService).loadEquipments(carrierbestellung, witaCbVorgang.getAuftragId());
        verify(anbieterwechselConfigDao)
                .findConfig(any(Carrier.class), any(ProduktGruppe.class), any(NeuProdukt.class));
    }

    @DataProvider
    public Object[][] produktGruppenProvider() {
        // @formatter:off
        return new Object[][] {
                { new VorabstimmungBuilder().withCarrierDtag(), ProduktGruppe.ADSL_SA },
                { new VorabstimmungBuilder().withCarrierDtag(), ProduktGruppe.ADSL_SH },
                { new VorabstimmungBuilder().withCarrierDtag(), ProduktGruppe.CLS },
                { new VorabstimmungBuilder().withCarrierDtag(), ProduktGruppe.SDSL },
                { new VorabstimmungBuilder().withCarrierDtag(), ProduktGruppe.TAL },
                { new VorabstimmungBuilder().withCarrierDtag(), ProduktGruppe.VDSL },
                { new VorabstimmungBuilder().withCarrierDtag(), ProduktGruppe.FTTH },
                { new VorabstimmungBuilder().withCarrierO2(), ProduktGruppe.DTAG_ANY },
        };
        // @formatter:on
    }

    @Test(dataProvider = "produktGruppenProvider", expectedExceptions = IllegalArgumentException.class)
    public void testUnsupportedProduktGruppen(VorabstimmungBuilder vorabstimmungBuilder,
            ProduktGruppe produktGruppe) throws FindException {

        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().setPersist(false).build();

        when(witaDataService.loadVorabstimmung(witaCbVorgang)).thenReturn(
                vorabstimmungBuilder.withProduktGruppe(produktGruppe).build());
        when(witaDataService.loadEquipments(carrierbestellung, witaCbVorgang.getAuftragId())).thenReturn(
                new Pair<>(null, new Equipment()));

        underTest.determineGeschaeftsfall(carrierbestellung, witaCbVorgang);
    }

    @Test(expectedExceptions = WitaBaseException.class)
    public void testNeu() {
        testGeschaeftsfallWithException(GeschaeftsfallTyp.BEREITSTELLUNG);
    }

    @Test(expectedExceptions = WitaBaseException.class)
    public void testRexMk() {
        testGeschaeftsfallWithException(GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG);
    }

    private void testGeschaeftsfallWithException(GeschaeftsfallTyp geschaeftsfallTyp) {
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().setPersist(false).build();
        when(witaDataService.loadVorabstimmung(witaCbVorgang)).thenReturn(new VorabstimmungBuilder().build());

        doReturn(true).when(underTest).isProduktGruppeValid(any(Vorabstimmung.class));
        doReturn(mock(Equipment.class)).when(underTest).getEquipment(carrierbestellung, witaCbVorgang.getAuftragId());

        doReturn(geschaeftsfallTyp).when(
                underTest).determineAnbieterwechseltyp(any(Vorabstimmung.class), any(Equipment.class));

        underTest.determineGeschaeftsfall(carrierbestellung, witaCbVorgang);
    }

    @DataProvider(name = "testGeschaeftsfallWithWbciVaIdDP")
    public Object[][] testGeschaeftsfallWithWbciVaIdDP() {
        return new Object[][] {
                { CarrierCode.DTAG, buildAkmTr(true), buildRuemVa(Technologie.KUPFER), dtagEquipment(), GeschaeftsfallTyp.VERBUNDLEISTUNG },
                { CarrierCode.VODAFONE, buildAkmTr(true), buildRuemVa(Technologie.ADSL_SA), dtagEquipment(), GeschaeftsfallTyp.VERBUNDLEISTUNG },
                { CarrierCode.EINS_UND_EINS, buildAkmTr(true), buildRuemVa(Technologie.ADSL_SH), dtagEquipment(), GeschaeftsfallTyp.VERBUNDLEISTUNG },
                { CarrierCode.KABEL_DEUTSCHLAND, buildAkmTr(true), buildRuemVa(Technologie.TAL_DSL), dtagEquipment(), GeschaeftsfallTyp.PROVIDERWECHSEL },
                { CarrierCode.TELEFONICA, buildAkmTr(true), buildRuemVa(Technologie.TAL_ISDN), dtagEquipment(), GeschaeftsfallTyp.PROVIDERWECHSEL },
                { CarrierCode.SIPGATE, buildAkmTr(true), buildRuemVa(Technologie.TAL_DSL), dtagEquipment(), GeschaeftsfallTyp.PROVIDERWECHSEL },
        };
    }

    @Test(dataProvider = "testGeschaeftsfallWithWbciVaIdDP")
    public void testGeschaeftsfallWithWbciVaId(CarrierCode carrier, UebernahmeRessourceMeldung akmTr, RueckmeldungVorabstimmung ruemva, Equipment equipment, GeschaeftsfallTyp geschaeftsfallTyp) {
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().setPersist(false).build();
        WitaCBVorgang cbv = new WitaCBVorgangBuilder().withAuftragId(123L).withVorabstimmungsId("DEU.MNET.123")
                .setPersist(false).build();

        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAbgebenderEKP(carrier)
                .buildValid(WbciCdmVersion.V1, de.mnet.wbci.model.GeschaeftsfallTyp.VA_KUE_MRN);

        doReturn(equipment).when(underTest).getEquipment(carrierbestellung, witaCbVorgang.getAuftragId());
        when(witaWbciServiceFacade.getWbciGeschaeftsfall(cbv.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(witaWbciServiceFacade.getLastAkmTr(cbv.getVorabstimmungsId())).thenReturn(akmTr);
        when(witaWbciServiceFacade.getRuemVa(cbv.getVorabstimmungsId())).thenReturn(ruemva);

        if (ruemva != null) {
            when(anbieterwechselConfigDao.findConfig(eq(Carrier.OTHER), eq(ruemva.getTechnologie().getProduktGruppe()), eq(NeuProdukt.TAL)))
                    .thenReturn(getAnbieterWechselConfig(ruemva.getTechnologie(), geschaeftsfallTyp));
        }

        GeschaeftsfallTyp result = underTest.determineGeschaeftsfall(carrierbestellung, cbv);
        assertEquals(result, geschaeftsfallTyp);

        GeschaeftsfallTyp resultWithoutWitaVorgang = underTest.determineAnbieterwechseltyp(cbv.getVorabstimmungsId(), equipment);
        assertEquals(resultWithoutWitaVorgang, geschaeftsfallTyp);
    }

    @Test
    public void testGeschaeftsfallWithWbciVaIdInvalidGfResult() {
        try {
            testGeschaeftsfallWithWbciVaId(CarrierCode.VODAFONE, buildAkmTr(true), buildRuemVa(Technologie.SDSL_SA), dtagEquipment(), GeschaeftsfallTyp.BEREITSTELLUNG);
            Assert.fail("Missing exception due to Bereitstellung GF which is not allowed");
        }
        catch (WitaBaseException e) {
            Assert.assertTrue(e.getMessage().contains("Dieser Geschaeftsfall wird z.Z. nicht automatisch unterstützt"));
        }

        try {
            testGeschaeftsfallWithWbciVaId(CarrierCode.VODAFONE, buildAkmTr(true), buildRuemVa(Technologie.TAL_DSL), dtagEquipment(), GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG);
            Assert.fail("Missing exception due to Rex-Mk GF which is not allowed");
        }
        catch (WitaBaseException e) {
            Assert.assertTrue(e.getMessage().contains("Rex-Mk"));
        }
    }

    @Test
    public void testGeschaeftsfallWithWbciVaIdInvalidNeuProdukt() {
        try {
            testGeschaeftsfallWithWbciVaId(CarrierCode.VODAFONE, buildAkmTr(true), buildRuemVa(Technologie.TAL_DSL), mnetEquipment(), GeschaeftsfallTyp.PROVIDERWECHSEL);
            Assert.fail("Missing exception due to NeuProdukt is not TAL");
        }
        catch (WitaBaseException e) {
            Assert.assertTrue(e.getMessage().contains("Zieltechnologie ungleich TAL"));
        }
    }

    @Test
    public void testGeschaeftsfallWithWbciVaIdNotUebernahmeResource() {
        try {
            testGeschaeftsfallWithWbciVaId(CarrierCode.VODAFONE, buildAkmTr(false), buildRuemVa(Technologie.TAL_DSL), dtagEquipment(), GeschaeftsfallTyp.PROVIDERWECHSEL);
            Assert.fail("Missing exception due to AKM-TR with UebernahmeResource = false");
        }
        catch (WitaBaseException e) {
            Assert.assertTrue(e.getMessage().contains("keine Übernahme der technischen Resource"));
        }
    }

    @Test
    public void testGeschaeftsfallWithWbciVaIdMissingAkmTr() {
        try {
            testGeschaeftsfallWithWbciVaId(CarrierCode.VODAFONE, null, buildRuemVa(Technologie.SDSL_SA), dtagEquipment(), GeschaeftsfallTyp.PROVIDERWECHSEL);
            Assert.fail("Missing exception due missing AMK-TR");
        }
        catch (NullPointerException e) {
            Assert.assertTrue(e.getMessage().contains("UebernahmeRessource Meldung"));
            Assert.assertTrue(e.getMessage().contains("konnte nicht ermittelt werden"));
        }
    }

    @Test
    public void testGeschaeftsfallWithWbciVaIdMissingRuemVa() {
        try {
            testGeschaeftsfallWithWbciVaId(CarrierCode.VODAFONE, buildAkmTr(true), null, dtagEquipment(), GeschaeftsfallTyp.PROVIDERWECHSEL);
            Assert.fail("Missing exception due to missing RUEMVA");
        }
        catch (NullPointerException e) {
            Assert.assertTrue(e.getMessage().contains("Rueckmeldung Vorabstimmung"));
            Assert.assertTrue(e.getMessage().contains("konnte nicht ermittelt werden"));
        }
    }

    private AnbieterwechselConfig getAnbieterWechselConfig(Technologie technologie, GeschaeftsfallTyp geschaeftsfallTyp) {
        AnbieterwechselConfig config = new AnbieterwechselConfig();
        config.setCarrierAbgebend(Carrier.OTHER);
        config.setAltProdukt(technologie.getProduktGruppe());
        config.setNeuProdukt(NeuProdukt.TAL);
        config.setGeschaeftsfallTyp(geschaeftsfallTyp);
        return config;
    }

    private UebernahmeRessourceMeldung buildAkmTr(boolean uebernahme) {
        return new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(uebernahme)
                .buildValid(WbciCdmVersion.V1, de.mnet.wbci.model.GeschaeftsfallTyp.VA_KUE_MRN);
    }

    private RueckmeldungVorabstimmung buildRuemVa(Technologie technologie) {
        return new RueckmeldungVorabstimmungTestBuilder()
                .withTechnologie(technologie)
                .buildValid(WbciCdmVersion.V1, de.mnet.wbci.model.GeschaeftsfallTyp.VA_KUE_MRN);
    }

    private Equipment dtagEquipment() {
        return new EquipmentBuilder().withCarrier(de.augustakom.hurrican.model.cc.Carrier.CARRIER_DTAG).build();
    }

    private Equipment mnetEquipment() {
        return new EquipmentBuilder().withCarrier(TNB.MNET.carrierNameUC).build();
    }
}
