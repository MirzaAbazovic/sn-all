/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.05.2016
 */
package de.mnet.wita.schema;

import java.io.*;
import java.nio.charset.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import de.mnet.wita.BaseTest;
import de.mnet.wita.WitaCdmVersion;

@Test(groups = BaseTest.UNIT)
public class SchemaValidationTest {
    private static final WitaCdmVersion CDM_VERSION = WitaCdmVersion.V2;

    private static final String LOCATION_PATTERN = "/wsdl/v%s/%s.wsdl";
    private static final String NAMESPACE_PATTERN = "http://www.mnet.de/esb/cdm/SupplierPartner/%s/v%s";

    public void thatLineOrderNotificationSchemaCouldBeLoaded() throws Exception {
        String location = String.format(LOCATION_PATTERN, CDM_VERSION.getVersion(), "LineOrderNotificationService");
        String namespace = String.format(NAMESPACE_PATTERN, "LineOrderNotificationService", CDM_VERSION.getVersion());
        final InputStream inputStream = extractSchemaFromWsdl(location, namespace);
        XMLValidation.loadSchema(inputStream);
    }

    public void thatLineOrderSchemaCouldBeLoaded() throws Exception {
        String location = String.format(LOCATION_PATTERN, CDM_VERSION.getVersion(), "LineOrderService");
        String namespace = String.format(NAMESPACE_PATTERN, "LineOrderService", CDM_VERSION.getVersion());
        final InputStream inputStream = extractSchemaFromWsdl(location, namespace);
        XMLValidation.loadSchema(inputStream);
    }

    private InputStream extractSchemaFromWsdl(String wsdlLocation, String namespace) throws Exception {
        System.out.println("loading wsdl from location " + wsdlLocation);
        try (InputStream wsdl = getClass().getResourceAsStream(wsdlLocation)) {
            Element schemaNode = (Element) WsdlHelper.extractXSDFromWsdL(wsdl);
            schemaNode.setAttribute("xmlns:tns", namespace);
            return new ByteArrayInputStream(WsdlHelper.nodeToString(schemaNode).getBytes(StandardCharsets.UTF_8));
        }
    }

}

