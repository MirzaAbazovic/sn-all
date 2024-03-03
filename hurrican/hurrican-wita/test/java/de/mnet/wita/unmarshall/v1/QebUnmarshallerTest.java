package de.mnet.wita.unmarshall.v1;

import static de.mnet.wita.BaseTest.*;
import static org.testng.Assert.*;

import com.google.common.collect.Iterables;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnsprechpartnerBaseType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeQEBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypQEBType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.position.AnsprechpartnerTelekom;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.AnsprechpartnerBaseTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.MeldungsattributeQEBTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.MeldungspositionQEBTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.MeldungspositionsattributeQEBTypeBuilder;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.MeldungstypQEBTypeBuilder;
import de.mnet.wita.unmarshal.v1.AnredeConverter;
import de.mnet.wita.unmarshal.v1.QebUnmarshaller;

@Test(groups = UNIT)
public class QebUnmarshallerTest extends BaseTest {

    public void testQEB() {
        MeldungsattributeQEBType meldungsattribute = new MeldungsattributeQEBTypeBuilder()
                .withExterneAuftragsnummer("12345")
                .withKundennummer("23456")
                .withKundennummerBesteller("34567")
                .build();
        AnsprechpartnerBaseType ansprechpartner = new AnsprechpartnerBaseTypeBuilder()
                .withAnrede("1")
                .withVorname("vorname")
                .withNachname("nachname")
                .withEmailadresse("email")
                .withTelefonnummer("telefon")
                .build();
        MeldungstypQEBType meldung =
                new MeldungstypQEBTypeBuilder()
                        .withMeldungsattribute(meldungsattribute)
                        .addPosition(
                                new MeldungspositionQEBTypeBuilder()
                                        .withMeldungscode("0000")
                                        .withMeldungstext("foobar")
                                        .withPositionsattribute(
                                                new MeldungspositionsattributeQEBTypeBuilder()
                                                        .withAnsprechpartner(ansprechpartner)
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        WitaMessage witaMessage = new QebUnmarshaller().apply(meldung);

        assertTrue(witaMessage instanceof QualifizierteEingangsBestaetigung);

        QualifizierteEingangsBestaetigung qeb = (QualifizierteEingangsBestaetigung) witaMessage;

        assertEquals(qeb.getMeldungsTyp(), MeldungsType.QEB);
        assertEquals(qeb.getExterneAuftragsnummer(), meldungsattribute.getExterneAuftragsnummer());
        assertEquals(qeb.getKundenNummer(), meldungsattribute.getKundennummer());
        assertEquals(qeb.getKundennummerBesteller(), meldungsattribute.getKundennummerBesteller());

        MeldungsPositionWithAnsprechpartner mp = Iterables.getOnlyElement(qeb.getMeldungsPositionen());

        assertEquals(mp.getMeldungsCode(), "0000");
        assertEquals(mp.getMeldungsText(), "foobar");

        AnsprechpartnerTelekom apt = mp.getAnsprechpartnerTelekom();

        assertEquals(apt.getAnrede(), AnredeConverter.toMwf(ansprechpartner.getAnrede()));
        assertEquals(apt.getVorname(), ansprechpartner.getVorname());
        assertEquals(apt.getNachname(), ansprechpartner.getNachname());
        assertEquals(apt.getEmailAdresse(), ansprechpartner.getEmailadresse());
        assertEquals(apt.getTelefonNummer(), ansprechpartner.getTelefonnummer());

    }

}
