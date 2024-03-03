/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.12.2011 15:16:24
 */
package de.augustakom.hurrican.service.cc.impl.ipaddress;


import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.commons.lang.time.DateUtils;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.service.base.exceptions.FindException;

/**
 * Modultests fuer {@link ReleaseDateCalculator}.
 *
 *
 * @since Release 10
 */
@Test(groups = BaseTest.UNIT)
public class ReleaseDateCalculatorTest extends BaseTest {

    @Spy
    private ReleaseDateCalculator cut = new ReleaseDateCalculator();

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider
    public Object[][] dataProviderGet() {
        Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        return new Object[][] {
                { 20, DateTools.changeDate(now, Calendar.DAY_OF_MONTH, -20) },
                { 30, DateTools.changeDate(now, Calendar.DAY_OF_MONTH, -30) },
                { 100, DateTools.changeDate(now, Calendar.DAY_OF_MONTH, -100) },
        };
    }

    @Test(dataProvider = "dataProviderGet")
    public void get(int daysConfigured, Date expectedResult) throws FindException {
        doReturn(daysConfigured).when(cut).getDaysConfigured();
        Date result = cut.get();
        Assert.assertTrue(result.getTime() == expectedResult.getTime());
    }

} // end


