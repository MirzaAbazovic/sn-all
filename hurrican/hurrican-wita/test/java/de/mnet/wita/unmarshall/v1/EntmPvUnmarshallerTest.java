/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 09:32:56
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

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMPVType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.message.meldung.EntgeltMeldungPv;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.EntmPvJaxbBuilder;
import de.mnet.wita.unmarshal.v1.EntmPvUnmarshaller;

@Test(groups = UNIT)
public class EntmPvUnmarshallerTest extends BaseTest {

    private final EntmPvUnmarshaller entmPvUnmarshaller = new EntmPvUnmarshaller();

    public void kundenNummer() {
        String kundenNummer = "M426586785";
        MeldungstypENTMPVType entmPv = new EntmPvJaxbBuilder().withKundenNummer(kundenNummer).build();
        EntgeltMeldungPv unmarshalled = entmPvUnmarshaller.unmarshal(entmPv);
        assertThat(unmarshalled.getKundenNummer(), equalTo(kundenNummer));
    }

    public void vertragsNummer() {
        String vertragsNummer = "924676789678";
        MeldungstypENTMPVType entmPv = new EntmPvJaxbBuilder().withVertragsNummer(vertragsNummer).build();
        EntgeltMeldungPv unmarshalled = entmPvUnmarshaller.unmarshal(entmPv);
        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void meldungsPositionen() {
        MeldungstypENTMPVType entmPv = new EntmPvJaxbBuilder().addMeldungsposition("foo", "bar").build();
        EntgeltMeldungPv unmarshalled = entmPvUnmarshaller.unmarshal(entmPv);

        assertNotNull(unmarshalled);
        Set<MeldungsPosition> meldungsPositionen = unmarshalled.getMeldungsPositionen();
        assertNotNull(meldungsPositionen);
        assertThat(meldungsPositionen, hasSize(1));
        assertThat(meldungsPositionen.iterator().next().getMeldungsCode(), equalTo("foo"));
        assertThat(meldungsPositionen.iterator().next().getMeldungsText(), equalTo("bar"));
    }

    public void entgelttermin() {
        LocalDate entgelttermin = LocalDate.of(2012, 1, 12);
        MeldungstypENTMPVType entmPv = new EntmPvJaxbBuilder().withEntgelttermin(entgelttermin).build();
        EntgeltMeldungPv unmarshalled = entmPvUnmarshaller.unmarshal(entmPv);
        assertThat(unmarshalled.getEntgelttermin(), equalTo(entgelttermin));
    }

}
