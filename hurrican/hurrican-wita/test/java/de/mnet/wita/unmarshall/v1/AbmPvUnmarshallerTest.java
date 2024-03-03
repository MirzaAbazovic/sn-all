/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2011 08:29:47
 */
package de.mnet.wita.unmarshall.v1;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.*;

import java.time.*;
import org.testng.annotations.Test;

import de.mnet.wita.BaseTest;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.AbmPvJaxbBuilder;
import de.mnet.wita.unmarshal.v1.AbmPvUnmarshaller;

@Test(groups = BaseTest.UNIT)
public class AbmPvUnmarshallerTest extends BaseTest {

    private final AbmPvUnmarshaller underTest = new AbmPvUnmarshaller();

    public void testVertragsNummerShouldBeRead() {
        String vertragsNummer = "124676789678";
        AuftragsBestaetigungsMeldungPv unmarshalled = unmarshal(new AbmPvJaxbBuilder().withVertragsNummer(vertragsNummer));
        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void testUebernahmeDatumVerbindlichShouldBeRead() {
        LocalDate uebernahmeDatumVerbindlich = LocalDate.now().plusDays(7);
        AuftragsBestaetigungsMeldungPv unmarshalled = unmarshal(new AbmPvJaxbBuilder().withUebernahmeDatumVerbindlich(uebernahmeDatumVerbindlich));
        assertThat(unmarshalled.getAufnehmenderProvider().getUebernahmeDatumVerbindlich(), equalTo(uebernahmeDatumVerbindlich));
    }

    public void testAufnehmenderProviderShouldBeRead() {
        String aufnehmenderProvider = "Mnet Konkurrent";
        AuftragsBestaetigungsMeldungPv unmarshalled = unmarshal(new AbmPvJaxbBuilder().withAufnehmendenProvidernamen(aufnehmenderProvider));
        assertThat(unmarshalled.getAufnehmenderProvider().getProvidernameAufnehmend(), equalTo(aufnehmenderProvider));
    }

    public void testLeitungShouldBeRead() {
        LeitungsBezeichnung lbz = new LeitungsBezeichnung("96W/82100/82100/1234", "821");
        Leitung leitung = new Leitung(lbz);
        leitung.setSchleifenWiderstand("100");

        AuftragsBestaetigungsMeldungPv unmarshalled = unmarshal(new AbmPvJaxbBuilder().withLeitung(leitung));
        Leitung achievedLeitung = unmarshalled.getLeitung();
        assertNotNull(achievedLeitung);
        assertThat(achievedLeitung.getLeitungsBezeichnung(), notNullValue());
        assertThat(achievedLeitung.getLeitungsBezeichnung().getLeitungsbezeichnungString(), equalTo(lbz
                .getLeitungsbezeichnungString()));
        assertThat(achievedLeitung.getSchleifenWiderstand(), equalTo(leitung.getSchleifenWiderstand()));
    }

    AuftragsBestaetigungsMeldungPv unmarshal(AbmPvJaxbBuilder builder) {
        return (AuftragsBestaetigungsMeldungPv) underTest.unmarshal(builder.build());
    }

}
