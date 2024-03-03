/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.13
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

@Test(groups = UNIT)
public class CheckAbbmTerminTest extends AbstractValidatorTest<CheckAbbmTermin.WechselterminValidator> {

    @Override
    protected CheckAbbmTermin.WechselterminValidator createTestling() {
        return new CheckAbbmTermin.WechselterminValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.defaultMessage = "%s, %s";
    }

    @DataProvider(name = "validation")
    public Object[][] validation() {
        // abbmTermin, wechseltermin, valid
        return new Object[][] {
                { LocalDate.of(2014, 2, 7), LocalDate.of(2014, 2, 5), false },
                { LocalDate.of(2014, 2, 5), LocalDate.of(2014, 2, 5), true },
                { LocalDate.of(2014, 2, 4), LocalDate.of(2014, 2, 5), false },
        };
    }

    @Test(dataProvider = "validation")
    public void testKundenwunschtermin(LocalDate abbmTermin, LocalDate wechseltermin, boolean valid) throws Exception {
        Abbruchmeldung abbruchmeldung = new AbbruchmeldungTestBuilder()
                .withWechseltermin(abbmTermin)
                .withWbciGeschaeftsfall(
                        new WbciGeschaeftsfallKueMrnTestBuilder()
                                .withWechseltermin(wechseltermin)
                                .build()
                )
                .build();
        Assert.assertEquals(testling.isValid(abbruchmeldung, contextMock), valid);
        assertErrorMessageSet(valid);
    }

}
