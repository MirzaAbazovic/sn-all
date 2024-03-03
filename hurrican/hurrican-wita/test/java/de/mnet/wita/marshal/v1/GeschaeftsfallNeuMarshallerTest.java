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

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionBereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALBereitstellungType;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Vormieter;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.auftrag.SchaltungKupferBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;

@Test(groups = UNIT)
public class GeschaeftsfallNeuMarshallerTest extends AbstractWitaMarshallerTest {

    private final GeschaeftsfallNeuMarshaller marshaller = new GeschaeftsfallNeuMarshaller();
    private de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled;

    public void auftragspositionShouldBeMarshalled() {
        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(GeschaeftsfallTyp.BEREITSTELLUNG, WitaCdmVersion.V1).buildValid();

        marshalled = marshaller.generate(geschaeftsfall);

        AuftragspositionBereitstellungType auftragsposition = marshalled.getNEU().getAuftragsposition().iterator().next();

        assertNotNull(auftragsposition.getProdukt());
        assertNotNull(auftragsposition.getGeschaeftsfallProdukt());
    }

    public void geschaeftsfallProduktShouldBeMarshalled() {
        Auftrag validBereitstellung = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer();

        GeschaeftsfallProdukt geschaeftsfallProdukt = validBereitstellung.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setVormieter(new Vormieter("Max", "Weber", null, null, null));
        geschaeftsfallProdukt.setVorabstimmungsId("DEU.MNET.V123456789");

        marshalled = marshaller.generate(validBereitstellung.getGeschaeftsfall());

        TALBereitstellungType talGeschaeftsfallProdukt = getTal(marshalled);

        assertNotNull(talGeschaeftsfallProdukt.getStandortA());
        assertNotNull(talGeschaeftsfallProdukt.getStandortB());
        assertNotNull(talGeschaeftsfallProdukt.getVormieter());
        assertEquals(talGeschaeftsfallProdukt.getVormieter().getPerson().getNachname(), "Weber");
        assertNotNull(talGeschaeftsfallProdukt.getMontageleistung());
        assertNotNull(talGeschaeftsfallProdukt.getUebertragungsverfahren());
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "01", "01");
        assertNotNull(talGeschaeftsfallProdukt.getVorabstimmungsID());
    }

    public void geschaeftsfallProduktSchaltangaben() {
        Auftrag validBereitstellung = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WitaCdmVersion.V1)
                        .withEVS("2").withDoppelader("1").buildValid());
        marshalled = marshaller.generate(validBereitstellung.getGeschaeftsfall());

        TALBereitstellungType talGeschaeftsfallProdukt = getTal(marshalled);
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "02", "01");

        validBereitstellung = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG, WitaCdmVersion.V1)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WitaCdmVersion.V1)
                        .withEVS("12").withDoppelader("11").buildValid());
        marshalled = marshaller.generate(validBereitstellung.getGeschaeftsfall());

        talGeschaeftsfallProdukt = getTal(marshalled);
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "12", "11");
    }

    public void testFieldsCanBeNull() {
        Auftrag validBereitstellung = new AuftragBuilder(GeschaeftsfallTyp.BEREITSTELLUNG, WitaCdmVersion.V1).buildValid();

        GeschaeftsfallProdukt geschaeftsfallProdukt = validBereitstellung.getGeschaeftsfall().getAuftragsPosition().getGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setMontageleistung(null);

        marshalled = marshaller.generate(validBereitstellung.getGeschaeftsfall());

        TALBereitstellungType talGeschaeftsfallProdukt = getTal(marshalled);
        assertNull(talGeschaeftsfallProdukt.getVormieter());
        assertNull(talGeschaeftsfallProdukt.getMontageleistung());
    }

    protected TALBereitstellungType getTal(de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled) {
        return marshalled.getNEU().getAuftragsposition().iterator().next().getGeschaeftsfallProdukt().getTAL();
    }

}