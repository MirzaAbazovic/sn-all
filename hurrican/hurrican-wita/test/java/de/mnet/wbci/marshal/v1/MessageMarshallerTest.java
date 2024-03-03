/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import javax.xml.transform.dom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.validation.XmlValidationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.StornoAenderungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class MessageMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private MessageMarshaller testling;

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void shouldNotMarshallNull() throws Exception {
        testling.marshal(null, new DOMResult());
        Assert.assertTrue(false, "no IllegalArgumentException thrown");
    }

    @Test
    public void testMarshalVorabstimmung() throws Exception {
        List<WbciRequest> inputs = new ArrayList<>();
        for (GeschaeftsfallTyp gf : GeschaeftsfallTyp.values()) {
            inputs.add(new VorabstimmungsAnfrageTestBuilder().buildValid(WbciCdmVersion.V1, gf));
        }

        for (WbciRequest input : inputs) {
            StringResult result = new StringResult();
            testling.marshal(input, result);

            assertThat(result.toString(), notNullValue());

            if (input.getWbciGeschaeftsfall() != null) {
                assertSchemaValidCarrierNegotiationService(WbciCdmVersion.V1, result.toString());
            }
            else {
                try {
                    assertSchemaValidCarrierNegotiationService(WbciCdmVersion.V1, result.toString());
                    Assert.fail("Exception not thrown");
                }
                catch (XmlValidationException e) {
                    continue; // Exception thrown as expected
                }
            }
        }
    }

    @Test
    public void testMarshallTerminverschiebung() throws Exception {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        StringResult result = new StringResult();
        testling.marshal(tv, result);

        assertThat(result.toString(), notNullValue());
        assertSchemaValidCarrierNegotiationService(WbciCdmVersion.V1, result.toString());
        Assert.assertTrue(result.toString().contains("rescheduleCarrierChange"));
    }

    @Test
    public void testMarshallStorno() throws Exception {
        StornoAnfrage stornoAnfrage = new StornoAenderungAufAnfrageTestBuilder<>().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        StringResult result = new StringResult();
        testling.marshal(stornoAnfrage, result);

        assertThat(result.toString(), notNullValue());
        assertSchemaValidCarrierNegotiationService(WbciCdmVersion.V1, result.toString());
        Assert.assertTrue(result.toString().contains("cancelCarrierChange"));
    }

    @Test
    public void testMarshalMeldung() throws Exception {
        List<Meldung<?>> inputs = new ArrayList<>();
        inputs.add(new UebernahmeRessourceMeldungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN));

        for (Meldung<?> input : inputs) {
            StringResult result = new StringResult();
            testling.marshal(input, result);

            assertThat(result.toString(), notNullValue());
            assertSchemaValidCarrierNegotiationService(WbciCdmVersion.V1, result.toString());
        }
    }
}
