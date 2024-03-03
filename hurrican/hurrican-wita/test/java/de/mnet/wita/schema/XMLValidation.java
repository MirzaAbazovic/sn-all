/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2016
 */
package de.mnet.wita.schema;

import java.io.*;
import javax.xml.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import org.xml.sax.SAXException;

/**
 */
public class XMLValidation {
    public static void validateXMLSchema(Schema schema, InputStream xmlPath) throws Exception {
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xmlPath));
    }

    public static Schema loadSchema(InputStream xsdPath) throws SAXException {
        SchemaFactory factory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return factory.newSchema(new StreamSource(xsdPath));

    }


}

