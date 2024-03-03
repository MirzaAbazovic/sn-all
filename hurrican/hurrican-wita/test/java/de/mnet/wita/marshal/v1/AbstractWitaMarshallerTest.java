/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.13
 */
package de.mnet.wita.marshal.v1;

import static org.testng.Assert.*;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.xml.transform.StringSource;
import org.springframework.xml.xsd.XsdSchema;
import org.testng.Assert;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.mnet.common.xml.WsdlXsdSchema;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.SchaltungKupferType;
import de.mnet.wita.WitaCdmVersion;

/**
 *
 */
@SuppressWarnings("Duplicates")
@ContextConfiguration({ "classpath:de/mnet/wita/marshal/wita-marshaller-test-context.xml" })
public abstract class AbstractWitaMarshallerTest extends AbstractTestNGSpringContextTests {

    private WsdlXsdSchema lineOrderServiceV1Xsd;

    void assertSchemaValidLineOrderService(WitaCdmVersion version, String message) throws IOException, ParserConfigurationException, SAXException {
        XsdSchema xsdSchema = getLineOrderServiceXsd(version);
        SAXParseException[] validationErrors = xsdSchema.createValidator().validate(new StringSource(message));
        Assert.assertEquals(validationErrors, new SAXParseException[] { }, String.format("Expecting no exceptions but got %s", Arrays.toString(validationErrors)));
    }

    private XsdSchema getLineOrderServiceXsd(WitaCdmVersion version) throws IOException, SAXException, ParserConfigurationException {
        if (version.equals(WitaCdmVersion.V1)) {
            if (lineOrderServiceV1Xsd == null) {
                lineOrderServiceV1Xsd = new WsdlXsdSchema(new ClassPathResource("/wsdl/v1/LineOrderService.wsdl"));
                lineOrderServiceV1Xsd.afterPropertiesSet();
            }

            return lineOrderServiceV1Xsd;
        }
        else {
            throw new IllegalArgumentException("Unsupported cdm version for LineOrderService");
        }
    }

    void verifySchaltangaben(SchaltangabenType schaltangaben, String expectedEVS, String expectedDoppelader) {
        assertNotNull(schaltangaben);
        for (SchaltangabenType.Schaltung schaltungAbstractType : schaltangaben.getSchaltung()) {
            if (schaltungAbstractType.getKupfer() != null) {
                SchaltungKupferType kupferType = schaltungAbstractType.getKupfer();
                assertEquals(kupferType.getEVS().length(), 2);
                assertEquals(kupferType.getEVS(), expectedEVS);
                assertEquals(kupferType.getDoppelader().length(), 2);
                assertEquals(kupferType.getDoppelader(), expectedDoppelader);
            }
        }
    }
}
