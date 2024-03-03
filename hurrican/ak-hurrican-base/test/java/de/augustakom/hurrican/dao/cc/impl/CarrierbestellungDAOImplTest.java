/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.14
 */
package de.augustakom.hurrican.dao.cc.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Carrier;
import de.augustakom.hurrican.model.cc.CarrierBuilder;
import de.mnet.wbci.model.CarrierCode;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class CarrierbestellungDAOImplTest {

    @InjectMocks
    private CarrierbestellungDAOImpl testling;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @BeforeMethod
    public void setup() {
        testling = new CarrierbestellungDAOImpl();

        MockitoAnnotations.initMocks(this);

        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void testFindCarrierByCarrierCode() throws Exception {
        Carrier dtagCarrier = new CarrierBuilder().withITUCarrierCode(CarrierCode.DTAG.getITUCarrierCode())
                .withPortierungskennung("D001").withName("Deutsche Telekom").build();

        Query query = Mockito.mock(Query.class);

        when(session.createQuery(anyString())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(dtagCarrier);

        testling.findCarrierByCarrierCode(CarrierCode.DTAG);

        verify(query).uniqueResult();
        verify(query).setParameter("ituCarrierCode", CarrierCode.DTAG.getITUCarrierCode());
    }

    @Test(expectedExceptions = EmptyResultDataAccessException.class)
    public void testFindNothing() throws Exception {
        Query query = Mockito.mock(Query.class);

        when(session.createQuery(anyString())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        testling.findCarrierByCarrierCode(CarrierCode.TELEFONICA);

        verify(query).uniqueResult();
        verify(query).setParameter("ituCarrierCode", CarrierCode.TELEFONICA.getITUCarrierCode());
    }
}
