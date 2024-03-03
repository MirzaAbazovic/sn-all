/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2012 09:19:43
 */
package de.augustakom.hurrican.model.wholesale;

import static de.augustakom.common.BaseTest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.fttx.CVlan;
import de.augustakom.hurrican.model.cc.fttx.CVlanBuilder;
import de.augustakom.hurrican.model.cc.fttx.CvlanServiceTyp;

@Test(groups = UNIT)
public class WholesalePbitTest extends BaseTest {

    @Test
    public void createPbitsFromCVlans() {
        CVlan cvlanVoip = new CVlanBuilder().withTyp(CvlanServiceTyp.VOIP).withPbitLimit(Integer.valueOf(1)).setPersist(false).build();
        CVlan cvlanHsi = new CVlanBuilder().withTyp(CvlanServiceTyp.HSI).withPbitLimit(Integer.valueOf(2)).setPersist(false).build();
        CVlan cvlanNoPbit = new CVlanBuilder().withTyp(CvlanServiceTyp.IAD).withPbitLimit(null).setPersist(false).build();

        List<WholesalePbit> result = WholesalePbit.createPbitsFromCVlans(Arrays.asList(cvlanVoip, cvlanHsi, cvlanNoPbit));
        assertNotEmpty(result);
        assertThat(result.size(), equalTo(Integer.valueOf(2)));

        for (WholesalePbit pbit : result) {
            if (CvlanServiceTyp.IAD.name().equals(pbit.getService())) {
                fail("CVlan without Pbit limit should not be in the Pbit result!");
            }
        }
    }

}


