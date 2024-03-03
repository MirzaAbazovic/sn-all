/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 16:42:03
 */
package de.mnet.wita.ticketing.converter;

import static de.augustakom.common.BaseTest.*;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.wita.message.builder.meldung.AbbruchMeldungPvBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;

@Test(groups = UNIT)
public class AbbmPvBsiProtokollConverterTest extends
        AbstractMeldungPvBsiProtokollConverterTest<AbbruchMeldungPv, AbbmPvBsiProtokollConverter> {

    @BeforeMethod
    public void setupMocks() {
        protokollConverter = new AbbmPvBsiProtokollConverter();
        MockitoAnnotations.initMocks(this);
    }

    @Override
    protected AbbruchMeldungPv createMeldung() {
        return new AbbruchMeldungPvBuilder().build();
    }
}
