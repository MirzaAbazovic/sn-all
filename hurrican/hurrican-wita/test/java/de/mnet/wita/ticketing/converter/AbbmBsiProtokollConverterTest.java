/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 11:45:01
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.apache.log4j.Logger;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;


@Test(groups = UNIT)
public class AbbmBsiProtokollConverterTest extends
        BaseMeldungBsiProtokollConverterTest<AbbruchMeldung, AbbmBsiProtokollConverter> {

    private static final Logger LOGGER = Logger.getLogger(AbbmBsiProtokollConverterTest.class);

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new AbbmBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void aenderungsKennzeichenTvShouldChangeText() throws Exception {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder()
                .withAenderungsKennzeichen(AenderungsKennzeichen.TERMINVERSCHIEBUNG)
                .build();
        setupCbVorgang(abbm);

        AddCommunication protokollEintrag = protokollConverter.apply(abbm);
        LOGGER.debug(protokollEintrag.getNotes());

        assertThat(protokollEintrag.getNotes(), containsString("Terminverschiebung wurde abgelehnt"));
    }

    public void aenderungsKennzeichenStornoShouldChangeText() throws Exception {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder()
                .withAenderungsKennzeichen(AenderungsKennzeichen.STORNO)
                .build();
        setupCbVorgang(abbm);

        AddCommunication protokollEintrag = protokollConverter.apply(abbm);
        LOGGER.debug(protokollEintrag.getNotes());

        assertThat(protokollEintrag.getNotes(), containsString("Stornierung wurde abgelehnt"));
    }

    public void anbieterwechsel() throws Exception {
        AbbruchMeldung abbm = new AbbruchMeldungBuilder().withAenderungsKennzeichen(AenderungsKennzeichen.STORNO)
                .build();
        setupCbVorgang(abbm, Boolean.TRUE);

        AddCommunication protokollEintrag = protokollConverter.apply(abbm);
        assert protokollEintrag != null;
        LOGGER.debug(protokollEintrag.getNotes());

        assertThat(protokollEintrag.getNotes(), containsString(WitaCBVorgang.ANBIETERWECHSEL_46TKG));
    }

    @Override
    protected AbbruchMeldung createMeldung() {
        return new AbbruchMeldungBuilder().build();
    }

}
