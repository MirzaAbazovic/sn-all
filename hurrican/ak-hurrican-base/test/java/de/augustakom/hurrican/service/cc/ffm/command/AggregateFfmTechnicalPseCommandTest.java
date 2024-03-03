package de.augustakom.hurrican.service.cc.ffm.command;

import static de.augustakom.hurrican.service.cc.ffm.command.AbstractFfmCommand.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.DescriptionBuilder;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.v1.WorkforceOrderBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.EquipmentBuilder;
import de.augustakom.hurrican.model.cc.HWBaugruppeBuilder;
import de.augustakom.hurrican.model.cc.HWDpuBuilder;
import de.augustakom.hurrican.model.cc.HWMduBuilder;
import de.augustakom.hurrican.model.cc.RangierungBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWMdu;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;
import de.mnet.esb.cdm.resource.workforceservice.v1.WorkforceOrder;

/**
 * Testet ob wenn noetig eine PSE ausgeliefert wird
 */
@Test(groups = BaseTest.UNIT)
public class AggregateFfmTechnicalPseCommandTest {

    @Mock
    private EndstellenService endstellenService;

    @Mock
    private RangierungsService rangierungsService;

    @Mock
    private HWService hwService;

    @Spy
    @InjectMocks
    private AggregateFfmTechnicalPseCommand cut;

    @BeforeMethod
    protected void setUp() {
        cut = new AggregateFfmTechnicalPseCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void wennDpuMitReversePower_dannPse() throws Exception {
        final Long auftragId = 815L;
        final WorkforceOrder workforceOrder = buildTestDataAndMockServiceCalls(auftragId, Boolean.TRUE);
        final Object result = cut.execute();
        assertServiceCommandResultOk(result);
        assertPse(workforceOrder);
    }

    private void assertServiceCommandResultOk(Object result) {
        assertServiceCommandResult(result, true);
    }

    private void assertServiceCommandResultNotOk(Object result) {
        assertServiceCommandResult(result, false);
    }

    private void assertServiceCommandResult(final Object result, final boolean ok) {
        assertThat(result, instanceOf(ServiceCommandResult.class));
        assertThat(((ServiceCommandResult) result).isOk(), is(ok));
    }

    private void assertPse(WorkforceOrder workforceOrder) {
        final OrderTechnicalParams.PSE pse = workforceOrder.getDescription().getTechParams().getPSE();
        assertThat(pse, not(nullValue(OrderTechnicalParams.PSE.class)));
        assertThat(pse.getManufacturer(), equalTo("Huawei"));
        assertThat(pse.getModel(), equalTo("PSE-235"));
        assertThat(pse.getVersion(), equalTo("1.0"));
    }

    @Test
    public void wennDpuOhneReversePower_dannKeinPse() throws Exception {
        final Long auftragId = 815L;
        final WorkforceOrder workforceOrder = buildTestDataAndMockServiceCalls(auftragId, Boolean.FALSE);
        final Object result = cut.execute();
        assertServiceCommandResultOk(result);
        assertNoPse(workforceOrder);
    }

    private void assertNoPse(WorkforceOrder workforceOrder) {
        final OrderTechnicalParams.PSE pse = workforceOrder.getDescription().getTechParams().getPSE();
        assertThat(pse, nullValue(OrderTechnicalParams.PSE.class));
    }

    @Test
    public void wennKeineDpu_dannKeinPse() throws Exception {
        final HWMdu hwMdu = new HWMduBuilder()
                .withRandomId()
                .build();
        keinePse(() -> when(hwService.findRackById(anyLong())).thenReturn(hwMdu));
    }

    @Test
    public void wennKeinEqIn_dannKeinePse() throws Exception    {
        when(rangierungsService.findEquipment(anyLong())).thenReturn(null);
    }

    @Test
    public void wennKeineRangierung_dannKeinPse() throws Exception {
        keinePse(() -> when(rangierungsService.findRangierungTx(anyLong())).thenReturn(null));

    }

    @Test
    public void wennKeineEndstelle_dannKeinPse() throws Exception {
        keinePse(() -> when(endstellenService.findEndstelle4Auftrag(anyLong(), anyString())).thenReturn(null));
    }

    @Test
    public void wennKeinHwRack_dannKeinPse() throws Exception {
        keinePse(() -> when(hwService.findRackById(anyLong())).thenReturn(null));
    }

    private void keinePse(final Callable mocks) throws Exception {
        final Long auftragId = 815L;
        final WorkforceOrder workforceOrder = buildTestDataAndMockServiceCalls(auftragId, Boolean.TRUE);
        mocks.call();
        final Object result = cut.execute();
        assertServiceCommandResultOk(result);
        assertNoPse(workforceOrder);
    }

    @Test
    public void wennException_dannResultFailure() throws Exception {
        final Long auftragId = 815L;
        buildTestDataAndMockServiceCalls(auftragId, Boolean.FALSE);
        when(rangierungsService.findRangierungTx(anyLong())).thenThrow(new RuntimeException());
        final Object result = cut.execute();
        assertServiceCommandResultNotOk(result);
    }

    @Test
    public void wennKeineTechnicalParams_dannResultFailure() throws Exception {
        final WorkforceOrder workforceOrder = new WorkforceOrderBuilder()
                .withDescription(new WorkforceOrder.Description())
                .build();
        wennWorkforceOrderUnvollstaendig_dannResultFailure(workforceOrder);
    }

    @Test
    public void wennKeineDescription_dannResultFailure() throws Exception {
        wennWorkforceOrderUnvollstaendig_dannResultFailure(new WorkforceOrder());
    }

    private void wennWorkforceOrderUnvollstaendig_dannResultFailure(final WorkforceOrder workforceOrder) throws Exception {
        final Long auftragId = 815L;
        buildTestDataAndMockServiceCalls(auftragId, Boolean.FALSE);
        cut.prepare(KEY_WORKFORCE_ORDER, workforceOrder);
        final Object result = cut.execute();
        assertServiceCommandResultNotOk(result);
    }

    private WorkforceOrder buildTestDataAndMockServiceCalls(final Long auftragId, final Boolean reversePower) throws Exception {
        final HWDpuBuilder hwDpuBuilder = new HWDpuBuilder()
                .withRandomId()
                .withReversePower(reversePower);
        final HWBaugruppeBuilder baugruppeBuilder = new HWBaugruppeBuilder()
                .withRandomId()
                .withRackBuilder(hwDpuBuilder);
        final EquipmentBuilder equipmentBuilder = new EquipmentBuilder()
                .withRandomId()
                .withBaugruppeBuilder(baugruppeBuilder);
        final RangierungBuilder rangierungBuilder = new RangierungBuilder()
                .withRandomId()
                .withEqInBuilder(equipmentBuilder);
        final Endstelle endstelle = new EndstelleBuilder()
                .withRandomId()
                .withRangierungBuilder(rangierungBuilder)
                .build();

        when(endstellenService.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B))
                .thenReturn(endstelle);
        when(rangierungsService.findRangierungTx(rangierungBuilder.get().getId()))
                .thenReturn(rangierungBuilder.get());
        when(rangierungsService.findEquipment(equipmentBuilder.get().getId())).thenReturn(equipmentBuilder.get());
        when(hwService.findBaugruppe(baugruppeBuilder.get().getId())).thenReturn(baugruppeBuilder.get());
        when(hwService.findRackById(anyLong())).thenReturn(hwDpuBuilder.get());

        doReturn(rangierungsService).when(cut).getCCService(RangierungsService.class);
        doReturn(endstellenService).when(cut).getCCService(EndstellenService.class);
        doReturn(hwService).when(cut).getCCService(HWService.class);

        final WorkforceOrder workforceOrder = new WorkforceOrderBuilder()
                .withDescription(
                        new DescriptionBuilder()
                                .withOrderTechnicalParams(new OrderTechnicalParams())
                                .build()
                )
                .build();

        cut.prepare(KEY_WORKFORCE_ORDER, workforceOrder);
        cut.prepare(KEY_AUFTRAG_ID, auftragId);
        return workforceOrder;
    }
}
