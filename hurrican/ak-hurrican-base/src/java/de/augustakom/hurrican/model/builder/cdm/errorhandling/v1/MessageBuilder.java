/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.10.13
 */
package de.augustakom.hurrican.model.builder.cdm.errorhandling.v1;

import java.math.*;
import java.util.*;

import de.mnet.esb.cdm.shared.errorhandlingservice.v1.HandleError;

public class MessageBuilder extends V1ErrorHandlingBuilder<HandleError.Message> {

    public MessageBuilder() {
        this.objectType = OBJECT_FACTORY.createHandleErrorMessage();
    }

    public MessageBuilder withJMSEndpoint(String jmsEndpoint) {
        objectType.setJMSEndpoint(jmsEndpoint);
        return this;
    }

    public MessageBuilder withPayload(String payload) {
        objectType.setPayload(payload);
        return this;
    }

    public MessageBuilder withRetryInfo(String origErrorId, int retryCount) {
        HandleError.Message.RetryInfo retryInfo = OBJECT_FACTORY.createHandleErrorMessageRetryInfo();
        retryInfo.setOrigErrorId(origErrorId);
        retryInfo.setRetryCount(BigInteger.valueOf(retryCount));
        objectType.setRetryInfo(retryInfo);
        return this;
    }

    public MessageBuilder withContexts(List<HandleError.Message.Context> contextList) {
        objectType.getContext().clear();
        objectType.getContext().addAll(contextList);
        return this;
    }

    public MessageBuilder addContexts(HandleError.Message.Context context) {
        objectType.getContext().add(context);
        return this;
    }

    public MessageBuilder withJMSProperties(List<String[]> keyValuePairs) {
        objectType.getJMSProperty().clear();
        for (String[] keyValue : keyValuePairs) {
            addJMSProperties(new JMSPropertyBuilder().withKeyValue(keyValue[0], keyValue[1]).build());
        }
        return this;
    }

    public MessageBuilder addJMSProperties(HandleError.Message.JMSProperty jmsProperty) {
        objectType.getJMSProperty().add(jmsProperty);
        return this;
    }


}
