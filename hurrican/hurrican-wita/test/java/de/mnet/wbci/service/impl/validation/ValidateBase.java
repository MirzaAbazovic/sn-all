/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.11.13
 */
package de.mnet.wbci.service.impl.validation;

import static de.mnet.wbci.TestGroups.*;
import static org.mockito.Mockito.*;

import java.util.*;
import javax.validation.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.service.impl.WbciValidationServiceImpl;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;
import de.mnet.wbci.validation.helper.ValidationHelper;

@Test(groups = UNIT)
@SuppressWarnings("unchecked")
public abstract class ValidateBase {
    @Mock
    protected ValidationHelper validationHelper;

    @InjectMocks
    protected final WbciValidationService testling = new WbciValidationServiceImpl();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterGroups(groups = UNIT)
    public void afterGroups() {
        // outputs all validation errors - useful for troubleshooting purposes
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, String> entry : ValidationTestUtil.allViolations.entrySet()) {
            s.append(String.format("%200s - Message:%s%n", entry.getKey(), entry.getValue()));
        }
        System.out.println(s);
    }

    public abstract Class<?> getErrorGroup();

    public abstract Class<?> getWarnGroup();

    public abstract GeschaeftsfallTyp getGeschaeftsfallTyp();

    public <T extends WbciMessage> Set<ConstraintViolation<T>> checkMessageForErrors(WbciCdmVersion wbciCdmVersion, T wbciMessage, Class<?> group) {
        when(validationHelper.getErrorValidationGroups(wbciCdmVersion, wbciMessage)).thenReturn(new Class<?>[] { group });
        return testling.checkWbciMessageForErrors(wbciCdmVersion, wbciMessage);
    }

    public <T extends WbciMessage> Set<ConstraintViolation<T>> checkMessageForWarnings(WbciCdmVersion wbciCdmVersion, T wbciMessage, Class<?> group) {
        when(validationHelper.getWarningValidationGroups(wbciCdmVersion, wbciMessage)).thenReturn(new Class<?>[] { group });
        return testling.checkWbciMessageForWarnings(wbciCdmVersion, wbciMessage);
    }

    protected <T extends WbciMessage> void assertConstraintViolationSet(
            Set<ConstraintViolation<T>> constraintViolationSet, int expectedCountOfConstraints) {
        Assert.assertNotNull(constraintViolationSet);
        if (!constraintViolationSet.isEmpty()) {
            System.out.println(new ConstraintViolationHelper().generateErrorMsg(constraintViolationSet));
        }
        Assert.assertEquals(constraintViolationSet.size(), expectedCountOfConstraints);
    }
}
