/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.13
 */
package de.augustakom.hurrican.service.location;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Helper service for looking up Camel Proxies. This helper class was introduced to prevent circular dependencies
 * between camel and spring (where camel routes and processors depended on spring beans via autowiring before the camel
 * context could be initialised fully and in turn spring beans depended on camel proxies before they could be
 * initialised fully). To get around this problem the camel proxies are looked up at runtime, rather than at spring
 * context startup. Spring beans using camel proxies should therefore <b>never</b> use {@link
 * org.springframework.beans.factory.annotation.Autowired} when using a camel proxy and should use this helper service
 * instead.
 */
@Component
public class CamelProxyLookupService implements ApplicationContextAware {
    private static final Logger LOGGER = Logger.getLogger(CamelProxyLookupService.class);
    public static final String PROXY_LINE_ORDER = "lineOrderService";
    public static final String PROXY_CARRIER_NEGOTIATION = "carrierNegotiationService";
    public static final String PROXY_LOCATION_SERVICE = "locationService";
    public static final String PROXY_CUSTOMER_SERVICE = "customerService";

    private ApplicationContext applicationContext;

    public <T> T lookupCamelProxy(String proxyName, Class<T> proxyClazz) {
        LOGGER.debug(String.format("Looking up proxy class '%s' by name '%s'", proxyClazz, proxyName));
        T t = (T) applicationContext.getBean(proxyName, proxyClazz);
        if (t == null) {
            LOGGER.warn(String.format("No proxy class '%s' found in spring context matching name '%s'", proxyClazz, proxyName));
        }
        LOGGER.debug(String.format("Looking up proxy class '%s' by name '%s'", proxyClazz, proxyName));
        return t;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
