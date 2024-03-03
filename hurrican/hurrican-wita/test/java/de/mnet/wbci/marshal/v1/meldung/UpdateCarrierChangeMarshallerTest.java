/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungABBMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungAKMTRType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungERLMType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UpdateCarrierChange;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class UpdateCarrierChangeMarshallerTest extends AbstractWbciMarshallerTest {

    @Mock
    private UebernahmeRessourceMeldungMarshaller uebernahmeRessourceMeldungMarshaller;
    @Mock
    private AbbruchmeldungVaMarshaller abbruchmeldungMarshaller;
    @Mock
    private AbbruchmeldungStornoAufMarshaller abbruchmeldungStornoAufMarshaller;
    @Mock
    private AbbruchmeldungStornoAenMarshaller abbruchmeldungStornoAenMarshaller;
    @Mock
    private AbbruchmeldungTvMarshaller abbruchmeldungTvMarshaller;
    @Mock
    private RueckmeldungVorabstimmungMarshaller rueckmeldungVorabstimmungMarshaller;
    @Mock
    private AbbruchmeldungTechnRessourceMarshaller abbruchmeldungTechnRessourceMarshaller;
    @Mock
    private ErledigtmeldungVaMarshaller erledigtmeldungMarshaller;
    @Mock
    private ErledigtmeldungStornoAufMarshaller erledigtmeldungStornoAufMarshaller;
    @Mock
    private ErledigtmeldungStornoAenMarshaller erledigtmeldungStornoAenMarshaller;
    @Mock
    private ErledigtmeldungTvMarshaller erledigtmeldungTvMarshaller;

    @InjectMocks
    private UpdateCarrierChangeMarshaller testling;

    @BeforeClass
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(uebernahmeRessourceMeldungMarshaller.apply(any(UebernahmeRessourceMeldung.class))).thenReturn(
                new MeldungAKMTRType());
        when(abbruchmeldungMarshaller.apply(any(Abbruchmeldung.class))).thenReturn(new MeldungABBMType());
        when(abbruchmeldungStornoAufMarshaller.apply(any(AbbruchmeldungStornoAuf.class))).thenReturn(new MeldungABBMType());
        when(abbruchmeldungStornoAenMarshaller.apply(any(AbbruchmeldungStornoAen.class))).thenReturn(new MeldungABBMType());
        when(abbruchmeldungTvMarshaller.apply(any(AbbruchmeldungTerminverschiebung.class))).thenReturn(new MeldungABBMType());
        when(rueckmeldungVorabstimmungMarshaller.apply(any(RueckmeldungVorabstimmung.class))).thenReturn(new MeldungRUEMVAType());
        when(abbruchmeldungTechnRessourceMarshaller.apply(any(AbbruchmeldungTechnRessource.class))).thenReturn(
                new MeldungABBMTRType());
        when(erledigtmeldungMarshaller.apply(any(Erledigtmeldung.class))).thenReturn(new MeldungERLMType());
        when(erledigtmeldungStornoAufMarshaller.apply(any(ErledigtmeldungStornoAuf.class))).thenReturn(new MeldungERLMType());
        when(erledigtmeldungStornoAenMarshaller.apply(any(ErledigtmeldungStornoAen.class))).thenReturn(new MeldungERLMType());
        when(erledigtmeldungTvMarshaller.apply(any(ErledigtmeldungTerminverschiebung.class))).thenReturn(new MeldungERLMType());
    }

    @Test
    public void testApplyAkmTR() {
        UebernahmeRessourceMeldung meldung = new UebernahmeRessourceMeldung();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, true, true, false, true, true);
        verify(uebernahmeRessourceMeldungMarshaller).apply(meldung);
    }

    @Test
    public void testApplyABBM() {
        Abbruchmeldung meldung = new Abbruchmeldung();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, false, true, true, true, true);
        verify(abbruchmeldungMarshaller).apply(meldung);
    }

    @Test
    public void testApplyABBMStornoAuf() {
        AbbruchmeldungStornoAuf meldung = new AbbruchmeldungStornoAuf();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, false, true, true, true, true);
        verify(abbruchmeldungStornoAufMarshaller).apply(meldung);
    }

    @Test
    public void testApplyABBMStornoAen() {
        AbbruchmeldungStornoAen meldung = new AbbruchmeldungStornoAen();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, false, true, true, true, true);
        verify(abbruchmeldungStornoAenMarshaller).apply(meldung);
    }

    @Test
    public void testApplyABBMTerminverschiebung() {
        AbbruchmeldungTerminverschiebung meldung = new AbbruchmeldungTerminverschiebung();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, false, true, true, true, true);
        verify(abbruchmeldungTvMarshaller).apply(meldung);
    }

    @Test
    public void testApplyRuemVa() {
        RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmung();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, true, true, true, true, false);
        verify(rueckmeldungVorabstimmungMarshaller).apply(meldung);
    }

    @Test
    public void testApplyAbbruchmeldungTechnRessource() {
        AbbruchmeldungTechnRessource meldung = new AbbruchmeldungTechnRessource();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, true, false, true, true, true);
        verify(abbruchmeldungTechnRessourceMarshaller).apply(any(AbbruchmeldungTechnRessource.class));
    }

    @Test
    public void testApplyERLM() {
        Erledigtmeldung meldung = new Erledigtmeldung();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, true, true, true, false, true);
        verify(erledigtmeldungMarshaller).apply(any(Erledigtmeldung.class));
    }

    @Test
    public void testApplyERLMStornoAuf() {
        ErledigtmeldungStornoAuf meldung = new ErledigtmeldungStornoAuf();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, true, true, true, false, true);
        verify(erledigtmeldungStornoAufMarshaller).apply(any(ErledigtmeldungStornoAuf.class));
    }

    @Test
    public void testApplyERLMStornoAen() {
        ErledigtmeldungStornoAen meldung = new ErledigtmeldungStornoAen();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, true, true, true, false, true);
        verify(erledigtmeldungStornoAenMarshaller).apply(any(ErledigtmeldungStornoAen.class));
    }

    @Test
    public void testApplyERLMTerminverschiebung() {
        ErledigtmeldungTerminverschiebung meldung = new ErledigtmeldungTerminverschiebung();

        UpdateCarrierChange updateCarrierChange = testling.apply(meldung);
        assertNull(updateCarrierChange, true, true, true, false, true);
        verify(erledigtmeldungTvMarshaller).apply(any(ErledigtmeldungTerminverschiebung.class));
    }

    private void assertNull(UpdateCarrierChange updateCarrierChange, boolean abbm, boolean abbmtr, boolean akmtr, boolean erlm, boolean ruemva) {
        Assert.assertEquals(updateCarrierChange.getABBM() == null, abbm);
        Assert.assertEquals(updateCarrierChange.getABBMTR() == null, abbmtr);
        Assert.assertEquals(updateCarrierChange.getAKMTR() == null, akmtr);
        Assert.assertEquals(updateCarrierChange.getERLM() == null, erlm);
        Assert.assertEquals(updateCarrierChange.getRUEMVA() == null, ruemva);
    }
}
