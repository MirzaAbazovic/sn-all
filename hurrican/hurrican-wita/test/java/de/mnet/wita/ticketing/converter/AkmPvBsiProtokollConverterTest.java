/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.02.2012 17:10:39
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;

@Test(groups = UNIT)
public class AkmPvBsiProtokollConverterTest extends
        AbstractMeldungPvBsiProtokollConverterTest<AnkuendigungsMeldungPv, AkmPvBsiProtokollConverter> {

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new AkmPvBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void uebernahmeDatumGeplantShouldBeSet() {
        AnkuendigungsMeldungPv akmPv = createMeldung();
        setupUserTask(akmPv);
        AddCommunication protokollEintrag = protokollConverter.apply(akmPv);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(),
                containsString(akmPv.getAufnehmenderProvider().getUebernahmeDatumGeplant().toString()));
    }

    public void providerAufnehmendMustBeSet() {
        AnkuendigungsMeldungPv akmPv = createMeldung();
        setupUserTask(akmPv);
        AddCommunication protokollEintrag = protokollConverter.apply(akmPv);

        assert protokollEintrag != null;
        assertThat(protokollEintrag.getNotes(),
                containsString(akmPv.getAufnehmenderProvider().getProvidernameAufnehmend()));
    }

    @Override
    protected AnkuendigungsMeldungPv createMeldung() {
        return new AnkuendigungsMeldungPvBuilder().build();
    }


}
