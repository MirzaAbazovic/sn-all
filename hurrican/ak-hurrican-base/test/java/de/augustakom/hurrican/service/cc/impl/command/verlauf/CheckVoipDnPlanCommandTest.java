/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.13
 */
package de.augustakom.hurrican.service.cc.impl.command.verlauf;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.base.ServiceCommandResult;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlan;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder;
import de.augustakom.hurrican.model.shared.view.voip.AuftragVoipDNView;
import de.augustakom.hurrican.model.shared.view.voip.BlockDNView;
import de.augustakom.hurrican.service.cc.VoIPService;

@Test(groups = BaseTest.UNIT)
public class CheckVoipDnPlanCommandTest extends BaseTest {

    private static final Long AUFTRAG_ID = Long.MAX_VALUE;

    private static final Date REAL_DATE = new Date();

    @InjectMocks
    @Spy
    private CheckVoipDnPlanCommand cut;

    @Mock
    private VoIPService voipService;

    @BeforeMethod
    public void setUp() {
        cut = new CheckVoipDnPlanCommand();
        MockitoAnnotations.initMocks(this);

        doReturn(AUFTRAG_ID).when(cut).getAuftragId();
        doReturn(REAL_DATE).when(cut).getRealDate();
    }

    @Test
    public void checkAuftragWithoutBlock() throws Exception {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withAuftragId(AUFTRAG_ID)
                .withBlock(BlockDNView.NO_BLOCK)
                .setPersist(false)
                .build();

        when(voipService.findVoIPDNView(AUFTRAG_ID)).thenReturn(ImmutableList.of(dnView));

        ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertTrue(result.isOk());
    }

    @Test
    public void checkAuftragWithValidPlan() throws Exception {
        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("1000", "6999", false),
                new VoipDnBlock("700000", "709999", false),
                new VoipDnBlock("7100", "7999", false),
                new VoipDnBlock("8000", "9999", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(REAL_DATE)
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withAuftragId(AUFTRAG_ID)
                .withBlock(new BlockDNView("", "0", "9999"))
                .withVoipDnPlanViews(ImmutableList.of(planView))
                .withGueltigVon(Date.from(LocalDate.now().plusDays(-1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(DateTools.getHurricanEndDate())
                .setPersist(false)
                .build();

        when(voipService.findVoIPDNView(AUFTRAG_ID)).thenReturn(ImmutableList.of(dnView));

        ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertTrue(result.isOk());
    }

    @Test
    public void checkAuftragWithoutValidPlan() throws Exception {
        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("10", "99", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withAuftragId(AUFTRAG_ID)
                .withBlock(new BlockDNView("", "00", "99"))
                .withVoipDnPlanViews(ImmutableList.of(planView))
                .setPersist(false)
                .build();

        when(voipService.findVoIPDNView(AUFTRAG_ID)).thenReturn(ImmutableList.of(dnView));

        ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertFalse(result.isOk());
    }

    @Test
    public void checkAuftragWithExpiredPlan() throws Exception {
        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("10", "99", false));

        final VoipDnPlan plan = VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(REAL_DATE)
                .withVoipDnBlocks(blocks)
                .setPersist(false)
                .build();

        final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", plan);

        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withAuftragId(AUFTRAG_ID)
                .withBlock(new BlockDNView("", "0", "9999"))
                .withVoipDnPlanViews(ImmutableList.of(planView))
                .withGueltigVon(Date.from(LocalDate.now().plusDays(-2).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .withGueltigBis(Date.from(LocalDate.now().plusDays(-1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .setPersist(false)
                .build();

        when(voipService.findVoIPDNView(AUFTRAG_ID)).thenReturn(ImmutableList.of(dnView));

        ServiceCommandResult result = (ServiceCommandResult) cut.execute();
        assertTrue(result.isOk());
    }
}
