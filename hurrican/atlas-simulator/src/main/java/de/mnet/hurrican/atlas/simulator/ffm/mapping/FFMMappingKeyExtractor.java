/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.mnet.hurrican.atlas.simulator.ffm.mapping;

import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import de.mnet.hurrican.atlas.simulator.ffm.FFMOperation;
import de.mnet.hurrican.atlas.simulator.ffm.FFMXpathExpressions;
import de.mnet.hurrican.simulator.mapping.SimulatorMappingKeyExtractor;

/**
 * Special mapping key extractor. First of all reads plain request root qname element as mapping key.
 * In case request is a createOrder request also reads description element and tries to get a more detailed mapping key.
 *
 *
 */
public class FFMMappingKeyExtractor extends SimulatorMappingKeyExtractor implements InitializingBean {

    @Autowired
    private NamespaceContextBuilder namespaceContextBuilder;

    /** Mapping key extractor reads order details as key */
    private XPathPayloadMappingKeyExtractor orderDetailsMappingKeyExtractor;

    @Override
    public String getMappingKey(Message request) {
        String requestType = super.getMappingKey(request);

        if (requestType.equals(FFMOperation.CREATE_ORDER.getName())) {
            try {
                // Additionally read save order description and if present use this identifier as mapping key
                String description = orderDetailsMappingKeyExtractor.getMappingKey(request);
                if (StringUtils.hasText(description)) {
                    LOGGER.info("Using mapping key: " + description);
                    return description;
                }
            } catch (CitrusRuntimeException e) {
                if (e.getMessage().contains("No result for XPath expression")) {
                    return requestType;
                } else {
                    throw e;
                }
            }
        }

        LOGGER.info("Using mapping key: " + requestType);

        // use plain request type which is the message root qname
        return requestType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        orderDetailsMappingKeyExtractor = new XPathPayloadMappingKeyExtractor();

        if (namespaceContextBuilder != null) {
            orderDetailsMappingKeyExtractor.setNamespaceContextBuilder(namespaceContextBuilder);
        }

        orderDetailsMappingKeyExtractor.setXpathExpression(FFMXpathExpressions.ORDER_DETAILS.getXpath());
    }
}
