/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2011 14:34:04
 */
package de.augustakom.hurrican.validation.cc;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import javax.validation.*;
import org.hibernate.validator.internal.engine.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.PathImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.validation.cc.CBVorgangSubmitWithoutReturnValid.CBVorgangSubmitWithoutReturnValidator;

@Test(groups = BaseTest.UNIT)
public class CBVorgangSubmitWithoutReturnValidTest extends BaseTest {

    ConstraintValidatorContext context;

    @BeforeMethod
    public void setUp() {
        context = new ConstraintValidatorContextImpl(PathImpl.createRootPath(), null);
    }

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { CBVorgang.STATUS_SUBMITTED, null, true },
                { CBVorgang.STATUS_SUBMITTED, Boolean.FALSE, false },
                { CBVorgang.STATUS_SUBMITTED, Boolean.TRUE, false },
                { CBVorgang.STATUS_TRANSFERRED, null, true },
                { CBVorgang.STATUS_TRANSFERRED, Boolean.FALSE, false },
                { CBVorgang.STATUS_TRANSFERRED, Boolean.TRUE, false },
                { CBVorgang.STATUS_ANSWERED, Boolean.FALSE, true },
                { CBVorgang.STATUS_ANSWERED, Boolean.TRUE, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(Long state, Boolean returnOk, boolean expected) {
        CBVorgang cbVorgang = new CBVorgangBuilder().withStatus(state).withVorgabeMnet(null).withReturnOk(returnOk).setPersist(false).build();

        assertThat(new CBVorgangSubmitWithoutReturnValidator().isValid(cbVorgang, context), equalTo(expected));

    }

}
