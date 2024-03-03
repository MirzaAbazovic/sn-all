/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.03.2012 11:34:18
 */
package de.mnet.hurrican.e2e.wholesale.acceptance.matcher;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.hurrican.e2e.wholesale.acceptance.matcher.DslamProfileValidFromMatcher.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Auftrag2DSLAMProfile;

@Test(groups = UNIT)
public class DslamProfileValidFromMatcherTest extends BaseTest {

    public void testMatching() {
        LocalDate now = LocalDate.now();
        Auftrag2DSLAMProfile profile = new Auftrag2DSLAMProfile();
        profile.setGueltigVon(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        assertThat(profile, dslamProfileValidFrom(now));
    }

    public void testNotMatching() {
        LocalDate now = LocalDate.now();
        Auftrag2DSLAMProfile profile = new Auftrag2DSLAMProfile();
        profile.setGueltigVon(Date.from(now.minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        assertThat(profile, not(dslamProfileValidFrom(now)));
    }

}


