/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.02.14
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

/**
 *
 */
@Test(groups = UNIT)
public class CheckRuemVaTerminBeforeKwtTest extends AbstractValidatorTest<CheckRuemVaTerminBeforeKwt.KundenwunschterminValidator> {

    @Override
    protected CheckRuemVaTerminBeforeKwt.KundenwunschterminValidator createTestling() {
        return new CheckRuemVaTerminBeforeKwt.KundenwunschterminValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.defaultMessage = "%s, %s";
    }

    @DataProvider
    public Object[][] validation() {
        // ruemVaTermin, kundenwunschtermin, valid
        return new Object[][] {
                { LocalDate.of(2014, 2, 7), LocalDate.of(2014, 2, 5), true },
                { LocalDate.of(2014, 2, 5), LocalDate.of(2014, 2, 5), true },
                { LocalDate.of(2014, 2, 4), LocalDate.of(2014, 2, 5), false },
                { LocalDate.of(2014, 2, 4), null, true },
                { null, LocalDate.of(2014, 2, 5), true },
                { null, null, true },
        };
    }

    @Test(dataProvider = "validation")
    public void testKundenwunschtermin(LocalDate ruemVaTermin, LocalDate kundenwunschtermin, boolean valid) throws Exception {
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withWechseltermin(ruemVaTermin)
                .withWbciGeschaeftsfall(
                        new WbciGeschaeftsfallKueMrnTestBuilder()
                                .withKundenwunschtermin(kundenwunschtermin)
                                .build()
                )
                .build();

        Assert.assertEquals(testling.isValid(ruemVa, contextMock), valid);
        assertErrorMessageSet(valid);
    }

}
