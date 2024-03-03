/**
 *  * Copyright (c) 2010 - M-net Telekommunikations GmbH  * All rights reserved.  *
 * -------------------------------------------------------  * File created: 15.06.2010 12:09:20  
 */

package de.augustakom.hurrican.service.cc.impl;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static de.augustakom.hurrican.model.cc.Abteilung.*;
import static de.augustakom.hurrican.model.cc.Endstelle.*;
import static de.mnet.common.tools.DateConverterUtils.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEmptyString.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.RandomTools;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.dao.cc.VerlaufAbteilungDAO;
import de.augustakom.hurrican.dao.cc.VerlaufDAO;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.AuftragAktion;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.AuftragTechnik;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.BAVerlaufConfig;
import de.augustakom.hurrican.model.cc.BAVerlaufZusatzBuilder;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.HWOltBuilder;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnungBuilder;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilungBuilder;
import de.augustakom.hurrican.model.cc.VerlaufBuilder;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.cps.CPSProvisioningAllowed;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSServiceOrderData.LazyInitMode;
import de.augustakom.hurrican.model.cc.ffm.FfmQualificationBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspBuilder;
import de.augustakom.hurrican.model.cc.fttx.A10NspPort;
import de.augustakom.hurrican.model.cc.fttx.A10NspPortBuilder;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.Auftrag2EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContract;
import de.augustakom.hurrican.model.cc.fttx.EkpFrameContractBuilder;
import de.augustakom.hurrican.model.cc.fttx.EqVlan;
import de.augustakom.hurrican.model.cc.hardware.HWOlt;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.billing.BillingWorkflowService;
import de.augustakom.hurrican.service.cc.BAConfigService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CPSService;
import de.augustakom.hurrican.service.cc.EndgeraeteService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.InnenauftragService;
import de.augustakom.hurrican.service.cc.LockService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.augustakom.hurrican.service.cc.fttx.EkpFrameContractService;
import de.augustakom.hurrican.service.cc.fttx.VlanService;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.NotifyPortOrderUpdate;
import de.mnet.esb.cdm.resource.resourceordermanagementnotificationservice.v1.ResourceOrderManagementNotificationService;

/**
 * Tests fuer {@link BAServiceImpl}.
 */
@Test(groups = BaseTest.UNIT)
public class BAServiceImplTest extends BaseTest {

    public static final String EXCEPTION_MSG_INVALID_PARAMETERS_TO_STORE = "Es wurden ung.*ltige Parameter zum Speichern angegeben!";
    @Spy
    private BAServiceImpl sut = new BAServiceImpl();
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private EndstellenService endstellenService;
    @Mock
    private HVTService hvtService;
    @Mock
    private BAConfigService baConfigService;
    @Mock
    private EndgeraeteService endgeraeteService;
    @Mock
    private CPSService cpsService;
    @Mock
    private VerlaufDAO verlaufDAO;
    @Mock
    private VerlaufAbteilungDAO verlaufAbteilungDAO;
    @Mock
    private LockService lockService;
    @Mock
    private AKUserService userService;
    @Mock
    private ProduktService produktService;
    @Mock
    private BillingWorkflowService billingWorkflowService;
    @Mock
    private InnenauftragService innenauftragService;
    @Mock
    private CCLeistungsService leistungsService;
    @Mock
    private FFMService ffmService;
    @Mock
    private ReferenceService referenceService;
    @Mock
    private NiederlassungService niederlassungService;
    @Mock
    private PhysikService physikService;
    @Mock
    private HardwareDAO hardwareDAO;
    @Mock
    private EkpFrameContractService ekpFrameContractService;
    @Mock
    private VlanService vlanService;
    @Mock
    private ResourceOrderManagementNotificationService romNotificationService;

    @BeforeMethod
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut.setAuftragService(auftragService);
        sut.setEndstellenService(endstellenService);
        sut.setHvtService(hvtService);
        sut.setBaConfigService(baConfigService);
        sut.setEndgeraeteService(endgeraeteService);
        sut.setCpsService(cpsService);
        sut.setVerlaufDAO(verlaufDAO);
        sut.setVerlaufAbteilungDAO(verlaufAbteilungDAO);
        sut.setLockService(lockService);
        sut.setProduktService(produktService);
        sut.setAkUserService(userService);
        sut.setInnenauftragService(innenauftragService);
        sut.setLeistungsService(leistungsService);
        sut.setFfmService(ffmService);
        sut.setReferenceService(referenceService);
        sut.setRomNotificationService(romNotificationService);
        sut.setNiederlassungService(niederlassungService);
        sut.setPhysikService(physikService);
        sut.setHardwareDAO(hardwareDAO);
        sut.setEkpFrameContractService(ekpFrameContractService);
        sut.setVlanService(vlanService);

        doReturn(billingWorkflowService).when(sut).getBillingService(BillingWorkflowService.class);
    }

    @DataProvider
    public Object[][] autoDispatchConfigs() {
        return new Object[][] {
                // cfgAuto - Verlaufsconfig erlaubt automatische Verteilung
                // hvtAuto - HVTStandort erlaubt automatische Verteilung
                // hvtCPS - HVTStandort ist für CPS-Provisionierung konfiguriert
                // verlaufCPS - Verlauf ist für CPS-Provisionierung konfiguriert
                // auftragCPS - Auftrag ist für CPS-Provisionierung konfiguriert
                // extInstall - Externe Installation für BA erforderlich
                // result - darf automatisch verteilt werden?

                // cfgAuto, cpsNecessary, hvtAuto, hvtCPS, verlaufCPS, auftragCPS, extInstall, result
                new Object[] { true, false, true, true, true, true, false, true },
                new Object[] { true, false, true, true, true, true, true, false },
                new Object[] { false, false, true, true, true, true, false, false },
                new Object[] { true, false, false, true, true, true, false, false },
                new Object[] { true, true, true, false, true, true, false, false },
                new Object[] { true, false, true, false, true, true, false, true },
                new Object[] { true, true, true, false, true, true, false, false },
                new Object[] { true, true, true, true, false, true, false, false },
                new Object[] { true, false, true, true, false, true, false, true },
                new Object[] { true, true, true, true, true, false, false, false }, };
    }

    @Test(dataProvider = "autoDispatchConfigs")
    public void testIsAutomaticallyDispatchable(boolean cfgAuto, boolean cpsNecessary, boolean hvtAuto, boolean hvtCPS,
            boolean verlaufCPS, boolean auftragCPS, boolean techLsPrevent, boolean automatic) throws Exception {
        AuftragDatenBuilder adb = new AuftragDatenBuilder();
        AuftragDaten auftragDaten = adb.withProdId(42L).setPersist(false).build();
        AuftragBuilder ab = new AuftragBuilder();
        ab.withRandomId().withAuftragDatenBuilder(adb).setPersist(false).build();

        AuftragTechnikBuilder atb = new AuftragTechnikBuilder();
        AuftragTechnik auftragTechnik = atb.setPersist(false).build();

        VerlaufBuilder vb = new VerlaufBuilder();
        Verlauf verlauf = vb.withRandomId().withAnlass(BAVerlaufAnlass.NEUSCHALTUNG)
                .withPreventCPSProvisioning(!verlaufCPS).setPersist(false).build();

        EndstelleBuilder eb = new EndstelleBuilder();
        Endstelle endstelle = eb.withRandomId().setPersist(false).build();

        HVTStandortBuilder hvtsb = new HVTStandortBuilder();
        HVTStandort hvtStandort = hvtsb.withRandomId().withAutoVerteilen(hvtAuto).withCpsProvisioning(hvtCPS)
                .setPersist(false).build();

        BAVerlaufConfig cfg = new BAVerlaufConfig();
        cfg.setAutoVerteilen(cfgAuto);
        cfg.setCpsNecessary(cpsNecessary);

        TechLeistung techLs = new TechLeistungBuilder().withPreventAutoDispatch(techLsPrevent).build();

        when(auftragService.findAuftragDatenByAuftragIdTx(verlauf.getAuftragId())).thenReturn(auftragDaten);
        when(auftragService.findAuftragTechnikByAuftragIdTx(verlauf.getAuftragId())).thenReturn(auftragTechnik);
        when(endstellenService.findEndstelle4Auftrag(verlauf.getAuftragId(), ENDSTELLEN_TYP_B)).thenReturn(
                endstelle);
        when(hvtService.findHVTStandort(endstelle.getHvtIdStandort())).thenReturn(hvtStandort);
        when(baConfigService.findBAVerlaufConfig(verlauf.getAnlass(), auftragDaten.getProdId(), false)).thenReturn(cfg);
        when(verlaufDAO.findById(verlauf.getId(), Verlauf.class)).thenReturn(verlauf);
        when(leistungsService.findTechLeistungen4Verlauf(verlauf.getId(), true)).thenReturn(Arrays.asList(techLs));
        when(cpsService.isCPSProvisioningAllowed(verlauf.getAuftragId(), LazyInitMode.noInitialLoad, true, false, true))
                .thenReturn(new CPSProvisioningAllowed(auftragCPS && hvtCPS, null));

        boolean result = sut.isAutomaticallyDispatchable(verlauf);
        assertEquals(result, automatic);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = EXCEPTION_MSG_INVALID_PARAMETERS_TO_STORE)
    public void amPrAbschliessen_SomeParameterNull() throws StoreException {
        doThrow(new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE)).when(sut)
                .checkParameterNullAndThrowExceptionIf(Mockito.anyVararg());
        sut.amPrAbschliessen(1234L, 12341234L);
    }

    @Test(expectedExceptions = StoreException.class/* , expectedExceptionsMessageRegExp = ".*Beim Abschluss.*" */)
    public void amPrAbschliessen_NoVerlaufFound() throws StoreException, FindException {
        final Long verlaufId = (long) 1234;
        doThrow(new StoreException("TEST")).when(sut).findVerlaufAndThrowExceptionIfNotFound(Mockito.eq(verlaufId));
        sut.amPrAbschliessen(verlaufId, 2341234L);
    }

    @Test
    public void amPrAbschliessen_VerlaufNotPossible() throws StoreException, FindException, AKAuthenticationException {
        final Long verlaufId = 1234L;
        Verlauf verlauf = new Verlauf();
        Set<Long> orderIds = Sets.newHashSet(2L, 3L, 4L);
        verlauf.setAuftragId(1L);
        verlauf.setSubAuftragsIds(orderIds);
        verlauf.setNotPossible(Boolean.TRUE);
        verlauf.setStatusIdAlt(AuftragStatus.KUENDIGUNG_TECHN_REAL);
        verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_DISPO);
        doNothing().when(sut).checkParameterNullAndThrowExceptionIf(Mockito.anyVararg());
        doReturn(verlauf).when(sut).findVerlaufAndThrowExceptionIfNotFound(Mockito.eq(verlaufId));
        doNothing().when(sut).setVerlaufAbteilungAmErledigt(Mockito.anyLong(), Mockito.anyLong());
        doNothing().when(sut).findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(Mockito.anyLong());
        doNothing().when(sut).checkIfBauauftragBereitsAbgeschlossen(Mockito.eq(verlauf));
        doNothing().when(sut).saveVerlauf(Mockito.eq(verlauf));
        doReturn(false).when(sut).findAuftragDatenAndChangeStatusErledigtIfPossible(Mockito.anyLong(),
                Mockito.eq(verlauf));

        Verlauf result = sut.amPrAbschliessen(verlaufId, 5678568L);

        assertFalse(result.getAkt());
        assertEquals(result.getVerlaufStatusId(), VerlaufStatus.VERLAUF_ABGESCHLOSSEN);
        verify(lockService, times(0)).finishActiveLocks(Mockito.anyLong());
    }

    @Test
    public void amPrAbschliessen_AllAuftragsdatenCorrectlyChanged() throws StoreException, FindException,
            AKAuthenticationException {
        final Long verlaufId = 1234L;
        Set<Long> orderIds = Sets.newHashSet(2L, 3L, 4L);
        Verlauf verlauf = new Verlauf();
        verlauf.setAuftragId(1L);
        verlauf.setSubAuftragsIds(orderIds);
        verlauf.setNotPossible(Boolean.FALSE);
        verlauf.setStatusIdAlt(AuftragStatus.KUENDIGUNG_TECHN_REAL);
        verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_BEI_DISPO);
        doNothing().when(sut).checkParameterNullAndThrowExceptionIf(Mockito.anyVararg());
        doReturn(verlauf).when(sut).findVerlaufAndThrowExceptionIfNotFound(Mockito.eq(verlaufId));
        doNothing().when(sut).setVerlaufAbteilungAmErledigt(Mockito.anyLong(), Mockito.anyLong());
        doNothing().when(sut).findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(Mockito.anyLong());
        doNothing().when(sut).checkIfBauauftragBereitsAbgeschlossen(Mockito.eq(verlauf));
        doNothing().when(sut).saveVerlauf(Mockito.eq(verlauf));
        doReturn(true).when(sut).findAuftragDatenAndChangeStatusErledigtIfPossible(Mockito.anyLong(),
                Mockito.eq(verlauf));

        Verlauf result = sut.amPrAbschliessen(verlaufId, 5678568L);

        assertFalse(result.getAkt());
        assertEquals(result.getVerlaufStatusId(), VerlaufStatus.VERLAUF_ABGESCHLOSSEN);
        verify(lockService, times(verlauf.getAllOrderIdsOfVerlauf().size())).finishActiveLocks(Mockito.anyLong());
    }

    @Test(expectedExceptions = FindException.class)
    public void findAuftragDatenAndThrowExceptionIfNotFound_ThrowFindException() throws StoreException, FindException {
        doThrow(new FindException()).when(auftragService).findAuftragDatenByAuftragIdTx(Mockito.anyLong());
        sut.findAuftragDatenAndThrowExceptionIfNotFound(1234L);
    }

    @Test(expectedExceptions = StoreException.class)
    public void findAuftragDatenAndThrowExceptionIfNotFound_NoAuftragDatenFound() throws StoreException, FindException {
        doReturn(null).when(auftragService).findAuftragDatenByAuftragIdTx(Mockito.anyLong());
        sut.findAuftragDatenAndThrowExceptionIfNotFound(1234L);
    }

    @Test
    public void findAuftragDatenAndThrowExceptionIfNotFound_Found() throws StoreException, FindException {
        final Long auftragId = 1234L;
        AuftragDaten auftragDaten = new AuftragDaten();
        auftragDaten.setAuftragId(auftragId);

        doReturn(auftragDaten).when(auftragService).findAuftragDatenByAuftragIdTx(Mockito.eq(auftragId));
        AuftragDaten result = sut.findAuftragDatenAndThrowExceptionIfNotFound(auftragId);
        assertNotNull(result);
        assertEquals(result.getAuftragId(), auftragId);
    }

    @Test
    public void findAuftragDatenAndChangeStatusErledigtIfPossible_Good() throws StoreException, FindException {
        final Long auftragId = 1234L;

        AuftragDaten auftragDaten = new AuftragDaten();
        doReturn(auftragDaten).when(sut).findAuftragDatenAndThrowExceptionIfNotFound(Mockito.eq(auftragId));
        Verlauf verlaufPossible = new Verlauf();
        verlaufPossible.setNotPossible(Boolean.FALSE);
        boolean result = sut.findAuftragDatenAndChangeStatusErledigtIfPossible(auftragId, verlaufPossible);
        assertTrue(result);
        verify(auftragService, times(1)).saveAuftragDaten(Mockito.eq(auftragDaten), Mockito.eq(false));
        assertEquals(auftragDaten.getStatusId(), AuftragStatus.PROJEKTIERUNG_ERLEDIGT);
    }

    @Test
    public void findAuftragDatenAndChangeStatusErledigtIfPossible_Bad() throws StoreException, FindException {
        final Long auftragId = 1234L;

        AuftragDaten auftragDaten = new AuftragDaten();
        doReturn(auftragDaten).when(sut).findAuftragDatenAndThrowExceptionIfNotFound(Mockito.eq(auftragId));
        Verlauf verlaufNotPossible = new Verlauf();
        verlaufNotPossible.setNotPossible(Boolean.TRUE);
        Long statusIdAlt = 3456L;
        verlaufNotPossible.setStatusIdAlt(statusIdAlt);
        boolean result = sut.findAuftragDatenAndChangeStatusErledigtIfPossible(auftragId, verlaufNotPossible);
        assertFalse(result);
        verify(auftragService, times(1)).saveAuftragDaten(Mockito.eq(auftragDaten), Mockito.eq(false));
        assertEquals(auftragDaten.getStatusId(), statusIdAlt);
    }

    @Test(expectedExceptions = FindException.class)
    public void findVerlaufAndThrowExceptionIfNotFound_TechnicalException() throws StoreException, FindException {
        doThrow(new FindException()).when(sut).findVerlauf(Mockito.anyLong());
        sut.findVerlaufAndThrowExceptionIfNotFound(Mockito.anyLong());
    }

    @Test(expectedExceptions = StoreException.class)
    public void findVerlaufAndThrowExceptionIfNotFound_NotFound() throws StoreException, FindException {
        final Long verlaufId = 1234L;
        doReturn(null).when(sut).findVerlauf(Mockito.eq(verlaufId));
        sut.findVerlaufAndThrowExceptionIfNotFound(verlaufId);
    }

    @Test
    public void findVerlaufAndThrowExceptionIfNotFound_Found() throws StoreException, FindException {
        final Long verlaufId = 1234L;
        Verlauf verlauf = new Verlauf();
        doReturn(verlauf).when(sut).findVerlauf(Mockito.eq(verlaufId));
        Verlauf result = sut.findVerlaufAndThrowExceptionIfNotFound(verlaufId);
        assertNotNull(result);
    }

    @Test(expectedExceptions = FindException.class)
    public void findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet_TechnicalException() throws FindException,
            StoreException {
        final Long verlaufId = 1234L;
        doThrow(new FindException()).when(sut).findVerlaufAbteilungen(Mockito.eq(verlaufId));
        sut.findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(verlaufId);
    }

    @Test(expectedExceptions = StoreException.class)
    public void findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet_NotFoundNull() throws FindException,
            StoreException {
        final Long verlaufId = 1234L;
        doReturn(null).when(sut).findVerlaufAbteilungen(Mockito.eq(verlaufId));
        sut.findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(verlaufId);
    }

    @Test(expectedExceptions = StoreException.class)
    public void findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet_NotFoundEmpty() throws FindException,
            StoreException {
        final Long verlaufId = 1234L;
        doReturn(Collections.emptyList()).when(sut).findVerlaufAbteilungen(Mockito.eq(verlaufId));
        sut.findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(verlaufId);
    }

    @Test(expectedExceptions = StoreException.class)
    public void findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet_VerlaufAbteilungThreeTimesAndOneError()
            throws FindException, StoreException {
        final Long verlaufId = 1234L;
        VerlaufAbteilung badVerlaufAbteilung = new VerlaufAbteilung();
        badVerlaufAbteilung.setVerlaufId(verlaufId);
        List<VerlaufAbteilung> verlaufAbteilungen = Arrays.asList(new VerlaufAbteilung(), new VerlaufAbteilung(),
                badVerlaufAbteilung);
        doReturn(verlaufAbteilungen).when(sut).findVerlaufAbteilungen(Mockito.eq(verlaufId));
        doReturn(false).when(sut).isVerlaufErledigtDatumNotSetAndAbteilungNotAM(Mockito.any(VerlaufAbteilung.class));
        doReturn(true).when(sut).isVerlaufErledigtDatumNotSetAndAbteilungNotAM(Mockito.eq(badVerlaufAbteilung));
        sut.findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(verlaufId);
    }

    @Test
    public void findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet_Good()
            throws FindException, StoreException {
        final Long verlaufId = 1234L;
        VerlaufAbteilung badVerlaufAbteilung = new VerlaufAbteilung();
        badVerlaufAbteilung.setVerlaufId(verlaufId);
        List<VerlaufAbteilung> verlaufAbteilungen = Arrays.asList(new VerlaufAbteilung(), new VerlaufAbteilung(),
                badVerlaufAbteilung);
        doReturn(verlaufAbteilungen).when(sut).findVerlaufAbteilungen(Mockito.eq(verlaufId));
        doReturn(false).when(sut).isVerlaufErledigtDatumNotSetAndAbteilungNotAM(Mockito.any(VerlaufAbteilung.class));
        sut.findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(verlaufId);
    }

    @DataProvider(name = "dataProviderIsVerlaufErledigtDatumNotSetAndAbteilungNotAM")
    public Object[][] dataProviderIsVerlaufErledigtDatumNotSetAndAbteilungNotAM() {
        VerlaufAbteilung returnsTrue = new VerlaufAbteilung();
        returnsTrue.setAbteilungId(Abteilung.FIBU);
        returnsTrue.setDatumErledigt(null);

        VerlaufAbteilung returnsFalseOne = new VerlaufAbteilung();
        returnsFalseOne.setAbteilungId(Abteilung.AM);

        VerlaufAbteilung returnsFalseTwo = new VerlaufAbteilung();
        returnsFalseTwo.setDatumErledigt(new Date());

        return new Object[][] {
                { returnsTrue, true },
                { returnsFalseOne, false },
                { returnsFalseTwo, false }
        };
    }

    @Test(dataProvider = "dataProviderIsVerlaufErledigtDatumNotSetAndAbteilungNotAM")
    public void isVerlaufErledigtDatumNotSetAndAbteilungNotAM_Good(VerlaufAbteilung verlaufAbteilung,
            boolean expectedResult) {
        boolean result = sut.isVerlaufErledigtDatumNotSetAndAbteilungNotAM(verlaufAbteilung);
        assertEquals(result, expectedResult);
    }

    @Test(expectedExceptions = FindException.class)
    public void findVerlaufAbteilungAndSetErledigt_TechnicalException() throws FindException, AKAuthenticationException {
        final Long verlaufId = 1234L;
        doThrow(new FindException()).when(sut).findVerlaufAbteilung(Mockito.eq(verlaufId), Mockito.eq(Abteilung.AM));
        sut.setVerlaufAbteilungAmErledigt(verlaufId, 124L);
    }

    @Test(expectedExceptions = FindException.class)
    public void findVerlaufAbteilungAndSetErledigt_UserNotFoundForSession() throws FindException,
            AKAuthenticationException {
        final Long sessionId = 1234L;
        VerlaufAbteilung verlaufAbteilung = new VerlaufAbteilung();
        doReturn(verlaufAbteilung).when(sut).findVerlaufAbteilung(Mockito.anyLong(), Mockito.eq(Abteilung.AM));
        doThrow(new FindException()).when(sut).getUser(sessionId);
        sut.setVerlaufAbteilungAmErledigt(3245L, sessionId);
        verify(verlaufAbteilungDAO, Mockito.never()).store(Mockito.eq(verlaufAbteilung));
    }

    @Test
    public void findVerlaufAbteilungAndSetErledigt_Good() throws FindException, AKAuthenticationException {
        final Long sessionId = 1234L;
        VerlaufAbteilung verlaufAbteilungGood = new VerlaufAbteilung();
        assertNull(verlaufAbteilungGood.getDatumErledigt());
        assertNull(verlaufAbteilungGood.getAusgetragenAm());
        assertNull(verlaufAbteilungGood.getAusgetragenVon());
        assertNotEquals(verlaufAbteilungGood.getVerlaufStatusId(), VerlaufStatus.STATUS_ERLEDIGT);
        doReturn(verlaufAbteilungGood).when(sut).findVerlaufAbteilung(Mockito.anyLong(), Mockito.eq(Abteilung.AM));
        AKUser user = new AKUser();
        final String userName = "TEST_NAME";
        user.setName(userName);
        doReturn(user).when(sut).getUser(Mockito.eq(sessionId));

        sut.setVerlaufAbteilungAmErledigt(12341234L, sessionId);
        verify(verlaufAbteilungDAO, times(1)).store(Mockito.eq(verlaufAbteilungGood));
        assertNotNull(verlaufAbteilungGood.getDatumErledigt());
        assertNotNull(verlaufAbteilungGood.getAusgetragenAm());
        assertNotNull(verlaufAbteilungGood.getAusgetragenVon());
        assertEquals(verlaufAbteilungGood.getAusgetragenVon(), user.getName());
        assertEquals(verlaufAbteilungGood.getVerlaufStatusId(), VerlaufStatus.STATUS_ERLEDIGT);
    }

    @Test(expectedExceptions = StoreException.class, expectedExceptionsMessageRegExp = EXCEPTION_MSG_INVALID_PARAMETERS_TO_STORE)
    public void amBaAbschliessen_SomeParameterNull() throws StoreException {
        doThrow(new StoreException(StoreException.ERROR_INVALID_PARAMETER_TO_STORE)).when(sut)
                .checkParameterNullAndThrowExceptionIf(Mockito.anyVararg());
        sut.amBaAbschliessen(1234L, 12341234L);
    }

    @DataProvider(name = "dataProviderAmBaAbschliessenVerlaufPossible")
    public Object[][] dataProviderAmBaAbschliessenVerlaufPossible() {
        return new Object[][] {
                {
                        false,
                        true,
                        AuftragStatus.AUFTRAG_GEKUENDIGT
                },
                {
                        false,
                        false,
                        AuftragStatus.IN_BETRIEB
                }
        };
    }

    @Test(dataProvider = "dataProviderAmBaAbschliessenVerlaufPossible")
    public void amBaAbschliessen_VerlaufPossible(boolean verlaufNotPossible, boolean isVerlaufStatusKuendigung,
            Long expectedAuftragStatus) throws StoreException, FindException,
            AKAuthenticationException, ServiceNotFoundException {
        final Long verlaufId = 1234L;
        final Long sessionId = 4567L;
        Set<Long> subOrdersIds = Sets.newHashSet(2L, 3L, 4L);
        Verlauf verlauf = new Verlauf();
        verlauf.setAuftragId(1L);
        verlauf.setSubAuftragsIds(subOrdersIds);
        verlauf.setNotPossible(verlaufNotPossible);
        AuftragDaten auftragDatenToSave = new AuftragDaten();
        auftragDatenToSave.setWholesaleAuftragsId(RandomTools.createString());
        doNothing().when(sut).checkParameterNullAndThrowExceptionIf(Mockito.anyVararg());
        doReturn(verlauf).when(sut).findVerlaufAndThrowExceptionIfNotFound(Mockito.eq(verlaufId));
        doNothing().when(sut).setVerlaufAbteilungAmErledigt(Mockito.anyLong(), Mockito.anyLong());
        doNothing().when(sut).findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(Mockito.anyLong());
        doNothing().when(sut).checkIfBauauftragBereitsAbgeschlossen(Mockito.eq(verlauf));
        doNothing().when(sut).saveVerlauf(Mockito.eq(verlauf));
        doReturn(auftragDatenToSave).when(sut).findAuftragDatenAndThrowExceptionIfNotFound(Mockito.anyLong());
        doReturn(isVerlaufStatusKuendigung).when(sut).isVerlaufStatusKuendigung(Mockito.eq(verlauf));
        doNothing().when(sut).sendStatusToTaifun(Mockito.eq(verlauf.getId()), Mockito.eq(sessionId));
        doNothing().when(sut).notifyWholesale(Mockito.eq(auftragDatenToSave.getWholesaleAuftragsId()), Mockito.eq(verlaufId));

        Verlauf result = sut.amBaAbschliessen(verlaufId, sessionId);
        verify(auftragService, times(verlauf.getAllOrderIdsOfVerlauf().size())).saveAuftragDaten(
                Mockito.eq(auftragDatenToSave), Mockito.anyBoolean());
        assertEquals(auftragDatenToSave.getStatusId(), expectedAuftragStatus);
        assertFalse(result.getAkt());
        verify(lockService, times((isVerlaufStatusKuendigung) ? verlauf.getAllOrderIdsOfVerlauf().size() : 0))
                .finishActiveLocks(Mockito.anyLong());
        verify(romNotificationService, times(subOrdersIds.size() + 1)).notifyPortOrderUpdate(any(NotifyPortOrderUpdate.class));
    }

    @Test
    public void amBaAbschliessen_VerlaufNotPossible() throws StoreException, FindException, AKAuthenticationException {
        final Long verlaufId = 1234L;
        final Long sessionId = 3456L;
        final Long statusIdAlt = AuftragStatus.KUENDIGUNG_ERFASSEN;
        Set<Long> orderIds = Sets.newHashSet(2L, 3L, 4L);
        Verlauf verlauf = new Verlauf();
        verlauf.setAuftragId(1L);
        verlauf.setStatusIdAlt(statusIdAlt);
        verlauf.setSubAuftragsIds(orderIds);
        verlauf.setNotPossible(Boolean.TRUE);
        verlauf.setVerlaufStatusId(VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN);
        AuftragDaten auftragDatenToSave = new AuftragDaten();
        doNothing().when(sut).checkParameterNullAndThrowExceptionIf(Mockito.anyVararg());
        doReturn(verlauf).when(sut).findVerlaufAndThrowExceptionIfNotFound(Mockito.eq(verlaufId));
        doNothing().when(sut).setVerlaufAbteilungAmErledigt(Mockito.anyLong(), Mockito.anyLong());
        doNothing().when(sut).findVerlaeufeAndCheckIfAbteilungAndDatumErledigtCorrectlySet(Mockito.anyLong());
        doNothing().when(sut).checkIfBauauftragBereitsAbgeschlossen(Mockito.eq(verlauf));
        doNothing().when(sut).saveVerlauf(Mockito.eq(verlauf));
        doReturn(auftragDatenToSave).when(sut).findAuftragDatenAndThrowExceptionIfNotFound(Mockito.anyLong());
        doNothing().when(sut).setVerlaufStatusAbgeschlossen(Mockito.eq(verlauf));

        Verlauf result = sut.amBaAbschliessen(verlaufId, sessionId);
        verify(auftragService, times(verlauf.getAllOrderIdsOfVerlauf().size())).saveAuftragDaten(
                Mockito.eq(auftragDatenToSave), Mockito.anyBoolean());
        assertEquals(auftragDatenToSave.getStatusId(), statusIdAlt);
        assertFalse(result.getAkt());
        verify(lockService, times(0)).finishActiveLocks(Mockito.anyLong());
    }

    @Test(expectedExceptions = StoreException.class/* , expectedExceptionsMessageRegExp = ".*Beim Abschluss.*" */)
    public void amBaAbschliessen_NoVerlaufFound() throws StoreException, FindException {
        final Long verlaufId = (long) 1234;
        doThrow(new StoreException("TEST")).when(sut).findVerlaufAndThrowExceptionIfNotFound(Mockito.eq(verlaufId));
        sut.amBaAbschliessen(verlaufId, 2341234L);
    }

    @DataProvider(name = "dataProviderCheckIfBauauftragBereitsAbgeschlossen")
    public Object[][] dataProviderCheckIfBauauftragBereitsAbgeschlossen() {
        // @formatter:off
        return new Object[][] {
                { VerlaufStatus.BEI_DISPO, false },
                { VerlaufStatus.BEI_TECHNIK, false },
                { VerlaufStatus.BEI_ZENTRALER_DISPO, false },
                { VerlaufStatus.KUENDIGUNG_BEI_DISPO, false },
                { VerlaufStatus.KUENDIGUNG_BEI_TECHNIK, false },
                { VerlaufStatus.KUENDIGUNG_RL_AM, false },
                { VerlaufStatus.KUENDIGUNG_RL_DISPO, false },
                { VerlaufStatus.KUENDIGUNG_VERLAUF_STORNIERT, false },
                { VerlaufStatus.RUECKLAEUFER_AM, false },
                { VerlaufStatus.RUECKLAEUFER_DISPO, false },
                { VerlaufStatus.RUECKLAEUFER_ZENTRALE_DISPO, false },
                { VerlaufStatus.STATUS_CPS_BEARBEITUNG, false },
                { VerlaufStatus.STATUS_ERLEDIGT, false },
                { VerlaufStatus.STATUS_ERLEDIGT_CPS, false },
                { VerlaufStatus.STATUS_ERLEDIGT_SYSTEM, false },
                { VerlaufStatus.STATUS_IM_UMLAUF, false },
                { VerlaufStatus.STATUS_IN_BEARBEITUNG, false },
                { VerlaufStatus.VERLAUF_STORNIERT, false },

                { VerlaufStatus.VERLAUF_ABGESCHLOSSEN, true },
                { VerlaufStatus.KUENDIGUNG_VERLAUF_ABGESCHLOSSEN, true }
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderCheckIfBauauftragBereitsAbgeschlossen")
    public void checkIfBauauftragBereitsAbgeschlossen(Long verlaufStatusId, boolean isExceptionExpected) {
        Verlauf verlauf = new Verlauf();
        verlauf.setVerlaufStatusId(verlaufStatusId);
        try {
            sut.checkIfBauauftragBereitsAbgeschlossen(verlauf);
            assertFalse(isExceptionExpected);

        }
        catch (StoreException e) {
            assertTrue(isExceptionExpected);
        }
    }

    @DataProvider(name = "dataProviderIsVerlaufStatusKuendigung")
    public Object[][] dataProviderIsVerlaufStatusKuendigung() {
        // @formatter:off
        return new Object[][] {
                { AuftragStatus.ABSAGE, false },
                { AuftragStatus.AENDERUNG, false },
                { AuftragStatus.AENDERUNG_IM_UMLAUF, false },
                { AuftragStatus.ANSCHREIBEN_KUNDEN_ERFASSUNG, false },
                { AuftragStatus.AUS_TAIFUN_UEBERNOMMEN, false },
                { AuftragStatus.BESTELLUNG_CUDA, false },
                { AuftragStatus.ERFASSUNG, false },
                { AuftragStatus.ERFASSUNG_SCV, false },
                { AuftragStatus.IN_BETRIEB, false },
                { AuftragStatus.INTERNET_AUFTRAG, false },
                { AuftragStatus.PROJEKTIERUNG, false },
                { AuftragStatus.PROJEKTIERUNG_ERLEDIGT, false },
                { AuftragStatus.STORNO, false },
                { AuftragStatus.TECHNISCHE_REALISIERUNG, false },
                { AuftragStatus.TELEFONBUCH, false },
                { AuftragStatus.UNDEFINIERT, false },

                { AuftragStatus.KUENDIGUNG, true },
                { AuftragStatus.KUENDIGUNG_CUDA, true },
                { AuftragStatus.KUENDIGUNG_ERFASSEN, true },
                { AuftragStatus.KUENDIGUNG_TECHN_REAL, true },
                { AuftragStatus.ANSCHREIBEN_KUNDE_KUEND, true },
                { AuftragStatus.AUFTRAG_GEKUENDIGT, true },
                { AuftragStatus.KONSOLIDIERT, true },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIsVerlaufStatusKuendigung")
    public void isVerlaufStatusKuendigung(Long auftragStatusId, boolean expectedResult) {
        Verlauf verlauf = new Verlauf();
        verlauf.setStatusIdAlt(auftragStatusId);
        assertEquals(sut.isVerlaufStatusKuendigung(verlauf), expectedResult);
    }

    @DataProvider
    public Object[][] testDispoVerlaufAbschlussAmBARuecklaeuferNecessaryDataProvider() {
        final Date today = new Date();
        final Date tomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        final Long RL = VerlaufStatus.RUECKLAEUFER_AM;
        final Long AB = VerlaufStatus.VERLAUF_ABGESCHLOSSEN;
        return new Object[][] {
                { today, today, false, "asdf", AB, null, false }, // kein RL
                { today, today, true, null, AB, false, false }, // notPossible, aber kein RL erzwungen
                { today, tomorrow, false, null, AB, false, false }, // erledigt morgen, aber kein RL erzwungen

                { today, today, false, null, null, true, true }, // kein RL, da nicht alle Netzplanungen abegschlossen haben HUR-18659

                { today, today, false, null, RL, true, false }, // RL erzwungen
                { today, tomorrow, false, null, RL, null, false }, // erledigt morgen -> RL
                { today, today, true, null, RL, null, false }, // notPossible -> RL
        };
    }

    @Test(dataProvider = "testDispoVerlaufAbschlussAmBARuecklaeuferNecessaryDataProvider")
    public void testDispoVerlaufAbschlussAmBARuecklaeuferNecessary(final Date realisierungsTermin, final Date erledigtAm,
            final Boolean notPossible, final String fieldServiceBemerkung, final Long verlaufStatusExpected,
            final Boolean amRuecklaeuferErzwingen, final boolean npUnerledigt) throws Exception {
        final AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        final VerlaufBuilder verlaufBuilder = new VerlaufBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .withRealisierungstermin(realisierungsTermin)
                .withVerlaufStatusIdAlt(VerlaufStatus.STATUS_IN_BEARBEITUNG)
                .withNotPossible(false);
        final VerlaufAbteilungBuilder verlaufAMBuilder = new VerlaufAbteilungBuilder()
                .withRandomId()
                .withVerlaufBuilder(verlaufBuilder)
                .withAbteilungId(Abteilung.AM);
        final VerlaufAbteilungBuilder verlaufDispoBuilder = new VerlaufAbteilungBuilder()
                .withRandomId()
                .withVerlaufBuilder(verlaufBuilder)
                .withAbteilungId(Abteilung.DISPO)
                .withDatumErledigt(erledigtAm);
        final VerlaufAbteilungBuilder verlaufFieldserviceBuilder = new VerlaufAbteilungBuilder()
                .withRandomId()
                .withVerlaufBuilder(verlaufBuilder)
                .withAbteilungId(Abteilung.FIELD_SERVICE)
                .withDatumErledigt(erledigtAm)
                .withNotPossible(notPossible)
                .withBemerkung(fieldServiceBemerkung);
        final VerlaufAbteilungBuilder verlaufNpBuilder = new VerlaufAbteilungBuilder()
                .withRandomId()
                .withVerlaufBuilder(verlaufBuilder)
                .withAbteilungId(Abteilung.NP)
                .withDatumErledigt(null)
                .withNotPossible(notPossible)
                .withBemerkung(fieldServiceBemerkung);
        final VerlaufAbteilungBuilder verlaufNpBuilder2 = new VerlaufAbteilungBuilder()
                .withRandomId()
                .withVerlaufBuilder(verlaufBuilder)
                .withAbteilungId(Abteilung.NP)
                .withDatumErledigt(null)
                .withNotPossible(notPossible)
                .withBemerkung(fieldServiceBemerkung);
        final AKUser user = new AKUser();
        final AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder().withRandomId().withAuftragBuilder(auftragBuilder);
        final ProduktBuilder produktBuilder = new ProduktBuilder().withRandomId();
        final AuftragTechnikBuilder auftragTechnikBuilder = new AuftragTechnikBuilder().withRandomId().withAuftragBuilder(auftragBuilder);

        when(auftragService.findAuftragTechnikByAuftragIdTx(anyLong())).thenReturn(auftragTechnikBuilder.get());

        when(userService.findUserBySessionId(anyLong())).thenReturn(user);
        when(verlaufDAO.findById(verlaufBuilder.get().getId(), Verlauf.class)).thenReturn(verlaufBuilder.get());
        when(verlaufAbteilungDAO.findById(verlaufDispoBuilder.get().getParentVerlaufAbteilungId(), VerlaufAbteilung.class)).thenReturn(verlaufAMBuilder.get());
        when(verlaufAbteilungDAO.findById(verlaufDispoBuilder.get().getId(), VerlaufAbteilung.class)).thenReturn(verlaufDispoBuilder.get());
        when(verlaufAbteilungDAO.findById(verlaufNpBuilder.get().getId(), VerlaufAbteilung.class)).thenReturn(verlaufNpBuilder.get());
        when(auftragService.findAuftragDatenByAuftragIdTx(anyLong())).thenReturn(auftragDatenBuilder.get());
        when(produktService.findProdukt4Auftrag(verlaufBuilder.get().getAuftragId())).thenReturn(produktBuilder.get());
        when(verlaufAbteilungDAO.queryByExample(any(), eq(VerlaufAbteilung.class)))
                .thenReturn(npUnerledigt
                        ? Lists.newArrayList(verlaufFieldserviceBuilder.get(), verlaufNpBuilder.get(), verlaufNpBuilder2.get())
                        : Lists.newArrayList(verlaufFieldserviceBuilder.get())
                );
        when(verlaufAbteilungDAO.findByVerlaufAndAbtId(eq(verlaufBuilder.getId()), eq(Abteilung.AM), anyLong())).thenReturn(verlaufAMBuilder.get());
        when(auftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(auftragDatenBuilder.get());

        sut.dispoVerlaufAbschluss(verlaufBuilder.get().getId(),
                (npUnerledigt) ? verlaufNpBuilder.get().getId() : verlaufDispoBuilder.get().getId(),
                new Date(), 1234L, amRuecklaeuferErzwingen);

        final ArgumentCaptor<Verlauf> verlaufCaptor = ArgumentCaptor.forClass(Verlauf.class);

        if (npUnerledigt) {
            verify(verlaufDAO, never()).store(verlaufCaptor.capture());
        }
        else {
            verify(verlaufDAO, atLeast(1)).store(verlaufCaptor.capture());
            assertThat(verlaufCaptor.getValue().getVerlaufStatusId(), equalTo(verlaufStatusExpected));
        }
    }

    @Test
    public void findVerlaufByWorkforceOrder() throws FindException {
        String workforceOrderId = UUID.randomUUID().toString();
        when(verlaufDAO.findByWorkforceOrderId(workforceOrderId)).thenReturn(Collections.singletonList(new Verlauf()));

        Verlauf bauauftrag = sut.findVerlaufByWorkforceOrder(workforceOrderId);
        Assert.assertNotNull(bauauftrag);

        verify(verlaufDAO).findByWorkforceOrderId(workforceOrderId);
    }

    @Test
    public void findVerlaufByWorkforceOrderNull() throws FindException {
        final String workforceOrderId = null;
        Assert.assertNull(sut.findVerlaufByWorkforceOrder(workforceOrderId));

        verify(verlaufDAO, never()).findByWorkforceOrderId(workforceOrderId);
    }

    @Test
    public void findVerlaufByWorkforceOrderEmptyResult() throws FindException {
        String workforceOrderId = UUID.randomUUID().toString();
        when(verlaufDAO.findByWorkforceOrderId(workforceOrderId)).thenReturn(Collections.<Verlauf>emptyList());

        assertNull(sut.findVerlaufByWorkforceOrder(workforceOrderId));

        verify(verlaufDAO).findByWorkforceOrderId(workforceOrderId);
    }

    @Test
    public void findVerlaufByWorkforceOrderNotFound() throws FindException {
        String workforceOrderId = UUID.randomUUID().toString();
        when(verlaufDAO.findByWorkforceOrderId(workforceOrderId)).thenThrow(new EmptyResultDataAccessException(1));

        assertNull(sut.findVerlaufByWorkforceOrder(workforceOrderId));

        verify(verlaufDAO).findByWorkforceOrderId(workforceOrderId);
    }

    @Test
    public void findVerlaufByWorkforceOrderMultipleResults() throws FindException {
        String workforceOrderId = UUID.randomUUID().toString();
        List<Verlauf> bauauftragList = new ArrayList<>();
        bauauftragList.add(new Verlauf());
        bauauftragList.add(new Verlauf());

        when(verlaufDAO.findByWorkforceOrderId(workforceOrderId)).thenReturn(bauauftragList);

        assertNull(sut.findVerlaufByWorkforceOrder(workforceOrderId));

        verify(verlaufDAO).findByWorkforceOrderId(workforceOrderId);
    }

    @Test(expectedExceptions = StoreException.class)
    public void testDispoVerlaufAbschlussNichtMoeglichWennNichtAlleNpsAbgeschlossenSind() throws Exception {
        final Date realisierungsTermin = new Date();
        final Date erledigtAm = new Date();

        final AuftragBuilder auftragBuilder = new AuftragBuilder().withRandomId();
        final VerlaufBuilder verlaufBuilder = new VerlaufBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .withRealisierungstermin(realisierungsTermin)
                .withVerlaufStatusIdAlt(VerlaufStatus.STATUS_IN_BEARBEITUNG);
        final VerlaufAbteilungBuilder verlaufDispoBuilder = new VerlaufAbteilungBuilder()
                .withRandomId()
                .withVerlaufBuilder(verlaufBuilder)
                .withAbteilungId(Abteilung.DISPO)
                .withDatumErledigt(erledigtAm);
        final VerlaufAbteilungBuilder verlaufNetzplanungBuilder = new VerlaufAbteilungBuilder()
                .withVerlaufBuilder(verlaufBuilder)
                .withAbteilungId(Abteilung.NP)
                .withDatumErledigt(null);
        final AKUser user = new AKUser();
        final AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder().withRandomId().withAuftragBuilder(auftragBuilder);
        final ProduktBuilder produktBuilder = new ProduktBuilder().withRandomId();
        final AuftragTechnikBuilder auftragTechnikBuilder = new AuftragTechnikBuilder().withRandomId().withAuftragBuilder(auftragBuilder);

        when(auftragService.findAuftragTechnikByAuftragIdTx(anyLong())).thenReturn(auftragTechnikBuilder.get());

        when(userService.findUserBySessionId(anyLong())).thenReturn(user);
        when(verlaufDAO.findById(verlaufBuilder.get().getId(), Verlauf.class)).thenReturn(verlaufBuilder.get());
        when(verlaufAbteilungDAO.findById(verlaufDispoBuilder.get().getId(), VerlaufAbteilung.class)).thenReturn(verlaufDispoBuilder.get());
        when(auftragService.findAuftragDatenByAuftragIdTx(anyLong())).thenReturn(auftragDatenBuilder.get());
        when(produktService.findProdukt4Auftrag(verlaufBuilder.get().getAuftragId())).thenReturn(produktBuilder.get());
        when(verlaufAbteilungDAO.queryByExample(any(), eq(VerlaufAbteilung.class))).thenReturn(Lists.newArrayList(verlaufNetzplanungBuilder.get()));

        sut.dispoVerlaufAbschluss(verlaufBuilder.get().getId(), verlaufDispoBuilder.get().getId(), new Date(), 1234L, false);
    }


    @Test
    public void notifyWholesale_sendNOTIFICATION() {
        final Long verlaufIdONE = 1L;
        final String orderIdONE = "1";
        List<VerlaufAbteilung> verlaufAbteilungen = new ArrayList<>();
        when(verlaufAbteilungDAO.queryByExample(any(), eq(VerlaufAbteilung.class))).thenReturn(verlaufAbteilungen);

        sut.notifyWholesale(orderIdONE, verlaufIdONE);

        ArgumentCaptor<NotifyPortOrderUpdate> argument = ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);
        verify(romNotificationService).notifyPortOrderUpdate(argument.capture());
        NotifyPortOrderUpdate current = argument.getValue();

        assertEquals(current.getOrderId(), orderIdONE);
        assertNotNull(current.getNotification());
        assertNull(current.getError());
    }

    @Test
    public void notifyWholesale_sendERRORS() throws FindException {
        final Long refId1304 = 1304L;
        final String refIdTXT = "technisch nicht realisierbar";
        final Long verlaufIdTWO = 2L;
        final String orderIdTWO = "2";
        List<VerlaufAbteilung> verlaufErrorAbteilungen = new ArrayList<>();
        VerlaufAbteilung abt1 = new VerlaufAbteilung();
        abt1.setVerlaufId(2L);
        abt1.setAbteilungId(Abteilung.FFM);
        abt1.setNotPossible(true);
        abt1.setNotPossibleReasonRefId(1304L);
        verlaufErrorAbteilungen.add(abt1);

        Reference ref2 = new Reference();
        ref2.setId(refId1304);
        ref2.setStrValue(refIdTXT);

        when(verlaufAbteilungDAO.queryByExample(any(), eq(VerlaufAbteilung.class))).thenReturn(verlaufErrorAbteilungen);

        when(verlaufAbteilungDAO.findByVerlaufAndAbtId(verlaufIdTWO, Abteilung.FFM, null)).thenReturn(abt1);
        when(referenceService.findReference(refId1304)).thenReturn(ref2);

        sut.notifyWholesale(orderIdTWO, verlaufIdTWO);

        ArgumentCaptor<NotifyPortOrderUpdate> argument = ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);
        verify(romNotificationService).notifyPortOrderUpdate(argument.capture());
        NotifyPortOrderUpdate current = argument.getValue();

        assertEquals(current.getOrderId(), orderIdTWO);
        assertNotNull(current.getError());

    }

    @Test
    public void hasTechLsPreventAutoDispatch() throws FindException {
        TechLeistung prevent = new TechLeistungBuilder().withPreventAutoDispatch(Boolean.TRUE).build();
        TechLeistung doNotPrevent = new TechLeistungBuilder().build();

        when(leistungsService.findTechLeistungen4Verlauf(1L, true)).thenReturn(Arrays.asList(prevent, doNotPrevent));
        when(leistungsService.findTechLeistungen4Verlauf(2L, true)).thenReturn(Arrays.asList(doNotPrevent));
        when(leistungsService.findTechLeistungen4Verlauf(3L, true)).thenReturn(Arrays.asList());

        assertTrue(sut.hasTechLsPreventAutoDispatch(new VerlaufBuilder().withId(1L).build()));
        assertFalse(sut.hasTechLsPreventAutoDispatch(new VerlaufBuilder().withId(2L).build()));
        assertFalse(sut.hasTechLsPreventAutoDispatch(new VerlaufBuilder().withId(3L).build()));
    }


    @DataProvider(name = "validateAbteilungsSetDP")
    public Object[][] validateAbteilungsSetDP() {
        return new Object[][] {
                { Sets.newHashSet(ST_VOICE), true, true, Sets.newHashSet(ST_VOICE) },
                { Sets.newHashSet(ST_VOICE, MQUEUE), false, false, Sets.newHashSet(ST_VOICE, MQUEUE) },
                { Sets.newHashSet(ST_VOICE, MQUEUE), true, false, Sets.newHashSet(ST_VOICE, MQUEUE) },
                { Sets.newHashSet(MQUEUE, FIELD_SERVICE), true, false, Sets.newHashSet(MQUEUE, FIELD_SERVICE) },
                { Sets.newHashSet(MQUEUE, FIELD_SERVICE), false, false, Sets.newHashSet(MQUEUE) },
                { Sets.newHashSet(MQUEUE, FFM), false, true, Sets.newHashSet(MQUEUE, FFM) },
                { Sets.newHashSet(MQUEUE, FFM), false, false, Sets.newHashSet(MQUEUE) },
                { Sets.newHashSet(MQUEUE, FIELD_SERVICE, FFM), true, true, Sets.newHashSet(MQUEUE, FIELD_SERVICE, FFM) },
                { Sets.newHashSet(MQUEUE, FIELD_SERVICE, FFM), true, false, Sets.newHashSet(MQUEUE, FIELD_SERVICE) },
                { Sets.newHashSet(MQUEUE, FIELD_SERVICE, FFM), false, true, Sets.newHashSet(MQUEUE, FFM) },
        };
    }

    @Test(dataProvider = "validateAbteilungsSetDP")
    public void validateAbteilungsSet(Set<Long> abtIds, boolean externInstallation, boolean techLsWithSkill, Set<Long> expectedAbtIds) throws FindException {
        doReturn(externInstallation).when(sut).hasExternInstallation(anyLong());

        TechLeistung techLs = new TechLeistungBuilder().build();
        List<TechLeistung> techLsList = Arrays.asList(techLs);
        when(leistungsService.findTechLeistungen4Verlauf(anyLong(), eq(true))).thenReturn(techLsList);
        when(ffmService.getFfmQualifications(techLsList)).thenReturn(
                techLsWithSkill ? Collections.singletonList(new FfmQualificationBuilder().build()) : Collections.emptyList());

        sut.validateAbteilungsSet(new VerlaufBuilder().build(), abtIds);

        Assert.assertEquals(abtIds, expectedAbtIds);
    }

    @DataProvider(name = "testReadZusaetzeDP")
    public Object[][] testReadZusaetzeDP() {
        final Long difAbt = 0L;
        return new Object[][] {
                { null, Sets.newHashSet(FFM), true, false, Sets.newHashSet(FFM) },
                { null, Sets.newHashSet(FFM), false, true, Sets.newHashSet(FFM) },
                { null, Sets.newHashSet(FFM), true, true, Sets.newHashSet(FFM) },
                { null, Sets.newHashSet(FFM), false, false, Sets.newHashSet(FFM) },
                { null, Sets.newHashSet(FFM, FIELD_SERVICE), true, true, Sets.newHashSet(FFM, FIELD_SERVICE) },
                { null, Sets.newHashSet(FFM, FIELD_SERVICE), true, false, Sets.newHashSet(FFM, FIELD_SERVICE) },
                { null, Sets.newHashSet(FFM, FIELD_SERVICE), false, true, Sets.newHashSet(FFM, FIELD_SERVICE) },
                { null, Sets.newHashSet(FFM, FIELD_SERVICE), false, false, Sets.newHashSet(FFM) },

                // abtIds hat bereits einen Eintrag
                { difAbt, Sets.newHashSet(FFM), true, true, Sets.newHashSet(difAbt, FFM) },
                { difAbt, Sets.newHashSet(FFM, FIELD_SERVICE), true, true, Sets.newHashSet(difAbt, FFM, FIELD_SERVICE) },
                // doppelte Zusaetze werden 'geschluckt'
                { difAbt, Sets.newHashSet(difAbt, FFM), true, true, Sets.newHashSet(difAbt, FFM) },
                { difAbt, Sets.newHashSet(difAbt, FFM, FIELD_SERVICE), true, true, Sets.newHashSet(difAbt, FFM, FIELD_SERVICE) },
        };
    }

    @Test(dataProvider = "testReadZusaetzeDP")
    public void testReadZusaetze(Long difAbt, Set<Long> zusatzAbtIds, boolean externInstallation,
            boolean selbstmontage, Set<Long> expectedAbtIds) throws Exception {
        doReturn(externInstallation).when(sut).hasExternInstallation(anyLong());

        when(referenceService.findReference(anyLong())).thenReturn(new ReferenceBuilder().withRandomId().build());
        when(baConfigService.findBAVerlaufZusaetze4BAVerlaufConfig(anyLong(), anyLong(), anyLong()))
                .thenReturn(zusatzAbtIds.stream()
                        .map(a -> new BAVerlaufZusatzBuilder().withAbtId(a).withAuchSelbstmontage(selbstmontage).build())
                        .collect(Collectors.toList()));

        Set<Long> abtIds = Sets.newHashSet();
        if (difAbt != null) {
            abtIds.add(difAbt);
        }
        sut.readZusaetze(new VerlaufBuilder().build(), abtIds,
                new EndstelleBuilder().build(), 1L);

        Assert.assertEquals(abtIds, expectedAbtIds);
    }

    /**
     * Test for {@link BAServiceImpl#baErstellen(Long, List, Date, Long)}.
     *
     * Test case: create BA for FTTH wholesale product.
     *
     * @throws Exception
     */
    @Test
    public void testBaErstellenFtthProduct() throws Exception {
        final Long verlaufId = 123L;
        final List<Long> abtIds = new ArrayList<>();
        final LocalDate now = LocalDate.now();
        final Date auftragsDate = asDate(now);
        final Date realDate = asDate(now.plusWeeks(1));
        final Long sessionId = 123L;
        final AKUser user = new AKUser();
        final Niederlassung niederlassung = new Niederlassung();
        final HWRack rack = new HWRack();
        final String lineId = "DEU.MNET.000111";
        final String orderId = "123";

        final VerbindungsBezeichnungBuilder vbzBuilder = new VerbindungsBezeichnungBuilder()
                .withVbz(lineId);
        final AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder()
                .withWholesaleAuftragsId(orderId)
                .withProdId(600L)
                .withStatusId(1000L)
                .withVorgabeSCV(auftragsDate);
        final AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withRandomId()
                .withAuftragDatenBuilder(auftragDatenBuilder);
        final Verlauf verlauf = new VerlaufBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .withRealisierungstermin(realDate)
                .withVerlaufStatusIdAlt(VerlaufStatus.STATUS_IN_BEARBEITUNG).build();
        final Auftrag2EkpFrameContract auftrag2EkpFrameContract = new Auftrag2EkpFrameContractBuilder()
                .withEkpFrameContractBuilder(new EkpFrameContractBuilder()).build();
        final HWOlt olt = new HWOltBuilder().withRandomId().build();
        final A10NspPort a10nsp = new A10NspPortBuilder()
                .withA10NspBuilder(new A10NspBuilder())
                .withVbzBuilder(new VerbindungsBezeichnungBuilder())
                .build();

        final EqVlan eqVlan = new EqVlan();
        eqVlan.setSvlanEkp(5678);
        final List<EqVlan> vlans = new ArrayList<>(Collections.singletonList(eqVlan));

        final Endstelle endstelle = new Endstelle();
        endstelle.setHvtIdStandort(123L);
        endstelle.setEndstelleTyp(ENDSTELLEN_TYP_B);
        final List<Endstelle> endstellen = new ArrayList<>(Collections.singletonList(endstelle));

        when(userService.findUserBySessionId(sessionId)).thenReturn(user);
        when(verlaufDAO.findById(verlaufId, Verlauf.class)).thenReturn(verlauf);
        when(niederlassungService.findNiederlassung4Auftrag(verlauf.getAuftragId())).thenReturn(niederlassung);
        when(auftragService.findAuftragDatenByAuftragIdTx(anyLong())).thenReturn(auftragBuilder.getAuftragDatenBuilder().get());
        when(physikService.findVerbindungsBezeichnungByAuftragId(anyLong())).thenReturn(vbzBuilder.get());
        when(ekpFrameContractService.findAuftrag2EkpFrameContract(anyLong(), any(LocalDate.class)))
                .thenReturn(auftrag2EkpFrameContract);
        when(auftragService.findHwRackByAuftragId(anyLong())).thenReturn(rack);
        when(hardwareDAO.findHwOltForRack(rack)).thenReturn(olt);
        when(ekpFrameContractService.findA10NspPort(any(EkpFrameContract.class), anyLong())).thenReturn(a10nsp);
        when(vlanService.assignEqVlans(any(EkpFrameContract.class), anyLong(), anyLong(), any(LocalDate.class),
                any(AuftragAktion.class))).thenReturn(vlans);
        when(endstellenService.findEndstellen4Auftrag(anyLong())).thenReturn(endstellen);
        when(hvtService.findAnschlussart4HVTStandort(anyLong())).thenReturn(16L);

        sut.baErstellen(verlaufId, abtIds, realDate, sessionId);

        ArgumentCaptor<NotifyPortOrderUpdate> argument = ArgumentCaptor.forClass(NotifyPortOrderUpdate.class);
        verify(romNotificationService).notifyPortOrderUpdate(argument.capture());
        NotifyPortOrderUpdate current = argument.getValue();

        assertThat(current.getOrderId(), equalTo(orderId));
        assertThat(current.getNotification().getExecutionDate(), equalTo(DateConverterUtils.asLocalDate(realDate)));
        assertThat(current.getNotification().getLineId(), equalTo(lineId));
        assertThat(current.getNotification().getNotificationDetail(), hasSize(5));
    }

    /**
     * Test for {@link BAServiceImpl#baErstellen(Long, List, Date, Long)}.
     *
     * Test case: create BA for FTTB wholesale product.
     *
     * @throws Exception
     */
    @Test
    public void testBaErstellenFttbProduct() throws Exception {
        final Long verlaufId = 123L;
        final List<Long> abtIds = new ArrayList<>();
        final Date realDate = asDate(LocalDate.now());
        final Long sessionId = 123L;
        final AKUser user = new AKUser();
        final Niederlassung niederlassung = new Niederlassung();
        final String orderId = "123";

        final AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder()
                .withWholesaleAuftragsId(orderId)
                .withProdId(600L)
                .withVorgabeSCV(realDate);
        final AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withRandomId()
                .withAuftragDatenBuilder(auftragDatenBuilder);
        final Verlauf verlauf = new VerlaufBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .withRealisierungstermin(realDate)
                .withVerlaufStatusIdAlt(VerlaufStatus.STATUS_IN_BEARBEITUNG).build();

        final Endstelle endstelle = new Endstelle();
        endstelle.setHvtIdStandort(123L);
        endstelle.setEndstelleTyp(ENDSTELLEN_TYP_B);
        final List<Endstelle> endstellen = new ArrayList<>(Collections.singletonList(endstelle));

        when(userService.findUserBySessionId(sessionId)).thenReturn(user);
        when(verlaufDAO.findById(verlaufId, Verlauf.class)).thenReturn(verlauf);
        when(niederlassungService.findNiederlassung4Auftrag(verlauf.getAuftragId())).thenReturn(niederlassung);
        when(auftragService.findAuftragDatenByAuftragIdTx(anyLong())).thenReturn(auftragBuilder.getAuftragDatenBuilder().get());
        when(endstellenService.findEndstellen4Auftrag(anyLong())).thenReturn(endstellen);
        when(hvtService.findAnschlussart4HVTStandort(anyLong())).thenReturn(15L);

        sut.baErstellen(verlaufId, abtIds, realDate, sessionId);

        verifyNoMoreInteractions(physikService);
        verifyNoMoreInteractions(ekpFrameContractService);
        verifyNoMoreInteractions(hardwareDAO);
        verifyNoMoreInteractions(vlanService);
    }

    /**
     * Test for {@link BAServiceImpl#baErstellen(Long, List, Date, Long)}.
     *
     * Test case: create BA for not a wholesale product.
     *
     * @throws Exception
     */
    @Test
    public void testBaErstellenNotWholesaleProduct() throws Exception {
        final Long verlaufId = 123L;
        final List<Long> abtIds = new ArrayList<>();
        final Date realDate = asDate(LocalDate.now());
        final Long sessionId = 123L;
        final AKUser user = new AKUser();
        final Niederlassung niederlassung = new Niederlassung();

        final AuftragDatenBuilder auftragDatenBuilder = new AuftragDatenBuilder()
                .withProdId(600L)
                .withVorgabeSCV(realDate);
        final AuftragBuilder auftragBuilder = new AuftragBuilder()
                .withRandomId()
                .withAuftragDatenBuilder(auftragDatenBuilder);
        final Verlauf verlauf = new VerlaufBuilder()
                .withRandomId()
                .withAuftragBuilder(auftragBuilder)
                .withRealisierungstermin(realDate)
                .withVerlaufStatusIdAlt(VerlaufStatus.STATUS_IN_BEARBEITUNG).build();

        when(userService.findUserBySessionId(sessionId)).thenReturn(user);
        when(verlaufDAO.findById(verlaufId, Verlauf.class)).thenReturn(verlauf);
        when(niederlassungService.findNiederlassung4Auftrag(verlauf.getAuftragId())).thenReturn(niederlassung);
        when(auftragService.findAuftragDatenByAuftragIdTx(anyLong())).thenReturn(auftragBuilder.getAuftragDatenBuilder().get());

        sut.baErstellen(verlaufId, abtIds, realDate, sessionId);

        verifyNoMoreInteractions(physikService);
        verifyNoMoreInteractions(ekpFrameContractService);
        verifyNoMoreInteractions(hardwareDAO);
        verifyNoMoreInteractions(vlanService);
        verifyNoMoreInteractions(endstellenService);
        verifyNoMoreInteractions(hvtService);
    }

    @DataProvider
    Object[][] changeRealDateMitUngueltigemDatumDataProvider() {
        final LocalDate realDate = gueltigerRealisierungstermin();
        final LocalDate weihnachten = LocalDate.of(LocalDate.now().getYear(), 12, 25);
        final LocalDate neujahr = LocalDate.of(LocalDate.now().getYear() + 1, 1, 1);

        return new Object[][] {
                { realDate, LocalDate.now().minusDays(1) }, //Termin in Vergangenheit
                { realDate, realDate.minusDays(1) },//Termin vorverschieben
        };
    }

    private LocalDate gueltigerRealisierungstermin() {
        return asWorkingDay(LocalDate.now().plusDays(5));
    }

    @Test(dataProvider = "changeRealDateMitUngueltigemDatumDataProvider", expectedExceptions = BAService.TerminverschiebungException.class)
    public void testChangeRealDateMitUngueltigemDatumCausesTerminverschiebungException(final LocalDate realDate, LocalDate neuerTermin) throws Exception {
        final Verlauf verlauf = new VerlaufBuilder()
                .withRandomId()
                .withRealisierungstermin(asDate(realDate))
                .build();
        final VerlaufAbteilung verlaufAbteilung = new VerlaufAbteilungBuilder()
                .withRandomId()
                .build();

        when(verlaufAbteilungDAO.queryByExample(any(VerlaufAbteilung.class), eq(VerlaufAbteilung.class)))
                .thenReturn(Collections.singletonList(verlaufAbteilung));
        when(verlaufDAO.findById(verlauf.getId(), Verlauf.class))
                .thenReturn(verlauf);

        sut.changeRealDate(verlauf.getId(), asDate(neuerTermin), new AKUser());
    }

    public void testChangeRealDateTerminWirdVerschoben() throws Exception {
        final Date realDate = asDate(gueltigerRealisierungstermin());
        final Date neuerTermin = asDate(gueltigerNeuerTermin());
        final String newWorkforceOrderId = RandomTools.createString();

        final Verlauf verlauf = new VerlaufBuilder()
                .withRandomId()
                .withRealisierungstermin(realDate)
                .withAuftragId(RandomTools.createLong())
                .withWorkforceOrderId(RandomTools.createString())
                .build();
        final VerlaufAbteilung verlaufAbteilung = new VerlaufAbteilungBuilder()
                .withRandomId()
                .build();
        final Produkt produkt = new ProduktBuilder()
                .withRandomId()
                .withBATerminVerschieben(true)
                .build();
        final AuftragTechnik auftragTechnik = new AuftragTechnikBuilder()
                .withRandomId()
                .withVPNBuilder(null)
                .build();

        when(verlaufAbteilungDAO.queryByExample(any(VerlaufAbteilung.class), eq(VerlaufAbteilung.class)))
                .thenReturn(Collections.singletonList(verlaufAbteilung));
        when(verlaufDAO.findById(verlauf.getId(), Verlauf.class))
                .thenReturn(verlauf);

        when(produktService.findProdukt4Auftrag(verlauf.getAuftragId())).thenReturn(produkt);
        when(auftragService.findAuftragTechnikByAuftragId(verlauf.getAuftragId())).thenReturn(auftragTechnik);

        when(ffmService.createAndSendOrder(verlauf)).thenReturn(newWorkforceOrderId);

        sut.changeRealDate(verlauf.getId(), neuerTermin, new AKUser());

        assertVerlaufVerschoben(neuerTermin, verlauf);
        assertVerlaufabteilungVerschoben(neuerTermin, verlaufAbteilung);
        assertFFMVerschoben(newWorkforceOrderId, verlauf);

        verify(verlaufDAO, times(2)).store(verlauf);
    }

    private void assertVerlaufVerschoben(Date neuerTermin, Verlauf verlauf) {
        assertThat(verlauf.getRealisierungstermin(), equalTo(neuerTermin));
        assertThat(verlauf.getBemerkung(), not(isEmptyString()));
    }

    private void assertVerlaufabteilungVerschoben(Date neuerTermin, VerlaufAbteilung verlaufAbteilung) {
        verify(verlaufAbteilungDAO).store(verlaufAbteilung);
        assertThat(verlaufAbteilung.getRealisierungsdatum(), equalTo(neuerTermin));
    }

    private void assertFFMVerschoben(String newWorkforceOrderId, Verlauf verlauf) {
        verify(ffmService).deleteOrder(verlauf);
        verify(ffmService).createAndSendOrder(verlauf);
        assertThat(verlauf.getWorkforceOrderId(), equalTo(newWorkforceOrderId));
    }

    private LocalDate gueltigerNeuerTermin() {
        return asWorkingDay(gueltigerRealisierungstermin().plusDays(1));
    }
}
