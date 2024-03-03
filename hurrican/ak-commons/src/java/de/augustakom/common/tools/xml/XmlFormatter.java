/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.06.2011 16:21:05
 */
package de.augustakom.common.tools.xml;

import java.io.*;
import javax.xml.parsers.*;
import com.google.common.base.Function;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Formatiert XML-Strings mit Einrückungen und Zeilenumbrüchen.
 */
public final class XmlFormatter {

    static DocumentBuilderFactory dbf;

    static {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
    }

    private XmlFormatter() {
        // instances not allowed
    }

    public static String formatSilent(String unformattedXml, int indent, XmlFormatterFunction... modifiers) {
        try {
            return format(unformattedXml, indent, modifiers);
        }
        catch (Exception e) {
            return unformattedXml;
        }
    }

    public static String formatSilent(String unformattedXml, XmlFormatterFunction... modifiers) {
        return formatSilent(unformattedXml, 8, modifiers);
    }

    public static String format(String unformattedXml, int indent, XmlFormatterFunction... modifiers) {
        if (StringUtils.isBlank(unformattedXml)) {
            return unformattedXml;
        }
        try {
            Document document = parseXmlFile(unformattedXml);

            for (Function<Document, Document> modifier : modifiers) {
                document = modifier.apply(document);
            }

            OutputFormat format = new OutputFormat(document);
            format.setIndenting(true);
            format.setIndent(indent);
            format.setLineWidth(120);

            format.setOmitXMLDeclaration(true);

            Writer writer = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(writer, format);
            serializer.serialize(document);
            return writer.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Document parseXmlFile(String in) {
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document createDocumentFromNode(Node node) {
        try {
            Document document = dbf.newDocumentBuilder().newDocument();
            Node imported = document.importNode(node, true);
            document.appendChild(imported);

            return document;
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
