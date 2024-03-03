package de.mnet.wita.unmarshall.v2;

import static de.mnet.wita.BaseTest.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypTAMType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.MeldungsattributeTAMTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.MeldungspositionTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2.MeldungstypTAMTypeBuilder;
import de.mnet.wita.unmarshal.v2.TamUnmarshallerV2;

@SuppressWarnings("Duplicates")
@Test(groups = UNIT)
public class TamUnmarshallerTest extends BaseTest {

    public void testTAM() throws Exception {
        MeldungstypTAMType meldung = new MeldungstypTAMTypeBuilder()
                .withMeldungsattribute(
                        new MeldungsattributeTAMTypeBuilder()
                                .withExterneAuftragsnummer("123456789")
                                .withKundennummer("234567890")
                                .withKundennummerBesteller("345678901")
                                .withVertragsnummer("TAL-12345")
                                .build()
                )
                .addPosition(
                        new MeldungspositionTypeBuilder()
                                .withMeldungscode("FOO")
                                .withMeldungstext("BAR")
                                .build()
                )
                .build();

        WitaMessage witaMessage = new TamUnmarshallerV2().apply(meldung);

        assertTrue(witaMessage instanceof TerminAnforderungsMeldung);

        TerminAnforderungsMeldung tam = (TerminAnforderungsMeldung) witaMessage;

        assertEquals(tam.getMeldungsTyp(), MeldungsType.TAM);
        assertEquals(tam.getExterneAuftragsnummer(), "123456789");
        assertEquals(tam.getKundenNummer(), "234567890");
        assertEquals(tam.getKundennummerBesteller(), "345678901");
        assertEquals(tam.getVertragsNummer(), "TAL-12345");
        assertEquals(tam.isMahnTam(), false);

        Set<MeldungsPosition> meldungsPositionen = tam.getMeldungsPositionen();

        assertEquals(meldungsPositionen.size(), 1);
        assertEquals(meldungsPositionen.iterator().next().getMeldungsCode(), "FOO");
        assertEquals(meldungsPositionen.iterator().next().getMeldungsText(), "BAR");
    }

}
