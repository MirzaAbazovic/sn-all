package de.mnet.hurrican.simulator.mapping;

import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Special mapping key extractor for evaluating xpath expression on incoming request message. In case xpath evaluation
 * fails we assume that the incoming message is a intermediate message that is handled within a running test builder
 * instance.
 *
 *
 */
public class SimulatorMappingKeyExtractor extends XPathPayloadMappingKeyExtractor {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass()); // NOSONAR squid:S1312

    @Override
    public String getMappingKey(Message request) {
        try {
            return translateMappingKey(super.getMappingKey(request));
        }
        catch (CitrusRuntimeException e) {
            // when mapping key extraction fails we assume that the request is a intermediate
            // message that needs to be handled separately
            LOGGER.warn(e.getMessage() + " - Unable to extract mapping key for message " + request);
            return "INTERMEDIATE_MESSAGE";
        }
    }

    /**
     * Need to take care on german special letters in mapping keys. We do not want to have these special characters in
     * Java class names so we do escape them accordingly.
     *
     * @param mappingKey
     * @return
     */
    protected String translateMappingKey(String mappingKey) {
        StringBuilder escaped = new StringBuilder();

        String search = "ÄäÖöÜü";
        String replace = "AaOoUu";

        for (int i = 0; i < mappingKey.length(); i++) {
            int index = search.indexOf(mappingKey.charAt(i));
            if (index != -1) {
                escaped.append(replace.charAt(index)).append("e");
            }
            else {
                escaped.append(mappingKey.charAt(i));
            }
        }

        return escaped.toString().replaceAll("-", "_");
    }
}
