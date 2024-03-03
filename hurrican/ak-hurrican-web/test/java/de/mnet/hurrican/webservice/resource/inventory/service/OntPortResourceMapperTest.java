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
import de.augustakom.hurrican.model.cc.view.FTTHOntPortImportView;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.webservice.resource.inventory.builder.OltChildPortResourceBuilder;
import de.mnet.hurrican.webservice.resource.inventory.builder.ResourceIdBuilder;
import de.mnet.hurrican.webservice.resource.inventory.builder.ResourceSpecIdBuilder;

@Test(groups = BaseTest.UNIT)
public class OntPortResourceMapperTest {

    @InjectMocks
    OntPortResourceMapper cut;

    @Mock
    FTTXHardwareService fttxHardwareService;

    Resource in;

    final String schnittstelle = "POTS";

    @BeforeMethod
    public void setUp() throws Exception {
        in = new OltChildPortResourceBuilder()
                .withName("fsda")
                .withSchnittstelle(schnittstelle)
                .withResourceSpec(
                        new ResourceSpecIdBuilder()
                                .withId(cut.ONT_PORT_RESOURCE_SPEC_ID)
                                .withInventory(cut.COMMAND_INVENTORY)
                                .build()
                )
                .withParentResources(
                        new ResourceIdBuilder()
                                .withId("ontId")
                                .build()
                )
                .build();
        initMocks(this);
    }

    @DataProvider
    Object[][] testIsResourceSupportedDataProvider() {
        return new Object[][] {
                { cut.ONT_PORT_RESOURCE_SPEC_ID, cut.COMMAND_INVENTORY, Boolean.TRUE },
                { "fasdf", cut.COMMAND_INVENTORY, Boolean.FALSE },
                { cut.ONT_PORT_RESOURCE_SPEC_ID, "asdfasd", Boolean.FALSE }
        };
    }

    @Test(dataProvider = "testIsResourceSupportedDataProvider")
    public void testIsResourceSupported(final String specId, final String inventory, final Boolean expectedResult) throws Exception {
        final Resource in =
                new OltChildPortResourceBuilder()
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
    public void testToFtthImportView() {
        final FTTHOntPortImportView result = cut.mapOltChildPortResource(in);
        assertThat(result.getOltChild(), equalTo(in.getParentResource().getId()));
        assertThat(result.getPort(), equalTo(in.getName()));
        assertThat(result.getSchnittstelle(), equalTo(schnittstelle));
    }

    @Test
    public void testProcessResource() throws Exception {
        cut.processResource(in, -1L);
        verify(fttxHardwareService).generateFTTHOntPort(any(FTTHOntPortImportView.class), eq(-1L));
    }

}
