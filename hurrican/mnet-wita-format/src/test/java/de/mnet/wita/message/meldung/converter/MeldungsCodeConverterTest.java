/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.14
 */
package de.mnet.wita.message.meldung.converter;

import java.util.*;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wita.message.meldung.position.MeldungsPosition;

/**
 *
 */
public class MeldungsCodeConverterTest {

    @Test
    public void testMeldungsPositionToCodeString() throws Exception {
        Set<MeldungsPosition> testdata = new LinkedHashSet<>();
        testdata.add(getMockMeldungPositionWithMeldungsCode("OK"));
        testdata.add(getEmptyMockMeldungPosition());
        testdata.add(getMockMeldungPositionWithMeldungsCode("GOOD"));

        String codeString = MeldungsCodeConverter.meldungsPositionToCodeString(testdata);
        Assert.assertEquals(codeString, "OK,GOOD");
    }

    @Test
    public void testMeldungsPositionToTextString() throws Exception {
        Set<MeldungsPosition> testdata = new LinkedHashSet<>();
        testdata.add(getMockMeldungPositionWithMeldungsCode("OK"));
        testdata.add(getEmptyMockMeldungPosition());
        testdata.add(getMockMeldungPositionWithMeldungsCode("GOOD"));

        String codeString = MeldungsCodeConverter.meldungsPositionToTextString(testdata);
        Assert.assertEquals(codeString, "StandardText OK,StandardText GOOD");
    }

    private MeldungsPosition getMockMeldungPositionWithMeldungsCode(String meldungsCode) {
        MeldungsPosition meldungPositionMock = Mockito.mock(MeldungsPosition.class, Mockito.CALLS_REAL_METHODS);
        meldungPositionMock.setMeldungsCode(meldungsCode);
        meldungPositionMock.setMeldungsText("StandardText " + meldungsCode);
        return meldungPositionMock;
    }

    private MeldungsPosition getEmptyMockMeldungPosition() {
        MeldungsPosition meldungPositionMock = Mockito.mock(MeldungsPosition.class, Mockito.CALLS_REAL_METHODS);
        return meldungPositionMock;
    }
}
