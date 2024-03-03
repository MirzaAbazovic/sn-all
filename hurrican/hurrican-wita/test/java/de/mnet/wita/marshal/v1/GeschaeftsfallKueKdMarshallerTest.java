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

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALKuendigungType;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.GeschaeftsfallProdukt;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;

@Test(groups = UNIT)
public class GeschaeftsfallKueKdMarshallerTest extends AbstractWitaMarshallerTest {

    private final GeschaeftsfallKueKdMarshaller marshaller = new GeschaeftsfallKueKdMarshaller();
    private KuendigungType marshalled;

    @BeforeMethod(groups = "unit")
    public void setupMarshaller() {
        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(GeschaeftsfallTyp.KUENDIGUNG_KUNDE, WitaCdmVersion.V1).buildValid();
        GeschaeftsfallProdukt geschaeftsfallProdukt = geschaeftsfall.getAuftragsPosition().getGeschaeftsfallProdukt();
        geschaeftsfallProdukt.setVorabstimmungsId("DEU.MNET.V123456789");
        marshalled = marshaller.generate(geschaeftsfall).getKUEKD();
    }

    public void auftragspositionShouldBeMarshalled() {
        AuftragspositionKuendigungType auftragsposition = marshalled.getAuftragsposition().iterator().next();

        assertNotNull(auftragsposition.getProdukt());
        TALKuendigungType geschaeftsfallProdukt = auftragsposition.getGeschaeftsfallProdukt().getTAL();
        assertNotNull(geschaeftsfallProdukt);
        assertNotNull(geschaeftsfallProdukt.getVorabstimmungsID());
    }

    public void bestandsvalidierung2ShouldBeMarshalled() {
        AuftragspositionKuendigungType auftragsposition = marshalled.getAuftragsposition().iterator().next();
        TALKuendigungType geschaeftsfallProdukt = auftragsposition.getGeschaeftsfallProdukt().getTAL();
        assertNotNull(geschaeftsfallProdukt.getBestandsvalidierung2());
    }
}
