/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2011 14:34:27
 */
package de.mnet.wita.unmarshall.v1;

import static de.mnet.wita.BaseTest.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERLMType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.ErlmJaxbBuilder;
import de.mnet.wita.unmarshal.v1.ErlmUnmarshaller;

@Test(groups = UNIT)
public class ErlmUnmarshallerTest extends BaseTest {

    private final ErlmUnmarshaller erlmUnmarshaller = new ErlmUnmarshaller();

    public void testExterneAuftragsnummerShouldBeRead() {
        String externeAuftragsnummer = "123456789";

        MeldungstypERLMType erlm = (new ErlmJaxbBuilder()).withExterneAuftragsnummer(externeAuftragsnummer).build();
        ErledigtMeldung unmarshalled = erlmUnmarshaller.unmarshal(erlm);

        assertThat(unmarshalled.getExterneAuftragsnummer(), equalTo(externeAuftragsnummer));
    }

    public void testKundenNummerShouldBeRead() {
        String kundenNummer = "M326586785";

        MeldungstypERLMType erlm = new ErlmJaxbBuilder().withKundenNummer(kundenNummer).build();
        ErledigtMeldung unmarshalled = erlmUnmarshaller.unmarshal(erlm);

        assertThat(unmarshalled.getKundenNummer(), equalTo(kundenNummer));
    }

    public void testKundenNummerBestellerShouldBeRead() {
        String kundennummerBesteller = "M123456789";

        MeldungstypERLMType erlm = new ErlmJaxbBuilder().withKundennummerBesteller(kundennummerBesteller).build();
        ErledigtMeldung unmarshalled = erlmUnmarshaller.unmarshal(erlm);

        assertThat(unmarshalled.getKundennummerBesteller(), equalTo(kundennummerBesteller));
    }

    public void testVertragsNummerShouldBeRead() {
        String vertragsNummer = "124676789678";

        MeldungstypERLMType erlm = new ErlmJaxbBuilder().withVertragsNummer(vertragsNummer).build();
        ErledigtMeldung unmarshalled = erlmUnmarshaller.unmarshal(erlm);

        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void testMeldungsPositionenShouldBeRead() {
        MeldungstypERLMType erlm = new ErlmJaxbBuilder().addMeldungsposition("Code", "Text").build();

        ErledigtMeldung unmarshalled = erlmUnmarshaller.unmarshal(erlm);
        assertNotNull(unmarshalled);

        Set<MeldungsPosition> meldungsPositionen = unmarshalled.getMeldungsPositionen();
        assertNotNull(meldungsPositionen);
        assertThat(meldungsPositionen, hasSize(1));

        assertThat(meldungsPositionen.iterator().next().getMeldungsCode(), equalTo("Code"));
        assertThat(meldungsPositionen.iterator().next().getMeldungsText(), equalTo("Text"));
    }

    public void testErledigungsterminShouldBeRead() {
        LocalDate erledigttermin = LocalDate.of(2012, 1, 12);

        MeldungstypERLMType erlm = new ErlmJaxbBuilder().withErledigungstermin(erledigttermin).build();
        ErledigtMeldung unmarshalled = erlmUnmarshaller.unmarshal(erlm);

        assertThat(unmarshalled.getErledigungstermin(), equalTo(erledigttermin));
    }

}
