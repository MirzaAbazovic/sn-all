/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 06.06.2014 
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.MeldungsCode.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.MeldungPositionAbbruchmeldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.builder.AbbruchmeldungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungBuilder;

@Test(groups = UNIT)
public class CheckOutgoingAbbmMeldungsCodesTest extends AbstractValidatorTest<CheckOutgoingAbbmMeldungsCodes.AbbmValidator> {

    @Override
    protected CheckOutgoingAbbmMeldungsCodes.AbbmValidator createTestling() {
        return new CheckOutgoingAbbmMeldungsCodes.AbbmValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @DataProvider
    public Object[][] checkOutgoingAbbmMeldungsCodesDataProvider() {
        return new Object[][] {
                { true, IOType.OUT, Arrays.asList(ADFSTR, NAT) },
                { true, IOType.OUT, Arrays.asList(NAT) },
                { true, IOType.OUT, Arrays.asList() },
                { true, IOType.IN, Arrays.asList(ADFSTR) },
                { true, IOType.IN, Arrays.asList(ADFSTR, ADFPLZ) },
                { false, IOType.OUT, Arrays.asList(ADFSTR) },
                { false, IOType.OUT, Arrays.asList(ADFSTR, ADFPLZ) },
        };
    }

    @Test(dataProvider = "checkOutgoingAbbmMeldungsCodesDataProvider")
    public void testCheckOutgoingAbbmMeldungsCodes(boolean valid, IOType ioType, List<MeldungsCode> meldungsCodes) throws Exception {
        testling.defaultMsg = "%s";

        Abbruchmeldung abbm = create(ioType, meldungsCodes);

        assertEquals(testling.isValid(abbm, contextMock), valid);
        assertErrorMessageSet(valid);

        //check for incoming messages
        reset(contextMock);
        abbm.setIoType(IOType.IN);
        assertTrue(testling.isValid(abbm, contextMock));
        assertErrorMessageSet(0);
    }

    private static Abbruchmeldung create(IOType ioType, List<MeldungsCode> meldungsCodes) {
        AbbruchmeldungBuilder abbmBuilder = new AbbruchmeldungBuilder();
        abbmBuilder.withIoType(ioType);
        for (MeldungsCode code : meldungsCodes) {
            MeldungPositionAbbruchmeldung mp = new MeldungPositionAbbruchmeldungBuilder()
                    .withMeldungsCode(code)
                    .build();
            abbmBuilder.addMeldungPosition(mp);
        }
        return abbmBuilder.build();
    }

}