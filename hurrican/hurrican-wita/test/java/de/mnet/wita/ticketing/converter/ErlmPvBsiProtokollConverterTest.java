/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 17:42:07
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.wita.message.builder.meldung.ErledigtMeldungPvBuilder;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;

@Test(groups = UNIT)
public class ErlmPvBsiProtokollConverterTest extends
        AbstractMeldungPvBsiProtokollConverterTest<ErledigtMeldungPv, ErlmPvBsiProtokollConverter> {

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new ErlmPvBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    @Override
    protected ErledigtMeldungPv createMeldung() {
        return new ErledigtMeldungPvBuilder().build();
    }

}
