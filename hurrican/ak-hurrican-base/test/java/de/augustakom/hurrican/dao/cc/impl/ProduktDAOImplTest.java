/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2016
 */
package de.augustakom.hurrican.dao.cc.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.ProduktDAO;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;

@Test(groups = BaseTest.SERVICE)
public class ProduktDAOImplTest extends AbstractHurricanBaseServiceTest {

    @Autowired
    ProduktDAO produktDAO;

    @Test
    void testFindByParentPhysikTypIds() {
        final List<Long> parentPhysikTypIds = Collections.singletonList(0L);
        produktDAO.findByParentPhysikTypIds(parentPhysikTypIds); // Dies testet nur das HQL.
    }

    @Test
    void testFindProductsByTechLeistungType() {
        final List<Produkt> produkte = produktDAO.findProductsByTechLeistungType(TechLeistung.TYP_DOWNSTREAM);
        assertNotEmpty(produkte);
    }
    
    @Test
    void testProduct_With_AftrAddr() throws Exception {
        final Produkt produkt = produktDAO.findActualByProdId(false, 513L);  // sufr flat
        Assert.assertNotNull(produkt);
        Assert.assertNotNull(produkt.getAftrAddress());
    }
}
