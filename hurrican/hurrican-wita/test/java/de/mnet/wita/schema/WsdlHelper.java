/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2016
 */
package de.mnet.wita.schema;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 */
abstract class WsdlHelper {
    static Node extractXSDFromWsdL(InputStream wsdl) throws Exception {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        Map<String, String> prefMap = new HashMap<String, String>() {{
            put("wsdl", "http://schemas.xmlsoap.org/wsdl/");
            put("jms", "http://www.tibco.com/namespaces/ws/2004/soap/binding/JMS");
            put("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
            put("tns", "http://www.mnet.de/esb/cdm/SupplierPartner/LineOrderNotificationService/v2");
            put("xsd", "http://www.w3.org/2001/XMLSchema");
        }};
        SimpleNamespaceContext namespaces = new SimpleNamespaceContext(prefMap);
        xpath.setNamespaceContext(namespaces);
        XPathExpression expr = xpath
                .compile("/wsdl:definitions/wsdl:types/xsd:schema");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(wsdl);
        NodeList schemaNodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        return schemaNodeList.item(0);
    }

    static String nodeToString(Node node) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        DOMSource source = new DOMSource(node);
        final StringWriter stringWriter = new StringWriter();
        final StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(source, streamResult);
        return stringWriter.toString();
    }

}

