/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.04.2014
 */
package de.augustakom.hurrican.model.shared.view.voip;

import java.time.*;
import java.util.*;
import java.util.function.*;
import com.google.common.collect.ImmutableList;
import org.testng.Assert;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.VoipDnBlock;
import de.augustakom.hurrican.model.cc.VoipDnPlanBuilder;
import de.augustakom.hurrican.model.cc.VoipDnPlanView;
import de.augustakom.hurrican.model.cc.view.AuftragVoipDNViewBuilder;
import de.mnet.common.tools.DateConverterUtils;

@Test(groups = BaseTest.UNIT)
public class AuftragVoipDNViewTest {

    @DataProvider
    private Object[][] testValidVoipDnPlanViewsDp() {
        return new Object[][] {
                { new int[] {},                 0, 0 },
                { new int[] {0},                0, 1 },
                { new int[] {0,0},              0, 1 },
                { new int[] {1},                0, 1 },
                { new int[] {1,1},              0, 2 },
                { new int[] {-1},               1, 0 },
                { new int[] {-1,-1},            1, 0 },
                { new int[] {1,3,5},            0, 3 },
                { new int[] {-5,-3,-1},         1, 0 },
                { new int[] {-3,-1,1,3},        1, 2 },
                { new int[] {-3,-1,-1,1,3},     1, 2 },
                { new int[] {-1,0,1},           0, 2 },
                { new int[] {-1,0,0,1},         0, 2 },
        };
    }

    @Test(dataProvider = "testValidVoipDnPlanViewsDp")
    public void testValidVoipDnPlanViews(int[] dayDeltas, int pastCount, int futureCount) {
        final AuftragVoipDNView dnView = createView(dayDeltas);
        assertValidPlans(dnView, pastCount, futureCount);
    }

    private AuftragVoipDNView createView(int... dayDeltas) {
        final List<VoipDnBlock> blocks = ImmutableList.of(
                new VoipDnBlock("0", null, true),
                new VoipDnBlock("10", "19", false),
                new VoipDnBlock("20", "29", false));

        List<VoipDnPlanView> planViews = new ArrayList<>();
        Date today = new Date();
        for (int dayDelta : dayDeltas) {
            final VoipDnPlanView planView = new VoipDnPlanView("012345", "67890", VoipDnPlanBuilder.aVoipDnPlan()
                    .withRandomId()
                    .withGueltigAb(Date.from(LocalDate.from(today.toInstant().atZone(ZoneId.systemDefault())).plusDays(dayDelta).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .withVoipDnBlocks(blocks)
                    .setPersist(false)
                    .build());
            planViews.add(planView);
        }
        return new AuftragVoipDNViewBuilder()
                .withVoipDnPlanViews(planViews)
                .setPersist(false)
                .build();
    }

    private void assertValidPlans(AuftragVoipDNView dnView, int expectedPast, int expectedFuture) {
        List<VoipDnPlanView> validVoipDnPlanViews = dnView.getValidVoipDnPlanViews();
        Assert.assertEquals(validVoipDnPlanViews.size(), expectedPast + expectedFuture);
        int future = 0;
        int past = 0;
        Date today = new Date();
        for (VoipDnPlanView aView : validVoipDnPlanViews) {
            if (DateTools.isDateAfterOrEqual(aView.getGueltigAb(), today)) {
                future++;
            }
            else {
                past++;
            }
        }
        Assert.assertEquals(past, expectedPast);
        Assert.assertEquals(future, expectedFuture);
    }

    @DataProvider
    public Object[][] voipDnPlanDataProvider() {
        Function<Date, VoipDnPlanView> viewGueltigAb = date ->
                new VoipDnPlanView("", "", VoipDnPlanBuilder.aVoipDnPlan()
                        .withRandomId()
                        .withGueltigAb(date)
                        .withVoipDnBlocks(ImmutableList.of())
                        .setPersist(false)
                        .build());

        // @formatter:off
       return new Object[][] {
                { viewGueltigAb.apply(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())),
                        viewGueltigAb.apply(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant())), Optional.of(true)},  // tests FutureOnly
                { viewGueltigAb.apply(Date.from(LocalDate.now().plusDays(-2).atStartOfDay(ZoneId.systemDefault()).toInstant())),
                        viewGueltigAb.apply(Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant())), Optional.of(false)}, // tests PastAndFuture
                { viewGueltigAb.apply(Date.from(LocalDate.now().plusDays(-2).atStartOfDay(ZoneId.systemDefault()).toInstant())),
                        viewGueltigAb.apply(Date.from(LocalDate.now().plusDays(-1).atStartOfDay(ZoneId.systemDefault()).toInstant())), Optional.empty()},  // tests PastOnly
        };
        // @formatter:on
    }

    @Test(dataProvider = "voipDnPlanDataProvider")
    public void testGetFirstInFutureOrLatest(VoipDnPlanView plan01, VoipDnPlanView plan02, Optional<Boolean> expectFirstPlan) {
        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withVoipDnPlanViews(ImmutableList.of(plan01, plan02))
                .setPersist(false)
                .build();

        final Optional<VoipDnPlanView> plan = dnView.getFirstInFutureOrLatestVoipDnPlanView();
        if (!expectFirstPlan.isPresent()) {
            Assert.assertFalse(plan.isPresent());
        }
        else {
            Assert.assertTrue(plan.isPresent());
            if(expectFirstPlan.get()) {
                Assert.assertEquals(plan.get(), plan01);
            } else {
                Assert.assertEquals(plan.get(), plan02);
            }
        }
    }

    @Test
    public void testGetVoipDnPlanViewsAfterDate() {
        final VoipDnPlanView plan1 = new VoipDnPlanView("", "", VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(DateConverterUtils.asDate(LocalDate.now().plusDays(2)))
                .withVoipDnBlocks(ImmutableList.of())
                .setPersist(false)
                .build());
        final VoipDnPlanView plan2 = new VoipDnPlanView("", "", VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(DateConverterUtils.asDate(LocalDate.now().plusDays(1)))
                .withVoipDnBlocks(ImmutableList.of())
                .setPersist(false)
                .build());
        final VoipDnPlanView plan3 = new VoipDnPlanView("", "", VoipDnPlanBuilder.aVoipDnPlan()
                .withRandomId()
                .withGueltigAb(DateConverterUtils.asDate(LocalDate.now().minusDays(1)))
                .withVoipDnBlocks(ImmutableList.of())
                .setPersist(false)
                .build());

        final AuftragVoipDNView dnView = new AuftragVoipDNViewBuilder()
                .withVoipDnPlanViews(ImmutableList.of(plan1, plan2, plan3))
                .setPersist(false)
                .build();

        final List<VoipDnPlanView> planViews = dnView.getVoipDnPlanViewsAfterDate(new Date());
        assertNotNull(planViews);
        assertEquals(planViews.size(), 2);
        assertEquals(planViews.get(0).getGueltigAb(), DateConverterUtils.asDate(LocalDate.now().plusDays(1)));
        assertEquals(planViews.get(1).getGueltigAb(), DateConverterUtils.asDate(LocalDate.now().plusDays(2)));
    }
}
