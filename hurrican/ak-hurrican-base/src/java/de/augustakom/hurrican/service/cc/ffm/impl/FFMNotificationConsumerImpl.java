/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.2014
 */
package de.augustakom.hurrican.service.cc.ffm.impl;

import javax.annotation.*;
import javax.servlet.*;
import javax.xml.ws.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.BusinessKeyBuilder;
import de.augustakom.hurrican.service.cc.ffm.FFMNotificationConsumer;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.service.exceptions.AtlasErrorHandlingService;
import de.augustakom.hurrican.service.exceptions.helper.HandleErrorFactory;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyOrderFeedback;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.NotifyUpdateOrder;
import de.mnet.esb.cdm.resource.workforcenotificationservice.v1.WorkforceNotificationService;

/**
 * 'Consumer'-Implementierung, um {@link NotifyUpdateOrder} bzw. {@link NotifyOrderFeedback} von FFM zu verarbeiten.
 */
@ServiceMode(Service.Mode.MESSAGE)
@CcTxRequired
@Component("de.augustakom.hurrican.service.ffm.FFMNotificationConsumer")
public class FFMNotificationConsumerImpl implements FFMNotificationConsumer, WorkforceNotificationService, ServletContextAware {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(FFMNotificationConsumer.class);

    final static String ERROR_CODE = "FFM-TECH-001";
    final static String PROCESS_NAME = "FFMNotificationConsumer";
    private static final String BUSINESS_KEY_ORDER_ID = "orderId";

    @Resource
    protected WebServiceContext webServiceContext;

    @Autowired
    private AtlasErrorHandlingService atlasErrorHandlingService;

    @Autowired
    private FFMService ffmService;

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void notifyUpdateOrder(NotifyUpdateOrder in) {
        try {
            Long sessionId = (servletContext != null)
                    ? (Long) servletContext.getAttribute(HurricanConstants.HURRICAN_SESSION_ID)
                    : null;
            ffmService.notifyUpdateOrder(in, sessionId);
        }
        catch (Exception e) {
            LOGGER.error("FFM-Consumer \"notifyUpdateOrder\" throws an Excpetion, try to send it to the ATLAS ESB:", e);
            atlasErrorHandlingService.handleError(new HandleErrorFactory(webServiceContext).create(e, ERROR_CODE, PROCESS_NAME,
                    new BusinessKeyBuilder().withKeyValue(BUSINESS_KEY_ORDER_ID, in.getOrderId()).build()));
        }
    }

    @Override
    public void notifyOrderFeedback(NotifyOrderFeedback in) {
        try {
            if (in.getMaterial() != null) {
                // Material-Feedback
                ffmService.materialFeedback(in.getOrderId(), in.getMaterial());
            }
            else if (StringUtils.isNotBlank(in.getText())) {
                // Text-Feedback
                ffmService.textFeedback(in.getOrderId(), in.getText(), in.getCaptured());
            }
            else {
                LOGGER.info(String.format(
                        "Ignored workforce order feedback notification with order id '%s' because" +
                                " neither text nor material is defined.",
                        in.getOrderId()));
            }
        }
        catch (Exception e) {
            LOGGER.error("FFM-Consumer \"notifyOrderFeedback\" throws an Excpetion, try to send it to the ATLAS ESB:", e);
            atlasErrorHandlingService.handleError(new HandleErrorFactory(webServiceContext).create(e, ERROR_CODE, PROCESS_NAME,
                    new BusinessKeyBuilder().withKeyValue(BUSINESS_KEY_ORDER_ID, in.getOrderId()).build()));
        }
    }

}
