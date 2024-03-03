/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2011 19:29:17
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
import de.augustakom.hurrican.validation.cc.CBVorgangCBNotNullExceptRexMkValid.CBVorgangCBNotNullExceptRexMkValidator;

@Test(groups = BaseTest.UNIT)
public class CBVorgangCBNotNullExceptRexMkValidTest extends BaseTest {

    ConstraintValidatorContext context;

    @BeforeMethod
    public void setUp() {
        context = new ConstraintValidatorContextImpl(PathImpl.createRootPath(), null);
    }

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { null, CBVorgang.TYP_NEU, false },
                { null, CBVorgang.TYP_REX_MK, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void test(Long cbId, Long typ, boolean expected) {
        CBVorgang cbVorgang = new CBVorgangBuilder().withCbId(cbId).withTyp(typ).setPersist(false).build();

        assertThat(new CBVorgangCBNotNullExceptRexMkValidator().isValid(cbVorgang, context), equalTo(expected));

    }

}
