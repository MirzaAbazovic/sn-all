/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 09:32:56
 */
package de.mnet.wita.unmarshall.v1;

import static de.mnet.wita.BaseTest.*;
import static de.mnet.wita.message.auftrag.AktionsCode.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AktionscodeType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnlagentypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.DokumenttypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.UebertragungsverfahrenType;
import de.mnet.wita.BaseTest;
import de.mnet.wita.message.auftrag.Auftragsposition.ProduktBezeichner;
import de.mnet.wita.message.common.Anlage;
import de.mnet.wita.message.common.Anlagentyp;
import de.mnet.wita.message.common.Dateityp;
import de.mnet.wita.message.common.Uebertragungsverfahren;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1.EntmJaxbBuilder;
import de.mnet.wita.unmarshal.v1.EntmUnmarshaller;

@Test(groups = UNIT)
public class EntmUnmarshallerTest extends BaseTest {

    private final EntmUnmarshaller entmUnmarshaller = new EntmUnmarshaller();

    public void testExterneAuftragsnummerShouldBeRead() {
        String externeAuftragsnummer = "123456789";

        MeldungstypENTMType entm = (new EntmJaxbBuilder()).withExterneAuftragsnummer(externeAuftragsnummer).build();
        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);

        assertThat(unmarshalled.getExterneAuftragsnummer(), equalTo(externeAuftragsnummer));
    }

    public void testKundenNummerShouldBeRead() {
        String kundenNummer = "M326586785";

        MeldungstypENTMType entm = new EntmJaxbBuilder().withKundenNummer(kundenNummer).build();
        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);

        assertThat(unmarshalled.getKundenNummer(), equalTo(kundenNummer));
    }

    public void testVertragsNummerShouldBeRead() {
        String vertragsNummer = "124676789678";

        MeldungstypENTMType entm = new EntmJaxbBuilder().withVertragsNummer(vertragsNummer).build();
        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);

        assertThat(unmarshalled.getVertragsNummer(), equalTo(vertragsNummer));
    }

    public void testAktionscodeShouldBeRead() {
        MeldungstypENTMType entm = new EntmJaxbBuilder().addProduktposition(AktionscodeType.A,
                ProduktBezeichner.HVT_2H, UebertragungsverfahrenType.H_13).build();

        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);

        assertThat(unmarshalled.getProduktPositionen().get(0).getAktionsCode(), equalTo(AENDERUNG));
    }

    public void testUebertragungsVerfahrenShouldBeRead() {
        MeldungstypENTMType entm = new EntmJaxbBuilder().addProduktposition(AktionscodeType.A,
                ProduktBezeichner.HVT_2H, UebertragungsverfahrenType.H_13).build();

        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);

        assertThat(unmarshalled.getProduktPositionen().get(0).getUebertragungsVerfahren(),
                equalTo(Uebertragungsverfahren.H13));
    }

    public void testProduktBezeichnerShouldBeRead() {
        MeldungstypENTMType entm = new EntmJaxbBuilder().addProduktposition(AktionscodeType.A,
                ProduktBezeichner.HVT_2H, UebertragungsverfahrenType.H_13).build();

        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);

        assertThat(unmarshalled.getProduktPositionen().get(0).getProduktBezeichner(), equalTo(ProduktBezeichner.HVT_2H));
    }

    public void testAnlageShouldBeRead() {
        MeldungstypENTMType entm = new EntmJaxbBuilder().addAnlage("testdatei.pdf", DokumenttypType.APPLICATION_PDF,
                "Testdatei", "Inhalt der Testdatei",
                AnlagentypType.LETZTE_TELEKOM_RECHNUNG).build();
        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);
        assertNotNull(unmarshalled);

        Set<Anlage> anlagen = unmarshalled.getAnlagen();
        assertNotNull(anlagen);
        assertThat(anlagen, hasSize(1));

        Anlage anlage = anlagen.iterator().next();
        assertThat(anlage.getDateiname(), equalTo("testdatei.pdf"));
        assertThat(anlage.getDateityp(), equalTo(Dateityp.PDF));
        assertThat(anlage.getBeschreibung(), equalTo("Testdatei"));
        assertThat(new String(anlage.getInhalt()), equalTo("Inhalt der Testdatei"));
        assertThat(anlage.getAnlagentyp(), equalTo(Anlagentyp.LETZTE_TELEKOM_RECHNUNG));
    }

    public void testMeldungsPositionenShouldBeRead() {
        MeldungstypENTMType entm = new EntmJaxbBuilder().addMeldungsposition("Code", "Text").build();

        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);
        assertNotNull(unmarshalled);

        Set<MeldungsPosition> meldungsPositionen = unmarshalled.getMeldungsPositionen();
        assertNotNull(meldungsPositionen);
        assertThat(meldungsPositionen, hasSize(1));

        assertThat(meldungsPositionen.iterator().next().getMeldungsCode(), equalTo("Code"));
        assertThat(meldungsPositionen.iterator().next().getMeldungsText(), equalTo("Text"));
    }

    public void testEntgeltterminShouldBeRead() {
        LocalDate entgelttermin = LocalDate.of(2012, 1, 12);

        MeldungstypENTMType entm = new EntmJaxbBuilder().withEntgelttermin(entgelttermin).build();
        EntgeltMeldung unmarshalled = entmUnmarshaller.unmarshal(entm);

        assertThat(unmarshalled.getEntgelttermin(), equalTo(entgelttermin));
    }

}
