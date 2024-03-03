/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2011 14:34:04
 */
package de.augustakom.hurrican.validation.cc;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import javax.validation.*;
import org.mockito.Mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.validation.cc.CBVorgangSubmitDateValid.CBVorgangSubmitDateValidator;

@Test(groups = BaseTest.UNIT)
public class CBVorgangSubmitDateValidTest extends BaseTest {

    @Mock
    ConstraintValidatorContext context;

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { CBVorgang.STATUS_SUBMITTED, null, false },
                { CBVorgang.STATUS_SUBMITTED, new Date(), true },
                { CBVorgang.STATUS_TRANSFERRED, null, false },
                { CBVorgang.STATUS_TRANSFERRED, new Date(), true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(Long state, Date submitDate, boolean expected) {
        CBVorgang cbVorgang = new CBVorgangBuilder().withStatus(state).withSubmittedAt(submitDate).setPersist(false).build();

        assertThat(new CBVorgangSubmitDateValidator().isValid(cbVorgang, context), equalTo(expected));

    }

}
