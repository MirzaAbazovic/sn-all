/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 14:36:23
 */
package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import java.util.*;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.Inhouse;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2PhysikTyp;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.VPNBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.query.Produkt2PhysikTypQuery;
import de.augustakom.hurrican.model.cc.view.VerbindungsBezeichnungHistoryView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * JUnit-Test fuer <code>PhysikServiceImpl</code>.
 *
 *
 */
@Test(groups = SERVICE)
public class PhysikServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(PhysikServiceTest.class);
    private PhysikService physikService;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        physikService = getCCService(PhysikService.class);
    }

    @Test(enabled = true)
    public void testCalculateVerbindungsBezeichnung() throws FindException {
        String kouProduct = "X";
        String kouType = "Y";
        Produkt produkt = getProduktBuilder(kouProduct, kouType)
                .build();

        VerbindungsBezeichnung verbindungsBezeichnung = physikService.calculateVerbindungsBezeichnung(produkt.getId(), null);
        flushAndClear();
        assertNotNull(verbindungsBezeichnung, "VerbindungsBezeichnung not created!");
        assertNull(verbindungsBezeichnung.getId(), "VerbindungsBezeichnung was saved!");
        assertEquals(verbindungsBezeichnung.getKindOfUseProduct(), kouProduct, "Parameter kindOfUseProduct not as expected");
        assertEquals(verbindungsBezeichnung.getKindOfUseType(), kouType, "Parameter kindOfUseType not as expected");
        assertNotNull(verbindungsBezeichnung.getUniqueCode(), "Unique code not generated!");
        assertNotNull(verbindungsBezeichnung.getVbz(), "values not combined to VerbindungsBezeichnung!");
    }

    @Test(enabled = true)
    public void testCalculateVerbindungsBezeichnungForWholesale() throws FindException {
        Produkt produkt = getBuilder(ProduktBuilder.class).withLeitungsNrAnlegen(Boolean.TRUE).withProduktGruppeId(ProduktGruppe.WHOLESALE).build();

        VerbindungsBezeichnung verbindungsBezeichnung = physikService.calculateVerbindungsBezeichnung(produkt.getId(), null);
        flushAndClear();
        assertNotNull(verbindungsBezeichnung, "VerbindungsBezeichnung not created!");
        assertNull(verbindungsBezeichnung.getId(), "VerbindungsBezeichnung was saved!");
        assertTrue(verbindungsBezeichnung.getVbz().startsWith("DEU.MNET."), "LineId Format invalid!");
        assertNull(verbindungsBezeichnung.getUniqueCode(), "UniqueCode musst be null!");
        assertNull(verbindungsBezeichnung.getKindOfUseType(), "KindOfUseType musst be null!");
        assertNull(verbindungsBezeichnung.getKindOfUseProduct(), "KindOfUseProduct musst be null!");
    }

    @Test(enabled = true)
    public void testCreateVerbindungsBezeichnung() throws StoreException {
        String kouProduct = "X";
        String kouType = "Y";
        Produkt produkt = getProduktBuilder(kouProduct, kouType)
                .build();

        VerbindungsBezeichnung verbindungsBezeichnung = physikService.createVerbindungsBezeichnung(produkt.getId(), null);
        flushAndClear();
        assertNotNull(verbindungsBezeichnung, "VerbindungsBezeichnung not created!");
        assertNotNull(verbindungsBezeichnung.getId(), "VerbindungsBezeichnung was not saved!");
        assertEquals(verbindungsBezeichnung.getKindOfUseProduct(), kouProduct, "Parameter kindOfUseProduct not as expected");
        assertEquals(verbindungsBezeichnung.getKindOfUseType(), kouType, "Parameter kindOfUseType not as expected");
        assertNotNull(verbindungsBezeichnung.getUniqueCode(), "Unique code not generated!");
        assertNotNull(verbindungsBezeichnung.getVbz(), "values not combined to VerbindungsBezeichnung!");
    }

    @Test(enabled = true)
    public void testCreateVerbindungsBezeichnungNoResult() throws StoreException {
        Produkt produkt = getBuilder(ProduktBuilder.class)
                .withLeitungsNrAnlegen(Boolean.FALSE)
                .build();

        VerbindungsBezeichnung verbindungsBezeichnung = physikService.createVerbindungsBezeichnung(produkt.getId(), null);
        assertNull(verbindungsBezeichnung, "VerbindungsBezeichnung created but not expected!");
    }

    @Test(enabled = true)
    public void testGetVerbindungsBezeichnungFromMaster() throws FindException {
        ProduktBuilder slaveProduktBuilder = getBuilder(ProduktBuilder.class)
                .withLeitungsNrAnlegen(Boolean.FALSE);

        Long taifunOrderNo = 123L;
        ProduktBuilder masterProduktBuilder = getProduktBuilder("X", "Y");
        AuftragBuilder masterAuftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class)
                        .withAuftragNoOrig(taifunOrderNo)
                        .withProdBuilder(masterProduktBuilder));
        VerbindungsBezeichnungBuilder vbzBuilder = getVerbindungsBezeichnungBuilder(masterProduktBuilder);
        getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder)
                .withAuftragBuilder(masterAuftragBuilder)
                .build();

        getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class)
                        .withAuftragNoOrig(taifunOrderNo)
                        .withProdBuilder(slaveProduktBuilder))
                .build();

        VerbindungsBezeichnung verbindungsBezeichnung = physikService.getVerbindungsBezeichnungFromMaster(taifunOrderNo);
        assertNotNull(verbindungsBezeichnung, "VerbindungsBezeichnung from master not found!");
        assertEquals(verbindungsBezeichnung.getVbz(), vbzBuilder.get().getVbz(), "returned VerbindungsBezeichnung not equal to master VerbindungsBezeichnung");
        assertEquals(verbindungsBezeichnung.getId(), vbzBuilder.get().getId(), "returned VerbindungsBezeichnung not equal to master VerbindungsBezeichnung");
    }

    @Test(enabled = true)
    public void testReCalculateVerbindungsBezeichnung() throws StoreException, FindException {
        String vpnName = "BMW000";
        VPNBuilder vpnBuilder = getVPNBuilder()
                .withVpnName(vpnName);

        ProduktBuilder prodBuilder = getProduktBuilder("X", "Y");

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class)
                        .withProdBuilder(prodBuilder));

        VerbindungsBezeichnungBuilder vbzBuilder = getVerbindungsBezeichnungBuilder(prodBuilder);
        getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder)
                .withAuftragBuilder(auftragBuilder)
                .withVPNBuilder(vpnBuilder)
                .build();

        physikService.reCalculateVerbindungsBezeichnung(auftragBuilder.get().getId());
        VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragIdTx(auftragBuilder.get().getId());
        assertNotNull(verbindungsBezeichnung, "VerbindungsBezeichnung not found!");
        assertTrue(verbindungsBezeichnung.getVbz().startsWith(vpnName + "-" + VerbindungsBezeichnung.KindOfUseProduct.V.name()), "VPN not recognized for VerbindungsBezeichnung");
    }

    @Test(enabled = true)
    public void testReCalculateVerbindungsBezeichnungOverwritten() throws StoreException, FindException {
        VPNBuilder vpnBuilder = getVPNBuilder();
        ProduktBuilder prodBuilder = getProduktBuilder("X", "Y");

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class)
                        .withProdBuilder(prodBuilder));

        VerbindungsBezeichnungBuilder vbzBuilder = getVerbindungsBezeichnungBuilder(prodBuilder)
                .withRandomUniqueCode()
                .withOverwritten(Boolean.TRUE);

        getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder)
                .withAuftragBuilder(auftragBuilder)
                .withVPNBuilder(vpnBuilder)
                .build();

        physikService.reCalculateVerbindungsBezeichnung(auftragBuilder.get().getId());
        VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragIdTx(auftragBuilder.get().getId());
        assertNotNull(verbindungsBezeichnung, "VerbindungsBezeichnung not found!");
        assertEquals(verbindungsBezeichnung.getVbz(), vbzBuilder.get().getVbz(), "VerbindungsBezeichnung is modified but should not (because of flag 'overwritten')");
    }

    @Test(enabled = true)
    public void testSaveVerbindungsBezeichnung() throws StoreException, FindException, ValidationException {
        VerbindungsBezeichnung verbindungsBezeichnung = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withUniqueCode(20)
                .withKindOfUseProduct("X")
                .withKindOfUseType("Y")
                .build();
        verbindungsBezeichnung.setCustomerIdent("MNET1A");
        physikService.saveVerbindungsBezeichnung(verbindungsBezeichnung);

        assertTrue(verbindungsBezeichnung.getVbz().startsWith("MNET1A-"), "VerbindungsBezeichnung value is not valid!");
        assertEquals(verbindungsBezeichnung.getVbz(), "MNET1A-XY" + String.format("%08d", verbindungsBezeichnung.getUniqueCode()), "VerbindungsBezeichnung value not OK!");
    }

    @Test(expectedExceptions = { ValidationException.class })
    public void testSaveVerbindungsBezeichnungInvalidCustomerIdent1() throws StoreException, FindException, ValidationException {
        createAndSaveVerbindungsBezeichnung("MNET1");
    }

    @Test(expectedExceptions = { ValidationException.class })
    public void testSaveVerbindungsBezeichnungInvalidCustomerIdent2() throws StoreException, FindException, ValidationException {
        createAndSaveVerbindungsBezeichnung("MNET1a");
    }

    @Test
    public void testFindVerbindungsBezeichnungByAuftragId() throws FindException {
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("TEST-VBZ");
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder);
        auftragTechnikBuilder.build();
        VerbindungsBezeichnung foundVerbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragIdTx(
                auftragTechnikBuilder.get().getAuftragId());
        assertEquals(foundVerbindungsBezeichnung, vbzBuilder.get());
    }

    /**
     * test the creation and found logic of the method {@link PhysikService#findOrCreateVerbindungsBezeichnungForWbci(Long)},
     * when the Verbindungsbezeichnung won't macht to the WBCI pattern.
     */
    @Test
    public void testFindOrCreateVerbindungsBezeichnungForWbci() throws Exception {
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withRandomUniqueCode()
                .withKindOfUseProduct("X")
                .withKindOfUseType("Y")
                .withCustomerIdent("WBCI01");
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder);
        auftragTechnikBuilder.build();

        VerbindungsBezeichnung foundVbz = physikService.findOrCreateVerbindungsBezeichnungForWbci(
                auftragTechnikBuilder.get().getAuftragId());
        assertEquals(foundVbz, vbzBuilder.get());
        assertNotNull(foundVbz.getWbciLineId());
        assertTrue(foundVbz.getWbciLineId().startsWith("DEU.MNET.W"));
        assertEquals(foundVbz.getWbciLineId().length(), 19);

        VerbindungsBezeichnung secondfoundVbz = physikService.findOrCreateVerbindungsBezeichnungForWbci(auftragTechnikBuilder.get().getAuftragId());
        assertEquals(foundVbz, secondfoundVbz);
        assertEquals(foundVbz.getWbciLineId(), secondfoundVbz.getWbciLineId());
    }

    /**
     * test the creation and found logic of the method {@link PhysikService#findOrCreateVerbindungsBezeichnungForWbci(Long)},
     * when the Verbindungsbezeichnung will macht to the WBCI pattern.
     */
    @Test
    public void testFindOrCreateVerbindungsBezeichnungForWbci2() throws Exception {
        String vbz = "DEU.MNET.000001";
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withRandomUniqueCode()
                .withKindOfUseProduct("X")
                .withKindOfUseType("Y")
                .withCustomerIdent("WBCI02")
                .withVbz(vbz);
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder);
        auftragTechnikBuilder.build();

        VerbindungsBezeichnung foundVbz = physikService.findOrCreateVerbindungsBezeichnungForWbci(
                auftragTechnikBuilder.get().getAuftragId());
        assertEquals(foundVbz, vbzBuilder.get());
        assertNotNull(foundVbz.getWbciLineId());
        assertNotEquals(foundVbz.getWbciLineId(), vbz);
        assertTrue(foundVbz.getWbciLineId().startsWith("DEU.MNET.W"));
        assertEquals(foundVbz.getWbciLineId().length(), 19);

        VerbindungsBezeichnung secondfoundVbz = physikService.findOrCreateVerbindungsBezeichnungForWbci(auftragTechnikBuilder.get().getAuftragId());
        assertEquals(foundVbz, secondfoundVbz);
        assertEquals(foundVbz.getWbciLineId(), secondfoundVbz.getWbciLineId());
    }

    @Test
    public void testFindVerbindungsBezeichnungByKundeNoOrig() throws FindException {
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("TEST-VBZ");
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder);
        long kundeNo = 123345623L;
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withKundeNo(kundeNo);
        auftragBuilder.build();
        List<VerbindungsBezeichnung> foundVerbindungsBezeichnungen =
                physikService.findVerbindungsBezeichnungenByKundeNoOrig(kundeNo);
        assertEquals(foundVerbindungsBezeichnungen.iterator().next(), vbzBuilder.get());
    }

    @Test
    public void testFindVerbindungsBezeichnungLike() throws FindException {
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("BLA-UNIT-TEST25-VBZ");
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder);
        auftragTechnikBuilder.build();
        List<VerbindungsBezeichnung> foundVerbindungsBezeichnungen =
                physikService.findVerbindungsBezeichnungLike("*UNIT-TEST25*");
        assertEquals(foundVerbindungsBezeichnungen.iterator().next(), vbzBuilder.get());
    }

    @Test
    public void testFindVerbindungsHistoryView() throws FindException {
        VerbindungsBezeichnungBuilder vbzBuilder = getBuilder(VerbindungsBezeichnungBuilder.class).withVbz("UNIT-TEST26");
        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withVerbindungsBezeichnungBuilder(vbzBuilder)
                .withEndstelleBuilder(getBuilder(EndstelleBuilder.class));
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class));
        auftragBuilder.build();
        List<VerbindungsBezeichnungHistoryView> foundVerbindungsBezeichnungen =
                physikService.findVerbindungsBezeichnungHistory("UNIT-TEST26");
        assertNotEmpty(foundVerbindungsBezeichnungen, "Keine History zu der Verbindungsbezeichnung gefunden!");
    }

    private VerbindungsBezeichnung createAndSaveVerbindungsBezeichnung(String customerIdent) throws StoreException, ValidationException {
        VerbindungsBezeichnung verbindungsBezeichnung = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withRandomUniqueCode()
                .withKindOfUseProduct("X")
                .withKindOfUseType("Y")
                .withCustomerIdent(customerIdent)
                .build();
        physikService.saveVerbindungsBezeichnung(verbindungsBezeichnung);
        return verbindungsBezeichnung;
    }

    @Test(enabled = true)
    public void testSaveVerbindungsBezeichnungWithoutUniqueCode() throws StoreException, ValidationException {
        String vbzValue = "old-verbindungsBezeichnung-value";
        VerbindungsBezeichnung verbindungsBezeichnung = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withVbz(vbzValue)
                .build();
        verbindungsBezeichnung.setCustomerIdent("custom");
        physikService.saveVerbindungsBezeichnung(verbindungsBezeichnung);

        assertEquals(verbindungsBezeichnung.getVbz(), vbzValue, "VerbindungsBezeichnung value not OK!");
    }

    @Test(enabled = true, expectedExceptions = ValidationException.class)
    public void testSaveVerbindungsBezeichnungWithoutKindOfUseProduct() throws StoreException, ValidationException {
        VerbindungsBezeichnung verbindungsBezeichnung = getBuilder(VerbindungsBezeichnungBuilder.class)
                .withRandomUniqueCode()
                .withKindOfUseProduct(null)
                .withKindOfUseType("Y")
                .build();
        verbindungsBezeichnung.setCustomerIdent("custom");
        physikService.saveVerbindungsBezeichnung(verbindungsBezeichnung);
    }

    @Test(enabled = true)
    public void testGetVbzValue4TAL() throws FindException {
        VPNBuilder vpnBuilder = getVPNBuilder();
        ProduktBuilder prodBuilder = getProduktBuilder("X", "Y");

        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class)
                .withAuftragDatenBuilder(getBuilder(AuftragDatenBuilder.class)
                        .withProdBuilder(prodBuilder));

        VerbindungsBezeichnungBuilder vbzBuilder = getVerbindungsBezeichnungBuilder(prodBuilder);
        EndstelleBuilder endstelleBuilder = getBuilder(EndstelleBuilder.class)
                .withEndstelleTyp(Endstelle.ENDSTELLEN_TYP_B)
                .withRangierungBuilder(getBuilder(RangierungBuilder.class)
                        .withEqOutBuilder(getBuilder(EquipmentBuilder.class)
                                .withRangStift1("13")));

        getBuilder(AuftragTechnikBuilder.class)
                .withEndstelleBuilder(endstelleBuilder)
                .withVerbindungsBezeichnungBuilder(vbzBuilder)
                .withAuftragBuilder(auftragBuilder)
                .withVPNBuilder(vpnBuilder)
                .build();

        String vbzValue = physikService.getVbzValue4TAL(auftragBuilder.get().getId(), Endstelle.ENDSTELLEN_TYP_B);
        assertNotNull(vbzValue, "VerbindungsBezeichnung value not created!");
        assertEquals(vbzValue, vbzBuilder.get().getVbz() + "_B_0013", "TAL-ID not as expected!");
    }

    private VPNBuilder getVPNBuilder() {
        Long vpnNr = 11L;
        return getBuilder(VPNBuilder.class)
                .withVpnNr(vpnNr)
                .withDatum(new Date())
                .withKundeNo(500000001L);
    }

    private VerbindungsBezeichnungBuilder getVerbindungsBezeichnungBuilder(ProduktBuilder produktBuilder) {
        return getBuilder(VerbindungsBezeichnungBuilder.class)
                .withRandomUniqueCode()
                .withKindOfUseProduct(produktBuilder.get().getVbzKindOfUseProduct())
                .withKindOfUseType(produktBuilder.get().getVbzKindOfUseType());
    }

    private ProduktBuilder getProduktBuilder(String kindOfUseProduct, String kindOfUseType) {
        return getBuilder(ProduktBuilder.class)
                .withLeitungsNrAnlegen(Boolean.TRUE)
                .withVbzKindOfUseProduct(kindOfUseProduct)
                .withVbzKindOfUseType(kindOfUseType);
    }

    /* #################### old tests ###################### */

    @Test(enabled = true)
    public void testFindPhysikTypen() throws FindException {
        List<PhysikTyp> result = physikService.findPhysikTypen();
        assertNotEmpty(result, "Keine Physik-Typen gefunden!");
        LOGGER.debug("Anzahl Physik-Typen: " + result.size());
    }

    @Test(enabled = false)
    public void testFindPhysikTyp() throws FindException {
        Long id = 1L;
        PhysikTyp pt = physikService.findPhysikTyp(id);
        assertNotNull(pt, "Kein PhysikTyp gefunden fuer ID " + id + "!");
    }

    @Test(enabled = false)
    public void testFindInhouse4ES() throws FindException {
        Long esId = 3700L;
        Inhouse result = physikService.findInhouse4ES(esId);
        assertNotNull(result, "Es wurde kein Inhouse-Eintrag fuer die ES-ID " + esId + " gefunden!");
        result.debugModel(LOGGER);
    }

    @Test(enabled = false)
    public void testFindInhouses4ES() throws FindException {
        Long esId = 3700L;
        List<Inhouse> result = physikService.findInhouses4ES(esId);
        assertNotEmpty(result, "Es wurden keine Inhouse-Eintraege fuer die ES-ID " + esId + " gefunden!");
        for (Inhouse element : result) {
            element.debugModel(LOGGER);
        }
    }

    @Test(enabled = false)
    public void testFindP2PTsByQuery() throws FindException {
        Produkt2PhysikTypQuery query = new Produkt2PhysikTypQuery();
        query.setProduktId(10L);
        query.setParentPhysikTypId(7L);

        List<Produkt2PhysikTyp> result = physikService.findP2PTsByQuery(query);
        assertNotEmpty(result, "Es wurden keine Produkt-PhysikTyp-Zuordnungen zum Query gefunden!");
        LOGGER.debug("Anzahl gefundener Produkt-PhysikTyp-Zuordnungen: " + result.size());
        for (Produkt2PhysikTyp p2pt : result) {
            p2pt.debugModel(LOGGER);
        }
    }

    @Test(enabled = false)
    public void testFindAnschlussart() throws FindException {
        Long artID = 1L;
        Anschlussart art = physikService.findAnschlussart(artID);
        assertNotNull(art, "Keine Anschlussart mit ID " + artID + " gefunden!");
        LOGGER.debug("Beschreibung/Name der Anschlussart: " + art.getAnschlussart());

        Long noID = 99999L;
        Anschlussart mustBeNull = physikService.findAnschlussart(noID);
        assertNull(mustBeNull, "Es wurde eine Anschlussart mit der ID " + noID + " gefunden - sollte aber nicht!");
    }

    @Test
    public void testFindAnschlussarten() throws FindException {
        List<Anschlussart> arten = physikService.findAnschlussarten();
        assertNotEmpty(arten, "Keine Anschlussarten gefunden!");
        LOGGER.debug("Anzahl gefundener Anschlussarten: " + arten.size());
    }

    @Test
    public void testFindPhysiktypKombinationen() throws FindException {
        List<Object[]> result = physikService.findPhysiktypKombinationen();
        assertNotEmpty(result);
    }


}


