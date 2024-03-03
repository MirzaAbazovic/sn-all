/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.13
 */
package de.augustakom.hurrican.model.billing.view;

import java.time.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class BAuftragLeistungViewTest {

    public void displayAsText() {
        ZonedDateTime von = ZonedDateTime.of(2014, 2, 12, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime bis = ZonedDateTime.of(2016, 7, 1, 0, 0, 0, 0, ZoneId.systemDefault());

        BAuftragLeistungView view = new BAuftragLeistungViewBuilder()
                .withAuftragPosGueltigVon(Date.from(von.toInstant()))
                .withAuftragPosGueltigBis(Date.from(bis.toInstant()))
                .withLeistungName("Ü/Test")
                .build();
        Assert.assertEquals(view.displayAsText(), "Ü/Test (von 12.02.2014; bis 01.07.2016)");

        view.setAuftragPosGueltigBis(null);
        Assert.assertEquals(view.displayAsText(), "Ü/Test (von 12.02.2014)");
    }

}
