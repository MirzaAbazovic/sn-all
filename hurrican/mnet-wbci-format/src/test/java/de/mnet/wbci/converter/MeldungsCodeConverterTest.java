/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.wbci.converter;

import static de.mnet.wbci.TestGroups.*;
import static de.mnet.wbci.model.MeldungsCode.*;

import java.util.*;
import org.mockito.Mockito;
import org.springframework.util.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.mnet.wbci.model.MeldungPosition;
import de.mnet.wbci.model.MeldungsCode;

/**
 *
 */
@Test(groups = UNIT)
public class MeldungsCodeConverterTest {

    @Test
    public void testMeldungsPositionToCodeString() throws Exception {
        Set<MeldungPosition> testdata = new HashSet<>();
        testdata.add(getMockMeldungPositionWithMeldungsCode(ZWA));
        testdata.add(getMockMeldungPositionWithMeldungsCode(ADAORT));

        List<String> stringCodes = Arrays.asList(MeldungsCodeConverter.meldungsPositionToCodeString(testdata).split(MeldungsCodeConverter.SEPARATOR));
        Assert.assertTrue(stringCodes.contains("ZWA"));
        Assert.assertTrue(stringCodes.contains("ADAORT"));
        Assert.assertEquals(stringCodes.size(), 2);
    }

    @Test
    public void testIsMeldungscodeInCodeString() throws Exception {
        Set<MeldungPosition> testdata = new HashSet<>();
        testdata.add(getMockMeldungPositionWithMeldungsCode(ZWA));
        testdata.add(getMockMeldungPositionWithMeldungsCode(ADAORT));

        // test with null code-string
        String codeString = null;
        Assert.assertFalse(MeldungsCodeConverter.isMeldungscodeInCodeString(codeString, ZWA));

        // test with empty code-string
        codeString = "";
        Assert.assertFalse(MeldungsCodeConverter.isMeldungscodeInCodeString(codeString, ZWA));

        // test for presence of single code in code-string
        codeString = ZWA.name();
        Assert.assertTrue(MeldungsCodeConverter.isMeldungscodeInCodeString(codeString, ZWA));
        Assert.assertFalse(MeldungsCodeConverter.isMeldungscodeInCodeString(codeString, ADAORT));

        // test for presence of multiple codes in code-string
        codeString = StringUtils.arrayToDelimitedString(new String[] { ZWA.name(), ADAORT.name() }, MeldungsCodeConverter.SEPARATOR);
        Assert.assertTrue(MeldungsCodeConverter.isMeldungscodeInCodeString(codeString, ZWA, ADAORT));
        Assert.assertTrue(MeldungsCodeConverter.isMeldungscodeInCodeString(codeString, ADAORT, ADAHSNR));
        Assert.assertFalse(MeldungsCodeConverter.isMeldungscodeInCodeString(codeString, ADAHSNR, NAT));
    }

    @Test
    public void testMeldungsPositionToTextString() throws Exception {
        Set<MeldungPosition> testdata = new HashSet<>();
        testdata.add(getMockMeldungPositionWithMeldungsCode(ZWA));
        testdata.add(getMockMeldungPositionWithMeldungsCode(ADAORT));

        List<String> stringCodes = Arrays.asList(MeldungsCodeConverter.meldungsPositionToTextString(testdata).split(MeldungsCodeConverter.SEPARATOR));
        Assert.assertTrue(stringCodes.contains(ZWA.getStandardText()));
        Assert.assertTrue(stringCodes.contains(ADAORT.getStandardText()));
        Assert.assertEquals(stringCodes.size(), 2);
    }

    @Test
    public void retrieveMeldungsCodes() throws Exception {
        Set<MeldungsCode> meldungsCodes = MeldungsCodeConverter.retrieveMeldungsCodes("ADFORT" + MeldungsCodeConverter.SEPARATOR + "ADFPLZ");
        Assert.assertEquals(meldungsCodes.size(), 2);
        Assert.assertTrue(meldungsCodes.contains(ADFORT));
        Assert.assertTrue(meldungsCodes.contains(ADFPLZ));
    }

    private MeldungPosition getMockMeldungPositionWithMeldungsCode(MeldungsCode meldungsCode) {
        MeldungPosition meldungPositionMock = Mockito.mock(MeldungPosition.class, Mockito.CALLS_REAL_METHODS);
        meldungPositionMock.setMeldungsCode(meldungsCode);
        meldungPositionMock.setMeldungsText(meldungsCode.getStandardText());
        return meldungPositionMock;
    }

}
