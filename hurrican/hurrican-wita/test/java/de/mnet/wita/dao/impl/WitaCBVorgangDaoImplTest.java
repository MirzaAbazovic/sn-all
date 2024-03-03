/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.14
 */
package de.mnet.wita.dao.impl;

import java.util.*;
import javax.inject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.servicetest.AbstractServiceTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.dao.WitaCBVorgangDao;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

/**
 *
 */
@Test(groups = BaseTest.SERVICE)
public class WitaCBVorgangDaoImplTest extends AbstractServiceTest {
    @Autowired
    private WitaCBVorgangDao testling;
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private Provider<WitaCBVorgangBuilder> cbVorgangBuilder;


    @Test
    public void testFindWitaCBVorgangIDsForKlammerId() throws Exception {
        Long klammerId = 99999L;
        WitaCBVorgang cbVorgang1 = cbVorgangBuilder.get().withAuftragsKlammer(klammerId).build();
        WitaCBVorgang cbVorgang2 = cbVorgangBuilder.get().withAuftragsKlammer(klammerId).build();
        mwfEntityDao.store(cbVorgang1);
        mwfEntityDao.store(cbVorgang2);
        List<Long> result = testling.findWitaCBVorgangIDsForKlammerId(klammerId);
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.contains(cbVorgang1.getCbId()));
        Assert.assertTrue(result.contains(cbVorgang2.getCbId()));
    }

    @Test
    public void shouldFindWitaCBVorgaengByRefId() throws Exception {
        Long cbVorgangRefId = 1L;
        WitaCBVorgang cbVorgang1 = cbVorgangBuilder.get().withCbVorgangRefId(cbVorgangRefId).build();
        mwfEntityDao.store(cbVorgang1);

        WitaCBVorgang result = testling.findWitaCBVorgangByRefId(cbVorgangRefId);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getId(), cbVorgang1.getId());
    }

    @Test
    public void shouldNotFindWitaCBVorgaengByRefId() throws Exception {
        WitaCBVorgang result = testling.findWitaCBVorgangByRefId(-1L);
        Assert.assertNull(result);
    }
}
