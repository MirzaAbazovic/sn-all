package de.augustakom.hurrican.service.cc.ffm.command;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalLeistungenCommandTest extends AbstractAggregateFfmCommandTest {

    @Mock
    private CCLeistungsService leistungsService;

    @InjectMocks
    @Spy
    private AggregateFfmTechnicalLeistungenCommand testling;

    @BeforeMethod
    public void setUp() {
        testling = new AggregateFfmTechnicalLeistungenCommand();
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

        TechLeistung techLs1 = new TechLeistungBuilder().withRandomId().build();
        TechLeistung techLs2 = new TechLeistungBuilder().withRandomId().build();
        TechLeistung techLs2Duplicate = new TechLeistungBuilder().withId(techLs2.getId()).build();
        TechLeistung techLs3 = new TechLeistungBuilder().withRandomId().build();

        when(leistungsService.findTechLeistungen4Auftrag(anyLong(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(techLs1, techLs2, techLs2Duplicate));

        doReturn(Optional.of(new VerlaufBuilder().setPersist(false).build())).when(testling).getBauauftrag();
        when(leistungsService.findTechLeistungen4Verlauf(anyLong(), eq(true)))
                .thenReturn(Arrays.asList(techLs1, techLs3));

        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());

        List<OrderTechnicalParams.TechnicalService> technischeLeistungen =
                workforceOrder.getDescription().getTechParams().getTechnicalService();
        assertNotNull(technischeLeistungen);
        assertTrue(technischeLeistungen.size() == 3);
    }


    @Test
    public void testExecuteWithoutTechLsResult() throws Exception {
        prepareFfmCommand(testling, true);

        when(leistungsService.findTechLeistungen4Auftrag(anyLong(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        Object result = testling.execute();

        assertNotNull(result);
        assertTrue(result instanceof ServiceCommandResult);
        assertTrue(((ServiceCommandResult) result).isOk());

        List<OrderTechnicalParams.TechnicalService> technischeLeistungen =
                workforceOrder.getDescription().getTechParams().getTechnicalService();
        assertNotNull(technischeLeistungen);
        assertTrue(technischeLeistungen.isEmpty());
    }


}