/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.13
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.MeldungPositionRueckmeldungVa;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaTestBuilder;
import de.mnet.wbci.model.builder.StandortTestBuilder;
import de.mnet.wbci.model.builder.StrasseTestBuilder;

@Test(groups = UNIT)
public class CheckRuemVaMeldungscodeADATest extends
        AbstractValidatorTest<CheckRuemVaMeldungscodeADA.MeldungscodesValidator> {

    @Override
    protected CheckRuemVaMeldungscodeADA.MeldungscodesValidator createTestling() {
        return new CheckRuemVaMeldungscodeADA.MeldungscodesValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.defaultMessage = "%s%s%s";
    }

    @DataProvider
    public Object[][] validationADA() {
        return new Object[][] {
                //@formatter:off
                {MeldungsCode.ADAORT, new StandortTestBuilder().withOrt("München").build(),GeschaeftsfallTyp.VA_KUE_MRN  , true},
                {MeldungsCode.ADAORT, new StandortTestBuilder().withOrt("München").build(),GeschaeftsfallTyp.VA_KUE_ORN  , true},
                {MeldungsCode.ADAORT, new StandortTestBuilder().withOrt("München").build(),GeschaeftsfallTyp.VA_RRNP  , true},
                {MeldungsCode.ADAORT, new StandortTestBuilder().withPostleitzahl("81673").build(),GeschaeftsfallTyp.VA_KUE_MRN , false},
                {MeldungsCode.ADAORT, new StandortTestBuilder().withPostleitzahl("81673").build(),GeschaeftsfallTyp.VA_KUE_ORN , false},
                {MeldungsCode.ADAORT, new StandortTestBuilder().withPostleitzahl("81673").build(),GeschaeftsfallTyp.VA_RRNP , false},
                {MeldungsCode.ADAPLZ, new StandortTestBuilder().withOrt("München").build(),GeschaeftsfallTyp.VA_KUE_MRN  , false},
                {MeldungsCode.ADAPLZ, new StandortTestBuilder().withOrt("München").build(),GeschaeftsfallTyp.VA_KUE_ORN  , false},
                {MeldungsCode.ADAPLZ, new StandortTestBuilder().withOrt("München").build(),GeschaeftsfallTyp.VA_RRNP  , false},
                {MeldungsCode.ADAPLZ, new StandortTestBuilder().withPostleitzahl("81673").build(),GeschaeftsfallTyp.VA_KUE_MRN , true},
                {MeldungsCode.ADAPLZ, new StandortTestBuilder().withPostleitzahl("81673").build(),GeschaeftsfallTyp.VA_KUE_ORN , true},
                {MeldungsCode.ADAPLZ, new StandortTestBuilder().withPostleitzahl("81673").build(),GeschaeftsfallTyp.VA_RRNP , true},
                {MeldungsCode.ADASTR, new StandortTestBuilder().withStrasse(new StrasseTestBuilder().withStrassenname("Strasse").build()).build(),GeschaeftsfallTyp.VA_KUE_MRN  , true},
                {MeldungsCode.ADASTR, new StandortTestBuilder().withStrasse(new StrasseTestBuilder().withStrassenname("Strasse").build()).build(),GeschaeftsfallTyp.VA_KUE_ORN  , true},
                {MeldungsCode.ADASTR, new StandortTestBuilder().withStrasse(new StrasseTestBuilder().withStrassenname("Strasse").build()).build(),GeschaeftsfallTyp.VA_RRNP  , true},
                {MeldungsCode.ADASTR, new StandortTestBuilder().withStrasse(new StrasseTestBuilder().withHausnummer("4").build()).build(),GeschaeftsfallTyp.VA_KUE_MRN  , false},
                {MeldungsCode.ADASTR, new StandortTestBuilder().withStrasse(new StrasseTestBuilder().withHausnummer("4").build()).build(),GeschaeftsfallTyp.VA_KUE_ORN  , false},
                {MeldungsCode.ADASTR, new StandortTestBuilder().withStrasse(new StrasseTestBuilder().withHausnummer("4").build()).build(),GeschaeftsfallTyp.VA_RRNP  , false},
                //@formatter:on
        };
    }

    /**
     * a mor detailed Test is not necessary here, see {@link de.mnet.wbci.model.helper.StandortHelperTest}.
     */
    @Test(dataProvider = "validationADA")
    public void testCheckStandort(MeldungsCode code, Standort standort, GeschaeftsfallTyp gfTyp, boolean valid)
            throws Exception {
        MeldungPositionRueckmeldungVa ruemVaPos = new MeldungPositionRueckmeldungVaTestBuilder()
                .withMeldungsCode(code)
                .withStandortAbweichend(standort).build();

        Assert.assertEquals(testling.isValid(ruemVaPos, contextMock), valid);
        assertErrorMessageSet(valid);
    }
}
