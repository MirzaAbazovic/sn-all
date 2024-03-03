/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2009 13:35:49
 */
package de.augustakom.hurrican.service.cc.impl;

import java.util.*;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AbteilungBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.SperreInfo;
import de.augustakom.hurrican.model.cc.SperreInfoBuilder;
import de.augustakom.hurrican.model.cc.SperreVerteilung;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.SperreVerteilungService;


/**
 *
 */
public class SperreVerteilungServiceTest extends AbstractHurricanBaseServiceTest {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(LockServiceTest.class);

    private SperreVerteilungService sperreVerteilungService = null;

    /**
     * Initialize the tests
     */
    @SuppressWarnings("unused")
    @BeforeMethod(groups = "service", dependsOnMethods = "beginTransactions")
    private void prepareTest() {
        // get the LockService
        sperreVerteilungService = getCCService(SperreVerteilungService.class);
    }

    /**
     * Tested createSperre und findSperre4Produkt
     *
     * @throws StoreException
     * @throws FindException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testCreateFindSperreVerteilungen() throws StoreException, FindException {
        Produkt produkt = getBuilder(ProduktBuilder.class).build();
        Abteilung abteilung = getBuilder(AbteilungBuilder.class).build();

        sperreVerteilungService.createSperreVerteilungen(produkt.getProdId(),
                Arrays.asList(new Long[] { abteilung.getId() }));

        List<SperreVerteilung> foundSperreVerteilungen =
                sperreVerteilungService.findSperreVerteilungen4Produkt(produkt.getId());

        Assert.assertEquals(foundSperreVerteilungen.size(), 1);
    }

    /**
     * Tested findSperreInfos
     *
     * @throws FindException
     */
    @Test(groups = BaseTest.SERVICE)
    public void testFindSperreInfos() throws FindException {
        SperreInfo sperreInfo = getBuilder(SperreInfoBuilder.class).build();
        List<SperreInfo> sperreInfos = sperreVerteilungService.findSperreInfos(true, sperreInfo.getAbteilungId());
        Assert.assertEquals(sperreInfos.size(), 1);
    }

}
