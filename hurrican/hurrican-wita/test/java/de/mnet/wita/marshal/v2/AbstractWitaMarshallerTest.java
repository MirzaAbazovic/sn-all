/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.13
 */
package de.mnet.wita.marshal.v2;

import static org.testng.Assert.*;

import java.io.*;
import javax.xml.parsers.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.xml.xsd.XsdSchema;
import org.xml.sax.SAXException;

import de.mnet.common.xml.WsdlXsdSchema;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.SchaltangabenType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.SchaltungKupferType;
import de.mnet.wita.WitaCdmVersion;

/**
 *
 */
@SuppressWarnings("Duplicates")
@ContextConfiguration({ "classpath:de/mnet/wita/marshal/wita-marshaller-test-context.xml" })
public abstract class AbstractWitaMarshallerTest extends AbstractTestNGSpringContextTests {

    static final WitaCdmVersion WITA_CDM_VERSION = WitaCdmVersion.V2;

    private WsdlXsdSchema lineOrderServiceXsd;

    private XsdSchema getLineOrderServiceXsd(WitaCdmVersion version) throws IOException, SAXException, ParserConfigurationException {
        if (lineOrderServiceXsd == null) {
            if (version == WitaCdmVersion.V2) {
                lineOrderServiceXsd = new WsdlXsdSchema(new ClassPathResource("/wsdl/v2/LineOrderService.wsdl"));
            }
            else {
                throw new IllegalArgumentException("Unsupported cdm version for LineOrderService");
            }
            lineOrderServiceXsd.afterPropertiesSet();
        }
        return lineOrderServiceXsd;
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
