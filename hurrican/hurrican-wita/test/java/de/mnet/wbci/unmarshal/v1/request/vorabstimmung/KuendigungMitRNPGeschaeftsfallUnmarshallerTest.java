/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.unmarshal.v1.request.vorabstimmung;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.wbci.model.Person;
import de.mnet.wbci.model.Projekt;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.KuendigungMitRNPGeschaeftsfallTypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.RufnummernPortierungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.request.AnfrageUnmarshallerTest;

@Test(groups = BaseTest.UNIT)
public class KuendigungMitRNPGeschaeftsfallUnmarshallerTest
        extends AnfrageUnmarshallerTest<KuendigungMitRNPGeschaeftsfallType, WbciGeschaeftsfallKueMrn, KuendigungMitRNPGeschaeftsfallUnmarshaller> {

    @Mock
    protected RufnummernPortierungUnmarshaller rufnummernPortierungUnmarshaller;

    @BeforeClass
    @Override
    public void init() {
        super.init();

        Mockito.when(rufnummernPortierungUnmarshaller.apply(input.getRufnummernPortierung()))
                .thenReturn(new RufnummernportierungAnlage());
        Mockito.when(personOderFirmaUnmarshaller.apply(input.getEndkunde())).thenReturn(new Person());
        Mockito.when(projektUnmarshaller.apply(input.getProjektId())).thenReturn(new Projekt());
    }

    @Test
    public void testApplyKuendigungMitRNP() {
        WbciGeschaeftsfallKueMrn result = testling.apply(input);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getRufnummernportierung());

        Assert.assertEquals(result.getVorabstimmungsId(), input.getVorabstimmungsId());
        Assert.assertNotNull(result.getKundenwunschtermin());
        Assert.assertNotNull(result.getEndkunde());
        Assert.assertNotNull(result.getProjekt());
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Override
    protected KuendigungMitRNPGeschaeftsfallType getInput() {
        return new KuendigungMitRNPGeschaeftsfallTypeTestBuilder().buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
    }

    @Override
    protected WbciGeschaeftsfallKueMrn getWbciGeschaeftsfall() {
        return new WbciGeschaeftsfallKueMrn();
    }

    @Override
    protected KuendigungMitRNPGeschaeftsfallUnmarshaller getTestling() {
        return new KuendigungMitRNPGeschaeftsfallUnmarshaller();
    }
}
