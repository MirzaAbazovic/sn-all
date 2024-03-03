/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 12:55:00
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
import de.mnet.wita.message.builder.meldung.TerminAnforderungsMeldungBuilder;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;
import de.mnet.wita.model.WitaCBVorgang;

@Test(groups = UNIT)
public class TamBsiProtokollConverterTest extends
        BaseMeldungBsiProtokollConverterTest<TerminAnforderungsMeldung, TamBsiProtokollConverter> {

    private static final Logger LOGGER = Logger.getLogger(TamBsiProtokollConverterTest.class);

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new TamBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void anbieterwechsel() throws Exception {
        TerminAnforderungsMeldung tam = createMeldung();
        setupCbVorgang(tam, Boolean.TRUE);

        AddCommunication protokollEintrag = protokollConverter.apply(tam);
        assert protokollEintrag != null;
        LOGGER.debug(protokollEintrag.getNotes());

        assertThat(protokollEintrag.getNotes(), containsString(WitaCBVorgang.ANBIETERWECHSEL_46TKG));
    }

    @Override
    protected TerminAnforderungsMeldung createMeldung() {
        return new TerminAnforderungsMeldungBuilder()
                .build();
    }

}


