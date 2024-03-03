package de.mnet.hurrican.webservice.resource.inventory.service;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.*;
import javax.annotation.*;
import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.validation.ValidationException;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWDpo;
import de.augustakom.hurrican.model.cc.view.FTTBHDpoImportView;
import de.augustakom.hurrican.model.cc.view.FTTHOntImportView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.FTTXHardwareService;
import de.augustakom.hurrican.service.cc.HWService;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.ResourceProcessException;
import de.mnet.hurrican.webservice.resource.inventory.builder.DpuResourceBuilder;
import de.mnet.hurrican.webservice.resource.inventory.builder.OltChildResourceBuilder;
import de.mnet.hurrican.webservice.resource.inventory.builder.ResourceSpecIdBuilder;

@Test(groups = BaseTest.UNIT)
public class AbstractOltChildResourceMapperTest {

    @Mock
    FTTXHardwareService fttxHardwareService;

    @Mock
    HWService hwService;

    @Mock
    CPSService cpsService;

    @Mock
    HWDpo hwOltChild;

    FTTBHDpoImportView oltChildImportView;
    String resourceSpecId = "defaultSpecId";
    Long sessionId = 1L;
    String oltChildType = "OltChildType";
    Class<HWDpo> hwOltChildClass = HWDpo.class;

    @InjectMocks
    @Spy
    DpoResourceMapper cut;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        oltChildImportView = new FTTBHDpoImportView();
        oltChildImportView.setBezeichnung("ont123");
    }

    void mockAbstractMethodsUsingDefaults(Resource resource) throws Exception {
        mockAbstractMethodsUsingDefaults(resource, oltChildImportView);
    }

    void mockAbstractMethodsUsingDefaults(Resource resource, FTTBHDpoImportView oltChildImportView) throws Exception {
        mockAbstractMethods(resource, resourceSpecId, oltChildImportView, hwOltChild, true, true, sessionId, oltChildType);
    }

    void mockAbstractMethods(Resource resource, String resourceSpecId, FTTBHDpoImportView oltChildImportView,
            HWDpo oltChildHw, boolean isEqual, boolean isChildHwComplete, Long sessionId, String oltChildType) throws Exception {
        when(cut.getResourceSpecId()).thenReturn(resourceSpecId);
        when(cut.createImportView(resource)).thenReturn(oltChildImportView);
        when(cut.getChildHwClass()).thenReturn(hwOltChildClass);
        when(cut.isEqual(oltChildImportView, oltChildHw)).thenReturn(isEqual);
        when(cut.isChildHwComplete(oltChildHw)).thenReturn(isChildHwComplete);
        when(cut.createChildHwFromView(oltChildImportView, sessionId)).thenReturn(oltChildHw);
        when(cut.getOltChildType()).thenReturn(oltChildType);
    }

    @DataProvider
    private Object[][] getIsResourceSupportedDP() {
        ResourceSpecId validResSpec = new ResourceSpecIdBuilder()
                .withId(resourceSpecId)
                .withInventory(cut.COMMAND_INVENTORY)
                .build();

        ResourceSpecId invalidId = new ResourceSpecIdBuilder()
                .withId("some other id")
                .withInventory(cut.COMMAND_INVENTORY)
                .build();

        ResourceSpecId invalidCommand = new ResourceSpecIdBuilder()
                .withId(resourceSpecId)
                .withInventory("some other command")
                .build();

        return new Object[][] {
                { prepareResource().withResourceSpec(validResSpec).build(), true },
                { prepareResource().withResourceSpec(null).build(), false },
                { prepareResource().withResourceSpec(invalidId).build(), false },
                { prepareResource().withResourceSpec(invalidCommand).build(), false }
        };
    }

    @Test(dataProvider = "getIsResourceSupportedDP")
    public void testIsResourceSupported(Resource resource, boolean isSupported) throws Exception {
        mockAbstractMethodsUsingDefaults(resource);

        Assert.assertEquals(cut.isResourceSupported(resource), isSupported);
    }

    @Test(expectedExceptions = ResourceProcessException.class)
    public void processResourceShouldFailDueToUnsupportedResource() throws Exception {
        Resource resource = prepareResource().withResourceSpec(null).build();
        mockAbstractMethodsUsingDefaults(resource);
        cut.processResource(resource, sessionId);
    }

    @Test
    public void shouldMapOltChildResource() {
        Resource resource = prepareResource()
                .withSeriennummer("123")
                .withInstallationsstatus("installation-status")
                .build();
        cut.mapOltChildResource(resource, oltChildImportView);
        assertThat(oltChildImportView.getBezeichnung(), equalTo(resource.getName()));

        for (ResourceCharacteristic resChar : resource.getCharacteristic()) {
            switch (resChar.getName().toLowerCase()) {
                case OntResourceMapper.CHARACTERISTIC_HERSTELLER:
                    assertThat(oltChildImportView.getHersteller(), equalTo(extractString(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_SERIENNUMMER:
                    assertThat(oltChildImportView.getSeriennummer(), equalTo(extractString(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_MODELLNUMMER:
                    assertThat(oltChildImportView.getModellnummer(), equalTo(extractString(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_OLT:
                    assertThat(oltChildImportView.getOlt(), equalTo(extractString(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_OLTRACK:
                    assertThat(oltChildImportView.getOltRack(), equalTo(extractLong(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_OLTSUBRACK:
                    assertThat(oltChildImportView.getOltSubrack(), equalTo(extractLong(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_OLTSLOT:
                    assertThat(oltChildImportView.getOltSlot(), equalTo(extractLong(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_OLTPORT:
                    assertThat(oltChildImportView.getOltPort(), equalTo(extractLong(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_OLTGPONID:
                    assertThat(oltChildImportView.getGponId(), equalTo(extractLong(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_STANDORT:
                    assertThat(oltChildImportView.getStandort(), equalTo(extractString(resChar.getValue(), 0)));
                    break;
                case OntResourceMapper.CHARACTERISTIC_RAUMBEZEICHNUNG:
                    assertThat(oltChildImportView.getRaumbezeichung(), equalTo(extractString(resChar.getValue(), 0)));
                    break;
            }
        }

        Assert.assertNotNull(oltChildImportView.getBezeichnung());
        Assert.assertNotNull(oltChildImportView.getHersteller());
        Assert.assertNotNull(oltChildImportView.getSeriennummer());
        Assert.assertNotNull(oltChildImportView.getModellnummer());
        Assert.assertNotNull(oltChildImportView.getOlt());
        Assert.assertNotNull(oltChildImportView.getOltRack());
        Assert.assertNotNull(oltChildImportView.getOltSubrack());
        Assert.assertNotNull(oltChildImportView.getOltSlot());
        Assert.assertNotNull(oltChildImportView.getOltPort());
        Assert.assertNotNull(oltChildImportView.getGponId());
        Assert.assertNotNull(oltChildImportView.getStandort());
        Assert.assertNotNull(oltChildImportView.getRaumbezeichung());
        Assert.assertNotNull(oltChildImportView.getInstallationsstatus());
    }

    @Test
    public void deleteOltChildShouldSucceed() throws Exception {
        Resource resource = prepareResource()
                .withInstallationsstatus(FTTHOntImportView.INSTALLTIONSSTATUS_DELETED)
                .build();
        oltChildImportView.setInstallationsstatus(FTTBHDpoImportView.INSTALLTIONSSTATUS_DELETED);
        mockAbstractMethodsUsingDefaults(resource);
        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);
        when(fttxHardwareService.checkHwOltChildForActiveAuftraegeAndDelete(hwOltChild, true, sessionId)).thenReturn(1L);

        cut.processResource(resource, sessionId);

        verify(fttxHardwareService).checkHwOltChildForActiveAuftraegeAndDelete(hwOltChild, true, sessionId);
        assertThat(oltChildImportView.isInstallationsstatusDeleted(), equalTo(Boolean.TRUE));
    }

    @Test
    public void moveOltChildShouldSucceedWithInactiveAuftrag() throws Exception {
        String seriennummer = "serien-nummer";
        Resource resource = prepareResource()
                .withSeriennummer(seriennummer)
                .build();
        oltChildImportView.setSeriennummer(seriennummer);
        mockAbstractMethodsUsingDefaults(resource, oltChildImportView);

        HWDpo oltChildWithInactiveAuftrag = Mockito.mock(HWDpo.class);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);
        when(hwService.findHWOltChildBySerialNo(seriennummer, hwOltChildClass))
                .thenReturn(Lists.newArrayList(hwOltChild, oltChildWithInactiveAuftrag));
        when(fttxHardwareService.checkHwOltChildForActiveAuftraege(oltChildWithInactiveAuftrag)).thenReturn(null);
        when(fttxHardwareService.deleteHwOltChildWithCpsTx(oltChildWithInactiveAuftrag, true, sessionId)).thenReturn(1L);

        cut.processResource(resource, sessionId);

        verify(fttxHardwareService).deleteHwOltChildWithCpsTx(oltChildWithInactiveAuftrag, true, sessionId);
    }

    @Test(expectedExceptions = ResourceProcessException.class, expectedExceptionsMessageRegExp = "OltChildType Update mit Seriennummer serien-nummer abgelehnt! Zur Seriennummer existieren noch OltChildTypes mit folgenden aktiven Auftraegen: 1,2,3")
    public void moveOltChildShouldFailDueToActiveAuftrag() throws Exception {
        String seriennummer = "serien-nummer";
        Resource resource = prepareResource()
                .withSeriennummer(seriennummer)
                .build();
        oltChildImportView.setSeriennummer(seriennummer);
        mockAbstractMethodsUsingDefaults(resource, oltChildImportView);

        HWDpo oltChildWithInactiveAuftrag = Mockito.mock(HWDpo.class);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);
        when(hwService.findHWOltChildBySerialNo(seriennummer, hwOltChildClass))
                .thenReturn(Lists.newArrayList(hwOltChild, oltChildWithInactiveAuftrag));
        when(fttxHardwareService.checkHwOltChildForActiveAuftraege(oltChildWithInactiveAuftrag)).thenReturn("1,2,3");

        cut.processResource(resource, sessionId);

        verify(fttxHardwareService, never()).deleteHwOltChildWithCpsTx(oltChildWithInactiveAuftrag, true, sessionId);
    }

    @Test
    public void updateOltChildShouldSucceedWithNewSeriennummer() throws Exception {
        String newSerienNummer = "updated-serien-nummer";
        Resource resource = prepareResource()
                .withSeriennummer(newSerienNummer)
                .build();
        oltChildImportView.setSeriennummer(newSerienNummer);
        mockAbstractMethodsUsingDefaults(resource, oltChildImportView);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);
        long hwOltChildId = 10L;
        when(hwOltChild.getId()).thenReturn(hwOltChildId);
        when(hwService.saveHWRack(hwOltChild)).thenReturn(hwOltChild);
        when(cpsService.isCpsTxServiceOrderTypeExecuteable(hwOltChildId, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)).thenReturn(true);

        cut.processResource(resource, sessionId);

        verify(cpsService).findActiveCPSTransactions(null, hwOltChildId, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
        verify(hwOltChild).setSerialNo(newSerienNummer);
        verify(hwService).saveHWRack(hwOltChild);
    }

    @Test
    public void updateOltChildShouldSucceedWithUpdatedSeriennummer() throws Exception {
        String currentSerienNummer = "current-serien-nummer";
        String updatedSerienNummer = "updated-serien-nummer";
        Resource resource = prepareResource()
                .withSeriennummer(updatedSerienNummer)
                .build();
        oltChildImportView.setSeriennummer(updatedSerienNummer);
        mockAbstractMethodsUsingDefaults(resource, oltChildImportView);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);
        long hwOltChildId = 10L;
        when(hwOltChild.getId()).thenReturn(hwOltChildId);
        when(hwOltChild.getSerialNo()).thenReturn(currentSerienNummer);
        when(hwService.saveHWRack(hwOltChild)).thenReturn(hwOltChild);
        Long cpsCreateDevice = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE;
        when(cpsService.isCpsTxServiceOrderTypeExecuteable(hwOltChildId, cpsCreateDevice)).thenReturn(true);
        CPSTransactionResult cpsTransactionResult = mockCPSTransactionResult();
        when(cpsService.createCPSTransaction4OltChild(hwOltChildId, cpsCreateDevice, sessionId)).thenReturn(cpsTransactionResult);

        cut.processResource(resource, sessionId);

        verify(cpsService).findActiveCPSTransactions(null, hwOltChildId, cpsCreateDevice);
        verify(hwOltChild).setSerialNo(updatedSerienNummer);
        verify(hwService).saveHWRack(hwOltChild);
        verifyCpsTransactionGetsCreatedAndSent(hwOltChildId, cpsCreateDevice, sessionId);
    }

    @Test(expectedExceptions = ResourceProcessException.class, expectedExceptionsMessageRegExp = "Daten der Resource stimmen nicht mit einer bereits existierenden OltChildType ueberein. Beim Update einer existierenden OltChildType duerfen sich nur die Seriennummer, die Chassisbezeichnung und der Steckplatz unterscheiden.")
    public void updateOltChildShouldFailDueToNonMatchingAttributes() throws Exception {
        Resource resource = prepareResource().build();
        mockAbstractMethods(resource, resourceSpecId, oltChildImportView, hwOltChild, false, true, sessionId, oltChildType);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);

        cut.processResource(resource, sessionId);
    }

    @Test(expectedExceptions = ResourceProcessException.class, expectedExceptionsMessageRegExp = "Die existierende OltChildType kann nicht aktualisiert werden, da die Anlage der OltChildType im CPS noch laeuft.")
    public void updateOltChildShouldFailDueToActiveCPSTransaction() throws Exception {
        String updatedSerienNummer = "updated-serien-nummer";
        Resource resource = prepareResource()
                .withSeriennummer(updatedSerienNummer)
                .build();
        mockAbstractMethodsUsingDefaults(resource);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);
        long hwOltChildId = 10L;
        when(hwOltChild.getId()).thenReturn(hwOltChildId);
        CPSTransaction activeCpsTransaction = Mockito.mock(CPSTransaction.class);
        when(cpsService.findActiveCPSTransactions(null, hwOltChildId, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)).thenReturn(Arrays.asList(activeCpsTransaction));

        cut.processResource(resource, sessionId);
    }

    @Test(expectedExceptions = ResourceProcessException.class, expectedExceptionsMessageRegExp = "Eine bereits angelegte OltChildType mit gesetzter Seriennummer, darf nicht mit einer leeren Seriennummer modifiziert werden!")
    public void updateOltChildShouldFailDueToRemovingSerialNumber() throws Exception {
        Resource resource = prepareResource().build();
        mockAbstractMethodsUsingDefaults(resource);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);
        long hwOltChildId = 10L;
        when(hwOltChild.getId()).thenReturn(hwOltChildId);
        when(hwOltChild.getSerialNo()).thenReturn("non-empty-serial-nr");

        cut.processResource(resource, sessionId);
    }

    @Test(expectedExceptions = ResourceProcessException.class, expectedExceptionsMessageRegExp = "Der angeforderte ServiceOrderType 14010 ist nicht ausfuehrbar!")
    public void updateOltChildShouldFailDueToNonExecutableServiceOrderType() throws Exception {
        String updatedSerienNummer = "updated-serien-nummer";
        Resource resource = prepareResource()
                .withSeriennummer(updatedSerienNummer)
                .build();
        oltChildImportView.setSeriennummer(updatedSerienNummer);
        mockAbstractMethodsUsingDefaults(resource);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(hwOltChild);
        long hwOltChildId = 10L;
        when(hwOltChild.getId()).thenReturn(hwOltChildId);
        when(hwService.saveHWRack(hwOltChild)).thenReturn(hwOltChild);
        when(cpsService.isCpsTxServiceOrderTypeExecuteable(hwOltChildId, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE)).thenReturn(false);

        cut.processResource(resource, sessionId);
    }

    @Test
    public void createOltChildShouldSucceedWithCPSSend() throws Exception {
        Resource resource = prepareResource().build();
        mockAbstractMethodsUsingDefaults(resource);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(null);
        long hwOltChildId = 10L;
        when(hwOltChild.getId()).thenReturn(hwOltChildId);
        when(hwOltChild.getSerialNo()).thenReturn("some-serial-nr");
        Long cpsCreateDevice = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE;
        when(cpsService.isCpsTxServiceOrderTypeExecuteable(hwOltChildId, cpsCreateDevice)).thenReturn(true);
        CPSTransactionResult cpsTransactionResult = mockCPSTransactionResult();
        when(cpsService.createCPSTransaction4OltChild(hwOltChildId, cpsCreateDevice, sessionId)).thenReturn(cpsTransactionResult);

        cut.processResource(resource, sessionId);

        verifyCpsTransactionGetsCreatedAndSent(hwOltChildId, cpsCreateDevice, sessionId);
    }

    @Test
    public void createDpuOltChildShouldSucceedWithCPSSend() throws Exception {
        Resource resource = prepareDpuResource().build();
        mockAbstractMethodsUsingDefaults(resource);

        when(hwService.findActiveRackByBezeichnung(resource.getName())).thenReturn(null);
        long hwOltChildId = 10L;
        when(hwOltChild.getId()).thenReturn(hwOltChildId);
        when(hwOltChild.getSerialNo()).thenReturn("some-serial-nr");
        Long cpsCreateDevice = CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE;
        when(cpsService.isCpsTxServiceOrderTypeExecuteable(hwOltChildId, cpsCreateDevice)).thenReturn(true);
        CPSTransactionResult cpsTransactionResult = mockCPSTransactionResult();
        when(cpsService.createCPSTransaction4OltChild(hwOltChildId, cpsCreateDevice, sessionId)).thenReturn(cpsTransactionResult);

        cut.processResource(resource, sessionId);

        verifyCpsTransactionGetsCreatedAndSent(hwOltChildId, cpsCreateDevice, sessionId);
    }

    private OltChildResourceBuilder prepareResource() {
        return new OltChildResourceBuilder()
                .withId(resourceSpecId)
                .withInventory(cut.COMMAND_INVENTORY)
                .withName("ont123")
                .withBezeichnung("ONT-000004")
                .withHersteller("Huawei")
                .withModellnummer("O-123-T")
                .withOlt("af7f97bf-5c74-40bd-8")
                .withOltRack("1")
                .withOltSubrack("2")
                .withOltSlot("3")
                .withOltPort("4")
                .withOltGponId("5")
                .withStandort("OrtsteilExample")
                .withRaumbezeichnung("RaumExample")
                .withResourceSpec(
                        new ResourceSpecIdBuilder()
                                .withId(resourceSpecId)
                                .withInventory(cut.COMMAND_INVENTORY)
                                .build()
                );
    }

    private DpuResourceBuilder prepareDpuResource() {
        return (DpuResourceBuilder) new DpuResourceBuilder()
                .withId(resourceSpecId)
                .withInventory(cut.COMMAND_INVENTORY)
                .withName("dpu123")
                .withBezeichnung("DPU-000004")
                .withHersteller("Huawei")
                .withModellnummer("O-123-T")
                .withOlt("af7f97bf-5c74-40bd-8")
                .withOltRack("1")
                .withOltSubrack("2")
                .withOltSlot("3")
                .withOltPort("4")
                .withOltGponId("5")
                .withStandort("OrtsteilExample")
                .withRaumbezeichnung("RaumExample")
                .withResourceSpec(
                        new ResourceSpecIdBuilder()
                                .withId(resourceSpecId)
                                .withInventory(cut.COMMAND_INVENTORY)
                                .build()
                );
    }


    @Nullable
    String extractString(List<String> value, int index) {
        if (value == null || index >= value.size()) {
            return null;
        }
        return value.get(index);
    }

    @Nullable
    Long extractLong(List<String> value, int index) {
        if (value == null || index >= value.size()) {
            return null;
        }
        return Long.valueOf(value.get(index));
    }

    private CPSTransactionResult mockCPSTransactionResult() {
        CPSTransactionResult cpsTransactionResult = Mockito.mock(CPSTransactionResult.class);
        CPSTransaction cpsTransaction = Mockito.mock(CPSTransaction.class);
        when(cpsTransactionResult.getCpsTransactions()).thenReturn(Arrays.asList(cpsTransaction));

        return cpsTransactionResult;
    }

    private void verifyCpsTransactionGetsCreatedAndSent(Long hwOltChildId, Long txType, Long sessionId) throws StoreException {
        verify(cpsService).createCPSTransaction4OltChild(hwOltChildId, txType, sessionId);
        verify(cpsService).sendCpsTx2CPSAsyncWithoutNewTx(any(CPSTransaction.class), eq(sessionId));
    }

    @Test
    public void handleUpdateOltChildSerialNumber() throws FindException, StoreException, ResourceProcessException, ValidationException {
        HWDpo hwDpo = new HWDpo();
        hwDpo.setId(1L);
        when(cpsService.findActiveCPSTransactions(null, hwDpo.getId(),CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE))
                .thenReturn(Collections.EMPTY_LIST);
        oltChildImportView.setSeriennummer("123-123-123");
        doNothing().when(cut).updateExistingOltChild(oltChildImportView, hwDpo, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, sessionId);
        when(cut.isEqual(oltChildImportView, hwDpo)).thenReturn(true);

        cut.handleUpdateOltChild(oltChildImportView, hwDpo, 1L);
        verify(cpsService).findActiveCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
        verify(cut).updateExistingOltChild(oltChildImportView, hwDpo, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, sessionId);
    }

    @Test
    public void handleUpdateOltChildSameSerialNumbers() throws FindException, StoreException, ResourceProcessException, ValidationException {
        HWDpo hwDpo = new HWDpo();
        hwDpo.setId(1L);
        hwDpo.setSerialNo("123-123-123");
        when(cpsService.findActiveCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE))
                .thenReturn(Collections.EMPTY_LIST);
        oltChildImportView.setSeriennummer("123-123-123");
        doReturn(hwDpo).when(cut).updateOltChildFields(oltChildImportView, hwDpo);
        when(cut.isEqual(oltChildImportView, hwDpo)).thenReturn(true);

        cut.handleUpdateOltChild(oltChildImportView, hwDpo, 1L);
        verify(cpsService).findActiveCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
        verify(cut).updateOltChildFields(oltChildImportView, hwDpo);
        verify(cut, never()).updateExistingOltChild(any(), any(), any(), any());
    }

    @Test
    public void handleUpdateOltChildNewSerialNumberAndCreateDevice() throws FindException, StoreException, ResourceProcessException, ValidationException {
        HWDpo hwDpo = new HWDpo();
        hwDpo.setId(1L);
        hwDpo.setSerialNo("123-123-123");
        when(cpsService.findActiveCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE))
                .thenReturn(Collections.EMPTY_LIST);
        oltChildImportView.setSeriennummer("123-123-124");
        doReturn(hwDpo).when(cut).updateOltChildFields(oltChildImportView, hwDpo);
        when(cpsService.findSuccessfulCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE))
                .thenReturn(Arrays.asList(new CPSTransactionExt()));
        when(cut.isEqual(oltChildImportView, hwDpo)).thenReturn(true);

        cut.handleUpdateOltChild(oltChildImportView, hwDpo, 1L);
        verify(cpsService).findActiveCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
        verify(cut).updateExistingOltChild(oltChildImportView, hwDpo, CPSTransaction.SERVICE_ORDER_TYPE_MODIFY_DEVICE, 1L);
    }

    @Test
    public void handleUpdateOltChildNewSerialNumberAndNoCreateDevice() throws FindException, StoreException, ResourceProcessException, ValidationException {
        HWDpo hwDpo = new HWDpo();
        hwDpo.setId(1L);
        hwDpo.setSerialNo("123-123-123");
        when(cpsService.findActiveCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE))
                .thenReturn(Collections.EMPTY_LIST);
        oltChildImportView.setSeriennummer("123-123-124");
        doReturn(hwDpo).when(cut).updateOltChildFields(oltChildImportView, hwDpo);
        when(cpsService.findSuccessfulCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE))
                .thenReturn(Collections.EMPTY_LIST);
        when(cut.isEqual(oltChildImportView, hwDpo)).thenReturn(true);

        cut.handleUpdateOltChild(oltChildImportView, hwDpo, 1L);
        verify(cpsService).findActiveCPSTransactions(null, hwDpo.getId(), CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE);
        verify(cut).updateExistingOltChild(oltChildImportView, hwDpo, CPSTransaction.SERVICE_ORDER_TYPE_CREATE_DEVICE, 1L);
    }

}
