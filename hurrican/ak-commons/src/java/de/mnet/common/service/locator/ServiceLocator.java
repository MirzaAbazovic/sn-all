/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.09.2011 17:51:07
 */
package de.mnet.common.service.locator;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import de.augustakom.common.service.iface.IServiceCommand;

/**
 * Ein Service-Locator um sich dynamisch Beans aus dem Context zu holen. Ein sehr duenner Wrapper um den
 * Applikation-Context von Spring. Muss als Bean im Applikation-Context existieren. Implementiert nur Sachen, die sich
 * nicht via Annotations abbilden lassen, also speziell auch nicht: <ol> <li>Alle Beans eines Typs zu holen ->
 * <tt>@Autowired List<Type> allBeansOfType</tt></li> <li>Eine Factory fuer eine Bean -> <tt>@Autowired Provider<Type>
 * beanFactory</tt></li> </ol>
 */
public class ServiceLocator {
    @Autowired
    private ApplicationContext applicationContext;

    public IServiceCommand getCmdBean(String name) {
        return applicationContext.getBean(name, IServiceCommand.class);
    }

    public <T> T getBean(String name, Class<T> type) {
        return applicationContext.getBean(name, type);
    }

    public <T> IServiceCommand getCmdBean(Class<T> type) {
        return applicationContext.getBean(type.getName(), IServiceCommand.class);
    }

    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return applicationContext.getBeansOfType(type);
    }
}


