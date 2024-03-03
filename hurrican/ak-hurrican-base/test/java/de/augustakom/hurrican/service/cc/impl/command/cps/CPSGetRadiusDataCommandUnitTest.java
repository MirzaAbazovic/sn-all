/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.10.2011 15:54:02
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.Lists;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AuftragBuilder;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.IntAccount;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSRadiusAccountData;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.RangierungsService;

@Test(groups = BaseTest.UNIT)
public class CPSGetRadiusDataCommandUnitTest extends BaseTest {

    @Spy
    @InjectMocks
    private CPSGetRadiusDataCommand cut;

    @Mock
    private CCLeistungsService leistungsService;

    @Mock
    protected EndstellenService endstellenService;
    @Mock
    protected HVTService hvtService;
    @Mock
    protected RangierungsService rangierungsService;
    @Mock
    protected HWService hwService;

    private CPSTransaction cpsTx;

    @BeforeMethod
    public void setUp() {
        cut = new CPSGetRadiusDataCommand();
        MockitoAnnotations.initMocks(this);

        cpsTx = new CPSTransactionBuilder().withAuftragBuilder(new AuftragBuilder().withRandomId()).setPersist(false)
                .build();
        doReturn(cpsTx).when(cut).getCPSTransaction();
    }

    public void testExecuteWithEmptyAccountsAndNeedsAccount() throws Exception {
        doNothing().when(cut).loadTechnicalOrderId();
        doReturn(new ProduktBuilder().setPersist(false).build()).when(cut).getProdukt4Auftrag(any(Long.class));
        doReturn(new ArrayList<IntAccount>()).when(cut).getAccounts();
        doReturn(true).when(cut).needsAccount(any(Produkt.class));

        doCallRealMethod().when(cut).execute();
        Object result = cut.execute();
        assertTrue(result instanceof ServiceCommandResult);
        ServiceCommandResult scr = (ServiceCommandResult) result;
        assertFalse(scr.isOk());

        verify(cut, times(0)).defineAccountData(any(CPSRadiusAccountData.class), any(IntAccount.class));
    }

    @DataProvider
    Object[][] loadQosProfileDataProvider() {
        final TechLeistung techLsQosProfile = new TechLeistungBuilder().withRandomStrValue().build();
        return new Object[][] {
                { techLsQosProfile, techLsQosProfile.getStrValue() },
                { null, null }
        };
    }

    @Test(dataProvider = "loadQosProfileDataProvider")
    public void testLoadQosProfile(final TechLeistung techLsQosProfile, String expectedResult) throws Exception {
        final AuftragDaten auftragDaten = new AuftragDatenBuilder().withRandomAuftragId().build();
        final Date execDate = new Date();
        final CPSRadiusAccountData accData = new CPSRadiusAccountData();

        doReturn(auftragDaten).when(cut).getAuftragDaten();
        when(leistungsService.findTechLeistung4Auftrag(auftragDaten.getAuftragId(),
                TechLeistung.TYP_SIPTRUNK_QOS_PROFILE, execDate)).thenReturn(techLsQosProfile);

        cut.loadQosProfile(execDate, accData);

        assertThat(accData.getQosProfile(), equalTo(expectedResult));
    }

    @DataProvider
    Object[][] isRadiusAccountNecessaryDataProvider() {
        return new Object[][] {
                { true , 511 , true },
                { false, 511 , false },
                { true , 1234, true },
                { false, 1234, true },
        };
    }

    @Test(dataProvider = "isRadiusAccountNecessaryDataProvider")
    public void testIsRadiusAccountNecessary(boolean findTechLs, long prod_id, boolean expectedResult)
            throws Exception {
        final TechLeistung techLs = new TechLeistungBuilder().withRandomId().build();
        final Produkt produkt = new ProduktBuilder().withRandomId()
                .withAutoHvtZuordnung(false)
                .withId(prod_id)
                .build();
        if (findTechLs) {
            when(leistungsService.findTechLeistungen4Auftrag(cpsTx.getAuftragId(), TechLeistung.TYP_ENDGERAET,
                    cpsTx.getEstimatedExecTime())).thenReturn(Lists.newArrayList(techLs));
        }

        assertEquals(cut.isRadiusAccountNecessary(cpsTx.getAuftragId(), produkt), expectedResult);
    }

}
