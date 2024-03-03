/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2014
 */
package de.mnet.wita.unmarshall.v2;

import static de.mnet.wita.BaseTest.*;
import static org.testng.AssertJUnit.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.unmarshal.v2.MessageUnmarshallerV2;

@Test(groups = UNIT)
@ContextConfiguration({ "classpath:de/mnet/wita/unmarshal/wita-unmarshaller-test-context.xml" })
public class MessageUnmarshallerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    protected MessageUnmarshallerV2 messageUnmarshaller;

    @DataProvider
    public Object[][] dataProviderShouldUnmarshallMeldung() {
        return new Object[][] {
                { "soap-request/v10/valid-abbm.xml", AbbruchMeldung.class },
                { "soap-request/v10/valid-abm.xml", AuftragsBestaetigungsMeldung.class },
                { "soap-request/v10/valid-entm.xml", EntgeltMeldung.class },
                { "soap-request/v10/valid-erlm.xml", ErledigtMeldung.class },
                { "soap-request/v10/valid-mtam.xml", TerminAnforderungsMeldung.class },
                { "soap-request/v10/valid-qeb.xml", QualifizierteEingangsBestaetigung.class },
                { "soap-request/v10/valid-tam.xml", TerminAnforderungsMeldung.class },
                { "soap-request/v10/valid-vzm.xml", VerzoegerungsMeldung.class }
        };
    }

    @Test(dataProvider = "dataProviderShouldUnmarshallMeldung")
    public <T extends WitaMessage> void testShouldUnmarshallMeldung(String resource, Class<T> clazz) throws Exception {
        Source source = new StreamSource(new ClassPathResource(resource).getInputStream());
        Meldung meldung = (Meldung) messageUnmarshaller.unmarshal(source);
        assertNotNull(meldung);
        assertTrue(clazz.isAssignableFrom(meldung.getClass()));
    }

}
