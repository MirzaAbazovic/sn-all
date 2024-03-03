/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2014
 */
package de.augustakom.hurrican.service.cc.impl.command.leistung;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.Mock;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragTechnikBuilder;
import de.augustakom.hurrican.model.cc.HWSwitchBuilder;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomain;
import de.augustakom.hurrican.model.cc.Produkt2SIPDomainBuilder;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.ReferenceBuilder;
import de.augustakom.hurrican.model.cc.hardware.HWSwitch;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.SIPDomainService;
import de.augustakom.hurrican.service.cc.VoIPService;


@Test(groups = BaseTest.UNIT)
public class SetMaxiSipDomainCommandTest {

    @Spy
    private SetMaxiSipDomainCommand cut = new SetMaxiSipDomainCommand();
    @Mock
    private SIPDomainService sipDomainService;
    @Mock
    private VoIPService voipService;
    @Mock
    CCAuftragService auftragService;

    @BeforeMethod
    void setUp() throws Exception {
        initMocks(this);
        doReturn(sipDomainService).when(cut).getCCService(SIPDomainService.class);
        doReturn(voipService).when(cut).getCCService(VoIPService.class);
        doReturn(auftragService).when(cut).getCCService(CCAuftragService.class);

        when(auftragService.findAuftragDatenByAuftragIdTx(anyLong())).thenReturn(new AuftragDatenBuilder()
                .withProdId(511L).setPersist(false).build());
        when(auftragService.findAuftragTechnikByAuftragIdTx(anyLong())).thenReturn(new AuftragTechnikBuilder()
                .withHwSwitch(new HWSwitchBuilder().build()).setPersist(false).build());
        doNothing().when(cut).createAuftragVoIPDN();
    }

    @DataProvider
    public Object[][] dataProviderSipDomains() {
        Reference defaultSipDomain = new ReferenceBuilder().withId(999999L).withStrValue("def").setPersist(false).build();
        Produkt2SIPDomain defaultProd2Sip = new Produkt2SIPDomainBuilder().withSIPDomainRef(defaultSipDomain).build();
        Reference maxiSipDomain = new ReferenceBuilder().withId(Reference.REF_ID_SIP_DOMAIN_MAXI_M_CALL).withStrValue("maxi").setPersist(false).build();
        Produkt2SIPDomain maxiProd2Sip = new Produkt2SIPDomainBuilder().withSIPDomainRef(maxiSipDomain).build();
        return new Object[][] {
                { Arrays.asList(defaultProd2Sip), defaultSipDomain },
                { Arrays.asList(defaultProd2Sip, new Produkt2SIPDomainBuilder().withSIPDomainRef(
                        new ReferenceBuilder().withId(888888L).withStrValue("other").setPersist(false).build()).setPersist(false).build()), defaultSipDomain },
                { Arrays.asList(defaultProd2Sip, maxiProd2Sip), maxiSipDomain },
        };
    }

    @Test(dataProvider = "dataProviderSipDomains")
    public void testProduktHasMaxiDomain(List<Produkt2SIPDomain> prod2SipDomainRefs, Reference expectedSipDomain) throws Exception {
        when(sipDomainService.findProdukt2SIPDomains(anyLong(), any(HWSwitch.class), anyBoolean()))
                .thenReturn(prod2SipDomainRefs);
        AuftragVoipDNView auftragVoipDNView1 = new AuftragVoipDNViewBuilder().withSipDomain(prod2SipDomainRefs.get(0)
                .getSipDomainRef()).setPersist(false).build();
        List<AuftragVoipDNView> auftragVoipDns = Arrays.asList(auftragVoipDNView1);
        when(voipService.findVoIPDNView(anyLong())).thenReturn(auftragVoipDns);

        ServiceCommandResult result = cut.execute();

        assertNotNull(result);
        assertThat(result.isOk(), equalTo(true));

        assertThat(auftragVoipDNView1.getSipDomain(), equalTo(expectedSipDomain));
    }
}
