/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.03.2005 07:56:12
 */
package de.augustakom.common.tools.dao.jdbc;

import java.sql.*;
import java.util.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;


/**
 * Ableitung von <code>org.apache.commons.dbcp.BasicDataSource</code>, um der Connection weitere Properties ueber die
 * Spring-Konfiguration uebergeben zu koennen. <br><br>
 * <p/>
 * Moegliche Konfiguration ueber Spring: <br> <property name="connectionProperties"> <props> <prop
 * key="zeroDateTimeBehavior">convertToNull</prop> <prop key="useUnicode">true</prop> </props> </property>
 *
 *
 */
public class AKBasicDataSource extends BasicDataSource {

    private static final Logger LOGGER = Logger.getLogger(AKBasicDataSource.class);

    /**
     * Fuegt der Connection die Properties <code>properties</code> hinzu.
     *
     * @param properties
     */
    public void setConnectionProperties(Properties properties) {
        if (properties != null) {
            Iterator<Object> keyIt = properties.keySet().iterator();
            while (keyIt.hasNext()) {
                String nextKey = (String) keyIt.next();
                String value = properties.getProperty(nextKey);

                // Properties der Connection uebergeben.
                addConnectionProperty(nextKey, value);

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("add connection Property: " + nextKey + " - Value: " + value);
                }
            }
        }
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}


