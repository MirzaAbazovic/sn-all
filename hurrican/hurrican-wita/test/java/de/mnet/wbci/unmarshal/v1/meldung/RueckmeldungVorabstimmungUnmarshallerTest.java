/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.unmarshal.v1.meldung;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.time.format.*;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.GeschaeftsfallEnumType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.MeldungsPositionRUEMVAType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMeldungType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.TechnischeRessourceType;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Rufnummernportierung;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.builder.cdm.carriernegotiation.v1.MeldungRUEMVATypeTestBuilder;
import de.mnet.wbci.unmarshal.v1.enities.MeldungsPositionRUEMVAUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.RufnummerportierungMeldungUnmarshaller;
import de.mnet.wbci.unmarshal.v1.enities.TechnischeRessourceUnmarshaller;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class RueckmeldungVorabstimmungUnmarshallerTest extends
        AbstractMeldungUnmarshallerTest<MeldungRUEMVAType, RueckmeldungVorabstimmung, RueckmeldungVorabstimmungUnmarshaller> {

    @Mock
    private MeldungsPositionRUEMVAUnmarshaller meldungsPositionRUEMVAUnmarshaller;
    @Mock
    private RufnummerportierungMeldungUnmarshaller rufnummerportierungUnmarshaller;
    @Mock
    private TechnischeRessourceUnmarshaller technischeRessourceUnmarshaller;

    private MeldungPositionRueckmeldungVa meldungPositionRueckmeldungVa;
    private Rufnummernportierung rufnummernportierung;
    private TechnischeRessource technischeRessource;

    @BeforeClass
    @Override
    public void init() {
        super.init();

        meldungPositionRueckmeldungVa = new MeldungPositionRueckmeldungVa();
        when(meldungsPositionRUEMVAUnmarshaller.apply(any(MeldungsPositionRUEMVAType.class))).thenReturn(
                meldungPositionRueckmeldungVa);

        rufnummernportierung = new RufnummernportierungAnlage();
        when(rufnummerportierungUnmarshaller.apply(any(RufnummernportierungMeldungType.class))).thenReturn(
                rufnummernportierung);

        technischeRessource = new TechnischeRessource();
        when(technischeRessourceUnmarshaller.apply(any(TechnischeRessourceType.class))).thenReturn(
                technischeRessource);
    }

    @Test
    public void testApplyRueckmeldung() throws Exception {
        RueckmeldungVorabstimmung result = testling.apply(input);
        Assert.assertNotNull(result.getMeldungsPositionen());
        Assert.assertTrue(result.getMeldungsPositionen().contains(meldungPositionRueckmeldungVa));

        Assert.assertNotNull(result.getTechnischeRessourcen());
        Assert.assertTrue(result.getTechnischeRessourcen().contains(technischeRessource));

        Assert.assertEquals(result.getRufnummernportierung(), rufnummernportierung);

        Assert.assertEquals(result.getWechseltermin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), input.getWechseltermin().toXMLFormat());
        Assert.assertEquals(result.getTechnologie().getWbciTechnologieCode(), input.getTechnologie());
    }

    @Test
    public void testApplyNull() throws Exception {
        Assert.assertNull(testling.apply(null));
    }

    @Override
    protected RueckmeldungVorabstimmungUnmarshaller getTestling() {
        return new RueckmeldungVorabstimmungUnmarshaller();
    }

    @Override
    protected MeldungRUEMVAType getInput() {
        return new MeldungRUEMVATypeTestBuilder()
                .buildValid(GeschaeftsfallEnumType.VA_KUE_MRN);
    }

}
