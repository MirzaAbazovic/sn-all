/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 15:17:53
 */
package de.mnet.wita.marshal.v2;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.AuftragspositionVerbundleistungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.TALVerbundleistungType;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.Montageleistung;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.auftrag.SchaltungKupferBuilder;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.common.Personenname;

@SuppressWarnings("Duplicates")
@Test(groups = UNIT)
public class GeschaeftsfallVblMarshallerTest extends AbstractWitaMarshallerTest {

    private final GeschaeftsfallVblMarshallerV2 marshaller = new GeschaeftsfallVblMarshallerV2();
    private de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall marshalled;
    private Geschaeftsfall geschaeftsfall;

    @BeforeMethod(groups = "unit")
    public void setupMarshaller() {
        geschaeftsfall = new GeschaeftsfallBuilder(GeschaeftsfallTyp.VERBUNDLEISTUNG, WITA_CDM_VERSION).buildValid();
    }

    public void auftragspositionShouldBeMarshalled() {
        marshalled = marshaller.generate(geschaeftsfall);

        AuftragspositionVerbundleistungType auftragsposition = marshalled.getVBL().getAuftragsposition().iterator().next();

        assertNotNull(auftragsposition.getProdukt());
        assertNotNull(auftragsposition.getGeschaeftsfallProdukt());
    }

    public void geschaeftsfallProduktShouldBeMarshalled() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt();
        Personenname person = new Personenname();
        person.setAnrede(Anrede.HERR);
        person.setNachname("Meyer");
        Montageleistung montageleistung = new Montageleistung();
        montageleistung.setPersonenname(person);
        montageleistung.setTelefonnummer("1234");
        geschaeftsfallProdukt.setMontageleistung(montageleistung);
        geschaeftsfallProdukt.setVorabstimmungsId("DEU.MNET.V123456789");

        marshalled = marshaller.generate(geschaeftsfall);

        TALVerbundleistungType talGeschaeftsfallProdukt = getTal(marshalled);

        assertNull(talGeschaeftsfallProdukt.getStandortA());
        assertNotNull(talGeschaeftsfallProdukt.getStandortB());
        assertNotNull(talGeschaeftsfallProdukt.getAnsprechpartnerMontage());
        assertNotNull(talGeschaeftsfallProdukt.getUebertragungsverfahren());
        assertNotNull(talGeschaeftsfallProdukt.getSchaltangaben());
        assertNotNull(talGeschaeftsfallProdukt.getVorabstimmungsID());
    }

    public void geschaeftsfallProduktSchaltangaben() {
        Auftrag verbundleistung = new AuftragBuilder(GeschaeftsfallTyp.VERBUNDLEISTUNG, WITA_CDM_VERSION)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WITA_CDM_VERSION)
                        .withEVS("2").withDoppelader("1").buildValid());
        marshalled = marshaller.generate(verbundleistung.getGeschaeftsfall());

        TALVerbundleistungType talGeschaeftsfallProdukt = getTal(marshalled);
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "02", "01");

        verbundleistung = new AuftragBuilder(GeschaeftsfallTyp.VERBUNDLEISTUNG, WITA_CDM_VERSION)
                .buildAuftragWithSchaltungKupfer(new SchaltungKupferBuilder(WITA_CDM_VERSION)
                        .withEVS("12").withDoppelader("11").buildValid());
        marshalled = marshaller.generate(verbundleistung.getGeschaeftsfall());

        talGeschaeftsfallProdukt = marshalled.getVBL().getAuftragsposition().iterator().next().getGeschaeftsfallProdukt().getTAL();
        verifySchaltangaben(talGeschaeftsfallProdukt.getSchaltangaben(), "12", "11");
    }

    public void testFieldsThatCanBeNull() {
        GeschaeftsfallProdukt geschaeftsfallProdukt = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setMontageleistung(null);
        geschaeftsfallProdukt.setBestandsSuche(null);
        geschaeftsfallProdukt.setRufnummernPortierung(null);
        marshalled = marshaller.generate(geschaeftsfall);
        TALVerbundleistungType talGeschaeftsfallProdukt = getTal(marshalled);
        assertNull(talGeschaeftsfallProdukt.getAnsprechpartnerMontage());
    }

    private TALVerbundleistungType getTal(de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.Geschaeftsfall gf) {
        return gf.getVBL().getAuftragsposition().iterator().next().getGeschaeftsfallProdukt().getTAL();
    }


}