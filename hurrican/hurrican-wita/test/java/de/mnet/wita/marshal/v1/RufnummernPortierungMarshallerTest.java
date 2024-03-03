/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 15:17:53
 */
package de.mnet.wita.marshal.v1;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzDurchwahlAbfragestelleType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzRufNrType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungDurchwahlanlageType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortierungEinzelanschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernblockType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RufnummernportierungType;
import de.mnet.wita.message.builder.common.portierung.RufnummernPortierungBuilder;
import de.mnet.wita.message.common.portierung.RufnummernPortierung;

@Test(groups = UNIT)
public class RufnummernPortierungMarshallerTest extends AbstractWitaMarshallerTest {

    private final RufnummernPortierungMarshaller marshaller = new RufnummernPortierungMarshaller();
    private RufnummernportierungType marshalled;

    public void rufnummernToBeMarshalledWithEinzelAnschluss() {
        RufnummernPortierung rufnummernPortierung = new RufnummernPortierungBuilder().buildAuftragPortierung(true);
        marshalled = marshaller.generate(rufnummernPortierung);
        PortierungEinzelanschlussType portierung = marshalled.getEinzelanschluss();
        OnkzRufNrType onkzRufNr = portierung.getRufnummernliste().getZuPortierendeOnkzRnr()
                .iterator().next();
        assertEquals(onkzRufNr.getONKZ(), "89");
        assertEquals(onkzRufNr.getRufnummer(), "123");
    }

    public void rufnummernToBeMarshalledWithAnlagenAnschluss() {
        RufnummernPortierung rufnummernPortierung = new RufnummernPortierungBuilder().buildAuftragPortierung(false);
        marshalled = marshaller.generate(rufnummernPortierung);
        PortierungDurchwahlanlageType portierung = marshalled.getAnlagenanschluss();
        OnkzDurchwahlAbfragestelleType onkzDurchwahlAbfragestelle = portierung.getOnkzDurchwahlAbfragestelle();
        RufnummernblockType rufnummernblock = portierung.getZuPortierenderRufnummernblock().iterator().next();
        assertEquals(onkzDurchwahlAbfragestelle.getAbfragestelle(), "2345");
        assertEquals(onkzDurchwahlAbfragestelle.getDurchwahlnummer(), "1234");
        assertEquals(onkzDurchwahlAbfragestelle.getONKZ(), "89");
        assertEquals(rufnummernblock.getRnrBlockVon(), "100");
        assertEquals(rufnummernblock.getRnrBlockBis(), "299");
    }

}