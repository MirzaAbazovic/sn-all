/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 11:32:57
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungKundeBuilder;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.model.WitaCBVorgang;

@Test(groups = UNIT)
public class ErlmkBsiProtokollConverterTest extends
        BaseMeldungBsiProtokollConverterTest<ErledigtMeldungKunde, ErlmkBsiProtokollConverter> {

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new ErlmkBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void anbieterwechsel() throws Exception {
        ErledigtMeldungKunde erlmk = createMeldung();
        setupCbVorgang(erlmk, Boolean.TRUE);

        AddCommunication protokollEintrag = protokollConverter.apply(erlmk);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(), containsString(WitaCBVorgang.ANBIETERWECHSEL_46TKG));
    }

    @Override
    protected ErledigtMeldungKunde createMeldung() {
        return new ErledigtMeldungKundeBuilder().build();
    }

}
