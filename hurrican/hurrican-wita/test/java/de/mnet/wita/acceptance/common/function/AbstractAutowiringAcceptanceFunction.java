/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.2011 13:19:13
 */
package de.mnet.wita.acceptance.common.function;

import com.google.common.base.Function;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public abstract class AbstractAutowiringAcceptanceFunction<T> implements Function<Void, T> {
    private static final Logger LOGGER = Logger.getLogger(AbstractAutowiringAcceptanceFunction.class);

    public AbstractAutowiringAcceptanceFunction(ApplicationContext applicationContext) {
        LOGGER.debug("Autowiring bean " + getClass() + " with beans from context " + applicationContext.toString());
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
        beanFactory.initializeBean(this, getClass().getName());
    }
}


