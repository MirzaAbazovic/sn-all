/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 11:27:54
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.model.WitaCBVorgang;

@Test(groups = UNIT)
public class TerminVerschiebungBsiProtokollConverterTest extends
        BaseRequestBsiProtokollConverterTest<TerminVerschiebung, TerminVerschiebungBsiProtokollConverter> {

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new TerminVerschiebungBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void terminShouldBeSet() throws Exception {
        TerminVerschiebung tv = createRequest();
        setupCbVorgang(tv);

        AddCommunication protokollEintrag = protokollConverter.apply(tv);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(), containsString(tv.getTermin().toString()));
    }

    public void anbieterwechsel() throws Exception {
        TerminVerschiebung tv = createRequest();
        setupCbVorgang(tv, Boolean.TRUE);

        AddCommunication protokollEintrag = protokollConverter.apply(tv);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(), containsString(WitaCBVorgang.ANBIETERWECHSEL_46TKG));
    }

    @Override
    protected TerminVerschiebung createRequest() {
        return new TerminVerschiebungBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
    }
}


