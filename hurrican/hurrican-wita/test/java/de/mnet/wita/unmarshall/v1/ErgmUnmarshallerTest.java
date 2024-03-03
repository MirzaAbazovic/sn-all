/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2011
 */
package de.mnet.wita.unmarshall.v1;

import static de.mnet.wita.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERGMType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.message.meldung.ErgebnisMeldung;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.ErgmJaxbBuilder;
import de.mnet.wita.unmarshal.v1.ErgmUnmarshaller;

@Test(groups = UNIT)
public class ErgmUnmarshallerTest extends BaseTest {

    private final ErgmUnmarshaller ergmUnmarshaller = new ErgmUnmarshaller();

    public void testExterneAuftragsnummerShouldBeRead() {
        String externeAuftragsnummer = "123456789";

        MeldungstypERGMType ergm = (new ErgmJaxbBuilder()).withExterneAuftragsnummer(externeAuftragsnummer).build();
        ErgebnisMeldung unmarshalled = ergmUnmarshaller.unmarshal(ergm);

        assertThat(unmarshalled.getExterneAuftragsnummer(), equalTo(externeAuftragsnummer));
    }

    public void testKundennummerShouldBeRead() {
        String kundennummer = "M326586785";

        MeldungstypERGMType ergm = new ErgmJaxbBuilder().withKundennummer(kundennummer).build();
        ErgebnisMeldung unmarshalled = ergmUnmarshaller.unmarshal(ergm);

        assertThat(unmarshalled.getKundenNummer(), equalTo(kundennummer));
    }

    public void testKundennummerBestellerShouldBeRead() {
        String kundennummerBesteller = "N326586785";

        MeldungstypERGMType ergm = new ErgmJaxbBuilder().withKundennummerBesteller(kundennummerBesteller).build();
        ErgebnisMeldung unmarshalled = ergmUnmarshaller.unmarshal(ergm);

        assertThat(unmarshalled.getKundennummerBesteller(), equalTo(kundennummerBesteller));
    }

    public void testVertragsnummerShouldBeRead() {
        String vertragsnummer = "124676789678";

        MeldungstypERGMType ergm = new ErgmJaxbBuilder().withVertragsnummer(vertragsnummer).build();
        ErgebnisMeldung unmarshalled = ergmUnmarshaller.unmarshal(ergm);

        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsnummer));
    }

    public void testErgebnislinkShouldBeRead() {
        String ergebnislink = "https://ergebnislink.test";

        MeldungstypERGMType ergm = new ErgmJaxbBuilder().withErgebnislink(ergebnislink).build();
        ErgebnisMeldung unmarshalled = ergmUnmarshaller.unmarshal(ergm);

        assertThat(unmarshalled.getErgebnislink(), equalTo(ergebnislink));
    }

}
