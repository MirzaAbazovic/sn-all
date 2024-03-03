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

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionProviderwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALProviderwechselType;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.auftrag.SchaltungKupferBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;

@Test(groups = UNIT)
public class GeschaeftsfallPvMarshallerTest extends AbstractWitaMarshallerTest {
    private final GeschaeftsfallPvMarshaller marshaller = new GeschaeftsfallPvMarshaller();
    private de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled;
    private Geschaeftsfall geschaeftsfall;

    @BeforeMethod(groups = "unit")
    public void setupMarshaller() {
        geschaeftsfall = new GeschaeftsfallBuilder(GeschaeftsfallTyp.PROVIDERWECHSEL, WitaCdmVersion.V1).buildValid();
    }

    public void auftragspositionShouldBeMarshalled() {
        marshalled = marshaller.generate(geschaeftsfall);

        AuftragspositionProviderwechselType auftragsposition = marshalled.getPV().getAuftragsposition().iterator().next();

        assertNotNull(auftragsposition.getProdukt());
        assertNotNull(auftragsposition.getGeschaeftsfallProdukt());
    }

    public void geschaeftsfallProduktShouldBeMarshalled() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setVorabstimmungsId("DEU.MNET.V123456789");

        marshalled = marshaller.generate(geschaeftsfall);

        TALProviderwechselType talGeschaeftsfallProdukt = getTal(marshalled);

        assertNull(talGeschaeftsfallProdukt.getStandortA());
        assertNotNull(talGeschaeftsfallProdukt.getStandortB());
        assertNotNull(talGeschaeftsfallProdukt.getMontageleistung());
        assertNotNull(talGeschaeftsfallProdukt.getUebertragungsverfahren());
        assertNotNull(talGeschaeftsfallProdukt.getSchaltangaben());
        assertNotNull(talGeschaeftsfallProdukt.getVorabstimmungsID());
    }

    public void geschaeftsfallProduktSchaltangaben() {
        Auftrag providerwechsel = new AuftragBuilder(GeschaeftsfallTyp.PROVIDERWECHSEL, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WitaCdmVersion.V1)
                        .withEVS("2").withDoppelader("1").buildValid());
        marshalled = marshaller.generate(providerwechsel.getGeschaeftsfall());

        TALProviderwechselType talGeschaeftsfallProdukt = getTal(marshalled);
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "02", "01");

        providerwechsel = new AuftragBuilder(GeschaeftsfallTyp.PROVIDERWECHSEL, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WitaCdmVersion.V1)
                        .withEVS("12").withDoppelader("11").buildValid());
        marshalled = marshaller.generate(providerwechsel.getGeschaeftsfall());

        talGeschaeftsfallProdukt = getTal(marshalled);
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "12", "11");
    }

    public void testFieldsThatCanBeNull() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setLeitungsBezeichnung(null);
        geschaeftsfallProdukt.setMontageleistung(null);

        marshalled = marshaller.generate(geschaeftsfall);

        TALProviderwechselType talGeschaeftsfallProdukt = marshalled.getPV().getAuftragsposition()
                .iterator().next().getGeschaeftsfallProdukt().getTAL();

        assertNull(talGeschaeftsfallProdukt.getMontageleistung());
        assertNull(talGeschaeftsfallProdukt.getMontageleistung());
    }

    protected TALProviderwechselType getTal(de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled) {
        return marshalled.getPV().getAuftragsposition().iterator().next().getGeschaeftsfallProdukt().getTAL();
    }

}