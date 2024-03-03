/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.12.14
 */
package de.mnet.wbci.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.model.cc.temp.RevokeTerminationModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = BaseTest.UNIT)
public class WbciAutomationTxHelperServiceImplTest {

    @Mock
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    @Mock
    private CCAuftragStatusService auftragStatusService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private CarrierService carrierService;

    @InjectMocks
    @Spy
    private WbciAutomationTxHelperServiceImpl testling;
    private static WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;

    @BeforeMethod
    public void setUp() {
        testling = new WbciAutomationTxHelperServiceImpl();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testUndoCancellationOfHurricanOrders() throws FindException, StoreException {
        ErledigtmeldungStornoAuf erlmStrAufhAuf = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .buildValidForStorno(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);

        AuftragDaten alreadyCancelled = new AuftragDatenBuilder()
                .withRandomAuftragId()
                .withStatusId(AuftragStatus.AUFTRAG_GEKUENDIGT).setPersist(false).build();
        AuftragDaten cancelInProgress = new AuftragDatenBuilder()
                .withRandomAuftragId()
                .withStatusId(AuftragStatus.KUENDIGUNG_TECHN_REAL).setPersist(false).build();
        AuftragDaten active = new AuftragDatenBuilder()
                .withRandomAuftragId()
                .withStatusId(AuftragStatus.IN_BETRIEB).setPersist(false).build();
        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder()
                .withRandomId()
                .withKuendigungAnCarrier(new Date())
                .setPersist(false).build();
        WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder()
                .withRandomId()
                .withTyp(WitaCBVorgang.TYP_KUENDIGUNG)
                .withCbId(carrierbestellung.getId())
                .setPersist(false).build();
        AKUser user = new AKUserBuilder().setPersist(false).build();

        when(auftragService.findAuftragDaten4OrderNoOrig(anyLong())).thenReturn(
                Arrays.asList(alreadyCancelled, cancelInProgress, active));
        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(eq(cancelInProgress.getAuftragId()), anyString()))
                .thenReturn(witaCBVorgang);
        when(wbciWitaServiceFacade.doWitaStorno(erlmStrAufhAuf, witaCBVorgang.getId(), user))
                .thenReturn(witaCBVorgang);
        when(carrierService.findCB(witaCBVorgang.getCbId())).thenReturn(carrierbestellung);

        testling.undoCancellationOfHurricanOrders(1L, erlmStrAufhAuf, user, 99L);

        assertNull(carrierbestellung.getKuendigungAnCarrier());
        verify(auftragService).revokeTermination(any(RevokeTerminationModel.class));
        verify(carrierService).saveCB(carrierbestellung);
        verify(testling).doWitaStorno(erlmStrAufhAuf, cancelInProgress.getAuftragId(), user);
    }


    @DataProvider(name = "doWitaStornoDP")
    public Object[][] doWitaStornoDP() {
        return new Object[][] {
                { new WitaCBVorgangBuilder().withTyp(WitaCBVorgang.TYP_KUENDIGUNG).setPersist(false).build(), 1 },
                { null, 0 },
                { new WitaCBVorgangBuilder().withTyp(WitaCBVorgang.TYP_NEU).setPersist(false).build(), 0 },
                { new WitaCBVorgangBuilder().withTyp(WitaCBVorgang.TYP_ANBIETERWECHSEL).setPersist(false).build(), 0 },
                { new WitaCBVorgangBuilder().withTyp(WitaCBVorgang.TYP_KUENDIGUNG)
                        .withAenderungsKennzeichen(AenderungsKennzeichen.STORNO).setPersist(false).build(), 0 },
        };
    }


    @Test(dataProvider = "doWitaStornoDP")
    public void testDoWitaStorno(WitaCBVorgang existingCbv, int expectStornoCalls) {
        ErledigtmeldungStornoAuf erlmStrAufhAuf = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .buildValidForStorno(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);
        Long auftragId = 99L;

        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(eq(auftragId), anyString())).thenReturn(existingCbv);

        testling.doWitaStorno(erlmStrAufhAuf, auftragId, new AKUserBuilder().setPersist(false).build());

        verify(wbciWitaServiceFacade, times(expectStornoCalls)).doWitaStorno(
                any(ErledigtmeldungStornoAuf.class), anyLong(), any(AKUser.class));
    }
    
}
