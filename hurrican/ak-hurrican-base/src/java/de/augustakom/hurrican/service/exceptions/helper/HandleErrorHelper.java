/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.service.exceptions.helper;

import static de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError.*;
import static de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError.Message.*;

import java.time.format.*;
import java.util.*;
import org.apache.commons.collections.CollectionUtils;

import de.augustakom.common.tools.lang.DateTools;
import de.mnet.common.webservice.tools.AtlasEsbConstants;
import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

/**
 *
 */
public class HandleErrorHelper {

    public static String handleErrorToString(HandleError handleError) {
        StringBuilder sb = new StringBuilder();
        sb.append("ESB-ERROR-Message:\n")
                .append(AtlasEsbConstants.ESB_TRACKING_ID_KEY).append("=").append(handleError.getTrackingId());
        if (handleError.getError() != null) {
            sb.append(",\n").append(errorToString(handleError.getError()));
        }
        if (handleError.getMessage() != null) {
            sb.append(",\n").append(messageToString(handleError.getMessage()));
        }
        if (handleError.getComponent() != null) {
            sb.append(",\n").append(componentToString(handleError.getComponent()));
        }
        if (CollectionUtils.isNotEmpty(handleError.getBusinessKey())) {
            sb.append(",\n").append(businessKeystoString(handleError.getBusinessKey()));
        }
        return sb.toString();
    }

    public static String errorToString(HandleError.Error error) {
        StringBuilder sb = new StringBuilder("Error=[");
        if (error.getTime() != null) {
            sb.append("time=").append(error.getTime().format(DateTimeFormatter.ofPattern(DateTools.PATTERN_DATE_TIME_FULL))).append(", ");
        }
        return sb.append("errorCode=").append(error.getCode())
                .append(", message=").append(error.getMessage())
                .append(", details=").append(error.getErrorDetails())
                .append("]")
                .toString();
    }

    public static String businessKeystoString(List<HandleError.BusinessKey> businessKeys) {
        StringBuilder sb = new StringBuilder("BusinessKey=[");
        if (businessKeys != null) {
            Iterator<HandleError.BusinessKey> it = businessKeys.iterator();
            while (it.hasNext()) {
                HandleError.BusinessKey key = it.next();
                sb.append(key.getKey()).append("=").append(key.getValue());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        return sb.append("]").toString();
    }

    public static String messageToString(Message message) {
        StringBuilder sb = new StringBuilder("Message=[");
        if (message != null) {
            sb.append("JMSEndpoint=").append(message.getJMSEndpoint());
            sb.append(", Payload=").append(message.getPayload());
            if (message.getContext() != null) {
                sb.append(", ").append(contextToString(message.getContext()));
            }
            if (message.getJMSProperty() != null) {
                sb.append(", ").append(jmsPropertiesToString(message.getJMSProperty()));
            }
            if (message.getRetryInfo() != null) {
                sb.append(", ").append(retryInfoToString(message.getRetryInfo()));
            }
        }
        return sb.append("]").toString();
    }

    public static String jmsPropertiesToString(List<JMSProperty> jmsProperties) {
        StringBuilder sb = new StringBuilder("JMSProperties=[");
        if (jmsProperties != null) {
            Iterator<JMSProperty> it = jmsProperties.iterator();
            while (it.hasNext()) {
                JMSProperty key = it.next();
                sb.append(key.getKey()).append("=").append(key.getValue());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        return sb.append("]").toString();
    }

    public static String contextToString(List<Context> contextList) {
        StringBuilder sb = new StringBuilder("Context=[");
        if (contextList != null) {
            Iterator<Context> it = contextList.iterator();
            while (it.hasNext()) {
                Context key = it.next();
                sb.append(key.getKey()).append("=").append(key.getValue());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        return sb.append("]").toString();
    }

    private static String retryInfoToString(RetryInfo retryInfo) {
        return "RetryInfo=["
                + "OrigErrorId=" + retryInfo.getOrigErrorId()
                + ", RetryCount=" + retryInfo.getRetryCount()
                + "]";
    }

    public static String componentToString(Component component) {
        return "Component=["
                + "host=" + component.getHost()
                + ", name=" + component.getName()
                + ", service=" + component.getService()
                + ", operation=" + component.getOperation()
                + ", processId=" + component.getProcessId()
                + ", processName=" + component.getProcessName()
                + "]";
    }

}
