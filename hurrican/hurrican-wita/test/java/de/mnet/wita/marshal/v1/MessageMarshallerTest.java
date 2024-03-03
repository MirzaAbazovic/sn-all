/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.08.13
 */
package de.mnet.wita.marshal.v1;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import javax.xml.transform.dom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.xml.transform.StringResult;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungKundeBuilder;
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.meldung.OutgoingMeldung;

@Test(groups = BaseTest.UNIT)
public class MessageMarshallerTest extends AbstractWitaMarshallerTest {

    @Autowired
    @Qualifier("witaMessageMarshallerV1")
    private MessageMarshaller testling;

    private static final GeschaeftsfallTyp[] GESCHAEFTSFAELLE = {
            BEREITSTELLUNG,
            KUENDIGUNG_KUNDE,
            LEISTUNGS_AENDERUNG,
            LEISTUNGSMERKMAL_AENDERUNG,
            PORTWECHSEL,
            PROVIDERWECHSEL,
            RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG,
            VERBUNDLEISTUNG,
            // BESTANDSUEBERSICHT
            // KUENDIGUNG_TELEKOM
            // PRODUKTGRUPPENWECHSEL
    };

    @DataProvider
    public Object[][] createOrderDP() {
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            data.add(new Object[] { new AuftragBuilder(typ).buildValid()});
        }
        return data.toArray(new Object[data.size()][]);
    }

    @DataProvider
    public Object[][] cancelOrderDP() {
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            data.add(new Object[] { new StornoBuilder(typ).buildValid()});
        }
        return data.toArray(new Object[data.size()][]);
    }

    @DataProvider
    public Object[][] rescheduleOrderDP() {
        List<Object[]> data = new ArrayList<>();
        for (GeschaeftsfallTyp typ : GESCHAEFTSFAELLE) {
            data.add(new Object[] { new TerminVerschiebungBuilder(typ).buildValid()});
        }
        return data.toArray(new Object[data.size()][]);
    }

    @DataProvider
    public Object[][] updateOrderDP() {
        return new Object[][] {
                { new ErledigtMeldungKundeBuilder().build()},
                { new RueckMeldungPvBuilder().build()}
        };
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void shouldNotMarshallNull() throws Exception {
        testling.marshal(null, new DOMResult());
        Assert.assertTrue(false, "no IllegalArgumentException thrown");
    }

    @Test(dataProvider = "createOrderDP")
    public void testCreateOrder(Auftrag auftrag) throws Exception {
        StringResult result = new StringResult();

        testling.marshal(auftrag, result);

        assertThat(result.toString(), notNullValue());
        assertSchemaValidLineOrderService(WitaCdmVersion.V1, result.toString());
        Assert.assertTrue(result.toString().contains("createOrder"));
    }

    @Test(dataProvider = "cancelOrderDP")
    public void testCancelOrder(Storno storno) throws Exception {
        StringResult result = new StringResult();

        testling.marshal(storno, result);

        assertThat(result.toString(), notNullValue());
        assertSchemaValidLineOrderService(WitaCdmVersion.V1, result.toString());
        Assert.assertTrue(result.toString().contains("cancelOrder"));
    }

    @Test(dataProvider = "rescheduleOrderDP")
    public void testRescheduleOrder(TerminVerschiebung tv) throws Exception {
        StringResult result = new StringResult();

        testling.marshal(tv, result);

        assertThat(result.toString(), notNullValue());
        assertSchemaValidLineOrderService(WitaCdmVersion.V1, result.toString());
        Assert.assertTrue(result.toString().contains("rescheduleOrder"));
    }

    @Test(dataProvider = "updateOrderDP")
    public void testUpdateOrder(OutgoingMeldung meldung) throws Exception {
        StringResult result = new StringResult();

        testling.marshal(meldung, result);

        assertThat(result.toString(), notNullValue());
        assertSchemaValidLineOrderService(WitaCdmVersion.V1, result.toString());
        Assert.assertTrue(result.toString().contains("updateOrder"));
    }

}
