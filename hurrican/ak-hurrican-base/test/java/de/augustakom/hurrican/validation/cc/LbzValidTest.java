/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.2011 14:34:04
 */
package de.augustakom.hurrican.validation.cc;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import javax.validation.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.validation.cc.LbzValid.LbzValidator;

@Test(groups = BaseTest.UNIT)
public class LbzValidTest extends BaseTest {

    LbzValidator lbzValidator = new LbzValidator();

    @DataProvider
    public Object[][] validLbz() {
        return new Object[][] { { "96W/00821/0821/123456" }, { "96W / 0821/ 821 /  123456" }, { null } };
    }

    @DataProvider
    public Object[][] invalidLbz() {
        return new Object[][] { { "96W/00821" }, { "abc" }, { "96W/abc/821/123" }, { "96W / 0821/ 821 /" },
                { "96W/21200821/0821/123456" }, { "////" } };
    }

    @Test(dataProvider = "validLbz")
    public void testValidLbz(String lbz) {
        assertTrue(lbzValidator.isValid(lbz, mock(ConstraintValidatorContext.class)));
    }

    @Test(dataProvider = "invalidLbz")
    public void testInvalidLbz(String lbz) {
        assertFalse(lbzValidator.isValid(lbz, mock(ConstraintValidatorContext.class)));
    }

}
