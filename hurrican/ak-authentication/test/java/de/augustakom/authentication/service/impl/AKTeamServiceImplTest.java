/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.01.14
 */
package de.augustakom.authentication.service.impl;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKTeam;
import de.augustakom.authentication.service.AbstractAuthenticationTest;
import de.augustakom.common.BaseTest;

public class AKTeamServiceImplTest extends AbstractAuthenticationTest {
    @Test(groups = BaseTest.SERVICE)
    public void testFindAll() throws Exception {
        List<AKTeam> result = getTeamService().findAll();
        Assert.assertTrue(!result.isEmpty());
    }

    @Test(groups = BaseTest.SERVICE)
    public void testFindAllAsMap() throws Exception {
        List<AKTeam> result = getTeamService().findAll();
        Assert.assertTrue(!result.isEmpty());

        Map<Long, AKTeam> map = getTeamService().findAllAsMap();
        Assert.assertTrue(map.values().containsAll(result));
    }
}
