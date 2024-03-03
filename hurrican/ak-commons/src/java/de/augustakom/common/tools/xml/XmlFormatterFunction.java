/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.09.2011 15:25:31
 */
package de.augustakom.common.tools.xml;

import java.util.*;
import javax.xml.namespace.*;
import javax.xml.xpath.*;
import com.google.common.base.Function;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class XmlFormatterFunction implements Function<Document, Document> {

    private XPathFactory xpf = XPathFactory.newInstance();

    protected final Map<String, String> namespaces = new HashMap<String, String>();

    protected XPath getXpath() {
        XPath xpath = xpf.newXPath();

        xpath.setNamespaceContext(new NamespaceContext() {

            @Override
            public Iterator<?> getPrefixes(final String namespaceURI) {
                final Set<String> result = new HashSet<String>();
                for (String key : namespaces.keySet()) {
                    final String value = namespaces.get(key);
                    if (value.equals(namespaceURI)) {
                        result.add(value);
                    }
                }
                return result.iterator();
            }

            @Override
            public String getPrefix(final String namespaceURI) {
                if (namespaces.containsValue(namespaceURI)) {
                    for (final String key : namespaces.keySet()) {
                        final String value = namespaces.get(key);
                        if (value.equals(namespaceURI)) {
                            return value;
                        }
                    }
                }
                return null;
            }

            @Override
            public String getNamespaceURI(final String prefix) {
                return namespaces.get(prefix);
            }

        });
        return xpath;
    }

    protected Node getNode(Document input, String xpathExpression) {
        try {
            XPathExpression expression = getXpath().compile(xpathExpression);
            Node node = (Node) expression.evaluate(input, XPathConstants.NODE);
            if (node == null) {
                throw new RuntimeException("Node not found: " + xpathExpression);
            }
            return node;
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
