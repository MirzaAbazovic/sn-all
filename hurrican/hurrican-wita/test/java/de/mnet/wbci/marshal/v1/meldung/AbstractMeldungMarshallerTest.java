/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import org.testng.Assert;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AbstractMeldungType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.Meldung;

public abstract class AbstractMeldungMarshallerTest extends AbstractWbciMarshallerTest {

    public void assertEquals(AbstractMeldungType abstractMeldungType, Meldung meldung) throws Exception {
        Assert.assertEquals(abstractMeldungType.getGeschaeftsfall().name(), meldung.getWbciGeschaeftsfall().getTyp().name());
        Assert.assertEquals(abstractMeldungType.getAbsender().getCarrierCode(), meldung.getAbsender().getITUCarrierCode());
        Assert.assertEquals(abstractMeldungType.getEmpfaenger().getCarrierCode(),
                meldung.getWbciGeschaeftsfall().getAbgebenderEKP().getITUCarrierCode());
        Assert.assertEquals(abstractMeldungType.getEndkundenvertragspartner().getEKPabg().getCarrierCode(),
                meldung.getWbciGeschaeftsfall().getAbgebenderEKP().getITUCarrierCode());
        Assert.assertEquals(abstractMeldungType.getEndkundenvertragspartner().getEKPauf().getCarrierCode(),
                meldung.getWbciGeschaeftsfall().getAufnehmenderEKP().getITUCarrierCode());
        Assert.assertEquals(abstractMeldungType.getVorabstimmungsIdRef(), meldung.getWbciGeschaeftsfall().getVorabstimmungsId());
    }

}
