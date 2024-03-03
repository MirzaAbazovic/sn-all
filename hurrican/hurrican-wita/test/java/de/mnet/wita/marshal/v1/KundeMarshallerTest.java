/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2014
 */
package de.mnet.wita.marshal.v1;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundeType;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.builder.AuftragBuilder;

@Test(groups = UNIT)
public class KundeMarshallerTest extends AbstractWitaMarshallerTest {
    KundeMarshaller kundeMarshaller = new KundeMarshaller();

    @DataProvider
    public Object[][] dataProvider() {
        // @formatter:off
        return new Object[][] {
                { BEREITSTELLUNG },
                { KUENDIGUNG_KUNDE },
                { LEISTUNGS_AENDERUNG },
                { LEISTUNGSMERKMAL_AENDERUNG },
                { PORTWECHSEL },
                { PROVIDERWECHSEL },
                { RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG },
                { VERBUNDLEISTUNG },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProvider")
    public void kundeShouldBeMarshalled(GeschaeftsfallTyp geschaeftsfallTyp) {
        Auftrag auftrag = new AuftragBuilder(geschaeftsfallTyp).buildValid();
        KundeType kunde = kundeMarshaller.generate(auftrag.getKunde());
        assertEquals(kunde.getKundennummer(), "1234567890");
        assertEquals(kunde.getLeistungsnummer(), "1234567890");
    }


}
