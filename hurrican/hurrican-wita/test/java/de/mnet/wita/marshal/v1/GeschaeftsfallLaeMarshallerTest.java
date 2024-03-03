/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 15:17:53
 */
package de.mnet.wita.marshal.v1;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionLeistungsaenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALLeistungsaenderungType;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.StandortKunde;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.auftrag.SchaltungKupferBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.common.Personenname;

@Test(groups = UNIT)
public class GeschaeftsfallLaeMarshallerTest extends AbstractWitaMarshallerTest {

    private final GeschaeftsfallLaeMarshaller marshaller = new GeschaeftsfallLaeMarshaller();
    private de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled;
    private Geschaeftsfall geschaeftsfall;

    @BeforeMethod(groups = "unit")
    public void setupMarshaller() {
        geschaeftsfall = new GeschaeftsfallBuilder(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, WitaCdmVersion.V1).buildValid();
    }

    public void auftragspositionShouldBeMarshalled() {
        marshalled = marshaller.generate(geschaeftsfall);
        AuftragspositionLeistungsaenderungType auftragsposition = marshalled.getLAE().getAuftragsposition().iterator().next();
        assertNotNull(auftragsposition.getProdukt());
        assertNotNull(auftragsposition.getGeschaeftsfallProdukt());
        assertNotNull(auftragsposition.getAktionscode());
    }

    public void geschaeftsfallProduktShouldBeMarshalled() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt();
        Personenname person = new Personenname();
        person.setAnrede(Anrede.HERR);
        person.setNachname("Meyer");
        StandortKunde standortKunde = new StandortKunde();
        standortKunde.setKundenname(person);
        standortKunde.setStrassenname("Emmy-Noether-Str.");
        standortKunde.setHausnummer("2");
        standortKunde.setPostleitzahl("82000");
        standortKunde.setOrtsname("München");
        geschaeftsfallProdukt.setStandortKunde(standortKunde);

        marshalled = marshaller.generate(geschaeftsfall);

        TALLeistungsaenderungType talGeschaeftsfallProdukt = getTal(marshalled);

        assertNotNull(talGeschaeftsfallProdukt.getStandortA());
        assertNotNull(talGeschaeftsfallProdukt.getStandortB());
        assertNotNull(talGeschaeftsfallProdukt.getMontageleistung());
        assertNotNull(talGeschaeftsfallProdukt.getUebertragungsverfahren());
        assertNotNull(talGeschaeftsfallProdukt.getSchaltangaben());
        assertNotNull(talGeschaeftsfallProdukt.getBestandsvalidierung2());
    }

    public void geschaeftsfallProduktSchaltangaben() {
        Auftrag leistungsAenderung = new AuftragBuilder(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WitaCdmVersion.V1)
                        .withEVS("2").withDoppelader("1").buildValid());
        marshalled = marshaller.generate(leistungsAenderung.getGeschaeftsfall());

        TALLeistungsaenderungType talGeschaeftsfallProdukt = getTal(marshalled);
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "02", "01");

        leistungsAenderung = new AuftragBuilder(GeschaeftsfallTyp.LEISTUNGS_AENDERUNG, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WitaCdmVersion.V1)
                        .withEVS("12").withDoppelader("11").buildValid());
        marshalled = marshaller.generate(leistungsAenderung.getGeschaeftsfall());

        talGeschaeftsfallProdukt = getTal(marshalled);
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "12", "11");
    }

    public void testFieldsThatCanBeNull() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = geschaeftsfall.getAuftragsPosition()
                .getGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setStandortKunde(null);
        geschaeftsfallProdukt.setLeitungsBezeichnung(null);
        geschaeftsfallProdukt.setMontageleistung(null);
        geschaeftsfallProdukt.setSchaltangaben(null);

        marshalled = marshaller.generate(geschaeftsfall);

        TALLeistungsaenderungType talGeschaeftsfallProdukt = getTal(marshalled);

        assertNull(talGeschaeftsfallProdukt.getStandortA());
        assertNull(talGeschaeftsfallProdukt.getMontageleistung());
        assertNull(talGeschaeftsfallProdukt.getUebertragungsverfahren());
        assertNull(talGeschaeftsfallProdukt.getSchaltangaben());
        assertNull(talGeschaeftsfallProdukt.getBestandsvalidierung2());
    }

    private TALLeistungsaenderungType getTal(de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall gf) {
        return gf.getLAE().getAuftragsposition().iterator().next().getGeschaeftsfallProdukt().getTAL();
    }

}
