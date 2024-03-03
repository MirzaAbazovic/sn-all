/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2012 11:41:43
 */
package de.augustakom.common.tools.spring;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class ExtendedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private static final Logger LOG = Logger.getLogger(ExtendedPropertyPlaceholderConfigurer.class);
    private Properties properties;
    private PropertyBean propertyBean;

    public void setPropertyBean(PropertyBean propertyBean) {
        this.propertyBean = propertyBean;
    }

    public ExtendedPropertyPlaceholderConfigurer() {
        super();
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
            throws BeansException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("processProperties() - processing properties...");
        }
        addAll(props, propertyBean.getProperties());
        properties = props;
        super.processProperties(beanFactory, props);
        if (LOG.isDebugEnabled()) {
            LOG.debug("processProperties() - processing properties... done.");
        }
    }

    private void addAll(Properties props, Properties toAddProperties) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("addAll() - adding " + toAddProperties.size() + " properties to existing property object (size: "
                    + props.size() + ")");
        }
        Enumeration<?> toAddEnumeration = toAddProperties.propertyNames();
        while (toAddEnumeration.hasMoreElements()) {
            String key = (String) toAddEnumeration.nextElement();
            String value = toAddProperties.getProperty(key);
            props.put(key, value);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("addAll() - property object has now " + props.size() + " properties");
        }
    }

}


