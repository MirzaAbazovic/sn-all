/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.14
 */
package de.mnet.hurrican.atlas.simulator.archive.mapping;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import de.mnet.hurrican.atlas.simulator.archive.DocumentArchiveOperation;
import de.mnet.hurrican.atlas.simulator.archive.DocumentArchiveXpathExpressions;
import de.mnet.hurrican.simulator.mapping.SimulatorMappingKeyExtractor;

/**
 *
 */
public class DocumentArchiveMappingKeyExtractor extends SimulatorMappingKeyExtractor implements InitializingBean {

    @Autowired
    private NamespaceContextBuilder namespaceContextBuilder;

    /** Mapping key extractor reads order details as key according to request types */
    private XPathPayloadMappingKeyExtractor getDocumentMappingKeyExtractor;

    @Override
    public String getMappingKey(Message request) {
        String mappingKey = super.getMappingKey(request);

        if (mappingKey.equals(DocumentArchiveOperation.GET_DOCUMENT.getName())) {
            mappingKey = getMappingKey(request, getDocumentMappingKeyExtractor, mappingKey);
        }

        LOGGER.info("Using mapping key: " + mappingKey);

        // use plain request type which is the message root qname
        return mappingKey;
    }

    private String getMappingKey(Message request, MappingKeyExtractor mappingKeyExtractor, String requestType) {
        try {
            String key = mappingKeyExtractor.extractMappingKey(request);
            if (StringUtils.hasText(key)) {
                LOGGER.info("Using mapping key: " + key);
                return key;
            }
        } catch (CitrusRuntimeException e) {
            if (!e.getMessage().contains("No result for XPath expression")) {
                throw e;
            } else {
                LOGGER.warn("Ignore error while evaluating XPath expression", e);
            }
        }

        return requestType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getDocumentMappingKeyExtractor = new XPathPayloadMappingKeyExtractor();
        getDocumentMappingKeyExtractor.setXpathExpression(DocumentArchiveXpathExpressions.DOCUMENT_ID.getXpath());

        if (namespaceContextBuilder != null) {
            getDocumentMappingKeyExtractor.setNamespaceContextBuilder(namespaceContextBuilder);
        }
    }
}
