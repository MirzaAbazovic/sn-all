/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2015
 */
package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingBuildingBuilder;
import de.augustakom.hurrican.service.cc.HousingService;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmHeaderLocationHousingCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private HousingService housingService;

    @InjectMocks
    @Spy
    private AggregateFfmHeaderLocationHousingCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmHeaderLocationHousingCommand();
        prepareFfmCommand(testling);
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testExecuteFailure() throws Exception {
        doThrow(new FFMServiceException("failure")).when(testling).getEndstelleB(anyBoolean());

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(!((ServiceCommandResult) result).isOk());
    }


    @Test
    public void testExecute() throws Exception {
        CCAddress address = new CCAddressBuilder()
                .withNummer("22").withHausnummerZusatz("b").setPersist(false).build();

        HousingBuilding building = new HousingBuildingBuilder()
                .withCCAddress(address)
                .build();

        when(housingService.findHousingBuilding4Auftrag(anyLong())).thenReturn(building);

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        Assert.assertNotNull(workforceOrder.getLocation());
        Assert.assertEquals(workforceOrder.getLocation().getCountry(), address.getLandId());
        Assert.assertEquals(workforceOrder.getLocation().getCity(), address.getCombinedOrtOrtsteil());
        Assert.assertEquals(workforceOrder.getLocation().getZipCode(), address.getPlz());
        Assert.assertEquals(workforceOrder.getLocation().getStreet(), address.getStrasse());
        Assert.assertEquals(workforceOrder.getLocation().getFloor(), address.getStrasseAdd());
        Assert.assertEquals(workforceOrder.getLocation().getHouseNumber(),
                address.getNummer() + " " + address.getHausnummerZusatz());
    }


}
