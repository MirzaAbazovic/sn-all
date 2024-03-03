/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2004 09:02:29
 */
package de.augustakom.hurrican.service.billing.impl;

import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.billing.OeDAO;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.service.billing.OEService;

/**
 * UnitTest, um die Service-Implementierung fuer Objekte des Typs <code>OE</code> zu testen.
 *
 */
@Test(groups = { BaseTest.UNIT })
public class OEServiceTest  extends BaseTest  {

    private static final Logger LOGGER = Logger.getLogger(OEServiceTest.class);

    @Spy
    private OEServiceImpl sut;

    @Mock
    private OeDAO oEMock;

    @BeforeMethod
    void setUp() {
        sut = new OEServiceImpl();
        initMocks(this);
        sut.setDAO(oEMock);
    }

    /**
     * Test method for OEService#findOE(Long)
     */
    public void testFindOE() {
        try {

            OE expectedOe = getOeBuilder(2000L, 2000L,"Maxi");
            Mockito.when(oEMock.findByOeNoOrig(2000L)).thenReturn(expectedOe);
            OE result = sut.findOE(2000L);
            assertEquals(expectedOe.getName(), result.getName());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test fuer die Methode OEService#findProduktNamen4Auftraege(List<Long>)
     */
    public void testFindProduktNamen4Auftraege() {
        try {
            List<Long> aNoOrigs = new ArrayList<>();
            aNoOrigs.add(133069L);
            aNoOrigs.add(1936L);
            aNoOrigs.add(36218L);
            aNoOrigs.add(108846L);

            Map<Long,String> expectedResult = Maps.newHashMap();
            expectedResult.put(133069L,"Maxi");
            expectedResult.put(1936L,"Surf+Fon Sondertarif");
            expectedResult.put(36218L,"Surf-Flat");
            expectedResult.put(108846L,"Surf+Fon-Flat");

            Mockito.when(oEMock.findProduktNamen4Auftraege(aNoOrigs)).thenReturn(expectedResult);
            Map result = sut.findProduktNamen4Auftraege(aNoOrigs);
            assertEquals(result, expectedResult);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for OEService#findProduktName4Auftrag(Long)
     */
    public void testfindProduktName4Auftrag() {
        try {
            String expectedProdName = "Surf+Fon";
            Mockito.when(oEMock.findProduktName4Auftrag(915991L)).thenReturn(expectedProdName);
            String result = sut.findProduktName4Auftrag(915991L);
            assertEquals(result, expectedProdName);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test method for OEService#findProduktName4Auftrag(Long)
     */
    public void testfindVaterOeNoOrig4Auftrag() {
        try {
            Long expectedVaterOE = 3400L;
            Mockito.when(oEMock.findVaterOeNoOrig4Auftrag(23L)).thenReturn(expectedVaterOE);
            Long result = sut.findVaterOeNoOrig4Auftrag(23L);
            assertEquals(result, expectedVaterOE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Test for method OEService#findOEByOeTyp()
     */
    public void testfindOEByOeTyp() {
        try {
            List<OE> expectedOeList = new ArrayList<>();
            expectedOeList.add(getOeBuilder(3401L,3401L,"Surf+Fon-Flat"));
            expectedOeList.add(getOeBuilder(3402L,3402L,"Surf-Flat"));
            expectedOeList.add(getOeBuilder(3403L,3403L,"Telefon-Flat"));

            Mockito.when(oEMock.findAllByOetyp(OE.OE_OETYP_PRODUKT)).thenReturn(expectedOeList);
            List<OE> result = sut.findOEByOeTyp(OE.OE_OETYP_PRODUKT, OEService.FIND_STRATEGY_ALL);
            assertEquals(result,expectedOeList);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    private OE getOeBuilder(Long oeNo, Long oeNoOrig, String name) {
        OE oe = new OE();
        oe.setOeNo(oeNo);
        oe.setOeNoOrig(oeNoOrig);
        oe.setName(name);
        return oe;
    }
}
