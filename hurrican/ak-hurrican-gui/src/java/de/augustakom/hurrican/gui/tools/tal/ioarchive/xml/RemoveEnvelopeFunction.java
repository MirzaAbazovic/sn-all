/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.2011 16:17:58
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.augustakom.common.tools.xml.XmlFormatter;
import de.augustakom.common.tools.xml.XmlFormatterFunction;

/**
 * Function to remove an envelope from an existing xml document, e.g.
 * <p/>
 * {@code <a><b>test</b></a>}
 * <p/>
 * becomes
 * <p/>
 * {@code <b>test</b>}
 * <p/>
 * by xpathExpression //a. Note: The first child of a is taken while the rest is ignored!
 */
class RemoveEnvelopeFunction extends XmlFormatterFunction {

    private final String xpathExpression;

    public RemoveEnvelopeFunction(String xpathExpression) {
        this.xpathExpression = xpathExpression;

        namespaces.put("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
    }

    @Override
    public Document apply(Document input) {
        Node node = getNode(input, xpathExpression);
        org.w3c.dom.NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return XmlFormatter.createDocumentFromNode(child);
            }
        }
        throw new RuntimeException("No child nodes found");
    }
}

