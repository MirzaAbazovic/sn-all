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
import de.augustakom.hurrican.validation.cc.CBVorgangAnswerVorgabeMnetValid.CBVorgangAnswerVorgabeMnetValidator;

@Test(groups = BaseTest.UNIT)
public class CBVorgangAnswerVorgabeMnetValidTest extends BaseTest {

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
                { CBVorgang.STATUS_ANSWERED, null, false },
                { CBVorgang.STATUS_ANSWERED, new Date(), true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(Long state, Date vorgabeMnet, boolean expected) {
        CBVorgang cbVorgang = new CBVorgangBuilder().withStatus(state).withVorgabeMnet(vorgabeMnet).setPersist(false).build();

        assertThat(new CBVorgangAnswerVorgabeMnetValidator().isValid(cbVorgang, context), equalTo(expected));

    }

}
