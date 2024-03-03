/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 09:32:56
 */
package de.mnet.wita.unmarshall.v2;

import static de.mnet.wita.BaseTest.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypVZMType;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.VzmJaxbBuilder;
import de.mnet.wita.unmarshal.v2.VzmUnmarshallerV2;

@SuppressWarnings("Duplicates")
@Test(groups = UNIT)
public class VzmUnmarshallerTest {

    private final VzmUnmarshallerV2 vzmUnmarshaller = new VzmUnmarshallerV2();

    public void testExterneAuftragsnummerShouldBeRead() {
        String externeAuftragsnummer = "123456789";

        MeldungstypVZMType vzm = (new VzmJaxbBuilder()).withExterneAuftragsnummer(externeAuftragsnummer).build();
        VerzoegerungsMeldung unmarshalled = vzmUnmarshaller.unmarshal(vzm);

        assertThat(unmarshalled.getExterneAuftragsnummer(), equalTo(externeAuftragsnummer));
    }

    public void testKundenNummerShouldBeRead() {
        String kundenNummer = "M326586785";

        MeldungstypVZMType vzm = new VzmJaxbBuilder().withKundenNummer(kundenNummer).build();
        VerzoegerungsMeldung unmarshalled = vzmUnmarshaller.unmarshal(vzm);

        assertThat(unmarshalled.getKundenNummer(), equalTo(kundenNummer));
    }

    public void testVertragsNummerShouldBeRead() {
        String vertragsNummer = "124676789678";

        MeldungstypVZMType vzm = new VzmJaxbBuilder().withVertragsNummer(vertragsNummer).build();
        VerzoegerungsMeldung unmarshalled = vzmUnmarshaller.unmarshal(vzm);

        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void testMeldungsPositionenShouldBeRead() {
        MeldungstypVZMType vzm = new VzmJaxbBuilder().addMeldungsposition("Code", "Text").build();

        VerzoegerungsMeldung unmarshalled = vzmUnmarshaller.unmarshal(vzm);
        assertNotNull(unmarshalled);

        Set<MeldungsPosition> meldungsPositionen = unmarshalled.getMeldungsPositionen();
        assertNotNull(meldungsPositionen);
        assertThat(meldungsPositionen, hasSize(1));

        assertThat(meldungsPositionen.iterator().next().getMeldungsCode(), equalTo("Code"));
        assertThat(meldungsPositionen.iterator().next().getMeldungsText(), equalTo("Text"));
    }

    public void testVerzoegerungsTerminShouldBeRead() {
        LocalDate verzoegerungstermin = LocalDate.of(2012, 1, 12);

        MeldungstypVZMType vzm = new VzmJaxbBuilder().withVerzoegerungstermin(verzoegerungstermin).build();
        VerzoegerungsMeldung unmarshalled = vzmUnmarshaller.unmarshal(vzm);

        assertThat(unmarshalled.getVerzoegerungstermin(), equalTo(verzoegerungstermin));
    }

}
