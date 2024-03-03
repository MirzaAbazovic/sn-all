/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.07.13
 */
package de.mnet.wbci.marshal.v1.entities;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungMitPortierungskennungType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class RufnummernportierungMitPortierungskennungMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private RufnummernportierungMitPortierungskennungMarshaller testling;

    @Test
    public void testApplyWithValidInput() {
        RufnummernportierungAnlage inputAnlage = new RufnummernportierungAnlageTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP);

        assertNotNull(inputAnlage.getPortierungskennungPKIauf());

        RufnummernportierungMitPortierungskennungType output = testling.apply(inputAnlage);

        assertNotNull(output);
        assertNotNull(output.getAnlagenanschluss());
        assertEquals(output.getPortierungskennungPKIauf(), inputAnlage.getPortierungskennungPKIauf());
    }

    @Test
    public void testApplyWithNullInput() {
        RufnummernportierungMitPortierungskennungType output = testling.apply(null);
        assertNull(output);
    }
}
