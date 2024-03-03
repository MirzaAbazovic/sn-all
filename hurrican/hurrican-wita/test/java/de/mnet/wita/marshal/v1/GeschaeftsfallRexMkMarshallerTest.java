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

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AuftragspositionRnrExportMitKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.TALRnrExportMitKuendigungType;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;

@Test(groups = UNIT)
public class GeschaeftsfallRexMkMarshallerTest extends AbstractWitaMarshallerTest {

    private final GeschaeftsfallRexMkMarshaller marshaller = new GeschaeftsfallRexMkMarshaller();
    private de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled;
    private Geschaeftsfall geschaeftsfall;

    @BeforeMethod(groups = "unit")
    public void setupMarshaller() {
        geschaeftsfall = new GeschaeftsfallBuilder(GeschaeftsfallTyp.RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, WitaCdmVersion.V1).buildValid();
    }

    public void auftragspositionShouldBeMarshalled() {
        marshalled = marshaller.generate(geschaeftsfall);

        AuftragspositionRnrExportMitKuendigungType auftragsposition = marshalled.getREXMK().getAuftragsposition().iterator().next();

        assertNotNull(auftragsposition.getProdukt());
        assertNotNull(auftragsposition.getGeschaeftsfallProdukt());
    }

    public void geschaeftsfallProduktShouldBeMarshalled() {
        marshalled = marshaller.generate(geschaeftsfall);

        TALRnrExportMitKuendigungType talGeschaeftsfallProdukt = getTal(marshalled);

        assertNotNull(talGeschaeftsfallProdukt.getStandortA());
        assertNotNull(talGeschaeftsfallProdukt.getBestandssuche());
        assertNotNull(talGeschaeftsfallProdukt.getRufnummernPortierung());
    }

    protected TALRnrExportMitKuendigungType getTal(de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled) {
        return marshalled.getREXMK().getAuftragsposition().iterator().next().getGeschaeftsfallProdukt().getTAL();
    }


}