/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.04.2006 09:03:39
 */
package de.augustakom.hurrican.service.cc.impl;


import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.cc.ReferenceService;


@Test(groups = BaseTest.SERVICE)
public class ReferenceServiceTest extends AbstractHurricanBaseServiceTest {

    /**
     * Test method for 'de.augustakom.hurrican.service.cc.impl.ReferenceServiceImpl.findReferencesByType(String,
     * boolean)'
     */
    @Test
    public void testFindReferencesByType() throws Exception {
        ReferenceService service = getCCService(ReferenceService.class);
        List<Reference> result = service.findReferencesByType(Reference.REF_TYPE_LAUFZEIT, null);

        assertNotEmpty(result, "Keine References gefunden!");
    }

    @Test
    public void testFindReferencesByTypeTxNew() throws Exception {
        ReferenceService service = getCCService(ReferenceService.class);
        List<Reference> result = service.findReferencesByTypeTxNew(Reference.REF_TYPE_LAUFZEIT, null);

        assertNotEmpty(result, "Keine References gefunden!");
    }

    /**
     * Test method for 'de.augustakom.hurrican.service.cc.impl.ReferenceServiceImpl.findReference(Long) '
     */
    @Test
    public void testFindReference() throws Exception {
        ReferenceService service = getCCService(ReferenceService.class);
        Reference result = service.findReference(1001L);

        assertNotNull(result, "Keine entsprechende Reference gefunden! Reference: " + result.getGuiText());
    }

    /**
     * Test method for 'de.augustakom.hurrican.service.cc.impl.ReferenceServiceImpl.findReference(String, Integer)'
     */
    public void testFindReferenceByTypeAndInt() throws Exception {
        ReferenceService service = getCCService(ReferenceService.class);
        Reference result = service.findReference(
                Reference.REF_TYPE_EINWAHLDN_4_PRODUKT, 14);

        assertNotNull(result, "Keine entsprechende Reference gefunden! Reference: " + result.getStrValue());
    }

}


