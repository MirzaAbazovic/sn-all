/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.14
 */
package de.augustakom.hurrican.service.exceptions.helper;

import static de.augustakom.hurrican.service.exceptions.helper.AtlasErrorMessageHelper.*;

import java.time.*;
import java.util.*;
import javax.jms.*;
import javax.validation.constraints.*;
import javax.xml.ws.*;
import com.google.common.base.Throwables;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.jms.JMSConstants;
import org.apache.cxf.transport.jms.JMSMessageHeadersType;
import org.apache.cxf.transport.jms.JMSPropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.ErrorBuilder;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.HandleErrorBuilder;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.JMSPropertyBuilder;
import de.augustakom.hurrican.model.builder.cdm.errorhandling.v1.MessageBuilder;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 * Factory creates new error message object for Atlas ESB error handling service. Error message object is either created
 * from WebService message context or SOAP message itself.
 *
 *
 */
public class HandleErrorFactory {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HandleErrorFactory.class);

    private final WebServiceContext webServiceContext;

    private final Message message;

    /**
     * Constructor using web service message context;
     *
     * @param webServiceContext
     */
    public HandleErrorFactory(WebServiceContext webServiceContext) {
        this.message = null;
        this.webServiceContext = webServiceContext;
    }

    /**
     * Constructor using web service message;
     *
     * @param message
     */
    public HandleErrorFactory(Message message) {
        this.message = message;
        this.webServiceContext = null;
    }

    /**
     * Creates new handle error object from exception. Uses error code, process name and optional business key.
     *
     * @param cause
     * @param errorCode
     * @param processName
     * @param key         optional business key
     * @return
     */
    public HandleError create(Exception cause, String errorCode, String processName, HandleError.BusinessKey key) {
        HandleErrorBuilder handleErrorBuilder = new HandleErrorBuilder()
                .withTrackingId(getEsbTrackingId())
                .withMessage(getMessage())
                .withComponent(getComponent(processName))
                .withError(new ErrorBuilder()
                        .withCode(errorCode)
                        .withMessage(cause.getMessage())
                        .withErrorDetails(Throwables.getStackTraceAsString(cause))
                        .withTime(LocalDateTime.now())
                        .build());

        if (key != null) {
            handleErrorBuilder.addBusinessKey(key);
        }

        return handleErrorBuilder.build();
    }

    /**
     * Creates new handle error object from exception. Uses error code and process name.
     *
     * @param cause
     * @param errorCode
     * @param processName
     * @return
     */
    public HandleError create(Exception cause, String errorCode, String processName) {
        return create(cause, errorCode, processName, null);
    }

    private JMSMessageHeadersType getJmsMessageHeaders() {
        if (message != null) {
            return (JMSMessageHeadersType) message.get(JMSConstants.JMS_SERVER_REQUEST_HEADERS);
        }
        else {
            return (JMSMessageHeadersType) webServiceContext.getMessageContext().get(JMSConstants.JMS_SERVER_REQUEST_HEADERS);
        }
    }

    private TextMessage getJmsRequest() {
        if (message != null) {
            return (TextMessage) message.get(JMSConstants.JMS_REQUEST_MESSAGE);
        }
        else {
            return (TextMessage) webServiceContext.getMessageContext().get(JMSConstants.JMS_REQUEST_MESSAGE);
        }
    }

    private String getEsbTrackingId() {
        JMSMessageHeadersType headers = getJmsMessageHeaders();
        String esbTrackingID = getHeaderField(AtlasEsbConstants.ESB_TRACKING_ID_KEY);
        final String jmsMessageID = headers != null ? headers.getJMSMessageID() : "";
        return StringUtils.isNotEmpty(esbTrackingID) ? esbTrackingID : jmsMessageID;
    }

    private HandleError.Component getComponent(String processName) {
        return AtlasErrorMessageHelper.buildComponent(getHeaderField(AtlasEsbConstants.SOAPACTION_TIBCO), processName);
    }

    private HandleError.Message getMessage() {
        MessageBuilder messageBuilder = new MessageBuilder();
        //filter out some unrelevant headers and add the rest
        if (getJmsMessageHeaders() != null && getJmsMessageHeaders().getProperty() != null) {
            getJmsMessageHeaders().getProperty().stream()
                    //sort list after names
                    .sorted(Comparator.comparing(JMSPropertyType::getName))
                    .forEachOrdered(property -> {
                                //check if value is a valid property
                                if (!isValueInList(FILTER_OUT_JMS_HEADERS, property.getName())) {
                                    messageBuilder.addJMSProperties(
                                            new JMSPropertyBuilder()
                                                    .withKeyValue(property.getName(), property.getValue())
                                                    .build());
                                }
                            }
                    );
        }
        //add retry info's, if there is one
        String origErrorId = getHeaderField(AtlasEsbConstants.ESB_ERROR_ID_KEY);
        if (origErrorId != null) {
            messageBuilder.withRetryInfo(origErrorId, Integer.parseInt(getHeaderField(AtlasEsbConstants.ESB_RETRY_COUNT_KEY)));
        }

        //try to extract the payload
        try {
            TextMessage textMessage = getJmsRequest();
            messageBuilder.withPayload(textMessage.getText());
            if (textMessage.getJMSDestination() instanceof javax.jms.Queue) {
                messageBuilder.withJMSEndpoint(((javax.jms.Queue) textMessage.getJMSDestination()).getQueueName());
            }
        }
        catch (JMSException e) {
            //on error do nothing leave text message blank
            LOGGER.error("Failed to extract SOAP message payload from JMS-Message", e);
        }
        return messageBuilder.build();
    }

    private String getHeaderField(@NotNull String headerKey) {
        if (getJmsMessageHeaders() != null && getJmsMessageHeaders().getProperty() != null) {
            for (JMSPropertyType property : getJmsMessageHeaders().getProperty()) {
                if (headerKey.equals(property.getName())) {
                    return property.getValue();
                }
            }
        }
        return null;
    }
}
