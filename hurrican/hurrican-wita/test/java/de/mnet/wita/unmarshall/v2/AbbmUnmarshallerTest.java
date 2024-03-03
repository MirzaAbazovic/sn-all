package de.mnet.wita.unmarshall.v2;

import static de.mnet.wita.BaseTest.*;
import static org.testng.Assert.*;

import java.util.*;
import org.springframework.oxm.UnmarshallingFailureException;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortKorrekturType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.auftrag.StandortKundeKorrektur;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.GebaeudeteilTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.MeldungsattributeABBMTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.MeldungspositionABBMTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.MeldungspositionsattributeABBMTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.MeldungstypABBMTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.OrtTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.StandortKorrekturTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.StrasseTypeBuilder;
import de.mnet.wita.unmarshal.v2.AbbmUnmarshallerV2;

@SuppressWarnings("Duplicates")
@Test(groups = UNIT)
public class AbbmUnmarshallerTest extends BaseTest {

    private final AbbmUnmarshallerV2 unmarshaller = new AbbmUnmarshallerV2();

    public void testABBM() throws Exception {
        assertUnmarshaledAbbm(unmarshaller.apply(createJaxbMeldungstypABBMType(null)));
    }

    public void testAbbmWithMeldungspositionattributeWithoutAlternativprodukt() throws Exception {
        assertUnmarshaledAbbm(unmarshaller.apply(createJaxbMeldungstypABBMType(null)));
    }

    public void testAbbmWithValidAlternativprodukt() throws Exception {
        MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute positionsattribute =
                new MeldungspositionsattributeABBMTypeBuilder()
                        .withAlternativprodukt("TAL; CuDA 2 Draht mit ZwR")
                        .build();
        assertUnmarshaledAbbm(unmarshaller.apply(createJaxbMeldungstypABBMType(positionsattribute)));
    }

    @Test(expectedExceptions = UnmarshallingFailureException.class)
    public void testAbbmExceptionBecauseAlternativproduktIsNotTal() throws Exception {
        MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute positionsattribute =
                new MeldungspositionsattributeABBMTypeBuilder()
                        .withAlternativprodukt("ADSL SA 6000 (3072)")
                        .build();
        unmarshaller.apply(createJaxbMeldungstypABBMType(positionsattribute));
    }

    private MeldungstypABBMType createJaxbMeldungstypABBMType(MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute attribute) {
        return new MeldungstypABBMTypeBuilder()
                .withMeldungsattribute(new MeldungsattributeABBMTypeBuilder()
                        .withExterneAuftragsnummer("123456789")
                        .withKundennummer("234567890")
                        .withKundennummerBesteller("345678901")
                        .withVertragsnummer("TAL-12345")
                        .build()
                )
                .addPosition(new MeldungspositionABBMTypeBuilder()
                        .withMeldungscode("FOO")
                        .withMeldungstext("BAR")
                        .withPositionsattribute(attribute)
                        .build()
                )
                .build();
    }

    private void assertUnmarshaledAbbm(WitaMessage witaMessage) {
        assertTrue(witaMessage instanceof AbbruchMeldung);

        AbbruchMeldung abbm = (AbbruchMeldung) witaMessage;

        assertEquals(abbm.getMeldungsTyp(), MeldungsType.ABBM);
        assertEquals(abbm.getExterneAuftragsnummer(), "123456789");
        assertEquals(abbm.getKundenNummer(), "234567890");
        assertEquals(abbm.getKundennummerBesteller(), "345678901");
        assertEquals(abbm.getVertragsNummer(), "TAL-12345");

        Set<MeldungsPosition> meldungsPositionen = abbm.getMeldungsPositionen();

        assertEquals(meldungsPositionen.size(), 1);
        assertEquals(meldungsPositionen.iterator().next().getMeldungsCode(), "FOO");
        assertEquals(meldungsPositionen.iterator().next().getMeldungsText(), "BAR");
    }

    public void unmarshalStandortKundeKorrekturNull() {
        assertNull(invokeMethod(AbbmUnmarshallerV2.class, "unmarshalStandortKundeKorrektur",
                new Class[] { StandortKorrekturType.class }, unmarshaller, new Object[] { null }));
    }

    public void unmarshalStandortKundeKorrektur() {
        StandortKorrekturType standortKorrekturType =
                new StandortKorrekturTypeBuilder()
                        .withGebaeudeteil(new GebaeudeteilTypeBuilder()
                                .withGebaeudeteilName("foo")
                                .withGebaeudeteilZusatz("foo zusatz")
                                .build()
                        )
                        .withStrasse(new StrasseTypeBuilder()
                                .withHausnummer("12")
                                .withHausnummernZusatz("a")
                                .withStrassenname("foo strasse")
                                .build()
                        )
                        .withOrt(new OrtTypeBuilder()
                                .withOrtsname("ortsname")
                                .withOrtsteil("ortsteil")
                                .build()
                        )
                        .withLand("Schweiz")
                        .build();

        StandortKundeKorrektur standortKundeKorrektur = (StandortKundeKorrektur) invokeMethod(AbbmUnmarshallerV2.class,
                "unmarshalStandortKundeKorrektur", new Class[] { StandortKorrekturType.class }, unmarshaller, standortKorrekturType);
        assertEquals(standortKundeKorrektur.getGebaeudeteilName(), "foo");
        assertEquals(standortKundeKorrektur.getGebaeudeteilZusatz(), "foo zusatz");
        assertEquals(standortKundeKorrektur.getHausnummer(), "12");
        assertEquals(standortKundeKorrektur.getHausnummernZusatz(), "a");
        assertEquals(standortKundeKorrektur.getStrassenname(), "foo strasse");
        assertEquals(standortKundeKorrektur.getOrtsname(), "ortsname");
        assertEquals(standortKundeKorrektur.getOrtsteil(), "ortsteil");
        assertEquals(standortKundeKorrektur.getLand(), "Schweiz");
    }
}
