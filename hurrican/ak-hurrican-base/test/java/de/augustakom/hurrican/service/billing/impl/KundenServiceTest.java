/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 08.06.2010 10:54:16
  */
package de.augustakom.hurrican.service.billing.impl;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.shared.view.DefaultSharedAuftragView;
import de.augustakom.hurrican.service.AbstractHurricanBaseServiceTest;
import de.augustakom.hurrican.service.billing.KundenService;

@Test(groups = BaseTest.SERVICE)
public class KundenServiceTest extends AbstractHurricanBaseServiceTest {

    @DataProvider
    public Object[][] createAuftragsViews() {
        DefaultSharedAuftragView unlocked = new DefaultSharedAuftragView();
        unlocked.setKundeNo(100000002L);
        DefaultSharedAuftragView locked = new DefaultSharedAuftragView();
        locked.setKundeNo(100000412L);
        return new Object[][] { new Object[] { unlocked, locked } };
    }

    @Test(dataProvider = "createAuftragsViews")
    public void testLoadKundendaten4AuftragViewsFilter(DefaultSharedAuftragView unlocked, DefaultSharedAuftragView locked) throws Exception {
        List<DefaultSharedAuftragView> views = new ArrayList<DefaultSharedAuftragView>(Arrays.asList(unlocked, locked));
        KundenService sut = getBillingService(KundenService.class);
        sut.loadKundendaten4AuftragViews(views);
        assertEquals(views.size(), 1);
        assertEquals(views.get(0), unlocked);
    }
}
