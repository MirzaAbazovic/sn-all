/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2015
 */
package de.mnet.hurrican.webservice.resource.inventory.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoImportView;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.inventory.builder.DpoResourceBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class DpoResourceMapperTest {

    @InjectMocks
    @Spy
    DpoResourceMapper cut;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
    }

    public void testCreateImportView() {
        String chassisIdentifier = "A-12-E";
        String chassisSlot = "1";
        Resource resource =
                new DpoResourceBuilder()
                        .withChassisIdentifier(chassisIdentifier)
                        .withChassisSlot(chassisSlot)
                        .build();
        FTTBHDpoImportView importView = cut.createImportView(resource);
        assertEquals(importView.getClass(), FTTBHDpoImportView.class);
        assertEquals(importView.getChassisIdentifier(), chassisIdentifier);
        assertEquals(importView.getChassisSlot(), chassisSlot);
        verify(cut).mapOltChildResource(eq(resource), any(FTTBHDpoImportView.class));
    }

}
