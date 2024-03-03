/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 15:17:53
 */
package de.mnet.wita.marshal.v1;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragskennerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundeType;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.message.builder.AuftragBuilder;

@Test(groups = UNIT)
public class AuftragMarshallerTest extends AbstractWitaMarshallerTest {

    @Autowired
    private AuftragMarshaller auftragMarshaller;

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
    public void externeAuftragsnummerShouldBeMarshalled(GeschaeftsfallTyp geschaeftsfallTyp) {
        Auftrag auftrag = new AuftragBuilder(geschaeftsfallTyp).withExterneAuftragsnummer("1234567430")
                .buildValid();
        AuftragType auftragType = auftragMarshaller.apply(auftrag);
        assert auftragType != null;
        assertEquals(auftragType.getExterneAuftragsnummer(), "1234567430");
    }

    @Test(dataProvider = "dataProvider")
    public void auftragKennerShouldBeMarshalled(GeschaeftsfallTyp geschaeftsfallTyp) {
        Auftrag auftrag = new AuftragBuilder(geschaeftsfallTyp).withAuftragsKenner(12445L, 10)
                .buildValid();
        AuftragType auftragType = auftragMarshaller.apply(auftrag);
        assert auftragType != null;
        AuftragskennerType auftragskenner = auftragType.getKenner().getAuftragskenner();
        assertEquals(auftragskenner.getAuftragsklammer(), "12445");
        assertEquals(auftragskenner.getAnzahlAuftraege(), 10);
    }

    @Test(dataProvider = "dataProvider")
    public void projektKennerShouldBeMarshalled(GeschaeftsfallTyp geschaeftsfallTyp) {
        Auftrag auftrag = new AuftragBuilder(geschaeftsfallTyp).withAuftragsKenner(12445L, 10)
                .withProjekt("1223466")
                .buildValid();
        AuftragType auftragType = auftragMarshaller.apply(auftrag);
        assert auftragType != null;
        String projektkenner = auftragType.getKenner().getProjektID().getProjektkenner();
        assertEquals(projektkenner, "1223466");
    }

    @Test(dataProvider = "dataProvider")
    public void bestellerShouldBeMarshalled(GeschaeftsfallTyp geschaeftsfallTyp) {
        Kunde besteller = new Kunde();
        besteller.setKundennummer("1234567890");
        besteller.setLeistungsnummer("1234567890");
        Auftrag auftrag = new AuftragBuilder(geschaeftsfallTyp).withBesteller(besteller)
                .buildValid();
        AuftragType auftragType = auftragMarshaller.apply(auftrag);
        assert auftragType != null;
        KundeType bestellerKunde = auftragType.getBesteller();
        assertEquals(bestellerKunde.getKundennummer(), "1234567890");
        assertEquals(bestellerKunde.getLeistungsnummer(), "1234567890");
    }

    @Test(dataProvider = "dataProvider")
    public void geschaeftsfallShouldBeMarshalled(GeschaeftsfallTyp geschaeftsfallTyp) {
        Auftrag auftrag = new AuftragBuilder(geschaeftsfallTyp).withExterneAuftragsnummer("1234567430")
                .buildValid();
        AuftragType auftragType = auftragMarshaller.apply(auftrag);
        assert auftragType != null;
        assertNotNull(auftragType.getGeschaeftsfall());
    }

}