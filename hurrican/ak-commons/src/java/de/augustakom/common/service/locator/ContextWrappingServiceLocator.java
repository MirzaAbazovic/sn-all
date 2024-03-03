/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 15.03.2010 15:16:50
  */

package de.augustakom.common.service.locator;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class ContextWrappingServiceLocator extends AbstractSpringServiceLocator {

    private final ConfigurableApplicationContext context;
    private final String name;

    public ContextWrappingServiceLocator(ConfigurableApplicationContext context, String name) {
        this.context = context;
        this.name = name;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public String getServiceLocatorName() {
        return name;
    }

    @Override
    protected void initServiceLocator() {
        // NOP
    }

    @Override
    protected boolean isInitialized() {
        return true;
    }

    @Override
    public void closeServiceLocator() {
        context.close();
    }
}
