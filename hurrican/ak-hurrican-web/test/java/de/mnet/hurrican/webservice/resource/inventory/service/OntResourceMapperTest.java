package de.mnet.hurrican.webservice.resource.inventory.service;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HWOntBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.augustakom.hurrican.model.cc.view.FTTHOntImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HWService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;
import de.mnet.hurrican.webservice.resource.inventory.builder.OltChildResourceBuilder;

@Test(groups = BaseTest.UNIT)
public class OntResourceMapperTest {

    @InjectMocks
    @Spy
    OntResourceMapper cut;

    @Mock
    FTTXHardwareService fttxHardwareService;
    @Mock
    HWService hwService;
    @Mock
    CPSService cpsService;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
    }

    public void testGetResourceSpecId() {
        Assert.assertEquals(cut.getResourceSpecId(), OntResourceMapper.ONT_RESOURCE_SPEC_ID);
    }

    public void testCreateImportView() {
        Resource resource = new OltChildResourceBuilder().build();
        FTTHOntImportView importView = cut.createImportView(resource);
        Assert.assertEquals(importView.getClass(), FTTHOntImportView.class);
        verify(cut).mapOltChildResource(eq(resource), any(FTTHOntImportView.class));
    }

    public void testGetChildHwClass() {
        Assert.assertEquals(cut.getChildHwClass(), HWOnt.class);
    }

    public void testIsEqual() throws FindException {
        FTTHOntImportView oltChildView = Mockito.mock(FTTHOntImportView.class);
        HWOnt oltChildHw = Mockito.mock(HWOnt.class);
        when(fttxHardwareService.isOntEqual(oltChildView, oltChildHw)).thenReturn(true);

        Assert.assertTrue(cut.isEqual(oltChildView, oltChildHw));

        verify(fttxHardwareService).isOntEqual(oltChildView, oltChildHw);
    }

    public void testIsChildHwComplete() {
        HWOnt oltChildHw = Mockito.mock(HWOnt.class);
        when(fttxHardwareService.checkIfOntFieldsComplete(oltChildHw)).thenReturn(true);

        Assert.assertTrue(cut.isChildHwComplete(oltChildHw));

        verify(fttxHardwareService).checkIfOntFieldsComplete(oltChildHw);
    }

    public void testCreateChildHwFromView() throws StoreException {
        FTTHOntImportView oltChildView = Mockito.mock(FTTHOntImportView.class);
        HWOnt oltChildHw = Mockito.mock(HWOnt.class);
        long sessionId = 1L;
        when(fttxHardwareService.generateFTTHOnt(oltChildView, sessionId)).thenReturn(oltChildHw);

        Assert.assertEquals(cut.createChildHwFromView(oltChildView, sessionId), oltChildHw);
    }

    public void testGetOltChildType() {
        Assert.assertEquals(cut.getOltChildType(), "ONT");
    }

    public void testIsRackSupported() {
        Assert.assertTrue(cut.isRackSupported(new HWOnt()));
        Assert.assertFalse(cut.isRackSupported(new HWDpo()));
    }

    public void testToResource() throws ResourceProcessException {
        String geraeteBez = "GeraeteBez";
        HWOnt hwOnt = new HWOntBuilder()
                .withGeraeteBez(geraeteBez)
                .withFreigabe(new Date())
                .build();

        Resource resource = cut.toResource(hwOnt);

        Assert.assertEquals(resource.getResourceSpec().getId(), OntResourceMapper.ONT_RESOURCE_SPEC_ID);
        Assert.assertEquals(resource.getResourceSpec().getInventory(), AbstractResourceMapper.COMMAND_INVENTORY);
        Assert.assertEquals(resource.getName(), geraeteBez);
        Assert.assertEquals(resource.getId(), geraeteBez);
        Assert.assertEquals(resource.getInventory(), AbstractResourceMapper.COMMAND_INVENTORY);
        ResourceCharacteristic freigabe = resource.getCharacteristic().get(0);
        Assert.assertNotNull(freigabe);
        Assert.assertEquals(freigabe.getName(), AbstractOltChildResourceMapper.CHARACTERISTIC_FREIGABE);
        Assert.assertNotNull(freigabe.getValue());
    }
}
