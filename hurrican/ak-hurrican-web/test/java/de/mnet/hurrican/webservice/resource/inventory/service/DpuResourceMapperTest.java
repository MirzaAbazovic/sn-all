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
import de.augustakom.hurrican.model.cc.view.FTTBDpuImportView;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.inventory.builder.DpuResourceBuilder;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class DpuResourceMapperTest {

    @InjectMocks
    @Spy
    DpuResourceMapper cut;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
    }

    public void testCreateImportView() {
        Boolean stromVersorgung = true;

        Resource resource =
                new DpuResourceBuilder()
                        .withReversePower(stromVersorgung)
                        .build();
        FTTBDpuImportView importView = cut.createImportView(resource);
        assertEquals(importView.getClass(), FTTBDpuImportView.class);
        assertEquals(importView.getReversePower(), stromVersorgung);
        verify(cut).mapOltChildResource(eq(resource), any(FTTBDpuImportView.class));
    }

}
