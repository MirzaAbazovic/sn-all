/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 15:17:53
 */
package de.mnet.wita.marshal.v1;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import com.google.common.base.Strings;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionPortwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALPortwechselType;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.auftrag.SchaltungKupferBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.common.LeitungsBezeichnung;

@Test(groups = UNIT)
public class GeschaeftsfallSerPowMarshallerTest extends AbstractWitaMarshallerTest {
    private final GeschaeftsfallSerPowMarshaller marshaller = new GeschaeftsfallSerPowMarshaller();
    private de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled;
    private Geschaeftsfall geschaeftsfall;

    @BeforeMethod(groups = "unit")
    public void setupMarshaller() {
        geschaeftsfall = new GeschaeftsfallBuilder(GeschaeftsfallTyp.PORTWECHSEL, WitaCdmVersion.V1).buildValid();
    }

    public void auftragspositionShouldBeMarshalled() {
        marshalled = marshaller.generate(geschaeftsfall);

        AuftragspositionPortwechselType auftragsposition = marshalled.getSERPOW().getAuftragsposition().iterator().next();

        assertNotNull(auftragsposition.getProdukt());
        assertNotNull(auftragsposition.getGeschaeftsfallProdukt());
    }

    public void geschaeftsfallProduktSchaltangaben() {
        Auftrag portwechsel = new AuftragBuilder(GeschaeftsfallTyp.PORTWECHSEL, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WitaCdmVersion.V1)
                        .withEVS("2").withDoppelader("1").buildValid());
        marshalled = marshaller.generate(portwechsel.getGeschaeftsfall());

        TALPortwechselType talGeschaeftsfallProdukt = getTal(marshalled);

        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "02", "01");

        portwechsel = new AuftragBuilder(GeschaeftsfallTyp.PORTWECHSEL, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WitaCdmVersion.V1)
                        .withEVS("12").withDoppelader("11").buildValid());
        marshalled = marshaller.generate(portwechsel.getGeschaeftsfall());

        talGeschaeftsfallProdukt = getTal(marshalled);

        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "12", "11");
    }

    public void bestandsValidierung2ShouldBeSet() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = geschaeftsfall.getAuftragsPosition()
                .getGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setLeitungsBezeichnung(new LeitungsBezeichnung("22", "22", "33", Strings.padStart("123",
                10, '0')));

        marshalled = marshaller.generate(geschaeftsfall);

        TALPortwechselType geschaeftsfallProduktType = getTal(marshalled);

        assertNotNull(geschaeftsfallProduktType.getBestandsvalidierung2());
    }

    protected TALPortwechselType getTal(de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled) {
        return marshalled.getSERPOW().getAuftragsposition().iterator().next().getGeschaeftsfallProdukt().getTAL();
    }

}