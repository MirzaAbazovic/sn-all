package de.mnet.hurrican.simulator.helper;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.util.XMLUtils;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import com.consol.citrus.xml.xpath.XPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Helper class for evaluating XPath Expressions against XML Document
 */
public class XPathHelper {

    @Autowired
    private NamespaceContextBuilder namespaceContextBuilder;

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass()); // NOSONAR squid:S1312

    public String evaluateAsString(String payload, String expression) {
        return evaluateAsString(new DefaultMessage(payload), expression);
    }

    public String evaluateAsString(Message message, String expression) {
        try {
            return XPathUtils.evaluateAsString(XMLUtils.parseMessagePayload(message.getPayload().toString()), expression,
                    namespaceContextBuilder.buildContext(message, null));
        }
        catch (CitrusRuntimeException e) {
            // ignore unresolved xpath expressions and return empty string.
            LOGGER.warn("Ignoring unresolved xpath expression: '" + expression, e.getMessage() + "'");

            return "";
        }
    }

    /**
     * Sets the namespace context builder.
     *
     * @param namespaceContextBuilder
     */
    public void setNamespaceContextBuilder(NamespaceContextBuilder namespaceContextBuilder) {
        this.namespaceContextBuilder = namespaceContextBuilder;
    }
}
