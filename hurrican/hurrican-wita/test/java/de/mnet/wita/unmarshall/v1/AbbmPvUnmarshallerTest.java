/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 09:59:30
 */
package de.mnet.wita.unmarshall.v1;

import static de.mnet.wita.BaseTest.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.notNullValue;

import org.testng.annotations.Test;

import de.mnet.wita.BaseTest;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.AbbmPvJaxbBuilder;
import de.mnet.wita.unmarshal.v1.AbbmPvUnmarshaller;

@Test(groups = UNIT)
public class AbbmPvUnmarshallerTest extends BaseTest {

    private final AbbmPvUnmarshaller underTest = new AbbmPvUnmarshaller();

    public void testKundenNummerShouldBeRead() {
        String kundenNummer = "M326586785";
        AbbruchMeldungPv unmarshalled = unmarshal(new AbbmPvJaxbBuilder().withKundenNummer(kundenNummer));
        assertThat(unmarshalled.getKundenNummer(), equalTo(kundenNummer));
    }

    public void testVertragsNummerShouldBeRead() {
        String vertragsNummer = "124676789678";
        AbbruchMeldungPv unmarshalled = unmarshal(new AbbmPvJaxbBuilder().withVertragsNummer(vertragsNummer));
        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void testLeitungShouldBeRead() {
        LeitungsBezeichnung lbz = new LeitungsBezeichnung("96W/82100/82100/1234", "821");
        Leitung leitung = new Leitung(lbz);
        leitung.setSchleifenWiderstand("100");

        AbbruchMeldungPv unmarshalled = unmarshal(new AbbmPvJaxbBuilder().withLeitung(leitung));
        Leitung achievedLeitung = unmarshalled.getLeitung();
        assertThat(achievedLeitung, notNullValue());
        assertThat(achievedLeitung.getLeitungsBezeichnung(), notNullValue());
        assertThat(achievedLeitung.getLeitungsBezeichnung().getLeitungsbezeichnungString(), equalTo(lbz
                .getLeitungsbezeichnungString()));
        assertThat(achievedLeitung.getSchleifenWiderstand(), equalTo(leitung.getSchleifenWiderstand()));
    }

    AbbruchMeldungPv unmarshal(AbbmPvJaxbBuilder builder) {
        return underTest.unmarshal(builder.build());
    }

}
