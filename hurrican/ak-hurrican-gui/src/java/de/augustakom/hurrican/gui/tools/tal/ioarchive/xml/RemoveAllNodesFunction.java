/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.2011 16:17:46
 */
package de.augustakom.hurrican.gui.tools.tal.ioarchive.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.augustakom.common.tools.xml.XmlFormatterFunction;

/**
 * Function to remove all nodes matching an xpath from an existing xml document, e.g.
 * <p/>
 * {@code <a> <b1> <c1>test</c1> <c2>test</c2> </b1> <b2> <c1>test</c1> <c2>test</c2> </b2> </a> }
 * <p/>
 * becomes
 * <p/>
 * {@code <a> <b1> <c1>test</c1> </b1> <b2> <c1>test</c1> </b2> </a> }
 * <p/>
 * by xpathExpression //a/ * /c2
 */
class RemoveAllNodesFunction extends XmlFormatterFunction {

    private final String xpathExpression;

    public RemoveAllNodesFunction(String xpathExpression) {
        this.xpathExpression = xpathExpression;
        namespaces.put("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
    }

    @Override
    public Document apply(Document input) {
        Node node;
        while (true) {
            try {
                node = getNode(input, xpathExpression);
            }
            catch (RuntimeException e) {
                // No more nodes found
                break;
            }
            node.getParentNode().removeChild(node);
        }
        return input;
    }
}
