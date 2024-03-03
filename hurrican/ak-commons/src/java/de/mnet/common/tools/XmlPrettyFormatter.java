/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.08.13
 */
package de.mnet.common.tools;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class XmlPrettyFormatter {

    private static final int DEFAULT_INDENT = 2;
    public static final String YES = "yes";
    public static final String INDENT_NUMBER = "indent-number";
    public static final String INDENT_AMOUNT = "{http://xml.apache.org/xslt}indent-amount";

    public static String prettyFormat(String input) {
        return prettyFormat(input, DEFAULT_INDENT);
    }

    public static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // This statement works with JDK 6
            transformerFactory.setAttribute(INDENT_NUMBER, indent);

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, YES);
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        }
        catch (Throwable e) {  // NOSONAR squid:S1181 ; it´s mandatory to get an output regardless of the thrown exception/error
            // You'll come here if you are using JDK 1.5 you are getting the following exeption:
            // java.lang.IllegalArgumentException: Not supported: indent-number
            try {
                Source xmlInput = new StreamSource(new StringReader(input));
                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, YES);
                transformer.setOutputProperty(INDENT_AMOUNT, String.valueOf(indent));
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString();
            }
            catch (Throwable t) {  // NOSONAR squid:S1181 ; it´s mandatory to get an output regardless of the thrown exception/error
                return input;
            }
        }
    }

}
