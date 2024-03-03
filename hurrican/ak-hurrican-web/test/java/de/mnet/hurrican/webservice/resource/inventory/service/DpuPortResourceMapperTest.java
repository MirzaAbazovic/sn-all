package de.mnet.hurrican.webservice.resource.inventory.service;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.view.FTTBDpuPortImportView;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.inventory.builder.DpuPortResourceBuilder;
import de.mnet.hurrican.webservice.resource.inventory.builder.ResourceIdBuilder;
import de.mnet.hurrican.webservice.resource.inventory.builder.ResourceSpecIdBuilder;

@Test(groups = BaseTest.UNIT)
public class DpuPortResourceMapperTest {

    @InjectMocks
    DpuPortResourceMapper cut;

    @Mock
    FTTXHardwareService fttxHardwareService;

    Resource inDpuPort1;
    Resource inDpuPort2;

    final String schnittstelle = "POTS";
    final String leiste = "L1";
    final String stift = "S1";

    @BeforeMethod
    public void setUp() throws Exception {
        inDpuPort1 = new DpuPortResourceBuilder()
                .withName("fsda")
                .withSchnittstelle(schnittstelle)
                .withLeiste(leiste)
                .withStift(stift)
                .withResourceSpec(
                        new ResourceSpecIdBuilder()
                                .withId(cut.DPU_PORT1_RESOURCE_SPEC_ID)
                                .withInventory(cut.COMMAND_INVENTORY)
                                .build()
                )
                .withParentResources(
                        new ResourceIdBuilder()
                                .withId("dpuId")
                                .build()
                )
                .build();

        inDpuPort2 = new DpuPortResourceBuilder()
                .withName("fsda")
                .withSchnittstelle(schnittstelle)
                .withLeiste(leiste)
                .withStift(stift)
                .withResourceSpec(
                        new ResourceSpecIdBuilder()
                                .withId(cut.DPU_PORT2_RESOURCE_SPEC_ID)
                                .withInventory(cut.COMMAND_INVENTORY)
                                .build()
                )
                .withParentResources(
                        new ResourceIdBuilder()
                                .withId("modName")
                                .build()
                )
                .build();
        initMocks(this);
    }

    @DataProvider
    Object[][] testIsResourceSupportedDataProvider() {
        return new Object[][] {
                { cut.DPU_PORT1_RESOURCE_SPEC_ID, cut.COMMAND_INVENTORY, Boolean.TRUE },
                { "fasdf", cut.COMMAND_INVENTORY, Boolean.FALSE },
                { cut.DPU_PORT1_RESOURCE_SPEC_ID, "asdfasd", Boolean.FALSE }
        };
    }

    @Test(dataProvider = "testIsResourceSupportedDataProvider")
    public void testIsResourceSupported(final String specId, final String inventory, final Boolean expectedResult) throws Exception {
        final Resource in =
                new DpuPortResourceBuilder()
                        .withResourceSpec(
                                new ResourceSpecIdBuilder()
                                        .withId(specId)
                                        .withInventory(inventory)
                                        .build()
                        )
                        .build();
        assertThat(cut.isResourceSupported(in), equalTo(expectedResult));
    }

    @Test
    public void testToFttbDpuPort1ImportView() {
        final FTTBDpuPortImportView result = cut.mapOltChildPortResource(inDpuPort1);

        assertThat(result.getParent(), equalTo(inDpuPort1.getParentResource().getId()));
        assertThat(result.getPort(), equalTo(inDpuPort1.getName()));
        verifyResult(result);

    }

    @Test
    public void testProcessResourceDdpuport1() throws Exception {
        cut.processResource(inDpuPort1, -1L);
        verify(fttxHardwareService).generateFTTBDpuPort(any(FTTBDpuPortImportView.class), eq(-1L));
    }

    @Test
    public void testProcessResourceDpuport2() throws Exception {
        cut.processResource(inDpuPort2, -1L);
        verify(fttxHardwareService).generateFTTBDpuPort(any(FTTBDpuPortImportView.class), eq(-1L));
    }

    @Test
    public void testToFttbDpuPort2ImportView() {
        final FTTBDpuPortImportView result = cut.mapOltChildPortResource(inDpuPort2);
        assertThat(result.getParent(), equalTo(inDpuPort2.getParentResource().getId()));
        assertThat(result.getPort(), equalTo(inDpuPort2.getName()));
        verifyResult(result);
    }

    private void verifyResult(FTTBDpuPortImportView result){
        assertThat(result.getSchnittstelle(), equalTo(schnittstelle));
        assertThat(result.getLeiste(), equalTo(leiste));
        assertThat(result.getStift(), equalTo(stift));

    }

}
