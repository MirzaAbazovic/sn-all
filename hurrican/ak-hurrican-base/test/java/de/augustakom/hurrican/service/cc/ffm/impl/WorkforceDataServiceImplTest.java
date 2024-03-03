package de.augustakom.hurrican.service.cc.ffm.impl;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.math.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.builder.cdm.workforcedataservice.v1.WorkforceDataRequestBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DescriptionBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.WorkforceOrderBuilder;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;
import de.mnet.hurrican.resource.workforcedataservice.v1.WorkforceDataFault;
import de.mnet.hurrican.resource.workforcedataservice.v1.WorkforceDataRequest;
import de.mnet.hurrican.resource.workforcedataservice.v1.WorkforceDataResponse;

@Test(groups = BaseTest.UNIT)
public class WorkforceDataServiceImplTest extends BaseTest {

    @Mock
    private FFMServiceImpl ffmService;

    @Mock
    private BAConfigService baConfigService;

    @Mock
    private CCAuftragService ccAuftragService;


    @InjectMocks
    @Spy
    private WorkforceDataServiceImpl cut;

    private WorkforceDataRequest request;

    @BeforeMethod
    public void setUp() {
        cut = new WorkforceDataServiceImpl();
        MockitoAnnotations.initMocks(this);
        request =
                new WorkforceDataRequestBuilder()
                        .withTechnicalOrderId(1L)
                        .withIncidentReason("Stoerungsgrund")
                        .build();
    }

    @Test(expectedExceptions = { WorkforceDataFault.class }, expectedExceptionsMessageRegExp = ".*Auftrag.*")
    public void testGetWorkforceDataNoAuftrag() throws Exception {
        when(baConfigService.findBAVerlaufAnlass(request.getIncidentReason())).thenReturn(new BAVerlaufAnlass());
        when(ccAuftragService.findAuftragById(request.getTechnicalOrderId())).thenReturn(null);

        cut.getWorkforceData(request);
        verify(baConfigService).findBAVerlaufAnlass(request.getIncidentReason());
        verify(ccAuftragService).findAuftragById(request.getTechnicalOrderId());
    }

    @Test(expectedExceptions = { WorkforceDataFault.class }, expectedExceptionsMessageRegExp = ".*Störungsgrund.*")
    public void testGetWorkforceDataNoAnlass() throws Exception {
        when(baConfigService.findBAVerlaufAnlass(request.getIncidentReason())).thenReturn(null);
        cut.getWorkforceData(request);
        verify(baConfigService).findBAVerlaufAnlass(request.getIncidentReason());
        verify(ccAuftragService, never()).findAuftragById(request.getTechnicalOrderId());
    }

    @Test
    public void testGetWorkforceData() throws Exception {
        WorkforceOrder wfOrder =
                new WorkforceOrderBuilder()
                        .withDescription(
                                new DescriptionBuilder()
                                        .withOrderTechnicalParams(new OrderTechnicalParams())
                                        .build()
                        )
                        .withActivityType("activityType")
                        .withPlannedDuration(BigInteger.valueOf(47L))
                        .addQualification("Quali1")
                        .addQualification("Quali2")
                        .build();
        when(ffmService.createOrder(anyLong(), anyLong())).thenReturn(wfOrder);

        when(baConfigService.findBAVerlaufAnlass(request.getIncidentReason())).thenReturn(new BAVerlaufAnlass());
        when(ccAuftragService.findAuftragById(request.getTechnicalOrderId())).thenReturn(new Auftrag());
        WorkforceDataResponse response = cut.getWorkforceData(request);

        verify(baConfigService).findBAVerlaufAnlass(request.getIncidentReason());
        verify(ccAuftragService).findAuftragById(request.getTechnicalOrderId());
        verify(ffmService).createOrder(anyLong(), anyLong());

        assertEquals(response.getActivityType(), wfOrder.getActivityType());
        assertEquals(response.getPlannedDuration(), wfOrder.getPlannedDuration());
        assertEquals(response.getQualification(), wfOrder.getQualification());
        assertEquals(response.getTechParams(), wfOrder.getDescription().getTechParams());
    }

}
