/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2014
 */
package de.mnet.hurrican.webservice.resource;

import static org.testng.Assert.*;

import java.math.*;
import javax.xml.bind.*;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpec;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecCharacteristic;
import de.mnet.hurrican.webservice.resource.inventory.ResourceSpecificationInventory;
import de.mnet.hurrican.webservice.resource.inventory.service.AbstractOltChildResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.AbstractResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.DpoPortResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.DpoResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.OntPortResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.OntResourceMapper;

@Test(groups = BaseTest.UNIT)
public class ResourceSpecificationInventoryTest extends BaseTest {

    private static final Long DEFAULT_CARDINALITY_MIN = 1L;
    private static final String DEFAULT_CARDINALITY_MAX = "defaultCardinalityMax";

    @InjectMocks
    ResourceSpecificationInventory cut;

    @BeforeMethod
    public void prepare() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    private Object[][] specNamesDataProvider() {
        return new Object[][] {
                {OntResourceMapper.ONT_RESOURCE_SPEC_ID},
                {OntPortResourceMapper.ONT_PORT_RESOURCE_SPEC_ID},
                {"dpo-1"},
                { DpoPortResourceMapper.DPO_PORT_RESOURCE_SPEC_ID},
        };
    }

    @Test(dataProvider = "specNamesDataProvider")
    public void testSpecAvailable(String name) throws JAXBException, ResourceValidationException {
        ResourceSpec ontSpec = cut.get(name, AbstractResourceMapper.COMMAND_INVENTORY);
        assertNotNull(ontSpec);
        assertNotEmpty(ontSpec.getCharacteristic());
    }

    @DataProvider
    private Object[][] specNamesCardinalitiesDataProvider() {
        return new Object[][] {
                {OntResourceMapper.ONT_RESOURCE_SPEC_ID},
                {"dpo-1"},
        };
    }

    @Test(dataProvider = "specNamesCardinalitiesDataProvider")
    public void testSpecCardinalities(String name) throws JAXBException, ResourceValidationException {
        ResourceSpec ontSpec = cut.get(name, AbstractResourceMapper.COMMAND_INVENTORY);
        assertNotNull(ontSpec);
        assertNotEmpty(ontSpec.getCharacteristic());
        for (ResourceSpecCharacteristic resourceSpecCharacteristic : ontSpec.getCharacteristic()) {
            if (AbstractOltChildResourceMapper.CHARACTERISTIC_SERIENNUMMER.equals(resourceSpecCharacteristic.getName().toLowerCase())
                    || AbstractOltChildResourceMapper.CHARACTERISTIC_OLTSUBRACK.equals(resourceSpecCharacteristic.getName().toLowerCase())
                    || DpoResourceMapper.CHARACTERISTIC_CHASSISBEZEICHNUNG.equals(resourceSpecCharacteristic.getName().toLowerCase())
                    || DpoResourceMapper.CHARACTERISTIC_POSITION_IN_CHASSIS.equals(resourceSpecCharacteristic.getName().toLowerCase())
                    ) {
                assertMin(resourceSpecCharacteristic, 0L);
            }
            else {
                assertMin(resourceSpecCharacteristic, DEFAULT_CARDINALITY_MIN);
            }
            assertMaxDefault(resourceSpecCharacteristic);
        }
    }

    @DataProvider
    private Object[][] portSpecNamesCardinalitiesDataProvider() {
        return new Object[][] {
                {DpoPortResourceMapper.DPO_PORT_RESOURCE_SPEC_ID},
                {OntPortResourceMapper.ONT_PORT_RESOURCE_SPEC_ID},
        };
    }

    @Test(dataProvider = "portSpecNamesCardinalitiesDataProvider")
    public void testPortSpecCardinalities(String name) throws JAXBException, ResourceValidationException {
        ResourceSpec ontSpec = cut.get(name, AbstractResourceMapper.COMMAND_INVENTORY);
        assertNotNull(ontSpec);
        assertNotEmpty(ontSpec.getCharacteristic());
        for (ResourceSpecCharacteristic resourceSpecCharacteristic : ontSpec.getCharacteristic()) {
            if (DpoPortResourceMapper.CHARACTERISTIC_LEISTE.equals(resourceSpecCharacteristic.getName())
                    || DpoPortResourceMapper.CHARACTERISTIC_STIFT.equals(resourceSpecCharacteristic.getName())) {
                assertMin(resourceSpecCharacteristic, 0L);
            }
            else {
                assertMin(resourceSpecCharacteristic, DEFAULT_CARDINALITY_MIN);
            }
            assertMaxDefault(resourceSpecCharacteristic);
        }
    }

    // Helper methods
    private void assertMaxDefault(ResourceSpecCharacteristic resourceSpecCharacteristic) {
        assertNotNull(resourceSpecCharacteristic.getCardinality());
        assertNotNull(resourceSpecCharacteristic.getCardinality().getMax());
        assertEquals(resourceSpecCharacteristic.getCardinality().getMax(), "1");
    }

    private void assertMin(ResourceSpecCharacteristic resourceSpecCharacteristic, long min) {
        assertNotNull(resourceSpecCharacteristic.getCardinality());
        assertNotNull(resourceSpecCharacteristic.getCardinality().getMin());
        assertEquals(resourceSpecCharacteristic.getCardinality().getMin(), BigInteger.valueOf(min));
    }

}
