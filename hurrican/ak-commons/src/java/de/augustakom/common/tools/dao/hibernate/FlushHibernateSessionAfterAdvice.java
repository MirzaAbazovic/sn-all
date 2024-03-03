/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.09.2005 08:42:09
 */
package de.augustakom.common.tools.dao.hibernate;

import java.lang.reflect.*;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;


/**
 *
 * @see de.augustakom.common.tools.dao.hibernate.FlushHibernateSessionBeforeAdvice Unterschied: die Hibernate-Session
 * wird erst nach erfolgreichem Beenden einer bestimmten Methode geflusht.
 */
public class FlushHibernateSessionAfterAdvice extends AbstractFlushHibernateSessionAdvice implements
        AfterReturningAdvice, InitializingBean {

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if (getHibernateSessionFactory() == null) {
            throw new BeanCreationException("FlushHibernateSessionAfterAdvice needs a Hibernate Session-Factory!");
        }
    }

    /**
     * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object, java.lang.reflect.Method,
     * java.lang.Object[], java.lang.Object)
     */
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        flushHibernateSession(method, target);
    }

}


