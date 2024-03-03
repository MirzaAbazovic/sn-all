/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 14:40:53
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
import de.mnet.wita.message.builder.meldung.RueckMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.attribute.AbgebenderProviderBuilder;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.message.meldung.position.AbgebenderProvider;

@Test(groups = UNIT)
public class RuemPvBsiProtokollConverterTest extends
        AbstractMeldungPvBsiProtokollConverterTest<RueckMeldungPv, RuemPvBsiProtokollConverter> {

    private static final Logger LOGGER = Logger.getLogger(RuemPvBsiProtokollConverterTest.class);

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new RuemPvBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    public void negativeRuemPvShouldHaveDifferentText() {
        RueckMeldungPv ruemPvPos = createMeldung();
        setupUserTask(ruemPvPos);

        AddCommunication protokollEintragPos = protokollConverter.apply(ruemPvPos);
        assert protokollEintragPos != null;
        LOGGER.debug(protokollEintragPos.getNotes());

        assertThat(protokollEintragPos.getNotes(), containsString("Positive Rückmeldung"));

        AbgebenderProvider abgebenderProviderNoZustimmung =
                new AbgebenderProviderBuilder().withZustimmungProviderWechsel(false).build();
        RueckMeldungPv ruemPvNeg = new RueckMeldungPvBuilder().withAbgebenderProvider(abgebenderProviderNoZustimmung).build();
        setupUserTask(ruemPvNeg);

        AddCommunication protokollEintragNeg = protokollConverter.apply(ruemPvNeg);
        assert protokollEintragNeg != null;
        LOGGER.debug(protokollEintragNeg.getNotes());

        assertThat(protokollEintragNeg.getNotes(), containsString("Negative Rückmeldung"));
    }

    @Override
    protected RueckMeldungPv createMeldung() {
        AbgebenderProvider abgebenderProvider =
                new AbgebenderProviderBuilder().withZustimmungProviderWechsel(true).build();
        return new RueckMeldungPvBuilder().withAbgebenderProvider(abgebenderProvider).build();
    }

}
