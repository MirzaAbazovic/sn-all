/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.03.2012 07:06:05
 */
package de.augustakom.hurrican.service.cc.impl.command.cps;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.time.*;
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
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.billing.Rufnummer;
import de.augustakom.hurrican.model.billing.RufnummerBuilder;
import de.augustakom.hurrican.model.cc.AuftragVoIPDN;
import de.augustakom.hurrican.model.cc.AuftragVoIPDNBuilder;
import de.augustakom.hurrican.model.cc.CPSTransactionBuilder;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.cps.CPSTransaction;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CPSVoIPSIPAccountData;
import de.augustakom.hurrican.model.cc.cps.serviceorder.CpsVoipRangeData;
import de.augustakom.hurrican.service.cc.CCLeistungsService;

@Test(groups = BaseTest.UNIT)
public class CPSGetVoIPDataCommandUnitTest extends BaseTest {

    @Spy
    @InjectMocks
    private CPSGetVoIPDataCommand cut;

    @Mock
    private CCLeistungsService ccLeistungsService;

    @BeforeMethod
    public void setUp() {
        cut = new CPSGetVoIPDataCommand();
        MockitoAnnotations.initMocks(this);

        CPSTransaction cpsTx = new CPSTransactionBuilder().setPersist(false).withEstimatedExecTime(new Date()).build();
        doReturn(cpsTx).when(cut).getCPSTransaction();
    }

    @DataProvider
    public Object[][] addBlockToSipAccountDataProvider() {
        return new Object[][] {
                { "1", "10" },
                { "10", "20" },
                { "1", "100" },
                { "1", "100" }
        };
    }

    @Test(dataProvider = "addBlockToSipAccountDataProvider")
    public void addBlockToSipAccount(String rangeFrom, String rangeTo) {
        final Rufnummer dn = new Rufnummer();
        dn.setRangeFrom(rangeFrom);
        dn.setRangeTo(rangeTo);
        final CPSVoIPSIPAccountData sipAccountData = new CPSVoIPSIPAccountData();

        cut.addBlockToSipAccData(sipAccountData, dn);

        assertThat(sipAccountData.getBlockStart().length(), equalTo(rangeTo.length()));
        assertThat(sipAccountData.getBlockEnd(), equalTo(rangeTo));
    }

    public void testGetCpsVoipRangeDatas() {
        final Rufnummer rufnummer = new RufnummerBuilder()
                .withOnKz("0821")
                .withDnBase("1234")
                .build();
        final VoipDnBlock expectedVoipDnBlock = new VoipDnBlock("0", null, true);
        final VoipDnPlan dnPlanNichtMehrGueltig = new VoipDnPlanBuilder()
                .withRandomId()
                .withGueltigAb(Date.from(LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()))
                .withVoipDnBlocks(Lists.newArrayList(new VoipDnBlock("1", null, true)))
                .build();
        final VoipDnPlan dnPlanAktuellGueltig = new VoipDnPlanBuilder()
                .withRandomId()
                .withVoipDnBlocks(Lists.newArrayList(expectedVoipDnBlock))
                .withGueltigAb(Date.from(LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        final VoipDnPlan dnPlanNochNichtGueltig = new VoipDnPlanBuilder()
                .withRandomId()
                .withGueltigAb(Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()))
                .withVoipDnBlocks(Lists.newArrayList(new VoipDnBlock("2", null, true)))
                .build();
        final List<VoipDnPlan> dnPlans = Lists.newArrayList(dnPlanNichtMehrGueltig, dnPlanNochNichtGueltig,
                dnPlanAktuellGueltig);
        Collections.shuffle(dnPlans);
        final AuftragVoIPDN auftragVoipDn = new AuftragVoIPDNBuilder()
                .withRufnummernplaene(dnPlans)
                .build();
        final List<CpsVoipRangeData> result = cut.getCpsVoipRangeDatas(rufnummer, auftragVoipDn);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDn(), equalTo(rufnummer.getDnBase()));
        assertThat(result.get(0).getLac(), equalTo(rufnummer.getOnKz()));
        assertThat(result.get(0).getRangeStart(), equalTo(expectedVoipDnBlock.getAnfang()));
        assertThat(result.get(0).getRangeEnd(), equalTo(expectedVoipDnBlock.getEnde()));
        assertThat(result.get(0).getMainOffice(),
                equalTo(BooleanTools.getBooleanAsString(expectedVoipDnBlock.getZentrale())));
    }

}
