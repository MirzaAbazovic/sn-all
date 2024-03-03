/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.14
 */
package de.mnet.wita.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;

import java.io.*;
import java.util.*;
import javax.xml.transform.dom.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Marshaller;
import org.springframework.xml.xsd.XsdSchema;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.mnet.common.xml.WsdlXsdSchema;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.marshal.v1.AbstractWitaMarshallerTest;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.builder.AuftragBuilder;

/**
 *
 */
@Test(groups = UNIT)
public class WitaSchemaValidationServiceImplTest extends AbstractWitaMarshallerTest {

    @Autowired
    @Qualifier("witaMessageMarshallerV1")
    private Marshaller messageMarshallerV1;

    @Autowired
    @Qualifier("witaMessageMarshallerV2")
    private Marshaller messageMarshallerV2;

    private WitaSchemaValidationServiceImpl schemaValidationService = new WitaSchemaValidationServiceImpl();

    @BeforeTest
    public void setUp() throws Exception {
        List<XsdSchema> schemasV1 = setupSchema(WitaCdmVersion.V1);
        List<XsdSchema> schemasV2 = setupSchema(WitaCdmVersion.V2);

        List<XsdSchema> allSchemas = Lists.newArrayList();
        allSchemas.addAll(schemasV1);
        allSchemas.addAll(schemasV2);

        schemaValidationService.setSchemas(allSchemas);
    }

    private List<XsdSchema> setupSchema(WitaCdmVersion version) throws Exception {
        String lineOrderWsdl = String.format("/wsdl/v%s/LineOrderService.wsdl", version.getVersion());
        WsdlXsdSchema wsdl = new WsdlXsdSchema(new ClassPathResource(lineOrderWsdl));
        wsdl.afterPropertiesSet();
        List<XsdSchema> schemas = new ArrayList<>();
        schemas.add(wsdl);

        String lineOrderNotificationWsdl = String.format("/wsdl/v%s/LineOrderNotificationService.wsdl", version.getVersion());
        wsdl = new WsdlXsdSchema(new ClassPathResource(lineOrderNotificationWsdl));
        wsdl.afterPropertiesSet();
        schemas.add(wsdl);
        return schemas;
    }

    @Test
    public void testSchemaValidationOk() throws IOException {
        thatSchemaValidationIsOK(messageMarshallerV1);
        thatSchemaValidationIsOK(messageMarshallerV2);
    }

    private void thatSchemaValidationIsOK(Marshaller marshaller) throws IOException {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).withExterneAuftragsnummer("1234567430")
                .buildValid();

        DOMResult result = new DOMResult();
        marshaller.marshal(auftrag, result);

        schemaValidationService.validatePayload(new DOMSource(result.getNode()));
    }


    @Test(expectedExceptions = WitaBaseException.class, expectedExceptionsMessageRegExp = "Schema validation failed!")
    public void testSchemaValidationFailed() throws IOException {
        thatSchemaValidationFailsWithoutExterneAuftragsnummer(messageMarshallerV1);
        thatSchemaValidationFailsWithoutExterneAuftragsnummer(messageMarshallerV2);
    }

    private void thatSchemaValidationFailsWithoutExterneAuftragsnummer(Marshaller marshaller) throws IOException {
        Auftrag auftrag = new AuftragBuilder(BEREITSTELLUNG).buildValid();

        auftrag.setExterneAuftragsnummer(null);

        DOMResult result = new DOMResult();
        marshaller.marshal(auftrag, result);

        schemaValidationService.validatePayload(new DOMSource(result.getNode()));
    }
}
