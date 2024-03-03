/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2010 11:46:11
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.IPAddressBuilder;
import de.augustakom.hurrican.model.cc.IpRoute;
import de.augustakom.hurrican.model.cc.IpRouteBuilder;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.base.exceptions.DeleteException;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.IpRouteService;


/**
 * TestNG Klasse fuer {@link IpRouteService}
 */
@Test(groups = BaseTest.SERVICE)
public class IpRouteServiceTest extends AbstractHurricanBaseServiceTest {

    private IpRoute ipRoute;

    private void buildTestData(boolean persist) {
        IPAddressBuilder ipAddressBuilder = getBuilder(IPAddressBuilder.class)
                .withAddress("192.168.1.1");
        ipRoute = getBuilder(IpRouteBuilder.class)
                .withIpAddressRefBuilder(ipAddressBuilder)
                .setPersist(persist).build();
    }

    public void testSaveIpRoute() throws StoreException, ValidationException {
        buildTestData(false);
        IpRouteService service = getCCService(IpRouteService.class);
        service.saveIpRoute(ipRoute, -1L);

        assertNotNull(ipRoute.getId(), "IpRoute not saved!");
        assertNotNull(ipRoute.getUserW(), "User not defined!");
        assertNotNull(ipRoute.getIpAddressRef().getGueltigVon(), "'GueltigVon' not defined!");
    }

    @Test(expectedExceptions = ValidationException.class)
    public void testSaveIpRouteInvalidIp() throws StoreException, ValidationException {
        buildTestData(false);
        ipRoute.setIpAddressRef(null);

        IpRouteService service = getCCService(IpRouteService.class);
        service.saveIpRoute(ipRoute, -1L);
    }

    public void testFindIpRoutesByOrder() throws FindException {
        buildTestData(true);
        IpRouteService service = getCCService(IpRouteService.class);

        List<IpRoute> result = service.findIpRoutesByOrder(ipRoute.getAuftragId());
        assertNotEmpty(result, "keine IpRoutes gefunden!");
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), ipRoute.getId());
    }

    public void testDeleteIpRoute() throws DeleteException {
        buildTestData(true);
        IpRouteService service = getCCService(IpRouteService.class);
        service.deleteIpRoute(ipRoute, -1L);

        assertTrue(ipRoute.getDeleted());
        assertTrue(DateTools.compareDates(ipRoute.getIpAddressRef().getGueltigBis(), new Date()) == 0);
    }

    public void testMoveIpRoute() throws StoreException, FindException {
        AuftragBuilder auftragBuilder = getBuilder(AuftragBuilder.class);

        buildTestData(true);
        IpRouteService service = getCCService(IpRouteService.class);

        assertFalse(NumberTools.equal(ipRoute.getAuftragId(), auftragBuilder.get().getId()));

        service.moveIpRoute(ipRoute, auftragBuilder.get().getId(), -1L);

        List<IpRoute> routesOnOldOrder = service.findIpRoutesByOrder(ipRoute.getAuftragId());
        assertNotEmpty(routesOnOldOrder, "alter Auftrag besitzt keine Routen mehr");

        List<IpRoute> routesOnNewOrder = service.findIpRoutesByOrder(auftragBuilder.get().getId());
        assertNotEmpty(routesOnNewOrder, "neuer Auftrag besitzt keine Routen");

        assertEquals(ipRoute.getIpAddressRef().getAddress(), routesOnNewOrder.get(0).getIpAddressRef().getAddress());
    }

    @Test(expectedExceptions = StoreException.class)
    public void testMoveIpRouteAuftragIdEquals() throws StoreException, FindException {
        buildTestData(false);
        IpRouteService service = getCCService(IpRouteService.class);
        service.moveIpRoute(ipRoute, ipRoute.getAuftragId(), -1L);
    }

}


