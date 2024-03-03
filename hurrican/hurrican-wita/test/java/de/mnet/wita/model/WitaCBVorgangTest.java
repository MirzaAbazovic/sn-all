/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2011 11:12:51
 */
package de.mnet.wita.model;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import javax.validation.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.model.WitaCBVorgang.AbmState;

@Test(groups = UNIT)
public class WitaCBVorgangTest extends BaseTest {

    protected Validator validator;

    @BeforeMethod
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public void witaCBVorgangShouldValidate() {
        WitaCBVorgang cbVorgang = buildValidWitaCBVorgang();
        Set<ConstraintViolation<WitaCBVorgang>> violations = validator.validate(cbVorgang);
        assertThat(violations, hasSize(0));
    }

    public void auftragsKlammerShouldBeChecked() {
        WitaCBVorgang cbVorgang = buildValidWitaCBVorgang();
        cbVorgang.setAuftragsKlammer(12345678901L);
        Set<ConstraintViolation<WitaCBVorgang>> violations = validator.validate(cbVorgang);
        assertThat(violations, hasSize(1));
    }

    @DataProvider
    public Object[][] nonValidStrings() {
        return new Object[][] {
                { "ABCD123456ABCD123456ABCD123456F", 1 },
                { "ABCD123456ABCD123456ABCD123456", 0 },
        };
    }

    @Test(dataProvider = "nonValidStrings")
    public void projektKennerShouldBeChecked(String nonValidString, int numValidations) {
        WitaCBVorgang cbVorgang = buildValidWitaCBVorgang();
        cbVorgang.setProjektKenner(nonValidString);
        Set<ConstraintViolation<WitaCBVorgang>> violations = validator.validate(cbVorgang);
        assertThat(violations, hasSize(numValidations));
    }

    private WitaCBVorgang buildValidWitaCBVorgang() {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().build();
        cbVorgang.setCbId(13L);
        cbVorgang.setAuftragId(13L);
        return cbVorgang;
    }

    public void closeShouldRemoveSecondAbmReceivedFlag() {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().setPersist(false).build();
        cbVorgang.answer(true);
        cbVorgang.setAbmState(AbmState.SECOND_ABM);
        cbVorgang.close();
        assertFalse(cbVorgang.getSecondAbmReceived().booleanValue(), "SecondAbmReceived should be false");
    }
}


