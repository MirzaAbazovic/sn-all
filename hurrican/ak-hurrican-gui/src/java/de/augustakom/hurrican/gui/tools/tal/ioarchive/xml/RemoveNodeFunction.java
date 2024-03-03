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
 * Function to remove a node from an existing xml document, e.g.
 * <p/>
 * {@code <a><b>test</b></a>}
 * <p/>
 * becomes
 * <p/>
 * {@code <a></a>}
 * <p/>
 * by xpathExpression //a/b
 */
class RemoveNodeFunction extends XmlFormatterFunction {

    private final String xpathExpression;
    private final boolean optional;

    public RemoveNodeFunction(String xpathExpression, boolean optional) {

        this.xpathExpression = xpathExpression;
        this.optional = optional;

        namespaces.put("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
    }

    @Override
    public Document apply(Document input) {
        Node node;
        try {
            node = getNode(input, xpathExpression);
        }
        catch (RuntimeException e) {
            // Only ignore missing node if optional is set
            if (optional) {
                return input;
            }
            else {
                throw e;
            }
        }
        node.getParentNode().removeChild(node);
        return input;
    }
}
