/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wbci.marshal.v1.entities;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PortierungDurchwahlanlageType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.RufnummernblockType;
import de.mnet.wbci.marshal.AbstractWbciMarshallerTest;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Rufnummernblock;
import de.mnet.wbci.model.RufnummernportierungAnlage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.RufnummernportierungAnlageTestBuilder;

@Test(groups = BaseTest.UNIT)
public class PortierungDurchwahlanlageMarshallerTest extends AbstractWbciMarshallerTest {

    @Autowired
    private PortierungDurchwahlanlageMarshaller testling;

    @Test
    public void testApplyWithValidInput() throws Exception {
        RufnummernportierungAnlage input = new RufnummernportierungAnlageTestBuilder()
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        PortierungDurchwahlanlageType output = testling.apply(input);

        assertEquals(output.getOnkzDurchwahlAbfragestelle().getAbfragestelle(), input.getAbfragestelle());
        assertEquals(output.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer(), input.getDurchwahlnummer());
        assertEquals(output.getOnkzDurchwahlAbfragestelle().getONKZ(), input.getOnkz());
        assertEquals(output.getZuPortierenderRufnummernblock().size(), input.getRufnummernbloecke().size());
        assertNotNull(output.getZuPortierenderRufnummernblock());

        RufnummernblockType outputRufnummernblock = output.getZuPortierenderRufnummernblock().get(0);
        Rufnummernblock inputRufnummernblock = input.getRufnummernbloecke().get(0);

        assertEquals(outputRufnummernblock.getRnrBlockBis(), inputRufnummernblock.getRnrBlockBis());
        assertEquals(outputRufnummernblock.getRnrBlockVon(), inputRufnummernblock.getRnrBlockVon());
    }

    @Test
    public void testApplyWithNullInput() throws Exception {
        PortierungDurchwahlanlageType output = testling.apply(null);
        assertNull(output);
    }

}
