/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.08.2004 10:19:42
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.log4j.Logger;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.CarrierbestellungDAO;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierBuilder;
import de.augustakom.hurrican.model.cc.CarrierVaModus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.model.CarrierCode;

@Test(groups = { "unit" })
public class CarrierServiceImplTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(CarrierServiceImplTest.class);

    private static final String VERTRAGSNUMMER = "123";

    @Mock
    CarrierbestellungDAO carrierbestellungDAO;

    @InjectMocks
    CarrierServiceImpl underTest = new CarrierServiceImpl();

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    public void findCarrierbestellungWithoutLeadingZeros() {
        String paddedVertragsnummer = "000" + VERTRAGSNUMMER;
        when(carrierbestellungDAO.findByVertragsnummer(VERTRAGSNUMMER)).thenReturn(
                ImmutableList.of(new Carrierbestellung()));

        Carrierbestellung carrierbestellung = Iterables.getOnlyElement(underTest
                .findCBsByNotExactVertragsnummer(paddedVertragsnummer));

        assertNotNull(carrierbestellung);
        verify(carrierbestellungDAO).findByVertragsnummer(VERTRAGSNUMMER);
        verify(carrierbestellungDAO).findByVertragsnummer(paddedVertragsnummer);
    }

    public void findCarrierbestellungWithoutMiddleZeros() {
        String paddedVertragsnummer = "123A000" + VERTRAGSNUMMER;
        String trimmedVertragsnummer = "123A" + VERTRAGSNUMMER;
        when(carrierbestellungDAO.findByVertragsnummer(trimmedVertragsnummer)).thenReturn(
                ImmutableList.of(new Carrierbestellung()));

        Carrierbestellung carrierbestellung = Iterables.getOnlyElement(underTest
                .findCBsByNotExactVertragsnummer(paddedVertragsnummer));

        assertNotNull(carrierbestellung);
        verify(carrierbestellungDAO).findByVertragsnummer(paddedVertragsnummer);
        verify(carrierbestellungDAO).findByVertragsnummer(trimmedVertragsnummer);
    }

    public void testFindCarrierByVaModus() throws FindException {
        when(carrierbestellungDAO.findCarrierByVaModus(CarrierVaModus.FAX)).thenReturn(
                ImmutableList.of(new Carrier()));

        Carrier carrier = Iterables.getOnlyElement(underTest.findCarrier(CarrierVaModus.FAX));

        assertNotNull(carrier);
        verify(carrierbestellungDAO).findCarrierByVaModus(CarrierVaModus.FAX);
    }

    @Test
    public void findWbciAwareCarrier() throws FindException {
        List<Carrier> expectedCarrierList = new ArrayList<>(2);
        expectedCarrierList.add(new CarrierBuilder().withPortierungskennung("D061").withName("Telefonica").build());
        expectedCarrierList.add(new CarrierBuilder().withPortierungskennung("D009").withName("Vodafone").build());
        when(carrierbestellungDAO.findWbciAwareCarrier()).thenReturn(expectedCarrierList);

        List<Carrier> carrierList = underTest.findWbciAwareCarrier();

        assertNotNull(carrierList);
        verify(carrierbestellungDAO).findWbciAwareCarrier();
        assertEquals(carrierList.size(), expectedCarrierList.size());
    }

    @Test
    public void findCarrierByCarrierCode() throws FindException {
        Carrier telefonica = new CarrierBuilder().withPortierungskennung("D061").withName("Telefonica").build();
        Carrier vodafone = new CarrierBuilder().withPortierungskennung("D009").withName("Vodafone").build();

        when(carrierbestellungDAO.findCarrierByCarrierCode(CarrierCode.TELEFONICA)).thenReturn(telefonica);
        when(carrierbestellungDAO.findCarrierByCarrierCode(CarrierCode.VODAFONE)).thenReturn(vodafone);

        Carrier carrier = underTest.findCarrierByCarrierCode(CarrierCode.TELEFONICA);
        assertNotNull(carrier);
        assertEquals(carrier, telefonica);

        carrier = underTest.findCarrierByCarrierCode(CarrierCode.VODAFONE);
        assertNotNull(carrier);
        assertEquals(carrier, vodafone);

        verify(carrierbestellungDAO).findCarrierByCarrierCode(CarrierCode.TELEFONICA);
        verify(carrierbestellungDAO).findCarrierByCarrierCode(CarrierCode.VODAFONE);
    }

    @Test(expectedExceptions = FindException.class)
    public void findWbciAwareCarrierThrowingException() throws FindException {
        List<Carrier> expectedCarrierList = new ArrayList<>(2);
        expectedCarrierList.add(new CarrierBuilder().withPortierungskennung("D061").withName("Telefonica").build());
        expectedCarrierList.add(new CarrierBuilder().withPortierungskennung("D009").withName("Vodafone").build());
        when(carrierbestellungDAO.findWbciAwareCarrier()).thenThrow(RuntimeException.class);

        underTest.findWbciAwareCarrier();
    }

}
