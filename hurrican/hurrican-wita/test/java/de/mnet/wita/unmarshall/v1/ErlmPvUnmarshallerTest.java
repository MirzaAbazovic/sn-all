package de.mnet.wita.unmarshall.v1;

import static de.mnet.wita.BaseTest.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.mnet.wita.BaseTest;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.ErlmPvJaxbBuilder;
import de.mnet.wita.unmarshal.v1.ErlmPvUnmarshaller;

@Test(groups = UNIT)
public class ErlmPvUnmarshallerTest extends BaseTest {

    private final ErlmPvUnmarshaller erlmPvUnmarshaller = new ErlmPvUnmarshaller();

    public void testKundenNummerShouldBeRead() {
        String kundenNummer = "M326586785";
        ErledigtMeldungPv unmarshalled = unmarshal(new ErlmPvJaxbBuilder().withKundenNummer(kundenNummer));
        assertThat(unmarshalled.getKundenNummer(), equalTo(kundenNummer));
    }

    public void testVertragsNummerShouldBeRead() {
        String vertragsNummer = "124676789878";
        ErledigtMeldungPv unmarshalled = unmarshal(new ErlmPvJaxbBuilder().withVertragsNummer(vertragsNummer));
        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void erledigungstermin() {
        assertNotNull(unmarshal(new ErlmPvJaxbBuilder()).getErledigungstermin());
    }

    public void testLeitungShouldBeRead() {
        LeitungsBezeichnung lbz = new LeitungsBezeichnung("96W/82100/82100/9876", "821");
        Leitung leitung = new Leitung(lbz);
        leitung.setSchleifenWiderstand("100");

        ErledigtMeldungPv unmarshalled = unmarshal(new ErlmPvJaxbBuilder().withLeitung(leitung));
        Leitung achievedLeitung = unmarshalled.getLeitung();
        assertThat(achievedLeitung.getSchleifenWiderstand(), equalTo(leitung.getSchleifenWiderstand()));
        assertThat(achievedLeitung, notNullValue());
        assertThat(achievedLeitung.getLeitungsBezeichnung(), notNullValue());
        assertThat(achievedLeitung.getLeitungsBezeichnung().getLeitungsbezeichnungString(),
                equalTo(lbz.getLeitungsbezeichnungString()));
    }

    public void testAnschlussShouldBeRead() {
        ErledigtMeldungPv unmarshalled = unmarshal(new ErlmPvJaxbBuilder().withAnschluss("89", "98765"));
        assertThat(unmarshalled.getAnschlussOnkz(), equalTo("89"));
        assertThat(unmarshalled.getAnschlussRufnummer(), equalTo("98765"));
    }

    private ErledigtMeldungPv unmarshal(ErlmPvJaxbBuilder builder) {
        return erlmPvUnmarshaller.unmarshal(builder.build());
    }

}
