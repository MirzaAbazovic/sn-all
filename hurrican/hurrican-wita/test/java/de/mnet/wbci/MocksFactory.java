/* 
 * Copyright (c) 2013 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 31.10.13 
 */
package de.mnet.wbci;

import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;

/**
 * Mockito Mock-Factory, useful when creating mocked classes, especially when defined in a spring test context.
 */
public class MocksFactory<T> implements FactoryBean<T> {
    private Class<T> mockClass;

    public MocksFactory(Class<T> mockClass) {
        super();
        this.mockClass = mockClass;
    }

    @Override
    public T getObject() throws Exception {
        return Mockito.mock(mockClass);
    }

    @Override
    public Class<?> getObjectType() {
        return mockClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
