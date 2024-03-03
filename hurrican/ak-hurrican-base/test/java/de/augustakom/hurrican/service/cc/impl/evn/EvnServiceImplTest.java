package de.augustakom.hurrican.service.cc.impl.evn;

import static de.augustakom.hurrican.model.cc.cps.CPSTransaction.SERVICE_ORDER_TYPE_CANCEL_SUB;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.verification.Times;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.tools.messages.AKWarning;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.CPSTransactionExtBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.IntAccountBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionExt;
import de.augustakom.hurrican.model.cc.cps.CPSTransactionResult;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.hardware.HWSwitchType;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.AccountService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.CreateCPSTransactionParameter;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnEnum;
import de.augustakom.hurrican.service.cc.impl.evn.model.EvnServiceException;
import de.mnet.common.tools.DateConverterUtils;

/**
 * Created by winterul on 03.08.2016.
 */
public class EvnServiceImplTest {

    @Mock
    private CPSService cpsService;
    @Mock
    private AccountService accountService;
    @Mock
    private CCAuftragService auftragService;

    @InjectMocks
    @Spy
    private EvnServiceImpl cut;


    @BeforeMethod
    public void setUp() {
        initMocks(this);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetEvnData() throws EvnServiceException {
        long auftragId = 1L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();

        IntAccount intAccount = new IntAccountBuilder().build();

        EvnEnum evnEnum = cut.getEvnData(auftragDaten, intAccount);
        assertEquals(evnEnum, EvnEnum.UNKNOWN);

        intAccount.setEvnStatus(Boolean.TRUE);
        evnEnum = cut.getEvnData(auftragDaten, intAccount);
        assertEquals(evnEnum, EvnEnum.ACTIVATED);

        intAccount.setEvnStatus(Boolean.FALSE);
        evnEnum = cut.getEvnData(auftragDaten, intAccount);
        assertEquals(evnEnum, EvnEnum.DEACTIVATED);

        intAccount.setEvnStatusPending(Boolean.TRUE);
        evnEnum = cut.getEvnData(auftragDaten, intAccount);
        assertEquals(evnEnum, EvnEnum.CHANGE_IN_PROGRESS);
    }

    @Test
    public void testActivateEvn() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;
        long auftragId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        IntAccount intAccount = new IntAccountBuilder().build();

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);
        doReturn(auftragDaten).when(cut).getAuftragByRadiusAccount(anyString());
        doReturn(true).when(cut).verifyCpsTransactionsForProvisionierung(auftragDaten);
        doNothing().when(cut).doCpsProvisionierung(auftragDaten, sessionId, false);

        cut.activateEvn(radiusAccountId, sessionId);
    }

    @Test
    public void testActivateEvnStatusActive() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;
        long auftragId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        IntAccount intAccount = new IntAccountBuilder().build();
        intAccount.setEvnStatus(Boolean.TRUE);

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);
        doReturn(auftragDaten).when(cut).getAuftragByRadiusAccount(anyString());
        doNothing().when(cut).doCpsProvisionierung(auftragDaten, sessionId, false);
        doReturn(true).when(cut).verifyCpsTransactionsForProvisionierung(auftragDaten);

        cut.activateEvn(radiusAccountId, sessionId);
    }

    @Test
    public void testActivateEvnChangeInProgress() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;
        long auftragId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        IntAccount intAccount = new IntAccountBuilder().build();
        intAccount.setEvnStatusPending(Boolean.TRUE);

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);
        doReturn(auftragDaten).when(cut).getAuftragByRadiusAccount(anyString());
        doNothing().when(cut).doCpsProvisionierung(auftragDaten, sessionId, false);

        cut.activateEvn(radiusAccountId, sessionId);
    }

    @Test(expectedExceptions = EvnServiceException.class, expectedExceptionsMessageRegExp = "Fehler bei der Erfassung von Auftrag Id für radiusAccountId .*")
    public void testActivateEvnAccountNotFound() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;
        IntAccount intAccount = new IntAccountBuilder().build();

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);

        cut.activateEvn(radiusAccountId, sessionId);
    }

    @Test(expectedExceptions = EvnServiceException.class, expectedExceptionsMessageRegExp = "Account .* nicht gefunden")
    public void testActivateIntAccountNotFound() throws EvnServiceException {
        String radiusAccountId = "1";
        long sessionId = 2L;

        cut.activateEvn(radiusAccountId, sessionId);
    }

    @Test(expectedExceptions = EvnServiceException.class)
    public void testActivateEvnIntAccountFindException() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;

        doThrow(FindException.class).when(accountService).findIntAccount(radiusAccountId);

        cut.activateEvn(radiusAccountId, sessionId);
    }

    @Test
    public void testDeactivateEvn() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;
        long auftragId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        IntAccount intAccount = new IntAccountBuilder().build();

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);
        doReturn(auftragDaten).when(cut).getAuftragByRadiusAccount(anyString());
        doNothing().when(cut).doCpsProvisionierung(auftragDaten, sessionId, false);
        doReturn(true).when(cut).verifyCpsTransactionsForProvisionierung(auftragDaten);

        cut.deactivateEvn(radiusAccountId, sessionId);

        verify(cut, times(1)).doCpsProvisionierung(anyObject(), anyLong(), anyBoolean());
    }

    @Test
    public void testDeactivateEvn_CPSTxVerfication_Failed_NoCpsTx() throws Exception {
        String radiusAccountId = "1";
        long sessionId = 2L;
        long auftragId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        IntAccount intAccount = new IntAccountBuilder().build();

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);
        doReturn(auftragDaten).when(cut).getAuftragByRadiusAccount(anyString());
        when(cpsService.findLatestCPSTransactions4TechOrder(eq(auftragDaten.getAuftragId()), anyObject(), anyObject())).thenReturn(Optional.empty());

        cut.deactivateEvn(radiusAccountId, sessionId);
        verify(cpsService, times(1)).findLatestCPSTransactions4TechOrder(anyLong(), anyObject(), anyObject());
        verify(cut, times(0)).doCpsProvisionierung(anyObject(), anyLong(), anyBoolean());
        verify(cpsService, times(0)).isCPSProvisioningAllowed(anyLong(),anyObject(), anyBoolean(),anyBoolean(),anyBoolean());
        verify(cpsService, times(0)).createCPSTransaction(anyObject());
    }

    @Test
    public void testDeactivateEvn_CPSTxVerfication_Failed_HasDeleteCpsTx() throws Exception {
        String radiusAccountId = "1";
        long sessionId = 2L;
        long auftragId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        IntAccount intAccount = new IntAccountBuilder().build();

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);
        doReturn(auftragDaten).when(cut).getAuftragByRadiusAccount(anyString());

        final CPSTransaction cpsTx = new CPSTransaction();
        cpsTx.setServiceOrderType(SERVICE_ORDER_TYPE_CANCEL_SUB);
        when(cpsService.findLatestCPSTransactions4TechOrder(eq(auftragDaten.getAuftragId()), anyObject(), anyObject())).thenReturn(Optional.of(cpsTx));

        cut.deactivateEvn(radiusAccountId, sessionId);
        verify(cpsService, times(1)).findLatestCPSTransactions4TechOrder(anyLong(), anyObject(), anyObject());
        verify(cut, times(0)).doCpsProvisionierung(anyObject(), anyLong(), anyBoolean());
        verify(cpsService, times(0)).isCPSProvisioningAllowed(anyLong(),anyObject(), anyBoolean(),anyBoolean(),anyBoolean());
        verify(cpsService, times(0)).createCPSTransaction(anyObject());
    }

    @Test
    public void testDeactivateEvnStatusInctive() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;
        long auftragId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        IntAccount intAccount = new IntAccountBuilder().build();
        intAccount.setEvnStatus(Boolean.FALSE);

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);
        doReturn(auftragDaten).when(cut).getAuftragByRadiusAccount(anyString());
        doNothing().when(cut).doCpsProvisionierung(auftragDaten, sessionId, false);
        doReturn(true).when(cut).verifyCpsTransactionsForProvisionierung(auftragDaten);

        cut.deactivateEvn(radiusAccountId, sessionId);
    }

    @Test
    public void testDeactivateEvnChangeInProgress() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;
        long auftragId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        IntAccount intAccount = new IntAccountBuilder().build();
        intAccount.setEvnStatusPending(Boolean.TRUE);

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);
        doReturn(auftragDaten).when(cut).getAuftragByRadiusAccount(anyString());
        doNothing().when(cut).doCpsProvisionierung(auftragDaten, sessionId, false);

        cut.deactivateEvn(radiusAccountId, sessionId);
    }

    @Test(expectedExceptions = EvnServiceException.class, expectedExceptionsMessageRegExp = "Fehler bei der Erfassung von Auftrag Id für radiusAccountId .*")
    public void testDeactivateEvnAccountNotFound() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        long sessionId = 2L;
        IntAccount intAccount = new IntAccountBuilder().build();

        when(accountService.findIntAccount(anyString())).thenReturn(intAccount);

        cut.deactivateEvn(radiusAccountId, sessionId);
    }

    @Test(expectedExceptions = EvnServiceException.class, expectedExceptionsMessageRegExp = "Account .* nicht gefunden")
    public void testDeactivateIntAccountNotFound() throws EvnServiceException {
        String radiusAccountId = "1";
        long sessionId = 2L;

        cut.activateEvn(radiusAccountId, sessionId);
    }

    @Test
    public void testGetAuftragByRadiusAccount() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        Long intAccountId = 2L;
        Long auftragId = 3L;

        IntAccount intAccount = new IntAccountBuilder().withId(intAccountId).build();
        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT), any(LocalDate.class))).thenReturn(intAccount);

        AuftragDaten auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNull(auftragDaten);

        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT), any(LocalDate.class))).thenReturn(null);
        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT_KONFIG), any(LocalDate.class))).thenReturn(intAccount);

        auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNull(auftragDaten);


        auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNull(auftragDaten);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten2 = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .build();

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.NSP);
        auftragTechnik.setAuftragId(auftragId);
        auftragTechnik.setHwSwitch(hwSwitch);
        auftragTechnik.setIntAccountId(intAccountId);
        when(auftragService.findAuftragTechnik4IntAccount(intAccountId)).thenReturn(Arrays.asList(auftragTechnik));

        when(auftragService.findAuftragTechnik4IntAccount(intAccountId)).thenReturn(Arrays.asList(auftragTechnik));

        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten2);
        auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNotNull(auftragDaten);
    }

    @Test
    public void testGetAuftragByRadiusAccount_InBetrieb() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        Long intAccountId = 2L;
        Long auftragId = 3L;

        IntAccount intAccount = new IntAccountBuilder().withId(intAccountId).build();
        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT), any(LocalDate.class))).thenReturn(intAccount);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten2 = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.IN_BETRIEB)
                .build();

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.NSP);
        auftragTechnik.setAuftragId(auftragId);
        auftragTechnik.setHwSwitch(hwSwitch);
        auftragTechnik.setIntAccountId(intAccountId);

        when(auftragService.findAuftragTechnik4IntAccount(intAccountId)).thenReturn(Arrays.asList(auftragTechnik));
        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten2);

        AuftragDaten auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNotNull(auftragDaten);
    }

    @Test
    public void testGetAuftragByRadiusAccount_Gekuendigt_Yesterday() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        Long intAccountId = 2L;
        Long auftragId = 3L;

        IntAccount intAccount = new IntAccountBuilder().withId(intAccountId).build();
        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT), any(LocalDate.class))).thenReturn(intAccount);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten2 = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withKuendigung(DateConverterUtils.asDate(LocalDate.now().minusDays(1)))
                .build();

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.NSP);
        auftragTechnik.setAuftragId(auftragId);
        auftragTechnik.setHwSwitch(hwSwitch);
        auftragTechnik.setIntAccountId(intAccountId);

        when(auftragService.findAuftragTechnik4IntAccount(intAccountId)).thenReturn(Arrays.asList(auftragTechnik));
        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten2);

        final AuftragDaten auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNull(auftragDaten);// not present
    }

    @Test
    public void testGetAuftragByRadiusAccount_Gekuendigt_Today() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        Long intAccountId = 2L;
        Long auftragId = 3L;

        IntAccount intAccount = new IntAccountBuilder().withId(intAccountId).build();
        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT), any(LocalDate.class))).thenReturn(intAccount);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten2 = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withKuendigung(DateConverterUtils.asDate(LocalDate.now()))
                .build();

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.NSP);
        auftragTechnik.setAuftragId(auftragId);
        auftragTechnik.setHwSwitch(hwSwitch);
        auftragTechnik.setIntAccountId(intAccountId);

        when(auftragService.findAuftragTechnik4IntAccount(intAccountId)).thenReturn(Arrays.asList(auftragTechnik));
        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten2);

        final AuftragDaten auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNull(auftragDaten);  // not present
    }

    @Test
    public void testGetAuftragByRadiusAccount_Gekuendigt_Tomorrow() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        Long intAccountId = 2L;
        Long auftragId = 3L;

        IntAccount intAccount = new IntAccountBuilder().withId(intAccountId).build();
        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT), any(LocalDate.class))).thenReturn(intAccount);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten2 = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT)
                .withKuendigung(DateConverterUtils.asDate(LocalDate.now().plusDays(1)))
                .build();

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.NSP);
        auftragTechnik.setAuftragId(auftragId);
        auftragTechnik.setHwSwitch(hwSwitch);
        auftragTechnik.setIntAccountId(intAccountId);

        when(auftragService.findAuftragTechnik4IntAccount(intAccountId)).thenReturn(Arrays.asList(auftragTechnik));
        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten2);

        final AuftragDaten auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNotNull(auftragDaten);
    }

    @Test
    public void testGetAuftragByRadiusAccount_In_Kuendigung() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        Long intAccountId = 2L;
        Long auftragId = 3L;

        IntAccount intAccount = new IntAccountBuilder().withId(intAccountId).build();
        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT), any(LocalDate.class))).thenReturn(intAccount);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten2 = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.KUENDIGUNG)
                .withKuendigung(DateConverterUtils.asDate(LocalDate.now().plusDays(1)))
                .build();

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.NSP);
        auftragTechnik.setAuftragId(auftragId);
        auftragTechnik.setHwSwitch(hwSwitch);
        auftragTechnik.setIntAccountId(intAccountId);

        when(auftragService.findAuftragTechnik4IntAccount(intAccountId)).thenReturn(Arrays.asList(auftragTechnik));
        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten2);

        final AuftragDaten auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNotNull(auftragDaten);
    }

    public void testGetAuftragByRadiusAccount_Not_Valid() throws EvnServiceException, FindException {
        String radiusAccountId = "1";
        Long intAccountId = 2L;
        Long auftragId = 3L;

        IntAccount intAccount = new IntAccountBuilder().withId(intAccountId).build();
        when(accountService.findIntAccount(eq(radiusAccountId), eq(IntAccount.LINR_EINWAHLACCOUNT), any(LocalDate.class))).thenReturn(intAccount);

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten2 = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragBuilder(auftragBuilder)
                .withStatusId(AuftragStatus.KUENDIGUNG)
                .withGueltigBis(DateConverterUtils.asDate(LocalDate.now().minusDays(1)))
                .withKuendigung(DateConverterUtils.asDate(LocalDate.now().plusDays(1)))
                .build();

        final AuftragTechnik auftragTechnik = spy(new AuftragTechnik());
        final HWSwitch hwSwitch = spy(new HWSwitch());
        hwSwitch.setType(HWSwitchType.NSP);
        auftragTechnik.setAuftragId(auftragId);
        auftragTechnik.setHwSwitch(hwSwitch);
        auftragTechnik.setIntAccountId(intAccountId);

        when(auftragService.findAuftragTechnik4IntAccount(intAccountId)).thenReturn(Arrays.asList(auftragTechnik));
        when(auftragService.findAuftragDatenByAuftragIdTx(auftragId)).thenReturn(auftragDaten2);

        final AuftragDaten auftragDaten = cut.getAuftragByRadiusAccount(radiusAccountId);
        assertNull(auftragDaten, "Not valid / GueltigBis = yesterday");
    }

    @Test(expectedExceptions = EvnServiceException.class, expectedExceptionsMessageRegExp = "Fehler bei der Erfassung von Auftrag Id für radiusAccountId .*")
    public void testGetAuftragByRadiusAccountAccountNotFound() throws EvnServiceException {
        String radiusAccountId = "1";

        cut.getAuftragByRadiusAccount(radiusAccountId);
    }

    @Test
    public void testGetIntAccounts4Auftrag() throws FindException {
        Long auftragId = 1L;
        Long intAccountId = 2L;

        List<IntAccount> accountList = cut.getIntAccounts4Auftrag(auftragId);
        assertEquals(accountList, Collections.EMPTY_LIST);

        IntAccount intAccount = new IntAccountBuilder().withId(intAccountId).build();
        List<IntAccount> intAccountList = Arrays.asList(intAccount);
        when(accountService.findIntAccounts4Auftrag(auftragId)).thenReturn(intAccountList);

        accountList = cut.getIntAccounts4Auftrag(auftragId);
        assertEquals(accountList, intAccountList);
    }

    @Test
    public void testGetIntAccounts4AuftragFindException() throws FindException {
        Long auftragId = 1L;

        doThrow(FindException.class).when(accountService).findIntAccounts4Auftrag(auftragId);
        List<IntAccount> accountList = cut.getIntAccounts4Auftrag(auftragId);
        assertEquals(accountList, Collections.EMPTY_LIST);
    }

    @Test
    public void testHasActiveCpsTransaction() throws FindException {
        Long auftragId = 1L;
        Long auftragNoOrigId = 2L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(auftragNoOrigId)
                .withAuftragBuilder(auftragBuilder)
                .build();
        boolean result = cut.hasActiveCpsTransaction(auftragDaten);
        assertFalse(result);

        final CPSTransactionExt cpsTransactionExt = new CPSTransactionExtBuilder().build();
        List<CPSTransactionExt> cpsTransactionList = Arrays.asList(cpsTransactionExt);
        when(cpsService.findActiveCPSTransactions(auftragNoOrigId)).thenReturn(cpsTransactionList);
        result = cut.hasActiveCpsTransaction(auftragDaten);
        assertTrue(result);
    }

    @Test
    public void testHasActiveCpsTransactionFindException() throws FindException {
        Long auftragId = 1L;
        Long auftragNoOrigId = 2L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(auftragNoOrigId)
                .withAuftragBuilder(auftragBuilder)
                .build();

        doThrow(FindException.class).when(cpsService).findActiveCPSTransactions(auftragNoOrigId);

        boolean result = cut.hasActiveCpsTransaction(auftragDaten);
        assertFalse(result);
    }

    @Test
    public void testDoCpsProvisionierung() throws EvnServiceException, FindException, StoreException {
        Long auftragId = 1L;
        Long auftragNoOrigId = 2L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(auftragNoOrigId)
                .withAuftragBuilder(auftragBuilder)
                .build();

        CPSProvisioningAllowed cpsProvisioningAllowed = new CPSProvisioningAllowed(true, null);
        CPSTransactionResult cpsTransactionResult = new CPSTransactionResult();

        when(cpsService.isCPSProvisioningAllowed(auftragId, null, false, false, true)).thenReturn(cpsProvisioningAllowed);
        when(cpsService.createCPSTransaction(any(CreateCPSTransactionParameter.class))).thenReturn(cpsTransactionResult);

        cut.doCpsProvisionierung(auftragDaten, eq(anyLong()), eq(true));
    }

    @Test(expectedExceptions = EvnServiceException.class, expectedExceptionsMessageRegExp = "Techn. Auftrag .* Provisionierung nicht erlaubt .*")
    public void testDoCpsProvisionierungProvisioningNotAllowed() throws EvnServiceException, FindException {
        Long auftragId = 1L;
        Long auftragNoOrigId = 2L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(auftragNoOrigId)
                .withAuftragBuilder(auftragBuilder)
                .build();

        CPSProvisioningAllowed cpsProvisioningAllowed = new CPSProvisioningAllowed(false, "reason");
        when(cpsService.isCPSProvisioningAllowed(auftragId, null, false, false, true)).thenReturn(cpsProvisioningAllowed);
        cut.doCpsProvisionierung(auftragDaten, eq(anyLong()), eq(true));
    }

    @Test(expectedExceptions = EvnServiceException.class, expectedExceptionsMessageRegExp = "Techn. Auftrag .* Fehler bei der Provisionierung.")
    public void testDoCpsProvisionierungTechnicalError() throws EvnServiceException, FindException {
        Long auftragId = 1L;
        Long auftragNoOrigId = 2L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(auftragNoOrigId)
                .withAuftragBuilder(auftragBuilder)
                .build();

        when(cpsService.isCPSProvisioningAllowed(auftragId, null, false, false, true)).thenThrow(FindException.class);
        cut.doCpsProvisionierung(auftragDaten, eq(anyLong()), eq(true));
    }

    @Test(expectedExceptions = EvnServiceException.class, expectedExceptionsMessageRegExp = "Techn. Auftrag .* Provisionierungsfehler.")
    public void testDoCpsProvisionierungWithWarnings() throws EvnServiceException, FindException, StoreException {
        Long auftragId = 1L;
        Long auftragNoOrigId = 2L;
        Long sessionId = 3L;

        AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        AuftragDaten auftragDaten = new AuftragDatenBuilder()
                .withAuftragId(auftragId)
                .withAuftragNoOrig(auftragNoOrigId)
                .withAuftragBuilder(auftragBuilder)
                .build();

        AKWarnings akWarnings = new AKWarnings();
        akWarnings.addAKWarning(new AKWarning("", ""));

        CPSProvisioningAllowed cpsProvisioningAllowed = new CPSProvisioningAllowed(true, null);
        CPSTransactionResult cpsTransactionResult = new CPSTransactionResult(new ArrayList<>(), akWarnings);

        when(cpsService.isCPSProvisioningAllowed(auftragId, null, false, false, true)).thenReturn(cpsProvisioningAllowed);
        when(cpsService.createCPSTransaction(any(CreateCPSTransactionParameter.class))).thenReturn(cpsTransactionResult);

        cut.doCpsProvisionierung(auftragDaten, sessionId, true);
    }

    @Test
    public void testCompleteEvnChange() {
        Long auftragId = 1L;
        Long intAccountId = 2L;

        IntAccount intAccount = new IntAccountBuilder()
                .withId(intAccountId)
                .build();
        intAccount.setEvnStatusPending(Boolean.TRUE);

        when(cut.getIntAccounts4Auftrag(auftragId)).thenReturn(Arrays.asList(intAccount));

        cut.completeEvnChange(auftragId);
    }

    @Test
    public void testCompleteEvnChangeAccountsFindException() throws FindException {
        Long auftragId = 1L;

        doThrow(FindException.class).when(accountService).findIntAccounts4Auftrag(auftragId);

        cut.completeEvnChange(auftragId);
    }

    @Test
    public void testCompleteEvnChangeSaveStoreException() throws StoreException {
        Long auftragId = 1L;
        Long intAccountId = 2L;

        IntAccount intAccount = new IntAccountBuilder()
                .withId(intAccountId)
                .build();
        intAccount.setEvnStatusPending(Boolean.TRUE);

        when(cut.getIntAccounts4Auftrag(auftragId)).thenReturn(Arrays.asList(intAccount));
        doThrow(StoreException.class).when(accountService).saveIntAccountInSeparateTransaction(intAccount, true);

        cut.completeEvnChange(auftragId);
    }
}