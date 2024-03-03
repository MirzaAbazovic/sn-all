/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.02.14
 */
package de.mnet.wbci.validation.constraints;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

/**
 *
 */
public class CheckErlmTvTerminTest extends AbstractValidatorTest<CheckErlmTvTermin.WechselterminValidator> {

    @Override
    protected CheckErlmTvTermin.WechselterminValidator createTestling() {
        return new CheckErlmTvTermin.WechselterminValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.defaultMessage = "%s, %s";
    }

    @DataProvider(name = "validation")
    public Object[][] validation() {
        // erlmTvTermin, kundenwunschtermin, valid
        return new Object[][] {
                { LocalDate.of(2014, 2, 7), LocalDate.of(2014, 2, 5), false },
                { LocalDate.of(2014, 2, 5), LocalDate.of(2014, 2, 5), true },
                { LocalDate.of(2014, 2, 4), LocalDate.of(2014, 2, 5), false },
        };
    }

    @Test(dataProvider = "validation")
    public void testKundenwunschtermin(LocalDate erlmTvTermin, LocalDate kundenwunschtermin, boolean valid) throws Exception {
        ErledigtmeldungTerminverschiebung erledigtmeldung = new ErledigtmeldungTestBuilder()
                .withWechseltermin(erlmTvTermin)
                .withWbciGeschaeftsfall(
                        new WbciGeschaeftsfallKueMrnTestBuilder()
                                .withKundenwunschtermin(kundenwunschtermin)
                                .build()
                )
                .buildForTv();
        Assert.assertEquals(testling.isValid(erledigtmeldung, contextMock), valid);
        assertErrorMessageSet(valid);
    }

}
