/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 08.06.2010 10:54:16
  */
package de.augustakom.hurrican.service.billing.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.shared.view.DefaultSharedAuftragView;
import de.augustakom.hurrican.service.billing.KundenService;

@Test(groups = BaseTest.UNIT)
public class KundenServiceUnitTest extends BaseTest {

    @DataProvider
    public Object[][] dataProviderLoadKundendaten4AuftragViews() {
        final Kunde kunde1 = createKunde(1L, "Müller", "Tom", null);
        final Kunde kunde2 = createKunde(2L, "Peters", "Hans", new Date());
        final Kunde kunde3 = createKunde(3L, "Maier", "Heike", null);
        return new Object[][] {
                { Arrays.asList(), createViewsForKunden() },
                { Arrays.asList(kunde1), createViewsForKunden(kunde1) },
                { Arrays.asList(kunde2), createViewsForKunden() },
                { Arrays.asList(kunde1, kunde2, kunde3), createViewsForKunden(kunde1, kunde3) },
        };
    }

    @Test(dataProvider = "dataProviderLoadKundendaten4AuftragViews")
    public void testLoadKundendaten4AuftragViews(final List<Kunde> kunden,
            final List<DefaultSharedAuftragView> expectedViews) throws Exception {

        // stub findKundenByKundeNos to create a correct map of requested ids
        KundenService kundenService = spy(new KundenServiceImpl());
        doAnswer(new Answer<Map<Long, Kunde>>() {
            @Override
            public Map<Long, Kunde> answer(InvocationOnMock invocation) throws Throwable {
                Map<Long, Kunde> result = new HashMap<Long, Kunde>();
                Long[] kundenNos = (Long[]) invocation.getArguments()[0];
                for (Long kundeNo : kundenNos) {
                    for (Kunde kunde : kunden) {
                        if (kunde.getKundeNo().equals(kundeNo)) {
                            result.put(kundeNo, kunde);
                        }
                    }
                }
                return result;
            }
        }).when(kundenService).findKundenByKundeNos(any(Long[].class));

        // create a view element for each Kunde
        List<DefaultSharedAuftragView> views = new ArrayList<DefaultSharedAuftragView>();
        for (Kunde kunde : kunden) {
            DefaultSharedAuftragView view = new DefaultSharedAuftragView();
            view.setKundeNo(kunde.getKundeNo());
            views.add(view);
        }
        kundenService.loadKundendaten4AuftragViews(views);

        assertEquals(views.size(), expectedViews.size(), "more or less views expected");
        for (DefaultSharedAuftragView expectedView : expectedViews) {
            boolean exists = false;
            for (DefaultSharedAuftragView actualView : views) {
                if (equalsView(actualView, expectedView)) {
                    exists = true;
                    break;
                }
            }
            assertTrue(exists, "missing view");
        }
    }

    private Kunde createKunde(long kundeNo, String name, String vorname, Date lockDate) {
        Kunde result = new Kunde();
        result.setKundeNo(kundeNo);
        result.setName(name);
        result.setVorname(vorname);
        result.setLockDate(lockDate);
        return result;
    }

    private List<DefaultSharedAuftragView> createViewsForKunden(Kunde... kunden) {
        List<DefaultSharedAuftragView> result = new ArrayList<DefaultSharedAuftragView>();
        for (Kunde kunde : kunden) {
            DefaultSharedAuftragView view = new DefaultSharedAuftragView();
            view.setKundeNo(kunde.getKundeNo());
            view.setName(kunde.getName());
            view.setVorname(kunde.getVorname());
            result.add(view);
        }
        return result;
    }

    private boolean equalsView(DefaultSharedAuftragView actualView, DefaultSharedAuftragView expectedView) {
        if ((actualView.getKundeNo().equals(expectedView.getKundeNo()))
                && (actualView.getName().equals(expectedView.getName()))
                && (actualView.getVorname().equals(expectedView.getVorname()))) {
            return true;
        }
        return false;
    }
}
