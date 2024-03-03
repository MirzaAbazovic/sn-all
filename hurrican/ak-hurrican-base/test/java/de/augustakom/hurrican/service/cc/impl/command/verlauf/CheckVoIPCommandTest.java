/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.07.2012 10:54:49
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDNBuilder;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.VoIPService;

@Test(groups = { BaseTest.UNIT })
public class CheckVoIPCommandTest extends BaseTest {

    private static final Long AUFTRAG_ID = Long.MAX_VALUE;

    @InjectMocks
    @Spy
    private CheckVoIPCommand cut;

    @Mock
    private VoIPService voipServiceMock;

    @BeforeMethod
    public void setUp() throws ServiceNotFoundException {
        cut = new CheckVoIPCommand();
        MockitoAnnotations.initMocks(this);

        doReturn(AUFTRAG_ID).when(cut).getAuftragId();
    }

    public void testCheckDNWithRufnummernNull() throws StoreException, FindException {
        doReturn(null).when(cut).getRufnummern();
        cut.checkDN();
    }

    public void testCheckDNWithRufnummernEmpty() throws StoreException, FindException {
        doReturn(Collections.emptyList()).when(cut).getRufnummern();
        cut.checkDN();
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckDNWithAuftragVoIPDNNull() throws StoreException, FindException {
        doReturn(Arrays.asList(new Rufnummer())).when(cut).getRufnummern();
        when(voipServiceMock.createVoIPDN4Auftrag(eq(AUFTRAG_ID), any(Long.class))).thenReturn(null);
        cut.checkDN();
    }

    @Test(expectedExceptions = FindException.class)
    public void testCheckDNWithNoPassword() throws StoreException, FindException {
        AuftragVoIPDN auftragVoipDn = new AuftragVoIPDNBuilder().setPersist(false).withSIPPassword("").build();
        doReturn(Arrays.asList(new Rufnummer())).when(cut).getRufnummern();
        when(voipServiceMock.createVoIPDN4Auftrag(eq(AUFTRAG_ID), any(Long.class))).thenReturn(auftragVoipDn);
        cut.checkDN();
    }

    public void testCheckDNSuccess() throws StoreException, FindException {
        AuftragVoIPDN auftragVoipDn = new AuftragVoIPDNBuilder().setPersist(false).build();
        doReturn(Arrays.asList(new Rufnummer())).when(cut).getRufnummern();
        when(voipServiceMock.createVoIPDN4Auftrag(eq(AUFTRAG_ID), any(Long.class))).thenReturn(auftragVoipDn);
        cut.checkDN();
    }
}


