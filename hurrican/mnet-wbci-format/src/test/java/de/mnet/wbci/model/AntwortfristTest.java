/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.01.14
 */
package de.mnet.wbci.model;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wbci.model.builder.AntwortfristBuilder;

/**
 *
 */
public class AntwortfristTest {

    @DataProvider(name = "antwortfristen")
    public Object[][] getAnswerDeadlines() {
        return new Object[][] {
                { 72L, 3L },
                { 60L, 3L },
                { 47L, 2L },
                { 48L, 2L },
                { 49L, 3L },
        };
    }

    @Test(dataProvider = "antwortfristen")
    public void calculateAnswerDeadline(Long hours, Long expectedDays) {
        final Antwortfrist antwortfrist = new AntwortfristBuilder()
                .withFristInStunden(hours)
                .build();
        assertEquals(antwortfrist.getFristInTagen(), expectedDays);
    }

}
