/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.marshal.v1.meldung;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.RufnummernportierungEinzeln;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;

/**
 *
 */
public class RueckmeldungVorabstimmungMarshallerTest extends AbstractMeldungMarshallerTest {

    @Autowired
    private RueckmeldungVorabstimmungMarshaller testling;

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Test
    public void testApplyRUEMVAEinzeln() throws Exception {
        RueckmeldungVorabstimmung input = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        MeldungRUEMVAType meldungRUEMVAType = testling.apply(input);

        assertEquals(meldungRUEMVAType, input);

        Assert.assertEquals(Technologie.lookUpWbciTechnologieCode(meldungRUEMVAType.getTechnologie()), input.getTechnologie());
        Assert.assertEquals(meldungRUEMVAType.getWechseltermin(), DateConverterUtils.toXmlGregorianCalendar(input.getWechseltermin()));
        Assert.assertEquals(meldungRUEMVAType.getRessource().size(), input.getTechnischeRessourcen().size());
        Assert.assertNull(meldungRUEMVAType.getRufnummernPortierung().getPortierungRufnummernbloecke());
        Assert.assertNotNull(meldungRUEMVAType.getRufnummernPortierung().getPortierungRufnummern());
        Assert.assertEquals(meldungRUEMVAType.getRufnummernPortierung().getPortierungRufnummern().getZuPortierendeOnkzRnr().size(), ((RufnummernportierungEinzeln) input.getRufnummernportierung()).getRufnummernOnkz().size());
        Assert.assertEquals(meldungRUEMVAType.getPosition().size(), input.getMeldungsPositionen().size());
    }

    @Test
    public void testApplyRUEMVAAnlage() throws Exception {
        RueckmeldungVorabstimmungTestBuilder inputBuilder = new RueckmeldungVorabstimmungTestBuilder();
        inputBuilder.withRufnummernportierung(new RufnummernportierungAnlageTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN));

        RueckmeldungVorabstimmung input = inputBuilder.buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        MeldungRUEMVAType meldungRUEMVAType = testling.apply(input);

        assertEquals(meldungRUEMVAType, input);

        Assert.assertEquals(Technologie.lookUpWbciTechnologieCode(meldungRUEMVAType.getTechnologie()), input.getTechnologie());
        Assert.assertEquals(meldungRUEMVAType.getWechseltermin(), DateConverterUtils.toXmlGregorianCalendar(input.getWechseltermin()));
        Assert.assertEquals(meldungRUEMVAType.getRessource().size(), input.getTechnischeRessourcen().size());
        Assert.assertNull(meldungRUEMVAType.getRufnummernPortierung().getPortierungRufnummern());
        Assert.assertNotNull(meldungRUEMVAType.getRufnummernPortierung().getPortierungRufnummernbloecke());
        Assert.assertEquals(meldungRUEMVAType.getRufnummernPortierung().getPortierungRufnummernbloecke().getZuPortierenderRufnummernblock().size(), ((RufnummernportierungAnlage) input.getRufnummernportierung()).getRufnummernbloecke().size());
        Assert.assertEquals(meldungRUEMVAType.getPosition().size(), input.getMeldungsPositionen().size());
    }
}
