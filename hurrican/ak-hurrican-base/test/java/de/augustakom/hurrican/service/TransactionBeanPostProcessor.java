/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.04.2012 16:18:14
 */
package de.augustakom.hurrican.service;

import java.lang.reflect.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;

/**
 * BeanPostProcessor for a Test context that wraps a proxy around PlatformTransactionManager Beans and marks each
 * transaction as "RollbackOnly" before the commit call is executed on the proxied bean. This is necessary to rollback
 * nested transactions with REQUIRES_NEW transaction attribute.
 *
 *
 */
public class TransactionBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = Logger.getLogger(TransactionBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof PlatformTransactionManager) {
            InvocationHandler invocationHandler = new InvocationHandler() {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("getTransaction")) {
                        TransactionStatus tStatus = ((PlatformTransactionManager) bean)
                                .getTransaction((TransactionDefinition) args[0]);
                        if (tStatus.isNewTransaction()) {
                            LOGGER.info("new transaction TM='" + beanName + "' status="
                                    + Integer.toHexString(tStatus.hashCode()));
                        }
                        return tStatus;
                    }
                    else if (method.getName().equals("commit")) {
                        TransactionStatus tStatus = (TransactionStatus) args[0];
                        if (tStatus.isNewTransaction()) {
                            LOGGER.info("commit request (setRollbackOnly) for TM='" + beanName + "' status="
                                    + Integer.toHexString(tStatus.hashCode()));
                            tStatus.setRollbackOnly();
                        }
                    }
                    return method.invoke(bean, args);
                }
            };
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(),
                    new Class[] { PlatformTransactionManager.class, ResourceTransactionManager.class },
                    invocationHandler);
        }
        return bean;
    }
}
