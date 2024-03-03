/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.14
 */
package de.mnet.wbci.validation.constraints;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import javax.validation.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

/**
 *
 */
public abstract class AbstractValidatorTest<T extends ConstraintValidator> {
    @InjectMocks
    protected T testling;
    @Mock
    protected ConstraintValidatorContext contextMock;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilderMock;

    protected abstract T createTestling();

    @BeforeMethod
    public void setUp() throws Exception {
        testling = createTestling();
        MockitoAnnotations.initMocks(this);
        prepareContextMock();
    }

    protected void prepareContextMock() {
        reset(contextMock);
        when(contextMock.buildConstraintViolationWithTemplate(any(String.class))).thenReturn(
                constraintViolationBuilderMock);
    }

    protected void assertErrorMessageSet(int times) {
        verify(contextMock, times(times)).buildConstraintViolationWithTemplate(any(String.class));
    }

    protected void assertErrorMessageSet(boolean valid) {
        if (valid) {
            assertErrorMessageSet(0);
        }
        else {
            assertErrorMessageSet(1);
        }
    }
}
