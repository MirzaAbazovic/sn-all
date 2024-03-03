/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.11.14
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfileBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;

@Test(groups = BaseTest.UNIT)
public class RemoveCpeCommandTest {

    @InjectMocks
    @Spy
    private RemoveCpeCommand testling = new RemoveCpeCommand();

    @Mock
    private DSLAMService dslamService;
    @Mock
    private VoIPService voipService;
    @Mock
    private SIPDomainService sipDomainService;

    @BeforeMethod
    void setUp() throws Exception {
        initMocks(this);
    }

    public void testExecute() throws Exception {
        doNothing().when(testling).deactivateDslamProfile();
        doNothing().when(testling).resetSipDomainToDefault();

        Object result = testling.execute();
        assertTrue(result instanceof ServiceCommandResult);
        assertEquals(((ServiceCommandResult) result).getCheckStatus(), ServiceCommandResult.CHECK_STATUS_OK);

        verify(testling).deactivateDslamProfile();
        verify(testling).resetSipDomainToDefault();
    }

    @Test(expectedExceptions = HurricanServiceCommandException.class, expectedExceptionsMessageRegExp = "Fehler beim Entfernen des CPEs:.*")
    public void testExecuteWithException() throws Exception {
        doNothing().when(testling).deactivateDslamProfile();
        doThrow(new RuntimeException()).when(testling).resetSipDomainToDefault();

        testling.execute();
    }

    public void deactivateDslamProfile() throws HurricanServiceCommandException, FindException, StoreException {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime past = now.minusDays(10);
        ZonedDateTime future = now.plusDays(10);

        Auftrag2DSLAMProfile inactiveProfile = new Auftrag2DSLAMProfileBuilder().withRandomId().withGueltigBis(Date.from(past.toInstant())).setPersist(false).build();
        Auftrag2DSLAMProfile activeProfile = new Auftrag2DSLAMProfileBuilder().withRandomId().withGueltigBis(Date.from(future.toInstant())).setPersist(false).build();

        when(dslamService.findAuftrag2DSLAMProfiles(anyLong())).thenReturn(Arrays.asList(inactiveProfile, activeProfile));
        doReturn(Date.from(now.toInstant())).when(testling).getAktivBis();

        testling.deactivateDslamProfile();

        assertEquals(activeProfile.getGueltigBis(), Date.from(now.toInstant()));
        verify(dslamService, times(1)).saveAuftrag2DSLAMProfile(activeProfile);
    }


    @Test(expectedExceptions = HurricanServiceCommandException.class, expectedExceptionsMessageRegExp = "Fehler bei der Deaktivierung des DSLAM-Profils:.*")
    public void deactivateDslamProfileWithException() throws HurricanServiceCommandException, FindException, StoreException {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime past = now.minusDays(10);
        ZonedDateTime future = now.plusDays(10);

        Auftrag2DSLAMProfile inactiveProfile = new Auftrag2DSLAMProfileBuilder().withRandomId().withGueltigBis(Date.from(past.toInstant())).setPersist(false).build();
        Auftrag2DSLAMProfile activeProfile = new Auftrag2DSLAMProfileBuilder().withRandomId().withGueltigBis(Date.from(future.toInstant())).setPersist(false).build();

        when(dslamService.findAuftrag2DSLAMProfiles(anyLong())).thenReturn(Arrays.asList(inactiveProfile, activeProfile));
        doReturn(Date.from(now.toInstant())).when(testling).getAktivBis();
        doThrow(new RuntimeException()).when(dslamService).saveAuftrag2DSLAMProfile(activeProfile);

        testling.deactivateDslamProfile();
    }

    @Test(expectedExceptions = HurricanServiceCommandException.class, expectedExceptionsMessageRegExp = "Es konnten keine VOIP Daten zu dem Auftrag ermittelt werden!")
    public void resetSipDomainToDefaultNoVoipData() throws FindException, StoreException, HurricanServiceCommandException {
        when(voipService.findVoIPDNView(anyLong())).thenReturn(null);
        testling.resetSipDomainToDefault();
    }


    @Test(expectedExceptions = HurricanServiceCommandException.class, expectedExceptionsMessageRegExp = "Die Default SIP-Domain fuer den Auftrag konnte nicht ermitteln!")
    public void resetSipDomainToDefaultNoDefaultSipDomain() throws FindException, StoreException, HurricanServiceCommandException {
        when(voipService.findVoIPDNView(anyLong())).thenReturn(
                Arrays.asList(new AuftragVoipDNViewBuilder().build()));
        when(sipDomainService.findDefaultSIPDomain4Auftrag(anyLong())).thenReturn(null);

        testling.resetSipDomainToDefault();
    }


    public void resetSipDomainToDefault() throws FindException, StoreException, HurricanServiceCommandException {
        AuftragVoipDNViewBuilder auftragVoipDNViewBuilder = new AuftragVoipDNViewBuilder();
        List<AuftragVoipDNView> auftragVoipDNViews = Arrays.asList(auftragVoipDNViewBuilder.build());

        Long auftragId = 1L;
        Reference defaultSipDomain = new ReferenceBuilder().withRandomId().setPersist(false).build();

        doReturn(auftragId).when(testling).getAuftragId();
        when(voipService.findVoIPDNView(anyLong())).thenReturn(auftragVoipDNViews);
        when(sipDomainService.findDefaultSIPDomain4Auftrag(anyLong())).thenReturn(defaultSipDomain);

        testling.resetSipDomainToDefault();

        assertEquals(auftragVoipDNViews.get(0).getSipDomain(), defaultSipDomain);

        verify(voipService).saveSipDomainOnVoIPDNs(auftragVoipDNViews, auftragId);
    }


    @Test(expectedExceptions = HurricanServiceCommandException.class, expectedExceptionsMessageRegExp = "Fehler bei der Zuordnung der Default SIP-Domain zu dem Auftrag:.*")
    public void resetSipDomainToDefaultWithException() throws FindException, StoreException, HurricanServiceCommandException {
        AuftragVoipDNViewBuilder auftragVoipDNViewBuilder = new AuftragVoipDNViewBuilder();
        List<AuftragVoipDNView> auftragVoipDNViews = Arrays.asList(auftragVoipDNViewBuilder.build());

        Long auftragId = 1L;
        Reference defaultSipDomain = new ReferenceBuilder().withRandomId().setPersist(false).build();

        doReturn(auftragId).when(testling).getAuftragId();
        when(voipService.findVoIPDNView(anyLong())).thenReturn(auftragVoipDNViews);
        when(sipDomainService.findDefaultSIPDomain4Auftrag(anyLong())).thenReturn(defaultSipDomain);
        doThrow(new RuntimeException()).when(voipService).saveSipDomainOnVoIPDNs(auftragVoipDNViews, auftragId);

        testling.resetSipDomainToDefault();
    }


}
