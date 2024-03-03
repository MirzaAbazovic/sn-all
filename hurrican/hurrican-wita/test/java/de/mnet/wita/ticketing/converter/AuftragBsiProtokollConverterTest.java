/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 16:19:40
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.apache.log4j.Logger;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.model.WitaCBVorgang;

@Test(groups = UNIT)
public class AuftragBsiProtokollConverterTest extends
        BaseRequestBsiProtokollConverterTest<Auftrag, AuftragBsiProtokollConverter> {

    static final Logger LOGGER = Logger.getLogger(AuftragBsiProtokollConverterTest.class);

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new AuftragBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void geschaeftsfallShouldBeSet() throws Exception {
        Auftrag auftrag = createRequest();
        setupCbVorgang(auftrag);

        AddCommunication protokollEintrag = protokollConverter.apply(auftrag);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(),
                containsString(auftrag.getGeschaeftsfall().getGeschaeftsfallTyp().getDisplayName()));
    }

    public void anbieterwechsel() throws Exception {
        Auftrag auftrag = createRequest();
        setupCbVorgang(auftrag, Boolean.TRUE);

        AddCommunication protokollEintrag = protokollConverter.apply(auftrag);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(), containsString(WitaCBVorgang.ANBIETERWECHSEL_46TKG));
    }

    @Override
    protected Auftrag createRequest() {
        return new AuftragBuilder(BEREITSTELLUNG).buildValid();
    }

}
