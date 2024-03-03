package de.mnet.wita.unmarshall.v2;

import static de.mnet.wita.BaseTest.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnlagentypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.DokumenttypType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.message.auftrag.Anrede;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Dateityp;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Kundenname;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.common.Personenname;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.attribute.AufnehmenderProvider;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.AkmPvJaxbBuilder;
import de.mnet.wita.unmarshal.v2.AkmPvUnmarshallerV2;

@SuppressWarnings("Duplicates")
@Test(groups = UNIT)
public class AkmPvUnmarshallerTest extends BaseTest {

    private final AkmPvUnmarshallerV2 underTest = new AkmPvUnmarshallerV2();

    public void testKundenNummerShouldBeRead() {
        String kundenNummer = "M326586785";
        AnkuendigungsMeldungPv unmarshalled = unmarshal(new AkmPvJaxbBuilder().withKundenNummer(kundenNummer));
        assertThat(unmarshalled.getKundenNummer(), equalTo(kundenNummer));
    }

    public void testVertragsNummerShouldBeRead() {
        String vertragsNummer = "124676789678";
        AnkuendigungsMeldungPv unmarshalled = unmarshal(new AkmPvJaxbBuilder().withVertragsNummer(vertragsNummer));
        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void testVorabstimmungsIdShouldBeRead() {
        String vorabstimmungsId = "DEU.MNET.V123456789";
        AnkuendigungsMeldungPv unmarshalled = unmarshal(new AkmPvJaxbBuilder().withVorabstimmungsId(vorabstimmungsId));
        assertThat(unmarshalled.getVorabstimmungsId(), equalTo(vorabstimmungsId));
    }

    public void testLeitungShouldBeRead() {
        LeitungsBezeichnung lbz = new LeitungsBezeichnung("96W/82100/82100/1234", "821");
        Leitung leitung = new Leitung(lbz);
        leitung.setSchleifenWiderstand("100");

        AnkuendigungsMeldungPv unmarshalled = unmarshal(new AkmPvJaxbBuilder().withLeitung(leitung));
        Leitung achievedLeitung = unmarshalled.getLeitung();
        assertThat(achievedLeitung, notNullValue());
        assertThat(achievedLeitung.getLeitungsBezeichnung(), notNullValue());
        assertThat(achievedLeitung.getLeitungsBezeichnung().getLeitungsbezeichnungString(),
                equalTo(lbz.getLeitungsbezeichnungString()));
        assertThat(achievedLeitung.getSchleifenWiderstand(), equalTo(leitung.getSchleifenWiderstand()));
    }

    public void testAnschlussShouldBeRead() {
        AnkuendigungsMeldungPv unmarshalled = unmarshal(new AkmPvJaxbBuilder().withAnschluss("89", "12345"));
        assertThat(unmarshalled.getAnschlussOnkz(), equalTo("89"));
        assertThat(unmarshalled.getAnschlussRufnummer(), equalTo("12345"));
    }

    public void testAufnehmenderProviderShouldBeRead() {
        AufnehmenderProvider aufnehmenderProvider = new AufnehmenderProvider();
        String providernameAufnehmend = "O2";
        LocalDate uebernahmeDatumGeplant = LocalDate.now();
        LocalDate antwortFrist = LocalDate.now();

        aufnehmenderProvider.setProvidernameAufnehmend(providernameAufnehmend);
        aufnehmenderProvider.setUebernahmeDatumGeplant(uebernahmeDatumGeplant);
        aufnehmenderProvider.setAntwortFrist(antwortFrist);

        AnkuendigungsMeldungPv unmarshalled = unmarshal(new AkmPvJaxbBuilder()
                .withAufnehmenderProvider(aufnehmenderProvider));
        assertThat(unmarshalled.getAufnehmenderProvider().getAntwortFrist(), equalTo(antwortFrist));
        assertThat(unmarshalled.getAufnehmenderProvider().getProvidernameAufnehmend(), equalTo(providernameAufnehmend));
        assertThat(unmarshalled.getAufnehmenderProvider().getUebernahmeDatumGeplant(), equalTo(uebernahmeDatumGeplant));
    }

    public void testFirmaEndkundeShouldBeRead() {
        Firmenname endkunde = new Firmenname();
        endkunde.setAnrede(Anrede.FIRMA);
        endkunde.setErsterTeil("Firma");
        endkunde.setZweiterTeil("abc");

        Kundenname achieved = testEndkundeShouldBeRead(endkunde);

        assertThat("Kein Firmenname", achieved instanceof Firmenname);
        assertThat(((Firmenname) achieved).getErsterTeil(), equalTo(endkunde.getErsterTeil()));
        assertThat(((Firmenname) achieved).getZweiterTeil(), equalTo(endkunde.getZweiterTeil()));
    }

    public void testPersonEndkundeShouldBeRead() {
        Personenname endkunde = new Personenname();
        endkunde.setAnrede(Anrede.HERR);
        endkunde.setVorname("Gerd");
        endkunde.setNachname("Mueller");

        Kundenname achieved = testEndkundeShouldBeRead(endkunde);

        assertThat("Kein Personenname", achieved instanceof Personenname);
        assertThat(((Personenname) achieved).getVorname(), equalTo(endkunde.getVorname()));
        assertThat(((Personenname) achieved).getNachname(), equalTo(endkunde.getNachname()));
    }

    private Kundenname testEndkundeShouldBeRead(Kundenname endkunde) {
        AnkuendigungsMeldungPv unmarshalled = unmarshal(new AkmPvJaxbBuilder().withEndkunde(endkunde));

        Kundenname achieved = unmarshalled.getEndkunde();
        assertThat(achieved, notNullValue());
        assertThat(achieved.getAnrede(), equalTo(endkunde.getAnrede()));
        return achieved;
    }

    public void anlagen() {
        AkmPvJaxbBuilder akmPv = new AkmPvJaxbBuilder().addAnlage("testdatei.pdf", DokumenttypType.APPLICATION_PDF,
                "Testdatei", "Inhalt der Testdatei", AnlagentypType.KUENDIGUNG_ABGEBENDER_PROVIDER);

        AnkuendigungsMeldungPv unmarshalled = unmarshal(akmPv);
        assertNotNull(unmarshalled);

        Set<Anlage> anlagen = unmarshalled.getAnlagen();
        assertNotNull(anlagen);
        assertThat(anlagen, hasSize(1));

        Anlage anlage = anlagen.iterator().next();
        assertThat(anlage.getDateiname(), equalTo("testdatei.pdf"));
        assertThat(anlage.getDateityp(), equalTo(Dateityp.PDF));
        assertThat(anlage.getBeschreibung(), equalTo("Testdatei"));
        assertThat(new String(anlage.getInhalt()), equalTo("Inhalt der Testdatei"));
        assertThat(anlage.getAnlagentyp(), equalTo(Anlagentyp.KUENDIGUNG_ABGEBENDER_PROVIDER));
    }

    private AnkuendigungsMeldungPv unmarshal(AkmPvJaxbBuilder builder) {
        return underTest.unmarshal(builder.build());
    }

}
