/*
 * Copyright (c) 2003 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.03
 */
package de.mnet.wbci.marshal.v1.entities;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernportierungType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class RufnummernportierungMarshallerTest extends AbstractWbciMarshallerTest {
    @Autowired
    private RufnummernportierungMarshaller testling;

    @Test
    public void testApplyWithValidInput() {
        RufnummernportierungAnlage inputAnlage = new RufnummernportierungAnlageTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        RufnummernportierungType output = testling.apply(inputAnlage);
        assertNotNull(output);
        assertNotNull(output.getAnlagenanschluss());
    }

    @Test
    public void testApplyWithNullInput() {
        RufnummernportierungType output = testling.apply(null);
        assertNull(output);
    }
}
