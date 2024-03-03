/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.05.14
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class KuendigungFristTest {

    @Test
    public void calculateNextMvlz() {
        LocalDateTime vertragsende = LocalDateTime.of(2013, 7, 1, 0, 0, 0, 0);
        LocalDateTime cancellationIncome = LocalDateTime.of(2014, 2, 1, 0, 0, 0, 0);

        LocalDateTime result = new KuendigungFristBuilder()
                .withAutoVerlaengerung(Long.valueOf(3))
                .build()
                .calculateNextMvlz(cancellationIncome, vertragsende);
        Assert.assertEquals(result, LocalDateTime.of(2014, 4, 1, 0, 0, 0, 0));
    }

    @Test
    public void calculateNextMvlzWithoutAutoRenewal() {
        LocalDateTime vertragsende = LocalDateTime.of(2013, 7, 1, 0, 0, 0, 0);
        LocalDateTime cancellationIncome = LocalDateTime.of(2014, 2, 1, 0, 0, 0, 0);

        LocalDateTime result = new KuendigungFristBuilder()
                .withAutoVerlaengerung(Long.valueOf(0))
                .build()
                .calculateNextMvlz(cancellationIncome, vertragsende);
        Assert.assertEquals(result, vertragsende);
    }

}
