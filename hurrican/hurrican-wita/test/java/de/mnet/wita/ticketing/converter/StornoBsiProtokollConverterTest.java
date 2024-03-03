/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 13:15:43
 */
package de.mnet.wita.ticketing.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.model.WitaCBVorgang;

public class StornoBsiProtokollConverterTest extends
        BaseRequestBsiProtokollConverterTest<Storno, StornoBsiProtokollConverter> {

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new StornoBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void anbieterwechsel() throws Exception {
        Storno storno = createRequest();
        setupCbVorgang(storno, Boolean.TRUE);

        AddCommunication protokollEintrag = protokollConverter.apply(storno);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(), containsString(WitaCBVorgang.ANBIETERWECHSEL_46TKG));
    }

    @Override
    protected Storno createRequest() {
        return new StornoBuilder(GeschaeftsfallTyp.BEREITSTELLUNG).buildValid();
    }

}


