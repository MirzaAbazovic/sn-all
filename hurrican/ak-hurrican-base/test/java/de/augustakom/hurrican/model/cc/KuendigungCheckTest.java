/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.12.13
 */
package de.augustakom.hurrican.model.cc;

import java.time.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragBuilder;

@Test(groups = BaseTest.UNIT)
public class KuendigungCheckTest {

    @DataProvider(name = "getRelevantKuendigungFristDataProvider")
    public Object[][] getRelevantKuendigungFristDataProvider() {
        KuendigungFrist frist201306 = new KuendigungFristBuilder().withMitMvlz().withVertragsabschlussJahr(2013L).withVertragsabschlussMonat(6L).setPersist(false).build();
        KuendigungFrist frist201201 = new KuendigungFristBuilder().withMitMvlz().withVertragsabschlussJahr(2012L).withVertragsabschlussMonat(1L).setPersist(false).build();
        KuendigungFrist fristOhneMvlz = new KuendigungFristBuilder().setPersist(false).build();

        KuendigungCheck kuendigungCheck = new KuendigungCheckBuilder()
                .addKuendiungFrist(frist201201)
                .addKuendiungFrist(frist201306)
                .addKuendiungFrist(fristOhneMvlz)
                .setPersist(false).build();

        BAuftrag billingOrderOhneMvlz = new BAuftragBuilder().build();
        BAuftrag billingOrder201306 = new BAuftragBuilder().withVertragsLaufzeit(12L).withVertragsdatum(new Date()).build();
        BAuftrag billingOrder201201 = new BAuftragBuilder().withVertragsLaufzeit(12L)
                .withVertragsdatum(Date.from(ZonedDateTime.of(2012, 6, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant())).build();

        return new Object[][] {
                { kuendigungCheck, billingOrderOhneMvlz, fristOhneMvlz },
                { kuendigungCheck, billingOrder201306, frist201306 },
                { kuendigungCheck, billingOrder201201, frist201201 },
        };
    }


    @Test(dataProvider = "getRelevantKuendigungFristDataProvider")
    public void getRelevantKuendigungFrist(KuendigungCheck kuendigungCheck, BAuftrag billingOrder, KuendigungFrist expectedResult) {
        KuendigungFrist result = kuendigungCheck.getRelevantKuendigungFrist(billingOrder);
        Assert.assertEquals(result, expectedResult);
    }


    @Test
    public void testCompareByYearMonth() {
        KuendigungFrist frist201306 = new KuendigungFristBuilder().withMitMvlz().withVertragsabschlussJahr(2013L).withVertragsabschlussMonat(6L).setPersist(false).build();
        KuendigungFrist frist201201 = new KuendigungFristBuilder().withMitMvlz().withVertragsabschlussJahr(2012L).withVertragsabschlussMonat(1L).setPersist(false).build();
        KuendigungFrist frist201207 = new KuendigungFristBuilder().withMitMvlz().withVertragsabschlussJahr(2012L).withVertragsabschlussMonat(7L).setPersist(false).build();
        KuendigungFrist fristOhneMvlz = new KuendigungFristBuilder().setPersist(false).build();
        KuendigungFrist fristOhneMvlz2 = new KuendigungFristBuilder().setPersist(false).build();

        List<KuendigungFrist> unsorted = Arrays.asList(frist201201, frist201306, fristOhneMvlz, frist201207, fristOhneMvlz2);
        List<KuendigungFrist> sorted = new KuendigungCheck().sortKuendigungsFristen(unsorted);
        Assert.assertEquals(sorted.get(2), frist201201);
        Assert.assertEquals(sorted.get(3), frist201207);
        Assert.assertEquals(sorted.get(4), frist201306);
    }

}
