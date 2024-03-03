/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2004 10:19:42
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.AuftragTechnik2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierBuilder;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.CarrierVaModus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Carrierbestellung2EndstelleBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.CarrierbestellungVormieter;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.GeoIdBuilder;
import de.augustakom.hurrican.model.cc.view.AQSView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * TestNG-Test fuer CarrierService.
 */
@Test(groups = { BaseTest.SERVICE })
public class CarrierServiceTest extends AbstractHurricanBaseServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CarrierServiceTest.class);

    public void testFindAqsLL4GeoId() throws FindException {
        Carrierbestellung2EndstelleBuilder cb2EsBuilder = getBuilder(Carrierbestellung2EndstelleBuilder.class);
        GeoIdBuilder geoIdBuilder = getBuilder(GeoIdBuilder.class);

        AuftragTechnikBuilder auftragTechnikBuilder = getBuilder(AuftragTechnikBuilder.class)
                .withAuftragTechnik2EndstelleBuilder(getBuilder(AuftragTechnik2EndstelleBuilder.class));

        getBuilder(EndstelleBuilder.class)
                .withCb2EsBuilder(cb2EsBuilder)
                .withGeoIdBuilder(geoIdBuilder)
                .withAuftragTechnikBuilder(auftragTechnikBuilder)
                .build();

        Carrierbestellung cb = getBuilder(CarrierbestellungBuilder.class)
                .withAqs("aqs")
                .withLl("ll")
                .withCb2EsBuilder(cb2EsBuilder)
                .build();

        List<AQSView> result = getCarrierService().findAqsLL4GeoId(geoIdBuilder.get().getId());
        assertNotEmpty(result, "keine AQS-Views gefunden!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getAqs(), cb.getAqs());
        assertEquals(result.get(0).getLl(), cb.getLl());
    }

    public void testSaveCBWithoutAddress() throws StoreException, ValidationException, FindException {
        Endstelle endstelle = getBuilder(EndstelleBuilder.class).build();

        Carrierbestellung carrierBestellung = new Carrierbestellung();
        carrierBestellung.setCarrier(Carrier.ID_DTAG);

        getCarrierService().saveCBWithoutAddress(carrierBestellung, endstelle);

        endstelle = getCCService(EndstellenService.class).findEndstelle(endstelle.getId());

        assertNotNull(carrierBestellung.getId());
        assertEquals(carrierBestellung.getCb2EsId(), endstelle.getCb2EsId());
    }

    public void testSaveAndFindCarrierbestellungWithVormieter() throws Exception {
        Endstelle endstelle = getBuilder(EndstelleBuilder.class).build();

        CarrierService carrierService = getCarrierService();

        CarrierbestellungVormieter cbVormieter = new CarrierbestellungVormieter();
        cbVormieter.setVorname("Max");
        cbVormieter.setNachname("Mustermann");
        cbVormieter.setOnkz("3456");
        cbVormieter.setRufnummer("1234567890");
        cbVormieter.setUfaNummer("123C123456");

        Carrierbestellung carrierBestellung = new Carrierbestellung();
        carrierBestellung.setCarrier(Carrier.ID_DTAG);
        carrierBestellung.setCarrierbestellungVormieter(cbVormieter);

        carrierService.saveCBWithoutAddress(carrierBestellung, endstelle);
        flushAndClear();

        Carrierbestellung result = carrierService.findCB(carrierBestellung.getId());
        assertNotNull(result);
        assertNotNull(result.getCarrierbestellungVormieter());
        assertEquals(result.getCarrierbestellungVormieter().getVorname(), cbVormieter.getVorname());
        assertEquals(result.getCarrierbestellungVormieter().getNachname(), cbVormieter.getNachname());
        assertEquals(result.getCarrierbestellungVormieter().getOnkz(), cbVormieter.getOnkz());
        assertEquals(result.getCarrierbestellungVormieter().getRufnummer(), cbVormieter.getRufnummer());
        assertEquals(result.getCarrierbestellungVormieter().getUfaNummer(), cbVormieter.getUfaNummer());
    }

    /**
     * Test fuer die Methode CarrierService#findCBs4EndstelleTx(Long)
     */
    public void testFindCBs4EndstelleTx() throws FindException {
        CarrierbestellungBuilder carrierbestellungBuilder = getBuilder(CarrierbestellungBuilder.class);
        carrierbestellungBuilder.build();
        Endstelle endstelle = getBuilder(EndstelleBuilder.class)
                .withCb2EsBuilder(carrierbestellungBuilder.getCb2EsBuilder()).build();
        flushAndClear();

        List<Carrierbestellung> result = getCarrierService().findCBs4EndstelleTx(endstelle.getId());
        assertNotEmpty(result, "Es wurden keine Carrierbestellungen zur Endstelle gefunden. ES: " +
                endstelle.getCb2EsId());
        assertEquals(result.size(), 1);
        Carrierbestellung actual = result.iterator().next();
        Carrierbestellung expected = carrierbestellungBuilder.get();
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getLbz(), expected.getLbz());
        assertEquals(actual.getVtrNr(), expected.getVtrNr());
        assertEquals(actual.getAqs(), expected.getAqs());
    }

    /**
     * Test fuer die Methode CarrierService#cbKuendigen(Long)
     */
    public void testCbKuendigen() throws StoreException {
        Carrierbestellung carrierbestellung = getBuilder(CarrierbestellungBuilder.class)
                .withLbz("99Z/89000/89000/999999").build();
        getCarrierService().cbKuendigen(carrierbestellung.getId());
    }

    /**
     * Test fuer die Methode CarrierService#validateCB(...)
     */
    public void testValidateCB() throws ValidationException {
        getCarrierService().validateLbz(Carrier.ID_DTAG, "96U/12345/12345/123");
    }

    /**
     * Test fuer die Methode CarrierService#createLbz(Integer, Integer)
     */
    public void testCreateLbz() {
        String lbz = getCarrierService().createLbz(Carrier.ID_DTAG, 34478L);

        assertNotNull("Es wurde keine LBZ erstellt!", lbz);
        assertTrue(lbz.matches("/[1-9]\\d{4}/[1-9]\\d{4}/"),
                "LBZ stimmt nicht mit dem erwarteten Pattern ueberein!");
        LOGGER.debug("Erzeugte LBZ: " + lbz);
    }

    /**
     * Test fuer die Methode CarrierService#findCBs4Carrier(Integer, int, int)
     */
    public void testFindCBs4Carrier() throws FindException {
        getBuilder(CarrierbestellungBuilder.class)
                .build();
        getBuilder(CarrierbestellungBuilder.class)
                .build();
        getBuilder(CarrierbestellungBuilder.class)
                .build();
        flushAndClear();

        List<Carrierbestellung> result = getCarrierService().findCBs4Carrier(Carrier.ID_DTAG, 2, 0);
        assertNotEmpty(result, "Es wurden keine Carrierbestellungen ermittelt!");
        assertEquals(result.size(), 2, "Anzahl der ermittelten Carrierbestellungen stimmt nicht!");
    }

    /**
     * Test fuer die Methode CarrierService#findCarrierKennung(Long)
     */
    public void testFindCarrierKennung() throws FindException {
        CarrierKennung result = getCarrierService().findCarrierKennung(new Long(1));
        assertNotNull(result, "CarrierKennung nicht gefunden!");
        LOGGER.debug("  CarrierKennung.name    : " + result.getName());
        LOGGER.debug("  CarrierKennung.kundenNr: " + result.getKundenNr());
    }

    /**
     * Test fuer die Methode {@link CarrierService#findCarrierForAnbieterwechsel()}
     */
    public void testFindCarrierForAnbieterwechsel() throws FindException {
        List<Carrier> result = getCarrierService().findCarrierForAnbieterwechsel();
        assertNotEmpty(result, "Carrier Liste nicht gefunden!");
        for (Carrier carrier : result) {
            assertNotNull(carrier.getPortierungskennung());
        }
    }

    @Test(expectedExceptions = DeleteException.class)
    public void deleteCbWithExistingData() throws DeleteException {
        Carrierbestellung cb = getBuilder(CarrierbestellungBuilder.class).withAqs("12").withLl("12").build();
        getCarrierService().deleteCB(cb);
    }

    @Test(expectedExceptions = DeleteException.class)
    public void deleteCbWithExistingVorgang() throws DeleteException {
        CarrierbestellungBuilder cbBuilder = getBuilder(CarrierbestellungBuilder.class);
        new CBVorgangBuilder().withRandomId().withCbBuilder(cbBuilder).withVorgabeMnet(new Date()).build();
        getCarrierService().deleteCB(cbBuilder.get());
    }

    /**
     * Test fuer die Methode CarrierService#findCarrier(CarrierVaModus)
     */
    public void testFindCarrierByVaModus() throws FindException {
        getBuilder(CarrierBuilder.class).build();

        List<Carrier> result = getCarrierService().findCarrier(CarrierVaModus.WBCI);
        assertNotEmpty(result);
    }

    private CarrierService getCarrierService() {
        return getCCService(CarrierService.class);
    }

    public void testFindCBs4LBZ() throws FindException {
        final String LBZ = "96W/12345/12345/123";
        getBuilder(CarrierbestellungBuilder.class).withLbz(LBZ).build();
        List<Carrierbestellung> result = getCarrierService().findCBs4LBZ(LBZ);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertTrue(StringUtils.equals(result.get(0).getLbz(), LBZ));
    }

}


