/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.14
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class AuftragDatenTest {
    
    
    @DataProvider(name = "isActiveAtDP")
    public Object[][] isActiveAtDP() {
        Date yesterday = Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        Date today = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        Date tomorrow = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        
        AuftragDaten activeSinceYesterday = new AuftragDatenBuilder().setPersist(false).withInbetriebnahme(yesterday).build();
        AuftragDaten activeSinceToday = new AuftragDatenBuilder().setPersist(false).withInbetriebnahme(today).build();
        AuftragDaten activeFromTomorrow = new AuftragDatenBuilder().setPersist(false).withInbetriebnahme(tomorrow).build();
        AuftragDaten activeSinceWithCancelToday = new AuftragDatenBuilder().setPersist(false).withInbetriebnahme(yesterday).withKuendigung(today).build();
        AuftragDaten cancelledYesterday = new AuftragDatenBuilder().setPersist(false).withInbetriebnahme(yesterday).withKuendigung(yesterday).build();
        AuftragDaten activeWithoutDateButState = new AuftragDatenBuilder().setPersist(false).withInbetriebnahme(null).withKuendigung(null).withStatusId(AuftragStatus.IN_BETRIEB).build();
        
        return new Object[][] {
                { activeSinceYesterday, true },
                { activeSinceToday, true },
                { activeFromTomorrow, false },
                { activeSinceWithCancelToday, false },
                { cancelledYesterday, false },
                { activeWithoutDateButState, true },
        };
    }
    
    @Test(dataProvider = "isActiveAtDP")
    public void testIsActiveAt(AuftragDaten testling, boolean expectedResult) throws Exception {
        Assert.assertEquals(testling.isActiveAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())), expectedResult);
    }
    
}
