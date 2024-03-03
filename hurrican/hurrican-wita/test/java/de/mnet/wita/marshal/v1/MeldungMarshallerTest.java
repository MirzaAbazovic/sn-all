/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 15:17:53
 */
package de.mnet.wita.marshal.v1;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import com.google.common.base.Function;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AbgebenderProviderType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypAbstractType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypERLMKType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypRUEMPVType;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungKundeBuilder;
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.attribute.AbgebenderProviderBuilder;
import de.mnet.wita.message.meldung.OutgoingMeldung;
import de.mnet.wita.message.meldung.RueckMeldungPv;

@Test(groups = UNIT)
public class MeldungMarshallerTest extends AbstractWitaMarshallerTest {

    @DataProvider
    public Object[][] marshalProvider() {
        return new Object[][] {
                {
                        new ErledigtMeldungKundeMarshaller(),
                        new ErledigtMeldungKundeBuilder().withKundennummer("42").build(),
                        erlmkAssertionFunction
                },

                // Positive RuemPv
                {
                        new RueckMeldungPvMarshaller(),
                        new RueckMeldungPvBuilder()
                                .withVorabstimmungsId("DEU.MNET.V123456789")
                                .withKundennummer("42").build(),
                        new RuemPvAssertionFunction(RuemPvAntwortCode.OK)
                },

                // Negative RuemPv
                {
                        new RueckMeldungPvMarshaller(),
                        new RueckMeldungPvBuilder()
                                .withVorabstimmungsId("DEU.MNET.V123456789")
                                .withKundennummer("42")
                                .withAbgebenderProvider(
                                        new AbgebenderProviderBuilder()
                                                .withZustimmungProviderWechsel(false)
                                                .withAntwortCode(RuemPvAntwortCode.SONSTIGES)
                                                .withAntwortText("Antworttext")
                                                .build())
                                .build(),
                        new RuemPvAssertionFunction(RuemPvAntwortCode.SONSTIGES)
                }
        };
    }

    @Test(dataProvider = "marshalProvider")
    public <T extends OutgoingMeldung, U extends MeldungstypAbstractType> void testMarshalling(MeldungMarshaller<T, U> marshaller, T meldung, Function<U, Void> assertionFunction) {
        U jaxbErlmk = marshaller.apply(meldung);

        assert jaxbErlmk != null;

        assertionFunction.apply(jaxbErlmk);
    }

    private static final Function<MeldungstypAbstractType, Void> erlmkAssertionFunction = new Function<MeldungstypAbstractType, Void>() {

        @Override
        public Void apply(MeldungstypAbstractType input) {
            MeldungstypERLMKType erlmkType = (MeldungstypERLMKType) input;
            assertEquals(erlmkType.getMeldungsattribute().getKundennummer(), "42");
            assertEquals(erlmkType.getMeldungspositionen().getPosition().get(0).getMeldungscode(), "0015");
            assertEquals(erlmkType.getMeldungspositionen().getPosition().get(0).getMeldungstext(),
                    "Endkunde hat die Bereitstellung der Leistung bestätigt. Anschluss störungsfrei in Betrieb");

            return null;
        }
    };

    class RuemPvAssertionFunction implements Function<MeldungstypAbstractType, Void> {

        private final RuemPvAntwortCode antwortCode;

        public RuemPvAssertionFunction(RuemPvAntwortCode antwortCode) {
            this.antwortCode = antwortCode;

        }

        @Override
        public Void apply(MeldungstypAbstractType input) {
            MeldungstypRUEMPVType type = (MeldungstypRUEMPVType) input;
            MeldungstypRUEMPVType.Meldungsattribute attribute = type.getMeldungsattribute();
            assertEquals(attribute.getKundennummer(), "42");
            assertEquals(attribute.getTAL().getVorabstimmungsID(), "DEU.MNET.V123456789");

            assertAbgebenderProviderCorrect(attribute.getAbgebenderProvider());
            assertLeitungCorrect((AngabenZurLeitungType) (attribute.getTAL().getLeitung()));
            assertAnschlussCorrect(attribute.getTAL().getAnschluss());

            MeldungspositionType position = type.getMeldungspositionen().getPosition().get(0);
            assertEquals(position.getMeldungscode(), RueckMeldungPv.MELDUNGSCODE);
            assertEquals(position.getMeldungstext(), RueckMeldungPv.MELDUNGSTEXT);

            return null;
        }

        /**
         * @see de.mnet.wita.message.builder.meldung.position.LeitungBuilder
         */
        private void assertLeitungCorrect(AngabenZurLeitungType leitung) {
            assertThat(leitung, notNullValue());

            assertThat(leitung.getLeitungsbezeichnung(), notNullValue());
            assertThat(leitung.getLeitungsbezeichnung().getLeitungsschluesselzahl(), notNullValue());
            assertThat(leitung.getLeitungsbezeichnung().getOnkzA(), notNullValue());
            assertThat(leitung.getLeitungsbezeichnung().getOnkzB(), notNullValue());
            assertThat(leitung.getLeitungsbezeichnung().getOrdnungsnummer(), notNullValue());

            // Schleifenwiderstand must not be set in WITA 4.0 ! (Even if the xsd allows it)
            assertThat(leitung.getSchleifenwiderstand(), nullValue());
        }

        private void assertAnschlussCorrect(AnschlussType anschlussType) {
            assertThat(anschlussType, notNullValue());
            assertThat(anschlussType.getONKZ(), notNullValue());
            assertThat(anschlussType.getRufnummer(), notNullValue());
        }

        private void assertAbgebenderProviderCorrect(AbgebenderProviderType abgebenderProvider) {
            assertThat(abgebenderProvider, notNullValue());

            if (antwortCode == RuemPvAntwortCode.OK) {
                assertThat(abgebenderProvider.getZustimmungProviderwechsel().name(), equalTo("J"));
                assertThat(abgebenderProvider.getAntwortcode(), nullValue());
                assertThat(abgebenderProvider.getAntworttext(), nullValue());
                assertThat(abgebenderProvider.getProvidername(), equalTo("M-net Telekommunikations GmbH"));
            }
            if (antwortCode == RuemPvAntwortCode.SONSTIGES) {
                assertThat(abgebenderProvider.getZustimmungProviderwechsel().name(), equalTo("N"));
                assertThat(abgebenderProvider.getAntwortcode(), equalTo(RuemPvAntwortCode.SONSTIGES.antwortCode));
                assertThat(abgebenderProvider.getAntworttext(), notNullValue());
                assertThat(abgebenderProvider.getProvidername(), equalTo("M-net Telekommunikations GmbH"));
            }
        }

    }


}