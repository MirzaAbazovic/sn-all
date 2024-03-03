/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.2012 16:32:05
 */
package de.augustakom.hurrican.service.cc.impl;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.ws.MnetWebServiceTemplate;
import de.augustakom.hurrican.model.cc.WebServiceConfig;
import de.augustakom.hurrican.service.cc.QueryCCService;

/**
 * Abstrakte Klasse, um Webservice-Clients zu konfigurieren
 */
public class AbstractHurricanWebServiceClient extends DefaultCCService {

    private static final Logger LOGGER = Logger.getLogger(AbstractHurricanWebServiceClient.class);

    @Resource(name = "de.augustakom.hurrican.service.cc.QueryCCService")
    QueryCCService queryCcService;

    protected WebServiceTemplate configureAndGetWsTemplateForConfig(Long webServiceConfigId,
            MnetWebServiceTemplate wsTemplate) throws ServiceNotFoundException {
        try {
            WebServiceConfig wsCfg = queryCcService.findById(webServiceConfigId, WebServiceConfig.class);
            wsTemplate.configureWSTemplate(wsCfg);
            return wsTemplate;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Error configuring WebServiceTemplate: " + e.getMessage(), e);
        }
    }

}


