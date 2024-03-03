/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2014
 */
package de.mnet.wbci.helper;

import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.CarrierCode;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class MailEscalationHelperTest {

    @DataProvider
    public static Object[][] generateOverviewReportSubjectDataProvider() {
        return new Object[][] {
                { Date.from(LocalDate.of(2014, 5, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), 
                        "Liste WBCI-Eskalationen (Stand 01.05.2014, Applikationsmodus " },
                { Date.from(LocalDate.of(2014, 5, 2).atStartOfDay(ZoneId.systemDefault()).toInstant()), 
                        "Liste WBCI-Eskalationen (Stand 02.05.2014, Applikationsmodus " },
        };
    }

    @Test(dataProvider = "generateOverviewReportSubjectDataProvider")
    public void testGenerateOverviewReportSubjectExtern(Date date, String expectedSubject) throws Exception {
        assertTrue(MailEscalationHelper.generateCarrierOverviewReportSubject(date).startsWith(expectedSubject));
    }

    @DataProvider
    public static Object[][] generateInternalReportSubjectDataProvider() {
        return new Object[][] {
                { Date.from(LocalDate.of(2014, 5, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        "Interne Prueflisten WBCI (Stand 01.05.2014, Applikationsmodus " },
                { Date.from(LocalDate.of(2014, 5, 2).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        "Interne Prueflisten WBCI (Stand 02.05.2014, Applikationsmodus " },
        };
    }

    @Test(dataProvider = "generateInternalReportSubjectDataProvider")
    public void testGenerateOverviewReportSubjectIntern(Date date, String expectedSubject) throws Exception {
        assertTrue(MailEscalationHelper.generateInternalOverviewReportSubject(date).startsWith(expectedSubject));
    }

    @DataProvider
    public static Object[][] generateCarrierReportSubjectDataProvider() {
        return new Object[][] {
                { CarrierCode.DTAG, Date.from(LocalDate.of(2014, 5, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()), 
                        "Liste WBCI-Eskalationen für DEU.DTAG (Stand 01.05.2014, Applikationsmodus " },
                { CarrierCode.VODAFONE, Date.from(LocalDate.of(2014, 5, 2).atStartOfDay(ZoneId.systemDefault()).toInstant()), 
                        "Liste WBCI-Eskalationen für DEU.VFDE (Stand 02.05.2014, Applikationsmodus " },
        };
    }

    @Test(dataProvider = "generateCarrierReportSubjectDataProvider")
    public void testGenerateCarrierReportSubject(CarrierCode carrierCode, Date date, String expectedSubject) throws Exception {
        assertTrue(MailEscalationHelper.generateCarrierReportSubject(carrierCode, date).startsWith(expectedSubject));
    }

}
