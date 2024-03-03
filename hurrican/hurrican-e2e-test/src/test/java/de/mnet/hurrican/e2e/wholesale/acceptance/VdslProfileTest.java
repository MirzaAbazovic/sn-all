/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2012 13:37:31
 */
package de.mnet.hurrican.e2e.wholesale.acceptance;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.DSLAMProfile;
import de.augustakom.hurrican.service.cc.DSLAMService;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.Produkt;
import de.mnet.hurrican.e2e.wholesale.acceptance.model.WholesaleOrderState;
import de.mnet.hurrican.wholesale.fault.clearance.ChangeReason;
import de.mnet.hurrican.wholesale.fault.clearance.VdslProfile;

/**
 * Acceptance-Tests fuer WholesaleFaultClearance.getVdslProfiles und WholesaleFaultClearance.changeVdslProfile.
 */
@Test(groups = E2E, enabled = false)
public class VdslProfileTest extends BaseWholesaleE2ETest {
    @Autowired
    private DSLAMService dslamService;

    public void getVdslProfilesTest() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        state.product(Produkt.fttb50().withTP())
                .reservePortInPast(LocalDate.now().minusDays(5));
        state.getVdslProfiles();
        assertNotNull(state.getVdslProfilesResponse);
        assertNotEmpty(state.getVdslProfilesResponse.getPossibleChangeReasons());
        assertPossibleProfiles(state.getVdslProfilesResponse.getPossibleProfiles(), state.produkt.upstream(),
                state.produkt.downstream());
    }

    private void assertPossibleProfiles(List<VdslProfile> possibleProfiles, Integer upstream, Integer downstream)
            throws Exception {
        assertNotEmpty(possibleProfiles);
        for (VdslProfile vdslProfile : possibleProfiles) {
            DSLAMProfile dslamProfile = dslamService.findDSLAMProfiles(vdslProfile.getName()).get(0);
            Assert.assertTrue(NumberTools.isLessOrEqual(dslamProfile.getBandwidth().getDownstream(), downstream));
            Assert.assertTrue(NumberTools.isLessOrEqual(dslamProfile.getBandwidth().getUpstream(), upstream));
        }
    }


    public void changeVdslProfileTest() throws Exception {
        WholesaleOrderState state = getNewWholesaleOrderState();
        state.product(Produkt.fttb50().withTP())
                .reservePortInPast(LocalDate.now().minusDays(5));
        state.getVdslProfiles();

        List<VdslProfile> profiles = state.getVdslProfilesResponse.getPossibleProfiles();
        assertThat(profiles.size(), greaterThan(1));
        VdslProfile newVdslProfile = profiles.get(profiles.size() - 1);
        LocalDate validFrom = LocalDate.now().plusDays(2);
        ChangeReason changeReason = state.getVdslProfilesResponse.getPossibleChangeReasons().get(0);

        state.changeVdslProfile(newVdslProfile.getId(), changeReason.getId(), validFrom,
                "WS-E2E-User", "E2E Test Wechsel");

        DSLAMProfile profile4Auftrag = dslamService.findDSLAMProfile4Auftrag(state.getAuftrag().getAuftragId(),
                Date.from(validFrom.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()), false);
        Assert.assertNotEquals(profile4Auftrag.getId(), Long.valueOf(newVdslProfile.getId()));

        DSLAMProfile newProfile4Auftrag = dslamService.findDSLAMProfile4Auftrag(state.getAuftrag().getAuftragId(),
                Date.from(validFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()), false);
        Assert.assertEquals(newProfile4Auftrag.getId(), Long.valueOf(newVdslProfile.getId()));
    }
}
