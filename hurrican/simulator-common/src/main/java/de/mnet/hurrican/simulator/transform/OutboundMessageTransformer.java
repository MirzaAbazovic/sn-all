/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.07.2014
 */
package de.mnet.hurrican.simulator.transform;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import de.mnet.hurrican.simulator.exception.SimulatorException;

/**
 * Transformer performs XSLT transformation on outbound messages in order to
 * remove empty elements that would break schema validation. Empty elements are caused by empty
 * variable values in simulator HTML form GUI.
 *
 *
 */
public class OutboundMessageTransformer {

    /** Transformation source */
    public static final String XSLT_SOURCE = "de/mnet/hurrican/simulator/transform/transform_outbound.xsl";

    /** XSLT transformer factory */
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /**
     * Transforms outbound message with XSLT.
     * @param messagePayload
     * @return transformed message payload
     */
    public String transform(String messagePayload) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource(XSLT_SOURCE).getInputStream());
            xsltSource.setSystemId("transform-outbound");
            xmlSource = new StringSource(messagePayload);

            //create transformer
            Transformer transformer = transformerFactory.newTransformer(xsltSource);

            //transform
            StringResult result = new StringResult();
            transformer.transform(xmlSource, result);
            return result.toString();
        } catch (IOException e) {
            throw new SimulatorException("Failed to transform outbound message", e);
        } catch (TransformerException e) {
            throw new SimulatorException("Failed to transform outbound message", e);
        }
    }

    /**
     * Transform outbound message resource with XSLT.
     * @param messagePayloadResource
     * @return transformed message payload
     */
    public String transform(Resource messagePayloadResource) {
        try {
            return transform(FileCopyUtils.copyToString(new InputStreamReader(messagePayloadResource.getInputStream(), "UTF-8")));
        } catch (IOException e) {
            throw new SimulatorException("Failed to read payload resource", e);
        }
    }

}
