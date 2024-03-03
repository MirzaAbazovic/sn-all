/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2014
 */
package de.mnet.hurrican.webservice.resource.inventory;

import java.io.*;
import java.math.*;
import java.util.*;
import javax.annotation.*;
import javax.inject.*;
import javax.xml.*;
import javax.xml.bind.*;
import javax.xml.namespace.*;
import javax.xml.parsers.*;
import javax.xml.validation.*;
import javax.xml.xpath.*;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.GetResourceSpecsResponse;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpec;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecCharacteristic;
import de.mnet.hurrican.webservice.resource.ResourceValidationException;

/**
 * Das ResourceSpecificationInventory haelt alle bekannten ResourceSpecification vor. Über die Id und den Namen der
 * Inventory wird eine ResourceSpecification eindeutig referenziert. Diese kann dann verwendet werden um z.B. Resourcen
 * zu validieren.
 */

@Named
@Scope("singleton")
public final class ResourceSpecificationInventory {
    private static final Logger LOGGER = Logger.getLogger(ResourceSpecificationInventory.class);
    final Map<ResourceSpecId, ResourceSpec> specifications = Maps.newHashMap();
    final Map<String, String> defaultValues = Maps.newHashMap();

    static final String DEFAULT_MAX_USAGE = "defaultMaxUsage";
    static final String DEFAULT_CARDINALITY_MIN = "defaultCardinalityMin";
    static final String DEFAULT_CARDINALITY_MAX = "defaultCardinalityMax";

    private ResourceSpecificationInventory() {
        try {
            init();
        }
        catch (Exception e) {
            LOGGER.error("Kann das Resource Inventory nicht initialisieren.", e);
        }
    }

    private void init() throws JAXBException, SAXException, XPathExpressionException, IOException, ParserConfigurationException {
        readDefaultValues();
        GetResourceSpecsResponse getResourceSpecsResponse = readSpecs();
        for (ResourceSpec resourceSpec : getResourceSpecsResponse.getResourceSpec()) {
            ResourceSpecId id = new ResourceSpecId(resourceSpec.getId(), resourceSpec.getInventory());
            specifications.put(id, resourceSpec);
            addDefaultValues(resourceSpec);
        }
    }

    private GetResourceSpecsResponse readSpecs() throws JAXBException, SAXException {
        JAXBContext context = JAXBContext.newInstance(GetResourceSpecsResponse.class);
        Schema resourceSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(getClass().getResource("/xsd/resource/ResourceInventoryService.xsd"));
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(resourceSchema);
        InputStream inputStream = getClass().getResourceAsStream("/ResourceInventorySpecsCommand.xml");
        return (GetResourceSpecsResponse) unmarshaller.unmarshal(inputStream);
    }

    private void readDefaultValues() throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        Document document = newDocumentInstance();
        XPath xPath = newXPathInstance();
        final String defaultMaxUsage = xPath.evaluate("/xs:schema/xs:complexType[@name='ResourceSpec']" +
                "/xs:sequence/xs:element[@name='maxUsage']/@default", document);
        final String defaultCardinalityMin = xPath.evaluate("/xs:schema/xs:complexType[@name='ResourceSpecCharacteristic']" +
                "/xs:sequence/xs:element[@name='cardinality']/xs:complexType" +
                "/xs:sequence/xs:element[@name='min']/@default", document);
        final String defaultCardinalityMax = xPath.evaluate("/xs:schema/xs:complexType[@name='ResourceSpecCharacteristic']" +
                "/xs:sequence/xs:element[@name='cardinality']/xs:complexType/xs:sequence" +
                "/xs:element[@name='max']/@default", document);
        defaultValues.put(DEFAULT_MAX_USAGE, defaultMaxUsage);
        defaultValues.put(DEFAULT_CARDINALITY_MIN, defaultCardinalityMin);
        defaultValues.put(DEFAULT_CARDINALITY_MAX, defaultCardinalityMax);
    }

    private void addDefaultValues(ResourceSpec resourceSpec) {
        if (resourceSpec.getMaxUsage() == null) {
            resourceSpec.setMaxUsage(defaultValues.get(DEFAULT_MAX_USAGE));
        }
        if (resourceSpec.getCharacteristic() != null) {
            for (ResourceSpecCharacteristic resourceSpecCharacteristic : resourceSpec.getCharacteristic()) {
                if (resourceSpecCharacteristic.getCardinality() == null) {
                    resourceSpecCharacteristic.setCardinality(new ResourceSpecCharacteristic.Cardinality());
                }
                if (resourceSpecCharacteristic.getCardinality().getMin() == null) {
                    resourceSpecCharacteristic.getCardinality().setMin(new BigInteger(defaultValues.get(DEFAULT_CARDINALITY_MIN)));
                }
                if (resourceSpecCharacteristic.getCardinality().getMax() == null) {
                    resourceSpecCharacteristic.getCardinality().setMax(defaultValues.get(DEFAULT_CARDINALITY_MAX));
                }
            }
        }

    }

    private Document newDocumentInstance() throws ParserConfigurationException, IOException, SAXException {
        InputStream inputStream = getClass().getResourceAsStream("/xsd/resource/ResourceInventoryService.xsd");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(inputStream);
    }

    private XPath newXPathInstance() {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NamespaceContext namespaceContext = new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                return prefix.equals("xs") ? "http://www.w3.org/2001/XMLSchema" : null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        };
        xpath.setNamespaceContext(namespaceContext);
        return xpath;
    }

    @Nullable
    public ResourceSpec get(String id, String inventory) throws ResourceValidationException {
        if (specifications.isEmpty()) {
            throw new ResourceValidationException("Resource Inventory nicht verfügbar " +
                    "(ggf. ist die Initialisierung fehlgeschlagen)!");
        }
        return specifications.get(new ResourceSpecId(id, inventory));
    }

    private class ResourceSpecId extends de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId {

        public ResourceSpecId(String id, String inventory) {
            this.id = id;
            this.inventory = inventory;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ResourceSpecId that = (ResourceSpecId) o;

            if (id != null ? !id.equals(that.id) : that.id != null) {
                return false;
            }
            if (inventory != null ? !inventory.equals(that.inventory) : that.inventory != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (inventory != null ? inventory.hashCode() : 0);
            return result;
        }
    }

}
