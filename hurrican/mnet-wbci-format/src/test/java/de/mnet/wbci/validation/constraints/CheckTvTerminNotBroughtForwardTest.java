/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 08.07.2014 
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;

@Test(groups = UNIT)
public class CheckTvTerminNotBroughtForwardTest  extends AbstractValidatorTest<CheckTvTerminNotBroughtForward.TvTerminValidator> {

    @Override
    protected CheckTvTerminNotBroughtForward.TvTerminValidator createTestling() {
        return new CheckTvTerminNotBroughtForward.TvTerminValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.defaultMessage = "%s, %s";
    }

    @DataProvider(name = "isTerminVorziehungDP")
    public Object[][] isTerminVorziehungDP() {
        return new Object[][]{
                { LocalDate.now(), LocalDate.now(), true },
                { LocalDate.now(), LocalDate.now().plusDays(1), true },
                { LocalDate.now(), LocalDate.now().minusDays(1), false },
        };
    }

    @Test(dataProvider = "isTerminVorziehungDP")
    public void isTerminVorziehung(LocalDate wechseltermin, LocalDate tvDate, boolean valid) {
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder()
                .withVorabstimmungsIdRef("DEU.MNET.TH0000123")
                .withTvTermin(tvDate)
                .withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder().withWechseltermin(wechseltermin).build())
                .build();

        Assert.assertEquals(testling.isValid(tv, contextMock), valid);
        assertErrorMessageSet(valid);
    }

}