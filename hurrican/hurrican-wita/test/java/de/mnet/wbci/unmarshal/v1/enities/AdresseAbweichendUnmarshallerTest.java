/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.unmarshal.v1.enities;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AdresseAbweichendType;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.AdresseAbweichendTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.AbstractWbciUnmarshallerContext;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AdresseAbweichendUnmarshallerTest extends AbstractWbciUnmarshallerContext {

    @Autowired
    private AdresseAbweichendUnmarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplyWithHausnummer() throws Exception {
        AdresseAbweichendType testdata = new AdresseAbweichendTypeTestBuilder()
                .withHausnummer("1")
                .build();
        Standort result = testling.apply(testdata);

        Assert.assertNotNull(result.getStrasse());
        Assert.assertEquals(result.getStrasse().getHausnummer(), testdata.getHausnummer());
        Assert.assertNull(result.getStrasse().getStrassenname());
        Assert.assertNull(result.getOrt());
        Assert.assertNull(result.getPostleitzahl());
    }

    @Test
    public void testApplyWithStrassenname() throws Exception {
        AdresseAbweichendType testdata = new AdresseAbweichendTypeTestBuilder()
                .withStrassenname("Elm Street")
                .build();
        Standort result = testling.apply(testdata);

        Assert.assertNotNull(result.getStrasse());
        Assert.assertEquals(result.getStrasse().getStrassenname(), testdata.getStrassenname());
        Assert.assertNull(result.getStrasse().getHausnummer());
        Assert.assertNull(result.getOrt());
        Assert.assertNull(result.getPostleitzahl());
    }

    @Test
    public void testApplyWithOrt() throws Exception {
        AdresseAbweichendType testdata = new AdresseAbweichendTypeTestBuilder()
                .withOrt("dublin")
                .build();

        Standort result = testling.apply(testdata);

        Assert.assertNull(result.getStrasse());
        Assert.assertNull(result.getPostleitzahl());
        Assert.assertEquals(result.getOrt(), testdata.getOrt());
    }

    @Test
    public void testApplyWithPlz() throws Exception {
        AdresseAbweichendType testdata = new AdresseAbweichendTypeTestBuilder()
                .withPostleitzahl("12345")
                .build();

        Standort result = testling.apply(testdata);

        Assert.assertNull(result.getStrasse());
        Assert.assertNull(result.getOrt());
        Assert.assertEquals(result.getPostleitzahl(), testdata.getPostleitzahl());
    }
}
