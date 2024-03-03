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
import org.hibernate.validator.internal.engine.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.PathImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.CBVorgangBuilder;
import de.augustakom.hurrican.model.cc.tal.CBVorgang;
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerOkValid.CBVorgangAnswerOkValidator;

@Test(groups = BaseTest.UNIT)
public class CBVorgangAnswerOkValidTest extends BaseTest {

    ConstraintValidatorContext context;

    private static final String LBZ = "lbz";

    @BeforeMethod
    public void setUp() {
        context = new ConstraintValidatorContextImpl(PathImpl.createRootPath(), null);
    }

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { CBVorgang.STATUS_ANSWERED, null, null, false },
                { CBVorgang.STATUS_ANSWERED, null, LBZ, false },
                { CBVorgang.STATUS_ANSWERED, new Date(), null, false },
                { CBVorgang.STATUS_ANSWERED, new Date(), LBZ, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(Long state, Date returnRealDate, String returnLbz, boolean expected) {
        CBVorgang cbVorgang = new CBVorgangBuilder().withStatus(state)
                .withReturnRealDate(returnRealDate).withReturnLBZ(returnLbz)
                .withReturnOk(Boolean.TRUE).setPersist(false).build();

        assertThat(new CBVorgangAnswerOkValidator().isValid(cbVorgang, context), equalTo(expected));
    }

}
