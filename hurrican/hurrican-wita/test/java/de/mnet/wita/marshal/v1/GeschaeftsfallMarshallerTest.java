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

import java.lang.reflect.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BereitstellungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BestandsuebersichtType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.GeschaeftsfallType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsaenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalAenderungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktgruppenwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProviderwechselType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.RnrExportMitKuendigungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.VerbundleistungType;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.geschaeftsfall.Geschaeftsfall;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;

@Test(groups = UNIT)
public class GeschaeftsfallMarshallerTest extends AbstractWitaMarshallerTest {
    de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall marshalled = null;

    @DataProvider
    public Object[][] dataProvider() {
        // @formatter:off
        return new Object[][] {
                { BEREITSTELLUNG, new GeschaeftsfallNeuMarshaller(), BereitstellungType.class },
                { KUENDIGUNG_KUNDE, new GeschaeftsfallKueKdMarshaller(), KuendigungType.class },
                { LEISTUNGS_AENDERUNG, new GeschaeftsfallLaeMarshaller(), LeistungsaenderungType.class },
                { LEISTUNGSMERKMAL_AENDERUNG, new GeschaeftsfallLmaeMarshaller(), LeistungsmerkmalAenderungType.class },
                { PORTWECHSEL, new GeschaeftsfallSerPowMarshaller(), PortwechselType.class },
                { PROVIDERWECHSEL, new GeschaeftsfallPvMarshaller(), ProviderwechselType.class },
                { RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG, new GeschaeftsfallRexMkMarshaller(), RnrExportMitKuendigungType.class },
                { VERBUNDLEISTUNG, new GeschaeftsfallVblMarshaller(), VerbundleistungType.class },
        };
        // @formatter:on
    }

    @DataProvider
    public Object[][] dataProviderWithVertragsnummer() {
        // @formatter:off
        return new Object[][] {
                { KUENDIGUNG_KUNDE, new GeschaeftsfallKueKdMarshaller(), KuendigungType.class },
                { LEISTUNGS_AENDERUNG, new GeschaeftsfallLaeMarshaller(), LeistungsaenderungType.class },
                { LEISTUNGSMERKMAL_AENDERUNG, new GeschaeftsfallLmaeMarshaller(), LeistungsmerkmalAenderungType.class },
                { PORTWECHSEL, new GeschaeftsfallSerPowMarshaller(), PortwechselType.class },
                { PROVIDERWECHSEL, new GeschaeftsfallPvMarshaller(), ProviderwechselType.class },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProvider")
    public <T extends GeschaeftsfallType> void testEmptyAnlage(GeschaeftsfallTyp geschaeftsfallTyp,
            GeschaeftsfallMarshaller<T> marshaller, Class<T> resultClazz) throws Exception {
        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(geschaeftsfallTyp).buildValid();

        marshalled = marshaller.generate(geschaeftsfall);
        T marshalledGf = lookupGeschaeftsfallType(resultClazz, marshalled);

        assertFieldNull(marshalledGf, "Anlagen", resultClazz);
    }

    @Test(dataProvider = "dataProvider")
    public <T extends GeschaeftsfallType> void testNotEmptyFields(GeschaeftsfallTyp geschaeftsfallTyp,
            GeschaeftsfallMarshaller<T> marshaller, Class<T> resultClazz) throws Exception {
        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(geschaeftsfallTyp).buildValid();

        marshalled = marshaller.generate(geschaeftsfall);
        T marshalledGf = lookupGeschaeftsfallType(resultClazz, marshalled);

        assertFieldNotNull(marshalledGf, "Ansprechpartner", resultClazz);
        assertFieldNotNull(marshalledGf, "Termine", resultClazz);
        assertFieldNotNull(marshalledGf, "Auftragsposition", resultClazz);
        assertFieldNotNull(marshalledGf, "BKTOFaktura", resultClazz);
    }

    @Test(dataProvider = "dataProviderWithVertragsnummer")
    public <T extends GeschaeftsfallType> void testMarshalledVertragsnummer(GeschaeftsfallTyp geschaeftsfallTyp,
            GeschaeftsfallMarshaller<T> marshaller, Class<T> resultClazz) throws Exception {
        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(geschaeftsfallTyp).buildValid();

        marshalled = marshaller.generate(geschaeftsfall);
        T marshalledGf = lookupGeschaeftsfallType(resultClazz, marshalled);

        assertFieldNotNull(marshalledGf, "Vertragsnummer", resultClazz);
        assertFieldVertragsnummer(marshalledGf, resultClazz);
    }

    @Test(dataProvider = "dataProvider")
    public <T extends GeschaeftsfallType> void testBktoFatkuraCanBeNull(GeschaeftsfallTyp geschaeftsfallTyp,
            GeschaeftsfallMarshaller<T> marshaller, Class<T> resultClazz) throws Exception {
        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(geschaeftsfallTyp).buildValid();
        geschaeftsfall.setBktoFatkura(null);

        marshalled = marshaller.generate(geschaeftsfall);
        T marshalledGf = lookupGeschaeftsfallType(resultClazz, marshalled);

        assertFieldNull(marshalledGf, "BKTOFaktura", resultClazz);
    }

    @Test(dataProvider = "dataProvider")
    public <T extends GeschaeftsfallType> void testMarshalledBktoFatkura(GeschaeftsfallTyp geschaeftsfallTyp,
            GeschaeftsfallMarshaller<T> marshaller, Class<T> resultClazz) throws Exception {

        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(geschaeftsfallTyp).buildValid();

        marshalled = marshaller.generate(geschaeftsfall);
        T marshalledGf = lookupGeschaeftsfallType(resultClazz, marshalled);

        assertFieldNotNull(marshalledGf, "BKTOFaktura", resultClazz);
        assertFieldBktoFaktura(marshalledGf, resultClazz);
    }

    @Test(dataProvider = "dataProvider")
    public <T extends GeschaeftsfallType> void testMarshalledAnsprechPartnerNachname(GeschaeftsfallTyp geschaeftsfallTyp,
            GeschaeftsfallMarshaller<T> marshaller, Class<T> resultClazz) throws Exception {

        Geschaeftsfall geschaeftsfall = new GeschaeftsfallBuilder(geschaeftsfallTyp).buildValid();

        marshalled = marshaller.generate(geschaeftsfall);
        T marshalledGf = lookupGeschaeftsfallType(resultClazz, marshalled);

        assertFieldAnsprechPartnerNachname(marshalledGf, resultClazz);
    }

    private <T extends GeschaeftsfallType> void assertFieldNull(T marshalled, String fieldName, Class<T> resultClazz)
            throws Exception {
        Method getFieldMethod = resultClazz.getMethod("get" + fieldName);
        Object result = getFieldMethod.invoke(marshalled);
        assertNull(result);
    }

    private <T extends GeschaeftsfallType> void assertFieldNotNull(T marshalled, String fieldName, Class<T> resultClazz)
            throws Exception {
        Method getFieldMethod = resultClazz.getMethod("get" + fieldName);
        Object result = getFieldMethod.invoke(marshalled);
        assertNotNull(result);
    }

    private <T extends GeschaeftsfallType> void assertFieldVertragsnummer(T marshalled, Class<T> resultClazz)
            throws Exception {
        Method getFieldMethod = resultClazz.getMethod("getVertragsnummer");
        Object result = getFieldMethod.invoke(marshalled);
        assertEquals("0000000123", result.toString());
    }

    private <T extends GeschaeftsfallType> void assertFieldBktoFaktura(T marshalled, Class<T> resultClazz)
            throws Exception {
        Method getFieldMethod = resultClazz.getMethod("getBKTOFaktura");
        Object result = getFieldMethod.invoke(marshalled);
        assertEquals("5883000320", result.toString());
    }

    private <T extends GeschaeftsfallType> void assertFieldAnsprechPartnerNachname(T marshalled, Class<T> resultClazz)
            throws Exception {
        Method getFieldMethod = resultClazz.getMethod("getAnsprechpartner");
        AnsprechpartnerType result = (AnsprechpartnerType) getFieldMethod.invoke(marshalled);
        assertNotNull(result.getAuftragsmanagement().getNachname());
    }

    private <T extends GeschaeftsfallType> T lookupGeschaeftsfallType(Class<T> resultClazz, de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.Geschaeftsfall gf) {
        if (resultClazz.isAssignableFrom(LeistungsmerkmalAenderungType.class)) {
            return (T) gf.getAENLMAE();
        }
        if (resultClazz.isAssignableFrom(BestandsuebersichtType.class)) {
            return (T) gf.getAUSBUE();
        }
        if (resultClazz.isAssignableFrom(KuendigungType.class)) {
            return (T) gf.getKUEKD();
        }
        if (resultClazz.isAssignableFrom(BereitstellungType.class)) {
            return (T) gf.getNEU();
        }
        if (resultClazz.isAssignableFrom(ProduktgruppenwechselType.class)) {
            return (T) gf.getPGW();
        }
        if (resultClazz.isAssignableFrom(ProviderwechselType.class)) {
            return (T) gf.getPV();
        }
        if (resultClazz.isAssignableFrom(RnrExportMitKuendigungType.class)) {
            return (T) gf.getREXMK();
        }
        if (resultClazz.isAssignableFrom(PortwechselType.class)) {
            return (T) gf.getSERPOW();
        }
        if (resultClazz.isAssignableFrom(LeistungsaenderungType.class)) {
            return (T) gf.getLAE();
        }
        if (resultClazz.isAssignableFrom(VerbundleistungType.class)) {
            return (T) gf.getVBL();
        }
        return null;
    }
}