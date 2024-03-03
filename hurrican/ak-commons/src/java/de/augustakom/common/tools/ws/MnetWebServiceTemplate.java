/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.05.2009 09:26:05
 */
package de.augustakom.common.tools.ws;

import java.io.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;

import de.augustakom.common.service.exceptions.InitializationException;


/**
 * Ableitung vom Spring WebServiceTemplate.
 */
public class MnetWebServiceTemplate extends WebServiceTemplate implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MnetWebServiceTemplate.class);

    /**
     * Konfiguriert das WebService-Template mit den in <code>wsConfig</code> eingetragenen Parametern.
     */
    public void configureWSTemplate(IWebServiceConfiguration wsConfig) throws InitializationException {
        if (StringUtils.isNotBlank(wsConfig.getWsURL())) {
            this.setDefaultUri(wsConfig.getWsURL());

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("WebService template configured to URI: " + wsConfig.getWsURL());
            }
        }
        else {
            throw new InitializationException("Default URI for WebService is not defined!");
        }

        if (StringUtils.isNotBlank(wsConfig.getWsSecurementAction())) {
            // SecurityInterceptor setzen
            Wss4jSecurityInterceptor secInt = new Wss4jSecurityInterceptor();
            secInt.setSecurementActions(wsConfig.getWsSecurementAction());
            secInt.setSecurementUsername(wsConfig.getWsSecurementUser());
            secInt.setSecurementPassword(wsConfig.getWsSecurementPassword());

            ClientInterceptor[] cis = this.getInterceptors();
            int newLength = (cis != null) ? cis.length + 1 : 1;
            ClientInterceptor[] newCIS = new ClientInterceptor[newLength];
            newCIS[newCIS.length - 1] = secInt;

            this.setInterceptors(newCIS);
        }
        else {
            LOGGER.warn("No SecurementAction defined!");
        }
    }

}


