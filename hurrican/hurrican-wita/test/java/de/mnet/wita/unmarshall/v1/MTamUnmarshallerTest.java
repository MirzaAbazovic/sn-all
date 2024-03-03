package de.mnet.wita.unmarshall.v1;

import static de.mnet.wita.BaseTest.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypMTAMType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.MeldungsattributeMTAMTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.MeldungspositionTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.MeldungstypMTAMTypeBuilder;
import de.mnet.wita.unmarshal.v1.MTamUnmarshaller;

@Test(groups = UNIT)
public class MTamUnmarshallerTest extends BaseTest {

    public void testMTAM() throws Exception {
        MeldungstypMTAMType meldung = new MeldungstypMTAMTypeBuilder()
                .withMeldungsattribute(
                        new MeldungsattributeMTAMTypeBuilder()
                                .withExterneAuftragsnummer("123456789")
                                .withKundennummer("234567890")
                                .withKundennummerBesteller("345678901")
                                .withVertragsnummer("TAL-12345")
                                .build())
                .addPosition(
                        new MeldungspositionTypeBuilder()
                                .withMeldungstext("BAR")
                                .withMeldungscode("FOO")
                                .build()
                )
                .build();

        WitaMessage witaMessage = new MTamUnmarshaller().apply(meldung);

        assertTrue(witaMessage instanceof TerminAnforderungsMeldung);

        TerminAnforderungsMeldung tam = (TerminAnforderungsMeldung) witaMessage;

        assertEquals(tam.getMeldungsTyp(), MeldungsType.TAM);
        assertEquals(tam.getExterneAuftragsnummer(), "123456789");
        assertEquals(tam.getKundenNummer(), "234567890");
        assertEquals(tam.getKundennummerBesteller(), "345678901");
        assertEquals(tam.getVertragsNummer(), "TAL-12345");
        assertEquals(tam.isMahnTam(), true);

        Set<MeldungsPosition> meldungsPositionen = tam.getMeldungsPositionen();

        assertEquals(meldungsPositionen.size(), 1);
        assertEquals(meldungsPositionen.iterator().next().getMeldungsCode(), "FOO");
        assertEquals(meldungsPositionen.iterator().next().getMeldungsText(), "BAR");
    }

}
