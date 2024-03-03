package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AuftragHousingBuilder;
import de.augustakom.hurrican.model.cc.CCAddressBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingBuildingBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingFloorBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingParcelBuilder;
import de.augustakom.hurrican.model.cc.housing.HousingRoomBuilder;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;
import de.augustakom.hurrican.service.cc.HousingService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

public class AggregateFfmTechnicalHousingCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private HousingService housingService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalHousingCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalHousingCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteFailure() throws Exception {
        prepareFfmCommand(testling);
        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertFalse(((ServiceCommandResult) result).isOk());

        verify(testling).checkThatWorkforceOrderHasTechnicalParams();
    }


    @Test
    public void testExecute() throws Exception {
        prepareFfmCommand(testling, true);

        when(housingService.findAuftragHousing(anyLong())).thenReturn(new AuftragHousingBuilder().build());
        when(housingService.findHousingBuilding4Auftrag(anyLong())).thenReturn(new HousingBuildingBuilder()
                .withCCAddress(new CCAddressBuilder().build())
                .build());
        when(housingService.findHousingFloorById(anyLong())).thenReturn(new HousingFloorBuilder().build());
        when(housingService.findHousingParcelById(anyLong())).thenReturn(new HousingParcelBuilder().build());
        when(housingService.findHousingRoomById(anyLong())).thenReturn(new HousingRoomBuilder().build());
        when(housingService.findHousingKeys(anyLong())).thenReturn(Arrays.asList(
                new AuftragHousingKeyView(), new AuftragHousingKeyView()));

        Object result = testling.execute();

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ServiceCommandResult);
        Assert.assertTrue(((ServiceCommandResult) result).isOk());

        OrderTechnicalParams.Housing housing = workforceOrder.getDescription().getTechParams().getHousing();
        Assert.assertNotNull(housing);
        Assert.assertNotNull(housing.getBuilding());
        Assert.assertEquals(housing.getTransponder().size(), 2);
    }

}