/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2017
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.MockitoAnnotations.initMocks;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.view.FTTBDpuKarteImportView;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.inventory.builder.DpuKarteResourceBuilder;
import de.mnet.hurrican.webservice.resource.inventory.builder.ResourceIdBuilder;
import de.mnet.hurrican.webservice.resource.inventory.builder.ResourceSpecIdBuilder;

/**
 * Created by molterfe on 07.03.2017.
 */
@Test(groups = BaseTest.UNIT)
public class DpuKarteResourceMapperTest {

    @InjectMocks
    DpuKarteResourceMapper cut;

    @Mock
    FTTXHardwareService fttxHardwareService;

    Resource in;

    @BeforeMethod
    public void setUp() throws Exception {
        in = new DpuKarteResourceBuilder()
                .withId("dpuId_1-1")
                .withName("1-1")
                .withKartentyp("MA5819")
                .withResourceSpec(
                        new ResourceSpecIdBuilder()
                                .withId(cut.DPU_KARTE_RESOURCE_SPEC_ID)
                                .withInventory(cut.COMMAND_INVENTORY)
                                .build()
                )
                .withParentResources(
                        new ResourceIdBuilder()
                                .withId("dpuId")
                                .build()
                )
                .build();
        initMocks(this);
    }

    @DataProvider
    Object[][] testIsResourceSupportedDataProvider() {
        return new Object[][] {
                { cut.DPU_KARTE_RESOURCE_SPEC_ID, cut.COMMAND_INVENTORY, Boolean.TRUE },
                { "asdf", cut.COMMAND_INVENTORY, Boolean.FALSE },
                { cut.DPU_KARTE_RESOURCE_SPEC_ID, "asdf", Boolean.FALSE }
        };
    }

    @Test(dataProvider = "testIsResourceSupportedDataProvider")
    public void testIsResourceSupported(final String specId, final String inventory, final Boolean expectedResult) throws Exception {
        final Resource in =
                new DpuKarteResourceBuilder()
                        .withResourceSpec(
                                new ResourceSpecIdBuilder()
                                        .withId(specId)
                                        .withInventory(inventory)
                                        .build()
                        )
                        .build();
        assertThat(cut.isResourceSupported(in), equalTo(expectedResult));
    }

    @Test
    public void testToFttbDpuKarteImportView() {
        final FTTBDpuKarteImportView result = cut.mapOltChildKarteResource(in);
        assertThat(result.getDpu(), equalTo(in.getParentResource().getId()));
        assertThat(result.getKarte(), equalTo(in.getName()));
        assertThat(result.getKarteId(), equalTo(in.getId()));
        assertThat(result.getKartentyp(),equalTo(in.getCharacteristic().get(0).getValue().get(0)));
    }
}
