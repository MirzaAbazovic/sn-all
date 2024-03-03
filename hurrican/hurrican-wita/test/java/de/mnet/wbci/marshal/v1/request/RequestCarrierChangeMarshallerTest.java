/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.request;

import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungMitRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.KuendigungOhneRNPGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.ReineRufnummernportierungGeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RequestCarrierChange;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.marshal.v1.request.vorabstimmung.KuendigungMitRNPGeschaeftsfallMarshaller;
import de.mnet.wbci.marshal.v1.request.vorabstimmung.KuendigungOhneRNPGeschaeftsfallMarshaller;
import de.mnet.wbci.marshal.v1.request.vorabstimmung.ReineRufnummernportierungGeschaeftsfallMarshaller;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class RequestCarrierChangeMarshallerTest extends AbstractWbciMarshallerTest {

    @Mock
    private KuendigungMitRNPGeschaeftsfallMarshaller kuendigungMitRNPGeschaeftsfallMarshallerMock;
    @Mock
    private KuendigungOhneRNPGeschaeftsfallMarshaller kuendigungOhneRNPGeschaeftsfallMarshallerMock;
    @Mock
    private ReineRufnummernportierungGeschaeftsfallMarshaller reineRufnummernportierungGeschaeftsfallMarshallerMock;

    @InjectMocks
    private RequestCarrierChangeMarshaller testling = new RequestCarrierChangeMarshaller();

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public <GF extends WbciGeschaeftsfall> void testApply() throws Exception {
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(
                WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        WbciGeschaeftsfallKueOrn wbciGeschaeftsfallKueOrn = new WbciGeschaeftsfallKueOrnTestBuilder().buildValid(
                WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);
        WbciGeschaeftsfallRrnp wbciGeschaeftsfallRrnp = new WbciGeschaeftsfallRrnpTestBuilder().buildValid(
                WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP);

        KuendigungMitRNPGeschaeftsfallType aspectedVAKUEMRN = new KuendigungMitRNPGeschaeftsfallType();
        KuendigungOhneRNPGeschaeftsfallType aspectedAKUEORN = new KuendigungOhneRNPGeschaeftsfallType();
        ReineRufnummernportierungGeschaeftsfallType aspectedVARRNP = new ReineRufnummernportierungGeschaeftsfallType();
        when(kuendigungMitRNPGeschaeftsfallMarshallerMock.apply(wbciGeschaeftsfallKueMrn)).thenReturn(aspectedVAKUEMRN);
        when(kuendigungOhneRNPGeschaeftsfallMarshallerMock.apply(wbciGeschaeftsfallKueOrn)).thenReturn(aspectedAKUEORN);
        when(reineRufnummernportierungGeschaeftsfallMarshallerMock.apply(wbciGeschaeftsfallRrnp)).thenReturn(
                aspectedVARRNP);

        List<GF> geschaeftsfallListe = new ArrayList<>();
        geschaeftsfallListe.addAll((Collection<? extends GF>) Arrays.asList(wbciGeschaeftsfallKueMrn,
                wbciGeschaeftsfallKueOrn, wbciGeschaeftsfallRrnp));
        for (GF gfTyp : geschaeftsfallListe) {

            VorabstimmungsAnfrage<GF> vorabstimmungsAnfrage = new VorabstimmungsAnfrageBuilder<GF>().withWbciGeschaeftsfall(gfTyp).build();

            RequestCarrierChange jaxbElement = testling.apply(vorabstimmungsAnfrage);
            switch (gfTyp.getTyp()) {
                case VA_KUE_MRN:
                    Assert.assertNull(jaxbElement.getVAKUEORN());
                    Assert.assertNull(jaxbElement.getVARRNP());
                    Assert.assertEquals(jaxbElement.getVAKUEMRN(), aspectedVAKUEMRN);
                    break;
                case VA_KUE_ORN:
                    Assert.assertNull(jaxbElement.getVAKUEMRN());
                    Assert.assertNull(jaxbElement.getVARRNP());
                    Assert.assertEquals(jaxbElement.getVAKUEORN(), aspectedAKUEORN);
                    break;
                case VA_RRNP:
                    Assert.assertNull(jaxbElement.getVAKUEORN());
                    Assert.assertNull(jaxbElement.getVAKUEMRN());
                    Assert.assertEquals(jaxbElement.getVARRNP(), aspectedVARRNP);
                    break;
            }

        }
    }

}
